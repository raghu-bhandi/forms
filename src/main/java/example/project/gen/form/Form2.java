package example.project.gen.form;

import org.simplity.fm.form.Field;
import org.simplity.fm.form.Form;
import org.simplity.fm.validn.IValidation;
import org.simplity.fm.form.ChildForm;
import org.simplity.fm.validn.DependentListValidation;
import example.project.gen.DefinedDataTypes;

/**
 * class that represents structure of form2
 * <br /> generated at 2019-07-07T21:54:20.465 from file C:\Users\raghu\eclipse-workspace\ef\src\main\resources\fm\spec\form\form2.xlsx
 */ 
public class Form2 extends Form {
	public static final int productId = 0;
	public static final int quantity = 1;
	public static final int color = 2;

	/**
	 *
	 */
	public Form2() {
		this.uniqueName = "form2";
			this.partialSaveAllowed = false;

		Field[] flds = {
			new Field("productId", 0, DefinedDataTypes.textId, null, null, true, true, false, true, null), 
			new Field("quantity", 1, DefinedDataTypes.orderQty, null, null, true, true, false, false, null), 
			new Field("color", 2, DefinedDataTypes.color, null, null, true, true, false, false, "colors")
		};
		this.fields = flds;

		IValidation[] vlds = {};
		this.validations = vlds;

		this.initialize();
	}
}
