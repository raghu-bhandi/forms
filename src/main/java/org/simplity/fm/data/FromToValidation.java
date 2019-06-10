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

package org.simplity.fm.data;

import java.util.List;

import org.simplity.fm.IForm;
import org.simplity.fm.Message;

/**
 * @author simplity.org
 *
 */
public class FromToValidation implements IFormValidation{

	private final String fromName;
	private final String toName;
	private final boolean equalValueOk;
	private final String errorMessageId;
	
	/**
	 * 
	 * @param fromName
	 * @param toName
	 * @param equalValueOk 
	 * @param errorMessageId 
	 */
	public FromToValidation(String fromName, String toName, boolean equalValueOk, String errorMessageId) {
		this.fromName = fromName;
		this.toName = toName;
		this.equalValueOk = equalValueOk;
		this.errorMessageId = errorMessageId;
	}
	@Override
	public boolean validate(IForm form, List<Message> messages) {
		long fromValue = form.getLongValue(this.fromName);
		long toValue = form.getLongValue(this.toName);
		if(fromValue == 0 || toValue == 0) {
			return true;
		}if(this.equalValueOk) {
			if(fromValue <= toValue) {
				return true;
			}
		}else {
			if(fromValue < toValue) {
				return true;
			}
		}
		messages.add(Message.getValidationMessage(this.fromName, this.errorMessageId));
		return false;
	}

}
