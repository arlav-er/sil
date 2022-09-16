package it.eng.myportal.enums;

public enum ErroreNotificaCambioStatoAdesioneYGEnum {
	NP("NP", "Adesione non presente"),
	ST("ST", "Stato precedente non congruente"),
	EM("EM", "Si riporta il contenuto del tag \"MessaggioErrore\""),
	XML("XML", "Messaggio xml mal formattato"),
	E("E", "Errore generico");
	
	private String codice;
	private String messaggio;
	
	private ErroreNotificaCambioStatoAdesioneYGEnum(String codice, String messaggio) {
		this.codice = codice;
		this.messaggio = messaggio;
	}
	
	public String getCodice() {
		return codice;
	}
	
	public String getMessaggio() {
		return messaggio;
	}
}
