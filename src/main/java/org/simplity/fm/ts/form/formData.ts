import { Form, Field, ChildForm } from './form';
import { DataStore } from './dataStore';
import { FormGroup, FormBuilder, FormArray, FormControl, ValidationErrors } from '@angular/forms';
import { WebDriverLogger } from 'blocking-proxy/built/lib/webdriver_logger';

// tslint:disable: indent
/**
 * represents the data contained in a form
 */
export abstract class AbstractData {
	static TAG_HEADER = 'header';
	static TAG_DATA = 'data';
	static FORM_SERVICE = 'manageForm';
	static LIST_SERVICE = 'listService';
	/*
	 * form I/O service prefixes
	 */
	static OP_FETCH = 'get';
	static OP_NEW = 'create';
	static OP_UPDATE = 'update';
	static OP_DELETE = 'delete';
	static OP_FILTER = 'filter';

	/*
	 * filter operators
	 */
	static FILTER_EQ = '=';
	static FILTER_NE = '!=';
	static FILTER_LE = '<=';
	static FILTER_LT = '<';
	static FILTER_GE = '>=';
	static FILTER_GT = '>';
	static FILTER_BETWEEN = '><';
	static FILTER_STARTS_WITH = '^';
	static FILTER_CONTAINS = '~';
	static FILTER_IN_LIST = '@';

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
	/**
	 * ng model for all fields in this form
	 */
	formGroup: FormGroup;
	/**
	 * list of options/values for alldrop-downs in this form
	 */
	lists: { [key: string]: Array<[any, string]> };

