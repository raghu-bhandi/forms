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
import org.simplity.fm.rdb.RdbDriver;
import org.simplity.fm.service.IService;
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
public class FormData implements IFormData {
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

	@Override
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

	@Override
	public ValueType getValueType(String fieldName) {
		Field field = this.form.getField(fieldName);
		if (field == null) {
			return null;
		}
		return field.getValueType();
	}

	@Override
	public int getFieldIndex(String fieldName) {
		Field field = this.form.getField(fieldName);
		if (field != null) {
			return field.getIndex();
		}
		return -1;
	}

	@Override
	public Object getObject(int idx) {
		if (this.idxOk(idx)) {
			return this.fieldValues[idx];
		}
		return null;
	}

	@Override
	public boolean setObject(int idx, Object value) {
		if (this.idxOk(idx)) {
			this.fieldValues[idx] = value;
			return true;
		}
		return false;
	}

	@Override
	public long getLongValue(int idx) {
		Object obj = this.getObject(idx);
		if (obj != null && obj instanceof Number) {
			return ((Number) obj).longValue();
		}
		return 0;
	}

	@Override
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

	@Override
	public String getStringValue(int idx) {
		Object obj = this.getObject(idx);
		if (obj == null) {
			return null;
		}
		return obj.toString();
	}

	@Override
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

	@Override
	public LocalDate getDateValue(int idx) {
		Object obj = this.getObject(idx);
		if (obj != null && obj instanceof LocalDate) {
			return (LocalDate) obj;
		}
		return null;
	}

	@Override
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

	@Override
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

	@Override
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

	@Override
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

	@Override
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

	@Override
	public Instant getTimestamp(int idx) {
		Object obj = this.getObject(idx);
		if (obj != null && obj instanceof Instant) {
			return (Instant) obj;
		}
		return null;
	}

	@Override
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

	@Override
	public void load(ObjectNode json) {
		this.validateAndLoad(json, true, null);
	}

	@Override
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

	@Override
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
		JsonNode child = json.get(fieldName);
		if (child == null) {
			if (errors != null && childForm.minRows > 0) {
				errors.add(Message.newFieldError(fieldName, childForm.errorMessageId, null));
			}
			return null;
		}

		JsonNodeType nt = child.getNodeType();
		if (childForm.isTabular == false) {
			if (nt != JsonNodeType.OBJECT) {
				if (errors != null) {
					logger.error(
							"Form {} has a child form named {} and hence an object is expeted. But {} is received as data",
							this.form.getFormId(), fieldName, nt);
					return null;
				}
			}
			FormData fd = this.form.newFormData();
			fd.validateAndLoad((ObjectNode) child, allFieldsAreOptional, errors);
			FormData[] result = { fd };
			return result;
		}

		ArrayNode node = null;
		int n = 0;
		if (nt == JsonNodeType.ARRAY) {
			node = (ArrayNode) child;
			n = node.size();
			if (errors != null && (n < childForm.minRows || n > childForm.maxRows)) {
				node = null;
			}
		}

		if (node == null) {
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
			JsonNode col = node.get(j);

			if (col == null || col.getNodeType() != JsonNodeType.OBJECT) {
				if (errors != null) {
					errors.add(Message.newError(IService.MSG_INVALID_DATA));
				}
				continue;
			}
			FormData fd = this.form.newFormData();
			fds.add(fd);
			fd.validateAndLoad(json, allFieldsAreOptional, errors);
		}
		if (fds.size() == 0) {
			return null;
		}
		return fds.toArray(new FormData[0]);
	}

	private static void setFeilds(ObjectNode json, Form struct, Object[] row, boolean allFieldsAreOptional,
			List<Message> errors) {
		Field[] fields = struct.getFields();
		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];
			String value = getChildAsText(json, field.getFieldName());
			validateAndSet(field, value, row, i, allFieldsAreOptional, errors);
		}
	}

	private static void validateAndSet(Field field, String value, Object[] row, int idx, boolean allFieldsAreOptional,
			List<Message> errors) {
		if (value == null) {
			if (allFieldsAreOptional) {
				row[idx] = null;
				return;
			}
		}
		try {
			row[idx] = field.parse(value);
		} catch (InvalidValueException e) {
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

	@Override
	public void serializeAsJson(Writer writer) throws IOException {
		try (JsonGenerator gen = new JsonFactory().createGenerator(writer)) {
			this.serialize(gen);
		}
	}

	private void serialize(JsonGenerator gen) throws IOException {
		gen.writeStartObject();
		this.writeFields(gen, this.fieldValues, this.form.getFields());
		if (this.childData != null) {
			this.serializeChildren(gen);
		}

		gen.writeEndObject();
	}

	private void serializeChildren(JsonGenerator gen) throws IOException {
		int i = 0;
		for (ChildForm cf : this.form.childForms) {
			FormData[] data = this.childData[i];
			if (data == null) {
				continue;
			}
			gen.writeFieldName(cf.fieldName);
			if (this.form.childForms[i].isTabular) {
				gen.writeStartArray();
				for (FormData fd : data) {
					fd.serialize(gen);
				}
				gen.writeEndArray();
			} else {
				data[0].serialize(gen);
			}
			i++;
		}
	}

	private void writeFields(JsonGenerator gen, Object[] values, Field[] fields) throws IOException {
		for (int j = 0; j < values.length; j++) {
			Object value = values[j];
			if (value == null) {
				continue;
			}
			gen.writeFieldName(fields[j].getFieldName());
			gen.writeObject(jsonQuickFix(value));
		}
	}

	private Object jsonQuickFix(Object value) {
		if (value instanceof LocalDate || value instanceof Instant) {
			return value.toString();
		}
		return value;
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

	@Override
	public boolean insertToDb() throws SQLException {
		return RdbDriver.getDriver().insert(this);
	}

	@Override
	public boolean updateInDb() throws SQLException {
		return RdbDriver.getDriver().update(this);
	}

	@Override
	public boolean deleteFromDb() throws SQLException {
		return RdbDriver.getDriver().delete(this);
	}

	@Override
	public boolean fetchFromDb() throws SQLException {
		return RdbDriver.getDriver().readForm(this);
	}

	@Override
	public boolean isOwner(LoggedInUser user) {
		int idx = this.form.userIdFieldIdx;
		if (idx == -1) {
			logger.warn("Form {} has not set user id field name. isOwner() will always return true",
					this.form.uniqueName);
			return true;
		}
		return user.getUserId().equals(this.fieldValues[idx]);
	}

	@Override
	public void setOwner(LoggedInUser user) {
		int idx = this.form.userIdFieldIdx;
		if (idx != -1) {
			this.fieldValues[idx] = user.getUserId();
		}
	}

	@Override
	public Object[] getFieldValues() {
		return this.fieldValues;
	}

	@Override
	public FormData[][] getChildData() {
		return this.childData;
	}

	@Override
	public Form getForm() {
		return this.form;
	}

	/**
	 * pre-fill data into this form
	 */
	public void prefill() {
		logger.info("Going to prefill data into from {}. Dummy as of now...", this.getForm().getFormId());
		
	}
}
