package com.engiweb.framework.dispatching.module;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Vector;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.ResponseContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.configuration.ConfigSingleton;
import com.engiweb.framework.util.ContextScooping;

public class ModuleDependencies {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(ModuleDependencies.class.getName());

	private ModuleDependencies() {
		super();
	} // private ModuleDependencies()

	public static Collection getTargetRequests(RequestContainer requestContainer, ResponseContainer responseContainer,
			String sourceName, SourceBean pageConfig) {
		_logger.debug("ModuleDependencies::getTargetRequests: sourceName [" + sourceName + "]");

		ArrayList targetRequests = new ArrayList();
		String page = (String) requestContainer.getServiceRequest().getAttribute("PAGE");
		ConfigSingleton config = ConfigSingleton.getInstance();
		if (pageConfig == null) {
			_logger.warn("ModuleDependencies::getTargetRequests: pageConfig [" + page + "] nullo");

			return targetRequests;
		} // if (pageConfig == null)
		if (sourceName != null) {
			SourceBean moduleConfig = (SourceBean) pageConfig.getFilteredSourceBeanAttribute("MODULES.MODULE", "NAME",
					sourceName);
			if (moduleConfig == null) {
				_logger.warn(
						"ModuleDependencies::getTargetRequests: module [" + sourceName + "] non censito nella pagina");

				return targetRequests;
			} // if (moduleConfig == null)
		} // if (module != null)
		Vector dependencies = null;
		if (sourceName == null)
			dependencies = pageConfig.getFilteredSourceBeanAttributeAsVector("DEPENDENCIES.DEPENDENCE", "SOURCE", page);
		else
			dependencies = pageConfig.getFilteredSourceBeanAttributeAsVector("DEPENDENCIES.DEPENDENCE", "SOURCE",
					sourceName);
		if (dependencies.size() == 0)
			_logger.debug("ModuleDependencies::getTargetRequests: nessuna dipendenza trovata");

		for (int i = 0; i < dependencies.size(); i++) {
			SourceBean dependence = (SourceBean) dependencies.get(i);
			Vector conditions = dependence.getAttributeAsVector("CONDITIONS.PARAMETER");
			if (conditions.size() == 0)
				_logger.debug("ModuleDependencies::getTargetRequests: nessuna condizione trovata");

			boolean conditionVerified = true;
			for (int j = 0; j < conditions.size(); j++) {
				SourceBean condition = (SourceBean) conditions.get(j);
				String parameterName = (String) condition.getAttribute("NAME");
				String parameterScope = (String) condition.getAttribute("SCOPE");
				String parameterValue = (String) condition.getAttribute("VALUE");
				String inParameterValue = null;
				Object parameterValueObject = ContextScooping.getScopedParameter(requestContainer, responseContainer,
						parameterName, parameterScope);
				if (parameterValueObject != null)
					inParameterValue = parameterValueObject.toString();
				if (parameterValue.equalsIgnoreCase("AF_DEFINED")) {
					if (inParameterValue == null) {
						conditionVerified = false;
						break;
					} // if (inParameterValue == null)
					continue;
				} // if (parameterValue.equalsIgnoreCase("AF_DEFINED"))
				if (parameterValue.equalsIgnoreCase("AF_NOT_DEFINED")) {
					if (inParameterValue != null) {
						conditionVerified = false;
						break;
					} // if (inParameterValue != null)
					continue;
				} // if (parameterValue.equalsIgnoreCase("AF_NOT_DEFINED"))
				if (!(parameterValue.equalsIgnoreCase(inParameterValue))) {
					conditionVerified = false;
					break;
				} // if (!(parameterValue.equalsIgnoreCase(inParameterValue)))
			} // for (int j = 0; conditions.size(); j++)
			if (conditionVerified) {
				Vector consequences = dependence.getAttributeAsVector("CONSEQUENCES.PARAMETER");
				if (consequences.size() == 0)
					_logger.debug("ModuleDependencies::getTargetRequests: nessuna conseguenza trovata");

				SourceBean targetRequest = (SourceBean) requestContainer.getServiceRequest().cloneObject();
				String targetName = (String) dependence.getAttribute("TARGET");
				try {
					targetRequest.updAttribute("AF_MODULE_NAME", targetName);
				} // try
				catch (SourceBeanException sbe) {
					it.eng.sil.util.TraceWrapper.fatal(_logger,
							"ModuleDependencies::getTargetRequests: targetRequest.updAttribute(\"AF_MODULE_NAME\", "
									+ targetName + ")",
							sbe);

				} // catch (SourceBeanException sbe)
				for (int j = 0; j < consequences.size(); j++) {
					SourceBean consequence = (SourceBean) consequences.get(j);
					String parameterName = (String) consequence.getAttribute("NAME");
					String parameterScope = (String) consequence.getAttribute("SCOPE");
					String parameterType = (String) consequence.getAttribute("TYPE");
					String parameterValue = (String) consequence.getAttribute("VALUE");
					Object inParameterValue = null;
					if (parameterType.equalsIgnoreCase("ABSOLUTE"))
						inParameterValue = parameterValue;
					else
						inParameterValue = ContextScooping.getScopedParameter(requestContainer, responseContainer,
								parameterValue, parameterScope);
					String parameterAppend = (String) consequence.getAttribute("APPEND");
					boolean append = ((parameterAppend != null) && parameterAppend.equalsIgnoreCase("true"));
					try {
						if (inParameterValue == null)
							targetRequest.delAttribute(parameterName);
						else if (inParameterValue instanceof SourceBean)
							if (append)
								targetRequest.setAttribute((SourceBean) inParameterValue);
							else
								targetRequest.updAttribute((SourceBean) inParameterValue);
						else if (append)
							targetRequest.setAttribute(parameterName, inParameterValue);
						else
							targetRequest.updAttribute(parameterName, inParameterValue);
					} // try
					catch (SourceBeanException sbe) {
						it.eng.sil.util.TraceWrapper.fatal(_logger,
								"ModuleDependencies::getTargetRequests: targetRequest.updAttribute(" + parameterName
										+ ", " + inParameterValue + ")",
								sbe);

					} // catch (SourceBeanException sbe)
				} // for (int j = 0; j < consequences.size(); j++)
				targetRequests.add(targetRequest);
			} // if (conditionVerified)
		} // for (int i = 0; i < dependencies.size(); i++)
		return targetRequests;
	} // public static Collection getTargetRequests(RequestContainer
		// requestContainer, ResponseContainer responseContainer, String
		// sourceName, SourceBean pageConfig)
} // public class ModuleDependencies
