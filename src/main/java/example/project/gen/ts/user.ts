/*
 * generated from C:/Users/raghu/eclipse-workspace/ef/src/main/resources/fm/spec/form/user.xlsx at 2019-07-28T20:24:02.259
 */
import { Form , Field } from '../form/form';

export class User extends Form {
	private static _instance = new User();
	userId = new Field('userId', 0, 'User Id', null, null, null, true, true, false, true,
				0, '[A-Z]{3}-[\\d]{2}-[A-Z]{3}', 'invalidCustId', 10, 10, 0, 0, null, null, null, null, null);
	firstName = new Field('firstName', 1, 'Form Name', null, null, null, true, true, false, false,
				0, null, 'invalidText', 2, 100, 0, 0, null, null, null, null, null);
	lastName = new Field('lastName', 2, 'Ref Year', null, null, null, true, true, false, false,
				0, null, 'invalidText', 2, 100, 0, 0, null, null, null, null, null);

	public static getInstance(): User {
		return User._instance;
	}

	constructor() {
		super();
		this.fields = [
			this.userId,
			this.firstName,
			this.lastName
		];
	}

	public getName(): string {
		 return 'user';
	}
}
