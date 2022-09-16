/*
 * Created on Dec 6, 2007
 */
package it.eng.sil.cig.bean;

import java.math.BigDecimal;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanAttribute;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.command.std.StoredProcedureCommand;
import com.engiweb.framework.dbaccess.sql.result.DataResult;
import com.engiweb.framework.error.EMFErrorSeverity;
import com.engiweb.framework.error.EMFInternalError;

import it.eng.afExt.utils.TransactionQueryExecutor;

/**
 * @author Savino,Rodi
 */
public class UnitaAziendaBean {

	private String prgAzienda;
	private String prgUnita;
	private String strIndirizzo;
	private String flgSede;
	private String strRea;
	private String strLocalita;
	private String codCom;
	private String strCap;
	private String flgMezziPub;
	private String codAzStato;
	private String strResponsabile;
	private String strReferente;
	private String strTel;
	private String strFax;
	private String strEmail;
	private String codAteco;
	private String codCCNL;
	private String datInizio;
	private String datFine;
	private String strNote;
	private String cdnUtIns;
	private String cdnUtMod;
	private String strnumeroinps;
	private String strNumRegistroCommitt;
	private String datRegistroCommit;
	private String strRiferimentoSare;
	private String strRepartoInps;
	private String strDenominazione;
	private String strPECemail;

	private boolean flagNuova; // flag che indica se l'unità è già presente o meno su database
	private boolean flagUpdate; // flag che indica se è necessario aggiornare il database impostando questa come nuova
								// sede legale
	private boolean isSedeLegale;

	private TransactionQueryExecutor tex;

	private static final Logger _logger = Logger.getLogger(UnitaAziendaBean.class.getName());

	public UnitaAziendaBean(Object prgAzienda, String codCom, String codAzStato, String codAteco, String strIndirizzo,
			Object cdnUtIns, Object cdnUtMod, TransactionQueryExecutor tex) throws IllegalArgumentException {
		if (prgAzienda == null)
			throw new IllegalArgumentException("Il prgAzienda non puo' essere null.");
		if (codCom == null)
			throw new IllegalArgumentException("Il codCom non puo' essere null.");
		if (codAzStato == null)
			throw new IllegalArgumentException("Il codAzStato non puo' essere null.");
		if (codAteco == null)
			throw new IllegalArgumentException("Il codAteco non puo' essere null.");
		if (strIndirizzo == null)
			throw new IllegalArgumentException("Il strIndirizzo non puo' essere null.");
		if (cdnUtIns == null)
			throw new IllegalArgumentException("Il cdnUtIns non puo' essere null.");
		if (cdnUtMod == null)
			throw new IllegalArgumentException("Il cdnUtMod non puo' essere null.");
		if (tex == null)
			throw new IllegalArgumentException("Il TransactionQueryExecutor non puo' essere null.");
		//
		this.prgAzienda = prgAzienda.toString();
		this.codCom = codCom;
		this.codAzStato = codAzStato;
		this.codAteco = codAteco;
		this.strIndirizzo = strIndirizzo;
		this.cdnUtIns = cdnUtIns.toString();
		this.cdnUtMod = cdnUtMod.toString();
		this.flagNuova = false;
		this.flagUpdate = false;
		this.isSedeLegale = false;
		this.tex = tex;
	}

	public UnitaAziendaBean(String codCom, String codAzStato, String codAteco, String strIndirizzo, Object cdnUtIns,
			Object cdnUtMod, TransactionQueryExecutor tex) throws IllegalArgumentException {
		if (codCom == null)
			throw new IllegalArgumentException("Il codCom non puo' essere null.");
		if (codAzStato == null)
			throw new IllegalArgumentException("Il codAzStato non puo' essere null.");
		if (codAteco == null)
			throw new IllegalArgumentException("Il codAteco non puo' essere null.");
		if (strIndirizzo == null)
			throw new IllegalArgumentException("Il strIndirizzo non puo' essere null.");
		if (cdnUtIns == null)
			throw new IllegalArgumentException("Il cdnUtIns non puo' essere null.");
		if (cdnUtMod == null)
			throw new IllegalArgumentException("Il cdnUtMod non puo' essere null.");
		if (tex == null)
			throw new IllegalArgumentException("Il TransactionQueryExecutor non puo' essere null.");
		//
		this.codCom = codCom;
		this.codAzStato = codAzStato;
		this.codAteco = codAteco;
		this.strIndirizzo = strIndirizzo;
		this.cdnUtIns = cdnUtIns.toString();
		this.cdnUtMod = cdnUtMod.toString();
		this.flagNuova = false;
		this.isSedeLegale = false;
		this.tex = tex;
	}

