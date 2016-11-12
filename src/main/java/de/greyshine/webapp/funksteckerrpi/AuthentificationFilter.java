package de.greyshine.webapp.funksteckerrpi;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.greyshine.webapp.funksteckerrpi.Utils.Kvp;

@WebFilter( urlPatterns={"/*"} )
public class AuthentificationFilter implements Filter {

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		
		final HttpServletRequest req = (HttpServletRequest) request;
		final HttpServletResponse res = (HttpServletResponse) response;
		
		if ( !isAuthentificated(req) ) {
			res.addHeader( "WWW-Authenticate" , "BASIC realm=\"Go away, honk!\"");
			res.sendError( HttpServletResponse.SC_UNAUTHORIZED );
			return;
		}
		
		chain.doFilter(request, response);
		
	}

	private boolean isAuthentificated(HttpServletRequest req) {
		
		final Configuration cfg = (Configuration) req.getServletContext().getAttribute( Configuration.SERVLET_CONTEXT_KEY );
		
		Kvp<String,String> credentials = cfg.getCredentials();
		
		if ( credentials == null || Utils.isBlank( credentials.key ) ) { return true; }
		
		final Kvp<String,String> kvp = HttpUtils.evaluateUserPassword(req);
		
		return credentials.equals( kvp );
	}
	
	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

}
