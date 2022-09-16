package it.eng.myportal.rest.app.exception;

import javax.ejb.ApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;

@ApplicationException(rollback = true)
public class AppEjbException extends Exception {

	private static final long serialVersionUID = 1156880255118546995L;

	private static Log log = LogFactory.getLog(AppEjbException.class);

	static final String OK = "OK";
	static final String KO = "ko";
	static final String ERROR = "error";
	static final String STATUS = "status";
	static final String CODE = "code";

	// Codifica codice eccezioni
	static final int GENERIC_CODE = 100;
	static final int USER_NOT_FOUND_CODE = 110;
	static final int USER_NOT_ACTIVE_CODE = 120;
	static final int USER_NOT_ENABLED_CODE = 130;
	static final int WRONG_PASSWORD_CODE = 140;
	static final int ONESIGNAL_CODE = 150;
	static final int INFORMATIVA_NOT_FOUND_CODE = 160;
	static final int EMPTY_PARAMETER_CODE = 170;
	static final int WRONG_PARAMETER_CODE = 180;
	static final int USER_SERV_AMM_NOT_ENABLED_CODE = 190;
	static final int NOTIFY_NOT_FOUND_CODE = 200;
	static final int RICERCA_SALVATA_NOT_FOUND_CODE = 210;
	static final int EXPIRED_PASSWORD_CODE = 220;
	static final int BLOCKED_LOGIN_CODE = 230;
	static final int CURRICULUM_UTENTE_NOT_FOUND_CODE = 240;
	static final int EMAIL_NOT_UNIQUE_CODE = 250;
	static final int ALLEGATO_CURRICULUM_UTENTE_NOT_FOUND_CODE = 260;

	// Codifica descrizione eccezioni
	static final String USER_NOT_FOUND_DES = "Utente non trovato";
	static final String USER_NOT_ACTIVE_DES = "Utente non attivo";
	static final String USER_NOT_ENABLED_DES = "Utente non abilitato";
	static final String WRONG_PASSWORD_DES = "Password errata";
	static final String INFORMATIVA_NOT_FOUND_DES = "Informativa non trovata";
	static final String EMPTY_PARAMETER_DES = "Parametri obbligatori non presenti";
	static final String WRONG_PARAMETER_DES = "Parametri non validi";
	static final String USER_SERV_AMM_NOT_ENABLED_DES = "Utente non abilitato ai servizi amministrativi";
	static final String NOTIFY_NOT_FOUND_DES = "Notifica non trovata";
	static final String RICERCA_SALVATA_NOT_FOUND_DES = "Ricerca salvata non trovata";
	static final String EXPIRED_PASSWORD_DES = "Password scaduta";
	static final String BLOCKED_LOGIN_DES = "Raggiunto il numero massimo di tentativi di accesso";
	static final String CURRICULUM_UTENTE_NOT_FOUND_DES = "Curriculum non presente";
	static final String EMAIL_NOT_UNIQUE_DES = "Indirizzo email non disponibile";
	static final String ALLEGATO_CURRICULUM_UTENTE_NOT_FOUND_DES = "Allegato non presente per il Curriculum";

	private int code;
	private String description;

	public AppEjbException() {
		super();
	}

	public AppEjbException(int code, String description) {
		super();
		this.code = code;
		this.description = description;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	Response.Status getResponseStatus() {
		return Response.Status.INTERNAL_SERVER_ERROR;
	}

	JSONObject getExceptionJson() {
		log.error("GRAVE: Errore " + this.getClass().getName() + ": " + code + "|" + description);

		JSONObject obj = new JSONObject();
		try {
			obj.put(STATUS, KO);
			obj.put(CODE, code);
			obj.put(ERROR, description);
		} catch (JSONException e) {
			log.error(e.getMessage());
		}
		return obj;
	}

	public Response createJsonResponse() {
		ResponseBuilder builder = Response.status(getResponseStatus());
		builder.entity(getExceptionJson().toString());
		Response response = builder.build();
		return response;
	}
}
