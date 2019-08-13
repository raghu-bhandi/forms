package example.project.gen.list;

import org.simplity.fm.validn.ValueList;

/**
 * List of valid values for list states
 * <br /> generated at 2019-08-13T10:43:13.128
 */ 
public class States extends ValueList {
	 private static final String[][] VALUES = { 
			{"KA", "Karnataka"}, 
			{"TN", "Tamil Nadu"}, 
			{"TS", "Telengana"}
		};
	 private static final String NAME = "states";

/**
 *
	 * @param name
	 * @param valueList
 */
	public States(String name, String[][] valueList) {
		super(name, valueList);
	}

/**
 *states
 */
	public States() {
		super(NAME, VALUES);
	}
}
