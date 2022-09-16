package it.eng.sil.anag.module;

import java.math.BigDecimal;

/**
 * Classe che gestisce l'aggiornamento di un record del nucleo familiare collegato ad un utente.
 * Utilizzata dal modulo M_UpdateNucleoFamiliare
 * @author Giacomo Pandini
 */

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.CF_utils;
import it.eng.afExt.utils.CfException;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.util.blen.StringUtils;

public class UpdateNucleoFamiliare extends AbstractSimpleModule {
	public void service(SourceBean request, SourceBean response) throws Exception {
		boolean ret = false;
		int idSuccess = this.disableMessageIdSuccess();
		int idFail = this.disableMessageIdFail();
		TransactionQueryExecutor transExec = null;
		ReportOperationResult reportOperation = new ReportOperationResult(this, response);

		// Prende i campi necessari per la verifica del Codice fiscale
		String codiceFiscale = StringUtils.getAttributeStrNotNull(request, "CODICEFISCALE");
		String nome = StringUtils.getAttributeStrNotNull(request, "NOME");
		String cognome = StringUtils.getAttributeStrNotNull(request, "COGNOME");
		String dataNascita = StringUtils.getAttributeStrNotNull(request, "DATANASCITA");
		String codComuneNascita = StringUtils.getAttributeStrNotNull(request, "LUOGONASCITACOD");

		try {
			CF_utils.verificaParzialeCF(codiceFiscale, nome, cognome, dataNascita, codComuneNascita);
			transExec = new TransactionQueryExecutor(getPool());
			this.enableTransactions(transExec);

			request.delAttribute("NUMKLNUCLEOFAM");
			this.setSectionQuerySelect("QUERY_SELECT");
			SourceBean row = doSelect(request, response);
			String NUMKLNUCLEOFAM = String.valueOf(((BigDecimal) row.getAttribute("ROW.NUMKLNUCLEOFAM")).intValue());
			request.setAttribute("NUMKLNUCLEOFAM", NUMKLNUCLEOFAM);

			transExec.initTransaction();
			this.setSectionQueryUpdate("QUERY_UPDATE_NUCLEO_FAMILIARE");
			ret = doUpdate(request, response);

			if (!ret) {
				throw new Exception("impossibile aggiornare AN_NUCLEO_FAMILIARE in transazione");
			}

			transExec.commitTransaction();
			this.setMessageIdSuccess(MessageCodes.General.UPDATE_SUCCESS);
			reportOperation.reportSuccess(idSuccess);
		} catch (CfException cfEx) {
			response.setAttribute("SHOWMESSAGE", "1");
			reportOperation.reportSuccess(cfEx.getMessageIdFail());
		} catch (Exception e) {
			transExec.rollBackTransaction();
			this.setMessageIdFail(MessageCodes.General.UPDATE_FAIL);
			reportOperation.reportFailure(e, "service()", "aggiornamento in transazione");
		} finally {
		}
	}
}
