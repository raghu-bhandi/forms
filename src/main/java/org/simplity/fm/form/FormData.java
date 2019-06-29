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
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.simplity.fm.DateUtil;
import org.simplity.fm.Message;
import org.simplity.fm.datatypes.InvalidValueException;
import org.simplity.fm.datatypes.ValueType;
import org.simplity.fm.http.LoggedInUser;
import org.simplity.fm.service.IService;
import org.simplity.fm.validn.IValidation;

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
	private static final char KEY_JOINER = '_';
	/**
	 * data structure describes the template for which this object provides
	 * actual data
	 */
	private final Form form;
	/**
	 * field values. null if this template has no fields
	 */
	private final Object[] fieldValues;
	/**
	 * grid data. null if this template has no fields
	 */
	private final Object[][][] gridData;

	/**
	 * operation for which this form is created
	 */
	private final FormOperation operation;

	/**
	 * @param form
	 *            data structure describes the template for which this object
	 *            provides
	 *            actual data
	 * @param operation
	 * @param values
	 *            grid data. null if this template has no fields
	 * @param tables
	 *            grid data. null if this template has no fields
	 */
	public FormData(Form form, FormOperation operation, Object[] values, Object[][][] tables) {
		this.form = form;
		this.fieldValues = values;
		this.gridData = tables;
		this.operation = operation;
	}

	/**
	 * @return the operation
	 */
	public FormOperation getOperation() {
		return this.operation;
	}

	@Override
	public String getFormId() {
		return this.form.getFormId();
	}

	@Override
	public String getDocumentId() {
		/*
		 * concatenate key fields to get document id
		 */
		int[] indexes = this.form.getKeyIndexes();
		if (indexes == null || indexes.length == 0) {
			return null;
		}
		String key = this.getFormId();
		for (int idx : indexes) {
			Object obj = this.fieldValues[idx];
			if (obj == null) {
				return null;
			}
			key += KEY_JOINER + obj.toString();
		}
		return key;
	}

	/**
	 * 
	 * @return value of user id field in this form. null if such a field is not
	 *         defined, or that field has no value.
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

	/**
	 * set owner for this form
	 * 
	 * @param user
	 * @return true if the owner was indeed set. false in case of any issue in
	 *         setting it
	 */
	public boolean setOwner(LoggedInUser user) {
		int idx = this.form.getUserIdFieldIdx();
		if (idx == -1) {
			return false;
		}
		Field field = this.form.fields[idx];
		String uid = user.getUserId();
		if(uid != null) {
			try {
				this.fieldValues[idx] = field.parse(user.getUserId());
				return true;
			} catch (InvalidValueException e) {
			//;
			}
		}
		return false;
	}

	/**
	 * does this form belong the the user
	 * 
	 * @param user
	 *            logged in user object
	 * @return true if this form belongs to the logged in user. false if it does
	 *         not,or if we can't say
	 */
	public boolean isOwner(LoggedInUser user) {
		String uidIn = user.getUserId();
		if (uidIn == null) {
			return false;
		}
		String uid = this.getUserId();
		if (uid == null) {
			return false;
		}
		return uid.equals(uidIn);
	}

	@Override
	public void deserialize(String data) {
		throw new Error("internal serialization method not yet implemented for form. Use json format instead");
	}

	@Override
	public boolean deserialize(String data, List<Message> errors) {
		throw new Error(
				"internal serialization method not yet implemented for form. Use json format instead");
	}

	@Override
	public String serialize() {
		throw new Error(
				"internal serialization method not yet implemented for form. Use json format instead");
	}

	@Override
	public void load(ObjectNode json) {
		this.validateAndLoad(json, true, null);
	}

	@Override
	public void loadKeys(Map<String, String> values, List<Message> errors) {
		int[] indexes = this.form.getKeyIndexes();
		if (indexes == null) {
			return;
		}

		Field[] fields = this.form.getFields();

		for (int idx : indexes) {
			Field f = fields[idx];
			validateAndSet(f, values.get(f.getFieldName()), this.fieldValues, idx, false, errors);
		}
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
		if(!allFieldsAreOptional) {
		this.validateForm(errors);
		}

		ChildForm[] tables = this.form.getChildForms();
		if (tables == null) {
			return;
		}


		for (int i = 0; i < tables.length; i++) {
			ChildForm  field = tables[i];
			String fieldName = field.fieldName;
			JsonNode child = json.get(fieldName);
			Form struct = field.form;
			ArrayNode node = null;
			if (child != null && child.getNodeType() == JsonNodeType.ARRAY) {
				node = (ArrayNode) child;
			}
			/*
			 * TODO: if this table had rows in the saved form, should we retain
			 * that or reset that to null?
			 */
			if (node == null) {
				// continue;
			}
			int n = node == null ? 0 : node.size();

			if (errors != null) {
				if (n < field.minRows || (n != 0 && n > field.maxRows)) {
					errors.add(Message.newFieldError(fieldName, field.errorMessageId, null));
					continue;
				}
			}
			if (n == 0 || node == null) {
				continue;
			}

			int nbrCols = struct.getFields().length;
			Object[][] grid = new Object[n][];
			this.gridData[i] = grid;
			for (int j = 0; j < n; j++) {
				JsonNode col = node.get(j);
				if (col == null || col.getNodeType() != JsonNodeType.OBJECT) {
					if (errors != null) {
						errors.add(Message.newError(IService.MSG_INVALID_DATA));
					}
					break;
				}

				Object[] row = new Object[nbrCols];
				setFeilds((ObjectNode) col, struct, row, allFieldsAreOptional, errors);
				grid[j] = row;
			}
		}
	}

	private static void setFeilds(ObjectNode json, Form struct, Object[] row, boolean allFieldsAreOptional, List<Message> errors) {
		Field[] fields = struct.getFields();
		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];
			String value = getChildAsText(json, field.getFieldName());
			validateAndSet(field, value, row, i, allFieldsAreOptional, errors);
		}
	}

	private static void validateAndSet(Field field, String value, Object[] row, int idx, boolean allFieldsAreOptional, List<Message> errors) {
		if(value == null) {
			if(allFieldsAreOptional) {
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
			gen.writeStartObject();
			this.writeFields(gen, this.fieldValues, this.form.getFields());
			if (this.gridData != null) {
				this.writeGrids(gen);
			}
			gen.writeEndObject();
		}
	}

	private void writeGrids(JsonGenerator gen) throws IOException {
		ChildForm[] tableFields = this.form.getChildForms();
		for (int i = 0; i < this.gridData.length; i++) {
			Object[][] grid = this.gridData[i];
			if (grid == null) {
				continue;
			}
			ChildForm field = tableFields[i];
			gen.writeArrayFieldStart(field.fieldName);
			this.writeGrid(gen, grid, field.form);
			gen.writeEndArray();
		}
	}

	private void writeGrid(JsonGenerator gen, Object[][] grid, Form gridStructure) throws IOException {
		Field[] columns = gridStructure.getFields();
		for (int i = 0; i < grid.length; i++) {
			gen.writeStartObject();
			this.writeFields(gen, grid[i], columns);
			gen.writeEndObject();
		}
	}

	private void writeFields(JsonGenerator gen, Object[] values, Field[] fields) throws IOException {
		for (int j = 0; j < values.length; j++) {
			Object value = values[j];
			if (value == null) {
				continue;
			}
			gen.writeFieldName(fields[j].getFieldName());
			if (value instanceof Date) {
				gen.writeString(DateUtil.formatDateTime((Date) value));
			} else {
				gen.writeObject(value);
			}
		}
	}

	private Object getObject(int idx) {
		if(idx < 0 || idx >= this.fieldValues.length) {
			return null;
		}
		return this.fieldValues[idx];
	}
	@Override
	public String getValue(int fieldIndex) {
		Object obj = this.getObject(fieldIndex);
		if (obj == null) {
			return null;
		}
		return obj.toString();
	}


	@Override
	public boolean setValue(int idx, String value) {
		if(idx < 0 || idx >= this.fieldValues.length) {
			return false;
		}
		Field field =this.form.fields[idx];
		if (field.getValueType() == ValueType.TEXT) {
			this.fieldValues[idx] = value;
			return true;
		}

		long val = 0;
		if (value != null) {
			try {
				val = Long.parseLong(value);
			} catch (Exception e) {
				return false;
			}
		}
		this.fieldValues[idx] = val;
		return true;
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
	public ValueType getValueType(String fieldName) {
		Field field = this.form.getField(fieldName);
		if (field == null) {
			return null;
		}
		return field.getValueType();
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
	public String getStringValue(int idx) {
		Object obj = this.getObject(idx);
		if (obj == null) {
			return null;
		}
		if(obj instanceof String) {
			return (String)obj;
		}
		return this.form.fields[idx].getDataType().toTextValue(obj);
	}

	@Override
	public Date getDateValue(int idx) {
		Object obj = this.getObject(idx);
		if (obj != null && obj instanceof Date) {
			return (Date) obj;
		}
		return null;
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
		if (obj instanceof Number) {
			return ((Number) obj).intValue() != 0;
		}
		return false;
	}

	@Override
	public boolean setStringValue(int idx, String value) {
		if (idx <0 || idx >= this.fieldValues.length) {
			return false;
		}
		ValueType vt = this.form.getFields()[idx].getValueType();
		if (vt == ValueType.TEXT) {
			this.fieldValues[idx] = value;
			return true;
		}
		return false;
	}

	@Override
	public boolean setDateValue(int idx, Date value) {
		if (idx <0 || idx >= this.fieldValues.length) {
			return false;
		}
		ValueType vt = this.form.getFields()[idx].getValueType();
		if (vt == ValueType.DATE) {
			this.fieldValues[idx] = value;
			return true;
		}
		return false;
	}

	@Override
	public boolean setBoolValue(int idx, boolean value) {
		if (idx <0 || idx >= this.fieldValues.length) {
			return false;
		}
		ValueType vt = this.form.getFields()[idx].getValueType();
		if (vt == ValueType.BOOLEAN) {
			this.fieldValues[idx] = value;
			return true;
		}
		return false;
	}

	@Override
	public boolean setLongValue(int idx, long value) {
		if (idx <0 || idx >= this.fieldValues.length) {
			return false;
		}
		ValueType vt = this.form.getFields()[idx].getValueType();
		if (vt == ValueType.NUMBER) {
			this.fieldValues[idx] = value;
			return true;
		}
		return false;
	}

	@Override
	public int getFieldIndex(String fieldName) {
		Field field = this.form.getField(fieldName);
		if(field != null) {
			return field.getIndex();
		}
		return -1;
	}
}
