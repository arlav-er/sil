/*
 * Creato il 21-gen-05
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.module.alert;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.DataConnectionManager;
import com.engiweb.framework.dbaccess.Utils;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.SQLCommand;
import com.engiweb.framework.dbaccess.sql.result.DataResult;
import com.engiweb.framework.dispatching.module.AbstractModule;

/**
 * @author Togna Cosimo
 * @author D'Auria Giovanni
 * 
 *         Per modificare il modello associato al commento di questo tipo generato, aprire
 *         Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
public class MessaggiVisti extends AbstractModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(MessaggiVisti.class.getName());

	/*
	 * (non Javadoc)
	 * 
	 * @see com.engiweb.framework.dispatching.service.ServiceIFace#service(com.engiweb.framework.base.SourceBean,
	 * com.engiweb.framework.base.SourceBean)
	 */
	public void service(SourceBean serviceRequest, SourceBean serviceResponse) throws Exception {

		String pool = (String) getConfig().getAttribute("POOL");
		String className = this.getClass().getName();
		int numeroMessaggi = Integer.parseInt((String) serviceRequest.getAttribute("numeromessaggi"));
		String utente = (String) serviceRequest.getAttribute("utente");

		for (int i = 0; i < numeroMessaggi; i++) {
			String statement = "INSERT INTO TS_MESSAGGI_LETTI (CDNUTENTE, PRGTSMESSAGGILETTI, CDNMESSAGGIO) VALUES (";
			statement += utente + ",";
			statement += "S_TS_MESSAGGI_LETTI.NEXTVAL, ";
			statement += ((String) serviceRequest.getAttribute("codmessaggio" + i)) + ")";

			DataConnectionManager dcm = null;
			DataConnection dc = null;
			SQLCommand cmdInsert = null;
			DataResult dr = null;
			try {
				// Istanzia la classe che ritorna il contenuto della query

				dcm = DataConnectionManager.getInstance();
				dc = dcm.getConnection(pool);
				cmdInsert = dc.createInsertCommand(statement);
				cmdInsert.execute();
			} // try
			catch (Exception ex) {
				it.eng.sil.util.TraceWrapper.fatal(_logger, className + "MessaggiVisti::insert:", ex);

			} finally {
				Utils.releaseResources(dc, cmdInsert, dr);
			}
		}

	}

}