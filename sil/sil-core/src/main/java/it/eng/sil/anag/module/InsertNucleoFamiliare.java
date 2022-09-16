package it.eng.sil.anag.module;

import java.math.BigDecimal;

/**
 * Classe che gestisce l'inserimento di un record nel nucleo familiare collegato ad un utente.
 * Utilizzata dal modulo M_InsertNucleoFamiliare
 * @author Giacomo Pandini
 */

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.CF_utils;
import it.eng.afExt.utils.CfException;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.util.blen.StringUtils;

public class InsertNucleoFamiliare extends AbstractSimpleModule {
	public void service(SourceBean request, SourceBean response) throws Exception {
		boolean ret = false;
		int idSuccess = this.disableMessageIdSuccess();
		int idFail = this.disableMessageIdFail();
		TransactionQueryExecutor transExec = null;
		ReportOperationResult reportOperation = new ReportOperationResult(this, response);
		RequestContainer r = this.getRequestContainer();

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

			transExec.initTransaction();

			BigDecimal prgNucleoFamiliare = doNextVal(request, response);

			if (prgNucleoFamiliare == null) {
				throw new Exception("Impossibile leggere S_CM_NUCLEO_FAMILIARE.NEXTVAL");
			}

			request.delAttribute("PRGNUCLEOFAMILIARE");
			request.setAttribute("PRGNUCLEOFAMILIARE", prgNucleoFamiliare);

			this.setSectionQueryInsert("QUERY_INSERT_NUCLEOFAMILIARE");
			ret = doInsert(request, response);

			if (!ret) {
				throw new Exception("impossibile inserire in AN_NUCLEO_FAMILIARE in transazione");
			}
			r.setServiceRequest(request);

			transExec.commitTransaction();

			response.delAttribute("PRGNUCLEOFAMILIARE");
			response.setAttribute("PRGNUCLEOFAMILIARE", prgNucleoFamiliare);

			reportOperation.reportSuccess(idSuccess);
		} catch (CfException cfEx) {
			response.setAttribute("SHOWMESSAGE", "1");
			reportOperation.reportSuccess(cfEx.getMessageIdFail());
		} catch (Exception e) {
			transExec.rollBackTransaction();
			// this.setMessageIdFail(MessageCodes.General.INSERT_FAIL);
			reportOperation.reportFailure(MessageCodes.General.INSERT_FAIL, e, "services()", "insert in transazione");
		} finally {
		}
	}
}
