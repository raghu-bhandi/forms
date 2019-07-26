/*
 * generated from C:/Users/raghu/eclipse-workspace/ef/src/main/resources/fm/spec/form/formStorage.xlsx at 2019-07-26T10:00:13.314
 */
import { Form , Field } from '../form/form';

export class FormStorage extends Form {
	private static _instance = new FormStorage();
	customerId = new Field('customerId', 0, 'CustomerId', null, null, null, true, true, false, true,
				0, '[A-Z]{3}-[\\d]{2}-[A-Z]{3}', 'invalidCustId', 10, 10, 0, 0, null, null, null, null, null);
	formName = new Field('formName', 1, 'Form Name', null, null, null, true, true, false, true,
				0, null, 'invalidText', 2, 100, 0, 0, null, null, null, null, null);
	referenceYear = new Field('referenceYear', 2, 'Ref Year', null, null, null, true, true, false, true,
				1, null, 'invalidFy', 4, 4, 1989, 2025, null, null, null, null, null);
	ackId = new Field('ackId', 3, 'Acknwledgment Id', null, null, null, false, false, false, false,
				0, null, 'invalidText', 2, 100, 0, 0, null, null, null, null, null);
	status = new Field('status', 4, 'Status', null, null, null, false, false, false, false,
				0, null, 'invalidText', 2, 100, 0, 0, null, null, null, null, null);
	formData = new Field('formData', 5, 'Form Data', null, null, null, false, false, false, false,
				0, null, 'invalidMahabharat', 1, 10000, 0, 0, null, null, null, null, null);
	operation = new Field('operation', 6, 'Operation', null, null, null, true, true, false, false,
				0, null, 'invalidText', 2, 100, 0, 0, null, null, null, null, null);

	public static getInstance(): FormStorage {
		return FormStorage._instance;
	}

	constructor() {
		super();
		this.fields = [
			this.customerId,
			this.formName,
			this.referenceYear,
			this.ackId,
			this.status,
			this.formData,
			this.operation
		];
	}

	public getName(): string {
		 return 'formStorage';
	}
}
