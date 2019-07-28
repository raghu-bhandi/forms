// tslint:disable: indent
import { FormData } from './formData';
import { Message } from './message';
import { createUrlResolverWithoutPackagePrefix } from '@angular/compiler';

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
	static MESSAGE = 'messages';

	formData: FormData;

	constructor(formData: FormData) {
		this.formData = formData;
	}

	public manageForm(operation: string): void {
		const hdr = this.getHeader(this.formData.form.getName(), operation);
		let payload: any;
		if(operation === 'get'){
			payload = { header: hdr };
		}else{
			payload = { header: hdr, data: this.formData.extractAll() }
		}
		this.getResponse(DataStore.SERVICE_NAME, payload, true);
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
		successFn?: (data: any, messages: Message[]) => void,
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

			let json = {};
			let messages: Message[] = null;
			console.log('Response text: ' + xhr.responseText + ' because state = ' + xhr.readyState + ' status=' + xhr.status);
			if (xhr.response) {
				try {
					json = JSON.parse(xhr.responseText);
					console.log('json = ' + JSON.stringify(json));
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
