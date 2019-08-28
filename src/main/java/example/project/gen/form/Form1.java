package example.project.gen.form;

import org.simplity.fm.form.Field;
import org.simplity.fm.form.Form;
import org.simplity.fm.validn.IValidation;
import org.simplity.fm.form.ChildForm;
import org.simplity.fm.form.DbMetaData;
import org.simplity.fm.form.ChildDbMetaData;
import org.simplity.fm.form.ColumnType;
import org.simplity.fm.validn.FromToValidation;
import org.simplity.fm.validn.ExclusiveValidation;
import org.simplity.fm.validn.InclusiveValidation;
import org.simplity.fm.validn.DependentListValidation;
import example.project.gen.DefinedDataTypes;

/**
 * class that represents structure of form1
 * <br /> generated at 2019-08-29T00:54:11.685 from file C:/Users/raghu/eclipse-workspace/ef/src/main/resources/fm/spec/form/form1.xlsx
 */ 
public class Form1 extends Form {
	public static final int headerId = 0;
	public static final int customerId = 1;
	public static final int financialYear = 2;
	public static final int boolField = 3;
	public static final int fromDate = 4;
	public static final int toDate = 5;
	public static final int intField1 = 6;
	public static final int intField2 = 7;
	public static final int fyStartDate = 8;
	public static final int state = 9;
	public static final int district = 10;
	public static final int kaSpecificField = 11;
	public static final int aadhaar = 12;
	public static final int pan = 13;
	public static final int orderLines = 0;
	private static final String SELECT = "SELECT header_id, customer_id, financial_year, bool_field, from_date, to_date, int_field1, int_field2, state, district, ka_specific_field, aadhaar, pan FROM headerId";
	private static final int[] SELECT_IDX = {0, 1, 2, 3, 4, 5, 6, 7, 9, 10, 11, 12, 13};
	private static final  String INSERT = "INSERT INTO headerId(customer_id, financial_year, bool_field, from_date, to_date, int_field1, int_field2, state, district, ka_specific_field, aadhaar, pan) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	private static final int[] INSERT_IDX = {1, 2, 3, 4, 5, 6, 7, 9, 10, 11, 12, 13};
	private static final String WHERE = " WHERE header_id=?";
	private static final int[] WHERE_IDX = {0};
	private static final  String UPDATE = "UPDATE headerId SET financial_year= ? , bool_field= ? , from_date= ? , to_date= ? , int_field1= ? , int_field2= ? , state= ? , district= ? , ka_specific_field= ? , aadhaar= ? , pan= ?  WHERE header_id=?";
	private static final  int[] UPDATE_IDX = {2, 3, 4, 5, 6, 7, 9, 10, 11, 12, 13, 0, 0};
	private static final String DELETE = "DELETE FROM headerId";

	private static final String[] FORM2_LINK = {"headerId"};
	private static final int[] FORM2_IDX = {0};

	private void setDbMeta(){
		DbMetaData m = new DbMetaData();
		m.dbOperationOk[0] = true;
		m.dbOperationOk[4] = true;
		m.selectClause = SELECT;
		m.selectParams = this.getParams(SELECT_IDX);
		m.insertClause = INSERT;
		m.insertParams = this.getParams(INSERT_IDX);
		m.whereClause = WHERE;
		m.whereParams = this.getParams(WHERE_IDX);
		m.updateClause = UPDATE;
		m.updateParams = this.getParams(UPDATE_IDX);
		m.deleteClause = DELETE;
		m.keyIsGenerated = true;
		ChildDbMetaData[] cm = {this.newChildDbMeta(FORM2_LINK, FORM2_IDX)};
		m.childMeta = cm;
		this.dbMetaData = m;
	}

	/**
	 *
	 */
	public Form1() {
		this.uniqueName = "form1";
		this.userIdFieldName = "customerId";

		Field[] flds = {
			new Field("headerId", 0, DefinedDataTypes.id, null, null, false, false, null, "header_id", ColumnType.GeneratedPrimaryKey), 
			new Field("customerId", 1, DefinedDataTypes.customerId, "AAA-99-AAA", null, true, true, null, "customer_id", ColumnType.UniqueKey), 
			new Field("financialYear", 2, DefinedDataTypes.fy, "2010", null, true, true, null, "financial_year", ColumnType.RequiredData), 
			new Field("boolField", 3, DefinedDataTypes.trueFalse, "true", null, false, true, null, "bool_field", ColumnType.RequiredData), 
			new Field("fromDate", 4, DefinedDataTypes.futureDate, "44175", null, false, true, null, "from_date", ColumnType.RequiredData), 
			new Field("toDate", 5, DefinedDataTypes.futureDate, "44896", null, false, true, null, "to_date", ColumnType.RequiredData), 
			new Field("intField1", 6, DefinedDataTypes.qty, "33", null, false, true, null, "int_field1", ColumnType.OptionalData), 
			new Field("intField2", 7, DefinedDataTypes.qty, "45", null, false, true, null, "int_field2", ColumnType.OptionalData), 
			new Field("fyStartDate", 8, DefinedDataTypes.date, null, null, false, false, null, null, null), 
			new Field("state", 9, DefinedDataTypes.state, "KA", null, true, true, "states", "state", ColumnType.RequiredData), 
			new Field("district", 10, DefinedDataTypes.district, "123", null, true, true, null, "district", ColumnType.RequiredData), 
			new Field("kaSpecificField", 11, DefinedDataTypes.text, "kannada", null, false, true, null, "ka_specific_field", ColumnType.OptionalData), 
			new Field("aadhaar", 12, DefinedDataTypes.aadhaar, "111122223333", null, false, true, null, "aadhaar", ColumnType.OptionalData), 
			new Field("pan", 13, DefinedDataTypes.pan, "ACTPB3029K", null, false, true, null, "pan", ColumnType.OptionalData)
		};
		this.fields = flds;

		ChildForm[] chlds = {
			new ChildForm("orderLines", "form2", true, 1, 200, "wrongLines")};
		this.childForms = chlds;

		IValidation[] vlds = {new FromToValidation(4, 5, false, "fromDate", "invalidDateRange"),
			new ExclusiveValidation(12, 13, true, "aadhaar", "panOrAadhaar"),
			new InclusiveValidation(6, 7, null, "intField1", "intfield1INtField2AreTogether"),
			new InclusiveValidation(9, 11, "KA", "state", "requiredOnKa"),
			new DependentListValidation(10, 9, "districts", "district", null),
			new example.project.custom.Form1Validation()};
		this.validations = vlds;

		this.setDbMeta();
		this.initialize();
	}
}
