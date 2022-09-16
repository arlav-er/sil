package it.eng.myportal.beans;

import java.util.HashMap;

public class RestoreParameters extends HashMap<String, Object> {

	
	private static final long serialVersionUID = -434153878081559616L;

	public String getToken() {		
		return (String) this.get("token");
	}

}
