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

package org.simplity.fm.validn;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Base class to specify an enumeration of valid values for a field. The
 * enumeration are further restricted based on a key field. This class is
 * extended by the generated key value list clsses
 * 
 * @author simplity.org
 */
public abstract class KeyedValueList {
	protected String name;
	protected Map<String, Set<String>> values = new HashMap<>();

	/**
	 * is this key value pair valid?
	 * 
	 * @param key
	 * @param value
	 * @return true if the value valid for the key
	 */
	public boolean isValid(String key, String value) {
		Set<String> vals = this.values.get(key);
		if (vals == null) {
			return false;
		}
		return vals.contains(value);
	}

	/**
	 * 
	 * @return unique name of this list. A naiming ocnvention must be followe
	 *         dto ensure that names do not clash
	 */
	public String getName() {
		return this.name;
	}
}