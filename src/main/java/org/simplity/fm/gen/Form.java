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

import java.text.DateFormat;
import java.util.Date;

import org.apache.poi.ss.usermodel.Workbook;
import org.simplity.fm.form.DependentFieldValidation;
import org.simplity.fm.form.EitherOrValidation;
import org.simplity.fm.form.FormStructure;
import org.simplity.fm.form.FromToValidation;
import org.simplity.fm.form.IFormValidation;
import org.simplity.fm.form.TabularField;

/**
 * represents the contents of a spread sheet for a form
 * 
 * @author simplity.org
 *
 */
class Form {
	private static final String SI = "specialInstructions";
	private static final String FIELDS = "fields";
	private static final String TABLES = "tables";

	private String name;
	private SpecialInstructions si;
	private Field[] fields;
	private Table[] tables;
	private Validation[] validations;

	static Form fromBook(Workbook book, String formName) {
		Form form = new Form();
		form.name = formName;
		form.si = SpecialInstructions.fromSheet(book.getSheet(SI));
		form.fields = Field.fromSheet(book.getSheet(FIELDS));
		form.tables = Table.fromSheet(book.getSheet(TABLES));
		form.validations = Validation.fromBook(book);
		return form;
	}

	void emitJavaClass(StringBuilder sbf, String customPackageName, String generatedPackageName) {
		sbf.append("package ").append(generatedPackageName).append(';');
		sbf.append('\n');
		Util.emitImport(sbf, org.simplity.fm.form.Field.class);
		Util.emitImport(sbf, FormStructure.class);
		Util.emitImport(sbf, IFormValidation.class);
		Util.emitImport(sbf, TabularField.class);
		Util.emitImport(sbf, FromToValidation.class);
		Util.emitImport(sbf, EitherOrValidation.class);
		Util.emitImport(sbf, DependentFieldValidation.class);
		String cls = Util.toClassName(this.name);
		sbf.append("\n\n/**\n * class that represents structure of ");
		sbf.append(this.name);
		sbf.append("\n * <br /> generated at ").append(DateFormat.getDateTimeInstance().format(new Date()));
		sbf.append("\n */ ");
		sbf.append("\npublic class ").append(cls).append(" extends FormStructure {");

		this.emitJavaConstants(sbf);
		sbf.append("\n\n\t/**\n\t *\n\t */");
		sbf.append("\n\tpublic ").append(cls).append("() {");
		sbf.append("\n\t\tthis.uniqueName = \"").append(this.name).append("\";");

		this.si.emitJavaAttrs(sbf, customPackageName);

		if (this.fields != null) {
			this.emitJavaFields(sbf);
		}

		if (this.tables != null) {
			this.emitJavaTables(sbf);
		}

		if (this.validations != null) {
			this.emitJavaValidations(sbf, customPackageName);
		}

		sbf.append("\n\n\t\tthis.initialize();\n\t}\n}\n");
	}

	private void emitJavaConstants(StringBuilder sbf) {
		if (this.fields != null) {
			for (int i = 0; i < this.fields.length; i++) {
				this.fields[i].emitJavaConstant(sbf, i);
			}
		}
		if (this.tables != null) {
			for (int i = 0; i < this.tables.length; i++) {
				this.tables[i].emitJavaConstant(sbf, i);
			}
		}
	}

	private void emitJavaFields(StringBuilder sbf) {
		sbf.append("\n\t\tField[] flds = {");
		for (int i = 0; i < this.fields.length; i++) {
			if (i != 0) {
				sbf.append(',');
			}
			this.fields[i].emitJavaCode(sbf);
		}
		sbf.append("\n\t\t};\n\t\tthis.fields = flds;");
	}

	private void emitJavaTables(StringBuilder sbf) {
		sbf.append("\n\t\tTabularField[] tbls = {");
		for (int i = 0; i < this.tables.length; i++) {
			if (i != 0) {
				sbf.append(',');
			}
			this.tables[i].emitJavaCode(sbf);
		}
		sbf.append("\n\t\t};\n\t\tthis.tabularFields = tbls;");
	}

	private void emitJavaValidations(StringBuilder sbf, String customPackageName) {
		sbf.append("\n\t\tIFormValidation[] valns = {");
		for (int i = 0; i < this.validations.length; i++) {
			if (i != 0) {
				sbf.append(',');
			}
			this.validations[i].emitJavaCode(sbf, customPackageName, this.name);
		}
		sbf.append("\n\t\t};\n\t\tthis.tabularFields = tbls;");
	}
}
