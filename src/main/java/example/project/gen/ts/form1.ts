/*
 * generated from C:/Users/raghu/eclipse-workspace/ef/src/main/resources/fm/spec/form/form1.xlsx at 2019-07-28T20:24:01.725
 */
import { Form , Field } from '../form/form';
import { ChildForm } from '../form/form';
import { Form2 } from './form2';

export class Form1 extends Form {
	private static _instance = new Form1();
	headerId = new Field('headerId', 0, 'internal id', null, 'Header Id', null, false, false, false, true,
				1, null, 'invalidId', 1, 13, 1, 9999999999999, null, null, null, null, null);
	customerId = new Field('customerId', 1, 'customer Id', null, 'Customer Id', 'AAA-99-AAA', true, true, false, false,
				0, '[A-Z]{3}-[\\d]{2}-[A-Z]{3}', 'invalidCustId', 10, 10, 0, 0, null, null, null, null, null);
	financialYear = new Field('financialYear', 2, 'Financial Year', null, 'financial year', '2010', true, true, false, false,
				1, null, 'invalidFy', 4, 4, 1989, 2025, null, null, null, null, null);
	boolField = new Field('boolField', 3, 'Boolean Field', null, 'boolean field', 'true', false, true, false, false,
				3, null, 'invalidTrueFalse', 0, 0, 0, 0, 'True', 'False', null, null, null);
	fromDate = new Field('fromDate', 4, 'From Date', null, 'from field', '44175', false, true, false, false,
				4, null, 'invalidFutureDate', 0, 0, 1, 73000, null, null, null, null, null);
	toDate = new Field('toDate', 5, 'To Date', null, 'to field', '44896', false, true, false, false,
				4, null, 'invalidFutureDate', 0, 0, 1, 73000, null, null, null, null, null);
	intField1 = new Field('intField1', 6, 'Int Field 1', null, 'int field 1', '33', false, true, false, false,
				1, null, 'invalidQty', 1, 9, 1, 9874, null, null, null, null, null);
	intField2 = new Field('intField2', 7, 'Int Field 2', null, 'int field 2', '45', false, true, false, false,
				1, null, 'invalidQty', 1, 9, 1, 9874, null, null, null, null, null);
	derivedField = new Field('derivedField', 8, 'Total', null, null, null, false, false, true, false,
				1, null, 'invalidQty', 1, 9, 1, 9874, null, null, null, null, null);
	fyStartDate = new Field('fyStartDate', 9, 'FY Start Date', null, null, null, false, false, false, false,
				4, null, 'invalidDate', 0, 0, -73000, 73000, null, null, null, null, null);
	state = new Field('state', 10, 'State', null, 'State', 'KA', true, true, false, false,
				0, '[A-Z][A-Z]', 'invalidState', 2, 2, 0, 0, null, null, null, [
				['Karnataka', 'KA'],
				['Tamil Nadu', 'TN'],
				['Telengana', 'TS']
			],null);
	district = new Field('district', 11, 'District', null, 'District', '123', true, true, false, false,
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
	kaSpecificField = new Field('kaSpecificField', 12, 'Mother Tongue', null, 'Mother Tongue', 'kannada', false, true, false, false,
				0, null, 'invalidText', 2, 100, 0, 0, null, null, null, null, null);
	aadhaar = new Field('aadhaar', 13, 'Aadhaar', null, 'Aadhaar', '111122223333', false, true, false, false,
				1, '12 digits', 'invalidAadhaar', 0, 0, 100000000000, 999999999999, null, null, null, null, null);
	pan = new Field('pan', 14, 'pan', null, 'Pan', 'ACTPB3029K', false, true, false, false,
				0, '[A-Z,a-z]{5}[0-9]{4}[A-Z,a-z]', 'invalidPan', 10, 10, 0, 0, null, null, null, null, null);

	orderLines = new ChildForm('orderLines', 0, 'Order Lines', Form2.getInstance(), 1, 200, 'wrongLines');

	public static getInstance(): Form1 {
		return Form1._instance;
	}

	constructor() {
		super();
		this.fields = [
			this.headerId,
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
