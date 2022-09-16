/*
 * Creato il 6-lug-06
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.module.coop;

import java.math.BigInteger;
import java.util.ArrayList;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.command.std.StoredProcedureCommand;
import com.engiweb.framework.dbaccess.sql.result.DataResult;
import com.engiweb.framework.dbaccess.sql.result.DataResultInterface;
import com.engiweb.framework.dbaccess.sql.result.ScrollableDataResult;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.Values;
import it.eng.sil.coop.DataSourceJNDI;
import it.eng.sil.coop.bean.XMLCoopMessage;
import it.eng.sil.coop.messages.TestataMessageTO;
import it.eng.sil.coop.messages.jmsmessages.InviaMigrazioniMessage;
import it.eng.sil.coop.queues.OutQ;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.security.User;
import it.eng.sil.util.InfoProvinciaSingleton;
import it.eng.sil.util.amministrazione.impatti.DBLoad;
import oracle.jdbc.OracleTypes;

/**
 * @author giuliani
 * 
 *         Metodo che raccoglie i dati relativi ad un movimento da inviare come migrazione. I dati vengono reperiti a
 *         partire dal prgMovimento che deve essere inserito nella request nell'attributo PRGMOVIMENTO. Tale classe può 
 *         essere invocata "normalmente" quando si esegue un il modulo associato all'interno di una PAGE, oppure a parte
 *         sia in transazione che a se stante.
 * 
 */
