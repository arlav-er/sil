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
public final class FacebookAppSingleton extends HashMap<String, Map<String, ? extends Object>> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3705743854521397992L;

	protected static Log log = LogFactory.getLog(FacebookAppSingleton.class);

	private static FacebookAppSingleton instance = new FacebookAppSingleton();

	public static FacebookAppSingleton getInstance() {
		return instance;
	}

	private FacebookAppSingleton() {
	}

}
