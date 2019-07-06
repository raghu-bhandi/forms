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

package org.simplity.fm.validn;

import java.util.List;

import org.simplity.fm.Message;
import org.simplity.fm.form.FormData;

/**
 * a pair of mutually fields that are mutually exclusive. That is, bit should
 * not be specified
 * 
 * @author simplity.org
 *
 */
public class InclusiveValidation implements IValidation {
	private final int mainIndex;
	private final int depndentIndex;
	private final String mainValue;
	private final String fieldName;
	private final String messageId;

	/**
	 * 
	 * @param mainIndex
	 * @param dependentIndex
	 * @param mainValue
	 * @param fieldName
	 * @param messageId
	 */
	public InclusiveValidation(int mainIndex, int dependentIndex, String mainValue, String fieldName,  String messageId) {
		this.mainIndex = mainIndex;
		this.depndentIndex = dependentIndex;
		this.mainValue = mainValue;
		this.fieldName = fieldName;
		this.messageId = messageId;
	}

	@Override
	public boolean isValid(FormData formData, List<Message> messages) {
		Object main = formData.getObject(this.mainIndex);
		/*
		 * rule applicable only if main is non-null
		 */
		if(main == null) {
			return true;
		}
		/*
		 * value constraint on main?
		 */
		if(this.mainValue!= null && main.toString().equals(this.mainValue) == false) {
			return true;
		}
		
		if(formData.getObject(this.depndentIndex) != null) {
			return true;
		}
		messages.add(Message.newFieldError(this.fieldName, this.messageId, null));
		return false;
	}
}
