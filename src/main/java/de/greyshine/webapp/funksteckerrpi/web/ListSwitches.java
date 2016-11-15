package de.greyshine.webapp.funksteckerrpi.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonObject;

import de.greyshine.webapp.funksteckerrpi.Configuration;
import de.greyshine.webapp.funksteckerrpi.HttpUtils;
import de.greyshine.webapp.funksteckerrpi.Switch;

@WebServlet( urlPatterns={"/switches"} )
public class ListSwitches extends HttpServlet {

	private static final long serialVersionUID = -289036088549779554L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		final Configuration c = (Configuration) req.getServletContext().getAttribute( Configuration.SERVLET_CONTEXT_KEY );
		
		JsonObject jo  = new JsonObject();
		
		for( Switch s : c.getSwitches() ) {
			
			JsonObject sjo = new JsonObject();
			jo.add( s.id, sjo);
			sjo.addProperty( "id" , s.id);
			sjo.addProperty( "name" , s.name);
			sjo.addProperty( "code-1" , s.on.code);
			sjo.addProperty( "code-1-id" , s.on.id);
			sjo.addProperty( "code-0" , s.off.code);
			sjo.addProperty( "code-0-id" , s.off.id);
		}
		
		HttpUtils.respond(resp,200, jo);
	}
	
}
