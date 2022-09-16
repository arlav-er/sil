/**
 * @author rolfini
 */
package it.eng.sil.module.presel;

import java.util.ArrayList;
import java.util.StringTokenizer;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;

public class SalvaVisEvidenza extends AbstractSimpleModule {
	public void service(SourceBean request, SourceBean response) throws Exception {

		// 1) cancello prima tutti i record di visibilita' dell'evidenza
		// selezionata
		// 2) inserisco tutti i record di visibilità selezionati
		// tutto questo (ovviamente) in transazione
		ReportOperationResult reportOperation = new ReportOperationResult(this, response);
		int idSuccess = this.disableMessageIdSuccess();
		int idFail = this.disableMessageIdFail();
		TransactionQueryExecutor transExec = null;

		// recupero il parametro che contiene tutte le coppie
		// profilo, gruppo selezionate
		String strProfGruppi = (String) request.getAttribute("profGruppi");
		ArrayList coppiaPG = new ArrayList(2);

		try {

			transExec = new TransactionQueryExecutor(getPool(), this);
			this.enableTransactions(transExec);

			// cancello tutti i record selezionati
			transExec.initTransaction();
			setSectionQueryDelete("QUERY_DELETE");
			doDelete(request, response);

			setSectionQueryInsert("QUERY_SAVE");
			StringTokenizer sttCoppie = new StringTokenizer(strProfGruppi, ",");
			while (sttCoppie.hasMoreTokens()) {
				coppiaPG = getCoppia(sttCoppie.nextToken());
				setKeyinRequest("CDNPROFILO", coppiaPG.get(0), request);
				setKeyinRequest("CDNGRUPPO", coppiaPG.get(1), request);
				doInsert(request, response);
			}

			transExec.commitTransaction();
			reportOperation.reportSuccess(idSuccess);
		} catch (Exception ex) {
			transExec.rollBackTransaction();
			ex.printStackTrace();
		}

	}

	/**
	 * Data una coppia (profilo, gruppo) separata dal carattere '_' la procedura restituisce un arraylist di due
	 * elementi contenenti un elemento della coppia ognuno.
	 * 
	 * @param String
	 *            coppia: stringa che contiene la coppia
	 * @return ArrayList di due elementi (indice 0: profilo, indice 1: gruppo)
	 */
	private ArrayList getCoppia(String coppia) {
		int underscore;
		ArrayList coppiaPG = new ArrayList(2);
		String profilo = "";
		String gruppo = "";

		underscore = coppia.indexOf('_');

		if (underscore != -1) {
			profilo = coppia.substring(0, underscore);
			gruppo = coppia.substring(underscore + 1, coppia.length());
			coppiaPG.add(profilo);
			coppiaPG.add(gruppo);
			return coppiaPG;
		}

		return null;

	}

	private void setKeyinRequest(String keyName, Object key, SourceBean request) throws Exception {
		if (request.getAttribute(keyName) != null) {
			request.delAttribute(keyName);
		}
		request.setAttribute(keyName, key);
	}

}
