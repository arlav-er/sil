package it.eng.sil.myaccount.model.enums;

public enum TipoDelegato {
	
	SOGGETTO_INTERMEDIAZIONE("009"),
	PROMOTORE_TIROCINI("010");
	
	private String codice;

	private TipoDelegato(String codice) {
		this.codice = codice;
	}

	public String getCodice() {
		return codice;
	}

	public void setCodice(String codice) {
		this.codice = codice;
	}
}
