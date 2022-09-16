/*
 * Creato il 17-set-04
 */
package it.eng.sil.module.movimenti.processors;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Map;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.movimenti.RecordProcessor;
import it.eng.sil.security.User;
import it.eng.sil.util.amministrazione.impatti.CalcolaCpiLav;

/**
 * @author roccetti Processor che inserisce nella tabella AN_LAV_STORIA_INF i dati corretti per il CPI del lavoratore
 *         appena inserito.
 */
public class UpdateCpiLav implements RecordProcessor {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(UpdateCpiLav.class.getName());

	private String name;
	private String classname = this.getClass().getName();
	/** TransactionQueryExecutor da utilizzare per la query di insert */
	private TransactionQueryExecutor trans;
	/** Identificatore utente */
	private BigDecimal userId = null;

	private String findRegioneHeader = "Select De_REGIONE.CODREGIONE CODREGIONE, "
			+ "de_provincia.CODPROVINCIA CODPROVINCIA " + "from de_provincia inner join de_cpi "
			+ "on (de_provincia.CODPROVINCIA = de_cpi.CODPROVINCIA) " + "inner join de_regione "
			+ "on (de_regione.CODREGIONE = de_provincia.CODREGIONE) " + "where de_cpi.CODCPI = '";
	private String findRegioneFooter = "'";

	public UpdateCpiLav(String name, TransactionQueryExecutor transexec, BigDecimal user) throws NullPointerException {
		this.name = name;
		trans = transexec;
		if (user == null) {
			throw new NullPointerException("Identificatore utente nullo");
		}
		userId = user;
	}

	public SourceBean processRecord(Map record) throws SourceBeanException {

		// vettore delle warnings da restituire
		ArrayList warnings = new ArrayList();
		// Vettore dei risultati annidati da restituire
		ArrayList nested = new ArrayList();

		// Impostazione varibili
		String provinciaLav = null;
		String regioneLav = null;
		String provinciaRif = null;
		String regioneRif = null;
		String codCpiRif = null;
		String codCpiLav = (String) record.get("CODCPILAV");
		String codComDomLav = (String) record.get("CODCOMDOM");
		BigDecimal cdnLavoratore = (BigDecimal) record.get("CDNLAVORATORE");

		// Recupero dati della regione e della provinicia del lavoratore
		String statement = findRegioneHeader + codCpiLav + findRegioneFooter;
		SourceBean result = null;
		try {
			result = ProcessorsUtils.executeSelectQuery(statement, trans);
		} catch (Exception e) {
			return ProcessorsUtils.createResponse(name, classname, new Integer(MessageCodes.ImportMov.ERR_INSERT_LAV),
					"Impossibile cercare i dati della provincia e regione del lavoratore. ", warnings, nested);
		}

		// Esamino il risultato
		provinciaLav = (String) ((SourceBean) result).getAttribute("ROW.CODPROVINCIA");
		regioneLav = (String) ((SourceBean) result).getAttribute("ROW.CODREGIONE");

		// Recupero Cpi utente collegato
		RequestContainer requestContainer = RequestContainer.getRequestContainer();
		SessionContainer sessionContainer = (SessionContainer) requestContainer.getSessionContainer();
		User user = (User) sessionContainer.getAttribute(User.USERID);
		codCpiRif = user.getCodRif();

		// Recupero dati della regione e della provinicia dell'utente collegato
		statement = findRegioneHeader + codCpiRif + findRegioneFooter;
		result = null;
		try {
			result = ProcessorsUtils.executeSelectQuery(statement, trans);
		} catch (Exception e) {
			return ProcessorsUtils.createResponse(name, classname, new Integer(MessageCodes.ImportMov.ERR_INSERT_LAV),
					"Impossibile cercare i dati della provincia e regione dell'utente collegato. ", warnings, nested);
		}

		// Esamino il risultato
		provinciaRif = (String) ((SourceBean) result).getAttribute("ROW.CODPROVINCIA");
		regioneRif = (String) ((SourceBean) result).getAttribute("ROW.CODREGIONE");

		// calcolo cpi del lavoratore
		CalcolaCpiLav calcolatoreCpi = null;
		try {
			calcolatoreCpi = new CalcolaCpiLav(regioneLav, regioneRif, provinciaLav, provinciaRif, codCpiLav,
					codCpiRif);
		} catch (Exception e) {
			return ProcessorsUtils.createResponse(name, classname, new Integer(MessageCodes.ImportMov.ERR_INSERT_LAV),
					"Impossibile calcolare il CPI del lavoratore da inserire", warnings, nested);
		}

		// Impostazione del Cpi lavoratore nella tabella AN_LAV_STORIA_INF
		statement = "UPDATE AN_LAV_STORIA_INF " + "SET CODCPITIT= '" + calcolatoreCpi.getCodCpiTit() + "', "
				+ "CDNUTMOD= " + userId + ", " + "CODMONOTIPOCPI = '" + calcolatoreCpi.getCodMonoTipoCpi() + "', "
				+ "CODCPIORIG = '" + calcolatoreCpi.getCodCpiOrig() + "', " + "DTMMOD = SYSDATE, "
				+ "NUMKLOLAVSTORIAINF = NUMKLOLAVSTORIAINF + 1 " + "WHERE CDNLAVORATORE=" + cdnLavoratore.toString()
				+ " AND " + "DATFINE is null";

		Object insertResult = null;
		try {
			insertResult = trans.executeQueryByStringStatement(statement, null, TransactionQueryExecutor.UPDATE);
		} catch (Exception e) {
			return ProcessorsUtils.createResponse(name, classname, new Integer(MessageCodes.ImportMov.ERR_INSERT_LAV),
					"Impossibile inserire l'informazione del Cpi nei dati del lavoratore. Dettagli: " + e.getMessage(),
					warnings, nested);
		}

		// Esamino il risultato
		if (insertResult instanceof Boolean && ((Boolean) insertResult).booleanValue() == true) {
			// Tutto ok
			return null;
		} else if (insertResult instanceof Exception) {
			it.eng.sil.util.TraceWrapper.debug(_logger,
					"UpdateCpiLav::processRecord():Impossibile inserire l'informazione del Cpi nei dati del lavoratore.",
					(Exception) insertResult);

			return ProcessorsUtils.createResponse(name, classname, new Integer(MessageCodes.ImportMov.ERR_INSERT_LAV),
					"Impossibile inserire l'informazione del Cpi nei dati del lavoratore.", warnings, nested);
		} else {
			return ProcessorsUtils.createResponse(name, classname, new Integer(MessageCodes.ImportMov.ERR_INSERT_LAV),
					"Impossibile inserire l'informazione del Cpi nei dati del lavoratore.", warnings, nested);
		}

	}
}