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

import org.simplity.fm.datatypes.InvalidValueException;

/**
 * represents a validation error while accepting data from a client for a field
 * 
 * @author simplity.org.
 *
 */
public class Message {
	/**
	 * create an error message for a message id
	 * 
	 * @param messageId
	 * @return an error message for this message id
	 */
	public static Message newError(String messageId) {
		return new Message(MessageType.Error, messageId, null, null, null, 0);
	}

	/**
	 * @param e
	 * @return a validation message based on the exception
	 */
	public static Message newValidationError(InvalidValueException e) {
		return new Message(MessageType.Error, e.getMessageId(), e.getFieldName(), e.getParams(), null, 0);
	}

	/**
	 * create a validation error message for a field
	 * 
	 * @param fieldName
	 * @param messageId
	 * @param params
	 * @return validation error message
	 */
	public static Message newFieldError(String fieldName, String messageId, String params) {
		return new Message(MessageType.Error, messageId, fieldName, params, null, 0);
	}

	/**
	 * create a validation error message for a field
	 * 
	 * @param tableName
	 *            table name (primary field name in the parent form)
	 * @param columnName
	 *            column name field name of the child form)
	 * 
	 * @param messageId
	 * @param params
	 * @param rowNumber
	 *            1-based row number in which the error is detected
	 * @return validation error message
	 */
	public static Message newColumnError(String tableName, String columnName, String messageId, String params,
			int rowNumber) {
		return new Message(MessageType.Error, messageId, tableName, params, columnName, 0);
	}


	/**
	 * generic message could be warning/info etc..
	 * @param messageType
	 * @param messageId
	 * @param params
	 * @return message
	 */
	public static Message newMessage(MessageType messageType, String messageId, String params) {
		return new Message(messageType, messageId, null, params, null, 0);
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
	 * specific to a field. Could be a simple field name, or the name of a
	 * tabular data element
	 */
	public final String fieldName;

	/**
	 * If the field in error is tabular one, this is the name of teh column that
	 * is in error
	 */
	public final String columnName;

	/**
	 * 1-based row number in case this is a data tabular data
	 */
	public final int rowNumber;

	/**
	 * possibly comma separated list of parameters that go into the body of the
	 * message, in case the message is a template with place-holders for
	 * run-time parameter
	 */
	public final String params;

	private Message(MessageType messageType, String messageId, String fieldName, String params, String columnName,
			int rowNumber) {
		this.messageType = messageType;
		this.messageId = messageId;
		this.fieldName = fieldName;
		this.params = params;
		this.columnName = columnName;
		this.rowNumber = rowNumber;
	}

	@Override
	public String toString() {
		return "type:" + this.messageType + "  id:" + this.messageId + " field:" + this.fieldName;
	}

}
