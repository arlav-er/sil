package it.eng.sil.module.cig;

import java.math.BigDecimal;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;

public class DeleteCorso extends AbstractSimpleModule {
	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(DeleteCorso.class.getName());

	private TransactionQueryExecutor transExec;
	BigDecimal userid;
	private ReportOperationResult reportOperation;
	private SourceBean request;
	private SourceBean response;

	public void service(SourceBean request, SourceBean response) throws Exception {

		this.request = request;
		this.response = response;

		RequestContainer requestContainer = getRequestContainer();
		SessionContainer sessionContainer = (SessionContainer) requestContainer.getSessionContainer();

		boolean success = false;

		int idSuccess = this.disableMessageIdSuccess();
		reportOperation = new ReportOperationResult(this, response);

		/*
		 * String prgCorsoCi = (String)request.getAttribute("prgCorsoCi");
		 * 
		 * if(prgCorsoCi==null) throw new Exception("Il prgCorsoCi non può essere null.");
		 */

		try {
			transExec = new TransactionQueryExecutor(getPool(), this);
			this.enableTransactions(transExec);

			transExec.initTransaction();

			_logger.debug("Inizio Cancellazione corso con prg " + request.getAttribute("PRGCORSOCI"));

			// setKeyinRequest(new BigDecimal(prgCorsoCi), request);
			this.setSectionQueryUpdate("QUERY_DELETE_CI_CORSO");
			success = this.doUpdate(request, response);

			if (!success) {
				transExec.rollBackTransaction();
				return;
			}

			// se i contatti non sono abilitati salto tutto
			if (isContattiAbilitato()) {
				success = deleteContatto();

				if (!success) {
					transExec.rollBackTransaction();
					return;
				}

			}

			transExec.commitTransaction();
			reportOperation.reportSuccess(idSuccess);

		} catch (Exception e) {
			reportOperation.reportFailure(200006, e, "services()", "Errore generico");
			transExec.rollBackTransaction();
		}
	}

