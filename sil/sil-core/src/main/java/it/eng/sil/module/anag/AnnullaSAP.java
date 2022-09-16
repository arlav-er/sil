package it.eng.sil.module.anag;

import java.io.StringWriter;
import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.util.Vector;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.rpc.JAXRPCException;
import javax.xml.rpc.holders.StringHolder;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.error.EMFInternalError;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.coop.DataSourceJNDI;
import it.eng.sil.coop.endpoints.EndPointSingleton;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.pojo.yg.richiestaSAP.IDSAP;
import it.eng.sil.security.User;
import it.gov.lavoro.servizi.servizicoapSap.ServizicoapWSProxy;
import it.gov.lavoro.servizi.servizicoapSap.types.Risposta_annullaSAP_TypeEsito;
import it.gov.lavoro.servizi.servizicoapSap.types.holders.Risposta_annullaSAP_TypeEsitoHolder;

public class AnnullaSAP extends AbstractSimpleModule {

	private static final long serialVersionUID = 1L;
	public static final String END_POINT_NAME = "AnnullaSAP";
	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(AnnullaSAP.class.getName());

	public void service(SourceBean request, SourceBean response) throws Exception {
		String cdnLavoratore = (String) request.getAttribute("CDNLAVORATORE");
		String codMinSap = (String) request.getAttribute("CODMINSAP");
		ReportOperationResult reportOperation = new ReportOperationResult(this, response);
		TransactionQueryExecutor transExec = null;

		disableMessageIdFail();
		disableMessageIdSuccess();

		try {
			transExec = new TransactionQueryExecutor(getPool(), this);
			enableTransactions(transExec);
			transExec.initTransaction();

			Object[] fieldWhere = new Object[1];
			fieldWhere[0] = codMinSap;
			SourceBean sbSpLavoratore = (SourceBean) transExec.executeQuery("SELECT_SP_LAVORATORE", fieldWhere,
					"SELECT");
			if (sbSpLavoratore != null) {
				annullaSap(sbSpLavoratore, cdnLavoratore, codMinSap, transExec, reportOperation);
			}
			transExec.commitTransaction();
			reportOperation.reportSuccess(MessageCodes.YG.WS_ANNULLASAP_OK);
		} catch (Exception e) {
			try {
				if (transExec != null) {
					transExec.rollBackTransaction();
				}
			} catch (EMFInternalError ie) {
				reportOperation.reportFailure(MessageCodes.General.OPERATION_FAIL, e, "service()",
						"Errore Annulla SAP");
			}

			reportOperation.reportFailure(MessageCodes.General.OPERATION_FAIL, e, "service()", "Errore Annulla SAP");
		}
	}

