package example.project.gen.list;

import java.util.HashMap;
import org.simplity.fm.validn.KeyedValueList;
import org.simplity.fm.validn.ValueList;

/**
 * List of valid values for list Areas
 * <br /> generated at 2019-08-13T10:43:13.132
 */ 
public class Areas extends KeyedValueList {
	private static final String[] NAMES = {"100001", "560070"
		};
	private static final String[][][] VALUES = {
			{
				{"1", "New Delhi Central"}, 
				{"2", "AA Vihar"}, 
				{"3", "EEE Vihar"}
			}, 
			{
				{"127", "BSK Stage II"}, 
				{"128", "Padmanabha Nagar"}
			}};
	private static final String NAME = "Areas";

/**
 *Areas
 */
	public Areas() {
		this.name = NAME;
		this.values = new HashMap<>();
		for (int i = 0; i < NAMES.length;i++) {
			this.values.put(NAMES[i], new ValueList(NAMES[i], VALUES[i]));
		}
	}
}
