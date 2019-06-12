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
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.simplity.fm.ApplicationError;
import org.simplity.fm.Message;
import org.simplity.fm.service.IService;
import org.simplity.fm.service.ServiceResult;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;

import example.project.service.Services;

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

	private static Agent singleInstance = new Agent();

	/**
	 * 
	 * @return an instance of the agent
	 */
	public static Agent getAgent() {
		return singleInstance;
	}

	private static final String AUTH_HEADER = "Authorization";
	private static final String[] HDR_NAMES = { "Access-Control-Allow-Methods", "Access-Control-Allow-Headers",
			"Access-Control-Max-Age", "Access-Control-Allow-Origin", "Connection", "Cache-Control", "Expires" };
	private static final String[] HDR_TEXTS = { "POST, GET, OPTIONS", "authorization,content-type", "1728000", "*",
			"Keep-Alive", "no-cache, no-store, must-revalidate", "0" };
	private static final int STATUS_ALL_OK = 200;
	private static final int STATUS_AUTH_REQUIRED = 401;
	private static final int STATUS_INVALID_SERVICE = 404;
	// private static final int STATUS_METHOD_NOT_ALLOWED = 405;
	private static final int STATUS_INVALID_DATA = 406;
	private static final int STATUS_INTERNAL_ERROR = 500;
	private static final String SERVICE_NAME = "_serviceName";
	/**
	 * TODO: cache manager to be used for session cache. Using a local map for
	 * the time being
	 */
	private Map<String, LoggedInUser> activeUsers = new HashMap<>();

	/**
	 * response for a pre-flight request
	 * 
	 * @param resp
	 */
	public void setOptions(HttpServletResponse resp) {
		for (int i = 0; i < HDR_NAMES.length; i++) {
			resp.setHeader(HDR_NAMES[i], HDR_TEXTS[i]);
		}
		resp.setStatus(STATUS_ALL_OK);
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

		LoggedInUser user = this.getUser(req);
		if (user == null) {
			resp.setStatus(STATUS_AUTH_REQUIRED);
			return;
		}

		IService service = this.getService(req);
		if (service == null) {
			resp.setStatus(STATUS_INVALID_SERVICE);
			return;
		}

		/*
		 * TODO: we have not yet designed our data strategy for services. Do all
		 * the data required for a service come from client, or do we save some
		 * of them in the session-cache on the server?
		 * 
		 * As of now, we assume that the data comes from client.
		 * 
		 * We will have to re-design our code to populate inData partly from
		 * client and partly from session-cache
		 */
		ObjectNode json = null;
		Map<String, String> fields = null;
		if (inputDataIsInPayload) {
			try (InputStream ins = req.getInputStream()) {
				/*
				 * read it as json
				 */
				JsonNode node = new ObjectMapper().readTree(ins);
				if (node.getNodeType() != JsonNodeType.OBJECT) {
					resp.setStatus(STATUS_INVALID_DATA);
					return;
				}
				json = (ObjectNode) node;
			} catch (Exception e) {
				resp.setStatus(STATUS_INVALID_DATA);
				return;
			}
		} else {
			fields = this.readQueryString(req);
		}

		/*
		 * finally.... call the service
		 * We allow the service to use output stream, but not input stream. This
		 * is a safety mechanism against possible measures to be taken when
		 * receiving payload from an external source
		 */
		try (Writer writer = resp.getWriter()) {
			ServiceResult result = null;
			if (fields != null) {
				result = service.serve(user, fields, writer);
			} else {
				result = service.serve(user, json, writer);
			}
			if (result.allOk) {
				this.setHeaders(resp);
			} else {
				this.respondWithError(resp, result.messages, writer);
			}
		} catch (ApplicationError a) {
			resp.setStatus(STATUS_INTERNAL_ERROR);
			return;
		}
	}

	/**
	 * 
	 * @param resp
	 * @param messages
	 * @param writer
	 */
	private void respondWithError(HttpServletResponse resp, Message[] messages, Writer writer) {
		// TODO to send messages as pay-load
		resp.setStatus(STATUS_INVALID_DATA);
	}

	/**
	 * 
	 * @param resp
	 */
	private void setHeaders(HttpServletResponse resp) {
		resp.setStatus(STATUS_ALL_OK);
		for (int i = 0; i < HDR_NAMES.length; i++) {
			resp.setHeader(HDR_NAMES[i], HDR_TEXTS[i]);
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
		return values;
	}

	private IService getService(HttpServletRequest req) {
		String serviceName = req.getHeader(SERVICE_NAME);
		if (serviceName == null) {
			return null;
		}
		return Services.getService(serviceName);
	}

	private LoggedInUser getUser(HttpServletRequest req) {
		String token = req.getHeader(AUTH_HEADER);
		if (token == null) {
			return null;
		}
		LoggedInUser user = this.activeUsers.get(token);
		if (user == null) {
			/*
			 * we assume that the token is valid when we get called. Hence we
			 * have to create a user!!
			 */
			user = LoggedInUser.newUser("dummyPan", token);
			this.activeUsers.put(token, user);
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