public class InviaMigrazione extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(InviaMigrazione.class.getName());

	private String className = this.getClass().getName();

	//
	public void service(SourceBean request, SourceBean response) throws Exception {
		// DAVIDE 08/08/2006 modificato per poter essere esguito in transazione
		_logger.debug(className + "::insert() CALLED...");

		TransactionQueryExecutor txExecutor = null;
		try {
			txExecutor = new TransactionQueryExecutor(Values.DB_SIL_DATI);
			txExecutor.initTransaction();
			service(request, response, txExecutor);
			txExecutor.commitTransaction();
		} catch (Exception e) {
			txExecutor.rollBackTransaction();
			throw e;
		}
	}

	public void service(SourceBean request, SourceBean response, TransactionQueryExecutor txExecutor) throws Exception {
		// DAVIDE 08/08/2006 aggiunto per poter essere esguito in transazione
		RequestContainer requestContainer = getRequestContainer();
		SessionContainer sessionContainer = requestContainer.getSessionContainer();
		User user = (User) sessionContainer.getAttribute(User.USERID);

		service(request, response, txExecutor, user);
	}

	public void service(SourceBean request, SourceBean response, TransactionQueryExecutor txExecutor, User user)
			throws Exception {

		ReportOperationResult reportOperation = new ReportOperationResult(this, response);

		String cdnGruppo = String.valueOf(user.getCdnGruppo());
		String cdnProfilo = String.valueOf(user.getCdnProfilo());
		String strMittente = user.getNome() + " " + user.getCognome();
		String cdnUtente = InfoProvinciaSingleton.getInstance().getCodice();
		String poloMittente = InfoProvinciaSingleton.getInstance().getCodice();

		String destinazione = StringUtils.getAttributeStrNotNull(request, "codProvinciaDestinazione");

		// Recuprero il sourcebean del movimento
		SourceBean rowMovimento = this.exportMovimento(request, response, txExecutor);

		// Dal momento che il movimento inviato verrà importato come se fosse
		// estratto
		// dal tracciato (SARE), imposto la versione del tracciato
		// corrispondente
		rowMovimento.setAttribute("ROW.versioneTracciato", "2,0,3");

		// parametri per il logger
		Object datInizioMov = null;
		// recupero della data inizio mov dell'avviamento
		Object dataAvvTempo = rowMovimento.getAttribute("DATAAVVTEMPOIND");
		if (dataAvvTempo == null) {
			dataAvvTempo = rowMovimento.getAttribute("DATAAVVTEMPODET");
		}
		datInizioMov = dataAvvTempo;
		Object codFiscLav = rowMovimento.getAttribute("CODFISCLAV");
		Object codFiscAz = rowMovimento.getAttribute("CODFISCAZ");
		Object codComunicazione = rowMovimento.getAttribute("CODCOMUNICAZIONE");

		// inserisco il sourcebean del movimento nella response
		response.setAttribute(rowMovimento);

		try {
			TestataMessageTO testata = new TestataMessageTO();
			testata.setPoloMittente(poloMittente);
			testata.setCdnUtente(cdnUtente);
			testata.setCdnGruppo(cdnGruppo);
			testata.setCdnProfilo(cdnProfilo);
			testata.setStrMittente(strMittente);
			testata.setServizio("InviaMigrazioni");

			// Polo provinciare a cui inviare il movimento
			testata.setDestinazione(destinazione);

			// Preparo il messaggio
			XMLCoopMessage xmlMessage = new XMLCoopMessage();
			xmlMessage.setCodiceCPIMitt(user.getCodRif());
			xmlMessage.setDescrizioneMitt("DescCPI_Mitt");
			xmlMessage.setNomeServizio("Migrazioni");
			// xmlMessage.setDati("cognome", cognome);
			xmlMessage.setDati((SourceBean) rowMovimento.getAttribute("ROW"));

			InviaMigrazioniMessage messaggio = new InviaMigrazioniMessage();

			// Setto i parametri. In questo caso uno solo passato come
			// SourceBean sottoforma di String
			// messaggio.setSbParameter(rowMovimento.toString());
			messaggio.setXMLMessage(xmlMessage.toXML());

			// Imposto la testata del messaggio
			messaggio.setTestata(testata);

			DataSourceJNDI dataSourceJndi = new DataSourceJNDI();
			String dataSourceJndiName = dataSourceJndi.getJndi();

			messaggio.setDataSourceJndi(dataSourceJndiName);

			OutQ outQ = new OutQ();

			System.out.println("CHIAMO il servizio InviaMigrazioni >>>>");
			messaggio.send(outQ);

			response.setAttribute("retCode", "0");
			reportOperation.reportSuccess(MessageCodes.Coop.INSERIMENTO_IR_SUCCESS);

		} catch (Exception exc) {
			it.eng.sil.util.TraceWrapper.debug(_logger, "InviaMigrazione::service()", exc);

			// inserisco nella tabella di log l'eventuale migrazione fallita
			DBLoad.insertMigrazioneLogger(datInizioMov, codFiscLav, codFiscAz, codComunicazione, destinazione,
					rowMovimento.toXML(), exc.toString());

			reportOperation.reportFailure(MessageCodes.EsportaMigrazioni.ERR_INVIO_MIGRAZ_IN_COOP);
		}

	}// service();

	/**
	 * dato un prgMovimento presente nella request viene richiamata la stored PG_MIGRAZIONI.ESPORTA_MOVIMENTO che
	 * restituisce i dati del movimento e avviamento da esportare i dati vengono inseriti nel SourceBean che viene
	 * restituito
	 * 
	 * @param request
	 * @param response
	 */
	private SourceBean exportMovimento(SourceBean request, SourceBean response, TransactionQueryExecutor txExecutor)
			throws Exception {

		SourceBean row = null;
		DataConnection conn = null;
		DataResult dr = null;
		ScrollableDataResult dr2 = null;
		StoredProcedureCommand command = null;
		StoredProcedureCommand command2 = null;
		ReportOperationResult ror = new ReportOperationResult(this, response);
		try {
			// DAVIDE 08/08/2006 modificato per poter essere esguito in
			// transazione
			// String pool = (String) getConfig().getAttribute("POOL");
			// DataConnectionManager dcm = DataConnectionManager.getInstance();
			// conn = dcm.getConnection(pool);
			// SourceBean statementSB = null;
			// String statement = "";
			// String sqlStr = "";

			conn = txExecutor.getDataConnection();
			String codiceRit = "";
			String errCode = "";
			int paramIndex = 0;
			ArrayList parameters = null;

			// DAVIDE 08/08/2006 modificato per poter essere esguito in
			// transazione
			// statementSB = (SourceBean)
			// getConfig().getAttribute("ESPORTA_MOVIMENTO");
			// statement = statementSB.getAttribute("STATEMENT").toString();
			// sqlStr = SQLStatements.getStatement(statement);
			command = (StoredProcedureCommand) conn
					.createStoredProcedureCommand("{ call ? := PG_MIGRAZIONI.ESPORTA_MOVIMENTO(?) }");// (sqlStr);
			String p_prgMovimento = StringUtils.getAttributeStrNotNull(request, "PRGMOVIMENTO");
			;

			parameters = new ArrayList(2);
			// 1.Parametro di Ritorno
			parameters.add(conn.createDataField("row", OracleTypes.CURSOR, null));
			command.setAsOutputParameters(paramIndex++);
			// preparazione dei Parametri di Input
			// 2. p_prgMovimento
			parameters
					.add(conn.createDataField("p_prgMovimento", java.sql.Types.BIGINT, new BigInteger(p_prgMovimento)));
			command.setAsInputParameters(paramIndex++);

			// Chiamata alla Stored Procedure
			dr = command.execute(parameters);
			if (dr.getDataResultType().equals(DataResultInterface.SCROLLABLE_DATA_RESULT)) {
				dr2 = (ScrollableDataResult) dr.getDataObject();
			}

			row = dr2.getSourceBean();

		} catch (Exception e) {
			String msg = "Errore nella chiamata alla Stored Procedure ";
			try {
				response.setAttribute("row.codiceRit", "-1");
			} catch (SourceBeanException e1) {
				throw e1;
			}
			ror.reportFailure(MessageCodes.General.OPERATION_FAIL, e, className, msg);
			throw e;
		}
		/*
		 * finally { Utils.releaseResources(conn, command, dr); }
		 */

		return row;
	}
}