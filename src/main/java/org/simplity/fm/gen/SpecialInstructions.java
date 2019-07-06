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

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * represents specials instructions sheet in a forms work book
 * 
 * @author simplity.org
 *
 */
class SpecialInstructions {
	protected static final Logger logger = LoggerFactory.getLogger(SpecialInstructions.class);
	private static final String L = "\n\t\t\tthis.";

	private static final String[] ATTS = { "userIdFIeldName", "createGetService", "createSaveService",
			"createSubmitService", "partialSaveAllowed" };
	private static final String[] PROCS = { "preGetProcessor", "postGetProcessor", "preSaveProcessor",
			"postSaveProcessor", "preSubmitProcessor", "postSubmitProcessor" };

	final Map<String, Object> settings = new HashMap<>();
	boolean addCommonFields = false;
	String[] dbKeyFields;

	static SpecialInstructions fromSheet(Sheet sheet) {
		SpecialInstructions si = new SpecialInstructions();
		Consumer<Row> consumer = new Consumer<Row>() {

			@Override
			public void accept(Row row) {
				String key = Util.textValueOf(row.getCell(0));
				Object val = Util.objectValueOfCell(row.getCell(1));
				logger.info("{}={}", key, val);
				if (key != null || val != null) {
					si.settings.put(key, val);
				}
			}
		};
		Util.consumeRows(sheet, 2, consumer);
		Object obj = si.settings.get("addCommonFields");
		if (obj != null) {
			if (obj instanceof Boolean) {
				si.addCommonFields = (Boolean) obj;
			}
		}
		obj = si.settings.get("dbKeyFields");
		if (obj != null) {
			if (si.settings.containsKey("dbTableName") == false) {
				logger.error("dbTableName must be specified when dbKeyFields specified");
			} else if (obj instanceof String) {
				String[] names = obj.toString().split(",");
				si.dbKeyFields = new String[names.length];
				for (int i = 0; i < names.length; i++) {
					si.dbKeyFields[i] = names[i].trim();
				}
				logger.info("Db Key fields{} extracted", obj);
			} else {
				logger.error(
						"dbKeyFieldNames should be one field name, or a comma separated list of names. value of {} is not accepted, and db related code not geenrated",
						obj);
			}
		}

		return si;
	}

	void emitJavaAttrs(StringBuilder sbf, String customPackageName) {
		for (String att : ATTS) {
			Object obj = this.settings.get(att);
			if (obj != null) {
				sbf.append(L).append(att).append(" = ").append(Util.escapeObject(obj)).append(";");
			}
		}
		/*
		 * 
		 */
		for (int i = 0; i < PROCS.length; i++) {
			Object obj = this.settings.get(PROCS[i]);
			if (obj != null) {
				sbf.append(L).append("formProcessors[").append(i).append("] = new ");
				sbf.append(customPackageName).append('.').append(obj).append("();");
			}
		}
	}
}
