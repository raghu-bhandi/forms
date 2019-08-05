import { Form, Field, ChildForm } from './form';
import { DataStore } from './dataStore';
import { FormGroup, FormBuilder, FormArray, FormControl, ValidationErrors } from '@angular/forms';
import { WebDriverLogger } from 'blocking-proxy/built/lib/webdriver_logger';

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
				const fd = new TabularData(child.form, fb);
				this.childData.set(child.name, fd);
				this.formGroup.addControl(child.name, fd.formArray);
			} else {
				const fd = new FormData(child.form, fb);
				this.childData.set(child.name, fd);
				this.formGroup.addControl(child.name, fd.formGroup);
			}
		});
	}

	public isValid(): boolean {
		return this.formGroup.valid;
	}

	public setFieldValue(name: string, value: any) {
		const f = this.formGroup.get(name);
		if (f) {
			f.setValue(value);
			return;
		}
		console.error(name + ' is not a field in this form. value ' + value + ' not set');
	}

	public getFieldValue(name: string) {
		const f = this.formGroup.get(name);
		if (f) {
			return f.value;
		}
		console.error(name + ' is not a field in this form. null value returned.');
		return null;
	}

	public getChildData(name: string): AbstractData {
		return this.childData.get(name);
	}

	public setAll(data: object) {
		this.formGroup.patchValue(data);
		if (!this.childData) {
			return;
		}

		this.form.childForms.forEach((child: ChildForm, key: string) => {
			console.log('Looking at child ' + key)
			let d = data[key];
			if (!d) {
				console.warn('No data received for child ' + child.name);
			}
			const td = this.childData.get(key);
			if (child.isTabular) {
				console.log('resetting child ' + key + ' to remove all lines..');
				let arr = [];
				if (d && d instanceof Array) {
					arr = d as Array<any>;
				} else if (d) {
					console.error('Data for child ' + key + ' is expected as an array, but a non-array is recieved')
				}
				console.log('assigning array ' + arr + ' to tabular data ' + td);
				(td as TabularData).setAll(arr);
			} else {
				console.log('assigning object ' + d + ' to form data ' + td);
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
			this.validateForm();
			if (!this.formGroup.valid) {
				console.error('Form has errors. "' + operation + '" operation aborted.');
				alert("Form data has some errors. Please fix and then and try again.");
				return;
			}
			msg += '" after successful validation';
		} else {
			msg += '" without validating data';
		}
		console.log(msg);
		new DataStore(this).manageForm(operation);
	}


	public validateForm(): boolean {
		const vals = this.form.validations;
		if (!vals) {
			return true;
		}

		let allOk = true;
		for (const v of this.form.validations) {
			const c1 = this.formGroup.get(v.f1) as FormControl;
			const c2 = this.formGroup.get(v.f2) as FormControl;
			const t = v.type;
			let ok: boolean;
			if (t === 'range') {
				ok = this.validateRange(c1.value, c2.value, v.isStrict);
			} else if (t === 'incl') {
				ok = this.validateInclPair(c1.value, c2.value, v.value);
			} else if (t === 'excl') {
				ok = this.validateExclPair(c1.value, c2.value, v.stLeastOne);
			} else {
				console.error('Form validation type ' + t + ' is not valid. validation ignored');
				ok = true;
			}
			if (!ok) {
				const err = { interfield: t, errorId: v.errorId }
				c1.setErrors(err);
				c2.setErrors(err);
				allOk = false;

			}
		}
		return allOk;
	}
	/**
	 * check if v1 to v2 us a range
	 * @param v1 
	 * @param v2 
	 * @param useStrict if true, v2 must be > v2, v1 == v2 woudn't cut
	 */
	private validateRange(v1: string, v2: string, equalOk: boolean): boolean {
		const n1 = Number.parseFloat(v1);
		const n2 = Number.parseFloat(v2);
		if (n1 === NaN || n2 === NaN || n2 > n1) {
			return true;
		}
		if (n1 > n2) {
			return false;
		}
		//equal. is it ok?
		return equalOk;
	}

	/**
	 * if v1 is specified, v2 is to be specified. 
	 * Hoever, if value is specified, this conditio applies only if v1 == value
	 * @param v1 
	 * @param v2 
	 * @param value 
	 */
	private validateInclPair(v1: string, v2: string, value: string): boolean {
		if (!v1 || v2) {
			return true;
		}
		//v1 specieid, but not v1.
		if (value && value != v1) {
			//Lucky, the rule is not applicable 
			return true;
		}
		return false;
	}

	/**
	 * 
	 * @param errorId v1 and v2 are exclusive
	 * @param primaryField 
	 * @param otherField 
	 * @param atLeastOne if true, exactly one of teh twoto be specified
	 */
	private validateExclPair(v1: string, v2: string, noneOk: boolean): boolean {
		if (v1) {
			if (v2) {
				return false;
			}
			return true;
		}
		if (v2) {
			return true;
		}
		//none specifield, is it ok?
		return noneOk;
	}
}

/**
 * represents table/grid data
 */
export class TabularData extends AbstractData {
	data: Array<FormData>;
	formArray: FormArray;
	public constructor(f: Form, private fb: FormBuilder) {
		super(f);
		this.data = [];
		this.formArray = new FormArray([]);
	}

	public isValid(): boolean {
		return this.formArray.valid;
	}

	public setAll(data: Array<object>) {
		const n = data.length;
		this.data.length = n;
		for (let i = 0; i < n; i++) {
			let fd = this.data[i];
			if (!fd) {
				fd = new FormData(this.form, this.fb);
				this.data[i] = fd;
				this.formArray[i] = fd.formGroup;
			}
			fd.setAll(data[i]);
		}
	}

	public extractAll(): any {
		const arr = [];
		for (const fd of this.data) {
			if (fd) {
				arr.push(fd.extractAll());
			}
		}
		return arr;
	}

	public appendRow(): FormData {
		const fd = new FormData(this.form, this.fb);
		this.data.push(fd);
		this.formArray.push(fd.formGroup);
		return fd;
	}

	public getRow(idx: number): FormData {
		return this.data[idx];
	}
}
