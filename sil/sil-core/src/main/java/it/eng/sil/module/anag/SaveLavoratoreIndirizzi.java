package it.eng.sil.module.anag;

import java.math.BigDecimal;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.MessageCodes; //di errore:"dati salvati corrrettamente" "..erroneamente" etc.
import it.eng.afExt.utils.ReportOperationResult; //Servono per per gestire i messaggi
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;

public class SaveLavoratoreIndirizzi extends AbstractSimpleModule {
	public void service(SourceBean request, SourceBean response) throws Exception {
		// doUpdate(request, response);
		// System.out.println("InsertColloquio chiamato");
		// if (1==1) return ;
		boolean ret = false;
		int idSuccess = this.disableMessageIdSuccess();
		int idFail = this.disableMessageIdFail();
		TransactionQueryExecutor transExec = null;
		ReportOperationResult reportOperation = new ReportOperationResult(this, response);

		try {
			transExec = new TransactionQueryExecutor(getPool());
			this.enableTransactions(transExec);

			//
			transExec.initTransaction();

			this.setSectionQueryUpdate("QUERY_SAVE_AN_LAVORATOREINDIRIZZI");
			ret = doUpdate(request, response);

			if (!ret) {
				throw new Exception("impossibile aggiornare AN_LAVORATORE in transazione");
			}

			// devo rileggere il numkloLavStoriaInf
			this.setSectionQuerySelect("QUERY_SELECT");

			SourceBean numklo = doSelect(request, response);

			BigDecimal numkloLavStoriaInf = (BigDecimal) numklo.getAttribute("ROW.NUMKLOLAVSTORIAINF");

			if (numkloLavStoriaInf != null) {
				numkloLavStoriaInf = numkloLavStoriaInf.add(new BigDecimal("1"));
				request.delAttribute("numKloLavStoriaInf");
				request.setAttribute("numKloLavStoriaInf", numkloLavStoriaInf.toString());
			}

			// ///////////////////////////
			setCodCpi(request, response);

			// ///////////////////////////
			this.setSectionQueryUpdate("QUERY_UPDATE_AN_LAV_S");
			ret = doUpdate(request, response);

			if (!ret) {
				throw new Exception("impossibile aggiornare AN_LAV_STORIA_INF in transazione");
			}

			transExec.commitTransaction();

			// this.setMessageIdSuccess(MessageCodes.General.INSERT_SUCCESS);
			reportOperation.reportSuccess(idSuccess);
		} catch (Exception e) {
			transExec.rollBackTransaction();

			// this.setMessageIdFail(MessageCodes.General.INSERT_FAIL);
			reportOperation.reportFailure(MessageCodes.General.INSERT_FAIL, e, "services()", "update in transazione");
		} finally {
		}
	}

	/**
	 * Nel caso in cui la residenza del lavoratore sia uguale al domicilio debbo valorizzare il parametro codcpi per la
	 * successiva update della tabella am_lav_storia_inf (.codCpiTit) Il parametro codCPIifDOMeqRESHid viene valorizzato
	 * dalla funzione AggiornaForm(..) della pagina FindComune.jsp
	 */
	private void setCodCpi(SourceBean request, SourceBean response) {
		try {
			if (((String) request.getAttribute("CODCOMDOM")).equals("")
					&& ((String) request.getAttribute("CODCPI")).equals("")) {
				String codCpiResidenza = (String) request.getAttribute("codCPIifDOMeqRESHid");

				if (codCpiResidenza.length() > 0) {
					request.delAttribute("CODCPI");
					request.setAttribute("CODCPI", codCpiResidenza);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
