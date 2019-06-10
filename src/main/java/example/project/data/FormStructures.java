
package example.project.data;

import java.util.HashMap;
import java.util.Map;

import org.simplity.fm.data.Field;
import org.simplity.fm.data.FormStructure;
import org.simplity.fm.data.FromToValidation;
import org.simplity.fm.data.IFormValidation;

/**
 * class that is generated based on the all the form-structures defined under
 * folder struct/ This class may tend to be quite large in case the project has
 * hundreds of forms. A different mechanism should be devised to handle lrge
 * numbers
 * 
 */
public class FormStructures {
	private static final Map<String, FormStructure> allStructures = new HashMap<>();
	
	/**
	 * 
	 * @param structureName
	 * @return for structure, or null if there is such form structure
	 */
	public static FormStructure getStructure(String structureName) {
		return allStructures.get(structureName);
	}
		
	/**
	 * form 1 description
	 */
	public static final FormStructure form1 = form1();
	/**
	 * form 2 description
	 */
	public static final FormStructure form2 = form2();
	
	private static FormStructure form1() {
		Field[] fields = { 
				new Field("customerId",  DataTypes.customerId, true,  null, true, null, false, true),
				new Field("financialYear", DataTypes.fy,       true,  null, true, null, false, true),
				new Field("boolField", DataTypes.trueFalse,    false, null, true, null, false, false),
				new Field("fromDate", DataTypes.futureDate,    false, null, true, null, false, false),
				new Field("toDate", DataTypes.futureDate,      false, null, true, null, false, false),
				new Field("intFeidl1", DataTypes.qty,          true,  "33", true, null, false, false),
				new Field("intField2", DataTypes.qty,          false, "45", true, null, false, false),
				new Field("derivedField", DataTypes.qty,       false, null, false,null, true,  false),
				new Field("currentFy", DataTypes.fy,           false, null, false,null, false, false)
		};
		
		String[] gridNames = {"orderLines"};
		FormStructure[] grids = {form2};
		IFormValidation[] validations = {new FromToValidation("fromDate", "toDate", false, "invalidDateRange")};
		
		
		FormStructure fs =  new FormStructure("form1", fields, gridNames, grids, validations, 1, 200);
		allStructures.put("form1", fs);
		return fs;
	}
	
	private static FormStructure form2() {
		Field[] fields = { 
				new Field("productid",  DataTypes.textId, true,  null, true, null, false, false),
				new Field("qty", DataTypes.orderQty,       true,  null, true, null, false, false)
		};
		
		String[] gridNames = null;
		FormStructure[] grids = null;
		IFormValidation[] validations = null;
		
		
		FormStructure fs =  new FormStructure("form2", fields, gridNames, grids, validations, 0, 0);
		allStructures.put("form2", fs);
		return fs;
	}
}
