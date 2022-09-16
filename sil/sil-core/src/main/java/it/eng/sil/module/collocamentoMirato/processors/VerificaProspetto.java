package it.eng.sil.module.collocamentoMirato.processors;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Map;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.collocamentoMirato.constant.ProspettiConstant;
import it.eng.sil.module.movimenti.processors.ProcessorsUtils;
import it.eng.sil.module.movimenti.processors.Warning;

public class VerificaProspetto implements RecordProcessor {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(VerificaProspetto.class.getName());
	private TransactionQueryExecutor trans;
	private BigDecimal userId;
	private static final String name = "Verifica prospetto";
	private static final String classname = "VerificaProspetto";

	public VerificaProspetto(TransactionQueryExecutor tx, BigDecimal user) {
		this.trans = tx;
		this.userId = user;
	}

	public SourceBean processRecord(Map record) throws Exception {
		CallableStatement command = null;
		ResultSet dr = null;
		try {
			// vettore delle warnings da restituire
			ArrayList warnings = new ArrayList();
			BigDecimal prgProspetto = (BigDecimal) record.get(ProspettiConstant.FIELD_PROGRESSIVO);
			boolean storicizzaProspetto = true;
			String dettaglioWarning = "";
			String statement = "";

			int err_percCompensazione = 0;
			int err_numCompInProv = 0;
			int err_numCompArt18 = 0;
			int err_numCompRiduz = 0;
			int err_numCompensEcc = 0;
			int err_dataConsegnaProspetto = 0;
			int err_dataRiferimento = 0;
			int err_checkProspStoriciz = 0;

			String stmProcedure = "{ call ? := PG_COLL_MIRATO_2.VERIFICAPROSPETTO(?,?,?,?,?,?,?,?,?,?,?,?,?,?) }";

			command = this.trans.getDataConnection().getInternalConnection().prepareCall(stmProcedure);
			command.registerOutParameter(1, Types.VARCHAR);
			command.setBigDecimal(2, prgProspetto);
			command.registerOutParameter(3, Types.INTEGER);
			command.registerOutParameter(4, Types.INTEGER);
			command.registerOutParameter(5, Types.INTEGER);
			command.registerOutParameter(6, Types.INTEGER);
			command.registerOutParameter(7, Types.INTEGER);
			command.registerOutParameter(8, Types.INTEGER);
			command.registerOutParameter(9, Types.INTEGER);
			command.registerOutParameter(10, Types.INTEGER);
			command.registerOutParameter(11, Types.INTEGER);
			command.registerOutParameter(12, Types.INTEGER);
			command.registerOutParameter(13, Types.INTEGER);
			command.registerOutParameter(14, Types.INTEGER);
			command.registerOutParameter(15, Types.INTEGER);

			dr = command.executeQuery();

			// LEGGO IL RISULTATO
			String codiceRit = command.getString(1);
			if (!codiceRit.equals("0")) {
				storicizzaProspetto = false;
			} else {
				err_percCompensazione = command.getInt(4);
				err_numCompInProv = command.getInt(5);
				err_numCompArt18 = command.getInt(6);
				err_numCompRiduz = command.getInt(7);
				err_numCompensEcc = command.getInt(8);
				err_dataConsegnaProspetto = command.getInt(9);
				err_dataRiferimento = command.getInt(11);
				err_checkProspStoriciz = command.getInt(12);

				if (err_percCompensazione > 0 || err_numCompInProv > 0 || err_numCompArt18 > 0 || err_numCompRiduz > 0
						|| err_numCompensEcc > 0) {
					dettaglioWarning = "ATTENZIONE: problema sulle compensazioni territoriali.";
					storicizzaProspetto = false;
				} else {
					if (err_dataConsegnaProspetto > 0) {
						dettaglioWarning = "ATTENZIONE: problema sulla data di consegna del prospetto.";
						storicizzaProspetto = false;
					} else {
						if (err_dataRiferimento > 0) {
							dettaglioWarning = "ATTENZIONE: problema sulla data di riferimento del prospetto.";
							storicizzaProspetto = false;
						} else {
							if (err_checkProspStoriciz > 0) {
								dettaglioWarning = "ATTENZIONE: esiste un altro prospetto storicizzato.";
								storicizzaProspetto = false;
							}
						}
					}
				}
			}

			String query = "SELECT NUMKLOPROSPETTOINF FROM CM_PROSPETTO_INF WHERE PRGPROSPETTOINF = " + prgProspetto;
			SourceBean result = ProcessorsUtils.executeSelectQuery(query, trans);
			if (result != null) {
				BigDecimal numkloprospetto = (BigDecimal) result.getAttribute("ROW.NUMKLOPROSPETTOINF");
				numkloprospetto = numkloprospetto.add(new BigDecimal("1"));

				// EFFETTUO ANNULLAMENTO O STORICIZZAZIONE PROSPETTO
				String codComunicazione = "SYSTEM" + record.get(ProspettiConstant.FIELD_NUMANNORIF).toString();
				if (storicizzaProspetto) {
					statement = "UPDATE CM_PROSPETTO_INF SET CODMONOSTATOPROSPETTO = '"
							+ ProspettiConstant.STATO_STORICIZZATO + "', " + " CDNUTMOD = "
							+ ProspettiConstant.USER_MODIFICA + ", DTMMOD = SYSDATE, CODCOMUNICAZIONE = '"
							+ codComunicazione + "', " + " NUMKLOPROSPETTOINF = " + numkloprospetto
							+ " WHERE PRGPROSPETTOINF = " + prgProspetto;
				} else {
					statement = "UPDATE CM_PROSPETTO_INF SET CODMONOSTATOPROSPETTO = '"
							+ ProspettiConstant.STATO_ANNULLATO + "', " + " CDNUTMOD = "
							+ ProspettiConstant.USER_MODIFICA + ", DTMMOD = SYSDATE, CODCOMUNICAZIONE = '"
							+ codComunicazione + "', " + " NUMKLOPROSPETTOINF = " + numkloprospetto
							+ " WHERE PRGPROSPETTOINF = " + prgProspetto;
				}

				_logger.debug(classname + "::processRecord(): " + statement);

				// Aggiorno il record
				Object resultUpd = null;
				resultUpd = trans.executeQueryByStringStatement(statement, null, TransactionQueryExecutor.UPDATE);
				if (resultUpd instanceof Boolean && ((Boolean) resultUpd).booleanValue() == true) {
					record.put(ProspettiConstant.FIELD_NUMKLO, numkloprospetto);
					if (!storicizzaProspetto) {
						Warning w = new Warning(MessageCodes.LogOperazioniCopiaProspetti.WARNING_VERIFICA,
								dettaglioWarning);
						warnings.add(w);

						record.put(ProspettiConstant.FIELD_CODICE, ProspettiConstant.COD_WARNING_VERIFICA);
						dettaglioWarning = ProspettiConstant.DESC_WARNING_VERIFICA + dettaglioWarning;
						record.put(ProspettiConstant.FIELD_DESC_RISULTATO, dettaglioWarning);

						return ProcessorsUtils.createResponse(name, classname, null, null, warnings, null);

					} else {
						return null;
					}
				} else {
					throw new Exception("Errore in verifica dati prospetto da copiare");
				}
			} else {
				throw new Exception("Errore in verifica dati prospetto da copiare");
			}
		} catch (Exception e) {
			_logger.debug(classname + "::processRecord(): Errore in ricalcolo riepilogo prospetto");
			record.put(ProspettiConstant.FIELD_CODICE, ProspettiConstant.COD_ERRORE_VERIFICA);
			record.put(ProspettiConstant.FIELD_DESC_RISULTATO, ProspettiConstant.DESC_ERRORE_VERIFICA);
			return ProcessorsUtils.createResponse(name, classname,
					new Integer(MessageCodes.LogOperazioniCopiaProspetti.ERRORE_VERIFICA), "", null, null);
		} finally {
			if (dr != null) {
				dr.close();
			}
			if (command != null) {
				command.close();
			}
		}
	}

	public String toString() {
		return classname;
	}

}