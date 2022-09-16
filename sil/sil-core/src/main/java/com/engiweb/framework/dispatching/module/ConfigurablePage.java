package com.engiweb.framework.dispatching.module;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.configuration.ConfigSingleton;

public class ConfigurablePage extends DefaultPage {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(ConfigurablePage.class.getName());
	private String _pageName = null;
	private static final String GRAPH_ID = "id";
	private static final String GRAPH = "GRAPH";
	private static final String MODULES = "MODULES";
	private static final String DEPENDENCIES = "DEPENDENCIES";
	private static final String GRAPH_PATH = "GRAPHS.GRAPH";
	private static final String MODULE_PATH = "MODULES.MODULE";
	private static final String DEPENDENCE_PATH = "DEPENDENCIES.DEPENDENCE";
	private static final String CONDITION_PARAMETER_PATH = "CONDITIONS.PARAMETER";
	private static final String CONSEQUENCE_PARAMETER_PATH = "CONSEQUENCES.PARAMETER";
	private static final String PARAMETER_NAME = "name";
	private static final String PARAMETER_VALUE = "value";
	private static final String MODULE_ID = "id";
	private static final String MODULE_NAME = "name";
	private static final String DEPENDENCE_ID = "id";
	private static final String DEPENDENCE_SOURCE = "source";
	private static final String DEPENDENCE_TARGET = "target";

	public ConfigurablePage() {
		super();
		_pageName = null;
	} // public ConfigurablePage()

	public void init(SourceBean config) {
		it.eng.sil.util.TraceWrapper.debug(_logger,
				"ConfigurablePage::instantiate: configurazione pagina prima del templating", config);

		_pageName = (String) config.getAttribute("name");
		SourceBean configGraph = (SourceBean) config.getAttribute(GRAPH);
		SourceBean templateGraph = instantiate(configGraph);
		try {
			config = (SourceBean) config.cloneObject();
			config.delAttribute(GRAPH);
			config.setAttribute((SourceBean) templateGraph.getAttribute(MODULES));
			config.setAttribute((SourceBean) templateGraph.getAttribute(DEPENDENCIES));
		} // try
		catch (SourceBeanException ex) {
			it.eng.sil.util.TraceWrapper.warn(_logger, "ConfigurablePage::init: ", ex);

		} // catch (SourceBeanException ex)
		it.eng.sil.util.TraceWrapper.debug(_logger,
				"ConfigurablePage::instantiate: configurazione pagina dopo il templating", config);

		super.init(config);
	} // public void init(SourceBean config)

