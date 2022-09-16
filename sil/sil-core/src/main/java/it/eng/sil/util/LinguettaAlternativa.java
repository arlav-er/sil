package it.eng.sil.util;

import java.io.IOException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.servlet.jsp.JspWriter;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.DataConnectionManager;
import com.engiweb.framework.dbaccess.SQLStatements;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.SQLCommand;
import com.engiweb.framework.dbaccess.sql.result.DataResult;
import com.engiweb.framework.dbaccess.sql.result.ScrollableDataResult;

import it.eng.afExt.utils.LogUtils;
import it.eng.sil.Values;

public class LinguettaAlternativa {

	protected Object prgAzienda = null;
	protected Object prgUnita = null;
	protected Object prgRichiestaAz = null;
	protected Object prgAlternativa_curr = null;
	protected boolean flag_insert = false;
	protected List listaAlternative = null;
	protected int _funzione = 0;
	protected String page = null;
	protected boolean flag_readonly = false;

	/**
	 * Costruttori.
	 * 
	 * @param prgRichiestaAz
	 */
	private LinguettaAlternativa() {
	}

	public LinguettaAlternativa(Object prgAzienda, Object prgUnita, Object prgRichiestaAz, Object prgAlternativa_curr,
			int _funzione, String page, boolean flag_readonly) {
		this(prgAzienda, prgUnita, prgRichiestaAz, prgAlternativa_curr, false, _funzione, page, flag_readonly);
	}

	public LinguettaAlternativa(Object prgAzienda, Object prgUnita, Object prgRichiestaAz, Object prgAlternativa_curr,
			boolean flag_insert, int _funzione, String page, boolean flag_readonly) {
		this.prgAzienda = prgAzienda;
		this.prgUnita = prgUnita;
		this.prgRichiestaAz = prgRichiestaAz;
		this.prgAlternativa_curr = prgAlternativa_curr;
		this.flag_insert = flag_insert;
		this._funzione = _funzione;
		this.page = page;
		if (prgRichiestaAz != null) {
			setListaAlternative(getListAlternative(prgRichiestaAz));
		}
		this.flag_readonly = flag_readonly;
	}

	private Vector getListAlternative(Object prgRichiestaAz) {
		Vector righe = null;
		Map result = new HashMap();

		DataConnection dataConnection = null;
		SQLCommand sqlCommand = null;
		DataResult dataResult = null;

		try {
			DataConnectionManager dataConnectionManager = DataConnectionManager.getInstance();
			dataConnection = dataConnectionManager.getConnection(Values.DB_SIL_DATI);

			String statement = SQLStatements.getStatement("GET_LIST_ALTERNATIVE");
			sqlCommand = dataConnection.createSelectCommand(statement);

			List inputParameter = new ArrayList();
			inputParameter.add(dataConnection.createDataField("", Types.NUMERIC, prgRichiestaAz));

			dataResult = sqlCommand.execute(inputParameter);

			ScrollableDataResult scrollableDataResult = (ScrollableDataResult) dataResult.getDataObject();
			SourceBean sb = scrollableDataResult.getSourceBean();

			righe = sb.getAttributeAsVector("ROW");
		} // try
		catch (com.engiweb.framework.error.EMFInternalError ex) {
			LogUtils.logError("GET_LIST_ALTERNATIVE", "Internal Error", ex, this);
		} finally {
			com.engiweb.framework.dbaccess.Utils.releaseResources(dataConnection, sqlCommand, dataResult);
		}
		return righe;
	}// getInfoAzienda(_)

	private void setListaAlternative(List alternative) {
		Object prgAlternativa = null;
		SourceBean prgAlternativaSb = null;
		listaAlternative = new ArrayList();
		for (int i = 0; i < alternative.size(); i++) {
			prgAlternativaSb = (SourceBean) alternative.get(i);
			prgAlternativa = prgAlternativaSb.getAttribute("prgAlternativa");
			listaAlternative.add(prgAlternativa);
		}
	}

