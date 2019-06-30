package example.project.gen.list;

import java.util.Arrays;
import java.util.Set;
import java.util.HashSet;
import org.simplity.fm.validn.ValueList;

/**
 * List of valid values for list color
 * <br /> generated at 30 Jun, 2019 12:33:18 PM
 */ 
public class Color extends ValueList {
	 private static final Set<String> _values = new HashSet<>(Arrays.asList("0", "1", "2"));
	 private static final String _name = "color";

/**
 *color
 */
	public Color() {
		this.name = _name;
		this.values = _values;
	}
}
