package com.engiweb.framework.dispatching.module;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.dispatching.service.RequestContextIFace;
import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

public class DefaultPage extends AbstractPage {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(DefaultPage.class.getName());
	private String _pageName = null;
	private HashMap _modulesInstancesToKeep = null;
	private HashMap _modulesResponsesToKeep = null;

	// private IEngUserProfile _userProfile = null;

	public DefaultPage() {
		super();
		_pageName = null;
		_modulesInstancesToKeep = null;
		_modulesResponsesToKeep = null;
		// _userProfile = null;
	} // public DefaultPage()

	public void init(SourceBean config) {
		super.init(config);
		_pageName = (String) config.getAttribute("NAME");
		_modulesInstancesToKeep = new HashMap();
		_modulesResponsesToKeep = new HashMap();
	} // public void init(SourceBean config)

	protected HashMap initModules() throws Exception {
		HashMap modulesInstances = new HashMap();
		SourceBean serviceResponse = getResponseContainer().getServiceResponse();
		SourceBean sharedData = null;
		try {
			sharedData = new SourceBean("SHARED_DATA");
		} // try
		catch (SourceBeanException sbe) {
			it.eng.sil.util.TraceWrapper.fatal(_logger, "DefaultPage::initModules: new SourceBean(\"SHARED_DATA\")",
					sbe);

		} // catch(SourceBeanException sbe)
		Vector moduleConfigs = getConfig().getAttributeAsVector("MODULES.MODULE");
		for (int i = 0; i < moduleConfigs.size(); i++) {
			SourceBean moduleConfig = (SourceBean) moduleConfigs.get(i);
			String moduleName = (String) moduleConfig.getAttribute("NAME");
			String keepInstanceStr = (String) moduleConfig.getAttribute("KEEP_INSTANCE");
			boolean keepInstance = ((keepInstanceStr != null) && keepInstanceStr.equalsIgnoreCase("TRUE"));
			ModuleIFace module = null;
			if (_modulesInstancesToKeep.containsKey(moduleName.toUpperCase())) {
				module = (ModuleIFace) _modulesInstancesToKeep.get(moduleName.toUpperCase());
				_logger.debug("DefaultPage::initModules: istanza module [" + moduleName.toUpperCase() + "] trovata");

			} // if
				// (_modulesInstancesToKeep.containsKey(moduleName.toUpperCase()))
			else {
				try {
					module = ModuleFactory.getModule(moduleName);
					_logger.debug("DefaultPage::initModules: istanza module [" + moduleName.toUpperCase() + "] creata");

				} // try
				catch (ModuleException pte) {
					it.eng.sil.util.TraceWrapper.fatal(_logger, "DefaultPage::initModules:", pte);

				} // catch (ModuleException pte)
			} // if
				// (_modulesInstancesToKeep.containsKey(moduleName.toUpperCase()))
				// else
			if (module != null) {
				((RequestContextIFace) module).setRequestContext(this);
				module.setPage(_pageName);
				module.setSharedData(sharedData);
				modulesInstances.put(moduleName.toUpperCase(), module);
				if (keepInstance)
					_modulesInstancesToKeep.put(moduleName.toUpperCase(), module);
			} // if (module != null)
			if (_modulesResponsesToKeep.containsKey(moduleName.toUpperCase())) {
				serviceResponse.updAttribute((SourceBean) _modulesResponsesToKeep.get(moduleName.toUpperCase()));
				_logger.debug("DefaultPage::initModules: risposta module [" + moduleName.toUpperCase() + "] trovata");

			} // if
				// (_modulesResponsesToKeep.containsKey(moduleName.toUpperCase()))
			else
				_logger.debug(
						"DefaultPage::initModules: risposta module [" + moduleName.toUpperCase() + "] non trovata");

		} // for (int i = 0; i < modules.size(); i++)
		/*
		 * _userProfile = (IEngUserProfile) getRequestContainer() .getSessionContainer() .getPermanentContainer()
		 * .getAttribute(IEngUserProfile.ENG_USER_PROFILE); if (_userProfile instanceof RequestContextIFace) {
		 * ((RequestContextIFace)_userProfile).setRequestContext(this); }
		 */
		return modulesInstances;
	} // protected void initModules() throws Exception

	protected void releaseModules(HashMap modulesInstances) {
		if (modulesInstances == null)
			return;
		ArrayList modulesArray = new ArrayList(modulesInstances.values());
		for (int i = 0; i < modulesArray.size(); i++) {
			ModuleIFace module = (ModuleIFace) modulesArray.get(i);
			((RequestContextIFace) module).setRequestContext(null);
			module.setSharedData(null);
		} // for (int i = 0; i < modulesArray.size(); i++)
	} // protected void releaseModules(HashMap modulesInstances)

