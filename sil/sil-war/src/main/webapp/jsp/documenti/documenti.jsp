<%@ page contentType="text/html;charset=utf-8" %>

<%@ page import=" com.engiweb.framework.base.*,
					it.eng.sil.security.*,
					it.eng.sil.util.*,
					it.eng.afExt.utils.*,
					java.util.*,
					java.math.*" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ taglib uri="aftags" prefix="af" %>

<%
	//String _page = "DocumentiAssociatiPage";
	String  _page          = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"PAGE"); 

	int     cdnfunzione      = SourceBeanUtils.getAttrInt(serviceRequest, "cdnfunzione");
	String  cdnLavoratore    = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"cdnLavoratore");
	String  prgAzienda       = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"prgAzienda");
	String  prgUnita         = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"prgUnita");
	boolean lookLavoratore   = SourceBeanUtils.getAttrBoolean(serviceRequest, "lookLavoratore", false);
	boolean lookAzienda      = SourceBeanUtils.getAttrBoolean(serviceRequest, "lookAzienda", false);
	String  contesto         = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "contesto");
	String  pagina           = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"pagina");
	String  popUp            = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"popUp");
	String  infStoriche      = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"infStoriche");
	String  strChiaveTabella = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"strChiaveTabella");
	String  goBackListPage   = _page;

	String frameName         = SourceBeanUtils.getAttrStr(serviceRequest, "FRAME_NAME", "main");
	// e' nome del frame da cui e' partita la richiesta del documento di identita', 
	// dichiarazione di immediata disponibilita' oppure il patto 150. In questo caso il refresh deve essere della popUp
	// aperta dalla pagina del patto e non del frame "main", come nel caso della did.

	String  titolo = "";
	if (infStoriche.equalsIgnoreCase("true")) titolo ="Informazioni storiche";
	else if (StringUtils.isFilled(pagina))    titolo ="Documenti associati alla pagina";
	
	boolean hasInfoStoriche = serviceResponse.containsAttribute("M_DOC_HASINFOSTORICHE.ROWS.ROW");
	
	//Se si proviene dal dettaglio movimento non si deve visualizzare il pulsante nuovo
	boolean canViewNuovo =true;
	canViewNuovo = !pagina.equalsIgnoreCase("MovDettaglioGeneraleConsultaPage");
%>

<html>
<head>
<link rel="stylesheet" type="text/css" href="../../css/stili.css" />
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css" />

<af:linkScript path="../../js/"/>
<script language="Javascript" src="../../js/documenti/docPopup.js"></script>

<script language="javascript">

	function nuovo() {
		// Se la pagina è già in submit, ignoro questo nuovo invio!
		if (isInSubmit()) return;
	
		var url = "AdapterHTTP?PAGE=DettagliDocumentoPage" +
							"&NUOVO=true" +
							"&cdnLavoratore=<%=cdnLavoratore%>" +
							"&prgAzienda=<%=prgAzienda%>&prgUnita=<%=prgUnita%>" +
							"&lookLavoratore=<%=lookLavoratore%>" +
							"&lookAzienda=<%=lookAzienda%>" +
							"&contesto=<%=contesto%>" +
							"&pagina=<%=pagina%>" +
							"&popUp=<%=popUp%>" +
							"&strChiaveTabella=<%=strChiaveTabella%>" +
							"&FRAME_NAME=<%=frameName%>" +
							"&goBackListPage=<%= goBackListPage %>" +
							"&cdnfunzione=<%=cdnfunzione%>";	
		setWindowLocation(url);
	}

	/*
	 * Va alla pagina di ricerca
	 */
	function ricerca() {
		// Se la pagina è già in submit, ignoro questo nuovo invio!
		if (isInSubmit()) return;

		var url = "AdapterHTTP?PAGE=RicercaDocAssociatiPage" +
							"&cdnLavoratore=<%=cdnLavoratore%>" +
							"&prgAzienda=<%=prgAzienda%>&prgUnita=<%=prgUnita%>" +
							"&lookLavoratore=<%=lookLavoratore%>" +
							"&lookAzienda=<%=lookAzienda%>" +
							"&contesto=<%=contesto%>" +
							"&pagina=<%=pagina%>" +
							"&popUp=<%=popUp%>" +
							"&strChiaveTabella=<%=strChiaveTabella%>" +
							"&infStoriche=<%=infStoriche%>" +
							"&goBackListPage=<%= goBackListPage %>" +
							"&cdnfunzione=<%=cdnfunzione%>";
		setWindowLocation(url);
	}


	/*
	 * Va oppure apre pop-up di informazioni storiche
	 */
	function infStoriche() {

		var url = "AdapterHTTP?PAGE=DocumentiAssociatiPage" +
							"&cdnLavoratore=<%=cdnLavoratore%>" +
							"&prgAzienda=<%=prgAzienda%>&prgUnita=<%=prgUnita%>" +
							"&lookLavoratore=<%=lookLavoratore%>" +
							"&lookAzienda=<%=lookAzienda%>" +
							"&contesto=<%=contesto%>" +
							"&pagina=<%=pagina%>" +
							"&strChiaveTabella=<%=strChiaveTabella%>" +
							"&infStoriche=true" +
							"&cdnfunzione=<%=cdnfunzione%>";

		<% if (StringUtils.isFilled(popUp)) { %>
			// Se la pagina è già in submit, ignoro questo nuovo invio!
			if (isInSubmit()) return;

			url += "&popUp=<%=popUp%>";
			setWindowLocation(url);
		<% } else { %>
			url += "&popUp=true";
			//var titolo = "InfoStoriche";
			var w = (screen.availWidth)*0.85;   var l = (screen.availHeight)*0.1;
			var h = (screen.availHeight)*0.8;   var t = (screen.availHeight)*0.1;
			var feat = "status=NO,location=NO,toolbar=NO,scrollbars=YES,resizable=YES,height="+h+",width="+w+",top="+t+",left="+l;
			window.open(url, '', feat);
		<% } %>
	}


	/*
	 * Torna alla lista dei documenti associati
	 */
	function listaDoc() {

		<% if (StringUtils.isFilled(pagina)) { %>

			// Se la pagina è già in submit, ignoro questo nuovo invio!
			if (isInSubmit()) return;
			
			var url = "AdapterHTTP?PAGE=DocumentiAssociatiPage" +
								"&cdnLavoratore=<%=cdnLavoratore%>" +
								"&prgAzienda=<%=prgAzienda%>&prgUnita=<%=prgUnita%>" +
								"&lookLavoratore=<%=lookLavoratore%>" +
								"&lookAzienda=<%=lookAzienda%>" +
								"&contesto=<%=contesto%>" +
								"&pagina=<%=pagina%>" +
								"&popUp=<%=popUp%>" +
								"&strChiaveTabella=<%=strChiaveTabella%>" +
								"&cdnfunzione=<%=cdnfunzione%>";
			setWindowLocation(url);

		<% } else { %>

			window.close();
		<% } %>
	}

	
	function onLoad() {
		checkError();
		<% if (! popUp.equalsIgnoreCase("true")) { %>
			rinfresca();
		<% } %>
	}

