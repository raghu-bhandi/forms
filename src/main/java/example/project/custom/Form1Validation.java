/*
 * Copyright (c) 2019 simplity.org
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package example.project.custom;

import java.util.List;

import org.simplity.fm.Message;
import org.simplity.fm.form.FormData;
import org.simplity.fm.validn.IValidation;

import example.project.gen.form.Form1;

/**
 * @author simplity.org
 *
 */
public class Form1Validation implements IValidation{

	@Override
	public boolean isValid(FormData form, List<Message> messages) {
		long int1 = form.getLongValue(Form1.intField1);
		if(int1 > 100 || int1 < 10) {
			messages.add(Message.newFieldError("intField1", "customValidaitonError", null));
			return false;
		}
		return true;
	}

}