	protected SourceBean instantiate(SourceBean configGraph) {
		String graphId = (String) configGraph.getAttribute(GRAPH_ID);
		if (graphId == null) {
			_logger.warn("ConfigurablePage::instantiate: id grafo configurazione non valido !");

			return configGraph;
		} // if (graphId == null)
		ConfigSingleton configure = ConfigSingleton.getInstance();
		SourceBean templateGraph = (SourceBean) configure.getFilteredSourceBeanAttribute(GRAPH_PATH, GRAPH_ID, graphId);
		templateGraph = (SourceBean) templateGraph.cloneObject();
		HashMap modulesMap = new HashMap();

		Vector templateModules = templateGraph.getAttributeAsVector(MODULE_PATH);
		for (int i = 0; i < templateModules.size(); i++) {
			SourceBean templateModule = (SourceBean) templateModules.get(i);
			String moduleId = (String) templateModule.getAttribute(MODULE_ID);
			if (moduleId == null) {
				_logger.warn("ConfigurablePage::instantiate: id modulo template non valido !");

				continue;
			} // if (moduleId == null)
			String moduleName = (String) templateModule.getAttribute(MODULE_NAME);
			modulesMap.put("{" + moduleId + "}", moduleName);
		} // for (int i = 0; i < templateModules.size(); i++)

		Vector configModules = configGraph.getAttributeAsVector(MODULE_PATH);
		for (int i = 0; i < configModules.size(); i++) {
			SourceBean configModule = (SourceBean) configModules.get(i);
			String moduleId = (String) configModule.getAttribute(MODULE_ID);
			if (moduleId == null) {
				_logger.warn("ConfigurablePage::instantiate: id modulo configurazione non valido !");

				continue;
			} // if (moduleId == null)
			String configModuleName = (String) configModule.getAttribute(MODULE_NAME);
			if ((configModuleName != null) && !(configModuleName.equals("")))
				modulesMap.put("{" + moduleId + "}", configModuleName);
			SourceBean templateModule = (SourceBean) templateGraph.getFilteredSourceBeanAttribute(MODULE_PATH,
					MODULE_ID, moduleId);
			try {
				templateModule.updContainedAttributes(configModule.getContainedAttributes());
			} // try
			catch (SourceBeanException ex) {
				it.eng.sil.util.TraceWrapper.fatal(_logger,
						"ConfigurablePage::instantiate: errore aggiornamento modulo template [" + moduleId + "]", ex);

			} // catch (SourceBeanException ex)
		} // for (int i = 0; i < configModules.size(); i++)

		Vector configDependencies = configGraph.getAttributeAsVector(DEPENDENCE_PATH);
		for (int i = 0; i < configDependencies.size(); i++) {
			SourceBean configDependence = (SourceBean) configDependencies.get(i);
			String dependenceId = (String) configDependence.getAttribute(DEPENDENCE_ID);
			if (dependenceId == null) {
				_logger.warn("ConfigurablePage::instantiate: id dipendenza non valido !");

				continue;
			} // if (dependenceId == null)
			SourceBean templateDependence = (SourceBean) templateGraph.getFilteredSourceBeanAttribute(DEPENDENCE_PATH,
					DEPENDENCE_ID, dependenceId);
			try {
				templateDependence.updContainedAttributes(configDependence.getContainedAttributes());
			} // try
			catch (SourceBeanException ex) {
				it.eng.sil.util.TraceWrapper.fatal(_logger,
						"ConfigurablePage::instantiate: errore aggiornamento template dipendenza [" + dependenceId
								+ "]",
						ex);

			} // catch (SourceBeanException ex)
		} // for (int i = 0; i < configDependencies(); i++)

		Vector templateDependencies = templateGraph.getAttributeAsVector(DEPENDENCE_PATH);
		for (int i = 0; i < templateDependencies.size(); i++) {
			SourceBean templateDependence = (SourceBean) templateDependencies.get(i);
			String templateDependenceSource = (String) templateDependence.getAttribute(DEPENDENCE_SOURCE);
			try {
				if ((templateDependenceSource == null) || (templateDependenceSource.equals("")))
					templateDependence.updAttribute(DEPENDENCE_SOURCE, _pageName);
				else
					templateDependence.updAttribute(DEPENDENCE_SOURCE,
							resolvePath(templateDependenceSource, modulesMap));
			} // try
			catch (SourceBeanException ex) {
				it.eng.sil.util.TraceWrapper.fatal(_logger,
						"ConfigurablePage::instantiate: errore aggiornamento modulo source template ["
								+ templateDependenceSource + "]",
						ex);

			} // catch (SourceBeanException ex)
			String templateDependenceTarget = (String) templateDependence.getAttribute(DEPENDENCE_TARGET);
			try {
				templateDependence.updAttribute(DEPENDENCE_TARGET, resolvePath(templateDependenceTarget, modulesMap));
			} // try
			catch (SourceBeanException ex) {
				it.eng.sil.util.TraceWrapper.fatal(_logger,
						"ConfigurablePage::instantiate: errore aggiornamento modulo target template ["
								+ templateDependenceTarget + "]",
						ex);

			} // catch (SourceBeanException ex)
			Vector conditionParameters = templateDependence.getAttributeAsVector(CONDITION_PARAMETER_PATH);
			for (int j = 0; j < conditionParameters.size(); j++) {
				SourceBean conditionParameter = (SourceBean) conditionParameters.get(j);
				String conditionParameterName = (String) conditionParameter.getAttribute(PARAMETER_NAME);
				try {
					conditionParameter.updAttribute(PARAMETER_NAME, resolvePath(conditionParameterName, modulesMap));
				} // try
				catch (SourceBeanException ex) {
					it.eng.sil.util.TraceWrapper.fatal(_logger,
							"ConfigurablePage::instantiate: errore aggiornamento nome parametro condizione ["
									+ conditionParameterName + "]",
							ex);

				} // catch (SourceBeanException ex)
				String conditionParameterValue = (String) conditionParameter.getAttribute(PARAMETER_VALUE);
				try {
					conditionParameter.updAttribute(PARAMETER_VALUE, resolvePath(conditionParameterValue, modulesMap));
				} // try
				catch (SourceBeanException ex) {
					it.eng.sil.util.TraceWrapper.fatal(_logger,
							"ConfigurablePage::instantiate: errore aggiornamento valore parametro condizione ["
									+ conditionParameterValue + "]",
							ex);

				} // catch (SourceBeanException ex)
			} // for (int j = 0; j < conditionParameters.size(); j++)
			Vector consequenceParameters = templateDependence.getAttributeAsVector(CONSEQUENCE_PARAMETER_PATH);
			for (int j = 0; j < consequenceParameters.size(); j++) {
				SourceBean consequenceParameter = (SourceBean) consequenceParameters.get(j);
				String consequenceParameterName = (String) consequenceParameter.getAttribute(PARAMETER_NAME);
				try {
					consequenceParameter.updAttribute(PARAMETER_NAME,
							resolvePath(consequenceParameterName, modulesMap));
				} // try
				catch (SourceBeanException ex) {
					it.eng.sil.util.TraceWrapper.fatal(_logger,
							"ConfigurablePage::instantiate: errore aggiornamento nome parametro conseguenza ["
									+ consequenceParameterName + "]",
							ex);

				} // catch (SourceBeanException ex)
				String consequenceParameterValue = (String) consequenceParameter.getAttribute(PARAMETER_VALUE);
				try {
					consequenceParameter.updAttribute(PARAMETER_VALUE,
							resolvePath(consequenceParameterValue, modulesMap));
				} // try
				catch (SourceBeanException ex) {
					it.eng.sil.util.TraceWrapper.fatal(_logger,
							"ConfigurablePage::instantiate: errore aggiornamento valore parametro conseguenza ["
									+ consequenceParameterValue + "]",
							ex);

				} // catch (SourceBeanException ex)
			} // for (int j = 0; j < consequenceParameters.size(); j++)
		} // for (int i = 0; i < configDependencies(); i++)

		return templateGraph;
	} // protected SourceBean override(SourceBean config)

	private String resolvePath(String toResolve, Map parameters) {
		if ((toResolve == null) || (parameters == null))
			return toResolve;
		StringBuffer resolved = new StringBuffer();
		int startIndex = toResolve.indexOf("{");
		while (startIndex != -1) {
			resolved.append(toResolve.substring(0, startIndex));
			int endIndex = toResolve.indexOf("}", startIndex);
			String moduleId = toResolve.substring(startIndex, endIndex + 1);
			String moduleName = (String) parameters.get(moduleId);
			if (moduleName == null)
				resolved.append(toResolve.substring(startIndex, endIndex + 1));
			else
				resolved.append(moduleName);
			toResolve = toResolve.substring(endIndex + 1, toResolve.length());
			startIndex = toResolve.indexOf("{");
		} // while (parameterIndex != -1)
		resolved.append(toResolve);
		return resolved.toString();
	} // private String resolvePath(String toResolve, Map parameters)
} // public class ConfigurablePage extends DefaultPage
