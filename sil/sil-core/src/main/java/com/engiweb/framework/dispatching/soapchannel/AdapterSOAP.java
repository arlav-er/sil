package com.engiweb.framework.dispatching.soapchannel;

import java.util.ArrayList;

import com.engiweb.framework.base.Constants;
import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.ResponseContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dispatching.action.NavigationErrorAction;
import com.engiweb.framework.dispatching.coordinator.CoordinatorIFace;
import com.engiweb.framework.dispatching.coordinator.DispatcherManager;
import com.engiweb.framework.dispatching.service.DefaultRequestContext;
import com.engiweb.framework.dispatching.service.RequestContextIFace;
import com.engiweb.framework.error.EMFErrorHandler;
import com.engiweb.framework.error.EMFErrorSeverity;
import com.engiweb.framework.error.EMFInternalError;
import com.engiweb.framework.navigation.NavigationException;
import com.engiweb.framework.navigation.Navigator;
import com.engiweb.framework.presentation.PresentationRendering;
import com.engiweb.framework.presentation.Publisher;
import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

public class AdapterSOAP {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(AdapterSOAP.class.getName());
	private RequestContainer _requestContainer = null;

	public AdapterSOAP() {
		_logger.debug("AdapterSOAP::AdapterSOAP: invocato");

		_requestContainer = null;
	} // public ActionWebService()

