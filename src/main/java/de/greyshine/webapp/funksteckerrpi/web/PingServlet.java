
package de.greyshine.webapp.funksteckerrpi.web;

import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonPrimitive;

import de.greyshine.webapp.funksteckerrpi.Configuration;
import de.greyshine.webapp.funksteckerrpi.HttpUtils;

@WebServlet( urlPatterns={"/ping"} )
public class PingServlet extends HttpServlet {

	private static final long serialVersionUID = 2095300379882168299L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		HttpUtils.respond(resp, 200, Configuration.get(req).getStatus( "pong", new JsonPrimitive( new Date().toString() ) ) );
	}
	
	

}
