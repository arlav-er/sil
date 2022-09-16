package it.eng.sil.module.amministrazione;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.module.AbstractSimpleModule;

public class deleteAssociazioneNO extends AbstractSimpleModule {
	public void service(SourceBean request, SourceBean response) {

		doUpdate(request, response);

	}

}
