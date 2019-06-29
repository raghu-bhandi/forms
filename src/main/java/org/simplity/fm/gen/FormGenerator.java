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
import java.util.List;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.simplity.fm.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author simplity.org
 *
 */
public class FormGenerator {
	private static final Logger logger = LoggerFactory.getLogger(DataTypeGenerator.class);
	private static final String EXT = ".xlsx";

	/**
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		Config config = Config.getConfig();
		String xlsRoot = config.getXlsRootFolder() + "form/";
		File xlsDir = new File(xlsRoot);
		if (xlsDir.exists() == false) {
			logger.error("Root folder for forms {} does not exist. Aborting..", xlsRoot);
			return;
		}
		logger.info("Going to prcess forms under folder {}", xlsRoot);
		/*
		 * ensure that the output directory exists
		 */
		String targetFolder = config.getGeneratedSourceRoot() + "form/";
		File f = new File(targetFolder);
		if (!f.exists()) {
			if (!f.mkdirs()) {
				logger.error("Uable to create folder {}. Aborting..." + targetFolder);
				return;
			}
		}

		StringBuilder sbf = new StringBuilder();
		List<String> forms = new ArrayList<>();
		for (File xls : xlsDir.listFiles()) {
			String fn = xls.getName();
			if (fn.endsWith(EXT) == false) {
				logger.info("Skipping non-xlsx file {} " + fn);
				continue;
			}
			fn = fn.substring(0, fn.length() - EXT.length());
			logger.info("Going to generate form " + fn);
			Form form = null;
			try (Workbook book = new XSSFWorkbook(new FileInputStream(xls))) {
				form = Form.fromBook(book, fn);
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("Form {} not generated. Error : {}", fn, e.getMessage());
				continue;
			}
			forms.add(fn);
			sbf.setLength(0);
			String fileName = xls.getAbsolutePath();
			File outFile = new File(targetFolder + Util.toClassName(fn) + ".java");
			form.emitJavaClass(sbf, fileName);
			try (Writer writer = new FileWriter(outFile)) {
				writer.write(sbf.toString());
				logger.info("{} generated.", outFile.getAbsolutePath());
			}catch (Exception e) {
				e.printStackTrace();
				logger.error("Error while writing to file {}. Error : {}", outFile.getAbsolutePath(), e.getMessage());
				continue;
			}
		}
	}
}
