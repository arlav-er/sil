/**
 * TrasmettiPattoSoapBindingImpl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.sil.coop.webservices.pattoonlinenew;

import java.io.File;

import com.engiweb.framework.configuration.ConfigSingleton;
import com.engiweb.framework.dbaccess.DataConnectionManager;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.error.EMFInternalError;

import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.Values;
import it.eng.sil.coop.webservices.apapi.XmlUtils;
import it.eng.sil.module.patto.bean.PattoOnLine;

public class TrasmettiPattoSoapBindingImpl implements it.eng.sil.coop.webservices.pattoonlinenew.TrasmettiPatto {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(TrasmettiPattoSoapBindingImpl.class.getName());

	private File aggiornaPattoOnline_schema = new File(ConfigSingleton.getRootPath() + File.separator + "WEB-INF"
			+ File.separator + "xsd" + File.separator + "ptonline" + File.separator + "ptonlinesil.xsd");

	private File aggiornaPattoOnline_schema_2 = new File(ConfigSingleton.getRootPath() + File.separator + "WEB-INF"
			+ File.separator + "xsd" + File.separator + "ptonline" + File.separator + "ptonlinesil_2.xsd");

	public EsitoType aggiornaPatto(PattoAccettatoType richiesta) throws java.rmi.RemoteException {

		EsitoType esito = null;
		DataConnection conn = null;
		TransactionQueryExecutor trans = null;
		PattoOnLine pattoBean = null;

		try {
			pattoBean = new PattoOnLine();
			esito = new EsitoType();

			String inputXML = pattoBean.requestToXML(richiesta);

			// valida la request rispetto allo schema xsd
			boolean inputXMLIsValid = XmlUtils.isXmlValid(inputXML, aggiornaPattoOnline_schema_2);
			if (!inputXMLIsValid) {
				_logger.error("aggiornaPatto on line: Validazione fallita xml request");
				esito = new EsitoType(EsitoTypeEsito.KO, "Validazione request fallita");
				return esito;
			}

			if (richiesta.getAccettazionePatto() == null
					|| richiesta.getAccettazionePatto().getDtmAccettazione() == null
					|| richiesta.getAccettazionePatto().getTipoAccettazione() == null) {
				_logger.debug(
						"Non è stato possibile aggiornare lo stato del patto/accordo: mancano i dati dell'accettazione");
				esito.setEsito(EsitoTypeEsito.KO);
				StringBuffer sb = new StringBuffer();
				sb.append(
						"Non è stato possibile aggiornare lo stato del patto/accordo: mancano i dati dell'accettazione ");
				esito.setDescrizione(sb.toString());
				return esito;
			}

			DataConnectionManager dataConnectionManager = DataConnectionManager.getInstance();
			conn = dataConnectionManager.getConnection(Values.DB_SIL_DATI);

			trans = new TransactionQueryExecutor(conn, null, null);

			// INIZIO TRANSAZIONE //
			////////////////////////

			trans.initTransaction();

			int result = pattoBean.aggiornaAccettazionePatto(conn.getInternalConnection(), richiesta,
					PattoOnLine.ACCETTATO);

			if (result == 0) {
				_logger.info(
						"Aggiornamento dati accettazione patto/accordo on line in  am_patto_lavoratore avvenuto con successo");
				trans.commitTransaction();
				esito.setEsito(EsitoTypeEsito.OK);
				esito.setDescrizione("Dati accettazione patto/accordo registrati con successo");
			} else {
				_logger.info(
						"Aggiornamento dati accettazione patto/accordo on line in  am_patto_lavoratore non riuscito");
				trans.rollBackTransaction();
				esito.setEsito(EsitoTypeEsito.KO);
				if (result == 40) {
					StringBuffer sb = new StringBuffer();
					sb.append("Non è stato trovato il patto/accordo indicato per l'accettazione. ");
					sb.append(
							"è possibile effettuare un nuovo tentativo o in alternativa puoi contattare il CPI per la verifica della problematica");
					esito.setDescrizione(sb.toString());
				} else {
					esito.setDescrizione("ERRORE");
				}

			}

			return esito;

		} catch (Throwable e) {
			try {
				if (trans != null) {
					trans.rollBackTransaction();
				}
				_logger.error("Errore: " + e);
			} catch (Exception e1) {
				_logger.error("Errore: " + e1);
			}
		} finally {
			if (trans != null) {
				try {
					trans.closeConnTransaction();
				} catch (EMFInternalError e) {
					_logger.error("Errore: " + e);
				}
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (EMFInternalError e) {
					_logger.error("Errore: " + e);
				}
			}
		}
		esito.setEsito(EsitoTypeEsito.KO);
		esito.setDescrizione("ERRORE");
		return esito;

	}

}
