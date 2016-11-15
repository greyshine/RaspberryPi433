package de.greyshine.webapp.funksteckerrpi;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import de.greyshine.webapp.funksteckerrpi.Switch.Code;
import de.greyshine.webapp.funksteckerrpi.Utils.Kvp;

public class Configuration {
	
	public static final String SERVLET_CONTEXT_KEY = Configuration.class.getCanonicalName();
	
	final Scheduler scheduler;
	
	final File file;
	final JsonObject json;
	
	Map<String,Switch> switches = new LinkedHashMap<>(1);
	Map<String,Switch.Code> codes = new HashMap<>(1);
	public Map<Switch,String> lastState = new LinkedHashMap<>(1);
	
	public Configuration( File inFile ) throws Exception {
		
		scheduler = StdSchedulerFactory.getDefaultScheduler();
		
		
		
		
		file = inFile;
		json = Utils.readJsonObject( inFile );
		
		json.get("switches").getAsJsonObject().entrySet().stream()
		.forEach( e -> {
			
			final Switch s = new Switch(e.getKey(), e.getValue().getAsJsonObject());
			switches.put( s.id , s);
			
			codes.put( s.on.id , s.on);
			codes.put( s.off.id , s.off);
			
		});

		createJobs();
	}
	
	private void createJobs() throws SchedulerException {
		
		
		
	}

	public int getPort() {
		return json.get("port").getAsInt();
	}

	public String getCodeSendCommand() {
		return json.get("codesend").getAsString();
	}

	public int getSendRepeats() {
		
		try {
			
			return Utils.min( 1, json.get("sendrepeats").getAsInt() );
			
		} catch (Exception e) {
			return 10;
		}
	}

	public Kvp<String, String> getCredentials() {
		
		if ( Utils.isBlank( json.get( "user" ).getAsString() ) ) {
			return null;
		}
		
		return new Kvp<>( json.get( "user" ).getAsString(), json.get( "password" ).getAsString() );
	}

	public static Configuration get(HttpServletRequest req) {
		return (Configuration) req.getServletContext().getAttribute( SERVLET_CONTEXT_KEY );
	}

	public Collection<Switch> getSwitches() {
		return switches.values();
	}

	 public Code getCodeForId(String inCodeId) {
		 return codes.get( inCodeId );
	}

	 public JsonObject getStatus() {
		 return getStatus(null, null);
	 }
	public JsonObject getStatus(String inMessage, JsonElement inData) {
		
		JsonObject jo = new JsonObject();
		jo.add( "message" , Utils.trimToNull(inMessage)==null ? JsonNull.INSTANCE : new JsonPrimitive( inMessage ));
		jo.add( "data" , inData == null ? JsonNull.INSTANCE : inData);
		
		return jo;
	}

}
