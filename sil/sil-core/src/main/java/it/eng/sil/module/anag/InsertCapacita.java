/*
 * Creato il 31-ago-06
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.module.anag;

import java.math.BigDecimal;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

/**
 * @author melandri
 * 
 * Per modificare il modello associato al commento di questo tipo generato,
 * aprire Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e
 * commenti
 */
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.util.Utils;

public class InsertCapacita extends AbstractSimpleModule {
	public void service(SourceBean request, SourceBean response) throws Exception {
		// System.out.println("InsertColloquio chiamato");
		// if (1==1) return ;
		boolean ret = false;
		int idSuccess = this.disableMessageIdSuccess();
		int idFail = this.disableMessageIdFail();
		TransactionQueryExecutor transExec = null;
		ReportOperationResult reportOperation = new ReportOperationResult(this, response);
		int msgErr = MessageCodes.General.INSERT_FAIL;

		try {

			transExec = new TransactionQueryExecutor(getPool());
			this.enableTransactions(transExec);

			transExec.initTransaction();

			int numeroCapacita = Integer.valueOf("" + request.getAttribute("numeroCapacita")).intValue();

			int k = 0;

			// ----------
			int p = 0;
			// ----------

			String codGradoCapacita = "";
			for (int i = 0; i < numeroCapacita; i++) {

				String j = Integer.toString(i);

				codGradoCapacita = "" + Utils.notNull(request.getAttribute("capCogn_" + j));

				if ("".equalsIgnoreCase(codGradoCapacita)) {
					String nota = Utils.notNull(request.getAttribute("descrGrado_" + j));
					if (!nota.equalsIgnoreCase(""))
						codGradoCapacita = "S";
				}

				String codCapacita = "" + request.getAttribute("codDescr_" + j);

				request.delAttribute("codGradoCapacita");
				request.setAttribute("codGradoCapacita", codGradoCapacita);

				request.delAttribute("codCapacita");
				request.setAttribute("codCapacita", codCapacita);

				// ----------
				String strDescGrado = "" + Utils.notNull(request.getAttribute("descrGrado_" + j));

				request.delAttribute("strDescGrado");
				request.setAttribute("strDescGrado", strDescGrado);
				// ----------

				setSectionQuerySelect("QUERY_SELECT_CODCAPACITA");
				SourceBean row = doSelect(request, response);

				if (row.getAttribute("ROW") != null) { // Aggiornamento

					request.delAttribute("prgCapacitaLav");
					BigDecimal prgCapacitaLav = (BigDecimal) row.getAttribute("ROW.prgCapacitaLav");
					request.setAttribute("prgCapacitaLav", prgCapacitaLav.toString());

					if (codGradoCapacita.equals("")) {
						setSectionQueryDelete("QUERY_DELETE_CAPACITA");
						ret = doDelete(request, response);
					} else {
						setSectionQueryUpdate("QUERY_UPDATE_CAPACITA");
						ret = doUpdate(request, response);
					}

					if (!ret) {
						msgErr = MessageCodes.General.UPDATE_FAIL;
						throw new Exception("Impossibile eseguire l'aggiornamento su CM_CAPACITA");
					}

					// ------------------------------------------------------------
					BigDecimal cdnUtMod = (BigDecimal) RequestContainer.getRequestContainer().getSessionContainer()
							.getAttribute("_CDUT_");
					request.delAttribute("cdnUtMod");
					request.setAttribute("cdnUtMod", cdnUtMod);

					String numKloDiagnosiFunzionale = "" + request.getAttribute("numKloDiagnosiFunzionale");
					request.delAttribute("numKloDiagnosiFunzionale");
					request.setAttribute("numKloDiagnosiFunzionale", numKloDiagnosiFunzionale);

					if (k == 0) {
						setSectionQueryUpdate("QUERY_UPDATE_DIAGNOSI_FUNZ");
						ret = doUpdate(request, response);

						if (!ret) {
							throw new Exception("Impossibile Aggiornare l'utente");
						}
					}
					// ------------------------------------------------------------

				} else if (!codGradoCapacita.equals("") && !codGradoCapacita.equals("null")) { // Inserimento

					BigDecimal prgCapacitaLav = doNextVal(request, response);

					if (prgCapacitaLav == null) {
						throw new Exception("Impossibile leggere S_CM_CAPACITA.NEXTVAL");
					}

					request.delAttribute("codGradoCapacita");
					request.setAttribute("codGradoCapacita", codGradoCapacita);

					request.delAttribute("codCapacita");
					request.setAttribute("codCapacita", codCapacita);

					request.delAttribute("prgCapacitaLav");
					request.setAttribute("prgCapacitaLav", prgCapacitaLav.toString());

					// ----------
					request.delAttribute("strDescGrado");
					request.setAttribute("strDescGrado", strDescGrado.toString());
					// ----------

					this.setSectionQueryInsert("QUERY_INSERT_CAPACITA");
					ret = doInsert(request, response);

					if (!ret) {
						throw new Exception("impossibile inserire in CM_CAPACITA in transazione");
					}

					// ------------------------------------------------------------
					BigDecimal cdnUtMod = (BigDecimal) RequestContainer.getRequestContainer().getSessionContainer()
							.getAttribute("_CDUT_");
					request.delAttribute("cdnUtMod");
					request.setAttribute("cdnUtMod", cdnUtMod);

					String numKloDiagnosiFunzionale = "" + request.getAttribute("numKloDiagnosiFunzionale");
					request.delAttribute("numKloDiagnosiFunzionale");
					request.setAttribute("numKloDiagnosiFunzionale", numKloDiagnosiFunzionale);

					if (k == 0) {
						setSectionQueryUpdate("QUERY_UPDATE_DIAGNOSI_FUNZ");
						ret = doUpdate(request, response);

						if (!ret) {
							throw new Exception("Impossibile Aggiornare l'utente");
						}
					}
					// ------------------------------------------------------------

				}
				k++;
			}
			transExec.commitTransaction();

			reportOperation.reportSuccess(idSuccess);

		} catch (Exception e) {
			transExec.rollBackTransaction();
			reportOperation.reportFailure(msgErr, e, "services()", "insert in transazione");
		} finally {
		}
	}
}
