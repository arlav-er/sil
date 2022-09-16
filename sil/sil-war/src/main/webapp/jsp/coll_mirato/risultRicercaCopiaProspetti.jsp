<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">

<%@ page import="com.engiweb.framework.base.*,
                com.engiweb.framework.security.*,
                java.math.*,
                java.lang.*,java.text.*,java.util.*,
                it.eng.afExt.utils.*, it.eng.sil.util.*,
                it.eng.sil.module.collocamentoMirato.constant.*,
                it.eng.sil.security.*" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%@ taglib uri="aftags" prefix="af"%>

<%
  boolean copiaInCorso = false;
  if (sessionContainer.getAttribute("PROCESSOCOPIAPROSPETTOCORRENTE") != null) {
	  copiaInCorso = true;
  }
  // NOTE: Attributi della pagina (pulsanti e link)
  String _page = (String) serviceRequest.getAttribute("PAGE");
  PageAttribs attributi = new PageAttribs(user, _page);
  
  String annoNuovo = StringUtils.getAttributeStrNotNull(serviceRequest,"annoNew");
  String annoVerifica = StringUtils.getAttributeStrNotNull(serviceRequest,"annoOld");
  String categoriaAz = StringUtils.getAttributeStrNotNull(serviceRequest,"codMonoCategoria");
  String categoriaDesc = "";
  String txtOut = "";
  
  if (categoriaAz.equals(ProspettiConstant.CATEG_AZ_MAGGIORE_50)) {
	  categoriaDesc =  ProspettiConstant.DESC_CATEG_AZ_MAGGIORE_50;
  }
  else {
	  if (categoriaAz.equals(ProspettiConstant.CATEG_AZ_TRA_36_50)) {
		  categoriaDesc =  ProspettiConstant.DESC_CATEG_AZ_TRA_36_50;
	  }
	  else {
		  if (categoriaAz.equals(ProspettiConstant.CATEG_AZ_TRA_15_35)) {
			  categoriaDesc =  ProspettiConstant.DESC_CATEG_AZ_TRA_15_35;  
		  }
		  else {
			  if (categoriaAz.equals(ProspettiConstant.CATEGORIA_NULLA)) {
				categoriaDesc = ProspettiConstant.DESC_CATEG_AZ_NULLA; 
			  }
		  }
	  }
  }
  
  String _funzione = serviceRequest.getAttribute("CDNFUNZIONE").toString();
  
  String parameters = "&CDNFUNZIONE=" + _funzione;		

  String queryString = null;
  Vector prospettiFiltrati = serviceResponse.getAttributeAsVector("M_ProspDaCopiare.ROWS.ROW");
  boolean permettiCopia = (prospettiFiltrati.size()>=1)? true : false;
  String htmlStreamTop = StyleUtils.roundTopTable(false);
  String htmlStreamBottom = StyleUtils.roundBottomTable(false);
%>

<html>
<head>
<title>Risultati Ricerca</title>
<link rel="stylesheet" type="text/css" href=" ../../css/stili.css"/>
<link rel="stylesheet" type="text/css" href=" ../../css/listdetail.css"/>
<af:linkScript path="../../js/"/>

<script type="text/JavaScript">
<% 
//Genera il Javascript che si occuperà di inserire i links nel footer
attributi.showHyperLinks(out, requestContainer,responseContainer,"");
%>

function altraRicerca() { // Se la pagina è già in submit, ignoro questo nuovo invio!
  if (isInSubmit()) return;
	var urlRicerca="AdapterHTTP?PAGE=CopiaProspRicercaPage";
	urlRicerca += "<%=parameters%>";             
    setWindowLocation(urlRicerca);
}

//Funzione che viene eseguita ad ogni click delle checkbox relativa a quella riga
function checkboxCopiaProspClick(checkbox, prgProspettoInf) {		 
}

function selectPage() {
	var form = document.FormListaValidazione;
    for(var i = 0; i < form.elements.length ; i ++) {
        if(form.elements[i].name == "CHECKBOXCOPIAPROSP") {
            if (document.FormListaValidazione.selezionaPagina.checked) {
	          	form.elements[i].click;
	            form.elements[i].checked = true;   
	        }
	        else {
	        	form.elements[i].click;
	            form.elements[i].checked = false;
	    	}
   	 	} 
	}
}

