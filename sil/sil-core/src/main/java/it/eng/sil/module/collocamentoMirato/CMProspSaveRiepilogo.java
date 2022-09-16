package it.eng.sil.module.collocamentoMirato;

import java.util.Vector;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;

public class CMProspSaveRiepilogo extends AbstractSimpleModule {

	public void service(SourceBean serviceRequest, SourceBean serviceResponse) throws Exception {

		boolean check = true;
		int idSuccess = this.disableMessageIdSuccess();
		int idFail = this.disableMessageIdFail();
		int msgCode = MessageCodes.General.UPDATE_FAIL;
		TransactionQueryExecutor transExec = null;
		int numLavL68Art18FlgBatt = 0;
		int numLavL68Art18 = 0;

		ReportOperationResult reportOperation = new ReportOperationResult(this, serviceResponse);
		try {
			if (!serviceRequest.containsAttribute("CHECK2011")) {
				this.setSectionQuerySelect("GET_LAV_L68_PROSP");
				SourceBean rowLav68 = (SourceBean) doSelect(serviceRequest, serviceResponse);
				Vector listaLav68 = rowLav68.getAttributeAsVector("ROW");
				for (int i = 0; i < listaLav68.size(); i++) {
					SourceBean row = (SourceBean) listaLav68.get(i);
					String flgBattistoni = StringUtils.getAttributeStrNotNull(row, "FLGBATTISTONI");
					String categoria = StringUtils.getAttributeStrNotNull(row, "CODMONOCATEGORIA");
					if (categoria.equalsIgnoreCase("A")) {
						if (flgBattistoni.equalsIgnoreCase("SI")) {
							numLavL68Art18FlgBatt = numLavL68Art18FlgBatt + 1;
						}
						numLavL68Art18 = numLavL68Art18 + 1;
					}
				}
				String numBattistoni17012000 = StringUtils.getAttributeStrNotNull(serviceRequest,
						"numbattistoniinforza");
				Integer numBattistoni = null;
				if (numBattistoni17012000.equals("")) {
					numBattistoni = new Integer("0");
				} else {
					numBattistoni = new Integer(numBattistoni17012000);
				}

				if (numBattistoni.intValue() < numLavL68Art18FlgBatt) {
					msgCode = MessageCodes.CollocamentoMirato.ERROR_BATTISTONI_17012000_ALTRA_CAT_ART18;
					throw new Exception("Errore durante l'aggiornamento. Operazione interrotta");
				}

				if (numBattistoni.intValue() > numLavL68Art18) {
					msgCode = MessageCodes.CollocamentoMirato.ERROR_BATTISTONI_17012000_ALTRA_CAT;
					throw new Exception("Errore durante l'aggiornamento. Operazione interrotta");
				}
			}

			transExec = new TransactionQueryExecutor(getPool());
			this.enableTransactions(transExec);

			transExec.initTransaction();

			this.setSectionQueryUpdate("QUERY_UPDATE");
			check = doUpdate(serviceRequest, serviceResponse);

			if (!check) {
				throw new Exception("Errore durante l'aggiornamento. Operazione interrotta");
			}

			transExec.commitTransaction();

			reportOperation.reportSuccess(idSuccess);

		} catch (Exception e) {
			if (transExec != null) {
				transExec.rollBackTransaction();
			}
			reportOperation.reportFailure(msgCode, e, "services()", "update in transazione");

		} finally {

		}

	}
}
