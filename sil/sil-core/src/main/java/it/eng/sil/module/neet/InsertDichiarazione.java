package it.eng.sil.module.neet;

import java.math.BigDecimal;
import java.util.Vector;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;

public class InsertDichiarazione extends AbstractSimpleModule {

	private static final long serialVersionUID = 1L;

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(InsertDichiarazione.class.getName());

	public void service(SourceBean request, SourceBean response) throws Exception {

		Object ris = null;
		BigDecimal prgDichiarazione = null;
		int idSuccess = this.disableMessageIdSuccess();
		TransactionQueryExecutor transExec = null;
		ReportOperationResult reportOperation = new ReportOperationResult(this, response);
		String cdnLavoratore = StringUtils.getAttributeStrNotNull(request, "cdnLavoratore");
		String dataDichiarazione = StringUtils.getAttributeStrNotNull(request, "datDichiarazione");
		BigDecimal cdnUtente = (BigDecimal) getRequestContainer().getSessionContainer().getAttribute("_CDUT_");
		try {
			transExec = new TransactionQueryExecutor(getPool());
			transExec.initTransaction();

			SourceBean rowKey = (SourceBean) transExec.executeQuery("QUERY_NEXTVAL_DICHIARAZIONE_NEET", null, "SELECT");
			if (rowKey == null) {
				throw new Exception("Impossibile leggere la sequence");
			}
			rowKey = (rowKey.containsAttribute("ROW") ? (SourceBean) rowKey.getAttribute("ROW") : rowKey);
			prgDichiarazione = (BigDecimal) rowKey.getAttribute("DO_NEXTVAL");

			ris = transExec.executeQuery("INSERT_DICHIARAZIONE_NEET_LAVORATORE", new Object[] { prgDichiarazione,
					new BigDecimal(cdnLavoratore), dataDichiarazione, cdnUtente, cdnUtente }, "INSERT");

			Boolean ret = ((Boolean) ris).booleanValue();

			if (!ret) {
				throw new Exception("impossibile inserire la dichiarazione neet");
			}

			SourceBean domandeNeetBean = (SourceBean) transExec.executeQuery("GET_DOMANDE_DICHIARAZIONE_NEET", null,
					"SELECT");
			Vector domandeNeet = domandeNeetBean.getAttributeAsVector("ROW");
			int numDomande = domandeNeet.size();

			for (int j = 0; j < numDomande; j++) {
				SourceBean domanda = (SourceBean) domandeNeet.get(j);
				String codDomanda = domanda.getAttribute("coddomandaneet").toString();
				String nomeCampoRisposta = "RISPOSTA" + codDomanda;
				String nomeCampoOsservazioni = "OSSERVAZIONI" + codDomanda;
				String strRisposta = StringUtils.getAttributeStrNotNull(request, nomeCampoRisposta);
				String strOsservazioni = StringUtils.getAttributeStrNotNull(request, nomeCampoOsservazioni);

				ris = transExec.executeQuery("INSERT_DICHIARAZIONE_RISPOSTE_NEET_LAVORATORE",
						new Object[] { prgDichiarazione, codDomanda, strRisposta, strOsservazioni }, "INSERT");

				ret = ((Boolean) ris).booleanValue();

				if (!ret) {
					throw new Exception("impossibile inserire la dichiarazione neet");
				}
			}

			transExec.commitTransaction();

			reportOperation.reportSuccess(MessageCodes.General.INSERT_SUCCESS);
		} catch (Exception ex) {
			if (transExec != null) {
				transExec.rollBackTransaction();
			}
			reportOperation.reportFailure(MessageCodes.General.INSERT_FAIL, ex, "execute()",
					"Fallito inserimento dichiarazione neet.");
			it.eng.sil.util.TraceWrapper.debug(_logger, "service(): Fallito inserimento dichiarazione neet.", ex);
		}
	}

}
