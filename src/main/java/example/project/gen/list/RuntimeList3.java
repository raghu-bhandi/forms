package example.project.gen.list;

import org.simplity.fm.validn.RuntimeList;

/**
 * run-time utility to get list of valid values and validate a field runtimeList3
 * <br /> generated at 2019-08-17T13:14:20.536
 */ 
public class RuntimeList3 extends RuntimeList {
	 private static final String NAME = "runtimeList3";
	 private static final String LIST_SQL = "SELECT colm1, colm2 FROM table3 WHERE keyColm=?";
	 private static final String CHECK_SQL = "SELECT colm1 FROM table3 WHERE colm1=? and keyColm=?";
	 private static final boolean HAS_KEY = true;
	 private static final boolean KEY_IS_NUMERIC = false;
	/**
	 *
	 */
	public RuntimeList3() {
		this.listSql = LIST_SQL;
		this.checkSql = CHECK_SQL;
		this.name = NAME;
		this.hasKey = HAS_KEY;
		this.keyIsNumeric = KEY_IS_NUMERIC;
	}
}
