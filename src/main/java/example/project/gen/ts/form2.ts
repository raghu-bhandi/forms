/*
 * generated from C:/Users/raghu/eclipse-workspace/ef/src/main/resources/fm/spec/form/form2.xlsx at 2019-08-30T23:09:24.579
 */
import { Form , Field } from '../form/form';
import { Validators } from '@angular/forms'

export class Form2 extends Form {
	private static _instance = new Form2();
	headerId = new Field('headerId', 0, 1, {
		label:'Header Id', 
		errorId:'invalidId', 
		isRequired:true, 
		minLength:1, 
		maxLength:13, 
		minValue:1, 
		maxValue:9999999999999
	});
	productId = new Field('productId', 1, 0, {
		label:'Product Id', 
		placeHolder:'product Id', 
		isEditable:true, 
		errorId:'invalidTextId', 
		isRequired:true, 
		minLength:4, 
		maxLength:15, 
		regex:'[\\w]*'
	});
	quantity = new Field('quantity', 2, 1, {
		label:'Quantity', 
		placeHolder:'quantity', 
		isEditable:true, 
		errorId:'InvalidOrderQty', 
		isRequired:true, 
		minLength:1, 
		maxLength:4, 
		minValue:1, 
		maxValue:2000
	});
	color = new Field('color', 3, 1, {
		label:'Color', 
		placeHolder:'Select', 
		isEditable:true, 
		errorId:'invalidColor', 
		isRequired:true, 
		minLength:1, 
		maxLength:5, 
		maxValue:100, 
		listName:'colors'
	});

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
			headerId: ['',[Validators.required, Validators.max(9999999999999), Validators.min(1), Validators.minLength(1), Validators.maxLength(13)]], 
			productId: ['',[Validators.required, Validators.pattern('[\\w]*'), Validators.minLength(4), Validators.maxLength(15)]], 
			quantity: ['',[Validators.required, Validators.max(2000), Validators.min(1), Validators.minLength(1), Validators.maxLength(4)]], 
			color: ['',[Validators.required, Validators.max(100), Validators.min(0), Validators.minLength(1), Validators.maxLength(5)]]
		};
		this.listFields = ['color'];
		this.keyFields = ['headerId', 'productId'];
	}

	public getName(): string {
		 return 'form2';
	}
}
