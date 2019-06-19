
package example.project.form;

import org.simplity.fm.form.Field;
import org.simplity.fm.form.FormStructure;
import org.simplity.fm.form.FromToValidation;
import org.simplity.fm.form.IFormValidation;
import org.simplity.fm.form.TabularField;

/**
 * class that is generated based on the spread sheet
 * 
 */
public class Form1 extends FormStructure {
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
		Field[] flds = { new Field("customerId", DataTypes.customerId, true, null, true, null, false, true),
				new Field("financialYear", DataTypes.fy, true, null, true, null, false, true),
				new Field("boolField", DataTypes.trueFalse, true, null, true, null, false, false),
				new Field("fromDate", DataTypes.futureDate, false, null, true, null, false, false),
				new Field("toDate", DataTypes.futureDate, false, null, true, null, false, false),
				new Field("intField1", DataTypes.qty, true, "33", true, null, false, false),
				new Field("intField2", DataTypes.qty, false, "45", true, null, false, false),
				new Field("derivedField", DataTypes.qty, false, null, false, null, true, false),
				new Field("currentFy", DataTypes.fy, false, null, false, null, false, false) };
		this.fields = flds;
		this.userIdFieldName = "customerId";
		TabularField[] tables = { new TabularField("orderLines", new Form2(), 1, 200, "invalidOrderLines") };
		this.tabularFields = tables;

		IFormValidation[] valns = { new FromToValidation("fromDate", "toDate", true, "invalidDateRange"),
				new Form1Validation() };
		this.validations = valns;

		this.getOk = true;
		this.saveOk = true;
		this.submitOk = true;
		this.initialize();
	}
}
