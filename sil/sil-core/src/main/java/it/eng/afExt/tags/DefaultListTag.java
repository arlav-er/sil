package it.eng.afExt.tags;

import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

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
 * @Modificato il 19/08/2003 da Stefania Orioli
 * il 28/01/2004 da Franco Vuoto
 * il 30/04/2004 da Stefania Orili
 * il 24/08/2004 da Maurizio Discepolo
 * il  7/09/2004 da Luigi Antenucci (sistemati table con td/tr chiusi male)
 */

/*
 * -------------------------------------------------------------------------------------
 * Questa classe esegue il rendering di una lista generica.
 * Esempio Generico:
 *  <af:list moduleName="M_DynRicercaSediAziende" ........./>
 *  
 *  Gli attributi che possono essere utilizzati sono i seguenti
 *  - moduleName: è l'unico parametro obbligatorio. E' il nome del SorceBean che contiene la lista
 *  - skipNavigationButton: facoltativo; se posto uguale a "1" fa sì che non vengano visualizzati i
 *                          pulsanti di scorrimento fra le pagine della lista (si usa di solito 
 *                          se la lista non è paginata).
 *  - canInsert: facoltativo; se posto a "0" allora il pulsante di inserimento non viene visualizzato.
 *  - canDelete: facoltativo; se posto a "0" allora non vengono visualizzati i pulsanti di cancellazione
 *               degli elementi della lista.
 *  - jsSelect: funzione Javascript da associare alla <SELECT_CAPTION> al posto della goConfirm standard.
 *  - jsDelete: funzione Javascript da associare alla <DELETE_CAPTION> al posto della goConfirm standard.
 *  - configProviderClass: nome del modulo java con il SourceBean di configurazione per la lista.
 *  - classRowCss: facoltativo: nome del class css per gestire la riga: regola deve esistere nei css "classRowCss_dispari" e "classRowCss_pari"

 *  
 *  Le liste possono essere create sia con statement di tipo DINAMICO:
 *    <MODULE name="listaDyn" class="it.eng.afExt.dispatching.module.impl.DynamicStatementListModule>
 *      <CONFIG pool="SIL_DATI" rows="15" title="Lista Dyn">
 *        <QUERIES>
 *          <SELECT_QUERY>
 *            <STATEMENT_PROVIDER CLASS="it.eng.sil.module.ido.DynStatementJava"/>
 *          </SELECT_QUERY>
 *        </QUERIES>
 *      </CONFIG>
 *      ......
 *    </MODULE>
 *    
 *   sia con statement di tipo STATICO (query nel modulo statements.xml)
 *    <MODULE name="listaDyn" class="it.eng.afExt.dispatching.module.impl.ListModule>
 *      <CONFIG pool="SIL_DATI" rows="15" title="Lista">
 *        <QUERIES>
 *          <SELECT_QUERY>
 *            <STATEMENT name="stm"/>
 *          </SELECT_QUERY>
 *        </QUERIES>
 *      </CONFIG>
 *      ......
 *    </MODULE>
 *    
 *  Inoltre entrambi i tipi di lista possono essere paginati, oppure non paginati.
 *  Per avere una LISTA NON PAGINATA basta porre l'attributo rows="-1".
 *  
 *  La parte di CONFIGURAZIONE è tipicamente strutturata in questo modo:
 *  <CONFIG pool="SIL_DATI" rows="15" title="Lista">
 *        <COLUMNS>
 *          <COLUMN name="colonna1" label="Colonna 1"/>
 *          ....
 *        </COLUMNS>
 *        <CAPTION>
 *          <SELECT_CAPTION image="../../img/detail.gif" confirm="FALSE" label="Dettaglio">
 *            <PARAMETER name="par1" scope="LOCAL" type="RELATIVE" value="par1" />
 *            ....
 *          </SELECT_CAPTION>
 *          <DELETE_CAPTION image="../../img/del.gif" confirm="FALSE" label="Cancella">
 *            <PARAMETER name="par1" scope="LOCAL" type="RELATIVE" value="par1" />
 *            ....
 *          </DELETE_CAPTION>
 *          <CAPTION image="../../img/btn1.gif" confirm="FALSE" label="Azione1">
 *            <PARAMETER name="par1" scope="LOCAL" type="RELATIVE" value="par1" />
 *            ....
 *          </CAPTION>
 *          <CAPTION image="../../img/btn2.gif" confirm="FALSE" label="Azione2">
 *            <PARAMETER name="par1" scope="LOCAL" type="RELATIVE" value="par1" />
 *            ....
 *          </CAPTION>
 *          ........
 *          <BUTTONS>
 *            <INSERT_BUTTON name="inserisci" label="Inserisci Nuovo Elemento" >
 *              <PARAMETER name="par1" scope="LOCAL" type="RELATIVE" value="par1" />
 *              ....
 *            </INSERT_BUTTON>
 *          </BUTTONS>
 *  </CONFIG>
 *  
 *  ATTRIBUTI SPECIALI (CAPTION dipendente dal singolo record):
 *  
 *  Se all'elemento <COLUMN> si associa l'attributo "notVisible" allora la colonna
 *  relativa non verrà visualizzata nella lista.
 *  (es. <COLUMN name="viewCaption1" notVisible>)
 *  Definire una colonna non visibile serve quando una caption deve esserci o
 *  non esserci a seconda della riga: nella CAPTION va definito l'attributo speciale 
 *  "hiddenColumn" che ha come valore il nome della colonna non visibile.
 *  (es. <CAPTION name="azione1" hiddenColumn="viewCaption1").
 *  Per ogni riga della lista se il valore del campo "viewCaption1" vale "0" allora
 *  la caption non verrà visualizzata, se vale "1" invece verrà visualizzata.
 * -------------------------------------------------------------------------------------
 *  
 *  CHECKBOXES 
 *  
 *  E' possibile definire delle colonne formate da checkbox nel seguente modo
 *  <CHECKBOXES>
 *    <CHECKBOX name="" label="" refColumn="" jsCheckBoxClick="">
 * 		<CHECKBOXVALUE name="" scope="" type="" value""/>
 * 		<PARAMETER name="" scope="" typeOf="" type="" value=""/>
 *      ......
 *    </CHECKBOX>
 *    .....
 *  </CHECKBOXES>
 *  
 *  I parametri definiti nel checkbox hanno lo stessi comportamento di quelli
 *  delle caption e vengono passati alla funzione definita in "jsCheckBoxClick"
 *  
 *  Il parametro CHECKBOXVALUE serve a definire il valore dell'attributo value della checkbox,
 *  il suo funzionamento è analogo a quello degli altri parametri ma il valore ottenuto viene
 *  inserito senza apici nell'attibuto value della checkbox.
 * 
 *  L'attributo opzionale typeOf (che indica il tipo di argomento Javascript passato) serve per 
 *  la corretta gestione degli apici, i possibili valori sono:
 *  - object: parametro oggetto passato senza apici (serve nel caso si volgiano inserire nella chamata 
 * 			  riferimenti al DOM, come ad esempio this);
 *  - string: parametro passato come stringa con apici (è il default)
 */

