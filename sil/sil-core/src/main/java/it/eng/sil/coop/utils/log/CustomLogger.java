/*
 * Creato il 07-apr-04
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.coop.utils.log;

import java.lang.reflect.Method;

import org.apache.log4j.DailyRollingFileAppender;

/**
 * @author rolfini
 *
 */

public class CustomLogger extends DailyRollingFileAppender {

	private String fileNameTemp = null;

	private void getLogPath() {
		try {
			javax.naming.InitialContext ctx = new javax.naming.InitialContext();
			fileNameTemp = (String) ctx.lookup("java:comp/env/Log");
		} catch (Exception sex) {
			sex.printStackTrace();
			System.out.println("Errore nel reperimento del path del log: " + sex.getMessage());
		}

	}

	/**
	 * 
	 */

	private String getProcessName() {

		try {

			Class adminServiceFactoryClass = Class.forName("com.ibm.websphere.management.AdminServiceFactory");

			Method getAdminServiceMethod = adminServiceFactoryClass.getMethod("getAdminService", new Class[] {});
			Object adminServiceObj = getAdminServiceMethod.invoke(null, new Object[] {});

			// retVal = adminService.getProcessName();
			Class adminServiceClass = Class.forName("com.ibm.websphere.management.AdminService");

			Method getProcessNameMethod = adminServiceClass.getMethod("getProcessName", new Class[] {});
			Object processNameObject = getProcessNameMethod.invoke(adminServiceObj, new Object[] {});

			if (processNameObject != null)
				return "_" + processNameObject.toString();

		} catch (ClassNotFoundException ex) {
			// siamo fuoi contesto WebSphere
			// es. Stiamo usando JBoss oppure lanciamo i batch
			// senza impostare admin.jar di WebSphere
		} catch (Exception e) {
			// TODO Blocco catch generato automaticamente
			// e.printStackTrace();
			System.out.println("CustomLogger::getProcessName: fuori contesto WebSphere");
		}

		return "";

	}

	private void impostaNomeFile() {

		getLogPath();

		int idxPunto = fileNameTemp.lastIndexOf(".");
		String suffix = "";

		if (idxPunto > 0) {
			suffix = "." + fileNameTemp.substring(idxPunto + 1);
			fileNameTemp = fileNameTemp.substring(0, idxPunto);
		}

		fileNameTemp = fileNameTemp + getProcessName() + suffix;
		fileName = fileNameTemp;
		System.out.println("Log in inizializzazione sul file: " + fileNameTemp);
	}

	/**
	 * 
	 */
	public CustomLogger() {
		super();
		impostaNomeFile();
	}

}
