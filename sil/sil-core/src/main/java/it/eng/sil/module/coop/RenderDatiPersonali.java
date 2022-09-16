/*
 * Created on May 29, 2006
 *s
 */
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
 * @deprecated CLASSE NON PIU' UTILIZZATA
 */
public class RenderDatiPersonali extends AbstractSimpleModule {

	public void service(SourceBean serviceRequest, SourceBean serviceResponse) throws Exception {
		SourceBean responseRemota = (SourceBean) getRequestContainer().getSessionContainer()
				.getAttribute("SCHEDA_LAVORATORE_COOP_ID");
		if (responseRemota == null)
			return;
		SourceBean serviceResponseRemota = (SourceBean) responseRemota.getAttribute("SERVICE_RESPONSE");
		SourceBean serviceResponseDellaPage = getResponseContainer().getServiceResponse();
		SourceBean dettaglio = (SourceBean) getConfig().getAttribute("MODULO_DETTAGLIO");
		SourceBean lista = null, modulo = null;
		String moduloGet = (String) dettaglio.getAttribute("GET");
		String moduloSet = (String) dettaglio.getAttribute("SET");
		String moduloLista = (String) serviceRequest.getAttribute("NOME_MODULO_LISTA");
		Vector keys = dettaglio.getAttributeAsVector("key");
		boolean recuperaDettaglio = keys.size() > 0;
		if (recuperaDettaglio) {
			lista = (SourceBean) getResponseContainer().getServiceResponse().getAttribute(moduloLista);

			Vector v = serviceResponseRemota.getAttributeAsVector(moduloGet);
			a: for (int i = 0; i < v.size(); i++) {
				modulo = (SourceBean) v.get(i);
				boolean trovato = false;
				for (int j = 0; j < keys.size(); j++) {
					SourceBean key = (SourceBean) keys.get(j);
					String nomeRequest = (String) key.getAttribute("NOME_REQUEST");
					String nomeDettaglio = (String) key.getAttribute("NOME_DETTAGLIO");
					String reqValue = (String) serviceRequest.getAttribute(nomeRequest);
					String modValue = Utils.notNull(modulo.getAttribute("rows.row." + nomeDettaglio));
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
		} else {
			Vector v = serviceResponseRemota.getAttributeAsVector(moduloGet);
			if (v.size() > 0) {
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
		if (moduloSet == null || moduloSet.equals(""))
			moduloEstratto = serviceResponse;
		else
			moduloEstratto = new SourceBean(moduloSet);
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
				moduloEstratto.setAttribute(key, value);
		}
		serviceResponseDellaPage.setAttribute(moduloEstratto);
	}

}
