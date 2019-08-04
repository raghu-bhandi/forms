import { FormData } from './formData';

/**
 * manages data persistence. knows how to connect to the server and request services
 * it will be enhanced to manage any future requirement about auto-save, local storage etc..
 */
export class DataStore {
	static URL = 'http://localhost:8080/a';
	static SERVICE_HEADER = '_s';
	static AUTH = 'AAA-99-AAA';
	static YEAR = '2010';
	static SERVICE_NAME = 'manageForm';
	static TAG_MESSAGES = 'messages';
	static TAG_ALL_OK = 'allOk';
	static TAG_HEADER = 'header';
	static TAG_DATA = 'data';


	constructor(private formData: FormData) {
		this.formData = formData;
	}

	static showMessages(msgs: Array<any>) {
		if (!msgs) {
			console.error('empty messages!!');
			return;
		}
		alert('Server returned with errors: ' + JSON.stringify(msgs));
	}

	public manageForm(operation: string): void {
		const hdr = this.getHeader(this.formData.form.getName(), operation);
		let payload = {};
		payload[DataStore.TAG_HEADER] = hdr;
		if (operation !== 'get') {
			payload[DataStore.TAG_DATA] = this.formData.extractAll();
		}

		this.getResponse(DataStore.SERVICE_NAME, payload, true, (data, messages) => {
			if (data) {
				if (operation === 'get') {
					this.formData.setAll(data);
				} else {
					console.log('Operation ' + operation + " successful");
				}
				return;
			}
			DataStore.showMessages(messages);
		}, (messages) => { DataStore.showMessages(messages) });
	}

	private receiveData(data: object) {
		this.formData.setAll(data);
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
		successFn?: (data: any, messages: Array<any>) => void,
		failureFn?: (messages: any[]) => void) {

		if (!successFn) {
			successFn = (data, messages) => {
				this.receiveData(data);
			}
		};
		if (!failureFn) {
			failureFn = (messages) => {
				for (const msg of messages) {
					console.error(msg.toString());
				}
			}
		};
		const xhr = new XMLHttpRequest();
		/*
		 * attach listners for XHR
		 */
		xhr.onreadystatechange = () => {
			if (xhr.readyState !== 4) {
				return;
			}

			if (xhr.status && xhr.status !== 200) {
				console.log('Http Status : ' + xhr.status + xhr.responseText);
				failureFn([{ type: 'error', id: 'serverErrror', text: 'http status ' + xhr.status }]);
				return;
			}

			let messages: Array<any> = null;
			let data = null;
			let allOk = true;
			if (xhr.response) {
				try {
					const json = JSON.parse(xhr.responseText);
					allOk = json[DataStore.TAG_ALL_OK];
					messages = json[DataStore.TAG_MESSAGES];
					data = json[DataStore.TAG_DATA];
				} catch (e) {
					console.log('Response is not json. response text is returned instead of js object....');
					failureFn([{ type: 'error', id: 'serverError', text: 'Server returned an invalid json' }]);
					return;
				}
			}
			/*
			 * any issue with our web agent?
			 */
			if (allOk) {
				successFn(data, messages);
			} else {
				failureFn(messages);
			}
			return;
		};
		xhr.ontimeout = () => {
			console.error('Http Request timed out');
			failureFn([{ type: 'error', id: 'timeOut', text: 'Server did not respond within reaosnable time.' }]);
		};

		let url = asPayload ? DataStore.URL : this.getUrlWithQry(data);
		try {
			xhr.open('POST', url, true);
			xhr.setRequestHeader(DataStore.SERVICE_HEADER, serviceName);
			xhr.setRequestHeader('_t', DataStore.AUTH);
			if (asPayload) {
				xhr.setRequestHeader('Content-Type', 'application/jsonp; charset=utf-8');
				xhr.send(JSON.stringify(data));
			} else {
				xhr.send();
			}
		} catch (e) {
			console.log('error during xhr : ' + e.message);
			failureFn([{ type: 'error', id: 'exception', text: 'Unable to connect to server. Error : ' + e.message }]);
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
		if (!data) {
			return url;
		}
		let con = '?';

		for (const a in data) {
			if (data.hasOwnProperty(a)) {
				url += con + a + '=' + encodeURIComponent(data[a]);
				con = '&';
			}
		}
		return url;
	}

	private getHeader(formName: string, operation: string): any {
		return {
			operation: operation,
			formName: formName,
			customerId: DataStore.AUTH,
			referenceYear: DataStore.YEAR
		};

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
