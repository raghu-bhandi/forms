
package example.project.form;

import java.util.HashSet;
import java.util.Set;

import org.simplity.fm.datatypes.BooleanType;
import org.simplity.fm.datatypes.DateType;
import org.simplity.fm.datatypes.NumberType;
import org.simplity.fm.datatypes.TextType;

/**
 * generated class based on datTypes.json
 * Each data type is defined as a static member of this class
 *
 */
public class DataTypes {
	/**
	 * customerId
	 */
	public static final TextType customerId = new TextType("invalidCustId", 10, 10, "[A-Z]{3}-[0-9]{2}-[A-Z]{3}", null);
	/**
	 * fy
	 */
	public static final NumberType fy = new NumberType("invalidFy", 1989, 2025, null);
	/**
	 * trueFalse
	 */
	public static final BooleanType trueFalse = new BooleanType("invalidTrueFalse");
	/**
	 * futureDate
	 */
	public static final DateType futureDate = new DateType("invalidFutureDate", 1, 73000);
	/**
	 * pastDate
	 */
	public static final DateType pastDate = new DateType("invalidPastDate", -73000, 0);
	/**
	 * qty
	 */
	public static final NumberType qty = new NumberType("invalidQty", 1, 999999999, null);
	/**
	 * orderQty
	 */
	public static final NumberType orderQty = new NumberType("invalidOrderQty", 1, 2000, null);
	/**
	 * textId
	 */
	public static final TextType textId = new TextType("invalidCustId", 4, 15, "[\\w]*", null);
	private static final Set<Long> colorList = new HashSet<>(3);
	static {
		colorList.add(1L);
		colorList.add(2L);
		colorList.add(3L);
	}
	/**
	 * color
	 */
	public static final NumberType color = new NumberType("invalidColor", 0, 0, colorList);
	private static final Set<Long> severityList = new HashSet<>(4);
	static {
		severityList.add(1L);
		severityList.add(2L);
		severityList.add(3L);
		severityList.add(4L);
	}
	/**
	 * severity
	 */
	public static final NumberType severity = new NumberType("invalidSeverity", 0, 0, null);
}
