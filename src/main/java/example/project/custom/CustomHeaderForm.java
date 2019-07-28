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

import java.sql.SQLException;
import java.util.UUID;

import org.simplity.fm.form.Form;
import org.simplity.fm.form.HeaderData;
import org.simplity.fm.form.HeaderForm;
import org.simplity.fm.form.HeaderIndexes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import example.project.gen.form.FormStorage;

/**
 * @author simplity.org
 *
 */
public class CustomHeaderForm extends HeaderForm {
	protected static final Logger logger = LoggerFactory.getLogger(CustomHeaderForm.class);
	/**
	 * 
	 */
	public CustomHeaderForm() {
		this.form = new FormStorage();
		this.indexes = new HeaderIndexes(FormStorage.formName, FormStorage.formData, FormStorage.operation);
	}
	
	@Override
	public HeaderData newHeaderData() {
		return new CustomData(this.form, this.indexes);
	}
	
	private static final class CustomData extends HeaderData{
		/**
		 * @param form
		 * @param indexes
		 */
		public CustomData(Form form, HeaderIndexes indexes) {
			super(form, indexes);
		}
		
		@Override
		public void submit() throws SQLException {
			this.fieldValues[FormStorage.ackId] = UUID.randomUUID().toString();
			this.fieldValues[FormStorage.status] = "A";
			super.submit();
		}
		
		@Override
		public void save() throws SQLException {
			logger.info("Going save form {} with serialzed data = {}", this.fieldValues[FormStorage.formName], this.fieldValues[FormStorage.formData]);
			this.fieldValues[FormStorage.status] = "S";
			super.save();
		}
		
	}
}
