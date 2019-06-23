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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.simplity.fm.service.GetService;
import org.simplity.fm.service.IFormProcessor;
import org.simplity.fm.service.IService;
import org.simplity.fm.service.SaveService;
import org.simplity.fm.service.SubmitService;

/**
 * @author simplity.org
 *
 */
public class FormStructure {

	/**
	 * array index for pre get form processors
	 */
	public static final int PRE_GET = 0;
	/**
	 * array index for post get form processors
	 */
	public static final int POST_GET = 1;
	/**
	 * array index for pre save form processors
	 */
	public static final int PRE_SAVE = 2;
	/**
	 * array index for post save form processors
	 */
	public static final int POST_SAVE = 3;
	/**
	 * array index for pre submit form processors
	 */
	public static final int PRE_SUBMIT = 4;
	/**
	 * array index for post submit form processors
	 */
	public static final int POST_SUBMIT = 5;

	private static final int NBR_PROCESSORS = 6;
	/**
	 * get operation
	 */
	public static final String SERVICE_TYPE_GET = "get";
	/**
	 * get operation
	 */
	public static final String SERVICE_TYPE_SAVE = "save";
	/**
	 * get operation
	 */
	public static final String SERVICE_TYPE_SUBMIT = "submit";
	/**
	 * this is the unique id given this to this form, it is an independent
	 * form. It is the section name in case it is a section of a composite form
	 */
	protected String uniqueName;
	/**
	 * Fields in this form.
	 */
	protected Field[] fields;

	/**
	 * field name that has the user id. Used for access control 
	 */
	protected String userIdFieldName;

	/**
	 * name of grids/tabular dta. null if there are no grids. grid name is the
	 * "fieldName" used in this form for the tabular data. like orderLines.;
	 */
	protected TabularField[] tabularFields;

	/**
	 * describes all the inter-field validations, and form-level validations
	 */
	protected IFormValidation[] validations;

	/**
	 * is a auto-service to get this form ok?
	 */
	protected boolean getOk;

	/**
	 * is a auto-service to save this form ok?
	 */
	protected boolean saveOk;

	/**
	 * is a auto-service to submit this form ok?
	 */
	protected boolean submitOk;

	/**
	 * is a auto-service to submit this form ok?
	 */
	protected boolean partialOk;

	/*
	 * following fields are derived from others. Defined for improving
	 * performance of some methods
	 */

	protected IFormProcessor[] formProcessors = new IFormProcessor[NBR_PROCESSORS];
	/**
	 * index to the values array for the key fields. this is derived based on
	 * fields. This based on the field meta data attribute isKeyField
	 */
	private int[] keyIndexes;
	/**
	 * field indexes are stored as Maps for ease of access
	 */
	private Map<String, Integer> fieldIndexes;
	/**
	 * indexes of tabular fields are also stored in map for ease of access
	 */
	private Map<String, Integer> tableIndexes;

	/**
	 * index to the field that represents the userId. User access may be
	 * implemented based on this field
	 */
	private int userIdFieldIdx = -1;

	/**
	 * for extended classes to set the attributes later
	 */
	public FormStructure() {

	}

	protected void setUserId(String name) {
		if (name != null) {
			this.userIdFieldIdx = this.fieldIndexes.get(name);
		}
	}

	/**
	 * MUST BE CALLED after setting all protected fields
	 */
	protected void initialize() {
		if (this.fields != null) {
			int n = this.fields.length;
			this.fieldIndexes = new HashMap<>(n, 1);
			int[] keys = new int[n];
			int keyIdx = 0;
			for (int i = 0; i < this.fields.length; i++) {
				Field field = this.fields[i];
				this.fieldIndexes.put(field.getFieldName(), i);
				if (field.isKeyField()) {
					keys[keyIdx] = i;
					keyIdx++;
				}
			}
			if (keyIdx != 0) {
				this.keyIndexes = Arrays.copyOf(keys, keyIdx);
			}
		}

		if(this.tabularFields != null) {
			int n = this.tabularFields.length;
			this.tableIndexes = new HashMap<>(n, 1);
			for (int i = 0; i < this.tabularFields.length; i++) {
				this.tableIndexes.put(this.tabularFields[i].fieldName, i);
			}
		}
		
		if(this.userIdFieldName != null) {
			this.userIdFieldIdx = this.getFieldIndex(this.userIdFieldName);
		}
	}