import it.eng.afExt.utils.StringUtils;
import it.eng.sil.util.Utils;

public class DefaultListTag extends TagSupport {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(DefaultListTag.class.getName());
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
	private String skipNavigationButton = "0";

	ConfigSingleton configure = null;
	SourceBean selectCaption = null;
	SourceBean deleteCaption = null;
	Vector genericCaption = null;
	Vector genericCheckBox = null;

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
		genericCheckBox = _layout.getAttributeAsVector("CHECKBOXES.CHECKBOX");

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
		String titolo = (String) _layout.getAttribute("TITLE");
		String sceltaNomeTitolo = "0";
		if (_serviceResponse.containsAttribute("M_CONFIG_UMB_NGE_AZ")) {
			sceltaNomeTitolo = Utils.notNull(_serviceResponse.getAttribute("M_CONFIG_UMB_NGE_AZ.ROWS.ROW.NUM"));
		}

		String titolo2 = null;
		if (_layout.getAttribute("TITLE2") != null) {
			titolo2 = (String) _layout.getAttribute("TITLE2");
		}
		if (titolo2 != null && sceltaNomeTitolo != null && sceltaNomeTitolo.equalsIgnoreCase("1")) {
			titolo = titolo2;
		}

		_htmlStream.append("<H2>" + titolo + "</H2>\n");
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

