package it.eng.sil.module.ido;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.dbaccess.DataConnectionManager;
import com.engiweb.framework.dbaccess.Utils;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.SQLCommand;
import com.engiweb.framework.dbaccess.sql.result.DataResult;
import com.engiweb.framework.dbaccess.sql.result.ScrollableDataResult;
import com.engiweb.framework.error.EMFErrorSeverity;
import com.engiweb.framework.error.EMFInternalError;
import com.engiweb.framework.error.EMFUserError;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.sil.Values;
import it.eng.sil.coop.DataSourceJNDI;
import it.eng.sil.coop.messages.TestataMessageTO;
import it.eng.sil.coop.messages.jmsmessages.InvioVacancyMyBlenMessage;
import it.eng.sil.coop.messages.jmsmessages.InvioVacancyMyPortalMessage;
import it.eng.sil.coop.queues.OutQ;
import it.eng.sil.coop.webservices.clicLavoro.candidatura.CLCandidaturaData;
import it.eng.sil.coop.webservices.clicLavoro.candidatura.CLCandidaturaInvia;
import it.eng.sil.coop.webservices.clicLavoro.ricercaPersonale.CLRicercaPersonaleData;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.util.xml.FieldFormatException;
import it.eng.sil.util.xml.MandatoryFieldException;

public class InsertClicLavoro extends AbstractSimpleModule {

	static Logger logger = Logger.getLogger(CLCandidaturaInvia.class.getName());

	private static final String CODTIPOCOMUNICAZIONE_BLEN = "01_BLEN";
	private static final String CODTIPOCOMUNICAZIONE_CLICLAVORO = "01";

	private static final String WS_LOGON_CLICLAVORO = "SELECT prgws, struserid, " + " strpassword FROM ts_ws "
			+ " WHERE  codservizio = 'SIL_CLICLAV_MYPORTAL' ";

	private static final String WS_LOGON_BLEN = "SELECT prgws, struserid, " + " strpassword FROM ts_ws "
			+ " WHERE  codservizio = 'SIL_CLICLAV_MYBLEN' ";

	private static final String SERVIZIO_CLIC_LAVORO = "InvioVacancyMyPortal";
	private static final String SERVIZIO_BLEN = "InvioVacancyMyBlen";

