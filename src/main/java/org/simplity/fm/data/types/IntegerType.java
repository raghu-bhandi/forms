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

import java.util.HashSet;

/**
 * validation parameters for a an integral value
 * 
 * @author simplity.org
 *
 */
public class IntegerType extends DataType {
	private final long minValue;
	private final long maxValue;

	/**
	 * 
	 * @param minValue
	 * @param maxValue
	 * @param errorId
	 */
	public IntegerType(long minValue, long maxValue, String errorId) {
		this.valueType = ValueType.Integer;
		this.minValue = minValue;
		this.maxValue = maxValue;
		if (this.minValue >= 0) {
			this.minLength = ("" + this.minValue).length();
		}
		if (this.maxValue >= 0) {
			this.maxLength = ("" + this.maxValue).length();
		}
		this.messageId = errorId;
	}

	/**
	 * 
	 * @param minValue
	 * @param maxValue
	 * @param errorId
	 * @param validValues
	 */
	public IntegerType(long minValue, long maxValue, String errorId, int[] validValues) {
		this(minValue, maxValue, errorId);
		if (validValues != null && validValues.length > 0) {
			this.validValues = new HashSet<>();
			for (int val : validValues) {
				this.validValues.add(val);
			}
		}
	}

	@Override
	public boolean validate(String value) {
		if (value == null || value.isEmpty()) {
			return true;
		}
		long val = 0;
		try {
			val = Long.parseLong(value, 10);
		} catch (Exception e) {
			return false;
		}
		return this.isOk(val);
	}

	private boolean isOk(long value) {
		return value >= this.minValue && value <= this.maxValue;
	}

	@Override
	public Object getDefaultValue() {
		return 0;
	}

	@Override
	public Object parse(String value) {
		long n = 0;
		try {
			n = Long.parseLong(value, 10);
		} catch (Exception e) {
			return null;
		}
		if (this.isOk(n)) {
			return n;
		}
		return null;
	}
}
