package it.eng.sil.coop.webservices.did;

import java.math.BigDecimal;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.mail.MessagingException;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.DataConnectionManager;
import com.engiweb.framework.dbaccess.SQLStatements;
import com.engiweb.framework.dbaccess.Utils;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.DataField;
import com.engiweb.framework.dbaccess.sql.SQLCommand;
import com.engiweb.framework.dbaccess.sql.command.std.StoredProcedureCommand;
import com.engiweb.framework.dbaccess.sql.result.DataResult;
import com.engiweb.framework.dbaccess.sql.result.PunctualDataResult;
import com.engiweb.framework.dbaccess.sql.result.ScrollableDataResult;
import com.engiweb.framework.dbaccess.sql.result.std.CompositeDataResult;
import com.engiweb.framework.error.EMFInternalError;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.Values;
import it.eng.sil.mail.SendMail;
import it.eng.sil.mail.SendMailException;
import it.eng.sil.sms.ContattoSMS;

public class AppuntamentoDid {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(AppuntamentoDid.class.getName());

	public boolean fissaAppuntamentoAndInviaNotifiche(BigDecimal cdnLavoratore, String codCpi) {

		boolean isAppuntamentoOk = false;
		boolean isSmsOk = false;
		boolean isEmailOk = false;
		boolean isEvidenzaOk = false;
		boolean isSpiDisponibile = false;

		DataConnection conn = null;
		StoredProcedureCommand command = null;
		DataResult dr = null;
		String pool = Values.DB_SIL_DATI;

		String strNomeLav = "";
		String strCognomeLav = "";
		String strCellLav = "";
		String strEmailLav = "";
		String strDescrizioneCpi = "";
		String strIndirizzoCpi = "";
		String strTelCpi = "";
		String strDataApp = "";
		String strOraApp = "";
		String strDurataApp = "";
		String strCanSendSms = "";
		String strCanSendEmail = "";
		String strIsSpiDisponibile = "";
		String strNomeSpi = "";
		String strCognomeSpi = "";
		String strTelSpi = "";

		///////////////////////////////////////////////////
		// STEP 0: FUNZIONALITA' DI PRENOTAZIONE ATTIVA? //
		///////////////////////////////////////////////////

		if (!isPrenotazioneAppAbilitata())
			return false;

		///////////////////////////////////////
		// STEP 1: PRENOTAZIONE APPUNTAMENTO //
		///////////////////////////////////////

		try {

			DataConnectionManager dcm = DataConnectionManager.getInstance();
			conn = dcm.getConnection(pool);
			conn.initTransaction();

			// preparazione statement
			int paramIndex = 0;
			ArrayList<DataField> parameters = new ArrayList<DataField>();

			String sqlStr = SQLStatements.getStatement("INS_APP_DA_DID_ONLINE");
			command = (StoredProcedureCommand) conn.createStoredProcedureCommand(sqlStr);

			// parametro di ritorno
			parameters.add(conn.createDataField("codiceRit", Types.VARCHAR, null));
			command.setAsOutputParameters(paramIndex++);

			// preparazione dei parametri di input
			parameters.add(conn.createDataField("in_cdnlavoratore", Types.BIGINT, cdnLavoratore));
			command.setAsInputParameters(paramIndex++);
			parameters.add(conn.createDataField("in_cpilavoratore", Types.VARCHAR, codCpi));
			command.setAsInputParameters(paramIndex++);

			// parametri di output
			parameters.add(conn.createDataField("out_strNomeLav", Types.VARCHAR, null));
			command.setAsOutputParameters(paramIndex++);
			parameters.add(conn.createDataField("out_strCognome", Types.VARCHAR, null));
			command.setAsOutputParameters(paramIndex++);
			parameters.add(conn.createDataField("out_strCellLav", Types.VARCHAR, null));
			command.setAsOutputParameters(paramIndex++);
			parameters.add(conn.createDataField("out_strEmailLav", Types.VARCHAR, null));
			command.setAsOutputParameters(paramIndex++);
			parameters.add(conn.createDataField("out_strDescrizioneCpi", Types.VARCHAR, null));
			command.setAsOutputParameters(paramIndex++);
			parameters.add(conn.createDataField("out_strIndirizzoCpi", Types.VARCHAR, null));
			command.setAsOutputParameters(paramIndex++);
			parameters.add(conn.createDataField("out_strTelCpi", Types.VARCHAR, null));
			command.setAsOutputParameters(paramIndex++);
			parameters.add(conn.createDataField("out_strDataApp", Types.VARCHAR, null));
			command.setAsOutputParameters(paramIndex++);
			parameters.add(conn.createDataField("out_strOraApp", Types.VARCHAR, null));
			command.setAsOutputParameters(paramIndex++);
			parameters.add(conn.createDataField("out_strDurataApp", Types.VARCHAR, null));
			command.setAsOutputParameters(paramIndex++);
			parameters.add(conn.createDataField("out_strCanSendSms", Types.VARCHAR, null));
			command.setAsOutputParameters(paramIndex++);
			parameters.add(conn.createDataField("out_strCanSendEmail", Types.VARCHAR, null));
			command.setAsOutputParameters(paramIndex++);
			parameters.add(conn.createDataField("out_strIsSpiDisponibile", Types.VARCHAR, null));
			command.setAsOutputParameters(paramIndex++);
			parameters.add(conn.createDataField("out_strNomeSpi", Types.VARCHAR, null));
			command.setAsOutputParameters(paramIndex++);
			parameters.add(conn.createDataField("out_strCognomeSpi", Types.VARCHAR, null));
			command.setAsOutputParameters(paramIndex++);
			parameters.add(conn.createDataField("out_strTelSpi", Types.VARCHAR, null));
			command.setAsOutputParameters(paramIndex++);
			parameters.add(conn.createDataField("ErrCodeOut", Types.VARCHAR, null));
			command.setAsOutputParameters(paramIndex++);

			/////////////////////////////////
			// ESECUZIONE STORED PROCEDURE //
			/////////////////////////////////

			dr = command.execute(parameters);

			///////////////////////
			// ANALISI RISULTATI //
			///////////////////////

			int i = 0;
			CompositeDataResult cdr = (CompositeDataResult) dr.getDataObject();
			List outputParams = cdr.getContainedDataResult();

			// Codice di Ritorno
			PunctualDataResult pdr = (PunctualDataResult) outputParams.get(i++);
			DataField df = pdr.getPunctualDatafield();
			String codiceRit = df.getStringValue();

			// System.out.println("Codice ritorno: " + codiceRit);

			strNomeLav = getStringParam(outputParams, i++);
			strCognomeLav = getStringParam(outputParams, i++);
			strCellLav = getStringParam(outputParams, i++);
			strEmailLav = getStringParam(outputParams, i++);
			strDescrizioneCpi = getStringParam(outputParams, i++);
			strIndirizzoCpi = getStringParam(outputParams, i++);
			strTelCpi = getStringParam(outputParams, i++);
			strDataApp = getStringParam(outputParams, i++);
			strOraApp = getStringParam(outputParams, i++);
			strDurataApp = getStringParam(outputParams, i++);
			strCanSendSms = getStringParam(outputParams, i++);
			strCanSendEmail = getStringParam(outputParams, i++);
			strIsSpiDisponibile = getStringParam(outputParams, i++);
			strNomeSpi = getStringParam(outputParams, i++);
			strCognomeSpi = getStringParam(outputParams, i++);
			strTelSpi = getStringParam(outputParams, i++);
			String ErrCodeOut = getStringParam(outputParams, i++);

			// System.out.println("strNomeLav: " + strNomeLav);
			// System.out.println("strCognomeLav: " + strCognomeLav);
			// System.out.println("strCellLav: " + strCellLav);
			// System.out.println("strEmailLav: " + strEmailLav);
			// System.out.println("strDescrizioneCpi: " + strDescrizioneCpi);
			// System.out.println("strIndirizzoCpi: " + strIndirizzoCpi);
			// System.out.println("strTelCpi: " + strTelCpi);
			// System.out.println("strDataApp: " + strDataApp);
			// System.out.println("strOraApp: " + strOraApp);
			// System.out.println("strDurataApp: " + strDurataApp);
			// System.out.println("strCanSendSms: " + strCanSendSms);
			// System.out.println("strCanSendEmail: " + strCanSendEmail);
			// System.out.println("ErrCodeOut: " + ErrCodeOut);

			// operatore associato a uno slot o al codservizio per l'appuntamento did online
			if (strIsSpiDisponibile != null && "S".equalsIgnoreCase(strIsSpiDisponibile)) {
				isSpiDisponibile = true;
				if (strTelSpi == null || "".equalsIgnoreCase(strTelSpi)) {
					strTelSpi = strTelCpi;
				}
			}

			// System.out.println("codiceRit: "+codiceRit);
			if ("0".equalsIgnoreCase(codiceRit)) {

				// System.out.println("isAppuntamentoOk: "+isAppuntamentoOk);
				isAppuntamentoOk = true;

				_logger.info("Prenotazione appuntamento did online (cdnLavoratore:" + cdnLavoratore + ")");

			} else if ("-2".equalsIgnoreCase(codiceRit)) {
				// Nessuno SLOT disponibile
				_logger.error("Warning: prenotazione appuntamento did online (Nessuno SLOT disponibile)(cdnLavoratore:"
						+ cdnLavoratore + ")");
			} else if ("-3".equalsIgnoreCase(codiceRit)) {
				// Insert ag_agenda fallito
				_logger.error("Warning: prenotazione appuntamento did online (Insert ag_agenda fallito)(cdnLavoratore:"
						+ cdnLavoratore + ")");
			} else if ("-4".equalsIgnoreCase(codiceRit)) {
				// Il lavoratore è già associato a questo appuntamento
				_logger.error(
						"Warning: prenotazione appuntamento did online (Il lavoratore è già associato a questo appuntamento)(cdnLavoratore:"
								+ cdnLavoratore + ")");
			} else if ("-5".equalsIgnoreCase(codiceRit)) {
				// Aggiornamento ag_slot fallito
				_logger.error(
						"Warning: prenotazione appuntamento did online (Aggiornamento ag_slot fallito)(cdnLavoratore:"
								+ cdnLavoratore + ")");
			} else if ("-6".equalsIgnoreCase(codiceRit)) {
				// riga di configurazione nella ts_config_loc non presente
				_logger.error(
						"Warning: prenotazione appuntamento did online (riga di configurazione nella ts_config_loc non presente)(cdnLavoratore:"
								+ cdnLavoratore + ")");
			} else {
				_logger.error("Warning: prenotazione appuntamento did online (warning generico)(cdnLavoratore:"
						+ cdnLavoratore + ")");
			}

			conn.commitTransaction();

		} catch (Exception e) {
			// appuntamento non ok
			// e.printStackTrace();
			try {
				conn.rollBackTransaction();
			} catch (EMFInternalError e1) {
				// e1.printStackTrace();
				_logger.error("Eccezione: prenotazione appuntamento did online (errore rollback)(cdnLavoratore:"
						+ cdnLavoratore + ")");
			}
			_logger.error("Eccezione: prenotazione appuntamento did online (cdnLavoratore:" + cdnLavoratore + ")");
		} finally {

			Utils.releaseResources(conn, command, dr);
		}

		//////////////////
		// RECUPERA SPI //
		//////////////////

		String prgSpi = recuperaPrgSpiPerContatto(); // cannot be null
		// System.out.println("prgSpi: "+prgSpi);

		//////////////////////////////////////
		// STEP 2: INVIO SMS (se possibile) //
		//////////////////////////////////////

		if (strCanSendSms != null && "S".equalsIgnoreCase(strCanSendSms)) {
			ContattoSMS contattoSms = new ContattoSMS();
			isSmsOk = contattoSms.creaPerDidOnline(cdnLavoratore, strCellLav, strNomeLav, strCognomeLav, codCpi,
					strDescrizioneCpi, strIndirizzoCpi, strTelCpi, strDataApp, strOraApp, prgSpi, isAppuntamentoOk,
					isSpiDisponibile, strNomeSpi, strCognomeSpi, strTelSpi);
		}
		// System.out.println("Sms sent: "+isSmsOk);

		////////////////////////////////////////
		// STEP 3: INVIO EMAIL (se possibile) //
		////////////////////////////////////////

		if (strCanSendEmail != null && "S".equalsIgnoreCase(strCanSendEmail)) {

			isEmailOk = insertContattoAndSendEmail(cdnLavoratore, strCellLav, codCpi, strEmailLav, strNomeLav,
					strCognomeLav, strEmailLav, strDataApp, strOraApp, strDurataApp, strDescrizioneCpi, strIndirizzoCpi,
					strTelCpi, prgSpi, isAppuntamentoOk, isSpiDisponibile, strNomeSpi, strCognomeSpi, strTelSpi);

		}

		// System.out.println("Email sent and ok: "+isEmailOk);

		//////////////////////////////////
		// STEP 4: INSERIMENTO EVIDENZA //
		//////////////////////////////////

		String testoEvidenza = "";

		testoEvidenza += "App: " + (isAppuntamentoOk ? "Sì" : "No") + "; ";
		testoEvidenza += "Sms: " + (isSmsOk ? "Sì" : "No") + "; ";
		testoEvidenza += "E-mail: " + (isEmailOk ? "Sì" : "No") + ". ";

		if (isAppuntamentoOk) {
			testoEvidenza += "Per il lavoratore è stato fissato automaticamente " + "l'appuntamento il giorno "
					+ strDataApp + " alle ore " + strOraApp + " presso il CPI " + strDescrizioneCpi + ".";

		} else {
			testoEvidenza += "Non è stato possibile fissare in automatico l'appuntamento.";
		}

		if (!isSmsOk) {
			testoEvidenza += "\n\nErrore durante l'invio della notifica SMS.";
		} else {
			testoEvidenza += "\n\nSMS inviato correttamente.";
		}

		if (!isEmailOk) {
			testoEvidenza += "\n\nErrore durante l'invio della notifica Email.";
		} else {
			testoEvidenza += "\n\nEmail inviata correttamente.";
		}

		isEvidenzaOk = insertEvidenza(cdnLavoratore, testoEvidenza);
		// System.out.println("Evidenza inserita: "+isEvidenzaOk);

		return isAppuntamentoOk;

	}

