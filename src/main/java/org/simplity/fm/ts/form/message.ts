export class Message {
	type: string;
	id: string;
	text: string;
	fieldName: string;
	tableName: string;
	rowNUmber: number;

	constructor(type: string, id: string, text: string){
		this.type = type;
		this.id = id;
		this.text = text;
	}
}