<%
if (! popUp.equalsIgnoreCase("true")) {
	if (contesto.equalsIgnoreCase("L")) {
		%>
		window.top.menu.caricaMenuLav(<%=cdnfunzione%>, <%=cdnLavoratore%>);
		<%
	} else if (contesto.equalsIgnoreCase("A")) {
		%>
		window.top.menu.caricaMenuAzienda(<%=cdnfunzione%>, <%=prgAzienda%>, <%=prgUnita%>);
		<%
	}
}
%>

</script>
</head>

<body class="gestione" onload="onLoad()">

<%
	if (! popUp.equalsIgnoreCase("true")) {
	
		// TESTATE
		if (StringUtils.isFilled(cdnLavoratore)) {
			InfCorrentiLav testata = new InfCorrentiLav(cdnLavoratore, user);
			testata.show(out);
		}
		
		if (StringUtils.isFilled(prgAzienda)) {
			InfCorrentiAzienda testata = new InfCorrentiAzienda(prgAzienda, prgUnita);
			testata.show(out);
		}
		
		// LINGUETTE!
		if (StringUtils.isFilled(cdnLavoratore)) {
			Linguette linguette = new Linguette(user, cdnfunzione, _page, new BigDecimal(cdnLavoratore));
			linguette.show(out);
		}
	}
%>

<af:error />

<af:form dontValidate="true">

