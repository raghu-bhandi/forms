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
import java.io.FileWriter;
import java.io.Writer;
import java.util.Map;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * @author simplity.org
 *
 */
public class TsGenerator {
	private static final String XLSX = "spec/dataTypes.xlsx";
	private static final String OUT_FOLDER = "C:/Users/raghu/eclipse-workspace/ef/src/main/java/example/project/gen/";
	private static final String IN_FOLDER = "C:/Users/raghu/eclipse-workspace/ef/src/main/resources/spec/struct/";
	private static final String EXT_IN = ".xlsx";
	private static final String EXT_OUT = ".ts";

	/**
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		DataTypes dataTypes = null;
		/*
		 * read the xls into our Java object
		 */
		try (XSSFWorkbook book = new XSSFWorkbook(ClassLoader.getSystemResourceAsStream(XLSX))) {
			dataTypes = DataTypes.fromWorkBook(book);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

		/*
		 * ensure the output directory exists
		 */
		File f = new File(OUT_FOLDER);
		if (!f.exists()) {
			if (!f.mkdirs()) {
				System.err.println("Uable to create folder " + OUT_FOLDER);
				return;
			}
		}
		/*
		 * get ready to read files from input
		 */
		f = new File(IN_FOLDER);
		if (f.exists() == false) {
			System.err.println("Folder " + IN_FOLDER + " does not exist. form work books are not converted to java");
			return;
		}
		Map<String, DataType> types = dataTypes.getTypes();
		Map<String, ValueList> valueLists = dataTypes.lists;
		/*
		 * generate java class file from our object now.
		 */
		StringBuilder sbf = new StringBuilder();
		for (File xls : f.listFiles()) {
			String fn = xls.getName();
			if (fn.endsWith(EXT_IN) == false) {
				System.out.println("Skipping non-xlsx file " + fn);
				continue;
			}

			fn = fn.substring(0, fn.length() - EXT_IN.length());
			System.out.println("Going to generate form " + fn);
			Form form = null;
			try (XSSFWorkbook book = new XSSFWorkbook(new FileInputStream(xls))) {
				form = Form.fromBook(book, fn);
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
			sbf.setLength(0);
			File outFile = new File(OUT_FOLDER + fn + EXT_OUT);
			String fileName = xls.getAbsolutePath();
			form.emitTs(sbf,  types, valueLists, fileName);
			try (Writer writer = new FileWriter(outFile)) {
				writer.write(sbf.toString());
			}
		}
	}

}
