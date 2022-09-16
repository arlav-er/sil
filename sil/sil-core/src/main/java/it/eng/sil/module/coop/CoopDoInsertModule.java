package it.eng.sil.module.coop;

import java.util.Vector;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanAttribute;
import com.engiweb.framework.base.SourceBeanException;

import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.util.Utils;

/**
 * @author savino
 */
public class CoopDoInsertModule extends AbstractSimpleModule {

	public void service(SourceBean serviceRequest, SourceBean serviceResponse) {
		boolean res = doInsertModule(serviceRequest, serviceResponse);
		if (!res)
			setFailure(serviceResponse);
		setNextStepConsequences(serviceRequest, serviceResponse);
	}

	public boolean doInsertModule(SourceBean serviceRequest, SourceBean serviceResponse) {
		return super.doInsert(serviceRequest, serviceResponse);
	}

	void setFailure(SourceBean serviceResponse) {
		try {
			serviceResponse.updAttribute("INSERT_FAIL", "TRUE");
		} catch (SourceBeanException e) {
			e.printStackTrace();
		}
	}

	static boolean insertFailed(SourceBean serviceResponse) {
		return Utils.notNull(serviceResponse.getAttribute("INSERT_FAIL")).equals("TRUE");
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
