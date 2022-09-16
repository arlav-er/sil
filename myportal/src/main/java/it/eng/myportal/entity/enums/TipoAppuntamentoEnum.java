package it.eng.myportal.entity.enums;

public enum TipoAppuntamentoEnum {
PL_APPGG("PL_APPGG"), PL_APPGO("PL_APPGO"), PL_APPSE("PL_APPSE"), PL_APPOB("PL_APPOB");

private String codTipoAppuntamento;

private TipoAppuntamentoEnum(String codTipoAppuntamento) {
	this.codTipoAppuntamento = codTipoAppuntamento;
}

public String getCodTipoAppuntamento() {
	return codTipoAppuntamento;
}
}
