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

import java.io.IOException;
import java.io.Writer;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.simplity.fm.Message;
import org.simplity.fm.datatypes.InvalidValueException;
import org.simplity.fm.datatypes.ValueType;
import org.simplity.fm.http.LoggedInUser;
import org.simplity.fm.rdb.DbHandle;
import org.simplity.fm.rdb.IDbClient;
import org.simplity.fm.rdb.RdbDriver;
import org.simplity.fm.validn.IValidation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * @author simplity.org
 *
 */
public class FormData {
	private static final Logger logger = LoggerFactory.getLogger(FormData.class);
	/**
	 * data structure describes the template for which this object provides
	 * actual data
	 */
	protected final Form form;
	/**
	 * field values. null if this template has no fields
	 */
	protected final Object[] fieldValues;
	/**
	 * data for child forms. null if this form has no children
	 */
	protected final FormData[][] childData;

	/**
	 * 
	 * @param form
	 * @param fieldValues
	 * @param childData
	 */
	public FormData(Form form, Object[] fieldValues, FormData[][] childData) {
		this.form = form;
		if (form.fields == null) {
			this.fieldValues = null;
		} else if (fieldValues == null) {
			this.fieldValues = new Object[form.fields.length];
		} else {
			this.fieldValues = fieldValues;
		}

		if (form.childForms == null) {
			this.childData = null;
		} else if (childData == null) {
			this.childData = new FormData[form.childForms.length][];
		} else {
			this.childData = childData;
		}
	}

	/**
	 * @param user
	 * @return is this user the owner of this form? If this form has no concept
	 *         of an owner, then this method returns true for any/all users.
	 */
	public boolean isOwner(LoggedInUser user) {
		int idx = this.form.userIdFieldIdx;
		if (idx == -1) {
			logger.warn("Form {} has not set user id field name. isOwner() will always return true",
					this.form.uniqueName);
			return true;
		}
		return user.getUserId().equals(this.fieldValues[idx]);
	}

	/**
	 * @param user
	 */
	public void setOwner(LoggedInUser user) {
		int idx = this.form.userIdFieldIdx;
		if (idx != -1) {
			this.fieldValues[idx] = user.getUserId();
		}
	}

	/**
	 * @return field values
	 */
	public Object[] getFieldValues() {
		return this.fieldValues;
	}

	/**
	 * 
	 * @return child data, or null if this form data has no child forms
	 */
	public FormData[][] getChildData() {
		return this.childData;
	}

	/**
	 * @return form for which data is carried
	 */
	public Form getForm() {
		return this.form;
	}

	/**
	 * @return get user id field, if one exists. null otherwise
	 */
	public String getUserId() {
		int idx = this.form.getUserIdFieldIdx();
		if (idx == -1) {
			return null;
		}
		Object obj = this.fieldValues[idx];
		if (obj == null) {
			return null;
		}
		return obj.toString();
	}

	private boolean idxOk(int idx) {
		return idx >= 0 && idx < this.fieldValues.length;
	}

	/**
	 * 
	 * @param fieldName
	 * @return Field in this form. null if no such field
	 */
	public ValueType getValueType(String fieldName) {
		Field field = this.form.getField(fieldName);
		if (field == null) {
			return null;
		}
		return field.getValueType();
	}

	/**
	 * 
	 * @param fieldName
	 * @return Field in this form. null if no such field
	 */
	public int getFieldIndex(String fieldName) {
		Field field = this.form.getField(fieldName);
		if (field != null) {
			return field.getIndex();
		}
		return -1;
	}

	/**
	 * @param idx
	 * @return object at the index. null if the index is out of range, or the
	 *         value at the index is null
	 */
	public Object getObject(int idx) {
		if (this.idxOk(idx)) {
			return this.fieldValues[idx];
		}
		return null;
	}

