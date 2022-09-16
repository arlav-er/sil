/*
 * Created on Jan 24, 2008
 */
package it.eng.sil.bean;

import java.math.BigDecimal;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.error.EMFErrorSeverity;
import com.engiweb.framework.error.EMFInternalError;

import it.eng.afExt.utils.TransactionQueryExecutor;

/**
 * @author savino
 */
public class AzReferenteBean {
	private String prgAzReferente;
	private String prgAzienda;
	private String prgUnita;
	private String strCognome;
	private String strNome;
	private String codRuoloAz;
	private String strTelefono;
	private String strFax;
	private String strEmail;
	private String cdnUtIns;
	private String cdnUtMod;

	private TransactionQueryExecutor tex;

	public AzReferenteBean(Object prgAzienda, Object prgUnita, String strCognome, String strTelefono, String codRuoloAz,
			Object cdnUtIns, TransactionQueryExecutor tex) throws IllegalArgumentException {
		if (prgAzienda == null)
			throw new IllegalArgumentException("Il prgAzienda non puo' essere null.");
		if (prgUnita == null)
			throw new IllegalArgumentException("Il prgUnita non puo' essere null.");
		if (strCognome == null)
			throw new IllegalArgumentException("Il strCognome non puo' essere null.");
		if (strTelefono == null)
			throw new IllegalArgumentException("Il strTelefono non puo' essere null.");
		if (codRuoloAz == null)
			throw new IllegalArgumentException("Il codRuoloAz non puo' essere null.");
		if (cdnUtIns == null)
			throw new IllegalArgumentException("Il cdnUtIns non puo' essere null.");

		this.prgAzienda = prgAzienda.toString();
		this.prgUnita = prgUnita.toString();
		this.strCognome = strCognome;
		this.strTelefono = strTelefono;
		this.codRuoloAz = codRuoloAz;
		this.cdnUtIns = (String) cdnUtIns;
		this.cdnUtMod = (String) cdnUtIns;
		this.tex = tex;

	}

	public void insert() throws EMFInternalError {
		Object pReferente[] = new Object[11];
		Object nextPrgAzReferente = getNextPrgAzReferente();
		pReferente[0] = nextPrgAzReferente;
		pReferente[1] = prgAzienda;
		pReferente[2] = prgUnita;
		pReferente[3] = strCognome;
		pReferente[4] = strNome;
		pReferente[5] = codRuoloAz;
		pReferente[6] = strTelefono;
		pReferente[7] = strFax;
		pReferente[8] = strEmail;
		pReferente[9] = cdnUtIns;
		pReferente[10] = cdnUtMod;

		tex.executeQuery("COOP_INSERT_REFERENZA", pReferente, "INSERT");
		// solo se l'inserimento va a buon fine setto il prgUnita con cui e' stato registrato il record
		setPrgAzReferente(nextPrgAzReferente.toString());
	}

	public Object getPrgAzReferente() {
		return this.prgAzReferente;
	}

	/**
	 * @param string
	 */
	private void setPrgAzReferente(String prgAzReferente) {
		this.prgAzReferente = prgAzReferente;

	}

	private BigDecimal getNextPrgAzReferente() throws EMFInternalError {
		String query = "select s_an_az_referente.nextval from dual";
		SourceBean row = (SourceBean) tex.executeQueryByStringStatement(query, new Object[] {}, "SELECT");
		BigDecimal nextval = (BigDecimal) row.getAttribute("row.nextval");

		if (nextval == null)
			throw new EMFInternalError(EMFErrorSeverity.BLOCKING,
					"Impossibile calcolare la sequence per la tabella an_az_referente");
		return nextval;
	}

	public void setStrNome(String strNome) {
		this.strNome = strNome;
	}

	public void setStrEmail(String strEmail) {
		this.strEmail = strEmail;
	}

	public void setStrFax(String strFax) {
		this.strFax = strFax;
	}

}
