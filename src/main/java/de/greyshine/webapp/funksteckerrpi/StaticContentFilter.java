package de.greyshine.webapp.funksteckerrpi;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;

@WebFilter( urlPatterns={"/*"} )
public class StaticContentFilter implements Filter {

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		
		final HttpServletRequest hsr = (HttpServletRequest) request;
		
		String uri = hsr.getRequestURI();
		uri = "/".equals( uri ) ? "/index.html" : uri;
		
		final InputStream theIs = Utils.getResource( "webapp"+uri );
		//System.out.println( uri + " / "+ theIs +" / "+ theIs.available() );
		
		if ( theIs.available() > 0) {
			
			HttpUtils.respond(response, theIs);
			return;
		}
		
		chain.doFilter(request, response);
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

	
	
	
}
