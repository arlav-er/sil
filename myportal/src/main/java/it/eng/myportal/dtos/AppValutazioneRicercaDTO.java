package it.eng.myportal.dtos;

import java.util.Date;

public class AppValutazioneRicercaDTO implements IDTO {

	private static final long serialVersionUID = 7356510301922534384L;

	private boolean escludiAnonime;
	private boolean soloAnonime;
	private Short numStelle;
	private Date dtaDa;
	private Date dtaA;

	public boolean isEscludiAnonime() {
		return escludiAnonime;
	}

	public void setEscludiAnonime(boolean escludiAnonime) {
		this.escludiAnonime = escludiAnonime;
	}

	public boolean isSoloAnonime() {
		return soloAnonime;
	}

	public void setSoloAnonime(boolean soloAnonime) {
		this.soloAnonime = soloAnonime;
	}

	public Short getNumStelle() {
		return numStelle;
	}

	public void setNumStelle(Short numStelle) {
		this.numStelle = numStelle;
	}

	public Date getDtaDa() {
		return dtaDa;
	}

	public void setDtaDa(Date dtaDa) {
		this.dtaDa = dtaDa;
	}

	public Date getDtaA() {
		return dtaA;
	}

	public void setDtaA(Date dtaA) {
		this.dtaA = dtaA;
	}
}
