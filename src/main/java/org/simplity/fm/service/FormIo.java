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


import org.simplity.fm.form.FormOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * service for a form based I/O operation from DB
 * 
 * @author simplity.org
 *
 */
public class FormIo implements IService{
	private static final Logger logger = LoggerFactory.getLogger(FormIo.class);

	/**
	 * 
	 * @param opern 
	 * @param formName 
	 * @return non-null instance
	 */
	public static FormIo getInstance(FormOperation opern, String formName) {
		
		return new FormIo(opern, formName);
	}

	/**
	 * 
	 * @param opern
	 * @param formName
	 */
	private FormIo(FormOperation opern, String formName) {
		//
	}

	@Override
	public void serve(IserviceContext ctx, ObjectNode payload)
			throws Exception {
		throw new Exception("Form IO service not yet implemented");
	}
}
