package it.eng.sil.module.iscrizioni;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.action.report.UtilsConfig;
import it.eng.sil.module.AbstractSimpleModule;

public class GetConfigurazione extends AbstractSimpleModule {
	private String className = this.getClass().getName();

	public void service(SourceBean request, SourceBean response) throws Exception {
		UtilsConfig utility = new UtilsConfig("AL_ISCR");
		String tipoConfig = utility.getConfigurazioneDefault_Custom();
		response.setAttribute("CONFIGURAZIONE", tipoConfig);
	}
}
