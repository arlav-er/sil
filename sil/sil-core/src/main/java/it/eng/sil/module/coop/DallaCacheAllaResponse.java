package it.eng.sil.module.coop;

import java.util.Vector;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanAttribute;
import com.engiweb.framework.error.EMFAbstractError;
import com.engiweb.framework.error.EMFUserError;

import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.util.Utils;

/**
 * @author savino
 * 
 */
public class DallaCacheAllaResponse extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(DallaCacheAllaResponse.class.getName());

	public void service(SourceBean serviceRequest, SourceBean serviceResponse) throws Exception {
		SourceBean modulo = null;
		SourceBean responseRemota = (SourceBean) getRequestContainer().getSessionContainer()
				.getAttribute("SCHEDA_LAVORATORE_COOP_ID");
		if (responseRemota == null) {
			_logger.debug("COOP: Richiesta modulo dalla cache. Modulo " + getModule()
					+ ". Oggetto cache in sessione non trovato.");

			return;
		}
		SourceBean serviceResponseRemota = (SourceBean) responseRemota.getAttribute("SERVICE_RESPONSE");
		SourceBean serviceResponseDellaPage = getResponseContainer().getServiceResponse();
		SourceBean dettaglio = (SourceBean) getConfig().getAttribute("MODULO_DETTAGLIO");
		Vector keys = dettaglio.getAttributeAsVector("KEY");
		boolean recuperaDettaglio = keys.size() > 0;
		String keyModuloSession = (String) dettaglio.getAttribute("KEY_MODULO_SESSION");
		String keyDettaglio = (String) dettaglio.getAttribute("KEY_DETTAGLIO");
		String keySetResponse = (String) dettaglio.getAttribute("KEY_SET_RESPONSE");
		String keySetPrefix = (String) dettaglio.getAttribute("KEY_SET_PREFIX");
		// String moduloLista =
		// (String)serviceRequest.getAttribute("NOME_MODULO_LISTA");
		// aggiungo il punto di separazione dall'attributo se il prefisso esiste
		if (keyDettaglio != null && !keyDettaglio.trim().equals(""))
			keyDettaglio += ".";
		else
			keyDettaglio = "";
		if (keySetPrefix != null && !keySetPrefix.trim().equals(""))
			keySetPrefix += ".";
		else
			keySetPrefix = "";
		if (recuperaDettaglio) {
			// lista =
			// (SourceBean)getResponseContainer().getServiceResponse().getAttribute(moduloLista);
			Vector v = serviceResponseRemota.getAttributeAsVector(keyModuloSession);
			a: for (int i = 0; i < v.size(); i++) {
				modulo = (SourceBean) v.get(i);
				boolean trovato = false;
				for (int j = 0; j < keys.size(); j++) {
					SourceBean key = (SourceBean) keys.get(j);
					String nomeRequest = (String) key.getAttribute("NOME_REQUEST");
					String nomeDettaglio = (String) key.getAttribute("NOME_DETTAGLIO");
					String reqValue = (String) serviceRequest.getAttribute(nomeRequest);
					String modValue = Utils.notNull(modulo.getAttribute(keyDettaglio + nomeDettaglio));
					if (reqValue.equals(modValue))
						trovato = true;
					else {
						modulo = null;
						continue a;
					}
				}
				if (trovato)
					break;
			}
		} else {// si recupera l'oggetto cosi' com'è', senza selezionarne un
				// elemento.
			Vector v = serviceResponseRemota.getAttributeAsVector(keyModuloSession);
			if (v.size() > 0) {
				// TODO Savino: controllo errore nel modulo.
				modulo = (SourceBean) v.get(0);
				if (modulo.containsAttribute("ERROR_ID")) {
					String errorId = (String) modulo.getAttribute("ERROR_ID");
					Vector errors = responseRemota.getAttributeAsVector("ERRORS.USER_ERROR");
					for (int i = 0; i < errors.size(); i++) {
						if (Utils.notNull(((SourceBean) errors.get(i)).getAttribute("ERROR_ID")).equals(errorId)) {
							SourceBean errorBean = (SourceBean) errors.get(i);
							String errorSeverity = (String) errorBean.getAttribute(EMFAbstractError.ERROR_SEVERITY);
							String errorCode = (String) errorBean.getAttribute(EMFUserError.USER_ERROR_CODE);
							EMFUserError error = new EMFUserError(errorSeverity, Integer.parseInt(errorCode));
							getErrorHandler().addError(error);
							break;
						}
					}
				}
			}
		}
		if (modulo == null) {
			// ERRORE
			return;
		}
		SourceBean moduloEstratto = null;
		if (keySetResponse == null || keySetResponse.equals(""))
			moduloEstratto = serviceResponse;
		else
			moduloEstratto = new SourceBean(keySetResponse);
		/*
		 * if (modulo.getAttribute("ROWS")!=null) {
		 * 
		 * moduloEstratto.setAttribute((SourceBean)modulo.getAttribute("ROWS")); }
		 */
		Vector atts = modulo.getContainedAttributes();
		for (int i = 0; i < atts.size(); i++) {
			SourceBeanAttribute att = (SourceBeanAttribute) atts.get(i);
			Object value = modulo.getAttribute(att.getKey());
			String key = att.getKey();
			if (value instanceof SourceBean)
				moduloEstratto.setAttribute((SourceBean) value);
			else
				moduloEstratto.setAttribute(keySetPrefix + key, value);
		}
		serviceResponseDellaPage.setAttribute(moduloEstratto);
	}

}
