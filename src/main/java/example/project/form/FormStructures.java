
package example.project.form;

import java.util.HashMap;
import java.util.Map;

import org.simplity.fm.form.FormStructure;

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
	 * form 2 description
	 */
	public static final FormStructure form2 = new Form1();
	/**
	 * form 1 description
	 */
	public static final FormStructure form1 = new Form1();

	static {
		allStructures.put("form2", form2);
		allStructures.put("form1", form1);
	}
}
