package com.engiweb.framework.tags;

import java.util.Hashtable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;
import javax.xml.transform.Result;
import javax.xml.transform.Templates;
import javax.xml.transform.stream.StreamResult;

import com.engiweb.framework.base.ApplicationContainer;
import com.engiweb.framework.base.ResponseContainer;
import com.engiweb.framework.base.ResponseContainerAccess;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.configuration.ConfigSingleton;
import com.engiweb.framework.presentation.StylesheetFile;
import com.engiweb.framework.transcoding.Transcoder;

public class ModuleTag extends TagSupport {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(ModuleTag.class.getName());

	public int doStartTag() throws JspException {
		_logger.debug("ModuleTag::doStartTag:: invocato");

		if (pageContext == null) {
			_logger.debug("ModuleTag::doStartTag:: pageContext nullo");

			throw new JspException("pageContext nullo");
		} // if (_httpRequest == null)
		HttpServletRequest httpRequest = null;
		try {
			httpRequest = (HttpServletRequest) pageContext.getRequest();
		} // try
		catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.debug(_logger, "ModuleTag::doStartTag::", ex);

			throw new JspException(ex.getMessage());
		} // catch (Exception ex)
		if (httpRequest == null) {
			_logger.debug("ModuleTag::doStartTag:: httpRequest nullo");

			throw new JspException("httpRequest nullo");
		} // if (_httpRequest == null)
		ResponseContainer responseContainer = ResponseContainerAccess.getResponseContainer(httpRequest);
		if (responseContainer == null) {
			_logger.debug("ModuleTag::doStartTag:: responseContainer nullo");

			throw new JspException("responseContainer nullo");
		} // if (responseContainer == null)
		SourceBean actionResponseBean = responseContainer.getServiceResponse();
		if (actionResponseBean == null) {
			_logger.debug("ModuleTag::doStartTag:: actionResponseBean nullo");

			throw new JspException("actionResponseBean nullo");
		} // if (actionResponseBean == null)
		SourceBean moduleResponseBean = (SourceBean) actionResponseBean.getAttribute(_moduleName);
		if (moduleResponseBean == null) {
			_logger.debug("ModuleTag::doStartTag:: moduleResponseBean nullo");

			throw new JspException("moduleResponseBean nullo");
		} // if (moduleResponseBean == null)
		String pageName = (String) actionResponseBean.getAttribute("PAGE");
		if (pageName == null) {
			_logger.debug("ModuleTag::doStartTag:: pageName nullo");

			throw new JspException("pageName nullo");
		} // if (pageName == null)
		ConfigSingleton configure = ConfigSingleton.getInstance();
		SourceBean page = (SourceBean) configure.getFilteredSourceBeanAttribute("PAGES.PAGE", "NAME", pageName);
		if (page == null) {
			_logger.debug("ModuleTag::doStartTag:: page nullo");

			throw new JspException("page nullo");
		} // if (page == null)
		String stylesheetLabel = (String) page.getAttribute("xsl");
		if (stylesheetLabel == null) {
			_logger.debug("ModuleTag::doStartTag:: stylesheetLabel nullo");

			throw new JspException("stylesheetLabel nullo");
		} // if (stylesheetLabel == null)
		ApplicationContainer applicationContainer = ApplicationContainer.getInstance();
		Hashtable xslTemplates = (Hashtable) applicationContainer.getAttribute("XSL_TEMPLATES");
		if (xslTemplates == null) {
			_logger.debug("ModuleTag::doStartTag:: xslTemplates nullo");

			throw new JspException("xslTemplates nullo");
		} // if (xslTemplates == null)
		StylesheetFile fileSS = (StylesheetFile) xslTemplates.get(stylesheetLabel);
		if (fileSS == null) {
			_logger.debug("ModuleTag::doStartTag: template nullo");

			throw new JspException("template nullo");
		} // if (fileSS == null)
		Templates template = fileSS.getTemplate();
		Result result = new StreamResult(pageContext.getOut());
		Transcoder.perform(moduleResponseBean.toXML(), template, result);
		return SKIP_BODY;
	} // public int doStartTag() throws JspException

	public void setModuleName(String moduleName) {
		_logger.debug("ModuleTag::setModuleName:: moduleName [" + moduleName + "]");

		_moduleName = moduleName;
	} // public void setModuleName(String moduleName)

	private String _moduleName = null;
} // public class ModuleTag extends TagSupport
