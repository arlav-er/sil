package it.eng.afExt.tags;

import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.RequestContainerAccess;
import com.engiweb.framework.base.ResponseContainer;
import com.engiweb.framework.base.ResponseContainerAccess;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanAttribute;
import com.engiweb.framework.configuration.ConfigSingleton;
import com.engiweb.framework.util.ContextScooping;
import com.engiweb.framework.util.JavaScript;

/*
 * @Scopiazzato da Paolo Roccetti in data 27/04/2004 da DefaultListTag
 * 
 */

import it.eng.afExt.utils.StringUtils;

/**
 * Crea una lista in cui è possibile associare una funzione JS anche ai pulsanti diversi dalla SELECT_CAPTION e dalla
 * DELETE_CAPTION. Basta indicare i nomi delle funzioni JS da chiamare, separati da ";", nell'attributo jsCaptions. Tali
 * funzioni verranno associate ai pulsanti CAPTION nell'ordine in cui sono definiti. Per saltare alcuni pulsanti è
 * sufficiente indicare 'null' nell'attributo jsCaptions corrispondente. Ad esempio con
 * jsCaptions="aggiungi;scollega;null;visualizza" alla prima caption verrà chiamata la funzione aggiungi() con i
 * parametri indicati nella definizione del modulo, alla seconda caption verrà chiamata la scollega() con argomenti
 * definiti nel modulo, alla terza caption non verrà chiamata alcuna funzione e alla quarta la funzione visualizza()
 * sempre con parametri definiti nel modulo... Chiaro no?
 * <p>
 * 
 * @author Paolo Roccetti
 */
public class JSButtonListTag extends DefaultListTag {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(JSButtonListTag.class.getName());
	private String _moduleName = null;
	private String _serviceName = null;
	private SourceBean _content = null;
	private SourceBean _layout = null;
	private String _providerURL = null;
	private RequestContainer _requestContainer = null;
	private SourceBean _serviceRequest = null;
	private ResponseContainer _responseContainer = null;
	private SessionContainer _sessionContainer = null;

	private SourceBean _serviceResponse = null;
	private StringBuffer _htmlStream = null;
	private Vector _columns = null;
	private String _canDelete = "1";
	private String _canInsert = "1";
	private String configProviderClass = "";
	private String _jsSelect = "";
	private String _jsDelete = "";
	private String _getBack = "";
	private ArrayList _jsCaptions = new ArrayList();
	private String skipNavigationButton = "0";

	ConfigSingleton configure = null;
	SourceBean selectCaption = null;
	SourceBean deleteCaption = null;
	Vector genericCaption = null;

	String pageName = null;

