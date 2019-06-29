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

/**
 * validation parameters for a an integral value
 * 
 * @author simplity.org
 *
 */
public class NumberType extends DataType {
	private final long minValue;
	private final long maxValue;

	/**
	 * 
	 * @param name 
	 * @param errorMessageId
	 * @param minValue
	 * @param maxValue
	 */
	public NumberType(String name, String errorMessageId, long minValue, long maxValue) {
		this.valueType = ValueType.NUMBER;
		this.messageId = errorMessageId;
		this.minValue = minValue;
		this.maxValue = maxValue;

		if (this.minValue >= 0) {
			this.minLength = ("" + this.minValue).length();
		}
		if (this.maxValue >= 0) {
			this.maxLength = ("" + this.maxValue).length();
		}
	}

	@Override
	protected boolean isOk(Object val) {
		long value = (Long) val;
		return value >= this.minValue && value <= this.maxValue;
	}
}
