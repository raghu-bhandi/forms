/*
 * generated from C:/Users/raghu/eclipse-workspace/ef/src/main/resources/fm/spec/form/form1.xlsx at 2019-08-05T21:20:45.099
 */
import { Form , Field } from '../form/form';
import { Validators } from '@angular/forms'
import { ChildForm } from '../form/form';
import { Form2 } from './form2';

export class Form1 extends Form {
	private static _instance = new Form1();
	headerId = new Field('headerId', 0, null, 'internal id', null, 'Header Id', null, null, false, 'invalidId', false, 1, 13, 1, null, 1, 9999999999999, 0, null, null, null);
	customerId = new Field('customerId', 1, 'AAA-99-AAA', 'customer Id', null, 'Customer Id', null, null, true, 'invalidCustId', true, 10, 10, 0, '[A-Z]{3}-[\\d]{2}-[A-Z]{3}', 0, 0, 0, null, null, null);
	financialYear = new Field('financialYear', 2, '2010', 'Financial Year', null, 'financial year', null, null, true, 'invalidFy', true, 4, 4, 1, null, 1989, 2025, 0, null, null, null);
	boolField = new Field('boolField', 3, 'true', 'Boolean Field', null, 'boolean field', 'True', 'False', true, 'invalidTrueFalse', false, 0, 0, 3, null, 0, 0, 0, null, null, null);
	fromDate = new Field('fromDate', 4, '44175', 'From Date', null, 'from field', null, null, true, 'invalidFutureDate', false, 0, 0, 4, null, 1, 73000, 0, null, null, null);
	toDate = new Field('toDate', 5, '44896', 'To Date', null, 'to field', null, null, true, 'invalidFutureDate', false, 0, 0, 4, null, 1, 73000, 0, null, null, null);
	intField1 = new Field('intField1', 6, '33', 'Int Field 1', null, 'int field 1', null, null, true, 'invalidQty', false, 1, 9, 1, null, 1, 9874, 0, null, null, null);
	intField2 = new Field('intField2', 7, '45', 'Int Field 2', null, 'int field 2', null, null, true, 'invalidQty', false, 1, 9, 1, null, 1, 9874, 0, null, null, null);
	derivedField = new Field('derivedField', 8, null, 'Total', null, null, null, null, false, 'invalidQty', false, 1, 9, 1, null, 1, 9874, 0, null, null, null);
	fyStartDate = new Field('fyStartDate', 9, null, 'FY Start Date', null, null, null, null, false, 'invalidDate', false, 0, 0, 4, null, -73000, 73000, 0, null, null, null);
	state = new Field('state', 10, 'KA', 'State', null, 'State', null, null, true, 'invalidState', true, 2, 2, 0, '[A-Z][A-Z]', 0, 0, 0, null, [
				['Karnataka', 'KA'],
				['Tamil Nadu', 'TN'],
				['Telengana', 'TS']
			],null);
	district = new Field('district', 11, '123', 'District', null, 'District', null, null, true, 'invalidDistrict', true, 0, 0, 1, null, 1, 9999, 0, 'state', null,{
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
	kaSpecificField = new Field('kaSpecificField', 12, 'kannada', 'Mother Tongue', null, 'Mother Tongue', null, null, true, 'invalidText', false, 2, 100, 0, null, 0, 0, 0, null, null, null);
	aadhaar = new Field('aadhaar', 13, '111122223333', 'Aadhaar', null, 'Aadhaar', null, null, true, 'invalidAadhaar', false, 0, 0, 1, '12 digits', 100000000000, 999999999999, 0, null, null, null);
	pan = new Field('pan', 14, 'ACTPB3029K', 'pan', null, 'Pan', null, null, true, 'invalidPan', false, 10, 10, 0, '[A-Z,a-z]{5}[0-9]{4}[A-Z,a-z]', 0, 0, 0, null, null, null);

	orderLines = new ChildForm('orderLines', 0, 'Order Lines', Form2.getInstance(), true, 1, 200, 'wrongLines');

	public static getInstance(): Form1 {
		return Form1._instance;
	}

	constructor() {
		super();
		this.fields = new Map();
		this.fields.set('headerId', this.headerId);
		this.fields.set('customerId', this.customerId);
		this.fields.set('financialYear', this.financialYear);
		this.fields.set('boolField', this.boolField);
		this.fields.set('fromDate', this.fromDate);
		this.fields.set('toDate', this.toDate);
		this.fields.set('intField1', this.intField1);
		this.fields.set('intField2', this.intField2);
		this.fields.set('derivedField', this.derivedField);
		this.fields.set('fyStartDate', this.fyStartDate);
		this.fields.set('state', this.state);
		this.fields.set('district', this.district);
		this.fields.set('kaSpecificField', this.kaSpecificField);
		this.fields.set('aadhaar', this.aadhaar);
		this.fields.set('pan', this.pan);
		this.controls = {
			headerId: ['',[Validators.max(9999999999999), Validators.min(1), Validators.minLength(1), Validators.maxLength(13)]], 
			customerId: ['AAA-99-AAA',[Validators.required, Validators.pattern('[A-Z]{3}-[\\d]{2}-[A-Z]{3}'), Validators.minLength(10), Validators.maxLength(10)]], 
			financialYear: ['2010',[Validators.required, Validators.max(2025), Validators.min(1989), Validators.minLength(4), Validators.maxLength(4)]], 
			boolField: ['true',[]], 
			fromDate: ['44175',[]], 
			toDate: ['44896',[]], 
			intField1: ['33',[Validators.max(9874), Validators.min(1), Validators.minLength(1), Validators.maxLength(9)]], 
			intField2: ['45',[Validators.max(9874), Validators.min(1), Validators.minLength(1), Validators.maxLength(9)]], 
			derivedField: ['',[Validators.max(9874), Validators.min(1), Validators.minLength(1), Validators.maxLength(9)]], 
			fyStartDate: ['',[]], 
			state: ['KA',[Validators.required, Validators.pattern('[A-Z][A-Z]'), Validators.minLength(2), Validators.maxLength(2)]], 
			district: ['123',[Validators.required, Validators.max(9999), Validators.min(1)]], 
			kaSpecificField: ['kannada',[Validators.minLength(2), Validators.maxLength(100)]], 
			aadhaar: ['111122223333',[Validators.max(999999999999), Validators.min(100000000000), Validators.pattern('12 digits')]], 
			pan: ['ACTPB3029K',[Validators.pattern('[A-Z,a-z]{5}[0-9]{4}[A-Z,a-z]'), Validators.minLength(10), Validators.maxLength(10)]]
		};

		this.childForms = new Map();
		this.childForms.set('orderLines', this.orderLines);
		this.validations = [{type: 'range', errorId: 'invalidDateRange', f1: 'fromDate', f2: 'toDate', equalOk: false}, {type: 'excl', errorId: 'panOrAadhaar', f1: 'aadhaar', f2: 'pan', atLeastOne: true}, {type: 'incl', errorId: 'intfield1INtField2AreTogether', f1: 'intField1', f2: 'intField2'}, {type: 'incl', errorId: 'requiredOnKa', f1: 'state', f2: 'kaSpecificField', value:'KA'}];
	}

	public getName(): string {
		 return 'form1';
	}
}
