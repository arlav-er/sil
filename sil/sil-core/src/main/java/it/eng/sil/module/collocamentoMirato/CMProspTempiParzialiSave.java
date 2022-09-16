package it.eng.sil.module.collocamentoMirato;

import java.math.BigDecimal;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;

public class CMProspTempiParzialiSave extends AbstractSimpleModule {

	public void service(SourceBean serviceRequest, SourceBean serviceResponse) throws Exception {

		boolean check = true;
		int idSuccess = this.disableMessageIdSuccess();
		int idFail = this.disableMessageIdFail();
		this.disableMessageIdElementDuplicate();
		TransactionQueryExecutor transExec = null;
		ReportOperationResult reportOperation = new ReportOperationResult(this, serviceResponse);

		try {
			transExec = new TransactionQueryExecutor(getPool());
			this.enableTransactions(transExec);

			transExec.initTransaction();

			String message = StringUtils.getAttributeStrNotNull(serviceRequest, "MESSAGE");

			this.setSectionQuerySelect("QUERY_SELECT");

			if (("INSERT").equalsIgnoreCase(message)) {
				BigDecimal prgDettPTDisabileVal = doNextVal(serviceRequest, serviceResponse);
				if (prgDettPTDisabileVal == null) {
					throw new Exception("Impossibile leggere s_pi_dett_pt_disabile.nextval");
				}

				serviceRequest.delAttribute("PRGDETTPTDISABILE");
				serviceRequest.setAttribute("PRGDETTPTDISABILE", prgDettPTDisabileVal.toString());

				this.setSectionQueryInsert("QUERY_INSERT");
				check = doInsertNoDuplicate(serviceRequest, serviceResponse);
			} else {
				check = canUpdateWithNoDuplicate(serviceRequest, serviceResponse, "PRGDETTPTDISABILE");
				if (check) {
					this.setSectionQueryUpdate("QUERY_UPDATE");
					if (!doUpdate(serviceRequest, serviceResponse))
						throw new Exception(
								"Errore durante l'aggiornamento del dettaglio del tempo parziale. Operazione interrotta");
				}
			}

			if (!check) {
				transExec.rollBackTransaction();
				reportOperation.reportWarning(MessageCodes.CollocamentoMirato.ERR_TEMPO_PARZIALE_DUPLICATO,
						"CMProspTempiParzialiSave", "Tempo Parziale duplicato");
			} else {
				String flgOltre50 = "N";
				BigDecimal decCopertura = new BigDecimal(0);

				this.setSectionQuerySelect("CALCOLO_TP");
				SourceBean sb = doSelect(serviceRequest, serviceResponse);
				if (sb != null) {
					flgOltre50 = (String) sb.getAttribute("ROW.FLGOLTRE50");
					decCopertura = (BigDecimal) sb.getAttribute("ROW.DECCOPERTURA");
				} else
					throw new Exception(
							"Errore durante il calcolo di flgOltre50 e di deccopertura del tempo parziale. Operazione interrotta");

				serviceRequest.delAttribute("FLGOLTRE50");
				serviceRequest.setAttribute("FLGOLTRE50", flgOltre50);
				serviceRequest.delAttribute("DECCOPERTURA");
				serviceRequest.setAttribute("DECCOPERTURA", decCopertura.toString());

				this.setSectionQueryUpdate("AGGIORNA_TP");
				if (!doUpdate(serviceRequest, serviceResponse))
					throw new Exception(
							"Errore durante l'aggiornamento di flgOltre50 e di deccopertura del tempo parziale. Operazione interrotta");

				transExec.commitTransaction();

				reportOperation.reportSuccess(idSuccess);
			}
		} catch (Exception e) {
			transExec.rollBackTransaction();
			reportOperation.reportFailure(idFail, e, "services()",
					"errore in transazione aggiornamento del dettaglio tempo parziale");

		} finally {
		}

	}
}
