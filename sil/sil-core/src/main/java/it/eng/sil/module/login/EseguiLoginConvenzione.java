package it.eng.sil.module.login;

import java.math.BigDecimal;
import java.sql.Types;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.dbaccess.DataConnectionManager;
import com.engiweb.framework.dbaccess.SQLStatements;
import com.engiweb.framework.dbaccess.Utils;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.DataField;
import com.engiweb.framework.dbaccess.sql.command.std.StoredProcedureCommand;
import com.engiweb.framework.dbaccess.sql.result.DataResult;
import com.engiweb.framework.dbaccess.sql.result.PunctualDataResult;
import com.engiweb.framework.dispatching.module.AbstractModule;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.sil.Values;
import it.eng.sil.security.Password;
import it.eng.sil.security.User;

public class EseguiLoginConvenzione extends AbstractModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(EseguiLogin.class.getName());
	private String className = this.getClass().getName();

	public EseguiLoginConvenzione() {
	}

	public void service(SourceBean request, SourceBean response) {
		RequestContainer requestContainer = getRequestContainer();
		SessionContainer sessionContainer = (SessionContainer) requestContainer.getSessionContainer();

		String pool = (String) getConfig().getAttribute("POOL");
		SourceBean statement = (SourceBean) getConfig().getAttribute("QUERIES.SELECT_QUERY");

		SourceBean rowsSourceBean = (SourceBean) QueryExecutor.executeQuery(getRequestContainer(),
				getResponseContainer(), pool, statement, "SELECT");

		String username = (String) rowsSourceBean.getAttribute("ROW.USERNAME");
		BigDecimal codUtente = (BigDecimal) rowsSourceBean.getAttribute("ROW.COD_UTENTE");
		String flgLogged = rowsSourceBean.getAttribute("ROW.FLGLOGGED") == null ? "N"
				: (String) rowsSourceBean.getAttribute("ROW.FLGLOGGED");

		SourceBean statementParam = (SourceBean) getConfig().getAttribute("QUERIES.QUERY_PARAM");
		SourceBean rowsSourceBeanParam = (SourceBean) QueryExecutor.executeQuery(getRequestContainer(),
				getResponseContainer(), pool, statementParam, "SELECT");

		BigDecimal numSecondiWait = (BigDecimal) rowsSourceBeanParam.getAttribute("ROW.NUMSECONDIWAIT");
		BigDecimal numTentativiMax = (BigDecimal) rowsSourceBeanParam.getAttribute("ROW.NUMMAXTENTATIVI");
		String flgLoginConvenzione = rowsSourceBeanParam.getAttribute("ROW.FLGLOGINCONVENZIONE") == null ? "N"
				: (String) rowsSourceBeanParam.getAttribute("ROW.FLGLOGINCONVENZIONE");

		try {
			response.setAttribute("FLG_LOGIN_CONVENZIONE", flgLoginConvenzione);
		} catch (SourceBeanException e) {
			it.eng.sil.util.TraceWrapper.debug(_logger, className + "::service()", e);
		}

		try {

			if (username == null) {
				response.setAttribute("ESITO_LOGIN", "KO");
				response.setAttribute("ESITO_LOGIN_MSG", "");

				String reqUsername = (String) request.getAttribute("USERNAME");
				response.setAttribute("REQ_USERNAME", reqUsername);

				return;
			}

			String passwordDB = (String) rowsSourceBean.getAttribute("ROW.PASSWORD");
			BigDecimal numTentativi = (BigDecimal) rowsSourceBean.getAttribute("ROW.NUMTENTATIVI");
			Date datUltimoAccesso = (Date) rowsSourceBean.getAttribute("ROW.DATULTIMOACCESSO");

			String password = (String) request.getAttribute("password");
			String hashPassword = new Password(password).getEncValue();

			boolean utenteBloccato = false;
			Date now = new Date();

			if (numTentativiMax.compareTo(numTentativi) == 0) {
				int secondsBetween = (int) ((now.getTime() - datUltimoAccesso.getTime()) / 1000);
				utenteBloccato = true;
			} else if ("S".equalsIgnoreCase(flgLogged)) {
				utenteBloccato = true;
			}

			if (!hashPassword.equals(passwordDB) || utenteBloccato) {

				// incremento il numero tentativi e verifico se è bloccato per TOT minuti
				String errorMessage = setNumeroTentativi(request, response, codUtente);

				response.setAttribute("ESITO_LOGIN", "KO");
				response.setAttribute("ESITO_LOGIN_MSG", errorMessage);

				String reqUsername = (String) request.getAttribute("USERNAME");
				response.setAttribute("REQ_USERNAME", reqUsername);
				return;
			}

			String nome = (String) rowsSourceBean.getAttribute("ROW.nome");
			String cognome = (String) rowsSourceBean.getAttribute("ROW.cognome");
			String oggi = (String) rowsSourceBean.getAttribute("ROW.oggi");
			String datinizioval = (String) rowsSourceBean.getAttribute("ROW.datinizioval");
			String datfineval = (String) rowsSourceBean.getAttribute("ROW.datfineval");
			String flgabilitato = (String) rowsSourceBean.getAttribute("ROW.flgabilitato");
			long nGiorniPwdScaduta = ((BigDecimal) rowsSourceBean.getAttribute("ROW.NUMGGPWDSCADUTA")).longValue();

			if ((flgabilitato != null) && flgabilitato.equalsIgnoreCase("N")) {
				response.setAttribute("ESITO_LOGIN", "BLOCCATO");
				return;

			}

			if (oggi.compareTo(datinizioval) < 0) {
				response.setAttribute("ESITO_LOGIN", "NON_ANCORA_VALIDO");
				return;

			}

			if (oggi.compareTo(datfineval) > 0) {
				response.setAttribute("ESITO_LOGIN", "SCADUTO");
				return;

			}

			// Le credenziali sono OK
			// A quesrto punto reata da controllare SOLO
			// la scadenza della password....

			// Cancellazione della
			// eventuale sessione precedente

			Set chiavi = sessionContainer.getVisibleAttributes().keySet();

			Iterator iter = chiavi.iterator();
			while (iter.hasNext()) {
				String key = (String) iter.next();
				iter.remove();

			}

			sessionContainer.setAttribute(Values.USERID, username);
			sessionContainer.setAttribute(Values.CODUTENTE, codUtente);
			sessionContainer.setAttribute("AF_LANGUAGE", "it");
			sessionContainer.setAttribute("AF_COUNTRY", "IT");
			sessionContainer.setAttribute("_ENCRYPTER_KEY_", System.getProperty("_ENCRYPTER_KEY_"));

			String strNumeroPratica = (String) request.getAttribute("NUMPRATICA");
			sessionContainer.setAttribute("NUMPRATICA", strNumeroPratica);

			// login OK resetto il numero di tentativi disponibili
			SourceBean statementReset = (SourceBean) getConfig().getAttribute("QUERIES.QUERY_RESET_NUMTENTATIVI");
			Boolean updatePerm = (Boolean) QueryExecutor.executeQuery(getRequestContainer(), getResponseContainer(),
					pool, statementReset, "UPDATE");

			// Scadenza della password
			if (nGiorniPwdScaduta > 0) {

				response.setAttribute("ESITO_LOGIN", "PWD_SCADUTA");
				return;
			}

			// Tutto OK!!
			response.setAttribute("ESITO_LOGIN", "OK");

			User user = new User(codUtente.intValue(), username, nome, cognome);
			sessionContainer.setAttribute(User.USERID, user);

		} catch (ConcurrentModificationException ex) {
			it.eng.sil.util.TraceWrapper.fatal(_logger,
					className + "::service() Errore di concorrenza nella rimozione di attributi dalla sessione", ex);

		} catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.debug(_logger, className + "::service()", ex);

		}
	}

	public String setNumeroTentativi(SourceBean serviceRequest, SourceBean serviceResponse, BigDecimal codUtente)
			throws Exception {

		String codiceRit = "";
		DataConnection conn = null;
		StoredProcedureCommand command = null;
		DataResult dr = null;
		try {
			// imposto la chiamata per la stored procedure
			String pool = (String) getConfig().getAttribute("POOL");
			DataConnectionManager dcm = DataConnectionManager.getInstance();
			conn = dcm.getConnection(pool);

			SourceBean statementSB = (SourceBean) getConfig().getAttribute("QUERIES.QUERY_NUMTENTATIVI");
			String statement = statementSB.getAttribute("statement").toString();
			String sqlStr = SQLStatements.getStatement(statement);
			command = (StoredProcedureCommand) conn.createStoredProcedureCommand(sqlStr);

			// imposto i parametri
			ArrayList parameters = new ArrayList(2);
			parameters.add(conn.createDataField("codiceRit", java.sql.Types.VARCHAR, null));
			command.setAsOutputParameters(0);
			parameters.add(conn.createDataField("p_CDNUTENTE", Types.BIGINT, codUtente));
			command.setAsInputParameters(1);

			// eseguo!!
			dr = command.execute(parameters);
			PunctualDataResult pdr = (PunctualDataResult) dr.getDataObject();
			// Reperisco i valori di output della stored e....
			DataField df = pdr.getPunctualDatafield();
			codiceRit = (String) df.getObjectValue();

		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.debug(_logger, "EseguiLoginConvenzione: ", (Exception) e);

		} finally {
			Utils.releaseResources(conn, command, null);
		}

		return codiceRit;
	}
}