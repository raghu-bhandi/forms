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
import java.util.function.Consumer;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

/**
 * represents a pair of from-to fields in the form
 * 
 * @author simplity.org
 *
 */
class FromToPair {
	private static final String C = ", ";
	private static final int NBR_CELLS = 4;

	int index1;
	int index2;
	boolean equalOk;
	String fieldName;
	String errorId;

	static FromToPair[] fromSheet(Sheet sheet, Map<String, Field> fields) {
		List<FromToPair> list = new ArrayList<>();
		Util.consumeRows(sheet, NBR_CELLS, new Consumer<Row>() {
			
			@Override
			public void accept(Row row) {
				FromToPair pair = FromToPair.fromRow(row, fields);
				if (pair != null) {
					list.add(pair);
				} 
			}
		});
		
		if (list.size() == 0) {
			return null;
		}
		return list.toArray(new FromToPair[0]);
	}

	/**
	 * @param row
	 * @param fields
	 * @return
	 */
	protected static FromToPair fromRow(Row row, Map<String, Field> fields) {
		FromToPair p = new FromToPair();
		String s1  = Util.textValueOf(row.getCell(0));
		String s2 = Util.textValueOf(row.getCell(1));
		if(s1 == null || s2 == null) {
			Form.logger.error("Row {} has missing column value/s. Skipped", row.getRowNum());
			return null;
		}
		Field f1 = fields.get(s1);
		if(f1 == null) {
			Form.logger.error("{} is not a field name in this form. row {} skipped", s1, row.getRowNum());
			return null;
		}
		p.index1 = f1.index;

		Field f2 = fields.get(s2);
		if(f2 == null) {
			Form.logger.error("{} is not a field name in this form. row {} skipped", s2, row.getRowNum());
			return null;
		}
		p.index2 = f2.index;

		p.equalOk = Util.boolValueOf(row.getCell(2));
		p.fieldName = s1;
		p.errorId = Util.textValueOf(row.getCell(3));
		return p;
	}


	void emitJavaCode(StringBuilder sbf) {
		sbf.append("new FromToValidation(").append(this.index1);
		sbf.append(C).append(this.index2);
		sbf.append(C).append(this.equalOk);
		sbf.append(C).append(Util.escape(this.fieldName));
		sbf.append(C).append(Util.escape(this.errorId));
		sbf.append(");");
	}
}

