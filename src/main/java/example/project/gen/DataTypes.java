package example.project.gen;

import org.simplity.fm.datatypes.TextType;
import org.simplity.fm.datatypes.NumberType;
import org.simplity.fm.datatypes.BooleanType;
import org.simplity.fm.datatypes.DateType;

/**
 * static class that has static attributes for all data types defined for this project
 * <br /> generated at 23 Jun, 2019 9:47:14 AM
 */ 
public class DataTypes {
	public static final TextType customerId = new TextType("invalidCustId", 10, 10, "[A-Z]{3}-[\\d]{2}-[A-Z]{3}", null);
	public static final NumberType fy = new NumberType("invalidFy", 1989L, 2025L, null);
	public static final BooleanType trueFalse = new BooleanType("invalidTrueFalse");
	public static final DateType futureDate = new DateType("invalidFutureDate", 1L, 73000L);
	public static final NumberType qty = new NumberType("invalidQty", 1L, 987654321012345678L, null);
	public static final DateType pastDate = new DateType("invalidPastDate", -73000L, 0L);
	public static final TextType textId = new TextType("invalidTextId", 4, 15, "[\\w]*", null);
	public static final NumberType orderQty = new NumberType("InvalidOrderQty", 1L, 2000L, null);
	public static final NumberType color = new NumberType("invalidColor", 0L, 0L, ValueLists.color);
	public static final NumberType severity = new NumberType("invalidSeverity", 0L, 0L, ValueLists.severity);
}
