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

package org.simplity.fm.service;

import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.simplity.fm.Message;
import org.simplity.fm.data.Form;
import org.simplity.fm.data.FormOperation;
import org.simplity.fm.data.FormStructure;
import org.simplity.fm.http.LoggedInUser;

import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Simple service that just saves the form with no saves the form received from
 * 
 * @author simplity.org
 *
 */
public class SubmitService extends SaveService {

	/**
	 * @param formStructure
	 */
	public SubmitService(FormStructure formStructure) {
		super(formStructure);
		this.operation = FormOperation.SUBMIT;
	}

	@Override
	public ServiceResult serve(LoggedInUser user, ObjectNode json, Writer writer) throws Exception {
		List<Message> messages = new ArrayList<>();
		Form form = this.newForm(user, json, messages);
		if (form == null) {
			return this.failed(messages);
		}

		boolean ok = this.doSave(form, user, json, messages);
		if(!ok) {
			return this.failed(messages);
		}

		if(!this.processForm(FormStructure.PRE_SUBMIT, form, messages)) {
			return this.failed(messages);
		}
		

		/*
		 * TODO : do whatever we have to do to submit this form...
		 */
		if(!this.processForm(FormStructure.POST_SUBMIT, form, messages)) {
			return this.failed(messages);
		}

		return this.succeeded();
	}
	
	
}