		// GG 2-set-04
		_htmlStream.append("<TABLE align=\"center\">");

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

		SourceBean rowsSB = (SourceBean) _content.getAttribute("ROWS");
		if (rowsSB != null) {
			currentPage = ((Integer) rowsSB.getAttribute("CURRENT_PAGE")).intValue();
			numPages = ((Integer) rowsSB.getAttribute("NUM_PAGES")).intValue();
			numRecords = ((Integer) rowsSB.getAttribute("NUM_RECORDS")).intValue();
			rowsXpage = ((Integer) rowsSB.getAttribute("ROWS_X_PAGE")).intValue();

			firstShownRec = (currentPage - 1) * rowsXpage + 1;
			lastShownRec = currentPage * rowsXpage;
			if (lastShownRec > numRecords) {
				lastShownRec = numRecords;
			}
		}

		int prevPage = currentPage - 1;
		if (prevPage < 1)
			prevPage = 1;
		int nextPage = currentPage + 1;
		// if (nextPage > numPages)
		// nextPage = numPages;
		String queryString = getQueryString();

		/* bottoni di navigazione */
		// GG 8/10/2004 - disabilito i bottoni non necessari.
		_htmlStream
				.append("<TABLE border=\"0\" cellspacing=\"0\" cellpadding=\"1px\" align=\"center\" maxwidth=\"55%\">"
						+ "<TR>\n");

		boolean disabled = (numPages == 0) || (numPages == 1);

		_htmlStream.append("<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>");

		if (disabled || (currentPage == 1)) { // Se su prima pagina, no
												// bottoni sinistra
			_htmlStream.append(
					"<td width=\"18px\"><img src=\"../../img/list_first_gri.gif\" alt=\" |<< \" border=\"0\" width=\"16\" height=\"16\" /></td>");
			_htmlStream.append(
					"<td width=\"18px\"><img src=\"../../img/list_prev_gri.gif\" alt=\" < \" border=\"0\" width=\"16\" height=\"16\" /></td>");
		} else {
			_htmlStream.append(
					"<TD width=\"18px\"><BUTTON width=\"16px\" type=\"button\" class=\"ListButtonChangePage\" onclick=\"goConfirm"
							+ _serviceName + "('" + queryString + _providerURL
							+ "MESSAGE=LIST_FIRST','FALSE'); return false;\">"
							+ "<img src=\"../../img/list_first.gif\" alt=\" |<< \" border=\"0\" width=\"16\" height=\"16\" /></BUTTON>"
							+ "</TD>\n");

			_htmlStream.append(
					"<TD width=\"18px\"><BUTTON width=\"16px\" type=\"button\" class=\"ListButtonChangePage\" onclick=\"goConfirm"
							+ _serviceName + "('" + queryString + _providerURL + "MESSAGE=LIST_PAGE&LIST_PAGE="
							+ prevPage + "','FALSE'); return false;\">"
							+ "<img src=\"../../img/list_prev.gif\" alt=\" < \" border=\"0\" width=\"16\" height=\"16\" /></BUTTON>"
							+ "</TD>\n");
		}

		if (disabled || (currentPage == numPages)) { // Se su ultima pagina,
														// no bottoni destra
			_htmlStream.append(
					"<td width=\"18px\"><img src=\"../../img/list_next_gri.gif\" alt=\" > \" border=\"0\" width=\"16\" height=\"16\" /></td>");
			_htmlStream.append(
					"<td width=\"18px\"><img src=\"../../img/list_last_gri.gif\" alt=\" >>| \" border=\"0\" width=\"16\" height=\"16\" /></td>");
		} else {
			_htmlStream.append(
					"<TD width=\"18px\"><BUTTON width=\"16px\" type=\"Button\" class=\"ListButtonChangePage\" onclick=\"goConfirm"
							+ _serviceName + "('" + queryString + _providerURL + "MESSAGE=LIST_PAGE&LIST_PAGE="
							+ nextPage + "','FALSE'); return false;\">"
							+ "<img src=\"../../img/list_next.gif\" alt=\" > \" border=\"0\" width=\"16\" height=\"16\" /></BUTTON>"
							+ "</TD>\n");

			_htmlStream.append(
					"<TD width=\"18px\"><BUTTON width=\"16px\" type=\"Button\" class=\"ListButtonChangePage\" onclick=\"goConfirm"
							+ _serviceName + "('" + queryString + _providerURL
							+ "MESSAGE=LIST_LAST','FALSE'); return false;\">"
							+ "<img src=\"../../img/list_last.gif\" alt=\" >>| \" border=\"0\" width=\"16\" height=\"16\" /></BUTTON>"
							+ "</TD>\n");
		}

