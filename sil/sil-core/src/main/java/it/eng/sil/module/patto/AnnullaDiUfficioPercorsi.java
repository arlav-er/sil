package it.eng.sil.module.patto;

import java.util.Vector;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.error.EMFInternalError;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;

public class AnnullaDiUfficioPercorsi extends AbstractSimpleModule {

	private static final long serialVersionUID = 0L;

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(AnnullaDiUfficioPercorsi.class.getName());

	public void service(SourceBean request, SourceBean response) throws SourceBeanException, EMFInternalError {
		ReportOperationResult reportOperation = new ReportOperationResult(this, response);

		Vector<String> azioniConcordate = request.getAttributeAsVector("checkbox_azione_concordata");
		TransactionQueryExecutor transExec = null;
		this.disableMessageIdSuccess();
		this.disableMessageIdFail();

		try {
			transExec = new TransactionQueryExecutor(getPool(), this);
			enableTransactions(transExec);
			transExec.initTransaction();

			for (String azioneConcordata : azioniConcordate) {

				Object lavParams[] = new Object[2];
				String[] pkAzioneConcordata = azioneConcordata.split("-");
				lavParams[0] = pkAzioneConcordata[0];
				lavParams[1] = pkAzioneConcordata[1];

				request.updAttribute("prgColloquio", lavParams[0]);
				request.updAttribute("prgPercorso", lavParams[1]);

				if (!doUpdate(request, response))
					throw new Exception("impossibile annullare di ufficio l'attività concordata");
			}

			transExec.commitTransaction();
			reportOperation.reportSuccess(MessageCodes.General.UPDATE_SUCCESS);
		} catch (Exception e) {
			reportOperation.reportFailure(MessageCodes.General.UPDATE_FAIL, e, "execute()",
					"annullamento di ufficio attività concordate fallito");
			if (transExec != null)
				transExec.rollBackTransaction();
			it.eng.sil.util.TraceWrapper.debug(_logger,
					"Errore in transazione annullamento di ufficio attività concordate", e);

		}
	}

}
