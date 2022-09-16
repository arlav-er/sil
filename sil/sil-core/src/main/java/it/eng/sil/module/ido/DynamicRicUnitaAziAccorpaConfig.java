package it.eng.sil.module.ido;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;

import it.eng.afExt.tags.AbstractConfigProvider;

/**
 * @author Luigi Antenucci
 */
public class DynamicRicUnitaAziAccorpaConfig extends AbstractConfigProvider {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(DynamicRicUnitaAziAccorpaConfig.class.getName());

	private String className = this.getClass().getName();

	public void service(SourceBean request, SourceBean response) {
	}

	public SourceBean getConfigSourceBean(SourceBean request, SourceBean response) {

		String configXmlStr = "<CONFIG title=\"\" rows=\"\">" + "	<COLUMNS>"
				+ "		<COLUMN name=\"desStato\" label=\"Stato attivit&agrave;\" />"
				+ "		<COLUMN name=\"strIndirizzo\" label=\"Indirizzo\" />"
				+ "		<COLUMN name=\"descomune\" label=\"Comune\" />"
				+ "		<COLUMN name=\"flgsede\" label=\"Sede Legale\" />" + "	</COLUMNS>" + "</CONFIG>";

		SourceBean configSB = null;
		try {
			configSB = SourceBean.fromXMLString(configXmlStr);
		} catch (SourceBeanException ex) {
			it.eng.sil.util.TraceWrapper.debug(_logger, className + "::getConfigSourceBean()", ex);

		}
		return configSB;
	}
}