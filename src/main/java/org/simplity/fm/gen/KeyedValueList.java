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

package org.simplity.fm.gen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Row;

/**
 * @author simplity.org
 *
 */
class KeyedValueList {
	private static final int NBR_CELLS = 4;
	private static final String C = ", ";
	final String name;
	final Map<String, Pair[]> lists;

	static Builder getBuilder() {
		return new Builder();
	}
	KeyedValueList(String name, Map<String, Pair[]> lists) {
		this.name = name;
		this.lists = lists;
	}

	void emitJava(StringBuilder sbf) {
		sbf.append("\n\tpublic static final Set<String> ").append(this.name).append(" = new HashSet<>(")
				.append(this.values.length).append(");");
		sbf.append("\n\tstatic{");
		for (int i = 0; i < this.values.length; i++) {
			sbf.append("\n\t\t").append(this.name).append(".add(").append(Util.escape(this.values[i])).append(");");
		}
		sbf.append("\n\t}");
	}

	protected void emitTs(StringBuilder sbf) {
		for (int i = 0; i < this.labels.length; i++) {
			if (i == 0) {
				sbf.append("\n\t\t\t");
			} else {
				sbf.append("\n\t\t\t,");
			}
			sbf.append("['").append(this.labels[i].replace("'", "''")).append("', '");
			sbf.append(this.values[i].replace("'", "''")).append("']");
		}
	}

	/**
	 * using a builder to accumulate rows for a list and then create a list
	 * 
	 */
	static class Builder {
		private String name = null;
		private String keyName = null;
		private Map<String, Pair[]> lists = new HashMap<>();
		private List<Pair> pairs = new ArrayList<>();

		protected Builder() {
			//
		}
		/**
		 * add row to the builder.
		 * 
		 * @param row
		 * @return ValueList if the previous row was the last row for that list.
		 *         null if this row is appended to the existing list
		 */
		KeyedValueList addRow(Row row) {
			if (row == null) {
				return this.build();
			}
			KeyedValueList result = null;
			String newName = Util.textValueOf(row.getCell(0));
			String newKey = Util.textValueOf(row.getCell(1));
			String val = Util.textValueOf(row.getCell(2));
			String label = Util.textValueOf(row.getCell(3));
			if (this.name == null) {
				/*
				 * this is the very first row being read.
				 */
				if (newName == null) {
					DataTypes.logger.error("name of the list not mentioned? row {} skipped...", row.getRowNum());
					return null;
				}
				this.newList(newName, newKey);
			} else if (newName != null && newName.equals(this.name) == false) {
				/*
				 * this row is for the next list. build the previous one.
				 */
				result = this.build();
				this.newList(newName, newKey);
			}else if(newKey != null && newKey.contentEquals(this.keyName) == false) {
				this.addList(newKey);
			}

			this.pairs.add(new Pair(label, val));
			return result;
		}

		private void newList(String newName, String newKey) {
			this.pairs.clear();
			this.lists.clear();
			this.keyName =newKey;
			this.name = newName;
			DataTypes.logger.info("New keyed value list initiated for for {} ", this.name);
		}

		private void addList(String newKey) {
			if (this.keyName == null || this.pairs.size() == 0) {
				DataTypes.logger.error("empty line in lists??, valueList not created.");
			}else {
				this.lists.put(this.keyName, this.pairs.toArray(new Pair[0]));
				this.pairs.clear();
			}
			this.keyName = newKey;
		}

		private KeyedValueList build() {
			if (this.name == null) {
				DataTypes.logger.error("empty line in lists??, valueList not created.");
				return null;
			}
			this.addList(null);
			return new KeyedValueList(this.name, this.lists);
		}
	}
}