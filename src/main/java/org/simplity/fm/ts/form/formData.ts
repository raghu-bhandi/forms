import { Form } from './form';
import { DataStore } from './dataStore';

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
	public abstract extractAll(): any;

	/**
	 *  set value for a field in this form
	 * @param fieldName field name
	 * @param value value to be assigned
	 */
	public setValue(fieldIdx: number, value: any): void {
		throw new Error('setValue not valid on a tabular data object');
	}

	/**
	 * get value of a field
	 * @param fieldName name
	 */
	public getValue(fieldIdx: number): any {
		throw new Error('geValue not valid on a tabular data object');
	}

	public getRow(idx: number): FormData {
		throw new Error('getRow not valid on a non-tabular data object');
	}

	public appendRow(): FormData {
		throw new Error('appendRow not valid on a non-tabular data object');
	}

	public getChildData(childIdx: number): AbstractData {
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
		let n = f.fields ? f.fields.length : 0;
		this.data = new Array<any>(n);

		n = f.childForms ? f.childForms.length : 0;
		this.childData = new Array<AbstractData>(n);

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


	public setValue(fieldIdx: number, value: any) {
		if (fieldIdx < this.data.length) {
			this.data[fieldIdx] = value;
		}
	}

	public getValue(fieldIdx: number) {
		return this.data[fieldIdx];
	}


	public getChildData(childIdx: number): AbstractData {
		return this.childData[childIdx];
	}

	/**
	 * @returns object contianing all data to be persisted
	 */
	public extractAll(): any {
		const d = {};
		let n = this.data.length;
		for (let i = 0; i < n; i++) {
			d[this.form.fields[i].name] = this.data[i];
		}

		n = this.childData.length;
		if (n > 0) {
			for (let i = 0; i < n; i++) {
				d[this.form.childForms[i].name] = this.childData[i].extractAll();
			}
		}
		return d;
	}

	public extractKeys(): any {
		const d = {};
		const indexes = this.form.keyIndexes;
		if (!indexes) {
			return d;
		}
		const n = indexes.length;
		for (let i = 0; i < n; i++) {
			const idx = indexes[i];
			d[this.form.fields[idx].name] = this.data[idx];
		}
	}

	/**
	 * submit this form
	 */
	public submit() {
		const ds = new DataStore(this);
		ds.submit();
	}

	/**
	 * save this form
	 */
	public save() {
		const ds = new DataStore(this);
		ds.save();
	}

	/**
	 * retrieve this form
	 */
	public retrieve() {
		const ds = new DataStore(this);
		ds.retrieve();
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
