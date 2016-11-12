package de.greyshine.webapp.funksteckerrpi;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;

import de.greyshine.webapp.funksteckerrpi.Utils.Kvp;

public abstract class HttpUtils {
	
	private static final Gson GSON_OUT = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

	public static void respond( HttpServletResponse resp, int status, JsonElement inJe ) {
		
		
		try {
			
			resp.setHeader("Content-Type", "application/json");
			resp.setStatus( status );

			final OutputStream os = resp.getOutputStream();
		
			os.write( GSON_OUT.toJson( inJe == null ? JsonNull.INSTANCE : inJe ).getBytes() );
			
			Utils.flush( os );
			
		} catch (Exception e) {
			
			throw Utils.toRuntimeException( e );
		}
	}
	
	public static Kvp<String,String> evaluateUserPassword(HttpServletRequest inReq) {
		
		String theAuthorisationHeader = inReq.getHeader("Authorization");
		
		if (theAuthorisationHeader == null || !theAuthorisationHeader.toUpperCase().startsWith("BASIC ")) {
			return new Kvp<String,String>();
		}
		
		String theUserAndPassword = inReq.getHeader("Authorization").substring(5).trim();
		theUserAndPassword = new String(java.util.Base64.getDecoder().decode(theUserAndPassword));
		final int idxColon = theUserAndPassword.indexOf(':');

		final String theUserCandidate = idxColon < 1 ? null
				: Utils.trimToNull(theUserAndPassword.substring(0, idxColon));
		final String thePassword = theUserCandidate == null ? null
				: Utils.trimToEmpty(theUserAndPassword.substring(idxColon+1));
		
		return new Kvp<String,String>( theUserCandidate, thePassword );
	}

	public static void respondResource(ServletResponse response, int inStatus, String resource) {
		
		((HttpServletResponse)response).setStatus(inStatus);
		
		try {
		
			Utils.copy( Utils.getResource( resource ), response.getOutputStream(), true, false );
		
		} catch (IOException e) {
			throw Utils.toRuntimeException( e );
		}
	}

	public static void respond(ServletResponse response, InputStream is) {
		
		((HttpServletResponse)response).setStatus(200);
		
		try {
			
			Utils.copy( is == null ? Utils.INPUTSTREAM_EOF : is, response.getOutputStream(), true, false );
			
		} catch (IOException e) {
			throw Utils.toRuntimeException( e );
		}
	}
	
}
