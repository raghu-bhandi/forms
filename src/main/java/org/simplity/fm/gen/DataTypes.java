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

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.simplity.fm.data.types.ValueType;

/**
 * @author simplity.org
 *
 */
public class DataTypes {
	private static final String LIST_SHEET_NAME = "valueLists";
	private static final String TYPES_SHEET_NAME = "dataTypes";
	protected DataType[] dataTypes;
	protected Map<String, ValueList> lists;

	protected static DataTypes fromWorkBook(XSSFWorkbook book) {
		DataTypes dt = new DataTypes();
		try {
			dt.loadLists(book.getSheet(LIST_SHEET_NAME));
			dt.loadTypes(book.getSheet(TYPES_SHEET_NAME));
			return dt;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private void loadTypes(Sheet sheet) {
		if (sheet == null) {
			System.out.println("No valueLists sheet found for data types.");
			return;
		}
		List<DataType> typeList = new ArrayList<>();
		int n = sheet.getLastRowNum();
		for (int i = 1; i < n; i++) {
			Row row = sheet.getRow(i);
			if (row == null) {
				break;
			}
			Cell cell = row.getCell(0);
			if (cell == null || cell.getCellType() == Cell.CELL_TYPE_BLANK) {
				System.out.println("Row " + i + " is empty?? Stopping");
				break;
			}
			DataType dt = DataType.fromRow(row);
			if (dt == null) {
				break;
			}
			typeList.add(dt);

		}
		this.dataTypes = typeList.toArray(new DataType[0]);
	}

	private void loadLists(Sheet sheet) {
		int n = sheet.getPhysicalNumberOfRows();
		this.lists = new HashMap<>();
		ValueList.Builder builder = ValueList.getBuilder();
		for (int i = 1; i < n;i++) {
			Row row = sheet.getRow(i);
			ValueList list = builder.addRow(row);
			if (list != null) {
				list.getIntoMap(this.lists);
			}
			if (row == null || row.getPhysicalNumberOfCells() == 0) {
				System.out.println(" row " + i + " is empty! Stopping.");
				return;
			}
		}
	}

	protected void emitJavaTypes(StringBuilder sbf, String packageName) {
		sbf.append("package ").append(packageName).append(';');
		sbf.append('\n');
		for (ValueType vt : ValueType.values()) {
			Util.emitImport(sbf, vt.getDataTypeClass());
		}
		sbf.append(
				"\n\n/**\n * static class that has static attributes for all data types defined for this project");
		sbf.append("\n * <br /> generated at ").append(DateFormat.getDateTimeInstance().format(new Date()));
		sbf.append("\n */ ");
		sbf.append("\npublic class DataTypes {");

		for (DataType dt : this.dataTypes) {
			dt.emitJava(sbf, this.lists.containsKey(dt.fieldName));
		}

		sbf.append("\n}\n");
	}

	protected void emitJavaLists(StringBuilder sbf, String packageName) {
		sbf.append("package ").append(packageName).append(';');
		sbf.append('\n');
		Util.emitImport(sbf, Set.class);
		Util.emitImport(sbf, HashSet.class);
		sbf.append("\n\n/**\n * Static class that has all valid value lists for data types");
		sbf.append("\n * <br /> generated at ").append(DateFormat.getDateTimeInstance().format(new Date()));
		sbf.append("\n */ ");
		sbf.append("\npublic class ValueLists {");

		if (this.lists == null) {
			sbf.append("\n\t// no value lists defined");
		} else {
			for (ValueList list : this.lists.values()) {
				list.emitJava(sbf);
			}
		}

		sbf.append("\n}\n");
	}

}
