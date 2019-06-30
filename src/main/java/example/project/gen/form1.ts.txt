import { Form } from '../form/form';
import { Field } from '../form/field';
import { ChildForm } from '../form/childForm';
import { Form2 } from './form2';

/**
 * generated from C:\Users\raghu\eclipse-workspace\ef\src\main\resources\spec\struct\form1.xlsx at 23 Jun, 2019 9:41:25 AM
 */
export class Form1 extends Form {

	constructor() {
		super();
		this.name = 'form1';

	this.fields = new Map();
		let vl: any = null;
		this.fields.set('customerId', new Field('customerId', "Customer Id", "customer id", null, true, true, false, true, 0, "[A-Z]{3}-[\\d]{2}-[A-Z]{3}", "invalidCustId", 10, 10, 0, 0, null));
		this.fields.set('finacialYear', new Field('finacialYear', "Financial Year", "financial year", null, true, true, false, true, 1, null, "invalidFy", 4, 4, 1989, 2025, null));
		this.fields.set('boolField', new Field('boolField', "Boolean Field", "boolean field", null, false, true, false, false, 2, null, "invalidTrueFalse", 0, 0, 0, 0, null));
		this.fields.set('fromDate', new Field('fromDate', "From Date", "from field", null, false, true, false, false, 3, null, "invalidFutureDate", 0, 0, 1, 73000, null));
		this.fields.set('toDate', new Field('toDate', "To Date", "to field", null, false, true, false, false, 3, null, "invalidFutureDate", 0, 0, 1, 73000, null));
		this.fields.set('intField1', new Field('intField1', "Int Field 1", "int field 1", "33", true, true, false, false, 1, null, "invalidQty", 1, 9, 1, 987654321012345678, null));
		this.fields.set('intField2', new Field('intField2', "Int Field 2", "int field 2", "45", false, true, true, false, 1, null, "invalidQty", 1, 9, 1, 987654321012345678, null));
		this.fields.set('derivedField', new Field('derivedField', "Total", null, null, false, false, true, false, 1, null, "invalidQty", 1, 9, 1, 987654321012345678, null));
		this.fields.set('currentFy', new Field('currentFy', "Current Financial Year", null, null, false, false, false, false, 1, null, "invalidFy", 4, 4, 1989, 2025, null));

		this.childForms = new Map();
		this.childForms.set('orderLines', new ChildForm('orderLines', 'Order Lines', new Form2(), 1, 200, null));
	}
}
