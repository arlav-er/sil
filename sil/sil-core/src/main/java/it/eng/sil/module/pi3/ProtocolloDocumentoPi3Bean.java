package it.eng.sil.module.pi3;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class ProtocolloDocumentoPi3Bean implements Serializable {

	private static final long serialVersionUID = -7687639310267240438L;

	private BigDecimal prgProtDocPitre;
	private BigDecimal prgProtPitre;
	private String flgPrincipale;
	private String flgNotificaAnnullamento;
	private BigDecimal prgDocumento;
	private BigDecimal cdnUtins;
	private BigDecimal cdnUtMod;
	private Date dtMins;
	private Date dtmMod;
	private Date datInvio;
	private String codStatoInvio;
	private BigDecimal numKloProtDocumento;

	public BigDecimal getPrgProtDocPitre() {
		return prgProtDocPitre;
	}

	public void setPrgProtDocPitre(BigDecimal prgProtDocPitre) {
		this.prgProtDocPitre = prgProtDocPitre;
	}

	public BigDecimal getPrgProtPitre() {
		return prgProtPitre;
	}

	public void setPrgProtPitre(BigDecimal prgProtPitre) {
		this.prgProtPitre = prgProtPitre;
	}

	public String getFlgPrincipale() {
		return flgPrincipale;
	}

	public void setFlgPrincipale(String flgPrincipale) {
		this.flgPrincipale = flgPrincipale;
	}

	public String getFlgNotificaAnnullamento() {
		return flgNotificaAnnullamento;
	}

	public void setFlgNotificaAnnullamento(String flgNotificaAnnullamento) {
		this.flgNotificaAnnullamento = flgNotificaAnnullamento;
	}

	public BigDecimal getPrgDocumento() {
		return prgDocumento;
	}

	public void setPrgDocumento(BigDecimal prgDocumento) {
		this.prgDocumento = prgDocumento;
	}

	public BigDecimal getCdnUtins() {
		return cdnUtins;
	}

	public void setCdnUtins(BigDecimal cdnUtins) {
		this.cdnUtins = cdnUtins;
	}

	public BigDecimal getCdnUtMod() {
		return cdnUtMod;
	}

	public void setCdnUtMod(BigDecimal cdnUtMod) {
		this.cdnUtMod = cdnUtMod;
	}

	public Date getDtMins() {
		return dtMins;
	}

	public void setDtMins(Date dtMins) {
		this.dtMins = dtMins;
	}

	public Date getDtmMod() {
		return dtmMod;
	}

	public void setDtmMod(Date dtmMod) {
		this.dtmMod = dtmMod;
	}

	public Date getDatInvio() {
		return datInvio;
	}

	public void setDatInvio(Date datInvio) {
		this.datInvio = datInvio;
	}

	public String getCodStatoInvio() {
		return codStatoInvio;
	}

	public void setCodStatoInvio(String codStatoInvio) {
		this.codStatoInvio = codStatoInvio;
	}

	public BigDecimal getNumKloProtDocumento() {
		return numKloProtDocumento;
	}

	public void setNumKloProtDocumento(BigDecimal numKloProtDocumento) {
		this.numKloProtDocumento = numKloProtDocumento;
	}

}
