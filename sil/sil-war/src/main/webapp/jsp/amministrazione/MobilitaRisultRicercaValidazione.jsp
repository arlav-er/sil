<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">

<%@ page import="com.engiweb.framework.base.*,
                com.engiweb.framework.security.*,
                java.math.*,
                java.lang.*,java.text.*,java.util.*,
                it.eng.afExt.utils.*, it.eng.sil.util.*,
                it.eng.sil.security.*" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%@ taglib uri="aftags" prefix="af"%>

<%
  //boolean che indicano lo stato della validazione massiva 
  boolean validazioneInCorso = false;
  if (sessionContainer.getAttribute("VALIDATOREMASSIVOMOBILITACORRENTE") != null) {
  	validazioneInCorso = true;
  }
  boolean risultatiInSessione = false;
  String prgUltimaValidazioneMassiva = "";
  BigDecimal prgMobilitaRis = null;	
  SourceBean risultatoUltimaVal = (SourceBean) serviceResponse.getAttribute("M_MobGetRisultatiUltimaValidazioneUtente.ROWS.ROW");
  if (risultatoUltimaVal != null) {
  	risultatiInSessione = risultatoUltimaVal.containsAttribute("PRGMOBILITARIS");
  	prgMobilitaRis = (BigDecimal)risultatoUltimaVal.getAttribute("PRGMOBILITARIS");
  	if (prgMobilitaRis != null) {
  		prgUltimaValidazioneMassiva = prgMobilitaRis.toString();	
  	}
  }  
  // NOTE: Attributi della pagina (pulsanti e link)
  String _page = (String) serviceRequest.getAttribute("PAGE");
  PageAttribs attributi = new PageAttribs(user, _page);
  boolean canSelectPage = attributi.containsButton("SELEZIONA");
  boolean canValidateSelect = attributi.containsButton("VALIDASELEZIONATI");
  boolean canValidateFilter = attributi.containsButton("VALIDAFILTRATI");
  boolean canValidateAll = attributi.containsButton("VALIDATUTTI");
  boolean canSearch = attributi.containsButton("CERCA"); 
  boolean canDelete = attributi.containsButton("CANCELLA");
  boolean canDeleteSelect = attributi.containsButton("CANCELLASELEZIONATI");
  boolean canStopValidation = attributi.containsButton("ARRESTA");
  boolean canViewResult = attributi.containsButton("RISULTATOTUTTI");
  
  String cognome=StringUtils.getAttributeStrNotNull(serviceRequest,"cognome");
  String nome=StringUtils.getAttributeStrNotNull(serviceRequest,"nome");
  String CF=StringUtils.getAttributeStrNotNull(serviceRequest,"codiceFiscaleLavoratore");
  String ragSoc=StringUtils.getAttributeStrNotNull(serviceRequest,"ragioneSociale");
  String codiceFiscaleAz=StringUtils.getAttributeStrNotNull(serviceRequest,"codFiscaleAzienda");
  String pIva=StringUtils.getAttributeStrNotNull(serviceRequest,"pIva");
  String codTipoAzienda=StringUtils.getAttributeStrNotNull(serviceRequest,"codTipoAzienda");
  String CodCPI=StringUtils.getAttributeStrNotNull(serviceRequest,"CodCPI");
  String CodTipoLista=StringUtils.getAttributeStrNotNull(serviceRequest,"CodTipoLista");
  String datcrtda=StringUtils.getAttributeStrNotNull(serviceRequest,"datcrtda");
  String datcrta=StringUtils.getAttributeStrNotNull(serviceRequest,"datcrta");
  String numDeliberaReg=StringUtils.getAttributeStrNotNull(serviceRequest,"NumDelReg");      
  String datfineda=StringUtils.getAttributeStrNotNull(serviceRequest,"datfineda");
  String datfinea=StringUtils.getAttributeStrNotNull(serviceRequest,"datfinea");      
  String datinizioda=StringUtils.getAttributeStrNotNull(serviceRequest,"datinizioda");
  String datinizioa=StringUtils.getAttributeStrNotNull(serviceRequest,"datinizioa");      
  String datmaxdiffda=StringUtils.getAttributeStrNotNull(serviceRequest,"datmaxdiffda");
  String datmaxdiffa=StringUtils.getAttributeStrNotNull(serviceRequest,"datmaxdiffa");
  String descCPI = StringUtils.getAttributeStrNotNull(serviceRequest,"descCPI_H"); 
  String _funzione=serviceRequest.getAttribute("CDNFUNZIONE").toString();

  String parameters= "&cognome=" + cognome + "&nome=" + nome +
  					"&codiceFiscaleLavoratore=" + CF + "&CodCPI=" + CodCPI + 
  					"&codFiscaleAzienda=" + codiceFiscaleAz + "&ragioneSociale=" + ragSoc + "&pIva=" + pIva +
  					"&codTipoAzienda=" + codTipoAzienda + "&CodTipoLista=" + CodTipoLista + "&datcrtda=" + datcrtda + 
  					"&datcrta=" + datcrta + "&datfineda=" + datfineda + "&NumDelReg=" + numDeliberaReg +
  					"&datfinea=" + datfinea + "&datinizioda=" + datinizioda + 
  					"&datinizioa=" + datinizioa + "&datmaxdiffda=" + datmaxdiffda + 
  					"&datmaxdiffa=" + datmaxdiffa + "&CDNFUNZIONE=" + _funzione;		

  String queryString = null;
  Vector mobilitaFiltrate = serviceResponse.getAttributeAsVector("M_Mob_RisMobilitaDaValidare.ROWS.ROW");
  boolean permettiValidazione = (mobilitaFiltrate.size()>=1)? true : false;
  String htmlStreamTop = StyleUtils.roundTopTable(false);
  String htmlStreamBottom = StyleUtils.roundBottomTable(false);
