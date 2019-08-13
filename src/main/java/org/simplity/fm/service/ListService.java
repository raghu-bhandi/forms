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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * handles request to get drop-down values for a field, typically from a client
 * @author simplity.org
 *
 */
public class ListService implements IService{
	private static final ListService instance = new ListService();
	protected static final Logger logger = LoggerFactory.getLogger(ListService.class);

	/**
	 * 
	 * @return non-null instance
	 */
	public static ListService getInstance() {
		return instance;
	}
	
	private ListService() {
		//privatised for a singleton pattern
	}

	@Override
	public void serve(IserviceContext ctx, ObjectNode payload) throws Exception {
		String listName = getTextAttribute(payload, "list");
		String key = getTextAttribute(payload, "key");
	}
	
	private static String getTextAttribute(JsonNode json, String fieldName) {
		JsonNode node = json.get(fieldName);
		if (node == null) {
			return null;
		}
		JsonNodeType nt = node.getNodeType();
		if (nt == JsonNodeType.NULL || nt == JsonNodeType.MISSING) {
			return null;
		}
		return node.asText();
	}

}
