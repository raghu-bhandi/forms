/*
 * generated from C:\Users\raghu\eclipse-workspace\ef\src\main\resources\fm\spec\form\form1.xlsx at 2019-07-09T22:32:56.913
 */
import { Form , Field } from '../form/form';
import { ChildForm } from '../form/form';
import { Form2 } from './form2';

export class Form1 extends Form {
	private static _instance = new Form1();
	customerIdd = new Field('customerIdd', 0, 'Customer Id', 'customer id', null, true, true, false, true,
				0, '[A-Z]{3}-[\\d]{2}-[A-Z]{3}', 'invalidCustId', 10, 10, 0, 0, null, null, null, null, null);
	customerId = new Field('customerId', 1, 'Customer Id', 'customer id', null, true, true, false, true,
				0, '[A-Z]{3}-[\\d]{2}-[A-Z]{3}', 'invalidCustId', 10, 10, 0, 0, null, null, null, null, null);
	financialYear = new Field('financialYear', 2, 'Financial Year', 'financial year', null, true, true, false, true,
				1, null, 'invalidFy', 4, 4, 1989, 2025, null, null, null, null, null);
	boolField = new Field('boolField', 3, 'Boolean Field', 'boolean field', null, false, true, false, false,
				3, null, 'invalidTrueFalse', 0, 0, 0, 0, 'True', 'False', null, null, null);
	fromDate = new Field('fromDate', 4, 'From Date', 'from field', null, false, true, false, false,
				4, null, 'invalidFutureDate', 0, 0, 1, 73000, null, null, null, null, null);
	toDate = new Field('toDate', 5, 'To Date', 'to field', null, false, true, false, false,
				4, null, 'invalidFutureDate', 0, 0, 1, 73000, null, null, null, null, null);
	intField1 = new Field('intField1', 6, 'Int Field 1', 'int field 1', '33', true, true, false, false,
				1, null, 'invalidQty', 1, 9, 1, 9874, null, null, null, null, null);
	intField2 = new Field('intField2', 7, 'Int Field 2', 'int field 2', '45', false, true, true, false,
				1, null, 'invalidQty', 1, 9, 1, 9874, null, null, null, null, null);
	derivedField = new Field('derivedField', 8, 'Total', null, null, false, false, true, false,
				1, null, 'invalidQty', 1, 9, 1, 9874, null, null, null, null, null);
	currentFy = new Field('currentFy', 9, 'Current Financial Year', null, null, false, false, false, false,
				1, null, 'invalidFy', 4, 4, 1989, 2025, null, null, null, null, null);

	orderLines = new ChildForm('orderLines', 0, 'Order Lines', Form2.getInstance(), 1, 200, 'wrongLines');

	public static getInstance(): Form1 {
		return Form1._instance;
	}

	constructor() {
		super();
		this.fields = [
			this.customerIdd,
			this.customerId,
			this.financialYear,
			this.boolField,
			this.fromDate,
			this.toDate,
			this.intField1,
			this.intField2,
			this.derivedField,
			this.currentFy
		];

		this.childForms = [
			this.orderLines
		];
	}

	public getName(): string {
		 return 'form1';
	}
}
