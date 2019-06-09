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

package org.simplity.fm.service;

import java.io.OutputStream;
import java.util.Map;

import org.simplity.fm.http.LoggedInUser;

import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Interface for service. The instance is expected to be re-usable, and
 * thread-safe. (immutable). Singleton pattern is suitable or this.
 * <br />
 * <br />
 * serve methods may be added for otehr formats on a need basis
 * 
 * @author simplity.org
 *
 */
public interface IService {
	/**
	 * message to be used if the user is not authorized for this specific form instance
	 */
	public static final String MSG_NOT_AUTHORIZED = null;
	/**
	 * error to be used in case of any internal  error
	 */
	public static final String MSG_INTERNAL_ERROR = null;
	/**
	 * serve when data is requested in a Map
	 * 
	 * @param user
	 *            logged-in user who has requested this service. This can be
	 *            used to check whether the user is authorized to deal with the
	 *            document/form being requested
	 * @param keyFields
	 *            fields that are required to uniquely identify the form
	 * @param outStream
	 *            stream to which the output can be written to
	 * @return non-null service result.
	 */
	public ServiceResult serve(LoggedInUser user, Map<String, String> keyFields, OutputStream outStream);

	/**
	 * serve the request when data is received as a JSON Object
	 * 
	 * @param user
	 *            logged-in user who has requested this service. This can be
	 *            used to check whether the user is authorized to deal with the
	 *            document/form being requested
	 * @param formData
	 *            fields that are required to uniquely identify the form
	 * @param outs
	 *            stream to which the output can be written to
	 * @return non-null service result.
	 */
	public ServiceResult serve(LoggedInUser user, ObjectNode formData, OutputStream outs);

}
