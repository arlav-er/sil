package it.eng.sil.module.ido;

import java.util.Vector;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;

public class InsertEsitiRichiesta extends AbstractSimpleModule {
	private String className = this.getClass().getName();

	public void service(SourceBean request, SourceBean response) throws Exception {
		ReportOperationResult reportOperation = new ReportOperationResult(this, response);
		TransactionQueryExecutor transExec = null;
		String codEsitiAll = StringUtils.getAttributeStrNotNull(request, "CODICIESITI");
		String prgRichiestaAz = StringUtils.getAttributeStrNotNull(request, "PRGRICHIESTAAZ");
		int sizeCodiceEsiti = 0;
		Vector rowsCodiciEsiti = null;
		String statInsert = "";
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
				statInsert = "insert into do_richiesta_esito (prgdorichiestaesito, prgrichiestaaz, codesitooff, numvalore) "
						+ " values (s_do_richiesta_esito.nextval, to_number('" + prgRichiestaAz + "'), '" + codEsito
						+ "', ";
				String valEsitoQuery = valoreEsito == null ? null : " to_number('" + valoreEsito + "')";
				statInsert = statInsert + valEsitoQuery + ")";
				Object resultIns = transExec.executeQueryByStringStatement(statInsert, null,
						TransactionQueryExecutor.INSERT);
				if (resultIns instanceof Exception) {
					throw new Exception("");
				} else {
					if (!((resultIns instanceof Boolean) && (((Boolean) resultIns).booleanValue() == true))) {
						throw new Exception("");
					}
				}
			}
			transExec.commitTransaction();
			reportOperation.reportSuccess(MessageCodes.General.INSERT_SUCCESS);
		} catch (Exception e) {
			reportOperation.reportFailure(MessageCodes.General.INSERT_FAIL);
			if (transExec != null) {
				transExec.rollBackTransaction();
			}
		}
	}

}