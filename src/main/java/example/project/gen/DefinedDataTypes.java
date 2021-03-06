package example.project.gen;

import java.util.HashMap;
import java.util.Map;

import org.simplity.fm.IDataTypes;
import org.simplity.fm.datatypes.DataType;
import org.simplity.fm.datatypes.TextType;
import org.simplity.fm.datatypes.IntegerType;
import org.simplity.fm.datatypes.DecimalType;
import org.simplity.fm.datatypes.BooleanType;
import org.simplity.fm.datatypes.DateType;
import org.simplity.fm.datatypes.TimestampType;

/**
 * class that has static attributes for all data types defined for this project. It also extends <code>DataTypes</code>
 * <br /> generated at 2019-08-30T23:09:23.385
 */ 
public class DefinedDataTypes implements IDataTypes {
	public static final TextType customerId = new TextType("customerId", "invalidCustId", 10, 10, "[A-Z]{3}-[\\d]{2}-[A-Z]{3}");
	public static final IntegerType fy = new IntegerType("fy", "invalidFy", 1989L, 2025L);
	public static final BooleanType trueFalse = new BooleanType("trueFalse", "invalidTrueFalse");
	public static final DateType futureDate = new DateType("futureDate", "invalidFutureDate", 1L, 73000L);
	public static final IntegerType qty = new IntegerType("qty", "invalidQty", 1L, 9874L);
	public static final DateType pastDate = new DateType("pastDate", "invalidPastDate", -73000L, 0L);
	public static final TextType textId = new TextType("textId", "invalidTextId", 4, 15, "[\\w]*");
	public static final IntegerType orderQty = new IntegerType("orderQty", "InvalidOrderQty", 1L, 2000L);
	public static final IntegerType color = new IntegerType("color", "invalidColor", 0L, 100L);
	public static final IntegerType severity = new IntegerType("severity", "invalidSeverity", 0L, 100L);
	public static final DateType date = new DateType("date", "invalidDate", -73000L, 73000L);
	public static final TextType state = new TextType("state", "invalidState", 2, 2, "[A-Z][A-Z]");
	public static final TextType text = new TextType("text", "invalidText", 2, 100, null);
	public static final TextType pan = new TextType("pan", "invalidPan", 10, 10, "[A-Z,a-z]{5}[0-9]{4}[A-Z,a-z]");
	public static final IntegerType aadhaar = new IntegerType("aadhaar", "invalidAadhaar", 100000000000L, 999999999999L);
	public static final IntegerType district = new IntegerType("district", "invalidDistrict", 1L, 9999L);
	public static final IntegerType id = new IntegerType("id", "invalidId", 1L, 9999999999999L);
	public static final TextType mahabharat = new TextType("mahabharat", "invalidMahabharat", 1, 10000, null);

	public static final DataType[] allTypes = {customerId, fy, trueFalse, futureDate, qty, pastDate, textId, orderQty, color, severity, date, state, text, pan, aadhaar, district, id, mahabharat};
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
