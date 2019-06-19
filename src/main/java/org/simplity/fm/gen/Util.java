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

import org.apache.poi.ss.usermodel.Cell;

/**
 * Utility methods for dealing with work book
 * 
 * @author simplity.org
 *
 */
class Util {
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
		System.err.println("Found " + s + " when we were looking for true/false ");
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
			return "";
		}
		if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
			return "" + (long) cell.getNumericCellValue();
		}
		return cell.getStringCellValue();
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
		System.err.println("Found " + cell.getStringCellValue() + " when we were looking for an integer");
		return 0;
	}
	
	/**
	 * this is actually just string escape, nothing to do with XLSX
	 * @param s
	 * @return string with \ and " escaped for it to be printed inside quotes as java literal
	 */
	static String escape(String s) {
		if (s == null) {
			return "null";
		}
		return '"' + s.replace("\\", "\\\\").replace("\"", "\\\"") + '"';
	}

	/**
	 * write an import statement for the class
	 * @param sbf
	 * @param cls
	 */
	static void emitImport(StringBuilder sbf, Class<?> cls) {
		sbf.append("\nimport ").append(cls.getName()).append(';');
	}
	
	static String toClassName(String name) {
		return name.substring(0,1).toUpperCase() + name.substring(1);
	}
}