	/**
	 * 
	 * @param idx
	 *            index of the field. refer to getFieldIndex to get the index by
	 *            name
	 * @param value
	 *            value of the right type.
	 * @return true if value was indeed set. false if the field is not defined,
	 *         or
	 *         the type of object was not right for the field
	 */
	public boolean setObject(int idx, Object value) {
		if (this.idxOk(idx)) {
			this.fieldValues[idx] = value;
			return true;
		}
		return false;
	}

	/**
	 * 
	 * @param idx
	 * @return get value at this index as long. 0 if the indexis not valid, or
	 *         the value is not long
	 */
	public long getLongValue(int idx) {
		Object obj = this.getObject(idx);
		if (obj != null && obj instanceof Number) {
			return ((Number) obj).longValue();
		}
		return 0;
	}

	/**
	 * 
	 * @param idx
	 *            index of the field. refer to getFieldIndex to get the index by
	 *            name
	 * @param value
	 * 
	 * @return true if field exists, and is of integer type. false otherwise,
	 *         and the value is not set
	 */
	public boolean setLongValue(int idx, long value) {
		if (!this.idxOk(idx)) {
			return false;
		}
		ValueType vt = this.form.getFields()[idx].getValueType();
		if (vt == ValueType.INTEGER) {
			this.fieldValues[idx] = value;
			return true;
		}
		if (vt == ValueType.DECIMAL) {
			double d = value;
			this.fieldValues[idx] = d;
			return true;
		}
		if (vt == ValueType.TEXT) {

			this.fieldValues[idx] = "" + value;
			return true;
		}
		return false;
	}

	/**
	 * 
	 * @param idx
	 * @return value of the field as text. null if no such field, or the field
	 *         has null value. toString() of object if it is non-string
	 */
	public String getStringValue(int idx) {
		Object obj = this.getObject(idx);
		if (obj == null) {
			return null;
		}
		return obj.toString();
	}

	/**
	 * 
	 * @param idx
	 *            index of the field. refer to getFieldIndex to get the index by
	 *            name
	 * @param value
	 * 
	 * @return true if field exists, and is of String type. false otherwise, and
	 *         the value is not set
	 */
	public boolean setStringValue(int idx, String value) {
		if (!this.idxOk(idx)) {
			return false;
		}
		ValueType vt = this.form.getFields()[idx].getValueType();
		if (vt == ValueType.TEXT) {
			this.fieldValues[idx] = value;
			return true;
		}
		Object obj = vt.parse(value);
		if (obj != null) {
			this.fieldValues[idx] = obj;
			return true;
		}
		return false;
	}

	/**
	 * 
	 * @param idx
	 * @return value of the field as Date. null if the field is not a date
	 *         field, or it has null value
	 */
	public LocalDate getDateValue(int idx) {
		Object obj = this.getObject(idx);
		if (obj != null && obj instanceof LocalDate) {
			return (LocalDate) obj;
		}
		return null;
	}

	/**
	 * 
	 * @param idx
	 *            index of the field. refer to getFieldIndex to get the index by
	 *            name
	 * @param value
	 * 
	 * @return true if field exists, and is of Date type. false otherwise, and
	 *         the value is not set
	 */
	public boolean setDateValue(int idx, LocalDate value) {
		if (!this.idxOk(idx)) {
			return false;
		}
		ValueType vt = this.form.getFields()[idx].getValueType();
		if (vt == ValueType.DATE) {
			this.fieldValues[idx] = value;
			return true;
		}
		if (vt == ValueType.TEXT) {
			this.fieldValues[idx] = value.toString();
			return true;
		}
		return false;
	}

	/**
	 * 
	 * @return value of the field as boolean. false if no such field, or the
	 * @param idx
	 *            field is null,or the field is not boolean.
	 */
	public boolean getBoolValue(int idx) {
		Object obj = this.getObject(idx);
		if (obj == null) {
			return false;
		}
		if (obj instanceof Boolean) {
			return (Boolean) obj;
		}
		return false;
	}

