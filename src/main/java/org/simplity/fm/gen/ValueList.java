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

/**
 * @author simplity.org
 *
 */
class ValueList {
	private static final String C = ", ";
	final String name;
	final Pair[] pairs;

	ValueList(String name, Pair[] pairs) {
		this.name = name;
		this.pairs = pairs;
	}

	void emitJava(StringBuilder sbf, String packageName) {
		sbf.append("package ").append(packageName).append(';');
		sbf.append('\n');

		Util.emitImport(sbf, org.simplity.fm.validn.ValueList.class);

		sbf.append("\n\n/**\n * List of valid values for list ").append(this.name);
		sbf.append("\n * <br /> generated at ").append(LocalDateTime.now());
		sbf.append("\n */ ");

		sbf.append("\npublic class ").append(Util.toClassName(this.name)).append(" extends ValueList {");

		sbf.append("\n\t private static final String[][] VALUES = { ");
		for (Pair p : this.pairs) {
			sbf.append("\n\t\t\t{").append(Util.escape(p.value)).append(C).append(Util.escape(p.label)).append("}");
			sbf.append(C);
		}
		sbf.setLength(sbf.length() - C.length());
		sbf.append("\n\t\t};");
		sbf.append("\n\t private static final String NAME = \"").append(this.name).append("\";");

		sbf.append("\n\n/**\n *\n\t * @param name\n\t * @param valueList\n */");
		sbf.append("\n\tpublic ").append(Util.toClassName(this.name)).append("(String name, String[][] valueList) {");
		sbf.append("\n\t\tsuper(name, valueList);");
		sbf.append("\n\t}");

		sbf.append("\n\n/**\n *").append(this.name).append("\n */");
		sbf.append("\n\tpublic ").append(Util.toClassName(this.name)).append("() {");
		sbf.append("\n\t\tsuper(NAME, VALUES);");
		sbf.append("\n\t}");

		sbf.append("\n}\n");
	}

	protected void emitTs(StringBuilder sbf, String indent) {
		for (int i = 0; i < this.pairs.length; i++) {
			if (i != 0) {
				sbf.append(',');
			}
			sbf.append(indent);
			Pair pair = this.pairs[i];
			sbf.append('[').append(Util.escapeTs(pair.value));
			sbf.append(C).append(Util.escapeTs(pair.label)).append(']');
		}
	}

}