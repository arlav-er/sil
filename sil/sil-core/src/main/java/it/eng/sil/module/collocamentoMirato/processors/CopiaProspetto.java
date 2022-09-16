package it.eng.sil.module.collocamentoMirato.processors;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.Map;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.collocamentoMirato.constant.ProspettiConstant;
import it.eng.sil.module.movimenti.processors.ProcessorsUtils;

public class CopiaProspetto implements RecordProcessor {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(CopiaProspetto.class.getName());
	private TransactionQueryExecutor trans;
	private BigDecimal userId;
	private String annoProspetto = null;
	private static final String name = "Copia prospetto";
	private static final String classname = "CopiaProspetto";

	public CopiaProspetto(TransactionQueryExecutor tx, String anno, BigDecimal user) {
		this.trans = tx;
		this.userId = user;
		this.annoProspetto = anno;
	}

	public SourceBean processRecord(Map record) throws Exception {
		CallableStatement command = null;
		ResultSet dr = null;
		String dettaglioWarning = "";
		try {
			BigDecimal prgProspetto = (BigDecimal) record.get(ProspettiConstant.FIELD_PROGRESSIVO);
			String encryptKey = System.getProperty("_ENCRYPTER_KEY_");
			String provinciaRif = (String) record.get(ProspettiConstant.FIELD_PROVINCIA);

			String stmProcedure = "{ call ? := PG_COLL_MIRATO_2.GENERACOPIAPROSPETTO(?,?,?,?,?,?,?) }";

			command = this.trans.getDataConnection().getInternalConnection().prepareCall(stmProcedure);
			command.registerOutParameter(1, Types.VARCHAR);
			command.setBigDecimal(2, prgProspetto);
			command.setString(3, provinciaRif);
			command.setString(4, this.annoProspetto);
			command.setBigDecimal(5, ProspettiConstant.USER_MODIFICA);
			command.setString(6, ProspettiConstant.FLAG_ANNULLA);
			command.setString(7, encryptKey);
			command.registerOutParameter(8, Types.NUMERIC);

			dr = command.executeQuery();

			// LEGGO IL RISULTATO
			String codiceRit = command.getString(1);
			BigDecimal prgNew = command.getBigDecimal(8);
			if (!codiceRit.equals("0") || prgNew == null) {
				throw new Exception("Impossibile effettuare la copia del prospetto.");
			} else {
				String query = "SELECT NUMKLOPROSPETTOINF FROM CM_PROSPETTO_INF WHERE PRGPROSPETTOINF = " + prgNew;
				SourceBean result = ProcessorsUtils.executeSelectQuery(query, trans);
				if (result != null) {
					BigDecimal numkloprospetto = (BigDecimal) result.getAttribute("ROW.NUMKLOPROSPETTOINF");
					numkloprospetto = numkloprospetto.add(new BigDecimal("1"));

					String statement = "UPDATE CM_PROSPETTO_INF SET DTMMOD = SYSDATE, NUMKLOPROSPETTOINF = "
							+ numkloprospetto + ", " + " CODCOMUNICAZIONE = 'SYSTEM' WHERE PRGPROSPETTOINF = " + prgNew;

					_logger.debug(classname + "::processRecord(): " + statement);
					// Aggiorno il record
					Object resultUpd = null;
					resultUpd = trans.executeQueryByStringStatement(statement, null, TransactionQueryExecutor.UPDATE);
					if (resultUpd instanceof Boolean && ((Boolean) resultUpd).booleanValue() == true) {
						record.put(ProspettiConstant.FIELD_PROGRESSIVO_NEW, prgNew);
						if (record.containsKey(ProspettiConstant.FIELD_DESC_RISULTATO)) {
							dettaglioWarning = record.get(ProspettiConstant.FIELD_DESC_RISULTATO).toString();
						}
						if (record.containsKey(ProspettiConstant.FIELD_CODICE)
								&& record.get(ProspettiConstant.FIELD_CODICE).toString()
										.equalsIgnoreCase(ProspettiConstant.COD_WARNING_VERIFICA)) {
							record.put(ProspettiConstant.FIELD_CODICE, ProspettiConstant.COD_ANNULLATO_COPIATO);
							dettaglioWarning = dettaglioWarning + ProspettiConstant.DESC_ANNULLATO_COPIATO;
						} else {
							record.put(ProspettiConstant.FIELD_CODICE, ProspettiConstant.COD_STORICIZZATO_COPIATO);
							dettaglioWarning = dettaglioWarning + ProspettiConstant.DESC_STORICIZZATO_COPIATO;
						}
						record.put(ProspettiConstant.FIELD_DESC_RISULTATO, dettaglioWarning);
						return null;
					} else {
						throw new Exception("Impossibile effettuare la copia del prospetto.");
					}
				} else {
					throw new Exception("Impossibile effettuare la copia del prospetto.");
				}
			}
		} catch (Exception e) {
			_logger.debug(classname + "::processRecord(): Errore in copia prospetto");
			record.put(ProspettiConstant.FIELD_CODICE, ProspettiConstant.COD_ERRORE_COPIA);
			return ProcessorsUtils.createResponse(name, classname,
					new Integer(MessageCodes.LogOperazioniCopiaProspetti.ERRORE_GENERAZIONE_COPIA), "", null, null);
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
