<%@ page contentType="text/html;charset=utf-8"%>

<%@ taglib uri="aftags" prefix="af" %>

<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import=" com.engiweb.framework.base.*,
                  com.engiweb.framework.security.*,
                  it.eng.sil.security.*,
                  it.eng.sil.util.*,
                  it.eng.afExt.utils.*,
                  java.util.*,
                  java.math.*" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
	// NOTE: Attributi della pagina (pulsanti e link) 
	//String _page = (String) serviceRequest.getAttribute("PAGE");
	String _page = "DocumentiAssociatiPage";
	PageAttribs attributi = new PageAttribs(user, _page);
	
	int cdnfunzione = SourceBeanUtils.getAttrInt(serviceRequest, "cdnfunzione");
	String  cdnLavoratore  = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "cdnLavoratore");
	String  prgAzienda     = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "prgAzienda");
	String  prgUnita       = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "prgUnita");
	boolean lookLavoratore = SourceBeanUtils.getAttrBoolean(serviceRequest, "lookLavoratore", false);
	boolean lookAzienda    = SourceBeanUtils.getAttrBoolean(serviceRequest, "lookAzienda", false);
	String  contesto       = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "contesto");

	%>
	<%@ include file="ricercaAutoInitLook.inc" %>
	<%

	// ALTRI PARAMETRI SPECIFICI PER LA RICERCA "DOCUMENTI ASSOCIATI":
	String pagina = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "pagina");
	String popUp = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "popUp");
	String infStoriche = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "infStoriche");
	String strChiaveTabella = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "strChiaveTabella");

	// il "goBackListPage" viene passato dalla pagina di LISTA (documenti.jsp) sul
	// bottone "RICERCA". In realtà servirebbe alla pagina del dettaglio per sapere
	// da che lista si è arrivati. Qua mi serve per sapere se posso tornare alla lista (ossia
	// se c'è veramente una lista a cui tornare poiché potrei essere arrivato qua direttamente).
	// nota: e' fissato in origine nell'XML di config del modulo di lista (nella "SELECT_CAPTION").
	String goBackListPage = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "goBackListPage");

	String titolo;
	if (StringUtils.isFilled(infStoriche))
		titolo = "Ricerca informazioni storiche documenti";
	else if (StringUtils.isFilled(pagina))
		titolo = "Ricerca documenti associati alla pagina";
	else
		titolo = "Ricerca documenti";

	Vector tipiDoc = serviceResponse.getAttributeAsVector("COMBOTIPODOCUMENTO.ROWS.ROW");

	String htmlStreamTop    = StyleUtils.roundTopTable(false);
	String htmlStreamBottom = StyleUtils.roundBottomTable(false);
%>

<html>
<head>
<link rel="stylesheet" href="../../css/stili.css" type="text/css">
<title>Ricerca Documenti Associati</title>

<af:linkScript path="../../js/" />
<script language="Javascript" src="../../js/documenti/docPopup.js"></script>
<script language="Javascript" src="../../js/documenti/lookEntityDoc.js"></script>
<script language="Javascript" src="../../js/documenti/protocolloDoc.js"></script>
<script language="Javascript" src="../../js/documenti/ricercaDoc.js"></script>