	private boolean insertContattoAndSendEmail(BigDecimal cdnLavoratore, String cellLavoratore, String codCpi,
			String destinatarioEmail, String strNomeLav, String strCognomeLav, String strEmailLav, String strDataApp,
			String strOraApp, String strDurataApp, String strDescrizioneCpi, String strIndirizzoCpi, String strTelCpi,
			String prgSpi, boolean isAppuntamentoOk, boolean isSpiDisponibile, String strNomeSpi, String strCognomeSpi,
			String strTelSpi) {

		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		Date oggi = new Date();
		String strData = formatter.format(oggi);
		formatter = new SimpleDateFormat("HH:mm");
		String strOra = formatter.format(oggi);

		boolean isContattoEmailInserito = false;
		boolean isEmailSpedita = false;
		boolean isContattoAggiornato = false;

		String[] emailSpecs = recuperaSpecsEmail();
		String oggettoEmail = emailSpecs[0];
		String mittenteEmail = emailSpecs[1];

		String mailMessage = "";

		if (isAppuntamentoOk) {

			if (isSpiDisponibile) {

				mailMessage = "Salve " + strNomeLav + " " + strCognomeLav + ","
						+ "\na seguito della Dichiarazione di Immediata Disponibilità da te rilasciata su Lavoro per Te in data "
						+ strData + ", ti è stato fissato un appuntamento presso il CPI di " + strDescrizioneCpi
						+ " in " + strIndirizzoCpi + " il giorno " + strDataApp + " alle ore " + strOraApp
						+ " per un colloquio con un operatore del CPI. Il colloquio avrà una durata indicativa di "
						+ strDurataApp + " minuti."
						+ "\n\nSe ti è impossibile presentarti all'appuntamento fissato, puoi contattare il CPI telefonicamente al numero "
						+ strTelSpi + " e chiedere di " + strNomeSpi + " " + strCognomeSpi
						+ ", per fissare un nuovo appuntamento."
						+ "\nTi ricordiamo che il colloquio con un operatore del CPI è obbligatorio per mantenere lo stato occupazionale derivante dalla tua Dichiarazione. ";

			} else {

				mailMessage = "Salve " + strNomeLav + " " + strCognomeLav + ","
						+ "\na seguito della Dichiarazione di Immediata Disponibilità da te rilasciata su Lavoro per Te in data "
						+ strData + ", ti è stato fissato un appuntamento presso il CPI di " + strDescrizioneCpi
						+ " in " + strIndirizzoCpi + " il giorno " + strDataApp + " alle ore " + strOraApp
						+ " per un colloquio con un operatore del CPI. Il colloquio avrà una durata indicativa di "
						+ strDurataApp + " minuti."
						+ "\n\nSe ti è impossibile presentarti all'appuntamento fissato, puoi contattare il CPI telefonicamente al numero "
						+ strTelCpi + " per fissare un nuovo appuntamento."
						+ "\nTi ricordiamo che il colloquio con un operatore del CPI è obbligatorio per mantenere lo stato occupazionale derivante dalla tua Dichiarazione. ";

			}

		} else {

			if (isSpiDisponibile) {

				mailMessage = "Salve " + strNomeLav + " " + strCognomeLav + ","
						+ "\nla Dichiarazione di Immediata Disponibilità (DID) da te rilasciata su Lavoro per Te in data "
						+ strData + "," + " prevede di fissare un appuntamento presso il CPI di " + strDescrizioneCpi
						+ " in " + strIndirizzoCpi + " per un colloquio con un operatore del CPI."
						+ "\nDevi presentarti o contattare telefonicamente  il CPI al numero " + strTelSpi
						+ " e chiedere di " + strNomeSpi + " " + strCognomeSpi + " per fissare un appuntamento."
						+ "\nTi ricordiamo che il colloquio con un operatore del CPI è obbligatorio per mantenere lo stato occupazionale derivante dalla tua Dichiarazione.";

			} else {

				mailMessage = "Salve " + strNomeLav + " " + strCognomeLav + ","
						+ "\nla Dichiarazione di Immediata Disponibilità (DID) da te rilasciata su Lavoro per Te in data "
						+ strData + "," + " prevede di fissare un appuntamento presso il CPI di " + strDescrizioneCpi
						+ " in " + strIndirizzoCpi + " per un colloquio con un operatore del CPI."
						+ "\nDevi presentarti o contattare telefonicamente  il CPI al numero " + strTelCpi
						+ " per fissare un appuntamento."
						+ "\nTi ricordiamo che il colloquio con un operatore del CPI è obbligatorio per mantenere lo stato occupazionale derivante dalla tua Dichiarazione.";

			}

		}

		// inserimento contatto

		TransactionQueryExecutor txExecutor = null;
		BigDecimal prgContatto = null;

		try {

			Object[] obj = new Object[13];

			txExecutor = new TransactionQueryExecutor(Values.DB_SIL_DATI);
			txExecutor.initTransaction();
			SourceBean rowsD = (SourceBean) txExecutor.executeQuery("NEXT_S_AG_CONTATTO", null, "SELECT");
			prgContatto = (BigDecimal) rowsD.getAttribute("ROW.KEY");

			String txtContatto =

					"Inviata a: " + destinatarioEmail + ", " + mittenteEmail + "\nOggetto: " + oggettoEmail + "\n\n"
							+ mailMessage;

			// Imposto i parametri
			obj[0] = prgContatto;
			obj[1] = codCpi;
			obj[2] = strData;
			obj[3] = strOra;
			obj[4] = prgSpi;
			obj[5] = txtContatto;
			obj[6] = "O";
			obj[7] = "3"; // tipo contatto email
			obj[8] = cdnLavoratore;
			obj[9] = "150";
			obj[10] = "150";
			obj[11] = cellLavoratore;
			obj[12] = "N";

			Boolean result = (Boolean) txExecutor.executeQuery("INSERT_AG_CONTATTO", obj, "INSERT");

			if (result && result.booleanValue()) {
				txExecutor.commitTransaction();
				isContattoEmailInserito = true;
				// System.out.println("Contatto email inserito correttamente");
			} else {
				txExecutor.rollBackTransaction();
				_logger.error("Errore: inserimento contatto email appuntamento did online");
			}

		} catch (Exception e) {

			_logger.error("Errore: inserimento contatto email appuntamento did online");

			// e.printStackTrace();
			try {
				txExecutor.rollBackTransaction();
			} catch (EMFInternalError e1) {
				_logger.error("Errore: inserimento contatto email appuntamento did online (su rollback)");
				// e1.printStackTrace();
			}
			// System.out.println("Errore nell'inserimento del contatto email");

		}

		/////////////////////////
		// STEP 2: INVIO EMAIL //
		/////////////////////////

		if (isContattoEmailInserito) {

			// solo se contatto inserito correttamente

			SendMail sendMail = new SendMail();
			sendMail.setSMTPServer(recuperaSmtpServerEmail());
			sendMail.setFromRecipient(mittenteEmail);
			sendMail.setToRecipient(strEmailLav);
			sendMail.setBccRecipient(mittenteEmail);
			sendMail.setSubject(oggettoEmail);
			sendMail.setBody(mailMessage);

			// System.out.println("smtp server: "+recuperaSmtpServerEmail());
			// System.out.println("email: "+strEmailLav);
			// System.out.println("oggetto: "+oggettoEmail);
			// System.out.println("mittente: "+mittenteEmail);
			// System.out.println("message: "+mailMessage);

			try {
				sendMail.send();
				isEmailSpedita = true;
			} catch (SendMailException e) {
				_logger.error("Errore: invio email appuntamento did online (a)");
				// e.printStackTrace();
			} catch (MessagingException e) {
				_logger.error("Errore: invio email appuntamento did online (b)");
				// e.printStackTrace();
			}

			/////////////////////////////////
			// STEP 3: AGGIORNA FLAG INVIO //
			/////////////////////////////////

			if (isEmailSpedita) {

				// solo se email spedita senza errori

				Object[] obj = new Object[2];
				obj[0] = "S";
				obj[1] = prgContatto;
				try {
					QueryExecutor.executeQuery("UPDATE_CONTATTO_SMS_INVIATO", obj, "UPDATE", Values.DB_SIL_DATI);
					isContattoAggiornato = true;
					// System.out.println("Contatto aggiornato.");
				} catch (Exception e) {
					_logger.error("Errore: aggiornamento contatto email appuntamento did online");
					// System.out.println("Errore aggiornamento contatto.");
				}

			}

		}

		// System.out.println("prgContatto (email): "+prgContatto);

		return isContattoAggiornato;

	}

