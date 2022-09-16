package it.eng.sil.module.trento;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.module.movimenti.constant.Properties;
import it.eng.sil.security.User;

public class MSalvaStampaParamLav extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(MSalvaStampaParamLav.class.getName());

	public MSalvaStampaParamLav() {
	}

	public void service(SourceBean request, SourceBean response) throws Exception {
		ReportOperationResult reportOperation = new ReportOperationResult(this, response);

		// Segnalazione soli errori/problemi
		int idSuccess = this.disableMessageIdSuccess();
		int idFail = this.disableMessageIdFail();
		int errorCode = MessageCodes.General.INSERT_FAIL;
		TransactionQueryExecutor trans = null;
		String flgFirmaGrafo = null;
		String cdnLavoratore = null;
		String prgAzienda = null;
		String prgUnita = null;
		boolean result = true;
		User user = null;
		try {
			user = (User) getRequestContainer().getSessionContainer().getAttribute(User.USERID);
			String codCpi = user.getCodRif();
			cdnLavoratore = request.containsAttribute("CDNLAVORATORE")
					? request.getAttribute("CDNLAVORATORE").toString()
					: "";
			prgAzienda = request.containsAttribute("PRGAZIENDA") ? request.getAttribute("PRGAZIENDA").toString() : "";
			prgUnita = request.containsAttribute("PRGUNITA") ? request.getAttribute("PRGUNITA").toString() : "";

			trans = new TransactionQueryExecutor(getPool());
			this.enableTransactions(trans);
			trans.initTransaction();

			request.delAttribute("NOMETEMPLATEDESC");
			request.delAttribute("CODTIPODOCUMENTO");
			setSectionQuerySelect("QUERY_SELECT_TEMPLATE");
			SourceBean rowTemplate = doSelect(request, response, false);
			Object nomeTemplate = rowTemplate.getAttribute("ROW.STRNOME");
			Object codTipoDoc = rowTemplate.getAttribute("ROW.CODAMBITOTEM");
			flgFirmaGrafo = (String) rowTemplate.getAttribute("ROW.FLGFIRMAGRAFO");
			request.setAttribute("NOMETEMPLATEDESC", nomeTemplate);
			request.setAttribute("CODTIPODOCUMENTO", codTipoDoc);

			if (request.containsAttribute("FLGCODMONOIO")
					&& request.getAttribute("FLGCODMONOIO").toString().equalsIgnoreCase("N")) {
				request.delAttribute("FLGCODMONOIO");
			}
			request.delAttribute("PRGDOCUMENTO");
			setSectionQuerySelect("QUERY_NEXT_VAL");
			SourceBean rowDoc = doSelect(request, response, false);
			Object progressivoDoc = rowDoc.getAttribute("ROW.KEY");
			request.setAttribute("PRGDOCUMENTO", progressivoDoc);

			request.delAttribute("PRGDOCUMENTOCOLL");
			setSectionQuerySelect("QUERY_NEXT_VAL_COLL");
			SourceBean rowDocColl = doSelect(request, response, false);
			Object progressivoDocColl = rowDocColl.getAttribute("ROW.KEY");
			request.setAttribute("PRGDOCUMENTOCOLL", progressivoDocColl);

			// AM_DOCUMENTO
			request.setAttribute("CODCPI", codCpi);
			setSectionQueryInsert("QUERY_INSERT_DOC");
			result = doInsert(request, response);
			if (!result) {
				trans.rollBackTransaction();
				reportOperation.reportFailure(errorCode);
				return;
			}

			setSectionQuerySelect("QUERY_SELECT_COMPONENTE");
			SourceBean rowComponente = doSelect(request, response, false);
			Object cdnComponente = rowComponente.getAttribute("ROW.CDNCOMPONENTE");

			// AM_DOCUMENTO_COLL
			request.setAttribute("CDNCOMPONENTE", cdnComponente);
			setSectionQueryInsert("QUERY_INSERT_DOC_COLL");
			result = doInsert(request, response);
			if (!result) {
				trans.rollBackTransaction();
				reportOperation.reportFailure(errorCode);
				return;
			}

			// AM_DOCUMENTO_BLOB
			request.delAttribute("PRGDOCUMENTOBLOB");
			setSectionQuerySelect("QUERY_NEXT_VAL_BLOB");
			SourceBean rowDocBlob = doSelect(request, response, false);
			Object progressivoDocBlob = rowDocBlob.getAttribute("ROW.KEY");
			request.setAttribute("PRGDOCUMENTOBLOB", progressivoDocBlob);
			setSectionQueryInsert("QUERY_INSERT_DOC_BLOB");
			result = doInsert(request, response);
			if (!result) {
				trans.rollBackTransaction();
				reportOperation.reportFailure(errorCode);
				return;
			}

			trans.commitTransaction();
			response.setAttribute("PRGDOCUMENTO", progressivoDoc);
		} catch (Throwable ex) {
			if (trans != null) {
				trans.rollBackTransaction();
			}
			it.eng.sil.util.TraceWrapper.debug(_logger, "MSalvaStampaParamLav.service()", ex);
			reportOperation.reportFailure(errorCode);
		}

		if (flgFirmaGrafo != null && flgFirmaGrafo.equals(Properties.FLAG_1)) {
			// Consenso consensoLav = (Consenso)getRequestContainer().getSessionContainer().getAttribute("CONSENSO_" +
			// cdnLavoratore);
			// if (consensoLav != null) {
			// String codStatoConsenso = consensoLav.getCodice();
			// if (codStatoConsenso.equalsIgnoreCase(Consenso.ASSENTE)) {
			// response.setAttribute("CONSENSOASSENTE", "true");
			// ProfileDataFilter filterConsenso = new ProfileDataFilter(user, "HomeConsensoPage");
			// Vector param = new Vector();
			// if(filterConsenso.canView()){
			// param.add("<a href=\"#\" name=\"btnGestioneConsenso\" onClick=\"apriGestioneConsenso()\" >\"Gestione
			// Consenso\"</a>");
			//
			// }else{
			// param.add("\"Gestione Consenso\"");
			// }
			// MessageAppender.appendMessage(response, MessageCodes.FirmaGrafometrica.CONSENSO_ASSENTE, param);
			// }
			// else {
			// if (codStatoConsenso.equalsIgnoreCase(Consenso.NON_DISPONIBILE)) {
			// reportOperation.reportSuccess(MessageCodes.FirmaGrafometrica.CONSENSO_NON_DISPONIBILE);
			// }
			// else {
			// if (codStatoConsenso.equalsIgnoreCase(Consenso.REVOCATO)) {
			// reportOperation.reportSuccess(MessageCodes.FirmaGrafometrica.CONSENSO_REVOCATO);
			// }
			// else {
			// reportOperation.reportSuccess(MessageCodes.General.OPERATION_SUCCESS);
			// }
			// }
			// }
			// }
			// else {
			// // Oggetto Consenso non presente in sessione
			// reportOperation.reportSuccess(MessageCodes.FirmaGrafometrica.CONSENSO_NON_DISPONIBILE);
			// }
		} else {
			reportOperation.reportSuccess(MessageCodes.General.OPERATION_SUCCESS);
		}
	}

}
