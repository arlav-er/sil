package it.eng.sil.module.patto;

import java.math.BigDecimal;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.error.EMFInternalError;

import it.eng.afExt.utils.LogUtils;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.util.collectionexecutor.Action;

/**
 * Facade che semplifica l'utilizzo del PattoManager. Relizzato con Facade Design Pattern.
 * 
 * @author vaccari
 * @created December 9, 2003
 */
public class PattoFacade {

	/**
	 * Description of the Method
	 * 
	 * @param request
	 *            Description of the Parameter
	 * @return Description of the Return Value
	 */
	public boolean withPatto(SourceBean request) {

		return PattoManager.withPatto(request);
	}

	/**
	 * Esegue la cancellazione del record del modulo client con annessa la gestione del patto.
	 * 
	 * @param module
	 *            Client del patto manager
	 * @param response
	 *            Response passata al client
	 * @param namePool
	 *            Description of the Parameter
	 * @param request
	 *            Description of the Parameter
	 * @return Ritorna true se tutto è andato bene, altrimenti false
	 */
	public boolean doDelete(AbstractSimpleModule module, String namePool, SourceBean request, SourceBean response) {

		ReportOperationResult reportOperation = new ReportOperationResult(module, response);

		TransactionQueryExecutor transExec = null;
		int idSuccess = module.disableMessageIdSuccess();
		boolean ret = false;

		try {

			transExec = new TransactionQueryExecutor(namePool);

			PattoManager patto = new PattoManager(module, transExec);
			transExec.initTransaction();

			if (module.doDelete(request, response) && patto.execute(request, response)) {

				transExec.commitTransaction();

				module.setMessageIdSuccess(idSuccess);
				reportOperation.reportSuccess(idSuccess);

				ret = true;
			}

		} catch (Exception e) {

			reportOperation.reportFailure(MessageCodes.General.DELETE_FAIL, e, "doDelete", "Error");
			if (transExec != null) {
				try {

					transExec.rollBackTransaction();

				} catch (EMFInternalError intErr) {

					LogUtils.logError("PattoManager.doDelete", "Rollback transaction failed", intErr, module);
				}
			}
			ret = false;
		}
		return (ret);
	}

	/**
	 * Esegue l'inserimento del record del modulo client con annessa la gestione del patto.
	 * 
	 * @param module
	 *            Client del patto manager
	 * @param response
	 *            Response passata al client
	 * @param namePool
	 *            Description of the Parameter
	 * @param request
	 *            Description of the Parameter
	 * @param namePrgField
	 *            Description of the Parameter
	 * @return Ritorna true se tutto è andato bene, altrimenti false
	 */
	public boolean doInsert(AbstractSimpleModule module, String namePool, SourceBean request, SourceBean response,
			String namePrgField) {

		return doInsertInternal(module, namePool, request, response, namePrgField, false);
	}

	/**
	 * Esegue l'inserimento del record del modulo client con annessa la gestione del patto.
	 * 
	 * @param module
	 *            Client del patto manager
	 * @param namePool
	 *            Nome del pool
	 * @param request
	 *            Request passata al client
	 * @param response
	 *            Response passata al client
	 * @param namePrgField
	 *            Nome del field del progressivo sulla tabella del database
	 * @param insertAction
	 *            Azione eseguita per l'inserimento
	 * @return Ritorna true se tutto è andato bene, altrimenti false
	 */
	public boolean doInsert(AbstractSimpleModule module, String namePool, SourceBean request, SourceBean response,
			String namePrgField, Action insertAction) {

		ReportOperationResult reportOperation = new ReportOperationResult(module, response);
		TransactionQueryExecutor transExec = null;
		int idSuccess = module.disableMessageIdSuccess();
		boolean ret = false;

		try {

			transExec = new TransactionQueryExecutor(namePool);

			PattoManager patto = new PattoManager(module, transExec);
			transExec.initTransaction();

			insertAction.execute(request, response);

			Object prgField = request.getAttribute(namePrgField);
			request.updAttribute("PRG_TAB", prgField);

			if (patto.execute(request, response)) {

				transExec.commitTransaction();

				module.setMessageIdSuccess(idSuccess);
				reportOperation.reportSuccess(idSuccess);

				ret = true;
			}

		} catch (Exception e) {

			reportOperation.reportFailure(MessageCodes.General.INSERT_FAIL, e, "doInsert", "Error");
			if (transExec != null) {
				try {

					transExec.rollBackTransaction();

				} catch (EMFInternalError intErr) {

					LogUtils.logError("PattoManager.doInsert", "Rollback transaction failed", intErr, module);
				}
			}
			ret = false;
		}
		return (ret);
	}

