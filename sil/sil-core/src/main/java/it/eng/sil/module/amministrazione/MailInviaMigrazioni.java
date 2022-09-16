/*
 * Creato il 22-nov-04
 * Author: vuoto
 * 
 */
package it.eng.sil.module.amministrazione;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import javax.mail.MessagingException;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.DataConnectionManager;
import com.engiweb.framework.dbaccess.SQLStatements;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.SQLCommand;
import com.engiweb.framework.dbaccess.sql.result.DataResult;
import com.engiweb.framework.dbaccess.sql.result.ScrollableDataResult;
import com.engiweb.framework.error.EMFErrorSeverity;
import com.engiweb.framework.error.EMFUserError;
import com.engiweb.framework.util.QueryExecutorObject;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.SourceBeanUtils;
import it.eng.afExt.utils.StringUtils;
import it.eng.sil.Values;
import it.eng.sil.mail.SendMail;
import it.eng.sil.mail.SendMailException;
import it.eng.sil.mail.Zip;
import it.eng.sil.module.StepByStepMarchable;

/**
 * @author vuoto
 * 
 */
public class MailInviaMigrazioni implements StepByStepMarchable {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(MailInviaMigrazioni.class.getName());

	private final String thisClassName = StringUtils.getClassName(this);

	private List dirs = null;
	private int indexDir = 0;
	private SendMail sendMail = null;
	private List bagList = null;
	String mittente = null;
	private BigDecimal bdPrgMigrazione = null;
	private String corpoMail = null;

	/**
	 * Inizializzazione dell'Esecutore. In genere è utile precaricare i dati per i passi da eseguire. In input riceve la
	 * "getConfig()" del modulo, in caso possa servire.
	 */
	public void initMarchable(SourceBean moduleConfig) throws EMFUserError {

		// Recupero record con i dati correnti
		SourceBean row = EsportaMigrUtils.recuperaDati();

		// Recupero e controllo il path in cui salvare
		String savetoPath = EsportaMigrUtils.getAndCheckSaveToPath(row);

		// CONTROLLI
		boolean flgInvio = SourceBeanUtils.getAttrBoolean(row, "FLGINVIO", false);
		boolean flgInCorso = SourceBeanUtils.getAttrBoolean(row, "FLGINCORSO", false);

		bdPrgMigrazione = (BigDecimal) row.getAttribute("PRGMIGRAZIONE");

		// E' in corso un'esportazione da parte di un altro utente
		if (flgInCorso) {
			throw new EMFUserError(EMFErrorSeverity.BLOCKING,
					MessageCodes.EsportaMigrazioni.ERR_INVMAIL_MIGRAZ_INCORSO);
		}
		// L'invio e-mail per questa esportazione è stato già effettuato
		if (flgInvio) {
			throw new EMFUserError(EMFErrorSeverity.BLOCKING, MessageCodes.EsportaMigrazioni.ERR_INVMAIL_GIA_INVIATA);
		}

		// INIZIALIZZAZIONE
		File dir = new File(savetoPath);
		if (dir.isDirectory()) {

			File[] files = dir.listFiles(new FileFilter() {
				public boolean accept(File f) {

					// Guardo quanti file (non directory) sono contenuti nella
					// directory
					// appena recuperata. Se nessuno, non la considero!
					File[] filesInF = f.listFiles(new FileFilter() {
						public boolean accept(File f) {
							return f.isFile();
						};
					});
					return f.isDirectory() && ((filesInF != null) && (filesInF.length > 0));
				};
			});
			dirs = Arrays.asList(files);
		}

		RequestContainer requestContainer = RequestContainer.getRequestContainer();
		SourceBean serviceRequest = (SourceBean) requestContainer.getServiceRequest();
		mittente = (String) serviceRequest.getAttribute("mittente");
		corpoMail = (String) serviceRequest.getAttribute("corpoMail");
		String oggetto = (String) serviceRequest.getAttribute("oggetto");

		sendMail = new SendMail();

		sendMail.setSMTPServer(recuperaSmtpServer());
		sendMail.setFromRecipient(mittente);
		sendMail.setSubject(oggetto);
		// sendMail.setBody(corpoMail);

		bagList = new ArrayList();

	}

