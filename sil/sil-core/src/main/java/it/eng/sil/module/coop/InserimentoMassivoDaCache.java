/*
 * Created on Jun 9, 2006
 *
 */
package it.eng.sil.module.coop;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

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
 * @author savino
 * 
 */
public class InserimentoMassivoDaCache extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(InserimentoMassivoDaCache.class.getName());
	TransactionQueryExecutor tex = null;

	private static final boolean DEBUG = false;

	public void service(SourceBean serviceRequest, SourceBean serviceResponse) throws Exception {

		boolean inserimentoAvvenuto = true;
		AccessoSchedaLavoratore accessoScheda = null;
		String codiceFiscale = null, moduloInserimento = null;
		User user = null;
		try {
			if (tex == null) {
				tex = new TransactionQueryExecutor(getPool());
				tex.initTransaction();
			}
			// inserisci o riprendi il cdnLavoratore

			List moduliInserimento = null;
			accessoScheda = new AccessoSchedaLavoratore();
			moduliInserimento = accessoScheda.moduliInserimento();
			SourceBean cache = (SourceBean) getRequestContainer().getSessionContainer()
					.getAttribute(GetDatiPersonali.SCHEDA_LAVORATORE_COOP_ID);
			SourceBean myServiceRequest = serviceRequest;
			codiceFiscale = (String) cache.getAttribute("codiceFiscale");
			// questa variabile viene usata per il passaggio dell'oggetto
			// consequences tra i vari moduli
			SourceBean _responsePrecStep = null;
			_responsePrecStep = new SourceBean("INIT_SERVICE_RESPONSE");
			// nella prima esecuzione il modulo di inserimento si attende nella
			// request il codice fiscale
			CoopDoInsertModule.setConsequencesParameter("strCodiceFiscale", codiceFiscale, _responsePrecStep);
			List moduli = new ArrayList();
			for (int i = 0; i < moduliInserimento.size(); i++) {
				SourceBean moduloBean = (SourceBean) moduliInserimento.get(i);
				moduloInserimento = (String) moduloBean.getAttribute("module_insert_name");
				String moduloInCacheKey = (String) moduloBean.getAttribute("module_cache_key");
				ModuleIFace modulo = ModuleFactory.getModule(moduloInserimento);
				Vector rows = cache.getAttributeAsVector("service_response." + moduloInCacheKey);
				for (int j = 0; j < rows.size(); j++) {
					SourceBean row = (SourceBean) rows.get(j);
					SourceBean _request = (SourceBean) row.cloneObject();
					SourceBean _response = new SourceBean(moduloInserimento);
					// i parametri presenti nelle "consequences" vengono
					// inseriti nella serviceRequest
					_request = CoopDoInsertModule.consequencesInServiceRequest(_responsePrecStep, _request);
					// l'oggetto consequences viene inserito nella nuova
					// response
					CoopDoInsertModule.setConsequences(_responsePrecStep, _response);
					this.getRequestContainer().setServiceRequest(_request);
					((RequestContextIFace) modulo).setRequestContext(this);
					((AbstractSimpleModule) modulo).enableTransactions(tex);

					modulo.service(_request, _response);
					// if (_response.getAttribute("INSERT_OK")!=null)
					// TracerSingleton.log(Values.APP_NAME,TracerSingleton.CRITICAL,
					// "errore");//Si e' verificato un errore: lanciare una
					// eccezione e bloccare l'inserimento?
					if (CoopDoInsertModule.insertFailed(_response)) {
						// _logger.fatal( "errore");
						throw new Exception("Inserimento massivo da cache: Il modulo " + modulo.getModule()
								+ " ha generato un errore " + getResponseContainer().getErrorHandler().toString());
					}
					// nello step successivo e' necessario recuperare l'oggetto
					// consequences eventualmente modificato
					// dall'esecuzione dell'ultimo modulo
					_responsePrecStep = _response;

				}
				if (serviceRequest.containsAttribute("inserisci")) {
					if (DEBUG) {// solo se voglio controllare nella jsp i moduli
								// che ho inserito pongo a true DEBUG (solo la
								// fase di inserimento scheda.
						// Per l'aggiornamento provvede la classe di
						// aggiornamento.
						user = (User) getRequestContainer().getSessionContainer().getAttribute(User.USERID);
						String descrModulo = accessoScheda.getDescrizioneModuloInsert(user, moduloInserimento);
						MessageAppender.appendMessage(serviceResponse, "Inserimento massivo da cache: Il modulo "
								+ descrModulo + " è stato eseguito con successo ", null);
					}
				} else {
					moduli.add(i, moduloInserimento);
				}
			}
			if (serviceRequest.containsAttribute("inserisci")) {
				tex.commitTransaction();
			} else {
				serviceResponse.setAttribute("moduli", moduli);
			}
		} catch (Exception e) {
			if (serviceRequest.containsAttribute("inserisci")) {
				tex.rollBackTransaction();
				serviceResponse.delAttribute("USER_MESSAGE");
			}
			inserimentoAvvenuto = false;
			getResponseContainer().getErrorHandler()
					.addError(new EMFUserError(EMFErrorSeverity.BLOCKING, MessageCodes.General.OPERATION_FAIL));
			String descrizioneModulo = "sconosciuto";
			if (moduloInserimento != null && accessoScheda != null) {
				user = (User) getRequestContainer().getSessionContainer().getAttribute(User.USERID);
				descrizioneModulo = accessoScheda.getDescrizioneModuloInsert(user, moduloInserimento);
			}
			MessageAppender.appendMessage(serviceResponse,
					"Inserimento scheda lavoratore fallito. Il modulo " + descrizioneModulo + " e' fallito.", null);
			if (!serviceRequest.containsAttribute("inserisci")) {
				serviceResponse.setAttribute("moduloInserimento", moduloInserimento);
			}
		}
		if (inserimentoAvvenuto) {
			String cdnLavoratore = recuperaCdnLavoratore(codiceFiscale);
			serviceResponse.setAttribute("cdnLavoratore", cdnLavoratore);
		}
	}

	public void setTransactionExecutor(TransactionQueryExecutor te) {
		tex = te;
	}

	private String recuperaCdnLavoratore(String strCodiceFiscaleLav) {
		SourceBean lav = (SourceBean) QueryExecutor.executeQuery("SELECT_AN_LAVORATORE",
				new Object[] { strCodiceFiscaleLav }, "SELECT", Values.DB_SIL_DATI);
		return it.eng.sil.util.Utils.notNull(lav.getAttribute("row.cdnLavoratore"));
	}
}