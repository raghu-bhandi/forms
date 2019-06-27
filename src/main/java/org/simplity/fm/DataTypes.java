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

import java.util.HashMap;
import java.util.Map;

import org.simplity.fm.datatypes.DataType;

/**
 * static class that locates a data type instance
 * @author simplity.org
 *
 */
public class DataTypes {
	private static final Map<String, DataType> allTypes = new HashMap<>();
	
	/**
	 * 
	 * @param name
	 * @return data type instance, or null if there is no such data type
	 */
	public static DataType getDataType(String name) {
		DataType dt = allTypes.get(name);
		if(dt != null) {
			return dt;
		}
		try {
			dt = (DataType)Class.forName(Config.getQualifiedClassName(name)).newInstance();
		}catch(Exception e) {
			return null;
		}
		allTypes.put(name, dt);
		return dt;
	}
}
