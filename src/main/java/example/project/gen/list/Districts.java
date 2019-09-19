package example.project.gen.list;

import java.util.HashMap;
import org.simplity.fm.validn.KeyedValueList;
import org.simplity.fm.validn.ValueList;

/**
 * List of valid values for list districts
 * <br /> generated at 2019-08-30T23:09:23.402
 */ 
public class Districts extends KeyedValueList {
	private static final String[] NAMES = {"KA", "TN", "TS"
		};
	private static final String[][][] VALUES = {
			{
				{"0", "Bengaluru"}, 
				{"1", "Mysuru"}, 
				{"2", "Uttara Kannada"}
			}, 
			{
				{"44", "Periyar"}, 
				{"45", "Chennai Urban"}, 
				{"46", "Chennai Rural"}, 
				{"47", "Coimbatore"}
			}, 
			{
				{"57", "Hydrabad Urban"}, 
				{"58", "Secundrabad"}, 
				{"61", "Kachiguda"}
			}};
	private static final String NAME = "districts";

/**
 *districts
 */
	public Districts() {
		this.name = NAME;
		this.values = new HashMap<>();
		for (int i = 0; i < NAMES.length;i++) {
			this.values.put(NAMES[i], new ValueList(NAMES[i], VALUES[i]));
		}
	}
}
