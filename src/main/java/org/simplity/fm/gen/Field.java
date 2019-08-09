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
import java.util.List;
import java.util.Map;

import org.simplity.fm.datatypes.ValueType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * represents a Field row in fields sheet of a forms work book
 * 
 * @author simplity.org
 *
 */
class Field {
	protected static final Logger logger = LoggerFactory.getLogger(Field.class);
	private static final String C = ", ";

	String name;
	String label;
	String altLabel;
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
	String dbColumnName;
	int index;
	ValueType valueType;

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
		if (this.listKey == null) {
			sbf.append(C).append(Util.escape(this.listName));
		} else {
			sbf.append(C).append("null");
		}
		sbf.append(C).append(Util.escape(this.dbColumnName));
		sbf.append(')');
	}

	void emitJavaCode(StringBuilder sbf) {
		sbf.append("\n\t\t\tnew Field(\"").append(this.name).append('"');
		sbf.append(C).append(this.index);
		sbf.append(C).append(this.isKey);
		sbf.append(C).append("ValueType.").append(this.valueType);
		sbf.append(C).append(Util.escape(this.dbColumnName));
		sbf.append(')');
	}

	void emitFg(StringBuilder sbf, DataType dt) {
		if (dt == null) {
			String msg = "Field " + this.name + " has an invalid data type of " + this.dataType + ". Field not added.";
			logger.error(msg);
			sbf.append("\n\t//ERROR: ").append(msg);
			return;
		}
		/*
		 * default value
		 */
		sbf.append(this.name).append(": ['");
		if (this.defaultValue != null) {
			sbf.append(this.defaultValue);
		}
		sbf.append("',[");
		/*
		 * validators
		 */
		List<String> vals = new ArrayList<>();
		if (this.isRequired) {
			vals.add("Validators.required");
		}
		
		if (dt.name.equalsIgnoreCase("email")) {
			vals.add("Validators.email");
		} else {
			if (dt.valueType == ValueType.DECIMAL || dt.valueType == ValueType.INTEGER) {
				vals.add("Validators.max(" + dt.maxValue + ")");
				vals.add("Validators.min(" + dt.minValue + ")");
			}
			if (dt.regex != null && dt.regex.isEmpty() == false) {
				vals.add("Validators.pattern(" + Util.escapeTs(dt.regex) + ")");
			}
			if(dt.minLength != 0) {
				vals.add("Validators.minLength(" + dt.minLength + ")");
			}
			if(dt.maxLength != 0) {
				vals.add("Validators.maxLength(" + dt.maxLength + ")");
			}
		}
		boolean isFirst = true;
		for(String s: vals) {
			if(isFirst) {
				isFirst = false;
			}else {
				sbf.append(C);
			}
			sbf.append(s);
		}
		sbf.append("]]");
	}

	void emitTs(StringBuilder sbf, DataType dt, Map<String, ValueList> valueLists,
			Map<String, KeyedValueList> keyedLists) {
		if (dt == null) {
			String msg = "Field " + this.name + " has an invalid data type of " + this.dataType + ". Field not added.";
			logger.error(msg);
			sbf.append("\n\t//ERROR: ").append(msg);
			return;
		}
		sbf.append("\n\t").append(this.name).append(" = new Field('").append(this.name);
		sbf.append("', ").append(this.index);
		sbf.append(C).append(Util.escapeTs(this.defaultValue));
		sbf.append(C).append(Util.escapeTs(this.label));
		sbf.append(C).append(Util.escapeTs(this.altLabel));
		sbf.append(C).append(Util.escapeTs(this.placeHolder));
		sbf.append(C).append(Util.escapeTs(dt.trueLabel));
		sbf.append(C).append(Util.escapeTs(dt.falseLabel));
		sbf.append(C).append(this.isEditable);
		String eid = this.errorId;
		if (eid == null || eid.isEmpty()) {
			eid = dt.errorId;
		}
		sbf.append(C).append(Util.escapeTs(eid));
		sbf.append(C).append(this.isRequired);
		sbf.append(C).append(dt.minLength);
		sbf.append(C).append(dt.maxLength);
		sbf.append(C).append(dt.valueType.ordinal());
		sbf.append(C).append(Util.escapeTs(dt.regex));
		sbf.append(C).append(dt.minValue);
		sbf.append(C).append(dt.maxValue);
		sbf.append(C).append(dt.nbrFractions);
		sbf.append(C).append(Util.escapeTs(this.listKey));
		sbf.append(C);
		if (this.listName == null) {
			sbf.append("null, null");
		} else if (this.listKey == null) {
			this.emitListTs(sbf, valueLists);
			sbf.append(",null");
		} else {
			sbf.append("null,");
			this.emitKeyedListTs(sbf, keyedLists);
		}

		sbf.append(");");
	}

	private void emitListTs(StringBuilder sbf, Map<String, ValueList> valueLists) {
		if (this.listName == null) {
			sbf.append("null");
			return;
		}

		ValueList list = valueLists.get(this.listName);
		if (list == null) {
			logger.info("List name {} is not found. probably it is a run time list. Client script with no list.",
					this.listName);
			sbf.append("null");
			return;
		}
		sbf.append("[");
		list.emitTs(sbf, "\n\t\t\t\t");
		sbf.append("\n\t\t\t]");
	}

	private void emitKeyedListTs(StringBuilder sbf, Map<String, KeyedValueList> valueLists) {
		if (this.listName == null) {
			sbf.append("null");
			return;
		}

		KeyedValueList list = valueLists.get(this.listName);
		if (list == null) {
			Form.logger.info("List name {} is not found. probably it is a run time list. Client script with no list.",
					this.listName);
			sbf.append("null");
			return;
		}
		sbf.append("{");
		list.emitTs(sbf, "\n\t\t\t\t");
		sbf.append("\n\t\t\t}");
	}
}
