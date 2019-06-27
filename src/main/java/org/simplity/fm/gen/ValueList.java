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
import java.util.List;

import org.apache.poi.ss.usermodel.Row;

/**
 * @author simplity.org
 *
 */
class ValueList {
	private static final int NBR_CELLS = 3;
	private static final String C = ", ";
	final String name;
	final Pair[] pairs;

	static Builder getBuilder() {
		return new Builder();
	}
	
	ValueList(String name, Pair[] pairs) {
		this.name = name;
		this.pairs = pairs;
	}

	void emitJava(StringBuilder sbf) {
		sbf.append("\n\tpublic static final Set<String> ").append(this.name).append(" = new HashSet<>(")
				.append(this.pairs.length).append(");");
		sbf.append("\n\tstatic{");
		for (int i = 0; i < this.pairs.length; i++) {
			sbf.append("\n\t\t").append(this.name).append(".add(").append(Util.escape(this.values[i])).append(");");
		}
		sbf.append("\n\t}");
	}

	protected void emitTs(StringBuilder sbf) {
		for (int i = 0; i < this.pairs.length; i++) {
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
		ValueList addRow(Row row) {
			if (row == null) {
				return this.build();
			}
			ValueList result = null;
			String newName = Util.textValueOf(row.getCell(0));
			String val = Util.textValueOf(row.getCell(1));
			String label = Util.textValueOf(row.getCell(2));
			if (this.name == null) {
				/*
				 * this is the very first row being read.
				 */
				if (newName == null) {
					DataTypes.logger.error("name of the list not mentioned? row {} skipped...", row.getRowNum());
					return null;
				}
				this.newList(newName);
			} else if (newName != null && newName.equals(this.name) == false) {
				/*
				 * this row is for the next list. build the previous one.
				 */
				result = this.build();
				this.newList(newName);
			}

			this.pairs.add(new Pair(label, val));
			return result;
		}

		private void newList(String newName) {
			this.pairs.clear();
			this.name = newName;
			DataTypes.logger.info("New valueList initiated for {} ", this.name);
		}

		private ValueList build() {
			if (this.name == null) {
				DataTypes.logger.error("empty line in lists??, Your list may be all mixed-up!!.");
				return null;
			}
			return new ValueList(this.name, this.pairs.toArray(new Pair[0]));
		}
	}
}