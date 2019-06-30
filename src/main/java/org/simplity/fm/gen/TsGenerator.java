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

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.simplity.fm.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author simplity.org
 *
 */
public class TsGenerator {
	private static final Logger logger = LoggerFactory.getLogger(TsGenerator.class); 
	private static final String EXT_IN = ".xlsx";
	private static final String EXT_OUT = ".ts";

	/**
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		DataTypes dataTypes = DataTypes.loadDataTypes();
		if(dataTypes == null) {
			return;
		}

		/*
		 * ensure the output directory exists
		 */
		Config config = Config.getConfig();
		String outFolder = config.getGeneratedSourceRoot() + "ts/";
		File f = new File(outFolder);
		if (!f.exists()) {
			if (!f.mkdirs()) {
				logger.error("Uable to create folder {}. Quitting..", outFolder);
				return;
			}
		}
		/*
		 * get ready to read files from input
		 */
		String inputFolder = config.getXlsRootFolder() + "form/";
		f = new File(inputFolder);
		if (f.exists() == false) {
			logger.error("Forms folder {} does not exist. Quitting..", inputFolder);
			return;
		}

		/*
		 * generate java class file from our object now.
		 */
		StringBuilder sbf = new StringBuilder();
		for (File xls : f.listFiles()) {
			String fn = xls.getName();
			if (fn.endsWith(EXT_IN) == false) {
				logger.info("Skipping non-xlsx file {}", fn);
				continue;
			}

			fn = fn.substring(0, fn.length() - EXT_IN.length());
			logger.info("Going to generate form {}", fn);
			Form form = null;
			try (Workbook book = new XSSFWorkbook(new FileInputStream(xls))) {
				form = Form.fromBook(book, fn);
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("{} NOT loaded. Error : {}", fn, e.getMessage());
				continue;
			}
			
			sbf.setLength(0);
			File outFile = new File(outFolder + fn + EXT_OUT);
			String fileName = xls.getAbsolutePath();
			form.emitTs(sbf,  dataTypes.getTypes(), dataTypes.lists,  dataTypes.keyedLists, fileName);
			try (Writer writer = new FileWriter(outFile)) {
				writer.write(sbf.toString());
			}catch(Exception e) {
				e.printStackTrace();
				logger.error("{} NOT written. Error : {}", outFile, e.getMessage());
				continue;
			}
		}
	}

}
