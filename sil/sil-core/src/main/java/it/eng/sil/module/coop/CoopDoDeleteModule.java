/*
 * Creato il 19-giu-06
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.module.coop;

import java.util.Vector;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanAttribute;
import com.engiweb.framework.base.SourceBeanException;

import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.util.Utils;

/**
 * @author riccardi
 * 
 *         Per modificare il modello associato al commento di questo tipo generato, aprire
 *         Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
public class CoopDoDeleteModule extends AbstractSimpleModule {

	public void service(SourceBean serviceRequest, SourceBean serviceResponse) {
		boolean res = doDeleteModule(serviceRequest, serviceResponse);
		if (!res)
			setFailure(serviceResponse);
		setNextStepConsequences(serviceRequest, serviceResponse);
	}

	public boolean doDeleteModule(SourceBean serviceRequest, SourceBean serviceResponse) {
		return super.doDelete(serviceRequest, serviceResponse);
	}

	void setFailure(SourceBean serviceResponse) {
		try {
			serviceResponse.updAttribute("DELETE_FAIL", "TRUE");
		} catch (SourceBeanException e) {
			e.printStackTrace();
		}
	}

	static boolean deleteFailed(SourceBean serviceResponse) {
		return Utils.notNull(serviceResponse.getAttribute("DELETE_FAIL")).equals("TRUE");
	}

	void setNextStepConsequences(SourceBean serviceRequest, SourceBean serviceResponse) {
	}

	/*
	 * esempio di uso del metodo sovrascritto dalla classe che la ha estesa (vedere CoopInsertOrSelectLavoratore)
	 * 
	 * void setNextStepConsequences(SourceBean serviceRequest, SourceBean serviceResponse) { Object cdnLavoratore =
	 * serviceResponse.getAttribute("cdnLavoratore"); setNextSpepConsequencesParameter("cdnLavoratore",
	 * cdnLavoratore.toString(), serviceResponse); }
	 */

	static void setConsequencesParameter(String key, String value, SourceBean serviceResponse) {
		SourceBean consequences = (SourceBean) serviceResponse.getAttribute("consequences");
		try {
			if (consequences == null) {
				consequences = new SourceBean("consequences");
				serviceResponse.setAttribute(consequences);
			}
			consequences.setAttribute(key, value);
		} catch (SourceBeanException e) {
		}
	}

	static SourceBean consequencesInServiceRequest(SourceBean serviceResponse, SourceBean serviceRequest) {
		SourceBean _sb = serviceRequest;
		SourceBean consequences = (SourceBean) serviceResponse.getAttribute("consequences");
		Vector attributes = consequences.getContainedAttributes();
		try {
			for (int i = 0; i < attributes.size(); i++) {
				SourceBeanAttribute attribute = (SourceBeanAttribute) attributes.get(i);
				Object value = consequences.getAttribute(attribute.getKey());
				String key = attribute.getKey();
				if (value instanceof SourceBean)
					serviceRequest.updAttribute((SourceBean) value);
				else
					serviceRequest.updAttribute(key, value);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return _sb;
	}

	static void setConsequences(SourceBean from, SourceBean to) throws SourceBeanException {
		to.setAttribute((SourceBean) from.getAttribute("consequences"));
	}
}
