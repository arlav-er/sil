package com.engiweb.framework.tags;

import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.RequestContainerAccess;
import com.engiweb.framework.base.ResponseContainer;
import com.engiweb.framework.base.ResponseContainerAccess;
import com.engiweb.framework.navigation.LabelID;
import com.engiweb.framework.navigation.Navigator;

public class DefaultNavigationToolbarTag extends TagSupport {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(DefaultNavigationToolbarTag.class.getName());

	public static final String NAVIGATOR_SERVICE = "NAVIGATOR_SERVICE";

	public int doStartTag() throws JspException {
		_logger.debug("DefaultNavigationToolbarTag::doStartTag:: invocato");

		if (pageContext == null) {
			_logger.fatal("DefaultNavigationToolbarTag::doStartTag:: pageContext nullo");

			throw new JspException("pageContext nullo");
		} // if (_httpRequest == null)
		HttpServletRequest httpRequest = null;
		try {
			httpRequest = (HttpServletRequest) pageContext.getRequest();
		} // try
		catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.fatal(_logger, "DefaultNavigationToolbarTag::doStartTag::", ex);

			throw new JspException(ex.getMessage());
		} // catch (Exception ex)
		if (httpRequest == null) {
			_logger.fatal("DefaultNavigationToolbarTag::doStartTag:: httpRequest nullo");

			throw new JspException("httpRequest nullo");
		} // if (_httpRequest == null)
		ResponseContainer responseContainer = ResponseContainerAccess.getResponseContainer(httpRequest);
		if (responseContainer == null) {
			_logger.fatal("DefaultNavigationToolbarTag::doStartTag:: responseContainer nullo");

			throw new JspException("responseContainer nullo");
		} // if (responseContainer == null)
		StringBuffer output = new StringBuffer();

		RequestContainer requestContainer = RequestContainerAccess.getRequestContainer(httpRequest);

		generateToolbar(output, requestContainer, responseContainer);
		try {
			pageContext.getOut().print(output.toString());
		} // try
		catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.fatal(_logger, "DefaultNavigationToolbarTag::doStartTag::", ex);

			throw new JspException(ex.getMessage());
		} // catch (Exception ex)
		return SKIP_BODY;
	} // public int doStartTag() throws JspException

	public int doEndTag() throws JspException {
		_logger.debug("DefaultNavigationToolbarTag::doEndTag:: invocato");

		return super.doEndTag();
	} // public int doEndTag() throws JspException

	protected void generateToolbar(StringBuffer output, RequestContainer requestContainer,
			ResponseContainer responseContainer) throws JspException {
		output.append("<TABLE><tr>");
		Vector ordered = new Vector();
		RequestContainer container = requestContainer;
		if (container != null)
			container = (RequestContainer) container.getParent();
		while (container != null) {
			LabelID labelID = (LabelID) container.getAttribute(NAVIGATOR_SERVICE);
			if (labelID != null) {
				ordered.addElement(labelID);
			} // if (label != null)
			container = (RequestContainer) container.getParent();
		} // while (container != null)
			// scorro vettore in ordine inverso (da più vecchia a più recente) e
			// creo html
		for (int i = ordered.size() - 1; i >= 0; i--) {
			LabelID label = (LabelID) ordered.elementAt(i);
			output.append("<td><a href=\"AdapterHTTP?" + Navigator.NAVIGATOR_BACK_TO_SERVICE_ID + "=");
			output.append(label.getId());
			output.append("\" name =\"pagina\">");
			output.append(label.getLabel());
			output.append("</a> > </td>");
		} // for(int i = ordered.size()-1; i >= 0; i--)
		output.append("</tr></TABLE>");
	} // protected void generateToolbar(StringBuffer output, SourceBean
		// _layout, RequestContainer requestContainer,
} // public class DefaultNavigationToolbarTag extends TagSupport
