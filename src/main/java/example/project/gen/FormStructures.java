package example.project.gen;

import java.util.HashMap;
import java.util.Map;
import org.simplity.fm.form.FormStructure;

/**
 * static class that has a static attribute for each form defined in this project
 */
 public class FormStructures {
	private static final Map<String, FormStructure> allStructures = new HashMap<>();

	/**
	 *
	 * @param structureName
	 * @return form structure, or null if no such form defined in the project
	 */
	public static FormStructure getStructure(String structureName) {
		return allStructures.get(structureName);
	}
	/**
	 * form1
	 */
	public static final FormStructure form1 = new Form1();
	/**
	 * form2
	 */
	public static final FormStructure form2 = new Form2();

	static{
		allStructures.put("form1", form1);
		allStructures.put("form2", form2);
	}
}
