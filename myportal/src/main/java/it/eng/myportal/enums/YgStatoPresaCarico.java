package it.eng.myportal.enums;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

/**
 * Classe di enumerazione per determinare il tipo di ricerca da effettuare 
 */
public enum YgStatoPresaCarico {
	
	/*TUTTI("Tutti"), 
	DA_PRENDERE_IN_CARICO("da prendere in carico"),  
	PRESI_IN_CARICO("presi in carico");*/
	
	TUTTI("Tutti"), 
	SENZA_APPUNTAMENTO("senza Appuntamento"),  
	CON_APPUNTAMENTO("con Appuntamento");
	
	private static List<SelectItem> tipiRicerca;
	private String label;

	private YgStatoPresaCarico(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}

	static {
		tipiRicerca = new ArrayList<SelectItem>();
		for (YgStatoPresaCarico tipo : YgStatoPresaCarico.values()) {
			tipiRicerca.add(new SelectItem(tipo, tipo.getLabel()));
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
		return tipiRicerca;
	}
	
}
