
package org.simplity.fm.service;

import java.util.HashMap;
import java.util.Map;

import org.simplity.fm.form.FormStructure;

import example.project.form.FormStructures;

/**
 * Place holder the serves as a source for service instances
 */
public class Services {
	/**
	 * separator between operation and form name to suggets a service name, like get-form1
	 */
	public static final char SERVICE_SEPARATOR = '-';
	private static final Services instance = new Services();
	/**
	 * 
	 * @param serviceName
	 * @return service instance for this service name, or null if no such service
	 */
	public static IService getService(String serviceName) {
		IService service = instance.services.get(serviceName);
		if (service != null) {
			return service;
		}
		int idx = serviceName.indexOf(SERVICE_SEPARATOR);
		if (idx <= 0) {
			return null;
		}

		String formName = serviceName.substring(idx + 1);
		FormStructure fs = FormStructures.getStructure(formName);
		if (fs == null) {
			return null;
		}
		
		String oper = serviceName.substring(0, idx);
		service = fs.getService(oper);
		if(service != null) {
			instance.services.put(serviceName, service);
		}
		return service;
	}

	/**
	 * utility for special services to register them selves
	 * @param serviceName
	 * @param service
	 */
	public static void registerService(String serviceName, IService service) {
		if (service != null && serviceName != null) {
			instance.services.put(serviceName, service);
		}
	}

	private Map<String, IService> services = new HashMap<>();

	private Services() {
		// forbidden
	}
}
