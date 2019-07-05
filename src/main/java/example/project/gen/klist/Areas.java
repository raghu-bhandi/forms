package example.project.gen.klist;

import java.util.Arrays;
import java.util.Set;
import java.util.HashSet;
import java.util.HashMap;
import org.simplity.fm.validn.KeyedValueList;

/**
 * List of valid values for list Areas
 * <br /> generated at 3 Jul, 2019 9:01:49 PM
 */ 
public class Areas extends KeyedValueList {
	private static final String[] _names = {"100001", "560070"};
	private static final Object[] _values = {new HashSet<>(Arrays.asList("1", "2", "3")), new HashSet<>(Arrays.asList("127", "128"))};
	private static final String _name = "Areas";

/**
 *Areas
 */
	public Areas() {
		this.name = _name;
		this.values = new HashMap<>();
		for (int i = 0; i < _names.length;i++) {
			this.values.put(_names[i], (Set<String>)_values[i]);
		}
	}
}
