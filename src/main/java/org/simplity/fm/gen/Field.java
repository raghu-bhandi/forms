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
	private String name;
	private String label;
	private String desc;
	private String placeHolder;
	private String dataType;
	private String errorId;
	private String defaultValue;
	private boolean isRequired;
	private boolean isEditable;
	private boolean isKey;
	private boolean isDerived;

	static Field[] fromSheet(Sheet sheet) {
		List<Field> list = new ArrayList<>();
		int n = sheet.getPhysicalNumberOfRows();
		for(int i = 1; i < n; i++) {
			Row row = sheet.getRow(i);
			if(row == null || row.getPhysicalNumberOfCells() == 0) {
				System.out.println("Row " + i + " is empty in fields sheet. Stopping..");
				break;
			}
			list.add(fromRow(row));
		}
		if(list.size() == 0 ) {
			return null;
		}
		return list.toArray(new Field[0]);
	}

	private static Field fromRow(Row row) {
		Field f = new Field();
		f.name = Util.textValueOf(row.getCell(0));
		f.label = Util.textValueOf(row.getCell(1));
		f.desc = Util.textValueOf(row.getCell(2));
		f.placeHolder = Util.textValueOf(row.getCell(3));
		f.dataType = Util.textValueOf(row.getCell(4));
		f.isRequired = Util.boolValueOf(row.getCell(5));
		f.defaultValue = Util.textValueOf(row.getCell(6));
		f.errorId = Util.textValueOf(row.getCell(7));
		f.isEditable = Util.boolValueOf(row.getCell(8));
		f.isDerived = Util.boolValueOf(row.getCell(9));
		f.isKey = Util.boolValueOf(row.getCell(10));
		return f;
	}

	void emitJavaConstant(StringBuilder sbf, int idx) {
		sbf.append("\n\tpublic static final int ").append(this.name).append(" = ").append(idx).append(';');
	}

	void emitJavaCode(StringBuilder sbf) {
		sbf.append("\n\t\t\tnew Field(\"").append(this.name).append("\", DataTypes.").append(this.dataType);
		sbf.append(C).append(this.isRequired).append(C).append(Util.escape(this.defaultValue));
		sbf.append(C).append(this.isEditable).append(C).append(Util.escape(this.errorId));
		sbf.append(C).append(this.isDerived).append(C).append(this.isKey).append(')');
	}
}
