/*
 * Creato il 17-mar-05
 * Author: rolfini
 * 
 */
package it.eng.sil.bean.SilProLabor;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.configuration.ConfigSingleton;

/**
 * @author rolfini
 * 
 */
public class Logger {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(Logger.class.getName());

	private boolean log = true; // flag che indica se deve essere scritto il
								// file di log. Viene modificato leggendo il
								// singleton di
	// configurazione nel costruttore. Comunque di default è TRUE (come si
	// evince :) )
	private String urlScambio;
	private final String admLog = "SIL.log"; // nome del file di log di
												// amministrazione
	private final String usrLog = "USER.log"; // nome del file di log per gli
												// utenti

	protected Logger(ConfigSingleton config) {
		SourceBean logConfig = (SourceBean) config.getFilteredSourceBeanAttribute("SILPROLABOR.LOG", "NAME",
				"ABILITATO");
		this.log = ((String) logConfig.getAttribute("value")).equals("true");
		SourceBean urlScambioConfig = (SourceBean) config.getFilteredSourceBeanAttribute("SILPROLABOR.DIR", "NAME",
				"SCAMBIO");
		this.urlScambio = (String) urlScambioConfig.getAttribute("PATH");
	}

	/**
	 * WriteLog Scrive sul file identificato dal nome passato nel secondo parametro il messaggio specificato nel
	 * parametro "message". Se il log esiste, appende il messaggio, altrimenti crea un file di log nuovo e scrive il
	 * messaggio in esso. Il formato del messaggio è il seguente: [DATA] [ORA] [MESSAGGIO] esempio: 07-07-2004 12:55:20
	 * Inizio elaborazione:MM_1168P.XML
	 * 
	 * @param message
	 *            messaggio da scrivere nel log
	 * @param logName
	 *            nome del file di log da scrivere
	 * @return nothing
	 */
	private void writeLog(String message, String logName) {

		String logUrl = urlScambio + File.separator + logName;

		try {
			FileOutputStream destination = new FileOutputStream(logUrl, true);
			String data = (new SimpleDateFormat("dd-MM-yyyy HH:mm:ss")).format(new Date());

			DataOutputStream d = new DataOutputStream(destination);
			String logRow = data + " \t " + message + "\r\n";
			d.writeBytes(logRow);
			d.close();
			destination.close();
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("Errore irreversibile dell'applicazione");
		}

	}

	/* FUNZIONI PUBLIC (PROTECTED) */

	protected void writeLogUsr(String message) {
		if (log) {
			writeLog(message, usrLog);
		}
	}

	protected void writeLogAdm(String message) {
		/*
		 * if (log) { writeLog(message, admLog); }
		 */
		_logger.debug(message);

	}

}