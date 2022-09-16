package it.eng.sil.cig.bean;

import java.math.BigDecimal;

import org.apache.log4j.Logger;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.error.EMFErrorSeverity;
import com.engiweb.framework.error.EMFInternalError;

import it.eng.afExt.utils.TransactionQueryExecutor;

/**
 * 
 * @author Esposito,Rodi
 *
 */
public class CISindacato {
	private String prgsindaccordo; // not null
	private String prgaccordo; // not null
	private String strcognome; // not null
	private String strnome; // not null
	private String strsindacato; // not null
	private String cdnutins; // not null
	private String dtmins; // not null
	private String cdnutmod; // not null
	private String dtmmod; // not null
	private String numklosind = "0"; // not null

	private TransactionQueryExecutor tex;

	private static final Logger _logger = Logger.getLogger(CISindacato.class.getName());

	public CISindacato(String strcognome, String strnome, String strsindacato, Object cdnUtIns, Object cdnUtMod,
			TransactionQueryExecutor tex) {
		_logger.info("Costruisco un nuovo bean sindacato " + strsindacato + ".");
		if (strcognome == null)
			throw new IllegalArgumentException("Il strcognome non puo' essere null.");
		if (strnome == null)
			throw new IllegalArgumentException("Il strnome non puo' essere null.");
		if (strsindacato == null)
			throw new IllegalArgumentException("Il strsindacato non puo' essere null.");
		if (cdnUtIns == null)
			throw new IllegalArgumentException("Il cdnUtIns non puo' essere null.");
		if (cdnUtMod == null)
			throw new IllegalArgumentException("Il cdnUtMod non puo' essere null.");
		if (tex == null)
			throw new IllegalArgumentException("Il TransactionQueryExecutor non puo' essere null.");
		//
		this.strcognome = strcognome;
		this.strnome = strnome;
		this.strsindacato = strsindacato;
		this.cdnutins = cdnUtIns.toString();
		this.cdnutmod = cdnUtMod.toString();
		this.tex = tex;
	}

	public void insert() throws EMFInternalError {

		_logger.info("inserisco sindacato " + strsindacato);
		Object pSindacato[] = new Object[8];
		Object nextPrgSindacato = getNextPrgSindacato();

		pSindacato[0] = nextPrgSindacato;
		pSindacato[1] = prgaccordo;
		pSindacato[2] = strcognome;
		pSindacato[3] = strnome;
		pSindacato[4] = strsindacato;
		pSindacato[5] = cdnutins;
		pSindacato[6] = cdnutmod;
		pSindacato[7] = numklosind;

		tex.executeQuery("INSERT_CI_SINDACATO", pSindacato, "INSERT");
		// solo se l'inserimento va a buon fine setto il prgsindaccordo con cui e' stato registrato il record
		setPrgsindaccordo(nextPrgSindacato.toString());
	}

	private BigDecimal getNextPrgSindacato() throws EMFInternalError {

		String query = "select s_ci_sindacato.nextval from dual";
		SourceBean row = (SourceBean) tex.executeQueryByStringStatement(query, new Object[] {}, "SELECT");
		BigDecimal nextval = (BigDecimal) row.getAttribute("row.nextval");

		if (nextval == null)
			throw new EMFInternalError(EMFErrorSeverity.BLOCKING,
					"Impossibile calcolare la sequence per la tabella ci_sindacato");
		return nextval;
	}

	public String getPrgsindaccordo() {
		return prgsindaccordo;
	}

	public void setPrgsindaccordo(String prgsindaccordo) {
		this.prgsindaccordo = prgsindaccordo;
	}

	public String getPrgaccordo() {
		return prgaccordo;
	}

	public void setPrgaccordo(String prgaccordo) {
		this.prgaccordo = prgaccordo;
	}

	public String getStrcognome() {
		return strcognome;
	}

	public void setStrcognome(String strcognome) {
		this.strcognome = strcognome;
	}

	public String getStrnome() {
		return strnome;
	}

	public void setStrnome(String strnome) {
		this.strnome = strnome;
	}

	public String getStrsindacato() {
		return strsindacato;
	}

	public void setStrsindacato(String strsindacato) {
		this.strsindacato = strsindacato;
	}

	public String getCdnutins() {
		return cdnutins;
	}

	public void setCdnutins(String cdnutins) {
		this.cdnutins = cdnutins;
	}

	public String getDtmins() {
		return dtmins;
	}

	public void setDtmins(String dtmins) {
		this.dtmins = dtmins;
	}

	public String getCdnutmod() {
		return cdnutmod;
	}

	public void setCdnutmod(String cdnutmod) {
		this.cdnutmod = cdnutmod;
	}

	public String getDtmmod() {
		return dtmmod;
	}

	public void setDtmmod(String dtmmod) {
		this.dtmmod = dtmmod;
	}

	public String getNumklosind() {
		return numklosind;
	}

	public void setNumklosind(String numklosind) {
		this.numklosind = numklosind;
	}

	public TransactionQueryExecutor getTex() {
		return tex;
	}

	public void setTex(TransactionQueryExecutor tex) {
		this.tex = tex;
	}
}
