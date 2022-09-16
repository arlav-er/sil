package com.engiweb.framework.presentation;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.xml.transform.Templates;
import javax.xml.transform.stream.StreamResult;

import com.engiweb.framework.base.ApplicationContainer;
import com.engiweb.framework.base.ResponseContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.transcoding.Transcoder;

public class PresentationRendering {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(PresentationRendering.class.getName());

	public static String render(ResponseContainer responseContainer, ArrayList resources) {
		String toRender = null;
		try {
			SourceBean responseBean = new SourceBean("RESPONSE");
			responseBean.setAttribute(responseContainer.getServiceResponse());
			responseBean.setAttribute(responseContainer.getErrorHandler().getSourceBean());
			toRender = responseBean.toXML();
		} // try
		catch (SourceBeanException sbe) {
			it.eng.sil.util.TraceWrapper.fatal(_logger,
					"PresentationRendering::render: errore creazione stream RESPONSE", sbe);

			return "";
		} // catch (SourceBeanException sbe)
		if ((resources == null) || (resources.size() == 0)) {
			_logger.warn("PresentationRendering::render: resources nullo");

			return toRender;
		} // if ((resources == null) || (resources.size() == 0))
			// Modifica Monica 15/01/2004 - inizio
			// recupero il SourceBean della busta ITEM, contenente la busta CONFIG
		String stylesheetName = (String) ((SourceBean) resources.get(0)).getAttribute("resource");
		// Modifica Monica 15/01/2004 - fine

		if (stylesheetName == null) {
			_logger.warn("PresentationRendering::render: stylesheetName nullo");

			return toRender;
		} // if (stylesheetName == null)
		ApplicationContainer application = ApplicationContainer.getInstance();
		Hashtable xslTemplates = (Hashtable) application.getAttribute("XSL_TEMPLATES");
		if (xslTemplates == null) {
			_logger.fatal("PresentationRendering::render: xslTemplates nullo");

			return toRender;
		} // if (xslTemplates == null)
			// Recupero dalla cache il file contrassegnato dalla label
			// stylesheetLabel
		StylesheetFile stylesheet = (StylesheetFile) xslTemplates.get(stylesheetName);
		if (stylesheet == null) {
			_logger.fatal(
					"PresentationRendering::render: template di stylesheetName [" + stylesheetName + "] non trovato");

			return toRender;
		} // if (stylesheet == null)
		_logger.debug("PresentationRendering::render: template di stylesheetName [" + stylesheetName + "]");

		// Recupero la struttura XSL dall'oggetto StylesheetFile
		Templates template = stylesheet.getTemplate();
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		StreamResult streamResult = new StreamResult(byteArrayOutputStream);
		Transcoder.perform(toRender, template, streamResult);
		String result = byteArrayOutputStream.toString();
		_logger.debug("PresentationRendering::render: risultato trasformazione\n" + result);

		return result;
	} // public static String render(ResponseContainer responseContainer,
		// ArrayList resources)
} // public class PresentationRendering
