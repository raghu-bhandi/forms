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

package example.project.service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

import org.simplity.fm.Forms;
import org.simplity.fm.Message;
import org.simplity.fm.form.Form;
import org.simplity.fm.form.FormData;
import org.simplity.fm.rdb.DbHandle;
import org.simplity.fm.rdb.IDbClient;
import org.simplity.fm.rdb.IDbReader;
import org.simplity.fm.rdb.RdbDriver;
import org.simplity.fm.service.IService;
import org.simplity.fm.service.IserviceContext;

import com.fasterxml.jackson.databind.node.ObjectNode;

import example.project.gen.form.Form1;
import example.project.gen.form.Form2;

/**
 * @author simplity.org
 *
 */
public class ModifyForm1 implements IService {

	@Override
	public void serve(IserviceContext ctx, ObjectNode inputPayload) throws Exception {
		Form form = Forms.getForm("form1");
		FormData fd = form.newFormData();
		fd.validateAndLoad(inputPayload, false, false, ctx);
		if (ctx.allOk() == false) {
			return;
		}
		/*
		 * if we want to do any more validations, we can do that here.
		 * This validation is already done by the form, but we are writing code
		 * just to demonstrate this aspect
		 */
		LocalDate d1 = fd.getDateValue(Form1.fromDate);
		LocalDate d2 = fd.getDateValue(Form1.toDate);
		if (d1.isAfter(d2)) {
			ctx.addMessage(Message.newFieldError("fromDate", "invalidFromTo", d1.toString() + ',' + d2.toString()));
			return;
		}

		/*
		 * if you want to modify some data in the form.
		 * Note that we use long for all integral and double for all decimal
		 * numbers
		 */
		fd.setLongValue(Form1.intField1, 101L);

		FormData fd2 = Forms.getForm("form2").newFormData();
		/**
		 * db operations must be in a transactions.
		 */
		RdbDriver.getDriver().transact(new IDbClient() {

			@Override
			public boolean transact(DbHandle handle) throws SQLException {
				handle.update(fd);
				/*
				 * do any other db operations you may want to do before or
				 * after...
				 */
				handle.read(new IDbReader() {

					@Override
					public String getPreparedStatement() {
						/*
						 * return "select a, b from bla...bla....";
						 * if you return null, the operation is abandoned..
						 */
						return null;
					}

					@Override
					public void setParams(PreparedStatement ps) throws SQLException {
						ps.setBoolean(1, true);
						ps.setString(2, "some text value");
					}

					@Override
					public boolean readARow(ResultSet rs) throws SQLException {
						String a1 = rs.getString(1);
						System.out.println("value of a1 " + a1);
						/*
						 * return false will stop the reading process. true will
						 * read the next one, and if exists, this method is
						 * called again
						 */
						return false;
					}

				});

				/*
				 * may be you want to read into another form
				 */
				//set key field
				fd2.setLongValue(Form2.headerId, fd.getLongValue(Form1.customerId));
				fd2.fetchFromDb();
				return true;
				/*
				 * no need to catch any sql exception. transact() handles the
				 * sqlException, rolls-back the transaction, and throws a new
				 * exception. this new exception is caught by the Agent, and a
				 * suitable response is sent back to the client
				 */
			}
		}, false);
		/*
		 * what do you want to send back? may be form2
		 */
		fd2.serializeAsJson(ctx.getResponseWriter());
		/*
		 * you are done with your service...
		 */
	}

}
