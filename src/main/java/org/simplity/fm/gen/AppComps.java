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
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import org.simplity.fm.Config;
import org.simplity.fm.datatypes.ValueType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author simplity.org
 *
 */
class AppComps {
	static final Logger logger = LoggerFactory.getLogger(AppComps.class);
	private static final String C = ", ";

	DataType[] dataTypes;
	Map<String, ValueList> lists;
	Map<String, KeyedValueList> keyedLists;
	Map<String, RuntimeList> runtimeLists;
	Field[] commonFields;



	void emitJava(String rootFolder, String packageName, String dataTypesFileName) {
		/*
		 * create DataTypes.java in the root folder.
		 */
		StringBuilder sbf = new StringBuilder();
		this.emitJavaTypes(sbf, packageName);
		Util.writeOut(rootFolder + dataTypesFileName + ".java", sbf);
		String pck = packageName + ".list";
		String fldr = rootFolder + "list/";
		
		File dir = new File(fldr);
		if (dir.exists() == false) {
			dir.mkdirs();
		}
		emitJavaLists(pck, fldr);
		emitJavaKlists(pck, fldr);
		emitJavaRlists(pck, fldr);
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
			Util.emitImport(sbf, Util.getDataTypeClass(vt));
		}

		String cls = Config.getConfig().getDataTypesClassName();

		sbf.append(
				"\n\n/**\n * class that has static attributes for all data types defined for this project. It also extends <code>DataTypes</code>");
		sbf.append("\n * <br /> generated at ").append(LocalDateTime.now());
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

	void emitJavaLists(String pack, String folder) {
		/**
		 * lists are created under list sub-package
		 */
		if (this.lists == null || this.lists.size() == 0) {
			logger.warn("No lists created for this project");
			return;
		}
		StringBuilder sbf = new StringBuilder();
		for (ValueList list : this.lists.values()) {
			sbf.setLength(0);
			list.emitJava(sbf, pack);
			Util.writeOut(folder + Util.toClassName(list.name) + ".java", sbf);
		}
	}
	
	void emitJavaKlists(String pack, String folder) {
		/**
		 * keyed lists
		 */
		if (this.keyedLists == null || this.keyedLists.size() == 0) {
			logger.warn("No keyed lists created for this project");
			return;
		}

		StringBuilder sbf = new StringBuilder();
		for (KeyedValueList list : this.keyedLists.values()) {
			sbf.setLength(0);
			list.emitJava(sbf, pack);
			Util.writeOut(folder + Util.toClassName(list.name) + ".java", sbf);
		}
	}
	
	void emitJavaRlists(String pack, String folder) {
		/**
		 * runtime lists
		 */
		if (this.runtimeLists == null || this.runtimeLists.size() == 0) {
			logger.warn("No runtime lists created for this project");
			return;
		}
		StringBuilder sbf = new StringBuilder();
		for (RuntimeList list : this.runtimeLists.values()) {
			sbf.setLength(0);
			list.emitJava(sbf, pack);
			Util.writeOut(folder + Util.toClassName(list.name) + ".java", sbf);
		}
	}
	
	Map<String, DataType> getTypes() {
		Map<String, DataType> types = new HashMap<>(this.dataTypes.length);
		for (DataType d : this.dataTypes) {
			types.put(d.name, d);
		}
		return types;
	}
}