	private void annullaSap(SourceBean sbSpLavoratore, String cdnLavoratore, String codMinSap,
			TransactionQueryExecutor transExec, ReportOperationResult reportOperation) throws Exception {
		User user = (User) getRequestContainer().getSessionContainer().getAttribute("@@USER@@");
		BigDecimal p_cdnUtente = new BigDecimal(user.getCodut());

		_logger.info("Inizio chiamata Annulla SAP (codice = " + codMinSap + ", cdnLavoratore = " + cdnLavoratore + ")");
		try {
			Risposta_annullaSAP_TypeEsitoHolder esito = new Risposta_annullaSAP_TypeEsitoHolder();
			StringHolder messaggioErrore = new StringHolder();

			DataSourceJNDI dataSourceJndi = new DataSourceJNDI();
			String dataSourceJndiName = dataSourceJndi.getJndi();
			EndPointSingleton eps = EndPointSingleton.getInstance(dataSourceJndiName);
			String annullaSapEP = eps.getUrl(END_POINT_NAME);

			ServizicoapWSProxy proxy = new ServizicoapWSProxy();
			proxy.setEndpoint(annullaSapEP);

			IDSAP idSapAnnulla = new IDSAP();
			idSapAnnulla.setIdentificativoSap(codMinSap);

			String codMinSapFromXml = convertAnnullaSapToString(idSapAnnulla);

			_logger.debug("Invoco Annulla SAP (codice = " + codMinSap + ", cdnLavoratore = " + cdnLavoratore + ")");
			proxy.annullaSAP(codMinSapFromXml, esito, messaggioErrore);

			_logger.info("Risposta Annulla SAP (codice = " + codMinSap + ", cdnLavoratore = " + cdnLavoratore + ")");
			_logger.debug("Risposta Annulla SAP (Messaggio di errore = " + messaggioErrore.value + ")");
			_logger.debug("Risposta Annulla SAP (Esito = " + esito.value + ")");

			if ((Risposta_annullaSAP_TypeEsito.KO).equals(esito.value)) {
				Vector paramV = new Vector(1);
				paramV.add("Il servizio Annulla SAP ha risposto con esito negativo");

				reportOperation.reportFailure(MessageCodes.General.RIPORTA_ERRORE_INTERNO, "WS ANNULLASAP",
						"ERRORE INTERNO", paramV);
				throw new Exception("Risposta Annulla sap KO");
			}

			if ((Risposta_annullaSAP_TypeEsito.OK).equals(esito.value)) {
				BigDecimal prgSpLav = (BigDecimal) sbSpLavoratore.getAttribute("ROW.PRGSPLAV");
				String datInizioVal = (String) sbSpLavoratore.getAttribute("ROW.DATINIZIOVAL");
				BigDecimal numKloSap = (BigDecimal) sbSpLavoratore.getAttribute("ROW.NUMKLOSAP");

				/*
				 * se la data di chiusura e' precedente all'apertura uso la stessa data di apertura anche per la
				 * chiusura
				 */
				String oggi = DateUtils.getNow();
				String datFineVal = DateUtils.giornoPrecedente(oggi);
				if (DateUtils.daysBetween(datInizioVal, datFineVal) < 0) {
					datFineVal = datInizioVal;
				}

				/* termino la validita' del record esistente */
				Object queryResChiusura = transExec.executeQuery("CHIUDU_SP_LAVORATORE",
						new Object[] { datFineVal, p_cdnUtente, numKloSap, prgSpLav }, "UPDATE");
				if (queryResChiusura == null || !(queryResChiusura instanceof Boolean
						&& ((Boolean) queryResChiusura).booleanValue() == true)) {
					throw new Exception("Impossibile eseguire update datfine a null in sp_lavoratore");
				}

				// inserisco un nuovo record
				Object queryResApertura = transExec.executeQuery("INSERT_SP_LAV_PER_ANNULLA",
						new Object[] { p_cdnUtente, p_cdnUtente, prgSpLav }, "INSERT");
				if (queryResApertura == null || !(queryResApertura instanceof Boolean
						&& ((Boolean) queryResApertura).booleanValue() == true)) {
					throw new Exception("Impossibile eseguire insert in sp_lavoratore con stato annullato");
				}
			}
		} catch (JAXRPCException ie) {
			reportOperation.reportFailure(MessageCodes.YG.ERR_EXEC_WS_ANNULLASAP, ie, "service()",
					"Errore chiamata servizio Annulla SAP");
			throw new Exception();
		} catch (RemoteException ie) {
			reportOperation.reportFailure(MessageCodes.YG.ERR_EXEC_WS_ANNULLASAP, ie, "service()",
					"Errore chiamata servizio Annulla SAP");
			throw new Exception();
		}

		_logger.info("Fine chiamata Annulla SAP (codice = " + codMinSap + ", cdnLavoratore = " + cdnLavoratore + ")");
	}

	private String convertAnnullaSapToString(IDSAP annulla) throws JAXBException {
		JAXBContext jc = JAXBContext.newInstance(IDSAP.class);
		Marshaller marshaller = jc.createMarshaller();
		StringWriter writer = new StringWriter();
		marshaller.marshal(annulla, writer);
		String xmlVerificaSAP = writer.getBuffer().toString();
		return xmlVerificaSAP;
	}

}
