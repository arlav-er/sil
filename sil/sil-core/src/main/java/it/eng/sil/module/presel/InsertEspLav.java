package it.eng.sil.module.presel;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.module.patto.PattoFacade;
import it.eng.sil.util.collectionexecutor.Action;
import it.eng.sil.util.collectionexecutor.ActionException;

public class InsertEspLav extends AbstractSimpleModule {
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

	public void service(SourceBean request, SourceBean response) throws Exception {
		boolean atecoValido = false;
		String codAteco = request.containsAttribute("codAteco") ? request.getAttribute("codAteco").toString() : "";
		int idSuccess = this.disableMessageIdSuccess();
		int idFail = this.disableMessageIdFail();
		ReportOperationResult reportOperation = new ReportOperationResult(this, response);

		atecoValido = it.eng.sil.module.anag.AtecoUtils.checkValiditaAteco(codAteco, false);

		if (!atecoValido) {
			reportOperation.reportFailure(idFail);
			reportOperation.reportFailure(MessageCodes.UnitaAzienda.ATECO_NON_VALIDO);
			return;
		}
		this.setMessageIdSuccess(MessageCodes.General.OPERATION_SUCCESS);
		setParameterForInsertEspLav(request, response);
	}

	public void service2(SourceBean request, SourceBean response) throws Exception {
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

	private void setParameterForInsertEspLav(SourceBean request, SourceBean response) throws Exception {

		String StringSize = ((String) request.getAttribute("SIZE"));

		if ((StringSize != null && !StringSize.equals(""))) {
			int SIZE = new Integer(StringSize).intValue();
			for (int i = 0; i < SIZE; i++) {
				if (request.getAttribute("CODMANSIONE_" + i) != null) {
					request.delAttribute("CODMANSIONE");
					request.setAttribute("CODMANSIONE", request.getAttribute("CODMANSIONE_" + i));
					// request.getAttribute("PRGMANSIONE" + i);
					request.delAttribute("PRGMANSIONE");
					request.setAttribute("PRGMANSIONE", request.getAttribute("PRGMANSIONE_" + i));
					// request.getAttribute("CODCONTRATTO" + i);
					request.delAttribute("CODCONTRATTO");
					request.setAttribute("CODCONTRATTO", request.getAttribute("CODCONTRATTO_" + i));
					// request.getAttribute("NUMANNOINIZIO" + i);
					request.delAttribute("NUMANNOINIZIO");
					request.setAttribute("NUMANNOINIZIO", request.getAttribute("NUMANNOINIZIO_" + i));
					request.delAttribute("desMansione");
					request.setAttribute("desMansione", request.getAttribute("desMansione_" + i));
					service2(request, response);
				}
			}

		} else {
			service2(request, response);
		}

	}
}