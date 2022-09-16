package com.engiweb.framework.dispatching.action;

import com.engiweb.framework.base.ApplicationContainer;
import com.engiweb.framework.base.Constants;
import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.configuration.ConfigSingleton;
import com.engiweb.framework.init.InitializerIFace;

public class ActionFactory {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(ActionFactory.class.getName());

	private ActionFactory() {
		super();
	} // private ActionFactory()

	public static ActionIFace getAction(RequestContainer requestContainer) throws ActionException {
		SourceBean serviceRequest = (SourceBean) requestContainer.getServiceRequest();
		String actionName = (String) serviceRequest.getAttribute(Constants.ACTION_NAME);
		_logger.debug("ActionFactory::getAction: actionName [" + actionName + "]");

		ConfigSingleton configure = ConfigSingleton.getInstance();
		SourceBean actionBean = (SourceBean) configure.getFilteredSourceBeanAttribute("ACTIONS.ACTION", "NAME",
				actionName);
		if (actionBean == null) {
			_logger.debug("ActionFactory::getAction: nome della action non valido !");

			throw new ActionException("Nome della action [" + actionName + "] non valido");
		} // if (actionBean == null)
		String actionClass = (String) actionBean.getAttribute("CLASS");
		_logger.debug("ActionFactory::getAction: actionClass [" + actionClass + "]");

		SourceBean actionConfig = (SourceBean) actionBean.getAttribute("CONFIG");
		if (actionConfig == null)
			_logger.debug("ActionFactory::getAction: config della action non censito");

		else
			it.eng.sil.util.TraceWrapper.debug(_logger, "ActionFactory::getAction: actionConfig", actionConfig);

		String actionScope = (String) actionBean.getAttribute("SCOPE");
		if (actionScope == null) {
			_logger.debug("ActionFactory::getAction: scope della action non censito, default [REQUEST]");

			actionScope = "REQUEST";
		} // if (actionScope == null)
		if ((actionScope == null) || (!actionScope.equalsIgnoreCase("REQUEST")
				&& !actionScope.equalsIgnoreCase("SESSION") && !actionScope.equalsIgnoreCase("APPLICATION"))) {
			_logger.debug("ActionFactory::getAction: scope della action non valido [" + actionScope + "]");

			actionScope = "REQUEST";
		} // if ((actionScope == null) ||
			// (!actionScope.equalsIgnoreCase("REQUEST") &&
			// !actionScope.equalsIgnoreCase("SESSION") &&
			// !actionScope.equalsIgnoreCase("APPLICATION")))
		_logger.debug("ActionFactory::getAction: actionScope [" + actionScope + "]");

		if (actionScope.equalsIgnoreCase("REQUEST")) {
			ActionIFace action = null;
			try {
				action = (ActionIFace) Class.forName(actionClass).newInstance();
			} // try
			catch (Exception ex) {
				it.eng.sil.util.TraceWrapper.debug(_logger, "ActionFactory::getAction: classe della action non valida",
						ex);

				throw new ActionException("Classe della action [" + actionName + "] non valida");
			} // catch (Exception ex) try
			action.setAction(actionName);
			((InitializerIFace) action).init(actionConfig);
			return action;
		} // if (actionScope.equalsIgnoreCase("REQUEST"))
		else if (actionScope.equalsIgnoreCase("SESSION")) {
			SessionContainer sessionContainer = requestContainer.getSessionContainer();
			ActionIFace action = (ActionIFace) sessionContainer.getAttribute("AF_ACTION_" + actionName);
			if (action == null) {
				synchronized (sessionContainer) {
					action = (ActionIFace) sessionContainer.getAttribute("AF_ACTION_" + actionName);
					if (action == null) {
						_logger.debug("ActionFactory::getAction: action non trovata in SESSION !");

						try {
							action = (ActionIFace) Class.forName(actionClass).newInstance();
						} // try
						catch (Exception ex) {
							it.eng.sil.util.TraceWrapper.debug(_logger,
									"ActionFactory::getAction: classe della action [" + actionName + "] non valida",
									ex);

							throw new ActionException("Classe della action [" + actionName + "] non valida");
						} // catch (Exception ex) try
						action.setAction(actionName);
						((InitializerIFace) action).init(actionConfig);
						sessionContainer.setAttribute("AF_ACTION_" + actionName, action);
					} // if (action == null)
				} // synchronized (sessionContainer)
			} // if (action == null)
			else
				_logger.debug("ActionFactory::getAction: action trovata in SESSION");

			return action;
		} // if (actionScope.equalsIgnoreCase("SESSION"))
		else {
			ApplicationContainer applicationContainer = ApplicationContainer.getInstance();
			ActionIFace action = (ActionIFace) applicationContainer.getAttribute("AF_ACTION_" + actionName);
			if (action == null) {
				synchronized (applicationContainer) {
					action = (ActionIFace) applicationContainer.getAttribute("AF_ACTION_" + actionName);
					if (action == null) {
						_logger.debug("ActionFactory::getAction: action non trovata in APPLICATION !");

						try {
							action = (ActionIFace) Class.forName(actionClass).newInstance();
						} // try
						catch (Exception ex) {
							it.eng.sil.util.TraceWrapper.debug(_logger,
									"ActionFactory::getAction: classe della action [" + actionName + "] non valida",
									ex);

							throw new ActionException("Classe della action [" + actionName + "] non valida");
						} // catch (Exception ex) try
						action.setAction(actionName);
						((InitializerIFace) action).init(actionConfig);
						applicationContainer.setAttribute("AF_ACTION_" + actionName, action);
					} // if (action == null)
				} // synchronized (applicationContainer)
			} // if (action == null)
			else
				_logger.debug("ActionFactory::getAction: action trovata in APPLICATION");

			return action;
		} // if (actionScope.equalsIgnoreCase("SESSION")) else
	} // public static ActionIFace getAction(RequestContainer
		// requestContainer) throws ActionException
} // public class ActionFactory
