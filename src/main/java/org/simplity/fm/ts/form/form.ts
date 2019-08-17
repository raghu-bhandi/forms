import { FormBuilder } from '@angular/forms';
import { FormData } from './formData';

/**
 * represents the data model, both structure and run-time data
 */
export abstract class Form {
	/**
	 * key-Field pairs. has all the fields of this form
	 */
	public fields: Map<string, Field>;
	/**
	 * key-Table pairs. has all the tables (sub-forms) in this form
	 */
	public childForms: Map<string, ChildForm>;

	/**
	 * meta-data for all controls that can be used to create a formGroup using formBuilder.group()
	 */
	public controls: { [key: string]: any; };

	/**
	 * inter-field validations
	 */
	public validations: Array<{ [key: string]: any }>;

	/**
	 * field names that have enumeratedlist of values. That is,fields that are to be rendered as drop-downs
	 */
	public listFields: Array<string>;

	/**
	 * name of this form. 
	 */
	public abstract getName(): string;

	/**
	 * create a model (data-holder) for this form
	 * @param builder 
	 */
	public newFormData(builder: FormBuilder): FormData {
		return new FormData(this, builder);
	}

	public constructor() {
	}
}

export class Field {
	static TYPE_TEXT = 0;
	static TYPE_INTEGER = 1;
	static TYPE_DECIMAL = 2;
	static TYPE_BOOLEAN = 3;
	static TYPE_DATE = 4;
	static TYPE_TIMESTAMP = 5;

	public name: string;
	public index: number
	public defaultValue: string;
	public label: string;
	public altLabel: string;
	public placeHolder: string;
	public trueLabel: string;
	public falseLabel: string;
	public isEditable: boolean;
	public errorId: string;
	public isRequired: boolean;
	public minLength: number;
	public maxLength: number;
	public valueType: number;
	public regex: string;
	public minValue: number;
	public maxValue: number;
	public nbrDecimals: number;
	public listName: string;
	public valueListKey: string;
	public valueList: Array<[string, string]>;
	public keyedList: { [key: string]: Array<[string, string]> };
	constructor(name: string, index: number, valueType: number, attrs: { [ket: string]: any }) {
		this.name = name;
		this.index = index;
		this.valueType = valueType;
		this.defaultValue = attrs.defaultValue || '';
		this.label = attrs.label || '';
		this.altLabel = attrs.altLabel || '';
		this.placeHolder = attrs.placeHolder || '';
		this.trueLabel = attrs.trueLabel || '';
		this.falseLabel = attrs.falseLabel || '';
		this.isEditable = attrs.isEditable ? true : false;
		this.errorId = attrs.errorId || '';
		this.isRequired = attrs.isRequired ? true : false;
		this.minLength = attrs.minLength || 0;
		this.maxLength = attrs.maxLength || 0;
		this.regex = attrs.regex || '';
		this.minValue = attrs.mibValue || 0;
		this.maxValue = attrs.maxValue || 0;
		this.nbrDecimals = attrs.nbrDecimals || 0;
		this.listName = attrs.listName || null;
		this.valueListKey = attrs.valueListKey || null;
		this.valueList = attrs.valueList || null;
		this.keyedList = attrs.keyedList || null;
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
