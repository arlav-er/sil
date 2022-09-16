package it.eng.sil.coop.webservices.agenda.appuntamento;

import java.math.BigDecimal;

public class EsitoContatto {

	public EsitoContatto() {
		inserito = false;
	}

	private boolean inserito;
	private BigDecimal prgContatto;

	public boolean isInserito() {
		return inserito;
	}

	public void setInserito(boolean inserito) {
		this.inserito = inserito;
	}

	public BigDecimal getPrgContatto() {
		return prgContatto;
	}

	public void setPrgContatto(BigDecimal prgContatto) {
		this.prgContatto = prgContatto;
	}

}
