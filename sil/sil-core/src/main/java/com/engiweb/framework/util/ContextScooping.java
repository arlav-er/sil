package com.engiweb.framework.util;

import java.util.Iterator;

import com.engiweb.framework.base.ApplicationContainer;
import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.ResponseContainer;
import com.engiweb.framework.configuration.ConfigSingleton;
import com.engiweb.framework.error.EMFErrorHandler;
import com.engiweb.framework.error.EMFErrorSeverity;
import com.engiweb.framework.error.EMFUserError;

public abstract class ContextScooping {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(ContextScooping.class.getName());

	public static Object getScopedParameter(RequestContainer requestContainer, ResponseContainer responseContainer,
			String parameterName, String parameterScope) {
		Object parameterValue = null;
		if (parameterScope == null)
			return parameterValue;
		if (parameterScope.equalsIgnoreCase("CONFIG"))
			parameterValue = ConfigSingleton.getInstance().getAttribute(parameterName);
		// else if (parameterScope.equalsIgnoreCase("USER")) {
		// IEngUserProfile userProfile =
		// (IEngUserProfile) requestContainer
		// .getSessionContainer()
		// .getPermanentContainer()
		// .getAttribute(IEngUserProfile.ENG_USER_PROFILE);
		// if (userProfile == null)
		// return parameterValue;
		// try {
		// parameterValue =
		// (String) userProfile.getUserAttribute(parameterName);
		// } // try
		// catch (EMFInternalError ex) {
		// TracerSingleton.log(// Constants.NOME_MODULO,//
		// TracerSingleton.CRITICAL,// "ContextScooping::getScopedParameter:
		// userProfile.getUserAttribute("// + parameterName// + ")",//
		// (Exception) ex);
		// } // catch (EntityException ex) try
		// } // if (parameterScope.equalsIgnoreCase("USER"))
		else if (parameterScope.equalsIgnoreCase("ADAPTER_REQUEST"))
			parameterValue = requestContainer.getAttribute(parameterName);
		else if (parameterScope.equalsIgnoreCase("SERVICE_REQUEST"))
			parameterValue = requestContainer.getServiceRequest().getAttribute(parameterName);
		else if (parameterScope.equalsIgnoreCase("SESSION"))
			parameterValue = requestContainer.getSessionContainer().getAttribute(parameterName);
		else if (parameterScope.equalsIgnoreCase("APPLICATION"))
			parameterValue = ApplicationContainer.getInstance().getAttribute(parameterName);
		else if (parameterScope.equalsIgnoreCase("ADAPTER_RESPONSE"))
			parameterValue = responseContainer.getAttribute(parameterName);
		else if (parameterScope.equalsIgnoreCase("SERVICE_RESPONSE"))
			parameterValue = responseContainer.getServiceResponse().getAttribute(parameterName);
		// Modifiche Monica 16/01/2004 - inizio
		// gestione dello scope="ERROR"
		else if (parameterScope.equalsIgnoreCase("ERROR")) {
			EMFErrorHandler errorHandler = responseContainer.getErrorHandler();
			// se attributo name = null si cercano tutti gli errori
			if (parameterName == null) {
				if (!errorHandler.isOK())
					parameterValue = new Boolean(true);
			} // if (parameterName == null)
				// se attributo name vale "AF_"+severity si cercano tutti gli errori
				// con tale severity
			else if (parameterName.equalsIgnoreCase("AF_INFORMATION")) {
				if (!errorHandler.isOKBySeverity(EMFErrorSeverity.INFORMATION))
					parameterValue = new Boolean(true);
			} // if (parameterName.equalsIgnoreCase("AF_INFORMATION"))
			else if (parameterName.equalsIgnoreCase("AF_WARNING")) {
				if (!errorHandler.isOKBySeverity(EMFErrorSeverity.WARNING))
					parameterValue = new Boolean(true);
			} // if (parameterName.equalsIgnoreCase("AF_WARNING"))
			else if (parameterName.equalsIgnoreCase("AF_ERROR")) {
				if (!errorHandler.isOKBySeverity(EMFErrorSeverity.ERROR))
					parameterValue = new Boolean(true);
			} // if (parameterName.equalsIgnoreCase("AF_ERROR"))
			else if (parameterName.equalsIgnoreCase("AF_BLOCKING")) {
				if (!errorHandler.isOKBySeverity(EMFErrorSeverity.BLOCKING))
					parameterValue = new Boolean(true);
			} // if (parameterName.equalsIgnoreCase("AF_BLOCKING"))
				// se la stringa contenuta nell'attributo name non corrisponde a
				// nessuna delle stringhe precedenti
				// è un codice di errore di tipo UserError
			else {
				// ciclo su Collection ottenuta da getErrors , che ritorna tutti
				// gli errori
				Iterator iterator = errorHandler.getErrors().iterator();
				while (iterator.hasNext()) {
					Object error = iterator.next();
					if (error instanceof EMFUserError) {
						EMFUserError userError = (EMFUserError) error;
						if (parameterName.equalsIgnoreCase(String.valueOf(userError.getCode()))) {
							parameterValue = new Boolean(true);
						} // if(parameterName.equalsIgnoreCase(String.valueOf(userError.getCode())))
					} // if(iterator.next() instanceof EMFUserError)
				} // while (iterator.hasNext())
			} // else
		} // else if (parameterScope.equalsIgnoreCase("ERROR"))
			// Modifiche Monica 16/01/2004 - fine
		return parameterValue;
	} // public static Object getScopedParameter(RequestContainer
		// requestContainer, ResponseContainer responseContainer, String
		// parameterName, String parameterScope)
} // public abstract class ContextScooping
