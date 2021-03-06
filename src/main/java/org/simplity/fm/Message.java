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
package org.simplity.fm;

import java.io.IOException;
import java.io.Writer;

import org.simplity.fm.datatypes.InvalidValueException;

/**
 * represents a validation error while accepting data from a client for a field
 * 
 * @author simplity.org.
 *
 */
public class Message {
	/**
	 * message to be used if the user is not authorized for this specific form
	 * instance
	 */
	public static final String MSG_NOT_AUTHORIZED = "notAuthorized";
	/**
	 * error to be used in case of any internal error
	 */
	public static final String MSG_INTERNAL_ERROR = "internalError";

	/**
	 * error to be used in case of any internal error
	 */
	public static final String MSG_INVALID_DATA = "invalidData";
	/**
	 * error to be used to indicate that value is required for an input field
	 */
	public static final String FIELD_REQUIRED = "valueRequired";

	/**
	 * create an error message for a message id
	 * 
	 * @param messageId
	 * @return an error message for this message id
	 */
	public static Message newError(String messageId) {
		return new Message(MessageType.ERROR, messageId, null, null, null, -1);
	}

	/**
	 * @param e
	 * @return a validation message based on the exception
	 */
	public static Message newValidationError(InvalidValueException e) {
		return new Message(MessageType.ERROR, e.getMessageId(), e.getFieldName(), e.getParams(), null, -1);
	}

	/**
	 * create a validation error message for a field
	 * 
	 * @param fieldName
	 * @param messageId
	 * @param params
	 * @return validation error message
	 */
	public static Message newFieldError(String fieldName, String messageId, String... params) {
		return new Message(MessageType.ERROR, messageId, fieldName, params, null, -1);
	}

	/**
	 * create a validation error message for a field inside an object/table
	 * 
	 * @param fieldName
	 *            name of the field inside the object
	 * @param objectName
	 *            attribute/field name of the parent that has this child object
	 *            as data
	 * 
	 * @param messageId
	 * @param rowNumber
	 *            1-based row number in which the error is detected
	 * @param params
	 *            run-time parameters
	 * @return validation error message
	 */
	public static Message newObjectFieldError(String fieldName, String objectName, String messageId, int rowNumber,
			String... params) {
		return new Message(MessageType.ERROR, messageId, fieldName, params, objectName, rowNumber);
	}

	/**
	 * generic message could be warning/info etc..
	 * 
	 * @param messageType
	 * @param messageId
	 * @param params
	 * @return message
	 */
	public static Message newMessage(MessageType messageType, String messageId, String... params) {
		return new Message(messageType, messageId, null, params, null, -1);
	}

	/**
	 * message type/severity.
	 */
	public MessageType messageType;
	/**
	 * error message id for this error. non-null;
	 */
	public final String messageId;
	/**
	 * name of the field that is in error. null if the error is not
	 * specific to a field. Could be a simple field name, or the it could be
	 * inside a child table/object
	 */
	public final String fieldName;

	/**
	 * If the field is inside a child table/object, this is the name of that
	 * table/object
	 */
	public final String objectName;

	/**
	 * 0-based row number in case this is a tabular data
	 */
	public final int rowNumber;

	/**
	 * run-time parameters to be used to format the text for this message.
	 * Please holders in the message are marked with {}
	 */
	public final String[] params;

	private Message(MessageType messageType, String messageId, String fieldName, String[] params, String objectName,
			int rowNumber) {
		this.messageType = messageType;
		this.messageId = messageId;
		this.fieldName = fieldName;
		this.params = params;
		this.objectName = objectName;
		this.rowNumber = rowNumber;
	}

	@Override
	public String toString() {
		return "type:" + this.messageType + "  id:" + this.messageId + " field:" + this.fieldName;
	}

	private static final char Q = '"';

	/**
	 * @param writer
	 * @throws IOException
	 */
	public void toJson(Writer writer) throws IOException {
		writer.write("{\"type\":\"");

		if (this.messageType == null) {
			writer.write("error");
		} else {
			writer.write(this.messageType.toString().toLowerCase());
		}

		writer.write("\",\"id\":\"");
		writer.write(this.messageId);
		writer.write(Q);

		writer.write("\",\"text:\"");
		writer.write(this.messageId);
		writer.write(Q);

		if (this.fieldName != null) {
			writer.write(",\"fieldName\":\"");
			writer.write(this.fieldName);
			writer.write(Q);
		}

		if (this.objectName != null) {
			writer.write(",\"objectName\": \"");
			writer.write(this.objectName);
			writer.write(Q);
		}

		if (this.params != null && this.params.length > 0) {
			writer.write(",\"params\":[");
			writer.write(this.params[0].replaceAll("\"", "\"\""));
			writer.write(Q);

			for (int i = 1; i < this.params.length; i++) {
				writer.write(",\"");
				writer.write(this.params[i].replaceAll("\"", "\"\""));
				writer.write(Q);
			}
			writer.write(']');
		}

		if (this.rowNumber != -1) {
			writer.write(",\"idx\":");
			writer.write(this.rowNumber);
		}

		writer.write("}");
	}
}
