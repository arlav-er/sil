package it.eng.sil.module.patto;

import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.util.Vector;

import javax.xml.rpc.JAXRPCException;
import javax.xml.rpc.holders.StringHolder;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.error.EMFInternalError;
import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.coop.DataSourceJNDI;
import it.eng.sil.coop.endpoints.EndPointSingleton;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.security.User;
import it.gov.lavoro.servizi.servizicoapSap.ServizicoapWSProxy;
import it.gov.lavoro.servizi.servizicoapSap.types.Risposta_invioSAP_TypeEsito;
import it.gov.lavoro.servizi.servizicoapSap.types.holders.Risposta_invioSAP_TypeEsitoHolder;
import oracle.jdbc.OracleTypes;

public class InviaSapYG extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(InviaSapYG.class.getName());

	public static final String END_POINT_NAME = "InvioSAP";

	public void service(SourceBean request, SourceBean response) throws Exception {
		User user = (User) getRequestContainer().getSessionContainer().getAttribute("@@USER@@");
		ReportOperationResult reportOperation = new ReportOperationResult(this, response);
		TransactionQueryExecutor transExec = null;
		DataConnection dataConnection = null;
		Connection connection = null;
		Monitor monitor = null;

		disableMessageIdFail();
		disableMessageIdSuccess();

		String cdnLavoratore = (String) request.getAttribute("CDNLAVORATORE");
		String rispostaWs = "";

		try {
			transExec = new TransactionQueryExecutor(getPool(), this);
			enableTransactions(transExec);
			transExec.initTransaction();

			monitor = MonitorFactory.start(this.getClass().getName() + "::getXmlSAP");
			dataConnection = transExec.getDataConnection();
			connection = dataConnection.getInternalConnection();

			String encrypterKey = System.getProperty("_ENCRYPTER_KEY_");
			String p_cdnUtente = String.valueOf(user.getCodut());

			// crea XML SAP
			CallableStatement stmtCreaSap = connection.prepareCall("{? = call pg_sap.invioXMLSAP(?, ?, ?, ?) }");

			stmtCreaSap.registerOutParameter(1, OracleTypes.CLOB);
			stmtCreaSap.setString(2, cdnLavoratore);
			stmtCreaSap.setString(3, encrypterKey);
			stmtCreaSap.setBigDecimal(4, new BigDecimal(p_cdnUtente));
			stmtCreaSap.registerOutParameter(5, OracleTypes.VARCHAR);
			stmtCreaSap.execute();

			String xml = (String) stmtCreaSap.getString(1);
			String codErrore = (String) stmtCreaSap.getString(5);

			if (xml == null || ("").equalsIgnoreCase(xml)) {
				// errore sap nulla
				reportOperation.reportFailure(MessageCodes.General.OPERATION_FAIL);
				throw new Exception("Impossibile calcolare l'xml SAP per il lavoratore");
			} else if (!("00").equalsIgnoreCase(codErrore)) {
				// errore nella creazione della sap mostrare codici errori
				SourceBean rows = null;
				Object[] fieldWhere = new Object[1];
				fieldWhere[0] = codErrore;
				rows = (SourceBean) transExec.executeQuery("GET_DESC_ERRORE_YG", fieldWhere, "SELECT");
				String descErr = (String) rows.getAttribute("ROW.STRDESCRIZIONE");

				Vector paramV = new Vector(1);
				paramV.add(descErr);

				reportOperation.reportFailure(MessageCodes.General.RIPORTA_ERRORE_INTERNO, "INVIOSAP", "ERRORE INTERNO",
						paramV);
				throw new Exception("la creazione della sap ha restituito un codice di errore");
			} else {
				// inviare SAP
				try {

					it.gov.lavoro.servizi.servizicoapSap.types.holders.Risposta_invioSAP_TypeEsitoHolder esito = new Risposta_invioSAP_TypeEsitoHolder();
					javax.xml.rpc.holders.StringHolder messaggioErrore = new StringHolder();
					javax.xml.rpc.holders.StringHolder codiceSAP = new StringHolder("");

					DataSourceJNDI dataSourceJndi = new DataSourceJNDI();
					String dataSourceJndiName = dataSourceJndi.getJndi();
					EndPointSingleton eps = EndPointSingleton.getInstance(dataSourceJndiName);
					String invioSapEP = eps.getUrl(END_POINT_NAME);

					ServizicoapWSProxy proxy = new ServizicoapWSProxy();
					proxy.setEndpoint(invioSapEP);

					_logger.debug("send invio la sap= " + xml);
					proxy.invioSAP(xml, esito, messaggioErrore, codiceSAP);

					_logger.debug("risposta invio la sap codice=" + codiceSAP.value);
					_logger.debug("risposta invio la sap messaggio di errore=" + messaggioErrore.value);

					rispostaWs = codiceSAP.value;

					if ((Risposta_invioSAP_TypeEsito.OK).equals(esito.value)) {
						String codMinSap = rispostaWs;

						// aggiorno il record
						Object[] params = new Object[3];
						params[0] = codMinSap;
						params[1] = p_cdnUtente;
						params[2] = cdnLavoratore;
						Object queryRes = transExec.executeQuery("UPDATE_DATI_INVIO_SAP", params, "UPDATE");
						if (queryRes == null
								|| !(queryRes instanceof Boolean && ((Boolean) queryRes).booleanValue() == true)) {
							throw new Exception("Impossibile eseguire update del codice sap in an_yg_dati_invio");
						}

						Object[] paramsEstr = new Object[2];
						paramsEstr[0] = codMinSap;
						paramsEstr[1] = cdnLavoratore;
						Object queryResEstr = transExec.executeQuery("UPDATE_TS_ESTRAZIONE_SAP", paramsEstr, "UPDATE");
						if (queryResEstr == null || !(queryResEstr instanceof Boolean
								&& ((Boolean) queryResEstr).booleanValue() == true)) {
							throw new Exception("Impossibile eseguire update del codice sap in ts_estrazione_sap");
						}
					} else {
						Vector paramV = new Vector(1);
						paramV.add("Il servizio invio SAP ha risposto con esito negativo");
						reportOperation.reportFailure(MessageCodes.General.RIPORTA_ERRORE_INTERNO, "WS INVIOSAP",
								"ERRORE INTERNO", paramV);
						throw new Exception("Risposta invio sap KO");
					}
				} catch (JAXRPCException ie) {
					reportOperation.reportFailure(MessageCodes.Webservices.WS_ERRORE_INVOCAZIONE, ie, "service()",
							"errore chiamata servizio");
				} catch (RemoteException ie) {
					reportOperation.reportFailure(MessageCodes.Webservices.WS_ERRORE_INVOCAZIONE, ie, "service()",
							"errore chiamata servizio");
				}
			}

			transExec.commitTransaction();
			reportOperation.reportSuccess(MessageCodes.General.OPERATION_SUCCESS);
		} catch (Exception e) {
			try {
				if (transExec != null) {
					transExec.commitTransaction();
				}
			} catch (EMFInternalError ie) {
				reportOperation.reportFailure(MessageCodes.General.OPERATION_FAIL, e, "service()",
						"errore invio xml sap");
			}
			reportOperation.reportFailure(MessageCodes.General.OPERATION_FAIL, e, "service()", "errore invio xml sap");
		}
	}
}
