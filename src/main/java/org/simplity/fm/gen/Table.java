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
 * represents a Table row in tables sheet of a forms work book
 * 
 * @author simplity.org
 *
 */
class Table {
	private static final String C = ", ";
	private static final int NAME_CELL = 0;

	private String name;
	private String label;
	private String description;
	private String formName;
	private int minRows;
	private int maxRows;
	private String errorId;
	
	static Table[] fromSheet(Sheet sheet) {
		List<Table> list = new ArrayList<>();
		int n = sheet.getPhysicalNumberOfRows();
		for(int i = 1; i < n; i++) {
			Row row = sheet.getRow(i);
			if (Util.toStop(row, NAME_CELL)) {
				break;
			}
			list.add(fromRow(row));
		}
		if(list.size() == 0 ) {
			return null;
		}
		return list.toArray(new Table[0]);
	}
	
	private static Table fromRow(Row row) {
		Table t = new Table();
		t.name = Util.textValueOf(row.getCell(0));
		t.label = Util.textValueOf(row.getCell(1));
		t.description = Util.textValueOf(row.getCell(2));
		t.formName = Util.textValueOf(row.getCell(3));
		t.minRows = (int)Util.longValueOf(row.getCell(4));
		t.maxRows = (int)Util.longValueOf(row.getCell(5));
		t.errorId = Util.textValueOf(row.getCell(6));
		return t;
	}
	
	void emitJavaConstant(StringBuilder sbf, int idx) {
		sbf.append("\n\tpublic static final int ").append(this.name).append(" = ").append(idx).append(';');
	}

	void emitJavaCode(StringBuilder sbf) {
		sbf.append("\n\t\t\tnew TabularField(\"").append(this.name).append("\", new ").append(Util.toClassName(this.formName)).append("()");
		sbf.append(C).append(this.minRows).append(C).append(this.maxRows);
		sbf.append(C).append(Util.escape(this.errorId)).append(")");
	}
	
	String getFormName() {
		return this.formName;
	}

	void emitTs(StringBuilder sbf) {
		sbf.append("\n\t\tthis.tables.set('").append(this.name).append("', new Table('").append(this.name).append("', '");
		sbf.append(this.label).append("', ").append(Util.toClassName(this.formName)).append(".getInstane(), ");
		sbf.append(this.minRows).append(C).append(this.maxRows).append(C).append(Util.escape(this.errorId)).append("));");
	}
}
