package it.eng.sil.module.ido;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.MessageCodes;
import it.eng.sil.module.AbstractSimpleModule;

public class FiltraSesso extends AbstractSimpleModule {
	public void service(SourceBean request, SourceBean response) {
		setMessageIdFail(MessageCodes.IDO.FILTRA_SESSO);
		doUpdate(request, response);

	}

}// class FiltraSesso
