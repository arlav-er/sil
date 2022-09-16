package it.eng.sil.coop.webservices.agenda.appuntamento;

import java.math.BigDecimal;

public class EsitoInserimentoLavoratore {

	boolean success;
	BigDecimal cdnLavoratore;
	String codErrore;
	String descrizioneErrore;

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public BigDecimal getCdnLavoratore() {
		return cdnLavoratore;
	}

	public void setCdnLavoratore(BigDecimal cdnLavoratore) {
		this.cdnLavoratore = cdnLavoratore;
	}

	public String getCodErrore() {
		return codErrore;
	}

	public void setCodErrore(String codErrore) {
		this.codErrore = codErrore;
	}

	public String getDescrizioneErrore() {
		return descrizioneErrore;
	}

	public void setDescrizioneErrore(String descrizioneErrore) {
		this.descrizioneErrore = descrizioneErrore;
	}

}
