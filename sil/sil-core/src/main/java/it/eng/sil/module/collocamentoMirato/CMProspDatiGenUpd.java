package it.eng.sil.module.collocamentoMirato;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.StringUtils;
import it.eng.sil.module.AbstractSimpleModule;

public class CMProspDatiGenUpd extends AbstractSimpleModule {

	/*
	 * (non Javadoc)
	 * 
	 * @see com.engiweb.framework.dispatching.service.ServiceIFace#service(com.engiweb.framework.base.SourceBean,
	 * com.engiweb.framework.base.SourceBean)
	 */
	public void service(SourceBean serviceRequest, SourceBean serviceResponse) throws Exception {

		RequestContainer requestContainer = getRequestContainer();
		SessionContainer sessionContainer = requestContainer.getSessionContainer();

		// Controlli sul FLAG CAPOGRUPPO
		String flgCapoGruppo = StringUtils.getAttributeStrNotNull(serviceRequest, "flgCapoGruppo");
		String strCFCapoGruppo = StringUtils.getAttributeStrNotNull(serviceRequest, "strCFAZCapoGruppo");
		if (flgCapoGruppo.equals("S") && strCFCapoGruppo.equals("")) {
			throw new Exception(
					"Impossibile inserire il prospetto informativo: valorizzare il codice fiscale capogruppo.");
		}

		String prgProspettoInf = StringUtils.getAttributeStrNotNull(serviceRequest, "PRGPROSPETTOINF");
		doUpdate(serviceRequest, serviceResponse);
	}

}
