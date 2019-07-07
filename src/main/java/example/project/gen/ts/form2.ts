/*
 * generated from C:\Users\raghu\eclipse-workspace\ef\src\main\resources\fm\spec\form\form2.xlsx at 2019-07-07T21:54:20.467
 */
import { Form , Field } from '../form/form';

export class Form2 extends Form {
	private static _instance = new Form2();
	productId = new Field('productId', 0, 'Product Id', 'product Id', null, true, true, false, true,
				0, '[\\w]*', 'invalidTextId', 4, 15, 0, 0, null, null, null, null, null);
	quantity = new Field('quantity', 1, 'Quantity', 'quantity', null, true, true, false, false,
				1, null, 'InvalidOrderQty', 1, 4, 1, 2000, null, null, null, null, null);
	color = new Field('color', 2, 'Color', 'Select', null, true, true, false, false,
				1, null, 'invalidColor', 1, 5, 0, 100, null, null, null, [
				['Red', '1'],
				['Green', '2'],
				['Blue', '3']
			],null);

	public static getInstance(): Form2 {
		return Form2._instance;
	}

	constructor() {
		super();
		this.fields = [
			this.productId,
			this.quantity,
			this.color
		];
	}

	public getName(): string {
		 return 'form2';
	}
}
