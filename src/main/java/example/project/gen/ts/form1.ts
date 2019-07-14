/*
 * generated from C:\Users\raghu\eclipse-workspace\ef\src\main\resources\fm\spec\form\form1.xlsx at 2019-07-14T14:02:43.332
 */
import { Form , Field } from '../form/form';
import { ChildForm } from '../form/form';
import { Form2 } from './form2';

export class Form1 extends Form {
	private static _instance = new Form1();
	customerId = new Field('customerId', 0, 'Customer Id', null, null, true, false, false, true,
				0, '[A-Z]{3}-[\\d]{2}-[A-Z]{3}', 'invalidCustId', 10, 10, 0, 0, null, null, null, null, null);
	financialYear = new Field('financialYear', 1, 'Financial Year', 'financial year', null, true, true, false, true,
				1, null, 'invalidFy', 4, 4, 1989, 2025, null, null, null, null, null);
	boolField = new Field('boolField', 2, 'Boolean Field', 'boolean field', null, false, true, false, false,
				3, null, 'invalidTrueFalse', 0, 0, 0, 0, 'True', 'False', null, null, null);
	fromDate = new Field('fromDate', 3, 'From Date', 'from field', null, false, true, false, false,
				4, null, 'invalidFutureDate', 0, 0, 1, 73000, null, null, null, null, null);
	toDate = new Field('toDate', 4, 'To Date', 'to field', null, false, true, false, false,
				4, null, 'invalidFutureDate', 0, 0, 1, 73000, null, null, null, null, null);
	intField1 = new Field('intField1', 5, 'Int Field 1', 'int field 1', '33', false, true, false, false,
				1, null, 'invalidQty', 1, 9, 1, 9874, null, null, null, null, null);
	intField2 = new Field('intField2', 6, 'Int Field 2', 'int field 2', '45', false, true, false, false,
				1, null, 'invalidQty', 1, 9, 1, 9874, null, null, null, null, null);
	derivedField = new Field('derivedField', 7, 'Total', null, null, false, false, true, false,
				1, null, 'invalidQty', 1, 9, 1, 9874, null, null, null, null, null);
	fyStartDate = new Field('fyStartDate', 8, 'FY Start Date', null, null, false, false, false, false,
				4, null, 'invalidDate', 0, 0, -73000, 73000, null, null, null, null, null);
	state = new Field('state', 9, 'State', 'State', null, true, true, false, false,
				0, '[A-Z][A-Z]', 'invalidState', 2, 2, 0, 0, null, null, null, [
				['Karnataka', 'KA'],
				['Tamil Nadu', 'TN'],
				['Telengana', 'TS']
			],null);
	district = new Field('district', 10, 'District', 'District', null, true, true, false, false,
				1, null, 'invalidDistrict', 0, 0, 1, 9999, null, null, 'state', null,{
				"KA" : [
					['Bengaluru', '0'], 
					['Mysuru', '1'], 
					['Uttara Kannada', '2']
				], 
				"TN" : [
					['Periyar', '44'], 
					['Chennai Urban', '45'], 
					['Chennai Rural', '46'], 
					['Coimbatore', '47']
				], 
				"TS" : [
					['Hydrabad Urban', '57'], 
					['Secundrabad', '58'], 
					['Kachiguda', '61']
				]
			});
	kaSpecificField = new Field('kaSpecificField', 11, 'Mother Tongue', 'Mother Tongue', null, false, true, false, false,
				0, null, 'invalidText', 2, 100, 0, 0, null, null, null, null, null);
	aadhaar = new Field('aadhaar', 12, 'Aadhaar', 'Aadhaar', null, false, true, false, false,
				1, null, 'invalidAadhaar', 0, 0, 100000000000, 999999999999, null, null, null, null, null);
	pan = new Field('pan', 13, 'pan', 'Pan', null, false, true, false, false,
				0, '[A-Z,a-z]{5}[0-9]{4}[A-Z,a-z]', 'invalidPan', 10, 10, 0, 0, null, null, null, null, null);

	orderLines = new ChildForm('orderLines', 0, 'Order Lines', Form2.getInstance(), 1, 200, 'wrongLines');

	public static getInstance(): Form1 {
		return Form1._instance;
	}

	constructor() {
		super();
		this.fields = [
			this.customerId,
			this.financialYear,
			this.boolField,
			this.fromDate,
			this.toDate,
			this.intField1,
			this.intField2,
			this.derivedField,
			this.fyStartDate,
			this.state,
			this.district,
			this.kaSpecificField,
			this.aadhaar,
			this.pan
		];

		this.childForms = [
			this.orderLines
		];
	}

	public getName(): string {
		 return 'form1';
	}
}
