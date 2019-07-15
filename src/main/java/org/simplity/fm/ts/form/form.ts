
/**
 * represents the data model, both structure and run-time data
 */
export abstract class Form {
	/**
	 * key-Field pairs. has all the fields of this form
	 */
	fields: Field[];
	/**
	 * key-Table pairs. has all the tables (sub-forms) in this form
	 */
	childForms: ChildForm[];

	/**
	 * if this form has key fields. null if not.
	 */
	keyIndexes: number[];

	public abstract getName(): string;
}
export class Field {
	// tslint:disable: indent
	static TYPE_TEXT = 0;
	static TYPE_INTEGER = 1;
    static TYPE_DECIMAL = 2;
    static TYPE_BOOLEAN = 3;
	static TYPE_DATE = 4;
	static TYPE_TIMESTAMP = 5;

	name: string;
	index: number;
	label: string;
	altLabel: string;
	placeHolder: string;
	defaultValue: string;
	isRequired: boolean;
	isEditable: boolean;
	isDerived: boolean;
	isKeyField: boolean;
	valueType: number;
	regex: string;
	errorId: string;
	minLength: number;
	maxLength: number;
	minValue: number;
	maxValue: number;
	trueLabel: string;
	falseLabel: string;
	valueListKey: string;
	valueList: [string, string][];
	keyedList: any;

	constructor(name: string, index: number, label: string, altLabel: string, placeHolder: string, defaultValue: string, isRequired: boolean,
				         isEditable: boolean, isDerived: boolean, isKeyField: boolean, valueType: number, regex: string,
						       errorId: string, minLength: number, maxLength: number, minValue: number, maxValue: number,
						       trueLabel: string, falseLabel: string, valueListKey: string, valueList: [string, string][],
						       keyedList: any) {
		this.name = name;
		this.index = index;
		this.label = label;
		this.altLabel = altLabel;
		this.placeHolder = placeHolder;
		this.defaultValue = defaultValue;
		this.isRequired = isRequired;
		this.isEditable = isEditable;
		this.isDerived = isDerived;
		this.isKeyField = isKeyField;
		this.valueType = valueType;
		this.regex = regex;
		this.errorId = errorId;
		this.minLength = minLength;
		this.maxLength = maxLength;
		this.minValue = minValue;
		this.maxValue = maxValue;
		this.trueLabel = trueLabel;
		this.falseLabel = falseLabel;
		this.valueListKey = valueListKey;
		this.valueList = valueList;
		this.keyedList = keyedList;
	}
}

export class ChildForm {
	name: string;
	index: number;
	label: string;
	form: Form;
	isTabular = true;
	minRows: number;
	maxRows: number;
	errorId: string;

	constructor(name: string, index: number, label: string, form: Form, minRows: number, maxRows: number, errorId: string) {
		this.name = name;
		this.index = index;
		this.label = label;
		this.form = form;
		this.minRows = minRows;
		this.maxRows = maxRows;
		this.errorId = errorId;
	}
}

