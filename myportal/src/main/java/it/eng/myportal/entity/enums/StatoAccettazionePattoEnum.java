package it.eng.myportal.entity.enums;

public enum StatoAccettazionePattoEnum {
	A("Accettato"), D("Da accettare"), S("Termine Scaduto");
	
	String descrizione;

	private StatoAccettazionePattoEnum(String descrizione) {
		this.descrizione = descrizione;
	}

	public String getDescrizione() {
		return descrizione;
	}
	
	
}
