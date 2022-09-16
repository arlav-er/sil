<%@ page
	contentType="text/html;charset=utf-8"
	
	import="com.engiweb.framework.base.*,
			it.eng.sil.security.*,
			it.eng.afExt.utils.*,
			it.eng.sil.util.*,
			java.math.*,
			java.util.*"
	
	extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage"
%>

<%@ taglib uri="aftags" prefix="af" %>

<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%
	String  titolo = "xxx";
	String  _page  = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"PAGE"); 
	// Nomi dei moduli di lista e di dettaglio
	String  _listModuleName   = "TemplateListModule";
	String  _dettModuleName   = "TemplateSelectModule";
	String  _insertModuleName = "TemplateInsertModule";
	String  _updateModuleName = "TemplateUpdateModule";
	String  _deleteModuleName = "TemplateDeleteModule";
	String  _prgKeyName       = "PRGCREDITO";

	// Lettura parametri dalla REQUEST
	int     cdnfunzione      = SourceBeanUtils.getAttrInt(serviceRequest, "cdnfunzione");
	String  cdnfunzioneStr   = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "cdnfunzione");
	String  cdnLavoratore    = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"cdnLavoratore");
	String  prgAzienda       = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"prgAzienda");
	String  prgUnita         = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"prgUnita");


	// CONTROLLO ACCESSO ALLA PAGINA
	ProfileDataFilter filter = new ProfileDataFilter(user, _page);
	filter.setCdnLavoratore(new BigDecimal(cdnLavoratore));
	boolean canView = filter.canViewLavoratore();
	if (! canView){
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
		return;
	}

	// CONTROLLO PERMESSI SULLA PAGINA
	PageAttribs attributi = new PageAttribs(user, _page);

	boolean canModify = attributi.containsButton("aggiorna");
	boolean canDelete = attributi.containsButton("rimuovi");

	Object cdnUtCorrente    = sessionContainer.getAttribute("_CDUT_");
	String cdnUtCorrenteStr = StringUtils.getStringValueNotNull(cdnUtCorrente);



	// Recupero la ROW (eventuale) contenuta nella RISPOSTA DEL MODULO DI DETTAGLIO
	SourceBean row = (SourceBean) serviceResponse.getAttribute(_dettModuleName+".ROWS.ROW");
	
	// Leggo i singoli dati della risposta (dalla ROW o da altro SourceBean)
	// (se non presenti, li inizializzo a un valore di default - terzo parametro!)
	BigDecimal prgKey = SourceBeanUtils.getAttrBigDecimal(row, _prgKeyName, null);

	String  strSpecifica = SourceBeanUtils.getAttrStrNotNull(row, "strSpecifica");
	String  strXxx = SourceBeanUtils.getAttrStrNotNull(row, "strXxx");
	String  strYyy = SourceBeanUtils.getAttrStr(row, "strXxx", "default");
	int     cdnXxx = SourceBeanUtils.getAttrInt(row, "cdnXxx", -1);
	boolean flgXxx = SourceBeanUtils.getAttrBoolean(row, "flgXxx", false);

	// Recupero (da ROW) il numKlo (per controllo concorrenza)
	String numKlo = SourceBeanUtils.getAttrStrNotNull(row, "numKlo");
	
	// Recupero (da ROW) l'operatore di creazione e ultima modifica
	String cdnUtins = SourceBeanUtils.getAttrStrNotNull(row, "cdnUtins");
	String dtmins   = SourceBeanUtils.getAttrStrNotNull(row, "dtmins");
	String cdnUtmod = SourceBeanUtils.getAttrStrNotNull(row, "cdnUtmod");
	String dtmmod   = SourceBeanUtils.getAttrStrNotNull(row, "dtmmod");
	Testata operatoreInfo = new Testata(cdnUtins, dtmins, cdnUtmod, dtmmod);


	// Apertura popup?
	String apriDiv = (String) serviceRequest.getAttribute("APRIDIV");
	apriDiv = (apriDiv == null)? "none" : "";

	// Sono in nuovo?
	boolean nuovo = (row == null);
	// era: nuovo = ! serviceResponse.containsAttribute(_dettModuleName);

	// URL da usare per aprire la finestra di "nuovo elemento"
	String urlNuovo = "AdapterHTTP?PAGE=" + _page +
                     "&CDNLAVORATORE=" + cdnLavoratore +
                     "&CDNFUNZIONE=" + cdnfunzioneStr +
                     "&APRIDIV=1";

	// Sola lettura: viene usato per tutti i campi di input
	String readonly = String.valueOf( ! canModify );