	public void service(SourceBean serviceRequest, SourceBean serviceResponse) throws Exception {
		HashMap modulesInstances = null;
		try {
			modulesInstances = initModules();
			nextStep(modulesInstances, null);
		} // try
		catch (Exception ex) {
			throw ex;
		} // catch (Exception ex)
		finally {
			releaseModules(modulesInstances);
		} // finally
	} // public void service(SourceBean serviceRequest, SourceBean
		// serviceResponse) throws Exception

	protected void nextStep(HashMap modulesInstances, String sourceName) throws Exception {
		ArrayList targetRequests = new ArrayList(ModuleDependencies.getTargetRequests(getRequestContainer(),
				getResponseContainer(), sourceName, getConfig()));
		SourceBean serviceResponse = getResponseContainer().getServiceResponse();
		for (int i = 0; i < targetRequests.size(); i++) {
			SourceBean targetRequest = (SourceBean) targetRequests.get(i);
			String targetName = (String) targetRequest.getAttribute("AF_MODULE_NAME");
			if (!(modulesInstances.containsKey(targetName.toUpperCase()))) {
				_logger.warn("DefaultPage::nextStep: module targetName [" + targetName + "] non censito nella page");

				continue;
			} // if (!(_modules.containsKey(targetName.toUpperCase())))
			boolean isModuleAuthorized = true;
			// if (SecurityConfigurationSingleton
			// .getInstance()
			// .isPageModuleChecked(_pageName, targetName)) {
			// if (_userProfile == null) {
			// TracerSingleton.log(// Constants.NOME_MODULO,//
			// TracerSingleton.WARNING,// "DefaultPage::nextStep: _userProfile
			// nullo !");
			// isModuleAuthorized = false;
			// } // if (_userProfile == null)
			// else {
			// try {
			// isModuleAuthorized =
			// _userProfile.isAbleToExecuteModuleInPage(
			// _pageName,
			// targetName);
			// } // try
			// catch (EMFInternalError emfie) {
			// TracerSingleton.log(// Constants.NOME_MODULO,//
			// TracerSingleton.CRITICAL,// "DefaultPage::nextStep::
			// _userProfile.isAbleToExecuteModuleInPage("// + _pageName// + ",
			// "// + targetName// + ")");
			// isModuleAuthorized = false;
			// } // catch (EMFInternalError emfie)
			// } // if (_userProfile == null) else
			// } // if
			// (SecurityConfigurationSingleton.getInstance().isPageModuleChecked(getPage(),
			// targetName))
			SourceBean targetResponse = new SourceBean(targetName);
			if (!isModuleAuthorized)
				_logger.warn("DefaultPage::nextStep: autorizzazione esecuzione module [" + targetName + "] nella page ["
						+ _pageName + "] negata !");

			else {
				ModuleIFace module = (ModuleIFace) modulesInstances.get(targetName.toUpperCase());
				SourceBean serviceRequest = getRequestContainer().getServiceRequest();
				Monitor moduleMonitor = MonitorFactory.start("model.module." + targetName.toLowerCase());
				try {
					getRequestContainer().setServiceRequest(targetRequest);
					module.service(targetRequest, targetResponse);
				} // try
				catch (Exception ex) {
					_logger.fatal("DefaultPage::nextStep:: errore nel module [" + targetName.toUpperCase() + "]");

					throw ex;
				} // catch (Exception ex)
				finally {
					getRequestContainer().setServiceRequest(serviceRequest);
					moduleMonitor.stop();
				} // finally
			} // if (!isModuleAuthorized) else
			serviceResponse.updAttribute(targetResponse);
			SourceBean moduleConfig = (SourceBean) getConfig().getFilteredSourceBeanAttribute("MODULES.MODULE", "NAME",
					targetName.toUpperCase());
			String keepResponseStr = (String) moduleConfig.getAttribute("KEEP_RESPONSE");
			boolean keepResponse = ((keepResponseStr != null) && keepResponseStr.equalsIgnoreCase("TRUE"));
			if (keepResponse) {
				_modulesResponsesToKeep.put(targetName.toUpperCase(), targetResponse);
				_logger.debug("DefaultPage::nextStep: risposta module [" + targetName.toUpperCase() + "] archiviata");

			} // if (keepResponse)
			nextStep(modulesInstances, targetName);
		} // for (int i = 0; i < targetRequests.size(); i++)
	} // protected void nextStep(HashMap modulesInstances, String sourceName)
		// throws Exception
} // public class DefaultPage extends AbstractPage
