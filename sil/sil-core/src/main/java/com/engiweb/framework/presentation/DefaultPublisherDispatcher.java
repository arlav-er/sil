package com.engiweb.framework.presentation;

import java.util.Vector;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.ResponseContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.util.ContextScooping;

public class DefaultPublisherDispatcher extends AbstractPublisherDispatcher {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(DefaultPublisherDispatcher.class.getName());

	public DefaultPublisherDispatcher() {
		super();
	} // public DefaultPublisherDispatcher()

	public String getPublisherName(RequestContainer requestContainer, ResponseContainer responseContainer) {
		it.eng.sil.util.TraceWrapper.debug(_logger, "DefaultPublisherDispatcher::getPublisherName: config",
				getConfig());

		// Modifiche Monica 16/01/2004 - inizio
		// Interpreta la grammatica contenuta nella busta <CONFIG> dell'ITEM
		String targetName = null;
		Vector checks = getConfig().getAttributeAsVector("CHECKS.CHECK");
		if (checks.size() == 0)
			_logger.warn("DefaultPublisherDispatcher::getPublisherName: nessun check trovato");

		for (int i = 0; i < checks.size(); i++) {
			SourceBean check = (SourceBean) checks.get(i);
			Vector conditions = check.getAttributeAsVector("CONDITIONS.PARAMETER");
			if (conditions.size() == 0)
				_logger.debug("DefaultPublisherDispatcher::getPublisherName: nessuna condizione trovata");

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
				// ATTRIBUTO TARGET DEFINISCE IL PUBLISHER DI DESTINAZIONE
				targetName = (String) check.getAttribute("TARGET");
				break;
			} // if (conditionVerified)
		} // for (int i = 0; i < checks.size(); i++)
		return targetName;
		// Modifiche Monica 16/01/2004 - fine
	} // public String getPublisherName(RequestContainer requestContainer,
		// ResponseContainer responseContainer)
} // public class DefaultPublisherDispatcher extends
	// AbstractPublisherDispatcher
