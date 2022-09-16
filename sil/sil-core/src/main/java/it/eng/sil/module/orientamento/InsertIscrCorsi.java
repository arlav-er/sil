package it.eng.sil.module.orientamento;

import java.util.Collection;
import java.util.Iterator;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.PercorsoConcordatoException;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.module.patto.InsertPercorso;

public class InsertIscrCorsi extends AbstractSimpleModule {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(InsertPercorso.class.getName());

	public void service(SourceBean request, SourceBean response) throws Exception {

		Boolean ris = new Boolean(false);
		boolean ret = false;
		boolean resultIns = false;
		int idSuccess = this.disableMessageIdSuccess();

		String codice = "";// Utils.notNull(request.getAttribute("codice"));
		String[] tempListprg = null;// codice.split(DELIMETER);
		String strprgColloquio = "";// tempListprg[0];
		String strprgPercorso = "";// tempListprg[1];
		String attributeValueColloquioPercorso = "";

		int j = 0;
		Object obj[] = new Object[6];
		// int idFail = this.disableMessageIdFail();
		TransactionQueryExecutor transExec = null;
		ReportOperationResult reportOperation = new ReportOperationResult(this, response);
		RequestContainer r = this.getRequestContainer();
		try {
			transExec = new TransactionQueryExecutor(getPool());
			this.enableTransactions(transExec);
			String DELIMETER = "-";
			transExec.initTransaction();

			/*
			 * Collection elencoIscrLav = AbstractSimpleModule.getArgumentValues( request, "codice");
			 */
			try {

				Collection elencoIscrLav = AbstractSimpleModule.getArgumentValues(request, "codici_percorso_col");
				obj[j++] = request.getAttribute("prgcorso");
				obj[j++] = ""; // colloquio
				obj[j++] = ""; // percorso
				obj[j++] = getRequestContainer().getSessionContainer().getAttribute("_CDUT_");
				obj[j++] = getRequestContainer().getSessionContainer().getAttribute("_CDUT_");
				obj[j++] = request.getAttribute("codesitocorso");

				for (Iterator iter = elencoIscrLav.iterator(); iter.hasNext();) {
					try {
						transExec = new TransactionQueryExecutor(getPool(), this);
						enableTransactions(transExec);
						transExec.initTransaction();
						attributeValueColloquioPercorso = (String) iter.next();
						tempListprg = attributeValueColloquioPercorso.split(DELIMETER);
						strprgColloquio = tempListprg[0];
						strprgPercorso = tempListprg[1];

						if (strprgColloquio != null && strprgPercorso != null) {
							obj[1] = strprgColloquio;
							obj[2] = strprgPercorso;

							ris = (Boolean) transExec.executeQuery("INS_ISCR_CORSO", obj, "INSERT");

							if (!ris.booleanValue())
								throw new Exception("errore nell'inserimento dei lavoratori nel corso");

							r.setServiceRequest(request);
							transExec.commitTransaction();
							resultIns = true;
							// _logger.debug("errore nell'inserimento dei lavoratori nel corso ");

						} else {
							throw new Exception("errore nell'inserimento dei lavoratori nel corso");
						}
					} catch (PercorsoConcordatoException tEx) {
						reportOperation.reportFailure(((PercorsoConcordatoException) tEx).getMessageIdFail());
						if (transExec != null)
							transExec.rollBackTransaction();
					} catch (Exception tEx) {
						reportOperation.reportFailure(MessageCodes.General.INSERT_FAIL);
						it.eng.sil.util.TraceWrapper.debug(_logger,
								"service(): errore nell'inserimento dei lavoratori nel corso.", tEx);
						if (transExec != null)
							transExec.rollBackTransaction();
					}
				}
				if (resultIns)
					reportOperation.reportSuccess(MessageCodes.General.INSERT_SUCCESS);

			} catch (Exception ex) {
				reportOperation.reportFailure(MessageCodes.General.INSERT_FAIL, ex, "execute()",
						"Fallito inserimento dei lavoratori nel corso.");
				it.eng.sil.util.TraceWrapper.debug(_logger, "service(): Fallito inserimento dei lavoratori nel corso.",
						ex);

			}

		} finally {
		}
	}

}
