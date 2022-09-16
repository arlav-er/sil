package com.engiweb.framework.tags;

import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.RequestContainerAccess;
import com.engiweb.framework.base.ResponseContainer;
import com.engiweb.framework.base.ResponseContainerAccess;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanAttribute;
import com.engiweb.framework.base.XMLObject;
import com.engiweb.framework.configuration.ConfigSingleton;
import com.engiweb.framework.paginator.smart.IFaceListItemAdapter;
import com.engiweb.framework.util.ContextScooping;
import com.engiweb.framework.util.JavaScript;

public class DefaultLookupTag extends TagSupport {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(DefaultLookupTag.class.getName());
	protected RequestContainer _requestContainer = null;
	protected SourceBean _serviceRequest = null;
	protected ResponseContainer _responseContainer = null;
	protected SourceBean _serviceResponse = null;
	protected SourceBean _moduleResponseBean = null;
	protected String _moduleName = null;
	protected StringBuffer _htmlStream = null;
	protected SourceBean _lista = null;
	protected String _pageName = null;
	protected Vector _columns = null;
	protected String _idlookup = null;
	protected SourceBean _lookup = null;
	protected IFaceListItemAdapter _adapter = null;

	public int doStartTag() throws JspException {
		_logger.debug("DefaultLookupTag::doStartTag: invocato");

		ConfigSingleton configure = ConfigSingleton.getInstance();
		HttpServletRequest httpRequest = null;
		httpRequest = (HttpServletRequest) pageContext.getRequest();
		_requestContainer = RequestContainerAccess.getRequestContainer(httpRequest);
		_serviceRequest = _requestContainer.getServiceRequest();
		_responseContainer = ResponseContainerAccess.getResponseContainer(httpRequest);
		_serviceResponse = _responseContainer.getServiceResponse();
		_idlookup = (String) _serviceRequest.getAttribute("idlookup");
		if (_idlookup == null) {
			_logger.error("DefaultLookupTag::doStartTag: non è presente l'id del lookup");

			throw new JspException("Non è presente l'id del lookup");
		} // if (_idlookup == null){
		_lookup = (SourceBean) configure.getFilteredSourceBeanAttribute("LOOKUPS.LOOKUP", "idlookup", _idlookup);
		if (_lookup == null) {
			_logger.error("DefaultLookupTag::doStartTag: la definizione del lookup " + _idlookup
					+ " non è censita nel file xml");

			throw new JspException("La definizione del lookup " + _idlookup + " non è censita nel file xml");
		} // if (_lookup == null){
		_pageName = (String) _serviceRequest.getAttribute("PAGE");
		if (_pageName == null) {
			_logger.fatal("DefaultLookupTag::doStartTag: pageName nullo");

			throw new JspException("pageName nullo");
		} // if (_pageName == null)
		_moduleName = (String) _lookup.getAttribute("PARAMETERS.moduleName");
		// recupero il modulo
		_moduleResponseBean = (SourceBean) _serviceResponse.getAttribute(_moduleName);
		if (_moduleResponseBean == null) {
			_logger.fatal("DefaultLookupTag::doStartTag: moduleResponseBean nullo");

			throw new JspException("moduleResponseBean nullo");
		} // if (_moduleResponseBean == null)
			// recupero la sezione XML relativa alla descrizione della lista
			// _lista =
			// (SourceBean)configure.getFilteredSourceBeanAttribute("LIST.MODULE",
			// "name", _moduleName);
		SourceBean moduleBean = (SourceBean) configure.getFilteredSourceBeanAttribute("MODULES.MODULE", "NAME",
				_moduleName);
		_lista = (SourceBean) moduleBean.getAttribute("CONFIG");
		if (_lista == null) {
			_logger.fatal("DefaultLookupTag::doStartTag: lista nullo");

			throw new JspException("Non esiste la descrizione della lista relativa al modulo indicato");
		} // if (_lista == null)
		JspWriter out = pageContext.getOut();
		_htmlStream = new StringBuffer();
		// costruzione sezione FORM
		makeForm();
		try {
			out.print(_htmlStream);
		} // try
		catch (Exception ex) {
			_logger.fatal("DefaultLookupTag::doStartTag: impossibile inviare lo stream");

			throw new JspException("Impossibile inviare lo stream");
		} // catch (Exception ex)
		return SKIP_BODY;
	} // public int doStartTag() throws JspException

