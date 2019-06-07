
package in.gov.itd.ef.data.generated;

import org.simplity.fm.data.types.DataType;
import org.simplity.fm.data.types.DateType;

/**
 * all the <code>DataType</code> instances defined in this project
 *
 */
public class DataTypes {
	/**
	 * date in the past, including today
	 */
	public static final DataType pastDate = new DateType(-73000,0,"invalidPastDate"); 
	/**
	 * date strictly in the future, not even today
	 */
	public static final DataType futureDate = new DateType(1, 73000,"invalidFutureDate");
}
