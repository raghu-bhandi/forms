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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.simplity.fm.validn.FromToValidation;
import org.simplity.fm.validn.IValidation;
import org.simplity.fm.Config;
import org.simplity.fm.form.ChildDbMetaData;
import org.simplity.fm.form.DbMetaData;
import org.simplity.fm.form.DbOperation;
import org.simplity.fm.validn.DependentListValidation;
import org.simplity.fm.validn.ExclusiveValidation;
import org.simplity.fm.validn.InclusiveValidation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * represents the contents of a spread sheet for a form
 * 
 * @author simplity.org
 *
 */
class Form {
	/*
	 * this logger is used by all related classes of form to give the programmer
	 * the right stream of logs to look for any issue in the workbook
	 */
	static final Logger logger = LoggerFactory.getLogger("Form");

	private static final String C = ", ";
	private static final String EQ = " = ";
	private static final String P = "\n\tprivate static final ";

	String name;
	final Map<String, Object> params = new HashMap<>();
	Field[] fields;
	Map<String, Field> fieldMap;
	Field[] fieldsWithList;
	Field[] keyFields;
	ChildForm[] childForms;
	FromToPair[] fromToPairs;
	ExclusivePair[] exclusivePairs;
	InclusivePair[] inclusivePairs;
	boolean hasCustomValidations;
	boolean isForDbOnly;

	void buildFieldMap() {
		this.fieldMap = new HashMap<>();
		if (this.fields != null) {
			List<Field> list = new ArrayList<>();
			List<Field> keyList = new ArrayList<>();
			for (Field field : this.fields) {
				this.fieldMap.put(field.name, field);
				if (field.listName != null) {
					list.add(field);
				}
				if(field.isKey) {
					keyList.add(field);
				}
			}
			if (list.size() > 0) {
				this.fieldsWithList = list.toArray(new Field[0]);
			}
			if (keyList.size() > 0) {
				this.keyFields = keyList.toArray(new Field[0]);
			}
		}
	}

	Set<String> getNameSet() {
		Set<String> names = new HashSet<>();
		if (this.fields != null) {
			for (Field field : this.fields) {
				names.add(field.name);
			}
		}
		return names;
	}

	void emitJavaClass(StringBuilder sbf, String fileName) {
		Config config = Config.getConfig();
		String customPackage = config.getCustomCodePackage();
		String generatedPackage = config.getGeneratedPackageName();
		String typesName = config.getDataTypesClassName();
		sbf.append("package ").append(generatedPackage).append(".form;");
		sbf.append('\n');

		/*
		 * imports
		 */
		Util.emitImport(sbf, org.simplity.fm.form.Field.class);
		Util.emitImport(sbf, org.simplity.fm.form.Form.class);
		Util.emitImport(sbf, IValidation.class);
		Util.emitImport(sbf, org.simplity.fm.form.ChildForm.class);
		Util.emitImport(sbf, DbMetaData.class);
		Util.emitImport(sbf, ChildDbMetaData.class);

		/*
		 * validation imports on need basis
		 */
		if (this.fromToPairs != null) {
			Util.emitImport(sbf, FromToValidation.class);
		}
		if (this.exclusivePairs != null) {
			Util.emitImport(sbf, ExclusiveValidation.class);
		}
		if (this.inclusivePairs != null) {
			Util.emitImport(sbf, InclusiveValidation.class);
		}
		Util.emitImport(sbf, DependentListValidation.class);
		sbf.append("\nimport ").append(generatedPackage).append('.').append(typesName).append(';');
		/*
		 * class definition
		 */
		String cls = Util.toClassName(this.name);
		sbf.append("\n\n/**\n * class that represents structure of ");
		sbf.append(this.name);
		sbf.append("\n * <br /> generated at ").append(LocalDateTime.now()).append(" from file ").append(fileName);
		sbf.append("\n */ ");
		sbf.append("\npublic class ").append(cls).append(" extends Form {");

		/*
		 * all fields and child forms indexes are available as constants
		 */
		this.emitJavaConstants(sbf);
		this.emitDbStuff(sbf);

		/*
		 * constructor
		 */
		sbf.append("\n\n\t/**\n\t *\n\t */");
		sbf.append("\n\tpublic ").append(cls).append("() {");
		sbf.append("\n\t\tthis.uniqueName = \"").append(this.name).append("\";");
		/*
		 * userIdFieldName
		 */
		Object obj = this.params.get("userIdFieldName");
		if (obj != null) {
			String t = obj.toString().trim();
			if (!t.isEmpty()) {
				sbf.append("\n\t\tthis.userIdFieldName = \"").append(t).append("\";");
			}
		}
		/*
		 * form processors
		 */
		obj = this.params.get("prefillProcessor");
		if (obj != null) {
			String t = obj.toString().trim();
			if (!t.isEmpty()) {
				sbf.append("\n\t\tthis.prefillProcessor = new ").append(customPackage).append('.').append(t)
						.append("();");
			}
		}
		obj = this.params.get("refillProcessor");
		if (obj != null) {
			String t = obj.toString().trim();
			if (!t.isEmpty()) {
				sbf.append("\n\t\tthis.refillProcessor = new ").append(customPackage).append('.').append(t)
						.append("();");
			}
		}

		if (this.fields != null) {
			this.emitJavaFields(sbf, typesName);
		}

		if (this.childForms != null) {
			this.emitJavaChildren(sbf);
		}

		if (this.isForDbOnly == false) {
			this.emitJavaValidations(sbf, customPackage);
		}

		sbf.append("\n\n\t\tthis.setDbMeta();");
		sbf.append("\n\t\tthis.initialize();");

		sbf.append("\n\t}\n}\n");
	}