	public UnitaAziendaBean(Object prgAzienda, String codCom, String codAzStato, String codAteco, String strIndirizzo,
			String codCCNLUAz, Object cdnUtIns, Object cdnUtMod, TransactionQueryExecutor tex)
			throws IllegalArgumentException {
		if (prgAzienda == null)
			throw new IllegalArgumentException("Il prgAzienda non puo' essere null.");
		if (codCom == null)
			throw new IllegalArgumentException("Il codCom non puo' essere null.");
		if (codAzStato == null)
			throw new IllegalArgumentException("Il codAzStato non puo' essere null.");
		if (codAteco == null)
			throw new IllegalArgumentException("Il codAteco non puo' essere null.");
		if (strIndirizzo == null)
			throw new IllegalArgumentException("Il strIndirizzo non puo' essere null.");
		if (codCCNLUAz == null)
			throw new IllegalArgumentException("Il codCCNL non puo' essere null.");

		if (cdnUtIns == null)
			throw new IllegalArgumentException("Il cdnUtIns non puo' essere null.");
		if (cdnUtMod == null)
			throw new IllegalArgumentException("Il cdnUtMod non puo' essere null.");
		if (tex == null)
			throw new IllegalArgumentException("Il TransactionQueryExecutor non puo' essere null.");
		//
		this.prgAzienda = prgAzienda.toString();
		this.codCom = codCom;
		this.codAzStato = codAzStato;
		this.codAteco = codAteco;
		this.strIndirizzo = strIndirizzo;
		this.codCCNL = codCCNLUAz;
		this.cdnUtIns = cdnUtIns.toString();
		this.cdnUtMod = cdnUtMod.toString();
		this.flagNuova = false;
		this.isSedeLegale = false;
		this.tex = tex;
	}

	public UnitaAziendaBean(String codCom, String codAzStato, String codAteco, String strIndirizzo, String codCCNLUAz,
			Object cdnUtIns, Object cdnUtMod, TransactionQueryExecutor tex) throws IllegalArgumentException {
		if (codCom == null)
			throw new IllegalArgumentException("Il codCom non puo' essere null.");
		if (codAzStato == null)
			throw new IllegalArgumentException("Il codAzStato non puo' essere null.");
		if (codAteco == null)
			throw new IllegalArgumentException("Il codAteco non puo' essere null.");
		if (strIndirizzo == null)
			throw new IllegalArgumentException("Il strIndirizzo non puo' essere null.");
		if (codCCNLUAz == null)
			throw new IllegalArgumentException("Il codCCNL non puo' essere null.");
		if (cdnUtIns == null)
			throw new IllegalArgumentException("Il cdnUtIns non puo' essere null.");
		if (cdnUtMod == null)
			throw new IllegalArgumentException("Il cdnUtMod non puo' essere null.");
		if (tex == null)
			throw new IllegalArgumentException("Il TransactionQueryExecutor non puo' essere null.");
		//
		this.codCom = codCom;
		this.codAzStato = codAzStato;
		this.codAteco = codAteco;
		this.strIndirizzo = strIndirizzo;
		this.codCCNL = codCCNLUAz;
		this.cdnUtIns = cdnUtIns.toString();
		this.cdnUtMod = cdnUtMod.toString();
		this.flagNuova = false;
		this.isSedeLegale = false;
		this.tex = tex;
	}

