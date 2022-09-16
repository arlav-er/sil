package it.eng.sil.module.movimenti.enumeration;

public enum CodTipoOrarioEnum {

TEMPO_PIENO_2_TURNI("TP2"),
//
TEMPO_PIENO_3_TURNI("TP3"),
//
PART_TIME_WK("PTVS"),
//
TEMPO_PARZIALE_MISTO("M"),
//
NON_DEFINITO("N"),
//
TEMPO_PARZIALE_VERTICALE("V"),
//
TEMPO_PARZIALE_ORIZZONTALE("P"),
//
TEMPO_PIENO("F");

private String codice;

private CodTipoOrarioEnum(String codice) {
	this.codice = codice;
}

public String getCodice() {
	return codice;
}
}
