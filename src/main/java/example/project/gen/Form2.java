package example.project.gen;

import org.simplity.fm.form.Field;
import org.simplity.fm.form.FormStructure;
import org.simplity.fm.form.IFormValidation;
import org.simplity.fm.form.TabularField;
import org.simplity.fm.form.FromToValidation;
import org.simplity.fm.form.EitherOrValidation;
import org.simplity.fm.form.DependentFieldValidation;

/**
 * class that represents structure of form2
 * <br /> generated at 23 Jun, 2019 9:54:19 AM from file C:\Users\raghu\eclipse-workspace\ef\src\main\resources\spec\struct\form2.xlsx
 */ 
public class Form2 extends FormStructure {
	public static final int productId = 0;
	public static final int Qty = 1;
	public static final int color = 2;

	/**
	 *
	 */
	public Form2() {
		this.uniqueName = "form2";

		Field[] flds = {
			new Field("productId", DataTypes.textId, true, null, true, null, false, true),
			new Field("Qty", DataTypes.orderQty, true, null, true, null, false, false),
			new Field("color", DataTypes.color, true, null, true, null, false, false)
		};
		this.fields = flds;

		this.initialize();
	}
}
