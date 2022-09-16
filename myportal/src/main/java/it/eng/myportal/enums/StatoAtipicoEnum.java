package it.eng.myportal.enums;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

/**
 * Classe di enumerazione per determinare il Codice Stato Atipico
 * 
 * @author Turro
 * Y’: abilitato, ‘N’: disabilitato, ‘S’: sospeso
 */
public enum StatoAtipicoEnum {
	ABILITATO("Y","Abilitato"), DISABILITATO(
			"N","Disabilitato"), SOSPESO("S","Sospeso");
	private static List<SelectItem> statiAtipico;
	private String codice;
	private String label;

	private StatoAtipicoEnum(String codice,String label) {
		this.label = label;
		this.codice = codice;
	}

	public static StatoAtipicoEnum getInstance(String codice) {
		StatoAtipicoEnum atipicoEnum = null;

		if (codice != null) {
			if (codice.equals(ABILITATO.getCodice())) {
				atipicoEnum = StatoAtipicoEnum.ABILITATO;
			} else if (codice.equals(DISABILITATO.getCodice())) {
				atipicoEnum = StatoAtipicoEnum.DISABILITATO;
			} else if (codice.equals(SOSPESO.getCodice())) {
				atipicoEnum = StatoAtipicoEnum.SOSPESO;
			}
			
		}
		return atipicoEnum;
	}

	
	static {
		statiAtipico = new ArrayList<SelectItem>();
		for (StatoAtipicoEnum tipo : StatoAtipicoEnum.values()) {
			statiAtipico.add(new SelectItem(tipo.getCodice(), tipo.getLabel()));
		}
	}

	@Override
	public String toString() {
		return codice;
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
		return statiAtipico;
	}

	public String getCodice() {
		return codice;
	}

	public String getLabel() {
		return label;
	}
}
