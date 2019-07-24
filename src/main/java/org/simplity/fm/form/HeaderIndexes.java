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

/**
 * @author simplity.org
 *
 */
public class HeaderIndexes {
	/**
	 * index of the field that has the form name
	 */
	public final int formNameIndex;
	/**
	 * index for the field that has the serialized data
	 */
	public final int formDataIndex;
	/**
	 * index for the field that has the form operation
	 */
	public final int formOperationIndex;
	/**
	 * index for the field that indicates whether the form is submitted or not
	 */
	public final int isSubmittedIndex;
	
	/**
	 * create the immutable data structure
	 * @param formNameIndex
	 * @param formDataIndex
	 * @param formOperationIndex
	 * @param isSubmittedIndex
	 */
	public HeaderIndexes(int formNameIndex, int formDataIndex, int formOperationIndex, int isSubmittedIndex) {
		this.formNameIndex = formNameIndex;
		this.formDataIndex = formDataIndex;
		this.formOperationIndex = formOperationIndex;
		this.isSubmittedIndex = isSubmittedIndex;
	}
}
