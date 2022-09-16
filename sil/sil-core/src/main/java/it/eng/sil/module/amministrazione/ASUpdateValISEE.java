/*
 * Creato il 28-set-06
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.module.amministrazione;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Vector;

import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.SQLStatements;
import com.engiweb.framework.dbaccess.Utils;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.SQLCommand;
import com.engiweb.framework.dbaccess.sql.command.std.StoredProcedureCommand;
import com.engiweb.framework.dbaccess.sql.result.DataResult;
import com.engiweb.framework.dbaccess.sql.result.ScrollableDataResult;
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

public class ASUpdateValISEE extends AbstractSimpleModule {
	public ASUpdateValISEE() {
	}

	private String className = this.getClass().getName();

	public void service(SourceBean serviceRequest, SourceBean serviceResponse) throws EMFInternalError {
		ArrayList retList = null;
		DataConnection conn = null;
		DataResult dr = null;
		StoredProcedureCommand command = null;
		ReportOperationResult ror = new ReportOperationResult(this, serviceResponse);
		this.disableMessageIdSuccess();
		this.disableMessageIdFail();
		String err_msg = "";
		TransactionQueryExecutor transExec = null;
		int codiceMessaggio = 0;

		try {
			// utente connesso
			SessionContainer sessionContainer = getRequestContainer().getSessionContainer();
			User user = (User) sessionContainer.getAttribute(User.USERID);
			String utente = Integer.toString(user.getCodut(), 10);

			SourceBean statementSB = null;
			String statement = "";
			String sqlStr = "";
			int paramIndex = 0;
			ArrayList parameters = null;
			String query_select = "";
			boolean grigliaOk = false;
			SQLCommand stmt = null;
			DataResult res = null;
			ScrollableDataResult sdr = null;
			SourceBean rowsSourceBean = null;

			// recupero i parametri dalla request
			String cdnLavoratore = (String) serviceRequest.getAttribute("cdnLavoratore");
			String valoreISEE = (String) serviceRequest.getAttribute("numvaloreisee");
			String numanno = (String) serviceRequest.getAttribute("numanno");
			String strnota = (String) serviceRequest.getAttribute("strnota");
			String prgValoreIsee = (String) serviceRequest.getAttribute("PRGVALOREISEE");
			String tipoGraduatoria = (String) serviceRequest.getAttribute("graduatoria");
			String numKloValoreIsee = (String) serviceRequest.getAttribute("NUMKLOVALOREISEE");
			String strIbanNazione = (String) serviceRequest.getAttribute("strIbanNazione");
			String strIbanControllo = (String) serviceRequest.getAttribute("strIbanControllo");
			String strCinLav = (String) serviceRequest.getAttribute("strCinLav");
			String strAbiLav = (String) serviceRequest.getAttribute("strAbiLav");
			String strCabLav = (String) serviceRequest.getAttribute("strCabLav");
			String strCCLav = (String) serviceRequest.getAttribute("strCCLav");
			String inizio = (String) serviceRequest.getAttribute("inizio");
			String fine = (String) serviceRequest.getAttribute("fine");
			String codmonomotivomodifica = (String) serviceRequest.getAttribute("codmonomotivomodifica");
			String storicizza = StringUtils.getAttributeStrNotNull(serviceRequest, "STORICIZZAZIONE_ISEE");

			String skipcontroldatafine = (String) serviceRequest.getAttribute("SKIP_CONTROL_DATA_FINE");
			String esisteDatafinevaliditaVuota = (String) serviceRequest.getAttribute("ESISTE_DATAFINEVALIDITA_VUOTA");
			String esisteRecordDTFinePostNuovoInizio = (String) serviceRequest
					.getAttribute("ESISTE_RECORD_DTFINE_POST_NUOVOINIZIO");
			String do_update_2_dtfine_prgvaloreisee = (String) serviceRequest
					.getAttribute("DO_UPDATE_2_DTFINE_PRGVALOREISEE");

			if (skipcontroldatafine != null && !skipcontroldatafine.equals("TRUE")) {
				BigDecimal prgValoreIseeBD = new BigDecimal(prgValoreIsee);

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

				this.setSectionQuerySelect("QUERY_LISTA");
				SourceBean lista = doSelect(serviceRequest, serviceResponse);
				Vector vett = lista.getAttributeAsVector("ROW");

				// Controllo dati 2 sugli intervalli con nuovo isee con data fine validita valorizzata
				if (fine != null && !fine.trim().equals("") && vett != null && vett.size() > 0) {
					for (int i = 0; i < vett.size(); i++) {
						SourceBean sbValIsee = (SourceBean) vett.get(i);
						String tmpDATFINEVAL = StringUtils.getAttributeStrNotNull(sbValIsee, "strDATFINEVAL");
						String tmpDATINIZIOVAL = StringUtils.getAttributeStrNotNull(sbValIsee, "strDATINIZIOVAL");
						BigDecimal tmpPRGVALOREISEE = (BigDecimal) sbValIsee.getAttribute("PRGVALOREISEE");

						if (tmpPRGVALOREISEE.compareTo(prgValoreIseeBD) != 0) {
							if (tmpDATFINEVAL != null && !tmpDATFINEVAL.equals("")) {
								if ((DateUtils.compare(inizio, tmpDATINIZIOVAL) >= 0
										&& DateUtils.compare(inizio, tmpDATFINEVAL) <= 0)
										|| (DateUtils.compare(fine, tmpDATINIZIOVAL) >= 0
												&& DateUtils.compare(fine, tmpDATFINEVAL) <= 0)) {
									err_msg = "Peiodi sovrapposti";
									codiceMessaggio = MessageCodes.ISEE.ERR_AGG_PERIODI_SOVRAPPOSTI;
									throw new Exception(err_msg);
								}
								if (DateUtils.compare(inizio, tmpDATINIZIOVAL) <= 0
										&& DateUtils.compare(fine, tmpDATINIZIOVAL) >= 0) {
									err_msg = "Peiodi sovrapposti";
									codiceMessaggio = MessageCodes.ISEE.ERR_AGG_PERIODI_SOVRAPPOSTI;
									throw new Exception(err_msg);
								}
							} else {
								if (DateUtils.compare(inizio, tmpDATINIZIOVAL) <= 0
										&& DateUtils.compare(fine, tmpDATINIZIOVAL) >= 0) {
									err_msg = "Peiodi sovrapposti";
									codiceMessaggio = MessageCodes.ISEE.ERR_AGG_PERIODI_SOVRAPPOSTI;
									throw new Exception(err_msg);
								}
							}
						}
					}
				}

				// Controllo dati 3 sugli intervalli con nuovo isee con data fine validita non valorizzata
				if ((fine == null || fine.trim().equals("")) && vett != null && vett.size() > 0) {
					for (int i = 0; i < vett.size(); i++) {
						SourceBean sbValIsee = (SourceBean) vett.get(i);
						BigDecimal tmpPRGVALOREISEE = (BigDecimal) sbValIsee.getAttribute("PRGVALOREISEE");
						String tmpDATINIZIOVAL = StringUtils.getAttributeStrNotNull(sbValIsee, "strDATINIZIOVAL");
						// se sto eliminando la data fine di un isee allora posso eliminare solo se non esiste un isee
						// che inizia prima
						if (tmpPRGVALOREISEE.compareTo(prgValoreIseeBD) != 0) {
							if (DateUtils.compare(inizio, tmpDATINIZIOVAL) <= 0) {
								err_msg = "Peiodi sovrapposti";
								codiceMessaggio = MessageCodes.ISEE.ERR_AGG_PERIODI_SOVRAPPOSTI;
								throw new Exception(err_msg);
							}
						}
					}

					// Data fine non valorizzata/cancellata
					// 1. se esiste un record per cui la data fine è successiva alla data inizio del nuovo valore ISEE
					int numRecordTrovati = 0;
					BigDecimal prgValIseeDaAggiornare = null;
					for (int i = 0; i < vett.size(); i++) {
						SourceBean sbValIsee = (SourceBean) vett.get(i);
						String tmpDtfine = StringUtils.getAttributeStrNotNull(sbValIsee, "strDATFINEVAL");
						BigDecimal tmpPRGVALOREISEE = (BigDecimal) sbValIsee.getAttribute("PRGVALOREISEE");
						if (tmpPRGVALOREISEE.compareTo(prgValoreIseeBD) != 0) {
							if (tmpDtfine != null && !"".equals(tmpDtfine)
									&& DateUtils.compare(tmpDtfine, inizio) >= 0) {
								numRecordTrovati++;
								prgValIseeDaAggiornare = tmpPRGVALOREISEE;
							}
						}
					}
					if (numRecordTrovati > 1) {
						err_msg = "Peiodi sovrapposti";
						codiceMessaggio = MessageCodes.ISEE.ERR_AGG_PERIODI_SOVRAPPOSTI;
						throw new Exception(err_msg);
					} else {
						if (numRecordTrovati == 1) {
							serviceResponse.setAttribute("DO_UPDATE_2_DTFINE_PRGVALOREISEE", prgValIseeDaAggiornare);
							serviceResponse.setAttribute("ESISTE_RECORD_DTFINE_POST_NUOVOINIZIO", "TRUE");
							return;// chiedi nuova conferma
						}
					}
				}
			}

			transExec = new TransactionQueryExecutor(getPool());
			enableTransactions(transExec);

			transExec.initTransaction();

			conn = transExec.getDataConnection();

			statementSB = (SourceBean) getConfig().getAttribute("QUERY_UPDATE");
			statement = statementSB.getAttribute("STATEMENT").toString();
			sqlStr = SQLStatements.getStatement(statement);
			command = (StoredProcedureCommand) conn.createStoredProcedureCommand(sqlStr);

			parameters = new ArrayList();

			// Parametri di input : cdn lavoratore
			parameters.add(conn.createDataField("cdnlav", OracleTypes.VARCHAR, cdnLavoratore));
			command.setAsInputParameters(0);
			// Parametri di input : valoreISEE
			parameters.add(conn.createDataField("valoreisee", OracleTypes.NUMBER, valoreISEE));
			command.setAsInputParameters(1);
			// Parametri di input : inizio
			parameters.add(conn.createDataField("inizio", OracleTypes.VARCHAR, inizio));
			command.setAsInputParameters(2);
			// Parametri di input : fine
			parameters.add(conn.createDataField("fine", OracleTypes.VARCHAR, fine));
			command.setAsInputParameters(3);
			// Parametri di input : anno di riferimento
			parameters.add(conn.createDataField("anno", OracleTypes.VARCHAR, numanno));
			command.setAsInputParameters(4);
			// Parametri di input : note
			parameters.add(conn.createDataField("note", OracleTypes.VARCHAR, strnota));
			command.setAsInputParameters(5);
			// Parametri di input : codmonomotivomodifica
			parameters.add(conn.createDataField("codmonomotivomodifica", OracleTypes.VARCHAR, codmonomotivomodifica));
			command.setAsInputParameters(6);
			// Parametri di input : utente di inserimento
			parameters.add(conn.createDataField("utins", OracleTypes.VARCHAR, utente));
			command.setAsInputParameters(7);
			// Parametri di input : utente di modifica
			parameters.add(conn.createDataField("utmod", OracleTypes.VARCHAR, utente));
			command.setAsInputParameters(8);
			// Parametri di input : utente di modifica
			parameters.add(conn.createDataField("prgValIsee", OracleTypes.NUMBER, prgValoreIsee));
			command.setAsInputParameters(9);
			// Parametri di input : graduatoria
			parameters.add(conn.createDataField("grad", OracleTypes.VARCHAR, tipoGraduatoria));
			command.setAsInputParameters(10);
			// Parametri di input : controllo di lock
			parameters.add(conn.createDataField("numKloValoreIsee", OracleTypes.NUMBER, numKloValoreIsee));
			command.setAsInputParameters(11);

			// Parametri di input : IBAN
			parameters.add(conn.createDataField("ibanNazione", OracleTypes.VARCHAR, strIbanNazione));
			command.setAsInputParameters(12);
			parameters.add(conn.createDataField("ibanControllo", OracleTypes.VARCHAR, strIbanControllo));
			command.setAsInputParameters(13);
			parameters.add(conn.createDataField("cinLav", OracleTypes.VARCHAR, strCinLav));
			command.setAsInputParameters(14);
			parameters.add(conn.createDataField("abiLav", OracleTypes.VARCHAR, strAbiLav));
			command.setAsInputParameters(15);
			parameters.add(conn.createDataField("cabLav", OracleTypes.VARCHAR, strCabLav));
			command.setAsInputParameters(16);
			parameters.add(conn.createDataField("ccLav", OracleTypes.VARCHAR, strCCLav));
			command.setAsInputParameters(17);
			// Parametri di input per fare la storicizzazione quando uguale a TRUE
			parameters.add(conn.createDataField("storicizza", OracleTypes.VARCHAR, storicizza));
			command.setAsInputParameters(18);

			// Chiamata alla Stored Procedure
			dr = command.execute(parameters);

			if ((esisteDatafinevaliditaVuota != null && esisteDatafinevaliditaVuota.equals("TRUE"))
					|| (esisteRecordDTFinePostNuovoInizio != null
							&& esisteRecordDTFinePostNuovoInizio.equals("TRUE"))) {
				/* ESEGUI UPDATE VALORE PRECEDENTE */

				statementSB = (SourceBean) getConfig().getAttribute("QUERY_UPDATE_PREC");
				statement = statementSB.getAttribute("STATEMENT").toString();
				sqlStr = SQLStatements.getStatement(statement);
				command = (StoredProcedureCommand) conn.createStoredProcedureCommand(sqlStr);

				parameters = new ArrayList();

				// Parametri di input : cdn lavoratore
				parameters.add(conn.createDataField("fine", OracleTypes.VARCHAR, DateUtils.giornoPrecedente(inizio)));
				command.setAsInputParameters(0);
				// Parametri di input : valoreISEE
				parameters.add(conn.createDataField("utmod", OracleTypes.VARCHAR, utente));
				command.setAsInputParameters(1);

				parameters
						.add(conn.createDataField("prgValIsee", OracleTypes.NUMBER, do_update_2_dtfine_prgvaloreisee));
				command.setAsInputParameters(2);

				// Chiamata alla Stored Procedure
				dr = command.execute(parameters);
			}

			transExec.commitTransaction();
			ror.reportSuccess(MessageCodes.General.UPDATE_SUCCESS);
		} catch (Exception e) {
			if (transExec != null) {
				transExec.rollBackTransaction();
			}
			if (codiceMessaggio == 0) {
				codiceMessaggio = MessageCodes.General.UPDATE_FAIL;
			}
			ror.reportFailure(codiceMessaggio);
		} finally {
			Utils.releaseResources(conn, command, dr);
		}
	}

}