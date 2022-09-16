package it.eng.myportal.auth;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Singleton contenente i dati di accesso da Google
 * 
 * @author Rodi A.
 * 
 */
public final class GoogleAppSingleton extends HashMap<String, Map<String, ? extends Object>> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3705743854521397992L;

	protected static Log log = LogFactory.getLog(GoogleAppSingleton.class);

	private static GoogleAppSingleton instance = new GoogleAppSingleton();

	public static GoogleAppSingleton getInstance() {
		return instance;
	}

	private GoogleAppSingleton() {
	}

}