	private void emitDbStuff(StringBuilder sbf) {
		String tableName = (String) this.params.get("dbTableName");
		Field[] keys = null;
		if (tableName == null) {
			logger.warn("dbTableName not set. no db related code generated for this form");
			sbf.append("\n\n\tprivate void setDbMeta(){\n\t\t//\n\t}");
			return;
		}

		List<Field> list = new ArrayList<>();
		for (Field field : this.fields) {
			if (field.isKey) {
				list.add(field);
			}
		}

		boolean keyIsGenerated = false;
		Object obj = this.params.get("keyIsGenerated");
		if (obj != null && obj instanceof Boolean) {
			keyIsGenerated = (Boolean) obj;
		}
		int n = list.size();
		if (n == 0) {
			logger.error(
					"dbTable name is set but no field is marked as keyField. db operations require key field definition.");
		} else {
			if (n > 1 && keyIsGenerated) {
				logger.error("keyIsGenerated is set to true, but there are {} key fields!!", n);
			}

			keys = list.toArray(new Field[0]);
		}

		this.emitSelect(sbf, tableName);
		this.emitInsert(sbf, tableName);

		if (keys != null) {
			String whereIndexes = this.emitWhere(sbf, keys);
			this.emitUpdate(sbf, whereIndexes, tableName, keys);
			sbf.append(P).append("String DELETE = \"DELETE FROM ").append(tableName).append("\";");
		}

		this.emitChildDbDeclarations(sbf);

		sbf.append("\n\n\tprivate void setDbMeta(){");
		String t = "\n\t\tm.";
		sbf.append("\n\t\tDbMetaData m = new DbMetaData();");
		/*
		 * set dbOperationOk[] to true for auto-service
		 */
		obj = this.params.get("allowDbOperations");
		if (obj != null) {
			for (String op : obj.toString().split(",")) {
				try {
					DbOperation opn = DbOperation.valueOf(op.trim().toUpperCase());
					sbf.append(t).append("dbOperationOk[").append(opn.ordinal()).append("] = true;");
				} catch (Exception e) {
					logger.error("{} is not a valid dbOperation. directive in allowDbOperations ignored");
				}
			}
		}

		sbf.append(t).append("selectClause = SELECT;");
		sbf.append(t).append("selectParams = this.getParams(SELECT_IDX);");
		sbf.append(t).append("insertClause = INSERT;");
		sbf.append(t).append("insertParams = this.getParams(INSERT_IDX);");

		if (keys != null) {
			sbf.append(t).append("whereClause = WHERE;");
			sbf.append(t).append("whereParams = this.getParams(WHERE_IDX);");
			sbf.append(t).append("updateClause = UPDATE;");
			sbf.append(t).append("updateParams = this.getParams(UPDATE_IDX);");
			sbf.append(t).append("deleteClause = DELETE;");
			if (keyIsGenerated) {
				sbf.append(t).append("keyIsGenerated = true;");
			}
		}

		if (this.childForms != null && this.childForms.length > 0) {
			this.emitChildDbParam(sbf);
		}

		sbf.append("\n\t\tthis.dbMetaData = m;");
		sbf.append("\n\t}");
	}

