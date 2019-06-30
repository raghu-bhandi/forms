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

import org.simplity.fm.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author simplity.org
 *
 */
public class DataTypeGenerator {
	private static final Logger logger = LoggerFactory.getLogger(DataTypeGenerator.class);
	/**
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		DataTypes types = DataTypes.loadDataTypes();
		if(types == null) {
			return;
		}

		/*
		 * ensure the directory exists
		 */
		Config config = Config.getConfig();
		String rootFolder = config.getGeneratedSourceRoot();
		File f = new File(rootFolder);
		if (!f.exists()) {
			if (!f.mkdirs()) {
				System.err.println("Unable to create root folder {} for generated source  " + rootFolder);
				return;
			}
		}
		/*
		 * create DataTypes.java in the root folder.
		 */
		StringBuilder sbf = new StringBuilder();
		String rootPack = config.getGeneratedPackageName();
		types.emitJavaTypes(sbf, rootPack);
		writeOut(rootFolder + config.getDataTypesClassName() + ".java", sbf);

		/**
		 * lists are created under list sub-package
		 */
		if (types.lists != null && types.lists.size() > 0) {
			String pack = rootPack + ".list";
			String folder = rootFolder + "list/";
			File dir = new File(folder);
			if (dir.exists() == false) {
				dir.mkdirs();
			}
			for (ValueList list : types.lists.values()) {
				sbf.setLength(0);
				list.emitJava(sbf, pack);
				writeOut(folder + Util.toClassName(list.name) + ".java", sbf);
			}

		}

		/**
		 * keyed lists
		 */
		if (types.keyedLists != null && types.keyedLists.size() > 0) {
			String pack = rootPack + ".klist";
			String folder = rootFolder + "klist/";
			File dir = new File(folder);
			if (dir.exists() == false) {
				dir.mkdirs();
			}
			for (KeyedValueList list : types.keyedLists.values()) {
				sbf.setLength(0);
				list.emitJava(sbf, pack);
				writeOut(folder + Util.toClassName(list.name) + ".java", sbf);
			}

		}
	}

	private static void writeOut(String fileName, StringBuilder sbf) {
		try (Writer writer = new FileWriter(new File(fileName))) {
			writer.write(sbf.toString());
			logger.info("File {} generated.", fileName);
		} catch (Exception e) {
			logger.error("Error while writing file {} \n {}", fileName, e.getMessage());
		}

	}
}
