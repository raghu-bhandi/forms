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

package org.simplity.fm.form;

import java.sql.SQLException;

import org.simplity.fm.rdb.DbHandle;
import org.simplity.fm.rdb.IDbClient;
import org.simplity.fm.rdb.RdbDriver;

/**
 * @author simplity.org
 *
 */
public class SqlReader {
	protected final Form form;
	protected final String sql;
	protected final Object[] whereValues;
	protected final FormDbParam[] whereParams;

	/**
	 * constructor with all attributes
	 * @param form 
	 *
	 * @param sql
	 *            non-null prepared statement
	 * @param whereParams
	 *            non-null array with valueTypes of parameters in the SQL in the
	 *            right order. Empty array in case the SQL has no parameters.
	 * @param whereValues
	 *            non-null array of values for the parameters in the sql. Each
	 *            element should be of the right type for the corresponding
	 *            parameter.
	 * 
	 */
	public SqlReader(Form form, String sql, FormDbParam[] whereParams, Object[] whereValues) {
		this.form = form;
		this.sql = sql;
		this.whereParams = whereParams;
		this.whereValues = whereValues;
	}

	/**
	 * to be used when read is part of a transaction, or a service has several
	 * reads. (this is not a one-off db access). This method will have to be
	 * invoked from the lambda that uses the dbHandle
	 * 
	 * @param handle
	 * @return null if read did not succeed. a row of data form the DB.
	 * @throws SQLException
	 */
	public FormData read(DbHandle handle) throws SQLException {
		int nbrFields = this.form.fields.length;
		Object[] values = handle.readFormRow(this.sql, this.whereParams,this.whereValues, this.form.dbMetaData.selectParams, nbrFields);
		if(values == null) {
			return null;
		}
		return new FormData(this.form, values, null);
	}

	/**
	 * to be used when read is part of a transaction, or a service has several
	 * reads. (this is not a one-off db access). This method will have to be
	 * invoked from the lambda that uses the dbHandle
	 * 
	 * @param handle
	 * @return null if no rows read. rows of output, each row having an array of
	 *         objects extracted from the DB
	 * @throws SQLException
	 */
	public FormData[] filter(DbHandle handle) throws SQLException {
		int nbrFields = this.form.fields.length;
		Object[][] values = handle.readFormRows(this.sql, this.whereParams, this.whereValues, this.form.dbMetaData.selectParams, nbrFields);
		if(values == null || values.length == 0) {
			return null;
		}
		FormData[] result = new FormData[values.length];
		int idx = 0;
		for(Object[] row : values) {
			result[idx] = new FormData(this.form, row, null);
			idx++;
		}
		return result;
	}

	/**
	 * to be used if this operation is one-off in a service, and NOT part of a
	 * transaction
	 * 
	 * @return null if read did not succeed. a row of data form the DB.
	 * @throws SQLException
	 */
	public FormData read() throws SQLException {
		FormData[] result = new FormData[1];
		RdbDriver.getDriver().transact(new IDbClient() {

			@Override
			public boolean transact(DbHandle handle) throws SQLException {
				result[0] = SqlReader.this.read(handle);
				return true;
			}
		}, true);

		return result[0];
	}

	/**
	 * to be used if this operation is one-off in a service, and NOT part of a
	 * transaction
	 * 
	 * @return null if no rows read. rows of output, each row having an array of
	 *         objects extracted from the DB
	 * @throws SQLException
	 */
	public FormData[] filter() throws SQLException {
		FormData[][] result = new FormData[1][];
		RdbDriver.getDriver().transact(new IDbClient() {

			@Override
			public boolean transact(DbHandle handle) throws SQLException {
				result[0] = SqlReader.this.filter(handle);
				return true;
			}
		}, true);

		return result[0];
	}
}
