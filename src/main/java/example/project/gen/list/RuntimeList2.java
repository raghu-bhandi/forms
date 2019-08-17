package example.project.gen.list;

import org.simplity.fm.validn.RuntimeList;

/**
 * run-time utility to get list of valid values and validate a field runtimeList2
 * <br /> generated at 2019-08-17T13:14:20.534
 */ 
public class RuntimeList2 extends RuntimeList {
	 private static final String NAME = "runtimeList2";
	 private static final String LIST_SQL = "SELECT column1, column2 FROM table2 WHERE keyCol1=?";
	 private static final String CHECK_SQL = "SELECT column1 FROM table2 WHERE column1=? and keyCol1=?";
	 private static final boolean HAS_KEY = true;
	 private static final boolean KEY_IS_NUMERIC = true;
	/**
	 *
	 */
	public RuntimeList2() {
		this.listSql = LIST_SQL;
		this.checkSql = CHECK_SQL;
		this.name = NAME;
		this.hasKey = HAS_KEY;
		this.keyIsNumeric = KEY_IS_NUMERIC;
	}
}