	/**
	 * set/attach a pre-slotted for processor for standard services based on
	 * this form
	 * 
	 * @param processorType
	 * @param processor
	 */
	public void setFormProcessor(int processorType, IFormProcessor processor) {
		if (processorType >= NBR_PROCESSORS) {
			return;
		}
		this.formProcessors[processorType] = processor;
	}

	/**
	 * @param processorType
	 * @return pre-slotted for processor for standard services based on this
	 *         form
	 */
	public IFormProcessor getFormProcessor(int processorType) {
		if (processorType >= NBR_PROCESSORS) {
			return null;
		}
		return this.formProcessors[processorType];
	}

	/**
	 * @return the userIdFieldIdx. -1 if user id field is not present in this
	 *         form
	 */
	public int getUserIdFieldIdx() {
		return this.userIdFieldIdx;
	}

	/**
	 * unique id assigned to this form. like customerDetails. This is unique
	 * across all types of forms within a project
	 * 
	 * @return non-null unique id
	 */
	public String getFormId() {
		return this.uniqueName;
	}

	/**
	 * @return the keyIndexes
	 */
	public int[] getKeyIndexes() {
		return this.keyIndexes;
	}

	/**
	 * @return the fieldNames. non-null. could be empty
	 */
	public Field[] getFields() {
		return this.fields;
	}

	/**
	 * @return the grid names. non-null. could be empty
	 */
	public TabularField[] getTabularFields() {
		return this.tabularFields;
	}

	/**
	 * 
	 * @param fieldName
	 * @return index of the field in the fields array. -1 if this is not a field
	 */
	public int getFieldIndex(String fieldName) {
		Integer idx = this.fieldIndexes.get(fieldName);
		if (idx == null) {
			return -1;
		}
		return idx;
	}

	/**
	 * @param fieldName
	 * @return data element or null if there is no such field
	 */
	public Field getField(String fieldName) {
		Integer idx = this.fieldIndexes.get(fieldName);
		if (idx == null) {
			return null;
		}
		return this.fields[idx];
	}

	/**
	 * 
	 * @param tabularFieldName
	 * @return form structure that represents this grid, or null if no such grid
	 */
	public TabularField getTabularField(String tabularFieldName) {
		Integer idx = this.tableIndexes.get(tabularFieldName);
		if (idx == null) {
			return null;
		}
		return this.tabularFields[idx];
	}

	/**
	 * 
	 * @param tabularFieldName
	 * @return index of this grid in the gridArray. -1 if this is not a grid
	 */
	public int getTableIndex(String tabularFieldName) {
		Integer idx = this.tableIndexes.get(tabularFieldName);
		if (idx == null) {
			return -1;
		}
		return idx;
	}


	/**
	 * @return the validations
	 */
	public IFormValidation[] getValidations() {
		return this.validations;
	}

	/**
	 * 
	 * @param operation
	 * @return A form that can take field/table values
	 */
	public Form newForm(FormOperation operation) {
		Object[] values = null;
		Object[][][] tables = null;
		if (this.fields != null && this.fields.length != 0) {
			values = new Object[this.fields.length];
		}
		if (this.tabularFields != null && this.tabularFields.length != 0) {
			tables = new Object[this.tabularFields.length][][];
		}
		return new Form(this, operation, values, tables);
	}

	/**
	 * 
	 * @param operation
	 * @return a service for the specified operation. null if such an operation
	 *         is not valid for this form
	 */
	public IService getService(String operation) {
		if (SERVICE_TYPE_GET.equals(operation)) {
			if (this.getOk) {
				return new GetService(this);
			}
		} else if (SERVICE_TYPE_SAVE.equals(operation)) {
			if (this.saveOk) {
				return new SaveService(this);
			}
		} else if (SERVICE_TYPE_SUBMIT.equals(operation)) {
			if (this.submitOk) {
				return new SubmitService(this);
			}
		}
		return null;
	}
	
}
