package it.eng.sil.module.patto;

import java.util.Vector;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.error.EMFInternalError;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;

public class UpdateNotePatto extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(UpdateNotePatto.class.getName());

	public void service(SourceBean request, SourceBean response) throws Exception {

		// setSectionQuerySelect("QUERY_UPDATE");
		int prevIdSuccess = 0;
		int prevIdFail = 0;
		Vector prg = null;
		Vector strInf = null;
		boolean esito = false;
		boolean esitoNotes = true;
		TransactionQueryExecutor transExec = null;
		prevIdSuccess = disableMessageIdSuccess();
		prevIdFail = disableMessageIdFail();
		ReportOperationResult reportOperation = new ReportOperationResult(this, response);
		try {
			transExec = new TransactionQueryExecutor(getPool());
			enableTransactions(transExec);
			transExec.initTransaction();
			String stm = null;
			if (request.containsAttribute("strnoteazioni"))
				stm = "QUERY_UPDATE_NOTE_AZIONI";
			else if (request.containsAttribute("strnoteimpegni"))
				stm = "QUERY_UPDATE_NOTE_IMPEGNI";
			else if (request.containsAttribute("STRNOTEAMBITOPROF"))
				stm = "QUERY_UPDATE_NOTE_AMBITO_PROF";
			else if (request.containsAttribute("STRNOTESITUAZIONEAMM"))
				stm = "QUERY_UPDATE_NOTE_SITUAZIONE_AMM";
			else if (request.containsAttribute("STRNOTEFORMPROF"))
				stm = "QUERY_UPDATE_NOTE_FORMAZIONE_PROF";
			setSectionQueryUpdate(stm);
			esito = doUpdate(request, response);
			if (esito) {
				this.setSectionQueryUpdate("QUERY_UPDATE_NOTE_LAV_PATTO");
				prg = request.getAttributeAsVector("PRGLAVPATTOSCELTA");
				strInf = request.getAttributeAsVector("STRALTREINF");
				try {
					if ((prg.size() == strInf.size()) && (prg.size() > 0)) {
						for (int j = 0; j < prg.size(); j++) {
							request.delAttribute("PRGLAVPATTOSCELTA");
							request.setAttribute("PRGLAVPATTOSCELTA", prg.elementAt(j));
							request.delAttribute("STRALTREINF");
							request.setAttribute("STRALTREINF", strInf.elementAt(j));
							esitoNotes = doUpdate(request, response);
							if (!esitoNotes) {
								j = prg.size();
							}
						}
					}
				} catch (Exception e) {
					reportOperation.reportFailure(MessageCodes.Patto.ERR_AGG_NOTE_IMP, e, "execute()",
							"fallito aggiornamento note in am_lav_patto_scelta");
				}
				setMessageIdSuccess(prevIdSuccess);
				setMessageIdFail(prevIdFail);
				if (esitoNotes) {
					transExec.commitTransaction();
					reportSuccess(reportOperation);
				} else {
					throw new Exception();
				}
			}
		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.debug(_logger, "service()", e);

			reportOperation.reportFailure(MessageCodes.General.UPDATE_FAIL, e, "service()",
					"errore durante l'aggiornamento delle note degli impegni legati al patto.");
			try {
				if (transExec != null) {
					transExec.rollBackTransaction();
				}
			} catch (EMFInternalError ie) {
				it.eng.sil.util.TraceWrapper.debug(_logger, "service()", (Exception) ie);

			}
		}
	}
}