	private boolean insertEvidenza(BigDecimal cdnLavoratore, String strEvidenza) {

		Object params[] = new Object[2];
		params[0] = cdnLavoratore;
		params[1] = strEvidenza;
		Boolean res = (Boolean) QueryExecutor.executeQuery("INSERT_EVIDENZA_DID_ONLINE", params, "INSERT",
				Values.DB_SIL_DATI);
		if (!res.booleanValue()) {
			return false;
		}

		return true;

	}

	/**
	 * 
	 * @return Array di stringhe: [0] oggetto, [1] mittente
	 */
	private String[] recuperaSpecsEmail() {

		String[] retVal = new String[] { "", "" };
		DataConnection dataConnection = null;
		SQLCommand sqlCommand = null;
		DataResult dataResult = null;
		try {
			DataConnectionManager dataConnectionManager = DataConnectionManager.getInstance();
			dataConnection = dataConnectionManager.getConnection(Values.DB_SIL_DATI);

			String statement = SQLStatements.getStatement("SELECT_EMAIL_SPECS_DID_ONLINE");
			sqlCommand = dataConnection.createSelectCommand(statement);

			List inputParameter = new ArrayList();

			dataResult = sqlCommand.execute(inputParameter);

			ScrollableDataResult scrollableDataResult = (ScrollableDataResult) dataResult.getDataObject();
			SourceBean sb = scrollableDataResult.getSourceBean();

			retVal[0] = (String) sb.getAttribute("ROW.stroggetto");
			retVal[1] = (String) sb.getAttribute("ROW.stremailmittente");

		} catch (com.engiweb.framework.error.EMFInternalError ex) {
			// it.eng.sil.util.TraceWrapper.fatal(_logger, "QueryExecutor::executeQuery:", (Exception) ex);
			_logger.error("Errore: recupero specifiche email appuntamento did online");
		} finally {
			com.engiweb.framework.dbaccess.Utils.releaseResources(dataConnection, sqlCommand, dataResult);

		}

		return retVal;

	}

