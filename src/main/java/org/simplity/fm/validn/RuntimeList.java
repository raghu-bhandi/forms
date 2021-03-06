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

package org.simplity.fm.validn;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.simplity.fm.rdb.IDbReader;
import org.simplity.fm.rdb.RdbDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * represents meta data for a value list to be fetched at run time
 * 
 * @author simplity.org
 *
 */
public class RuntimeList implements IValueList{
	protected static final Logger logger = LoggerFactory.getLogger(RuntimeList.class);
	protected String name;
	protected String listSql;
	protected String checkSql;
	protected boolean hasKey;
	protected boolean keyIsNumeric;

	@Override
	public String getName() {
		return this.name;
	}
	@Override
	public boolean isKeyBased() {
		return this.hasKey;
	}

	@Override
	public String[][] getList(final String key) {
		if (this.hasKey) {
			if (key == null) {
				logger.error("Key should have value for list {}", this.name);
				return null;
			}
		}
		long l = 0;
		if(this.keyIsNumeric) {
			try {
				l = Long.parseLong(key);
			}catch(Exception e) {
				logger.error("Key should be numeric value for list {} but we got {}", this.name, key);
				return null;
			}
		}
		final long numericValue = l;
		final List<String[]> result = new ArrayList<>();

		try {
			RdbDriver.getDriver().read(new IDbReader() {
				@Override
				public String getPreparedStatement() {
					return RuntimeList.this.listSql;
				}

				@Override
				public void setParams(PreparedStatement ps) throws SQLException {
					if (RuntimeList.this.hasKey) {
						if(RuntimeList.this.keyIsNumeric) {
							ps.setLong(1, numericValue);
						}else {
							ps.setString(1, key);
						}
					}
				}

				@Override
				public boolean readARow(ResultSet rs) throws SQLException {
					String[] row = { rs.getString(1), rs.getString(2) };
					result.add(row);
					return true;
				}

			});
		} catch (SQLException e) {
			String msg = e.getMessage();
			logger.error("Error while getting values for list {}. ERROR: {} ", this.name, msg);
			return null;
		}
		if (result.size() == 0) {
			logger.error("No data found for list {} with key {}", this.name, key);
			return null;
		}
		return result.toArray(new String[0][]);
	}

	@Override
	public boolean isValid(final Object fieldValue, final Object keyValue) {
		if (this.hasKey) {
			if (keyValue == null) {
				logger.error("Key should have value for list {}", this.name);
				return false;
			}
		}

		final boolean[] result = new boolean[1];

		try {
			RdbDriver.getDriver().read(new IDbReader() {
				@Override
				public String getPreparedStatement() {
					return RuntimeList.this.checkSql;
				}

				@Override
				public void setParams(PreparedStatement ps) throws SQLException {
					if(fieldValue instanceof String) {
						ps.setString(1, (String)fieldValue);
					}else {
						ps.setLong(1, (Long)fieldValue);
					}
					if (RuntimeList.this.hasKey) {
						if(keyValue instanceof String) {
							ps.setString(2, (String)keyValue);
						}else {
							ps.setLong(2, (Long)keyValue);
						}
					}
				}

				@Override
				public boolean readARow(ResultSet rs) throws SQLException {
					result[0] = true;
					return false;
				}

			});
		} catch (SQLException e) {
			String msg = e.getMessage();
			logger.error("Error while getting values for list {}. ERROR: {} ", this.name, msg);
			return false;
		}
		return result[0];
	}
}
