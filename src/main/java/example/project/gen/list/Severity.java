package example.project.gen.list;

import org.simplity.fm.validn.ValueList;

/**
 * List of valid values for list severity
 * <br /> generated at 2019-08-13T10:43:13.123
 */ 
public class Severity extends ValueList {
	 private static final String[][] VALUES = { 
			{"1", "Success"}, 
			{"2", "Info"}, 
			{"3", "Warning"}, 
			{"3", "Error"}
		};
	 private static final String NAME = "severity";

/**
 *
	 * @param name
	 * @param valueList
 */
	public Severity(String name, String[][] valueList) {
		super(name, valueList);
	}

/**
 *severity
 */
	public Severity() {
		super(NAME, VALUES);
	}
}
