package it.eng.myportal.dtos;

import java.util.Date;

public class WsStampaDTO extends AbstractUpdatablePkDTO {

	private static final long serialVersionUID = -1659767708060595183L;

	private byte[] contenuto;
	private String codTipoStampa;
	private Integer idPfPrincipal;
	private Boolean flgDidRischioDisoccupazione;
	private Date dtDidLicenziamento;
	private Date dtDidLetteraLicenziamento;

	public byte[] getContenuto() {
		return contenuto;
	}

	public void setContenuto(byte[] contenuto) {
		this.contenuto = contenuto;
	}

	public String getCodTipoStampa() {
		return codTipoStampa;
	}

	public void setCodTipoStampa(String codTipoStampa) {
		this.codTipoStampa = codTipoStampa;
	}

	public Integer getIdPfPrincipal() {
		return idPfPrincipal;
	}

	public void setIdPfPrincipal(Integer idPfPrincipal) {
		this.idPfPrincipal = idPfPrincipal;
	}

	public Boolean getFlgDidRischioDisoccupazione() {
		return flgDidRischioDisoccupazione;
	}

	public void setFlgDidRischioDisoccupazione(Boolean flgDidRischioDisoccupazione) {
		this.flgDidRischioDisoccupazione = flgDidRischioDisoccupazione;
	}

	public Date getDtDidLicenziamento() {
		return dtDidLicenziamento;
	}

	public void setDtDidLicenziamento(Date dtDidLicenziamento) {
		this.dtDidLicenziamento = dtDidLicenziamento;
	}

	public Date getDtDidLetteraLicenziamento() {
		return dtDidLetteraLicenziamento;
	}

	public void setDtDidLetteraLicenziamento(Date dtDidLetteraLicenziamento) {
		this.dtDidLetteraLicenziamento = dtDidLetteraLicenziamento;
	}

}
