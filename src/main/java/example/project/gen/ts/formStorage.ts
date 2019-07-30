/*
 * generated from C:/Users/raghu/eclipse-workspace/ef/src/main/resources/fm/spec/form/formStorage.xlsx at 2019-07-30T19:43:51.190
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
			customerId: ['', Validators.required], 
			formName: ['', Validators.required], 
			referenceYear: ['', Validators.required], 
			ackId: [''], 
			status: [''], 
			formData: [''], 
			operation: ['', Validators.required]
		};
	}

	public getName(): string {
		 return 'formStorage';
	}
}
