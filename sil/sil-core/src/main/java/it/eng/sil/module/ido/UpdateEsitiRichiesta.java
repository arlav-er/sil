package it.eng.sil.module.ido;

import java.util.Vector;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;

public class UpdateEsitiRichiesta extends AbstractSimpleModule {
	public void service(SourceBean request, SourceBean response) throws Exception {
		ReportOperationResult reportOperation = new ReportOperationResult(this, response);
		TransactionQueryExecutor transExec = null;
		String codEsitiAll = StringUtils.getAttributeStrNotNull(request, "CODICIESITI");
		String prgRichiestaAz = StringUtils.getAttributeStrNotNull(request, "PRGRICHIESTAAZ");
		int sizeCodiceEsiti = 0;
		Vector rowsCodiciEsiti = null;
		String statUpdate = "";
		try {
			if (!codEsitiAll.equals("")) {
				rowsCodiciEsiti = StringUtils.split(codEsitiAll, "#");
				sizeCodiceEsiti = rowsCodiciEsiti.size();
			}
			transExec = new TransactionQueryExecutor(getPool());
			transExec.initTransaction();
			for (int i = 0; i < sizeCodiceEsiti; i++) {
				String codEsito = rowsCodiciEsiti.get(i).toString();
				String valoreEsito = StringUtils.getAttributeStrNotNull(request, "valoreEsito_" + codEsito);
				if (valoreEsito.equals("")) {
					valoreEsito = null;
				}
				String valEsitoQuery = valoreEsito == null ? null : " to_number('" + valoreEsito + "')";
				statUpdate = "update do_richiesta_esito set numvalore = " + valEsitoQuery
						+ " where prgrichiestaaz = to_number('" + prgRichiestaAz + "') and codesitooff = '" + codEsito
						+ "'";
				Object resultUpd = transExec.executeQueryByStringStatement(statUpdate, null,
						TransactionQueryExecutor.UPDATE);
				if (resultUpd instanceof Exception) {
					throw new Exception("");
				} else {
					if (!((resultUpd instanceof Boolean) && (((Boolean) resultUpd).booleanValue() == true))) {
						throw new Exception("");
					}
				}
			}
			transExec.commitTransaction();
			reportOperation.reportSuccess(MessageCodes.General.UPDATE_SUCCESS);
		} catch (Exception e) {
			reportOperation.reportFailure(MessageCodes.General.UPDATE_FAIL);
			if (transExec != null) {
				transExec.rollBackTransaction();
			}
		}
	}
}