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
import java.util.Map;

import org.simplity.fm.IForm;
import org.simplity.fm.Message;
import org.simplity.fm.MessageType;
import org.simplity.fm.data.FormStructure;
import org.simplity.fm.http.LoggedInUser;

import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * a service that has no additional work (other than the validation of input
 * that is already done by <code>IForm</code>
 * 
 * @author simplity.org
 *
 */
public abstract class AbstractService implements IService {
	protected static final String MSG_NOT_AUTHORIZED = null;
	protected static final String MSG_INTERNAL_ERROR = null;

	/**
	 * null if no input is expected
	 */
	protected FormStructure formStructure;


	@Override
	public ServiceResult serve(LoggedInUser user, Map<String, String> keyFields, Writer writer) {
		List<Message> errors = new ArrayList<>();
		IForm input = this.formStructure.newForm();
		input.loadKeys(keyFields, errors);
		if(errors.size() > 0) {
			return new ServiceResult(errors.toArray(new Message[0]), false);
		}
		return this.exec(user, input, writer);
	}

	@Override
	public ServiceResult serve(LoggedInUser user, ObjectNode json, Writer writer) {
		List<Message> errors = new ArrayList<>();
		IForm input = this.formStructure.newForm();
		input.validateAndLoad(json, errors);
		if(errors.size() > 0) {
			return new ServiceResult(errors.toArray(new Message[0]), false);
		}
		return this.exec(user, input, writer);
	}

	private ServiceResult exec(LoggedInUser user, IForm input, Writer writer) {
		String key = input.getDocumentId();
		Message msg = null;
		if (this.hasAccess(user, key)) {
			try {
				return this.processForm(user, input, writer);
			} catch (Exception e) {
				msg = Message.getGenericMessage(MessageType.Error, MSG_INTERNAL_ERROR, null, null, 0);
			}
			msg =  Message.getGenericMessage(MessageType.Error, MSG_NOT_AUTHORIZED, null, null, 0);
		}
	Message[] msgs = {msg};
	return new ServiceResult(msgs , false);
		
	}
	
	/**
	 * let the concrete service process the form and return its result
	 * 
	 * @param user
	 *            non-null logged in user
	 * @param inputForm
	 *            null if this service is not expecting any input
	 * @throws Exception
	 *             general catch-all
	 * @return service result
	 */
	protected abstract ServiceResult processForm(LoggedInUser user, IForm inputForm, Writer writer)
			throws Exception;

	/**
	 * let the concrete service check if the user has access to this form
	 * 
	 * @param user
	 * @param key
	 * @return true if ok, false if user has no access to this document
	 */
	protected abstract boolean hasAccess(LoggedInUser user, String key);
}
