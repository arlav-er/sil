/*
 * Creato il 17-dic-04
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.afExt.tracing;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FilePermission;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.Date;

import org.apache.log4j.DailyRollingFileAppender;

import com.engiweb.framework.configuration.ConfigSingleton;

/**
 * @author Maurizio Discepolo
 * @author Franco Vuoto
 * 
 */

public class CustomFileAppender extends DailyRollingFileAppender {
	private static final String TRACE_FILE_PATH = "/tmp/";
	private static final String TRACE_FILE_NAME = "SIL-";
	private String _name = null;
	private boolean _isOutOfService = false;

	private int _minLogSeverity = 0;
	private boolean _debug = false;
	private boolean _traceThreadName = false;

	/**
	 * 
	 */

	private String getProcessName() {

		try {

			/*
			 * String appServerName = adminService.getCellName()+ "/"+adminService.getNodeName()+
			 * "/"+adminService.getProcessName() ; long freeMemory=Runtime.getRuntime().freeMemory() / 1024; long
			 * totalMemory=Runtime.getRuntime().totalMemory() / 1024;
			 */

			// AdminService adminService =
			// AdminServiceFactory.getAdminService();
			Class adminServiceFactoryClass = Class.forName("com.ibm.websphere.management.AdminServiceFactory");

			Method getAdminServiceMethod = adminServiceFactoryClass.getMethod("getAdminService", new Class[] {});
			Object adminServiceObj = getAdminServiceMethod.invoke(null, new Object[] {});

			// retVal = adminService.getProcessName();
			Class adminServiceClass = Class.forName("com.ibm.websphere.management.AdminService");

			Method getProcessNameMethod = adminServiceClass.getMethod("getProcessName", new Class[] {});
			Object processNameObject = getProcessNameMethod.invoke(adminServiceObj, new Object[] {});

			if (processNameObject != null)
				return processNameObject.toString();

		} catch (ClassNotFoundException ex) {
			// siamo fuoi contesto WebSphere
			// es. Stiamo usando JBoss oppure lanciamo i batch
			// senza impostare admin.jar di WebSphere
		} catch (Exception e) {
			// TODO Blocco catch generato automaticamente
			// e.printStackTrace();
			System.out.println("CustomFileAppender::getProcessName: fuori contesto WebSphere");
		}

		return "";

	}

	private void impostaNomeFile() {
		ConfigSingleton config = ConfigSingleton.getInstance();
		setName((String) config.getAttribute("NAME"));
		String traceFilePath = (String) config.getAttribute("TRACING.LOGGER.CONFIG.TRACE_PATH");
		String afRootPath = ConfigSingleton.getRootPath();
		if (traceFilePath == null)
			traceFilePath = TRACE_FILE_PATH;

		String traceFileName = (String) config.getAttribute("TRACING.LOGGER.CONFIG.TRACE_NAME");
		if (traceFileName == null)
			traceFileName = TRACE_FILE_NAME;

		String debug = (String) config.getAttribute("TRACING.LOGGER.CONFIG.DEBUG");
		_debug = ((debug != null) && (debug.equalsIgnoreCase("TRUE")));

		String traceThreadName = (String) config.getAttribute("TRACING.LOGGER.CONFIG.TRACE_THREAD_NAME");
		_traceThreadName = ((traceThreadName != null) && (traceThreadName.equalsIgnoreCase("TRUE")));

		String appendStr = (String) config.getAttribute("TRACING.LOGGER.CONFIG.APPEND");
		boolean append = ((appendStr != null) && (appendStr.equalsIgnoreCase("TRUE")));

		fileName = "";
		if (ConfigSingleton.getRootPath() != null)
			fileName += ConfigSingleton.getRootPath();

		// Se il file none' scrivibile, si prova ad aggiungere un
		// progressivo numerico alla fine del nome del file. Si tenta per un
		// "tot"
		// di valori. Questo e' utile se c'e' un lock sul file da scrivere!
		// Nota: al primo tentativo non si aggiunge alcun "progressivo".

		fileName += traceFilePath + traceFileName + "_" + getProcessName();
		PrintWriter _traceFile = null;

		// ESCO quando "_isOutOfService" vale false (il file e' aperto in
		// scrittura)
		// oppure quando il progressivo "temp" arriva al valore 100, oppure
		// entrambi.

		int temp = 1;
		String fileNameTemp = fileName + "_" + temp + ".log";

		if (!append) {
			do {
				System.out.println("CustomFileAppender::openTraceFile: tentativo di apertura del file " + fileNameTemp);
				try {
					File f = new File(fileNameTemp);
					if (f.exists())
						throw new IOException("CustomFileAppender::openTraceFile: apertura FALLITA");
					_traceFile = new PrintWriter(new BufferedWriter(new FileWriter(fileNameTemp, append)));
					// Stampo un messaggio di prova (per controllo sulla
					// effettiva
					// scrittura in caso di più istanze dell'applicazione).
					_traceFile.println("===============================================================");
					_traceFile.println("====== Application started on " + (new Date()).toString());
					_traceFile.println("===============================================================");
					_traceFile.flush();

					System.out.println("CustomFileAppender::openTraceFile: apertura OK");
					fileName = fileNameTemp;
					_isOutOfService = false;
				} catch (NullPointerException ex) {
					System.out.println("CustomFileAppender::openTraceFile: nome file nullo");
					fileNameTemp = TRACE_FILE_NAME + temp + ".log";
					System.out.println("CustomFileAppender::openTraceFile: uso il file di log: " + fileNameTemp);
					_isOutOfService = true;
				} catch (IOException ex) {
					// Provo il prossimo file
					System.out.println("CustomFileAppender::openTraceFile: apertura FALLITA");
					temp++;
					fileNameTemp = fileName + "_" + temp + ".log";
					_isOutOfService = true;
				}
			} while ((_isOutOfService) && (temp < 100));
		} else {
			// Append = true
			// Si deve impostare a true tale flag solo
			// in un ambiente non cluster
			// In tal caso e' inutile ciclare.

			FilePermission permission = new FilePermission(traceFilePath, "read, write");
			try {
				_traceFile = new PrintWriter(new BufferedWriter(new FileWriter(fileNameTemp, append)));
				// Stampo un messaggio di prova (per controllo sulla effettiva
				// scrittura in caso di più istanze dell'applicazione).
				_traceFile.println("===============================================================");
				_traceFile.println("====== Application started on " + (new Date()).toString());
				_traceFile.println("===============================================================");

				_traceFile.flush();

				System.out.println("CustomFileAppender:impostaNomeFile: apertura OK");
				fileName = fileNameTemp;
				_traceFile.close();
			} catch (IOException ex) {
				// Provo il prossimo file
				System.out.println("CustomFileAppender:impostaNomeFile: apertura FALLITA");
				System.out.println(
						"CustomFileAppender:impostaNomeFile: [" + _name + "] impossibile aprire il file di log");
				_isOutOfService = true;

			}

		}
		if (_isOutOfService) {
			System.out.println("CustomFileAppender::openTraceFile: [" + _name + "] impossibile aprire il file di log");
		} else
			System.out.println("CustomFileAppender::openTraceFile: uso il file di log: " + fileName);

	} // private void openTraceFile()

	/**
	 * 
	 */
	public CustomFileAppender() {
		super();
		impostaNomeFile();
	}

}