	/**
	 * Esegue l'inserimento del record, senza duplicati, del modulo client con annessa la gestione del patto.
	 * 
	 * @param module
	 *            Client del patto manager
	 * @param response
	 *            Response passata al client
	 * @param namePool
	 *            Description of the Parameter
	 * @param request
	 *            Description of the Parameter
	 * @param namePrgField
	 *            Description of the Parameter
	 * @return Ritorna true se tutto è andato bene, altrimenti false
	 */
	public boolean doInsertNoDuplicate(AbstractSimpleModule module, String namePool, SourceBean request,
			SourceBean response, String namePrgField) {

		return doInsertInternal(module, namePool, request, response, namePrgField, true);
	}

	/**
	 * Esegue l'inserimento del record del modulo client con annessa la gestione del patto.
	 * 
	 * Gestisce sia l'inserimento semplice che con controllo di record non duplicato.
	 * 
	 * @param module
	 *            Client del patto manager
	 * @param response
	 *            Response passata al client
	 * @param namePool
	 *            Description of the Parameter
	 * @param request
	 *            Description of the Parameter
	 * @param namePrgField
	 *            Description of the Parameter
	 * @param withNoDuplicate
	 *            Se true viene eseguito <code>doInsertNoDuplicate</code> altrimenti <code>doInsert</code>.
	 * @return Ritorna true se tutto è andato bene, altrimenti false
	 */
	private boolean doInsertInternal(AbstractSimpleModule module, String namePool, SourceBean request,
			SourceBean response, String namePrgField, boolean withNoDuplicate) {

		ReportOperationResult reportOperation = new ReportOperationResult(module, response);
		TransactionQueryExecutor transExec = null;
		int idSuccess = module.disableMessageIdSuccess();
		boolean ret = false;

		try {

			transExec = new TransactionQueryExecutor(namePool);

			PattoManager patto = new PattoManager(module, transExec);
			transExec.initTransaction();

			BigDecimal prgField = module.doNextVal(request, response);
			request.setAttribute(namePrgField, prgField);

			ret = withNoDuplicate ? module.doInsertNoDuplicate(request, response) : module.doInsert(request, response);

			if (ret) {

				request.updAttribute("PRG_TAB", prgField);

				if (patto.execute(request, response)) {

					transExec.commitTransaction();

					module.setMessageIdSuccess(idSuccess);
					reportOperation.reportSuccess(idSuccess);

				}
			}

		} catch (Exception e) {

			reportOperation.reportFailure(MessageCodes.General.INSERT_FAIL, e, "doInsertInternal", "Error");
			if (transExec != null) {
				try {

					transExec.rollBackTransaction();

				} catch (EMFInternalError intErr) {

					LogUtils.logError("PattoManager.doInsertInternal", "Rollback transaction failed", intErr, module);
				}
			}
			ret = false;
		}
		return (ret);
	}

	/**
	 * Esegue l'aggiornamento del record del modulo client con annessa la gestione del patto.
	 * 
	 * @param module
	 *            Client del patto manager
	 * @param namePool
	 *            Nome del pool
	 * @param request
	 *            Request passata al client
	 * @param response
	 *            Response passata al client
	 * @param namePrgField
	 *            Nome del field del progressivo sulla tabella del database
	 * @return Ritorna true se tutto è andato bene, altrimenti false
	 */
	public boolean doUpdate(AbstractSimpleModule module, String namePool, SourceBean request, SourceBean response) {

		return doUpdate(module, namePool, request, response, null);
	}

	/**
	 * Esegue l'aggiornamento del record del modulo client con annessa la gestione del patto.
	 * 
	 * @param module
	 *            Client del patto manager
	 * @param namePool
	 *            Nome del pool
	 * @param request
	 *            Request passata al client
	 * @param response
	 *            Response passata al client
	 * @param namePrgField
	 *            Nome del field del progressivo sulla tabella del database
	 * @param updateAction
	 *            Azione eseguita per l'aggiornamento
	 * @return Ritorna true se tutto è andato bene, altrimenti false
	 */
	public boolean doUpdate(AbstractSimpleModule module, String namePool, SourceBean request, SourceBean response,
			Action updateAction) {

		ReportOperationResult reportOperation = new ReportOperationResult(module, response);
		TransactionQueryExecutor transExec = null;
		int idSuccess = module.disableMessageIdSuccess();
		boolean ret = false;

		try {

			transExec = new TransactionQueryExecutor(namePool);

			PattoManager patto = new PattoManager(module, transExec);
			transExec.initTransaction();

			boolean ris = true;
			if (updateAction != null) {
				updateAction.execute(request, response);
			} else {
				ris = module.doUpdate(request, response);
			}

			if (ris && patto.execute(request, response)) {

				transExec.commitTransaction();

				module.setMessageIdSuccess(idSuccess);
				reportOperation.reportSuccess(idSuccess);

				ret = true;
			}

		} catch (Exception e) {

			reportOperation.reportFailure(MessageCodes.General.DELETE_FAIL, e, "doUpdate", "Error");
			if (transExec != null) {
				try {

					transExec.rollBackTransaction();

				} catch (EMFInternalError intErr) {

					LogUtils.logError("PattoManager.doUpdate", "Rollback transaction failed", intErr, module);
				}
			}
			ret = false;
		}
		return (ret);
	}
}
