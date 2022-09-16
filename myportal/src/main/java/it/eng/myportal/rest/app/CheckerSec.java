package it.eng.myportal.rest.app;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;

import it.eng.myportal.utils.ConstantsSingleton;
import it.eng.myportal.utils.Utils;

public class CheckerSec {
	protected static Log log = LogFactory.getLog(CheckerSec.class);

	public static final String OK = "OK";
	public static final String KO = "ko";
	public static final String ERROR = "error";
	public static final String STATUS = "status";

	public static final String ERRORE_DI_SISTEMA = "Errore nell'elaborazione del percorso lavoratore";
	public static final String NON_ABILITATO_AI_SERVIZI_AMMINISTRATIVI = "Non abilitato ai servizi amministrativi";
	public static final String TOKEN_NON_VALIDO_O_SCADUTO = "Token/utente non valido o scaduto";
//	public static final String NON_CITTADINO = "L'utente non è un cittadino";

	private static final String TOKEN_SECURITY_NULLO = "Token security nullo";
	private static final String TOKEN_SECURITY_ERRATO = "Token security errato";
	private static final String PARAMETRI_ERRATI = "parametri errati";
	private static final String ERRORE_INTERNO = "Errore interno";

	private StatusEnum status = StatusEnum.NOT_DEFINED;

	/**
	 * Costruttore con chiave dell'App
	 * 
	 * @param tokenSecurity
	 * @param uri
	 */
	public CheckerSec(String tokenSecurity, String uri) {
		this(tokenSecurity, uri, ConstantsSingleton.App.KEY, true /* useUTC */);
	}

	/**
	 * Costruttore con possibilità di indicare la chiave da utilizzare
	 * 
	 * @param tokenSecurity
	 * @param uri
	 * @param key
	 * @param useUTC
	 */
	public CheckerSec(String tokenSecurity, String uri, String key, boolean useUTC) {
		try {
			String completeUrl = "/" + ConstantsSingleton.CONTESTO_APP + uri;
			if (tokenSecurity != null && !("").equalsIgnoreCase(tokenSecurity)) {
				if (Utils.isTokenSecurityCorrect(tokenSecurity, completeUrl, key, useUTC)) {
					status = StatusEnum.OK;
				} else {
					status = StatusEnum.KO;
				}
			} else {
				status = StatusEnum.NULL;
			}
		} catch (Exception e) {
			status = StatusEnum.FATAL;
		}

	}

	public boolean isOk() {

		if (status == StatusEnum.OK)
			return true;
		return false;

	}

	public JSONObject renderTokenSec() {

		switch (status) {
		case KO:
			return renderWrongTokenSec();
		case NULL:
			return renderNullTokenSec();
		case FATAL:
			return renderInternalErrorTokenSec();

		default:
			throw new RuntimeException("STATO NON DEFINITO");
		}

	}

	private JSONObject renderWrongTokenSec() {
		// token errato
		log.error("Errore nell'autenticazione token security errato.");
		JSONObject obj = new JSONObject();
		try {
			obj.put(STATUS, KO);
			obj.put(ERROR, TOKEN_SECURITY_ERRATO);
		} catch (JSONException e1) {
			log.error(e1);
		}
		return obj;
	}

	private JSONObject renderNullTokenSec() {
		// token nullo
		log.error("Errore nell'autenticazione token security nullo.");
		JSONObject obj = new JSONObject();
		try {
			obj.put(STATUS, KO);
			obj.put(ERROR, TOKEN_SECURITY_NULLO);
		} catch (JSONException e1) {
			log.error(e1);
		}
		return obj;
	}

	private JSONObject renderInternalErrorTokenSec() {
	//	log.debug("Errore recupero");
		JSONObject obj = new JSONObject();
		try {
			obj.put(STATUS, KO);
			obj.put(ERROR, ERRORE_INTERNO);
		} catch (JSONException e1) {
			log.error(e1.getMessage());
		}
		return obj;
	}

	public JSONObject renderWrongParameters() {
	//	log.debug("Errore recupero");

		JSONObject obj = new JSONObject();
		try {
			obj.put(STATUS, KO);
			obj.put(ERROR, PARAMETRI_ERRATI);
		} catch (JSONException e1) {
			log.error(e1.getMessage());
		}
		return obj;
	}

}

/**
 * Enumerazione interna alla classe
 * 
 * @author vuoto
 *
 */

enum StatusEnum {
OK, KO, NULL, FATAL, NOT_DEFINED
};
