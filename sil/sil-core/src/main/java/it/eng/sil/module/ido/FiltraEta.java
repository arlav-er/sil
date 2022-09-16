package it.eng.sil.module.ido;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.MessageCodes;
import it.eng.sil.module.AbstractSimpleModule;

public class FiltraEta extends AbstractSimpleModule {
	public void service(SourceBean request, SourceBean response) {
		setMessageIdFail(MessageCodes.IDO.FILTRA_ETA);
		doDynamicUpdate(request, response);

	}

}// class FiltraEta
