package example.project.gen.form;

import org.simplity.fm.form.Field;
import org.simplity.fm.form.Form;
import org.simplity.fm.validn.IValidation;
import org.simplity.fm.form.ChildForm;
import org.simplity.fm.validn.DependentListValidation;
import example.project.gen.DefinedDataTypes;

/**
 * class that represents structure of form2
 * <br /> generated at 3 Jul, 2019 7:40:02 PM from file C:\Users\raghu\eclipse-workspace\ef\src\main\resources\fm\spec\form\form2.xlsx
 */ 
public class Form2 extends Form {
	public static final int customerIdd = 0;
	public static final int productId = 1;
	public static final int quantity = 2;
	public static final int color = 3;

	/**
	 *
	 */
	public Form2() {
		this.uniqueName = "form2";

		Field[] flds = {
			new Field("customerIdd", 0, DefinedDataTypes.customerId, null, null, true, true, false, true, null), 
			new Field("productId", 1, DefinedDataTypes.textId, null, null, true, true, false, true, null), 
			new Field("quantity", 2, DefinedDataTypes.orderQty, null, null, true, true, false, false, null), 
			new Field("color", 3, DefinedDataTypes.color, null, null, true, true, false, false, "colors")
		};
		this.fields = flds;

		IValidation[] vlds = {};
		this.validations = vlds;

		this.initialize();
	}
}
