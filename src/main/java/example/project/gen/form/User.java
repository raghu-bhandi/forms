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
 * class that represents structure of user
 * <br /> generated at 2019-08-21T22:41:14.154 from file C:/Users/raghu/eclipse-workspace/ef/src/main/resources/fm/spec/form/user.xlsx
 */ 
public class User extends Form {
	public static final int userId = 0;
	public static final int firstName = 1;
	public static final int lastName = 2;
	private static final String SELECT = "SELECT user_id, first_name, last_name FROM all_users";
	private static final int[] SELECT_IDX = {0, 1, 2};
	private static final  String INSERT = "INSERT INTO all_users(user_id, first_name, last_name) values (?, ?, ?)";
	private static final int[] INSERT_IDX = {0, 1, 2};
	private static final String WHERE = " WHERE user_id=?";
	private static final int[] WHERE_IDX = {0};
	private static final  String UPDATE = "UPDATE all_users SET first_name=?, last_name=?";
	private static final  int[] UPDATE_IDX = {1, 2, 0};
	private static final String DELETE = "DELETE FROM all_users";

	private void setDbMeta(){
		DbMetaData m = new DbMetaData();
		m.dbOperationOk[0] = true;
		m.dbOperationOk[4] = true;
		m.dbOperationOk[2] = true;
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
		this.dbMetaData = m;
	}

	/**
	 *
	 */
	public User() {
		this.uniqueName = "user";

		Field[] flds = {
			new Field("userId", 0, true, DefinedDataTypes.textId, "user_id"), 
			new Field("firstName", 1, false, DefinedDataTypes.text, "first_name"), 
			new Field("lastName", 2, false, DefinedDataTypes.text, "last_name")
		};
		this.fields = flds;

		ChildForm[] chlds = {};
		this.childForms = chlds;

		this.setDbMeta();
		this.initialize();
	}
}