	public int doStartTag() throws JspException {
		_logger.debug("DefaultListTag::doStartTag:: invocato");

		HttpServletRequest httpRequest = (HttpServletRequest) pageContext.getRequest();
		_requestContainer = RequestContainerAccess.getRequestContainer(httpRequest);
		_sessionContainer = _requestContainer.getSessionContainer();
		_serviceRequest = _requestContainer.getServiceRequest();
		_responseContainer = ResponseContainerAccess.getResponseContainer(httpRequest);
		_serviceResponse = _responseContainer.getServiceResponse();
		ConfigSingleton configure = ConfigSingleton.getInstance();

		if (!configProviderClass.equals("")) {
			IDynamicConfigProvider configProvider = null;
			try {
				configProvider = (IDynamicConfigProvider) Class.forName(configProviderClass).newInstance();
				if (configProvider instanceof AbstractConfigProvider) {
					((AbstractConfigProvider) configProvider).setSessionContainer(_sessionContainer);
				}

			} catch (Exception ex) {
				String msg = "DefaultDetailTag::doStartTag:: errore nella classe " + configProviderClass
						+ " che implementa IDynamicConfigProvider ";
				_logger.debug(msg);

				throw new JspException(msg);

			}

			_layout = configProvider.getConfigSourceBean(_serviceRequest, _serviceResponse);

		}

		if (_moduleName != null) {

			// GG, faccio "_serviceName = _moduleName" solo se non è stato
			// definito
			// un "serviceName" tramite un eventuale e futuro metodo di "set".
			if ((_serviceName == null) || (_serviceName.length() == 0)) {
				_serviceName = _moduleName.replace('.', '_');
				// GG 15/9/04, inoltre faccio il replace dei punti per evitare
				// di avere problemi in javascript con nomi con punto!
			}

			_content = (SourceBean) _serviceResponse.getAttribute(_moduleName);
			SourceBean moduleBean = (SourceBean) configure.getFilteredSourceBeanAttribute("MODULES.MODULE", "NAME",
					_moduleName);

			if (_layout == null) {
				_layout = (SourceBean) moduleBean.getAttribute("CONFIG");
			}
			pageName = (String) _serviceRequest.getAttribute("PAGE");
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

		configure = ConfigSingleton.getInstance();
		selectCaption = (SourceBean) _layout.getAttribute("CAPTIONS.SELECT_CAPTION");
		deleteCaption = (SourceBean) _layout.getAttribute("CAPTIONS.DELETE_CAPTION");
		genericCaption = _layout.getAttributeAsVector("CAPTIONS.CAPTION");

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
		// form commentata in data 24/02/2004 per compatibilità con i layer
		// non dovrebbe più servire
		// _htmlStream.append("<FORM name=\"" + _serviceName + "\">\n");
		_htmlStream.append("<H2>" + (String) _layout.getAttribute("TITLE") + "</H2>\n");
		makeJavaScript();
		if (!skipNavigationButton.equals("1")) {
			makeNavigationButton();
		}
		/* Creazione dell'effetto di stondatura */
		// Stondature - Stefania Orioli - 29/03/2004 - Elemento TOP
		_htmlStream.append(
				"<br><table maxwidth=\"96%\" width=\"96%\" align=\"center\" margin=\"0\" cellpadding=\"0\" cellspacing=\"0\">");
		_htmlStream.append("<tr><td class=\"sfondo_lista\" valign=\"top\" align=\"left\" width=\"6\" height=\"6px\">");
		_htmlStream.append("<img src=\"../../img/angoli/bia1.gif\" width=\"6\" height=\"6\"></td>");
		_htmlStream.append("<td class=\"sfondo_lista\" height=\"6px\">&nbsp;</td>");
		_htmlStream.append("<td class=\"sfondo_lista\" valign=\"top\" align=\"right\" width=\"6\" height=\"6px\">");
		_htmlStream.append("<img src=\"../../img/angoli/bia2.gif\" width=\"6\" height=\"6\"></td></tr>");
		_htmlStream.append("<tr><td class=\"sfondo_lista\" width=\"6\">&nbsp;</td>");
		_htmlStream.append("<td class=\"sfondo_lista\" align=\"center\">");
		// fine TOP

		_htmlStream.append("<TABLE class=\"lista\" align=\"center\">\n");
		makeColumns();
		makeRows();

		// Stondature - Stefania Orioli - 29/03/2004 - Elemento BOTTOM
		_htmlStream.append("</td><td class=\"sfondo_lista\" width=\"6\">&nbsp;</td></tr>");
		_htmlStream.append("<tr valign=\"bottom\">");
		_htmlStream.append("<td class=\"sfondo_lista\" valign=\"bottom\" align=\"left\" width=\"6\" height=\"6px\">");
		_htmlStream.append("<img src=\"../../img/angoli/bia4.gif\" width=\"6\" height=\"6\"></td>");
		_htmlStream.append("<td class=\"sfondo_lista\" height=\"6px\">&nbsp;</td>");
		_htmlStream.append("<td class=\"sfondo_lista\" valign=\"bottom\" align=\"right\" width=\"6\" height=\"6px\">");
		_htmlStream.append("<img src=\"../../img/angoli/bia3.gif\" width=\"6\" height=\"6\"></td></tr></table>");
		// fine BOTTOM

		makeButton();
		_htmlStream.append("</TABLE>\n");
		// _htmlStream.append("</FORM>\n");
	} // public void makeForm()

	protected void makeJavaScript() {
		_htmlStream.append("<SCRIPT language=\"Javascript\" type=\"text/javascript\">\n");
		_htmlStream.append("<!--\n");
		_htmlStream.append("function goConfirm" + _serviceName + "(url, alertFlag) {\n");

		// GG 1/10/2004 - il corpo della funzione è stato spostato nella
		// funzione "goConfirmGenericCustomTL()" nel file customTL.js
		_htmlStream.append("  goConfirmGenericCustomTL(url, alertFlag);  // in customTL.js\n");

		_htmlStream.append("}\n");
		_htmlStream.append("// -->\n");
		_htmlStream.append("</SCRIPT>\n");
	} // private void makeJavaScript()

	protected void makeNavigationButton() throws JspException {
		int currentPage = 0;
		int numPages = 0;
		int numRecords = 0;
		int rowsXpage = 0;

		int firstShownRec = 0;
		int lastShownRec = 0;

		// Aggiunto da D'Auria Giovanni il 17/05/2005
		String numeroPagine = "";
		String numeroRecords = "";

		SourceBean rowsSB = (SourceBean) _content.getAttribute("ROWS");
		if (rowsSB != null) {
			currentPage = ((Integer) rowsSB.getAttribute("CURRENT_PAGE")).intValue();
			numPages = ((Integer) rowsSB.getAttribute("NUM_PAGES")).intValue();
			numRecords = ((Integer) rowsSB.getAttribute("NUM_RECORDS")).intValue();
			rowsXpage = ((Integer) rowsSB.getAttribute("ROWS_X_PAGE")).intValue();

			// Aggiunto da D'Auria Giovanni il 17/05/2005
			// per compatibilità con le modifiche apportate alla lista;
			// mentre prima già dalla prima pagina si poteva ottenere il numero
			// delle pagine
			// grazie all'attributo NUM_PAGES, ora NUM_PAGES viene valorizzato
			// correttamente solo nell'ultima pagine mentre in tutte quelle
			// precedenti vale -1
			if (numPages != -1) {
				numeroPagine = " di " + numPages;
				numeroRecords = " di " + numRecords;
			} else {
				numeroPagine = " ";
				numeroRecords = " ";
			}

			firstShownRec = (currentPage - 1) * rowsXpage + 1;
			lastShownRec = currentPage * rowsXpage;
			if (lastShownRec > numRecords) {
				lastShownRec = numRecords;
			}
		}

		int prevPage = currentPage - 1;
		if (prevPage < 1)
			prevPage = 1;
		int nextPage = currentPage;
		if (numPages == -1) {
			nextPage = currentPage + 1;
		}

		String queryString = getQueryString();

		_htmlStream.append("<TABLE border=\"0\" cellspacing=\"0\" cellpadding=\"1px\" align=\"center\"><TR>\n");
		if (numPages == 0 || numPages == 1) {
			_htmlStream.append(
					"<td width=\"18px\"><img src=\"../../img/list_first_gri.gif\" alt=\" |<< \" border=\"0\" /></td>");
			_htmlStream.append(
					"<td width=\"18px\"><img src=\"../../img/list_prev_gri.gif\" alt=\" < \" border=\"0\" /></td>");
			_htmlStream.append(
					"<td width=\"18px\"><img src=\"../../img/list_next_gri.gif\" alt=\" > \" border=\"0\" /></td>");
			_htmlStream.append(
					"<td width=\"18px\"><img src=\"../../img/list_last_gri.gif\" alt=\" >>| \" border=\"0\" /></td>");
			_htmlStream.append("<TR><TD COLSPAN=\"6\" align=\"center\"><B>Pag. " + currentPage + numeroPagine + "\n");
			_htmlStream.append(" (da " + firstShownRec + " a " + lastShownRec + numeroRecords + ")<B></TD>\n");

		} else {
			_htmlStream.append("<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>");
			_htmlStream.append(
					"<TD width=\"18px\"><BUTTON width=\"16px\" type=\"button\" class=\"ListButtonChangePage\" onclick=\"goConfirm"
							+ _serviceName + "('" + queryString + _providerURL
							+ "MESSAGE=LIST_FIRST','FALSE'); return false;\">"
							+ "<img src=\"../../img/list_first.gif\" alt=\" |<< \" border=\"0\" /></BUTTON>"
							+ "</TD>\n");
			_htmlStream.append(
					"<TD width=\"18px\"><BUTTON width=\"16px\" type=\"button\" class=\"ListButtonChangePage\" onclick=\"goConfirm"
							+ _serviceName + "('" + queryString + _providerURL + "MESSAGE=LIST_PAGE&LIST_PAGE="
							+ prevPage + "','FALSE'); return false;\">"
							+ "<img src=\"../../img/list_prev.gif\" alt=\" < \" border=\"0\" /></BUTTON>" + "</TD>\n");
			_htmlStream.append(
					"<TD width=\"18px\"><BUTTON width=\"16px\" type=\"button\" class=\"ListButtonChangePage\" onclick=\"goConfirm"
							+ _serviceName + "('" + queryString + _providerURL + "MESSAGE=LIST_PAGE&LIST_PAGE="
							+ nextPage + "','FALSE'); return false;\">"
							+ "<img src=\"../../img/list_next.gif\" alt=\" > \" border=\"0\" /></BUTTON>" + "</TD>\n");
			_htmlStream.append(
					"<TD width=\"18px\"><BUTTON width=\"16px\" type=\"button\" class=\"ListButtonChangePage\" onclick=\"goConfirm"
							+ _serviceName + "('" + queryString + _providerURL
							+ "MESSAGE=LIST_LAST','FALSE'); return false;\">"
							+ "<img src=\"../../img/list_last.gif\" alt=\" >>| \" border=\"0\" /></BUTTON></TD>"
							+ "<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>" + "\n</TR>\n");
			_htmlStream.append("<TR><TD COLSPAN=\"6\" align=\"center\"><B>Pag. " + currentPage + numeroPagine + "\n");
			_htmlStream.append(" (da " + firstShownRec + " a " + lastShownRec + numeroRecords + ")<B></TD>\n");
		}

		_sessionContainer.setAttribute("_TOKEN_" + pageName.toUpperCase(),
				_providerURL + queryString + "MESSAGE=LIST_PAGE&LIST_PAGE=" + currentPage);

		if ("true".equalsIgnoreCase(_getBack)) {
			_sessionContainer.delAttribute("_BACKURL_");
			_sessionContainer.setAttribute("_BACKURL_",
					_providerURL + queryString + "MESSAGE=LIST_PAGE&LIST_PAGE=" + currentPage);

			_sessionContainer.delAttribute("_BACKPAGE_");
			_sessionContainer.setAttribute("_BACKPAGE_", pageName.toUpperCase());
		}

		_htmlStream.append("</TR></TABLE>\n");

	} // public void makeNavigationButton()

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
			if (!((SourceBean) _columns.elementAt(i)).containsAttribute("notVisible")) {
				_htmlStream.append("<TH class=\"lista\">&nbsp;" + nomeColonna + "&nbsp;</TH>\n");
			}
		} // for (int i = 0; i < _columns.size(); i++)