	/**
	 * recupera il server smtp a partire dalla taballa ts_generale
	 * 
	 * @return stringa server smtp (es. smtp.eng.it)
	 */
	private String recuperaSmtpServerEmail() {

		String retVal = null;
		DataConnection dataConnection = null;
		SQLCommand sqlCommand = null;
		DataResult dataResult = null;
		try {
			DataConnectionManager dataConnectionManager = DataConnectionManager.getInstance();
			dataConnection = dataConnectionManager.getConnection(Values.DB_SIL_DATI);

			String statement = SQLStatements.getStatement("RecuperaSmtpServer");
			sqlCommand = dataConnection.createSelectCommand(statement);

			List inputParameter = new ArrayList();

			dataResult = sqlCommand.execute(inputParameter);

			ScrollableDataResult scrollableDataResult = (ScrollableDataResult) dataResult.getDataObject();
			SourceBean sb = scrollableDataResult.getSourceBean();

			retVal = (String) sb.getAttribute("ROW.smtpserver");

		} catch (com.engiweb.framework.error.EMFInternalError ex) {
			// it.eng.sil.util.TraceWrapper.fatal(_logger, "QueryExecutor::executeQuery:", (Exception) ex);
			_logger.error("Errore: recupero smtp server email appuntamento did online");
		} finally {
			com.engiweb.framework.dbaccess.Utils.releaseResources(dataConnection, sqlCommand, dataResult);

		}

		return retVal;

	}

