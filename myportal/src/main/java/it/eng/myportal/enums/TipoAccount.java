package it.eng.myportal.enums;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

/**
 * Classe di enumerazione per determinare il tipo di account per effettuare la ricerca utente
 * 
 * @author Enrico D'Angelo
 * 
 */
public enum TipoAccount {
	TUTTI("Tutti"), ACCOUNT_ATTIVO("Account attivo"), ACCOUNT_NON_ATTIVO("Account non attivo");
	private static List<SelectItem> tipiAccount;
	private String label;

	private TipoAccount(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}

	static {
		tipiAccount = new ArrayList<SelectItem>();
		for (TipoAccount tipo : TipoAccount.values()) {
			tipiAccount.add(new SelectItem(tipo, tipo.getLabel()));
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
		return tipiAccount;
	}
}