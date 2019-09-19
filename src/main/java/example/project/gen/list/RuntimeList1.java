package example.project.gen.list;

import org.simplity.fm.validn.RuntimeList;

/**
 * run-time utility to get list of valid values and validate a field runtimeList1
 * <br /> generated at 2019-08-30T23:09:23.412
 */ 
public class RuntimeList1 extends RuntimeList {
	 private static final String NAME = "runtimeList1";
	 private static final String LIST_SQL = "SELECT col1, col1 FROM table1";
	 private static final String CHECK_SQL = "SELECT col1 FROM table1 WHERE col1=?";
	 private static final boolean HAS_KEY = false;
	/**
	 *
	 */
	public RuntimeList1() {
		this.listSql = LIST_SQL;
		this.checkSql = CHECK_SQL;
		this.name = NAME;
		this.hasKey = HAS_KEY;
	}
}