	/**
	 * Rende TRUE se c'è almeno un passo successivo da eseguire col metodo "doNextStepMarchable()". Serve per
	 * controllare se si è terminata l'elaborazione.
	 */
	public boolean hasNextStepMarchable() {

		return (indexDir < dirs.size());

	}

	/**
	 * Elabora tutti i dati relativi al passo successivo. può essere invocato solo dopo una "initMarchable()". In caso
	 * di errore, genera un eccezione (già come messaggio per l'utente).
	 */
	public void doNextStepMarchable() throws EMFUserError {

		setToAndZips((File) dirs.get(indexDir));
		indexDir++;
	}

	/**
	 * Rende la percentuale di elaborazione fatta.
	 */
	public double getProgPercMarchable() {
		int size = dirs.size();

		if (size != 0)
			return (((double) indexDir) / size) * 100;
		else
			return 100;
	}

	/**
	 * Metodo chiamato durante l'esecuzione della fase di INIZIO. può essere utile completare la configurazione o fare
	 * ulteriori controlli.
	 */
	public void callbackOnInizioMarchable() throws EMFUserError {
	}

	/**
	 * Metodo chiamato al termine dell'esecuzione dell'ULTIMA SUCC. può essere utile per salvare il nuovo stato
	 * raggiunto. NB: insieme a "callbackOnInizioMarch" possono gestire operazioni fatte in un contesto transazionale.
	 */
	public void callbackOnLastSuccMarchable() throws EMFUserError {

		if (bdPrgMigrazione == null) {
			return;
		}

		// String retVal = null;
		DataConnection dataConnection = null;
		SQLCommand sqlCommand = null;
		DataResult dataResult = null;
		try {
			DataConnectionManager dataConnectionManager = DataConnectionManager.getInstance();
			dataConnection = dataConnectionManager.getConnection(Values.DB_SIL_DATI);

			// String statement =
			// SQLStatements.getStatement("ESPORTA_MIGR_SETINVIO_S_DOPO_MAIL");
			// sqlCommand = dataConnection.createUpdateCommand(statement);

			List inputParameter = new ArrayList();

			inputParameter.add(dataConnection.createDataField("", Types.BIGINT, bdPrgMigrazione));

			QueryExecutorObject qeo = new QueryExecutorObject();
			qeo.setTokenStatement("ESPORTA_MIGR_SETINVIO_S_DOPO_MAIL");
			qeo.setInputParameters(inputParameter);
			qeo.setType(QueryExecutorObject.UPDATE);
			qeo.setDataConnection(dataConnection);
			qeo.exec();

			// retVal = (String) sb.getAttribute("ROW.EMAIL");

		} catch (com.engiweb.framework.error.EMFInternalError ex) {
			it.eng.sil.util.TraceWrapper.debug(_logger, thisClassName + "::callbackOnLastSuccMarchable(...)",
					(Exception) ex);

			throw new EMFUserError(EMFErrorSeverity.ERROR, MessageCodes.MailMigrazioni.ERR_UPDATE_STATO);

		} finally {
			com.engiweb.framework.dbaccess.Utils.releaseResources(dataConnection, sqlCommand, dataResult);

		}

		// return retVal;
	}

	/**
	 * Metodo chiamato in caso di errore durante una SUCC. può essere utile per annullare dei "lock" iniziali o
	 * riportare l'errore. per altre vie. Viene passata l'eccezione che si è avuta. può essere utile o no.
	 */
	public void callbackOnErrorMarchable(Exception e) {
	}

