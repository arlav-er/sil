package com.engiweb.framework.presentation;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Vector;

import com.engiweb.framework.base.ApplicationContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.configuration.ConfigSingleton;
import com.engiweb.framework.init.InitializerIFace;

public class StylesheetInitializer implements InitializerIFace {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(StylesheetInitializer.class.getName());
	private SourceBean _config = null;

	public StylesheetInitializer() {
		super();
		_config = null;
	} // public StylesheetInitializer()

	public void init(SourceBean config) {
		it.eng.sil.util.TraceWrapper.debug(_logger, "StylesheetInitializer::init: config", config);

		_config = config;
		ArrayList stylesheets = new ArrayList();
		ConfigSingleton configure = ConfigSingleton.getInstance();
		Vector publishers = configure.getAttributeAsVector("PUBLISHERS.PUBLISHER");
		for (int i = 0; i < publishers.size(); i++) {
			SourceBean publisher = (SourceBean) publishers.get(i);
			Vector renderings = publisher.getFilteredSourceBeanAttributeAsVector("RENDERING", "TYPE", "XSL");
			for (int j = 0; j < renderings.size(); j++) {
				SourceBean rendering = (SourceBean) renderings.get(j);
				Vector items = rendering.getAttributeAsVector("RESOURCES.ITEM");
				for (int k = 0; k < items.size(); k++) {
					SourceBean item = (SourceBean) items.get(k);
					stylesheets.add(item.getAttribute("RESOURCE"));
				} // for (int k = 0; k < items.size(); k++)
			} // for (int j = 0; j < renderings.size(); j++)
		} // for (int i = 0; i < publishers.size(); i++)
		Hashtable xslTemplates = new Hashtable();
		for (int i = 0; i < stylesheets.size(); i++) {
			String stylesheet = (String) stylesheets.get(i);
			if (!xslTemplates.containsKey(stylesheet)) {
				StylesheetFile stylesheetFile = new StylesheetFile();
				stylesheetFile.setFileName(stylesheet);
				if (stylesheetFile.getTemplate() == null)
					_logger.debug("StylesheetInitializer::init: errore caricamento stylesheet [" + stylesheet + "]");

				else
					_logger.debug("StylesheetInitializer::init: stylesheet [" + stylesheet + "] caricato");

				xslTemplates.put(stylesheet, stylesheetFile);
			} // if (!xslTemplates.containsKey(stylesheet))
		} // for (int i = 0; i < stylesheets.size(); i++)
		ApplicationContainer.getInstance().setAttribute("XSL_TEMPLATES", xslTemplates);
	} // public void init(SourceBean config)

	public SourceBean getConfig() {
		return _config;
	} // public SourceBean getConfig()
} // public class StylesheetInitializer implements InitializerIFace