	/**
	 * recupera il server smtp a partire dalla taballa ts_generale
	 * 
	 * @return stringa server smtp (es. smtp.eng.it)
	 */
	private String recuperaPrgSpiPerContatto() {

		String retVal = null;
		DataConnection dataConnection = null;
		SQLCommand sqlCommand = null;
		DataResult dataResult = null;
		try {
			DataConnectionManager dataConnectionManager = DataConnectionManager.getInstance();
			dataConnection = dataConnectionManager.getConnection(Values.DB_SIL_DATI);

			String statement = SQLStatements.getStatement("SELECT_PRGSPI_DID_ONLINE");
			sqlCommand = dataConnection.createSelectCommand(statement);

			List inputParameter = new ArrayList();

			dataResult = sqlCommand.execute(inputParameter);

			ScrollableDataResult scrollableDataResult = (ScrollableDataResult) dataResult.getDataObject();
			SourceBean sb = scrollableDataResult.getSourceBean();

			retVal = sb.getAttribute("ROW.PRGSPI").toString();

		} catch (com.engiweb.framework.error.EMFInternalError ex) {

			// ex.printStackTrace();
			// it.eng.sil.util.TraceWrapper.fatal(_logger, "QueryExecutor::executeQuery:", (Exception) ex);
			_logger.error("Errore: recupero prgspi appuntamento did online (a)");
		} catch (Exception e) {
			// System.out.println("Errore2!");
			// e.printStackTrace();
			_logger.error("Errore: recupero prgspi appuntamento did online (b)");
		} finally {
			com.engiweb.framework.dbaccess.Utils.releaseResources(dataConnection, sqlCommand, dataResult);
		}

		return retVal;

	}

