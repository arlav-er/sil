package it.eng.sil.action.report.amministrazione;

import java.math.BigDecimal;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.DataConnectionManager;
import com.engiweb.framework.dbaccess.SQLStatements;
import com.engiweb.framework.dbaccess.Utils;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.SQLCommand;
import com.engiweb.framework.dbaccess.sql.command.std.StoredProcedureCommand;
import com.engiweb.framework.dbaccess.sql.result.DataResult;
import com.engiweb.framework.dbaccess.sql.result.ScrollableDataResult;
import com.engiweb.framework.error.EMFInternalError;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.afExt.utils.StringUtils;
import it.eng.sil.Values;
import it.eng.sil.action.report.AbstractSimpleReport;
import it.eng.sil.module.movimenti.DynRicercaMovimenti;
import oracle.jdbc.OracleConnection;
import oracle.jdbc.OraclePreparedStatement;
import oracle.sql.ARRAY;
import oracle.sql.ArrayDescriptor;

public class BackupMovimenti extends AbstractSimpleReport {

	private static final long serialVersionUID = 4700918424294983667L;
	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(BackupMovimenti.class.getName());
	private static final String thisClassName = StringUtils.getClassName(BackupMovimenti.class);

	// Connessione al DB del SIL
	private DataConnection connection;
	private String idSessione = null;
	private String prgMovimento = null;
	private String prgValidazioneMassiva = null;
	private Vector vettPrgMovimenti = new Vector();
	private OracleConnection oracleConn = null;

	public void service(SourceBean request, SourceBean response) {
		super.service(request, response);

		// recupero i parametri
		RequestContainer requestContainer = getRequestContainer();

		// idSessione =
		// requestContainer.getAttribute("HTTP_REQUEST_REQUESTED_SESSION_ID").toString();
		idSessione = getHttpRequest().getRequestedSessionId();

		String tipoStampa = (String) request.getAttribute("tipoStampa");
		try {
			prgMovimento = (String) request.getAttribute("prgMovimento");
			vettPrgMovimenti.add(prgMovimento);

			String apriFile = (String) request.getAttribute("apriFileBlob");
			if (apriFile != null && apriFile.equalsIgnoreCase("true")) {
				BigDecimal prgDoc = new BigDecimal((String) request.getAttribute("prgDocumento"));
				this.openDocument(request, response, prgDoc);
			} else {
				String tipoFile = (String) request.getAttribute("tipoFile");
				if (tipoFile != null) {
					setStrNomeDoc("BackupMovimenti." + tipoFile);
				} else {
					setStrNomeDoc("BackupMovimenti.pdf");
				}
				setStrDescrizione("Stampa movimenti da Backup");
				setReportPath("Amministrazione/BackupMovimenti.rpt");

				doEsportaMovimentiInTabAppoggio();

				// Passo l'id della sessione al report
				Vector params = null;
				params = new Vector(1);

				params.add(idSessione);

				setParams(params);

				String tipoDoc = (String) request.getAttribute("tipoDoc");
				if (tipoDoc != null)
					setCodTipoDocumento(tipoDoc);

				String salva = (String) request.getAttribute("salvaDB");
				String apri = (String) request.getAttribute("apri");
				if (salva != null && salva.equalsIgnoreCase("true"))
					insertDocument(request, response);
				else if (apri != null && apri.equalsIgnoreCase("true"))
					showDocument(request, response);
				doCancellaMovimentiAppoggio();
			} // else
		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.debug(_logger, this.getClass().getName() + "::service", e);

			setOperationFail(request, response, e);
		}
	}

	/**
	 * Esegue la cancellazione dei movimenti dalla tabella temporanea.
	 */
	private void doCancellaMovimentiAppoggio() throws Exception {

		StoredProcedureCommand command = null;

		try {
			openConnection();
			String statement = getStatement("CANCELLA_MOVIMENTI_IN_APPOGGIO");
			command = (StoredProcedureCommand) connection.createStoredProcedureCommand(statement);
			// imposto i parametri
			List parameters = new ArrayList(1);
			parameters.add(connection.createDataField("idSessione", Types.VARCHAR, idSessione));
			command.setAsInputParameters(0);
			command.execute(parameters); // puo' generare EMFInternalError
			// Tutto OK
		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.debug(_logger, this.getClass().getName() + "::service", e);

			throw e;
		} finally {
			// Chiudo tutto
			Utils.releaseResources(connection, command, null);
		}

	}

