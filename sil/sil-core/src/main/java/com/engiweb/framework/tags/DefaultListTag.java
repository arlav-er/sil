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
import com.engiweb.framework.base.SourceBeanAttribute;
import com.engiweb.framework.base.XMLObject;
import com.engiweb.framework.configuration.ConfigSingleton;
import com.engiweb.framework.dispatching.service.detail.impl.DelegatedDetailService;
import com.engiweb.framework.dispatching.service.list.basic.impl.DelegatedBasicListService;
import com.engiweb.framework.paginator.smart.IFaceListItemAdapter;
import com.engiweb.framework.util.ContextScooping;
import com.engiweb.framework.util.JavaScript;

public class DefaultListTag extends TagSupport {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(DefaultListTag.class.getName());
	protected String _actionName = null;
	protected String _moduleName = null;
	protected IFaceListItemAdapter _adapter = null;
	protected String _serviceName = null;
	protected SourceBean _content = null;
	protected SourceBean _layout = null;
	protected String _providerURL = null;
	protected RequestContainer _requestContainer = null;
	protected SourceBean _serviceRequest = null;
	protected ResponseContainer _responseContainer = null;
	protected SourceBean _serviceResponse = null;
	protected StringBuffer _htmlStream = null;
	protected Vector _columns = null;

