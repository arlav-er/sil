package it.eng.sil.module.patto;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.module.AbstractSimpleModule;

public class UpdateNotePattoImpegniAzienda extends AbstractSimpleModule {
	public void service(SourceBean request, SourceBean response) {

		doUpdate(request, response);
	}
}