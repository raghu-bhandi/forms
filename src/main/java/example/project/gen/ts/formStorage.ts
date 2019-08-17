/*
 * generated from C:/Users/raghu/eclipse-workspace/ef/src/main/resources/fm/spec/form/formStorage.xlsx at 2019-08-17T13:14:21.411
 */
import { Form , Field } from '../form/form';
import { Validators } from '@angular/forms'

export class FormStorage extends Form {
	private static _instance = new FormStorage();
	customerId = new Field('customerId', 0, 0, {
		label:'CustomerId', 
		isEditable:true, 
		errorId:'invalidCustId', 
		isRequired:true, 
		minLength:10, 
		maxLength:10, 
		reges:'[A-Z]{3}-[\\d]{2}-[A-Z]{3}'
	});
	formName = new Field('formName', 1, 0, {
		label:'Form Name', 
		isEditable:true, 
		errorId:'invalidText', 
		isRequired:true, 
		minLength:2, 
		maxLength:100
	});
	referenceYear = new Field('referenceYear', 2, 1, {
		label:'Ref Year', 
		isEditable:true, 
		errorId:'invalidFy', 
		isRequired:true, 
		minLength:4, 
		maxLength:4, 
		minValue:1989, 
		maxValue:2025
	});
	ackId = new Field('ackId', 3, 0, {
		label:'Acknwledgment Id', 
		errorId:'invalidText', 
		minLength:2, 
		maxLength:100
	});
	status = new Field('status', 4, 0, {
		label:'Status', 
		errorId:'invalidText', 
		minLength:2, 
		maxLength:100
	});
	formData = new Field('formData', 5, 0, {
		label:'Form Data', 
		errorId:'invalidMahabharat', 
		minLength:1, 
		maxLength:10000
	});
	operation = new Field('operation', 6, 0, {
		label:'Operation', 
		isEditable:true, 
		errorId:'invalidText', 
		isRequired:true, 
		minLength:2, 
		maxLength:100
	});

	public static getInstance(): FormStorage {
		return FormStorage._instance;
	}

	constructor() {
		super();
		this.fields = new Map();
		this.fields.set('customerId', this.customerId);
		this.fields.set('formName', this.formName);
		this.fields.set('referenceYear', this.referenceYear);
		this.fields.set('ackId', this.ackId);
		this.fields.set('status', this.status);
		this.fields.set('formData', this.formData);
		this.fields.set('operation', this.operation);
		this.controls = {
			customerId: ['',[Validators.required, Validators.pattern('[A-Z]{3}-[\\d]{2}-[A-Z]{3}'), Validators.minLength(10), Validators.maxLength(10)]], 
			formName: ['',[Validators.required, Validators.minLength(2), Validators.maxLength(100)]], 
			referenceYear: ['',[Validators.required, Validators.max(2025), Validators.min(1989), Validators.minLength(4), Validators.maxLength(4)]], 
			ackId: ['',[Validators.minLength(2), Validators.maxLength(100)]], 
			status: ['',[Validators.minLength(2), Validators.maxLength(100)]], 
			formData: ['',[Validators.minLength(1), Validators.maxLength(10000)]], 
			operation: ['',[Validators.required, Validators.minLength(2), Validators.maxLength(100)]]
		};
	}

	public getName(): string {
		 return 'formStorage';
	}
}
