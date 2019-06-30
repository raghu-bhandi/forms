package example.project.gen;

import java.util.HashMap;
import java.util.Map;

import org.simplity.fm.IDataTypes;
import org.simplity.fm.datatypes.DataType;
import org.simplity.fm.datatypes.TextType;
import org.simplity.fm.datatypes.NumberType;
import org.simplity.fm.datatypes.BooleanType;
import org.simplity.fm.datatypes.DateType;

/**
 * class that has static attributes for all data types defined for this project. It also extends <code>DataTypes</code>
 * <br /> generated at 30 Jun, 2019 1:18:05 PM
 */ 
public class DefinedDataTypes implements IDataTypes {
	public static final TextType customerId = new TextType("customerId", "invalidCustId", 10, 10, "[A-Z]{3}-[\\d]{2}-[A-Z]{3}");
	public static final NumberType fy = new NumberType("fy", "invalidFy", 1989L, 2025L);
	public static final BooleanType trueFalse = new BooleanType("trueFalse", "invalidTrueFalse");
	public static final DateType futureDate = new DateType("futureDate", "invalidFutureDate", 1L, 73000L);
	public static final NumberType qty = new NumberType("qty", "invalidQty", 1L, 987654321012345678L);
	public static final DateType pastDate = new DateType("pastDate", "invalidPastDate", -73000L, 0L);
	public static final TextType textId = new TextType("textId", "invalidTextId", 4, 15, "[\\w]*");
	public static final NumberType orderQty = new NumberType("orderQty", "InvalidOrderQty", 1L, 2000L);
	public static final NumberType color = new NumberType("color", "invalidColor", 0L, 100L);
	public static final NumberType severity = new NumberType("severity", "invalidSeverity", 0L, 100L);

	public static final DataType[] allTypes = {customerId, fy, trueFalse, futureDate, qty, pastDate, textId, orderQty, color, severity};
	 private Map<String, DataType> typesMap;
	/**
	 * default constructor
	 */
	public DefinedDataTypes() {
		this.typesMap = new HashMap<>();
		for(DataType dt: allTypes) {
			this.typesMap.put(dt.getName(), dt);
		}
	}

@Override
	public DataType getDataType(String name) {
		return this.typesMap.get(name);
	}
}
