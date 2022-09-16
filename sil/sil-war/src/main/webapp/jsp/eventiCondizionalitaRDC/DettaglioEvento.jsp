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
 	String  titolo = "Dettaglio evento condizionalità";
	String  _page  = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"PAGE"); 
	boolean canModify = false;
	boolean canInviato = false;
	
	boolean afterInsert = false;
	 
	boolean isKo = false;
	if (serviceResponse.containsAttribute("M_EventiCondizionalita.ESITO_KO") ) { 
		isKo = true;
	}
	
	boolean isPopup = false;
	if (serviceRequest.containsAttribute("IS_POPUP")) { 
		isPopup = true;
	}
	boolean isFromSearchOperatore = false;
	if (serviceRequest.containsAttribute("IS_SEARCH")) { 
		isFromSearchOperatore = true;
	}
	// Lettura parametri dalla REQUEST
	String  cdnfunzioneStr   = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "cdnfunzione");
	String  cdnLavoratore    = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"cdnLavoratore");
	
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
	
	boolean canAggiorna = attributi.containsButton("AGGIORNA");
	boolean canInviaAnpal = attributi.containsButton("EVENT_COND_WS");
	boolean canDeleteEvento = attributi.containsButton("DEL_CONDIZ");
	 
 
	Object cdnUtCorrente    = sessionContainer.getAttribute("_CDUT_");
	String cdnUtCorrenteStr = StringUtils.getStringValueNotNull(cdnUtCorrente);
	
	String codiceCpiMin = null;
	if(serviceResponse.containsAttribute("M_GET_CPI_MINISTERIALE_UTLOG.ROWS.ROW.CODCPIMIN")){
		codiceCpiMin = serviceResponse.getAttribute("M_GET_CPI_MINISTERIALE_UTLOG.ROWS.ROW.CODCPIMIN").toString();
	}else{
		codiceCpiMin = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "codiceCpiMin");
	}
	
	String prgCondizionalita = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"PRGCONDIZIONALITA");
	if(StringUtils.isEmptyNoBlank(prgCondizionalita) && serviceResponse.containsAttribute("M_INSERISCICONDIZIONALITA.PRGCONDIZIONALITA")){
		prgCondizionalita = serviceResponse.getAttribute("M_INSERISCICONDIZIONALITA.PRGCONDIZIONALITA").toString();
	}else if(StringUtils.isEmptyNoBlank(prgCondizionalita) && serviceResponse.containsAttribute("M_EVENTICONDIZIONALITA.PRGCONDIZIONALITA")){
		prgCondizionalita = serviceResponse.getAttribute("M_EVENTICONDIZIONALITA.PRGCONDIZIONALITA").toString();
	}else if(StringUtils.isEmptyNoBlank(prgCondizionalita) && serviceResponse.containsAttribute("M_AGGIORNACONDIZIONALITA.PRGCONDIZIONALITA")){
		prgCondizionalita = serviceResponse.getAttribute("M_AGGIORNACONDIZIONALITA.PRGCONDIZIONALITA").toString();
	}
		
	String numKLoCondizionalita = null;
	String descrCpi = null;
	String codiceCpi = null;
	String tipoEvento =null;
	String cfBeneficiario = null;
	String cfOperatoreCpi = null;
	String tipoDomanda =null;
	String codTipoDomanda = null;
	String protocolloInps =null;
	String dataDomanda =null;
	String dataEvento =null;
	String note =null;
	String programma =null;
	String dataInizioProg =null;
	String dataFineProg =null;
	String dataAvvio = null;
	String attivita = null;
	String dataConclusione = null;
	String esito = null;
	String dataInvio = null;
	String utenteInserimento = null;
	String utenteModifica = null;
	
	String prgPercorso = null;
	String prgColloquio = null;
	
	String strDifferenzaGiorni = null;
	int differenzaGiorni = 11;
	String strDataCancellazioneEvento = null;
	
	boolean showDettaglio = true;
	
	SourceBean dettaglioEvento = (SourceBean) serviceResponse.getAttribute("M_DettaglioEventoCondizionalita.ROWS.ROW");
	if(dettaglioEvento != null){
 		descrCpi = StringUtils.getAttributeStrNotNull(dettaglioEvento, "STRCPI"); 
		tipoEvento = StringUtils.getAttributeStrNotNull(dettaglioEvento, "TIPOEVENTO"); 
		cfBeneficiario = StringUtils.getAttributeStrNotNull(dettaglioEvento, "STRCODICEFISCALE"); 
		cfOperatoreCpi = StringUtils.getAttributeStrNotNull(dettaglioEvento, "STRCFOPERATORE"); 
		tipoDomanda = StringUtils.getAttributeStrNotNull(dettaglioEvento, "TIPODOMANDA"); 
		codTipoDomanda = StringUtils.getAttributeStrNotNull(dettaglioEvento, "CODTIPODOMANDA"); 
		protocolloInps = StringUtils.getAttributeStrNotNull(dettaglioEvento, "STRPROTOCOLLOINPS"); 
		dataDomanda = StringUtils.getAttributeStrNotNull(dettaglioEvento, "STRDATADOMANDA"); 
		dataEvento = StringUtils.getAttributeStrNotNull(dettaglioEvento, "STRDATEVENTO"); 
		note = StringUtils.getAttributeStrNotNull(dettaglioEvento, "STRNOTE"); 
		programma = StringUtils.getAttributeStrNotNull(dettaglioEvento, "TIPOPROGRAMMA"); 
		dataInizioProg = StringUtils.getAttributeStrNotNull(dettaglioEvento, "STRDATINIZIOPROGRAMMA"); 
		dataFineProg = StringUtils.getAttributeStrNotNull(dettaglioEvento, "STRDATFINEPROGRAMMA"); 
		dataInvio = StringUtils.getAttributeStrNotNull(dettaglioEvento, "STRDATINVIO"); 
		dataAvvio =  StringUtils.getAttributeStrNotNull(dettaglioEvento, "STRDATAVVIO"); ;
		attivita =  StringUtils.getAttributeStrNotNull(dettaglioEvento, "STRATTIVITA"); ;
		dataConclusione =  StringUtils.getAttributeStrNotNull(dettaglioEvento, "STRDATEFFETTIVA"); ;
		esito =  StringUtils.getAttributeStrNotNull(dettaglioEvento, "STRESITO"); 
		if(StringUtils.isFilledNoBlank(esito)){
			esito = esito.substring(0, 1).toUpperCase() + esito.substring(1).toLowerCase();
		}
		if(StringUtils.isEmptyNoBlank(dataInvio)){ //Evento non trasmesso
			canModify = true;
		}
		else {
			canInviato = true;
			strDifferenzaGiorni = dettaglioEvento.getAttribute("differenzaGiorni").toString();
			differenzaGiorni = new Integer(strDifferenzaGiorni).intValue();
		}
		strDataCancellazioneEvento = StringUtils.getAttributeStrNotNull(dettaglioEvento, "strdatinviocanc"); 
		numKLoCondizionalita = dettaglioEvento.getAttribute("NUMKLOCOND").toString();
		prgColloquio = dettaglioEvento.getAttribute("PRGCOLLOQUIO").toString(); 
		prgPercorso = dettaglioEvento.getAttribute("PRGPERCORSO").toString(); 
				
		utenteInserimento = SourceBeanUtils.getAttrStrNotNull(dettaglioEvento, "utenteInserimento");
		utenteModifica = SourceBeanUtils.getAttrStrNotNull(dettaglioEvento, "utenteModifica");
	}else{
		showDettaglio = false;
	}
	
	canModify = canModify && canAggiorna;
	
	String readonly = String.valueOf( ! canModify );
	
	// Stringhe con HTML per layout tabelle
  	
  	String htmlStreamTop = StyleUtils.roundTopTable(canModify);
  	String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);
  	
	String paginaIndietro = "ListCondizionalitaPage";
	if(isPopup){
		paginaIndietro = "ListaAzioniCondizionalitaPage";
	}
	
