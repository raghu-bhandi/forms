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

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.simplity.fm.validn.FromToValidation;
import org.simplity.fm.validn.IValidation;
import org.simplity.fm.Config;
import org.simplity.fm.form.ChildDbMetaData;
import org.simplity.fm.form.DbMetaData;
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
	private static final String[] SHEET_NAMES = { "specialInstructions", "fields", "childForms", "fromToPairs",
			"mutuallyExclusivePairs", "mutuallyInclusivepairs", "customValidations" };

	private static final String[] SHEET_DESC = { "service or processing ", "fields", "child Forms (tables, sub-forms)",
			"from-To inter-field validations", "either-or type of inter-field validaitons",
			"if-a-then-b type of inter-field validaitons", "custom validations" };
	private static final String C = ", ";
	private static final String EQ = " = ";
	private static final String P = "\n\tprivate static final ";

	String name;
	SpecialInstructions si;
	Field[] fields;
	Map<String, Field> fieldMap;
	ChildForm[] childForms;
	FromToPair[] fromToPairs;
	ExclusivePair[] exclusivePairs;
	InclusivePair[] inclusivePairs;
	boolean hasCustomValidations;

	static Form fromBook(Workbook book, String formName, Field[] commonFields) {
		logger.info("Started parsing work book " + formName);
		Form form = new Form();
		form.name = formName;
		Sheet[] sheets = new Sheet[SHEET_NAMES.length];
		Sheet sheet;
		for (int i = 0; i < sheets.length; i++) {
			sheet = book.getSheet(SHEET_NAMES[i]);
			if (sheet == null) {
				logger.error("Sheet {} is missing. Mo {} will be parsed.", SHEET_NAMES[i], SHEET_DESC[i]);
			} else {
				sheets[i] = sheet;
			}
		}

		sheet = sheets[0];
		if (sheet != null) {
			form.si = SpecialInstructions.fromSheet(sheet);
		}

		sheet = sheets[1];
		if (sheet != null) {
			if (form.si.addCommonFields) {
				form.fields = Field.fromSheet(sheet, commonFields);
			} else {
				form.fields = Field.fromSheet(sheet, null);
			}
		}

		Set<String> names = form.getNameSet();
		sheet = sheets[2];
		if (sheet != null) {
			form.childForms = ChildForm.fromSheet(sheet, names);
		}

		form.buildFieldMap();
		sheet = sheets[3];
		if (sheet != null) {
			form.fromToPairs = FromToPair.fromSheet(sheet, form.fieldMap);
		}

		sheet = sheets[4];
		if (sheet != null) {
			form.exclusivePairs = ExclusivePair.fromSheet(sheet, form.fieldMap);
		}

		sheet = sheets[5];
		if (sheet != null) {
			form.inclusivePairs = InclusivePair.fromSheet(sheet, form.fieldMap);
		}

		sheet = sheets[6];
		if (sheet != null) {
			if (Util.hasContent(sheet.getRow(1), 2)) {
				form.hasCustomValidations = true;
				logger.info("custom validiton added. Ensure that you write the desired java class for this.");
			} else {
				logger.info("No custom validaitons added.");
			}
		}

		logger.info("Done parsing form " + formName);
		return form;
	}

	private void buildFieldMap() {
		this.fieldMap = new HashMap<>();
		if (this.fields != null) {
			for (Field field : this.fields) {
				this.fieldMap.put(field.name, field);
			}
		}
	}

	private Set<String> getNameSet() {
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
		/*
		 * importing anyways (avoiding a loop thru all fields to see if any one
		 * has)
		 */
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

		this.si.emitJavaAttrs(sbf, customPackage);

		if (this.fields != null) {
			this.emitJavaFields(sbf, typesName);
		}

		if (this.childForms != null) {
			this.emitJavaChildren(sbf);
		}

		this.emitJavaValidations(sbf, customPackage);

		sbf.append("\n\n\t\tthis.setDbMeta();");
		sbf.append("\n\t\tthis.initialize();");

		sbf.append("\n\t}\n}\n");
	}

	private void emitDbStuff(StringBuilder sbf) {
		String tableName = (String) this.si.settings.get("dbTableName");
		Field[] keys = new Field[0];
		if (tableName == null) {
			logger.warn("dbTableName not set. no db related code genrated for this form");
		} else {
			List<Field> list = new ArrayList<>();
			for (Field field : this.fields) {
				if (field.isKey) {
					list.add(field);
				}
			}
			int n = list.size();
			if (n == 0) {
				logger.error(
						"dbTable name is set but no field is marked as keyField. db operations require key field definition.");
			} else {
				if (this.si.keyIsGenerated && n > 1) {
					logger.error("keyIsGenerated is set to true, but there are {} key fields!!", n);
				}
				keys = list.toArray(keys);
			}
		}

		if (tableName == null) {
			sbf.append("\n\n\tprivate void setDbMeta(){\n\t\t//\n\t}");
			return;
		}

		String whereIndexes = this.emitWhere(sbf, keys);

		this.emitSelect(sbf, tableName);
		this.emitInsert(sbf, tableName);
		this.emitUpdate(sbf, whereIndexes, tableName, keys);
		sbf.append(P).append("String DELETE = \"DELETE FROM ").append(tableName).append("\";");

		this.emitChildDbDeclarations(sbf);

		sbf.append("\n\n\tprivate void setDbMeta(){");
		String t = "\n\t\tm.";
		sbf.append("\n\t\tDbMetaData m = new DbMetaData();");
		sbf.append(t).append("whereClause = WHERE;");
		sbf.append(t).append("whereParams = this.getParams(WHERE_IDX);");
		sbf.append(t).append("selectClause = SELECT;");
		sbf.append(t).append("selectParams = this.getParams(SELECT_IDX);");
		sbf.append(t).append("insertClause = INSERT;");
		sbf.append(t).append("insertParams = this.getParams(INSERT_IDX);");
		sbf.append(t).append("updateClause = UPDATE;");
		sbf.append(t).append("updateParams = this.getParams(UPDATE_IDX);");
		sbf.append(t).append("deleteClause = DELETE;");
		if (this.si.keyIsGenerated) {
			sbf.append(t).append("keyIsGenerated = true;");
		}

		this.emitChildDbParam(sbf);

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
			field.emitJavaCode(sbf, dataTypesName);
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
		 * dependent lits
		 */
		if (this.fields != null) {
			for (Field field : this.fields) {
				if (field.listName == null || field.listKey == null) {
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
				sbf.append("\n\t\tthis.fields.set('").append(field.name).append("', ").append("this.").append(field.name).append(");");
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
				sbf.append("\n\t\tthis.childForms.set('").append(child.name).append("', ").append("this.").append(child.name).append(");");
			}
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
