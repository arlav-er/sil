package it.eng.sil.module.presel;

import java.math.BigDecimal;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.module.patto.PattoFacade;
import it.eng.sil.module.patto.PattoManager;
import it.eng.sil.util.collectionexecutor.Action;
import it.eng.sil.util.collectionexecutor.ActionException;

public class GetPrgMansioni extends AbstractSimpleModule {

	BigDecimal prgMansioniGlobal;

	class InsertEspLavAction implements Action {

		/**
		 * Esegue l'action. L'action è rappresentata dall'esecuzione del metodo doInsertEspLav di InsertEspLav, che
		 * quindi funziona da delegato.
		 * 
		 * @param request
		 * @param response
		 * @exception ActionException
		 *                Invocata se si verifica un problema durante l'esecuzione dell'action
		 * @exception SourceBeanException
		 */
		public void execute(SourceBean request, SourceBean response) throws ActionException, SourceBeanException {

			if (doInsertEspLav(request, response) == false) {

				// Se si è verificato un problema, in questo modo il
				// CollectionExecutor
				// viene avvertito.
				throw new ActionException();
			}
		}
	}

	public void service3(SourceBean request, SourceBean response) throws Exception {
		PattoFacade facade = new PattoFacade();
		if (facade.withPatto(request)) {

			InsertEspLavAction insertEspLav = new InsertEspLavAction();
			facade.doInsert(this, getPool(), request, response, "PRGESPLAVORO", insertEspLav);

		} else {

			doInsertEspLav(request, response);
		}
	}

	private boolean doInsertEspLav(SourceBean request, SourceBean response) {

		if (doInsert(request, response)) {
			int messageIdSuccess = this.disableMessageIdSuccess();
			int messageIdFail = this.disableMessageIdFail();
			// doUpdate(request, response);
			this.setMessageIdSuccess(messageIdSuccess);
			this.setMessageIdFail(messageIdFail);

			return true;
		}

		return false;
	}

	public void service2(SourceBean request, SourceBean response) throws Exception {
		boolean ret = false;
		if (PattoManager.withPatto(request)) {
			ReportOperationResult reportOperation = new ReportOperationResult(this, response);
			TransactionQueryExecutor transExec = null;
			int idSuccess = this.disableMessageIdSuccess();
			int idFail = this.disableMessageIdFail();

			try {
				transExec = new TransactionQueryExecutor(getPool());

				PattoManager patto = new PattoManager(this, transExec);
				transExec.initTransaction();

				BigDecimal prgMansione = getPrgMansione(request, response);
				setParameterForMansione(prgMansione, request);

				// this.setSectionQueryInsert("INSERT_IND_T");
				this.setSectionQuerySelect("QUERY_SELECT");
				this.setSectionQueryInsert("QUERY_INSERT");
				ret = this.doInsertNoDuplicate(request, response);

				if (ret) {
					if (request.getAttribute("PRG_TAB") != null) {
						request.delAttribute("PRG_TAB");
					}

					request.setAttribute("PRG_TAB", prgMansione);
					ret = patto.execute(request, response);
				} else {
					throw new Exception("");
				}

				//
				if (ret) {
					transExec.commitTransaction();
					this.setMessageIdSuccess(idSuccess);
					this.setMessageIdFail(idFail);
					reportOperation.reportSuccess(idSuccess);
				} else {
					throw new Exception();
				}
			} catch (Exception e) {
				// this.setMessageIdFail(MessageCodes.General.INSERT_FAIL);
				reportOperation.reportFailure(MessageCodes.General.INSERT_FAIL);

				if (transExec != null) {
					transExec.rollBackTransaction();
				}
			}
		} else {
			int idSuccess = this.disableMessageIdSuccess();
			BigDecimal prgMansione = getPrgMansione(request, response);

			if (prgMansione != null) {
				setParameterForMansione(prgMansione, request);
				this.setMessageIdSuccess(idSuccess);
				this.setSectionQuerySelect("QUERY_SELECT");
				doInsertNoDuplicate(request, response);
			}
		}

	}

