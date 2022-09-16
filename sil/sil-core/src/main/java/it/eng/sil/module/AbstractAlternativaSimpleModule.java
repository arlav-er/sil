package it.eng.sil.module;

import java.math.BigDecimal;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.LogUtils;

/**
 * 
 * Fornisce metodi di supporto per la sezione della richiesta alternativa
 * 
 * 
 * @author rolfini
 * @created january, 2004
 * @version 1.0
 * 
 */
public abstract class AbstractAlternativaSimpleModule extends AbstractSimpleModule {

	public AbstractAlternativaSimpleModule() {

		super();
	}

	/**
	 * restituisce il prgAlternativa a seconda della sua presenza in session, in request o in nessuna delle due
	 * 
	 * 
	 * La priorità è così definita: 1) request 2) session 3) valore di default (1)
	 */
	public Object getPrgAlternativa(SourceBean request) {

		RequestContainer requestContainer = getRequestContainer();
		SessionContainer session = requestContainer.getSessionContainer();

		Object prgAlternativa = "1";
		Object objPrgAlternativaReq = request.getAttribute("prgAlternativa");
		Object prgAlternativaRequest = null;

		if (objPrgAlternativaReq != null)
			if (objPrgAlternativaReq instanceof String) {
				prgAlternativaRequest = (String) objPrgAlternativaReq;
			} else {
				prgAlternativaRequest = (BigDecimal) objPrgAlternativaReq;
			}

		Object objPrgAlternativaSex = session.getAttribute("prgAlternativa");
		Object prgAlternativaSession = null;
		if (objPrgAlternativaSex != null)
			if (objPrgAlternativaSex instanceof String) {
				prgAlternativaSession = (String) objPrgAlternativaSex;
			} else {
				prgAlternativaSession = (BigDecimal) objPrgAlternativaSex;
			}

		if (prgAlternativaRequest != null) {
			prgAlternativa = prgAlternativaRequest;
			// debug
			LogUtils.logDebug("AbstractAlternativaSimpleModule::getPrgAlternativa", "prgAlternativa trovato in REQUEST",
					this);
		} else if (prgAlternativaSession != null) {
			prgAlternativa = prgAlternativaSession;
			// debug
			LogUtils.logDebug("AbstractAlternativaSimpleModule::getPrgAlternativa", "prgAlternativa trovato in SESSION",
					this);
		}

		try {
			// cancello gli eventuali valori presenti in session
			// ed in request
			session.delAttribute("prgAlternativa");
			request.delAttribute("prgAlternativa");

			// setto la nuova prgAlternativa in session ed in request.
			session.setAttribute("prgAlternativa", prgAlternativa);
			request.setAttribute("prgAlternativa", prgAlternativa);
		} catch (Exception ex) {
			LogUtils.logError("AbstractAlternativaSimpleModule.getPrgAlternativa::",
					"(session/request).setAttribute fallita", ex, this);
		}

		return prgAlternativa;
	}

}