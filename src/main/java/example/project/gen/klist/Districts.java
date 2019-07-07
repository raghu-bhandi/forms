package example.project.gen.klist;

import java.util.Arrays;
import java.util.Set;
import java.util.HashSet;
import java.util.HashMap;
import org.simplity.fm.validn.KeyedValueList;

/**
 * List of valid values for list districts
 * <br /> generated at 2019-07-07T21:54:19.859
 */ 
public class Districts extends KeyedValueList {
	private static final String[] _names = {"100001"};
	private static final Object[] _values = {new HashSet<>(Arrays.asList("1", "2", "3"))};
	private static final String _name = "districts";

/**
 *districts
 */
	public Districts() {
		this.name = _name;
		this.values = new HashMap<>();
		for (int i = 0; i < _names.length;i++) {
			this.values.put(_names[i], (Set<String>)_values[i]);
		}
	}
}
