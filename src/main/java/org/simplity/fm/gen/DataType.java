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

import org.apache.poi.ss.usermodel.Row;
import org.simplity.fm.datatypes.ValueType;

/**
 * represents a row in our spreadsheet for each data type
 * 
 * @author simplity.org
 *
 */
class DataType {
	private static final String C = ", ";
	/*
	 * all columns in the fields sheet
	 */
	String fieldName;
	ValueType valueType;
	String messageId;
	String regexDesc;
	String regex;
	int minLength;
	int maxLength;
	long minValue;
	long maxValue;
	String trueLabel;
	String falseLabel;

	void emitJava(StringBuilder sbf, boolean hasList) {
		String cls = this.valueType.getDataTypeClass().getSimpleName();
		String listName = null;
		if (hasList) {
			listName = "ValueLists." + this.fieldName;
		}
		/*
		 * following is the type of line to be output
		 * public static final {className} {fieldName} = new
		 * {className}({errorMessageId}.......);
		 */
		sbf.append("\n\tpublic static final ").append(cls).append(" ").append(this.fieldName);
		sbf.append(" = new ").append(cls).append("(").append(Util.escape(this.messageId));
		/*
		 * append parameters list based on the data type
		 */
		switch (this.valueType) {
		case BOOLEAN:
			break;
		case DATE:
			sbf.append(C).append(this.minValue).append(C).append(this.maxValue);
			break;
		case NUMBER:
			sbf.append(C).append(this.minValue).append(C).append(this.maxValue).append(C).append(listName);
			break;
		case TEXT:
			sbf.append(C).append(this.minLength).append(C).append(this.maxLength).append(C).append(Util.escape(this.regex))
					.append(C).append(listName);
			break;
		default:
			sbf.append(" generating compilation error on valueType=" + this.valueType);
			break;
		}
		/*
		 * close the constructor and we are done
		 */
		sbf.append(");");
	}

	protected static DataType fromRow(Row row) {
		DataType dt = new DataType();
		String s = Util.textValueOf(row.getCell(1)).toUpperCase();
		try {
			dt.valueType = ValueType.valueOf(s);
		} catch (Exception e) {
			System.err.println(s + " is not a valid data type. ");
			return null;
		}

		dt.fieldName = Util.textValueOf(row.getCell(0));
		dt.messageId = Util.textValueOf(row.getCell(2));
		dt.regexDesc = Util.textValueOf(row.getCell(3));
		dt.regex = Util.textValueOf(row.getCell(4));
		dt.minLength = (int) Util.longValueOf(row.getCell(5));
		dt.maxLength = (int) Util.longValueOf(row.getCell(6));
		dt.minValue = Util.longValueOf(row.getCell(7));
		dt.maxValue = Util.longValueOf(row.getCell(8));
		dt.trueLabel = Util.textValueOf(row.getCell(9));
		dt.falseLabel = Util.textValueOf(row.getCell(10));
		return dt;
	}

	protected String getName() {
		return this.fieldName;
	}

	void emitTs(StringBuilder sbf, String errorId, String vl) {
		String eid = errorId;
		if(eid == null || eid.isEmpty()) {
			eid = this.messageId;
		}
		sbf.append(C).append(this.valueType.getIdx()).append(C).append(Util.escape(this.regex));
		sbf.append(C).append(Util.escape(eid)).append(C).append(this.minLength).append(C).append(this.maxLength);
		sbf.append(C).append(this.minValue).append(C).append(this.maxValue).append(C).append(vl);
	}
}
