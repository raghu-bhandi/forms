package example.project.gen;

import org.simplity.fm.data.types.TextType;
import org.simplity.fm.data.types.NumberType;
import org.simplity.fm.data.types.DateType;
import org.simplity.fm.data.types.BooleanType;

/**
 * static class that has static attributes for all data types defined for this project
/* <br /> generated at 19 Jun, 2019 4:30:40 PM
 */ 
public class DataTypes {
	public static final TextType customerId = new TextType("invalidCustId",10,10,"[A-Z]{3}-[\\d]{2}-[A-Z]{3}",null);
	public static final NumberType fy = new NumberType("invalidFy",1989,2025,null);
	public static final BooleanType trueFalse = new BooleanType("invalidTrueFalse");
	public static final DateType futureDate = new DateType("invalidFutureDate",1,73000);
	public static final NumberType qty = new NumberType("invalidQty",1,999999999,null);
	public static final DateType pastDate = new DateType("invalidPastDate",-73000,0);
	public static final TextType textId = new TextType("invalidTextId",4,15,"[\\w]*",null);
	public static final NumberType orderQty = new NumberType("InvalidOrderQty",1,2000,null);
	public static final NumberType color = new NumberType("invalidColor",0,0,ValueLists.color);
	public static final NumberType severity = new NumberType("invalidSeverity",0,0,ValueLists.severity);
}
