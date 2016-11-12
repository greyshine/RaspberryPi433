package de.greyshine.webapp.funksteckerrpi;

import java.io.File;
import java.util.Arrays;

import org.eclipse.jetty.annotations.AnnotationConfiguration;
import org.eclipse.jetty.plus.webapp.EnvConfiguration;
import org.eclipse.jetty.plus.webapp.PlusConfiguration;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.webapp.FragmentConfiguration;
import org.eclipse.jetty.webapp.JettyWebXmlConfiguration;
import org.eclipse.jetty.webapp.Configuration;
import org.eclipse.jetty.webapp.MetaInfConfiguration;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.webapp.WebInfConfiguration;
import org.eclipse.jetty.webapp.WebXmlConfiguration;

public class ServerMain {

	public static void main(String[] args) throws Exception {
		
		final de.greyshine.webapp.funksteckerrpi.Configuration theConfiguration = new de.greyshine.webapp.funksteckerrpi.Configuration( args.length < 1 ? Utils.toCanonicalFile( new File("./config.json") ) : new File(args[0]) ); 

		final Server theServer = new Server( theConfiguration.getPort() );

		try {

			WebAppContext context = new WebAppContext();
			context.getServletContext().setAttribute(de.greyshine.webapp.funksteckerrpi.Configuration.SERVLET_CONTEXT_KEY, theConfiguration);
			context.setBaseResource(Resource.newResource(new File("./web")));
			context.setConfigurations(new Configuration[] { new AnnotationConfiguration(), new WebInfConfiguration(),
					new WebXmlConfiguration(), new MetaInfConfiguration(), new FragmentConfiguration(),
					new EnvConfiguration(), new PlusConfiguration(), new JettyWebXmlConfiguration() });

			context.getMetaData().setWebInfClassesDirs(Arrays.asList(
					// Resource.newResource( "target/classes" ),
					Resource.newResource(ServerMain.class.getProtectionDomain().getCodeSource().getLocation())));

			context.setContextPath("/");
			context.setParentLoaderPriority(true);
			// context.addServlet( PingServlet.class , "/ping");

			theServer.setHandler(context);
			theServer.setStopAtShutdown(true);
			theServer.start();
			
			System.out.println( "listening on: "+ theConfiguration.getPort() );
			
			theServer.join();

		} catch (Exception e) {

			theServer.stop();
			theServer.destroy();

			throw e;
		}

	}
	
	

}