function confirmValidateSelected() {
	// Se la pagina è già in submit, ignoro questo nuovo invio!
	if (isInSubmit()) return;
	
	//Controllo se la ricerca non ha prodotto alcun risultato  
	if(<%=!permettiCopia%>){
		alert("Non ci sono prospetti per cui effettuare la copia in corso d\'anno.");
    	return;
	}	
	
	//Controllo che sia stato selezionato almeno un prospetto	
	var form = document.FormListaValidazione;
    var numProspSel=0;
    var prgProspettoInfSelezionate = '';
    for(var i = 0; i < form.elements.length ; i ++) {
        if(form.elements[i].name == "CHECKBOXCOPIAPROSP" && form.elements[i].checked) {
        	if (prgProspettoInfSelezionate == '') {
        		prgProspettoInfSelezionate = prgProspettoInfSelezionate + form.elements[i].value;
        	 }
        	 else {
        		 prgProspettoInfSelezionate = prgProspettoInfSelezionate + '#' + form.elements[i].value;
        	 }
           	 numProspSel++;
        }
    } 
    if (numProspSel==0){ 
    	alert ("Selezionare almeno una riga");
    	return ;
   	}

	if (confirm("Vuoi effettuare la copia in corso d\'anno per le aziende selezionate?")) {
		document.FormListaValidazione.azioneScelta.value = "validaSelezionati";
		document.FormListaValidazione.prgListaProspDaCopiare.value = prgProspettoInfSelezionate;
		doFormSubmit(document.FormListaValidazione);
	}
  
}

function validateAll() {
	if (confirm("Vuoi effettuare la copia in corso d\'anno per le prime 300 aziende estratte?")) {
		document.FormListaValidazione.azioneScelta.value = "validaTutti";
		doFormSubmit(document.FormListaValidazione);
	}
}

</script>

</head>

<body onload="checkError();rinfresca()" class="gestione">
<af:error/>
<br/><p class="titolo">Elenco PI per copia in corso d&#39;anno</p>
<%
if(!annoNuovo.equals("")) {
	txtOut += "Anno per la nuova copia in corso d&#39;anno <strong>"+ annoNuovo +"</strong>; ";
}

if(!annoVerifica.equals("")) {
	txtOut += "Anno di verifica <strong>"+ annoVerifica +"</strong>; ";
}

if(!categoriaDesc.equals("")) {
	txtOut += "Categoria azienda <strong>"+ categoriaDesc +"</strong>; ";
}
%>    
<p align="center">
<%if(txtOut.length() > 0) { 
	txtOut = "Filtri di ricerca:<br/> " + txtOut;%>
    <table cellpadding="2" cellspacing="10" border="0" width="100%">
    <tr>
   		<td style="border-width: 1px; border-style: solid; text-color: #000080; border-color: #000080; background-color:#dcdcdc">
      	<%out.print(txtOut);%>
     	</td>
   	</tr>
    </table>
<%}%>

<af:form name="FormListaValidazione" method="POST" action="AdapterHTTP">
<table class="main">
	<tr>
	 <td class="campo">
 		<input type="checkbox" name="selezionaPagina" value="" onclick="selectPage();"/>&nbsp;Seleziona pagina
 	 </td>            
   	</tr>
</table>
<%out.print(htmlStreamTop);%>
<af:list moduleName="M_ProspDaCopiare"/>

<table class="main">
	<%if (!copiaInCorso && permettiCopia) {%>
	<tr>
		<td align="center">   
	    <input class="pulsanti" type="button" onclick="confirmValidateSelected();" 
	      	name="validaSelezionati" value="Genera copia per le aziende selezionate" />   
			&nbsp;&nbsp;<input class="pulsanti" type="button" onclick="validateAll();" name="validaTutti" 
				value="Genera copia per prime 300 aziende estratte" />
	    </td>
	</tr>
	<%}%>
	<tr>
		<td align="center">
      	<input class="pulsanti" type="button" onclick="altraRicerca();" name="cerca" value="Torna alla pagina di ricerca"/>
      	</td>
	</tr>
</table>
<%out.print(htmlStreamBottom);%>
<input type="hidden" name="PAGE" value="EffettuaCopiaProspettiPage"/>
<input type="hidden" name="azioneScelta" value=""/>
<input type="hidden" name="prgListaProspDaCopiare" value=""/>
<input type="hidden" name="cdnFunzione" value="<%=_funzione%>"/>

<input type="hidden" name="annoNew" value="<%=annoNuovo%>"/>
<input type="hidden" name="annoOld" value="<%=annoVerifica%>"/>
<input type="hidden" name="codMonoCategoria" value="<%=categoriaAz%>"/>

<input type="hidden" name="PAGERITORNOLISTA" value="CopiaProspVisualizzaRisultPage"/>

</af:form>
</body>
</html>