%>

<html>
<head>
<title><%= titolo %></title>
<link rel="stylesheet" type="text/css" href="../../css/stili.css" />
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
<link rel="stylesheet" type="text/css" href="../../css/stiliTemplate.css"/>

<af:linkScript path="../../js/"/>
 
<%-- INCLUDERE QUI ALTRI JAVASRIPT CON CLAUSOLE DEL TIPO:
<script language="Javascript" src="../../js/xxx.js"></script>
--%>
 
	<af:linkScript path="../../js/" />
	<script type="text/javascript"  src="../../js/script_comuni.js"></script>
	<script type="text/Javascript">
	
	/* Funzione chiamata al caricamento della pagina */
	function onLoad() {
		rinfresca();
		//rinfrescaLaterale();
		// altri funzioni da richiamare sulla onLoad...
	}
	
	//funzione che controlla se un campo è stato modificato o meno
	var flagChanged = false;
	function fieldChanged() {
		 
		 <% if (!readonly.equalsIgnoreCase("true")){ %> 
		  
		    flagChanged = true;
		 <%}%> 
		}
	
	/* Funzione per tornare alla pagina precedente */
	function tornaLista() {
		if (isInSubmit()) return;
		if (flagChanged==true) {
			if (! confirm("I dati sono cambiati.\r\nProcedere lo stesso ?")) {
				undoSubmit();
				return;
			}
		}
		<%
		// Recupero l'eventuale URL generato dalla LISTA precedente
		String token = "_TOKEN_" + paginaIndietro;
		String goBackListUrl = (String) sessionContainer.getAttribute(token.toUpperCase());	
		%>
		setWindowLocation("AdapterHTTP?<%= StringUtils.formatValue4Javascript(goBackListUrl) %>");
	  }
	
	function settaNomeTabella(){
		var codificaDomanda = document.Frm1.tipoDomandaAll.value;
		document.Frm1.tipoDomanda.value = codificaDomanda.split("$")[0]
		document.Frm1.domandaTable.value = codificaDomanda.split("$")[1];
		return true;
	}
	
	function cercaDomande() {
		
	  	// Se la pagina è già in submit, ignoro questo nuovo invio!
	  	if (isInSubmit()) return;
	  	
	  	if(document.Frm1.tipoDomandaAll.value == ''){
	  		alert("Selezionare tipo domanda");
	  		document.Frm1.tipoDomandaAll.focus();
			return;
	  	}
	  	
	  	if(document.Frm1.domandaTable.value.trim()== ""){
	  		alert("Per la modalità selezionata non è a disposizione alcuna ricerca. Compilare i campi protocollo INPS/codice domanda e data domanda manualmente.");
	  	}	else{
	  		var urlpage="AdapterHTTP?";
		    urlpage+="CDNFUNZIONE=<%=cdnfunzioneStr%>&";
		    urlpage+="CDNLAVORATORE=<%=cdnLavoratore%>&";
		    urlpage+="PAGE=RicercaDomandePage&";
		    urlpage+="CFLAV=<%=cfBeneficiario%>&";
		    urlpage+="TIPODOMANDA="+document.Frm1.tipoDomanda.value;
		  	urlpage+="&NOMETABELLA="+document.Frm1.domandaTable.value;
		  	 
			window.open(urlpage,"","toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,height=450,width=650" );		
	  	}
	  

	}
	
	function cercaProgrammi() {
		
	  	// Se la pagina è già in submit, ignoro questo nuovo invio!
	  	if (isInSubmit()) return;
 
	  	var urlpage="AdapterHTTP?";
	    urlpage+="CDNFUNZIONE=<%=cdnfunzioneStr%>&";
	    urlpage+="CDNLAVORATORE=<%=cdnLavoratore%>&";
	    urlpage+="PAGE=RicercaAzCondPage";
	   
	  	 
		window.open(urlpage,"","toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,height=430,width=700" );

	}
	
	function checkCampiAggiuntiva() { 
		
		if(document.Frm1.cfOperatoreCpi.value.trim()== "")
		{
			alert("Il campo "+ document.Frm1.cfOperatoreCpi.title +" è obbligatorio");
			document.Frm1.cfOperatoreCpi.focus();
			return false;
		}else if(document.Frm1.cfOperatoreCpi.value.trim().length != 16 && document.Frm1.cfOperatoreCpi.value.trim().length != 11) {
			alert("Il campo "+ document.Frm1.cfOperatoreCpi.title +" deve essere di 11 oppure di 16 caratteri");
			document.Frm1.cfOperatoreCpi.focus();
			return false;
		}
		if(document.Frm1.prgColloquio.value.trim()== "" && document.Frm1.prgPercorso.value.trim()== ""){
			alert("E' necessario selezionare un'Attività concordata.");
			document.Frm1.cfOperatoreCpi.focus();
			return false;
		}
		
	
	
		return true;
	}
	
	function confermaInvioCond(){
		if ( flagChanged==true){
         	 alert("I dati sono stati modificati. Per poter effettuare l'invio è necessario prima salvare le modifiche");
        	 return false;
        }
		if (!confirm ("ATTENZIONE. Una volta effettuato l'invio non sarà possibile effettuare modifiche o annullamenti dell'evento trasmesso. Continuare con l'invio?")) {
			return false;
		}
		return true;
	}
	
	
	function invioCondizionalitaAnpal(){
		
		if (document.Frm1.invioEventoCondizionalita != null) {
			document.Frm1.invioEventoCondizionalita.disabled = true;
			document.Frm1.invioEventoCondizionalita.setAttribute("class", "pulsantiDisabled");
			if(confermaInvioCond()){
				document.Frm2.submit();
			}
		}else{
			return false;
		}	
 
	}
	
	function trasmettiCancellazione(){
		
		if (document.Frm1.deleteEventoCondizionalita != null) {
			document.Frm1.deleteEventoCondizionalita.disabled = true;
			document.Frm1.deleteEventoCondizionalita.setAttribute("class", "pulsantiDisabled");
			document.Frm3.submit();
		}else{
			return false;
		}	
 
	}

 
