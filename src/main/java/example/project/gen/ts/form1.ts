/*
 * generated from C:/Users/raghu/eclipse-workspace/ef/src/main/resources/fm/spec/form/form1.xlsx at 2019-08-29T00:54:11.690
 */
import { Form , Field } from '../form/form';
import { Validators } from '@angular/forms'
import { ChildForm } from '../form/form';
import { Form2 } from './form2';

export class Form1 extends Form {
	private static _instance = new Form1();
	headerId = new Field('headerId', 0, 1, {
		label:'internal id', 
		placeHolder:'Header Id', 
		errorId:'invalidId', 
		minLength:1, 
		maxLength:13, 
		minValue:1, 
		maxValue:9999999999999
	});
	customerId = new Field('customerId', 1, 0, {
		defaultVlue:'AAA-99-AAA', 
		label:'customer Id', 
		placeHolder:'Customer Id', 
		isEditable:true, 
		errorId:'invalidCustId', 
		isRequired:true, 
		minLength:10, 
		maxLength:10, 
		regex:'[A-Z]{3}-[\\d]{2}-[A-Z]{3}'
	});
	financialYear = new Field('financialYear', 2, 1, {
		defaultVlue:'2010', 
		label:'Financial Year', 
		placeHolder:'financial year', 
		isEditable:true, 
		errorId:'invalidFy', 
		isRequired:true, 
		minLength:4, 
		maxLength:4, 
		minValue:1989, 
		maxValue:2025
	});
	boolField = new Field('boolField', 3, 3, {
		defaultVlue:'true', 
		label:'Boolean Field', 
		placeHolder:'boolean field', 
		trueLabel:'True', 
		falseLabel:'False', 
		isEditable:true, 
		errorId:'invalidTrueFalse'
	});
	fromDate = new Field('fromDate', 4, 4, {
		defaultVlue:'44175', 
		label:'From Date', 
		placeHolder:'from field', 
		isEditable:true, 
		errorId:'invalidFutureDate', 
		minValue:1, 
		maxValue:73000
	});
	toDate = new Field('toDate', 5, 4, {
		defaultVlue:'44896', 
		label:'To Date', 
		placeHolder:'to field', 
		isEditable:true, 
		errorId:'invalidFutureDate', 
		minValue:1, 
		maxValue:73000
	});
	intField1 = new Field('intField1', 6, 1, {
		defaultVlue:'33', 
		label:'Int Field 1', 
		placeHolder:'int field 1', 
		isEditable:true, 
		errorId:'invalidQty', 
		minLength:1, 
		maxLength:9, 
		minValue:1, 
		maxValue:9874
	});
	intField2 = new Field('intField2', 7, 1, {
		defaultVlue:'45', 
		label:'Int Field 2', 
		placeHolder:'int field 2', 
		isEditable:true, 
		errorId:'invalidQty', 
		minLength:1, 
		maxLength:9, 
		minValue:1, 
		maxValue:9874
	});
	fyStartDate = new Field('fyStartDate', 8, 4, {
		label:'FY Start Date', 
		errorId:'invalidDate', 
		minValue:-73000, 
		maxValue:73000
	});
	state = new Field('state', 9, 0, {
		defaultVlue:'KA', 
		label:'State', 
		placeHolder:'State', 
		isEditable:true, 
		errorId:'invalidState', 
		isRequired:true, 
		minLength:2, 
		maxLength:2, 
		regex:'[A-Z][A-Z]', 
		listName:'states', valueList:[
				['KA', 'Karnataka'],
				['TN', 'Tamil Nadu'],
				['TS', 'Telengana']
			]
	});
	district = new Field('district', 10, 1, {
		defaultVlue:'123', 
		label:'District', 
		placeHolder:'District', 
		isEditable:true, 
		errorId:'invalidDistrict', 
		isRequired:true, 
		minValue:1, 
		maxValue:9999, 
		listName:'districts', 
		listKey:'state', keyedList:{
				KA : [
					['0', 'Bengaluru'], 
					['1', 'Mysuru'], 
					['2', 'Uttara Kannada']
				], 
				TN : [
					['44', 'Periyar'], 
					['45', 'Chennai Urban'], 
					['46', 'Chennai Rural'], 
					['47', 'Coimbatore']
				], 
				TS : [
					['57', 'Hydrabad Urban'], 
					['58', 'Secundrabad'], 
					['61', 'Kachiguda']
				]
			}
	});
	kaSpecificField = new Field('kaSpecificField', 11, 0, {
		defaultVlue:'kannada', 
		label:'Mother Tongue', 
		placeHolder:'Mother Tongue', 
		isEditable:true, 
		errorId:'invalidText', 
		minLength:2, 
		maxLength:100
	});
	aadhaar = new Field('aadhaar', 12, 1, {
		defaultVlue:'111122223333', 
		label:'Aadhaar', 
		placeHolder:'Aadhaar', 
		isEditable:true, 
		errorId:'invalidAadhaar', 
		regex:'12 digits', 
		minValue:100000000000, 
		maxValue:999999999999
	});
	pan = new Field('pan', 13, 0, {
		defaultVlue:'ACTPB3029K', 
		label:'pan', 
		placeHolder:'Pan', 
		isEditable:true, 
		errorId:'invalidPan', 
		minLength:10, 
		maxLength:10, 
		regex:'[A-Z,a-z]{5}[0-9]{4}[A-Z,a-z]'
	});

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
		this.listFields = ['state', 'district'];
		this.keyFields = ['headerId'];
		this.opsAllowed = {get: true, filter: true};
	}

	public getName(): string {
		 return 'form1';
	}
}