	/**
	 * @param sbf
	 */
	private void emitChildDbParam(StringBuilder sbf) {
		if (this.childForms == null) {
			return;
		}
		sbf.append("\n\t\tChildDbMetaData[] cm = {");
		for (ChildForm child : this.childForms) {
			if (child.linkChildFields == null) {
				sbf.append("null, ");
			} else {
				String f = child.formName.toUpperCase();
				sbf.append("this.newChildDbMeta(").append(f).append("_LINK, ");
				sbf.append(f).append("_IDX), ");
			}
		}
		sbf.setLength(sbf.length() - 2);
		sbf.append("};\n\t\tm.childMeta = cm;");

	}

	/**
	 * @param sbf
	 */
	private void emitChildDbDeclarations(StringBuilder sbf) {
		if (this.childForms == null) {
			return;
		}
		for (ChildForm child : this.childForms) {
			if (child.linkChildFields == null) {
				continue;
			}
			sbf.append("\n\n\tprivate static final String[] ").append(child.formName.toUpperCase()).append("_LINK = {");
			for (String txt : child.linkChildFields) {
				sbf.append('"').append(txt).append("\", ");
			}
			sbf.setLength(sbf.length() - 2);
			sbf.append("};");

			sbf.append("\n\tprivate static final int[] ").append(child.formName.toUpperCase()).append("_IDX = {");
			for (String txt : child.linkParentFields) {
				Field field = this.fieldMap.get(txt);
				if (field == null) {
					logger.error(
							"{} is not a valid field. It is specified as a link parent field in th echild form {}, (form name = {})",
							txt, child.name, child.formName);
					sbf.append("\"InvalidName ").append(txt).append("\", ");
				} else {
					sbf.append(field.index).append(C);
				}
			}
			sbf.setLength(sbf.length() - 2);
			sbf.append("};");
		}

	}

	private void emitJavaConstants(StringBuilder sbf) {
		if (this.fields != null) {
			for (Field field : this.fields) {
				sbf.append("\n\tpublic static final int ").append(field.name).append(EQ).append(field.index)
						.append(';');
			}
		}

		if (this.childForms != null) {
			for (ChildForm child : this.childForms) {
				sbf.append("\n\tpublic static final int ").append(child.name).append(EQ).append(child.index)
						.append(';');
			}
		}
	}

	private void emitJavaFields(StringBuilder sbf, String dataTypesName) {
		if (this.fields == null) {
			sbf.append("\n\t\tthis.fields = null;");
			return;
		}
		sbf.append("\n\n\t\tField[] flds = {");
		boolean isFirst = true;
		for (Field field : this.fields) {
			if (isFirst) {
				isFirst = false;
			} else {
				sbf.append(C);
			}
			if (this.isForDbOnly) {
				field.emitJavaCodeSimple(sbf, dataTypesName);
			} else {
				field.emitJavaCode(sbf, dataTypesName);
			}
		}
		sbf.append("\n\t\t};\n\t\tthis.fields = flds;");
	}

	private void emitJavaChildren(StringBuilder sbf) {
		if (this.childForms == null) {
			sbf.append("\n\t\tthis.childForms = null;");
			return;
		}
		sbf.append("\n\n\t\tChildForm[] chlds = {");
		boolean isFirst = true;
		for (ChildForm child : this.childForms) {
			if (isFirst) {
				isFirst = false;
			} else {
				sbf.append(C);
			}
			child.emitJavaCode(sbf);
		}
		sbf.append("};");
		sbf.append("\n\t\tthis.childForms = chlds;");
	}

	private void emitJavaValidations(StringBuilder sbf, String customPackageName) {
		sbf.append("\n\n\t\tIValidation[] vlds = {");
		int n = sbf.length();
		String sufix = ",\n\t\t\t";
		if (this.fromToPairs != null) {
			for (FromToPair pair : this.fromToPairs) {
				pair.emitJavaCode(sbf);
				sbf.append(sufix);
			}
		}

		if (this.exclusivePairs != null) {
			for (ExclusivePair pair : this.exclusivePairs) {
				pair.emitJavaCode(sbf);
				sbf.append(sufix);
			}
		}

		if (this.inclusivePairs != null) {
			for (InclusivePair pair : this.inclusivePairs) {
				pair.emitJavaCode(sbf);
				sbf.append(sufix);
			}
		}

		/*
		 * dependent lists
		 */
		if (this.fieldsWithList != null) {
			for (Field field : this.fieldsWithList) {
				if (field.listKey == null) {
					continue;
				}
				sbf.append("new DependentListValidation(").append(field.index);
				Field f = this.fieldMap.get(field.listKey);
				sbf.append(C).append(f.index);
				sbf.append(C).append(Util.escape(field.listName));
				sbf.append(C).append(Util.escape(field.name));
				sbf.append(C).append(Util.escape(field.errorId));
				sbf.append(")");
				sbf.append(sufix);
			}
		}
		if (this.hasCustomValidations) {
			sbf.append("new ").append(customPackageName).append('.').append(Util.toClassName(this.name))
					.append("Validation()");
		} else if (sbf.length() > n) {
			/*
			 * remove last sufix
			 */
			sbf.setLength(sbf.length() - sufix.length());
		}

		sbf.append("};");
		sbf.append("\n\t\tthis.validations = vlds;");
	}