<%
	// Genera il Javascript che si occuperà di inserire i links nel footer
	if (StringUtils.isFilledNoBlank(cdnLavoratore)){
		attributi.showHyperLinks(out, requestContainer, responseContainer, "cdnLavoratore="+cdnLavoratore);
	}
		
%>

</script>
 
<style type="text/css">
td.etichetta{
vertical-align: top;
}
input.inputView{
 vertical-align: top;
}
td.campo {
 vertical-align: top;
}
</style>
	
</head>

<body class="gestione" onload="onLoad()">

<%
	if(!isPopup && !afterInsert &&StringUtils.isFilledNoBlank(cdnLavoratore)){
		InfCorrentiLav testata = new InfCorrentiLav(RequestContainer.getRequestContainer().getSessionContainer(), cdnLavoratore, user);
		if(isFromSearchOperatore)
	       testata.setPaginaLista("ListaRicercaCondizionalitaPage");
		testata.show(out);
		Linguette l  = new Linguette(user, Integer.parseInt(cdnfunzioneStr), paginaIndietro, new BigDecimal(cdnLavoratore));
		l.show(out);
	}
%>
<%if(!isPopup && !afterInsert && StringUtils.isFilledNoBlank(cdnLavoratore)){ %>
<script language="Javascript">
			window.top.menu.caricaMenuLav(<%=cdnfunzioneStr%>,<%=cdnLavoratore%>);
		</script>
<%}%>
<br/>
<p class="titolo"><%= titolo %></p>

	<p>
	 	<font color="green">
	 		<af:showMessages prefix="M_InserisciCondizionalita"/>
	 		<af:showMessages prefix="M_AggiornaCondizionalita"/>
	 		<af:showMessages prefix="M_EventiCondizionalita"/>
	 		<af:showMessages prefix="M_EliminaEventoCondizionalita"/>
 	  	</font>
	  	<font color="red">
	  		<af:showErrors />
	  	</font>
	</p>
