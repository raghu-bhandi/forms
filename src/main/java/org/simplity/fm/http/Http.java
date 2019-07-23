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

/**
 * one place to define all hard-coded values that are used across layers
 * 
 * @author simplity.org
 *
 */
public class Http {
	private Http() {
		//
	}

	/**
	 * tag/name of form header in the request pay load
	 */
	public static final String FORM_HEADER_TAG = "header";
	/**
	 * tag/name of form data in the request pay load
	 */
	public static final String FORM_DATA_TAG = "data";
	/**
	 * header name to specify the service name
	 */
	public static final String SERVICE_HEADER = "_s";
	/**
	 * standard header for auth token
	 */
	public static final String AUTH_HEADER = "Authorization";
	/**
	 * various headers that we respond back with
	 */
	public static final String[] HDR_NAMES = { "Access-Control-Allow-Methods", "Access-Control-Allow-Headers",
			"Access-Control-Max-Age", "Connection", "Cache-Control", "Expires" };
	/**
	 * values for the headers
	 */
	public static final String[] HDR_TEXTS = { "POST, GET, OPTIONS", "Authorization, content-type, " + SERVICE_HEADER,
			"1728000", "Keep-Alive", "no-cache, no-store, must-revalidate", "0" };
	/**
	 * http status
	 */
	public static final int STATUS_ALL_OK = 200;
	/**
	 * http status
	 */
	public static final int STATUS_AUTH_REQUIRED = 401;
	/**
	 * http status
	 */
	public static final int STATUS_INVALID_SERVICE = 404;
	/**
	 * http status
	 */
	public static final int STATUS_METHOD_NOT_ALLOWED = 405;
	/**
	 * http status
	 */
	public static final int STATUS_INVALID_DATA = 406;
	/**
	 * http status
	 */
	public static final int STATUS_INTERNAL_ERROR = 500;
}
