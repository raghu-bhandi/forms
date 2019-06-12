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

package org.simplity.fm.io;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * data is store din an RDBMS as Clob. This should work with any of the popular
 * RDBMS.
 * 
 * @author simplity.org
 *
 */
public class RdbStore extends DataStore{
	/**
	 * TODO: to be replaced with dbDriver concept for production
	 */
	private static final String CON_STRING = "";
	private static final String TABLE_NAME = "formData";
	private static final String KEY_NAME = "key";
	private static final String CLOB = "data";
	private static final String WHERE = " where " + KEY_NAME + "=?";
	private static final String INSERT = "insert into " + TABLE_NAME + " values(?,?)";
	private static final String SELECT = "select " + CLOB + " from " + TABLE_NAME + WHERE;
	private static final String DELETE = "delete  " + TABLE_NAME + WHERE;

	@Override
	public boolean retrieve(String id, IoConsumer<Reader> consumer) throws IOException {
		try (Connection con = DriverManager.getConnection(CON_STRING)) {
			PreparedStatement st = con.prepareStatement(SELECT);
			st.setString(1, id);
			// TODO: is this the fastest way to read?
			try (ResultSet rs = st.executeQuery()) {
				if (!rs.next()) {
					return false;
				}
				try (Reader reader = rs.getCharacterStream(1)) {
					consumer.accept(reader);
					return true;
				}
			}
		} catch (SQLException e) {
			throw new IOException(e);
		}
	}

	@Override
	public void Store(String id, IoConsumer<Writer> consumer) throws IOException {
		try (Connection con = DriverManager.getConnection(CON_STRING)) {
			/*
			 * TODO: We have four options. We do not know which one is better.
			 * using simpler one to begin with
			 * 1. delete and then insert.
			 * 2. check existence and then either update or insert.
			 * 3. update. on failure insert.
			 * 4. insert, on failure update.
			 */
			try (PreparedStatement st = con.prepareStatement(DELETE)) {
				st.setString(1, id);
				st.executeUpdate();
				st.close();
			}
			
			Clob clob = con.createClob();
			try (PreparedStatement st = con.prepareStatement(INSERT); Writer writer = clob.setCharacterStream(0)) {
				st.setString(1, id);
				st.setClob(2, clob);
				consumer.accept(writer);
				st.executeUpdate();
			}
		} catch (SQLException e) {
			throw new IOException(e);
		}
	}
}
