package it.eng.myportal.dtos;

import java.io.Serializable;

import it.eng.myportal.entity.enums.TipoNotificaEnum;
import it.eng.myportal.rest.app.helper.StatoNotifica;

/**
 * DTO per le notifiche da App
 * 
 * @author
 * 
 */
public class AppNotificaDTO extends AbstractUpdatablePkDTO implements IUpdatable, Serializable {

	private static final long serialVersionUID = 179081785596106324L;

	private String titolo;
	private String sottotitolo;
	private String messaggio;
	private Integer idPfPrincipalDest;
	private String sidNotifica;
	private TipoNotificaEnum tipoNotifica;
	private StatoNotifica stato;
	private String email;

	private Integer numRead;
	private Integer numSend;
	private Integer numFail;
	private Integer numDelay;

	public AppNotificaDTO() {
	}

	public String getTitolo() {
		return titolo;
	}

	public void setTitolo(String titolo) {
		this.titolo = titolo;
	}

	public String getSottotitolo() {
		return sottotitolo;
	}

	public void setSottotitolo(String sottotitolo) {
		this.sottotitolo = sottotitolo;
	}

	public String getMessaggio() {
		return messaggio;
	}

	public void setMessaggio(String messaggio) {
		this.messaggio = messaggio;
	}

	public Integer getIdPfPrincipalDest() {
		return idPfPrincipalDest;
	}

	public void setIdPfPrincipalDest(Integer idPfPrincipalDest) {
		this.idPfPrincipalDest = idPfPrincipalDest;
	}

	public String getSidNotifica() {
		return sidNotifica;
	}

	public void setSidNotifica(String sidNotifica) {
		this.sidNotifica = sidNotifica;
	}

	public TipoNotificaEnum getTipoNotifica() {
		return tipoNotifica;
	}

	public void setTipoNotifica(TipoNotificaEnum tipoNotifica) {
		this.tipoNotifica = tipoNotifica;
	}

	public StatoNotifica getStato() {
		return stato;
	}

	public void setStato(StatoNotifica stato) {
		this.stato = stato;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Integer getNumRead() {
		return numRead;
	}

	public void setNumRead(Integer numRead) {
		this.numRead = numRead;
	}

	public Integer getNumSend() {
		return numSend;
	}

	public void setNumSend(Integer numSend) {
		this.numSend = numSend;
	}

	public Integer getNumFail() {
		return numFail;
	}

	public void setNumFail(Integer numFail) {
		this.numFail = numFail;
	}

	public Integer getNumDelay() {
		return numDelay;
	}

	public void setNumDelay(Integer numDelay) {
		this.numDelay = numDelay;
	}

	public boolean getIsBroadcast() {
		boolean ret = false;
		/*
		 * Se di tipo BROADCAST lo è sicuramente, altrimenti si verifica se il destinatario è nullo (l'informazione
		 * tipoNotifica è stata aggiunta successivamente, senza bonifica del pregresso
		 */
		if (TipoNotificaEnum.BROADCAST.equals(this.getTipoNotifica())) {
			ret = true;
		} else if (this.getIdPfPrincipalDest() == null) {
			ret = true;
		}

		return ret;
	}

	public boolean getReaded() {
		boolean ret = false;

		if (this.stato != null && this.stato == StatoNotifica.R) {
			ret = true;
		}

		return ret;
	}

	public boolean getFailed() {
		boolean ret = false;

		if (this.stato != null && (this.stato == StatoNotifica.F || this.stato == StatoNotifica.L)) {
			ret = true;
		}

		return ret;
	}

	public boolean getDelayed() {
		boolean ret = false;

		if (this.stato != null && this.stato == StatoNotifica.D) {
			ret = true;
		}

		return ret;

	}

	public boolean getClicked() {
		boolean ret = false;

		if (this.stato != null && this.stato == StatoNotifica.C) {
			ret = true;
		}

		return ret;
	}

}
