package it.eng.sil.module;

import java.util.List;

import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.DataConnectionManager;
import com.engiweb.framework.dbaccess.Utils;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.DataField;
import com.engiweb.framework.dbaccess.sql.command.std.StoredProcedureCommand;
import com.engiweb.framework.dbaccess.sql.result.DataResult;
import com.engiweb.framework.dbaccess.sql.result.PunctualDataResult;
import com.engiweb.framework.dispatching.module.AbstractModule;
import com.engiweb.framework.error.EMFErrorSeverity;
import com.engiweb.framework.error.EMFInternalError;

import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.StringUtils;
import it.eng.sil.Values;

/**
 * Modulo astratto (occorre implementare dei metodi) per eseguire correttamente una Stored Procedure.
 * 
 * @author Luigi Antenucci
 */
public abstract class AbstractStoredProcModule extends AbstractModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(AbstractStoredProcModule.class.getName());

	protected String className = StringUtils.getClassName(this);

	protected SourceBean request, response;
	protected SessionContainer session;

	public void service(SourceBean request, SourceBean response) {

		_logger.debug(className + ".service() INIZIO");

		initEnv(request, response);
		doOperationWithTransaction();
	}

	/**
	 * Inizializza le variabili di ambiente. Se la si deve ridefinire, non scordarsi di invocare con
	 * "super.initEnv(req,res)";
	 */
	protected void initEnv(SourceBean request, SourceBean response) {
		this.request = request;
		this.response = response;
		session = getRequestContainer().getSessionContainer();
	}

	/**
	 * Esegue l'operazione in un contesto transazionale. Costituisce la logica di esecuzione: NON può essere modificata
	 * (è 'privata').
	 */
	private void doOperationWithTransaction() {

		DataConnection connection = null;

		try {

			String pool = getPool();
			DataConnectionManager dcm = DataConnectionManager.getInstance();
			connection = dcm.getConnection(pool);

			connection.initTransaction();
			_logger.debug("doOperationWithTransaction() - INIZIO TRANSAZIONE");

			doOperation(connection);

			// COMMIT
			_logger.debug("doOperationWithTransaction() - FINE TRANSAZIONE con COMMIT");

			connection.commitTransaction();

		} catch (Exception ex) {

			// ROLLBACK
			it.eng.sil.util.TraceWrapper.debug(_logger,
					"doOperationWithTransaction() - FINE TRANSAZIONE con ROLLBACK " + "a causa dell'eccezione:", ex);

			try {
				connection.rollBackTransaction();
			} catch (EMFInternalError ignored) {
			}
		} finally {
			// Rilascio la connessione
			if (connection != null) {
				try {
					connection.close();
				} catch (EMFInternalError ignored) {
				}
			}
		}
	}

	/**
	 * Esegue l'operazione (la stored procedure) usando la connessione passata. Costituisce la logica di esecuzione: NON
	 * può essere modificata (è 'privata').
	 */
	private void doOperation(DataConnection connection) throws EMFInternalError {

		ReportOperationResult reportOperation = new ReportOperationResult(this, this.response);

		StoredProcedureCommand command = null;
		DataResult dr = null;
		String strResult = null;

		try {

			String statement = getStoredProcStatement();
			command = (StoredProcedureCommand) connection.createStoredProcedureCommand(statement);

			// imposto i parametri
			List parameters = getAndSetStoredProcParameters(connection, command);

			dr = command.execute(parameters); // puo' generare
												// EMFInternalError

			// VALORE DI RITORNO (result)
			PunctualDataResult pdr = (PunctualDataResult) dr.getDataObject();
			DataField df = pdr.getPunctualDatafield();
			strResult = (String) df.getStringValue();

			if (!strResult.equals("0")) {
				throw new EMFInternalError(EMFErrorSeverity.ERROR,
						"La StoredProcedure non ha reso 0 ma [" + strResult + "]");
			}

			// Tutto OK
			reportOperation.reportSuccess(getMessageCodesOK());

		} catch (EMFInternalError emfi) {
			reportOperation.reportFailure(getMessageCodesERR(strResult));
			throw emfi;
		} finally {
			// Chiudo tutto
			_logger.debug("Chiudo tutto");

			Utils.releaseResources(null, command, dr);
		}
	}

	// --------------------------------------------------------------------------------
	// DA QUI, I METODI DEFINIBILI PER SCPECIFICARE IL COMPORTAMENTO DELLA
	// SOTTOCLASSE!
	// --------------------------------------------------------------------------------

	/**
	 * Rende lo statement SQL con cui invocare la Stored Procedure In genere si scrive un codice come: return
	 * SQLStatements.getStatement("JELLY_STORED"); E nel file xml sarà censito uno statement di nome "JELLY_STORED" a
	 * cui per es. è associato un attributo: query="{ call ?:=PACKAGE_CATS.JellyStoredProcedure(?, ?, ?) }"
	 */
	protected abstract String getStoredProcStatement();

	/**
	 * Popola, imposta e rende la lista dei parametri da passare alla stored procedure. In genere si scrive un codice
	 * come: Object cdUtente = session.getAttribute("_CDUT_"); String codice = (String) request.getAttribute("codice");
	 * List parameters = new ArrayList(4); parameters.add(connection.createDataField("result", Types.BIGINT, null));
	 * command.setAsOutputParameters(0); parameters.add(connection.createDataField("ut", Types.BIGINT, codice));
	 * command.setAsInputParameters(1); parameters.add(connection.createDataField("codice", Types.VARCHAR, codice));
	 * return parameters;
	 */
	protected abstract List getAndSetStoredProcParameters(DataConnection connection, StoredProcedureCommand command);

	/**
	 * Rende il codice per il messaggio di operazione completata correttamente. In genere si scrive un codice come:
	 * return MessageCodes.Jellicle.JELLY_OK;
	 */
	protected abstract int getMessageCodesOK();

	/**
	 * Rende il codice per il messaggio di operazione fallita (+Riccardi+ [o altro tipo di errore]). In genere si scrive
	 * un codice come: return MessageCodes.Jellicle.JELLY_ERR;
	 */
	protected abstract int getMessageCodesERR(String strResult);

	/**
	 * Rende il pool. Se non la si ridefinisce si usa il valore in Values.DB_SIL_DATI.
	 */
	protected String getPool() {
		return Values.DB_SIL_DATI;
	}
}