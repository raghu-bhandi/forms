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
import java.sql.SQLException;

import org.simplity.fm.datatypes.ValueType;
import org.simplity.fm.form.ChildDbMetaData;
import org.simplity.fm.form.ChildForm;
import org.simplity.fm.form.DbMetaData;
import org.simplity.fm.form.Form;
import org.simplity.fm.form.FormData;
import org.simplity.fm.form.FormDbParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author simplity.org
 *
 */
public class DbHandle {
	private static final Logger logger = LoggerFactory.getLogger(DbHandle.class);
	private final Connection con;
	private final boolean readOnly;
	private boolean isDirty = false;

	/**
	 * to be created by DbDriver ONLY
	 * 
	 * @param con
	 * @param readOnly
	 */
	DbHandle(Connection con, boolean readOnly) {
		this.con = con;
		this.readOnly = readOnly;
	}

	/**
	 * Most flexible way to read from db. Caller has full control of what
	 * and how to read.
	 * 
	 * @param reader
	 *            instance that wants to read from the database
	 * @return number of rows actually read by the reader.
	 * @throws SQLException
	 * 
	 */
	public int read(IDbReader reader) throws SQLException {
		return RdbDriver.doRead(reader, this.con);
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
	 *         if result-set had no rows
	 * @throws SQLException
	 */
	public Object[] read(String sql, ValueType[] whereTypes, Object[] whereData, ValueType[] resultTypes)
			throws SQLException {
		return RdbDriver.doRead(this.con, sql, whereTypes, whereData, resultTypes);
	}

	/**
	 * read data into a formData
	 * 
	 * @param formData
	 * @return true if a row is read. False otherwise
	 * @throws SQLException
	 */
	public boolean readForm(FormData formData) throws SQLException {
		Form form = formData.getForm();
		DbMetaData meta = form.getDbMetaData();
		if (meta == null) {
			logger.error("Form {} is not designed for db operation", form.getFormId());
			return false;
		}
		String sql = meta.selectClause + meta.whereClause;
		logger.info("Going to execute sql {} ", sql);
		Object[] data = formData.getFieldValues();
		boolean ok = RdbDriver.doReadForm(this.con, sql, meta.whereParams, meta.insertParams, data);
		if (!ok) {
			return false;
		}

		ChildDbMetaData[] metas = meta.childMeta;
		if (metas == null) {
			return true;
		}

		int idx = 0;
		FormData[][] childData = formData.getChildData();
		ChildForm[] childForms = form.getChildForms();

		for (ChildDbMetaData cm : meta.childMeta) {
			if (cm != null) {
				DbMetaData childDetils = cm.childMeta;
				Object[][] rows = RdbDriver.doReadFormRows(this.con, childDetils.selectClause + cm.whereClause,
						cm.whereParams, data, childDetils.selectParams, cm.nbrChildFields);
				childData[idx] = childForms[idx].form.createChildData(rows);
			}
			idx++;
		}

		return true;
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
		return RdbDriver.doReadRows(this.con, sql, whereTypes, whereData, resultTypes);
	}

	/**
	 * specialized method for Form with childForm (tabular data), filtering rows
	 * 
	 * @param sql
	 *            prepared statement for selecting data
	 * @param whereParams
	 *            parameters for the where clause
	 * @param selectParams
	 *            parameters for reading values from the result set
	 * @param whereValues
	 *            form data. parameter values are taken from this, and
	 *            extracted values are put into this
	 * @param nbrChildFields
	 *            number of fields in the child form. Object array for
	 *            tabular data is created with this length
	 * @return true if a row is read. False otherwise
	 * @throws SQLException
	 */
	public Object[] readFormRow(String sql, FormDbParam[] whereParams, Object[] whereValues, FormDbParam[] selectParams,
			int nbrChildFields) throws SQLException {
		return RdbDriver.doReadFormRow(this.con, sql, whereParams, whereValues, selectParams, nbrChildFields);
	}

	/**
	 * specialized method for Form with childForm (tabular data), filtering rows
	 * 
	 * @param sql
	 *            prepared statement for selecting data
	 * @param whereParams
	 *            parameters for the where clause
	 * @param selectParams
	 *            parameters for reading values from the result set
	 * @param whereValues
	 *            form data. parameter values are taken from this, and
	 *            extracted values are put into this
	 * @param nbrChildFields
	 *            number of fields in the child form. Object array for
	 *            tabular data is created with this length
	 * @return true if a row is read. False otherwise
	 * @throws SQLException
	 */
	public Object[][] readFormRows(String sql, FormDbParam[] whereParams, Object[] whereValues,
			FormDbParam[] selectParams, int nbrChildFields) throws SQLException {
		return RdbDriver.doReadFormRows(this.con, sql, whereParams, whereValues, selectParams, nbrChildFields);
	}

	/**
	 * 
	 * @param formData
	 * @return true if the data is update. false otherwise
	 * @throws SQLException
	 */
	public boolean update(FormData formData) throws SQLException {
		Form form = formData.getForm();
		DbMetaData meta = form.getDbMetaData();
		if (meta == null) {
			logger.error("Form {} is not designed for db operation", form.getFormId());
			return false;
		}
		String sql = meta.updateClause;
		Object[] data = formData.getFieldValues();
		int n = RdbDriver.doWriteForm(this.con, sql, meta.updateParams, data, null);
		if (n == 0) {
			return false;
		}
		if (meta.childMeta != null) {
			this.writeChildren(meta, data, formData.getChildData());
		}
		return true;
	}

	/**
	 * 
	 * @param formData
	 * @return true if insert succeeded. false if no rows got inserted
	 * @throws SQLException
	 */
	public boolean insert(FormData formData) throws SQLException {
		Form form = formData.getForm();
		DbMetaData meta = form.getDbMetaData();
		if (meta == null) {
			logger.error("Form {} is not designed for db operation", form.getFormId());
			return false;
		}
		String sql = meta.insertClause;
		Object[] data = formData.getFieldValues();
		final long[] generatedKeys = meta.keyIsGenerated ? new long[1] : null;
		int n = RdbDriver.doWriteForm(this.con, sql, meta.insertParams, data, generatedKeys);
		if (n == 0) {
			return false;
		}
		if (meta.childMeta != null) {
			this.writeChildren(meta, data, formData.getChildData());
		}
		return true;
	}

	/**
	 * 
	 * @param formData
	 * @return true if the row got deleted. false otherwise
	 * @throws SQLException
	 */
	public boolean delete(FormData formData) throws SQLException {
		Form form = formData.getForm();
		DbMetaData meta = form.getDbMetaData();
		if (meta == null) {
			logger.error("Form {} is not designed for db operation", form.getFormId());
			return false;
		}
		String sql = meta.deleteClause + meta.whereClause;
		Object[] data = formData.getFieldValues();
		int n = RdbDriver.doWriteForm(this.con, sql, meta.insertParams, data, null);
		if (n == 0) {
			return false;
		}
		if (meta.childMeta != null) {
			this.writeChildren(meta, data, formData.getChildData());
		}
		return true;
	}

	protected void writeChildren(DbMetaData meta, Object[] data, FormData[][] childData) throws SQLException {
		int idx = 0;
		for (ChildDbMetaData cm : meta.childMeta) {
			if (cm != null) {
				DbMetaData childDetils = cm.childMeta;
				/*
				 * delete child rows
				 */
				this.writeForm(childDetils.deleteClause + cm.whereClause, cm.whereParams, data, null);
				/*
				 * now insert them
				 */
				if (childData != null) {
					this.formBatch(childDetils.insertClause, childDetils.insertParams, childData[idx]);
				}
			}
			idx++;
		}
	}

	/**
	 * @param writer
	 * @return number of affected rows.
	 * @throws SQLException
	 */
	public int write(IDbWriter writer) throws SQLException {
		if (this.readOnly) {
			throw new SQLException("Transaction is opened for readOnly. write operation not allowed.");
		}
		this.isDirty = true;
		return RdbDriver.doWrite(writer, this.con);
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
	 *            generated key by the RDBMS. first element is populated
	 *            with
	 *            the generated key. SqlException is thrown if the driver
	 *            fails
	 *            to get the generated key. the caller expects a
	 * @return number of affected rows. -1 if the driver was unable to
	 *         determine it
	 * @throws SQLException
	 */
	public int write(String sql, ValueType[] paramTypes, Object[] params, long[] generatedKeys) throws SQLException {
		if (this.readOnly) {
			throw new SQLException("Transaction is opened for readOnly. write operation not allowed.");
		}
		this.isDirty = true;
		return RdbDriver.doWrite(this.con, sql, paramTypes, params, generatedKeys);
	}

	/**
	 * API for form data to be persisted
	 * 
	 * @param sql
	 *            insert/update/delete sql to be executed
	 * @param params
	 *            parameters to be set to the SQL
	 * @param formData
	 *            values for the parameters are picked up from here
	 * @param generatedKey
	 *            null if this operation is not an insert operation, or this
	 *            table is not designed to generate it primary key on
	 *            insert. array with one element. generated key is set in
	 *            the first element
	 * @return number of rows affected.
	 * @throws SQLException
	 */
	public int writeForm(String sql, FormDbParam[] params, Object[] formData, long[] generatedKey) throws SQLException {
		if (this.readOnly) {
			throw new SQLException("Transaction is opened for readOnly. write operation not allowed.");
		}
		this.isDirty = true;
		return RdbDriver.doWriteForm(this.con, sql, params, formData, generatedKey);
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
		if (this.readOnly) {
			throw new SQLException("Transaction is opened for readOnly. write operation not allowed.");
		}
		this.isDirty = true;
		return RdbDriver.doWriteBatch(this.con, sql, paramTypes, params);
	}

	/**
	 * API suitable for a form to persist its tabular data (child form data)
	 * 
	 * @param sql
	 *            to be used for child
	 * @param params
	 *            parameters to be set to the SQL
	 * @param childData
	 *            child data to be persisted
	 * @return array of number of affected rows for each child row
	 * @throws SQLException
	 */
	public int[] formBatch(String sql, FormDbParam[] params, FormData[] childData) throws SQLException {
		if (this.readOnly) {
			throw new SQLException("Transaction is opened for readOnly. write operation not allowed.");
		}
		this.isDirty = true;
		return RdbDriver.doFormBatch(this.con, sql, params, childData);
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

	protected void done(boolean allOk) throws SQLException {
		if (!this.isDirty) {
			return;
		}
		if (allOk) {
			this.con.commit();
		} else {
			this.con.rollback();
		}
	}
}