	private BigDecimal getPrgMansione(SourceBean request, SourceBean response) throws Exception {
		this.setSectionQuerySelect("QUERY_SEQUENCE");

		SourceBean beanPrgMansione = (SourceBean) doSelect(request, response);

		return prgMansioniGlobal = (BigDecimal) beanPrgMansione.getAttribute("ROW.PRGMANSIONE");
	}

	private void setParameterForMansione(BigDecimal prgMansione, SourceBean request) throws Exception {
		if (request.getAttribute("PRGMANSIONE") != null) {
			request.delAttribute("PRGMANSIONE");
		}

		request.setAttribute("PRGMANSIONE", prgMansione);
	}

	public void service(SourceBean request, SourceBean response) throws Exception {

		String StringSize = ((String) request.getAttribute("SIZE"));
		String check = "";
		String check1 = "";
		BigDecimal prgMansione;
		String prgMovimento = "";
		BigDecimal esplavoro;
		Object obj = null;

		if ((StringSize != null && !StringSize.equals(""))) {
			int SIZE = new Integer(StringSize).intValue();
			for (int i = 0; i < SIZE; i++) {
				/*
				 * A partire dal codice e dal lavoratore ricavo il prgMansione dalla PR_MANSIONE
				 */
				request.getAttribute("CODMANSIONE_" + i);/*
															 * ?? a che serve questa riga??
															 */
				request.delAttribute("CODMANSIONE");
				request.setAttribute("CODMANSIONE", request.getAttribute("CODMANSIONE_" + i));
				setSectionQuerySelect("QUERY_GET");

				SourceBean row2 = doSelect(request, response);
				prgMansione = (BigDecimal) row2.getAttribute("ROW.PRGMANSIONE");
				check = (String) request.getAttribute("I_PR_MAN" + i);
				if (check != null && check.equals("on")) {
					if (prgMansione != null && !prgMansione.equals("")) {
						request.delAttribute("PRGMANSIONE");
						request.setAttribute("PRGMANSIONE", prgMansione);
						request.delAttribute("I_PR_MAN");
						request.setAttribute("I_PR_MAN", request.getAttribute("I_PR_MAN" + i));
						request.delAttribute("CODMANSIONE");
						request.setAttribute("CODMANSIONE", request.getAttribute("CODMANSIONE_" + i));
						request.delAttribute("CODCONTRATTO");
						request.setAttribute("CODCONTRATTO", request.getAttribute("CODCONTRATTO_" + i));

						request.delAttribute("NUMANNOINIZIO");
						request.setAttribute("NUMANNOINIZIO", request.getAttribute("NUMANNOINIZIO_" + i));
						request.delAttribute("NUMMESEINIZIO");
						request.delAttribute("NUMMESEFINE");
						request.delAttribute("NUMANNOFINE");
						obj = request.getAttribute("NUMMESEINIZIO_" + i);
						if (obj != null)
							request.setAttribute("NUMMESEINIZIO", obj);
						obj = request.getAttribute("NUMMESEFINE_" + i);
						if (obj != null)
							request.setAttribute("NUMMESEFINE", obj);
						obj = request.getAttribute("NUMANNOFINE_" + i);
						if (obj != null)
							request.setAttribute("NUMANNOFINE", obj);

						request.delAttribute("FLGDISPONIBILE");
						request.setAttribute("FLGDISPONIBILE", request.getAttribute("FLGDISPONIBILE_" + i));
						request.delAttribute("desMansione");
						request.setAttribute("desMansione", request.getAttribute("desMansione_" + i));

						request.delAttribute("STRRAGSOCIALEAZIENDA");
						request.delAttribute("STRCODFISCALEAZIENDA");
						request.delAttribute("STRPARTITAIVAAZIENDA");
						request.delAttribute("CODNATGIURIDICA");
						request.delAttribute("CODATECO");
						request.delAttribute("CODCOMAZIENDA");
						request.delAttribute("STRINDIRIZZOAZIENDA");
						request.delAttribute("NUMMESI");

						obj = request.getAttribute("STRRAGSOCIALEAZIENDA_" + i);
						if (obj != null)
							request.setAttribute("STRRAGSOCIALEAZIENDA", obj);
						obj = request.getAttribute("STRCODFISCALEAZIENDA_" + i);
						if (obj != null)
							request.setAttribute("STRCODFISCALEAZIENDA", obj);
						obj = request.getAttribute("STRPARTITAIVAAZIENDA_" + i);
						if (obj != null)
							request.setAttribute("STRPARTITAIVAAZIENDA", obj);
						obj = request.getAttribute("CODNATGIURIDICA_" + i);
						if (obj != null)
							request.setAttribute("CODNATGIURIDICA", obj);
						obj = request.getAttribute("CODATECO_" + i);
						if (obj != null)
							request.setAttribute("CODATECO", obj);
						obj = request.getAttribute("CODCOMAZIENDA_" + i);
						if (obj != null)
							request.setAttribute("CODCOMAZIENDA", obj);
						obj = request.getAttribute("STRINDIRIZZOAZIENDA_" + i);
						if (obj != null)
							request.setAttribute("STRINDIRIZZOAZIENDA", obj);
						obj = request.getAttribute("NUMMESI_" + i);
						if (obj != null)
							request.setAttribute("NUMMESI", obj);

						setSectionQuerySelect("QUERY_SEQUENCE_ESP_LAV");
						SourceBean row1 = doSelect(request, response);

						esplavoro = (BigDecimal) row1.getAttribute("ROW.PRGESPLAVORO");
						request.delAttribute("PRGESPLAVORO");
						request.setAttribute("PRGESPLAVORO", esplavoro);
						setSectionQueryInsert("QUERY_INSERT_ESP_LAV");

						service3(request, response);

						request.delAttribute("PRGMOVIMENTO");
						request.setAttribute("PRGMOVIMENTO", request.getAttribute("PRGMOVIMENTO_" + i));
						prgMovimento = (String) request.getAttribute("PRGMOVIMENTO");
						if (prgMovimento != null && !prgMovimento.equals("")) {
							setSectionQueryInsert("QUERY_INSERT_ASS_ESP_MOVI");
							doInsert(request, response);
						}

					} // end if
					else {

						request.delAttribute("NUMANNOINIZIO");
						request.setAttribute("NUMANNOINIZIO", request.getAttribute("NUMANNOINIZIO_" + i));
						request.delAttribute("I_PR_MAN");
						request.setAttribute("I_PR_MAN", request.getAttribute("I_PR_MAN" + i));
						request.delAttribute("FLGDISPONIBILE");
						request.setAttribute("FLGDISPONIBILE", request.getAttribute("FLGDISPONIBILE_" + i));
						request.delAttribute("CODMANSIONE");
						request.setAttribute("CODMANSIONE", request.getAttribute("CODMANSIONE_" + i));
						setSectionQueryInsert("QUERY_INSERT_MANSIONE");
						service2(request, response);

						request.delAttribute("PRGMANSIONE");
						request.setAttribute("PRGMANSIONE", prgMansioniGlobal);
						request.delAttribute("CODCONTRATTO");
						request.setAttribute("CODCONTRATTO", request.getAttribute("CODCONTRATTO_" + i));

						request.delAttribute("NUMANNOINIZIO");
						request.setAttribute("NUMANNOINIZIO", request.getAttribute("NUMANNOINIZIO_" + i));
						request.delAttribute("NUMMESEINIZIO");
						obj = request.getAttribute("NUMMESEINIZIO_" + i);
						if (obj != null)
							request.setAttribute("NUMMESEINIZIO", obj);

						// Modifica D'Auria Giovanni 19/05/2005 inizio
						// ho aggiunto queste righe perché nel caso di
						// prgmansione = null
						// non venivano settati correttamente il mese e l'anno
						// di fine mov
						request.delAttribute("NUMMESEFINE");
						request.delAttribute("NUMANNOFINE");
						obj = request.getAttribute("NUMMESEFINE_" + i);
						if (obj != null)
							request.setAttribute("NUMMESEFINE", obj);
						obj = request.getAttribute("NUMANNOFINE_" + i);
						if (obj != null)
							request.setAttribute("NUMANNOFINE", obj);
						// fine

						request.delAttribute("desMansione");
						request.setAttribute("desMansione", request.getAttribute("desMansione_" + i));

						request.delAttribute("STRRAGSOCIALEAZIENDA");
						request.delAttribute("STRCODFISCALEAZIENDA");
						request.delAttribute("STRPARTITAIVAAZIENDA");
						request.delAttribute("CODNATGIURIDICA");
						request.delAttribute("CODATECO");
						request.delAttribute("CODCOMAZIENDA");
						request.delAttribute("STRINDIRIZZOAZIENDA");
						request.delAttribute("NUMMESI");

						obj = request.getAttribute("STRRAGSOCIALEAZIENDA_" + i);
						if (obj != null)
							request.setAttribute("STRRAGSOCIALEAZIENDA", obj);
						obj = request.getAttribute("STRCODFISCALEAZIENDA_" + i);
						if (obj != null)
							request.setAttribute("STRCODFISCALEAZIENDA", obj);
						obj = request.getAttribute("STRPARTITAIVAAZIENDA_" + i);
						if (obj != null)
							request.setAttribute("STRPARTITAIVAAZIENDA", obj);
						obj = request.getAttribute("CODNATGIURIDICA_" + i);
						if (obj != null)
							request.setAttribute("CODNATGIURIDICA", obj);
						obj = request.getAttribute("CODATECO_" + i);
						if (obj != null)
							request.setAttribute("CODATECO", obj);
						obj = request.getAttribute("CODCOMAZIENDA_" + i);
						if (obj != null)
							request.setAttribute("CODCOMAZIENDA", obj);
						obj = request.getAttribute("STRINDIRIZZOAZIENDA_" + i);
						if (obj != null)
							request.setAttribute("STRINDIRIZZOAZIENDA", obj);
						obj = request.getAttribute("NUMMESI_" + i);
						if (obj != null)
							request.setAttribute("NUMMESI", obj);

						setSectionQuerySelect("QUERY_SEQUENCE_ESP_LAV");
						SourceBean row1 = doSelect(request, response);

						esplavoro = (BigDecimal) row1.getAttribute("ROW.PRGESPLAVORO");
						request.delAttribute("PRGESPLAVORO");
						request.setAttribute("PRGESPLAVORO", esplavoro);
						setSectionQueryInsert("QUERY_INSERT_ESP_LAV");

						service3(request, response);

						request.delAttribute("PRGMOVIMENTO");
						request.setAttribute("PRGMOVIMENTO", request.getAttribute("PRGMOVIMENTO_" + i));
						prgMovimento = (String) request.getAttribute("PRGMOVIMENTO");
						if (prgMovimento != null && !prgMovimento.equals("")) {
							setSectionQueryInsert("QUERY_INSERT_ASS_ESP_MOVI");
							doInsert(request, response);
						}
					} // else
				} // if checked
			} // end for
		} // end if

		if (request.getAttribute("FLAG") != null && request.getAttribute("FLAG").equals("1")) {
			String StringSizeAssMov = ((String) request.getAttribute("SIZE_ASS"));
			int SIZE_ASS = new Integer(StringSizeAssMov).intValue();
			for (int i = 1; i < SIZE_ASS + 1; i++) {
				check1 = (String) request.getAttribute("I_PR_MAN" + i);
				if (check1 != null && check1.equals("on")) {
					request.delAttribute("PRGMOVIMENTO");
					request.setAttribute("PRGMOVIMENTO", request.getAttribute("PRGMOVIMENTO_" + i));
					setSectionQueryInsert("QUERY_INSERT_ASS_ESP_MOVI");
					doInsert(request, response);
				}
			}
		} // if

	}// service

}// end calss