	protected void makeForm() throws JspException {
		_htmlStream.append("<form name=\"" + _moduleName + "\">\n");
		_htmlStream.append("<H2>" + (String) _lista.getAttribute("TITLE") + "</H2>\n");
		// costruzione sezione bottoni di navigazione
		makeJavaScript();
		makeNavigationButton();
		_htmlStream.append("    <table class=\"lista\">\n");
		// costruzione sezione nomi colonne di lista
		makeColumns();
		makeRows();
		makeButton();
		_htmlStream.append("    </table>\n");
		_htmlStream.append("    </form>\n");
	} // protected void makeForm() throws JspException

	protected void makeNavigationButton() throws JspException {
		String pageNumberString = (String) _moduleResponseBean.getAttribute("PAGED_LIST.PAGE_NUMBER");
		int pageNumber = 1;
		try {
			pageNumber = Integer.parseInt(pageNumberString);
		} // / try
		catch (NumberFormatException ex) {
			it.eng.sil.util.TraceWrapper.fatal(_logger,
					"DefaultLookupTag::makeNavigationButton: Integer.parseInt(pageNumberString)", ex);

		} // catch (NumberFormatException ex)
		String pagesNumberString = (String) _moduleResponseBean.getAttribute("PAGED_LIST.PAGES_NUMBER");
		int pagesNumber = 1;
		try {
			pagesNumber = Integer.parseInt(pagesNumberString);
		} // / try
		catch (NumberFormatException ex) {
			it.eng.sil.util.TraceWrapper.fatal(_logger,
					"DefaultLookupTag::makeNavigationButton:: Integer.parseInt(pageNumberString)", ex);

		} // catch (NumberFormatException ex)
		int prevPage = pageNumber - 1;
		if (prevPage < 1)
			prevPage = 1;
		int nextPage = pageNumber + 1;
		if (nextPage > pagesNumber)
			nextPage = pagesNumber;
		String queryString = getQueryString();
		_htmlStream.append("    <table border=0><tr> \n");
		_htmlStream.append(
				"            <td><input type=\"Button\" class=\"ListButtonChangePage\" value=\"|<<\" onclick=\"goConfirm"
						+ _moduleName + "('" + queryString + "PAGE=" + _pageName + "&MODULE=" + _moduleName
						+ "&MESSAGE=LIST_FIRST','FALSE');return false\"></td>\n");
		_htmlStream.append(
				"            <td><input type=\"Button\" class=\"ListButtonChangePage\" value=\" < \" onclick=\"goConfirm"
						+ _moduleName + "('" + queryString + "PAGE=" + _pageName + "&MODULE=" + _moduleName
						+ "&MESSAGE=LIST_PAGE&LIST_PAGE=" + prevPage + "','FALSE');return false\"></td>\n");
		_htmlStream.append(
				"            <td><input type=\"Button\" class=\"ListButtonChangePage\" value=\" > \" onclick=\"goConfirm"
						+ _moduleName + "('" + queryString + "PAGE=" + _pageName + "&MODULE=" + _moduleName
						+ "&MESSAGE=LIST_PAGE&LIST_PAGE=" + nextPage + "','FALSE');return false\"></td>\n");
		_htmlStream.append(
				"            <td><input type=\"Button\" class=\"ListButtonChangePage\" value=\">>|\" onclick=\"goConfirm"
						+ _moduleName + "('" + queryString + "PAGE=" + _pageName + "&MODULE=" + _moduleName
						+ "&MESSAGE=LIST_LAST','FALSE');return false\"></td>\n");
		_htmlStream.append("            <td><B> &nbsp;Pag. " + pageNumber + "  di  " + pagesNumber + "<B></td> \n");
		_htmlStream.append("    </tr>\n");
		_htmlStream.append("    </table>\n");
	} // protected void makeNavigationButton() throws JspException

