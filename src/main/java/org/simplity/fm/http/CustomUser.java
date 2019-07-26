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

package org.simplity.fm.http;

import org.simplity.fm.Forms;
import org.simplity.fm.form.Form;
import org.simplity.fm.form.FormData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import example.project.gen.form.User;

/**
 * @author simplity.org
 *
 */
public class CustomUser extends LoggedInUser {
	private static final Logger logger = LoggerFactory.getLogger(CustomUser.class);
	private String firstName;
	private String lastName;

	/**
	 * 
	 * @param id
	 * @param token
	 */
	public CustomUser(String id, String token) {
		super(id, token);
		Form form = Forms.getForm("user");
		if (form == null) {
			logger.error(
					"Unable to get instance of form named 'user'. logged in user details not added to teh context");
			return;
		}
		FormData fd = form.newFormData();
		fd.setStringValue(User.userId, id);
		try {
			if (fd.fetchFromDb()) {
				this.firstName = fd.getStringValue(User.firstName);
				this.lastName = fd.getStringValue(User.lastName);
				logger.info("Details for user {} extracted from the data base", id);
			} else {
				logger.error("No row found for user {}", id);
			}
		} catch (Exception e) {
			logger.error("Error while reading user details from db {} ", e.getMessage());
		}
	}

	/**
	 * 
	 * @return the first name
	 */
	public String getFirstName() {
		return this.firstName;
	}

	/**
	 * 
	 * @return the last name
	 */
	public String getLastName() {
		return this.lastName;
	}
}