<script language="Javascript">

	// Qui la "flagChanged" serve perché la usano i moduli di "lookup"
	// lavoratore/azienda (che sono presi dalla dettaglioDocumento).
	var flagChanged = false;

	<%
	//Genera il Javascript che si occuperà di inserire i links nel footer
	if (StringUtils.isFilled(cdnLavoratore)) {
		attributi.showHyperLinks(out, requestContainer, responseContainer,
								 "cdnLavoratore="+cdnLavoratore);
	}
	%>
	
    function caricaDati() {
	    <%
	    for (int j = 0; j < tipiDoc.size(); j++) {
			SourceBean rowTipidoc = (SourceBean)tipiDoc.get(j);

			String codAmbito = (String)rowTipidoc.getAttribute("CODAMBITODOC");
			String codTipiDoc = (String)rowTipidoc.getAttribute("CODICE");
			String dscTipiDoc = (String)rowTipidoc.getAttribute("DESCRIZIONE");
			codAmbito  = StringUtils.formatValue4Javascript(codAmbito);
			codTipiDoc = StringUtils.formatValue4Javascript(codTipiDoc);
			dscTipiDoc = StringUtils.formatValue4Javascript(dscTipiDoc);
			%>
				vAmbiti[<%=j%>] = new Ambito("<%=codAmbito%>","<%=codTipiDoc%>","<%=dscTipiDoc%>");
			<%
		}
		%>
	}

	function dettaglioDocumenti(){
		// Se la pagina è già in submit, ignoro questo nuovo invio!
		if (isInSubmit()) return;

		<%
		// A seconda del contenuto di "goBackListPage" decido dove tornare
		String goBackListUrl = null;
		if (StringUtils.isFilled(goBackListPage)) {

			// Recupero l'eventuale URL generato dalla LISTA precedente
			String token = "_TOKEN_" + goBackListPage;
			goBackListUrl = (String) sessionContainer.getAttribute(token.toUpperCase());
		}

		if (StringUtils.isFilled(goBackListUrl)) {
			%>
			var url = "AdapterHTTP?<%= StringUtils.formatValue4Javascript(goBackListUrl) %>";
			<%
		}
		else {
			%>
			var url = "AdapterHTTP?PAGE=DocumentiAssociatiPage" +
									"&cdnLavoratore=<%=cdnLavoratore%>" +
									"&prgAzienda=<%=prgAzienda%>&prgUnita=<%=prgUnita%>" +
									"&lookLavoratore=<%=lookLavoratore%>" +
									"&lookAzienda=<%=lookAzienda%>" +
									"&contesto=<%=contesto%>" +
									"&pagina=<%=pagina%>" +
									"&strChiaveTabella=<%=strChiaveTabella%>" +
									"&popUp=<%=popUp%>" +
									"&infStoriche=<%=infStoriche%>" +
									"&cdnfunzione=<%=cdnfunzione%>";
			<%
		}
		%>
		setWindowLocation(url);
	}


	function nuovoBot() {
	    <%
	    // e' nome del frame da cui e' partita la richiesta del documento di identita',
	    // dichiarazione di immediata disponibilita' oppure il patto 150. In questo caso il refresh deve essere della popUp
	    // aperta dalla pagina del patto e non del frame "main", come nel caso della did.
	    
	    String frameName = SourceBeanUtils.getAttrStr(serviceRequest, "FRAME_NAME", "main");
		%>
	    // Se la pagina è già in submit, ignoro questo nuovo invio!
	    if (isInSubmit()) return;

		var url = "AdapterHTTP?PAGE=DettagliDocumentoPage&" +
							"NUOVO=true" +
							"&cdnLavoratore=<%=cdnLavoratore%>" +
							"&prgAzienda=<%=prgAzienda%>&prgUnita=<%=prgUnita%>" +
							"&lookLavoratore=<%=lookLavoratore%>" +
							"&lookAzienda=<%=lookAzienda%>" +
							"&contesto=<%=contesto%>" +
							"&pagina=<%=pagina%>" +
							"&strChiaveTabella=<%=strChiaveTabella%>" +
							"&popUp=<%=popUp%>" +
							"&FRAME_NAME=<%=frameName%>" +
							"&cdnfunzione=<%=cdnfunzione%>";
		setWindowLocation(url);
	}


	function onLoad() {
		checkError();
		<% if (! popUp.equalsIgnoreCase("true")) { %>
			rinfresca();
		<% } %>
		caricaDati();
	}

</script>

</head>

<body class="gestione" onload="onLoad()">