%>

<html>
<head>
 <title>Risultati Ricerca</title>
 <link rel="stylesheet" type="text/css" href=" ../../css/stili.css"/>
 <link rel="stylesheet" type="text/css" href=" ../../css/listdetail.css"/>
 <af:linkScript path="../../js/"/>

<%@ include file="../documenti/_apriGestioneDoc.inc"%>

<script type="text/JavaScript">
<% 
	//Genera il Javascript che si occuperà di inserire i links nel footer
    attributi.showHyperLinks(out, requestContainer,responseContainer,"");
%>
function altraRicerca() { // Se la pagina è già in submit, ignoro questo nuovo invio!
  if (isInSubmit()) return;
	url="AdapterHTTP?PAGE=MobilitaRicercaValidazionePage";
    url += "<%=parameters%>";             
    setWindowLocation(url);
}

function Delete(page, prgMob) {
	if (confirm('Vuoi proseguire con la cancellazione?')) {
		document.FormListaValidazione.PAGE.value = page;
		document.FormListaValidazione.OPERAZIONE.value = "cancella";
		document.FormListaValidazione.PRGMOBAPPCANC.value = prgMob;
		doFormSubmit(document.FormListaValidazione);
	}
}

//Funzione che viene eseguita ad ogni click delle checkbox relativa alla mobilità
function checkboxMobilitaClick(checkbox, prgMobilitaIscrApp) {		 
}

function selectPage() {
	var form = document.FormListaValidazione;
    for(var i = 0; i < form.elements.length ; i ++) {
        if(form.elements[i].name == "CHECKBOXMOBVALIDARE" && 
           !form.elements[i].checked) {
            form.elements[i].click;
            form.elements[i].checked = true;
        }
    } 
}

function confirmValidateSelected() {
	// Se la pagina è già in submit, ignoro questo nuovo invio!
	if (isInSubmit()) return;
	
	//Controllo se la ricerca non ha prodotto alcun risultato  
	if(<%=!permettiValidazione%>){
		alert("Non ci sono mobilità da validare");
    	return;
	}	
	
	//Controllo che sia stato selezionato almeno un movimento	
	var form = document.FormListaValidazione;
    var numMobilitaSel=0;
    var prgMobSelezionate = '';
    for(var i = 0; i < form.elements.length ; i ++) {
        if(form.elements[i].name == "CHECKBOXMOBVALIDARE" && form.elements[i].checked) {
        	if (prgMobSelezionate == '') {
        	 	prgMobSelezionate = prgMobSelezionate + form.elements[i].value;
        	 }
        	 else {
        	 	prgMobSelezionate = prgMobSelezionate + '#' + form.elements[i].value;
        	 }
           	 numMobilitaSel++;
        }
    } 
    if (numMobilitaSel==0){ 
    	alert ("Selezionare almeno una mobilità");
    	return ;
   	}

	if (confirm('Vuoi validare le mobilità selezionate?')) {
		document.FormListaValidazione.azioneScelta.value = "validaSelezionati";
		document.FormListaValidazione.prgListaMobilitaDaValidare.value = prgMobSelezionate;
		doFormSubmit(document.FormListaValidazione);
	}
  
}

