
package example.project.data;

import org.simplity.fm.data.types.BooleanType;
import org.simplity.fm.data.types.DateType;
import org.simplity.fm.data.types.IntegerType;
import org.simplity.fm.data.types.TextType;

/**
 * generated class based on datTypes.json
 * Each data type is defined as a static member of this class
 *
 */
public class DataTypes {
	/**
	 * customerId
	 */
	public static final TextType customerId = new TextType(10, 10, "[A-Z][A-Z][A-Z]-\\d\\d-[A-Z][A-Z][A-Z]", "invalidCustId");
	/**
	 * fy
	 */
	public static final IntegerType fy = new IntegerType(1989, 2025, "invalidFy"); 
	/**
	 * trueFalse
	 */
	public static final BooleanType trueFalse = new BooleanType("invalidTrueFalse");
	/**
	 * futureDate
	 */
	public static final DateType futureDate = new DateType(1,73000, "invalidFutureDate"); 
	/**
	 * pastDate
	 */
	public static final DateType pastDate = new DateType(-73000, 0, "invalidPastDate"); 
	/**
	 * qty
	 */
	public static final IntegerType qty = new IntegerType(1,999999999,"invalidQty"); 
	/**
	 * orderQty
	 */
	public static final IntegerType orderQty = new IntegerType(1,2000,"invalidOrderQty"); 
	/**
	 * textId
	 */
	public static final TextType textId = new TextType(4, 15, "[\\w]*", "invalidCustId");
}
