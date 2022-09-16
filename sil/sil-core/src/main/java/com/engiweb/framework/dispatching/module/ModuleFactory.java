package com.engiweb.framework.dispatching.module;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.configuration.ConfigSingleton;
import com.engiweb.framework.init.InitializerIFace;

public class ModuleFactory {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(ModuleFactory.class.getName());

	private ModuleFactory() {
		super();
	} // private ModuleFactory()

	public static ModuleIFace getModule(String moduleName) throws ModuleException {
		_logger.debug("ModuleFactory::getModule: moduleName [" + moduleName + "]");

		ConfigSingleton configure = ConfigSingleton.getInstance();
		SourceBean moduleBean = (SourceBean) configure.getFilteredSourceBeanAttribute("MODULES.MODULE", "NAME",
				moduleName);
		if (moduleBean == null) {
			_logger.debug("ModuleFactory::getModule: nome del module non valido !");

			throw new ModuleException("nome del module [" + moduleName + "] non valido");
		} // if (actionBean == null)
		String moduleClass = (String) moduleBean.getAttribute("CLASS");
		_logger.debug("ModuleFactory::getModule: moduleClass [" + moduleClass + "]");

		SourceBean moduleConfig = (SourceBean) moduleBean.getAttribute("CONFIG");
		if (moduleConfig == null)
			_logger.debug("ModuleFactory::getModule: config del module non censito");

		else
			it.eng.sil.util.TraceWrapper.debug(_logger, "ModuleFactory::getModule: moduleConfig", moduleConfig);

		ModuleIFace module = null;
		try {
			module = (ModuleIFace) Class.forName(moduleClass).newInstance();
		} // try
		catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.debug(_logger, "ModuleFactory::getModule: classe del module non valida", ex);

			throw new ModuleException("Classe del module [" + moduleName + "] non valida");
		} // catch (Exception ex) try
		module.setModule(moduleName);
		((InitializerIFace) module).init(moduleConfig);
		return module;
	} // public static ModuleIFace getModule(String moduleName) throws
		// ModuleException
} // public class ModuleFactory
