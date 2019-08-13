package example.project.gen.list;

import org.simplity.fm.validn.ValueList;

/**
 * List of valid values for list colors
 * <br />
 * generated at 2019-07-14T09:23:32.324
 */
public class Colors extends ValueList {
	private static final String[][] VALUES = { { "1", "red" }, { "2", "green" }, { "3", "yellow" } };
	private static final String NAME = "colors";

	/**
	 * 
	 * @param name
	 * @param valueList
	 */
	public Colors(String name, String[][] valueList) {
		super(name, valueList);
	}
	
	/**
	 * 
	 */
	public Colors() {
		super(NAME, VALUES);
	}
}