function validateAll() {
	if (confirm('Vuoi validare tutte le mobilità presenti nel sistema?')) {
		document.FormListaValidazione.azioneScelta.value = "validaTutti";
		doFormSubmit(document.FormListaValidazione);
	}
}

function stopValidazione() {
	if (confirm('Vuoi arrestare la validazione massiva corrente?')) {
		document.FormListaValidazione.azioneScelta.value = "arresta";
		doFormSubmit(document.FormListaValidazione);
	}
}

function confirmValidateExtracted(){
	// Se la pagina è già in submit, ignoro questo nuovo invio!
	if (isInSubmit()) return;
	if(<%=!permettiValidazione%>){
		alert("Non ci sono movimenti da validare");
        return;
	}
  	if(confirm('Vuoi validare i movimenti filtrati?')){
  		document.FormListaValidazione.azioneScelta.value="validaFiltrati";
		doFormSubmit(document.FormListaValidazione);	
  	}
}

function confirmDeleteSelected() {
	//Controllo che sia stato selezionato almeno un elemento	
	var form = document.FormListaValidazione;
    var numElemSel=0;
    var strPrgMobilita = '';
    for(var i = 0; i < form.elements.length ; i++) {
    	if(form.elements[i].name == "CHECKBOXMOBVALIDARE" && form.elements[i].checked) {
        	if (strPrgMobilita == '') {
           		strPrgMobilita = strPrgMobilita + form.elements[i].value;
           	}
           	else {
           		strPrgMobilita = strPrgMobilita + '#' + form.elements[i].value;
            }
            numElemSel++;
      	}
  	}
  	
 	if (numElemSel==0) { 
		alert ("Selezionare almeno una mobilità");
	   	return ;
	}

	if (confirm('Vuoi cancellare le mobilità selezionate?')) {
		document.FormListaValidazione.PAGE.value = "MobilitaValidazioneRisultRicercaPage";
		document.FormListaValidazione.OPERAZIONE.value = "cancella";
		document.FormListaValidazione.PRGMOBAPPCANC.value = strPrgMobilita;
		doFormSubmit(document.FormListaValidazione);
	}
}

//funzione di get per la visualizzazione dei risultati della validazione massiva
function visulizzaRisultati() {
	var get = "AdapterHTTP?PAGE=MobVisualizzaRisultValidazionePage&cdnFunzione=<%=_funzione%>&PAGERISULTVALMASSIVA=FIRST&prgUltimaValidazioneMassiva=<%=prgUltimaValidazioneMassiva%>";
	setWindowLocation(get);
}

</script>

</head>

<body onload="checkError();rinfresca()" class="gestione">
<af:error/>
<br/><p class="titolo">RISULTATO DELLA RICERCA SULLA MOBILITA' DA VALIDARE</p>