	public int doStartTag() throws JspException {
		_logger.debug("DefaultListTag::doStartTag:: invocato");

		HttpServletRequest httpRequest = (HttpServletRequest) pageContext.getRequest();
		_requestContainer = RequestContainerAccess.getRequestContainer(httpRequest);
		_serviceRequest = _requestContainer.getServiceRequest();
		_responseContainer = ResponseContainerAccess.getResponseContainer(httpRequest);
		_serviceResponse = _responseContainer.getServiceResponse();
		ConfigSingleton configure = ConfigSingleton.getInstance();
		if (_actionName != null) {
			_serviceName = _actionName;
			_content = _serviceResponse;
			SourceBean actionBean = (SourceBean) configure.getFilteredSourceBeanAttribute("ACTIONS.ACTION", "NAME",
					_actionName);
			_layout = (SourceBean) actionBean.getAttribute("CONFIG");
			_providerURL = "ACTION_NAME=" + _actionName + "&";
		} // if (_actionName != null)
		else if (_moduleName != null) {
			_serviceName = _moduleName;
			_content = (SourceBean) _serviceResponse.getAttribute(_moduleName);
			SourceBean moduleBean = (SourceBean) configure.getFilteredSourceBeanAttribute("MODULES.MODULE", "NAME",
					_moduleName);
			_layout = (SourceBean) moduleBean.getAttribute("CONFIG");
			String pageName = (String) _serviceRequest.getAttribute("PAGE");
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
		_htmlStream = new StringBuffer();
		makeForm();
		try {
			pageContext.getOut().print(_htmlStream);
		} // try
		catch (Exception ex) {
			_logger.fatal("DefaultListTag::doStartTag:: Impossibile inviare lo stream");

			throw new JspException("Impossibile inviare lo stream");
		} // catch (Exception ex)
		return SKIP_BODY;
	} // public int doStartTag() throws JspException

	protected void makeForm() throws JspException {
		_htmlStream.append("<FORM name=\"" + _serviceName + "\">\n");
		_htmlStream.append("<H2>" + (String) _layout.getAttribute("TITLE") + "</H2>\n");
		makeJavaScript();
		makeNavigationButton();
		_htmlStream.append("<TABLE class=\"lista\">\n");
		makeColumns();
		makeRows();
		_htmlStream.append("</TABLE>\n");
		_htmlStream.append("<TABLE class=\"lista\"><TR>\n");
		_htmlStream.append("<TD><BR></TD>\n");
		_htmlStream.append("</TR></TABLE>\n");
		makeButton();
		_htmlStream.append("</FORM>\n");
	} // protected void makeForm()

	protected void makeJavaScript() {
		_htmlStream.append("<SCRIPT language=\"Javascript\" type=\"text/javascript\">\n");
		_htmlStream.append("<!--\n");
		_htmlStream.append("function goConfirm" + _serviceName + "(url, alertFlag) {\n");
		_htmlStream.append("var _url = \"AdapterHTTP?\" + url;\n");
		_htmlStream.append("if (alertFlag == 'TRUE' ) {\n");
		_htmlStream.append("if (confirm('Confermi operazione'))\n");
		_htmlStream.append("window.location = _url;\n");
		_htmlStream.append("}\n");
		_htmlStream.append("else\n");
		_htmlStream.append("window.location = _url;\n");
		_htmlStream.append("}\n");
		_htmlStream.append("// -->\n");
		_htmlStream.append("</SCRIPT>\n");
	} // protected void makeJavaScript()

	protected void makeNavigationButton() throws JspException {
		String pageNumberString = (String) _content.getAttribute("PAGED_LIST.PAGE_NUMBER");
		int pageNumber = 1;
		try {
			pageNumber = Integer.parseInt(pageNumberString);
		} // / try
		catch (NumberFormatException ex) {
			_logger.warn("DefaultListTag::makeNavigationButton:: PAGE_NUMBER nullo");

		} // catch (NumberFormatException ex)
		String pagesNumberString = (String) _content.getAttribute("PAGED_LIST.PAGES_NUMBER");
		int pagesNumber = 1;
		try {
			pagesNumber = Integer.parseInt(pagesNumberString);
		} // / try
		catch (NumberFormatException ex) {
			_logger.warn("DefaultListTag::makeNavigationButton:: PAGES_NUMBER nullo");

		} // catch (NumberFormatException ex)
		int prevPage = pageNumber - 1;
		if (prevPage < 1)
			prevPage = 1;
		int nextPage = pageNumber + 1;
		if (nextPage > pagesNumber)
			nextPage = pagesNumber;
		String queryString = getQueryString();
		_htmlStream.append("<TABLE border=0><TR>\n");
		_htmlStream.append("<TD><INPUT type=\"Button\" class=\"ListButtonChangePage\" value=\"|<<\" onclick=\"goConfirm"
				+ _serviceName + "('" + queryString + _providerURL
				+ "MESSAGE=LIST_FIRST','FALSE'); return false;\"></TD>\n");
		_htmlStream.append("<TD><INPUT type=\"Button\" class=\"ListButtonChangePage\" value=\" < \" onclick=\"goConfirm"
				+ _serviceName + "('" + queryString + _providerURL + "MESSAGE=LIST_PAGE&LIST_PAGE=" + prevPage
				+ "','FALSE'); return false;\"></TD>\n");
		_htmlStream.append("<TD><INPUT type=\"Button\" class=\"ListButtonChangePage\" value=\" > \" onclick=\"goConfirm"
				+ _serviceName + "('" + queryString + _providerURL + "MESSAGE=LIST_PAGE&LIST_PAGE=" + nextPage
				+ "','FALSE'); return false;\"></TD>\n");
		_htmlStream.append("<TD><INPUT type=\"Button\" class=\"ListButtonChangePage\" value=\">>|\" onclick=\"goConfirm"
				+ _serviceName + "('" + queryString + _providerURL
				+ "MESSAGE=LIST_LAST','FALSE'); return false;\"></TD>\n");
		_htmlStream.append("<TD><B>&nbsp;Pag. " + pageNumber + " di " + pagesNumber + "<B></TD>\n");
		_htmlStream.append("</TR></TABLE>\n");
	} // protected void makeNavigationButton()

	protected void makeColumns() throws JspException {
		_htmlStream.append("<TR>\n");
		_htmlStream.append("<TH class=\"lista\">&nbsp;</TH>\n");
		_columns = _layout.getAttributeAsVector("COLUMNS.COLUMN");
		if ((_columns == null) || (_columns.size() == 0)) {
			_logger.fatal("DefaultListTag::makeColumns:: nomi delle colonne non definiti");

			throw new JspException("Nomi delle colonne non definiti");
		} // if ((_columns == null) || (_columns.size() == 0))
		for (int i = 0; i < _columns.size(); i++) {
			String nomeColonna = (String) ((SourceBean) _columns.elementAt(i)).getAttribute("LABEL");
			_htmlStream.append("<TH class=\"lista\">&nbsp;" + nomeColonna + "&nbsp;</TH>\n");
		} // for (int i = 0; i < _columns.size(); i++)
		_htmlStream.append("</TR>\n");
	} // protected void makeColumns() throws JspException

	protected void makeRows() throws JspException {
		ConfigSingleton configure = ConfigSingleton.getInstance();
		SourceBean selectCaption = (SourceBean) _layout.getAttribute("CAPTIONS.SELECT_CAPTION");
		SourceBean deleteCaption = (SourceBean) _layout.getAttribute("CAPTIONS.DELETE_CAPTION");
		Vector genericCaption = _layout.getAttributeAsVector("CAPTIONS.CAPTION");
		SourceBean rowsBean = (SourceBean) _content.getAttribute("PAGED_LIST.ROWS");
		Vector rows = rowsBean.getContainedAttributes();
		for (int i = 0; i < rows.size(); i++) {
			Object rowObject = ((SourceBeanAttribute) rows.elementAt(i)).getValue();
			SourceBean row = null;
			if (_adapter != null)
				row = _adapter.adapt(rowObject);
			else if (rowObject instanceof SourceBean)
				row = (SourceBean) rowObject;
			else if (rowObject instanceof XMLObject) {
				try {
					row = SourceBean.fromXMLString(((XMLObject) rowObject).toXML(false));
				} // try
				catch (Exception ex) {
					it.eng.sil.util.TraceWrapper.fatal(_logger, "DefaultListTag::makeRows: oggetto riga non valido",
							ex);

					throw new JspException("Oggetto riga non valido");
				} // catch (Exception ex)
			} // if (rowObject instanceof XMLObject)
			else {
				_logger.fatal("DefaultListTag::makeRows: oggetto riga non valido !");

				throw new JspException("Oggetto riga non valido");
			} // if (rowObject instanceof XMLObject) else
			_htmlStream.append("<TR class=\"lista\">\n");
			_htmlStream.append("<TD class=\"lista\">\n");
			_htmlStream.append("<TABLE border=0 cellpadding=0 cellspacing=0><TR>\n");
			if (selectCaption != null) {
				String labelSelect = (String) selectCaption.getAttribute("LABEL");
				if ((labelSelect == null) || (labelSelect.length() == 0))
					labelSelect = "Selezionare un dettaglio";
				String imageSelect = (String) selectCaption.getAttribute("IMAGE");
				if ((imageSelect == null) || (imageSelect.length() == 0))
					imageSelect = "../img/af/detail.gif";
				String confirmSelect = (String) selectCaption.getAttribute("CONFIRM");
				if ((confirmSelect == null) || (confirmSelect.length() == 0))
					confirmSelect = "FALSE";
				Vector parameters = selectCaption.getAttributeAsVector("PARAMETER");
				StringBuffer parametersStr = getParametersList(parameters, row);
				parametersStr.append("MESSAGE=" + DelegatedDetailService.DETAIL_SELECT + "&");
				_htmlStream.append(
						"<TD><A href=\"javascript://\" onclick=\"goConfirm" + _serviceName + "(" + "'" + parametersStr
								+ "'" + ", " + "'" + confirmSelect + "'" + "); return false;\"><IMG name=\"image\""
								+ " src=\"" + imageSelect + "\" alt=\"" + labelSelect + "\"/></A></TD>\n");
			} // if (selectCaption != null)
			if (deleteCaption != null) {
				String labelDelete = (String) deleteCaption.getAttribute("LABEL");
				if ((labelDelete == null) || (labelDelete.length() == 0))
					labelDelete = "Cancellare una riga";
				String imageDelete = (String) deleteCaption.getAttribute("IMAGE");
				if ((imageDelete == null) || (imageDelete.length() == 0))
					imageDelete = "../img/af/delete.gif";
				String confirmDelete = (String) deleteCaption.getAttribute("CONFIRM");
				if ((confirmDelete == null) || (confirmDelete.length() == 0))
					confirmDelete = "FALSE";
				Vector parameters = deleteCaption.getAttributeAsVector("PARAMETER");
				StringBuffer parametersStr = getParametersList(parameters, row);
				parametersStr.append("NAVIGATOR_DISABLED=TRUE&");
				parametersStr.append(_providerURL);
				parametersStr.append("MESSAGE=" + DelegatedBasicListService.LIST_DELETE + "&LIST_PAGE="
						+ _content.getAttribute("PAGED_LIST.PAGE_NUMBER"));
				_htmlStream.append(
						"<TD><A href=\"javascript://\" onclick=\"goConfirm" + _serviceName + "(" + "'" + parametersStr
								+ "'" + ", " + "'" + confirmDelete + "'" + "); return false;\"><IMG name=\"image\""
								+ "src=\"" + imageDelete + "\" alt=\" " + labelDelete + "\"/ ></A></TD>\n");
			} // if (deleteCaption != null)
			for (int j = 0; j < genericCaption.size(); j++) {
				SourceBean caption = (SourceBean) genericCaption.elementAt(j);
				String labelCaption = (String) caption.getAttribute("LABEL");
				if ((labelCaption == null) || (labelCaption.length() == 0))
					labelCaption = "";
				String imageCaption = (String) caption.getAttribute("IMAGE");
				if ((imageCaption == null) || (imageCaption.length() == 0))
					imageCaption = "";
				String confirmCaption = (String) caption.getAttribute("CONFIRM");
				if ((confirmCaption == null) || (confirmCaption.length() == 0))
					confirmCaption = "FALSE";
				Vector parameters = caption.getAttributeAsVector("PARAMETER");
				StringBuffer parametersStr = getParametersList(parameters, row);
				_htmlStream.append(
						"<TD><A href=\"javascript://\" onclick=\"goConfirm" + _serviceName + "(" + "'" + parametersStr
								+ "'" + ", " + "'" + confirmCaption + "'" + "); return false;\"><IMG name=\"image\" "
								+ "src=\"" + imageCaption + "\" alt=\"" + labelCaption + "\"/></A></TD>\n");
			} // for (int i = 0; i < genericCaption.size(); i++)
			_htmlStream.append("</TR></TABLE>\n");
			_htmlStream.append("</TD>\n");
			for (int j = 0; j < _columns.size(); j++) {
				String nomeColonna = (String) ((SourceBean) _columns.elementAt(j)).getAttribute("NAME");
				Object fieldObject = row.getAttribute(nomeColonna);
				String field = null;
				if (fieldObject != null)
					field = fieldObject.toString();
				else
					field = "&nbsp;";
				_htmlStream.append("<TD class=\"lista\"> " + field + "</TD>\n");
			} // for (int j = 0; j < _columns.size(); j++)
			_htmlStream.append("</TR>\n");
		} // for (int j = 0; j < rows.size(); j++)
	} // protected void makeRows()

	protected void makeButton() throws JspException {
		ConfigSingleton configure = ConfigSingleton.getInstance();
		SourceBean insertButton = (SourceBean) _layout.getAttribute("BUTTONS.INSERT_BUTTON");
		Vector genericButton = _layout.getAttributeAsVector("BUTTONS.BUTTON");
		_htmlStream.append("<TABLE classe=\"lista\"><TR>\n");
		if (insertButton != null) {
			String labelInsert = (String) insertButton.getAttribute("LABEL");
			if ((labelInsert == null) || (labelInsert.length() == 0))
				labelInsert = "NUOVO";
			String imageInsert = (String) insertButton.getAttribute("IMAGE");
			String confirmInsert = (String) insertButton.getAttribute("CONFIRM");
			if ((confirmInsert == null) || (confirmInsert.length() == 0))
				confirmInsert = "FALSE";
			Vector parameters = insertButton.getAttributeAsVector("PARAMETER");
			StringBuffer parametersStr = getParametersList(parameters, null);
			parametersStr.append("MESSAGE=" + DelegatedDetailService.DETAIL_NEW + "&");
			if ((imageInsert != null) && (imageInsert.length() > 0))
				_htmlStream.append(
						"<TD><A href=\"javascript://\" onclick=\"goConfirm" + _serviceName + "(" + "'" + parametersStr
								+ "'" + ", " + "'" + confirmInsert + "'" + "); return false;\"><IMG name=\"image\""
								+ " src=\"" + imageInsert + "\" alt=\"" + labelInsert + "\"/></A></TD>\n");
			else
				_htmlStream.append("<TD><INPUT type=\"Button\" class=\"ButtonChangePage\" value=\"" + labelInsert
						+ "\" onclick=\"goConfirm" + _serviceName + "(" + "'" + parametersStr + "'" + ", " + "'"
						+ confirmInsert + "'" + "); return false\"></TD>\n");
		} // if(insertButton != null )
		for (int i = 0; i < genericButton.size(); i++) {
			SourceBean button = (SourceBean) genericButton.elementAt(i);
			String labelButton = (String) button.getAttribute("LABEL");
			if ((labelButton == null) || (labelButton.length() == 0))
				labelButton = "BOTTONE";
			String imageButton = (String) button.getAttribute("IMAGE");
			String confirmButton = (String) button.getAttribute("CONFIRM");
			if ((confirmButton == null) || (confirmButton.length() == 0))
				confirmButton = "FALSE";
			Vector parameters = button.getAttributeAsVector("PARAMETER");
			StringBuffer parametersStr = getParametersList(parameters, null);
			if ((imageButton != null) && (imageButton.length() > 0))
				_htmlStream.append(
						"<TD><A href=\"javascript://\" onclick=\"goConfirm" + _serviceName + "(" + "'" + parametersStr
								+ "'" + ", " + "'" + confirmButton + "'" + "); return false;\"><IMG name=\"image\""
								+ " src=\"" + imageButton + "\" alt=\"" + labelButton + "\"/></A></TD>\n");
			else
				_htmlStream.append("<TD><INPUT type=\"Button\" class=\"ButtonChangePage\" value=\"" + labelButton
						+ "\" onclick=\"goConfirm" + _serviceName + "(" + "'" + parametersStr + "'" + ", " + "'"
						+ confirmButton + "'" + "); return false\"></TD>\n");
		} // for (int i = 0; i < genericButton.size(); i++)
		_htmlStream.append("</TR></TABLE>\n");
	} // protected void makeButton() throws JspException

	protected StringBuffer getParametersList(Vector parameters, SourceBean row) throws JspException {
		StringBuffer parametersList = new StringBuffer();
		for (int i = 0; i < parameters.size(); i++) {
			String name = (String) ((SourceBean) parameters.elementAt(i)).getAttribute("NAME");
			String type = (String) ((SourceBean) parameters.elementAt(i)).getAttribute("TYPE");
			String value = (String) ((SourceBean) parameters.elementAt(i)).getAttribute("VALUE");
			String scope = (String) ((SourceBean) parameters.elementAt(i)).getAttribute("SCOPE");
			if (name != null) {
				parametersList.append(JavaScript.escape(name.toUpperCase()) + "=");
				if ((type != null) && type.equalsIgnoreCase("RELATIVE")) {
					if ((scope != null) && scope.equalsIgnoreCase("LOCAL")) {
						if (row == null) {
							_logger.fatal(
									"DefaultListTag::getParametersList: Non è possibile associare a questo bottone lo scope LOCAL");

							throw new JspException("Non è possibile associare a questo bottone lo scope LOCAL");
						} // if (row == null)
						Object valueObject = row.getAttribute(value);
						if (valueObject != null)
							value = valueObject.toString();
					} // if ((scope != null) &&
						// scope.equalsIgnoreCase("LOCAL"))
					else
						value = (String) ContextScooping.getScopedParameter(_requestContainer, _responseContainer,
								value, scope);
				} // if ((type != null) && type.equalsIgnoreCase("RELATIVE"))
				if (value == null)
					value = "";
				parametersList.append(JavaScript.escape(value) + "&");
			} // if (name != null)
		} // for (int i = 0; i < parameters.size(); i++)
		return parametersList;
	} // protected StringBuffer getParametersList(Vector parameters,
		// SourceBean row) throws JspException

	protected String getQueryString() {
		StringBuffer queryStringBuffer = new StringBuffer();
		Vector queryParameters = _serviceRequest.getContainedAttributes();
		for (int i = 0; i < queryParameters.size(); i++) {
			SourceBeanAttribute parameter = (SourceBeanAttribute) queryParameters.get(i);
			String parameterKey = parameter.getKey();
			if (parameterKey.equalsIgnoreCase("ACTION_NAME") || parameterKey.equalsIgnoreCase("LIST_NOCACHE")
					|| parameterKey.equalsIgnoreCase("LIST_PAGE") || parameterKey.equalsIgnoreCase("MESSAGE")
					|| parameterKey.equalsIgnoreCase("MODULE") || parameterKey.equalsIgnoreCase("NAVIGATOR_DISABLED")
					|| parameterKey.equalsIgnoreCase("NEW_SESSION") || parameterKey.equalsIgnoreCase("PAGE"))
				continue;
			String parameterValue = parameter.getValue().toString();
			queryStringBuffer.append(JavaScript.escape(parameterKey.toUpperCase()));
			queryStringBuffer.append("=");
			queryStringBuffer.append(JavaScript.escape(parameterValue));
			queryStringBuffer.append("&");
		} // for (int i = 0; i < queryParameters.size(); i++)
		return queryStringBuffer.toString();
	} // protected String getQueryString()

	public int doEndTag() throws JspException {
		_logger.debug("DefaultListTag::doEndTag: invocato");

		_actionName = null;
		_moduleName = null;
		_adapter = null;
		_serviceName = null;
		_content = null;
		_layout = null;
		_providerURL = null;
		_requestContainer = null;
		_serviceRequest = null;
		_responseContainer = null;
		_serviceResponse = null;
		_htmlStream = null;
		_columns = null;
		return super.doEndTag();
	} // public int doEndTag() throws JspException

	public void setActionName(String actionName) {
		_logger.debug("DefaultDetailTag::setActionName: actionName [" + actionName + "]");

		_actionName = actionName;
	} // public void setActionName(String actionName)

	public void setModuleName(String moduleName) {
		_logger.debug("DefaultListTag::setModuleName: moduleName [" + moduleName + "]");

		_moduleName = moduleName;
	} // public void setModuleName(String moduleName)

	public void setAdapterClass(String adapterClass) {
		_logger.debug("DefaultListTag::setAdapterClass: adapterClass [" + adapterClass + "]");

		if (adapterClass != null) {
			try {
				_adapter = (IFaceListItemAdapter) Class.forName(adapterClass).newInstance();
			} // try
			catch (Exception ex) {
				it.eng.sil.util.TraceWrapper.error(_logger, "DefaultListTag::setAdapterClass:", ex);

			} // catch (Exception ex)
		} // if (adapterClass != null)
	} // public void setAdapterClass(String adapterClass)
} // public class DefaultListTag extends TagSupport
