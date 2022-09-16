package it.eng.myportal.dtos;

import java.util.Date;

public class AppNotificaRicercaDTO implements IDTO {

	private static final long serialVersionUID = 3165393986699654555L;

	private Integer idPfPrincipalDestinatario;
	private String emailDestinatario;
	private String codStato;
	private boolean escludiNotificheBatch;
	private Date dtaDa;
	private Date dtaA;

	public Integer getIdPfPrincipalDestinatario() {
		return idPfPrincipalDestinatario;
	}

	public void setIdPfPrincipalDestinatario(Integer idPfPrincipalDestinatario) {
		this.idPfPrincipalDestinatario = idPfPrincipalDestinatario;
	}

	public String getEmailDestinatario() {
		return emailDestinatario;
	}

	public void setEmailDestinatario(String emailDestinatario) {
		this.emailDestinatario = emailDestinatario;
	}

	public String getCodStato() {
		return codStato;
	}

	public void setCodStato(String codStato) {
		this.codStato = codStato;
	}

	public boolean getEscludiNotificheBatch() {
		return escludiNotificheBatch;
	}

	public void setEscludiNotificheBatch(boolean escludiNotificheBatch) {
		this.escludiNotificheBatch = escludiNotificheBatch;
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
