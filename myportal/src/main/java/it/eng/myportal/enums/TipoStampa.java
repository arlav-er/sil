package it.eng.myportal.enums;

public enum TipoStampa {
	DID("did"), RINNOVO_PATTO("rinpatto");

	private String codice;

	public String getCodice() {
		return codice;
	}

	TipoStampa(String codice) {
		this.codice = codice;
	}

}
