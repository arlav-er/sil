package it.eng.sil.module.movimenti.enumeration;

public enum CodMonoTempoEnum {

INDETERMINATO("I"),
//
DETERMINATO("D");

private String codice;

private CodMonoTempoEnum(String codice) {
	this.codice = codice;
}

public String getCodice() {
	return codice;
}
}
