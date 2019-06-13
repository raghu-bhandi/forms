
package example.project.service;

import java.util.HashMap;
import java.util.Map;

import org.simplity.fm.data.FormStructure;
import org.simplity.fm.service.GetService;
import org.simplity.fm.service.IService;
import org.simplity.fm.service.SaveService;
import org.simplity.fm.service.SubmitService;

import example.project.data.FormStructures;

/**
 * Place holder the serves as a source for service instances
 */
public class Services {
	private static final Services instance = new Services();
	/**
	 * service prefix for a get-form
	 */
	public static final String SERVICE_TYPE_GET = "get";
	/**
	 * service prefix for a save-form
	 */
	public static final String SERVICE_TYPE_SAVE = "save";
	/**
	 * service prefix for a submit-form
	 */
	public static final String SERVICE_TYPE_SUBMIT = "submit";

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
		int idx = serviceName.indexOf('-');
		if (idx <= 0) {
			return null;
		}

		String formName = serviceName.substring(idx + 1);
		FormStructure fs = FormStructures.getStructure(formName);
		if (fs == null) {
			return null;
		}
		String opr = serviceName.substring(0, idx);
		if (SERVICE_TYPE_GET.equals(opr)) {
			service = new GetService(fs);
		} else if (SERVICE_TYPE_SAVE.equals(opr)) {
			service = new SaveService(fs);
		} else if (SERVICE_TYPE_SUBMIT.equals(opr)) {
			service = new SubmitService(fs);
		} else {
			return null;
		}
		instance.services.put(serviceName, service);
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
