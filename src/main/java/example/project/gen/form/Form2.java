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
 * class that represents structure of form2
 * <br /> generated at 2019-08-29T00:54:12.059 from file C:/Users/raghu/eclipse-workspace/ef/src/main/resources/fm/spec/form/form2.xlsx
 */ 
public class Form2 extends Form {
	public static final int headerId = 0;
	public static final int productId = 1;
	public static final int quantity = 2;
	public static final int color = 3;
	private static final String SELECT = "SELECT header_id, product_id, quantity, color FROM detail";
	private static final int[] SELECT_IDX = {0, 1, 2, 3};
	private static final  String INSERT = "INSERT INTO detail(header_id, product_id, quantity, color) values (?, ?, ?, ?)";
	private static final int[] INSERT_IDX = {0, 1, 2, 3};
	private static final String WHERE = " WHERE header_id=? AND product_id=?";
	private static final int[] WHERE_IDX = {0, 1};
	private static final  String UPDATE = "UPDATE detail SET quantity= ? , color= ?  WHERE header_id=? AND product_id=?";
	private static final  int[] UPDATE_IDX = {2, 3, 0, 1, 0, 1};
	private static final String DELETE = "DELETE FROM detail";

	private void setDbMeta(){
		DbMetaData m = new DbMetaData();
		m.selectClause = SELECT;
		m.selectParams = this.getParams(SELECT_IDX);
		m.insertClause = INSERT;
		m.insertParams = this.getParams(INSERT_IDX);
		m.whereClause = WHERE;
		m.whereParams = this.getParams(WHERE_IDX);
		m.updateClause = UPDATE;
		m.updateParams = this.getParams(UPDATE_IDX);
		m.deleteClause = DELETE;
		this.dbMetaData = m;
	}

	/**
	 *
	 */
	public Form2() {
		this.uniqueName = "form2";

		Field[] flds = {
			new Field("headerId", 0, DefinedDataTypes.id, null, null, false, false, null, "header_id", ColumnType.PrimaryAndParentKey), 
			new Field("productId", 1, DefinedDataTypes.textId, null, null, true, true, null, "product_id", ColumnType.PrimaryKey), 
			new Field("quantity", 2, DefinedDataTypes.orderQty, null, null, true, true, null, "quantity", ColumnType.RequiredData), 
			new Field("color", 3, DefinedDataTypes.color, null, null, true, true, "colors", "color", ColumnType.RequiredData)
		};
		this.fields = flds;

		IValidation[] vlds = {};
		this.validations = vlds;

		this.setDbMeta();
		this.initialize();
	}
}
