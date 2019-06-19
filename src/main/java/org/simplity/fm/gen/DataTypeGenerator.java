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

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * @author simplity.org
 *
 */
public class DataTypeGenerator {
	private static final String PACKAGE_NAME = "example.project.gen";
	private static final String XLSX = "spec/dataTypes.xlsx";
	private static final String FOLDER = "C:/Users/raghu/eclipse-workspace/ef/src/main/java/example/project/gen/";
	private static final String TYPES_FILE = FOLDER + "DataTypes.java";
	private static final String LIST_FILE = FOLDER + "ValueLists.java";

	/**
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		DataTypes types = null;
		/*
		 * read the xls into our Java object 
		 */
		try (XSSFWorkbook book = new XSSFWorkbook(ClassLoader.getSystemResourceAsStream(XLSX))) {
			types = DataTypes.fromWorkBook(book);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		
		/*
		 * ensure the directory exists
		 */
		File f = new File(FOLDER);
		if (!f.exists()) {
			if (!f.mkdirs()) {
				System.err.println("Uable to create folder " + FOLDER);
				return;
			}
		}
		/*
		 * generate java class file from our object now. 
		 */
		StringBuilder sbf = new StringBuilder();

		types.emitJavaLists(sbf, PACKAGE_NAME);
		f = new File(LIST_FILE);
		try (Writer writer = new FileWriter(f)) {
			writer.write(sbf.toString());
		}
		
		sbf.setLength(0);
		types.emitJavaTypes(sbf, PACKAGE_NAME);
		f = new File(TYPES_FILE);
		try (Writer writer = new FileWriter(f)) {
			writer.write(sbf.toString());
		}

	}

}