	/**
	 * @param f form forwhich this data is tobe created
	 * @param fb form builder to be used to create formGroup
	 */
	public constructor(f: Form, fb: FormBuilder) {
		super(f);
		this.formGroup = fb.group(this.form.controls);
		let triggers = this.handleDropDowns(f);

		if (f.childForms) {
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
		if (triggers) {
			for (const field of triggers) {
				this.getListValues(field, null);
			}
		}
	}

	/**
	 * get drop-down list of values for a field. 
	 * it may be available locally, or we my have to get it from the server
	 * @param field for which drop-down list id to be fetched
	 */
	public getListValues(field: Field, key: string): void {
		if (field.keyedList) {
			console.log('Field has a design-time keyed list');
			let arr = field.keyedList[key];
			if (!arr) {
				console.error('Drop down values for field ' + field.name + ' not available in the design-time supplied list for value ' + key);
				arr = [];
			}
			this.lists[field.name] = arr;
			console.log("Drop down set with " + arr.length + ' rows');
			return;
		}
		console.log("We have to make a trip to the server to get values for drop down field " + field.name)
		let data: any;
		if (key) {
			data = { list: field.listName, key: key };
		} else {
			data = { list: field.listName };
		}
		DataStore.getResponse(FormData.LIST_SERVICE, null, data, false, (list, msg) => {
			const arr = list['list'] as Array<[any, string]>;
			console.log('list values received for field ' + field.name + ' with ' + (arr && arr.length) + ' values');
			this.lists[field.name] = arr;
		}, null);
	}

	private handleDropDowns(f: Form): Array<Field> {
		if (!f.listFields) {
			return null;
		}
		this.lists = {};
		let triggers: Array<Field> = [];
		for (const nam of f.listFields) {
			const field = f.fields.get(nam) as Field;
			if (field.valueList) {
				console.log('Field ' + nam + ' found design-time list with ' + field.valueList.length + ' entries in ti');
				this.lists[nam] = field.valueList;
			} else {
				this.lists[nam] = [];
				if (field.valueListKey) {
					//register on-change for the parent field
					console.log('Field ' + nam + '  is based on ' + field.valueListKey + '. Hence we just added a trigger');
					const fc = this.formGroup.get(field.valueListKey) as FormControl;
					fc.valueChanges.subscribe((value: string) => this.getListValues(field, value));
				} else {
					console.log('Field ' + nam + '  is not key-based and not design-time. We will make a call to the server.');
					//fixed list, but we have to get it from server at run time
					triggers.push(field);
				}
			}
		}
		if (triggers.length > 0) {
			return triggers;
		}
		return null;
	}

	/**
	 * is this form data valid?
	 */
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
	 * get data from the server and populates this formData
	 */
	public fetchData() {
		const operation = FormData.OP_FETCH;
		if (!this.opAllowed(operation)) {
			return;
		}

		const data = this.extractKeyFields();
		if (data == null) {
			console.error('Fetch request abandoned');
			return;
		}
		DataStore.getResponse(this.getServiceName(operation), null, data, false, (payload, messages) => {
			this.setAll(payload);
		});
		console.log('Fetch request sent to the server with data ', data);
	}

	private getServiceName(operation: string): string {
		return operation + '-' + this.form.getName();
	}

	public saveAsNew() {
		if (!this.opAllowed(FormData.OP_NEW)) {
			return;
		}

		this.validateForm();
		if (!this.isValid()) {
			//we have to ensure that the field in error is brought to focus!!
			alert("Form data has some errors. Please fix and then try again.");
			return;
		}
		//TODO: what to do on success???
		DataStore.getResponse(this.getServiceName(FormData.OP_NEW), null, this.extractAll(), true, (payload, msgs) => {
			alert("Crate New Operation Successful");
		});
	}

	public save() {
		if (!this.opAllowed(FormData.OP_UPDATE)) {
			return;
		}
		/*
		 * save requires key. just checking..
		 */
		if (!this.extractKeyFields) {
			return;
		}

		if (!this.validateForm()) {
			//we have to ensure that the field in error is brought to focus!!
			alert("Form data has some errors. Please fix and then try again.");
			return;
		}
		const data = this.extractAll();
		DataStore.getResponse(this.getServiceName(FormData.OP_UPDATE), null, data, true, (paylaod, msgs) => {
			alert('Updated');
		});
	}

	private opAllowed(operation: string): boolean {
		if (this.form.opsAllowed[operation]) {
			return true;
		}
		console.error('Form ', this.form.getName(), ' is not designed for ', operation, ' operation');
		return false;
	}

	public delete() {
		const operation = FormData.OP_DELETE;
		if (!this.opAllowed(operation)) {
			return;
		}

		const data = this.extractKeyFields();
		if (data == null) {
			return;
		}
		DataStore.getResponse(this.getServiceName(operation), null, data, false, (data, messages) => {
			alert('Delete succeeded');
		});
		console.log('Delete request sent to server with data ', data);
	}

	private extractKeyFields(): { [key: string]: any } {
		const data = {};
		for (const key of this.form.keyFields) {
			const val = this.getFieldValue(key);
			if (!val) {
				console.error('Key field ', key, ' has no value. operation abandoned.');
				return;
			}
			data[key] = val;
		}
		return data;
	}
	/**
	 * 
	 * @param conditions example {field1 : ['><', 13, 57], field2: ['@', '3,56,78'], f3:['=', '2018-12-21']..}
	 * possible conditions are listed in FormData.FILTER_XXX.
	 * @param maxRows =1 if you want the response to have one object in the response payload. > 1 if the
	 * pyalod shoudl combe back aith 0 or more objects in an array
	 */
	public filter(conditions: { [key: string]: [] }, maxRows: number): void {
		const operation = FormData.OP_FILTER;
		if (!this.opAllowed(operation)) {
			return;
		}
		const payload = {nbrRows: maxRows, conditions: conditions};
		DataStore.getResponse(this.getServiceName(operation), null, conditions, true, (data, messages) => {
			if(maxRows == 1){
				this.setAll(data);
			}else{
				alert('Filter returned, with' + (data as Array<any>).length + ' rows.');
			}
		});

		console.log('Filter request sent to the server with condition = ', conditions);
	}
	/**
	 * get/save/validate/submit this form
	 */
	public manageForm(operation: string): void {
		let msg = 'requesting operation "' + operation;
		if (operation === 'validate' || operation === 'submit') {
			if (!this.validateForm()) {
				console.error('Form has errors. "' + operation + '" operation aborted.');
				alert("Form data has some errors. Please fix and then try again.");
				return;
			}
			msg += '" after successful validation';
		} else {
			msg += '" without validating data';
		}
		console.log(msg);
		let data = {};
		this.addHeader(data, operation);
		if (operation !== 'get') {
			data[FormData.TAG_DATA] = this.extractAll();
		}
		DataStore.getResponse(FormData.FORM_SERVICE, null, data, true,
			(data, msgs) => {
				if (operation === 'get') {
					this.setAll(data);
				} else if (operation === 'submit') {
					window.alert('Submitted successfully with receipt id ' + data['ackId']);
				} else {
					window.alert("Data saved successfully");
				}
			}, null);
	}

	private addHeader(data: any, op: string): void {
		data[FormData.TAG_HEADER] = {
			operation: op,
			formName: this.form.getName(),
			customerId: DataStore.AUTH,
			referenceYear: DataStore.YEAR
		};

	}

	public validateForm(): boolean {
		this.formGroup.updateValueAndValidity();
		if (!this.formGroup.valid) {
			return false;
		}
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
				ok = this.validateExclPair(c1.value, c2.value, v.atLeastOne);
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
	 * two fields have to be both specified or both skipped.
	 * if value is specified, it means that the rule is applicable if v1 == value
	 * @param v1 
	 * @param v2 
	 * @param value 
	 */
	private validateInclPair(v1: string, v2: string, value: string): boolean {
		/*
		 * we assume v1 is specified when a value is given. 
		 * However, if value is specified, then it has to match it' 
		 */
		const v1Specified = v1 && (!value || value == v1);
		if(v1Specified){
			if(v2){
				return true;
			}
			return false;
		}
		// v1 is not specified, so v2 should not be specified
		if(v2){
			return false;
		}
		return true;
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
