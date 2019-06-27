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

import java.util.Set;
import java.util.regex.Pattern;

/**
 * validation parameters for a text value
 * 
 * @author simplity.org
 *
 */
public class TextType extends DataType {
	private final String regex;
	private final Set<String> validValues;

	/**
	 * 
	 * @param errorMessageId
	 * @param minLength
	 * @param maxLength
	 * @param regex
	 * @param valueList
	 */
	public TextType(String errorMessageId, int minLength, int maxLength, String regex, Set<String> valueList) {
		this.valueType = ValueType.TEXT;
		this.minLength = minLength;
		this.maxLength = maxLength;
		this.messageId = errorMessageId;
		if (regex == null || regex.isEmpty()) {
			this.regex = null;
		} else {
			this.regex = regex;
		}
		this.validValues = valueList;
	}

	@Override
	protected boolean isOk(Object val) {
		String value = val.toString();
		if (this.validValues != null) {
			return this.validValues.contains(value);
		}
		int len = value.length();
		if (len < this.minLength || (this.maxLength > 0 && len > this.maxLength)) {
			return false;
		}
		if (this.regex == null) {
			return true;
		}
		return Pattern.matches(this.regex, value);
	}
}
