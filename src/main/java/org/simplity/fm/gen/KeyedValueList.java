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

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author simplity.org
 *
 */
class KeyedValueList {
	private static final String C = ", ";
	final String name;
	final Map<String, Pair[]> lists;

	KeyedValueList(String name, Map<String, Pair[]> lists) {
		this.name = name;
		this.lists = lists;
	}

	void emitJava(StringBuilder sbf, String packageName) {
		AppComps.logger.info("Started generating java for keyed list {} with {} keys", this.name, this.lists.size());
		sbf.append("package ").append(packageName).append(';');
		sbf.append('\n');

		Util.emitImport(sbf, Arrays.class);
		Util.emitImport(sbf, Set.class);
		Util.emitImport(sbf, HashSet.class);
		Util.emitImport(sbf, HashMap.class);
		Util.emitImport(sbf, org.simplity.fm.validn.KeyedValueList.class);

		sbf.append("\n\n/**\n * List of valid values for list ").append(this.name);
		sbf.append("\n * <br /> generated at ").append(LocalDateTime.now());
		sbf.append("\n */ ");

		sbf.append("\npublic class ").append(Util.toClassName(this.name)).append(" extends KeyedValueList {");

		sbf.append("\n\tprivate static final String[] _names = {");
		StringBuilder vals = new StringBuilder();
		vals.append("\n\tprivate static final Object[] _values = {");
		for (Map.Entry<String, Pair[]> entry : this.lists.entrySet()) {
			sbf.append(Util.escape(entry.getKey())).append(C);
			this.emitJavaSet(vals, entry.getValue());
			vals.append(C);
		}
		sbf.setLength(sbf.length() - C.length());
		sbf.append("};");

		vals.setLength(vals.length() - C.length());
		vals.append("};");
		sbf.append(vals.toString());

		sbf.append("\n\tprivate static final String _name = \"").append(this.name).append("\";");

		sbf.append("\n\n/**\n *").append(this.name).append("\n */");

		sbf.append("\n\tpublic ").append(Util.toClassName(this.name)).append("() {");
		sbf.append("\n\t\tthis.name = _name;");
		sbf.append("\n\t\tthis.values = new HashMap<>();");

		sbf.append("\n\t\tfor (int i = 0; i < _names.length;i++) {");
		sbf.append("\n\t\t\tthis.values.put(_names[i], (Set<String>)_values[i]);");
		sbf.append("\n\t\t}");
		sbf.append("\n\t}");
		sbf.append("\n}\n");
	}

	private void emitJavaSet(StringBuilder vals, Pair[] ps) {
		vals.append("new HashSet<>(Arrays.asList(");
		for (Pair p : ps) {
			vals.append(Util.escape(p.value)).append(C);
		}
		vals.setLength(vals.length() - C.length());
		vals.append("))");
	}

	protected void emitTs(StringBuilder sbf, String indent) {
		boolean firstOne = true;
		for (Map.Entry<String, Pair[]> entry : this.lists.entrySet()) {
			if (firstOne) {
				firstOne = false;
			} else {
				sbf.append(C);
			}
			sbf.append(indent);
			sbf.append(Util.escape(entry.getKey())).append(" : [");
			String newIndent = indent + '\t';
			boolean f = true;
			for (Pair p : entry.getValue()) {
				if (f) {
					f = false;
				} else {
					sbf.append(C);
				}
				sbf.append(newIndent);
				sbf.append("[").append(Util.escapeTs(p.label));
				sbf.append(C).append(Util.escapeTs(p.value)).append("]");
			}
			sbf.append(indent).append(']');
		}
	}
}