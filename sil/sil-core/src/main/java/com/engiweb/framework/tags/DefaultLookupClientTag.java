package com.engiweb.framework.tags;

import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.configuration.ConfigSingleton;

public class DefaultLookupClientTag extends TagSupport {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(DefaultLookupClientTag.class.getName());
	protected String _idlookup = null;
	protected StringBuffer _htmlStream = null;

	public DefaultLookupClientTag() {
		super();
	}

	public int doStartTag() throws JspException {
		_logger.debug("DefaultLookupClientTag::doStartTag:: invocato");

		JspWriter out = pageContext.getOut();
		_htmlStream = new StringBuffer();
		// costruzione sezione FORM
		makeJavaScript();
		try {
			out.print(_htmlStream);
		} // try
		catch (Exception ex) {
			_logger.fatal("DefaultLookupClientTag::doStartTag:: Impossibile inviare lo stream");

			throw new JspException("Impossibile inviare lo stream");
		} // catch (Exception ex)
		return SKIP_BODY;
	} // public int doStartTag() throws JspException

	protected void makeJavaScript() throws JspException {
		ConfigSingleton configure = ConfigSingleton.getInstance();
		SourceBean lookup = (SourceBean) configure.getFilteredSourceBeanAttribute("LOOKUPS.LOOKUP", "idlookup",
				_idlookup);
		if (lookup == null) {
			_logger.error("DefaultLookupClientTag::makeJavaScript:: La definizione del lookup " + _idlookup
					+ " non è censita nel file xml. ");

			throw new JspException("La definizione del lookup " + _idlookup + " non è censita nel file xml. ");
		} // if (lookupPage == null){
		String formName = (String) lookup.getAttribute("PARAMETERS.formName");
		if (formName == null) {
			_logger.error("DefaultLookupClientTag::makeJavaScript:: Non è stato specificato nessun formName");

			throw new JspException("Non è stato specificato nessun formName");
		} // if (formName == null){
		String pageName = (String) lookup.getAttribute("PARAMETERS.pageName");
		if (pageName == null) {
			_logger.error("DefaultLookupClientTag::makeJavaScript:: Non è stato specificato nessun pageName");

			throw new JspException("Non è stato specificato nessun pageName");
		} // if (pageName == null){
		String moduleName = (String) lookup.getAttribute("PARAMETERS.moduleName");
		if (moduleName == null) {
			_logger.error("DefaultLookupClientTag::makeJavaScript:: Non è stato specificato nessun moduleName");

			throw new JspException("Non è stato specificato nessun moduleName");
		} // if (moduleName == null){
		StringBuffer fieldsBuffer = new StringBuffer("");
		Vector fields = lookup.getAttributeAsVector("PARAMETERS.FIELDS.FIELD");
		for (int i = 0; i < fields.size(); i++) {
			SourceBean field = (SourceBean) fields.elementAt(i);
			String lookupTableField = (String) field.getAttribute("lookupTableField");
			String formField = (String) field.getAttribute("formField");
			String likeField = (String) field.getAttribute("likeField");
			fieldsBuffer.append("    _url = _url + '" + formField + "=' +");
			fieldsBuffer.append("escape(Trim(document." + formName + "." + formField + ".value)");
			if (likeField != null && likeField.equalsIgnoreCase("true")) {
				fieldsBuffer.append(" + '%' ");
			} // if (likeField != null && likeField.equalsIgnoreCase("true") )
				// {
			fieldsBuffer.append(" )+'&';\n");
		} // for (int i=0;i<fields.size();i++){
		StringBuffer strURL = new StringBuffer("");
		strURL.append("PAGE=" + pageName);
		strURL.append("&module=" + moduleName);
		strURL.append("&idlookup=" + _idlookup);
		strURL.append("&list_nocache=TRUE");
		strURL.append("&NAVIGATOR_DISABLED=TRUE&");
		_htmlStream.append("<SCRIPT language=\"Javascript\" type=\"text/javascript\"> \n");
		_htmlStream.append("function lookup_" + _idlookup + "() { \n");
		_htmlStream.append("   var _url = 'AdapterHTTP?'; \n");
		_htmlStream.append("   _url = _url + '" + strURL + "' \n");
		_htmlStream.append(fieldsBuffer);
		_htmlStream.append(
				"  var modalWindow = window.open(_url,'ModalChild','dependent=yes,help=no,status=no,resizable=yes');\n");
		_htmlStream.append("  modalWindow.focus() \n");
		_htmlStream.append("} \n");
		_htmlStream.append("</SCRIPT> \n");
	} // protected void makeJavaScript()

	public void setIdLookup(String idlookup) {
		_logger.debug("DefaultLookupClientTag::setIdLookup:: idLookup [" + idlookup + "]");

		_idlookup = idlookup;
	} // public void setIdLookup(String idlookup) {
} // public class DefaultLookupClientTag extends TagSupport {
