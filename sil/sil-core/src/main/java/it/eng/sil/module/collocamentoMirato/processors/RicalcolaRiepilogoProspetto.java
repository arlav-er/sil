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

public class RicalcolaRiepilogoProspetto implements RecordProcessor {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(RicalcolaRiepilogoProspetto.class.getName());
	private TransactionQueryExecutor trans;
	private BigDecimal userId;
	private static final String name = "Ricalcola riepilogo";
	private static final String classname = "RicalcolaRiepilogoProspetto";

	public RicalcolaRiepilogoProspetto(TransactionQueryExecutor tx, BigDecimal user) {
		this.trans = tx;
		this.userId = user;
	}

	public SourceBean processRecord(Map record) throws Exception {
		ResultSet dr = null;
		CallableStatement command = null;
		try {
			BigDecimal prgProspetto = (BigDecimal) record.get(ProspettiConstant.FIELD_PROGRESSIVO);
			String stmProcedure = "{ call ? := PG_COLL_MIRATO_2.RICALCOLORIEPILOGOPROSP(?,?) }";
			command = this.trans.getDataConnection().getInternalConnection().prepareCall(stmProcedure);
			command.registerOutParameter(1, Types.VARCHAR);
			command.setBigDecimal(2, prgProspetto);
			command.setString(3, ProspettiConstant.FLAG_CONVENZIONE);
			dr = command.executeQuery();

			// LEGGO IL RISULTATO
			String codiceRit = command.getString(1);
			if (!codiceRit.equals("0")) {
				throw new Exception("Impossibile ricalcolare il riepilogo prospetto.");
			} else {
				return null;
			}
		} catch (Exception e) {
			_logger.debug(classname + "::processRecord(): Errore in ricalcolo riepilogo prospetto");
			record.put(ProspettiConstant.FIELD_CODICE, ProspettiConstant.COD_ERRORE_RICALCOLO);
			record.put(ProspettiConstant.FIELD_DESC_RISULTATO, ProspettiConstant.DESC_ERRORE_RICALCOLO);
			return ProcessorsUtils.createResponse(name, classname,
					new Integer(MessageCodes.LogOperazioniCopiaProspetti.ERRORE_RICALCOLO_RIEPILOGO), "", null, null);
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
