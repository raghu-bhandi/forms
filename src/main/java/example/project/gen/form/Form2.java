package example.project.gen.form;

import org.simplity.fm.form.Field;
import org.simplity.fm.form.Form;
import org.simplity.fm.validn.IValidation;
import org.simplity.fm.form.ChildForm;
import org.simplity.fm.validn.DependentListValidation;
import example.project.gen.DefinedDataTypes;

/**
 * class that represents structure of form2
 * <br /> generated at 2019-07-16T17:16:39.063 from file C:\Users\raghu\eclipse-workspace\ef\src\main\resources\fm\spec\form\form2.xlsx
 */ 
public class Form2 extends Form {
	public static final int headerId = 0;
	public static final int productId = 1;
	public static final int quantity = 2;
	public static final int color = 3;

	private void setDbMeta(){
		//
	}

	/**
	 *
	 */
	public Form2() {
		this.uniqueName = "form2";
			this.partialSaveAllowed = false;

		Field[] flds = {
			new Field("headerId", 0, DefinedDataTypes.id, null, null, true, false, false, true, null, "customer_id"), 
			new Field("productId", 1, DefinedDataTypes.textId, null, null, true, true, false, true, null, "product_id"), 
			new Field("quantity", 2, DefinedDataTypes.orderQty, null, null, true, true, false, false, null, "quantity"), 
			new Field("color", 3, DefinedDataTypes.color, null, null, true, true, false, false, "colors", "color")
		};
		this.fields = flds;

		IValidation[] vlds = {};
		this.validations = vlds;

		this.setDbMeta();
		this.initialize();
	}
}