	/**
	 * Inserisce una nuova unità e reimposta i flagSedeLegale
	 * 
	 * @throws EMFInternalError
	 */
	public void insert() throws EMFInternalError {

		_logger.info("la sede legale " + getStrIndirizzo() + " è nuova. La inserisco.");

		Object pUnita[] = new Object[29];
		Object nextPrgUnita = getNextPrgUnita();
		pUnita[0] = prgAzienda;
		pUnita[1] = nextPrgUnita;
		pUnita[2] = strIndirizzo;
		pUnita[3] = flgSede;
		pUnita[4] = strRea;
		pUnita[5] = strLocalita;
		pUnita[6] = codCom;
		pUnita[7] = strCap;
		pUnita[8] = flgMezziPub;
		pUnita[9] = codAzStato;
		pUnita[10] = strResponsabile;
		pUnita[11] = strReferente;
		pUnita[12] = strTel;
		pUnita[13] = strFax;
		pUnita[14] = strEmail;
		pUnita[15] = codAteco;
		pUnita[16] = codCCNL;
		pUnita[17] = datInizio;
		pUnita[18] = datFine;
		pUnita[19] = strNote;
		pUnita[20] = cdnUtIns;
		pUnita[21] = cdnUtMod;
		pUnita[22] = strnumeroinps;
		pUnita[23] = strNumRegistroCommitt;
		pUnita[24] = datRegistroCommit;
		pUnita[25] = strRiferimentoSare;
		pUnita[26] = strRepartoInps;
		pUnita[27] = strDenominazione;
		pUnita[28] = strPECemail;

		// controlliamo che esista il codAteco o la domanda CIG non puo' essere gestita.
		{
			Object checkAteco[] = new Object[1];
			checkAteco[0] = codAteco;

			SourceBean res = (SourceBean) tex.executeQuery("CONTROLLA_VALIDITA_ATECO", checkAteco, "SELECT");
			Vector<SourceBeanAttribute> rows = res.getAttributeAsVector("ROW");
			// String valid = row.get ("CDNLIVELLO");
			if (rows.isEmpty()) {
				throw new EMFInternalError(EMFErrorSeverity.ERROR,
						"Il codAteco all'interno dell'unita' azienda non e' valido. codAteco: " + codAteco + ".");
			}
		}

		tex.executeQuery("INSERT_UNITA_AZIENDA", pUnita, "INSERT");
		// solo se l'inserimento va a buon fine setto il prgUnita con cui e' stato registrato il record
		setPrgUnita(nextPrgUnita.toString());

		if ("S".equalsIgnoreCase(flgSede)) {
			// allora devo mettere ad 'N' i flag delle altre unità operative.
			reimpostaFlag();
		}
	}

	/**
	 * IMposta d N i flag di tutte le unità legate all'azienda, meno questa.
	 * 
	 * @throws EMFInternalError
	 */
	public void reimpostaFlag() throws EMFInternalError {
		_logger.info("reimposto i flag");
		DataConnection conn = tex.getDataConnection();
		StoredProcedureCommand command = (StoredProcedureCommand) conn
				.createStoredProcedureCommand("{ call ? := PG_GESTAMM.pdAggSedeAzienda(?,?,?) }");
		DataResult dr = null;

		ArrayList parameters = new ArrayList(4);
		parameters.add(conn.createDataField("result", Types.BIGINT, null));
		command.setAsOutputParameters(0);
		parameters.add(conn.createDataField("prgAzienda", Types.BIGINT, prgAzienda));
		command.setAsInputParameters(1);
		parameters.add(conn.createDataField("prgParUnita", Types.BIGINT, prgUnita));
		command.setAsInputParameters(2);
		parameters.add(conn.createDataField("cdnParUtMod", Types.BIGINT, cdnUtMod));
		command.setAsInputParameters(3);

		// dr = command.execute(parameters);
		command.execute(parameters);
	}

	private BigDecimal getNextPrgUnita() throws EMFInternalError {
		String query = "select s_an_unita_azienda.nextval from dual";
		SourceBean row = (SourceBean) tex.executeQueryByStringStatement(query, new Object[] {}, "SELECT");
		BigDecimal nextval = (BigDecimal) row.getAttribute("row.nextval");

		if (nextval == null)
			throw new EMFInternalError(EMFErrorSeverity.BLOCKING,
					"Impossibile calcolare la sequence per la tabella an_unita_azienda");
		return nextval;
	}

	public String getCdnUtIns() {
		return cdnUtIns;
	}

	public String getCdnUtMod() {
		return cdnUtMod;
	}

	public String getCodAteco() {
		return codAteco;
	}

	public String getCodAzStato() {
		return codAzStato;
	}

	public String getCodCCNL() {
		return codCCNL;
	}

