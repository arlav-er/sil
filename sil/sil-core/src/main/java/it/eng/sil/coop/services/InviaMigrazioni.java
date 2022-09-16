/*
 * Creato il 6-lug-06
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.coop.services;

import java.io.File;
import java.io.InputStream;
import java.io.Serializable;
import java.io.Writer;
import java.math.BigDecimal;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.ResponseContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.configuration.ConfigSingleton;
import com.engiweb.framework.dbaccess.DataConnectionManager;
import com.engiweb.framework.dbaccess.SQLStatements;
import com.engiweb.framework.dbaccess.Utils;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.DataField;
import com.engiweb.framework.dbaccess.sql.SQLCommand;
import com.engiweb.framework.dbaccess.sql.result.DataResult;
import com.engiweb.framework.dbaccess.sql.result.ScrollableDataResult;
import com.engiweb.framework.error.EMFInternalError;
import com.engiweb.framework.message.MessageBundle;

import it.eng.afExt.utils.StringUtils;
import it.eng.sil.Values;
import it.eng.sil.coop.CoopApplicationException_Lavoratore;
import it.eng.sil.coop.bean.XMLCoopMessage;
import it.eng.sil.module.movimenti.Importer;
import it.eng.sil.module.movimenti.MultipleTransactionQueryExecutor;
import it.eng.sil.module.movimenti.RecordProcessor;
import it.eng.sil.module.movimenti.processors.IdentificaMovimento;
import it.eng.sil.module.movimenti.processors.InsertAgevolazioniApp;
import it.eng.sil.module.movimenti.processors.InsertAvvPerCVE;
import it.eng.sil.module.movimenti.processors.InsertData;
import it.eng.sil.module.movimenti.processors.SelectFieldsMovimentoAppoggio;
import it.eng.sil.module.movimenti.processors.TransformCodTempo;
import oracle.sql.CLOB;

/**
 * @author giuliani
 * 
 *         Per modificare il modello associato al commento di questo tipo generato, aprire
 *         Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
public class InviaMigrazioni implements IFaceService {
	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(InviaMigrazioni.class.getName());
	private XMLCoopMessage xmlMessage = null;

	/*
	 * (non Javadoc)
	 * 
	 * @see it.eng.sil.coop.services.IFaceService#send(javax.jms.Message)
	 */
	public void send(Message msg) throws CoopApplicationException_Lavoratore, JMSException {

		ConfigSingleton config = ConfigSingleton.getInstance();

		BigDecimal user = new BigDecimal("190");

		ObjectMessage message = (ObjectMessage) msg;
		Serializable arrObj = message.getObject();
		List in = (ArrayList) arrObj;

		String cdnUtente = message.getStringProperty("cdnUtente");
		String cdnGruppo = message.getStringProperty("cdnGruppo");
		String cdnProfilo = message.getStringProperty("cdnProfilo");
		String strMittente = message.getStringProperty("strMittente");
		String poloMittente = message.getStringProperty("Polomittente");

		// System.out.println("Sto eseguendo il servizio InviaMigrazioni..
		// YUHUU!!");

		if (in.size() > 0) {
			String xmlStr = (String) in.get(0);

			try {
				/*
				 * 30/5/2007 savino: codice inutile, non usato SourceBean sbParameter =
				 * SourceBean.fromXMLString(xmlStr); sbParameter.setAttribute("ROW.CONTEXT","importa");
				 * sbParameter.setAttribute("ROW.CODMONOPROV","C");
				 */
				SourceBean mov = new SourceBean("MOVIMENTI");
				xmlMessage = new XMLCoopMessage(xmlStr);
				xmlMessage.setDati("CONTEXT", "importa");
				xmlMessage.setDati("CODMONOPROV", "C");

				SourceBean dati = new SourceBean(xmlMessage.getDatiAsSourceBean());
				mov.setAttribute(dati);

				try {// inserisco il movimento
					SourceBean result = inserisciMovimento(mov, user, "DATI", null);

					if (result.containsAttribute("RESPONSE")
							&& ((String) result.getAttribute("RESPONSE")).equals("OK")) {
						// System.out.println("Ho inserito il movimento in
						// AM_MOVIMENTO_APPOGGIO");

					} else {
						// System.out.println("DEVO inserire il movimento in
						// CA_MIGRAZIONI_SCARTATE");

						String errorCode = (String) result.getAttribute("MESSAGECODE");
						String errorMsg = MessageBundle.getMessage(errorCode);
						insMovimentoScartato(poloMittente, mov, errorMsg);

					}

				} catch (Exception e) {
					_logger.fatal(e);

				}

			} catch (SourceBeanException e) {
				_logger.fatal(e);
			}
		}
	}

	/**
	 * Inserisce il movimento in AM_MOVIMENTO_APPOGGIO. modifica 16/2/06/2008: puo' essere passato il
	 * MultipleTransactionQueryExecutor (vedere il trasferimento ramo azienda TrasferimentoRamoAzModule)
	 */
	public SourceBean inserisciMovimento(SourceBean movimenti, BigDecimal user, String keyExtr,
			MultipleTransactionQueryExecutor tex) throws Exception {

		SourceBean result = new SourceBean("RESULT");
		setFrameworkContext(user);
		// 12/06/2008 savino: modifca necessaria per permettere al web service di trasferimento ramo azienda di chiamare
		// all'interno di una transazione questo metodo.
		// Se il MultipleTransactionQueryExecutor passato come parametro e' null allora la transazione sara' locale
		// come nel caso migrazione e c.o. unilav/unisomm.
		// N.B. Per il trasf. ramo az. verra' passato il MultipleTransactionQueryExecutorWrapper che disabilita la
		// commit e rollback.
		MultipleTransactionQueryExecutor trans = null;
		try {
			if (tex == null)
				trans = new MultipleTransactionQueryExecutor(Values.DB_SIL_DATI);
			else
				trans = tex;
		} catch (Exception error) {
			_logger.fatal("Impossibile istanziare il MultipleTransactionQueryExecutor per importare i dati sul DB. ",
					error);
			result.setAttribute("RESPONSE", "ERROR");
			result.setAttribute("MESSAGE",
					"Impossibile istanziare il MultipleTransactionQueryExecutor per importare i dati sul DB. ");
			return result;
		}

		Importer importer = null;
		SourceBeanExtractor sbExtr = null;
		RecordProcessor procCodTempo;
		RecordProcessor selectCorrectFields;
		RecordProcessor insertMovApp;
		RecordProcessor insertAvvPerCve;
		RecordProcessor insertBenefici;
		boolean avviamentoCPTVE = false;

		try {
			// Istanzio l'importatore
			importer = new Importer();
			String configbase = ConfigSingleton.getRootPath() + File.separator + "WEB-INF" + File.separator + "conf"
					+ File.separator + "import" + File.separator;

			String configproc = configbase + "processors" + File.separator;
			sbExtr = new SourceBeanExtractor(movimenti, configbase + "TXTRecordMapping.xml", keyExtr);

			// Istanzio e configuro i processori
			// RecordProcessor selectCodContr = new
			// SelectCodContratto("Seleziona_Codice_Contratto", dataconn);
			procCodTempo = new TransformCodTempo("Trasforma_Tempo_Avviamento");
			selectCorrectFields = new SelectFieldsMovimentoAppoggio("Selezione_Dati_Movimento");

			insertMovApp = new InsertData("Inserisci_Movimento_Appoggio", trans,
					configproc + "InsertAM_MOVIMENTI_APPOGGIO.xml", "INSERT_MOVIMENTO_APPOGGIO", user);

			insertBenefici = new InsertAgevolazioniApp("Inserisci_Benefici_Appoggio", trans, avviamentoCPTVE);

			insertAvvPerCve = new InsertAvvPerCVE("Inserisci_Avviamento_per_CVE", trans,
					configproc + "insertMovimentoAppPerCVE.xml", "INSERT_AVVIAMENTO_PER_CVE", user);

			// Li aggiungo all'importer
			importer.addProcessor(new IdentificaMovimento());
			importer.addProcessor(procCodTempo);
			importer.addProcessor(selectCorrectFields);
			// importer.addProcessor(selectCodContr);
			importer.addProcessor(insertAvvPerCve);
			importer.addProcessor(insertMovApp);
			importer.addProcessor(insertBenefici);

			// Imposto i dati necessari nell'importer
			importer.setIdRecordKey("PRGMOVIMENTOAPP");
			importer.setRecordExtractor(sbExtr);

			// Processo i records e poi aggiungo il risultato alla response del
			// modulo
			SourceBean response = importer.importRecords(trans);
			// response.setAttribute(result);
			if (response != null) {
				Vector records = response.getAttributeAsVector("RECORD");
				for (int i = 0; i < records.size(); i++) {
					SourceBean record = (SourceBean) records.get(i);
					if ("ERROR".equalsIgnoreCase((String) record.getAttribute("RESULT"))) {
						result.setAttribute("RESPONSE", "ERROR");
						Vector processors = record.getAttributeAsVector("PROCESSOR");
						String msgCODE = "";
						for (int j = 0; j < processors.size(); j++) {
							SourceBean processor = (SourceBean) processors.get(j);
							if (processor.containsAttribute("ERROR")) {
								msgCODE = StringUtils.getAttributeStrNotNull(processor, "ERROR.messagecode");
								break;
							}
						}
						result.setAttribute("MESSAGECODE", msgCODE);
						return result;
					}
				}
			}
			result.setAttribute("RESPONSE", "OK");
			return result;
		} catch (Exception e) {
			_logger.fatal("Impossibile istanziare il processore per i record. ", e);
			result.setAttribute("RESPONSE", "ERROR");
			result.setAttribute("MESSAGE", "Impossibile istanziare il processore per i record. ");
			return result;
		} finally {
			if (trans != null) {
				// chiudo la connessione del MultipleTransactionQueryExecutor
				trans.closeConnection();
			}
		}
	}

	public void insMovimentoScartato(String poloMittente, SourceBean mov, String error) {
		SQLCommand sqlCommand = null;
		DataResult dr = null;
		Writer outStream = null;
		InputStream inStream = null;

		DataConnectionManager dataConnectionManager = null;
		DataConnection dataConnection = null;
		;

		try {
			// Imposto il progressivo per la tabella di scarto
			dataConnectionManager = DataConnectionManager.getInstance();
			dataConnection = dataConnectionManager.getConnection(Values.DB_SIL_DATI);
			dataConnection.initTransaction();

			String stmt = SQLStatements.getStatement("GET_PRGMIGRAZIONESCARTO");
			sqlCommand = dataConnection.createSelectCommand(stmt);
			dr = sqlCommand.execute();
			ScrollableDataResult sdr = (ScrollableDataResult) dr.getDataObject();
			DataField df = sdr.getDataRow().getColumn("PRGMIGRAZIONESCARTO");
			BigDecimal prgMigrazioneScarto = (BigDecimal) df.getObjectValue();
			String codcpimitt = null;
			// se proveniamo da una comunicazione obbligatoria xmlMessage e'
			// null.
			if (xmlMessage != null)
				codcpimitt = xmlMessage.getCodiceCPIMitt();
			// inserisco tutti i dati necessari meno il CLOB che lo creo vuoto,
			// l'inserimento viene fatto dopo
			ArrayList inputParameter = new ArrayList();
			inputParameter.add(dataConnection.createDataField("p1", java.sql.Types.INTEGER, prgMigrazioneScarto));
			inputParameter.add(dataConnection.createDataField("p2", java.sql.Types.INTEGER, new BigDecimal("190")));
			inputParameter.add(dataConnection.createDataField("p3", java.sql.Types.CHAR, codcpimitt));
			inputParameter.add(dataConnection.createDataField("p4", java.sql.Types.VARCHAR, poloMittente));
			if (error != null && !error.equals("")) {
				inputParameter.add(dataConnection.createDataField("p5", java.sql.Types.VARCHAR, error));
			} else {
				inputParameter
						.add(dataConnection.createDataField("p5", java.sql.Types.VARCHAR, "Errore non specificato"));
			}
			stmt = SQLStatements.getStatement("INSERT_CA_MIGRAZIONE_SCARTO_CLOB");
			sqlCommand = dataConnection.createInsertCommand(stmt);
			dr = sqlCommand.execute(inputParameter);

			// Inserisco il CLOB
			stmt = SQLStatements.getStatement("CA_MIGRAZIONE_SCARTO_CLOB_UPLOAD");
			sqlCommand = dataConnection.createSelectCommand(stmt);
			inputParameter = new ArrayList(1);
			inputParameter
					.add(dataConnection.createDataField("prgMigrazioneScarto", Types.BIGINT, prgMigrazioneScarto));
			dr = sqlCommand.execute(inputParameter);
			sdr = (ScrollableDataResult) dr.getDataObject();
			df = sdr.getDataRow().getColumn("TXTTRACCIATOINVIATO");
			CLOB resultClob = (CLOB) df.getObjectValue();

			outStream = resultClob.getCharacterOutputStream();

			// Buffer to hold chunks of data to being written to the Blob.
			int chunk = resultClob.getChunkSize();

			// Read a chunk of data from the input stream,
			// and write the chunk to the Clob column output stream.
			// Repeat till file has been fully read.
			String strMessage;
			if (xmlMessage != null)
				strMessage = xmlMessage.toString();
			else
				strMessage = mov.toString();
			String subStr = "";

			while (strMessage.length() > chunk) {
				subStr = strMessage.substring(0, chunk);
				strMessage = strMessage.substring(chunk - 1);
				outStream.write(subStr, 0, subStr.length()); // Write to Clob
			}
			outStream.write(strMessage, 0, strMessage.length()); // Write to
			// Clob
			outStream.flush();

			try {
				dataConnection.commitTransaction();
			} catch (EMFInternalError e1) {
				_logger.fatal(
						"Impossibile inserire il movimento di migrazione/comunicazione obbligatoria nella tabella degli scarti. ",
						(Exception) e1);
			}
		} catch (Exception e) {
			_logger.fatal(
					"Impossibile inserire il movimento di migrazione/comunicazione obbligatoria nella tabella degli scarti. ",
					e);
			try {
				dataConnection.rollBackTransaction();
			} catch (EMFInternalError e1) {
				_logger.fatal("ulteriore nella rollback. ", (Exception) e1);
			}
		} finally {
			// Rilascia eventuali risorse allocate
			Utils.releaseResources(dataConnection, sqlCommand, dr);
		}

	}

	private void setFrameworkContext(BigDecimal userid) throws Exception {
		if (RequestContainer.getRequestContainer() == null) {
			RequestContainer requestContainer = new RequestContainer();
			ResponseContainer responseContainer = new ResponseContainer();
			SourceBean serviceRequest = new SourceBean("SERVICE_REQUEST");
			SourceBean serviceResponse = new SourceBean("SERVICE_RESPONSE");
			SessionContainer sessionContainer = new SessionContainer(false);
			sessionContainer.setAttribute(Values.CODUTENTE, userid);
			sessionContainer.setAttribute("_ENCRYPTER_KEY_", System.getProperty("_ENCRYPTER_KEY_"));
			requestContainer.setSessionContainer(sessionContainer);
			requestContainer.setServiceRequest(serviceRequest); // imposto il serviceRequest nel RequestContainer
			// responseContainer.setServiceResponse(serviceResponse);
			// responseContainer.setErrorHandler(new EMFErrorHandler());
			RequestContainer.setRequestContainer(requestContainer);
		}
	}

}// END Class
