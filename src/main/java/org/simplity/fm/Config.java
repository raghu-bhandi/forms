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

package org.simplity.fm;

/**
 * all parameters used by this app that are loaded from config file
 * TODO: read all these from a properties file. Will read-up best practices as
 * of today and build that functionality
 * 
 * @author simplity.org
 *
 */
public class Config {
	/**
	 * package name with trailing '.' that all generated classes belong to
	 */
	public static final String GENERATED_PACKAGE_NAME = "example.prohject.gen";

	/**
	 * root source folder where sources are generated. folders are appended to
	 * this by the generator based on package name
	 */
	public static final String GENERATED_SOURCE_ROOT = "c:/";

	/**
	 * dataTypes.xlsx is found in the this folder, and forms are stored under
	 * forms folder inside of this
	 */
	public static final String XLS_RESOURCE_ROOT = "c:/";

	/**
	 * 
	 * @param name
	 * @return fully qualified class name (with package name) for the name
	 */
	public static String getQualifiedClassName(String name) {
		return GENERATED_PACKAGE_NAME + name.substring(0, 1).toUpperCase() + name.substring(1);
	}
}