		if ((deleteCaption != null) && (!_canDelete.equals("0"))) {
			_htmlStream.append("<TH class=\"lista\">&nbsp;</TH>\n");
		}

		_htmlStream.append("</TR>\n");
	} // private void makeColumns() throws JspException

	protected void makeRows() throws JspException {
		Vector rows = null;
		rows = _content.getAttributeAsVector("ROWS.ROW");

		String classRow = "";
		boolean pd = true;

		if (rows.size() == 0) {
			_htmlStream.append("<tr><td colspan=\"" + (_columns.size() + 1)
					+ "\"><b>Non &egrave; stato trovato alcun risultato.</b></td></tr>");
		}

		for (int i = 0; i < rows.size(); i++) {
			SourceBean row = (SourceBean) rows.elementAt(i);

			// il conteggio inizia da zero!
			if (pd == true) {
				pd = false;
				classRow = "lista_dispari";
			} else {
				pd = true;
				classRow = "lista_pari";
			}

			_htmlStream.append("<TR class=\"lista\">\n");
			_htmlStream.append("<TD class=\"" + classRow + "\">\n");
			_htmlStream.append("<TABLE border=\"0\" cellpadding=\"0\" cellspacing=\"0\" margin=\"0\"><TR>\n");
			if (selectCaption != null) { // Caption di selezione
				String labelSelect = (String) selectCaption.getAttribute("LABEL");
				if ((labelSelect == null) || (labelSelect.length() == 0))
					labelSelect = "Selezionare un dettaglio";
				String imageSelect = (String) selectCaption.getAttribute("IMAGE");
				if ((imageSelect == null) || (imageSelect.length() == 0))
					imageSelect = "../../img/detail.gif";
				String confirmSelect = (String) selectCaption.getAttribute("CONFIRM");
				if ((confirmSelect == null) || (confirmSelect.length() == 0))
					confirmSelect = "FALSE";
				Vector parameters = selectCaption.getAttributeAsVector("PARAMETER");

				if (_jsSelect == "") {
					// chiamo la go_confirm standard
					StringBuffer parametersStr = getParametersList(parameters, row);
					_htmlStream.append("<TD class=\"caption\"><A href=\"javascript://\" onclick=\"goConfirm"
							+ _serviceName + "(" + "'" + parametersStr + "'" + ", " + "'" + confirmSelect + "'"
							+ "); return false;\"><IMG name=\"image\" border=\"0\" " + " src=\"" + imageSelect
							+ "\" alt=\"" + labelSelect + "\"/></A></TD>\n");
				} else // altrimenti, invece della go-confirm standard chiamo
						// la mia funzione javascript
				{
					// Reperisco i parametri da passare come argomento alla
					// funzione javascript
					// I parametri sono quelli definiti nella caption
					String parametriJs = getJSParameters(parameters, row);

					_htmlStream.append("<TD class=\"caption\"><A href=\"javascript://\" onclick=\"" + _jsSelect + "("
							+ parametriJs + ");" + " return false;\"><IMG name=\"image\" border=\"0\" " + " src=\""
							+ imageSelect + "\" alt=\"" + labelSelect + "\"/></A></TD>\n");
				}
			}

			for (int j = 0; j < genericCaption.size(); j++) {
				SourceBean caption = (SourceBean) genericCaption.elementAt(j);
				boolean viewCapt = viewCaptionButton(caption, row);
				if (viewCapt) {
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

					// Estraggo l'eventuale funzione Js associata alla caption
					String jsCaption = "null";
					if (_jsCaptions.size() > j) {
						jsCaption = (String) _jsCaptions.get(j);
					}

					// Se ho una funzione Js associata alla Caption la inserisco,
					// altrimenti metto
					// il comportamento di default.
					if (!jsCaption.equals("null")) {
						// Reperisco i parametri da passare come argomento alla
						// funzione javascript
						// I parametri sono quelli definiti nella caption
						String parametriJs = getJSParameters(parameters, row);

						_htmlStream.append("<TD class=\"caption\"><A href=\"javascript://\" onclick=\"" + jsCaption
								+ "(" + parametriJs + ");" + " return false;\"><IMG name=\"image\" border=\"0\" "
								+ " src=\"" + imageCaption + "\" alt=\"" + labelCaption + "\"/></A></TD>\n");
					} else {
						StringBuffer parametersStr = getParametersList(parameters, row);
						_htmlStream.append("<TD class=\"caption\"><A href=\"javascript://\" onclick=\"goConfirm"
								+ _serviceName + "(" + "'" + parametersStr + "'" + ", " + "'" + confirmCaption + "'"
								+ "); return false;\"><IMG name=\"image\" border=\"0\" " + "src=\"" + imageCaption
								+ "\" alt=\"" + labelCaption + "\"/></A></TD>\n");
					}
				} else {
					_htmlStream.append("<TD class=\"caption\">&nbsp;</TD>");
				} // if(viewCapt)
			}

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
				if (!((SourceBean) _columns.elementAt(j)).containsAttribute("notVisible")) {
					_htmlStream.append("<TD class=\"" + classRow + "\"> " + field + "</TD>\n");
				}
			}

			if ((deleteCaption != null) && (!_canDelete.equals("0"))) {
				String labelDelete = (String) deleteCaption.getAttribute("LABEL");
				if ((labelDelete == null) || (labelDelete.length() == 0))
					labelDelete = "Cancellare una riga";
				String imageDelete = (String) deleteCaption.getAttribute("IMAGE");
				if ((imageDelete == null) || (imageDelete.length() == 0))
					imageDelete = "../../img/del.gif";
				String confirmDelete = (String) deleteCaption.getAttribute("CONFIRM");
				if ((confirmDelete == null) || (confirmDelete.length() == 0))
					confirmDelete = "FALSE";
				Vector parameters = deleteCaption.getAttributeAsVector("PARAMETER");
				if (_jsDelete == "") { // se non è presente alcun script
										// javascript per il caption di delete
					// chiamo la go_confirm standard
					StringBuffer parametersStr = getParametersList(parameters, row);
					// parametersStr.append("NAVIGATOR_DISABLED=TRUE&");
					// parametersStr.append(_providerURL);
					parametersStr.append("&LIST_PAGE=" + _content.getAttribute("ROWS.CURRENT_PAGE"));

					_htmlStream.append("<TD class=\"" + classRow + "\"><A href=\"javascript://\" onclick=\"goConfirm"
							+ _serviceName + "(" + "'" + parametersStr + "'" + ", " + "'" + confirmDelete + "'"
							+ "); return false;\"><IMG name=\"image\" border=\"0\" " + "src=\"" + imageDelete
							+ "\" alt=\" " + labelDelete + "\"/ ></A></TD>\n");
				} else {
					// altrimenti, invece della go-confirm standard chiamo la
					// mia funzione javascript
					// Reperisco i parametri da passarecome argomento alla
					// funzione javascript
					// I parametri sono quelli definiti nella caption
					String parametriJs = getJSParameters(parameters, row);
					_htmlStream.append("<TD class=\"" + classRow + "\"><A href=\"javascript://\" onclick=\"" + _jsDelete
							+ "(" + parametriJs + ");" + " return false;\"><IMG name=\"image\" border=\"0\" "
							+ " src=\"" + imageDelete + "\" alt=\"" + labelDelete + "\"/></A></TD>\n");
				}
			} // if (deleteCaption != null)

			_htmlStream.append("</TR>\n");
		} // for (int j = 0; j < rows.size(); j++)
		_htmlStream.append("</TABLE>\n");
	} // private void makeRows()

	protected void makeButton() throws JspException {
		// ConfigSingleton configure = ConfigSingleton.getInstance();
		SourceBean insertButton = (SourceBean) _layout.getAttribute("BUTTONS.INSERT_BUTTON");
		Vector genericButton = _layout.getAttributeAsVector("BUTTONS.BUTTON");
		_htmlStream.append("<TR class=\"lista\">\n");
		_htmlStream.append("<TD class=\"lista\">\n");
		_htmlStream.append("<TABLE border=0 cellpadding=0 cellspacing=0><TR>\n");
		_htmlStream.append("<TR><TD><BR></TD><TD><BR></TD></TR>\n");
		_htmlStream.append("<TABLE width=\"90%\" align=\"center\"><TR>\n");
		if ((insertButton != null) && (!_canInsert.equals("0"))) {
			String labelInsert = (String) insertButton.getAttribute("LABEL");
			if ((labelInsert == null) || (labelInsert.length() == 0))
				labelInsert = "NUOVO";
			String imageInsert = (String) insertButton.getAttribute("IMAGE");
			String confirmInsert = (String) insertButton.getAttribute("CONFIRM");
			if ((confirmInsert == null) || (confirmInsert.length() == 0))
				confirmInsert = "FALSE";
			Vector parameters = insertButton.getAttributeAsVector("PARAMETER");
			StringBuffer parametersStr = getParametersList(parameters, null);
			// parametersStr.append("MESSAGE=" +
			// DelegatedDetailService.DETAIL_NEW + "&");
			if ((imageInsert != null) && (imageInsert.length() > 0))
				_htmlStream.append("<TD align=\"center\"><A href=\"javascript://\" onclick=\"goConfirm" + _serviceName
						+ "(" + "'" + parametersStr + "'" + ", " + "'" + confirmInsert + "'"
						+ "); return false;\"><IMG name=\"image\" border=\"0\" " + " src=\"" + imageInsert + "\" alt=\""
						+ labelInsert + "\"/></A></TD>\n");
			else
				_htmlStream.append("<TD align=\"center\"><INPUT type=\"button\" class=\"ButtonChangePage\" value=\""
						+ labelInsert + "\" onclick=\"goConfirm" + _serviceName + "(" + "'" + parametersStr + "'" + ", "
						+ "'" + confirmInsert + "'" + "); return false\"></TD>\n");
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
				_htmlStream.append("<TD><A href=\"javascript://\" onclick=\"goConfirm" + _serviceName + "(" + "'"
						+ parametersStr + "'" + ", " + "'" + confirmButton + "'"
						+ "); return false;\"><IMG name=\"image\" border=\"0\" " + " src=\"" + imageButton + "\" alt=\""
						+ labelButton + "\"/></A></TD>\n");
			else
				_htmlStream.append("<TD><INPUT type=\"button\" class=\"ButtonChangePage\" value=\"" + labelButton
						+ "\" onclick=\"goConfirm" + _serviceName + "(" + "'" + parametersStr + "'" + ", " + "'"
						+ confirmButton + "'" + "); return false\"></TD>\n");
		} // for (int i = 0; i < genericButton.size(); i++)
		_htmlStream.append("</TR></TABLE>\n");
		_htmlStream.append("</TD>\n");
		_htmlStream.append("</TR>\n");
	} // private void makeButton() throws JspException

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
						else
							value = "";
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
	} // private StringBuffer getParametersList(Vector parameters, SourceBean
		// row) throws JspException

	protected String getJSParameters(Vector parameters, SourceBean row) throws JspException {
		StringBuffer parametersList = new StringBuffer();
		for (int i = 0; i < parameters.size(); i++) {
			String name = (String) ((SourceBean) parameters.elementAt(i)).getAttribute("NAME");
			String type = (String) ((SourceBean) parameters.elementAt(i)).getAttribute("TYPE");
			String value = (String) ((SourceBean) parameters.elementAt(i)).getAttribute("VALUE");
			String scope = (String) ((SourceBean) parameters.elementAt(i)).getAttribute("SCOPE");
			if (name != null) {
				// parametersList.append(JavaScript.escape(name.toUpperCase()) +
				// "=");
				if ((type != null) && type.equalsIgnoreCase("RELATIVE")) {
					if ((scope != null) && scope.equalsIgnoreCase("LOCAL")) {
						if (row == null) {
							_logger.fatal(
									"DefaultListTag::getParametersList: Non è possibile associare a questo bottone lo scope LOCAL");

							throw new JspException("Non è possibile associare a questo bottone lo scope LOCAL");
						} // if (row == null)
						Object valueObject = row.getAttribute(value);
						if (valueObject != null) {
							value = valueObject.toString();
						} else {
							value = "";
						}
					} else
						value = (String) ContextScooping.getScopedParameter(_requestContainer, _responseContainer,
								value, scope);
				}
				if (value == null)
					value = "";
				if (i > 0) {
					parametersList.append(",");
				}
				// parametersList.append("'" + JavaScript.escape(value) + "'");
				// parametersList.append("'" + value.replace('\'', '^') + "'");
				// faccio escaping degli apici
				value = StringUtils.replace(value, "\'", "\\\'");
				// faccio escaping delle virgolette trasformandole in due apici
				value = StringUtils.replace(value, "\"", "\\\'\\\'");
				parametersList.append("'" + value + "'");
			} // if (name != null)
		} // for (int i = 0; i < parameters.size(); i++)
		return (parametersList.toString());
	} // private StringBuffer getJSParameters(Vector parameters, SourceBean
		// row) throws JspException

	protected String getQueryString() {
		StringBuffer queryStringBuffer = new StringBuffer();
		Vector queryParameters = _serviceRequest.getContainedAttributes();
		for (int i = 0; i < queryParameters.size(); i++) {
			SourceBeanAttribute parameter = (SourceBeanAttribute) queryParameters.get(i);
			String parameterKey = parameter.getKey();
			if (parameterKey.equalsIgnoreCase("PAGE") || parameterKey.equalsIgnoreCase("MODULE")
					|| parameterKey.equalsIgnoreCase("MESSAGE") || parameterKey.equalsIgnoreCase("LIST_PAGE")
					|| parameterKey.equalsIgnoreCase("LIST_NOCACHE"))
				continue;
			String parameterValue = parameter.getValue().toString();
			queryStringBuffer.append(JavaScript.escape(parameterKey.toUpperCase()));
			queryStringBuffer.append("=");
			queryStringBuffer.append(JavaScript.escape(parameterValue));
			queryStringBuffer.append("&");
		} // for (int i = 0; i < queryParameters.size(); i++)
		return queryStringBuffer.toString();
	} // private String getQueryString()

	public void setModuleName(String moduleName) {
		_logger.debug("DefaultListTag::setModuleName:: moduleName [" + moduleName + "]");

		_moduleName = moduleName;
	} // public void setModuleName(String moduleName)

	public void setSkipNavigationButton(String skipNB) {
		_logger.debug("DefaultListTag::setModuleName:: moduleName [" + skipNB + "]");

		skipNavigationButton = skipNB;
	} // setSkipNavigationButton(String skipNB)

	public void setCanDelete(String canDelete) {
		_logger.debug("DefaultListTag::setModuleName:: moduleName [" + canDelete + "]");

		_canDelete = canDelete;
	} // public void setCanDelete(String canDelete)

	public void setCanInsert(String canInsert) {
		_logger.debug("DefaultListTag::setModuleName:: moduleName [" + canInsert + "]");

		_canInsert = canInsert;
	} // public void setCanInsert(String canInsert)

	public void setJsSelect(String jsSelect) {
		_logger.debug("DefaultListTag::setModuleName:: moduleName [" + jsSelect + "]");

		_jsSelect = jsSelect;
	}

	public void setJsDelete(String jsDelete) {
		_logger.debug("DefaultListTag::setModuleName:: moduleName [" + jsDelete + "]");

		_jsDelete = jsDelete;
	}

	public void setGetBack(String getBack) {
		_logger.debug("DefaultListTag::setGetBack:: moduleName [" + getBack + "]");

		_getBack = getBack;
	}

	public void setJsCaptions(String jsCaptions) {
		// Eseguo il parsing dell'attributo e metto il tutto nell'array
		// _jsCaptions
		StringTokenizer tokenizer = new StringTokenizer(jsCaptions, ";", false);
		_jsCaptions = new ArrayList();
		while (tokenizer.hasMoreElements()) {
			_jsCaptions.add(tokenizer.nextToken());
		}
		_logger.debug("DefaultListTag::setModuleName:: moduleName [" + jsCaptions + "]");

	}

	/**
	 * @see javax.servlet.jsp.tagext.Tag#doEndTag()
	 */
	public int doEndTag() throws JspException {
		_logger.debug("DefaultListTag::doEndTag:: invocato");

		_moduleName = null;
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

	public String getConfigProviderClass() {
		return configProviderClass;
	}

	public void setConfigProviderClass(String string) {
		configProviderClass = string;
	}

} // public class JSButtonListTag extends DefaultListTag
