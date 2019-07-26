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

import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.simplity.fm.Config;
import org.simplity.fm.Forms;
import org.simplity.fm.Message;
import org.simplity.fm.form.FormData;
import org.simplity.fm.form.FormOperation;
import org.simplity.fm.form.HeaderData;
import org.simplity.fm.form.Form;
import org.simplity.fm.http.Http;
import org.simplity.fm.http.LoggedInUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public class ManageForm implements IService{
	private static ManageForm instance = new ManageForm();
	private static final Logger logger = LoggerFactory.getLogger(ManageForm.class);

	/**
	 * 
	 * @return non-null instance
	 */
	public static ManageForm getInstance() {
		return instance;
	}

	private ManageForm() {
		//
	}

	@Override
	public ServiceResult serve(LoggedInUser user, ObjectNode payload, Writer writer)
			throws Exception {
		HeaderData headerData = Config.getConfig().newHeaderData();
		if(headerData == null) {
			logger.error("Header Form is not configured. FormService can not operate.");
			return this.failed(IService.MSG_INTERNAL_ERROR);
		}
		
		JsonNode node = payload.get(Http.FORM_HEADER_TAG);
		if (node == null || node.getNodeType() != JsonNodeType.OBJECT) {
			logger.error("Payload has to have a header object named {} ", Http.FORM_HEADER_TAG);
			return this.failed(IService.MSG_INVALID_DATA);
		}

		List<Message> msgs = new ArrayList<>();

		headerData.validateAndLoad((ObjectNode) node, false, msgs);
		if(msgs.size() > 0) {
			logger.error("Header data is invalid");
			return this.failed(msgs);
		}

		if(headerData.isOwner(user) == false) {
			logger.error("Logged in user {} is not authorized to manage this form with {} as userId ", user.getUserId(), headerData.getUserId());
			return this.failed(IService.MSG_NOT_AUTHORIZED);
		}
		
		String formName = headerData.getFormName();
		Form form = Forms.getForm(formName);
		if(form == null) {
			logger.error("Unable to get form {} ", formName);
			msgs.add(Message.newError("invalidFormName"));
			return this.failed(msgs);
		}

		FormOperation op = headerData.getFormOperation();
		if(op == null) {
			logger.error("Header has an invalid operation. it should be get, save or submit");
			msgs.add(Message.newError("invalidOperation"));
			return this.failed(msgs);
		}
		
		/*
		 * this is the actual form that is being managed (saved/submitted)
		 */
		FormData fd = form.newFormData();
		if(op == FormOperation.GET) {
			if(headerData.fetchFromDb()) {
				logger.info("Saved form retrieved and sent to the client");
				node = new ObjectMapper().readTree(headerData.getFormData());
				fd.load((ObjectNode)node);
			}else {
				logger.info("New form created and sent to the client");
				fd.prefill();
			}
			//copy profile fields
			fd.serializeAsJson(writer);
			return this.succeeded();
		}
		
		node = payload.get(Http.FORM_DATA_TAG);
		if (node == null || node.getNodeType() != JsonNodeType.OBJECT) {
			logger.error("Payload has to have a form data object named {}", Http.FORM_DATA_TAG);
			return this.failed(IService.MSG_INVALID_DATA);
		}
		
		fd.validateAndLoad((ObjectNode)node, op == FormOperation.SAVE, msgs);
		if(msgs.size() > 0) {
			logger.error("form has validation errors..");
			return this.failed(msgs);
		}

		StringWriter riter = new StringWriter();
		fd.serializeAsJson(riter);
		String text = riter.toString();
		headerData.setFormData(text);
		logger.info("Going {} form data : {}", op, text);
		if(op == FormOperation.SAVE) {
			headerData.save();
			logger.info("Saved");
		}else {
			headerData.submit();
			headerData.serializeAsJson(writer);
			logger.info("submitted and writing teh header data back");
		}
		return this.succeeded();
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

	@Override
	public ServiceResult serve(LoggedInUser user, Map<String, String> keyFields, Writer writer) throws Exception {
		throw new Exception("Form service cannot be invoked with parameters. It requires JSON as request payload");
	}
}