%>

<html>
<head>
<title><%= titolo %></title>
<link rel="stylesheet" type="text/css" href="../../css/stili.css" />
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>

<af:linkScript path="../../js/"/>
<%@ include file="../global/fieldChanged.inc" %>
<script language="Javascript" src="../../js/layers.js"></script>

<%-- INCLUDERE QUI ALTRI JAVASRIPT CON CLAUSOLE DEL TIPO:
<script language="Javascript" src="../../js/xxx.js"></script>
--%>

<script language="Javascript">

	// ...


	function dettaglioElemento(prgElem) {
		// Se la pagina è già in submit, ignoro questo nuovo invio!
		if (isInSubmit()) return;

		var url = "AdapterHTTP?PAGE=<%= _page %>" +
							"&MODULE=<%= _dettModuleName %>" +
							"&<%= _prgKeyName %>=" + prgElem +
							"&CDNLAVORATORE=<%= cdnLavoratore %>" +
							"&CDNFUNZIONE=<%= cdnfunzioneStr %>" +
							"&APRIDIV=1";
		setWindowLocation(url);
	}


	function cancellaElemento(prgElem, dettaglio) {
		// Se la pagina è già in submit, ignoro questo nuovo invio!
		if (isInSubmit()) return;

		var msg = "Eliminare il ... ";
		if ((dettaglio != null) && (dettaglio.length > 0)) {
			msg += dettaglio.replace(/\^/g, '\'');
		}
		msg += " ?";
		
		if (confirm(msg)) {

			var url = "AdapterHTTP?PAGE=<%= _page %>" +
								"&MODULE=<%= _deleteModuleName %>" +
								"&<%= _prgKeyName %>=" + prgElem +
								"&CDNLAVORATORE=<%= cdnLavoratore %>" +
								"&CDNFUNZIONE=<%= cdnfunzioneStr %>";
			setWindowLocation(url);
		}
	}

	function Insert() {
		if (controllaFunzTL())  {
			document.MainForm.MODULE.value = "<%= _insertModuleName %>";
			doFormSubmit(document.MainForm);
		}
	}
    
	function Update() {
		if (controllaFunzTL()) {
			document.MainForm.MODULE.value = "<%= _updateModuleName %>";
			doFormSubmit(document.MainForm);
		}
	}


	function chiudiLayer(scriptVar) {
	
		ok = true;
		if (flagChanged) {
			ok = confirm("I dati sono cambiati.\nProcedere lo stesso?");

			// Vogliamo chiudere il layer.
			// Pongo il flag=false per evitare che mi venga riproposto un "confirm"
			// quando poi navigo con le linguette nella pagina principale
			flagChanged = false;
		}
		if (ok) {
			eval(scriptVar);
			//ChiudiDivLayer('divLayerDett');
		}
	}

	function openPage(pagina, parametri) {
		// Se la pagina è già in submit, ignoro questo nuovo invio!
		if (isInSubmit()) return;
		
		setWindowLocation("AdapterHTTP?PAGE=" + pagina + parametri);
	}
	

	/* Funzione chiamata al caricamento della pagina */
	function onLoad() {
		rinfresca();
		// altri funzioni da richiamare sulla onLoad...
	}


<%
	// Genera il Javascript che si occuperà di inserire i links nel footer
    attributi.showHyperLinks(out, requestContainer, responseContainer, "cdnLavoratore=" + cdnLavoratore);
%>

	// Aggiorna il menu via JavaScript
	window.top.menu.caricaMenuLav(<%=cdnfunzioneStr%>,<%=cdnLavoratore%>);

</script>
</head>

<body class="gestione" onload="onLoad()">

<%
	// TESTATA LAVORATORE
	if (StringUtils.isFilled(cdnLavoratore)) {
		InfCorrentiLav testata = new InfCorrentiLav(cdnLavoratore, user);
		testata.show(out);
	}
	
	// LINGUETTE LAVORATORE
	if (StringUtils.isFilled(cdnLavoratore)) {
		Linguette linguette = new Linguette(user, cdnfunzione, _page, new BigDecimal(cdnLavoratore));
		linguette.show(out);
	}
%>

<af:showErrors />
<af:showMessages prefix="<%= _insertModuleName %>" />
<af:showMessages prefix="<%= _updateModuleName %>" />
<af:showMessages prefix="<%= _deleteModuleName %>" />


