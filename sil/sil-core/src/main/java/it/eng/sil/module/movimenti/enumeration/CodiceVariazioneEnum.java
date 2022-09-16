package it.eng.sil.module.movimenti.enumeration;

public enum CodiceVariazioneEnum {

PROROGA_ASSENZA_MISSIONE("2.01"),
//
PROROGA_LAVORO_E_MISSIONE("2.02"),
//
PROROGA_MISSIONE_RAPPORTO_TEMPO_INDETERMINATO("2.03"),
//
TRASFORMAZIONE_IN_COSTANZA_DI_MISSIONE("3.01"),
//
TRASFORMAZIONE_IN_ASSENZA_DI_MISSIONE("3.02"),
//
TRASFERIMENTO_ALTRA_SEDE_LAVORO_DITTA_UTILIZZATRICE("3.03"),
//
TRASFERIMENTO_ALTRA_SEDE_OPERATIVA_AGENZIA("3.04"),
//
CESSAZIONE_MISSIONE("4.01"),
//
CESSAZIONE_RAPPORTO_DI_LAVORO("4.02");

private String codice;

private CodiceVariazioneEnum(String codice) {
	this.codice = codice;
}

public String getCodice() {
	return codice;
}
}
