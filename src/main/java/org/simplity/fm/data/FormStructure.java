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
package org.simplity.fm.data;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.simplity.fm.ApplicationError;
import org.simplity.fm.IForm;

/**
 * @author simplity.org
 *
 */
public class FormStructure {
	/**
	 * this is the unique id given this to this form, it is an independent
	 * form. It is the section name in case it is a section of a composite form
	 */
	private String uniqueName;

	/**
	 * Fields in this form.
	 */
	private Field[] fields;

	/**
	 * name of grids/tabular dta. null if there are no gridsgrid name is the
	 * "fieldName" used in this form for the tabular data. like orderLines.;
	 */
	private String[] gridNames;
	/**
	 * structure of grids. same order as the array gridNames. Each element is a
	 * form-structure that describes the columns in the grid
	 */
	private FormStructure[] gridStructures;
	/**
	 * describes all the inter-field validations, and form-level validations
	 */
	private IFormValidation[] validations;
	/**
	 * minimum rows of data required. 0 if this is not a grid, or the data is
	 * optional
	 */
	private int minRows;
	/**
	 * maximum rows of data required. 0 if this is not a grid, or you do not
	 * want to restrict data
	 */
	private int maxRows;
	/*
	 * following fields are derived from others. Defined for improving
	 * performance of some methods
	 */
	/**
	 * index to the values array for the key fields. this is derived based on
	 * fields. This based on the field meta data attribute isKeyField
	 */
	private int[] keyIndexes;
	/**
	 * fields are also stored as Maps for ease of access
	 */
	private Map<String, Field> fieldsMap;
	/**
	 * grid structures are also stored in map for ease of access
	 */
	private Map<String, FormStructure> gridsMap;

	/**
	 * 
	 * this is the unique id given this to this form, it is an independent
	 * form. It is the section name in case it is a section of a composite form
	 * 
	 * @param uniqueName
	 *            this is the unique id given this to this form, it is an
	 *            independent form. It is the section name in case it is a
	 *            section of a composite form
	 * @param fields
	 *            Fields in this form. ensure that each field has its sequenceNo
	 *            set as per their position in the array
	 * @param gridNames
	 *            name of grids/tabular dta. null if there are no grids
	 * @param gridStructures
	 *            structure of grids. same order as the array gridNames. Each
	 *            element is a
	 *            form-structure that describes the columns in the grid
	 * @param validations
	 *            describes all the inter-field validations, and form-level
	 *            validations
	 * @param minRows
	 *            minimum rows of data required. 0 if this is not a grid, or the
	 *            data is optional
	 * @param maxRows
	 *            maximum rows of data required. 0 if this is not a grid, or you
	 *            do not want to restrict data
	 */
	public FormStructure(String uniqueName, Field[] fields, String[] gridNames, FormStructure[] gridStructures,
			IFormValidation[] validations, int minRows, int maxRows) {
		this.fields = fields;
		this.gridNames = gridNames;
		this.gridStructures = gridStructures;
		this.validations = validations;
		this.minRows = minRows;
		this.maxRows = maxRows;
		if (this.fields != null) {
			this.buildFieldsMap();
		}

		if (this.gridNames != null) {
			this.buildGridsMap();
		}
		if (this.validations != null && this.validations.length == 0) {
			this.validations = null;
		}
	}

	private void buildFieldsMap() {
		int n = this.fields.length;
		if (n == 0) {
			this.fields = null;
			return;
		}
		this.fieldsMap = new HashMap<>(n, 1);
		int[] keys = new int[n];
		int keyIdx = 0;
		for (int i = 0; i < this.fields.length; i++) {
			Field field = this.fields[i];
			if (i != field.getSequenceIdx()) {
				throw new ApplicationError(
						"Field " + field.getFieldName() + " in form strcuture " + this.uniqueName + " is at index " + i
								+ " in the fieldNames array, but its sequenceNo is set to " + field.getSequenceIdx());
			}
			this.fieldsMap.put(field.getFieldName(), field);
			if(field.isKeyField()) {
				keys[keyIdx] =i;
				keyIdx++;
			}
		}
		if(keyIdx != 0) {
			this.keyIndexes = Arrays.copyOf(keys, keyIdx);
		}
	}

	private void buildGridsMap() {
		int n = this.gridNames.length;
		if (n == 0) {
			this.gridNames = null;
			return;
		}
		if (this.gridStructures == null || this.gridStructures.length != n) {
			throw new ApplicationError("Form " + this.uniqueName + " has " + n
					+ " grid names but does not haev the same numberof entries in gridStructures");
		}
		this.gridsMap = new HashMap<>(n, 1);
		for (int i = 0; i < this.gridNames.length; i++) {
			this.gridsMap.put(this.gridNames[i], this.gridStructures[i]);
		}

	}

	/**
	 * message to be used if the grid has less than the min or greater than the
	 * max rows. null if no min/max restrictions
	 */
	private String gridMessageId;

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
	public FormStructure[] getGridStructures() {
		return this.gridStructures;
	}

	/**
	 * @param fieldName
	 * @return data element or null if there is no such field
	 */
	public Field getField(String fieldName) {
		return this.fieldsMap.get(fieldName);
	}

	/**
	 * 
	 * @param gridName
	 * @return form structure that represents this grid, or null if no such grid
	 */
	public FormStructure getGridStructure(String gridName) {
		return this.gridsMap.get(gridName);
	}

	/**
	 * @return the gridNames
	 */
	public String[] getGridNames() {
		return this.gridNames;
	}

	/**
	 * @return the minRows
	 */
	public int getMinRows() {
		return this.minRows;
	}

	/**
	 * @return the maxRows
	 */
	public int getMaxRows() {
		return this.maxRows;
	}

	/**
	 * @return the validations
	 */
	public IFormValidation[] getValidations() {
		return this.validations;
	}

	/**
	 * @return message id to be used if the grid does not have the right number
	 *         of rows
	 */
	public String getGridMessageId() {
		return this.gridMessageId;
	}

	/**
	 * 
	 * @return A form that can take field/table values
	 */
	public IForm newForm() {
		Object[] values = null;
		Object[][][] tables = null;
		if (this.fields != null && this.fields.length != 0) {
			values = new Object[this.fields.length];
		}
		if (this.gridNames != null && this.gridNames.length != 0) {
			tables = new Object[this.gridNames.length][][];
		}
		return new Form(this, values, tables);
	}
}
