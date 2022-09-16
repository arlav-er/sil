/*
 * Creato il 22-gen-04
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.module.config;

import java.sql.Connection;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.DataConnectionManager;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dispatching.module.AbstractModule;

import it.eng.sil.Values;
import it.eng.sil.util.ga.db.Tabella;

/**
 * @author Franco Vuoto
 * 
 */

public class RicercaTabDec extends AbstractModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(RicercaTabDec.class.getName());
	private String className = this.getClass().getName();

	public RicercaTabDec() {
	}

	public void service(SourceBean request, SourceBean response) {

		String tableName = (String) request.getAttribute("TABLE_NAME");
		DataConnection dataConnection = null;
		Connection conn = null;

		try {

			DataConnectionManager dataConnectionManager = DataConnectionManager.getInstance();
			dataConnection = dataConnectionManager.getConnection(Values.DB_SIL_DATI);

			conn = dataConnection.getInternalConnection();
			Tabella tab = new Tabella(conn, Values.DB_SIL_DATI, tableName);
			response.setAttribute("Tab", tab);

		} catch (Exception ex) {

			it.eng.sil.util.TraceWrapper.debug(_logger, className + "::service()", ex);

		} finally {
			try {
				dataConnection.close();
				// conn.close();
			} catch (Exception ex) {
				it.eng.sil.util.TraceWrapper.debug(_logger, className + "::service()", ex);

			}

		}

	}

}