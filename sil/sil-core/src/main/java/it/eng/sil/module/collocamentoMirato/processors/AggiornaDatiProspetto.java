package it.eng.sil.module.collocamentoMirato.processors;

import java.math.BigDecimal;
import java.util.Map;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.collocamentoMirato.constant.ProspettiConstant;
import it.eng.sil.module.movimenti.processors.ProcessorsUtils;

public class AggiornaDatiProspetto implements RecordProcessor {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(AggiornaDatiProspetto.class.getName());
	private TransactionQueryExecutor trans;
	private BigDecimal userId;
	private static final String name = "Aggiorna prospetto";
	private static final String classname = "AggiornaDatiProspetto";

	public AggiornaDatiProspetto(TransactionQueryExecutor tx, BigDecimal user) {
		this.trans = tx;
		this.userId = user;
	}

	@Override
	public SourceBean processRecord(Map record) throws Exception {
		SourceBean result = null;
		try {
			Object prgProspetto = record.get(ProspettiConstant.FIELD_PROGRESSIVO);
			if (prgProspetto == null) {
				throw new Exception("Dati mancanti per elaborare il prospetto");
			}
			String query = "SELECT NUMKLOPROSPETTOINF, TO_CHAR(NUMANNORIFPROSPETTO) ANNORIF, CODPROVINCIA "
					+ " FROM CM_PROSPETTO_INF WHERE PRGPROSPETTOINF = " + prgProspetto;
			result = ProcessorsUtils.executeSelectQuery(query, trans);
			if (result != null) {
				String annoRif = (String) result.getAttribute("ROW.ANNORIF");
				String provinciaRif = (String) result.getAttribute("ROW.CODPROVINCIA");
				BigDecimal numKloProspetto = (BigDecimal) result.getAttribute("ROW.NUMKLOPROSPETTOINF");
				numKloProspetto = numKloProspetto.add(new BigDecimal("1"));
				String dataSituazione = "31/12/" + annoRif;

				String statement = "UPDATE CM_PROSPETTO_INF SET DATPROSPETTO = TO_DATE('" + dataSituazione
						+ "', 'DD/MM/YYYY'), " + " DATCONSEGNAPROSPETTO = TRUNC(SYSDATE), CDNUTMOD = "
						+ ProspettiConstant.USER_MODIFICA + ", DTMMOD = SYSDATE, " + " NUMKLOPROSPETTOINF = "
						+ numKloProspetto + " WHERE PRGPROSPETTOINF = " + prgProspetto;

				_logger.debug(classname + "::processRecord(): " + statement);

				// Aggiorno il record
				Object resultUpd = null;
				resultUpd = trans.executeQueryByStringStatement(statement, null, TransactionQueryExecutor.UPDATE);
				if (resultUpd instanceof Boolean && ((Boolean) resultUpd).booleanValue() == true) {
					record.put(ProspettiConstant.FIELD_NUMKLO, numKloProspetto);
					record.put(ProspettiConstant.FIELD_NUMANNORIF, annoRif);
					record.put(ProspettiConstant.FIELD_PROVINCIA, provinciaRif);
					return null;
				} else {
					throw new Exception("Errore in aggiornamento dati prospetto da copiare");
				}
			} else {
				throw new Exception("Errore in aggiornamento dati prospetto da copiare");
			}
		} catch (Exception e) {
			_logger.debug(classname + "::processRecord(): Errore in aggiornamento dati prospetto");
			record.put(ProspettiConstant.FIELD_CODICE, ProspettiConstant.COD_ERRORE_GENERICO);
			record.put(ProspettiConstant.FIELD_DESC_RISULTATO, ProspettiConstant.DESC_ERRORE_GENERICO);
			return ProcessorsUtils.createResponse(name, classname,
					new Integer(MessageCodes.LogOperazioniCopiaProspetti.ERRORE_UPDATE_PROSPETTO_DA_COPIARE), "", null,
					null);
		}
	}

	public String toString() {
		return classname;
	}

}
