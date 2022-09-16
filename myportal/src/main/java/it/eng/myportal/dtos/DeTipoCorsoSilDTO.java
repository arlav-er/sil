package it.eng.myportal.dtos;

public class DeTipoCorsoSilDTO extends GenericDecodeDTO implements ISuggestible {
	private static final long serialVersionUID = -3385541087133510848L;

	private String codTipoCorsoMin;

	public String getCodTipoCorsoMin() {
		return codTipoCorsoMin;
	}

	public void setCodTipoCorsoMin(String codTipoCorsoMin) {
		this.codTipoCorsoMin = codTipoCorsoMin;
	}
}