	void emitTs(StringBuilder sbf, Map<String, DataType> dataTypes, Map<String, ValueList> valueLists,
			Map<String, KeyedValueList> keyedLists, String fileName) {

		sbf.append("/*\n * generated from ").append(fileName).append(" at ").append(LocalDateTime.now())
				.append("\n */");

		sbf.append("\nimport { Form , Field } from '../form/form';");
		sbf.append("\nimport { Validators } from '@angular/forms'");
		/*
		 * import for child forms being referred
		 */
		if (this.childForms != null) {
			sbf.append("\nimport { ChildForm } from '../form/form';");
			for (ChildForm child : this.childForms) {
				String fn = child.getFormName();
				sbf.append("\nimport { ").append(Util.toClassName(fn)).append(" } from './").append(fn).append("';");
			}
		}

		String cls = Util.toClassName(this.name);
		sbf.append("\n\nexport class ").append(cls).append(" extends Form {");
		sbf.append("\n\tprivate static _instance = new ").append(cls).append("();");

		/*
		 * fields as members
		 */
		if (this.fields != null && this.fields.length > 0) {
			for (Field field : this.fields) {
				field.emitTs(sbf, dataTypes.get(field.dataType), valueLists, keyedLists);
			}
		}

		/*
		 * child forms as members
		 */
		if (this.childForms != null && this.childForms.length != 0) {
			sbf.append("\n");
			for (ChildForm child : this.childForms) {
				child.emitTs(sbf);
			}
		}

		/*
		 * getInstance method
		 */
		sbf.append("\n\n\tpublic static getInstance(): ").append(cls).append(" {");
		sbf.append("\n\t\treturn ").append(cls).append("._instance;\n\t}");

		/*
		 * constructor
		 */
		sbf.append("\n\n\tconstructor() {");
		sbf.append("\n\t\tsuper();");

		/*
		 * put fields into a map.
		 */
		if (this.fields != null && this.fields.length > 0) {
			StringBuilder altSbf = new StringBuilder("\n\t\tthis.controls = {");
			sbf.append("\n\t\tthis.fields = new Map();");
			for (Field field : this.fields) {
				sbf.append("\n\t\tthis.fields.set('").append(field.name).append("', ").append("this.")
						.append(field.name).append(");");
				altSbf.append("\n\t\t\t");
				field.emitFg(altSbf, dataTypes.get(field.dataType));
				altSbf.append(C);
			}

			altSbf.setLength(altSbf.length() - C.length());
			sbf.append(altSbf.toString()).append("\n\t\t};");

		}

		/*
		 * put child forms into an array
		 */
		if (this.childForms != null && this.childForms.length != 0) {
			sbf.append("\n\n\t\tthis.childForms = new Map();");
			for (ChildForm child : this.childForms) {
				sbf.append("\n\t\tthis.childForms.set('").append(child.name).append("', ").append("this.")
						.append(child.name).append(");");
			}
		}

		/*
		 * inter field validations
		 */
		StringBuilder valBuf = new StringBuilder();
		if (this.fromToPairs != null) {
			for (FromToPair pair : this.fromToPairs) {
				if (valBuf.length() > 0) {
					valBuf.append(C);
				}
				pair.emitTs(valBuf);
			}
		}

		if (this.exclusivePairs != null) {
			for (ExclusivePair pair : this.exclusivePairs) {
				if (valBuf.length() > 0) {
					valBuf.append(C);
				}
				pair.emitTs(valBuf);
			}
		}

		if (this.inclusivePairs != null) {
			for (InclusivePair pair : this.inclusivePairs) {
				if (valBuf.length() > 0) {
					valBuf.append(C);
				}
				pair.emitTs(valBuf);
			}
		}

		if (valBuf.length() > 0) {
			sbf.append("\n\t\tthis.validations = [").append(valBuf).append("];");
		}
		/*
		 * fields with drop-downs
		 */
		if (this.fieldsWithList != null) {
			sbf.append("\n\t\tthis.listFields = [");
			for (Field f : this.fieldsWithList) {
				sbf.append(Util.escapeTs(f.name));
				sbf.append(C);
			}
			sbf.setLength(sbf.length() - C.length());
			sbf.append("];");
		}
		/*
		 * are there key fields?
		 */
		if (this.keyFields != null) {
			sbf.append("\n\t\tthis.keyFields = [");
			for (Field f : this.keyFields) {
				sbf.append(Util.escapeTs(f.name));
				sbf.append(C);
			}
			sbf.setLength(sbf.length() - C.length());
			sbf.append("];");
		}
		/*
		 * auto-service operations?
		 */
		Object obj = this.params.get("allowDbOperations");
		if (obj != null) {
			sbf.append("\n\t\tthis.opsAllowed = {");
			boolean first = true;
			for (String op : obj.toString().split(",")) {
				try {
					DbOperation.valueOf(op.trim().toUpperCase());
					if(first) {
						first = false;
					}else {
						sbf.append(C);
					}
					sbf.append(op.trim().toLowerCase()).append(": true");
				} catch (Exception e) {
					logger.error("{} is not a valid dbOperation. directive in allowDbOperations ignored");
				}
			}
			sbf.append("};");
		}
		/*
		 * end of constructor
		 */
		sbf.append("\n\t}");

		sbf.append("\n\n\tpublic getName(): string {");
		sbf.append("\n\t\t return '").append(this.name).append("';");
		sbf.append("\n\t}");

		sbf.append("\n}\n");
	}

