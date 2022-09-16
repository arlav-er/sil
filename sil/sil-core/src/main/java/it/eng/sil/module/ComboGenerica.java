package it.eng.sil.module;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.StringUtils;

public class ComboGenerica extends SelectModule {
	protected String className = StringUtils.getClassName(this);

	public void service(SourceBean request, SourceBean response) {
		super.service(request, response);
	}

}
