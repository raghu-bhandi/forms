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

import org.simplity.fm.rdb.DbParam;
import org.simplity.fm.service.IFormProcessor;
import org.simplity.fm.validn.IValidation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author simplity.org
 *
 */
public class Form {
	private static final Logger logger = LoggerFactory.getLogger(Form.class);
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
	protected boolean createGetService;

	/**
	 * is a auto-service to save this form ok?
	 */
	protected boolean createSaveService;

	/**
	 * is a auto-service to submit this form ok?
	 */
	protected boolean createSubmitService;

	/**
	 * is a auto-service to submit this form ok?
	 */
	protected boolean partialSaveAllowed;

	/*
	 * following fields are derived from others. Defined for improving
	 * performance of some methods
	 */

	protected IFormProcessor[] formProcessors = new IFormProcessor[NBR_PROCESSORS];

	/**
	 * index to the values array for the key fields. this is derived based on
	 * fields. This is based on the field meta data attribute isKeyField
	 */
	protected int[] keyIndexes;
	/**
	 * fields are also stored as Maps for ease of access
	 */
	protected Map<String, Field> fieldMap;
	/**
	 * indexes of tabular fields are also stored in map for ease of access
	 */
	protected Map<String, ChildForm> childMap;

	/**
	 * index to the field that represents the userId. User access may be
	 * implemented based on this field
	 */
	protected int userIdFieldIdx = -1;

	/**
	 * meta data required for db operations. null if this is not designed for db
	 * operations
	 */
	protected DbMetaData dbMetaData;

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

		if (this.childForms != null) {
			int n = this.childForms.length;
			this.childMap = new HashMap<>(n, 1);
			for (ChildForm child : this.childForms) {
				this.childMap.put(child.fieldName, child);
			}
		}

		if (this.userIdFieldName != null) {
			Field field = this.fieldMap.get(this.userIdFieldName);
			if (field == null) {
				logger.error("userIdField {} specified, but not defined", this.userIdFieldName);
			} else {
				this.userIdFieldIdx = field.getIndex();
			}
		}
		/*
		 * db meta for child forms?
		 */
		if (this.dbMetaData != null && this.dbMetaData.childMeta != null) {
			this.initializeDbMeta();
		}
	}

	private static final String WH = " WHERE ";

	/**
	 * 
	 */
	private void initializeDbMeta() {
		boolean foundOne = false;
		StringBuilder sbf = new StringBuilder(WH);
		for (int i = 0; i < this.childForms.length; i++) {
			ChildDbMetaData cm = this.dbMetaData.childMeta[i];
			if (cm == null) {
				continue;
			}
			foundOne = true;
			/*
			 * reset the sbf to re-use it
			 */
			sbf.setLength(WH.length());
			Form form = this.childForms[i].form;
			for (String f : cm.childLinkNames) {
				Field field = form.getField(f);
				if (field == null) {
					logger.error(
							"Child link field {} is specified in parent form, but is not defiined as a field in the child form {}",
							f, form.getFormId());
					foundOne = false;
					break;
				}
				sbf.append(field.getDbColumnName()).append("=?, ");
			}
			/*
			 * remove the last comma..
			 */
			sbf.setLength(sbf.length() - 2);
			cm.whereClause = sbf.toString();
		}

		if (!foundOne) {
			this.dbMetaData.childMeta = null;
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
		if (this.version == null) {
			return this.uniqueName;
		}
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
	 * @return db meta data for this form
	 */
	public DbMetaData getDbMetaData() {
		return this.dbMetaData;
	}

	/**
	 * 
	 * @return prepared statement to delete this form data from the DB
	 */
	public String getDeleeSql() {
		return this.dbMetaData.deleteClause + this.dbMetaData.whereClause;
	}

	protected DbParam[] getParams(int[] indexes) {
		DbParam[] params = new DbParam[indexes.length];
		for (int i = 0; i < params.length; i++) {
			int idx = indexes[i];
			params[i] = new DbParam(idx, this.fields[idx].getValueType());
		}
		return params;
	}

	/**
	 * 
	 * @param rows
	 * @return child data for this form based on rows of data
	 */
	public FormData[] createChildData(Object[][] rows) {
		FormData[] result = new FormData[rows.length];
		for (int i = 0; i < result.length; i++) {
			result[i] = new FormData(this, rows[i], null);
		}
		return result;
	}

	protected ChildDbMetaData newChildDbMeta(String[] names, int[] indexes) {
		ChildDbMetaData c = new ChildDbMetaData();
		c.whereParams = this.getParams(indexes);
		c.childLinkNames = names;
		return c;
	}

	/**
	 * @return a data structure
	 */
	public FormData newFormData() {
		Object[] row = new Object[this.fields.length];
		for(Field field: this.fields) {
			Object val = field.getDefaultValue();
			if(val != null) {
				row[field.getIndex()] = val;
			}
		}
		return new FormData(this, row, null);
	}
}