	/**
	 * Esegue la query di ricerca movimenti e mette i progressivi in vettPrgMovimenti.
	 */
	private void doSelMovimentiRicerca() throws Exception {

		SQLCommand cmdSelect = null;
		DataResult dr = null;
		SourceBean sb = null;

		try {
			openConnection();

			DynRicercaMovimenti ricMov = new DynRicercaMovimenti();
			String statement = ricMov.getStatement(getRequestContainer(), getConfig());
			cmdSelect = connection.createSelectCommand(statement);
			// eseguiamo la query
			List inputParameter = new ArrayList();
			dr = cmdSelect.execute(inputParameter);
			// crea la lista con il dataresult
			ScrollableDataResult sdr = null;
			sdr = (ScrollableDataResult) dr.getDataObject();
			sb = sdr.getSourceBean();
			List righe = sb.getAttributeAsVector("ROW");
			BigDecimal prgMov = null;
			for (int i = 0; i < righe.size(); i++) {
				prgMov = (BigDecimal) ((SourceBean) righe.get(i)).getAttribute("PRGMOV");
				vettPrgMovimenti.add(prgMov);
			}
		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.debug(_logger, this.getClass().getName() + "::service", e);

			throw e;
		}

		finally {
			// Chiudo tutto
			Utils.releaseResources(connection, cmdSelect, dr);
		}

	}

	/**
	 * Carica i movimenti nella tabella temporanea, utilizzando una stored procedure
	 */
	private void doEsportaMovimentiInTabAppoggio() throws Exception {

		writeLog("doEsportaMovimentiInTabAppoggio con vettPrgMovimenti=" + vettPrgMovimenti.toString());
		DataConnection dataConnection = null;

		try {

			DataConnectionManager dataConnectionManager = DataConnectionManager.getInstance();
			dataConnection = dataConnectionManager.getConnection(Values.DB_SIL_DATI);

			java.sql.Connection connection = dataConnection.getInternalConnection();
			oracle.jdbc.OracleConnection oracleConn = it.eng.sil.util.Utils.getOracleConnection(connection);
			
			ArrayDescriptor descriptor = ArrayDescriptor.createDescriptor("INT_ARRAY", oracleConn);

			Object prova[] = vettPrgMovimenti.toArray();

			ARRAY arrayPrgMovimenti = new ARRAY(descriptor, oracleConn, prova);

			OraclePreparedStatement ps = (OraclePreparedStatement) oracleConn
					.prepareStatement("begin PG_GESTAMM.SCRIVI_MOV_IN_APP(?, ?); end;");

			ps.setARRAY(1, arrayPrgMovimenti);
			ps.setString(2, idSessione);

			ps.execute();
		}

		catch (EMFInternalError emfi) {
			it.eng.sil.util.TraceWrapper.debug(_logger, this.getClass().getName() + "::service", (Exception) emfi);

			throw emfi;
		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.debug(_logger, this.getClass().getName() + "::service", e);

			throw e;
		}

		finally {
			// Chiudo tutto
			Utils.releaseResources(dataConnection, null, null);
		}

	}

	/**
	 * Rende la query SQL dello statement col nome passato.
	 */
	private String getStatement(String statementName) {

		return SQLStatements.getStatement(statementName);
	}

	private void writeLog(String str) {
		_logger.debug(str);

	}

	private void openConnection() throws EMFInternalError {

		String pool = Values.DB_SIL_DATI;

		DataConnectionManager dataConnectionManager = DataConnectionManager.getInstance();
		connection = dataConnectionManager.getConnection(pool);
	}

	protected SourceBean getSelectStatement(String queryName) {
		SourceBean beanQuery = null;
		beanQuery = (SourceBean) getConfig().getAttribute(queryName);

		return beanQuery;
	}

	public SourceBean doSelect(String queryName) throws Exception {
		SourceBean statement = getSelectStatement(queryName);

		SourceBean beanRows = null;
		beanRows = (SourceBean) QueryExecutor.executeQuery(getRequestContainer(), getResponseContainer(),
				Values.DB_SIL_DATI, statement, "SELECT");
		return beanRows;
	}

}// class
