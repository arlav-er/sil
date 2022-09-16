package com.engiweb.framework.tags;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import com.engiweb.framework.base.ResponseContainer;
import com.engiweb.framework.base.ResponseContainerAccess;
import com.engiweb.framework.error.EMFErrorHandler;
import com.engiweb.framework.util.JavaScript;

public class DefaultErrorTag extends TagSupport {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(DefaultErrorTag.class.getName());

	public int doStartTag() throws JspException {
		_logger.debug("DefaultErrorTag::doStartTag:: invocato");

		if (pageContext == null) {
			_logger.debug("DefaultErrorTag::doStartTag:: pageContext nullo");

			throw new JspException("pageContext nullo");
		} // if (_httpRequest == null)
		HttpServletRequest httpRequest = null;
		try {
			httpRequest = (HttpServletRequest) pageContext.getRequest();
		} // try
		catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.debug(_logger, "DefaultErrorTag::doStartTag::", ex);

			throw new JspException(ex.getMessage());
		} // catch (Exception ex)
		if (httpRequest == null) {
			_logger.debug("DefaultErrorTag::doStartTag:: httpRequest nullo");

			throw new JspException("httpRequest nullo");
		} // if (_httpRequest == null)
		ResponseContainer responseContainer = ResponseContainerAccess.getResponseContainer(httpRequest);
		if (responseContainer == null) {
			_logger.debug("DefaultErrorTag::doStartTag:: responseContainer nullo");

			throw new JspException("responseContainer nullo");
		} // if (responseContainer == null)
		StringBuffer output = new StringBuffer();
		output.append("<SCRIPT language=\"Javascript\" type=\"text/javascript\">\n");
		output.append("<!--\n");
		output.append("function checkError() {\n");
		EMFErrorHandler engErrorHandler = responseContainer.getErrorHandler();
		if ((engErrorHandler != null) && (!engErrorHandler.isOK()))
			output.append("alert(\"" + JavaScript.escapeText(engErrorHandler.getStackTrace()) + "\");\n");
		output.append("}\n");
		output.append("// -->\n");
		output.append("</SCRIPT>\n");
		try {
			pageContext.getOut().print(output.toString());
		} // try
		catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.debug(_logger, "DefaultErrorTag::doStartTag::", ex);

			throw new JspException(ex.getMessage());
		} // catch (Exception ex)
		return SKIP_BODY;
	} // public int doStartTag() throws JspException

	/**
	 * @see javax.servlet.jsp.tagext.Tag#doEndTag()
	 */
	public int doEndTag() throws JspException {
		_logger.debug("DefaultErrorTag::doEndTag:: invocato");

		return super.doEndTag();
	} // public int doEndTag() throws JspException
} // public class DefaultErrorTag extends TagSupport
