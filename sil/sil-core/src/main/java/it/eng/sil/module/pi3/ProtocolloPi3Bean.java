package it.eng.sil.module.pi3;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class ProtocolloPi3Bean implements Serializable {

	private static final long serialVersionUID = 7751796622762742124L;

	private BigDecimal prgProtPitre;
	private String strSegnatura;
	private String stridDoc;
	private Date dataProt;
	private String strMittente; // idUtenteMittente di SPIL (cdnLavoratore o prgAzienda o codCpi UO)
	private String strOggetto;
	private String strDestinatario; // idUtenteDestinatario di SPIL (cdnLavoratore o prgAzienda o codCpi UO)
	private BigDecimal prgTitolario;
	private BigDecimal cdnUtMod;
	private BigDecimal cdnUtins;
	private Date dtmMod;
	private Date dtMins;
	private String strNumPratica;
	private String strMittentePi3; // idUtenteMittente di PI3 (AM_PROTOCOLLO_PITRE.STRMITPROT)
	private String strDestinatarioPi3; // idUtenteDestinatario di PI3 (AM_PROTOCOLLO_PITRE.STRDESPROT)
	private BigDecimal numKloProtocollo;

	public BigDecimal getPrgProtPitre() {
		return prgProtPitre;
	}

	public void setPrgProtPitre(BigDecimal prgProtPitre) {
		this.prgProtPitre = prgProtPitre;
	}

	public String getStrSegnatura() {
		return strSegnatura;
	}

	public void setStrSegnatura(String strSegnatura) {
		this.strSegnatura = strSegnatura;
	}

	public String getStridDoc() {
		return stridDoc;
	}

	public void setStridDoc(String stridDoc) {
		this.stridDoc = stridDoc;
	}

	public Date getDataProt() {
		return dataProt;
	}

	public void setDataProt(Date dataProt) {
		this.dataProt = dataProt;
	}

	public String getStrMittente() {
		return strMittente;
	}

	public void setStrMittente(String strMittente) {
		this.strMittente = strMittente;
	}

	public String getStrOggetto() {
		return strOggetto;
	}

	public void setStrOggetto(String strOggetto) {
		this.strOggetto = strOggetto;
	}

	public String getStrDestinatario() {
		return strDestinatario;
	}

	public void setStrDestinatario(String strDestinatario) {
		this.strDestinatario = strDestinatario;
	}

	public BigDecimal getPrgTitolario() {
		return prgTitolario;
	}

	public void setPrgTitolario(BigDecimal prgTitolario) {
		this.prgTitolario = prgTitolario;
	}

	public BigDecimal getCdnUtMod() {
		return cdnUtMod;
	}

	public void setCdnUtMod(BigDecimal cdnUtMod) {
		this.cdnUtMod = cdnUtMod;
	}

	public BigDecimal getCdnUtins() {
		return cdnUtins;
	}

	public void setCdnUtins(BigDecimal cdnUtins) {
		this.cdnUtins = cdnUtins;
	}

	public Date getDtmMod() {
		return dtmMod;
	}

	public void setDtmMod(Date dtmMod) {
		this.dtmMod = dtmMod;
	}

	public Date getDtMins() {
		return dtMins;
	}

	public void setDtMins(Date dtMins) {
		this.dtMins = dtMins;
	}

	public String getStrNumPratica() {
		return strNumPratica;
	}

	public void setStrNumPratica(String strNumPratica) {
		this.strNumPratica = strNumPratica;
	}

	public String getStrMittentePi3() {
		return strMittentePi3;
	}

	public void setStrMittentePi3(String strMittentePi3) {
		this.strMittentePi3 = strMittentePi3;
	}

	public String getStrDestinatarioPi3() {
		return strDestinatarioPi3;
	}

	public void setStrDestinatarioPi3(String strDestinatarioPi3) {
		this.strDestinatarioPi3 = strDestinatarioPi3;
	}

	public BigDecimal getNumKloProtocollo() {
		return numKloProtocollo;
	}

	public void setNumKloProtocollo(BigDecimal numKloProtocollo) {
		this.numKloProtocollo = numKloProtocollo;
	}

}
