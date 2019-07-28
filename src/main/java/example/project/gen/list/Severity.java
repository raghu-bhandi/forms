package example.project.gen.list;

import java.util.Arrays;
import java.util.Set;
import java.util.HashSet;
import org.simplity.fm.validn.ValueList;

/**
 * List of valid values for list severity
 * <br /> generated at 2019-07-28T20:24:01.161
 */ 
public class Severity extends ValueList {
	 private static final Set<String> _values = new HashSet<>(Arrays.asList("1", "2", "3", "3"));
	 private static final String _name = "severity";

/**
 *severity
 */
	public Severity() {
		this.name = _name;
		this.values = _values;
	}
}
