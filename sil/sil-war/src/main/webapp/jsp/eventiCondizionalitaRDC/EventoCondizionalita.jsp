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
 	String  titolo = "Nuovo evento condizionalità";
	String  _page  = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"PAGE"); 
	boolean canModify = true;

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
	
	PageAttribs attribInserisci = new PageAttribs(user, "ListCondizionalitaPage");
	boolean canAdd = attribInserisci.containsButton("EVENT_COND");
 
	Object cdnUtCorrente    = sessionContainer.getAttribute("_CDUT_");
	String cdnUtCorrenteStr = StringUtils.getStringValueNotNull(cdnUtCorrente);
	
	/*String descrCpi = serviceResponse.getAttribute("M_GetInfoLavCond.ROWS.ROW.cpiTitolare").toString();
	String codiceCpi = serviceResponse.getAttribute("M_GetInfoLavCond.ROWS.ROW.CODCPI").toString();
	String codiceCpiMin = serviceResponse.getAttribute("M_GetInfoLavCond.ROWS.ROW.CODCPIMIN").toString();*/
	String descrCpi = serviceResponse.getAttribute("M_GET_CPI_MINISTERIALE_UTLOG.ROWS.ROW.descrCPI").toString();
	String codiceCpi = serviceResponse.getAttribute("M_GET_CPI_MINISTERIALE_UTLOG.ROWS.ROW.CODCPI").toString();
	String codiceCpiMin = serviceResponse.getAttribute("M_GET_CPI_MINISTERIALE_UTLOG.ROWS.ROW.CODCPIMIN").toString();
	String tipoEvento =null;
	String cfBeneficiario = serviceResponse.getAttribute("M_GetInfoLavCond.ROWS.ROW.STRCODICEFISCALE").toString();
	String cfOperatoreCpi = null;
	if(serviceResponse.containsAttribute("M_GetCfOperatoreCPI.ROWS.ROW")){
		cfOperatoreCpi =serviceResponse.getAttribute("M_GetCfOperatoreCPI.ROWS.ROW.STRVALORE").toString();
		cfOperatoreCpi = cfOperatoreCpi.trim();		
	}
	String tipoDomanda =null;
	String protocolloInps =null;
	String dataDomanda =null;
	String dataEvento = null;
	String note =null;
	String programma =null;
	String dataInizioProg =null;
	String dataFineProg =null;
	String dataAvvio = null;
	String attivita = null;
	String dataConclusione = null;
	String esito = null;
	
	
	String readonly = String.valueOf( ! canModify );
	// Stringhe con HTML per layout tabelle
  	
  	String htmlStreamTop = StyleUtils.roundTopTable(canModify);
  	String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);
  	String paginaIndietro = "ListCondizionalitaPage";
  	
  	boolean showDettaglio = false;
  	String codTipoDomanda = null;
	String prgPercorso = null;
	String prgColloquio = null;
	String strDataCancellazioneEvento = null;
	String dataInvio = null;
	
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

<%
	// Genera il Javascript che si occuperà di inserire i links nel footer
	attributi.showHyperLinks(out, requestContainer, responseContainer, "cdnLavoratore="+cdnLavoratore);
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
	if( StringUtils.isFilledNoBlank(cdnLavoratore)){
		InfCorrentiLav testata = new InfCorrentiLav(RequestContainer.getRequestContainer().getSessionContainer(), cdnLavoratore, user);
		testata.show(out);
		Linguette l  = new Linguette(user, Integer.parseInt(cdnfunzioneStr), paginaIndietro, new BigDecimal(cdnLavoratore));
		l.show(out);
	}
%>
<%if(  StringUtils.isFilledNoBlank(cdnLavoratore)){ %>
<script language="Javascript">
			window.top.menu.caricaMenuLav(<%=cdnfunzioneStr%>,<%=cdnLavoratore%>);
		</script>
<%}%>

<br/>
<p class="titolo"><%= titolo %></p>

	<p>
	 	<font color="green">
	 		<af:showMessages prefix="M_EventiCondizionalita"/>
 	  	</font>
	  	<font color="red">
	  		<af:showErrors />
	  	</font>
	</p>

<af:form name="Frm1" action="AdapterHTTP" method="POST" onSubmit="checkCampiAggiuntiva()">
 	<input type="hidden" name="PAGE" value="DettaglioCondizionalitaPage"/>
 	<af:textBox type="hidden" name="cdnlavoratore" value="<%= cdnLavoratore %>" />
	<af:textBox type="hidden" name="cdnfunzione" value="<%= cdnfunzioneStr %>" />
	<input type="hidden" name="operazioneSalvaEvento" value="operazioneSalvaEvento"/>
	<input type="hidden" name="codiceCpiMin" value="<%= Utils.notNull(codiceCpiMin)  %>" />
 			
	<%out.print(htmlStreamTop);%>
	
	<%@ include file="campiCondizionalita.inc" %>

	<%if (canAdd) {%>
		<center>
			<input type="submit" class="pulsanti" value="Inserisci" name="Inserisci"/>
		</center><br/>
		<center>
		<input class="pulsante" type = "button" name="torna" value="Chiudi senza inserire" onClick="tornaLista();"/>
		</center>
	<%} else {%>
		<center>
		<input class="pulsante" type = "button" name="torna" value="Torna alla lista" onClick="tornaLista();"/>
		</center>
	<%}%>
	 <%out.print(htmlStreamBottom);%>

</af:form>

</body>
</html>
