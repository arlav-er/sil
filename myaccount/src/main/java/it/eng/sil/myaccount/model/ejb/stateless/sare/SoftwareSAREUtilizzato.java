package it.eng.sil.myaccount.model.ejb.stateless.sare;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

/**
 * Classe di enumerazione per determinare il tipo di software gestionale
 * utilizzato per le comunicazioni obbligatorie
 * 
 * @author Enrico D'Angelo
 * 
 */
public enum SoftwareSAREUtilizzato {
	SARE_CLIENT_ONLINE("Sare Client / Sare OnLine", "01"), SOFTWARE_PROPRIETARIO(
			"Software gestionale proprietario", "02");
	private static List<SelectItem> softwareSAREUtilizzati;
	private String label;
	private String codSoftwareSAREUtilizzato;

	private SoftwareSAREUtilizzato(String label,
			String codSoftwareSAREUtilizzato) {
		this.label = label;
		this.codSoftwareSAREUtilizzato = codSoftwareSAREUtilizzato;
	}

	public static SoftwareSAREUtilizzato getInstance(String codSoftwareSAREUtilizzato) {
		SoftwareSAREUtilizzato softwareSAREUtilizzato = null;

		if (codSoftwareSAREUtilizzato != null) {
			if (codSoftwareSAREUtilizzato.equals(SARE_CLIENT_ONLINE.getCodSoftwareSAREUtilizzato())) {
				softwareSAREUtilizzato = SoftwareSAREUtilizzato.SARE_CLIENT_ONLINE;
			} else if (codSoftwareSAREUtilizzato.equals(SOFTWARE_PROPRIETARIO.getCodSoftwareSAREUtilizzato())) {
				softwareSAREUtilizzato = SoftwareSAREUtilizzato.SOFTWARE_PROPRIETARIO;
			}
		}
		return softwareSAREUtilizzato;
	}

	public String getLabel() {
		return label;
	}

	public String getCodSoftwareSAREUtilizzato() {
		return codSoftwareSAREUtilizzato;
	}

	static {
		softwareSAREUtilizzati = new ArrayList<SelectItem>();
		for (SoftwareSAREUtilizzato tipo : SoftwareSAREUtilizzato.values()) {
			softwareSAREUtilizzati.add(new SelectItem(tipo, tipo.getLabel()));
		}
	}

	@Override
	public String toString() {
		return codSoftwareSAREUtilizzato;
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
		return softwareSAREUtilizzati;
	}
}
