package it.eng.sil.coop.webservices.firmagrafometrica.custom;

import java.io.Serializable;

public class FirmaGrafometricaXmlOutputBean implements Serializable {

	private static final long serialVersionUID = 3068502779818309993L;

	private int codice;
	private String descrizione;

	public int getCodice() {
		return codice;
	}

	public void setCodice(int codice) {
		this.codice = codice;
	}

	public String getDescrizione() {
		return descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

}
