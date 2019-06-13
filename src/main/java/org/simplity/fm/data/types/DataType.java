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
package org.simplity.fm.data.types;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.simplity.fm.DateUtil;

/**
 * @author simplity.org
 *
 */
public abstract class DataType {
	protected String messageId;
	protected int minLength;
	protected int maxLength;
	protected ValueType valueType;
	protected Set<Object> validValues;

	/**
	 * 
	 * @param values
	 *            of the form "a,b,c,d" or "1:red,2:black,3:blue"
	 */
	protected void setValidValues(String values) {
		if (values == null || values.isEmpty()) {
			return;
		}
		this.validValues = new HashSet<Object>();
		for (String part : values.split("'")) {
			int idx = part.indexOf(':');
			if (idx != -1) {
				part = part.substring(0, idx);
			}
			this.validValues.add(part.trim());
		}
	}

	/**
	 * @return unique error message id that has the actual error message to be
	 *         used if a value fails validation
	 */
	public String getMessageId() {
		return this.messageId;
	}

	/**
	 * 
	 * @param value
	 * @return true if the value passes validation. false otherwise.
	 */
	public final boolean isValid(String value) {
		if (this.validValues != null) {
			return this.validValues.contains(value);
		}
		if (value == null) {
			return true;
		}
		int n = value.length();
		if (n < this.minLength || (this.maxLength > 0 && n > this.maxLength)) {
			return false;
		}
		return this.validate(value);
	}

	protected abstract boolean validate(String value);

	/**
	 * @return the valueType
	 */
	public ValueType getValueType() {
		return this.valueType;
	}

	/**
	 * @return default non-null value of this type. empty string for String, 0
	 *         number and date, false for boolean
	 */
	public abstract Object getDefaultValue();

	/**
	 * @param value
	 *            non-null value to be parsed into the right type after
	 *            validation
	 * @return null if the validation fails. object of the right type for the
	 *         field.
	 */
	public abstract Object parse(String value);

	/**
	 * get text value of this object
	 * 
	 * @param value
	 * @return null if object is null. else a string representation of value.
	 *         Date is represented standard UTC format
	 */
	public String toTextValue(Object value) {
		if (value == null) {
			return null;
		}
		if (value instanceof String) {
			return (String) value;
		}
		if (value instanceof Number) {
			return value.toString();
		}
		if (value instanceof Boolean) {
			if ((Boolean) value) {
				return "true";
			}
			return "false";
		}
		if (value instanceof Date) {
			return DateUtil.formatDateTime((Date) value);
		}
		return value.toString();
	}
}
