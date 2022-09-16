package com.engiweb.framework.presentation;

import java.util.ArrayList;
import java.util.Vector;

import com.engiweb.framework.base.Constants;
import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.ResponseContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.configuration.ConfigSingleton;
import com.engiweb.framework.init.InitializerIFace;

public class Publisher {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(Publisher.class.getName());
	private String _name = null;
	private String _channel = null;
	private String _type = null;
	private String _mode = null;
	private ArrayList _resources = null;

	public Publisher() {
		super();
		_name = null;
		_channel = null;
		_type = Constants.NOTHING_PUBLISHER_TYPE;
		_mode = null;
		_resources = new ArrayList();
	} // public Publisher()

	public String getName() {
		return _name;
	} // public String getName()

	public void setName(String name) {
		_name = name;
	} // public void setName(String name)

	public String getChannel() {
		return _channel;
	} // public String getChannel()

	public void setChannel(String channel) {
		_channel = channel;
	} // public void setChannel(String channel)

	public String getType() {
		return _type;
	} // public String getType()

	public void setType(String type) {
		if (type == null)
			_type = Constants.NOTHING_PUBLISHER_TYPE;
		else
			_type = type;
	} // public void setType(String type)

	public String getMode() {
		return _mode;
	} // public String getMode()

	public void setMode(String mode) {
		_mode = mode;
	} // public void setMode(String mode)

	public ArrayList getResources() {
		return _resources;
	} // public ArrayList getResources()

	// Modifica Monica 15/01/2004 - inizio
	// ArrayList di SourceBean anziché di String
	public void addResource(SourceBean resource) {
		if (resource == null)
			return;
		_resources.add(resource);
	} // public void addResource(String resource)

	// Modifica Monica 15/01/2004 - fine

	public static Publisher getPublisher(RequestContainer requestContainer, ResponseContainer responseContainer,
			Exception serviceException) {
		String publisherName = null;
		if (serviceException != null)
			if (serviceException instanceof SecurityException)
				publisherName = "SECURITY_ERROR_PUBLISHER";
			else
				publisherName = "SERVICE_ERROR_PUBLISHER";
		else {

			// TracerSingleton.log(// Constants.NOME_MODULO,//
			// TracerSingleton.DEBUG,// "Publisher::getPublisher: invocato");

			String businessType = responseContainer.getBusinessType();

			if (businessType == null) {
				_logger.warn("Publisher::getPublisher: businessType nullo (manca l'attributo PAGE/ACTION)");

				return null;
			} // if (businessType == null)

			// TracerSingleton.log(// Constants.NOME_MODULO,//
			// TracerSingleton.DEBUG,// "Publisher::getPublisher: businessType
			// [" + businessType + "]");

			String businessName = responseContainer.getBusinessName();
			if (businessName == null) {
				_logger.warn("Publisher::getPublisher: businessName nullo (manca il nome della PAGE/ACTION)");

				return null;
			} // if (businessName == null)

			_logger.debug("Publisher::getPublisher: businessName [" + businessName + "]");

			ConfigSingleton configSingleton = ConfigSingleton.getInstance();
			Object mappingsObject = configSingleton.getFilteredSourceBeanAttribute("PRESENTATION.MAPPING",
					"BUSINESS_NAME", businessName);
			if (mappingsObject == null) {
				_logger.warn("Publisher::getPublisher: mapping non trovato");

				return null;
			} // if (mappingsObject == null)
			Vector mappings = null;
			if (mappingsObject instanceof SourceBean) {
				mappings = new Vector();
				mappings.add(mappingsObject);
			} // if (mappingsObject instanceof SourceBean)
			else
				mappings = (Vector) mappingsObject;
			for (int i = 0; i < mappings.size(); i++) {
				SourceBean mapping = (SourceBean) mappings.get(i);
				String localBusinessType = (String) mapping.getAttribute("BUSINESS_TYPE");
				if ((localBusinessType != null) && (localBusinessType.equalsIgnoreCase(businessType))) {
					publisherName = (String) mapping.getAttribute("PUBLISHER_NAME");
					break;
				} // if ((localBusinessName != null) &&
					// (localBusinessName.equalsIgnoreCase(businessName)))
			} // for (int i = 0; i < mappings.size(); i++)
		} // if (serviceError)
		Publisher publisher = getPublisher(requestContainer, responseContainer, publisherName);
		while ((publisher != null) && (publisher.getType() != null) && (publisher.getType().equalsIgnoreCase("JAVA"))) {
			ArrayList resources = publisher.getResources();
			if (resources.size() == 0) {
				_logger.warn("Publisher::getPublisher: classe del publisher [" + publisherName + "] non specificata");

				return null;
			} // if (resources.size() == 0)

			// Modifica Monica 15/01/2004 - inizio
			// recupero il SourceBean della busta ITEM, contenente la busta
			// CONFIG
			String publisherDispatcherClass = (String) ((SourceBean) resources.get(0)).getAttribute("resource");
			_logger.debug("Publisher::getPublisher: publisherDispatcherClass [" + publisherDispatcherClass + "]");

			PublisherDispatcherIFace publisherDispatcher = null;
			try {
				publisherDispatcher = (PublisherDispatcherIFace) Class.forName(publisherDispatcherClass).newInstance();
			} // try
			catch (Exception ex) {
				it.eng.sil.util.TraceWrapper.fatal(_logger, "Publisher::getPublisher: classe del publisher non valida",
						ex);

				return null;
			} // catch (Exception ex) try
			if (publisherDispatcher instanceof InitializerIFace) {
				SourceBean bustaConfig = (SourceBean) ((SourceBean) resources.get(0)).getAttribute("CONFIG");
				((InitializerIFace) publisherDispatcher).init(bustaConfig);
			}
			// Modifica Monica 15/01/2004 - fine
			publisherName = publisherDispatcher.getPublisherName(requestContainer, responseContainer);
			publisher = getPublisher(requestContainer, responseContainer, publisherName);
		} // while ((publisher != null) && (publisher.getType() != null)
			// && (publisher.getType().equalsIgnoreCase("JAVA")))
		return publisher;
	} // public static Publisher getPublisher(RequestContainer
		// requestContainer, ResponseContainer responseContainer, boolean
		// serviceError)

