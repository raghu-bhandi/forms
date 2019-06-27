
package example.project.form;

import org.simplity.fm.form.Field;
import org.simplity.fm.form.Form;

/**
 * class that is generated based on the spread sheet
 * 
 */
public class Form2 extends Form {
	public static final int productId = 0;
	public static final int qty = 0;
	public static final int color = 0;

	/**
	 * 
	 */
	public Form2() {
		this.uniqueName = "form2";
		Field[] flds = { new Field("productId", DataTypes.textId, true, null, true, null, false, false),
				new Field("qty", DataTypes.orderQty, true, null, true, null, false, false),
				new Field("color", DataTypes.color, true, null, true, null, false, false) };
		this.fields = (flds);
		this.initialize();
	}
}
