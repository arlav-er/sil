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


  boolean codTitolo=(boolean) serviceRequest.containsAttribute("codTitolo");
  boolean isStrTitolo=(boolean) serviceRequest.containsAttribute("strTitolo");
  String strRicerca = "";
  boolean isConferimentoDid = false;
  boolean isProfilingGG = false;
  // (boolean) serviceRequest.containsAttribute("confDid");
  
  if(isConferimentoDid){
	  if (codTitolo) {
		    moduleName="M_CCD_CercaTitoloStudioCODTITOLO";
		  } else {
		    moduleName="M_CCD_CercaTitoloStudioDESTITOLO";
		    if(isStrTitolo){
		    	strRicerca = serviceRequest.getAttribute("strTitolo").toString();
		    }
		    
		  }
  }else{
	  if (codTitolo) {
		    moduleName="M_CercaTitoloStudioCODTITOLO";
		  } else {
		    moduleName="M_CercaTitoloStudioDESTITOLO";
		  }
  }

  
  boolean flgTornaLista = serviceRequest.containsAttribute("TORNALISTA");
  
%>



<html>
<head>
<title>Lista Titoli</title>
<link rel="stylesheet" href="../../css/stili.css" type="text/css">
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
<af:linkScript path="../../js/" />

<SCRIPT TYPE="text/javascript">
<!--
var isConferimentoDid = false; 
<%-- 	"<%= isConferimentoDid%>"; --%>
 var isProfilingGG = false; 
<%-- 	 "<%=isProfilingGG%>"; --%>
var isTitolo = "<%=isStrTitolo%>";

function  AggiornaForm (codTitolo, desTitolo, desTipologia, flgLaurea){
	  window.opener.document.Frm1.codTitolo.value = codTitolo;
    window.opener.document.Frm1.codTitoloHid.value=codTitolo;
		window.opener.document.Frm1.strTitolo.value = desTitolo;
  	window.opener.document.Frm1.strTipoTitolo.value = desTipologia;
	if(!isConferimentoDid){
		window.opener.document.Frm1.flgLaurea.value = flgLaurea;
	    window.opener.toggleVisStato(window.opener.document.Frm1.codMonoStato);
	}
 	
  	window.close();
}

function ricercaAvanzataTitoliStudio() {
  var w=800; var l=((screen.availWidth)-w)/2;
  var h=500; var t=((screen.availHeight)-h)/2;
  //var feat = "status=YES,location=YES,toolbar=NO,scrollbars=YES,resizable=YES,height="+h+",width="+w+",top="+t+",left="+l;
  var feat = "status=NO,location=NO,toolbar=NO,scrollbars=YES,resizable=NO,height="+h+",width="+w+",top="+t+",left="+l;
  if(isConferimentoDid){
	  var url = "AdapterHTTP?confDid=1&PAGE=CCD_RicercaTitoloStudioAvanzataPage&TORNARICERCA=";
	  if(isTitolo){
		  url +="&strTitolo="+"<%=strRicerca%>";
	  }
	  window.location=url;;
	  
  }else  if(isProfilingGG){
	  var url = "AdapterHTTP?FLG_GG=ok&PAGE=CCD_RicercaTitoloStudioAvanzataPage&TORNARICERCA=";
	  if(isTitolo){
		  url +="&strTitolo="+"<%=strRicerca%>";
	  }
	  window.location=url;;
	  
  }else{
	  window.location="AdapterHTTP?PAGE=RicercaTitoloStudioAvanzataPage";

  }
  
  //window.open("AdapterHTTP?PAGE=RicercaTitoloStudioAvanzataPage", "Titoli", 'toolbar=0, scrollbars=1');
}

-->
</SCRIPT>
</head>
<body class="gestione">
<af:list moduleName="<%= moduleName%>" jsSelect="AggiornaForm" />
<br/>
<center>
<table>
<tr>
<td colspan="2">
<% if (flgTornaLista) {%>
	<input type="button" class="pulsante" name="torna" value="Torna alla ricerca" onClick="javascript:ricercaAvanzataTitoliStudio();"/>
<%}%>
	<input type="button" class="pulsante" name="chiudi" value="Chiudi" onClick="javascript:window.close();"/>
</td>
<tr>
</table>
</center>
</body>
</html>