	public void service(SourceBean request, SourceBean response) {

		ReportOperationResult reportOperation = new ReportOperationResult(this, response);

		String prgRichiestaAz = request.getAttribute("prgRichiestaAz").toString();
		BigDecimal richAz = new BigDecimal(prgRichiestaAz);
		String dataInvio = (String) request.getAttribute("DATA_INVIO");
		String codCPI = (String) request.getAttribute("CODCPI");
		String dataScad = (String) request.getAttribute("DATA_SCADENZA");
		String codTipoComunicazioneCl = (String) request.getAttribute("codtipocomunicazionecl");

		BigDecimal prgAltern = new BigDecimal(1);
		String strProfiloRichiesto = "";
		if (request.getAttribute("PROFILORICHIESTO") != null) {
			strProfiloRichiesto = request.getAttribute("PROFILORICHIESTO").toString();
		}
		String codComun = "";

		// GENERA IL CODCOMUNICAZIONE
		try {
			CLRicercaPersonaleData clUtil = new CLRicercaPersonaleData(richAz, prgAltern, codCPI,
					codTipoComunicazioneCl);
			// 11/10/2012 reintrodotto su richiesta di Novella
			codComun = clUtil.getCodComunicazione(CLCandidaturaData.TIPO_CLICLAVORO_VACANCY, null);
		} catch (EMFUserError e) {
			logger.error(e);
		}

		/*
		 * XXX gestire la verifica dell'XML da inviare per la gestione degli errori XSD all'utente viene creato l'xml
		 * senza inserirlo sul SB
		 */

		// GENERA L'XML DELLA VACANCY DA INVIARE
		String xmlGenerato = null;
		try {
			xmlGenerato = buildRichiestaDiPersonale(richAz, "1", codCPI, dataInvio, strProfiloRichiesto, codComun,
					dataScad, codTipoComunicazioneCl);
			// if (StringUtils.isFilledNoBlank(xmlGenerato)) {
			// xmlGenerato = xmlGenerato.replace("<cliclavoro", "\n<cliclavoro");
			// xmlGenerato = xmlGenerato.replace("></", ">\n</");
			// logger.debug(">>>> INSERT_XML: "+xmlGenerato);
			// }
		} catch (MandatoryFieldException e) {
			logger.error("Errore MandatoryFieldException: " + e.getExceptionMessage());
			int messageCode = MessageCodes.ClicLavoro.CODE_INPUT_ERRATO;
			String problem = e.getExceptionMessage();
			Vector<String> params = new Vector<String>();
			params.add(e.getExceptionMessage());
			reportOperation.reportFailure(MessageCodes.General.OPERATION_FAIL);
			reportOperation.reportFailure(messageCode, "buildVacancy", problem, params);
			// reportSuccess(reportOperation);
			return;
		} catch (FieldFormatException e) {
			logger.error("Errore FieldFormatException: " + e.getExceptionMessage());
			int messageCode = MessageCodes.ClicLavoro.CODE_INPUT_ERRATO;
			String problem = e.getExceptionMessage();
			Vector<String> params = new Vector<String>();
			params.add(e.getExceptionMessage());
			reportOperation.reportFailure(MessageCodes.General.OPERATION_FAIL);
			reportOperation.reportFailure(messageCode, "buildCandidatura", problem, params);
			// reportSuccess(reportOperation);
			return;
		} catch (EMFUserError e) {
			logger.error("Errore EMFUserError: " + e.getMessage());
			reportOperation.reportFailure(MessageCodes.General.OPERATION_FAIL);
			reportOperation.reportFailure(MessageCodes.ClicLavoro.CODE_ERR_INTERNO);
			return;
		}

		try {
			request.updAttribute("codOfferta", codComun);
			request.setAttribute("TXTTRACCIATOXML", xmlGenerato);
		} catch (SourceBeanException e) {
			logger.error(e);
		}

		boolean checkIns = doInsert(request, response);

		if (checkIns) {
			String codStato = (String) request.getAttribute("statoInvioClicLavoro");
			DataConnection dataConnection = null;
			try {
				DataConnectionManager dataConnectionManager = DataConnectionManager.getInstance();
				dataConnection = dataConnectionManager.getConnection(Values.DB_SIL_DATI);

				if (CODTIPOCOMUNICAZIONE_CLICLAVORO.equals(codTipoComunicazioneCl)) {
					sendToClicLavoro(prgRichiestaAz, xmlGenerato, codStato, dataConnection);
				} else if (CODTIPOCOMUNICAZIONE_BLEN.equals(codTipoComunicazioneCl)) {
					sendToBlen(prgRichiestaAz, xmlGenerato, codStato, dataConnection);
				} else
					throw new Exception("Servizio non gestito codtipocomunicazionecl = " + codTipoComunicazioneCl);
			} catch (Exception e) {
				logger.error(e);
			} finally {
				Utils.releaseResources(dataConnection, null, null);
			}
		}

	}

	private void sendToClicLavoro(String prgRichiestaAz, String xmlGenerato, String codStato,
			DataConnection dataConnection) throws EMFInternalError, Exception {
		String statement = WS_LOGON_CLICLAVORO;
		SQLCommand sqlCommand = dataConnection.createSelectCommand(statement);
		List inputParameter = new ArrayList();
		DataResult dataResult = sqlCommand.execute(inputParameter);
		ScrollableDataResult scrollableDataResult = (ScrollableDataResult) dataResult.getDataObject();
		SourceBean userWs = scrollableDataResult.getSourceBean();

		String username = (String) userWs.getAttribute("ROW.STRUSERID");
		String pwd = (String) userWs.getAttribute("ROW.STRPASSWORD");

		// invio la candidatura a MyPortal tramite CODA
		DataSourceJNDI dataSourceJndi = new DataSourceJNDI();
		String dataSourceJndiName = dataSourceJndi.getJndi();
		OutQ outQ = new OutQ();
		TestataMessageTO testataMessaggio = new TestataMessageTO();
		testataMessaggio.setServizio(SERVIZIO_CLIC_LAVORO);
		InvioVacancyMyPortalMessage sendMessage = new InvioVacancyMyPortalMessage(username, pwd, codStato,
				prgRichiestaAz);

		// mando il messaggio
		sendMessage.setTestata(testataMessaggio);
		sendMessage.setDataSourceJndi(dataSourceJndiName);
		sendMessage.setDatiRichiestaXml(xmlGenerato);
		sendMessage.send(outQ);
	}

