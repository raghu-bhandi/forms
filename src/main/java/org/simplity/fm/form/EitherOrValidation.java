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

import java.util.List;

import org.simplity.fm.Message;

/**
 * a pair of mutually fields that are mutually exclusive. That is, bit should
 * not be specified
 * 
 * @author simplity.org
 *
 */
public class EitherOrValidation extends FormValidation {


	/**
	 * 
	 * @param fieldNam1
	 * @param fieldName2
	 * @param boolValue
	 * @param errorMessageId
	 */
	public EitherOrValidation(String fieldNam1, String fieldName2, boolean boolValue, String errorMessageId) {
		super(fieldNam1, fieldName2, boolValue, errorMessageId);
	}

	@Override
	public boolean isValid(Form form, List<Message> mesages) {
		// TODO Auto-generated method stub
		return false;
	}
}
