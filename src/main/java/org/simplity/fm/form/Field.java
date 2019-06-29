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
package org.simplity.fm.form;

import org.simplity.fm.ValueLists;
import org.simplity.fm.datatypes.DataType;
import org.simplity.fm.datatypes.InvalidValueException;
import org.simplity.fm.datatypes.ValueType;
import org.simplity.fm.validn.ValueList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author simplity.org
 *
 */
public class Field {
	private static final Logger logger = LoggerFactory.getLogger(Field.class);
	/**
	 * field name is unique within a form/template. However, it is strongly
	 * advised that the same name is used in different forms if they actually
	 * refer to the same data element
	 */
	private final String fieldName;

	/**
	 * 0-based index of the field in the parent form;
	 */
	private final int index;
	/**
	 * data type describes the type of value and restrictions (validations) on
	 * the value
	 */
	private final DataType dataType;
	/**
	 * default value is used only if this optional and the value is missing. not
	 * used if the field is mandatory
	 */
	private final String defaultValue;
	/**
	 * refers to the message id/code that is used for i18n of messages
	 */
	private final String messageId;
	/**
	 * required/mandatory. If set to true, text value of empty string and 0 for
	 * integral are assumed to be not valid. Relevant only for editable fields.
	 */
	private final boolean isRequired;
	/**
	 * Is this field editable by the client. If false, then this can be either a
	 * "reference field" that is used for display or validation purposes. It is
	 * typically not sent back from client.
	 */
	private final boolean isEditable;
	/**
	 * if true, this field value is calculated based on other fields. Typically
	 * not received from client, but some designs may receive and keep it for
	 * logging/legal purposes. Not relevant if the field is editable.
	 */
	private final boolean isDerivedField;
	/**
	 * is this part of the conceptual key (document-id) of the form.
	 */
	private boolean isKeyField;

	/**
	 * if this field has a list of valid values (to be rendered on the client as
	 * a drop-down)
	 * this list may be design-time in which it is available in valueLists.
	 * if the valid value list depends on another field, then it is not
	 * specified here, but as part of inter-field validations
	 * <code>ValueLists</code> Otherwise this value list is fetched at run time
	 * TODO: as of now, only design-time known list is supported
	 */
	private final String valueListName;

	/**
	 * cached value list for
	 */
	private ValueList valueList;

	/**
	 * this is generally invoked by the generated code for a Data Structure
	 * 
	 * @param fieldName
	 *            unique within its data structure
	 * @param index
	 *            0-based index of this field in the prent form
	 * @param dataType
	 *            pre-defined data type. used for validating data coming from a
	 *            client
	 * @param isRequired
	 *            is this field mandatory. used for validating data coming from
	 *            a client
	 * @param defaultValue
	 *            value to be used in case the client has not sent a value for
	 *            this. This e is used ONLY if isRequired is false. That is,
	 *            this is used if the field is optional, and the client skips
	 *            it. This value is NOT used if isRequired is set to true
	 * @param isEditable
	 *            can this field be edited/requested by the client? If false,
	 *            the field is either a reference field or a derived field
	 * @param messageId
	 *            can be null in which case the id from dataType is used
	 * @param isDerivedField
	 *            true if this field value is derived/calculated based on other
	 *            fields. Like sum of other fields, or calculated based on some
	 *            rule
	 * @param isKeyField
	 *            is this a key (document id) field?
	 * @param valueListName
	 *            if this field has a list of valid values that are typically
	 *            rendered in a drop-down. If the value list depends on value of
	 *            another field, then it is part of inter-field validation, and
	 *            not part of this field.
	 */
	public Field(String fieldName, int index, DataType dataType, String defaultValue, String messageId,
			boolean isRequired, boolean isEditable, boolean isDerivedField, boolean isKeyField, String valueListName) {
		this.fieldName = fieldName;
		this.index = index;
		this.isRequired = isRequired;
		this.isEditable = isEditable;
		this.messageId = messageId;
		this.defaultValue = defaultValue;
		this.isDerivedField = isDerivedField;
		this.dataType = dataType;
		this.isKeyField = isKeyField;
		if (valueListName == null) {
			this.valueListName = null;
			this.valueList = null;
		} else {
			this.valueListName = valueListName;
			this.valueList = ValueLists.getList(valueListName);
		}

	}

	/**
	 * @return the fieldName
	 */
	public String getFieldName() {
		return this.fieldName;
	}

	/**
	 * @return the defaultValue
	 */
	public String getDefaultValue() {
		return this.defaultValue;
	}

	/**
	 * @return the isRequired
	 */
	public boolean isRequired() {
		return this.isRequired;
	}

	/**
	 * @return the messageId
	 */
	public String getMessageId() {
		if (this.messageId != null) {
			return this.messageId;
		}
		return this.dataType.getMessageId();
	}

	/**
	 * @return is this field editable?
	 */
	public boolean isEditable() {
		return this.isEditable;
	}

	/**
	 * @return value type
	 */
	public ValueType getValueType() {
		return this.dataType.getValueType();
	}

	/**
	 * parse into the desired type, validate and return the value. caller should
	 * check for exception for validation failure and not returned value as
	 * null.
	 * <br />
	 * Caller may opt not to check for mandatory condition by checking for null
	 * before calling this method. That is, in such a case, caller handles the
	 * null condition, and calls this only if it is not null.
	 * 
	 * @param inputValue
	 *            input text. can be null, in which case it is validated for
	 *            mandatory
	 * @return object of the right type. or null if the value is null and it is
	 *         valid
	 * @throws InvalidValueException
	 *             if the value is invalid.
	 */
	public Object parse(String inputValue) throws InvalidValueException {
		logger.info("{}={}", this.fieldName, inputValue);
		if (inputValue == null) {
			if (this.isRequired == false) {
				return null;
			}
		} else if (this.valueList != null) {
			logger.info("{}={} being validated against list {}", this.fieldName, inputValue, this.valueListName);
			if (this.valueList.isValid(inputValue)) {
				logger.info("{}={} validated OK", this.fieldName, inputValue);
				return this.getValueType().parse(inputValue);
			}
		} else {
			logger.info("{}={} being validated as value", this.fieldName, inputValue);
			Object obj = this.dataType.parse(inputValue);
			if (obj != null) {
				logger.info("{} validated ok for {} ", this.fieldName, obj);
				return obj;
			}
		}

		throw new InvalidValueException(this.getMessageId(), this.fieldName, null);
	}

	/**
	 * @return true if this field is derived based on other fields. false
	 *         otherwise
	 */
	public boolean isDerivedField() {
		return this.isDerivedField;
	}

	/**
	 * is this a key field?
	 * 
	 * @return true if this is the key field, or one of the key fields
	 */
	public boolean isKeyField() {
		return this.isKeyField;
	}

	/**
	 * @return the dataType
	 */
	public DataType getDataType() {
		return this.dataType;
	}

	/**
	 * @return the valueListName
	 */
	public String getValueListName() {
		return this.valueListName;
	}

	/**
	 * @return the index
	 */
	public int getIndex() {
		return this.index;
	}
}