	private void sendToBlen(String prgRichiestaAz, String xmlGenerato, String codStato, DataConnection dataConnection)
			throws EMFInternalError, Exception {
		String statement = WS_LOGON_BLEN;
		SQLCommand sqlCommand = dataConnection.createSelectCommand(statement);
		List inputParameter = new ArrayList();
		DataResult dataResult = sqlCommand.execute(inputParameter);
		ScrollableDataResult scrollableDataResult = (ScrollableDataResult) dataResult.getDataObject();
		SourceBean userWs = scrollableDataResult.getSourceBean();

		String username = (String) userWs.getAttribute("ROW.STRUSERID");
		String pwd = (String) userWs.getAttribute("ROW.STRPASSWORD");

		// invio la candidatura a MyPortal tramite CODA
		DataSourceJNDI dataSourceJndi = new DataSourceJNDI();
		String dataSourceJndiName = dataSourceJndi.getJndi();
		OutQ outQ = new OutQ();
		TestataMessageTO testataMessaggio = new TestataMessageTO();
		testataMessaggio.setServizio(SERVIZIO_BLEN);
		InvioVacancyMyBlenMessage sendMessage = new InvioVacancyMyBlenMessage(username, pwd, codStato, prgRichiestaAz);

		// mando il messaggio
		sendMessage.setTestata(testataMessaggio);
		sendMessage.setDataSourceJndi(dataSourceJndiName);
		sendMessage.setDatiRichiestaXml(xmlGenerato);
		sendMessage.send(outQ);
	}

	private String buildRichiestaDiPersonale(BigDecimal prgRichiesta, String prgAlternativa, String codCPI,
			String codiceOfferta, String strProfiloRichiesto, String codComunicazione, String dataScad,
			String codTipoComunicazioneCl) throws EMFUserError, MandatoryFieldException, FieldFormatException {
		BigDecimal bdRichiesta;
		BigDecimal bdAlternativa;
		try {
			bdRichiesta = prgRichiesta;
			bdAlternativa = new BigDecimal(prgAlternativa);
		} catch (NumberFormatException ex) {
			throw new EMFUserError(EMFErrorSeverity.ERROR, MessageCodes.ClicLavoro.CODE_INPUT_ERRATO,
					"I valori passati prgRichiesta e prgAlternativa non sono numerici: prgRichiesta:" + prgRichiesta
							+ ", prgAlternativa:" + prgAlternativa);
		}
		CLRicercaPersonaleData risposta = new CLRicercaPersonaleData(bdRichiesta, bdAlternativa, codCPI,
				strProfiloRichiesto, codComunicazione, dataScad, codTipoComunicazioneCl);
		risposta.costruisci(true);

		return risposta.generaXML();
	}

	private String buildRichiestaDiPersonale(BigDecimal prgRichiesta, String prgAlternativa, String codCPI,
			String codiceOfferta, String strProfiloRichiesto, String codTipoComunicazioneCl)
			throws EMFUserError, MandatoryFieldException, FieldFormatException {
		BigDecimal bdRichiesta;
		BigDecimal bdAlternativa;
		try {
			bdRichiesta = prgRichiesta;
			bdAlternativa = new BigDecimal(prgAlternativa);
		} catch (NumberFormatException ex) {
			throw new EMFUserError(EMFErrorSeverity.ERROR, MessageCodes.ClicLavoro.CODE_INPUT_ERRATO,
					"I valori passati prgRichiesta e prgAlternativa non sono numerici: prgRichiesta:" + prgRichiesta
							+ ", prgAlternativa:" + prgAlternativa);
		}
		CLRicercaPersonaleData risposta = new CLRicercaPersonaleData(bdRichiesta, bdAlternativa, codCPI,
				strProfiloRichiesto, codTipoComunicazioneCl);
		risposta.costruisci(true);

		return risposta.generaXML();
	}

}
