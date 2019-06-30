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

package org.simplity.fm.gen;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

/**
 * represents a Field row in fields sheet of a forms work book
 * 
 * @author simplity.org
 *
 */
class Field {
	private static final String C = ", ";
	private static final int NBR_CELLS = 13;

	String name;
	String label;
	String placeHolder;
	String dataType;
	String errorId;
	String defaultValue;
	boolean isRequired;
	boolean isEditable;
	boolean isKey;
	boolean isDerived;
	String listName;
	String listKey;
	int index;

	static Field[] fromSheet(Sheet sheet) {
		List<Field> list = new ArrayList<>();
		Set<String> fieldNames = new HashSet<>();
		Util.consumeRows(sheet, NBR_CELLS, new Consumer<Row>() {
			
			@Override
			public void accept(Row row) {
				Field field = fromRow(row);
				if (field == null) {
					return;
				}
				if (fieldNames.add(field.name)) {
					list.add(field);
				} else {
					Form.logger.error("Field name {} is duplicate at row {}. skipped", field.name, row.getRowNum());
				}
			}
		});
		
		int n = list.size();
		if ( n == 0) {
			Form.logger.warn("No fields for this form!!");
			return null;
		}
		Form.logger.info("{} fields added..", n);
		Field[] arr = new Field[n];
		for(int i = 0; i < arr.length; i++) {
			Field field = list.get(i);
			field.index = i;
			arr[i] = field;
		}
		return arr;
	}

	static Field fromRow(Row row) {
		Field f = new Field();
		f.name = Util.textValueOf(row.getCell(0));
		if (f.name == null) {
			Form.logger.error("Name is null in row {}. Row is skipped", row.getRowNum());
			return null;
		}
		f.label = Util.textValueOf(row.getCell(1));
		// 2 is description. we are not parsing that.
		f.placeHolder = Util.textValueOf(row.getCell(3));
		f.dataType = Util.textValueOf(row.getCell(4));
		f.defaultValue = Util.textValueOf(row.getCell(5));
		f.errorId = Util.textValueOf(row.getCell(6));
		f.isRequired = Util.boolValueOf(row.getCell(7));
		f.isEditable = Util.boolValueOf(row.getCell(8));
		f.isDerived = Util.boolValueOf(row.getCell(9));
		f.isKey = Util.boolValueOf(row.getCell(10));
		f.listName = Util.textValueOf(row.getCell(11));
		f.listKey = Util.textValueOf(row.getCell(12));
		
		return f;
	}

	void emitJavaCode(StringBuilder sbf, String dataTypesName) {
		sbf.append("\n\t\t\tnew Field(\"").append(this.name).append('"');
		sbf.append(C).append(this.index);
		sbf.append(C).append(dataTypesName).append('.').append(this.dataType);
		sbf.append(C).append(Util.escape(this.defaultValue));
		sbf.append(C).append(Util.escape(this.errorId));
		sbf.append(C).append(this.isRequired);
		sbf.append(C).append(this.isEditable);
		sbf.append(C).append(this.isDerived);
		sbf.append(C).append(this.isKey);
		/*
		 * list is handled by inter-field in case key is specified
		 */
		if(this.listKey == null) {
		sbf.append(C).append(Util.escape(this.listName));
		}else {
			sbf.append(C).append("null");
		}
		sbf.append(')');
	}

	void emitTs(StringBuilder sbf, Map<String, DataType> dataTypes, Map<String, ValueList> valueLists, Map<String, KeyedValueList> keyedLists) {
		sbf.append("\n\t").append(this.name).append(" = new Field('").append(this.name);
		sbf.append("', ").append(this.index);
		sbf.append(C).append(Util.escapeTs(this.label));
		sbf.append(C).append(Util.escapeTs(this.placeHolder));
		sbf.append(C).append(Util.escapeTs(this.defaultValue));
		sbf.append(C).append(this.isRequired);
		sbf.append(C).append(this.isEditable);
		sbf.append(C).append(this.isDerived);
		sbf.append(C).append(this.isKey);
		sbf.append(",\n\t\t\t\t");
		/*
		 * data types params are inserted here
		 */
		dataTypes.get(this.dataType).emitTs(sbf, this.errorId);

		/*
		 * drop-down list
		 */
		sbf.append(C).append(Util.escapeTs(this.listKey));
		sbf.append(C);
		if(this.listName == null) {
			sbf.append("null");
		}else if(this.listKey == null) {
			this.emitListTs(sbf, valueLists);
		}else {
			this.emitKeyedListTs(sbf, keyedLists);
		}
		
		sbf.append(");");
	}
	
	private void emitListTs(StringBuilder sbf, Map<String, ValueList> valueLists) {
		if(this.listName == null) {
			sbf.append("null");
			return;
		}
		
		ValueList list = valueLists.get(this.listName);
		if (list == null) {
			Form.logger.info("List name {} is not found. probably it is a run time list. Client script with no list.", this.listName);
			sbf.append("null");
			return;
		}
		sbf.append("[");
		list.emitTs(sbf, "\n\t\t\t\t");
		sbf.append("\n\t\t\t]");
	}
	
	private void emitKeyedListTs(StringBuilder sbf, Map<String, KeyedValueList> valueLists) {
		if(this.listName == null) {
			sbf.append("null");
			return;
		}
		
		KeyedValueList list = valueLists.get(this.listName);
		if (list == null) {
			Form.logger.info("List name {} is not found. probably it is a run time list. Client script with no list.", this.listName);
			sbf.append("null");
			return;
		}
		sbf.append("{");
		list.emitTs(sbf, "\n\t\t\t\t");
		sbf.append("\n\t\t\t}");
	}
}