	/**
	 * Elimina un contatto
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	private boolean deleteContatto() throws Exception {

		boolean success;

		// cerco il prg del contatto associato al corso
		this.setSectionQuerySelect("QUERY_SELECT_PRGCONTATTO");
		SourceBean prgcontatto_bean = this.doSelect(request, response);

		// se la query va male salta tutto
		if (prgcontatto_bean == null) {
			_logger.error("errore nell'estrazione del prgcontatto");
			throw new Exception("errore nell'estrazione del prgcontatto");
		}
		// estraggo il prgcontatto
		BigDecimal prgcontatto = (BigDecimal) prgcontatto_bean.getAttribute("ROW.PRGCONTATTOISCR");

		if (prgcontatto == null) {
			_logger.debug("nessun contatto da eliminare per il corso.");
		} else { // se non è null allora c'è un contatto da gestire

			// vediamo se e' stata inviata una mail a questo contatto
			request.setAttribute("PRGCONTATTO", prgcontatto);
			this.setSectionQuerySelect("QUERY_SELECT_AG_CONTATTO");
			SourceBean contatto_bean = this.doSelect(request, response);

			if (contatto_bean == null) {
				_logger.error("errore nell'estrazione del flginviato");
				throw new Exception("errore nell'estrazione del flginviato");
			}

			String flginviato = (String) contatto_bean.getAttribute("ROW.FLGINVIATOSMS");
			// se la mail non e' stata inviata
			if ((flginviato == null) || ("N".equalsIgnoreCase(flginviato))) {
				_logger.debug("Contatto non inviato. Cancello fisicamente");

				// cancello il riferimento dalla tabella ci_corso_catalogo
				this.setSectionQueryUpdate("QUERY_DELETE_CI_CORSO_CATALOGO");
				success = this.doUpdate(request, response);

				if (!success) {
					return false;
				}
				// elimino il record
				this.setSectionQueryUpdate("QUERY_DELETE_AG_CONTATTO");
				success = this.doUpdate(request, response);

				if (!success) {
					return false;
				}

			}
			// se ho gia' inviato la mail
			else {
				_logger.debug("Contatto inviato");
				request.setAttribute("FLGINVIATO", flginviato);

				// inserisco un nuovo contatto

				// recuperiamo il prgcorsoci
				BigDecimal prgContatto = getPrgContattoPK();
				if (prgContatto != null) {

					setKeyinRequest(prgContatto);
					request.setAttribute("CODCPICONTATTO", contatto_bean.getAttribute("ROW.CODCPICONTATTO"));
					request.setAttribute("DATCONTATTO", contatto_bean.getAttribute("ROW.DATCONTATTO"));
					request.setAttribute("STRORACONTATTO", contatto_bean.getAttribute("ROW.STRORACONTATTO"));
					request.setAttribute("PRGSPICONTATTO", contatto_bean.getAttribute("ROW.PRGSPICONTATTO"));

					String txtcontatto = (String) contatto_bean.getAttribute("ROW.TXTCONTATTO");

					int saluti_index = txtcontatto.indexOf("Cordiali saluti");
					String saluti = txtcontatto.substring(saluti_index);
					txtcontatto = txtcontatto.substring(0, saluti_index);

					txtcontatto = "Disdetta della seguente comunicazione:\n\n<< " + txtcontatto + " >> \n\n" + saluti;

					request.setAttribute("TXTCONTATTO", txtcontatto);
					request.updAttribute("PRGMOTCONTRATTO", new BigDecimal(102));

					BigDecimal prgAzienda = (BigDecimal) contatto_bean.getAttribute("ROW.PRGAZIENDA");
					BigDecimal prgUnita = (BigDecimal) contatto_bean.getAttribute("ROW.PRGUNITA");
					// se non presenti, metto 0 in request perché NULL non è accettato, e poi uso decode sulla query
					if (prgAzienda == null) {
						prgAzienda = new BigDecimal(0);
					}
					if (prgUnita == null) {
						prgUnita = new BigDecimal(0);
					}

					request.setAttribute("PRGAZIENDA", prgAzienda);
					request.setAttribute("PRGUNITA", prgUnita);
					request.setAttribute("STREMAIL", contatto_bean.getAttribute("ROW.STRCELLSMSINVIO"));

					this.setSectionQueryInsert("QUERY_INSERT_CONTATTO");
					success = this.doInsert(request, response);

					if (!success) {
						return false;
					}

					// aggiorno il riferimento in ci_corso_catalogo
					this.setSectionQueryUpdate("QUERY_UPDATE_CORSOCAT");
					success = this.doUpdate(request, response);

					_logger.debug("Corso catalogo " + request.getAttribute("PRGCORSOCI") + " aggiornato!");
				}
			}
		}

		return true;
	}

	private boolean isContattiAbilitato() {
		this.setSectionQuerySelect("QUERY_ISCONTATTOAB");
		SourceBean beanContatto = (SourceBean) doSelect(request, response);
		String abilitato = (String) beanContatto.getAttribute("ROW.ABILITATO");
		return "S".equalsIgnoreCase(abilitato);
	}

	/**
	 * Ottiene la primary key dalla sequence S_AG_CONTATTO.
	 * 
	 * @param request
	 *            SourceBean request.
	 * @param response
	 *            SourceBean response.
	 * @return BigDecimal primary key.
	 * @throws Exception.
	 */
	private BigDecimal getPrgContattoPK() throws Exception {
		this.setSectionQuerySelect("QUERY_NEXTVAL");
		SourceBean beanprgCorsoCi = (SourceBean) doSelect(request, response);
		return (BigDecimal) beanprgCorsoCi.getAttribute("ROW.PRGCONTATTO");
	}

	private void setKeyinRequest(BigDecimal prgCorsoCi) throws Exception {
		if (request.getAttribute("PRGCONTATTO") != null) {
			request.delAttribute("PRGCONTATTO");
		}
		request.setAttribute("PRGCONTATTO", prgCorsoCi);
	}
}