	private void setToAndZips(File dirCPI) throws EMFUserError {

		String dirname = dirCPI.getName();

		// LA DIRECTORY DEVE ESSERE DI 6 o 7 CARATTERI (es: "T" + "VR" + "0100")
		// GG 29-03-05 - Aggiunta dimensione di 7 caratteri
		int length = dirname.length();
		if ((length != 6) && (length != 7)) {
			_logger.debug(thisClassName + "::setToAndZips() la directory non ha la lunghezza di 6 o 7 caratteri, ma "
					+ length + " caratteri");

			return; // ABORT
		}

		// ********************************************
		// Start..............
		// ********************************************
		MailStatusBag bag = new MailStatusBag();
		bag.setOK(true);

		// ***************************************
		// Recupero della mail del CPI
		// ***************************************

		String provincia = dirname.substring(1, 3); // caratteri 2-3

		String cpi = dirname.substring(3);
		// GG 29-03-05 - Modifica definitiva alla regola di generazione dei nomi
		// dir e file:
		// Il pezzo di codice CPI è di 4 caratteri:
		// nulla da fare, va bene prendere dal 4' in poi, cioè 4-5-6-7
		// ("TBO0300").
		// Perà si è dovuta cambiare la query SQL. Si veda statement
		// "RecuperaMailConCPI"

		String destinatario = recuperaMailCpi(provincia, cpi);
		if (destinatario == null)
			destinatario = "";

		bag.setCpi(cpi);
		bag.setEmail(destinatario);
		bag.setProvincia(provincia);

		sendMail.setToRecipient(destinatario);
		List fileList = new ArrayList();

		// *******************************************************
		// Zip dei file ed eventuale cancellazione degli originali
		// *******************************************************
		File[] files = dirCPI.listFiles();
		for (int i = 0; i < files.length; i++) {
			File file = files[i];

			if (file.isFile()) {

				// String filename = file.getName();

				List filesDaZippare = new ArrayList(1);
				filesDaZippare.add(file);
				Zip zip = new Zip();
				zip.setFiles(filesDaZippare);
				File fileZip = new File(file.getAbsolutePath().replace('.', '_') + ".zip");

				zip.setZipFile(fileZip);
				try {
					zip.zip();
				} catch (IOException e) {

					it.eng.sil.util.TraceWrapper.debug(_logger, e.getMessage(), e);

					throw new EMFUserError(EMFErrorSeverity.ERROR, MessageCodes.MailMigrazioni.ERR_ZIP);

				}

				if (!file.delete()) {
					_logger.debug(
							thisClassName + "::setToAndZips() Non si riesce a cancellare l'originale del file zippato "
									+ file.getAbsolutePath());

					throw new EMFUserError(EMFErrorSeverity.ERROR, MessageCodes.MailMigrazioni.ERR_DEL);

				}

				fileList.add(fileZip);
			}
		}

		// ***************************************
		// Spotsamento dei file nello storico
		// ***************************************

		List filesDaInviare = new ArrayList();

		int year = Calendar.getInstance().get(Calendar.YEAR);
		String anno = String.valueOf(year);
		for (int i = 0; i < fileList.size(); i++) {
			File fzip = (File) fileList.get(i);
			String pathStorico = fzip.getParentFile().getAbsolutePath().concat(File.separator + "STORICO_" + anno);

			File dirStorico = new File(pathStorico);

			boolean ok = true;
			if (!dirStorico.exists()) {
				ok = dirStorico.mkdir();
			}

			if (!ok) {
				_logger.debug(thisClassName + "::setToAndZips()Errore nella creazione della cartella dello storico:"
						+ pathStorico);

				throw new EMFUserError(EMFErrorSeverity.ERROR, MessageCodes.MailMigrazioni.ERR_MKDIR);

			}

			File renamedFile = new File(dirStorico + File.separator + fzip.getName());
			ok = fzip.renameTo(renamedFile);
			if (!ok) {

				_logger.debug(thisClassName
						+ "::setToAndZips() Errore nello spostamento del file nella cartella dello storico:"
						+ pathStorico);

				throw new EMFUserError(EMFErrorSeverity.ERROR, MessageCodes.MailMigrazioni.ERR_MV);

			}
			filesDaInviare.add(renamedFile);

		}

		sendMail.setAttachments(filesDaInviare);

		// *****************************************
		// Invio mail al mittente
		// ****************************************

		sendMail.setToRecipient(mittente);

		sendMail.setBody("Mail inviata a " + destinatario + "\r\n\r\n" + corpoMail);

		try {

			sendMail.send();

		} catch (SendMailException e1) {
			it.eng.sil.util.TraceWrapper.debug(_logger, e1.getMessage(), e1);

			throw new EMFUserError(EMFErrorSeverity.ERROR, MessageCodes.MailMigrazioni.ERR_PARAMS);

		} catch (MessagingException e1) {
			it.eng.sil.util.TraceWrapper.debug(_logger, e1.getMessage(), e1);

			throw new EMFUserError(EMFErrorSeverity.ERROR, MessageCodes.MailMigrazioni.ERR_MITTENTE);
		}

		// *****************************************
		// Invio mail al destinatario
		// ****************************************

		bag.setFileList(filesDaInviare);
		sendMail.setToRecipient(destinatario);
		sendMail.setBody(corpoMail);

		try {
			sendMail.send();
		} catch (SendMailException e1) {
			it.eng.sil.util.TraceWrapper.debug(_logger, e1.getMessage(), e1);

			bag.setMsg("Errore di invio della mail. Controllare i parametri di invio");

			bag.setOK(false);
			bagList.add(bag);
			return;
		} catch (MessagingException e1) {
			it.eng.sil.util.TraceWrapper.debug(_logger, e1.getMessage(), e1);

			bag.setMsg("Errore di invio della mail. Controllare l'indirizzo dei destinatari");
			bagList.add(bag);
			return;
		}

		bagList.add(bag);

	}