<af:form name="Frm1" action="AdapterHTTP" method="POST" onSubmit="checkCampiAggiuntiva()">
	<input type="hidden" name="PAGE" value="DettaglioCondizionalitaPage"/>
 	<input type="hidden"  name="cdnlavoratore" value="<%= cdnLavoratore %>" />
	<input type="hidden" name="cdnfunzione" value="<%= cdnfunzioneStr %>" />
	<input type="hidden" name="operazioneModificaEvento" value="operazioneModificaEvento">
    <input type="hidden" name="codiceCpiMin" value="<%= Utils.notNull(codiceCpiMin)  %>" />
   	<input type="hidden" name="PRGCONDIZIONALITA" value="<%=prgCondizionalita%>">
    <input type="hidden" name="NUMKLO" value="<%=numKLoCondizionalita %>">
    <%if(isPopup){ %>
 		<input type="hidden" name="IS_POPUP" value="IS_POPUP">
	<%}%>
	<%out.print(htmlStreamTop);%>
	
	<%if(showDettaglio){ %>
 	<%@ include file="campiCondizionalita.inc" %>
	<%}%>
	<center>
  	<table>
  		<tbody>
  	<%if(canModify){ %>
  			<tr>
				<td colspan="2" align="center">
					<input type="submit" class="pulsanti" value="Aggiorna" name="Aggiorna"/>
				</td>
			</tr>
	<%}%>
	<%if(canInviaAnpal && !canInviato){ %>
  			<tr>
				<td colspan="2" align="center">
					<input class="pulsanti" type="button" name="invioEventoCondizionalita" value="Invio ad ANPAL"  
					 onclick="invioCondizionalitaAnpal();"/>
				</td>
			</tr>
  	<%}%>
  	
 	<%if(canDeleteEvento && differenzaGiorni <= 10 && StringUtils.isEmptyNoBlank(strDataCancellazioneEvento)){ %>
  			<tr>
				<td colspan="2" align="center">
					<input class="pulsanti" type="button" name="deleteEventoCondizionalita" value="Trasmetti cancellazione evento cond"  
					 onclick="trasmettiCancellazione();"/>
				</td>
			</tr>
  	<%}%>
	<%if( !afterInsert && !isFromSearchOperatore){ %>
	 		<tr>
				<td colspan="2" align="center">
					<input class="pulsante" type = "button" name="torna" value="Torna alla lista" onClick="tornaLista();"/>
				</td>
			</tr>
	<%} %>
	<%if(!isFromSearchOperatore && ( afterInsert || isPopup)){ %>
	 		<tr>
				<td colspan="2" align="center">
					<input class="pulsante" type = "button" name="chiudi" value="Chiudi" onClick="window.close();"/>
				</td>
			</tr>
	<%} %>
			</tbody>
	</table>
