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
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

/**
 * @author simplity.org
 *
 */
abstract class ValueList {

	static Builder getBuilder() {
		return new Builder();
	}

	protected ValueList() {
		// prohibited from outside
	}

	protected String name;
	protected String[] labels;

	protected void getIntoMap(Map<String, ValueList> map) {
		map.put(this.name, this);
	}

	abstract void emitJava(StringBuilder sbf);

	/**
	 * concrete class for list of text values
	 *
	 */
	protected static class TextList extends ValueList {
		private String[] values;

		protected TextList(String name, String[] labels, String[] values) {
			this.name = name;
			this.values = values;
			this.labels = labels;
		}

		@Override
		void emitJava(StringBuilder sbf) {
			sbf.append("\n\tpublic static final Set<String> ").append(this.name).append(" = new HashSet<>(")
					.append(this.values.length).append(");");
			sbf.append("\n\tstatic{");
			for (int i = 0; i < this.values.length; i++) {
				sbf.append("\n\t\t").append(this.name).append(".add(").append(Util.escape(this.values[i])).append(");");
			}
			sbf.append("\n\t}");
		}

	}

	/**
	 * concrete class for list of long values
	 * 
	 */
	protected static class LongList extends ValueList {
		private long[] values;

		protected LongList(String name, String[] labels, long[] values) {
			this.name = name;
			this.values = values;
			this.labels = labels;
		}

		@Override
		void emitJava(StringBuilder sbf) {
			sbf.append("\n\tpublic static final Set<Long> ").append(this.name).append(" = new HashSet<>(")
					.append(this.values.length).append(");");
			sbf.append("\n\tstatic{");
			for (int i = 0; i < this.values.length; i++) {
				sbf.append("\n\t\t").append(this.name).append(".add(").append(this.values[i]).append("L);");
			}
			sbf.append("\n\t}");
		}
	}

	/**
	 * using a builder to accumulate rows for a list and then create a list
	 * 
	 */
	static class Builder {
		private String name = null;
		private List<String> labels = new ArrayList<>();
		private List<String> tVals = new ArrayList<>();
		private List<Long> iVals = new ArrayList<>();
		/*
		 * whether this list uses long internal values. Otherwise it woudl
		 * be text.
		 */
		private boolean isLong = false;

		protected Builder() {
			//
		}

		protected ValueList addRow(Row row) {
			if (row == null || row.getPhysicalNumberOfCells() == 0
					|| row.getCell(1).getCellType() == Cell.CELL_TYPE_BLANK) {
				System.out.println("Row is null. Last row??");
				return this.build();
			}
			ValueList list = null;
			String newName = Util.textValueOf(row.getCell(0));
			Cell cell = row.getCell(1);
			if (this.name == null) {
				/*
				 * this is the very first row being read.
				 */
				if (newName.isEmpty()) {
					System.err.println("name of data type not mentioned? row ignored...");
					return null;
				}
				this.newList(newName, cell);
			} else if (newName.isEmpty() == false && newName.equals(this.name) == false) {
				/*
				 * this row is for the next list. build the previous one.
				 */
				list = this.build();
				this.newList(newName, cell);
			}

			this.labels.add(Util.textValueOf(row.getCell(2)));

			if (this.isLong) {
				this.iVals.add(Util.longValueOf(cell));
			} else {
				this.tVals.add(Util.textValueOf(cell));
			}
			return list;

		}

		private void newList(String newName, Cell cell) {
			this.iVals.clear();
			this.tVals.clear();
			this.labels.clear();
			this.name = newName;
			this.isLong = cell.getCellType() == Cell.CELL_TYPE_NUMERIC;
			System.out.println("New list created for " + this.name + " with isLong = " + this.isLong);
		}

		private ValueList build() {
			if (this.name == null) {
				System.out.println("empty line in lists??");
				return null;
			}
			if (this.isLong) {
				long[] vals = new long[this.iVals.size()];
				int i = 0;
				for (Long val : this.iVals) {
					vals[i] = val;
					i++;
				}
				return new LongList(this.name, this.labels.toArray(new String[0]), vals);
			}
			return new TextList(this.name, this.labels.toArray(new String[0]), this.tVals.toArray(new String[0]));
		}
	}
}