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

public class ComboNavigationToolbarTag extends TagSupport {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(ComboNavigationToolbarTag.class.getName());

	public static final String NAVIGATOR_SERVICE = "NAVIGATOR_SERVICE";

	public int doStartTag() throws JspException {
		_logger.debug("DefaultNavigationToolbarTag::doStartTag:: invocato");

		if (pageContext == null) {
			_logger.fatal("ComboNavigationToolbarTag::doStartTag:: pageContext nullo");

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
			_logger.fatal("ComboNavigationToolbarTag::doStartTag:: httpRequest nullo");

			throw new JspException("httpRequest nullo");
		} // if (_httpRequest == null)
		ResponseContainer responseContainer = ResponseContainerAccess.getResponseContainer(httpRequest);
		if (responseContainer == null) {
			_logger.fatal("ComboNavigationToolbarTag::doStartTag:: responseContainer nullo");

			throw new JspException("responseContainer nullo");
		} // if (responseContainer == null)
		StringBuffer output = new StringBuffer();

		RequestContainer requestContainer = RequestContainerAccess.getRequestContainer(httpRequest);
		generateForm(output, requestContainer, responseContainer);
		try {
			pageContext.getOut().print(output.toString());
		} // try
		catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.fatal(_logger, "ComboNavigationToolbarTag", ex);

			throw new JspException(ex.getMessage());
		} // catch (Exception ex)
		return SKIP_BODY;
	} // public int doStartTag() throws JspException

	public int doEndTag() throws JspException {
		_logger.debug("ComboNavigationToolbarTag::doEndTag:: invocato");

		return super.doEndTag();
	} // public int doEndTag() throws JspException

	protected void generateForm(StringBuffer output, RequestContainer requestContainer,
			ResponseContainer responseContainer) throws JspException {
		generateJavaScript(output, requestContainer, responseContainer);
		output.append("<form name=\"navigation_combo\">\n");
		generateCombo(output, requestContainer, responseContainer);
		output.append("</form>\n");
	} // protected void generateForm(StringBuffer output, RequestContainer
		// requestContainer, ResponseContainer responseContainer) throws
		// JspException

	protected void generateJavaScript(StringBuffer output, RequestContainer requestContainer,
			ResponseContainer responseContainer) throws JspException {
		output.append("<SCRIPT language=\"Javascript\" type=\"text/javascript\"> \n");
		output.append("function goTo() { \n");
		output.append(
				"  window.location = document.navigation_combo.pagina.options[document.navigation_combo.pagina.selectedIndex].value; \n");
		output.append("} \n");
		output.append("</SCRIPT> \n");
	} // protected void generateJavaScript(StringBuffer output,
		// RequestContainer requestContainer, ResponseContainer
		// responseContainer) throws JspException

	protected void generateCombo(StringBuffer output, RequestContainer requestContainer,
			ResponseContainer responseContainer) throws JspException {
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
		if (ordered.size() != 0) {
			output.append("<TABLE><tr><td>\n");
			output.append("<select name =\"pagina\" onSelect=\"goTo();\">\n");
			// scorro vettore in ordine dalla più recente alla più vecchia e
			// creo html
			for (int i = 0; i < ordered.size(); i++) {
				LabelID label = (LabelID) ordered.elementAt(i);
				output.append(" <option value =\"AdapterHTTP?" + Navigator.NAVIGATOR_BACK_TO_SERVICE_ID + "=");
				output.append(label.getId());
				output.append("&\">");
				output.append(label.getLabel());
				output.append(" </option>\n");
			} // for(int i = 0; i < ordered.size(); i++)
			output.append("</select>");
			output.append("<input type=\"button\" value=\"Go\" onClick=\"goTo();\"></p>");
		} // if(ordered.size() != 0)
		output.append("</td></tr></TABLE>\n");
	} // protected void generateToolbar(StringBuffer output, SourceBean
		// _layout, RequestContainer requestContainer,
} // public class ComboNavigationToolbarTag extends TagSupport
