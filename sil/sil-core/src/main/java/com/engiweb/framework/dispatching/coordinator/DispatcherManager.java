package com.engiweb.framework.dispatching.coordinator;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Vector;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.configuration.ConfigSingleton;
import com.engiweb.framework.dispatching.service.RequestContextIFace;

public class DispatcherManager {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(DispatcherManager.class.getName());
	private static ArrayList _dispatchers = new ArrayList();

	private DispatcherManager() {
	} // private DispatcherManager()

	public static void registerDispatcher(DispatcherIFace dispatcher) {
		if (dispatcher == null) {
			_logger.debug("DispatcherManager::registerDispatcher: dispatcher nullo");

			return;
		} // if (dispatcher == null)
		_dispatchers.add(dispatcher);
	} // public static void registerDispatcher(DispatcherIFace dispatcher)

	public static CoordinatorIFace getCoordinator(RequestContextIFace requestContext) {
		RequestContainer requestContainer = requestContext.getRequestContainer();
		SourceBean serviceRequest = requestContainer.getServiceRequest();
		for (int i = 0; i < _dispatchers.size(); i++) {
			DispatcherIFace dispatcher = (DispatcherIFace) _dispatchers.get(i);
			if (dispatcher.acceptsURL(requestContext)) {
				_logger.debug("DispatcherManager::getCoordinator: dispatcher trovato ["
						+ dispatcher.getClass().getName() + "]");

				String distributeCoordinatorClass = null;
				String businessType = null;
				String businessName = null;
				if (!requestContainer.isRequestDistributed()) {
					businessType = dispatcher.getBusinessType(requestContext);
					businessName = dispatcher.getBusinessName(requestContext);
					ConfigSingleton configSingleton = ConfigSingleton.getInstance();
					Object distributionsObject = configSingleton.getFilteredSourceBeanAttribute(
							"DISTRIBUTIONS.DISTRIBUTION", "BUSINESS_NAME", businessName);
					if (distributionsObject != null) {
						Vector distributions = null;
						if (distributionsObject instanceof SourceBean) {
							distributions = new Vector();
							distributions.add(distributionsObject);
						} // if (distributionsObject instanceof SourceBean)
						else
							distributions = (Vector) distributionsObject;
						for (int j = 0; j < distributions.size(); j++) {
							SourceBean distribution = (SourceBean) distributions.get(j);
							String localBusinessType = (String) distribution.getAttribute("BUSINESS_TYPE");
							if ((localBusinessType != null) && (localBusinessType.equalsIgnoreCase(businessType))) {
								distributeCoordinatorClass = (String) distribution
										.getAttribute("DISTRIBUTE_COORDINATOR_CLASS");
								_logger.debug("DispatcherManager::getCoordinator: business distribuito ["
										+ distributeCoordinatorClass + "]");

								break;
							} // if ((localBusinessType != null) &&
								// (localBusinessType.equalsIgnoreCase(businessType)))
						} // for (int i = 0; i < distributions(); i++)
					} // if (distributionsObject != null)
				} // if (!requestContainer.isRequestDistributed())
				CoordinatorIFace coordinator = null;
				if (distributeCoordinatorClass == null)
					coordinator = dispatcher.getCoordinator(requestContext);
				else {
					try {
						Class distributeCoordinator = Class.forName(distributeCoordinatorClass);
						Class[] formalParameters = new Class[2];
						formalParameters[0] = String.class;
						formalParameters[1] = String.class;
						Constructor distributeCoordinatorConstructor = distributeCoordinator
								.getConstructor(formalParameters);
						Object[] actualParameters = new Object[2];
						actualParameters[0] = businessType;
						actualParameters[1] = businessName;
						coordinator = (CoordinatorIFace) distributeCoordinatorConstructor.newInstance(actualParameters);
					} // try
					catch (Exception ex) {
						it.eng.sil.util.TraceWrapper.debug(_logger,
								"DispatcherManager::getCoordinator: classe del coordinator distribuito non valida", ex);

						return null;
					} // catch (Exception ex) try
				} // if (distributeCoordinatorClass == null) else
				return coordinator;
			} // if (dispatcher.acceptsURL(request))
		} // for (int i = 0; i < _dispatchers.size(); i++)
		_logger.debug("DispatcherManager::getCoordinator: dispatcher non trovato");

		return null;
	} // public static CoordinatorIFace getCoordinator(RequestContextIFace
		// requestContext)

	public static void deregisterDispatcher(DispatcherIFace dispatcher) {
		if (dispatcher == null) {
			_logger.debug("DispatcherManager::deregisterDispatcher: dispatcher nullo");

			return;
		} // if (dispatcher == null)
		_dispatchers.remove(dispatcher);
	} // public static void deregisterDispatcher(DispatcherIFace dispatcher)
} // public class DispatcherManager
