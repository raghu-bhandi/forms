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
import java.util.function.Consumer;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.simplity.fm.datatypes.ValueType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author simplity.org
 *
 */
class DataTypes {
	static final Logger logger = LoggerFactory.getLogger(DataTypes.class);
	private static final String LIST_SHEET_NAME = "valueLists";
	private static final String KEYED_LIST_SHEET_NAME = "keyedValueLists";
	private static final String TYPES_SHEET_NAME = "dataTypes";

	DataType[] dataTypes;
	Map<String, ValueList> lists;
	Map<String, KeyedValueList> keyedLists;

	static DataTypes fromWorkBook(XSSFWorkbook book) {
		DataTypes dt = new DataTypes();
		try {
			dt.loadTypes(book.getSheet(TYPES_SHEET_NAME));
			dt.loadLists(book.getSheet(LIST_SHEET_NAME));
			dt.loadKeyedLists(book.getSheet(KEYED_LIST_SHEET_NAME));
			return dt;
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
			return null;
		}
	}

	private void loadKeyedLists(XSSFSheet sheet) {
		this.keyedLists = new HashMap<>();
		if(sheet == null) {
			logger.error("Sheet {} not found. keyed value lists will not be parsed.", KEYED_LIST_SHEET_NAME);
			return;
		}
		//we iterate up to a non-existing row to trigger build
		int n = sheet.getLastRowNum() + 1;
		logger.info("Started parsing {} for keyed values lists. Going to parse till row {}", KEYED_LIST_SHEET_NAME, (n-1));
		KeyedValueList.Builder builder = KeyedValueList.getBuilder();
		for (int i = 1; i < n;i++) {
			Row row = sheet.getRow(i);
			if(Util.hasContent(row, 4) == false) {
				row = null;
			}
			KeyedValueList list = builder.addRow(row);
			if (list != null) {
				this.keyedLists.put(list.name, list);
				logger.info("Keyed Valueist {} parsed and added to the list", list.name);
			}
		}
		n = this.keyedLists.size();
		if(n == 0) {
			logger.info("No keyed value lists added.");
		}else {
			logger.info("{} keyed value lists added.", n);
		}
	}

	private void loadTypes(Sheet sheet) {
		if (sheet == null) {
			logger.error("No sheet named {}. No data Types to generate.", TYPES_SHEET_NAME);
			return;
		}
		List<DataType> typeList = new ArrayList<>();
		logger.info("Sheet {} being read..", TYPES_SHEET_NAME);
		Util.consumeRows(sheet, 11, new Consumer<Row>() {
			
			@Override
			public void accept(Row row) {
				DataType dt = DataType.fromRow(row);
				if (dt != null) {
					typeList.add(dt);
				}
			}
		});
		
		int n = typeList.size();
		if(n == 0) {
			logger.error("No valid data type parsed!!");
			return;
		}
		logger.info("{} data types parsed.");
		this.dataTypes = typeList.toArray(new DataType[0]);
	}

	private void loadLists(Sheet sheet) {
		this.lists = new HashMap<>();
		if(sheet == null) {
			logger.error("Sheet {} not found. No value lists are going to be added.");
			return;
		}
		//we iterate up to a non-existing row to trigger build
		int n = sheet.getLastRowNum() + 1;
		logger.info("Started parsing {} for values lists. Going to parse till row {}", LIST_SHEET_NAME, (n-1));
		ValueList.Builder builder = ValueList.getBuilder();
		for (int i = 1; i < n;i++) {
			Row row = sheet.getRow(i);
			if(Util.hasContent(row, 3) == false) {
				row = null;
			}
			ValueList list = builder.addRow(row);
			if (list != null) {
				this.lists.put(list.name, list);
				logger.info("Valueist {} parsed and added to the list", list.name);
			}
		}
		n = this.lists.size();
		if(n == 0) {
			logger.info("No value lists added.");
		}else {
			logger.info("{} value lists added.", n);
		}
	}

	void emitJavaTypes(StringBuilder sbf, String packageName) {
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
			dt.emitJava(sbf, this.lists.containsKey(dt.name));
		}

		sbf.append("\n}\n");
	}

	void emitJavaLists(StringBuilder sbf, String packageName) {
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
	
	Map<String, DataType> getTypes(){
		Map<String, DataType> types = new HashMap<>(this.dataTypes.length);
		for(DataType d :this.dataTypes) {
			types.put(d.name, d);
		}
		return types;
	}
	
	Map<String, ValueList> getLists(){
		return this.lists;
	}
}
