package it.eng.sil.module.pi3;

import java.io.Serializable;
import java.math.BigDecimal;

public class BatchProtocollazioneDifferitaBean implements Serializable {

	private static final long serialVersionUID = -4204417299527017558L;

	private BigDecimal prgBatchProtDifferita;
	private String strDescrizione;
	private String numOraInizio;
	private String numOraFine;
	private String flgProtDiff;

	public BigDecimal getPrgBatchProtDifferita() {
		return prgBatchProtDifferita;
	}

	public void setPrgBatchProtDifferita(BigDecimal prgBatchProtDifferita) {
		this.prgBatchProtDifferita = prgBatchProtDifferita;
	}

	public String getStrDescrizione() {
		return strDescrizione;
	}

	public void setStrDescrizione(String strDescrizione) {
		this.strDescrizione = strDescrizione;
	}

	public String getNumOraInizio() {
		return numOraInizio;
	}

	public void setNumOraInizio(String numOraInizio) {
		this.numOraInizio = numOraInizio;
	}

	public String getNumOraFine() {
		return numOraFine;
	}

	public void setNumOraFine(String numOraFine) {
		this.numOraFine = numOraFine;
	}

	public String getFlgProtDiff() {
		return flgProtDiff;
	}

	public void setFlgProtDiff(String flgProtDiff) {
		this.flgProtDiff = flgProtDiff;
	}

}
