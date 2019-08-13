
package org.simplity.fm.service;

import java.util.HashMap;
import java.util.Map;

import org.simplity.fm.Forms;
import org.simplity.fm.form.DbOperation;
import org.simplity.fm.form.Form;

/**
 * Place holder the serves as a source for service instances
 */
public class Services {
	/**
	 * separator between operation and form name to suggest a service name, like
	 * get-form1
	 */
	public static final char SERVICE_SEPARATOR = '-';
	/**
	 * name of the standard service that is used for managing all requested
	 * related to forms
	 */
	public static final String MANAGE_FORM = "manageForm";
	/**
	 * list service
	 */
	public static final String LIST_SERVICE = "listService";
	private static final Services instance = new Services();

	/**
	 * 
	 * @param serviceName
	 * @return service instance for this service name, or null if no such
	 *         service
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
		Form fs = Forms.getForm(formName);
		if (fs == null) {
			return null;
		}

		DbOperation opern = null;
		try {
			opern = DbOperation.valueOf(serviceName.substring(0, idx).toUpperCase());
			return FormIo.getInstance(opern, serviceName.substring(idx + 1));
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * utility for special services to register them selves
	 * 
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
		this.services.put(MANAGE_FORM, ManageForm.getInstance());
		this.services.put(LIST_SERVICE, ListService.getInstance());
	}
}