	/**
	 * 
	 * @param idx
	 *            index of the field. refer to getFieldIndex to get the index by
	 *            name
	 * @param value
	 * 
	 * @return true if field exists, and is of boolean type. false otherwise,
	 *         and the value is not set
	 */
	public boolean setBoolValue(int idx, boolean value) {
		if (!this.idxOk(idx)) {
			return false;
		}
		ValueType vt = this.form.getFields()[idx].getValueType();
		if (vt == ValueType.BOOLEAN) {
			this.fieldValues[idx] = value;
			return true;
		}
		if (vt == ValueType.TEXT) {
			this.fieldValues[idx] = "" + value;
			return true;
		}
		return false;
	}

	/**
	 * 
	 * @param idx
	 * @return value of the field if it decimal. 0 index is invalid or the value
	 *         is not double/decimal.
	 */
	public double getDecimalValue(int idx) {
		Object obj = this.getObject(idx);
		if (obj == null) {
			return 0;
		}
		if (obj instanceof Number) {
			return ((Number) obj).doubleValue();
		}
		return 0;
	}

	/**
	 * 
	 * @param idx
	 *            index of the field. refer to getFieldIndex to get the index by
	 *            name
	 * @param value
	 * 
	 * @return true if field exists, and is of double type. false otherwise,
	 *         and the value is not set
	 */
	public boolean setDecimlValue(int idx, double value) {
		if (!this.idxOk(idx)) {
			return false;
		}
		ValueType vt = this.form.getFields()[idx].getValueType();
		if (vt == ValueType.DECIMAL) {
			this.fieldValues[idx] = value;
			return true;
		}
		if (vt == ValueType.INTEGER) {
			this.fieldValues[idx] = ((Number) value).longValue();
			return true;
		}
		if (vt == ValueType.TEXT) {
			this.fieldValues[idx] = "" + value;
			return true;
		}
		return false;
	}

	/**
	 * Note that this is NOT LocalDateTime. It is instant. We do not deal with
	 * localDateTime as of now.
	 * 
	 * @param idx
	 * @return value of the field as instant of time. null if the field is not
	 *         an instant.
	 *         field, or it has null value
	 */
	public Instant getTimestamp(int idx) {
		Object obj = this.getObject(idx);
		if (obj != null && obj instanceof Instant) {
			return (Instant) obj;
		}
		return null;
	}

	/**
	 * 
	 * @param idx
	 *            index of the field. refer to getFieldIndex to get the index by
	 *            name
	 * @param value
	 * 
	 * @return true if field exists, and is of Instant type. false otherwise,
	 *         and
	 *         the value is not set
	 */
	public boolean setTimestamp(int idx, Instant value) {
		if (!this.idxOk(idx)) {
			return false;
		}
		ValueType vt = this.form.getFields()[idx].getValueType();
		if (vt == ValueType.TIMESTAMP) {
			this.fieldValues[idx] = value;
			return true;
		}
		if (vt == ValueType.TEXT) {
			this.fieldValues[idx] = value.toString();
			return true;
		}
		return false;
	}

	/**
	 * load from a JSON node with no validation. To be called when loading from
	 * a dependable source
	 * 
	 * @param json
	 */
	public void load(ObjectNode json) {
		this.validateAndLoad(json, true, null);
	}

	/**
	 * load keys from a JSON. input is suspect.
	 * 
	 * @param json
	 *            non-null
	 * @param errors
	 *            non-null to which any validation errors are added
	 */
	public void loadKeys(ObjectNode json, List<Message> errors) {
		int[] indexes = this.form.getKeyIndexes();
		if (indexes == null) {
			return;
		}
		Field[] fields = this.form.getFields();
		for (int idx : indexes) {
			Field f = fields[idx];
			String value = getChildAsText(json, f.getFieldName());
			validateAndSet(f, value, this.fieldValues, idx, false, errors);
		}
	}

