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

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * interface for a class that wants to write/update/delete fromthe dta base
 * 
 * @author simplity.org
 *
 */
public interface IDbWriter {

	/**
	 * 
	 * @return the prepared statement that can be used to insert/update/delete
	 *         rows. null to indicate that the write operation be aborted by
	 *         design
	 */
	public String getPreparedStatement();

	/**
	 * method that is invoked by the db driver to populate the actual prepared.
	 * 
	 * @param ps
	 *            prepared statement to which params are to be set
	 * @throws SQLException
	 */
	public void setParams(PreparedStatement ps) throws SQLException;

	/**
	 * 
	 * @return if true, sql exception on execution is assumed to be for now rows
	 *         for the desired operation. In such a case, 0 is returned after
	 *         such an exception instead of throwing an exception.
	 */
	public boolean toTraetSqlExceptionAsNoRowsAffected();
}