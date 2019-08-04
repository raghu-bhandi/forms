/*
 * generated from C:/Users/raghu/eclipse-workspace/ef/src/main/resources/fm/spec/form/user.xlsx at 2019-08-04T22:59:21.171
 */
import { Form , Field } from '../form/form';
import { Validators } from '@angular/forms'

export class User extends Form {
	private static _instance = new User();
	userId = new Field('userId', 0, null, 'User Id', null, null, null, null, true, 'invalidCustId', true, 10, 10, 0, '[A-Z]{3}-[\\d]{2}-[A-Z]{3}', 0, 0, 0, null, null, null);
	firstName = new Field('firstName', 1, null, 'Form Name', null, null, null, null, true, 'invalidText', true, 2, 100, 0, null, 0, 0, 0, null, null, null);
	lastName = new Field('lastName', 2, null, 'Ref Year', null, null, null, null, true, 'invalidText', true, 2, 100, 0, null, 0, 0, 0, null, null, null);

	public static getInstance(): User {
		return User._instance;
	}

	constructor() {
		super();
		this.fields = new Map();
		this.fields.set('userId', this.userId);
		this.fields.set('firstName', this.firstName);
		this.fields.set('lastName', this.lastName);
		this.controls = {
			userId: ['',[Validators.required, Validators.pattern('[A-Z]{3}-[\\d]{2}-[A-Z]{3}'), Validators.minLength(10), Validators.maxLength(10)]], 
			firstName: ['',[Validators.required, Validators.minLength(2), Validators.maxLength(100)]], 
			lastName: ['',[Validators.required, Validators.minLength(2), Validators.maxLength(100)]]
		};
	}

	public getName(): string {
		 return 'user';
	}
}
