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

import java.util.Date;
import java.util.List;

import org.simplity.fm.Message;
import org.simplity.fm.form.FormData;

/**
 * TODO: hard coded for date. To be re-factored for all types
 * 
 * @author simplity.org
 *
 */
public class FromToValidation implements IValidation {
	private final String fieldName;
	private final int fromIndex;
	private final int toIndex;
	private final boolean equalOk;
	private final String messageId;

	/**
	 * 
	 * @param fromIndex
	 * @param toIndex
	 * @param equalOk
	 * @param fieldName 
	 * @param messageId
	 */
	public FromToValidation(int fromIndex, int toIndex, boolean equalOk, String fieldName, String messageId) {
		this.fromIndex = fromIndex;
		this.toIndex = toIndex;
		this.equalOk = equalOk;
		this.fieldName = fieldName;
		this.messageId = messageId;
	}

	@Override
	public boolean isValid(FormData formData, List<Message> messages) {
		Object fm = formData.getValue(this.fromIndex);
		Object to = formData.getValue(this.toIndex);
		if(fm == null || to == null) {
			return true;
		}
		
		boolean ok = false;
		if(fm instanceof Long) {
			ok = this.longOk((long) fm, (long)to);
		}else if(fm instanceof Date) {
			ok = this.dateOk((Date)fm, (Date)to);
		}else {
			ok = this.textOk(fm.toString(), to.toString());
		}
		if(ok) {
			return true;
		}
		
		messages.add(Message.newFieldError(this.fieldName, this.messageId, null));
		return false;
	}

	private boolean longOk(long fm, long to) {
		if (this.equalOk) {
			return to >= fm;
		}
		return to > fm;
	}

	private boolean dateOk(Date fm, Date to) {
		if (this.equalOk) {
			return !fm.after(to);
		}
		return to.after(fm);
	}

	private boolean textOk(String fm, String to) {
		int n = to.compareToIgnoreCase(fm);
		if (this.equalOk) {
			return n >= 0;
		}
		return n > 0;
	}
}