	private String recuperaMailCpi(String provincia, String pezzoCpi) {
		String retVal = null;
		DataConnection dataConnection = null;
		SQLCommand sqlCommand = null;
		DataResult dataResult = null;
		try {
			DataConnectionManager dataConnectionManager = DataConnectionManager.getInstance();
			dataConnection = dataConnectionManager.getConnection(Values.DB_SIL_DATI);

			// GG 29-03-05 - Modifica definitiva alla regola di generazione dei
			// nomi dir e file:
			// Modificato l'SQL di "RecuperaMailConCPI" per controllare i
			// caratteri 6-7-8-9 del COD.CPI
			String statement = SQLStatements.getStatement("RecuperaMailConCPI");
			sqlCommand = dataConnection.createSelectCommand(statement);

			List inputParameter = new ArrayList();

			inputParameter.add(dataConnection.createDataField("", Types.VARCHAR, pezzoCpi));
			inputParameter.add(dataConnection.createDataField("", Types.VARCHAR, provincia));

			dataResult = sqlCommand.execute(inputParameter);

			ScrollableDataResult scrollableDataResult = (ScrollableDataResult) dataResult.getDataObject();
			SourceBean sb = scrollableDataResult.getSourceBean();

			retVal = (String) sb.getAttribute("ROW.EMAIL");

		} catch (com.engiweb.framework.error.EMFInternalError ex) {
			it.eng.sil.util.TraceWrapper.fatal(_logger, "QueryExecutor::executeQuery:", (Exception) ex);

		} finally {
			com.engiweb.framework.dbaccess.Utils.releaseResources(dataConnection, sqlCommand, dataResult);

		}

		return retVal;

	}

	/**
	 * @return
	 */
	public List getBagList() {
		return bagList;
	}

	/**
	 * @param list
	 */
	public void setBagList(List list) {
		bagList = list;
	}

	private String recuperaSmtpServer() {

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
			it.eng.sil.util.TraceWrapper.fatal(_logger, "QueryExecutor::executeQuery:", (Exception) ex);

		} finally {
			com.engiweb.framework.dbaccess.Utils.releaseResources(dataConnection, sqlCommand, dataResult);

		}

		return retVal;

	}

}