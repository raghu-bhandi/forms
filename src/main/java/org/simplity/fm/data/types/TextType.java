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

import java.util.regex.Pattern;

/**
 * validation parameters for a text value
 * 
 * @author simplity.org
 *
 */
public class TextType extends DataType {
	private final String regex;

	/**
	 * 
	 * @param minLength
	 * @param maxLength
	 * @param regex
	 * @param errorMessageId
	 */
	public TextType(int minLength, int maxLength, String regex, String errorMessageId) {
		this.valueType = ValueType.Text;
		this.minLength = minLength;
		this.maxLength = maxLength;
		this.messageId = errorMessageId;
		this.regex = regex;
	}

	@Override
	public boolean validate(String value) {
		if (value == null) {
			return true;
		}
		return this.isOk(value);
	}

	private boolean isOk(String value) {
		if (this.regex == null) {
			return true;
		}
		return Pattern.matches(this.regex, value);
	}

	@Override
	public Object getDefaultValue() {
		return "";
	}

	@Override
	public Object parse(String value) {
		if (this.isOk(value)) {
			return value;
		}
		return null;
	}
}
