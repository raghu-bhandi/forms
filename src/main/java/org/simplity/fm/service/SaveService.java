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

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.simplity.fm.FormStorage;
import org.simplity.fm.Message;
import org.simplity.fm.form.FormData;
import org.simplity.fm.form.FormOperation;
import org.simplity.fm.form.Form;
import org.simplity.fm.http.LoggedInUser;
import org.simplity.fm.io.IFormStorage;

import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Simple service that just saves the form with no saves the form received from
 * 
 * @author simplity.org
 *
 */
public class SaveService extends AbstractService {
	/**
	 * a simple service that just saves the form. output form is null;
	 * 
	 * @param formStructure
	 */
	public SaveService(Form formStructure) {
		super(formStructure);
		this.operation = FormOperation.SAVE;
	}

	@Override
	public ServiceResult serve(LoggedInUser user, ObjectNode json, Writer writer) throws Exception {
		List<Message> messages = new ArrayList<>();
		FormData formData = this.newForm(user, json, messages);
		if (formData == null) {
			return this.failed(messages);
		}

		if(this.doSave(formData, user, json, true, messages)) {
			return this.succeeded();
		}
		return this.failed(messages);
	}
	
	protected boolean doSave(FormData formData, LoggedInUser user, ObjectNode json, boolean allFieldsAreOptional, List<Message> messages) throws Exception {

		/*
		 * TODO: if this is partial-save, then we should load existing data from
		 * store before, extracting data from user pay load
		 */
		boolean ok = this.retrieveForm(user, formData, messages, null);
		if (!ok) {
			// access issues..
			return false;
		}

		/*
		 * now load data coming from client. It could be just a section
		 */
		formData.validateAndLoad(json, allFieldsAreOptional, messages);
		if (messages.size() > 0) {
			return false;
		}

		if(!this.processForm(Form.PRE_SAVE, formData, messages)) {
			return false;
		}

		IFormStorage store = FormStorage.getStore();
		store.store(formData.getDocumentId(), new Consumer<Writer>() {

			@Override
			public void accept(Writer w)  {
				try {
					formData.serializeAsJson(w);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		return this.processForm(Form.POST_SAVE, formData, messages);
	}
}
