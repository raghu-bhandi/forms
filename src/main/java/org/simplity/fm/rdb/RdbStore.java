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

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.sql.Clob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.simplity.fm.io.DataStore;
import org.simplity.fm.io.IoConsumer;
import org.simplity.fm.rdb.RdbDriver.TransHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * data is store din an RDBMS as Clob. This should work with any of the popular
 * RDBMS.
 * 
 * @author simplity.org
 *
 */
public class RdbStore extends DataStore {
	protected static final Logger logger = LoggerFactory.getLogger(DataStore.class);
	/**
	 * TODO: to be replaced with dbDriver concept for production
	 */
	private static final String TABLE_NAME = "formData";
	private static final String KEY_NAME = "key";
	private static final String CLOB = "data";
	private static final String WHERE = " where submitted = 0 AND " + KEY_NAME + "=?";
	private static final String INSERT = "insert into " + TABLE_NAME + " values(?,0,?)";
	private static final String SELECT = "select " + CLOB + " from " + TABLE_NAME + WHERE;
	private static final String DELETE = "delete " + TABLE_NAME + WHERE;
	private static final String UPDATE = "update " + TABLE_NAME + " set submitted = 1, key =? " + WHERE;

	@Override
	public boolean retrieve(String id, IoConsumer<Reader> consumer) throws IOException {
		IDbReader dbReader = getReader(id, consumer);
		try {
			RdbDriver.getDriver().read(dbReader);
		} catch (SQLException e) {
			throw new IOException(e);
		}
		return true;
	}

	@Override
	public void store(String id, IoConsumer<Writer> consumer) throws IOException {
		IDbTransactor transactor = new IDbTransactor() {

			@Override
			public boolean transact(TransHandler handler) throws SQLException {
				/*
				 *TODO: is delete-and-insert the best way? or should we update the blob?
				 */
				IDbWriter dbDeleter = getDeleter(id);
				handler.write(dbDeleter);

				IDbWriter dbWriter = getInserter(id, handler);
				handler.write(dbWriter);
				
				return true;
			}
		};
		
		try {
			RdbDriver driver = RdbDriver.getDriver();
			driver.transact(transactor, false);
		} catch (SQLException e) {
			throw new IOException(e);
		}
	}

	@Override
	public void trash(String id) throws IOException {
		IDbWriter dbWriter = this.getDeleter(id);
		try {
			RdbDriver.getDriver().write(dbWriter);
		} catch (SQLException e) {
			throw new IOException(e);
		}
	}

	@Override
	public void moveToStaging(String id, String newId) throws IOException {
		IDbWriter dbWriter = this.getUpdater(id, newId);
		try {
			RdbDriver.getDriver().write(dbWriter);
		} catch (SQLException e) {
			throw new IOException(e);
		}
	}
	
	protected IDbWriter getDeleter(String id) {
		return new IDbWriter() {

			@Override
			public String getPreparedStatement() {
				return DELETE;
			}

			@Override
			public boolean toTreatSqlExceptionAsNoRowsAffected() {
				return true;
			}

			@Override
			public void setParams(PreparedStatement ps) throws SQLException {
				ps.setString(1, id);
			}
		};
	}
	
	protected IDbWriter getInserter(String id, RdbDriver.TransHandler handler) {
		return new IDbWriter() {

			@Override
			public String getPreparedStatement() {
				return INSERT;
			}

			@Override
			public boolean toTreatSqlExceptionAsNoRowsAffected() {
				return false;
			}

			@Override
			public void setParams(PreparedStatement ps) throws SQLException {
				ps.setString(1, id);
				Clob clob = handler.createClob();
				ps.setClob(2, clob);
			}
		};
	}
	
	protected IDbReader getReader(String id, IoConsumer<Reader> consumer) {
		return new IDbReader() {

			@Override
			public String getPreparedStatement() {
				return SELECT;
			}

			@Override
			public void setParams(PreparedStatement ps) throws SQLException {
				ps.setString(1, id);
			}

			@Override
			public boolean readARow(ResultSet rs) throws SQLException {
				try (Reader reader = rs.getCharacterStream(1)) {
					consumer.accept(reader);
					/*
					 * we read only one row. return false to say enough is
					 * enough !!!
					 */
					return false;
				} catch (IOException ioe) {
					logger.error("IO Exception whiel reading blob from table. Error {}", ioe.getMessage());
					throw new SQLException(ioe);
				}
			}
		};
	}
	
	protected IDbWriter getUpdater(String id, String newId) {
		return new IDbWriter() {

			@Override
			public String getPreparedStatement() {
				return UPDATE;
			}

			@Override
			public boolean toTreatSqlExceptionAsNoRowsAffected() {
				return false;
			}

			@Override
			public void setParams(PreparedStatement ps) throws SQLException {
				ps.setString(1, id);
				ps.setString(2, newId);
			}
		};
	}
}