<af:form name="MainForm" action="AdapterHTTP" method="POST">

	<af:textBox type="hidden" name="PAGE" value="<%= _page %>" />
	<af:textBox type="hidden" name="cdnfunzione" value="<%= cdnfunzioneStr %>" />
	<af:textBox type="hidden" name="numKlo" value="<%= numKlo %>" />
	<af:textBox type="hidden" name="CDNUTINS" value="<%= cdnUtCorrenteStr %>" />
	<af:textBox type="hidden" name="CDNUTMOD" value="<%= cdnUtCorrenteStr %>" />

	<af:textBox type="hidden" name="CDNLAVORATORE" value="<%= cdnLavoratore %>" />
	<af:textBox type="hidden" name="MODULE" value="(viaJavascript)" />

	<% if (! nuovo)  { %>
	<af:textBox type="hidden" name="<%= _prgKeyName %>" value="<%= String.valueOf(prgKey) %>" />
	<% } %>

	<af:list moduleName="<%= _listModuleName %>" skipNavigationButton="1"
			canInsert="<%= String.valueOf(canModify ? 1 : 0) %>" 
			canDelete="<%= String.valueOf(canDelete ? 1 : 0) %>" 
			jsSelect="dettaglioElemento" jsDelete="cancellaElemento" />

	<% if (canModify) { %>
		<p align="center">
			<input type="button" class="pulsanti"
					value="Nuovo ..."
					onClick="apriNuovoDivLayer(<%=nuovo%>,'divLayerDett','<%= urlNuovo %>');document.location='#aLayerDett'"
					/>
		</p>
	<% } %>
	


	<%-- ================================== LAYER ================================== --%>
	<%
	String divStreamTop    = StyleUtils.roundLayerTop(canModify);
	String divStreamBottom = StyleUtils.roundLayerBottom(canModify);
	%>
	
	<div id="divLayerDett" name="divLayerDett" class="t_layerDett"
		style="position:absolute; width:80%; left:50; top:100px; z-index:6; display:<%=apriDiv%>;">
		<a name="aLayerDett"></a>
		<%= divStreamTop %>
		<table width="100%">
			<tr>
				<td width="16" height="16" class="azzurro_bianco">
					<img src="../../img/move_layer.gif" onClick="return false"
						onMouseDown="engager(event,'divLayerDett');return false"></td>
				<td height="16" class="azzurro_bianco">
					<% if (nuovo) { %>
						Nuovo ...
					<%} else {%>
						...
					<%}%>
				</td>
				<td width="16" height="16" class="azzurro_bianco"
					onClick="ChiudiDivLayer('divLayerDett')">
						<img src="../../img/chiudi_layer.gif" alt="Chiudi"></td>
			</tr>
		</table>
		<br/>
		<table class="main" width="100%">
			<tr>
				<td colspan="2" align="center">&nbsp;</td>
			</tr>

			<%@ include file="listaConPopupDettCampi.inc" %>

			<tr>
				<td colspan="2" align="center">&nbsp;</td>
			</tr>
			<tr>
				<% if (nuovo) { %>
				<td colspan="2" align="center">
					<input type="button" class="pulsanti" name="inserisci" value="Inserisci"
							onClick="Insert();">
					<input type="button" class="pulsanti" name="chiudi" value="Chiudi"
							onClick="chiudiLayer('ChiudiDivLayer(\'divLayerDett\')');">
				</td>
				<% } else { %>
					<% if (canModify) { %>
						<td width="40%" align="right">
							<input type="button" class="pulsanti" name="salva" value="Aggiorna" onClick="Update();">
						</td>
					<% } %>
				<% } %>
				
				<td width="60%" align="left">
					<% if (canModify && ! nuovo) { %>
							<input type="button" class="pulsante" class="pulsanti" name="annulla"
									value="Chiudi senza aggiornare"
									onclick="chiudiLayer('openPage(\'<%= _page %>\',\'&cdnLavoratore=<%=cdnLavoratore%>&cdnfunzione=<%=cdnfunzioneStr%>\')');" />
					<% } else { 
						if (! canModify) { %>
							<input type="button" class="pulsanti" name="chiudi" value="Chiudi"
									onClick="chiudiLayer('ChiudiDivLayer(\'divLayerDett\')');" />
					<%	}
					   } %>
				</td>
			</tr>
		</table>
		<% if (! nuovo) { %>
			<table align="center">
				<tr>
					<td align="center">
						<% operatoreInfo.showHTML(out); %>
					</td>
				</tr>
			</table>
		<% } %>
		<%= divStreamBottom %>
	</div>

</af:form>

</body>
</html>
