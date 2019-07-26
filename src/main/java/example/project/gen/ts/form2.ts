/*
 * generated from C:/Users/raghu/eclipse-workspace/ef/src/main/resources/fm/spec/form/form2.xlsx at 2019-07-26T00:29:40.210
 */
import { Form , Field } from '../form/form';

export class Form2 extends Form {
	private static _instance = new Form2();
	headerId = new Field('headerId', 0, 'Header Id', null, null, null, true, false, false, true,
				1, null, 'invalidId', 1, 13, 1, 9999999999999, null, null, null, null, null);
	productId = new Field('productId', 1, 'Product Id', null, 'product Id', null, true, true, false, true,
				0, '[\\w]*', 'invalidTextId', 4, 15, 0, 0, null, null, null, null, null);
	quantity = new Field('quantity', 2, 'Quantity', null, 'quantity', null, true, true, false, false,
				1, null, 'InvalidOrderQty', 1, 4, 1, 2000, null, null, null, null, null);
	color = new Field('color', 3, 'Color', null, 'Select', null, true, true, false, false,
				1, null, 'invalidColor', 1, 5, 0, 100, null, null, null, null,null);

	public static getInstance(): Form2 {
		return Form2._instance;
	}

	constructor() {
		super();
		this.fields = [
			this.headerId,
			this.productId,
			this.quantity,
			this.color
		];
	}

	public getName(): string {
		 return 'form2';
	}
}