	private boolean isKey(String nam, Field[] keys) {
		for (Field field : keys) {
			if (nam.equals(field.name)) {
				return true;
			}
		}
		return false;
	}

	private String emitWhere(StringBuilder sbf, Field[] keys) {
		StringBuilder idxSbf = new StringBuilder();
		sbf.append(P).append("String WHERE = \" WHERE ");
		boolean firstOne = true;
		for (Field field : keys) {
			if (firstOne) {
				firstOne = false;
			} else {
				sbf.append(" AND ");
				idxSbf.append(C);
			}
			sbf.append(field.dbColumnName).append("=?");
			idxSbf.append(field.index);
		}
		sbf.append("\";");
		String idxStr = idxSbf.toString();
		sbf.append(P).append("int[] WHERE_IDX = {").append(idxStr).append("};");

		return idxStr;
	}

	private void emitSelect(StringBuilder sbf, String tableName) {
		StringBuilder idxSbf = new StringBuilder();
		sbf.append(P).append("String SELECT = \"SELECT ");

		boolean firstOne = true;
		for (Field field : this.fields) {
			if (field.dbColumnName == null) {
				continue;
			}
			if (firstOne) {
				firstOne = false;
			} else {
				sbf.append(C);
				idxSbf.append(C);
			}
			sbf.append(field.dbColumnName);
			idxSbf.append(field.index);
		}

		sbf.append(" FROM ").append(tableName);
		sbf.append("\";");
		sbf.append(P).append("int[] SELECT_IDX = {").append(idxSbf).append("};");

	}

	private void emitInsert(StringBuilder sbf, String tableName) {
		sbf.append(P).append(" String INSERT = \"INSERT INTO ").append(tableName).append('(');
		StringBuilder idxSdf = new StringBuilder();
		idxSdf.append(P).append("int[] INSERT_IDX = {");
		StringBuilder vbf = new StringBuilder();
		boolean firstOne = true;
		for (Field field : this.fields) {
			if (field.dbColumnName == null) {
				continue;
			}
			if (firstOne) {
				firstOne = false;
			} else {
				sbf.append(C);
				vbf.append(C);
				idxSdf.append(C);
			}
			sbf.append(field.dbColumnName);
			vbf.append('?');
			idxSdf.append(field.index);
		}

		sbf.append(") values (").append(vbf).append(")\";");
		sbf.append(idxSdf).append("};");
	}

	private void emitUpdate(StringBuilder sbf, String whereIndexes, String tableName, Field[] keys) {
		sbf.append(P).append(" String UPDATE = \"UPDATE ").append(tableName).append(" SET ");
		StringBuilder idxSbf = new StringBuilder();
		idxSbf.append(P).append(" int[] UPDATE_IDX = {");
		boolean firstOne = true;
		for (Field field : this.fields) {
			if (field.dbColumnName == null || this.isKey(field.name, keys)) {
				continue;
			}
			if (firstOne) {
				firstOne = false;
			} else {
				sbf.append(C);
				idxSbf.append(C);
			}
			sbf.append(field.dbColumnName).append("=?");
			idxSbf.append(field.index);
		}

		sbf.append("\";");
		sbf.append(idxSbf).append(C).append(whereIndexes).append("};");
	}
}
