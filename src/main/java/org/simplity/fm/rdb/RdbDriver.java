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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.simplity.fm.datatypes.ValueType;
import org.simplity.fm.form.FormData;
import org.simplity.fm.form.FormDbParam;
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
	 * factory..
	 */
	private static IConnectionFactory factory = null;

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

	protected static boolean doReadForm(Connection con, String sql, FormDbParam[] whereParams, FormDbParam[] selectParams,
			Object[] formData) throws SQLException {
		try (PreparedStatement ps = con.prepareStatement(sql)) {
			int posn = 0;
			for (FormDbParam p : whereParams) {
				posn++;
				p.valueType.setPsParam(ps, posn, formData[p.idx]);
			}

			try (ResultSet rs = ps.executeQuery()) {
				if (!rs.next()) {
					return false;
				}
				posn = 0;
				for (FormDbParam p : selectParams) {
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

	protected static Object[][] doReadChildRows(Connection con, String sql, FormDbParam[] whereParams,
			FormDbParam[] selectParams, Object[] formData, int nbrChildFields) throws SQLException {
		try (PreparedStatement ps = con.prepareStatement(sql)) {
			int posn = 0;
			for (FormDbParam p : whereParams) {
				posn++;
				p.valueType.setPsParam(ps, posn, formData[p.idx]);
			}

			List<Object[]> result = new ArrayList<>();
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					Object[] row = new Object[nbrChildFields];
					result.add(row);
					posn = 0;
					for (FormDbParam p : selectParams) {
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

	protected static int doWriteForm(Connection con, String sql, FormDbParam[] params, Object[] formData,
			long[] generatedKeys) throws SQLException {
		logger.info("Sql: {}", sql);
		try (PreparedStatement ps = con.prepareStatement(sql)) {
			int posn = 0;
			for (FormDbParam p : params) {
				posn++;
				p.valueType.setPsParam(ps, posn, formData[p.idx]);
			}
			int result = ps.executeUpdate();
			if (result > 0 && generatedKeys != null) {
				generatedKeys[0] = getGeneratedKey(ps);
			}
			logger.info("Sql: {}\nresult:{}", sql, result);
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

	protected static int[] doFormBatch(Connection con, String sql, FormDbParam[] params, FormData[] childData)
			throws SQLException {
		try (PreparedStatement ps = con.prepareStatement(sql)) {
			for (FormData fd : childData) {
				/*
				 * we do not have mechanism to persist grand children as of
				 * now..
				 */
				Object[] row = fd.getFieldValues();
				ps.addBatch();
				int posn = 0;
				for (FormDbParam p : params) {
					posn++;
					p.valueType.setPsParam(ps, posn, row[p.idx]);
				}
			}
			return ps.executeBatch();
		}
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
		if (factory == null) {
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
		if (factory == null) {
			warn(sql, whereTypes, whereData);
			return null;
		}
		try (Connection con = factory.getConnection()) {
			return doRead(con, sql, whereTypes, whereData, resultTypes);
		}
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
		if (factory == null) {
			warn(sql, whereTypes, whereData);
			return null;
		}
		try (Connection con = factory.getConnection()) {
			return doReadRows(con, sql, whereTypes, whereData, resultTypes);
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
		if (factory == null) {
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

	/**
	 * do transaction on a schema that is not the default schema used by this
	 * application. Use this ONLY id the schema is different from the default
	 * 
	 * @param transactor
	 * @param readOnly
	 *            true if the caller is not going to modify any data.
	 * @param schemaName
	 *            non-null schema name that is different from the default schema
	 * @throws SQLException
	 *             if update is attempted after setting readOnly=true, or any
	 *             other SqlException
	 * 
	 */
	public void transactUsingSchema(IDbClient transactor, boolean readOnly, String schemaName) throws SQLException {
		if (factory == null) {
			String msg = "A dummy handle is returned as RDBMS is not set up";
			logger.error(msg);
			throw new SQLException(msg);
		}
		try (Connection con = factory.getConnection(schemaName)) {
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

	/**
	 * @param conFactory non-null factory to be used to get  db-connection
	 */
	public static void setFactory(IConnectionFactory conFactory) {
		factory = conFactory;
	}
}