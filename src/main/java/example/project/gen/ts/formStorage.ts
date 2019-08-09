/*
 * generated from C:/Users/raghu/eclipse-workspace/ef/src/main/resources/fm/spec/form/formStorage.xlsx at 2019-08-09T13:36:30.962
 */
import { Form , Field } from '../form/form';
import { Validators } from '@angular/forms'

export class FormStorage extends Form {
	private static _instance = new FormStorage();
	customerId = new Field('customerId', 0, null, 'CustomerId', null, null, null, null, true, 'invalidCustId', true, 10, 10, 0, '[A-Z]{3}-[\\d]{2}-[A-Z]{3}', 0, 0, 0, null, null, null);
	formName = new Field('formName', 1, null, 'Form Name', null, null, null, null, true, 'invalidText', true, 2, 100, 0, null, 0, 0, 0, null, null, null);
	referenceYear = new Field('referenceYear', 2, null, 'Ref Year', null, null, null, null, true, 'invalidFy', true, 4, 4, 1, null, 1989, 2025, 0, null, null, null);
	ackId = new Field('ackId', 3, null, 'Acknwledgment Id', null, null, null, null, false, 'invalidText', false, 2, 100, 0, null, 0, 0, 0, null, null, null);
	status = new Field('status', 4, null, 'Status', null, null, null, null, false, 'invalidText', false, 2, 100, 0, null, 0, 0, 0, null, null, null);
	formData = new Field('formData', 5, null, 'Form Data', null, null, null, null, false, 'invalidMahabharat', false, 1, 10000, 0, null, 0, 0, 0, null, null, null);
	operation = new Field('operation', 6, null, 'Operation', null, null, null, null, true, 'invalidText', true, 2, 100, 0, null, 0, 0, 0, null, null, null);

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
