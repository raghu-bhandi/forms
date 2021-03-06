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

package org.simplity.fm.form;

import org.simplity.fm.datatypes.ValueType;

/**
 * @author simplity.org
 *
 */
public class FormDbParam {
	/**
	 * 0-based index in the form-fields that this parameter corresponds to (for getting/setting value in form data array)
	 */
	public final int idx;
	/**
	 * value type of this parameter based on which set/get method is ssued
	 * on the statement
	 */
	public final ValueType valueType;

	/**
	 * create this parameter as an immutable data structure
	 * 
	 * @param idx
	 * @param valueType
	 */
	public FormDbParam(int idx, ValueType valueType) {
		this.idx = idx;
		this.valueType = valueType;
	}

}
