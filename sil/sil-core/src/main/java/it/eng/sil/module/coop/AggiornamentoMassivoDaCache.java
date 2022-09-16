/*
 * Creato il 19-giu-06
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.module.coop;

import java.util.List;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dispatching.module.ModuleFactory;
import com.engiweb.framework.dispatching.module.ModuleIFace;
import com.engiweb.framework.dispatching.service.RequestContextIFace;
import com.engiweb.framework.error.EMFErrorSeverity;
import com.engiweb.framework.error.EMFUserError;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.afExt.utils.MessageAppender;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.Values;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.security.User;

/**
 * @author riccardi
 * 
 *         Per modificare il modello associato al commento di questo tipo generato, aprire
 *         Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
public class AggiornamentoMassivoDaCache extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(AggiornamentoMassivoDaCache.class.getName());

	private static final boolean DEBUG = false;

	public void service(SourceBean serviceRequest, SourceBean serviceResponse) throws Exception {
		TransactionQueryExecutor tex = null;
		boolean aggiornamentoAvvenuto = true;
		AccessoSchedaLavoratore accessoScheda = null;
		String codiceFiscale = null, cdnLavoratore = null, moduloCancellazione = null, moduloInserimento = null;
		User user = null;

		try {
			tex = new TransactionQueryExecutor(getPool());
			tex.initTransaction();
			// inserisci o riprendi il cdnLavoratore

			List moduliCancellazione = null;
			accessoScheda = new AccessoSchedaLavoratore();
			moduliCancellazione = accessoScheda.moduliCancellazione();
			SourceBean cache = (SourceBean) getRequestContainer().getSessionContainer()
					.getAttribute(GetDatiPersonali.SCHEDA_LAVORATORE_COOP_ID);
			SourceBean myServiceRequest = serviceRequest;
			codiceFiscale = (String) cache.getAttribute("codiceFiscale");
			// questa variabile viene usata per il passaggio dell'oggetto
			// consequences tra i vari moduli
			SourceBean _responsePrecStep = null;
			_responsePrecStep = new SourceBean("INIT_SERVICE_RESPONSE");
			// nella prima esecuzione il modulo di aggiornamento si attende
			// nella request il codice fiscale
			cdnLavoratore = recuperaCdnLavoratore(codiceFiscale);
			SourceBean _request = null; // new SourceBean("SERVICE_REQUEST");
			// _request.setAttribute("cdnLavoratore", cdnLavoratore);
			user = (User) getRequestContainer().getSessionContainer().getAttribute(User.USERID);
			CoopDoDeleteModule.setConsequencesParameter("cdnLavoratore", cdnLavoratore, _responsePrecStep);
			for (int i = 0; i < moduliCancellazione.size(); i++) {
				SourceBean moduloBean = (SourceBean) moduliCancellazione.get(i);
				moduloCancellazione = (String) moduloBean.getAttribute("module_delete_name");
				String responseName = (String) moduloBean.getAttribute("response_name");
				ModuleIFace modulo = ModuleFactory.getModule(moduloCancellazione);
				_request = (SourceBean) cache.getAttribute("service_response." + responseName);
				if (_request == null) {
					// se il modulo non esiste nella response significa che non
					// e' stato caricato e che quindi
					// quindi l'operatore non ha la profilatura per accederci.
					_logger.debug("cancellazione: il modulo " + moduloCancellazione
							+ " non verra' eseguito perche' il modulo non e' stato caricato nella response remota.");

					continue;
				}
				// NOTA: se le informazioni da inserire non ci sono quelle
				// locali LE CANCELLO COMUNQUE
				_request = (SourceBean) _request.cloneObject();
				_request.setAttribute("cdnLavoratore", cdnLavoratore);
				SourceBean _response = new SourceBean(moduloCancellazione);
				this.getRequestContainer().setServiceRequest(_request);
				((RequestContextIFace) modulo).setRequestContext(this);
				((AbstractSimpleModule) modulo).enableTransactions(tex);
				modulo.service(_request, _response);
				if (CoopDoDeleteModule.deleteFailed(_response)) {
					_logger.fatal("errore");

					throw new Exception(
							"Cancellazione massiva da cache per aggiornamento: Il modulo " + modulo.getModule()
									+ " ha generato un errore " + getResponseContainer().getErrorHandler().toString());
				}
				if (DEBUG) {
					// codice per test in fase di debug
					boolean pippo = false;
					if (pippo)
						throw new Exception("test");
					String descrModulo = accessoScheda.getDescrizioneModuloDelete(user, moduloCancellazione);
					MessageAppender.appendMessage(serviceResponse,
							"Cancellazione massiva da cache per aggiornamento: Il modulo " + descrModulo
									+ " è stato eseguito con successo ",
							null);
				}
			}
			moduloCancellazione = null;
			ModuleIFace moduloPerInserire = ModuleFactory.getModule("M_COOP_InserimentoMassivoDaCache");
			((RequestContextIFace) moduloPerInserire).setRequestContext(this);
			SourceBean _responseIns = new SourceBean("M_COOP_InserimentoMassivoDaCache");
			((InserimentoMassivoDaCache) moduloPerInserire).setTransactionExecutor(tex);
			_request = (SourceBean) serviceRequest.cloneObject();
			moduloPerInserire.service(_request, _responseIns);
			if (!getResponseContainer().getErrorHandler().isOK()) {
				moduloInserimento = (String) _responseIns.getAttribute("moduloInserimento");
				throw new Exception(
						"Inserimento massivo da cache per aggiornamento: Il modulo " + moduloPerInserire.getModule()
								+ " ha generato un errore " + getResponseContainer().getErrorHandler().toString());
			} else {
				if (DEBUG) {
					List moduli = (List) _responseIns.getAttribute("moduli");
					for (int k = 0; k < moduli.size(); k++) {
						String modul = (String) moduli.get(k);
						String descrModulo = accessoScheda.getDescrizioneModuloInsert(user, modul);
						MessageAppender.appendMessage(serviceResponse,
								"Inserimento massivo da cache per aggiornamento: Il modulo " + descrModulo
										+ " è stato eseguito con successo ",
								null);
					}
				}
			}
			tex.commitTransaction();
		} catch (Exception e) {
			tex.rollBackTransaction();
			serviceResponse.delAttribute("USER_MESSAGE");
			aggiornamentoAvvenuto = false;
			getResponseContainer().getErrorHandler()
					.addError(new EMFUserError(EMFErrorSeverity.BLOCKING, MessageCodes.General.OPERATION_FAIL));
			String descrizioneModulo = "sconosciuto";
			user = (User) getRequestContainer().getSessionContainer().getAttribute(User.USERID);
			if (accessoScheda != null) {
				if (moduloCancellazione != null) {
					descrizioneModulo = accessoScheda.getDescrizioneModuloDelete(user, moduloCancellazione);
				}
				if (moduloInserimento != null) {
					descrizioneModulo = accessoScheda.getDescrizioneModuloInsert(user, moduloInserimento);
				}
			}
			MessageAppender.appendMessage(serviceResponse,
					"Aggiornamento scheda lavoratore fallito. Il modulo " + descrizioneModulo + " e' fallito.", null);
		}
		if (aggiornamentoAvvenuto) {
			serviceResponse.setAttribute("cdnLavoratore", cdnLavoratore);
		}
	}

	private String recuperaCdnLavoratore(String strCodiceFiscaleLav) {
		SourceBean lav = (SourceBean) QueryExecutor.executeQuery("SELECT_AN_LAVORATORE",
				new Object[] { strCodiceFiscaleLav }, "SELECT", Values.DB_SIL_DATI);
		return it.eng.sil.util.Utils.notNull(lav.getAttribute("row.cdnLavoratore"));
	}
}