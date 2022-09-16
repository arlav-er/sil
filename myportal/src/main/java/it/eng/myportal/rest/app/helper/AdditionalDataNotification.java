package it.eng.myportal.rest.app.helper;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import it.eng.myportal.entity.AppNotifica;
import it.eng.myportal.entity.enums.TipoNotificaEnum;

public class AdditionalDataNotification extends HashMap<String, Object> {

	private static final long serialVersionUID = 5793330019344652939L;
	private static final String TYPE_KEY = "type";
	private static final String ID_RICERCA_SALVATA_KEY = "idRicercaSalvata";

	private enum Type {
	RIC_SALV, ALTRO, BROADCAST
	}

	public AdditionalDataNotification() {
		super();
	}

	public AdditionalDataNotification(String data) throws JSONException {

		JSONObject jsonObject = new JSONObject(data);
		String type = jsonObject.getString(TYPE_KEY);
		this.put(TYPE_KEY, type);

		if (jsonObject.has(ID_RICERCA_SALVATA_KEY)) {
			Integer idRicercaSalvata = jsonObject.getInt(ID_RICERCA_SALVATA_KEY);
			this.put(ID_RICERCA_SALVATA_KEY, idRicercaSalvata);
		}
	}

	public AdditionalDataNotification(AppNotifica appNotifica, Integer idRicercaSalvata) {
		super();
		Type type = null;

		// In prima battuta il Type della notifica da includere nei dati addizionali viene determinato rispetto al tipo
		// della notifica (se presente)
		type = mapTypeFromTipoNotifica(appNotifica.getTipoNotifica());

		// Se non trovato rispetto al tipo di notifica viene determinato rispetto agli altri parametri passati (vecchia
		// logica).
		if (type == null) {
			type = mapTypeFromParam(appNotifica, idRicercaSalvata);
		}

		this.put(TYPE_KEY, type);

		if (idRicercaSalvata != null) {
			this.put(ID_RICERCA_SALVATA_KEY, idRicercaSalvata);
		}
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
			case RIC_SALV:
				type = Type.RIC_SALV;
				break;
			case BROADCAST:
				type = Type.BROADCAST;
				break;
			case PUNT_CPI:
			case RISP_ASS:
			case OTP_PATTO:
			case INFO_PATTO:
				type = Type.ALTRO;
				break;
			case CV_SCAD:
				type = Type.ALTRO;
				break;
			default:
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

		if (idRicercaSalvata != null) {
			type = Type.RIC_SALV;
		} else if (appNotifica.isBroadcast()) {
			type = Type.BROADCAST;
		} else {
			type = Type.ALTRO;
		}

		return type;
	}
}
