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
import org.apache.poi.ss.usermodel.Workbook;

/**
 * represents form level validation (across fields) in a form.
 * 
 * @author simplity.org
 *
 */
class Validation {
	private static final int NAME_CELL = 0;
	private static final int TYPE_CUSTOM = 3;

	private static final String[] SHEET_NAMES = { "fromToValidations", "eitherOrValidations",
			"dependentFieldValidatios", "customValidations" };

	private static final String[] CLASS_NAMES = { "FromToValidation", "EitherOrValidation", "DependentFieldValidation",
			"Validation" };

	public static Validation[] fromBook(Workbook book) {
		List<Validation> list = new ArrayList<>();
		for(int i = 0; i < SHEET_NAMES.length; i++) {
			Sheet sheet = book.getSheet(SHEET_NAMES[i]);
			if(sheet != null) {
				loadSheet(sheet, list, i);
			}
		}
		if(list.size() == 0 ) {
			return null;
		}
		return list.toArray(new Validation[0]);
	}

	static void loadSheet(Sheet sheet, List<Validation> list, int idx) {
		int n = sheet.getPhysicalNumberOfRows();
		for(int i = 1; i < n; i++) {
			Row row = sheet.getRow(i);
			if (Util.toStop(row, NAME_CELL)) {
				break;
			}
			if(idx == TYPE_CUSTOM) {
				/*
				 * custom is just description
				 */
				list.add(newCustom());
				return;
			}
			list.add(fromRow(row, idx));
		}
	}

	static Validation fromRow(Row row, int idx) {
		Validation v = new Validation();
		v.validationType = idx;
		v.name1 = Util.textValueOf(row.getCell(0));
		v.name2 = Util.textValueOf(row.getCell(1));
		v.bool = Util.boolValueOf(row.getCell(2));
		v.msgId = Util.textValueOf(row.getCell(3));
		return v;
	}

	static Validation newCustom() {
		Validation v = new Validation();
		v.validationType = TYPE_CUSTOM;
		return v;
	}

	private int validationType;
	private String name1;
	private String name2;
	private String msgId;
	private boolean bool;
	
	void emitJavaCode(StringBuilder sbf, String customPackageName, String formName) {
		sbf.append("\n\t\t\tnew ");
		if (this.validationType == TYPE_CUSTOM) {
			sbf.append(customPackageName).append('.').append(Util.toClassName(formName)).append("Validation()");
			return;
		}
		sbf.append(CLASS_NAMES[this.validationType]).append("(\"").append(this.name1).append("\", \"");
		sbf.append(this.name2).append("\", ").append(this.bool).append(", \"").append(this.msgId).append("\")");
	}
}
