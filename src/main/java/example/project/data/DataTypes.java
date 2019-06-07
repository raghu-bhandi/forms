
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
	 * label : description
	 */
	public static final TextType textType1 = new TextType(5, 100, "I Don't know it off hand. Please get the Best one", "invalidEmail");
	/**
	 * label : description
	 */
	public static final IntegerType intType1 = new IntegerType(100, 999999999, "invalidInteger"); 
	/**
	 * label : description
	 */
	public static final DateType dateType1 = new DateType(-355000, 355000, "invalidDate");
	/**
	 * label : description
	 */
	public static final BooleanType boolType1 = new BooleanType("invalidInteger"); 
}
