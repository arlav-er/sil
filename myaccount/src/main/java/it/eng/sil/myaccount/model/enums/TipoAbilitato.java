package it.eng.sil.myaccount.model.enums;

public enum TipoAbilitato {

	DATORE_PRIVATO("01"), DATORE_PUBBLICO("02"), AGENZIA_SOMMINISTRAZIONE("03"), SOGGETTO_ABILITATO("04"), AGENZIA_LAVORO(
			"05"), SOGGETTO_TIROCINI("06");

	private String codice;

	private TipoAbilitato(String codice) {
		this.codice = codice;
	}

	public String getCodice() {
		return codice;
	}

	public void setCodice(String codice) {
		this.codice = codice;
	}
}
