/*
 * Copyright (c) 2019 simplity.org
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.simplity.fm.rdb;

import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Driver to deal with RDBMS read/write operations. Note that we expose
 * much-higher level APIs that the JDBC driver. And, of course we provide the
 * very basic feature : read/write. That is the whole idea of this class -
 * provide simple API to do the most common operation
 * 
 * @author simplity.org
 *
 */
public class RdbDriver {
	private static final Logger logger = LoggerFactory.getLogger(RdbDriver.class);

	/*
	 * connection factory
	 */
	private interface IFactory {
		Connection getConnection() throws SQLException;
	}

	/*
	 * stand-in factory until a real factory is set-up..
	 */
	private static IFactory factory = new IFactory() {

		@Override
		public Connection getConnection() throws SQLException {
			throw new SQLException("JDBC Driver is not set up");
		}
	};

	/**
	 * invoked by initialization/boot-strap to set up the driver using
	 * DataSource
	 * 
	 * @param jndiName
	 *            jndi name for dataSource.
	 * @throws NamingException
	 * @throws SQLException 
	 */
	public static void SetDataSource(String jndiName) throws NamingException, SQLException {
		factory = new IFactory() {
			private DataSource ds = (DataSource) new InitialContext().lookup(jndiName);
			{
				/*
				 *  microphone testing 1.. 2.. 3..
				 */
				this.ds.getConnection().close();
			}

			@Override
			public Connection getConnection() throws SQLException {
				return this.ds.getConnection();
			}
		};
		logger.info("DB driver set based on JNDI name");
	}

	/**
	 * invoked by initialization/boot-strap to set up the driver using
	 * connection string
	 * 
	 * @param conString
	 * @param driveClassName
	 * @throws Exception
	 */
	public static void SetConnectionString(String conString, String driveClassName) throws Exception {
		factory = new IFactory() {
			{
				Class.forName(driveClassName);
				/*
				 *  microphone testing 1.. 2.. 3..
				 */
				DriverManager.getConnection(conString).close();
				
			}

			@Override
			public Connection getConnection() throws SQLException {
				return DriverManager.getConnection(conString);
			}
		};
		logger.info("DB driver set based on connection string");
	}

	/**
	 * 
	 * @return db driver
	 * @throws SQLException
	 *             in case of any exception while dealing with the rdbms
	 */
	public static RdbDriver getDriver() throws SQLException {
		return new RdbDriver();
	}

	private RdbDriver() {
	}

	/**
	 * to be used by one-off readers who are not part of any transaction.
	 * 
	 * @param reader
	 *            instance that wants to read from the database
	 * @return number of rows actually read by the reader.
	 * @throws SQLException
	 * 
	 */
	public int read(IDbReader reader) throws SQLException {
		try (Connection con = factory.getConnection()) {
			return doRead(reader, con);
		}
	}

	/**
	 * @param writer
	 * @return number of affected rows.
	 * @throws SQLException
	 */
	public int write(IDbWriter writer) throws SQLException {
		try (Connection con = factory.getConnection()) {
			return doWrite(writer, con);
		}
	}

	/**
	 * @param transactor
	 * @param autoCommit
	 *            true if you do not want commit-roll-back facility. Any update
	 *            is carried out under auto-commit. You may also use this
	 *            facility to organize all your read operations in one method,
	 *            rather rdbDriver.read() method
	 * @throws SQLException
	 * 
	 */
	public void transact(IDbTransactor transactor, boolean autoCommit) throws SQLException {
		try (Connection con = factory.getConnection()) {
			con.setAutoCommit(autoCommit);
			if(autoCommit) {
				transactor.transact(new TransHandler(con));
				return;
			}
			try {
			if(transactor.transact(new TransHandler(con))){
				con.commit();
			}else {
				logger.warn("TRansaction rolled-back as the user-method retrurned with false");
				con.rollback();
				
			}
			}catch(Exception e) {
				logger.error("Exception occurred in the middle of a transaction: {}", e.getMessage());
				con.rollback();
				throw new SQLException(e.getMessage());
			}
		}
	}

	/**
	 * @param writer
	 * @return number of affected rows.
	 * @throws SQLException
	 */
	static int doWrite(IDbWriter writer, Connection con) throws SQLException {
		boolean swallowIt = writer.toTraetSqlExceptionAsNoRowsAffected();
		String pps = writer.getPreparedStatement();
		if(pps == null) {
			logger.warn("writer {} returned null as prepared statement, indicatiing taht it does not want to write.. Opertion skipped.", writer.getClass().getName() );
			return 0;
		}
		
		try (PreparedStatement ps = con.prepareStatement(pps)) {
			con.setAutoCommit(true);
			writer.setParams(ps);
			return ps.executeUpdate();
		} catch (SQLException e) {
			if (swallowIt) {
				logger.info("SQLException code:" + e.getErrorCode() + " message :" + e.getMessage()
						+ " is treated as zero rows affected.");
				return 0;
			}
			throw e;
		}
	}

	static int doRead(IDbReader reader, Connection con) throws SQLException {
		String pps = reader.getPreparedStatement();
		if(pps == null) {
			logger.warn("Reader {} returned null as prepared statement, indicatiing taht it does not want to write.. Opertion skipped.", reader.getClass().getName() );
			return 0;
		}
		
		try (PreparedStatement ps = con.prepareStatement(pps)) {
			reader.setParamsToPs(ps);
			try (ResultSet rs = ps.executeQuery()) {
				int n = 0;
				while (rs.next()) {
					if (reader.readARow(rs) == false) {
						break;
					}
					n++;
				}
				logger.info("{} rows read using read()", n);
				return n;
			}
		}
	}

	static class TransHandler {
		private final Connection con;

		protected TransHandler(Connection con) {
			this.con = con;
		}

		/**
		 * to be used by one-off readers who are not part of any transaction.
		 * 
		 * @param reader
		 *            instance that wants to read from the database
		 * @return number of rows actually read by the reader.
		 * @throws SQLException
		 * 
		 */
		public int read(IDbReader reader) throws SQLException {
			return doRead(reader, this.con);
		}

		/**
		 * @param writer
		 * @return number of affected rows.
		 * @throws SQLException
		 */
		public int write(IDbWriter writer) throws SQLException {
			return doWrite(writer, this.con);
		}

		/**
		 * 
		 * @return blob object
		 * @throws SQLException
		 */
		public Clob createClob() throws SQLException {
			return this.con.createClob();
		}

		/**
		 * 
		 * @return blob object
		 * @throws SQLException
		 */
		public Blob createBlob() throws SQLException {
			return this.con.createBlob();
		}
	}
}
