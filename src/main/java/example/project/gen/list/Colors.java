package example.project.gen.list;

import java.util.Arrays;
import java.util.Set;
import java.util.HashSet;
import org.simplity.fm.validn.ValueList;

/**
 * List of valid values for list colors
 * <br /> generated at 2019-07-14T09:23:32.324
 */ 
public class Colors extends ValueList {
	 private static final Set<String> _values = new HashSet<>(Arrays.asList("1", "2", "3"));
	 private static final String _name = "colors";

/**
 *colors
 */
	public Colors() {
		this.name = _name;
		this.values = _values;
	}
}
