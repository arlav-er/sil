package it.eng.sil.module.ido;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.MessageCodes;
import it.eng.sil.module.AbstractSimpleModule;

public class FiltraNazione extends AbstractSimpleModule {
	public void service(SourceBean request, SourceBean response) {
		setMessageIdFail(MessageCodes.IDO.FILTRA_NAZIONE);
		doDynamicUpdate(request, response);

	}

}// class FiltraNazione