<%	String attr   = null;
  	String valore = null;
	String txtOut = "";
  	attr= "cognome";
   	valore = (String) serviceRequest.getAttribute(attr);
   	if(valore != null && !valore.equals("")) {
   		txtOut += "Cognome <strong>"+ valore +"</strong>; ";
   	}
   	attr= "nome";
    valore = (String) serviceRequest.getAttribute(attr);
    if(valore != null && !valore.equals("")) {
    	txtOut += "Nome <strong>"+ valore +"</strong>; ";
    }
    attr= "codiceFiscaleLavoratore";
    valore = (String) serviceRequest.getAttribute(attr);
    if(valore != null && !valore.equals("")) {
    	txtOut += "Codice fiscale <strong>"+ valore +"</strong>; ";
    }
    attr= "ragioneSociale";
    valore = (String) serviceRequest.getAttribute(attr);
    if(valore != null && !valore.equals("")) {
    	txtOut += "Ragione sociale <strong>"+ valore +"</strong>; ";
    }
    attr= "codFiscaleAzienda";
    valore = (String) serviceRequest.getAttribute(attr);
    if(valore != null && !valore.equals("")) {
    	txtOut += "Codice fiscale azienda <strong>"+ valore +"</strong>; ";
    }
    attr= "pIva";
    valore = (String) serviceRequest.getAttribute(attr);
    if(valore != null && !valore.equals("")) {
    	txtOut += "Partita iva <strong>"+ valore +"</strong>; ";
    }
    attr= "descCodTipoLista_H";
    valore = (String) serviceRequest.getAttribute(attr);
    if(valore != null && !valore.equals("")) {
    	txtOut += "Tipo lista <strong>"+ valore +"</strong>; ";
   	}
    attr= "datinizioda";
    valore = (String) serviceRequest.getAttribute(attr);
    if(valore != null && !valore.equals("")) {
    	txtOut += "  Data inizio da <strong>"+ valore +"</strong>; ";
    }
    attr= "datinizioa";
    valore = (String) serviceRequest.getAttribute(attr);
    if(valore != null && !valore.equals("")) {
    	txtOut += "Data inizio a <strong>"+ valore +"</strong>; ";
    }
   	attr= "datfineda";
    valore = (String) serviceRequest.getAttribute(attr);
    if(valore != null && !valore.equals("")) {
    	txtOut += "Data fine da <strong>"+ valore +"</strong>; ";
    }
    attr= "datfinea";
    valore = (String) serviceRequest.getAttribute(attr);
    if(valore != null && !valore.equals("")) {
    	txtOut += "Data fine a <strong>"+ valore +"</strong>; ";
    }
    attr= "datmaxdiffda";
    valore = (String) serviceRequest.getAttribute(attr);
    if(valore != null && !valore.equals("")) {
    	txtOut += "Data max differimento da <strong>"+ valore +"</strong>; ";
    }
    attr= "datmaxdiffa";
    valore = (String) serviceRequest.getAttribute(attr);
    if(valore != null && !valore.equals("")) {
    	txtOut += "Data max differimento a <strong>"+ valore +"</strong>; ";
    }
    attr= "datcrtda";
    valore = (String) serviceRequest.getAttribute(attr);
    if(valore != null && !valore.equals("")) {
    	txtOut += "Data CRT da <strong>"+ valore +"</strong>; ";
    }
    attr= "datcrta";
    valore = (String) serviceRequest.getAttribute(attr);
    if(valore != null && !valore.equals("")) {
    	txtOut += "Data CRT a <strong>"+ valore +"</strong>; ";
    }
    attr= "NumDelReg";
    valore = (String) serviceRequest.getAttribute(attr);
    if(valore != null && !valore.equals("")) {
    	txtOut += "Numero delibera regionale <strong>"+ valore +"</strong>; ";
    }
    attr= "descCPI_H";
    valore = (String) serviceRequest.getAttribute(attr);
    if(valore != null && !valore.equals("")) {
    	txtOut += " Cpi comp.:<strong>" + valore + "</strong>; ";
    }
%>    
<p align="center">
<%if(txtOut.length() > 0)
  { txtOut = "Filtri di ricerca:<br/> " + txtOut;%>
    <table cellpadding="2" cellspacing="10" border="0" width="100%">
     <tr><td style="border-width: 1px; border-style: solid; text-color: #000080; border-color: #000080; background-color:#dcdcdc">
      <%out.print(txtOut);%>
     </td></tr>
    </table>
<%}%>

<af:form name="FormListaValidazione" method="POST" action="AdapterHTTP">
<%out.print(htmlStreamTop);%>
<af:list moduleName="M_Mob_RisMobilitaDaValidare" 
         canDelete="<%= canDelete ? \"1\" : \"0\" %>" 
         jsDelete="Delete" />

