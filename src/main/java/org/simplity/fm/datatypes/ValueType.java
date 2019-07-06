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

package org.simplity.fm.datatypes;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;

/**
 * text, number etc..
 * 
 * @author simplity.org
 *
 */
public enum ValueType {
	/**
	 * text
	 */
	TEXT(TextType.class, 0) {
		@Override
		public Object parse(String value) {
			return value;
		}

		@Override
		public void setPsParam(PreparedStatement ps, int position, Object value) throws SQLException {
			ps.setString(position, (String) value);
		}

		@Override
		public Object getFromRs(ResultSet rs, int position) throws SQLException {
			return rs.getString(position);
		}

		@Override
		public boolean isOfRightType(Object value) {
			return value instanceof String;
		}
	},
	/**
	 * whole number
	 */
	INTEGER(NumberType.class, 1) {
		@Override
		public Object parse(String value) {
			try {
				return Long.parseLong(value);
			} catch (Exception e) {
				return null;
			}
		}

		@Override
		public void setPsParam(PreparedStatement ps, int position, Object value) throws SQLException {
			ps.setLong(position, (long) value);
		}

		@Override
		public Object getFromRs(ResultSet rs, int position) throws SQLException {
			return rs.getLong(position);
		}

		@Override
		public boolean isOfRightType(Object value) {
			return value instanceof Long;
		}
	},
	/**
	 * whole number
	 */
	DECIMAL(NumberType.class, 2) {
		@Override
		public Object parse(String value) {
			try {
				return Double.parseDouble(value);
			} catch (Exception e) {
				return null;
			}
		}

		@Override
		public void setPsParam(PreparedStatement ps, int position, Object value) throws SQLException {
			ps.setDouble(position, (Double) value);
		}

		@Override
		public Object getFromRs(ResultSet rs, int position) throws SQLException {
			return rs.getDouble(position);
		}

		@Override
		public boolean isOfRightType(Object value) {
			return value instanceof Double;
		}
	},
	/**
	 * boolean
	 */
	BOOLEAN(BooleanType.class, 3) {
		@Override
		public Object parse(String value) {
			if ("1".equals(value)) {
				return true;
			}
			if ("0".equals(value)) {
				return false;
			}
			String v = value.toUpperCase();
			if ("TRUE".equals(v)) {
				return true;
			}
			if ("FALSE".equals(v)) {
				return true;
			}
			return null;
		}

		@Override
		public void setPsParam(PreparedStatement ps, int position, Object value) throws SQLException {
			ps.setBoolean(position, (boolean) value);
		}

		@Override
		public Object getFromRs(ResultSet rs, int position) throws SQLException {
			return rs.getBoolean(position);
		}

		@Override
		public boolean isOfRightType(Object value) {
			return value instanceof Boolean;
		}
	},
	/**
	 * Date as in calendar. No time, no time-zone. like a date-of-birth. Most
	 * commonly used value-type amongst the three types
	 */
	DATE(DateType.class, 4) {
		@Override
		public Object parse(String value) {
			try {
				return LocalDate.parse(value);
			} catch (Exception e) {
				//
			}
			return null;
		}

		@Override
		public void setPsParam(PreparedStatement ps, int position, Object value) throws SQLException {
			ps.setDate(position, Date.valueOf((LocalDate) value));
		}

		@Override
		public Object getFromRs(ResultSet rs, int position) throws SQLException {
			return rs.getDate(position).toLocalDate();
		}

		@Override
		public boolean isOfRightType(Object value) {
			return value instanceof LocalDate;
		}
	},

	/**
	 * an instant of time. will show up as different date/time .based on the
	 * locale. Likely candidate to represent most "date-time" fields
	 */
	TIMESTAMP(DateType.class, 5) {
		@Override
		public Object parse(String value) {
			try {
				return Instant.parse(value);
			} catch (Exception e) {
				//
			}
			return null;
		}

		@Override
		public void setPsParam(PreparedStatement ps, int position, Object value) throws SQLException {
			ps.setTimestamp(position, Timestamp.from((Instant) value));
		}

		@Override
		public Object getFromRs(ResultSet rs, int position) throws SQLException {
			return rs.getTimestamp(position).toInstant();
		}

		@Override
		public boolean isOfRightType(Object value) {
			return value instanceof Instant;
		}
	};

	private final Class<? extends DataType> dataType;
	private final int idx;

	ValueType(Class<? extends DataType> dataType, int idx) {
		this.dataType = dataType;
		this.idx = idx;
	}

	/**
	 * 
	 * @return extended concrete class for this value type
	 */
	public Class<? extends DataType> getDataTypeClass() {
		return this.dataType;
	}

	/**
	 * this is called ONLY from DataType
	 * 
	 * @param value
	 *            non-null
	 * @return parsed value of this type. null if value is null or the value can
	 *         not be parsed to the desired type
	 */
	public abstract Object parse(String value);

	/**
	 * @return 0-based index that can be used to represent valueType as int..
	 */
	public int getIdx() {
		return this.idx;
	}

	/**
	 * @param ps
	 * @param position
	 * @param value
	 * @throws SQLException
	 */
	public abstract void setPsParam(PreparedStatement ps, int position, Object value) throws SQLException;

	/**
	 * 
	 * @param rs
	 * @param position
	 * @return object returned in the result set
	 * @throws SQLException
	 */
	public abstract Object getFromRs(ResultSet rs, int position) throws SQLException;
	
	/**
	 * id this value an instance of the right type? e.g is it LocalDate for DATE?
	 * @param value
	 * @return true if this is of right type. false otherwise
	 */
	public abstract boolean isOfRightType(Object value);

}
