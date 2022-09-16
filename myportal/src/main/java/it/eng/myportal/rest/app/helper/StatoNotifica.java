package it.eng.myportal.rest.app.helper;

public enum StatoNotifica {
	S("SENDED","Inviata"),D("DELAYED", "Schedulata"), F("FAILED", "Invio fallito"), L("LOGOUT", "Invio fallito per logout dell'utente"), 
	R("READED", "Letta"), C("CLICKED", "Letta e confermata");
	
	private String stato;
	private String descrizione;
	
	public String getStato() {
		return stato;
	}
	
	public String getDescrizione() {
		return descrizione;
	}
	
	private StatoNotifica(String stato, String descrizione) {
		this.stato = stato;
		this.descrizione = descrizione;
	}
	
	public boolean isModificabile() {
		boolean statoModificabile = false;
	
		switch (this) {
		case D:
			statoModificabile = true;
			break;
		case S:
			statoModificabile = true;
			break;
		default:
			statoModificabile = false;
		}
	
		return statoModificabile;
	}
}
