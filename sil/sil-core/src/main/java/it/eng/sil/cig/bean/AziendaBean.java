/*
 * Created on Dec 6, 2007
 */
package it.eng.sil.cig.bean;

import java.math.BigDecimal;

import org.apache.log4j.Logger;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.error.EMFErrorSeverity;
import com.engiweb.framework.error.EMFInternalError;

import it.eng.afExt.utils.TransactionQueryExecutor;

/**
 * @author Savino,Rodi
 */
public class AziendaBean {
	private String prgAzienda; // not null
	private String strCodiceFiscale; // not null
	private String strPartitaIva;
	private String strRagioneSociale; // not null
	private String codNatGiuridica;
	private String codTipoAzienda; // not null
	private String strSitoInternet;
	private String strDescAttivita;
	private String numSoci;
	private String numDipendenti;
	private String numCollaboratori;
	private String numAltraPosizione;
	private String datInizio;
	private String datFine;
	private String dataGGInformazione;
	private String strHistory;
	private String strNote;
	private String cdnUtIns; // not null
	private String cdnUtMod; // not null
	private String flgdatiok;
	private String strNumAlboInterinali;
	private String strRepartoInail;
	private String strPatInail;
	private String flgObbligoL68;
	private String strNumAgSomministrazione;

	private boolean flagNuova;

	private TransactionQueryExecutor tex;

	private final Logger _logger = Logger.getLogger(AziendaBean.class.getName());

	public AziendaBean(String strCodiceFiscale, String strRagioneSociale, String codTipoAzienda, Object cdnUtIns,
			Object cdnUtMod, TransactionQueryExecutor tex) {
		if (strCodiceFiscale == null)
			throw new IllegalArgumentException("Il strCodiceFiscale non puo' essere null.");
		if (strRagioneSociale == null)
			throw new IllegalArgumentException("Il strRagioneSociale non puo' essere null.");
		if (codTipoAzienda == null)
			throw new IllegalArgumentException("Il codTipoAzienda non puo' essere null.");
		if (cdnUtIns == null)
			throw new IllegalArgumentException("Il cdnUtIns non puo' essere null.");
		if (cdnUtMod == null)
			throw new IllegalArgumentException("Il cdnUtMod non puo' essere null.");
		if (tex == null)
			throw new IllegalArgumentException("Il TransactionQueryExecutor non puo' essere null.");
		//
		this.strCodiceFiscale = strCodiceFiscale;
		this.strRagioneSociale = strRagioneSociale;
		this.codTipoAzienda = codTipoAzienda;
		this.cdnUtIns = cdnUtIns.toString();
		this.cdnUtMod = cdnUtMod.toString();
		this.flagNuova = false;
		this.tex = tex;
	}

	public AziendaBean(String strCodiceFiscale, String strRagioneSociale, String codTipoAzienda, String codNatGiuridica,
			Object cdnUtIns, Object cdnUtMod, TransactionQueryExecutor tex) {
		_logger.info("Inizio la costruzione del bean azienda.");
		if (strCodiceFiscale == null)
			throw new IllegalArgumentException("Il strCodiceFiscale non puo' essere null.");
		if (strRagioneSociale == null)
			throw new IllegalArgumentException("Il strRagioneSociale non puo' essere null.");
		if (codTipoAzienda == null)
			throw new IllegalArgumentException("Il codTipoAzienda non puo' essere null.");
		// if (codNatGiuridica==null) throw new IllegalArgumentException("Il codNatGiuridica non puo' essere null.");
		if (cdnUtIns == null)
			throw new IllegalArgumentException("Il cdnUtIns non puo' essere null.");
		if (cdnUtMod == null)
			throw new IllegalArgumentException("Il cdnUtMod non puo' essere null.");
		if (tex == null)
			throw new IllegalArgumentException("Il TransactionQueryExecutor non puo' essere null.");
		//
		this.strCodiceFiscale = strCodiceFiscale;
		this.strRagioneSociale = strRagioneSociale;
		this.codTipoAzienda = codTipoAzienda;
		this.codNatGiuridica = codNatGiuridica;
		this.cdnUtIns = cdnUtIns.toString();
		this.cdnUtMod = cdnUtMod.toString();
		this.flagNuova = false;
		this.tex = tex;
	}

	public void insert() throws EMFInternalError {
		Object pAzienda[] = new Object[25];
		Object nextPrgAzienda = getNextPrgAzienda();
		pAzienda[0] = nextPrgAzienda;
		pAzienda[1] = strCodiceFiscale;
		pAzienda[2] = strPartitaIva;
		pAzienda[3] = strRagioneSociale;
		pAzienda[4] = codNatGiuridica;
		pAzienda[5] = codTipoAzienda;
		pAzienda[6] = strSitoInternet;
		pAzienda[7] = strDescAttivita;
		pAzienda[8] = numSoci;
		pAzienda[9] = numDipendenti;
		pAzienda[10] = numCollaboratori;
		pAzienda[11] = numAltraPosizione;
		pAzienda[12] = datInizio;
		pAzienda[13] = datFine;
		pAzienda[14] = dataGGInformazione;
		pAzienda[15] = strHistory;
		pAzienda[16] = strNote;
		pAzienda[17] = cdnUtIns;
		pAzienda[18] = cdnUtMod;
		pAzienda[19] = flgdatiok;
		pAzienda[20] = strNumAlboInterinali;
		pAzienda[21] = strRepartoInail;
		pAzienda[22] = strPatInail;
		pAzienda[23] = flgObbligoL68;
		pAzienda[24] = strNumAgSomministrazione;

		tex.executeQuery("INSERT_TESTATA_AZIENDA", pAzienda, "INSERT");
		// solo se l'inserimento va a buon fine setto il prgAzienda con cui e' stato registrato il record
		setPrgAzienda(nextPrgAzienda.toString());
	}

