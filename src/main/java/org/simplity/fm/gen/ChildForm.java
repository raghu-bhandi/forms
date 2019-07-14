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
import java.util.Set;
import java.util.function.Consumer;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

/**
 * represents a Table row in tables sheet of a forms work book
 * 
 * @author simplity.org
 *
 */
class ChildForm {
	private static final String C = ", ";
	private static final int NBR_CELLS = 9;

	String name;
	String label;
	String formName;
	boolean isTabular;
	int minRows;
	int maxRows;
	String errorId;
	String[] linkParentFields;
	String[] linkChildFields;
	int index;

	static ChildForm[] fromSheet(Sheet sheet, Set<String> names) {
		List<ChildForm> list = new ArrayList<>();
		Util.consumeRows(sheet, NBR_CELLS, new Consumer<Row>() {

			@Override
			public void accept(Row row) {
				ChildForm child = fromRow(row);
				if (child == null) {
					return;
				}
				if (names.add(child.name)) {
					list.add(child);
				} else {
					Form.logger.error("Child form name {} is duplicate at row {}. skipped", child.name,
							row.getRowNum());
				}
			}
		});
		int n = list.size();
		if (n == 0) {
			Form.logger.info("No child forms parsed");
			return null;
		}
		ChildForm[] arr = new ChildForm[n];
		for (int i = 0; i < arr.length; i++) {
			ChildForm child = list.get(i);
			child.index = i;
			arr[i] = child;
		}
		Form.logger.info("{} child forms parsed and added.", n);
		return arr;
	}

	static ChildForm fromRow(Row row) {
		ChildForm t = new ChildForm();
		t.name = Util.textValueOf(row.getCell(0));
		if (t.name == null) {
			Form.logger.error("Name missing in row {}. Skipped", row.getRowNum());
			return null;
		}
		t.label = Util.textValueOf(row.getCell(1));
		t.formName = Util.textValueOf(row.getCell(2));
		if (t.formName == null) {
			Form.logger.error("formName is a MUST for a childForm. It is missing in row {}. Row kkipped",
					row.getRowNum());
			return null;
		}
		t.isTabular = Util.boolValueOf(row.getCell(3));
		t.minRows = (int) Util.longValueOf(row.getCell(4));
		t.maxRows = (int) Util.longValueOf(row.getCell(5));
		t.errorId = Util.textValueOf(row.getCell(6));
		String txt1 = Util.textValueOf(row.getCell(7));
		String txt2 = Util.textValueOf(row.getCell(8));
		
		if(txt1 == null) {
			if(txt2 != null) {
				Form.logger.error("Child form has specified parent link fields, but not child link fields. Ignored");
			}
			return t;
		}

		if(txt2 == null) {
			Form.logger.error("Child form has specified child link fields, but not prent link fields. Ignored");
			return t;
		}
		t.linkParentFields = splitToArray(txt1);
		t.linkChildFields = splitToArray(txt2);
		if(t.linkChildFields.length != t.linkParentFields.length) {
			Form.logger.error("Child form has specified {} child link fields, but {} prent link fields. Can not link with suh a mismatch", t.linkChildFields.length, t.linkParentFields.length);
			t.linkChildFields = null;
			t.linkParentFields = null;
		}
		return t;
	}

	private static String[] splitToArray(String text) {
		String result[] = text.split(",");
		for (int i = 0; i < result.length; i++) {
			result[i] = result[i].trim();
		}
		return result;
	}
	void emitJavaConstant(StringBuilder sbf, int idx) {
		sbf.append("\n\tpublic static final int ").append(this.name).append(" = ").append(idx).append(';');
	}

	/**
	 * push this as an element of an array
	 * 
	 * @param sbf
	 */
	void emitJavaCode(StringBuilder sbf) {
		sbf.append("\n\t\t\tnew ChildForm(");

		sbf.append(Util.escape(this.name));
		sbf.append(C).append(Util.escape(this.formName));
		sbf.append(C).append(this.isTabular);
		sbf.append(C).append(this.minRows);
		sbf.append(C).append(this.maxRows);
		sbf.append(C).append(Util.escape(this.errorId));

		sbf.append(')');
	}

	String getFormName() {
		return this.formName;
	}

	void emitTs(StringBuilder sbf) {
		sbf.append("\n\t").append(this.name).append(" = new ChildForm(").append(Util.escapeTs(this.name));
		sbf.append(C).append(this.index);
		sbf.append(C).append(Util.escapeTs(this.label));
		sbf.append(C).append(Util.toClassName(this.formName)).append(".getInstance()");
		sbf.append(C).append(this.minRows);
		sbf.append(C).append(this.maxRows);
		sbf.append(C).append(Util.escapeTs(this.errorId)).append(");");
	}
}
