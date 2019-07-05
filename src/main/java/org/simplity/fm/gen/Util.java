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
import java.io.FileWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.util.Date;
import java.util.function.Consumer;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility methods for dealing with work book
 * 
 * @author simplity.org
 *
 */
class Util {
	private static final Logger logger = LoggerFactory.getLogger(Util.class);

	/**
	 * get boolean value from a cell.
	 * 
	 * @param cell
	 * @return true if we are able to get true value in this cell. false
	 *         otherwise
	 */
	static boolean boolValueOf(Cell cell) {
		if (cell == null) {
			return false;
		}
		int ct = cell.getCellType();
		if (ct == Cell.CELL_TYPE_BOOLEAN) {
			return cell.getBooleanCellValue();
		}
		if (ct == Cell.CELL_TYPE_NUMERIC) {
			return (long) cell.getNumericCellValue() == 0;
		}
		String s = cell.getStringCellValue().toLowerCase();
		if ("true".equals(s)) {
			return true;
		}
		if ("false".equals(s)) {
			return true;
		}
		logger.error("Found  {}  when we were looking for true/false ", s);
		return false;
	}

	/**
	 * 
	 * @param cell
	 * @return value of a cell as text. always non-null. empty string in ase of
	 *         issues
	 */
	static String textValueOf(Cell cell) {
		if (cell == null) {
			return null;
		}
		int ct = cell.getCellType();
		if (ct == Cell.CELL_TYPE_BLANK) {
			return null;
		}
		if (ct == Cell.CELL_TYPE_STRING) {
			return cell.getStringCellValue();
		}
		if (ct == Cell.CELL_TYPE_NUMERIC) {
			return "" + (long) cell.getNumericCellValue();
		}
		return null;
	}

	/**
	 * 
	 * @param cell
	 * @return if cell does not have valid number, then 0
	 */
	static long longValueOf(Cell cell) {
		if (cell == null) {
			return 0;
		}
		int ct = cell.getCellType();
		if (ct == Cell.CELL_TYPE_NUMERIC) {
			return (long) cell.getNumericCellValue();
		}
		if (ct == Cell.CELL_TYPE_BLANK) {
			return 0;
		}
		if (ct == Cell.CELL_TYPE_STRING) {
			try {
				return Long.parseLong(cell.getStringCellValue());
			} catch (Exception e) {
				//
			}
		}
		logger.error("Found {} when we were looking for an integer", cell.getStringCellValue());
		return 0;
	}

	static Object objectValueOfCell(Cell cell) {
		if (cell == null) {
			return null;
		}

		int ct = cell.getCellType();

		if (ct == Cell.CELL_TYPE_STRING) {
			return cell.getStringCellValue();
		}

		if (ct == Cell.CELL_TYPE_BLANK) {
			return null;
		}

		if (ct == Cell.CELL_TYPE_NUMERIC) {
			return (long) cell.getNumericCellValue();
		}

		if (ct == Cell.CELL_TYPE_BOOLEAN) {
			return cell.getBooleanCellValue();
		}
		return null;
	}

	/**
	 * this is actually just string escape, nothing to do with XLSX
	 * 
	 * @param s
	 * @return string with \ and " escaped for it to be printed inside quotes as
	 *         java literal
	 */
	static String escape(String s) {
		if (s == null || s.isEmpty()) {
			return "null";
		}
		return '"' + s.replace("\\", "\\\\").replace("\"", "\\\"") + '"';
	}

	/**
	 * type-script prefers single quotes
	 */
	static String escapeTs(String s) {
		if (s == null || s.isEmpty()) {
			return "null";
		}
		return '\'' + s.replace("\\", "\\\\").replace("'", "''") + '\'';
	}

	/**
	 * write an import statement for the class
	 * 
	 * @param sbf
	 * @param cls
	 */
	static void emitImport(StringBuilder sbf, Class<?> cls) {
		sbf.append("\nimport ").append(cls.getName()).append(';');
	}

	static String toClassName(String name) {
		return name.substring(0, 1).toUpperCase() + name.substring(1);
	}

	/**
	 * 
	 * @param row
	 * @param idx
	 * @return true i the row is null, or cell at idx is empty
	 */
	static boolean hasContent(Row row, int nbrCells) {
		if (row == null) {
			return false;
		}
		for (Cell cell : row) {
			if (cell.getCellType() != Cell.CELL_TYPE_BLANK) {
				return true;
			}
			if (cell.getColumnIndex() >= nbrCells) {
				return false;
			}
		}
		return false;
	}

	/**
	 * @return time stamp
	 */
	public static Object timeStamp() {
		return DateFormat.getDateTimeInstance().format(new Date());
	}

	public static void consumeRows(Sheet sheet, int nbrCells, Consumer<Row> consumer) {
		boolean isFirst = true;
		for (Row row : sheet) {
			if (isFirst) {
				isFirst = false;
				continue;
			}
			if (hasContent(row, nbrCells) == false) {
				logger.info("row {} is empty till column {}. This is considered as the last row. ", row.getRowNum(),
						nbrCells);
				break;
			}
			consumer.accept(row);
		}
	}

	static Sheet[] readSheets(Workbook book, String[] names) {
		Sheet[] sheets = new Sheet[names.length];
		for (int i = 0; i < sheets.length; i++) {
			String s = names[i];
			Sheet sheet = book.getSheet(s);
			int n = sheet == null ? 0 : sheet.getPhysicalNumberOfRows();
			if (n == 0) {
				logger.error("Sheet {} is missing in the workbook or it has no rows.", s);
				sheet = book.createSheet(s);
			} else {
				logger.info("Sheet {} loaded with {} rows", s, n);
			}
			sheets[i] = sheet;
		}
		return sheets;
	}

	static void writeOut(String fileName, StringBuilder sbf) {
		try (Writer writer = new FileWriter(new File(fileName))) {
			writer.write(sbf.toString());
			logger.info("File {} generated.", fileName);
		} catch (Exception e) {
			logger.error("Error while writing file {} \n {}", fileName, e.getMessage());
		}

	}

	static Object escapeObject(Object obj) {
		if (obj == null) {
			return "null";
		}

		if (obj instanceof String) {
			return escape((String) obj);
		}

		return obj.toString();
	}

}
