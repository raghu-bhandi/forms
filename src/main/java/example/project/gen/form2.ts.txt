import { Form } from '../form/form';
import { Field } from '../form/field';

/**
 * generated from C:\Users\raghu\eclipse-workspace\ef\src\main\resources\spec\struct\form2.xlsx at 23 Jun, 2019 9:41:26 AM
 */
export class Form2 extends Form {

	constructor() {
		super();
		this.name = 'form2';

	this.fields = new Map();
		let vl: any = null;
		this.fields.set('productId', new Field('productId', "Product Id", "product Id", null, true, true, false, true, 0, "[\\w]*", "invalidTextId", 4, 15, 0, 0, null));
		this.fields.set('Qty', new Field('Qty', "Quantity", "quantity", null, true, true, false, false, 1, null, "InvalidOrderQty", 1, 4, 1, 2000, null));
	vl = [
			[0, 'Red']
			,[1, 'Green']
			,[2, 'Blue']
			];
		this.fields.set('color', new Field('color', "Color", "Select", null, true, true, false, false, 1, null, "invalidColor", 0, 0, 0, 0, vl));
	}
}
