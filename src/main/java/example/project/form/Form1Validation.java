
package example.project.form;

import java.util.List;

import org.simplity.fm.Message;
import org.simplity.fm.data.Form;
import org.simplity.fm.data.IFormValidation;

/**
 * custom validation for form1
 * @author simplity.org
 *
 */
public class Form1Validation implements IFormValidation{

	@Override
	public boolean isValid(Form form, List<Message> mesages) {
		// TODO Auto-generated method stub
		return false;
	}

}
