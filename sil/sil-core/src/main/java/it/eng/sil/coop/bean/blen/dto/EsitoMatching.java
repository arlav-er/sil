package it.eng.sil.coop.bean.blen.dto;

import it.eng.sil.coop.bean.blen.Risultato;

public class EsitoMatching {
	private static final long serialVersionUID = 1L;
	private String codice;
	private Risultato risultato;

	public EsitoMatching(String codice) {
		this.codice = codice;
	}

	public EsitoMatching(String codice, Risultato risultato) {
		this.codice = codice;
		this.risultato = risultato;
	}

	public Risultato getRisultato() {
		return this.risultato;
	}

	public void setRisultato(Risultato risultato) {
		this.risultato = risultato;
	}

	public String getCodcie() {
		return this.codice;
	}
}