	/**
	 * load from a JSON node that is not dependable. Like input from a client
	 * 
	 * @param json
	 *            non-null
	 * @param allFieldsAreOptional
	 *            true if this is for a draft-save operation, where we validate
	 *            only the fields that the user has opted to type. MUST be
	 *            called with true value for final submit operation
	 * @param errors
	 *            non-null to which any validation errors are added
	 */
	public void validateAndLoad(ObjectNode json, boolean allFieldsAreOptional, List<Message> errors) {
		setFeilds(json, this.form, this.fieldValues, allFieldsAreOptional, errors);

		ChildForm[] children = this.form.getChildForms();
		if (children != null) {
			for (int i = 0; i < children.length; i++) {
				this.childData[i] = this.validateChild(children[i], json, allFieldsAreOptional, errors);
			}
		}
		if (!allFieldsAreOptional) {
			this.validateForm(errors);
		}
	}

	private FormData[] validateChild(ChildForm childForm, ObjectNode json, boolean allFieldsAreOptional,
			List<Message> errors) {
		String fieldName = childForm.fieldName;
		JsonNode childNode = json.get(fieldName);
		if (childNode == null) {
			if (errors != null && childForm.minRows > 0) {
				errors.add(Message.newFieldError(fieldName, childForm.errorMessageId, null));
			}
			return null;
		}

		JsonNodeType nt = childNode.getNodeType();
		if (childForm.isTabular == false) {
			if (nt != JsonNodeType.OBJECT) {
				if (errors != null) {
					logger.error(
							"Form {} has a child form named {} and hence an object is expeted. But {} is received as data",
							this.form.getFormId(), fieldName, nt);
					return null;
				}
			}
			FormData fd = childForm.form.newFormData();
			fd.validateAndLoad((ObjectNode) childNode, allFieldsAreOptional, errors);
			FormData[] result = { fd };
			return result;
		}

		ArrayNode arr = null;
		int n = 0;
		if (nt == JsonNodeType.ARRAY) {
			arr = (ArrayNode) childNode;
			n = arr.size();
			if (errors != null && (n < childForm.minRows || n > childForm.maxRows)) {
				arr = null;
			}
		}

		if (arr == null) {
			if (errors != null) {
				errors.add(Message.newFieldError(fieldName, childForm.errorMessageId, null));
			}
			return null;
		}

		if (n == 0) {
			return null;
		}
		List<FormData> fds = new ArrayList<>();
		for (int j = 0; j < n; j++) {
			JsonNode col = arr.get(j);

			if (col == null || col.getNodeType() != JsonNodeType.OBJECT) {
				if (errors != null) {
					errors.add(Message.newError(Message.MSG_INVALID_DATA));
				}
				continue;
			}
			FormData fd = childForm.form.newFormData();
			fds.add(fd);
			fd.validateAndLoad((ObjectNode) col, allFieldsAreOptional, errors);
		}
		if (fds.size() == 0) {
			return null;
		}
		return fds.toArray(new FormData[0]);
	}

