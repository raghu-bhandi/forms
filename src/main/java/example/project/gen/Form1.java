package example.project.gen;

import org.simplity.fm.form.Field;
import org.simplity.fm.form.FormStructure;
import org.simplity.fm.form.IFormValidation;
import org.simplity.fm.form.TabularField;
import org.simplity.fm.form.FromToValidation;
import org.simplity.fm.form.EitherOrValidation;
import org.simplity.fm.form.DependentFieldValidation;

/**
 * class that represents structure of form1
 * <br /> generated at 23 Jun, 2019 9:54:19 AM from file C:\Users\raghu\eclipse-workspace\ef\src\main\resources\spec\struct\form1.xlsx
 */ 
public class Form1 extends FormStructure {
	public static final int customerId = 0;
	public static final int finacialYear = 1;
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
			this.formProcessors[0] = new example.project.custom.Junk1();
			this.formProcessors[1] = new example.project.custom.Junk2();
			this.formProcessors[2] = new example.project.custom.Junk3();
			this.formProcessors[3] = new example.project.custom.Junk4();
			this.formProcessors[4] = new example.project.custom.Junk5();
			this.formProcessors[5] = new example.project.custom.Junk6();

		Field[] flds = {
			new Field("customerId", DataTypes.customerId, true, null, true, null, false, true),
			new Field("finacialYear", DataTypes.fy, true, null, true, null, false, true),
			new Field("boolField", DataTypes.trueFalse, false, null, true, null, false, false),
			new Field("fromDate", DataTypes.futureDate, false, null, true, null, false, false),
			new Field("toDate", DataTypes.futureDate, false, null, true, null, false, false),
			new Field("intField1", DataTypes.qty, true, "33", true, null, false, false),
			new Field("intField2", DataTypes.qty, false, "45", true, null, true, false),
			new Field("derivedField", DataTypes.qty, false, null, false, null, true, false),
			new Field("currentFy", DataTypes.fy, false, null, false, null, false, false)
		};
		this.fields = flds;

		TabularField[] tbls = {
			new TabularField("orderLines", new Form2(), 1, 200, "wrongLines")
		};
		this.tabularFields = tbls;

		IFormValidation[] vlds = {
			new FromToValidation("fromdate", "toDate", false, "invalidDateRange"),
			new example.project.custom.Form1Validation()
		};
		this.validations = vlds;

		this.initialize();
	}
}
