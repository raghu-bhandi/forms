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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.simplity.fm.validn.FromToValidation;
import org.simplity.fm.validn.IValidation;
import org.simplity.fm.Config;
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
	private static final String COMA = ", ";
	private static final String EQ = " = ";

	private String name;
	private SpecialInstructions si;
	private Field[] fields;
	private Map<String, Field> fieldMap;
	private ChildForm[] childForms;
	private FromToPair[] fromToPairs;
	private ExclusivePair[] exclusivePairs;
	private InclusivePair[] inclusivePairs;
	private boolean hasCustom;

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
				form.hasCustom = true;
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
		sbf.append("\n * <br /> generated at ").append(Util.timeStamp()).append(" from file ").append(fileName);
		sbf.append("\n */ ");
		sbf.append("\npublic class ").append(cls).append(" extends Form {");

		/*
		 * all fields and child forms indexes are available as constants
		 */
		this.emitJavaConstants(sbf);

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

		sbf.append("\n\n\t\tthis.initialize();\n\t}\n}\n");
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
				sbf.append(COMA);
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
				sbf.append(COMA);
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
				sbf.append(COMA).append(f.index);
				sbf.append(COMA).append(Util.escape(field.listName));
				sbf.append(COMA).append(Util.escape(field.name));
				sbf.append(COMA).append(Util.escape(field.errorId));
				sbf.append(")");
				sbf.append(sufix);

			}
		}
		if (this.hasCustom) {
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

		sbf.append("/*\n * generated from ").append(fileName).append(" at ").append(Util.timeStamp()).append("\n */");

		sbf.append("\nimport { Form , Field } from '../form/form';");

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
				field.emitTs(sbf, dataTypes, valueLists, keyedLists);
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
		 * put fields into an array.
		 */
		if (this.fields != null && this.fields.length > 0) {
			sbf.append("\n\t\tthis.fields = [");
			for (Field field : this.fields) {
				sbf.append("\n\t\t\tthis.").append(field.name).append(',');
			}
			sbf.setLength(sbf.length() - 1);
			sbf.append("\n\t\t];");
		}

		/*
		 * put child forms into an array
		 */
		if (this.childForms != null && this.childForms.length != 0) {
			sbf.append("\n\n\t\tthis.childForms = [");
			for (ChildForm child : this.childForms) {
				sbf.append("\n\t\t\tthis.").append(child.name).append(',');
			}
			sbf.setLength(sbf.length() - 1);
			sbf.append("\n\t\t];");
		}
		sbf.append("\n\t}");

		/*
		 * end of constructor
		 */
		sbf.append("\n\n\tpublic getName(): string {");
		sbf.append("\n\t\t return '").append(this.name).append("';");
		sbf.append("\n\t}");

		sbf.append("\n}\n");
	}
}
