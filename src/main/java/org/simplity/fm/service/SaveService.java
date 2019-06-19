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

import org.simplity.fm.Message;
import org.simplity.fm.form.Form;
import org.simplity.fm.form.FormOperation;
import org.simplity.fm.form.FormStructure;
import org.simplity.fm.http.LoggedInUser;
import org.simplity.fm.io.DataStore;
import org.simplity.fm.io.IoConsumer;

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
	public SaveService(FormStructure formStructure) {
		super(formStructure);
		this.operation = FormOperation.SAVE;
	}

	@Override
	public ServiceResult serve(LoggedInUser user, ObjectNode json, Writer writer) throws Exception {
		List<Message> messages = new ArrayList<>();
		Form form = this.newForm(user, json, messages);
		if (form == null) {
			return this.failed(messages);
		}

		if(this.doSave(form, user, json, messages)) {
			return this.succeeded();
		}
		return this.failed(messages);
	}
	
	protected boolean doSave(Form form, LoggedInUser user, ObjectNode json, List<Message> messages) throws Exception {

		/*
		 * TODO: if this is partial-save, then we should load existing data from
		 * store before, extracting data from user pay load
		 */
		boolean ok = this.retrieveForm(user, form, messages, null);
		if (!ok) {
			// access issues..
			return false;
		}

		/*
		 * now load data coming from client. It could be just a section
		 */
		form.validateAndLoad(json, messages);
		if (messages.size() > 0) {
			return false;
		}

		if(!this.processForm(FormStructure.PRE_SAVE, form, messages)) {
			return false;
		}

		DataStore store = DataStore.getStore();
		store.Store(form.getDocumentId(), new IoConsumer<Writer>() {

			@Override
			public void accept(Writer w) throws IOException {
				form.serializeAsJson(w);
			}
		});

		return this.processForm(FormStructure.POST_SAVE, form, messages);
	}
}
