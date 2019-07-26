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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;

/**
 * serves as the main class as well as the handler
 * 
 * @author simplity.org
 *
 */
public class JettyHandler extends AbstractHandler {
	private static final int STATUS_METHOD_NOT_ALLOWED = 405;

	/*
	 * TODO : to research about use of baseREquest and request objects
	 */
	@Override
	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		baseRequest.setHandled(true);
		Agent agent = Agent.getAgent();
		System.out.println("Received a request for context method = " + baseRequest.getMethod() + " and pathInfo = " + baseRequest.getPathInfo());
		String method = baseRequest.getMethod().toUpperCase();
		if(method.equals("POST") || method.equals("GET")) {
			agent.serve(baseRequest, response, true);
			return;
		}
		if(method.equals("OPTIONS")) {
			agent.setOptions(baseRequest, response);
			return;
		}
		response.setStatus(STATUS_METHOD_NOT_ALLOWED);
	}

	/**
	 * start jetty server on port 8080. To be extended to get run-time parameter
	 * for port, and error handling if port is in-use etc..
	 * <br/>
	 * Simply invoke this as java app to run the server (of course the class
	 * path etc.. are to be taken care of)
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		Server server = new Server(8080);
		server.setHandler(new JettyHandler());

		server.start();
		server.join();
	}

}
