package it.eng.sil.coop.webservices.myportal.servizicittadino.utils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.Utils;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.DataField;
import com.engiweb.framework.dbaccess.sql.mappers.OracleSQLMapper;
import com.engiweb.framework.error.EMFInternalError;
import com.engiweb.framework.util.QueryExecutorObject;

import it.eng.sil.Values;

public class QueryUtils {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(QueryUtils.class.getName());

	public static final String[] getDatiCittadino(String cdnLavoratore) throws Exception {

		if (cdnLavoratore == null) {
			throw new Exception("Nessun cdnLavoratore inserito");
		}

		String[] datiCittadino = null;

		String cognome = null, nome = null, codiceFiscale = null, email = null;

		QueryUtils queryUtils = new QueryUtils();
		QueryExecutorObject qExec = null;

		try {
			qExec = queryUtils.getQueryExecutorObject();
			List<DataField> inPars = new ArrayList<DataField>();
			qExec.setInputParameters(inPars);
			qExec.setType(QueryExecutorObject.SELECT);
			qExec.setStatement(
					"SELECT strcognome, strnome, strcodicefiscale, stremail FROM AN_LAVORATORE where cdnlavoratore = "
							+ cdnLavoratore);
			Object result = qExec.exec();
			DataConnection dataConnection = qExec.getDataConnection();
			if (dataConnection != null) {
				dataConnection.close();
			}

			if (result instanceof SourceBean) {
				SourceBean dati = (SourceBean) result;
				cognome = (String) dati.getAttribute("ROW.strcognome");
				nome = (String) dati.getAttribute("ROW.strnome");
				codiceFiscale = (String) dati.getAttribute("ROW.strcodicefiscale");
				email = (String) dati.getAttribute("ROW.stremail");
			}

			if (cognome == null || nome == null) {
				// campi obbligatori
				throw new Exception("La query non ritorna il nome e/o il cognome");
			}

			datiCittadino = new String[4];
			datiCittadino[0] = cognome;
			datiCittadino[1] = nome;
			datiCittadino[2] = codiceFiscale;
			datiCittadino[3] = email;

			return datiCittadino;
		} finally {
			if (qExec != null) {
				Utils.releaseResources(qExec.getDataConnection(), null, null);
			}
		}
	}

	public final QueryExecutorObject getQueryExecutorObject() {
		InitialContext ctx;
		try {
			ctx = new InitialContext();
			return getQueryExecutorObject(ctx);
		} catch (Exception e) {
			_logger.error("Errore getQueryExecutorObject", e);
			return null;
		}
	}

	private final QueryExecutorObject getQueryExecutorObject(InitialContext ctx)
			throws NamingException, SQLException, EMFInternalError {

		Object objs = ctx.lookup(Values.JDBC_JNDI_NAME);
		DataConnection dc = null;
		QueryExecutorObject qExec;
		if (objs instanceof DataSource) {
			DataSource ds = (DataSource) objs;
			Connection conn = ds.getConnection();
			dc = new DataConnection(conn, "2", new OracleSQLMapper());
			qExec = getQueryExecutorObject(dc);
		} else {
			throw new SQLException();
		}
		return qExec;

	}

	private static QueryExecutorObject getQueryExecutorObject(DataConnection dc) {

		QueryExecutorObject qExec = new QueryExecutorObject();

		qExec.setRequestContainer(null);
		qExec.setResponseContainer(null);
		qExec.setDataConnection(dc);
		qExec.setType(QueryExecutorObject.SELECT);
		qExec.setTransactional(true);
		qExec.setDontForgetException(false);

		return qExec;

	}

}
