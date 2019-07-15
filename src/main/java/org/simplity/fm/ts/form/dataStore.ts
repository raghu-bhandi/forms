// tslint:disable: indent
import { FormData } from './formData';
import { Message } from './message';

/**
 * manages data persistence. knows how to connect to the server and request services
 * it will be enhanced to manage any future requirement about auto-save, local storage etc..
 */
export class DataStore {
	static URL = 'http://localhost:4200/a';
	static SERVICE = '_s';
	static MESSAGE: string | number | symbol;
	static TIMEOUT: number;

	formData: FormData;

	constructor(formData: FormData) {
		this.formData = formData;
	}

	retrieve() {
		const data: object = this.formData.extractKeys();
		console.log('Retrieve request to be invoked with\n' + JSON.stringify(data));
		// send rquest with these fields as query string.
		// on return, call form.setAllValues()
	}

	save() {
		const data: object = this.formData.extractAll();
		console.log('Save request to be invoked with \n' + JSON.stringify(data));
		// send all values as pay load. on return handle error message/success
	}

	submit() {
		console.log('Submit called ');
		const data: object = this.formData.extractAll();
		console.log('Submit to be invoked with\n' + JSON.stringify(data));
		// send all values as pay load. on return handle error message/success
	}

	private receiveData(data: any) {

		for (const field of this.formData.form.fields) {
			if (data.hasOwnProperty(field.name)) {
				this.formData.setValue(field.index, data[field.name]);
			}
		}
		if (this.formData.childData != null) {
			for (const child of this.formData.form.childForms) {
				if (data.hasOwnProperty(child.name)) {
					// this.formData.setValue(field.index, data[field.name]);
				}
			}
		}
	}
	/**
	 * gets response from server for the service and invokes call-back function
	 * with the response
	 *
	 * @param serviceName service name to be invoked
	 * @param data to be sent to server. could be empty object or null if no data is to be sent.
	 * @param successFn function is called with an object of data that is received from the server.
	 * @param failureFn null if default error handling is expected
	 */
	getResponse(serviceName: string, data: any, asPayload: boolean,
		           successFn?: (data: any, messages: any[]) => void,
		           failureFn?: (messages: any[]) => void) {

		const xhr = new XMLHttpRequest();
		/*
		 * attach listners for XHR
		 */
		xhr.onreadystatechange = () => {
			if (xhr.readyState !== 4) {
				return;
			}

			let json = {};
			let messages: Message[] = null;
			if (xhr.responseText) {
				try {
					json = JSON.parse(xhr.responseText);
					if (json.hasOwnProperty(DataStore.MESSAGE)) {
						messages = json[DataStore.MESSAGE];
					}
				} catch (e) {
					console.log('Response is not json. response text is returned instead of js object....');
					/*
					 * utility services may use non-jsons
					 */
					json = xhr.responseText;
				}
			}
			/*
			 * any issue with our web agent?
			 */
			if (xhr.status && xhr.status !== 200) {
				console.log('Gttp Status : ' + xhr.status + xhr.responseText);
				failureFn([new Message('error', 'serverErrror', 'Server or the communication infrastructure has failed to respond.')]);
				return;
			}
			successFn(json, messages);
			return;
		};
		xhr.ontimeout = () => {
			console.error('Http Request timed out');
			failureFn([new Message('error', 'timeOut', 'Server did not respond within reaosnable time.')]);
		};

		let url = DataStore.URL;
		let method = 'POST';
		let payload = null;
		if (asPayload) {
			payload = JSON.stringify(data);
			xhr.setRequestHeader('Content-Type', 'application/json; charset=utf-8');
		} else {
			method = 'GET';
			url = this.getUrlWithQry(data);
		}
		xhr.setRequestHeader(DataStore.SERVICE, serviceName);

		try {
			xhr.open(method, url, true);
			xhr.timeout = DataStore.TIMEOUT;
			xhr.setRequestHeader('Content-Type',
				'application/json; charset=utf-8');
			if (serviceName) {
				xhr.setRequestHeader(DataStore.SERVICE, serviceName);
			}
			if (asPayload) {
				xhr.send(data);
			} else {
				xhr.send();
			}
		} catch (e) {
			console.log('error during xhr : ' + e.message);
			failureFn([new Message('error', 'exception', 'Unable to connect to server. Error : ' + e.message)]);
		}
	}

	/**
	 * @param serviceName service to be called
	 * @param data to be sent. null/empty if this service requires no data
	 * @param asPayload true if the data is to be sent as paylaod. false means send them as query string
	 * @returns promise
	 */
	public serve(serviceName: string, data: any, asPayload: boolean): Promise<any> {
		/*
		 *code to be written to use  promise instead of call back
		 */
		return null;
	}

	private getUrlWithQry(data: any): string {
		let url = DataStore.URL;
		let con = '?';

		for (const e of Object.entries(data)) {
			url += con + e[0] + '=' + encodeURIComponent(e[1] as string);
			con = '&';
		}
		return url;
	}

	public download(data: any, fileName: string) {
		const json = JSON.stringify(data);
		const blob = new Blob([json], { type: 'octet/stream' });
		const url = window.URL.createObjectURL(blob);
		const a = window.document.createElement('a');
		a.style.display = 'none';
		a.href = url;
		a.target = '_blank';
		a.download = fileName;
		document.body.appendChild(a);
		a.click();
		document.body.removeChild(a);
	}
}
