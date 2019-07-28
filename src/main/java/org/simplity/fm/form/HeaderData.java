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

package org.simplity.fm.form;

import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author simplity.org
 *
 */
public class HeaderData extends FormData {
	private static final Logger logger = LoggerFactory.getLogger(FormData.class);
	private HeaderIndexes indexes;
	/**
	 * @param form
	 * @param indexes 
	 */
	public HeaderData(Form form, HeaderIndexes indexes) {
		super(form, null, null);
		this.indexes = indexes;
	}


	/**
	 * 
	 * @return name of the form this header is meant for
	 */
	public String getFormName() {
		return (String) this.fieldValues[this.indexes.formNameIndex];
	}

	/**
	 * 
	 * @return serialized form data
	 */
	public String getFormData() {
		return (String) this.fieldValues[this.indexes.formDataIndex];
	}

	/**
	 * 
	 * @return requested operation on this form
	 */

	public FormOperation getFormOperation() {
		String oper = (String) this.fieldValues[this.indexes.formOperationIndex];
		try {
			return FormOperation.valueOf(oper.toUpperCase());
		} catch (Exception e) {
			logger.error("Header data has an invalid formOperaiton of {}", oper);
			return null;
		}
	}

	/**
	 * 
	 * @param serializedData
	 */
	public void setFormData(String serializedData) {
		this.fieldValues[this.indexes.formDataIndex] = serializedData;
	}

	/**
	 * save this form..
	 * @throws SQLException 
	 */
	public void save() throws SQLException {
		if(this.updateInDb() == false) {
			this.insertToDb();
		}
	}

	/**
	 * submit this form..
	 * @throws SQLException 
	 */
	public void submit() throws SQLException {
		if(this.updateInDb() == false) {
			this.insertToDb();
		}
		/*
		 * we are not to send back the for serialized data!!
		 */
		this.setFormData(null);
	}
}