	public String service(String request) {
		_logger.debug("AdapterSOAP::service: request\n" + request);

		Monitor monitor = MonitorFactory.start("controller.adapter.soap");
		try {
			SourceBean serviceRequest = SourceBean.fromXMLString(request);
			RequestContainer requestContainer = new RequestContainer();
			synchronized (this) {
				if (!Navigator.isNavigatorEnabled())
					if (_requestContainer == null)
						requestContainer.setSessionContainer(new SessionContainer(true));
					else
						requestContainer.setSessionContainer(_requestContainer.getSessionContainer());
				else {
					if (_requestContainer == null)
						requestContainer.setSessionContainer(new SessionContainer(true));
					else {
						requestContainer.setSessionContainer(new SessionContainer(false));
						requestContainer.setParent(_requestContainer);
					} // if (_requestContainer == null) else
				} // if (!Navigator.isNavigatorEnabled())
				_requestContainer = requestContainer;
			} // synchronized (this)
			requestContainer.setChannelType(Constants.SOAP_CHANNEL);
			requestContainer.setServiceRequest(serviceRequest);
			it.eng.sil.util.TraceWrapper.debug(_logger, "AdapterSOAP::service: requestContainer", requestContainer);

			it.eng.sil.util.TraceWrapper.debug(_logger, "AdapterSOAP::service: sessionContainer",
					requestContainer.getSessionContainer());

			ResponseContainer responseContainer = new ResponseContainer();
			EMFErrorHandler emfErrorHandler = new EMFErrorHandler();
			responseContainer.setErrorHandler(emfErrorHandler);
			SourceBean serviceResponse = new SourceBean(Constants.SERVICE_RESPONSE);
			responseContainer.setServiceResponse(serviceResponse);
			try {
				Navigator.checkNavigation(requestContainer);
			} // try
			catch (NavigationException ne) {
				it.eng.sil.util.TraceWrapper.fatal(_logger, "AdapterSOAP::service: ", ne);

				requestContainer.setServiceRequest(NavigationErrorAction.getNavigationErrorServiceRequest());
			} // catch (NavigationException ne)
			serviceRequest = requestContainer.getServiceRequest();
			RequestContextIFace requestContext = new DefaultRequestContext(requestContainer, responseContainer);
			CoordinatorIFace coordinator = DispatcherManager.getCoordinator(requestContext);
			Exception serviceException = null;
			if (coordinator == null) {
				_logger.warn("AdapterSOAP::service: coordinator nullo !");

				serviceException = new Exception("Coordinatore non trovato");
				emfErrorHandler.addError(new EMFInternalError(EMFErrorSeverity.ERROR, "Coordinatore non trovato !"));
			} // if (coordinator == null)
			else {
				RequestContainer.setRequestContainer(requestContainer);
				((RequestContextIFace) coordinator).setRequestContext(requestContext);
				try {
					coordinator.service(serviceRequest, serviceResponse);
				} // try
				catch (Exception ex) {
					it.eng.sil.util.TraceWrapper.fatal(_logger, "AdapterSOAP::service:", ex);

					serviceException = ex;
					emfErrorHandler.addError(new EMFInternalError(EMFErrorSeverity.ERROR, ex));
				} // catch (Exception ex)
				((RequestContextIFace) coordinator).setRequestContext(null);
				RequestContainer.delRequestContainer();
				responseContainer.setBusinessType(coordinator.getBusinessType());
				responseContainer.setBusinessName(coordinator.getBusinessName());
			} // if (coordinator == null) else
			// try {
			// EventExecutor.service(requestContainer, responseContainer);
			// } // try
			// catch (Exception ex) {
			// it.eng.sil.util.TraceWrapper.fatal(_logger, "AdapterSOAP::service:", ex);
			//
			// emfErrorHandler.addError(new EMFInternalError(EMFErrorSeverity.ERROR, ex));
			// } // catch (Exception ex)
			it.eng.sil.util.TraceWrapper.debug(_logger, "AdapterSOAP::service: responseContainer", responseContainer);

			it.eng.sil.util.TraceWrapper.debug(_logger, "AdapterSOAP::service: sessionContainer",
					requestContainer.getSessionContainer());

			String publisherType = null;
			ArrayList resources = null;
			Publisher publisher = Publisher.getPublisher(requestContainer, responseContainer, serviceException);
			if (publisher == null) {
				publisherType = Constants.NOTHING_PUBLISHER_TYPE;
				resources = null;
			} // if (publisher == null)
			else {
				publisherType = publisher.getType();
				if ((publisherType == null) || (publisherType.equalsIgnoreCase(Constants.NOTHING_PUBLISHER_TYPE))) {
					publisherType = Constants.NOTHING_PUBLISHER_TYPE;
					resources = null;
				} // if ((publisherType == null) ||
					// (publisherType.equalsIgnoreCase(Constants.NOTHING_PUBLISHER_TYPE)))
				else {
					resources = publisher.getResources();
					if ((resources == null) || (resources.size() == 0)) {
						publisherType = Constants.NOTHING_PUBLISHER_TYPE;
						resources = null;
					} // if ((resources == null) || (resources.size() == 0))
				} // if ((publisherType == null) ||
					// (publisherType.equalsIgnoreCase(Constants.NOTHING_PUBLISHER_TYPE)))
					// else
			} // if (publisher == null) else
			Monitor renderingMonitor = null;
			if (publisher == null)
				renderingMonitor = MonitorFactory
						.start("view." + Constants.SOAP_CHANNEL.toLowerCase() + "." + publisherType.toLowerCase());
			else
				renderingMonitor = MonitorFactory.start("view." + Constants.SOAP_CHANNEL.toLowerCase() + "."
						+ publisherType.toLowerCase() + "." + publisher.getName().toLowerCase());
			String response = null;
			try {
				response = PresentationRendering.render(responseContainer, resources);
			} // try
			catch (Exception ex) {
				throw ex;
			} // catch (Exception ex)
			finally {
				renderingMonitor.stop();
			} // finally
			return response;
		} // try
		catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.fatal(_logger, "AdapterSOAP::service:", ex);

			/*
			 * StringWriter exStringWriter = new StringWriter(); PrintWriter exPrintWriter = new
			 * PrintWriter(exStringWriter); ex.printStackTrace(exPrintWriter);
			 */

			return "";
		} // catch (Excpetion ex) try
		finally {
			monitor.stop();
		} // finally
	} // public String service(String request)
} // public class AdapterSOAP
