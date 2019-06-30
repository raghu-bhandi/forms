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

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.simplity.fm.Config;
import org.simplity.fm.datatypes.ValueType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author simplity.org
 *
 */
class DataTypes {
	static final Logger logger = LoggerFactory.getLogger(DataTypes.class);
	private static final String XLSX = "dataTypes.xlsx";
	private static final String[] SHEET_NAMES = { "dataTypes", "valueLists", "keyedValueLists" };
	private static final String C = ", ";

	DataType[] dataTypes;
	Map<String, ValueList> lists;
	Map<String, KeyedValueList> keyedLists;

	public static DataTypes loadDataTypes() {
		/*
		 * read the xls into our Java object
		 */
		Config config = Config.getConfig();
		String res = config.getXlsRootFolder() + XLSX;
		File f = new File(res);
		if (f.exists() == false) {
			logger.error("xls file {} not found. Aborting..", res);
		}

		try (InputStream ins = new FileInputStream(f); Workbook book = new XSSFWorkbook(ins)) {
			int n = book.getNumberOfSheets();
			if (n == 0) {
				logger.error("Work book has no sheets in it. Quitting..");
				return null;
			}
			return fromWorkBook(book);

		} catch (Exception e) {
			logger.error("Exception while trying to read workbook {}. Error: {}", res, e.getMessage());
			e.printStackTrace();
			return null;
		}
	}

	private static DataTypes fromWorkBook(Workbook book) {
		Sheet[] sheets = Util.readSheets(book, SHEET_NAMES);
		DataTypes dt = new DataTypes();
		dt.dataTypes = loadTypes(sheets[0]);
		dt.lists = loadLists(sheets[1]);
		dt.keyedLists = loadKeyedLists(sheets[2]);
		return dt;
	}

	private static DataType[] loadTypes(Sheet sheet) {
		logger.info("Started parsing for data types from sheet {} with {} rows ", sheet.getSheetName(),
				sheet.getPhysicalNumberOfRows());
		List<DataType> typeList = new ArrayList<>();
		Util.consumeRows(sheet, DataType.NBR_CELLS, new Consumer<Row>() {

			@Override
			public void accept(Row row) {
				DataType dt = DataType.fromRow(row);
				if (dt != null) {
					typeList.add(dt);
				}
			}
		});

		int n = typeList.size();
		if (n == 0) {
			logger.error("No valid data type parsed!!");
			return null;
		}
		logger.info("{} data types parsed.", n);
		return typeList.toArray(new DataType[0]);
	}

	private static Map<String, ValueList> loadLists(Sheet sheet) {
		Map<String, ValueList> map = new HashMap<>();
		// we iterate up to a non-existing row to trigger build
		int n = sheet.getLastRowNum() + 1;
		logger.info("Started parsing for values lists. ");
		ValueList.Builder builder = ValueList.getBuilder();
		for (int i = 1; i < n; i++) {
			Row row = sheet.getRow(i);
			if (Util.hasContent(row, ValueList.NBR_CELLS) == false) {
				row = null;
			}
			ValueList list = builder.addRow(row);
			if (list != null) {
				map.put(list.name, list);
				logger.info("Valueist {} parsed and added to the list", list.name);
			}
		}
		n = map.size();
		if (n == 0) {
			logger.info("No value lists added.");
			return null;
		}
		logger.info("{} value lists added.", n);
		return map;
	}

	private static Map<String, KeyedValueList> loadKeyedLists(Sheet sheet) {
		Map<String, KeyedValueList> map = new HashMap<>();
		// we iterate up to a non-existing row to trigger build
		int n = sheet.getLastRowNum() + 1;
		logger.info("Started parsing keyed lists ");
		KeyedValueList.Builder builder = KeyedValueList.getBuilder();
		for (int i = 1; i < n; i++) {
			Row row = sheet.getRow(i);
			if (Util.hasContent(row, KeyedValueList.NBR_CELLS) == false) {
				row = null;
			}
			KeyedValueList list = builder.addRow(row);
			if (list != null) {
				map.put(list.name, list);
				logger.info("Keyed Valueist {} parsed and added to the list", list.name);
			}
		}
		n = map.size();
		if (n == 0) {
			logger.info("No keyed value lists added.");
			return null;
		}
		logger.info("{} keyed value lists added.", n);
		return map;
	}

	void emitJavaTypes(StringBuilder sbf, String packageName) {
		sbf.append("package ").append(packageName).append(';');
		sbf.append('\n');

		Util.emitImport(sbf, HashMap.class);
		Util.emitImport(sbf, Map.class);
		sbf.append("\n");

		Util.emitImport(sbf, org.simplity.fm.IDataTypes.class);
		Util.emitImport(sbf, org.simplity.fm.datatypes.DataType.class);
		for (ValueType vt : ValueType.values()) {
			Util.emitImport(sbf, vt.getDataTypeClass());
		}

		String cls = Config.getConfig().getDataTypesClassName();

		sbf.append(
				"\n\n/**\n * class that has static attributes for all data types defined for this project. It also extends <code>DataTypes</code>");
		sbf.append("\n * <br /> generated at ").append(DateFormat.getDateTimeInstance().format(new Date()));
		sbf.append("\n */ ");
		sbf.append("\npublic class ").append(cls).append(" implements IDataTypes {");

		for (DataType dt : this.dataTypes) {
			dt.emitJava(sbf);
		}

		sbf.append("\n\n\tpublic static final DataType[] allTypes = {");
		for (DataType dt : this.dataTypes) {
			sbf.append(dt.name).append(C);
		}
		sbf.setLength(sbf.length() - C.length());
		sbf.append("};");

		sbf.append("\n\t private Map<String, DataType> typesMap;");

		sbf.append("\n\t/**\n\t * default constructor\n\t */");

		sbf.append("\n\tpublic ").append(cls).append("() {");
		sbf.append("\n\t\tthis.typesMap = new HashMap<>();");
		sbf.append("\n\t\tfor(DataType dt: allTypes) {");
		sbf.append("\n\t\t\tthis.typesMap.put(dt.getName(), dt);");
		sbf.append("\n\t\t}\n\t}");

		sbf.append("\n\n@Override");
		sbf.append("\n\tpublic DataType getDataType(String name) {");
		sbf.append("\n\t\treturn this.typesMap.get(name);");
		sbf.append("\n\t}");

		sbf.append("\n}\n");
	}

	Map<String, DataType> getTypes() {
		Map<String, DataType> types = new HashMap<>(this.dataTypes.length);
		for (DataType d : this.dataTypes) {
			types.put(d.name, d);
		}
		return types;
	}
}
