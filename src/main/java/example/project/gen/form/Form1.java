package example.project.gen.form;

import org.simplity.fm.form.Field;
import org.simplity.fm.form.Form;
import org.simplity.fm.validn.IValidation;
import org.simplity.fm.form.ChildForm;
import org.simplity.fm.validn.FromToValidation;
import org.simplity.fm.validn.ExclusiveValidation;
import org.simplity.fm.validn.InclusiveValidation;
import org.simplity.fm.validn.DependentListValidation;
import example.project.gen.DefinedDataTypes;

/**
 * class that represents structure of form1
 * <br /> generated at 2019-07-14T14:02:43.330 from file C:\Users\raghu\eclipse-workspace\ef\src\main\resources\fm\spec\form\form1.xlsx
 */ 
public class Form1 extends Form {
	public static final int customerId = 0;
	public static final int financialYear = 1;
	public static final int boolField = 2;
	public static final int fromDate = 3;
	public static final int toDate = 4;
	public static final int intField1 = 5;
	public static final int intField2 = 6;
	public static final int derivedField = 7;
	public static final int fyStartDate = 8;
	public static final int state = 9;
	public static final int district = 10;
	public static final int kaSpecificField = 11;
	public static final int aadhaar = 12;
	public static final int pan = 13;
	public static final int orderLines = 0;
	private static final String WHERE = " WHERE cust_id=? AND financial_year=?";
	private static final int[] WHERE_IDX = {0, 1};
	private static final String SELECT = "SELECT cust_id, financial_year, bool_field, from_date, to_date, int_field1, int_field2, state, district FROM test_table";
	private static final int[] SELECT_IDX = {0, 1, 2, 3, 4, 5, 6, 9, 10};
	private static final  String INSERT = "INSERT INTO test_table(cust_id, financial_year, bool_field, from_date, to_date, int_field1, int_field2, state, district) values (?, ?, ?, ?, ?, ?, ?, ?, ?)";
	private static final int[] INSERT_IDX = {0, 1, 2, 3, 4, 5, 6, 9, 10};
	private static final  String UPDATE = "UPDATE test_table SET bool_field=?, from_date=?, to_date=?, int_field1=?, int_field2=?, state=?, district=?";
	private static final  int[] UPDATE_IDX = {2, 3, 4, 5, 6, 9, 10, 0, 1};
	private static final String DELETE = "DELETE FROM test_table";

	private static final String[] FORM2_LINK = {"customerId"};
	private static final int[] FORM2_IDX = {0};

	private void setDbMeta(){
		DbMetaData m = new DbMetaData();
		m.whereClause = WHERE;
		m.whereParams = this.getParams(WHERE_IDX);
		m.selectClause = SELECT;
		m.selectParams = this.getParams(SELECT_IDX);
		m.insertClause = INSERT;
		m.insertParams = this.getParams(INSERT_IDX);
		m.updateClause = UPDATE;
		m.updateParams = this.getParams(UPDATE_IDX);
		m.deleteClause = DELETE;
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
			this.createGetService = true;
			this.createSaveService = true;
			this.createSubmitService = true;
			this.partialSaveAllowed = true;
			this.formProcessors[0] = new example.project.custom.MyPreGet();
			this.formProcessors[1] = new example.project.custom.MyPostGet();

		Field[] flds = {
			new Field("customerId", 0, DefinedDataTypes.customerId, null, null, true, false, false, true, null, "cust_id"), 
			new Field("financialYear", 1, DefinedDataTypes.fy, null, null, true, true, false, true, null, "financial_year"), 
			new Field("boolField", 2, DefinedDataTypes.trueFalse, null, null, false, true, false, false, null, "bool_field"), 
			new Field("fromDate", 3, DefinedDataTypes.futureDate, null, null, false, true, false, false, null, "from_date"), 
			new Field("toDate", 4, DefinedDataTypes.futureDate, null, null, false, true, false, false, null, "to_date"), 
			new Field("intField1", 5, DefinedDataTypes.qty, "33", null, false, true, false, false, null, "int_field1"), 
			new Field("intField2", 6, DefinedDataTypes.qty, "45", null, false, true, false, false, null, "int_field2"), 
			new Field("derivedField", 7, DefinedDataTypes.qty, null, null, false, false, true, false, null, null), 
			new Field("fyStartDate", 8, DefinedDataTypes.date, null, null, false, false, false, false, null, null), 
			new Field("state", 9, DefinedDataTypes.state, null, null, true, true, false, false, "states", "state"), 
			new Field("district", 10, DefinedDataTypes.district, null, null, true, true, false, false, null, "district"), 
			new Field("kaSpecificField", 11, DefinedDataTypes.text, null, null, false, true, false, false, null, null), 
			new Field("aadhaar", 12, DefinedDataTypes.aadhaar, null, null, false, true, false, false, null, null), 
			new Field("pan", 13, DefinedDataTypes.pan, null, null, false, true, false, false, null, null)
		};
		this.fields = flds;

		ChildForm[] chlds = {
			new ChildForm("orderLines", "form2", true, 1, 200, "wrongLines")};
		this.childForms = chlds;

		IValidation[] vlds = {new FromToValidation(3, 4, false, "fromDate", "invalidDateRange"),
			new ExclusiveValidation(12, 13, true, "aadhaar", "panOrAadhaar"),
			new InclusiveValidation(5, 6, null, "intField1", "intfield1INtField2AreTogether"),
			new InclusiveValidation(9, 11, "KA", "state", "requiredOnKa"),
			new DependentListValidation(10, 9, "districts", "district", null),
			new example.project.custom.Form1Validation()};
		this.validations = vlds;

		this.setDbMeta();
		this.initialize();
	}
}
