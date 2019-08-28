package example.project.gen.form;

import org.simplity.fm.form.Field;
import org.simplity.fm.form.Form;
import org.simplity.fm.validn.IValidation;
import org.simplity.fm.form.ChildForm;
import org.simplity.fm.form.DbMetaData;
import org.simplity.fm.form.ChildDbMetaData;
import org.simplity.fm.form.ColumnType;
import org.simplity.fm.validn.DependentListValidation;
import example.project.gen.DefinedDataTypes;

/**
 * class that represents structure of user
 * <br /> generated at 2019-08-29T00:54:12.467 from file C:/Users/raghu/eclipse-workspace/ef/src/main/resources/fm/spec/form/user.xlsx
 */ 
public class User extends Form {
	public static final int userId = 0;
	public static final int firstName = 1;
	public static final int lastName = 2;
	private static final String SELECT = "SELECT user_id, first_name, last_name FROM all_users";
	private static final int[] SELECT_IDX = {0, 1, 2};
	private static final  String INSERT = "INSERT INTO all_users(user_id, first_name, last_name) values (?, ?, ?)";
	private static final int[] INSERT_IDX = {0, 1, 2};

	private void setDbMeta(){
		DbMetaData m = new DbMetaData();
		m.dbOperationOk[0] = true;
		m.dbOperationOk[4] = true;
		m.dbOperationOk[2] = true;
		m.selectClause = SELECT;
		m.selectParams = this.getParams(SELECT_IDX);
		m.insertClause = INSERT;
		m.insertParams = this.getParams(INSERT_IDX);
		this.dbMetaData = m;
	}

	/**
	 *
	 */
	public User() {
		this.uniqueName = "user";

		Field[] flds = {
			new Field("userId", 0, DefinedDataTypes.textId, "user_id", ColumnType.PrimaryKey), 
			new Field("firstName", 1, DefinedDataTypes.text, "first_name", ColumnType.RequiredData), 
			new Field("lastName", 2, DefinedDataTypes.text, "last_name", ColumnType.RequiredData)
		};
		this.fields = flds;

		ChildForm[] chlds = {};
		this.childForms = chlds;

		this.setDbMeta();
		this.initialize();
	}
}
