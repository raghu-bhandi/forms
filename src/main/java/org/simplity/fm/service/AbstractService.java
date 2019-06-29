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
import java.util.List;
import java.util.Map;

import org.simplity.fm.Message;
import org.simplity.fm.form.FormData;
import org.simplity.fm.form.FormOperation;
import org.simplity.fm.form.Form;
import org.simplity.fm.http.LoggedInUser;
import org.simplity.fm.io.DataStore;
import org.simplity.fm.io.IoConsumer;
import org.simplity.fm.io.IoUtil;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * BAse class for all forms based services
 * 
 * @author simplity.org
 *
 */
public abstract class AbstractService implements IService {
	/**
	 * what operation is this service for?
	 */
	protected FormOperation operation;
	/**
	 * null if no input is expected
	 */
	protected Form form;

	/**
	 * a simple service that just retrieves the required form.
	 * 
	 * @param formStructure
	 * 
	 */
	public AbstractService(Form formStructure) {
		this.form = formStructure;
	}

	@Override
	public ServiceResult serve(LoggedInUser user, ObjectNode json, Writer writer) throws Exception {
		/*
		 * extended class will over-ride this to provide required functionality
		 */
		Message[] msgs = { Message.newError(MSG_NOT_AUTHORIZED) };
		return new ServiceResult(msgs, false);
	}

	@Override
	public ServiceResult serve(LoggedInUser user, Map<String, String> keyFields, Writer writer) throws Exception {
		/*
		 * extended class will over-ride this to provide required functionality
		 */
		Message[] msgs = { Message.newError(MSG_NOT_AUTHORIZED) };
		return new ServiceResult(msgs, false);
	}

	/**
	 * @return the operation
	 */
	public FormOperation getOperation() {
		return this.operation;
	}

	protected FormData newForm(LoggedInUser user, Map<String, String> keyFields, List<Message> messages) {
		FormData formData = this.form.newFormData(this.operation);
		formData.setOwner(user);
		formData.loadKeys(keyFields, messages);
		if (messages.size() > 0) {
			return null;
		}

		return formData;
	}

	protected FormData newForm(LoggedInUser user, ObjectNode json, List<Message> messages) {
		FormData formData = this.form.newFormData(this.operation);
		formData.setOwner(user);
		formData.loadKeys(json, messages);
		if (messages.size() > 0) {
			return null;
		}

		return formData;
	}

	/**
	 * method to retrieve form data. Bit complex because we are trying to re-use
	 * this in different scenarios
	 * 
	 * @param user
	 *            non-null
	 * @param formData
	 *            non-null
	 * @param messages
	 *            non-null
	 * @param writer
	 *            nullable. non-null if the form is to be written out. null if
	 *            form is to be populated with data and returned
	 * @return true of all ok. false in case of any problem, and an error
	 *         message is added to the list
	 * @throws IOException
	 */
	protected boolean retrieveForm(LoggedInUser user, FormData formData, List<Message> messages, Writer writer)
			throws IOException {
		String key = formData.getDocumentId();
		if (this.hasAccess(user, formData) == false) {
			this.addMessage(MSG_NOT_AUTHORIZED, messages);
			return false;
		}

		if (!this.processForm(Form.PRE_GET, formData, messages)) {
			return false;
		}

		/*
		 * if there is no Post processing, we can stream the content directly.
		 */
		IFormProcessor processor = this.form.getFormProcessor(Form.POST_GET);
		if (processor == null && writer != null) {
			if (this.streamFromStore(key, writer)) {
				return true;
			}
			// else we will work with fresh form
		} else {
			this.loadFromStore(key, formData);
		}

		if (processor != null) {
			if (!processor.process(formData, messages)) {
				return false;
			}
		}
		if (writer != null) {
			formData.serializeAsJson(writer);
		}
		return true;
	}

	/**
	 * @param key
	 * @param formData
	 * @return true if the data was indeed retrieved and loaded into the form
	 * @throws IOException
	 */
	protected boolean loadFromStore(String key, FormData formData) throws IOException {
		/*
		 * small time cheating with lambda, to get a value set there..
		 */
		ObjectNode[] nodes = new ObjectNode[1];
		boolean ok = DataStore.getStore().retrieve(key, new IoConsumer<Reader>() {

			@Override
			public void accept(Reader reader) throws IOException {
				JsonNode node = new ObjectMapper().readTree(reader);
				if (node != null && node.getNodeType() == JsonNodeType.OBJECT) {
					nodes[0] = (ObjectNode) node;
				}
			}
		});
		if (ok) {
			ObjectNode node = nodes[0];
			if (node != null) {
				formData.load(node);
				return true;
			}
		}
		return false;
	}

	/**
	 * retrieve the form directly into the stream
	 * 
	 * @param key
	 * @return
	 * @throws IOException
	 */
	protected boolean streamFromStore(String key, Writer writer) throws IOException {
		return DataStore.getStore().retrieve(key, new IoConsumer<Reader>() {

			@Override
			public void accept(Reader reader) throws IOException {
				IoUtil.copy(reader, writer);
			}
		});
	}

	protected ServiceResult failed(List<Message> messages) {
		return new ServiceResult(messages.toArray(new Message[0]), false);
	}

	protected ServiceResult failed(String messageId) {
		Message[] msgs = { Message.newError(messageId) };
		return new ServiceResult(msgs, false);
	}

	protected ServiceResult succeeded() {
		return new ServiceResult(null, true);
	}

	/**
	 * checks if the logged in user is the owner of the form
	 * 
	 * @param user
	 * @param formData
	 * @return true if the logged in user can access this for, for this
	 *         operation.
	 */
	protected boolean hasAccess(LoggedInUser user, FormData formData) {
		return formData.isOwner(user);
	}

	protected void addMessage(String messageId, List<Message> messages) {
		messages.add(Message.newError(messageId));
	}

	protected boolean processForm(int processType, FormData formData, List<Message> messages) {
		IFormProcessor processor = this.form.getFormProcessor(processType);
		if (processor == null) {
			return true;
		}
		return processor.process(formData, messages);
	}
}
