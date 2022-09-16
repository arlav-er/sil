/*
 * Creato il 28-set-06
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.module.amministrazione;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Vector;

import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.dbaccess.SQLStatements;
import com.engiweb.framework.dbaccess.Utils;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.command.std.StoredProcedureCommand;
import com.engiweb.framework.dbaccess.sql.result.DataResult;
import com.engiweb.framework.error.EMFInternalError;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.security.User;
import it.eng.sil.util.blen.StringUtils;
import oracle.jdbc.OracleTypes;

// @author: Giordano Gritti

public class ASPunteggioISEE extends AbstractSimpleModule {
	public ASPunteggioISEE() {
	}

	private String className = this.getClass().getName();

	public void service(SourceBean serviceRequest, SourceBean serviceResponse)
			throws SourceBeanException, EMFInternalError {
		DataConnection conn = null;
		DataResult dr = null;
		StoredProcedureCommand command = null;
		ReportOperationResult ror = new ReportOperationResult(this, serviceResponse);
		this.disableMessageIdSuccess();
		String err_msg = "";
		int codiceMessaggio = 0;
		TransactionQueryExecutor transExec = null;
		SourceBean statementSB = null;
		String statement = "";
		String sqlStr = "";
		ArrayList parameters = null;

		this.setSectionQuerySelect("QUERY_LISTA");
		SourceBean lista = doSelect(serviceRequest, serviceResponse);
		Vector vett = lista.getAttributeAsVector("ROW");

		try {
			// utente connesso
			SessionContainer sessionContainer = getRequestContainer().getSessionContainer();
			User user = (User) sessionContainer.getAttribute(User.USERID);
			String utente = Integer.toString(user.getCodut(), 10);

			// recupero i parametri dalla request
			String valoreISEE = (String) serviceRequest.getAttribute("numvaloreisee");
			String tipoGraduatoria = (String) serviceRequest.getAttribute("graduatoria");
			String cdnlavoratore = (String) serviceRequest.getAttribute("cdnLavoratore");
			String inizio = (String) serviceRequest.getAttribute("inizio");
			String fine = (String) serviceRequest.getAttribute("fine");
			String numanno = (String) serviceRequest.getAttribute("numanno");
			String strnote = (String) serviceRequest.getAttribute("strnota");
			String strIbanNazione = (String) serviceRequest.getAttribute("strIbanNazione");
			String strIbanControllo = (String) serviceRequest.getAttribute("strIbanControllo");
			String strCinLav = (String) serviceRequest.getAttribute("strCinLav");
			String strAbiLav = (String) serviceRequest.getAttribute("strAbiLav");
			String strCabLav = (String) serviceRequest.getAttribute("strCabLav");
			String strCCLav = (String) serviceRequest.getAttribute("strCCLav");

			// Controllo dati 1
			String dataOdierna = DateUtils.getNow();
			if (DateUtils.compare(inizio, dataOdierna) > 0) {
				err_msg = "Errore la data inizio validità non può essere futura";
				codiceMessaggio = MessageCodes.ISEE.ERR_INIZIO_VALIDITA_FUTURA;
				throw new Exception(err_msg);
			} else if (fine != null && !fine.trim().equals("") && DateUtils.compare(fine, dataOdierna) > 0) {
				err_msg = "Errore la data fine validità non può essere futura";
				codiceMessaggio = MessageCodes.ISEE.ERR_FINE_VALIDITA_FUTURA;
				throw new Exception(err_msg);
			} else if (fine != null && !fine.trim().equals("") && DateUtils.compare(fine, inizio) < 0) {
				err_msg = "Errore la data inizio validità deve essere minore o uguale alla data di fine validità";
				codiceMessaggio = MessageCodes.ISEE.ERR_INZIO_FINE_NON_CONGRUENTI;
				throw new Exception(err_msg);
			}

			// Controllo dati 2 sugli intervalli con nuovo isee con data fine validita valorizzata
			if (fine != null && !fine.trim().equals("") && vett != null && vett.size() > 0) {
				for (int i = 0; i < vett.size(); i++) {
					SourceBean sbValIsee = (SourceBean) vett.get(i);
					String tmpDATFINEVAL = StringUtils.getAttributeStrNotNull(sbValIsee, "strDATFINEVAL");
					String tmpDATINIZIOVAL = StringUtils.getAttributeStrNotNull(sbValIsee, "strDATINIZIOVAL");

					if (tmpDATFINEVAL != null && !tmpDATFINEVAL.equals("")) {
						if ((DateUtils.compare(inizio, tmpDATINIZIOVAL) >= 0
								&& DateUtils.compare(inizio, tmpDATFINEVAL) <= 0)
								|| (DateUtils.compare(fine, tmpDATINIZIOVAL) >= 0
										&& DateUtils.compare(fine, tmpDATFINEVAL) <= 0)) {
							err_msg = "Peiodi sovrapposti";
							codiceMessaggio = MessageCodes.ISEE.ERR_INS_PERIODI_SOVRAPPOSTI;
							throw new Exception(err_msg);
						}
						if (DateUtils.compare(inizio, tmpDATINIZIOVAL) < 0
								&& DateUtils.compare(fine, tmpDATINIZIOVAL) >= 0) {
							err_msg = "Peiodi sovrapposti";
							codiceMessaggio = MessageCodes.ISEE.ERR_INS_PERIODI_SOVRAPPOSTI;
							throw new Exception(err_msg);
						}
					} else {
						if (DateUtils.compare(inizio, tmpDATINIZIOVAL) <= 0
								&& DateUtils.compare(fine, tmpDATINIZIOVAL) >= 0) {
							err_msg = "Peiodi sovrapposti";
							codiceMessaggio = MessageCodes.ISEE.ERR_INS_PERIODI_SOVRAPPOSTI;
							throw new Exception(err_msg);
						}
					}
				}
			} else {
				for (int i = 0; i < vett.size(); i++) {
					SourceBean sbValIsee = (SourceBean) vett.get(i);
					String tmpDATINIZIOVAL = StringUtils.getAttributeStrNotNull(sbValIsee, "strDATINIZIOVAL");
					if (DateUtils.compare(inizio, tmpDATINIZIOVAL) <= 0) {
						err_msg = "Peiodi sovrapposti";
						codiceMessaggio = MessageCodes.ISEE.ERR_INS_PERIODI_SOVRAPPOSTI;
						throw new Exception(err_msg);
					}
				}
			}

			transExec = new TransactionQueryExecutor(getPool());
			enableTransactions(transExec);

			transExec.initTransaction();

			conn = transExec.getDataConnection();

			statementSB = (SourceBean) getConfig().getAttribute("QUERY_CALCOLA");
			statement = statementSB.getAttribute("STATEMENT").toString();
			sqlStr = SQLStatements.getStatement(statement);
			command = (StoredProcedureCommand) conn.createStoredProcedureCommand(sqlStr);

			parameters = new ArrayList();
			// Parametri di input : valoreISEE
			parameters.add(conn.createDataField("valoreisee", OracleTypes.NUMBER, valoreISEE));
			command.setAsInputParameters(0);
			// Parametri di input : graduatoria
			parameters.add(conn.createDataField("grad", OracleTypes.VARCHAR, tipoGraduatoria));
			command.setAsInputParameters(1);
			// Parametri di input : cdn lavoratore
			parameters.add(conn.createDataField("cdnlav", OracleTypes.VARCHAR, cdnlavoratore));
			command.setAsInputParameters(2);
			// Parametri di input : inizio validità
			parameters.add(conn.createDataField("inizio", OracleTypes.VARCHAR, inizio));
			command.setAsInputParameters(3);
			// Parametri di input : fine validità
			parameters.add(conn.createDataField("fine", OracleTypes.VARCHAR, fine));
			command.setAsInputParameters(4);
			// Parametri di input : anno di riferimento
			parameters.add(conn.createDataField("anno", OracleTypes.VARCHAR, numanno));
			command.setAsInputParameters(5);
			// Parametri di input : note
			parameters.add(conn.createDataField("note", OracleTypes.VARCHAR, strnote));
			command.setAsInputParameters(6);
			// Parametri di input : utente di inserimento
			parameters.add(conn.createDataField("utins", OracleTypes.VARCHAR, utente));
			command.setAsInputParameters(7);
			// Parametri di input : utente di modifica
			parameters.add(conn.createDataField("utmod", OracleTypes.VARCHAR, utente));
			command.setAsInputParameters(8);

			// Parametri di input : IBAN
			parameters.add(conn.createDataField("ibanNazione", OracleTypes.VARCHAR, strIbanNazione));
			command.setAsInputParameters(9);
			parameters.add(conn.createDataField("ibanControllo", OracleTypes.VARCHAR, strIbanControllo));
			command.setAsInputParameters(10);
			parameters.add(conn.createDataField("cinLav", OracleTypes.VARCHAR, strCinLav));
			command.setAsInputParameters(11);
			parameters.add(conn.createDataField("abiLav", OracleTypes.VARCHAR, strAbiLav));
			command.setAsInputParameters(12);
			parameters.add(conn.createDataField("cabLav", OracleTypes.VARCHAR, strCabLav));
			command.setAsInputParameters(13);
			parameters.add(conn.createDataField("ccLav", OracleTypes.VARCHAR, strCCLav));
			command.setAsInputParameters(14);

			// Chiamata alla Stored Procedure
			dr = command.execute(parameters);

			transExec.commitTransaction();
			ror.reportSuccess(MessageCodes.General.OPERATION_SUCCESS);
		} catch (EMFInternalError e) {
			if (transExec != null) {
				transExec.rollBackTransaction();
			}
			Throwable errOrigine = ((EMFInternalError) e).getNativeException();
			// il codice di errore che la procedura di controllo di intersezione
			// date lancia e' -20012 (vedi pg_gestamm.chekIntersezioneDate
			if (errOrigine instanceof SQLException && ((SQLException) errOrigine).getErrorCode() == 20016) {
				codiceMessaggio = MessageCodes.General.ERR_INTERSEZIONE_DATA_REC_ESISTENTE;
			}
			if (codiceMessaggio == 0) {
				codiceMessaggio = MessageCodes.General.INSERT_FAIL;
			}
			ror.reportFailure(codiceMessaggio);
		} catch (Exception e1) {
			if (transExec != null) {
				transExec.rollBackTransaction();
			}
			if (codiceMessaggio == 0) {
				codiceMessaggio = MessageCodes.General.INSERT_FAIL;
			}
			ror.reportFailure(codiceMessaggio);
		} finally {
			Utils.releaseResources(conn, command, dr);
		}
	}
}