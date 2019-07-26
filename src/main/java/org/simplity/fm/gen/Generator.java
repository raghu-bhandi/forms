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
import java.util.Map;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.simplity.fm.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author simplity.org
 *
 */
public class Generator {
	private static final Logger logger = LoggerFactory.getLogger(Generator.class);
	private static final String APP_FILE = "application.xlsx";
	private static final String EXT = ".xlsx";

	/**
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		Config config = Config.getConfig();

		/*
		 * create output folders if required
		 */
		String outputRoot = config.getGeneratedSourceRoot();
		String[] folders = {"form/", "ts/", "list/", "klist/"};
		if(createOutputFolders(outputRoot, folders) == false) {
			return;
		}

		String inputRoot = config.getXlsRootFolder();
		String fileName = inputRoot + APP_FILE;
		File f = new File(fileName);
		if (f.exists() == false) {
			logger.error("project configuration file {} not found. Aborting..", fileName);
			return;
		}

		ProjectInfo project = null;
		try (InputStream ins = new FileInputStream(f); Workbook book = new XSSFWorkbook(ins)) {
			int n = book.getNumberOfSheets();
			if (n == 0) {
				logger.error("Project Work book {} has no sheets in it. Quitting..", f.getPath());
				return;
			}
			project = ProjectInfo.fromWorkbook(book);

		} catch (Exception e) {
			logger.error("Exception while trying to read workbook {}. Error: {}", f.getPath(), e.getMessage());
			e.printStackTrace();
			return;
		}

		/*
		 * generate project level components like data types
		 */
		project.emitJava(outputRoot, config.getGeneratedPackageName(), config.getDataTypesClassName());


		logger.info("Going to process forms under folder {}", inputRoot);
		f = new File(inputRoot + "form/");
		if(f.exists() == false) {
			logger.error("Forms folder {} not found. No forms are processed", f.getPath());
			return;
		}
		Map<String, DataType> typesMap = project.getTypes();
		for (File xls : f.listFiles()) {
			emitForm(xls, outputRoot, typesMap, project);
		}
	}

	private static boolean createOutputFolders(String root, String[] folders) {
		boolean allOk = true;
		for(String folder : folders) {
			File f = new File(root + folder);
			if (!f.exists()) {
				if (!f.mkdirs()) {
					logger.error("Unable to create folder {}. Aborting..." + f.getPath());
					allOk = false;
				}
			}
		}
		return allOk;
	}
	
	private static void emitForm(File xls, String outputRoot, Map<String, DataType> typesMap, ProjectInfo project) {
		String fn = xls.getName();
		if (fn.endsWith(EXT) == false) {
			logger.info("Skipping non-xlsx file {} " + fn);
			return;
		}
		
		fn = fn.substring(0, fn.length() - EXT.length());
		logger.info("Going to generate form " + fn);
		Form form = null;
		try (Workbook book = new XSSFWorkbook(new FileInputStream(xls))) {
			form = Form.fromBook(book, fn, project.commonFields);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Form {} not generated. Error : {}", fn, e.getMessage());
			return;
		}
		
		StringBuilder sbf = new StringBuilder();
		String fileName = xls.getPath().replace('\\', '/');
		form.emitJavaClass(sbf, fileName);
		String outName = outputRoot + "form/" + Util.toClassName(fn) + ".java";
		Util.writeOut(outName, sbf);
		
		sbf.setLength(0);
		form.emitTs(sbf, typesMap, project.lists, project.keyedLists, fileName);
		outName = outputRoot + "ts/" + fn + ".ts";
		Util.writeOut(outName, sbf);
	}
}
