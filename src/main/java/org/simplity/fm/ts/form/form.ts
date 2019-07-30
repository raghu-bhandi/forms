import { FormGroup, FormBuilder, FormArray } from '@angular/forms';
import { fcall } from 'q';
import { FormData } from './formData';

/**
 * represents the data model, both structure and run-time data
 */
export abstract class Form {
	/**
	 * key-Field pairs. has all the fields of this form
	 */
	public  fields:  Map<string, Field>;
	/**
	 * key-Table pairs. has all the tables (sub-forms) in this form
	 */
	public childForms:  Map<string, ChildForm>;

	public controls: {[key: string]: any;};

	public abstract getName(): string;

	public newFormData(builder: FormBuilder): FormData{
		return new FormData(this, builder);
	}

	public constructor(){
	}
}

export class Field {
	static TYPE_TEXT = 0;
	static TYPE_INTEGER = 1;
	static TYPE_DECIMAL = 2;
	static TYPE_BOOLEAN = 3;
	static TYPE_DATE = 4;
	static TYPE_TIMESTAMP = 5;

	constructor(public name: string,
		public index: number,
		public defaultValue: string,
		public label: string,
		public altLabel: string,
		public placeHolder: string,
		public trueLabel: string,
		public falseLabel: string,
		public isEditable: boolean,
		public errorId: string,
		public isRequired: boolean,
		public minLength: number,
		public maxLength: number,
		public valueType: number,
		public regex: string,
		public minValue: number,
		public maxValue: number,
		public nbrDecimals: number,
		public valueListKey: string,
		public valueList: [string, string][],
		public keyedList: any) {
	}
}

export class ChildForm {
	constructor(public name: string, 
		public index: number, 
		public label: string, 
		public form: Form, 
		public isTabular: boolean,
		public minRows: number, 
		public maxRows: number, 
		public errorId: string) {
	}
}