		// _htmlStream.append("<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>\n");
		// Stefy: vai a pagina nro:
		int colNum = 6;
		if (!disabled && numPages > 1) {
			colNum = 7;
			// _htmlStream.append("<tr><td colspan=\"6\" align=\"center\">");
			_htmlStream.append("<td>&nbsp;&nbsp;</td>\n");
			_htmlStream.append("<td align=\"right\">");
			_htmlStream.append("&nbsp;Pag. <select name=\"selPage\">");
			// _htmlStream.append("<option value=\"0\"></option>");
			for (int i = 1; i <= numPages; i++) {
				_htmlStream.append("<option value=\"" + i + "\"");
				if (i == currentPage) {
					_htmlStream.append(" selected");
				}
				_htmlStream.append(">" + i + "</option>");
			}
			_htmlStream.append("</select>&nbsp;");
			_htmlStream.append("<BUTTON width=\"16px\" type=\"Button\" class=\"pulsanti\" onclick=\"goPage"
					+ _serviceName + "('" + queryString + _providerURL + "MESSAGE=LIST_PAGE&LIST_PAGE='," + currentPage
					+ "); return false;\">" + "Vai</BUTTON>");
			_htmlStream.append("</td>");
		} else {
			_htmlStream.append("<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>\n");
		}

		_htmlStream.append("</TR>\n");

		_htmlStream.append("<TR><TD COLSPAN=\"" + colNum + "\" align=\"center\"><B>Pag. " + currentPage);

		if (numPages != -1) {
			_htmlStream.append(" di " + numPages + "\n");
		}

		if (numRecords == 0) {

			_htmlStream.append(" (Nessun risultato)");

		} else {

			_htmlStream.append(" (da " + firstShownRec + " a " + lastShownRec);

			if (numPages != -1) {
				_htmlStream.append(" di " + numRecords);
			}

			_htmlStream.append(")");

		}

		_htmlStream.append("<B></TD>\n");

		//
		if (!disabled && numPages > 1) {
			_htmlStream.append("<SCRIPT language=\"Javascript\" type=\"text/javascript\">\n");
			_htmlStream.append("<!--\n");
			_htmlStream.append("function goPage" + _serviceName + "(url, currP) {\n");

			// GG 1/10/2004 - il corpo della funzione è stato spostato nella
			// funzione "goConfirmGenericCustomTL()" nel file customTL.js
			_htmlStream.append("var coll = document.getElementsByName(\"selPage\");\n");
			_htmlStream.append("if(coll.length>0) {\n");
			_htmlStream.append("var i = coll[0].value;\n");
			_htmlStream.append("url = url + i;\n");
			// _htmlStream.append("alert(i); alert(currP);");
			_htmlStream
					.append("if((i>0) && (i!=currP)) { goConfirmGenericCustomTL(url, 'FALSE'); } // in customTL.js\n");
			_htmlStream.append("}\n");
			_htmlStream.append("}\n");
			_htmlStream.append("// -->\n");
			_htmlStream.append("</SCRIPT>\n");
		}

		_sessionContainer.setAttribute("_TOKEN_" + pageName.toUpperCase(),
				_providerURL + queryString + "MESSAGE=LIST_PAGE&LIST_PAGE=" + currentPage);

		// 01-04-10 Rodi - torno alla lista?
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

		for (int i = 0; i < genericCheckBox.size(); i++) {
			String nomeColonna = (String) ((SourceBean) genericCheckBox.elementAt(i)).getAttribute("LABEL");
			if (!((SourceBean) genericCheckBox.elementAt(i)).containsAttribute("notVisible")) {
				_htmlStream.append("<TH class=\"lista\">&nbsp;" + nomeColonna + "&nbsp;</TH>\n");
			}
		} // for (int i = 0; i < genericCheckBox.size(); i++)

