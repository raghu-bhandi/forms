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

import org.simplity.fm.DateUtil;

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
	},
	/**
	 * whole number
	 */
	NUMBER(NumberType.class, 1) {
		@Override
		public Object parse(String value) {
			try {
				return Long.parseLong(value);
			} catch (Exception e) {
				return null;
			}
		}
	},
	/**
	 * 0 is false and 1 is true
	 */
	BOOLEAN(BooleanType.class, 2) {
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
	},
	/**
	 * date, represented in milliseconds from epoch
	 */
	DATE(DateType.class, 3) {
		@Override
		public Object parse(String value) {
			return DateUtil.parseDateWithOptionalTime(value);
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
	 * @return  0-based index that can be used to represent valueType as int..
	 */
	public int getIdx() {
		return this.idx;
	}
}