	private static void setFeilds(ObjectNode json, Form form, Object[] row, boolean allFieldsAreOptional,
			List<Message> errors) {
		Field[] fields = form.getFields();
		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];
			String value = getChildAsText(json, field.getFieldName());
			validateAndSet(field, value, row, i, allFieldsAreOptional, errors);
		}
	}

	private static void validateAndSet(Field field, String value, Object[] row, int idx, boolean allFieldsAreOptional,
			List<Message> errors) {
		if (value == null || value.isEmpty()) {
			if (allFieldsAreOptional) {
				row[idx] = null;
				return;
			}
		}
		try {
			row[idx] = field.parse(value);
		} catch (InvalidValueException e) {
			logger.error("{} is not a valid value for {} which is of data-type {} and value type {}", value,
					field.getFieldName(), field.getDataType().getName(), field.getDataType().getValueType());
			if (errors != null) {
				errors.add(Message.newFieldError(field.getFieldName(), field.getMessageId(), null));
			}
		}
	}

	/**
	 * @param validations
	 */
	private void validateForm(List<Message> errors) {
		IValidation[] validations = this.form.getValidations();
		if (validations != null) {
			for (IValidation vln : validations) {
				vln.isValid(this, errors);
			}
		}
	}

	/**
	 * @param writer
	 * @throws IOException
	 */
	public void serializeAsJson(Writer writer) throws IOException {
		try (JsonGenerator gen = new JsonFactory().createGenerator(writer)) {
			this.serialize(gen);
		}
	}

	private void serialize(JsonGenerator gen) throws IOException {
		gen.writeStartObject();
		writeFields(gen, this.fieldValues, this.form.getFields());
		if (this.childData != null) {
			this.serializeChildren(gen);
		}

		gen.writeEndObject();
	}

	private void serializeChildren(JsonGenerator gen) throws IOException {
		int i = 0;
		for (ChildForm cf : this.form.childForms) {
			FormData[] fd = this.childData[i];
			if (fd == null) {
				continue;
			}
			gen.writeFieldName(cf.fieldName);
			if (cf.isTabular) {
				gen.writeStartArray();
				for (FormData cd : fd) {
					cd.serialize(gen);
				}
				gen.writeEndArray();
			} else {
				fd[0].serialize(gen);
			}
			i++;
		}
	}

	private static void writeFields(JsonGenerator gen, Object[] values, Field[] fields) throws IOException {
		for (int j = 0; j < values.length; j++) {
			Object value = values[j];
			if (value == null) {
				continue;
			}
			Field field = fields[j];
			if (field.isDerivedField()) {
				continue;
			}
			gen.writeFieldName(field.getFieldName());
			if (value instanceof LocalDate || value instanceof Instant) {
				value = value.toString();
			}
			gen.writeObject(value);
		}
	}

	private static String getChildAsText(JsonNode json, String fieldName) {
		JsonNode node = json.get(fieldName);
		if (node == null) {
			return null;
		}
		JsonNodeType nt = node.getNodeType();
		if (nt == JsonNodeType.NULL || nt == JsonNodeType.MISSING) {
			return null;
		}
		return node.asText();
	}

	/**
	 * insert/create this form data into the db.
	 * 
	 * @return true if it is created. false in case it failed because of an an
	 *         existing form with the same id/key
	 * @throws SQLException
	 */
	public boolean insertToDb() throws SQLException {
		Boolean[] result = new Boolean[1];
		RdbDriver.getDriver().transact(new IDbClient() {

			@Override
			public boolean transact(DbHandle handle) throws SQLException {
				return result[0] = handle.insert(FormData.this);
			}
		}, false);
		return result[0];
	}

	/**
	 * update this form data back into the db.
	 * 
	 * @return true if it is indeed updated. false in case there was no row to
	 *         update
	 * @throws SQLException
	 */
	public boolean updateInDb() throws SQLException {
		Boolean[] result = new Boolean[1];
		RdbDriver.getDriver().transact(new IDbClient() {

			@Override
			public boolean transact(DbHandle handle) throws SQLException {
				return result[0] = handle.update(FormData.this);
			}
		}, false);
		return result[0];
	}

	/**
	 * remove this form data from the db
	 * 
	 * @return true if it is indeed deleted happened. false otherwise
	 * @throws SQLException
	 */
	public boolean deleteFromDb() throws SQLException {
		Boolean[] result = new Boolean[1];
		RdbDriver.getDriver().transact(new IDbClient() {

			@Override
			public boolean transact(DbHandle handle) throws SQLException {
				return result[0] = handle.delete(FormData.this);
			}
		}, false);
		return result[0];
	}

	/**
	 * fetch data for this form from a db
	 * 
	 * @return true if it is read.false if no dta found for this form (key not
	 *         found...)
	 * @throws SQLException
	 */
	public boolean fetchFromDb() throws SQLException {
		Boolean[] result = new Boolean[1];
		RdbDriver.getDriver().transact(new IDbClient() {

			@Override
			public boolean transact(DbHandle handle) throws SQLException {
				result[0] = handle.readForm(FormData.this);
				return true;
			}
		}, true);
		return result[0];
	}
}