	// render dei dati
	private String buildJavascript() {
		String javascript = "<SCRIPT type=\"text/javascript\">\n";

		javascript += "boolaperto=false; \n" + "var tabShow=1; \n" + "function hidepoptext() { \n"
				+ "      var tabDiv = document.getElementById(\"tabDiv\"); \n"
				+ "      var poptextDiv=document.getElementById(\"poptextDiv\"); \n "
				+ "  if (tabShow == 0) { //chiudo la lista \n" + "      tabDiv.style.visibility = \"hidden\"; \n "
				+ "      tabDiv.style.visibility = \"visible\"; \n "
				+ "      poptextDiv.style.visibility = \"hidden\"; \n " + "      tabShow = 1; \n "
				+ "        return; \n " + "    } \n " + "  if (tabShow == 1) { //apro la lista \n "
				+ "			tabDiv.style.visibility = \"hidden\"; " + "     tabDiv.style.visibility = \"visible\"; \n "
				+ "     poptextDiv.style.visibility = \"visible\"; \n" + "      tabShow = 0; \n " + "      return; \n "
				+ "   }\n" + "}\n" + "imgLinguettaAlternativa=new Array(); \n"
				+ "imgLinguettaAlternativa[0]=new Image(); \n "
				+ "imgLinguettaAlternativa[0].src=\"../../img/chiuso.gif\"; \n "
				+ "imgLinguettaAlternativa[1]=new Image(); \n"
				+ "imgLinguettaAlternativa[1].src=\"../../img/aperto.gif\"; \n" + "</SCRIPT>\n";

		return javascript;
	}

	private String buildStyle() {
		String style = "<STYLE>\n";

		style += "<!--  \n" + "#tabdiv { \n" + "z-index:2; \n" + "visibility:show; \n" + "position:absolute; \n"
				+ "left:4%; \n" + "margin: 0; \n" + "padding: 0; \n" + "}\n" + "#poptextdiv { \n"
				+ "        visibility:hide; \n" + "        visibility:hidden;\n" + "        position:absolute; \n"
				+ "        width=85px; \n" + "        left:4%; \n" + "        z-index:0; \n"
				+ "	      color: #000066; \n" + "        border-width: 1px; \n" + "	      border-left-style: solid; \n"
				+ "        border-right-style:solid; \n" + "        border-bottom-style:solid;\n"
				+ "       	border-color: #4eb1fa;\n" + "        background-color:#ffffff; \n" + "        z-index:1; \n"
				+ "} \n" + " -->\n" + "</style>\n";

		return style;
	}

	private String buildHtml() {

		String html = "";
		html += "<div ID=\"tabDiv\"> \n"
				+ " <a href=\"javascript:hidepoptext();\" class=\"bordato1_alt\" onclick=\" if (boolaperto) { \n"
				+ "                                                           aperto.src=imgLinguettaAlternativa[0].src;\n "
				+ "                                                           boolaperto=false; \n "
				+ "                                                         } else {  \n"
				+ "                                                             aperto.src=imgLinguettaAlternativa[1].src; \n"
				+ "                                                             boolaperto=true;\n"
				+ "                                                         } \n"
				+ "                                                       \">&nbsp;&nbsp;&nbsp;&nbsp;Profili&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<img name=\"aperto\" src=\"../../img/chiuso.gif\"/></a>\n"
				+ "</div>\n" + "<div ID=\"poptextDiv\" >\n";
		if (!flag_readonly) {
			html += "<br/><br/> \n" + " <center><A HREF=\"AdapterHTTP?PAGE=IdoEtaEsperienzaPage&prgAzienda="
					+ prgAzienda + "&prgUnita=" + prgUnita + "&prgRichiestaAz=" + prgRichiestaAz + "&cdnFunzione="
					+ _funzione + "&insert_alternativa=true\" class=";
			html += (flag_insert) ? "\"sel2_alt\"" : "\"bordato2_alt\"";
			html += ">Nuovo</A><br/><br/>" + "</center>\n";
		} else {
			html += "<center><br/><br/><br/><br/></center> \n";
		}
		// itero il vector per prelevare le alternative
		Object prgAlternativa = null;
		for (int i = 0; i < listaAlternative.size(); i++) {
			prgAlternativa = listaAlternative.get(i);
			html += "<A HREF=\"AdapterHTTP?PAGE=" + page + "&prgRichiestaAz=" + prgRichiestaAz + "&prgAlternativa="
					+ prgAlternativa + "&cdnFunzione=" + _funzione + "\" \n" + " class=";
			html += (prgAlternativa_curr.toString().equals(prgAlternativa.toString())) ? "\"sel2_alt\""
					: "\"bordato2_alt\"";
			// riga modificata per debug
			html += "> Profilo n. " + prgAlternativa + "</A><br/><br/>";
		}
		html += "<br/>" + "</div><br> \n";

		return html;
	}

	public void show(JspWriter out) throws IOException {

		String outString = "";

		outString += buildJavascript();
		outString += buildStyle();
		outString += buildHtml();
		out.print(outString);
	}

}