package it.eng.myportal.enums;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.model.SelectItem;

public enum FasciaEta {
	F_18_25("18/25", "0"), F_26_32("26/32", "1"), F_33_45("33/45", "2"), F_45("oltre 45", "3");
	
	private String label;
	private String codFasciaEta;
	
	public String getCodFasciaEta() {
		return codFasciaEta;
	}

	public void setCodFasciaEta(String codFasciaEta) {
		this.codFasciaEta = codFasciaEta;
	}

	private FasciaEta(String label, String ordinal) {
		this.label = label;
		this.codFasciaEta = ordinal;
	}
	
	public String getLabel() {
		return label;
	}
	
	public static FasciaEta getFascia(Date dataNascita) {
		if (dataNascita == null) return null;
		Timestamp oggi = new Timestamp(System.currentTimeMillis());
		Date oggiDate = new Date(oggi.getTime());
		long ageInMillis = oggiDate.getTime() - dataNascita.getTime();

		Timestamp age = new Timestamp(ageInMillis);
		
		long eta = age.getYear()-70;
		if (eta < 26) {
			return F_18_25;
		} else if (eta >= 26 && eta < 33) {
			return F_26_32;
		} else if (eta >= 33 && eta < 45) {
			return F_33_45;
		} else {
			return F_45;
		} 
	}
	
	public void setLabel(String label) {
		this.label = label;
	}

	private static List<SelectItem> fascieEta;

	static {
		fascieEta = new ArrayList<SelectItem>();
		for (FasciaEta tipo : FasciaEta.values()) {
			fascieEta.add(new SelectItem(tipo, tipo.label));
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
		return fascieEta;
	}
	
	public static List<SelectItem> asSelectItems(boolean withBlank) {
		List<SelectItem> fa;
		
		if(withBlank) {
			fa = new ArrayList<SelectItem>();
			fa.add(0, new SelectItem(null, ""));
			fa.addAll(fascieEta);
		}
		else
			 fa = fascieEta;
			 
		return fa;
	}
	
}
