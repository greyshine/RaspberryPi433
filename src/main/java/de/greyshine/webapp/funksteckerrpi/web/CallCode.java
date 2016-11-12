package de.greyshine.webapp.funksteckerrpi.web;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonPrimitive;

import de.greyshine.webapp.funksteckerrpi.Configuration;
import de.greyshine.webapp.funksteckerrpi.HttpUtils;
import de.greyshine.webapp.funksteckerrpi.Utils;

@WebServlet( urlPatterns={"/code/*"} )
public class CallCode extends HttpServlet {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2971759066023723617L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPut(req, resp);
	}
	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		final Configuration c = Configuration.get( req );
		
		// substring due to "/code/".length() = 6
		String theCode = req.getRequestURI().substring( 6 );
		
		theCode = c.getCodeForId( theCode );
		
		if ( theCode == null ) {
			
			HttpUtils.respond(resp, 404, c.getStatus());
			return;
		}
		
		final String theCommand = getCommand();
		final File theCommandDir = getCommandDir();
		
		final ByteArrayOutputStream sout = new ByteArrayOutputStream();
		final ByteArrayOutputStream serr = new ByteArrayOutputStream();
		
		final int repeats = c.getSendRepeats(); 
		
		// synchronize so two calls for example synchronous on and off will be sequentelly worked off
		synchronized ( CallCode.class ) {
			
			for( int i=0, l=repeats; i < l; i++ ) {
				
				Utils.console(null, sout, serr, theCommandDir, "./"+ theCommand, theCode);
				
				if ( i < l-1 ) {
					
					final long s = System.currentTimeMillis();
					
					//System.out.println( "WAIT: "+ (i+1) );
					Utils.waitMillis( 30 );
					//System.out.println( "WAIT done: "+ (i+1) +" :: "+ ( System.currentTimeMillis()-s ) );
				};
			}
		
		}
		
		if ( serr.toByteArray().length != 0 ) {
		
			System.err.println( new String(serr.toByteArray()) );
		
		} else {
			
			System.out.println( "> handled: "+ theCode +"; repeats="+ repeats );
		}
		
		HttpUtils.respond( resp , 200, new JsonPrimitive( theCode ));
	}



	private String getCommand() {
		
		final Configuration c = (Configuration) getServletContext().getAttribute( Configuration.SERVLET_CONTEXT_KEY );
		return new File( c.getCodeSendCommand() ).getName();
	}
	private File getCommandDir() {
		final Configuration c = (Configuration) getServletContext().getAttribute( Configuration.SERVLET_CONTEXT_KEY );
		return new File( c.getCodeSendCommand() ).getParentFile();
	}

	
	
}
