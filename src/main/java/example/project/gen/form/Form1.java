package example.project.gen.form;

import org.simplity.fm.form.Field;
import org.simplity.fm.form.Form;
import org.simplity.fm.validn.IValidation;
import org.simplity.fm.form.ChildForm;
import org.simplity.fm.validn.FromToValidation;
import org.simplity.fm.validn.DependentListValidation;
import example.project.gen.DefinedDataTypes;

/**
 * class that represents structure of form1
 * <br /> generated at 2019-07-06T14:00:59.158 from file C:\Users\raghu\eclipse-workspace\ef\src\main\resources\fm\spec\form\form1.xlsx
 */ 
public class Form1 extends Form {
	private static final String WHERE = " WHERE customer_id=? AND financial_year=?";
	private static final String FETCH = "SELECT customer_id, financial_year, bool_field, from_date, to_date, int_field1, int_field2 FROM test_table" + WHERE;
	private static final String INSERT = "INSERT INTO test_table(customer_id, financial_year, bool_field, from_date, to_date, int_field1, int_field2) values (?, ?, ?, ?, ?, ?, ?)" + WHERE;
	private static final String UPDATE = "UPDATE test_table SET bool_field=?, from_date=?, to_date=?, int_field1=?, int_field2=?" + WHERE;
	private static final String DELETE = "DELETE FROM test_table" + WHERE;
	public static final int customerIdd = 0;
	public static final int customerId = 1;
	public static final int financialYear = 2;
	public static final int boolField = 3;
	public static final int fromDate = 4;
	public static final int toDate = 5;
	public static final int intField1 = 6;
	public static final int intField2 = 7;
	public static final int derivedField = 8;
	public static final int currentFy = 9;
	public static final int orderLines = 0;

	/**
	 *
	 */
	public Form1() {
		this.uniqueName = "form1";
			this.createGetService = true;
			this.createSaveService = true;
			this.createSubmitService = true;
			this.partialSaveAllowed = true;
			this.formProcessors[0] = new example.project.custom.MyPreGet();
			this.formProcessors[1] = new example.project.custom.MyPostGet();

		Field[] flds = {
			new Field("customerIdd", 0, DefinedDataTypes.customerId, null, null, true, true, false, true, null), 
			new Field("customerId", 1, DefinedDataTypes.customerId, null, null, true, true, false, true, null), 
			new Field("financialYear", 2, DefinedDataTypes.fy, null, null, true, true, false, true, null), 
			new Field("boolField", 3, DefinedDataTypes.trueFalse, null, null, false, true, false, false, null), 
			new Field("fromDate", 4, DefinedDataTypes.futureDate, null, null, false, true, false, false, null), 
			new Field("toDate", 5, DefinedDataTypes.futureDate, null, null, false, true, false, false, null), 
			new Field("intField1", 6, DefinedDataTypes.qty, "33", null, true, true, false, false, null), 
			new Field("intField2", 7, DefinedDataTypes.qty, "45", null, false, true, true, false, null), 
			new Field("derivedField", 8, DefinedDataTypes.qty, null, null, false, false, true, false, null), 
			new Field("currentFy", 9, DefinedDataTypes.fy, null, null, false, false, false, false, null)
		};
		this.fields = flds;

		ChildForm[] chlds = {
			new ChildForm("orderLines", "form2", true, 1, 200, "wrongLines")};
		this.childForms = chlds;

		IValidation[] vlds = {new FromToValidation(4, 5, false, "fromDate", "invalidDateRange"),
			new example.project.custom.Form1Validation()};
		this.validations = vlds;

		this.initialize();
	}

	@Override
	protected String getFetchSql() {
		return FETCH;
	}

	@Override
	protected String getInsertSql() {
		return INSERT;
	}

	@Override
	protected String getUpdateSql() {
		return UPDATE;
	}

	@Override
	protected String getDeleteSql() {
		return DELETE;
	}
}
