package it.eng.sil.module.consenso;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class ConsensoFirmaBean implements Serializable {

	private static final long serialVersionUID = -8145503937284137359L;

	private BigDecimal cdnLav;
	private BigDecimal prgAzienda;
	private String sistemaOrigine;
	private String codiceStatoConsenso;
	private Date dataRegistrazione;
	private Date dataRevoca;
	private BigDecimal cdnUtins;
	private Date dtMins;
	private BigDecimal cdnUtMod;
	private Date dtmMod;

	public BigDecimal getCdnLav() {
		return cdnLav;
	}

	public void setCdnLav(BigDecimal cdnLav) {
		this.cdnLav = cdnLav;
	}

	public BigDecimal getPrgAzienda() {
		return prgAzienda;
	}

	public void setPrgAzienda(BigDecimal prgAzienda) {
		this.prgAzienda = prgAzienda;
	}

	public String getSistemaOrigine() {
		return sistemaOrigine;
	}

	public void setSistemaOrigine(String sistemaOrigine) {
		this.sistemaOrigine = sistemaOrigine;
	}

	public String getCodiceStatoConsenso() {
		return codiceStatoConsenso;
	}

	public void setCodiceStatoConsenso(String codiceStatoConsenso) {
		this.codiceStatoConsenso = codiceStatoConsenso;
	}

	public Date getDataRegistrazione() {
		return dataRegistrazione;
	}

	public void setDataRegistrazione(Date dataRegistrazione) {
		this.dataRegistrazione = dataRegistrazione;
	}

	public Date getDataRevoca() {
		return dataRevoca;
	}

	public void setDataRevoca(Date dataRevoca) {
		this.dataRevoca = dataRevoca;
	}

	public BigDecimal getCdnUtins() {
		return cdnUtins;
	}

	public void setCdnUtins(BigDecimal cdnUtins) {
		this.cdnUtins = cdnUtins;
	}

	public Date getDtMins() {
		return dtMins;
	}

	public void setDtMins(Date dtMins) {
		this.dtMins = dtMins;
	}

	public BigDecimal getCdnUtMod() {
		return cdnUtMod;
	}

	public void setCdnUtMod(BigDecimal cdnUtMod) {
		this.cdnUtMod = cdnUtMod;
	}

	public Date getDtmMod() {
		return dtmMod;
	}

	public void setDtmMod(Date dtmMod) {
		this.dtmMod = dtmMod;
	}

}
