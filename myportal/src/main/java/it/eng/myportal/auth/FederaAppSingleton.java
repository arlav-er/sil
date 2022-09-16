package it.eng.myportal.auth;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Singleton contenente i dati di accesso da fedERa quando l'utene esegue il
 * login. Ho cambiato da EJB a Classe semplice in quanto non trovavo i dati in
 * CompletamentoDatiBean: ci ho messo una giornata poi ho scoperto che venivano
 * rimossi esplicitamente. Mi scuso con Franco per aver manomesso così tanto il
 * suo codice che era buono ma prima di tornare alla versione precedente mi
 * chiedo: va bene anche così? Almeno ci risparmiamo le lookup e le
 * NamingException.
 * 
 * @author Rodi A.
 * 
 */
public class FederaAppSingleton extends HashMap<String, Map<String, String>> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3705743854521397992L;

	protected static Log log = LogFactory.getLog(FederaAppSingleton.class);

	private static FederaAppSingleton instance = new FederaAppSingleton();

	public static FederaAppSingleton getInstance() {
		return instance;
	}

	private FederaAppSingleton() {
	}

}
