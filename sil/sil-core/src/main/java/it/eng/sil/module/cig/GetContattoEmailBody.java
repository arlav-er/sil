package it.eng.sil.module.cig;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.error.EMFInternalError;

import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;

/**
 * Recupero le informazioni necessarie alla costruzione del corpo della mail da inviare al contatto. Il modulo è
 * chiamato dopo l'inserimento in CI_CORSO e in CI_CORSO_CATALOGO.
 * 
 * @author uberti
 */
public class GetContattoEmailBody extends AbstractSimpleModule {

	private static final long serialVersionUID = -2808032455459344913L;

	private TransactionQueryExecutor transExec;

	public void service(SourceBean request, SourceBean response) throws Exception {

		try {
			transExec = new TransactionQueryExecutor(getPool());
			this.enableTransactions(transExec);
			transExec.initTransaction();
			boolean success = false;

			// recupero le informazioni per il corpo della mail, e le metto in response per il modulo insert contatto
			this.setSectionQuerySelect("QUERY_CONTATTO_EMAIL_BODY");
			SourceBean mailBody = this.doSelect(request, response);
			if (mailBody != null) {
				String referenteSede = (String) mailBody.getAttribute("ROW.REFERENTESEDE");
				response.updAttribute("REFERENTESEDE", referenteSede);
				String strSede = (String) mailBody.getAttribute("ROW.STRSEDE");
				response.updAttribute("STRSEDE", strSede);
				String lavoratore = (String) mailBody.getAttribute("ROW.LAVORATORE");
				response.updAttribute("LAVORATORE", lavoratore);
				String denominazione = (String) mailBody.getAttribute("ROW.DENOMINAZIONE");
				response.updAttribute("DENOMINAZIONE", denominazione);

				success = true;
			}

			if (success) {
				transExec.commitTransaction();
			} else {
				transExec.rollBackTransaction();
			}
		} catch (EMFInternalError e) {
			transExec.rollBackTransaction();
		}

	}

}
