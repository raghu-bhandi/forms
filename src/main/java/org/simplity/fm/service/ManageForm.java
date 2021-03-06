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
import java.util.List;

import org.simplity.fm.Config;
import org.simplity.fm.Forms;
import org.simplity.fm.Message;
import org.simplity.fm.form.FormData;
import org.simplity.fm.form.FormOperation;
import org.simplity.fm.form.HeaderData;
import org.simplity.fm.form.Form;
import org.simplity.fm.Conventions;
import org.simplity.fm.http.LoggedInUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Bsse class for all forms based services
 * 
 * @author simplity.org
 *
 */
public class ManageForm implements IService {
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
	public void serve(IserviceContext ctx, ObjectNode payload) throws Exception {
		HeaderData headerData = Config.getConfig().newHeaderData();
		if (headerData == null) {
			logger.error("Header Form is not configured. FormService can not operate.");
			ctx.addMessage(Message.newError(Message.MSG_INTERNAL_ERROR));
			return;
		}

		JsonNode node = payload.get(Conventions.Http.TAG_HEADER);
		if (node == null || node.getNodeType() != JsonNodeType.OBJECT) {
			logger.error("Payload has to have a header object named {} ", Conventions.Http.TAG_HEADER);
			ctx.addMessage(Message.newError(Message.MSG_INVALID_DATA));
			return;
		}

		headerData.validateAndLoad((ObjectNode) node, false, true, ctx);
		if (ctx.allOk() == false) {
			logger.error("Header data is invalid");
			return;
		}

		if (headerData.isOwner(ctx.getUser()) == false) {
			logger.error("Logged in user {} is not authorized to manage this form with {} as userId ",
					ctx.getUser().getUserId(), headerData.getUserId());
			ctx.addMessage(Message.newError(Message.MSG_NOT_AUTHORIZED));
			return;
		}

		String formName = headerData.getFormName();
		Form form = Forms.getForm(formName);
		if (form == null) {
			logger.error("Unable to get form {} ", formName);
			ctx.addMessage(Message.newError("invalidFormName"));
			return;
		}

		FormOperation op = headerData.getFormOperation();
		if (op == null) {
			logger.error("Header has an invalid operation. it should be get, save or submit");
			ctx.addMessage(Message.newError("invalidOperation"));
			return;
		}

		/*
		 * this is the actual form that is being managed (saved/submitted)
		 */
		FormData fd = form.newFormData();
		if (op == FormOperation.GET) {
			if (headerData.fetchFromDb()) {
				logger.info("Saved form retrieved and sent to the client");
				node = new ObjectMapper().readTree(headerData.getFormData());
				fd.load((ObjectNode) node);
			} else {
				IFormProcessor proc = form.getPrefillProcessor();
				if(proc != null) {
					if(proc.process(fd, ctx)) {
						logger.info("Form successfully pre-filled");
					}else {
						logger.error("FOrm prefill processer returned false indicating some trouble. Service execution stopped");
						return;
					}
				}
				logger.info("New form created and sent to the client");
			}
			
			IFormProcessor proc = form.getRefillProcessor();
			if(proc != null) {
				if(proc.process(fd, ctx)) {
					logger.info("Form successfully re-filled");
				}else {
					logger.error("Form refill processer returned false indicating some trouble. Service execution stopped");
					return;
				}
			}
			fd.serializeAsJson(ctx.getResponseWriter());
			return;
		}

		node = payload.get(Conventions.Http.TAG_DATA);
		if (node == null || node.getNodeType() != JsonNodeType.OBJECT) {
			logger.error("Payload has to have a form data object named {}", Conventions.Http.TAG_DATA);
			ctx.addMessage(Message.newError(Message.MSG_INVALID_DATA));
			return;
		}

		fd.validateAndLoad((ObjectNode) node, op == FormOperation.SAVE, true, ctx);
		if (ctx.allOk() == false) {
			return;
		}

		StringWriter riter = new StringWriter();
		fd.serializeAsJson(riter);
		String text = riter.toString();
		headerData.setFormData(text);
		logger.info("Going {} form data : {}", op, text);
		if (op == FormOperation.SUBMIT) {
			headerData.submit();
			headerData.serializeAsJson(ctx.getResponseWriter());
			logger.info("submitted and writing the header data back");
		} else {
			headerData.save();
			logger.info("Saved");
		}
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
}
