package example.project.gen.form;

import org.simplity.fm.form.Field;
import org.simplity.fm.form.Form;
import org.simplity.fm.validn.IValidation;
import org.simplity.fm.form.ChildForm;
import org.simplity.fm.validn.DependentListValidation;
import example.project.gen.DefinedDataTypes;

/**
 * class that represents structure of form1
 * <br /> generated at 29 Jun, 2019 9:34:20 PM from file C:\Users\raghu\eclipse-workspace\ef\src\main\resources\fm\spec\form\form1.xlsx
 */ 
public class Form1 extends Form {
	public static final int customerId = 0;
	public static final int financialYear = 1;
	public static final int boolField = 2;
	public static final int fromDate = 3;
	public static final int toDate = 4;
	public static final int intField1 = 5;
	public static final int intField2 = 6;
	public static final int derivedField = 7;
	public static final int currentFy = 8;
	public static final int orderLines = 0;

	/**
	 *
	 */
	public Form1() {
		this.uniqueName = "form1";
			this.userIdFieldName = "customerId";
			this.getOk = true;
			this.saveOk = true;
			this.submitOk = true;
			this.partialOk = true;
			this.formProcessors[0] = new example.project.custom.MyPreGet();
			this.formProcessors[1] = new example.project.custom.MyPostGet();

		Field[] flds = {
			new Field("customerId", 0, DefinedDataTypes.customerId, null, null, true, true, false, true, null), 
			new Field("financialYear", 1, DefinedDataTypes.fy, null, null, true, true, false, true, null), 
			new Field("boolField", 2, DefinedDataTypes.trueFalse, null, null, false, true, false, false, null), 
			new Field("fromDate", 3, DefinedDataTypes.futureDate, null, null, false, true, false, false, null), 
			new Field("toDate", 4, DefinedDataTypes.futureDate, null, null, false, true, false, false, null), 
			new Field("intField1", 5, DefinedDataTypes.qty, "33", null, true, true, false, false, null), 
			new Field("intField2", 6, DefinedDataTypes.qty, "45", null, false, true, true, false, null), 
			new Field("derivedField", 7, DefinedDataTypes.qty, null, null, false, false, true, false, null), 
			new Field("currentFy", 8, DefinedDataTypes.fy, null, null, false, false, false, false, null)
		};
		this.fields = flds;

		ChildForm[] chlds = {
			new ChildForm("orderLines", "form2", true, 1, 200, "wrongLines")};
		this.childForms = chlds;

		IValidation[] vlds = {new example.project.custom.Form1Validation()};
		this.validations = vlds;

		this.initialize();
	}
}
