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
 * <br /> generated at 20 Jun, 2019 2:27:37 AM
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
		Field[] flds = {
			new Field("customerId", DataTypes.customerId, true, "", true, "", false, true),
			new Field("finacialYear", DataTypes.fy, true, "", true, "", false, true),
			new Field("boolField", DataTypes.trueFalse, false, "", true, "", false, false),
			new Field("fromDate", DataTypes.futureDate, false, "", true, "", false, false),
			new Field("toDate", DataTypes.futureDate, false, "", true, "", false, false),
			new Field("intField1", DataTypes.qty, true, "33", true, "", false, false),
			new Field("intField2", DataTypes.qty, false, "45", true, "", true, false),
			new Field("derivedField", DataTypes.qty, false, "", false, "", true, false),
			new Field("currentFy", DataTypes.fy, false, "", false, "", false, false)
		};
		this.fields = flds;
		TabularField[] tbls = {
			new TabularField("orderLines", new Form2(), 1, 200, "")
		};
		this.tabularFields = tbls;
		IFormValidation[] valns = {
			new FromToValidation("fromdate", "toDate", false, "invalidDateRange"),
			new example.project.custom.Form1Validation()
		};
		this.tabularFields = tbls;

		this.initialize();
	}
}
