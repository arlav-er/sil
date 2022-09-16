/*
 * Creato il 14-gen-05
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.afExt.utils;

import java.net.InetAddress;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.DataConnectionManager;
import com.engiweb.framework.dbaccess.Utils;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.command.std.StoredProcedureCommand;
import com.engiweb.framework.dbaccess.sql.result.DataResult;
import com.engiweb.framework.init.InitializerIFace;

import it.eng.sil.Values;

/**
 * @author Togna Cosimo
 * 
 *         Questa classe, in fase di inizializzazione dell'applicazione, rimuove eventuali utenti rimasti loggati che in
 *         realtà non hanno più una sessione attiva
 */
public class InizializzaAccessoUtenti implements InitializerIFace {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(InizializzaAccessoUtenti.class.getName());

	private SourceBean config;

	public InizializzaAccessoUtenti() {
		super();
		config = null;
	}

	public void init(SourceBean config) {

		it.eng.sil.util.TraceWrapper.debug(_logger, "AccessoUtentiInitializer::init: config", config);

		this.config = config;
		DataConnection conn = null;
		StoredProcedureCommand command = null;
		DataResult dr = null;
		try {
			// imposto la chiamata per la stored procedure
			DataConnectionManager dcm = DataConnectionManager.getInstance();
			conn = dcm.getConnection(Values.DB_SIL_DATI);

			command = (StoredProcedureCommand) conn
					.createStoredProcedureCommand("{ call PG_ACCESSO.clearTableAccessoUtente(?) }");

			// imposto i parametri
			List parameters = new ArrayList(1);
			parameters
					.add(conn.createDataField("STRHOSTNAME", Types.VARCHAR, InetAddress.getLocalHost().getHostName()));
			command.setAsInputParameters(0);

			// eseguo!!
			dr = command.execute(parameters);
		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.fatal(_logger, "InizializzaAccessoUtenti: ", (Exception) e);

		} finally {
			Utils.releaseResources(conn, command, null);

		}

	}

	public SourceBean getConfig() {
		return config;
	}

}