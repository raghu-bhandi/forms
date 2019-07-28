import { Form, Field, ChildForm } from './form';
import { DataStore } from './dataStore';
import { WebDriverLogger } from 'blocking-proxy/built/lib/webdriver_logger';

// tslint:disable: indent
/**
 * represents the data contained in a form
 */
export abstract class AbstractData {
	/**
	 * form for which we aare carrying data
	 */
	form: Form;

	public getForm(): Form {
		return this.form;
	}
	/**
	 * @returns object contianing all data to be persisted
	 */
	public abstract extractAll(): object;

	/**
	 * set all data from the data source
	 * @param data :json data
	 */
	public abstract setAll(data: object): void;
	/**
	 *  set value for a field in this form
	 * @param fieldIdx field index
	 * @param value value to be assigned
	 */
	public setValue(fieldIdx: number, value: any): void {
		throw new Error('setValue not valid on a tabular data object');
	}

	/**
	 *  set value for a field in this form
	 * @param field field
	 * @param value value to be assigned
	 */
	public setFieldValue(field: Field, value: any): void {
		throw new Error('setValue not valid on a tabular data object');
	}

	/**
	 * get value of a field
	 * @param fieldIdx field index
	 */
	public getValue(fieldIdx: number): any {
		throw new Error('geValue not valid on a tabular data object');
	}

	/**
	 * get value of a field
	 * @param field field 
	 */
	public getFieldValue(field: Field): any {
		throw new Error('geValue not valid on a tabular data object');
	}

	public getRow(idx: number): FormData {
		throw new Error('getRow not valid on a non-tabular data object');
	}

	public appendRow(): FormData {
		throw new Error('appendRow not valid on a non-tabular data object');
	}

	public getChildDataByIndex(childIdx: number): AbstractData {
		throw new Error('child data is not vid for a tabular data');
	}

	public getChildData(child: ChildForm): AbstractData {
		throw new Error('child data is not vid for a tabular data');
	}
}

/**
 * represents the data contained in a form
 */
export class FormData extends AbstractData {
	/**
	 * data for all fields in an array. Array index of a filed is available as a
	 * static field in the corresponding form class
	 */
	data: Array<any>;

	/**
	 * data for child forms. index for a child form name is defined as static member of the form class
	 */
	childData: Array<AbstractData>;

	/**
	 * @param form for which data is to be captured
	 */
	public constructor(f: Form) {
		super();
		this.form = f;
		this.data = [];

		if (f.childForms) {
			this.childData = [];
			const n = f.childForms.length;
			for (let i = 0; i < n; i++) {
				const child = f.childForms[i];
				let fd: AbstractData;
				if (child.isTabular) {
					fd = new TabularData(child.form);
				} else {
					fd = new FormData(child.form);
				}
				this.childData[i] = fd;
			}
		}
	}

	public setFieldValue(field: Field, value: any) {
		this.data[field.index] = value;
	}


	public getFieldValue(field: Field) {
		return this.data[field.index];
	}

	public setValue(fieldIdx: number, value: any) {
		if (fieldIdx < this.data.length) {
			this.data[fieldIdx] = value;
		}
	}

	public getValue(fieldIdx: number) {
		return this.data[fieldIdx];
	}

	public getChildData(child: ChildForm): AbstractData {
		return this.childData[child.index];
	}

	public getChildDataByIndex(childIdx: number): AbstractData {
		return this.childData[childIdx];
	}

	public setAll(data: object) {
		console.log('Got data = ' + JSON.stringify(data));
		for (const field of this.form.fields) {
			console.log('looking for value for field ' + field.name);
			if (data.hasOwnProperty(field.name)) {
				console.log('found data of ' + data[field.name] + ' for field ' + field.name + ' at index ' + field.index);
				this.data[field.index] = data[field.name];
			}
		}

		if (!this.childData) {
			return;
		}

		for (const child of this.form.childForms) {
			console.log('looking for value for child ' + child.name);
			if (data.hasOwnProperty(child.name)) {
				console.log('found data for ' + child.name);
				this.childData[child.index].setAll(data[child.name]);
			}
		}
	}
	/**
	 * @returns object contianing all data to be persisted
	 */
	public extractAll(): any {
		const d = {};
		for (const field of this.form.fields) {
			d[field.name] = this.data[field.index];
		}
		if (this.childData) {
			for (const child of this.form.childForms) {
				d[child.name] = this.childData[child.index].extractAll();
			}
		}
		return d;
	}

	public extractKeys(): any {
		const d = {};
		const flds = this.form.keyFields;
		if (flds) {

			for (const field of flds) {
				d[field.name] = this.data[field.index];
			}
		}
		return d;
	}

	/**
	 * submit this form
	 */
	public manageForm(operation: string) {
		if(this.validate()){
			new DataStore(this).manageForm(operation);
		}else{
			console.log('Operation ' + operation + ' aborted because of validation error');
		}
	}
	/**
	 * validation is to be implemented
	 */
	public validate() : boolean {
		return true;
	}
}

/**
 * represents table/grid data
 */
export class TabularData extends AbstractData {
	/**
	 * data for all fields as name-value pair. initialized by th econstructir with default values
	 */
	data: FormData[];

	public constructor(f: Form) {
		super();
		this.form = f;
		this.data = [];
	}

	public setAll(data: object) {
		const arr = data as Array<object>;
		this.data.length = 0;
		for (const row of arr) {
			const childData = new FormData(this.form);
			this.data.push(childData);
			childData.setAll(row);
		}
	}

	public extractAll(): any {
		const arr = [];
		for (const fd of this.data) {
			arr.push(fd.extractAll());
		}
		return arr;
	}

	public appendRow(): FormData {
		const fd = new FormData(this.form);
		this.data.push(fd);
		return fd;
	}

	public getRow(idx: number): FormData {
		return this.data[idx];
	}
}
