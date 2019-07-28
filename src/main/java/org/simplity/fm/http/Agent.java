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
package org.simplity.fm.http;

import java.io.IOException;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.simplity.fm.Message;
import org.simplity.fm.service.DefaultContext;
import org.simplity.fm.service.IService;
import org.simplity.fm.service.IserviceContext;
import org.simplity.fm.service.Services;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Agent is the single-point-of-contact to invoke any service on this app.
 * Services are not to be invoked directly (bypassing the Agent) in production.
 * This design provides a simple and clean separation of web and service layer.
 * No code needs to be written for a service in the web layer.
 * 
 * @author simplity.org
 * 
 */
public class Agent {
	private static final Logger logger = LoggerFactory.getLogger(Agent.class);
	private static Agent singleInstance = new Agent();

	/**
	 * 
	 * @return an instance of the agent
	 */
	public static Agent getAgent() {
		return singleInstance;
	}

	/**
	 * TODO: cache manager to be used for session cache. Using a local map for
	 * the time being
	 */
	private Map<String, LoggedInUser> activeUsers = new HashMap<>();

	/**
	 * response for a pre-flight request
	 * 
	 * @param req
	 * 
	 * @param resp
	 */
	public void setOptions(HttpServletRequest req, HttpServletResponse resp) {
		for (int i = 0; i < Http.HDR_NAMES.length; i++) {
			resp.setHeader(Http.HDR_NAMES[i], Http.HDR_TEXTS[i]);
		}
		/*
		 * we have no issue with CORS. We are ready to respond to any client so
		 * long the auth is taken care of
		 */
		resp.setHeader("Access-Control-Allow-Origin", req.getHeader("Origin"));
		/*
		 * we are optimists!!
		 */
		resp.setStatus(Http.STATUS_ALL_OK);
	}

	/**
	 * serve an in-bound request.
	 *
	 * @param req
	 *            http request
	 * @param resp
	 *            http response
	 * @param inputDataIsInPayload
	 * @throws IOException
	 *             IO exception
	 *
	 */
	public void serve(HttpServletRequest req, HttpServletResponse resp, boolean inputDataIsInPayload)
			throws IOException {
		logger.info("Started serving request {}", req.getPathInfo());
		LoggedInUser user = this.getUser(req);
		if (user == null) {
			logger.info("No User. Responding with auth required status");
			resp.setStatus(Http.STATUS_AUTH_REQUIRED);
			return;
		}

		IService service = this.getService(req);
		if (service == null) {
			resp.setStatus(Http.STATUS_INVALID_SERVICE);
			return;
		}

		ObjectNode json = this.readContent(req);
		if(json == null) {
			logger.info("Invalid JSON recd from client ");
			resp.setStatus(Http.STATUS_INVALID_DATA);
			return;
		}
		Map<String, String> fields = this.readQueryString(req);

		/*
		 * We allow the service to use output stream, but not input stream. This
		 * is a safety mechanism against possible measures to be taken when
		 * receiving payload from an external source
		 */
		try (Writer writer = resp.getWriter()) {
			IserviceContext ctx = new DefaultContext(fields, user, writer);
			service.serve(ctx, json);
			if (ctx.allOk()) {
				logger.info("Service returned with All Ok");
			} else {
				Message[] msgs =  ctx.getMessages();
				for (Message msg :msgs) {
					logger.error("Message :" + msg);
				}
				this.respondWithError(resp, msgs, writer);
			}
		} catch (Throwable e) {
			/*
			 * TODO : wire this to error handling process provided by the
			 * configuration
			 */
			resp.setStatus(Http.STATUS_INTERNAL_ERROR);
			return;
		}
	}

	private ObjectNode readContent(HttpServletRequest req) {
		if(req.getContentLength() == 0) {
			return new ObjectMapper().createObjectNode();
		}
		try (Reader reader = req.getReader()) {
			/*
			 * read it as json
			 */
			JsonNode node = new ObjectMapper().readTree(reader);
			if (node.getNodeType() != JsonNodeType.OBJECT) {
				return null;
			}
			return (ObjectNode) node;
		} catch (Exception e) {
			logger.error("Invalid data recd from client {}", e.getMessage());
			return null;
		}
	}
	/**
	 * 
	 * @param resp
	 * @param messages
	 * @param writer
	 */
	private void respondWithError(HttpServletResponse resp, Message[] messages, Writer writer) {
		resp.setStatus(Http.STATUS_INVALID_DATA);
		if (messages == null || messages.length == 0) {
			return;
		}
		try (JsonGenerator gen = new JsonFactory().createGenerator(writer)) {
			gen.writeStartObject();
			gen.writeFieldName("messages");
			gen.writeStartArray();
			for (Message msg : messages) {
				gen.writeStartObject();
				gen.writeStringField("severity", msg.messageType.name());
				gen.writeStringField("messageId", msg.messageId);
				if (msg.fieldName != null) {
					gen.writeStringField("fieldName", msg.fieldName);
				}
				if (msg.columnName != null) {
					gen.writeStringField("columnName", msg.columnName);
				}
				if (msg.params != null) {
					gen.writeStringField("params", msg.params);
				}
				if (msg.rowNumber != 0) {
					gen.writeNumberField("rowNumber", msg.rowNumber);
				}
				gen.writeEndObject();
			}
			gen.writeEndArray();
			gen.writeEndObject();
		} catch (Exception e) {
			//
		}

	}

	private Map<String, String> readQueryString(HttpServletRequest req) {
		Map<String, String> values = new HashMap<>();
		String qry = req.getQueryString();
		if (qry == null) {
			return values;
		}

		for (String part : qry.split("&")) {
			String[] pair = part.split("=");
			String val;
			if (pair.length == 1) {
				val = "";
			} else {
				val = this.decode(pair[1]);
			}
			values.put(pair[0].trim(), val);
		}
		logger.info("{} parameters extracted from query string", values.size());
		return values;
	}

	private IService getService(HttpServletRequest req) {
		String serviceName = req.getHeader(Http.SERVICE_HEADER);
		if (serviceName == null) {
			logger.info("header {} not received", Http.SERVICE_HEADER);

			return null;
		}
		IService service = Services.getService(serviceName);
		if (service == null) {
			logger.info("{} is not a service", serviceName);
		}
		return service;
	}

	/**
	 * temp method in the absence of real authentication and session. We use
	 * AUthorization token as userId as well
	 * 
	 * @param req
	 * @return
	 */
	private LoggedInUser getUser(HttpServletRequest req) {
		String token = req.getHeader(Http.TOKEN_HEADER);
		if (token == null) {
			return null;
		}
		
		LoggedInUser user = this.activeUsers.get(token);
		if (user == null) {
			/*
			 * we assume that the token is valid when we get called. Hence we
			 * have to create a user. token is used as userId, there by allowing
			 * testing with different users
			 */
			CustomUser cu = new CustomUser(token, token);
			if(cu.getFirstName() == null) {
				logger.error("{} is not a valid user of this application.", token);
				return null;
			}
			logger.info("USer {} successfully logged-in", token);
			this.activeUsers.put(token, cu);
			user = cu;
		}
		return user;
	}

	private String decode(String text) {
		try {
			return URLDecoder.decode(text, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			/*
			 * we do know that this is supported. so, this is unreachable code.
			 */
			return text;
		}
	}
}
