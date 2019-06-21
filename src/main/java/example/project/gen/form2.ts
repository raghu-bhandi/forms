import { From } from '../form/form';
import { Field } from '../form/field';
import { Table } from '../form/table';

/**
 * generated from C:\Users\raghu\eclipse-workspace\ef\src\main\resources\spec\struct\form2.xlsx at 21 Jun, 2019 10:52:50 PM
 */
export class Form2 extends Form {
	private static instance = new Form2();

	/**
	 * @returns singleton instance of Form2
	 */
	public static getInstance(): Form2 {
		 return Form2.instance;
	}

	private constructor() {
		super();
		this.name = 'form2';
		let vl: any = null;
		this.fields.set('productId', new Field('productId', "Product Id", "product Id", "", true, true, false, true, 0, "[\\w]*", "invalidTextId", 4, 15, 0, 0, null));
		this.fields.set('Qty', new Field('Qty', "Quantity", "quantity", "", true, true, false, false, 1, "", "InvalidOrderQty", 1, 4, 1, 2000, null));
	vl = [
			[0, 'Red']
			,[1, 'Green']
			,[2, 'Blue']
			];
		this.fields.set('color', new Field('color', "Color", "Select", "", true, true, false, false, 1, "", "invalidColor", 0, 0, 0, 0, vl));
	}
}
