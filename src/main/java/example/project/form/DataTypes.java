
package example.project.form;

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
	public static final TextType customerId = new TextType(10, 10, "[A-Z]{3}-[0-9]{2}-[A-Z]{3}", "invalidCustId") ;
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
	private static final int[] colorList = {0,1,2};
	/**
	 * color
	 */
	public static final IntegerType color = new IntegerType(0,0,"invalidColor", colorList); 
	private static final int[] severityList = {0,1,2,3};
	/**
	 * severity
	 */
	public static final IntegerType severity = new IntegerType(0,0,"invalidSeverity", severityList); 
}