		Vector rows = null;
		rows = _content.getAttributeAsVector("ROWS.ROW");
		String sceltaNomeColonna = null;
		if (rows.size() > 0) {
			SourceBean row = (SourceBean) rows.get(0);
			Object valoreConfigLabel = row.getAttribute("CHOICELABEL");

			if (valoreConfigLabel != null) {
				sceltaNomeColonna = valoreConfigLabel.toString();
			}
		}

		for (int i = 0; i < _columns.size(); i++) {

			String nomeColonna = (String) ((SourceBean) _columns.elementAt(i)).getAttribute("LABEL");
			String nomeColonna2 = null;
			if (((SourceBean) _columns.elementAt(i)).getAttribute("LABEL2") != null) {
				nomeColonna2 = (String) ((SourceBean) _columns.elementAt(i)).getAttribute("LABEL2");
			}
			if (StringUtils.isEmptyNoBlank(sceltaNomeColonna)) {
				if (_serviceResponse.containsAttribute("M_CONFIG_UMB_NGE_AZ")) {
					sceltaNomeColonna = Utils
							.notNull(_serviceResponse.getAttribute("M_CONFIG_UMB_NGE_AZ.ROWS.ROW.NUM"));
				}

			}
			if (nomeColonna2 != null && sceltaNomeColonna != null && sceltaNomeColonna.equalsIgnoreCase("1")) {
				nomeColonna = nomeColonna2;
			}
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
			boolean viewCapt = true;

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
			viewCapt = viewCaptionButton(selectCaption, row);
			if (selectCaption != null) { // Caption di selezione
				if (viewCapt) {
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

					if ((_jsSelect == null) || (_jsSelect.length() == 0)) { // corretto
																			// da
																			// GG
						// chiamo la go_confirm standard
						StringBuffer parametersStr = getParametersList(parameters, row);
						_htmlStream.append("<TD class=\"caption\"><A href=\"javascript://\" onclick=\"goConfirm"
								+ _serviceName + "(" + "'" + parametersStr + "'" + ", " + "'" + confirmSelect + "'"
								+ "); return false;\"><IMG name=\"image\" border=\"0\" " + " src=\"" + imageSelect
								+ "\" alt=\"" + labelSelect + "\" title=\"" + labelSelect + "\"/></A></TD>\n");
					} else // altrimenti, invece della go-confirm standard
							// chiamo la mia funzione javascript
					{
						// Reperisco i parametri da passarecome argomento alla
						// funzione javascript
						// I parametri sono quelli definiti nella caption
						String parametriJs = getJSParameters(parameters, row);

						_htmlStream.append("<TD class=\"caption\"><A href=\"javascript://\" onclick=\"" + _jsSelect
								+ "(" + parametriJs + ");" + " return false;\"><IMG name=\"image\" border=\"0\" "
								+ " src=\"" + imageSelect + "\" alt=\"" + labelSelect + "\" title=\"" + labelSelect
								+ "\"/></A></TD>\n");
					}
				} else {
					_htmlStream.append("<TD class=\"caption\">&nbsp;</TD>");
				} // if(viewCapt)
			} else {
				_htmlStream.append("<TD class=\"caption\">&nbsp;</TD>");
			} // if (selectCaption != null)

			for (int j = 0; j < genericCaption.size(); j++) {
				SourceBean caption = (SourceBean) genericCaption.elementAt(j);
				viewCapt = viewCaptionButton(caption, row);
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
					StringBuffer parametersStr = getParametersList(parameters, row);

					// FV 14/02/2005
					if (!parametersStr.toString().endsWith("&")) {
						parametersStr.append("&LIST_PAGE=" + _content.getAttribute("ROWS.CURRENT_PAGE"));

					} else
						parametersStr.append("LIST_PAGE=" + _content.getAttribute("ROWS.CURRENT_PAGE"));

					String onClickCaption = (String) caption.getAttribute("ONCLICK");
					if ((onClickCaption == null) || (onClickCaption.length() == 0)) {
						onClickCaption = "goConfirm" + _serviceName;
					}

					_htmlStream.append("<TD class=\"caption\"><A href=\"javascript://\" onclick=\"" + onClickCaption
							+ "(" + "'" + parametersStr + "'" + ", " + "'" + confirmCaption + "'"
							+ "); return false;\"><IMG name=\"image\" border=\"0\" " + "src=\"" + imageCaption
							+ "\" alt=\"" + labelCaption + "\" title=\"" + labelCaption + "\"/></A></TD>\n");
				} else {
					_htmlStream.append("<TD class=\"caption\">&nbsp;</TD>");
				} // if(viewCapt)
			} // for (int j = 0; j < genericCaption.size(); j++)

