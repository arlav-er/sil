package it.eng.sil.module.movimenti.processors;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Map;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.movimenti.RecordProcessor;
import it.eng.sil.util.amministrazione.impatti.SituazioneAmministrativaFactory;
import it.eng.sil.util.amministrazione.impatti.StatoOccupazionaleBean;

public class UpdateRettificaPeriodo implements RecordProcessor {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(UpdateRettificaPeriodo.class.getName());
	private String name;
	private String classname = this.getClass().getName();
	private TransactionQueryExecutor trans;
	private BigDecimal userId;

	public UpdateRettificaPeriodo(String name, TransactionQueryExecutor transexec, BigDecimal user) {
		this.name = name;
		trans = transexec;
		this.userId = user;
	}

	public SourceBean processRecord(Map record) throws SourceBeanException {

		// vettore delle warnings da restituire
		ArrayList warnings = new ArrayList();
		// Vettore dei risultati annidati da restituire
		ArrayList nested = new ArrayList();

		// Se il record è nullo non lo posso elaborare, ritorno l'errore
		if (record == null) {
			return ProcessorsUtils.createResponse(name, classname, new Integer(MessageCodes.ImportMov.ERR_INSERT_DATA),
					"Record da elaborare nullo.", warnings, nested);
		}
		Object prgMovimentoRettPeriodo = record.get("PRGMOVRETTPERIODO");
		if (prgMovimentoRettPeriodo != null) {
			BigDecimal keyTable = (BigDecimal) record.get("PRGMOVIMENTO");
			Object cdnLavoratore = record.get("CDNLAVORATORE");
			Object params[] = new Object[3];
			params[0] = keyTable;
			params[1] = userId;
			params[2] = prgMovimentoRettPeriodo;
			Boolean res = null;
			try {
				res = (Boolean) trans.executeQuery("AGGIORNA_PERIODI_LAVORATIVI_MOVIMENTO_RETTIFICATO", params,
						"UPDATE");
				if (res == null || !res.booleanValue()) {
					return ProcessorsUtils.createResponse(name, classname,
							new Integer(MessageCodes.ImportMov.ERR_RIALLINEAMENTO_PERIODI_INTERMITTENTI), "", warnings,
							nested);
				}
				String dataCalcolo = (String) record.get("DATINIZIOMOV");
				StatoOccupazionaleBean statoOccFinale = SituazioneAmministrativaFactory
						.newInstance(cdnLavoratore.toString(), dataCalcolo, trans).calcolaImpatti();
			} catch (Exception e) {
				it.eng.sil.util.TraceWrapper.debug(_logger,
						"Impossibile aggiornare i periodi lavorativi del movimento rettificato.", e);
				return ProcessorsUtils.createResponse(name, classname,
						new Integer(MessageCodes.ImportMov.ERR_RIALLINEAMENTO_PERIODI_INTERMITTENTI), "", warnings,
						nested);
			}

		}
		return null;
	}
}