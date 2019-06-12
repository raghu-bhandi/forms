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
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.simplity.fm.ApplicationError;
import org.simplity.fm.IForm;
import org.simplity.fm.Message;
import org.simplity.fm.MessageType;
import org.simplity.fm.data.FormStructure;
import org.simplity.fm.http.LoggedInUser;
import org.simplity.fm.io.DataStore;
import org.simplity.fm.io.IoConsumer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Simple service that just saves the form with no saves the form received from
 * 
 * @author simplity.org
 *
 */
public class SaveService implements IService {

	/**
	 * null if no input is expected
	 */
	protected FormStructure formStructure;

	/**
	 * a simple service that just saves the form. output form is null;
	 * 
	 * @param formStructure
	 */
	public SaveService(FormStructure formStructure) {
		this.formStructure = formStructure;
	}

	@Override
	public ServiceResult serve(LoggedInUser user, Map<String, String> keyFields, Writer writer) {
		/*
		 * should not be called with pay-load
		 */
		Message[] msgs = { Message.getGenericMessage(MessageType.Error, MSG_NOT_AUTHORIZED, null, null, 0) };
		return new ServiceResult(msgs, false);
	}

	@Override
	public ServiceResult serve(LoggedInUser user, ObjectNode json, Writer writer) {
		List<Message> errors = new ArrayList<>();
		IForm form = this.formStructure.newForm();
		form.loadKeys(json, errors);
		if (errors.size() > 0) {
			return this.returnWithError(errors);
		}

		String key = form.getDocumentId();
		if (this.hasAccess(user, key) == false) {
			errors.add(Message.getGenericMessage(MessageType.Error, MSG_NOT_AUTHORIZED, null, null, 0));
			return this.returnWithError(errors);
		}

		/*
		 * load existing form first.
		 */
		DataStore store = DataStore.getStore();
		try {
			store.retrieve(key, new IoConsumer<Reader>() {

				@Override
				public void accept(Reader reader) throws IOException {
					JsonNode node = new ObjectMapper().readTree(reader);
					if (node.getNodeType() != JsonNodeType.OBJECT) {
						throw new ApplicationError("File content is not a JSON for id " + key);
					}
					form.load((ObjectNode) node);
				}
			});

			/*
			 * now load data coming from client. It could be just a section
			 */
			form.validateAndLoad(json, errors);
			if (errors.size() > 0) {
				return this.returnWithError(errors);
			}

			store.Store(key, new IoConsumer<Writer>() {

				@Override
				public void accept(Writer w) throws IOException {
					form.serializeAsJson(w);
				}
			});
			return new ServiceResult(null, true);
		} catch (IOException e) {
			errors.add(Message.getGenericMessage(MessageType.Error, MSG_INTERNAL_ERROR, null, null, 0));
			return this.returnWithError(errors);
		}
	}

	private ServiceResult returnWithError(List<Message> errors) {
		return new ServiceResult(errors.toArray(new Message[0]), false);
	}

	/**
	 * 
	 * @param user
	 * @param key
	 * @return
	 */
	private boolean hasAccess(LoggedInUser user, String key) {
		// TODO implement the logic to check if this user has write access to
		// this form
		return true;
	}

}