			_htmlStream.append("</TR></TABLE>\n");
			_htmlStream.append("</TD>\n");

			// CHECKBOX
			for (int j = 0; j < genericCheckBox.size(); j++) {
				if (!((SourceBean) genericCheckBox.elementAt(j)).containsAttribute("notVisible")) {
					SourceBean cbSb = (SourceBean) (genericCheckBox.elementAt(j));
					String refColumn = StringUtils.getAttributeStrNotNull(cbSb, "refColumn");
					Object cbName = null;
					if (!refColumn.equals("")) {
						cbName = row.getAttribute(refColumn);
					} else
						cbName = "";
					String _checkBoxClick = StringUtils.getAttributeStrNotNull(cbSb, "jsCheckBoxClick");
					Vector parameters = cbSb.getAttributeAsVector("PARAMETER");

					// Gestione del parametro che determina l'attributo value
					// della checkbox
					Vector checkboxValueVector = new Vector();
					SourceBean sbSheckboxValue = (SourceBean) cbSb.getAttribute("CHECKBOXVALUE");
					if (sbSheckboxValue != null) {
						checkboxValueVector.add(sbSheckboxValue);
					}
					String checkboxValue = getJSParameters(checkboxValueVector, row);
					// FINE Gestione del parametro che determina l'attributo
					// value della checkbox

					String parametriJs = getJSParameters(parameters, row);
					_htmlStream.append("<TD align=\"center\" class=\"" + classRow + "\"> " + "<INPUT type=\"checkbox\" "
							+ "name=\"" + (String) cbSb.getAttribute("NAME") + cbName.toString() + "\" ");
					if ((checkboxValue.length() > 0) && !checkboxValue.equals("''")) {
						_htmlStream.append("value=" + checkboxValue + " ");
					}
					if (!_checkBoxClick.equals("")) {
						_htmlStream.append("onclick=\"javascript:" + _checkBoxClick + "(" + parametriJs + ");\"");
					}
					_htmlStream.append("/>" + "</TD>\n");
				}
			}

			// Colonne
			for (int j = 0; j < _columns.size(); j++) {
				String nomeColonna = (String) ((SourceBean) _columns.elementAt(j)).getAttribute("NAME");
				String preformCol = StringUtils.getAttributeStrNotNull((SourceBean) _columns.elementAt(j), "PRE");
				Object fieldObject = row.getAttribute(nomeColonna);
				String field = null;
				if (fieldObject != null)
					field = fieldObject.toString();
				else
					field = "&nbsp;";
				if (!((SourceBean) _columns.elementAt(j)).containsAttribute("notVisible")) {
					if (preformCol.equals("1")) {
						_htmlStream.append(
								"<TD class=\"" + classRow + "\"><pre class=\"pre_lista\">" + field + "</pre></TD>\n");
					} else if (preformCol.equalsIgnoreCase("budget")) {
						_htmlStream.append("<TD class=\"" + classRow + "\" style=\"text-align: right !important;\"> "
								+ field + "</TD>\n");
					} else {
						_htmlStream.append("<TD class=\"" + classRow + "\"> " + field + "</TD>\n");
					}

				}
			}

