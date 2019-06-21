import { From } from '../form/form';
import { Field } from '../form/field';
import { Table } from '../form/table';
import { Form2 } from '../forms/form2';

/**
 * generated from C:\Users\raghu\eclipse-workspace\ef\src\main\resources\spec\struct\form1.xlsx at 21 Jun, 2019 10:52:50 PM
 */
export class Form1 extends Form {
	private static instance = new Form1();

	/**
	 * @returns singleton instance of Form1
	 */
	public static getInstance(): Form1 {
		 return Form1.instance;
	}

	private constructor() {
		super();
		this.name = 'form1';
		let vl: any = null;
		this.fields.set('customerId', new Field('customerId', "Customer Id", "customer id", "", true, true, false, true, 0, "[A-Z]{3}-[\\d]{2}-[A-Z]{3}", "invalidCustId", 10, 10, 0, 0, null));
		this.fields.set('finacialYear', new Field('finacialYear', "Financial Year", "financial year", "", true, true, false, true, 1, "", "invalidFy", 4, 4, 1989, 2025, null));
		this.fields.set('boolField', new Field('boolField', "Boolean Field", "boolean field", "", false, true, false, false, 2, "", "invalidTrueFalse", 0, 0, 0, 0, null));
		this.fields.set('fromDate', new Field('fromDate', "From Date", "from field", "", false, true, false, false, 3, "", "invalidFutureDate", 0, 0, 1, 73000, null));
		this.fields.set('toDate', new Field('toDate', "To Date", "to field", "", false, true, false, false, 3, "", "invalidFutureDate", 0, 0, 1, 73000, null));
		this.fields.set('intField1', new Field('intField1', "Int Field 1", "int field 1", "33", true, true, false, false, 1, "", "invalidQty", 1, 9, 1, 999999999, null));
		this.fields.set('intField2', new Field('intField2', "Int Field 2", "int field 2", "45", false, true, true, false, 1, "", "invalidQty", 1, 9, 1, 999999999, null));
		this.fields.set('derivedField', new Field('derivedField', "Total", "", "", false, false, true, false, 1, "", "invalidQty", 1, 9, 1, 999999999, null));
		this.fields.set('currentFy', new Field('currentFy', "Current Financial Year", "", "", false, false, false, false, 1, "", "invalidFy", 4, 4, 1989, 2025, null));

		this.tables = new Map();
		this.tables.set('orderLines', new Table('orderLines', 'Order Lines', Form2.getInstane(), 1, 200, ""));
	}
}
