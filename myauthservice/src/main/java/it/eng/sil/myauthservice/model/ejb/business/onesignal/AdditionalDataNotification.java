package it.eng.sil.myauthservice.model.ejb.business.onesignal;

import java.util.HashMap;
import java.util.logging.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import it.eng.sil.myauthservice.model.entity.AppNotifica;
import it.eng.sil.myauthservice.model.entity.TipoNotificaEnum;

/**
 * Il tipo della notifica conosciuto da MyAuthService e` solo "altro", utilizzato per le push OTP
 * 
 * @author Ale
 *
 */
public class AdditionalDataNotification extends HashMap<String, Object> {
	protected static Logger log = Logger.getLogger(AdditionalDataNotification.class.getName());
	
	private static final long serialVersionUID = 5793330019344652939L;
	private static final String TYPE_KEY = "type";

	private enum Type {
		ALTRO
	}

	public AdditionalDataNotification() {
		super();

		this.put(TYPE_KEY, Type.ALTRO.toString());
	}

	public JSONObject toJSONObject() {
		return new JSONObject(this);
	}
	 
	 
	/**
	 * Metodo per la mappatura del Type presente sui dati addizionali della notifica rispetto al tipo della notifica. Il
	 * Type presente sui dati addizionali è quello che pilota la logica lato App.
	 * 
	 * Diversi tipi di notifiche confluiscono nello stesso Type, di seguito: RIC_SALV -> RIC_SALV; BROADCAST ->
	 * BROADCAST; PUNT_CPI, RISP_ASS, OTP_PATTO, INFO_PATTO -> ALTRO.
	 * 
	 * @param tipoNotifica
	 * @return
	 */
	private Type mapTypeFromTipoNotifica(TipoNotificaEnum tipoNotifica) {
		Type type = null;

		if (tipoNotifica != null) {
			switch (tipoNotifica) {
			 
			case OTP_PATTO:
			 
				type = Type.ALTRO;
				break;
			default:
				log.warning("MYAUTH GESTISCE SOLO NOTIFICHE OTP_PATTO!! "+tipoNotifica);
				break;
			}
		}

		return type;
	}

	/**
	 * Metodo di mappatura del Type rispetto alle informazioni passate.
	 * 
	 * @param appNotifica
	 * @param idRicercaSalvata
	 * @return
	 */
	private Type mapTypeFromParam(AppNotifica appNotifica, Integer idRicercaSalvata) {
		Type type = null;

		//if (idRicercaSalvata != null) {
		//	type = Type.RIC_SALV;
		//} else if (appNotifica.isBroadcast()) {
		//	type = Type.BROADCAST;
		//} else {
			type = Type.ALTRO;
		//}

		return type;
	}
}
