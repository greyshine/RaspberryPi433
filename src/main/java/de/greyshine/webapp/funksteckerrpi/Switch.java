package de.greyshine.webapp.funksteckerrpi;

import java.util.UUID;

import com.google.gson.JsonObject;

public class Switch {
	
	public Switch(String key, JsonObject inJo) {
		
		name = key;
		on = new Code( inJo.get( "code-1" ).getAsInt() );
		off = new Code( inJo.get( "code-0" ).getAsInt() );
	}

	private static int ids = 0; 
	
	final String id = ""+ids++;
	
	public final String name; 
	
	public final Code on;
	public final Code off;
	
	public Code lastState;
	
	public class Code {
		
		public Code(int code) {
			this.code = code;
		}
		public String id = Switch.this.id +"-"+UUID.randomUUID();
		public int code;
	}
	
	
	
}