	private boolean isPrenotazioneAppAbilitata() {

		boolean success = false;

		String retVal = null;
		DataConnection dataConnection = null;
		SQLCommand sqlCommand = null;
		DataResult dataResult = null;
		try {
			DataConnectionManager dataConnectionManager = DataConnectionManager.getInstance();
			dataConnection = dataConnectionManager.getConnection(Values.DB_SIL_DATI);

			String statement = SQLStatements.getStatement("SELECT_FLG_APP_DID_ONLINE");
			sqlCommand = dataConnection.createSelectCommand(statement);

			List inputParameter = new ArrayList();

			dataResult = sqlCommand.execute(inputParameter);

			ScrollableDataResult scrollableDataResult = (ScrollableDataResult) dataResult.getDataObject();
			SourceBean sb = scrollableDataResult.getSourceBean();

			retVal = sb.getAttribute("ROW.STRVALORE").toString();

			if ("1".equalsIgnoreCase(retVal)) {
				success = true;
			}

		} catch (com.engiweb.framework.error.EMFInternalError ex) {
			_logger.error("Errore: lettura flag prenotazione abilitata per appuntamento DID online (a)");
		} catch (Exception e) {
			_logger.error("Errore: lettura flag prenotazione abilitata per appuntamento DID online (b)");
		} finally {
			com.engiweb.framework.dbaccess.Utils.releaseResources(dataConnection, sqlCommand, dataResult);
		}

		return success;

	}

	/**
	 * recupera un campo stringa restituito in output da una stored procedure
	 * 
	 * @param outputParams
	 * @param i
	 * @return
	 * 
	 */
	private String getStringParam(List outputParams, int i) {
		PunctualDataResult pdr = (PunctualDataResult) outputParams.get(i);
		DataField df = pdr.getPunctualDatafield();
		String value = df.getStringValue();
		if (value == null) {
			value = "";
		} else {
			value = value.trim();
		}
		return value;
	}

}
