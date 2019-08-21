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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.simplity.fm.Forms;
import org.simplity.fm.Message;
import org.simplity.fm.form.DbMetaData;
import org.simplity.fm.form.DbOperation;
import org.simplity.fm.form.Form;
import org.simplity.fm.form.FormData;
import org.simplity.fm.rdb.DbHandle;
import org.simplity.fm.rdb.IDbClient;
import org.simplity.fm.rdb.RdbDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * service for a form based I/O operation from DB
 * 
 * @author simplity.org
 *
 */
public abstract class FormIo implements IService {
	protected static final Logger logger = LoggerFactory.getLogger(FormIo.class);

	/**
	 * 
	 * @param opern
	 * @param formName
	 * @return non-null instance
	 */
	public static FormIo getInstance(DbOperation opern, String formName) {
		Form form = Forms.getForm(formName);
		if (form == null) {
			logger.error("No form named {}.", formName);
			return null;
		}
		DbMetaData meta = form.getDbMetaData();
		if (meta == null) {
			logger.error("Form {} is not designed for any db operation.", formName);
			return null;
		}

		if (meta.dbOperationOk[opern.ordinal()] == false) {
			logger.error("Form {} is not designed for db operation.", formName, opern);
			return null;
		}

		switch (opern) {
		case CREATE:
			return new FormInserter(form);

		case DELETE:
			return new FormDeleter(form);

		case FILTER:
			logger.error("Filter operation not yet ready.");
			return null;

		case GET:
			return new FormReader(form);

		case UPDATE:
			return new FormUpdater(form);

		default:
			logger.error("Form operation {} not yet implemented", opern);
			return null;
		}
	}

	protected static class FormReader extends FormIo {
		private final Form form;

		protected FormReader(Form form) {
			this.form = form;
		}

		@Override
		public void serve(IserviceContext ctx, ObjectNode payload) throws Exception {
			FormData fd = this.form.newFormData();
			List<Message> msgs = new ArrayList<>();
			fd.loadKeys(ctx.getInputFields(), msgs);
			if (msgs.size() > 0) {
				ctx.AddMessages(msgs);
				return;
			}
			if (fd.fetchFromDb() == false) {
				logger.error("No data found for the form {}", this.form.getFormId());
				ctx.AddMessage(Message.newError("noData"));
			} else {
				fd.serializeAsJson(ctx.getResponseWriter());
			}
			return;
		}
	}

	protected static class FormUpdater extends FormIo {
		private final Form form;

		protected FormUpdater(Form form) {
			this.form = form;
		}

		@Override
		public void serve(IserviceContext ctx, ObjectNode payload) throws Exception {
			FormData fd = this.form.newFormData();
			List<Message> msgs = new ArrayList<>();
			fd.validateAndLoad(payload, false, false, msgs);
			if (msgs.size() > 0) {
				ctx.AddMessages(msgs);
				return;
			}
			RdbDriver.getDriver().transact(new IDbClient() {

				@Override
				public boolean transact(DbHandle handle) throws SQLException {
					handle.update(fd);
					return true;
				}
			}, false);
			return;
		}
	}

	protected static class FormInserter extends FormIo {
		private final Form form;

		protected FormInserter(Form form) {
			this.form = form;
		}

		@Override
		public void serve(IserviceContext ctx, ObjectNode payload) throws Exception {
			FormData fd = this.form.newFormData();
			List<Message> msgs = new ArrayList<>();
			fd.validateAndLoad(payload, false, true, msgs);
			if (msgs.size() > 0) {
				ctx.AddMessages(msgs);
				return;
			}
			RdbDriver.getDriver().transact(new IDbClient() {

				@Override
				public boolean transact(DbHandle handle) throws SQLException {
					handle.insert(fd);
					return true;
				}
			}, false);
			return;
		}
	}

	protected static class FormDeleter extends FormIo {
		private final Form form;

		protected FormDeleter(Form form) {
			this.form = form;
		}

		@Override
		public void serve(IserviceContext ctx, ObjectNode payload) throws Exception {
			FormData fd = this.form.newFormData();
			List<Message> msgs = new ArrayList<>();
			fd.loadKeys(ctx.getInputFields(), msgs);
			if (msgs.size() > 0) {
				ctx.AddMessages(msgs);
				return;
			}
			RdbDriver.getDriver().transact(new IDbClient() {

				@Override
				public boolean transact(DbHandle handle) throws SQLException {
					handle.delete(fd);
					return true;
				}
			}, false);
			return;
		}
	}
}
