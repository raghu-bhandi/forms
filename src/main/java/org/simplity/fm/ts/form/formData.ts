import { Form, Field, ChildForm } from './form';
import { DataStore } from './dataStore';
import { FormGroup, FormBuilder } from '@angular/forms';

// tslint:disable: indent
/**
 * represents the data contained in a form
 */
export abstract class AbstractData {
	public constructor(public form: Form) {
	}
	/**
	 * @returns object contianing all data to be persisted
	 */
	public abstract extractAll(): any;

	/**
	 * @returns true if all fields are valid. false otherwise
	 */
	public abstract isValid(): boolean;
}

/**
 * represents the data contained in a form
 */
export class FormData extends AbstractData {
	/**
	 * data for child forms. index for a child form name is defined as static member of the form class
	 */
	childData: Map<string, AbstractData>;

	formGroup: FormGroup;
	/**
	 * @param form for which data is to be captured
	 */
	public constructor(f: Form, fb: FormBuilder) {
		super(f);
		this.formGroup = fb.group(this.form.controls);

		if (!f.childForms) {
			return;
		}
		this.childData = new Map<string, AbstractData>();
		f.childForms.forEach((child: ChildForm, key: string) => {
			if (child.isTabular) {
				this.childData.set(child.name, new TabularData(child.form, fb));
			} else {
				this.childData.set(child.name, new FormData(child.form, fb));
			}
		});
	}

	public isValid(): boolean {
		return this.formGroup.valid;
	}

	public setFieldValue(name: string, value: any) {
		this.formGroup.get(name).setValue(value);
	}

	public getFieldValue(name: string) {
		return this.formGroup.get(name).value;
	}

	public getChildData(name : string): AbstractData {
		return this.childData.get(name);
	}

	public setAll(data: object) {
		console.log('Got data = ' + JSON.stringify(data));
		this.formGroup.setValue(data);
		if (!this.childData) {
			return;
		}

		this.form.childForms.forEach((child: ChildForm, key: string) => {
			let d = data[child.name];
			const td = this.childData.get(child.name);
			if (!d) {
				console.warn('No data received for child ' + child.name);
			}
			if (child.isTabular) {
				let arr = [];
				if (d instanceof Array) {
					arr = d as Array<any>;
				} else {
					console.error('Data for child ' + child.name + ' is expected as an array, but a non-array is recieved')
				}
				(td as TabularData).setAll(arr);
			} else {
				(td as FormData).setAll(d || {});
			}
		});
	}
	/**
	 * @returns object contianing all data to be persisted
	 */
	public extractAll(): any {
		const d = this.formGroup.value;
		if (this.form.childForms) {
			this.form.childForms.forEach((child: ChildForm, key: string) => {
				d[child.name] = this.childData.get(child.name).extractAll();
			});
		}
		return d;
	}
	/**
	 * submit this form
	 */
	public manageForm(operation: string) {
		let msg = 'requesting operation "' + operation;
		if (operation === 'validate' || operation === 'submit') {
			this.formGroup.updateValueAndValidity();
			if (!this.formGroup.valid) {
				console.error('Form has errors. "' + operation + '" operation aborted.');
				alert("Form data has some errors. Please fix and them and try again.");
				return;
			}
			msg += '" after successful validation';
		} else {
			msg += '" without validating data';
		}
		console.log(msg);
		new DataStore(this).manageForm(operation);
	}
}

/**
 * represents table/grid data
 */
export class TabularData extends AbstractData {
	public isValid(): boolean {
		if (this.data) {
			for (const fd of this.data) {
				if (!fd.isValid) {
					return false;
				}
			}
		}
		return true;
	}
	data: Array<FormData>;
	public constructor(f: Form, private formBuilder: FormBuilder) {
		super(f);
		this.data = [];
	}

	public setAll(data: Array<object>) {
		const n = data.length;
		this.data.length = n;
		for (let i = 0; i < n; i++) {
			let fd = this.data[i];
			if (fd) {
				fd = new FormData(this.form, this.formBuilder);
				this.data[i] = fd;
			}
			fd.setAll(data[i]);
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
		const fd = new FormData(this.form, this.formBuilder);
		this.data.push(fd);
		return fd;
	}

	public getRow(idx: number): FormData {
		return this.data[idx];
	}
}
