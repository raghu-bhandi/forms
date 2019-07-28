package example.project.gen.form;

import org.simplity.fm.form.Field;
import org.simplity.fm.form.Form;
import org.simplity.fm.validn.IValidation;
import org.simplity.fm.form.ChildForm;
import org.simplity.fm.form.DbMetaData;
import org.simplity.fm.form.ChildDbMetaData;
import org.simplity.fm.validn.DependentListValidation;
import example.project.gen.DefinedDataTypes;

/**
 * class that represents structure of formStorage
 * <br /> generated at 2019-07-28T20:24:02.087 from file C:/Users/raghu/eclipse-workspace/ef/src/main/resources/fm/spec/form/formStorage.xlsx
 */ 
public class FormStorage extends Form {
	public static final int customerId = 0;
	public static final int formName = 1;
	public static final int referenceYear = 2;
	public static final int ackId = 3;
	public static final int status = 4;
	public static final int formData = 5;
	public static final int operation = 6;
	private static final String WHERE = " WHERE customer_id=? AND form_name=? AND reference_year=?";
	private static final int[] WHERE_IDX = {0, 1, 2};
	private static final String SELECT = "SELECT customer_id, form_name, reference_year, ack_id, status, form_data FROM form";
	private static final int[] SELECT_IDX = {0, 1, 2, 3, 4, 5};
	private static final  String INSERT = "INSERT INTO form(customer_id, form_name, reference_year, ack_id, status, form_data) values (?, ?, ?, ?, ?, ?)";
	private static final int[] INSERT_IDX = {0, 1, 2, 3, 4, 5};
	private static final  String UPDATE = "UPDATE form SET ack_id=?, status=?, form_data=?";
	private static final  int[] UPDATE_IDX = {3, 4, 5, 0, 1, 2};
	private static final String DELETE = "DELETE FROM form";

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
		this.dbMetaData = m;
	}

	/**
	 *
	 */
	public FormStorage() {
		this.uniqueName = "formStorage";
			this.userIdFieldName = "customerId";
			this.partialSaveAllowed = false;

		Field[] flds = {
			new Field("customerId", 0, DefinedDataTypes.customerId, null, null, true, true, false, true, null, "customer_id"), 
			new Field("formName", 1, DefinedDataTypes.text, null, null, true, true, false, true, null, "form_name"), 
			new Field("referenceYear", 2, DefinedDataTypes.fy, null, null, true, true, false, true, null, "reference_year"), 
			new Field("ackId", 3, DefinedDataTypes.text, null, null, false, false, false, false, null, "ack_id"), 
			new Field("status", 4, DefinedDataTypes.text, null, null, false, false, false, false, null, "status"), 
			new Field("formData", 5, DefinedDataTypes.mahabharat, null, null, false, false, false, false, null, "form_data"), 
			new Field("operation", 6, DefinedDataTypes.text, null, null, true, true, false, false, null, null)
		};
		this.fields = flds;

		IValidation[] vlds = {};
		this.validations = vlds;

		this.setDbMeta();
		this.initialize();
	}
}