<%
	if (! popUp.equalsIgnoreCase("true")) {
	
		// TESTATE!
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

<br />
<p class="titolo"><%=titolo%></p>
<br />
<af:form name="formina" action="AdapterHTTP" method="GET">
<!-- pregansi lasciare il GET senno' non funzia piu' bene il refresh post POP-UP dello storico -->
<input type="hidden" name="PAGE" value="DocumentiAssociatiPage" />
<input type="hidden" name="cdnFunzione" value="<%=cdnfunzione%>" />

<%-- Chiavi per le lookup (vedi file INC) --%>
<input type="hidden" name="cdnLavoratore" value="<%=cdnLavoratore%>" />
<input type="hidden" name="prgAzienda" value="<%=prgAzienda%>" />
<input type="hidden" name="prgUnita" value="<%=prgUnita%>" />
<input type="hidden" name="lookLavoratore" value="<%=lookLavoratore%>" />
<input type="hidden" name="lookAzienda" value="<%=lookAzienda%>" />
<input type="hidden" name="contesto" value="<%=contesto%>" />
	
<input type="hidden" name="pagina" value="<%=pagina%>" />
<input type="hidden" name="strChiaveTabella" value="<%=strChiaveTabella%>" />
<input type="hidden" name="popUp" value="<%=popUp%>" />
<input type="hidden" name="infStoriche" value="<%=infStoriche%>" />

<%= htmlStreamTop %>
<table class="main">

	<% if (lookLavoratore && ! contesto.equalsIgnoreCase("L")) { %>
		<%-- ***************************************************************************** --%>
		<tr class="note">
			<td colspan="2">
				<div class="sezione2">Lavoratore&nbsp;&nbsp;
					<a href="#" onClick="apriSelezionaLavoratore();return false"><img src="../../img/binocolo.gif" alt="Cerca"></a>
				</div>
			</td>
		</tr>
		<%@ include file="lookLavoratore.inc" %>
		<%-- ***************************************************************************** --%>
	<% } %>

	<% if (lookAzienda && ! contesto.equalsIgnoreCase("A")) { %>
		<%-- ***************************************************************************** --%>
		<tr class="note">
			<td colspan="2">
				<div class="sezione2">Azienda&nbsp;&nbsp;
					<a href="#" onClick="apriSelezionaAzienda();return false"><img src="../../img/binocolo.gif" alt="Cerca"></a>
				</div>
			</td>
		</tr>
		<%@ include file="lookAzienda.inc" %>
		<%-- ***************************************************************************** --%>
	<% } %>

	<tr class="note">
		<td colspan="2">
			<div class="sezione2">Protocollo</div>
		</td>
	</tr>
	<%-- ***************************************************************************** --%>
	<%@ include file="ProtocolloDoc_Elemento.inc" %>
	<%-- ***************************************************************************** --%>

	<tr class="note">
		<td colspan="2">
			<div class="sezione2"></div>
		</td>
	</tr>
	
	<%-- ***************************************************************************** --%>
	<%@ include file="ricercaCampiDoc.inc" %>
	<%-- ***************************************************************************** --%>

	<!--************************************************************************ -->
	<tr>
		<td colspan="2">&nbsp;</td>
	</tr>
	<tr>
		<td colspan="2">
			<span class="bottoni">
				<input type="submit" name="cerca" value="Cerca" class="pulsanti"
						onClick="return checkCampiRicerca()" />
				&nbsp;&nbsp;
				<input type="reset" name="reset" value="Annulla" class="pulsanti" />
			</span>
		</td>
	</tr>

	<%
	// gestione attributi	
	PageAttribs attributiDettaglio = new PageAttribs(user, "DocumentiAssociatiPage");
	if (attributiDettaglio.containsButton("nuovo")) {
	%>
	<tr>
		<td colspan="2">
			<span class="bottoni">
				<input type="button" name="nuovo" value="Nuovo documento" class="pulsante"
						onClick="nuovoBot()" />
			</span>
		</td>
	</tr>
	<%
	}
	%>

	<%
	String etichettaBot = null;
	if (StringUtils.isFilled(infStoriche))
		etichettaBot = "Torna alle informazioni storiche";
	else if (StringUtils.isFilled(pagina))
		etichettaBot = "Torna alla lista dei documenti associati";
	else if (StringUtils.isFilled(goBackListPage))
		etichettaBot = "Torna alla lista";
		
	if (StringUtils.isFilled(etichettaBot)) {
		%>
		<tr>
			<td colspan="2">
				<span class="bottoni">
					<input type="button" name="cameBack" class="pulsanti" value="<%= etichettaBot %>"
							onClick="dettaglioDocumenti()" />
				</span>
			</td>
		</tr>
		<%
	}
	%>

	<% if (popUp.equalsIgnoreCase("true")) { %>
		<tr>
			<td colspan="2">
				<span class="bottoni">
					<input type="button" class="pulsanti" value="Chiudi"
					       onClick="closePopupAndRefresh()" />
				</span>
			</td>
		</tr>
	<% } %>


</table>
<%= htmlStreamBottom %>

</af:form>

</body>
</html>
