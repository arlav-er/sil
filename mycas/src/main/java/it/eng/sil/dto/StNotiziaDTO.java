package it.eng.sil.dto;

import java.io.Serializable;

public class StNotiziaDTO implements Serializable{

	private static final long serialVersionUID = 8338954901999123561L;
	
	private String contenuto;
	private String oggetto;
	private String dtmPubblicazione;
	
	public StNotiziaDTO(){
		
	}

	public String getContenuto() {
		return contenuto;
	}

	public void setContenuto(String contenuto) {
		this.contenuto = contenuto;
	}

	public String getOggetto() {
		return oggetto;
	}

	public void setOggetto(String oggetto) {
		this.oggetto = oggetto;
	}

	public String getDtmPubblicazione() {
		return dtmPubblicazione;
	}

	public void setDtmPubblicazione(String dtmPubblicazione) {
		this.dtmPubblicazione = dtmPubblicazione;
	}
	
}
