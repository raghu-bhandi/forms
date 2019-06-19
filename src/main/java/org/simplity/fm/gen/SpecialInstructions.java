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

import org.apache.poi.ss.usermodel.Sheet;

/**
 * represents specials instructions sheet in a forms work book
 * 
 * @author simplity.org
 *
 */
class SpecialInstructions {
	private static final String L = "\n\t\t\tthis.";

	private String userId;
	private String[] procesors = new String[6];
	private boolean doGet;
	private boolean doSave;
	private boolean doSubmit;
	private boolean doPartial;

	static SpecialInstructions fromSheet(Sheet sheet) {
		SpecialInstructions s = new SpecialInstructions();
		s.userId = Util.textValueOf(sheet.getRow(1).getCell(1));
		s.doGet = Util.boolValueOf(sheet.getRow(2).getCell(1));
		s.doSave = Util.boolValueOf(sheet.getRow(3).getCell(1));
		s.doSubmit = Util.boolValueOf(sheet.getRow(4).getCell(1));
		s.doPartial = Util.boolValueOf(sheet.getRow(5).getCell(1));
		for (int i = 0; i < s.procesors.length; i++) {
			s.procesors[i] = Util.textValueOf(sheet.getRow(i + 6).getCell(1));
		}
		return s;
	}

	void emitJavaAttrs(StringBuilder sbf, String customPackageName) {
		if (this.userId.isEmpty() == false) {
			sbf.append(L).append("userIdFieldName = \"").append(this.userId).append("\";");
		}
		if (this.doGet) {
			sbf.append(L).append("getOk = true;");
		}
		if (this.doSave) {
			sbf.append(L).append("saveOk = true;");
		}
		if (this.doSubmit) {
			sbf.append(L).append("submitOk = true;");
		}
		if (this.doPartial) {
			sbf.append(L).append("partialOk = true;");
		}
		for (int i = 0; i < this.procesors.length; i++) {
			String s = this.procesors[i];
			if (s != null && s.isEmpty() == false) {
				sbf.append(L).append("formProcessors[").append(i).append("] = new ");
				sbf.append(customPackageName).append('.').append(s).append("();");
			}
		}
	}
}
