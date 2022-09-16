package it.eng.sil.module.movimenti.enumeration;

public enum CodMonoTipoEnum {

APPRENDISTATO("A"),
//
TIROCINIO("T"),
//
AUTONOMO("N");

private String codice;

private CodMonoTipoEnum(String codice) {
	this.codice = codice;
}

public String getCodice() {
	return codice;
}
}