	protected void makeColumns() throws JspException {
		_htmlStream.append("<tr>\n");
		_htmlStream.append("   <th class=\"lista\">&nbsp;</th>\n");
		_columns = _lista.getAttributeAsVector("COLUMNS.COLUMN");
		if ((_columns == null) || (_columns.size() == 0)) {
			_logger.fatal("DefaultLookupTag::makeColumns: nomi delle colonne non definiti");

			throw new JspException("Nomi delle colonne non definiti");
		} // if ((_columns == null) || (_columns.size() == 0))
		for (int ind = 0; ind < _columns.size(); ind++) {
			String nomeColonna = (String) ((SourceBean) _columns.elementAt(ind)).getAttribute("label");
			_htmlStream.append("   <th class=\"lista\">&nbsp;" + nomeColonna + "&nbsp;</th>\n");
		} // for (int ind = 0; ind < _columns.size(); ind++)
		_htmlStream.append("</tr>\n");
	} // protected void makeColumns() throws JspException

	protected void makeRows() throws JspException {
		ConfigSingleton configure = ConfigSingleton.getInstance();
		SourceBean selectCaption = (SourceBean) _lista.getAttribute("CAPTIONS.SELECT_CAPTION");
		Vector genericCaption = _lista.getAttributeAsVector("CAPTIONS.CAPTION");
		SourceBean rowsBean = (SourceBean) _moduleResponseBean.getAttribute("PAGED_LIST.ROWS");
		Vector rows = rowsBean.getContainedSourceBeanAttributes();
		for (int ind = 0; ind < rows.size(); ind++) {
			Object rowObject = ((SourceBeanAttribute) rows.elementAt(ind)).getValue();
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
					it.eng.sil.util.TraceWrapper.fatal(_logger, "DefaultLookupTag::makeRows: oggetto riga non valido",
							ex);

					throw new JspException("Oggetto riga non valido");
				} // catch (Exception ex)
			} // if (rowObject instanceof XMLObject)
			else {
				_logger.fatal("DefaultLookupTag::makeRows: oggetto riga non valido !");

				throw new JspException("Oggetto riga non valido");
			} // if (rowObject instanceof XMLObject) else
			_htmlStream.append("<tr class=\"lista\">\n");
			_htmlStream.append("   <td class=\"lista\">\n");
			_htmlStream.append("     <table border=0 cellpadding=0 cellspacing=0><tr>\n");
			if (selectCaption != null) {
				String labelSelect = (String) selectCaption.getAttribute("label");
				if ((labelSelect == null) || (labelSelect.length() == 0))
					labelSelect = "Imposta il valore";
				String imageSelect = (String) selectCaption.getAttribute("image");
				if ((imageSelect == null) || (imageSelect.length() == 0))
					imageSelect = "../img/af/detail.gif";
				String confirmSelect = (String) selectCaption.getAttribute("confirm");
				if ((confirmSelect == null) || (confirmSelect.length() == 0))
					confirmSelect = "FALSE";
				Vector parameters = selectCaption.getAttributeAsVector("PARAMETER");
				StringBuffer parametersStr = getParametersList(parameters, row);
				_htmlStream.append("<td><A href=\"javascript://\" onclick=\"goBack" + _moduleName + "(" + parametersStr
						+ " );return false;\"><IMG name=\"image\"" + " src=\"" + imageSelect + "\" alt=\"" + labelSelect
						+ "\"/></A></td> ");
			} // if (selectCaption != null)
			_htmlStream.append("     </tr></table>\n");
			_htmlStream.append("   </td>\n");
			for (int in = 0; in < _columns.size(); in++) {
				String nomeColonna = (String) ((SourceBean) _columns.elementAt(in)).getAttribute("name");
				Object fieldObject = row.getAttribute(nomeColonna);
				String field = null;
				if (fieldObject != null)
					field = fieldObject.toString();
				else
					field = "&nbsp;";
				_htmlStream.append("   <td class=\"lista\"> " + field + "</td>");
			} // for (int in = 0; in < _columns.size(); in++)
			_htmlStream.append("</tr>\n");
		} // for (int ind = 0; ind < rows.size(); ind++)
		_htmlStream.append("</table>\n");
	} // protected void makeRows() throws JspException

	protected void makeButton() throws JspException {
		ConfigSingleton configure = ConfigSingleton.getInstance();
		Vector genericButton = _lista.getAttributeAsVector("BUTTONS.BUTTON");
		_htmlStream.append("<tr class=\"lista\">\n");
		_htmlStream.append("   <td class=\"lista\">\n");
		_htmlStream.append("     <table border=0 cellpadding=0 cellspacing=0><tr>\n");
		_htmlStream.append("<TR><TD><BR></TD><TD><BR></TD></TR>\n");
		for (int i = 0; i < genericButton.size(); i++) {
			SourceBean button = (SourceBean) genericButton.elementAt(i);
			String labelButton = (String) button.getAttribute("label");
			if ((labelButton == null) || (labelButton.length() == 0))
				labelButton = "BOTTONE";
			String imageButton = (String) button.getAttribute("image");
			String confirmButton = (String) button.getAttribute("confirm");
			if ((confirmButton == null) || (confirmButton.length() == 0))
				confirmButton = "FALSE";
			Vector parameters = button.getAttributeAsVector("PARAMETER");
			StringBuffer parametersStr = getParametersList(parameters, null);
			if ((imageButton != null) && (confirmButton.length() > 0))
				_htmlStream.append(
						"<td><A href=\"javascript://\" onclick=\"goConfirm" + _moduleName + "(" + "'" + parametersStr
								+ "'" + ", " + "'" + confirmButton + "'" + " );return false;\"><IMG name=\"image\""
								+ " src=\"" + imageButton + "\" alt=\"" + labelButton + "\"/></A></td> ");
			else
				_htmlStream.append("<td><input type=\"Button\" class=\"ButtonChangePage\" value=\"" + labelButton
						+ "\" onclick=\"goConfirm" + _moduleName + "(" + "'" + parametersStr + "'" + ", " + "'"
						+ confirmButton + "'" + ");return false\"></td>\n");
		} // for (int i = 0; i < genericButton.size(); i++)
		_htmlStream.append("     </tr></table>\n");
		_htmlStream.append("   </td>\n");
		_htmlStream.append("</tr>\n");
	} // protected void makeButton() throws JspException

	protected StringBuffer getParametersList(Vector parameters, SourceBean row) throws JspException {
		StringBuffer parametersList = new StringBuffer();
		for (int ind = 0; ind < parameters.size(); ind++) {
			String name = (String) ((SourceBean) parameters.elementAt(ind)).getAttribute("name");
			String type = (String) ((SourceBean) parameters.elementAt(ind)).getAttribute("type");
			String value = (String) ((SourceBean) parameters.elementAt(ind)).getAttribute("value");
			String scope = (String) ((SourceBean) parameters.elementAt(ind)).getAttribute("scope");
			if (name != null) {
				if ((type != null) && type.equalsIgnoreCase("RELATIVE")) {
					if ((scope != null) && scope.equalsIgnoreCase("LOCAL")) {
						if (row == null) {
							_logger.fatal(
									"DefaultLookupTag::getParametersList: non è possibile associare a questo bottone lo scope LOCAL");

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
				if (ind != 0)
					parametersList.append(",");
				parametersList.append("'" + value + "'");
			} // if (name != null)
		} // for (int ind = 0; ind < parameters.size(); ind++)
		return parametersList;
	} // protected StringBuffer getParametersList(Vector parameters,
		// SourceBean row) throws JspException

	protected void makeJavaScript() throws JspException {
		_htmlStream.append("<SCRIPT language=\"Javascript\" type=\"text/javascript\"> \n");
		_htmlStream.append("function goConfirm" + _moduleName + "(url,alertFlag ) { \n");
		_htmlStream.append("   var _url = \"AdapterHTTP?\"; \n");
		_htmlStream.append("    _url = _url + url; \n");
		_htmlStream.append("    if (alertFlag == 'TRUE' ){ \n");
		_htmlStream.append("        if (confirm('Confermi operazione')) { \n");
		_htmlStream.append("             window.location = _url; \n");
		_htmlStream.append("        } \n");
		_htmlStream.append("    }else { \n");
		_htmlStream.append("         window.location = _url; \n");
		_htmlStream.append("    } \n");
		_htmlStream.append("} \n");
		_htmlStream.append("</SCRIPT> \n");
		ConfigSingleton configure = ConfigSingleton.getInstance();
		StringBuffer parametersBuffer = new StringBuffer("function goBack" + _moduleName + "( ");
		StringBuffer fieldsBuffer = new StringBuffer("");
		StringBuffer parameterBuffer = new StringBuffer("");
		_htmlStream.append("<SCRIPT language=\"Javascript\" type=\"text/javascript\"> \n");
		String formName = (String) _lookup.getAttribute("PARAMETERS.formName");
		if (formName == null) {
			_logger.fatal("DefaultLookupTag::makeJavaScript: non è stato specificato nessun formName");

			throw new JspException("Non è stato specificato nessun formName");
		} // if (formName == null){
		Vector fields = _lookup.getAttributeAsVector("PARAMETERS.FIELDS.FIELD");
		for (int i = 0; i < fields.size(); i++) {
			SourceBean field = (SourceBean) fields.elementAt(i);
			String lookupTableField = (String) field.getAttribute("lookupTableField");
			String formField = (String) field.getAttribute("formField");
			fieldsBuffer.append("window.opener.document." + formName + "." + formField + ".value = "
					+ lookupTableField.toUpperCase() + ";\n");
		} // for (int i = 0; i < fields.size(); i++)
		SourceBean selectCaption = (SourceBean) _lista.getAttribute("CAPTIONS.SELECT_CAPTION");
		Vector parameters = selectCaption.getAttributeAsVector("PARAMETER");
		for (int i = 0; i < parameters.size(); i++) {
			SourceBean parameter = (SourceBean) parameters.elementAt(i);
			String name = (String) parameter.getAttribute("name");
			Vector field = _lookup.getFilteredSourceBeanAttributeAsVector("PARAMETERS.FIELDS.FIELD", "lookupTableField",
					name);
			if ((field != null) || (field.size() > 0)) {
				if (i != 0)
					parameterBuffer.append(',');
				parameterBuffer.append(name.toUpperCase());
			} // if ((field != null) || (field.size() > 0))
			else {
				_logger.error("DefaultLookupTag::makeJavaScript: non esiste il valore del campo " + name
						+ " del file xml delle tabelle di lookup");

				throw new JspException(
						"Non esiste il valore del campo " + name + " del file xml delle tabelle di lookup");
			} // if ((field != null) || (field.size() > 0)) else
		} // for (int i = 0; i < parameters.size(); i++)
		parametersBuffer.append(parameterBuffer + ") {\n");
		_htmlStream.append(parametersBuffer + "\n");
		_htmlStream.append(fieldsBuffer);
		_htmlStream.append("window.close(); \n");
		_htmlStream.append("}\n");
		_htmlStream.append("</SCRIPT>\n");
	} // protected void makeJavaScript() throws JspException

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
		_logger.debug("DefaultLookupTag::doEndTag: invocato");

		_requestContainer = null;
		_serviceRequest = null;
		_responseContainer = null;
		_serviceResponse = null;
		_moduleResponseBean = null;
		_moduleName = null;
		_htmlStream = null;
		_lista = null;
		_pageName = null;
		_columns = null;
		_idlookup = null;
		_lookup = null;
		_adapter = null;
		return super.doEndTag();
	} // public int doEndTag() throws JspException

	public void setAdapterClass(String adapterClass) {
		_logger.debug("DefaultLookupTag::setAdapterClass: adapterClass [" + adapterClass + "]");

		if (adapterClass != null) {
			try {
				_adapter = (IFaceListItemAdapter) Class.forName(adapterClass).newInstance();
			} // try
			catch (Exception ex) {
				it.eng.sil.util.TraceWrapper.error(_logger, "DefaultLookupTag::setAdapterClass:", ex);

			} // catch (Exception ex)
		} // if (adapterClass != null)
	} // public void setAdapterClass(String adapterClass)
} // public class DefaultLookupTag extends TagSupport