<table class="main">
	<tr>
	  	<td colspan="10" align="center">
	  	  <% if (canSelectPage) { %>
		      <input class="pulsanti" type="button" onclick="selectPage();" name="selezionaPagina" value="Seleziona pagina"/>   
		      &nbsp;&nbsp;
		  <% }
		  if (canValidateSelect) { %>     
		      <input class="pulsanti" type="button" onclick="confirmValidateSelected();" name="validaSelezionati" value="Valida selezionati" />
		      &nbsp;&nbsp;
		  <% }
		  if (canDeleteSelect) { %>
		 	  <input class="pulsanti" type="button" onclick="confirmDeleteSelected();" name="cancellaSelezionati" value="Cancella selezionati" />
		 	  &nbsp;&nbsp;
		  <% }
		  if (canValidateFilter) { %>  
		      <input class="pulsanti" type="button" onclick="confirmValidateExtracted();" name="validaEstratti" value="Valida filtrati" />
	          &nbsp;&nbsp;
	      <% }
	      %>
	      </td></tr>
	      <tr>
	  	  <td colspan="10" align="center">
	      <%
	      if (!validazioneInCorso) {   
		      if (canValidateAll) {%>        
				  <input class="pulsanti" type="button" onclick="validateAll();" name="validaTutti" value="Valida tutti" title="Valida tutti i movimenti presenti nel sistema"/>
				  &nbsp;&nbsp;
			 <%}        
		  } else {
		  	if (canStopValidation) {%>  	 
				<input class="pulsanti" type="button" onclick="stopValidazione();" name="arrestaValidazione" value="Arresta validazione" title="Ferma la validazione massiva attualmente in esecuzione"/>        
			  	&nbsp;&nbsp;
			<%}
		  } 
		  if (canSearch) { %>
	      	<input class="pulsanti" type="button" onclick="altraRicerca();" name="cerca" value="Altra ricerca"/>
	      	&nbsp;&nbsp;
	      <%}
	      if (canViewResult) {
	      	if (risultatiInSessione) {%>
	      		<input class="pulsanti" type="button" onclick="visulizzaRisultati();" name="azioneVisualizzaRis" value="Risultati ultima validazione" title="Visualizza i risultati dell'ultima validazione massiva eseguita"/>
	  		<%}
	  	 }%>
	  	</td>
	  </tr>
</table>
<%out.print(htmlStreamBottom);%>
<input type="hidden" name="PAGE" value="MobilitaValidazionePage"/>
<input type="hidden" name="azioneScelta" value=""/>
<input type="hidden" name="prgListaMobilitaDaValidare" value=""/>
<input type="hidden" name="cdnFunzione" value="<%=_funzione%>"/>
<input type="hidden" name="cognome" value="<%=cognome%>"/>
<input type="hidden" name="nome" value="<%=nome%>"/>
<input type="hidden" name="codiceFiscaleLavoratore" value="<%=CF%>"/>
<input type="hidden" name="ragioneSociale" value="<%=ragSoc%>"/>
<input type="hidden" name="codFiscaleAzienda" value="<%=codiceFiscaleAz%>"/>
<input type="hidden" name="pIva" value="<%=pIva%>"/>
<input type="hidden" name="codTipoAzienda" value="<%=codTipoAzienda%>"/>
<input type="hidden" name="CodCPI" value="<%=CodCPI%>"/>
<input type="hidden" name="CodTipoLista" value="<%=CodTipoLista%>"/>
<input type="hidden" name="datcrtda" value="<%=datcrtda%>"/>
<input type="hidden" name="datcrta" value="<%=datcrta%>"/>
<input type="hidden" name="NumDelReg" value="<%=numDeliberaReg%>"/>
<input type="hidden" name="datfineda" value="<%=datfineda%>"/>
<input type="hidden" name="datfinea" value="<%=datfinea%>"/>
<input type="hidden" name="datinizioda" value="<%=datinizioda%>"/>
<input type="hidden" name="datinizioa" value="<%=datinizioa%>"/>
<input type="hidden" name="datmaxdiffda" value="<%=datmaxdiffda%>"/>
<input type="hidden" name="datmaxdiffa" value="<%=datmaxdiffa%>"/>
<input type="hidden" name="descCPI_H" value="<%=descCPI%>"/>
<input type="hidden" name="OPERAZIONE" value=""/>
<input type="hidden" name="PRGMOBAPPCANC" value=""/>
<input type="hidden" name="PAGERITORNOLISTA" value="MobilitaValidazioneRisultRicercaPage"/>

</af:form>
</body>
</html>