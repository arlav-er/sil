package com.engiweb.framework.tags;

import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.RequestContainerAccess;
import com.engiweb.framework.base.ResponseContainer;
import com.engiweb.framework.base.ResponseContainerAccess;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.configuration.ConfigSingleton;
import com.engiweb.framework.dbaccess.sql.DataRow;
import com.engiweb.framework.dispatching.service.detail.impl.DelegatedDetailService;
import com.engiweb.framework.util.ContextScooping;
import com.engiweb.framework.util.JavaScript;

public class DefaultDetailTag extends TagSupport {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(DefaultDetailTag.class.getName());
	protected String _actionName = null;
	protected String _moduleName = null;
	protected String _serviceName = null;
	protected SourceBean _content = null;
	protected SourceBean _layout = null;
	protected String _providerURL = null;

	public int doStartTag() throws JspException {
		_logger.debug("DefaultDetailTag::doStartTag:: invocato");

		HttpServletRequest httpRequest = (HttpServletRequest) pageContext.getRequest();
		RequestContainer requestContainer = RequestContainerAccess.getRequestContainer(httpRequest);
		SourceBean serviceRequest = requestContainer.getServiceRequest();
		ResponseContainer responseContainer = ResponseContainerAccess.getResponseContainer(httpRequest);
		SourceBean serviceResponse = responseContainer.getServiceResponse();
		ConfigSingleton configure = ConfigSingleton.getInstance();
		if (_actionName != null) {
			_serviceName = _actionName;
			_content = serviceResponse;
			SourceBean actionBean = (SourceBean) configure.getFilteredSourceBeanAttribute("ACTIONS.ACTION", "NAME",
					_actionName);
			_layout = (SourceBean) actionBean.getAttribute("CONFIG");
			_providerURL = "ACTION_NAME=" + _actionName + "&";
		} // if (_actionName != null)
		else if (_moduleName != null) {
			_serviceName = _moduleName;
			_content = (SourceBean) serviceResponse.getAttribute(_moduleName);
			SourceBean moduleBean = (SourceBean) configure.getFilteredSourceBeanAttribute("MODULES.MODULE", "NAME",
					_moduleName);
			_layout = (SourceBean) moduleBean.getAttribute("CONFIG");
			String pageName = (String) serviceRequest.getAttribute("PAGE");
			_providerURL = "PAGE=" + pageName + "&MODULE=" + _moduleName + "&";
		} // if (_moduleName != null)
		else {
			_logger.fatal("DefaultDetailTag::doStartTag:: service name non specificato !");

			throw new JspException("Business name non specificato !");
		} // if (_content == null)
		if (_content == null) {
			_logger.warn("DefaultDetailTag::doStartTag:: content nullo");

			return SKIP_BODY;
		} // if (_content == null)
		if (_layout == null) {
			_logger.warn("DefaultDetailTag::doStartTag:: layout nullo");

			return SKIP_BODY;
		} // if (_layout == null)
		StringBuffer output = new StringBuffer();
		generateModuleHeader(output, _layout, requestContainer, responseContainer, _content);
		generateFormHeader(output, _layout, requestContainer, responseContainer, _content);
		generateFields(output, _layout, requestContainer, responseContainer, _content);
		generateButtons(output, _layout, requestContainer, responseContainer, _content);
		generateFormFooter(output, _layout, requestContainer, responseContainer, _content);
		generateModuleFooter(output, _layout, requestContainer, responseContainer, _content);
		try {
			pageContext.getOut().print(output.toString());
		} // try
		catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.fatal(_logger, "DefaultDetailTag::doStartTag::", ex);

			throw new JspException(ex.getMessage());
		} // catch (Exception ex)
		return SKIP_BODY;
	} // public int doStartTag() throws JspException

	protected void generateModuleHeader(StringBuffer output, SourceBean _layout, RequestContainer requestContainer,
			ResponseContainer responseContainer, SourceBean _content) throws JspException {
		String serviceMode = (String) _content.getAttribute(DelegatedDetailService.SERVICE_MODE);
		String fieldMode = null;
		SourceBean row = null;
		if ((serviceMode == null) || (serviceMode.equalsIgnoreCase(DelegatedDetailService.SERVICE_MODE_INSERT)))
			fieldMode = "INSERT";
		else {
			row = (SourceBean) _content.getAttribute(DataRow.ROW_TAG);
			if (row == null) {
				fieldMode = "INSERT";
				_logger.warn("DefaultDetailTag::generateFields:: row nullo");

			} // if (row == null)
			else
				fieldMode = "UPDATE";
		} // if ((serviceMode == null) ||
		output.append("<SCRIPT language=\"Javascript\" type=\"text/javascript\">\n");
		output.append("<!--\n");
		output.append("function confirm" + _serviceName + "(message, url) {\n");
		output.append("if (confirm(message))\n");
		output.append("window.location = url;\n");
		output.append("}\n");
		output.append("function check" + _serviceName + "(toConfirm) {\n");
		Vector fields = _layout.getAttributeAsVector("FIELDS.FIELD");
		for (int i = 0; i < fields.size(); i++) {
			SourceBean field = (SourceBean) fields.elementAt(i);
			SourceBean fieldProperties = (SourceBean) field.getAttribute(fieldMode);
			String fieldName = (String) field.getAttribute("NAME");
			String fieldLabel = (String) field.getAttribute("LABEL");
			String fieldIsMandatoryString = (String) fieldProperties.getAttribute("IS_MANDATORY");
			boolean fieldIsMandatory = true;
			if ((fieldIsMandatoryString != null) && (fieldIsMandatoryString.equalsIgnoreCase("FALSE")))
				fieldIsMandatory = false;
			if (fieldIsMandatory) {
				output.append("if (document." + _serviceName + "." + fieldName + ".value == \"\") {\n");
				output.append("alert(\"Il campo " + fieldLabel + " è obbligatorio\");\n");
				output.append("return;\n");
				output.append("}\n");
			} // if (fieldIsMandatory)
		} // for (int i = 0; i < fields.size(); i++)
		output.append("if (toConfirm) {\n");
		output.append("if (confirm(\"Confermi i dati inseriti ?\"))\n");
		output.append("document." + _serviceName + ".submit();\n");
		output.append("}\n");
		output.append("else\n");
		output.append("document." + _serviceName + ".submit();\n");
		output.append("}\n");
		output.append("// -->\n");
		output.append("</SCRIPT>\n");
		output.append("<H2>" + (String) _layout.getAttribute("TITLE") + "</H2>\n");
		output.append("<BR>\n");
	} // protected void generateModuleHeader(StringBuffer output, SourceBean

	protected void generateFormHeader(StringBuffer output, SourceBean _layout, RequestContainer requestContainer,
			ResponseContainer responseContainer, SourceBean _content) throws JspException {
		String serviceMode = (String) _content.getAttribute(DelegatedDetailService.SERVICE_MODE);
		String message = DelegatedDetailService.DETAIL_UPDATE;
		if ((serviceMode == null) || (serviceMode.equalsIgnoreCase(DelegatedDetailService.SERVICE_MODE_INSERT)))
			message = DelegatedDetailService.DETAIL_INSERT;
		Vector submitButtonParameters = _layout.getAttributeAsVector("BUTTONS.SUBMIT_BUTTON.PARAMETER");
		String submitButtonUrl = generateUrl(requestContainer, responseContainer, submitButtonParameters);
		String action = "AdapterHTTP?NAVIGATOR_DISABLED=TRUE&" + _providerURL + "MESSAGE=" + message + "&"
				+ submitButtonUrl;
		output.append("<FORM");
		output.append(" class=\"detail\"");
		output.append(" name=\"" + _serviceName + "\"");
		output.append(" method=\"post\"");
		output.append(" action=\"" + action + "\"");
		output.append(">\n");
		output.append("<TABLE class=\"detail\" border=\"0\" cellpadding=\"5\" cellspacing=\"0\">\n");
	} // protected void generateFormHeader(StringBuffer output, SourceBean

	protected void generateFields(StringBuffer output, SourceBean _layout, RequestContainer requestContainer,
			ResponseContainer responseContainer, SourceBean _content) throws JspException {
		String serviceMode = (String) _content.getAttribute(DelegatedDetailService.SERVICE_MODE);
		String fieldMode = null;
		SourceBean row = (SourceBean) _content.getAttribute(DataRow.ROW_TAG);
		if ((serviceMode == null) || (serviceMode.equalsIgnoreCase(DelegatedDetailService.SERVICE_MODE_INSERT)))
			fieldMode = "INSERT";
		else {
			if (row == null) {
				fieldMode = "INSERT";
				_logger.warn("DefaultDetailTag::generateFields:: row nullo");

			} // if (row == null)
			else
				fieldMode = "UPDATE";
		} // if ((serviceMode == null) ||
		Vector fields = _layout.getAttributeAsVector("FIELDS.FIELD");
		for (int i = 0; i < fields.size(); i++) {
			SourceBean field = (SourceBean) fields.elementAt(i);
			SourceBean fieldProperties = (SourceBean) field.getAttribute(fieldMode);
			String fieldName = (String) field.getAttribute("NAME");
			String fieldLabel = (String) field.getAttribute("LABEL");
			String fieldSizeString = (String) field.getAttribute("SIZE");
			int fieldSize = Integer.parseInt(fieldSizeString);
			String fieldIsReadOnlyString = (String) fieldProperties.getAttribute("IS_READONLY");
			boolean fieldIsReadOnly = false;
			if ((fieldIsReadOnlyString != null) && (fieldIsReadOnlyString.equalsIgnoreCase("TRUE")))
				fieldIsReadOnly = true;
			String fieldIsMandatoryString = (String) fieldProperties.getAttribute("IS_MANDATORY");
			boolean fieldIsMandatory = true;
			if ((fieldIsMandatoryString != null) && (fieldIsMandatoryString.equalsIgnoreCase("FALSE")))
				fieldIsMandatory = false;
			String fieldIsVisibleString = (String) fieldProperties.getAttribute("IS_VISIBLE");
			boolean fieldIsVisible = true;
			if ((fieldIsVisibleString != null) && (fieldIsVisibleString.equalsIgnoreCase("FALSE")))
				fieldIsVisible = false;
			String fieldValue = null;
			if ((fieldMode.equalsIgnoreCase("INSERT")) && (row == null)) {
				String defaultType = (String) fieldProperties.getAttribute("DEFAULT_TYPE");
				String defaultValue = (String) fieldProperties.getAttribute("DEFAULT_VALUE");
				String defaultScope = (String) fieldProperties.getAttribute("DEFAULT_SCOPE");
				if (defaultType.equalsIgnoreCase("ABSOLUTE"))
					fieldValue = defaultValue;
				else {
					Object fieldValueObject = ContextScooping.getScopedParameter(requestContainer, responseContainer,
							defaultValue, defaultScope);
					if (fieldValueObject != null)
						fieldValue = fieldValueObject.toString();
				} // if (defaultType.equalsIgnoreCase("ABSOLUTE")) else
			} // if ((fieldMode.equalsIgnoreCase("INSERT")) && (row == null))
			else {
				Object fieldValueObject = row.getAttribute(fieldName);
				if (fieldValueObject != null)
					fieldValue = fieldValueObject.toString();
			} // if (defaultType.equalsIgnoreCase("ABSOLUTE")) else
			if (fieldValue == null)
				fieldValue = "";
			output.append("<TR class=\"detail\">\n");
			if (fieldIsReadOnly)
				fieldLabel = "<I>" + fieldLabel + "</I>\n";
			if (fieldIsMandatory)
				fieldLabel = "<B>" + fieldLabel + "</B>\n";
			output.append("<TD class=\"detail\">" + fieldLabel + "</TD>\n");
			output.append("<TD class=\"detail\"><INPUT");
			output.append(" name=\"" + fieldName + "\"");
			output.append(" type=\"text\"");
			output.append(" size=\"" + fieldSize + "\"");
			output.append(" value=\"" + fieldValue + "\"");
			if (fieldIsReadOnly)
				output.append(" readonly");
			output.append("/></TD>\n");
			output.append("</TR>\n");
		} // for (int i = 0; i < fields.size(); i++)
	} // protected void generateFields(StringBuffer output, SourceBean

	protected void generateButtons(StringBuffer output, SourceBean _layout, RequestContainer requestContainer,
			ResponseContainer responseContainer, SourceBean _content) throws JspException {
		output.append("<TR><TD><BR></TD><TD><BR></TD></TR>\n");
		output.append("<TR>\n");
		output.append("<TD>");
		SourceBean submitButton = (SourceBean) _layout.getAttribute("BUTTONS.SUBMIT_BUTTON");
		generateButton(output, requestContainer, responseContainer, submitButton, true);
		output.append("</TD>\n");
		Vector buttons = _layout.getAttributeAsVector("BUTTONS.BUTTON");
		for (int i = 0; i < buttons.size(); i++) {
			output.append("<TD>");
			SourceBean button = (SourceBean) buttons.elementAt(i);
			generateButton(output, requestContainer, responseContainer, button, false);
			output.append("</TD>\n");
		} // for (int i = 0; i < buttons.size(); i++)
		output.append("</TR>\n");
	} // protected void generateButtons(StringBuffer output, SourceBean

	protected void generateFormFooter(StringBuffer output, SourceBean _layout, RequestContainer requestContainer,
			ResponseContainer responseContainer, SourceBean _content) throws JspException {
		output.append("</TABLE>\n");
		output.append("</FORM>\n");
		output.append("<BR>\n");
	} // protected void generateFormFooter(StringBuffer output, SourceBean

	protected void generateModuleFooter(StringBuffer output, SourceBean _layout, RequestContainer requestContainer,
			ResponseContainer responseContainer, SourceBean _content) throws JspException {
	} // protected void generateModuleFooter(StringBuffer output, SourceBean

	protected String generateUrl(RequestContainer requestContainer, ResponseContainer responseContainer,
			Vector parameters) {
		if (parameters == null)
			return "";
		StringBuffer url = new StringBuffer();
		for (int i = 0; i < parameters.size(); i++) {
			SourceBean parameter = (SourceBean) parameters.elementAt(i);
			String parameterType = (String) parameter.getAttribute("TYPE");
			String parameterValue = (String) parameter.getAttribute("VALUE");
			if ((parameterType != null) && (parameterType.equalsIgnoreCase("RELATIVE"))) {
				String parameterScope = (String) parameter.getAttribute("SCOPE");
				Object parameterValueObject = ContextScooping.getScopedParameter(requestContainer, responseContainer,
						parameterValue, parameterScope);
				if (parameterValueObject != null)
					parameterValue = parameterValueObject.toString();
			} // if ((parameterType != null) &&
			if (parameterValue != null) {
				String parameterName = (String) parameter.getAttribute("NAME");
				url.append(
						JavaScript.escape(parameterName.toUpperCase()) + "=" + JavaScript.escape(parameterValue) + "&");
			} // if (parameterValue != null)
		} // for (int i = 0; i < parameters.size(); i++)
		return url.toString();
	} // protected String generateUrl(RequestContainer requestContainer,

	protected void generateButton(StringBuffer output, RequestContainer requestContainer,
			ResponseContainer responseContainer, SourceBean button, boolean check) {
		String buttonImage = (String) button.getAttribute("IMAGE");
		String buttonLabel = (String) button.getAttribute("LABEL");
		String buttonConfirmString = (String) button.getAttribute("CONFIRM");
		boolean buttonConfirm = false;
		if ((buttonConfirmString != null) && (buttonConfirmString.equalsIgnoreCase("TRUE")))
			buttonConfirm = true;
		Vector parameters = button.getAttributeAsVector("PARAMETER");
		String buttonUrl = generateUrl(requestContainer, responseContainer, parameters);
		if ((buttonImage == null) || (buttonImage.length() == 0)) {
			output.append("<INPUT");
			output.append(" class=\"ButtonChangePage\"");
			output.append(" type=\"button\"");
			output.append(" value=\"" + buttonLabel + "\"");
			if (check)
				output.append(" onclick=\"check" + _serviceName + "(" + buttonConfirm + "); return false;\"");
			else if (buttonConfirm)
				output.append(" onclick=\"confirm" + _serviceName + "('Confermi l\\'operazione ?', 'AdapterHTTP?"
						+ buttonUrl + "'); return false;\"");
			else
				output.append(" onclick=\"window.location='AdapterHTTP?" + buttonUrl + "'; return false;\"");
			output.append("/>");
		} // if ((buttonImage == null) || (buttonImage.length() == 0))
		else {
			output.append("<A");
			output.append(" href=\"javascript://\"");
			if (check)
				output.append(" onclick=\"check" + _serviceName + "(" + buttonConfirm + ", '" + buttonUrl
						+ "'); return false;\"");
			else if (buttonConfirm)
				output.append(" onclick=\"confirm" + _serviceName + "('Confermi l\\'operazione ?', 'AdapterHTTP?"
						+ buttonUrl + "'); return false;\"");
			else
				output.append(" onclick=\"window.location='AdapterHTTP?" + buttonUrl + "'; return false;\"");
			output.append("><IMG");
			output.append(" src=\"" + buttonImage + "\"");
			output.append(" alt=\"" + buttonLabel + "\"");
			output.append("></A>");
		} // if ((buttonImage == null) || (buttonImage.length() == 0)) else
	} // protected void generateButton(StringBuffer output, RequestContainer

	public void setActionName(String actionName) {
		_logger.debug("DefaultDetailTag::setActionName:: actionName [" + actionName + "]");

		_actionName = actionName;
	} // public void setActionName(String actionName)

	public void setModuleName(String moduleName) {
		_logger.debug("DefaultDetailTag::setModuleName:: moduleName [" + moduleName + "]");

		_moduleName = moduleName;
	} // public void setModuleName(String moduleName)

	/**
	 * @see javax.servlet.jsp.tagext.Tag#doEndTag()
	 */
	public int doEndTag() throws JspException {
		_logger.debug("DefaultDetailTag::doEndTag:: invocato");

		_actionName = null;
		_moduleName = null;
		_serviceName = null;
		_content = null;
		_layout = null;
		_providerURL = null;
		return super.doEndTag();
	} // public int doEndTag() throws JspException
} // public class DefaultDetailTag extends TagSupport