	private BigDecimal getNextPrgAzienda() throws EMFInternalError {

		String query = "select s_an_azienda.nextval from dual";
		SourceBean row = (SourceBean) tex.executeQueryByStringStatement(query, new Object[] {}, "SELECT");
		BigDecimal nextval = (BigDecimal) row.getAttribute("row.nextval");

		if (nextval == null)
			throw new EMFInternalError(EMFErrorSeverity.BLOCKING,
					"Impossibile calcolare la sequence per la tabella an_azienda");
		return nextval;

	}

	public String getCdnUtins() {
		return cdnUtIns;
	}

	public String getCdnUtmod() {
		return cdnUtMod;
	}

	public String getCodNatGiuridica() {
		return codNatGiuridica;
	}

	public String getCodTipoAzienda() {
		return codTipoAzienda;
	}

	public String getDataGGInformazione() {
		return dataGGInformazione;
	}

	public String getDatFine() {
		return datFine;
	}

	public String getDatInizio() {
		return datInizio;
	}

	public String getFlgdatiok() {
		return flgdatiok;
	}

	public String getFlgObbligoL68() {
		return flgObbligoL68;
	}

	public String getNumAltraPosizione() {
		return numAltraPosizione;
	}

	public String getNumCollaboratori() {
		return numCollaboratori;
	}

	public String getNumDipendenti() {
		return numDipendenti;
	}

	public String getNumSoci() {
		return numSoci;
	}

	public String getStrPartitaIva() {
		return strPartitaIva;
	}

	public String getPrgAzienda() {
		return prgAzienda;
	}

	public String getStrRagioneSociale() {
		return strRagioneSociale;
	}

	public String getStrCodiceFiscale() {
		return strCodiceFiscale;
	}

	public String getStrDescAttivita() {
		return strDescAttivita;
	}

	public String getStrHistory() {
		return strHistory;
	}

	public String getStrNote() {
		return strNote;
	}

	public String getStrNumAgSomministrazione() {
		return strNumAgSomministrazione;
	}

	public String getStrNumAlboInterinali() {
		return strNumAlboInterinali;
	}

	public String getStrPatInail() {
		return strPatInail;
	}

	public String getStrRepartoInail() {
		return strRepartoInail;
	}

	public String getStrSitoInternet() {
		return strSitoInternet;
	}

	public void setCdnUtins(String string) {
		cdnUtIns = string;
	}

	public void setCdnUtmod(String string) {
		cdnUtMod = string;
	}

	public void setCodNatGiuridica(String string) {
		codNatGiuridica = string;
	}

	public void setCodTipoAzienda(String string) {
		codTipoAzienda = string;
	}

	public void setDataGGInformazione(String string) {
		dataGGInformazione = string;
	}

	public void setDatFine(String string) {
		datFine = string;
	}

	public void setDatInizio(String string) {
		datInizio = string;
	}

	public void setFlgdatiok(String string) {
		flgdatiok = string;
	}

	public void setFlgObbligoL68(String string) {
		flgObbligoL68 = string;
	}

	public void setNumAltraPosizione(String string) {
		numAltraPosizione = string;
	}

	public void setNumCollaboratori(String string) {
		numCollaboratori = string;
	}

	public void setNumDipendenti(String string) {
		numDipendenti = string;
	}

	public void setNumSoci(String string) {
		numSoci = string;
	}

	public void setStrPartitaIva(String string) {
		strPartitaIva = string;
	}

	public void setPrgAzienda(String string) {
		prgAzienda = string;
	}

	public void setRagioneSocialeNuova(String string) {
		strRagioneSociale = string;
	}

	public void setStrCodiceFiscale(String string) {
		strCodiceFiscale = string;
	}

	public void setStrDescAttivita(String string) {
		strDescAttivita = string;
	}

	public void setStrHistory(String string) {
		strHistory = string;
	}

	public void setStrNote(String string) {
		strNote = string;
	}

	public void setStrNumAgSomministrazione(String string) {
		strNumAgSomministrazione = string;
	}

	public void setStrNumAlboInterinali(String string) {
		strNumAlboInterinali = string;
	}

	public void setStrPatInail(String string) {
		strPatInail = string;
	}

	public void setStrRepartoInail(String string) {
		strRepartoInail = string;
	}

	public void setStrSitoInternet(String string) {
		strSitoInternet = string;
	}

	public boolean isFlagNuova() {
		return flagNuova;
	}

	public void setFlagNuova(boolean flagNuova) {
		this.flagNuova = flagNuova;
	}

}
