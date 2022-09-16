package it.eng.sil.module.movimenti.enumeration;

public enum TipoMovimentoEnum {

AVVIAMENTO("AVV"),
//
TRASFORMAZIONE("TRA"),
//
PROROGA("PRO"),
//
CESSAZIONE("CES");

private String codice;

private TipoMovimentoEnum(String codice) {
	this.codice = codice;
}

public String getCodice() {
	return codice;
}

public static TipoMovimentoEnum getValueByCodice(String codice) {
	if (codice.equals(TipoMovimentoEnum.AVVIAMENTO.getCodice()))
		return TipoMovimentoEnum.AVVIAMENTO;
	if (codice.equals(TipoMovimentoEnum.TRASFORMAZIONE.getCodice()))
		return TipoMovimentoEnum.TRASFORMAZIONE;
	if (codice.equals(TipoMovimentoEnum.PROROGA.getCodice()))
		return TipoMovimentoEnum.PROROGA;
	if (codice.equals(TipoMovimentoEnum.CESSAZIONE.getCodice()))
		return TipoMovimentoEnum.CESSAZIONE;
	return null;
}

}
