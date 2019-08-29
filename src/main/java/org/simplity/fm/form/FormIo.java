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

package org.simplity.fm.form;

import java.io.Writer;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.simplity.fm.Conventions;
import org.simplity.fm.Forms;
import org.simplity.fm.Message;
import org.simplity.fm.rdb.DbHandle;
import org.simplity.fm.rdb.IDbClient;
import org.simplity.fm.rdb.RdbDriver;
import org.simplity.fm.service.IService;
import org.simplity.fm.service.IserviceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
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
			return new FormFilter(form);

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
			fd.loadKeys(ctx.getInputFields(), ctx);
			if (!ctx.allOk()) {
				return;
			}
			Field tenant = this.form.dbMetaData.tenantField;
			if(tenant != null) {
				fd.setObject(tenant.getIndex(), ctx.getTenantId());
			}

			if (fd.fetchFromDb() == false) {
				logger.error("No data found for the form {}", this.form.getFormId());
				ctx.addMessage(Message.newError("noData"));
			} else {
				fd.serializeAsJson(ctx.getResponseWriter());
			}
			return;
		}
	}

	protected static class FormFilter extends FormIo {
		private final Form form;

		protected FormFilter(Form form) {
			this.form = form;
		}

		@Override
		public void serve(IserviceContext ctx, ObjectNode payload) throws Exception {
			List<Message> msgs = new ArrayList<>();
			ObjectNode conditions = null;
			JsonNode node = payload.get(Conventions.Http.TAG_CONDITIONS);
			if (node != null && node.getNodeType() == JsonNodeType.OBJECT) {
				conditions = (ObjectNode) node;
			} else {
				ctx.addMessage(Message.newError(Message.MSG_INVALID_DATA));
				return;
			}

			int nbrRows = Conventions.Http.DEFAULT_NBR_ROWS;
			node = payload.get(Conventions.Http.TAG_NBR_ROWS);
			if (node != null && node.getNodeType() == JsonNodeType.NUMBER) {
				nbrRows = node.asInt(nbrRows);
			}

			SqlReader reader = this.form.parseForFilter(conditions, msgs, ctx);

			if (msgs.size() > 0) {
				ctx.addMessages(msgs);
				return;
			}

			if (reader == null) {
				logger.error("DESIGN ERROR: form.parseForFilter() returned null, but failed to put ay error message. ");
				ctx.addMessage(Message.newError(Message.MSG_INTERNAL_ERROR));
				return;
			}

			@SuppressWarnings("resource")
			Writer writer = ctx.getResponseWriter();
			if (nbrRows == 1) {
				FormData data = reader.read();
				data.serializeAsJson(writer);
			} else {
				FormData[] rows = reader.filter();
				if (rows == null) {
					logger.info("No data found for the form {}", this.form.getFormId());
					rows = new FormData[0];
				}

				writer.write("{\"");
				writer.write(Conventions.Http.TAG_LIST);
				writer.write("\":[");
				boolean firstOne = true;
				for (FormData fd : rows) {
					if (firstOne) {
						firstOne = false;
					} else {
						writer.write(',');
					}
					fd.serializeAsJson(writer);
				}
				writer.write("]}");
			}
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
			fd.validateAndLoad(payload, false, false, ctx);
			if (ctx.allOk() == false) {
				return;
			}
			Field tenant = this.form.dbMetaData.tenantField;
			if(tenant != null) {
				fd.setObject(tenant.getIndex(), ctx.getTenantId());
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
			fd.validateAndLoad(payload, false, true, ctx);
			if (!ctx.allOk()) {
				return;
			}
			Field tenant = this.form.dbMetaData.tenantField;
			if(tenant != null) {
				fd.setObject(tenant.getIndex(), ctx.getTenantId());
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
			fd.loadKeys(ctx.getInputFields(), ctx);
			if (!ctx.allOk()) {
				return;
			}
			Field tenant = this.form.dbMetaData.tenantField;
			if(tenant != null) {
				fd.setObject(tenant.getIndex(), ctx.getTenantId());
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
