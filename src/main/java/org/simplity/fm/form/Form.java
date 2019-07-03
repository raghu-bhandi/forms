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
import org.simplity.fm.validn.IValidation;

/**
 * @author simplity.org
 *
 */
public class Form {

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
	 * forms maybe revised periodically, and we may have to keep tarck of them
	 */
	protected String version;
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
	protected ChildForm[] childForms;

	/**
	 * describes all the inter-field validations, and form-level validations
	 */
	protected IValidation[] validations;

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
	 * fields are also stored as Maps for ease of access
	 */
	private Map<String, Field> fieldMap;
	/**
	 * indexes of tabular fields are also stored in map for ease of access
	 */
	private Map<String, ChildForm> childMap;

	/**
	 * index to the field that represents the userId. User access may be
	 * implemented based on this field
	 */
	private int userIdFieldIdx = -1;

	/**
	 * for extended classes to set the attributes later
	 */
	public Form() {
		//
	}

	protected void setUserId(String name) {
		if (name != null) {
			this.userIdFieldIdx = this.fieldMap.get(name).getIndex();
		}
	}

	/**
	 * MUST BE CALLED after setting all protected fields
	 */
	protected void initialize() {
		if (this.fields != null) {
			int n = this.fields.length;
			this.fieldMap = new HashMap<>(n, 1);
			int[] keys = new int[n];
			int keyIdx = 0;
			for (Field field : this.fields) {
				this.fieldMap.put(field.getFieldName(), field);
				if (field.isKeyField()) {
					keys[keyIdx] = field.getIndex();
					keyIdx++;
				}
			}
			if (keyIdx != 0) {
				this.keyIndexes = Arrays.copyOf(keys, keyIdx);
			}
		}

		if(this.childForms != null) {
			int n = this.childForms.length;
			this.childMap = new HashMap<>(n, 1);
			for (ChildForm child :  this.childForms) {
				this.childMap.put(child.fieldName, child);
			}
		}
		
		if(this.userIdFieldName != null) {
			this.userIdFieldIdx = this.getField(this.userIdFieldName).getIndex();
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
		return this.uniqueName + '_' + this.version;
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
	public ChildForm[] getChildForms() {
		return this.childForms;
	}

	/**
	 * @param fieldName
	 * @return data element or null if there is no such field
	 */
	public Field getField(String fieldName) {
		return this.fieldMap.get(fieldName);
	}

	/**
	 * 
	 * @param childName
	 * @return child-form structure that represents the sub-form in this form
	 */
	public ChildForm getChildForm(String childName) {
		return this.childMap.get(childName);
	}

	/**
	 * @return the validations
	 */
	public IValidation[] getValidations() {
		return this.validations;
	}

	/**
	 * 
	 * @param operation
	 * @return A form that can take field/table values
	 */
	public FormData newFormData(FormOperation operation) {
		Object[] values = null;
		Object[][][] tables = null;
		if (this.fields != null && this.fields.length != 0) {
			values = new Object[this.fields.length];
		}
		if (this.childForms != null && this.childForms.length != 0) {
			tables = new Object[this.childForms.length][][];
		}
		return new FormData(this, operation, values, tables);
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