	private static Publisher getPublisher(RequestContainer requestContainer, ResponseContainer responseContainer,
			String publisherName) {
		if (publisherName == null) {
			_logger.warn("Publisher::getPublisher: mapping non trovato");

			return null;
		} // if (publisherName == null)
		_logger.debug("Publisher::getPublisher: publisherName [" + publisherName + "]");

		ConfigSingleton configSingleton = ConfigSingleton.getInstance();
		SourceBean publisherConfig = (SourceBean) configSingleton.getFilteredSourceBeanAttribute("PUBLISHERS.PUBLISHER",
				"NAME", publisherName);
		if (publisherConfig == null) {
			_logger.warn("Publisher::getPublisher: publisher non trovato");

			return null;
		} // if (publisherConfig == null)
		String channelType = requestContainer.getChannelType();
		if (channelType == null) {
			_logger.warn("Publisher::getPublisher: channelType nullo");

			return null;
		} // if (channelType == null)

		// TracerSingleton.log(// Constants.NOME_MODULO,//
		// TracerSingleton.DEBUG,// "Publisher::getPublisher: channelType [" +
		// channelType + "]");

		SourceBean renderingConfig = (SourceBean) publisherConfig.getFilteredSourceBeanAttribute("RENDERING", "CHANNEL",
				channelType);
		if (renderingConfig == null) {
			_logger.warn("Publisher::getPublisher: publisher non trovato per il canale [" + channelType + "]");

			return null;
		} // if (renderingConfig == null)
		Publisher publisher = new Publisher();
		publisher.setName(publisherName);
		publisher.setChannel(channelType);
		publisher.setType((String) renderingConfig.getAttribute("TYPE"));
		// if (publisher
		// .getType()
		// .equalsIgnoreCase(Constants.LOOP_PUBLISHER_TYPE)) {
		// try {
		// SourceBean loopbackServiceRequest =
		// new SourceBean(Constants.SERVICE_REQUEST);
		// loopbackServiceRequest.setAttribute(
		// Navigator.NAVIGATOR_DISABLED,
		// "TRUE");
		// Vector resourcesConfig =
		// renderingConfig.getAttributeAsVector("RESOURCES.PARAMETER");
		// for (int j = 0; j < resourcesConfig.size(); j++) {
		// SourceBean consequence =
		// (SourceBean) resourcesConfig.get(j);
		// String parameterName =
		// (String) consequence.getAttribute("NAME");
		// String parameterScope =
		// (String) consequence.getAttribute("SCOPE");
		// String parameterType =
		// (String) consequence.getAttribute("TYPE");
		// String parameterValue =
		// (String) consequence.getAttribute("VALUE");
		// Object inParameterValue = null;
		// if (parameterType.equalsIgnoreCase("ABSOLUTE"))
		// inParameterValue = parameterValue;
		// else
		// inParameterValue =
		// ContextScooping.getScopedParameter(
		// requestContainer,
		// responseContainer,
		// parameterValue,
		// parameterScope);
		// if (inParameterValue == null)
		// continue;
		// if (inParameterValue instanceof SourceBean)
		// loopbackServiceRequest.setAttribute(
		// (SourceBean) inParameterValue);
		// else
		// loopbackServiceRequest.setAttribute(
		// parameterName,
		// inParameterValue);
		// } // for (int j = 0; j < consequences.size(); j++)
		// responseContainer.setLoopbackServiceRequest(
		// loopbackServiceRequest);
		// } // try
		// catch (SourceBeanException sbe) {
		// TracerSingleton.log(// Constants.NOME_MODULO,//
		// TracerSingleton.CRITICAL,// "Publisher::getPublisher:",// sbe);
		// } // catch (SourceBeanException sbe)
		// } // if
		// (publisher.getType().equalsIgnoreCase(Constants.LOOP_PUBLISHER_TYPE))
		// else {
		Vector resourcesConfig = renderingConfig.getAttributeAsVector("RESOURCES.ITEM");
		for (int i = 0; i < resourcesConfig.size(); i++) {
			SourceBean resourceConfig = (SourceBean) resourcesConfig.get(i);
			// Modifica Monica 15/01/2004 - inizio
			// String resource =
			// (String)resourceConfig.getAttribute("RESOURCE");
			// if (resource != null)
			publisher.addResource(resourceConfig);
			// Modifica Monica 15/01/2004 - fine
		} // for (int i = 0; i < resourcesConfig.size(); i++)
			// } // if ((publisher.getType() != null) &&
			// (publisher.getType().equalsIgnoreCase(Constants.LOOP_PUBLISHER_TYPE)))
			// else
		publisher.setMode((String) renderingConfig.getAttribute("MODE"));
		return publisher;
	} // private static Publisher getPublisher(RequestContainer
		// requestContainer, ResponseContainer responseContainer, String
		// publisherName)
} // public class Publisher