<table class="main" width="100%">
<%
if (popUp.equalsIgnoreCase("true")) {

	if (StringUtils.isFilled(cdnLavoratore)) {
		Vector rows = serviceResponse.getAttributeAsVector("M_GetDatiLav.ROWS.ROW");
		SourceBean row = null;
		if ((rows != null) && ! rows.isEmpty()) row = (SourceBean) rows.elementAt(0);

		StringBuffer txtOut = new StringBuffer();
		RicercaUtils.addUsedFilterPuntiSpaziato(txtOut, "Lavoratore", row, "STRCOGNOME");
		RicercaUtils.addUsedFilterSpaziato(txtOut, "", row, "STRNOME");
		RicercaUtils.addUsedFilterPuntiSpaziato(txtOut, "codice&nbsp;fiscale", row, "STRCODICEFISCALE");
		%>
	    <tr><td><%= RicercaUtils.getTableWithData(txtOut, "") %></td></tr>
	    <%
	}

	if (StringUtils.isFilled(prgAzienda)) {
		Vector rowsT = serviceResponse.getAttributeAsVector("M_GetTestataAzienda.ROWS.ROW");
		Vector rowsU = serviceResponse.getAttributeAsVector("M_GetUnitaAzienda.ROWS.ROW");

		SourceBean rowT = null;
		if ((rowsT != null) && ! rowsT.isEmpty()) rowT = (SourceBean) rowsT.elementAt(0);

		SourceBean rowU = null;
		if ((rowsU != null) && ! rowsU.isEmpty()) rowU = (SourceBean) rowsU.elementAt(0);

		StringBuffer txtOut = new StringBuffer();
		RicercaUtils.addUsedFilterPuntiSpaziato(txtOut, "Azienda: CF", rowT, "strCodiceFiscale");
		RicercaUtils.addUsedFilterPuntiSpaziato(txtOut, "P.IVA", rowT, "strPartitaIva");
		RicercaUtils.addUsedFilterPuntiSpaziato(txtOut, "Rag.soc.", rowT, "strRagioneSociale");
		RicercaUtils.addUsedFilterPuntiSpaziato(txtOut, "Unit&agrave;: indirizzo", rowU, "strIndirizzo");
		RicercaUtils.addUsedFilterPuntiSpaziato(txtOut, "comune", rowU, "strDenominazione");
		%>
	    <tr><td><%= RicercaUtils.getTableWithData(txtOut, "") %></td></tr>
	    <%
	}
	%>
    <tr><td>&nbsp;</td></tr>
    <tr><td><p class="titolo"><%=titolo%></p></td></tr>
    <tr><td>&nbsp;</td></tr>
<%}%>
    
  <af:list moduleName="M_GetDocAssociati"/>  		

</table>


<%
	String _current_page = (String) serviceRequest.getAttribute("PAGE"); 
	ProfileDataFilter filter = new ProfileDataFilter(user, _current_page);
	boolean canView   = true;
	boolean canInsert = false;

	if (StringUtils.isFilled(cdnLavoratore)) {
		filter.setCdnLavoratore(new BigDecimal(cdnLavoratore));
		canView = canView && filter.canViewLavoratore();
	}

	if (StringUtils.isFilled(prgAzienda)) {
		filter.setPrgAzienda(new BigDecimal(prgAzienda));
		filter.setPrgUnita(new BigDecimal(prgUnita));
		//canView = canView && filter.canView
	}
	
	PageAttribs attributi = new PageAttribs(user, "DocumentiAssociatiPage");
	
	if (! canView){
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
	}
	else {
		boolean existsSalva = attributi.containsButton("nuovo");
		if (! existsSalva) {
			canInsert = false;
		} else {
			canInsert = true;
			if (StringUtils.isFilled(cdnLavoratore)) {
				canInsert = canInsert && filter.canEditLavoratore();
			}
			if (StringUtils.isFilled(prgAzienda)) {
				canInsert = canInsert && filter.canEditUnitaAzienda();
			}
		}
	} //else
%>
<script language="Javascript">
<%
	String myUrl =	"cdnLavoratore=" + cdnLavoratore +
					"&prgAzienda=" + prgAzienda + "&prgUnita=" + prgUnita +
					"&lookLavoratore=" + lookLavoratore +
					"&lookAzienda=" + lookAzienda +
					"&contesto=" + contesto;
					
	//Mauro Riccardi & Alessandro Pegoraro 12/2007 patch link nel footer
	//evita di inserire nello stack di navigazione le pagine mostrate con un pop-up
	if (! popUp.equalsIgnoreCase("true")) { 
		attributi.showHyperLinks(out, requestContainer, responseContainer, myUrl);
	}
%>
</script>

<table class="main" cellpadding="0" cellspacing="0" border="0" width="100%">
	<tr><td width="33%">&nbsp;</td>
		<td width="34%">
			<% if (canInsert && ! infStoriche.equalsIgnoreCase("true") && canViewNuovo ) { %>
				<input type="button" class="pulsante" onClick="nuovo()"
				       value="Nuovo documento" />
			<% } %>
		</td>
		<td width="33%">
			<% if (infStoriche.equalsIgnoreCase("true")) {
					if (StringUtils.isFilled(pagina)) { %>
						<input type="button" class="pulsante" onClick="listaDoc()"
						       value="Torna alla lista dei documenti associati" />
						<%
					}
			   } else {
					// caso infStoriche=false
					%>
					<input class="pulsante<%=((hasInfoStoriche)?"":"Disabled")%>" type="button" value="Informazioni storiche" onClick="infStoriche()" <%=(!hasInfoStoriche)?"disabled=\"True\"":""%>>      
			<% } %>
		</td> 
	</tr>
	<tr><td colspan="2">&nbsp;</td>
		<td>
			<input type="button" class="pulsante" onClick="ricerca()"
			       value="Ricerca documento" />
	    </td>
	</tr>
</table>


       
<% if (popUp.equalsIgnoreCase("true")) { %>
	<table class="main" width="100%">
		<tr><td>
			<input type="button" value="Chiudi" class="pulsanti" onClick="closePopupAndRefresh()" />
		</td></tr>
	</table>
<% } %>

<br/>

</af:form>

</body>
</html>
