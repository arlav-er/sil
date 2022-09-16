package it.eng.myportal.auth;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Singleton contenente i dati di accesso da Twitter
 * 
 * @author Rodi A.
 * 
 */
public final class TwitterAppSingleton extends HashMap<String, Map<String, ? extends Object>> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3705743854521397992L;

	protected static Log log = LogFactory.getLog(TwitterAppSingleton.class);

	private static TwitterAppSingleton instance = new TwitterAppSingleton();

	public static TwitterAppSingleton getInstance() {
		return instance;
	}

	private TwitterAppSingleton() {
	}

}