	public String getCodCom() {
		return codCom;
	}

	public String getDatFine() {
		return datFine;
	}

	public String getDatInizio() {
		return datInizio;
	}

	public String getDatRegistroCommit() {
		return datRegistroCommit;
	}

	public String getFlgMezziPub() {
		return flgMezziPub;
	}

	public String getFlgSede() {
		return flgSede;
	}

	public String getPrgAzienda() {
		return prgAzienda;
	}

	public String getPrgUnita() {
		return prgUnita;
	}

	public String getStrCap() {
		return strCap;
	}

	public String getStrDenominazione() {
		return strDenominazione;
	}

	public String getStrEmail() {
		return strEmail;
	}

	public String getStrFax() {
		return strFax;
	}

	public String getStrIndirizzo() {
		return strIndirizzo;
	}

	public String getStrLocalita() {
		return strLocalita;
	}

	public String getStrNote() {
		return strNote;
	}

	public String getStrnumeroinps() {
		return strnumeroinps;
	}

	public String getStrNumRegistroCommitt() {
		return strNumRegistroCommitt;
	}

	public String getStrRea() {
		return strRea;
	}

	public String getStrReferente() {
		return strReferente;
	}

	public String getStrRepartoInps() {
		return strRepartoInps;
	}

	public String getStrResponsabile() {
		return strResponsabile;
	}

	public String getStrRiferimentoSare() {
		return strRiferimentoSare;
	}

	public String getStrTel() {
		return strTel;
	}

	public void setCdnUtIns(String string) {
		cdnUtIns = string;
	}

	public void setCdnUtMod(String string) {
		cdnUtMod = string;
	}

	public void setCodAteco(String string) {
		codAteco = string;
	}

	public void setCodAzStato(String string) {
		codAzStato = string;
	}

	public void setCodCCNL(String string) {
		codCCNL = string;
	}

	public void setCodCom(String string) {
		codCom = string;
	}

	public void setDatFine(String string) {
		datFine = string;
	}

	public void setDatInizio(String string) {
		datInizio = string;
	}

	public void setDatRegistroCommit(String string) {
		datRegistroCommit = string;
	}

	public void setFlgMezziPub(String string) {
		flgMezziPub = string;
	}

	public void setFlgSede(String string) {
		flgSede = string;
	}

	public void setPrgAzienda(String string) {
		prgAzienda = string;
	}

	public void setPrgUnita(String string) {
		prgUnita = string;
	}

	public void setStrCap(String string) {
		strCap = string;
	}

	public void setStrDenominazione(String string) {
		strDenominazione = string;
	}

	public void setStrEmail(String string) {
		strEmail = string;
	}

	public void setStrFax(String string) {
		strFax = string;
	}

	public void setStrIndirizzo(String string) {
		strIndirizzo = string;
	}

	public void setStrLocalita(String string) {
		strLocalita = string;
	}

	public void setStrNote(String string) {
		strNote = string;
	}

	public void setStrnumeroinps(String string) {
		strnumeroinps = string;
	}

	public void setStrNumRegistroCommitt(String string) {
		strNumRegistroCommitt = string;
	}

	public void setStrRea(String string) {
		strRea = string;
	}

	public void setStrReferente(String string) {
		strReferente = string;
	}

	public void setStrRepartoInps(String string) {
		strRepartoInps = string;
	}

	public void setStrResponsabile(String string) {
		strResponsabile = string;
	}

	public void setStrRiferimentoSare(String string) {
		strRiferimentoSare = string;
	}

	public void setStrTel(String string) {
		strTel = string;
	}

	public boolean isFlagNuova() {
		return flagNuova;
	}

	public void setFlagNuova(boolean flagNuova) {
		this.flagNuova = flagNuova;
	}

	public boolean isSedeLegale() {
		return isSedeLegale;
	}

	public void setSedeLegale(boolean isSedeLegale) {
		this.isSedeLegale = isSedeLegale;
	}

	public boolean isFlagUpdate() {
		return flagUpdate;
	}

	public void setFlagUpdate(boolean flagUpdate) {
		this.flagUpdate = flagUpdate;
	}

	public String getStrPECemail() {
		return strPECemail;
	}

	public void setStrPECemail(String strPECemail) {
		this.strPECemail = strPECemail;
	}

}
