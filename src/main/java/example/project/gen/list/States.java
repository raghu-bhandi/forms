package example.project.gen.list;

import java.util.Arrays;
import java.util.Set;
import java.util.HashSet;
import org.simplity.fm.validn.ValueList;

/**
 * List of valid values for list states
 * <br /> generated at 2019-08-09T13:36:30.079
 */ 
public class States extends ValueList {
	 private static final Set<String> _values = new HashSet<>(Arrays.asList("KA", "TN", "TS"));
	 private static final String _name = "states";

/**
 *states
 */
	public States() {
		this.name = _name;
		this.values = _values;
	}
}
