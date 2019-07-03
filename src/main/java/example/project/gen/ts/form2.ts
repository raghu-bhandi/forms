/*
 * generated from C:\Users\raghu\eclipse-workspace\ef\src\main\resources\fm\spec\form\form2.xlsx at 3 Jul, 2019 7:40:02 PM
 */
import { Form , Field } from '../form/form';

export class Form2 extends Form {
	private static _instance = new Form2();
	customerIdd = new Field('customerIdd', 0, 'Customer Id', 'customer id', null, true, true, false, true,
				0, '[A-Z]{3}-[\\d]{2}-[A-Z]{3}', 'invalidCustId', 10, 10, 0, 0, null, null, null, null);
	productId = new Field('productId', 1, 'Product Id', 'product Id', null, true, true, false, true,
				0, '[\\w]*', 'invalidTextId', 4, 15, 0, 0, null, null, null, null);
	quantity = new Field('quantity', 2, 'Quantity', 'quantity', null, true, true, false, false,
				1, null, 'InvalidOrderQty', 1, 4, 1, 2000, null, null, null, null);
	color = new Field('color', 3, 'Color', 'Select', null, true, true, false, false,
				1, null, 'invalidColor', 1, 5, 0, 100, null, null, null, [
				['Red', '1'],
				['Green', '2'],
				['Blue', '3']
			]);

	public static getInstance(): Form2 {
		return Form2._instance;
	}

	constructor() {
		super();
		this.fields = [
			this.customerIdd,
			this.productId,
			this.quantity,
			this.color
		];
	}

	public getName(): string {
		 return 'form2';
	}
}
