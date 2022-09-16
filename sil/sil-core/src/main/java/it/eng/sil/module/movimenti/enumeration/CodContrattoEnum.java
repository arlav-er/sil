package it.eng.sil.module.movimenti.enumeration;

public enum CodContrattoEnum {

LAVORO_DIPENDENTE_TD("LT"),
//
CONTRATTO_FORMAZIONE("FL"),
//
INTERINALE("IN"),
//
TIROCINIO("TI"),
//
LAVORO_SOC_UTILE_LSU("RP1"),
//
PIANO_INS_PROF_PIP("RP2"),
//
BORSA_LAVORO("RP3"),
//
LAVORO_DOMICILIO("LD"),
//
SOCIO_LAVORATORE_COOP("SO"),
//
LAVORO_OCCASIONALE("LO"),
//
LAVORO_DIPENDENTE_TI("LP"),
//
STAGIONALE("SG"),
//
LAVORO_AUTONOMO_PIVA("PI"),
//
COLL_COORDINATA_CONTINUATIVA("CO"),
//
APPRENDISTATO("AP"),
//
CODIFICA_INESISTENTE("NT"),
//
STAGE("ST"),
//
PRATICANTATO("PR"),
//
CONTRATTO_INSERIMENTO("CI"),
//
LAVORO_ACCESSORIO("LA"),
//
LAVORO_PROGETTO_OCCASIONALE("PG"),
//
LAVORO_RIPARTITO("LR"),
//
LAVORO_INTERMITTENTE("LI"),
//
LAVORO_DIPENDENTE_TD_TI("LTP");

private String codice;

private CodContrattoEnum(String codice) {
	this.codice = codice;
}

public String getCodice() {
	return codice;
}
}
