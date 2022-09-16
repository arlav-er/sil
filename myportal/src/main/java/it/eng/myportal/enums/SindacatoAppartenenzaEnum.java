package it.eng.myportal.enums;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

/**
 * Classe di enumerazione per determinare il Sindacato di Appartenenza
 * 
 * @author Turro
 */
public enum SindacatoAppartenenzaEnum {
	NIDIL("NIDIL","0"),FELSA("FeLSA","1"),CPO("CPO","2"),INPS("INPS","3"),CNA("CNA Interpreta","4");

	private static List<SelectItem> sindacatiAppartenenza;
	private String value;
	private String cod;

	private SindacatoAppartenenzaEnum(String value,String cod) {
		this.value = value;
		this.cod = cod;
		
	}

	public static SindacatoAppartenenzaEnum getInstance(String value) {
		SindacatoAppartenenzaEnum atipicoEnum = null;

		if (value != null) {
			if (value.equals(NIDIL.ordinal())) {
				atipicoEnum = SindacatoAppartenenzaEnum.NIDIL;
			} else if (value.equals(FELSA.ordinal())) {
				atipicoEnum = SindacatoAppartenenzaEnum.FELSA;
			} else if (value.equals(CPO.ordinal())) {
				atipicoEnum = SindacatoAppartenenzaEnum.CPO;
			} else if (value.equals(INPS.ordinal())) {
				atipicoEnum = SindacatoAppartenenzaEnum.INPS;
			} else if (value.equals(CNA.ordinal())) {
				atipicoEnum = SindacatoAppartenenzaEnum.CNA;
			}
			
		}
		return atipicoEnum;
	}

	
	static {
		sindacatiAppartenenza = new ArrayList<SelectItem>();
		for (SindacatoAppartenenzaEnum tipo : SindacatoAppartenenzaEnum.values()) {
			sindacatiAppartenenza.add(new SelectItem(tipo.ordinal(), tipo.getValue()));
		}
	}

	@Override
	public String toString() {
		return value;
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
		return sindacatiAppartenenza;
	}


	public String getValue() {
		return value;
	}

	public String getCod() {
		return cod;
	}

	public void setCod(String cod) {
		this.cod = cod;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
}
