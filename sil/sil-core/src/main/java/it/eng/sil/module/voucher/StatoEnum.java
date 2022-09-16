package it.eng.sil.module.voucher;

public enum StatoEnum {

ASSEGNATO("ASS"), ANNULLATO("ANN"), ATTIVATO("ATT"), CONCLUSO("CHI");

private String codice;

private StatoEnum(String codice) {
	this.codice = codice;
}

public String getCodice() {
	return codice;
}

}