			viewCapt = viewCaptionButton(deleteCaption, row);
			if ((deleteCaption != null) && (!_canDelete.equals("0"))) {
				if (viewCapt) {
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
					if ((_jsDelete == null) || (_jsDelete.length() == 0)) { // corretto
																			// da
																			// GG
						// se non è presente alcun script javascript per il
						// caption di delete
						// chiamo la go_confirm standard
						StringBuffer parametersStr = getParametersList(parameters, row);
						// parametersStr.append("NAVIGATOR_DISABLED=TRUE&");
						// parametersStr.append(_providerURL);
						parametersStr.append("&LIST_PAGE=" + _content.getAttribute("ROWS.CURRENT_PAGE"));

						_htmlStream.append("<TD class=\"" + classRow
								+ "\"><A href=\"javascript://\" onclick=\"goConfirm" + _serviceName + "(" + "'"
								+ parametersStr + "'" + ", " + "'" + confirmDelete + "'"
								+ "); return false;\"><IMG name=\"image\" border=\"0\" " + "src=\"" + imageDelete
								+ "\" alt=\" " + labelDelete + "\" title=\"" + labelDelete + "\"/ ></A></TD>\n");
					} else {
						// altrimenti, invece della go-confirm standard chiamo
						// la mia funzione javascript
						// Reperisco i parametri da passarecome argomento alla
						// funzione javascript
						// I parametri sono quelli definiti nella caption
						String parametriJs = getJSParameters(parameters, row);
						_htmlStream.append("<TD class=\"" + classRow + "\"><A href=\"javascript://\" onclick=\""
								+ _jsDelete + "(" + parametriJs + ");"
								+ " return false;\"><IMG name=\"image\" border=\"0\" " + " src=\"" + imageDelete
								+ "\" alt=\"" + labelDelete + "\" title=\"" + labelDelete + "\"/></A></TD>\n");
					}
				} else {
					_htmlStream.append("<TD class=\"" + classRow + "\">&nbsp;</TD>");
				} // if(viewCapt) {
			} // if (deleteCaption != null)

			_htmlStream.append("</TR>\n");
		} // for (int j = 0; j < rows.size(); j++)
		_htmlStream.append("</TABLE>\n");
	} // private void makeRows()

	protected boolean viewCaptionButton(SourceBean caption, SourceBean row) {
		String hiddenValue = "";
		String hiddenColumn = "";
		if (caption != null) {
			hiddenColumn = StringUtils.getAttributeStrNotNull(caption, "hiddenColumn");
		}
		if (!hiddenColumn.equals("")) {
			hiddenValue = StringUtils.getAttributeStrNotNull(row, hiddenColumn);
		}
		if (hiddenValue.equals("0")) {
			return (false);
		} else {
			return (true);
		}
	}

	protected void makeButton() throws JspException {
		SourceBean insertButton = (SourceBean) _layout.getAttribute("BUTTONS.INSERT_BUTTON");
		Vector genericButton = _layout.getAttributeAsVector("BUTTONS.BUTTON");

		// GG 2-set-04: sostituite le due righe:
		// _htmlStream.append("<TR class=\"lista\">\n");
		// _htmlStream.append("<TD class=\"lista\">\n");
		// con le:
		_htmlStream.append("<TR>\n");
		_htmlStream.append("<TD>\n");

		// GG 2-set-04: sostituita riga:
		// _htmlStream.append("<TABLE border=0 cellpadding=0
		// cellspacing=0><TR>\n");
		// con:
		_htmlStream.append("<TABLE border=0 cellpadding=0 cellspacing=0>\n");

		_htmlStream.append("<TR><TD><BR></TD><TD><BR></TD></TR>\n");

		// GG 2-set-04: sostituita riga:
		// _htmlStream.append("<TABLE width=\"90%\" align=\"center\"><TR>\n");
		// con:
		_htmlStream.append("<TR>\n");

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
						+ labelInsert + "\" title=\"" + labelInsert + "\"/></A></TD>\n");
			else
				_htmlStream.append("<TD align=\"center\"><INPUT type=\"Button\" class=\"ButtonChangePage\" value=\""
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
						+ labelButton + "\" title=\"" + labelButton + "\"/></A></TD>\n");
			else
				_htmlStream.append("<TD><INPUT type=\"Button\" class=\"ButtonChangePage\" value=\"" + labelButton
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
			String typeOf = (String) ((SourceBean) parameters.elementAt(i)).getAttribute("TYPEOF");
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
						if (valueObject != null)
							value = valueObject.toString();
						else
							value = "";
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
				// Controllo se devo inserire o meno gli apici
				if (typeOf != null && typeOf.equalsIgnoreCase("OBJECT")) {
					parametersList.append(value);
				} else {
					parametersList.append("'" + value + "'");
				}
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

} // public class DefaultListTag extends TagSupport
