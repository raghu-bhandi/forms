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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author simplity.org
 *
 */
public class DummyUser extends LoggedInUser {
	private long trustId = 1;
	private long instituteId = 2;
	private int userType;

	/**
	 * 
	 * @param id
	 * @param token
	 */
	public DummyUser(String id, String token) {
		super(id, token);
	}

	/**
	 * 
	 * @return trust id, 0 if this user is not associated with a truet
	 */
	public long getTrustId() {
		return this.trustId;
	}
	
	/**
	 * 
	 * @return institute id being worked on. 
	 */
	public long getInstituteId() {
		return this.instituteId;
	}
	
	/**
	 * @return the userType
	 */
	public int getUserType() {
		return this.userType;
	}
	
	
}
