package it.eng.myportal.enums;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

/**
 * Classe di enumerazione per determinare il tipo di registrazione per effettuare la ricerca utente
 * 
 * @author Enrico D'Angelo
 * 
 */
public enum TipoRegistrazione {
	TUTTI("Tutti"), REGISTRAZIONE_DEBOLE("Registrazione debole"), REGISTRAZIONE_FORTE("Registrazione forte");
	private static List<SelectItem> tipiRegistrazione;
	private String label;

	private TipoRegistrazione(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}

	static {
		tipiRegistrazione = new ArrayList<SelectItem>();
		for (TipoRegistrazione tipo : TipoRegistrazione.values()) {
			tipiRegistrazione.add(new SelectItem(tipo, tipo.getLabel()));
		}
	}

	/**
	 * Restituisce l'enumerazione convertita in lista di selectItem.<br/>
	 * <b>Attenzione! Per usare l'enumerazione come valore nel BackingBean è
	 * necessario che l'EnumConverter sia associato alla classe di enumerazione
	 * da convertire. Vedi faces-config.xml
	 * 
	 * @return l'enumerazione convertita in lista di selectItem.
	 */
	public static List<SelectItem> asSelectItems() {
		return tipiRegistrazione;
	}
}