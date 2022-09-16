/*
 * Creato il 6-set-05
 *
 */
package it.eng.sil.module.movimenti;

import java.math.BigDecimal;
import java.sql.Types;
import java.util.ArrayList;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.SQLStatements;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.command.std.StoredProcedureCommand;
import com.engiweb.framework.dbaccess.sql.result.DataResult;
import com.engiweb.framework.error.EMFInternalError;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.MessageAppender;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.SourceBeanUtils;
import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.module.movimenti.constant.Properties;
import it.eng.sil.util.Sottosistema;
import it.eng.sil.util.amministrazione.impatti.Controlli;
import it.eng.sil.util.amministrazione.impatti.SituazioneAmministrativaFactory;
import it.eng.sil.util.amministrazione.impatti.StatoOccupazionaleBean;

/**
 * @author D'Auria
 * 
 *         Per modificare il modello associato al commento di questo tipo generato, aprire
 *         Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
public class UpdateMovimento extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(UpdateMovimento.class.getName());

	public void service(SourceBean serviceRequest, SourceBean serviceResponse) throws Exception {

		String prgMov = (String) serviceRequest.getAttribute("PRGMOV");
		String dataInizioMov = (String) serviceRequest.getAttribute("DATINIZIOMOV");
		String dataInizioAttuale = (String) serviceRequest.getAttribute("datInizioAttuale");
		String cdnLavoratore = (String) serviceRequest.getAttribute("CDNLAVORATORE");
		BigDecimal numKloMov = new BigDecimal((String) serviceRequest.getAttribute("NUMKLOMOV"));
		BigDecimal user = (BigDecimal) getRequestContainer().getSessionContainer().getAttribute("_CDUT_");
		String prgAz = (String) serviceRequest.getAttribute("prgAzienda");
		String prgUnita = (String) serviceRequest.getAttribute("prgunita");
		boolean cambia = serviceRequest.containsAttribute("cambiaTutto");
		Boolean updateDoc = new Boolean(false);
		Boolean updateMov = new Boolean(false);
		// CM 07/02/2007 savino: incentivi art. 13
		String datFineSgravio = StringUtils.getAttributeStrNotNull(serviceRequest, "DATFINESGRAVIO");
		String decImportoConcesso = StringUtils.getAttributeStrNotNull(serviceRequest, "DECIMPORTOCONCESSO");
		String datFineSgravioOld = StringUtils.getAttributeStrNotNull(serviceRequest, "DATFINESGRAVIO_OLD");
		String decImportoConcessoOld = StringUtils.getAttributeStrNotNull(serviceRequest, "DECIMPORTOCONCESSO_OLD");
		String prgMovimentoArt13 = StringUtils.getAttributeStrNotNull(serviceRequest, "PRGMOVIMENTO_ART13");
		boolean aggiornaSoloIncentivi = serviceRequest.containsAttribute("aggiornaSoloIncentivi");

		// Ricavo il prg del documento associato al movimento
		Object params[] = new Object[1];
		TransactionQueryExecutor transExec = null;

		try {

			transExec = new TransactionQueryExecutor(getPool(), this);
			enableTransactions(transExec);
			transExec.initTransaction();

			// INIT-PARTE-TEMP
			if (Sottosistema.CM.isOn()) {
				// END-PARTE-TEMP
				String art13_statement = null;
				String operazione = null;
				String colonne = "", valori = "";
				if (prgMovimentoArt13.equals("")) {
					if (!datFineSgravio.equals("") || !decImportoConcesso.equals("")) {
						colonne += "PRGMOVIMENTO";
						valori = prgMov;
						if (!datFineSgravio.equals("")) {
							colonne += ", DATFINESGRAVIO";
							valori += ", to_date('" + datFineSgravio + "','dd/mm/yyyy')";
						}
						if (!decImportoConcesso.equals("")) {
							colonne += ", DECIMPORTOCONCESSO";
							valori += ", " + decImportoConcesso;
						}
						art13_statement = "INSERT INTO CM_MOV_L68_ART13 (" + colonne + ") VALUES (" + valori + ")";
						operazione = "INSERT";
					}
				} else { // Lo aggiorno solo se almeno uno dei campi e'
							// diverso da stringa vuota.
					// N.B. Se l'operatore inserisce uno spazio bisogna
					// cancellare il dato.
					if (!datFineSgravio.equals("") || !decImportoConcesso.equals("")) {
						if (!datFineSgravio.equals("") && !datFineSgravio.trim().equals("")) {
							colonne += " DATFINESGRAVIO = to_date('" + datFineSgravio + "','dd/mm/yyyy')";
						} else {
							colonne += "DATFINESGRAVIO=null";
						}
						if (!decImportoConcesso.equals("") && !decImportoConcesso.trim().equals("")) {
							colonne += ", DECIMPORTOCONCESSO=" + decImportoConcesso;
						} else {
							colonne += ", DECIMPORTOCONCESSO=null";
						}

						art13_statement = "UPDATE CM_MOV_L68_ART13 SET " + colonne + " WHERE PRGMOVIMENTO = " + prgMov;
						operazione = "UPDATE";
					}
				}
				if (art13_statement != null) {
					System.out.println(art13_statement);
					Object result = transExec.executeQueryByStringStatement(art13_statement, null, operazione);
					if (result instanceof Exception) {
						// Se ho avuto problemi nella query lo segnalo
						_logger.debug(result.toString());

						throw new Exception("aggiornamento/inserimento CM_MOV_L68_ART13 fallito");
					} else if (result instanceof Boolean && ((Boolean) result).booleanValue() == false) {
						throw new Exception("aggiornamento/inserimento CM_MOV_L68_ART13 fallito");
					}
				}
				// INIT-PARTE-TEMP
			}
			// END-PARTE-TEMP
			SourceBean doc = null;
			BigDecimal prgDoc = null;
			BigDecimal numKloDoc = null;
			if (!aggiornaSoloIncentivi) {
				params[0] = prgMov;

				doc = (SourceBean) transExec.executeQuery("DETTAGLIO_DOC_PRGMOV", params, "SELECT");
				prgDoc = null;
				numKloDoc = null;
				if (doc.containsAttribute("ROW")) {
					prgDoc = SourceBeanUtils.getAttrBigDecimal(doc, "ROW.PRGDOCUMENTO");
					numKloDoc = SourceBeanUtils.getAttrBigDecimal(doc, "ROW.NUMKLODOCUMENTO");
				}
			}

			// Se ho il prg documento allora aggiorno il documento
			if (!aggiornaSoloIncentivi && prgDoc != null) {
				params = new Object[4];

				params[0] = prgAz;
				params[1] = prgUnita;
				params[2] = numKloDoc;
				params[3] = prgDoc;

				updateDoc = (Boolean) transExec.executeQuery("UPDATE_AZIENDA_DOC", params, "UPDATE");

				if (!updateDoc.booleanValue()) {
					throw new Exception("Errore nell'aggiornamento");
				}

			}

			// Aggiorno il movimento se il documento è stato aggiornato
			// correttamente
			// oppuse se il movimento non ha un documento associato (da porting)
			if (!aggiornaSoloIncentivi && (updateDoc.booleanValue() || prgDoc == null)) {
				params = new Object[8];

				String datFineSosp150 = Controlli.calcolaFineSospensione(dataInizioMov,
						Properties.MESI_SOSP_DECRETO150);

				params[0] = dataInizioMov;
				params[1] = dataInizioMov;
				params[2] = numKloMov;
				params[3] = user;
				params[4] = prgAz;
				params[5] = prgUnita;
				params[6] = datFineSosp150;
				params[7] = prgMov;

				updateMov = (Boolean) transExec.executeQuery("UPDATE_MOVIMENTO", params, "UPDATE");

				if (!updateMov.booleanValue()) {
					throw new Exception("Errore nell'aggiornamento");
				}

				if (dataInizioMov != null && !dataInizioMov.equals("") && dataInizioAttuale != null
						&& !dataInizioAttuale.equals("") && DateUtils.compare(dataInizioMov, dataInizioAttuale) != 0) {
					if (!getRequestContainer().getServiceRequest().containsAttribute("FORZA_INSERIMENTO")) {
						getRequestContainer().getServiceRequest().setAttribute("FORZA_INSERIMENTO", "true");
					}
					if (!getRequestContainer().getServiceRequest().containsAttribute("CONTINUA_CALCOLO_SOCC")) {
						getRequestContainer().getServiceRequest().setAttribute("CONTINUA_CALCOLO_SOCC", "true");
					}
					if (!getRequestContainer().getServiceRequest().containsAttribute("FORZA_CHIUSURA_MOBILITA")) {
						getRequestContainer().getServiceRequest().setAttribute("FORZA_CHIUSURA_MOBILITA", "true");
					}

					StatoOccupazionaleBean statoOccupazionale = SituazioneAmministrativaFactory
							.newInstance(cdnLavoratore, dataInizioMov, transExec).calcolaImpatti();
				}
			}

			if (!aggiornaSoloIncentivi && cambia) {

				DataConnection conn = transExec.getDataConnection();
				StoredProcedureCommand command = null;
				DataResult dr = null;

				// imposto la chiamata per la stored procedure
				SourceBean statementSB = (SourceBean) getConfig().getAttribute("QUERY");
				String statement = statementSB.getAttribute("statement").toString();
				String sqlStr = SQLStatements.getStatement(statement);
				command = (StoredProcedureCommand) conn.createStoredProcedureCommand(sqlStr);

				// imposto i parametri
				ArrayList parameters = new ArrayList(3);
				parameters.add(conn.createDataField("PRGMOV", Types.VARCHAR, prgMov));
				command.setAsInputParameters(0);
				parameters.add(conn.createDataField("PRGAZ", Types.BIGINT, prgAz));
				command.setAsInputParameters(1);
				parameters.add(conn.createDataField("PRGUA", Types.VARCHAR, prgUnita));
				command.setAsInputParameters(2);

				// eseguo!!
				dr = command.execute(parameters);
			}

			transExec.commitTransaction();
			MessageAppender.appendMessage(serviceResponse, MessageCodes.General.UPDATE_SUCCESS);
			serviceResponse.setAttribute("ESITO", "OK");

		} catch (Exception ex) {
			try {
				if (transExec != null) {
					MessageAppender.appendMessage(serviceResponse, MessageCodes.General.UPDATE_FAIL);
					transExec.rollBackTransaction();
					serviceResponse.setAttribute("ESITO", "NO");
				}
			} catch (EMFInternalError ie) {
				it.eng.sil.util.TraceWrapper.debug(_logger, "service()", (Exception) ie);

			}
		}

	}

}