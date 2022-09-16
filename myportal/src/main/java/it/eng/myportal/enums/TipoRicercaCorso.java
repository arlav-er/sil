package it.eng.myportal.enums;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

/**
 * Classe di enumerazione per determinare il tipo di ricerca da effettuare
 * 
 * @author Turrini
 * 
 */
public enum TipoRicercaCorso {
	RICERCA_MULTIPLA("Ricerca Multipla"),
	RICERCA_MULTIPLA_FR("Ricerca Multipla Formazione regolamentata"),
	RICERCA_LIBERA("Ricerca Libera"), RIF_PA("Rif. PA");
	private static List<SelectItem> tipiRicerca;
	private String label;

	TipoRicercaCorso(String label) {
		this.label = label;
	}

	String getLabel() {
		return label;
	}

	static {
		tipiRicerca = new ArrayList<SelectItem>();
		for (TipoRicercaCorso tipo : TipoRicercaCorso.values()) {
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