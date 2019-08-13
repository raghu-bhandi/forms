/*
 * generated from C:/Users/raghu/eclipse-workspace/ef/src/main/resources/fm/spec/form/form2.xlsx at 2019-08-13T10:43:13.920
 */
import { Form , Field } from '../form/form';
import { Validators } from '@angular/forms'

export class Form2 extends Form {
	private static _instance = new Form2();
	headerId = new Field('headerId', 0, null, 'Header Id', null, null, null, null, false, 'invalidId', false, 1, 13, 1, null, 1, 9999999999999, 0, null, null, null, null);
	productId = new Field('productId', 1, null, 'Product Id', null, 'product Id', null, null, true, 'invalidTextId', true, 4, 15, 0, '[\\w]*', 0, 0, 0, null, null, null, null);
	quantity = new Field('quantity', 2, null, 'Quantity', null, 'quantity', null, null, true, 'InvalidOrderQty', true, 1, 4, 1, null, 1, 2000, 0, null, null, null, null);
	color = new Field('color', 3, null, 'Color', null, 'Select', null, null, true, 'invalidColor', true, 1, 5, 1, null, 0, 100, 0, 'colors', null, null,null);

	public static getInstance(): Form2 {
		return Form2._instance;
	}

	constructor() {
		super();
		this.fields = new Map();
		this.fields.set('headerId', this.headerId);
		this.fields.set('productId', this.productId);
		this.fields.set('quantity', this.quantity);
		this.fields.set('color', this.color);
		this.controls = {
			headerId: ['',[Validators.max(9999999999999), Validators.min(1), Validators.minLength(1), Validators.maxLength(13)]], 
			productId: ['',[Validators.required, Validators.pattern('[\\w]*'), Validators.minLength(4), Validators.maxLength(15)]], 
			quantity: ['',[Validators.required, Validators.max(2000), Validators.min(1), Validators.minLength(1), Validators.maxLength(4)]], 
			color: ['',[Validators.required, Validators.max(100), Validators.min(0), Validators.minLength(1), Validators.maxLength(5)]]
		};
		this.listFields = ['color'];
	}

	public getName(): string {
		 return 'form2';
	}
}
