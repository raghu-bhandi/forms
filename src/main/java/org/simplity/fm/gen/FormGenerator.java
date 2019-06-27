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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * @author simplity.org
 *
 */
public class FormGenerator {
	private static final String EXT = ".xlsx";
	private static final String PACKAGE_NAME = "example.project.gen";
	private static final String CUSTOM_PACKAGE_NAME = "example.project.custom";
	private static final String XLSX = "C:/Users/raghu/eclipse-workspace/ef/src/main/resources/spec/struct/";
	private static final String FOLDER = "C:/Users/raghu/eclipse-workspace/ef/src/main/java/example/project/gen/";
	private static final String STRUCT_FILE_NAME = "FormStructures.java";

	/**
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
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
		f = new File(XLSX);
		if (f.exists() == false) {
			System.err.println("Folder " + XLSX + " does not exist. form work books are not converted to java");
			return;
		}
		StringBuilder sbf = new StringBuilder();

		List<String> forms = new ArrayList<>();
		for (File xls : f.listFiles()) {
			String fn = xls.getName();
			if (fn.endsWith(EXT) == false) {
				System.out.println("Skipping non-xlsx file " + fn);
				continue;
			}
			fn = fn.substring(0, fn.length() - EXT.length());
			System.out.println("Going to generate form " + fn);
			Form form = null;
			try (XSSFWorkbook book = new XSSFWorkbook(new FileInputStream(xls))) {
				form = Form.fromBook(book, fn);
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
			forms.add(fn);
			sbf.setLength(0);
			String fileName = xls.getAbsolutePath();
			File outFile = new File(FOLDER + Util.toClassName(fn) + ".java");
			form.emitJavaClass(sbf, CUSTOM_PACKAGE_NAME, PACKAGE_NAME, fileName);
			try (Writer writer = new FileWriter(outFile)) {
				writer.write(sbf.toString());
			}
		}
		sbf.setLength(0);
		emitStructures(sbf, forms, PACKAGE_NAME);
		f = new File(FOLDER + STRUCT_FILE_NAME);
		try (Writer writer = new FileWriter(f)) {
			writer.write(sbf.toString());
		}
	}

	private static void emitStructures(StringBuilder sbf, List<String> names, String packageName) {
		sbf.append("package ").append(packageName).append(';');
		sbf.append("\n");

		Util.emitImport(sbf, HashMap.class);
		Util.emitImport(sbf, Map.class);
		Util.emitImport(sbf, Form.class);

		sbf.append("\n\n/**\n * static class that has a static attribute for each form defined in this project\n */");
		sbf.append("\n public class FormStructures {");
		sbf.append("\n\tprivate static final Map<String, FormStructure> allStructures = new HashMap<>();");
		sbf.append("\n\n\t/**\n\t *\n\t * @param structureName");
		sbf.append("\n\t * @return form structure, or null if no such form defined in the project\n\t */");

		sbf.append("\n\tpublic static FormStructure getStructure(String structureName) {");
		sbf.append("\n\t\treturn allStructures.get(structureName);\n\t}");

		for (String name : names) {
			sbf.append("\n\t/**\n\t * ").append(name).append("\n\t */");
			sbf.append("\n\tpublic static final FormStructure ").append(name).append(" = new ");
			sbf.append(Util.toClassName(name)).append("();");
		}
		sbf.append("\n\n\tstatic{");
		for (String name : names) {
			sbf.append("\n\t\tallStructures.put(\"").append(name).append("\", ").append(name).append(");");
		}
		sbf.append("\n\t}\n}\n");
	}

}