</center>
	 
	 <%out.print(htmlStreamBottom);%>
	 </af:form>
<af:form method="POST" action="AdapterHTTP" name="Frm2">
	<input type="hidden" name="PAGE" value="DettaglioCondizionalitaPage">
	<input type="hidden" name="cdnlavoratore" value="<%= cdnLavoratore %>" />
	<input type="hidden" name="cdnfunzione" value="<%= cdnfunzioneStr %>" />
	<input type="hidden" name="PRGCONDIZIONALITA" value="<%=prgCondizionalita%>">
	<input type="hidden" name="operazioneInviaEvento" value="operazioneInviaEvento"/>
	<input type="hidden" name="codiceCpiMin" value="<%= Utils.notNull(codiceCpiMin)  %>" />
	<input type="hidden" name="cfBeneficiario" value="<%= Utils.notNull(cfBeneficiario) %>" />
	<input type="hidden" name="tipoEvento" value="<%= Utils.notNull(tipoEvento) %>" />
	<input type="hidden" name="cfOperatoreCpi" value="<%= Utils.notNull(cfOperatoreCpi) %>" />
	<input type="hidden" name="protInps" value="<%= Utils.notNull(protocolloInps) %>" />
	<input type="hidden" name="dataDomanda" value="<%= Utils.notNull(dataDomanda) %>" />
	<input type="hidden" name="dataEvento" value="<%= Utils.notNull(dataEvento) %>" />
    <input type="hidden" name="NUMKLO" value="<%=numKLoCondizionalita %>">
    <%if(isPopup){ %>
 		<input type="hidden" name="IS_POPUP" value="IS_POPUP">
	<%}%>
	 <%if(isFromSearchOperatore){ %>
 		<input type="hidden" name="IS_SEARCH" value="IS_SEARCH">
	<%}%>
</af:form>
<af:form method="POST" action="AdapterHTTP" name="Frm3">
	<input type="hidden" name="PAGE" value="DettaglioCondizionalitaPage">
	<input type="hidden" name="cdnlavoratore" value="<%= cdnLavoratore %>" />
	<input type="hidden" name="cdnfunzione" value="<%= cdnfunzioneStr %>" />
	<input type="hidden" name="PRGCONDIZIONALITA" value="<%=prgCondizionalita%>">
	<input type="hidden" name="operazioneDeleteEvento" value="operazioneDeleteEvento"/>
	<input type="hidden" name="codiceCpiMin" value="<%= Utils.notNull(codiceCpiMin)  %>" />
	<input type="hidden" name="cfBeneficiario" value="<%= Utils.notNull(cfBeneficiario) %>" />
	<input type="hidden" name="tipoEvento" value="<%= Utils.notNull(tipoEvento) %>" />
	<input type="hidden" name="cfOperatoreCpi" value="<%= Utils.notNull(cfOperatoreCpi) %>" />
	<input type="hidden" name="protInps" value="<%= Utils.notNull(protocolloInps) %>" />
	<input type="hidden" name="dataDomanda" value="<%= Utils.notNull(dataDomanda) %>" />
	<input type="hidden" name="dataEvento" value="<%= Utils.notNull(dataEvento) %>" />
    <input type="hidden" name="NUMKLO" value="<%=numKLoCondizionalita %>">
    <%if(isPopup){ %>
 		<input type="hidden" name="IS_POPUP" value="IS_POPUP">
	<%}%>
</af:form>
<%if(!isKo){ %>
<center>
  	<table>
  		<tbody>
  			<tr>
	  			<td align="center">
					<table class="info_mod" align="center">
						<tr>
							<td class="info_mod">Inserimento</td>
							<td class="info_mod"><b><%= utenteInserimento %></b></td>
							<%if(StringUtils.isFilledNoBlank(utenteModifica)){ %>
							<td class="info_mod">Ultima Modifica</td>
							<td class="info_mod"><b><%= utenteModifica %></b></td>
							<%}%>
						</tr>
					</table>
				</td>
			</tr>
		</tbody>
	</table>
</center>
<%}%>
 
</body>
</html>
