
package example.project.data;

import org.simplity.fm.data.Field;
import org.simplity.fm.data.FormStructure;
import org.simplity.fm.data.IFormValidation;

/**
 * class that is generated based on the all the form-structures defined under
 * folder struct/ This class may tend to be quite large in case the project has
 * hundreds of forms. A different mechanism should be devised to handle lrge
 * numbers
 * 
 */
public class FormStructures {
	/**
	 * form 1 description
	 */
	public static final FormStructure form1 = form1();
	/**
	 * form 2 description
	 */
	public static final FormStructure form2 = form2();
	
	private static FormStructure form1() {
		Field[] fields = { new Field("f1", DataTypes.textType1, true, null, null, false, 0, true),
				new Field("f2", DataTypes.textType1, true, null, null, false, 1, true),
				new Field("f3", DataTypes.intType1, false, "12", null, false, 2, false),
				new Field("f4", DataTypes.dateType1, true, null, null, false, 3, false),
		};
		
		String[] gridNames = null;
		FormStructure[] grids = null;
		IFormValidation[] validations = null;
		
		
		return new FormStructure("form1", fields, gridNames, grids, validations, 0, 0);
	}
	
	private static FormStructure form2() {
		return null;
	}
}
