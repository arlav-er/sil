/*
 * Created on 10-nov-06
 *
 */
package it.eng.sil.module.profil;

import java.math.BigDecimal;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.DataConnectionManager;
import com.engiweb.framework.dbaccess.Utils;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dispatching.module.AbstractModule;
import com.engiweb.framework.error.EMFInternalError;
import com.engiweb.framework.error.EMFUserError;
import com.engiweb.framework.util.QueryExecutorObjectExt;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.sil.Values;

/**
 * @author vuoto
 * 
 */

public class ProfMonitoraggioSalva extends AbstractModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(ProfMonitoraggioSalva.class.getName());
	private String className = this.getClass().getName();

	public void service(SourceBean request, SourceBean response) {
		_logger.debug(className + "::service()");

		/*
		 * 1) se l'utente è gia' censito, si inseriscono solo i profili in exo-membership
		 * 
		 * 2) altrimenti si inserisce l'utente, i profili e l'associazione
		 * 
		 */

		String strlogin = (String) request.getAttribute("strlogin");

		ReportOperationResult result = new ReportOperationResult(this, response);
		DataConnection connDWH = null;

		try {

			connDWH = DataConnectionManager.getInstance().getConnection(Values.DB_SIL_DWH);
			connDWH.initTransaction();

			boolean utenteGiaCensito = isUtenteCensito(connDWH, strlogin);

			// if (utenteGiaCensito) {
			// L'utente e' gia' presente
			// bisogna cambiare i soli gruppi

			if (!utenteGiaCensito) {

				SourceBean sb2 = estraiDatiUtenteFromSIL(connDWH, strlogin);

				String password = (String) sb2.getAttribute("row.strpassword");
				String cognome = (String) sb2.getAttribute("row.STRCOGNOME");
				String nome = (String) sb2.getAttribute("row.strnome");
				String email = (String) sb2.getAttribute("row.stremail");

				inserisciUtente(connDWH, strlogin, password, nome, cognome, email);
				inserisciProfiloUtente(connDWH, strlogin);

			}

			// in tutti gli altri casi bisogna aggiornare i gruppi

			// Cancellazione dei gruppi gia' associati
			eraseGruppiUtente(connDWH, strlogin);

			// if (!utenteGiaCensito) {
			String seq = estraiSeqGruppi(connDWH);
			inserisciGruppoUtente(connDWH, strlogin, "/user", seq);
			seq = estraiSeqGruppi(connDWH);
			inserisciGruppoUtente(connDWH, strlogin, "/spagobi", seq);
			// }

			Vector gruppi = request.getAttributeAsVector("gruppi");
			Enumeration _enum = gruppi.elements();
			while (_enum.hasMoreElements()) {

				// recupero gruppo
				String gruppo = (String) _enum.nextElement();
				String seq1 = estraiSeqGruppi(connDWH);

				inserisciGruppoUtente(connDWH, strlogin, gruppo, seq1);

			}

			connDWH.commitTransaction();
			result.reportSuccess(MessageCodes.General.OPERATION_SUCCESS);

		} catch (Exception esitoEx) {

			/*******************************************************************
			 * ERRORE
			 ******************************************************************/

			// altri errori
			result.reportFailure(MessageCodes.General.OPERATION_FAIL);

			if (connDWH != null)
				try {
					connDWH.rollBackTransaction();
				} catch (EMFInternalError e) {
					it.eng.sil.util.TraceWrapper.debug(_logger, className, (Exception) e);

				}

		} finally {
			Utils.releaseResources(connDWH, null, null);

		}

		_logger.debug(className + "::service()");

	}

	private void inserisciGruppoUtente(DataConnection connDWH, String strlogin, String gruppo, String seq)
			throws EMFInternalError, EMFUserError {

		QueryExecutorObjectExt qDWH = new QueryExecutorObjectExt();
		qDWH.setDataConnection(connDWH);
		qDWH.setTransactional(true);

		qDWH.setStatement("insert into exo_membership  (id, membershiptype, username, groupid)"
				+ "values  ( ? , 'member', ? , ?)");
		qDWH.setType(QueryExecutorObjectExt.INSERT);

		List inputParameters = new ArrayList();
		inputParameters.add(connDWH.createDataField("seq", Types.VARCHAR, seq));
		inputParameters.add(connDWH.createDataField("login", Types.VARCHAR, strlogin));
		inputParameters.add(connDWH.createDataField("gruppo", Types.VARCHAR, gruppo));

		qDWH.setInputParameters(inputParameters);

		qDWH.execExt();

	}

	private String estraiSeqGruppi(DataConnection connDWH) throws EMFInternalError, EMFUserError {
		String seq;
		// 1. recupero sequence
		QueryExecutorObjectExt qDWH = new QueryExecutorObjectExt();
		qDWH.setDataConnection(connDWH);
		qDWH.setTransactional(true);

		qDWH.setStatement("SELECT s_EXO_MEMBERSHIP.nextval seq from dual");
		qDWH.setType(QueryExecutorObjectExt.SELECT);

		SourceBean sb1 = (SourceBean) qDWH.execExt();

		seq = "" + ((BigDecimal) sb1.getAttribute("ROW.SEQ"));
		return seq;
	}

	private void eraseGruppiUtente(DataConnection connDWH, String strlogin) throws EMFInternalError, EMFUserError {

		QueryExecutorObjectExt qDWH = new QueryExecutorObjectExt();
		qDWH.setDataConnection(connDWH);
		qDWH.setTransactional(true);

		qDWH.setStatement("delete FROM EXO_MEMBERSHIP t where t.USERNAME = ?");
		qDWH.setType(QueryExecutorObjectExt.DELETE);

		List inputParameters = new ArrayList();
		inputParameters.add(connDWH.createDataField("login", Types.VARCHAR, strlogin));

		qDWH.setInputParameters(inputParameters);

		qDWH.execExt();

	}

	private void inserisciUtente(DataConnection connDWH, String strlogin, String password, String firstname,
			String lastname, String email) throws EMFInternalError, EMFUserError {

		QueryExecutorObjectExt qDWH = new QueryExecutorObjectExt();
		qDWH.setDataConnection(connDWH);
		qDWH.setTransactional(true);

		qDWH.setStatement(
				"insert into exo_user(id, username, password, firstname, lastname, email, createddate, lastlogintime)"
						+ " values " + "(?, ?, ?, ?, ?, ?, sysdate, null)");

		qDWH.setType(QueryExecutorObjectExt.INSERT);

		List inputParameters = new ArrayList();
		inputParameters.add(connDWH.createDataField("id", Types.VARCHAR, strlogin));
		inputParameters.add(connDWH.createDataField("username", Types.VARCHAR, strlogin));

		inputParameters.add(connDWH.createDataField("password", Types.VARCHAR, password));
		inputParameters.add(connDWH.createDataField("firstname", Types.VARCHAR, firstname));
		inputParameters.add(connDWH.createDataField("lastname", Types.VARCHAR, lastname));
		inputParameters.add(connDWH.createDataField("email", Types.VARCHAR, email));

		qDWH.setInputParameters(inputParameters);

		qDWH.execExt();

	}

	private void inserisciProfiloUtente(DataConnection connDWH, String strlogin) throws EMFInternalError, EMFUserError {

		QueryExecutorObjectExt qDWH = new QueryExecutorObjectExt();
		qDWH.setDataConnection(connDWH);
		qDWH.setTransactional(true);

		qDWH.setStatement("insert into exo_user_profile( username, profile) values (?, ?)");

		qDWH.setType(QueryExecutorObjectExt.INSERT);

		List inputParameters = new ArrayList();
		inputParameters.add(connDWH.createDataField("id", Types.VARCHAR, strlogin));
		String prof = "<user-profile><userName>" + strlogin + "</userName></user-profile>";
		inputParameters.add(connDWH.createDataField("username", Types.VARCHAR, prof));

		qDWH.setInputParameters(inputParameters);

		qDWH.execExt();

	}

	private boolean isUtenteCensito(DataConnection connDWH, String strlogin) throws EMFInternalError, EMFUserError {

		QueryExecutorObjectExt qDWH = new QueryExecutorObjectExt();
		qDWH.setDataConnection(connDWH);
		qDWH.setTransactional(true);

		qDWH.setStatement("SELECT t.ID IDUSER	FROM EXO_USER t	where t.ID =?");
		qDWH.setType(QueryExecutorObjectExt.SELECT);

		List inputParameters = new ArrayList();
		inputParameters.add(connDWH.createDataField("id", Types.VARCHAR, strlogin));
		qDWH.setInputParameters(inputParameters);

		SourceBean sb1 = (SourceBean) qDWH.execExt();

		String idUser = (String) sb1.getAttribute("row.idUser");
		return (idUser == null) ? false : true;
	}

	private SourceBean estraiDatiUtenteFromSIL(DataConnection connDWH, String strlogin)
			throws EMFInternalError, EMFUserError {

		DataConnection connSIL = DataConnectionManager.getInstance().getConnection(Values.DB_SIL_DATI);
		QueryExecutorObjectExt qSIL = new QueryExecutorObjectExt();
		qSIL.setDataConnection(connSIL);
		qSIL.setTransactional(false);

		qSIL.setStatement("SELECT STRPASSWORD, STRCOGNOME, STRNOME, STREMAIL FROM TS_UTENTE T where t.STRLOGIN =?");
		qSIL.setType(QueryExecutorObjectExt.SELECT);

		List inputParameters = new ArrayList();
		inputParameters.add(connDWH.createDataField("id", Types.VARCHAR, strlogin));
		qSIL.setInputParameters(inputParameters);

		SourceBean sb1 = (SourceBean) qSIL.execExt();

		return sb1;
	}

}