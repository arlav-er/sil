<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">

<%@ page import="com.engiweb.framework.base.*,
                 java.lang.*,
                 java.text.*,
                 java.util.*, 
                 it.eng.afExt.utils.StringUtils,
                 it.eng.sil.security.*"%>
            
<%@ taglib uri="aftags" prefix="af"%>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc"%>
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>


<% 
  String moduleName="";

  boolean flgFrequente=(boolean) serviceRequest.containsAttribute("flgFrequente");
  boolean ricercaDescrizione=(boolean) serviceRequest.containsAttribute("desMansione");

  if (ricercaDescrizione) {
    moduleName=(flgFrequente)?"M_CercaMansioneDESMANSIONE_Frequente":"M_CercaMansioneDESMANSIONE";
  } else {
    moduleName=(flgFrequente)?"M_CercaMansioneCODMANSIONE_Frequente":"M_CercaMansioneCODMANSIONE";
  }
  
  boolean flgTornaLista = serviceRequest.containsAttribute("TORNALISTA");
  String flgIdo = StringUtils.getAttributeStrNotNull(serviceRequest, "FLGIDO");
  String indiceMansione = StringUtils.getAttributeStrNotNull(serviceRequest, "indiceMansione");

	//Aggiunta Togna Cosimo 
	//27/01/05
	Vector res=(Vector)serviceResponse.getAttributeAsVector(moduleName.toUpperCase()+".ROWS.ROW");
	SourceBean risultato = (SourceBean) serviceResponse.getAttribute(moduleName.toUpperCase());
	SourceBean rowsSB = (SourceBean) risultato.getAttribute("ROWS");
	int numPages = 0;
	if (rowsSB != null && rowsSB.containsAttribute("NUM_PAGES") && !rowsSB.getAttribute("NUM_PAGES").toString().equals("")) {
		numPages = ((Integer) rowsSB.getAttribute("NUM_PAGES")).intValue();
	}

	String codMansione = "";
	String desMansione ="";
	String desTipoMansione = "";
	String onLoad="";
	boolean oneAndOnly = false;
		 
	if( res.size() == 1 && numPages == 1){
		oneAndOnly=true;
		codMansione=(String) ( (SourceBean)res.get(0) ).getAttribute("codMansione");
		desMansione=(String) ( (SourceBean)res.get(0) ).getAttribute("desMansione");
		desTipoMansione=(String) ( (SourceBean)res.get(0) ).getAttribute("desTipoMansione");
		desMansione = StringUtils.replace(desMansione,"'","\\'");
		desTipoMansione = StringUtils.replace(desTipoMansione,"'","\\'");
		
		onLoad="AggiornaForm('" + codMansione + "', '" + desMansione + "','" + desTipoMansione +  "')";
		
	}

%>

<html>
<head>
<title>
Mansioni-Ricerca avanzata
</title>
<link rel="stylesheet" href="../../css/stili.css" type="text/css">
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
<af:linkScript path="../../js/" />

<script language="javascript">
	var flgProvIdo = '<%=flgIdo%>';
	var indiceFieldMansione = '<%=indiceMansione%>';
</script>

<SCRIPT TYPE="text/javascript">
<!--
function AggiornaForm(codMansione, desMansione, strTipoMansione) {
	if (isScaduto(desMansione)) {
		alert("Non è possibile inserire una Mansione scaduta");
		if (indiceFieldMansione == '') {
			window.opener.document.Frm1.CODMANSIONE.value = "";
		}
		else {
			var fieldMansione = eval('window.opener.document.Frm1.CODMANSIONE' + indiceFieldMansione);
			field.value = "";
		}	
	} else {

	    <% if (!serviceRequest.containsAttribute("RICERCA2") )  {%>
	    	if (indiceFieldMansione == '') {
			 	window.opener.document.Frm1.CODMANSIONE.value = codMansione;
			    window.opener.document.Frm1.codMansioneHid.value=codMansione;
				window.opener.document.Frm1.DESCMANSIONE.value = desMansione.replace('^', '\'');
			  	window.opener.document.Frm1.strTipoMansione.value = strTipoMansione.replace('^', '\'');
	    	}
			else {
				var fieldMansione = eval('window.opener.document.Frm1.CODMANSIONE' + indiceFieldMansione);
				var fieldMansioneHid = eval('window.opener.document.Frm1.codMansioneHid' + indiceFieldMansione);
				var fieldMansioneDesc = eval('window.opener.document.Frm1.DESCMANSIONE' + indiceFieldMansione);
				var fieldMansioneTipo = eval('window.opener.document.Frm1.strTipoMansione' + indiceFieldMansione);
				fieldMansione.value = codMansione;
				fieldMansioneHid.value = codMansione;
				fieldMansioneDesc.value = desMansione.replace('^', '\'');
				fieldMansioneTipo.value = strTipoMansione.replace('^', '\'');
			}
	    <%}  else {
	    // sono giunto a questa pagina da una pagina che gestisce in modo autonomo la valorizzazione dei campi
	    %>    
	    window.opener.setValues(codMansione, desMansione.replace('^', '\''), strTipoMansione.replace('^', '\''));
	    <%}%>
	    if (window.opener.document.Frm1.paginaMansione != null){
	    	window.opener.visualizzaTipoDescrMansione("inline");
	    }    
	   	window.close();
   	}
}


function ricercaAvanzataMansioni() {

  	var w=800; var l=((screen.availWidth)-w)/2;
  	var h=500; var t=((screen.availHeight)-h)/2;
  	//var feat = "status=YES,location=YES,toolbar=NO,scrollbars=YES,resizable=YES,height="+h+",width="+w+",top="+t+",left="+l;
  	var feat = "status=NO,location=NO,toolbar=NO,scrollbars=YES,resizable=NO,height="+h+",width="+w+",top="+t+",left="+l;

  	var url = "AdapterHTTP?PAGE=RicercaMansioneAvanzataPage";
  	if (flgProvIdo == 'S') {
   		url = url + "&FLGIDO=S";
  	}
  	window.location = url;
  	
  	//window.open(url, "Mansioni", 'toolbar=0, scrollbars=1');
}


function isScaduto(str) {
	if (opener.flagRicercaPage != "S") { // Se NON provengo da una page di ricerca ammetto solo quelli non scaduti.
		if (str.substring(str.length-9) == "(scaduto)") {
			return true;
		}
	}
	return false;
}

-->
</SCRIPT>
</head>
<body class="gestione" onload="<%=onLoad%>">
<af:list moduleName="<%=moduleName%>" jsSelect="AggiornaForm" />
<br/>
<center>
<table>
<tr>
<td colspan="2">
<% if (flgTornaLista) {%>
	<input type="button" class="pulsante" name="torna" value="Torna alla ricerca" onClick="javascript:ricercaAvanzataMansioni();"/>
<%}%>
	<input type="button" class="pulsante" name="chiudi" value="Chiudi" onClick="javascript:window.close();"/>
</td>
<tr>
</table>
</center>
</body>
</html>