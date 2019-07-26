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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.simplity.fm.datatypes.ValueType;
import org.simplity.fm.form.FormData;
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
	protected static final Logger logger = LoggerFactory.getLogger(RdbDriver.class);

	/*
	 * connection factory
	 */
	private interface IFactory {
		Connection getConnection() throws SQLException;
	}

	/*
	 * factory..
	 */
	private static IFactory factory = null;

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
		DataSource ds = (DataSource) new InitialContext().lookup(jndiName);
		/*
		 * test it..
		 */
		ds.getConnection().close();
		factory = new IFactory() {
			@Override
			public Connection getConnection() throws SQLException {
				return ds.getConnection();
			}
		};
		logger.info("DB driver set based on JNDI name");
	}

	/**
	 * invoked by initialization/boot-strap to set up the driver using
	 * connection string
	 * 
	 * @param conString
	 * @param driverClassName
	 * @throws Exception
	 */
	public static void SetConnectionString(String conString, String driverClassName) throws Exception {
		Class.forName(driverClassName);
		/*
		 * test it
		 */
		DriverManager.getConnection(conString).close();
		factory = new IFactory() {
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
		//
	}

	/*
	 * core worker methods are static. They are called either directly from
	 * driver (for one-off operation) or from transHandler as part of
	 * transactions
	 */

	protected static int doRead(IDbReader reader, Connection con) throws SQLException {
		String pps = reader.getPreparedStatement();
		if (pps == null) {
			return 0;
		}

		try (PreparedStatement ps = con.prepareStatement(pps)) {
			reader.setParams(ps);
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

	protected static Object[] doRead(Connection con, String sql, ValueType[] whereTypes, Object[] whereData,
			ValueType[] resultTypes) throws SQLException {
		try (PreparedStatement ps = con.prepareStatement(sql)) {
			int posn = 0;
			for (ValueType vt : whereTypes) {
				Object val = whereData[posn];
				posn++;
				vt.setPsParam(ps, posn, val);
			}

			try (ResultSet rs = ps.executeQuery()) {
				if (!rs.next()) {
					return null;
				}
				Object[] result = new Object[resultTypes.length];
				for (int i = 0; i < resultTypes.length; i++) {
					result[i] = resultTypes[i].getFromRs(rs, i + 1);
				}
				return result;
			}
		}
	}

	protected static boolean doReadForm(Connection con, String sql, DbParam[] whereParams, DbParam[] selectParams,
			Object[] formData) throws SQLException {
		try (PreparedStatement ps = con.prepareStatement(sql)) {
			int posn = 0;
			for (DbParam p : whereParams) {
				posn++;
				p.valueType.setPsParam(ps, posn, formData[p.idx]);
			}

			try (ResultSet rs = ps.executeQuery()) {
				if (!rs.next()) {
					return false;
				}
				posn = 0;
				for (DbParam p : selectParams) {
					posn++;
					formData[p.idx] = p.valueType.getFromRs(rs, posn);
				}
				return true;
			}
		}
	}

	protected static Object[][] doReadRows(Connection con, String sql, ValueType[] whereTypes, Object[] whereData,
			ValueType[] resultTypes) throws SQLException {
		try (PreparedStatement ps = con.prepareStatement(sql)) {
			int posn = 0;
			for (ValueType vt : whereTypes) {
				Object val = whereData[posn];
				posn++;
				vt.setPsParam(ps, posn, val);
			}
			List<Object[]> result = new ArrayList<>();
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					Object[] row = new Object[resultTypes.length];
					result.add(row);
					for (int i = 0; i < resultTypes.length; i++) {
						row[i] = resultTypes[i].getFromRs(rs, i + 1);
					}
				}
				if (result.size() == 0) {
					return null;
				}
				return result.toArray(new Object[0][]);
			}
		}
	}

	protected static Object[][] doReadChildRows(Connection con, String sql, DbParam[] whereParams,
			DbParam[] selectParams, Object[] formData, int nbrChildFields) throws SQLException {
		try (PreparedStatement ps = con.prepareStatement(sql)) {
			int posn = 0;
			for (DbParam p : whereParams) {
				posn++;
				p.valueType.setPsParam(ps, posn, formData[p.idx]);
			}

			List<Object[]> result = new ArrayList<>();
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					Object[] row = new Object[nbrChildFields];
					result.add(row);
					posn = 0;
					for (DbParam p : selectParams) {
						posn++;
						row[p.idx] = p.valueType.getFromRs(rs, posn);
					}
				}
				if (result.size() == 0) {
					return null;
				}
				return result.toArray(new Object[0][]);
			}
		}
	}

	protected static int doWrite(IDbWriter writer, Connection con) throws SQLException {
		String pps = writer.getPreparedStatement();
		if (pps == null) {
			logger.warn(
					"writer {} returned null as prepared statement, indicatiing taht it does not want to write.. Opertion skipped.",
					writer.getClass().getName());
			return 0;
		}

		try (PreparedStatement ps = con.prepareStatement(pps)) {
			con.setAutoCommit(true);
			writer.setParams(ps);
			return ps.executeUpdate();
		}
	}

	protected static int doWrite(Connection con, String sql, ValueType[] paramTypes, Object[] paramValues,
			long[] generatedKeys) throws SQLException {
		try (PreparedStatement ps = con.prepareStatement(sql)) {

			for (int i = 0; i < paramValues.length; i++) {
				paramTypes[i].setPsParam(ps, i + 1, paramValues[i]);
			}
			int result = ps.executeUpdate();
			if (result > 0 && generatedKeys != null) {
				generatedKeys[0] = getGeneratedKey(ps);
			}
			return result;
		}
	}

	private static long getGeneratedKey(PreparedStatement ps) throws SQLException {
		try (ResultSet rs = ps.getGeneratedKeys()) {
			if (rs.next()) {
				return rs.getLong(1);
			}
			throw new SQLException("Driver failed to return a generated key ");
		}
	}

	protected static int doWriteForm(Connection con, String sql, DbParam[] params, Object[] formData,
			long[] generatedKeys) throws SQLException {
		try (PreparedStatement ps = con.prepareStatement(sql)) {
			int posn = 0;
			for (DbParam p : params) {
				posn++;
				p.valueType.setPsParam(ps, posn, formData[p.idx]);
			}
			int result = ps.executeUpdate();
			if (result > 0 && generatedKeys != null) {
				generatedKeys[0] = getGeneratedKey(ps);
			}
			return result;
		}
	}

	protected static int[] doWriteBatch(Connection con, String sql, ValueType[] paramTypes, Object[][] paramValues)
			throws SQLException {
		try (PreparedStatement ps = con.prepareStatement(sql)) {
			for (Object[] row : paramValues) {
				ps.addBatch();
				for (int i = 0; i < paramValues.length; i++) {
					paramTypes[i].setPsParam(ps, i + 1, row[i]);
				}
			}
			return ps.executeBatch();
		}
	}

	protected static int[] doFormBatch(Connection con, String sql, DbParam[] params, FormData[] childData)
			throws SQLException {
		try (PreparedStatement ps = con.prepareStatement(sql)) {
			for (FormData fd : childData) {
				/*
				 * we do not have mechanism to persist grand children as of now..
				 */
				Object[] row = fd.getFieldValues();
				ps.addBatch();
				int posn = 0;
				for (DbParam p : params) {
					posn++;
					p.valueType.setPsParam(ps, posn, row[p.idx]);
				}
			}
			return ps.executeBatch();
		}
	}

	static void warn(String sql, DbParam[] params, Object[] vals) {
		StringBuilder sbf = new StringBuilder();
		sbf.append("RDBMS is not set up. Sql = ").append(sql);
		for (DbParam p : params) {
			sbf.append('(').append(p.valueType).append(", ").append(vals[p.idx]).append(") ");
		}
		logger.warn(sbf.toString());
	}

	static void warn(String sql, ValueType[] types, Object[] vals) {
		StringBuilder sbf = new StringBuilder();
		sbf.append("RDBMS is not set up. Sql = ").append(sql);
		for (int i = 0; i < types.length; i++) {
			sbf.append('(').append(types[i]).append(", ").append(vals[i]).append(") ");
		}
		logger.warn(sbf.toString());
	}
	
	static void warn(String sql) {
		logger.error("RDBMS is not set up. Sql = ", sql);
	}
	/**
	 * To be used for a one-off read operation, not part of any transaction.
	 * Most flexible way to read from db. Caller has full control of what and
	 * how to read.
	 * 
	 * @param reader
	 *            instance that wants to read from the database
	 * @return number of rows actually read by the reader.
	 * @throws SQLException
	 * 
	 */
	public int read(IDbReader reader) throws SQLException {
		if(factory == null) {
			warn(reader.getPreparedStatement());
			return 0;
		}
		try (Connection con = factory.getConnection()) {
			return doRead(reader, con);
		}
	}

	/**
	 * lower level API that is very close to the JDBC API for reading one
	 * row from the result of a select query
	 * 
	 * @param sql
	 *            a prepared statement
	 * @param whereTypes
	 *            parameter types of where clause. These are the parameters
	 *            set to the prepared statement before getting the result
	 *            set from the prepared statement
	 * @param whereData
	 *            must have the same number of elements as in whereTypes.
	 *            Values to be set to the prepared statement
	 * @param resultTypes
	 *            this array has one element for each parameter expected in
	 *            the output result set. Values are extracted from the
	 *            result set based on these types
	 * @return an array of object values extracted from the result set. null
	 *         if result set had no rows
	 * @throws SQLException
	 */
	public Object[] read(String sql, ValueType[] whereTypes, Object[] whereData, ValueType[] resultTypes)
			throws SQLException {
		if(factory == null) {
			warn(sql, whereTypes, whereData);
			return null;
		}
		try (Connection con = factory.getConnection()) {
			return doRead(con, sql, whereTypes, whereData, resultTypes);
		}
	}

	/**
	 * 
	 * @param formData
	 * @return true if data is read. false otherwise
	 * @throws SQLException
	 */
	public boolean readForm(FormData formData) throws SQLException {
		final Boolean[] result = new Boolean[1];
		this.transact(new IDbClient() {
			
			@Override
			public boolean transact(DbHandle handle) throws SQLException {
				boolean ok =  handle.readForm(formData);
				result[0] = ok;
				return ok;
			}
		}, true);
		return result[0];
	}
	
	/**
	 * 
	 * @param formData
	 * @return true if the data is update. false otherwise
	 * @throws SQLException
	 */
	public boolean update(FormData formData) throws SQLException {
		final Boolean[] result = new Boolean[1];
		this.transact(new IDbClient() {
			
			@Override
			public boolean transact(DbHandle handle) throws SQLException {
				boolean ok =  handle.update(formData);
				result[0] = ok;
				return ok;
			}
		}, true);
		return result[0];
	}
	
	/**
	 * 
	 * @param formData
	 * @return true if insert succeeded. false if no rows got inserted
	 * @throws SQLException
	 */
	public boolean insert(FormData formData) throws SQLException {
		final Boolean[] result = new Boolean[1];
		this.transact(new IDbClient() {
			
			@Override
			public boolean transact(DbHandle handle) throws SQLException {
				boolean ok =  handle.insert(formData);
				result[0] = ok;
				return ok;
			}
		}, true);
		return result[0];
	}
	/**
	 * 
	 * @param formData
	 * @return true if the row got deleted. false otherwise
	 * @throws SQLException
	 */
	public boolean delete(FormData formData) throws SQLException {
		final Boolean[] result = new Boolean[1];
		this.transact(new IDbClient() {
			
			@Override
			public boolean transact(DbHandle handle) throws SQLException {
				boolean ok =  handle.delete(formData);
				result[0] = ok;
				return ok;
			}
		}, true);
		return result[0];
	}
	/**
	 * lower level API that is very close to the JDBC API for reading all
	 * rows from the result of a select query
	 * 
	 * @param sql
	 *            a prepared statement
	 * @param whereTypes
	 *            parameter types of where clause. These are the parameters
	 *            set to the prepared statement before getting the result
	 *            set from the prepared statement
	 * @param whereData
	 *            must have the same number of elements as in whereTypes.
	 *            Values to be set to the prepared statement
	 * @param resultTypes
	 *            this array has one element for each parameter expected in
	 *            the output result set. Values are extracted from the
	 *            result set based on these types
	 * @return an array of rows from the result set. Each row is an array of
	 *         object values from the result set row.
	 * @throws SQLException
	 */
	public Object[][] readRows(String sql, ValueType[] whereTypes, Object[] whereData, ValueType[] resultTypes)
			throws SQLException {
		if(factory == null) {
			warn(sql, whereTypes, whereData);
			return null;
		}
		try (Connection con = factory.getConnection()) {
			return doReadRows(con, sql, whereTypes, whereData, resultTypes);
		}
	}

	/**
	 * @param writer
	 * @return number of affected rows.
	 * @throws SQLException
	 */
	public int write(IDbWriter writer) throws SQLException {
		if(factory == null) {
			warn(writer.getPreparedStatement());
			return 0;
		}
		try (Connection con = factory.getConnection()) {
			return doWrite(writer, con);
		}
	}

	/**
	 * API that is close to the JDBC API for updating/inserting/deleting
	 * 
	 * @param sql
	 *            a prepared statement that manipulates data.
	 * @param paramTypes
	 *            type of parameters to be set the prepared statement
	 * @param params
	 *            values to be set to the prepared statement
	 * @param generatedKeys
	 *            null, unless the sql execution is expected to result in a
	 *            generated key by the RDBMS. first element is populated with
	 *            the generated key. SqlException is thrown if the driver fails
	 *            to get the generated key. the caller expects a
	 * @return number of affected rows. -1 if the driver was unable to
	 *         determine it
	 * @throws SQLException
	 */
	public int write(String sql, ValueType[] paramTypes, Object[] params, long[] generatedKeys) throws SQLException {
		if(factory == null) {
			warn(sql, paramTypes, params);
			return 0;
		}
		try (Connection con = factory.getConnection()) {
			return doWrite(con, sql, paramTypes, params, generatedKeys);
		}
	}

	/**
	 * API that is close to the JDBC API for updating/inserting/deleting
	 * 
	 * @param sql
	 *            a prepared statement that manipulates data.
	 * @param paramTypes
	 *            type of parameters to be set the prepared statement
	 * @param params
	 *            values to be set to the prepared statement. these will be
	 *            executed in a batch
	 * @return number of affected rows, on element per batch. -1 implies
	 *         that the driver was unable to determine it
	 * @throws SQLException
	 */
	public int[] writeBatch(String sql, ValueType[] paramTypes, Object[][] params) throws SQLException {
		if(factory == null) {
			warn(sql, paramTypes, params);
			return new int[params.length];
		}
		try (Connection con = factory.getConnection()) {
			return doWriteBatch(con, sql, paramTypes, params);
		}
	}

	/**
	 * @param transactor
	 * @param readOnly
	 *            true if the caller is not going to modify any data.
	 * @throws SQLException
	 *             if update is attempted after setting readOnly=true, or any
	 *             other SqlException
	 * 
	 */
	public void transact(IDbClient transactor, boolean readOnly) throws SQLException {
		if(factory == null) {
			String msg = "A dummy handle is returned as RDBMS is not set up";
			logger.error(msg);
			throw new SQLException(msg);
		}
		try (Connection con = factory.getConnection()) {
			DbHandle handle = new DbHandle(con, readOnly);
			try {
				boolean ok = transactor.transact(handle);
				handle.done(ok);

			} catch (Exception e) {
				logger.error("Exception occurred in the middle of a transaction: {}", e.getMessage());
				handle.done(false);
				throw new SQLException(e.getMessage());
			}
		}
	}
}