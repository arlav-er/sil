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


  boolean codCorso=(boolean) serviceRequest.containsAttribute("codCorso");
  boolean isStrCorso=(boolean) serviceRequest.containsAttribute("strTitolo");
  String strRicerca = "";
  boolean isConferimentoDid = false;
  boolean isProfilingGG = false;
  // (boolean) serviceRequest.containsAttribute("confDid");
  

	  if (codCorso) {
		    moduleName="M_CercaCorsiCODCORSO";
		} else {
		    moduleName="M_CercaCorsiDESCORSO";
		  }

  
  boolean flgTornaLista = serviceRequest.containsAttribute("TORNALISTA");
  boolean formPro = (boolean) serviceRequest.containsAttribute("FORMPRO");

  
%>



<html>
<head>
<title>Lista Corsi</title>
<link rel="stylesheet" href="../../css/stili.css" type="text/css">
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
<af:linkScript path="../../js/" />

<SCRIPT TYPE="text/javascript">
<!--
var isConferimentoDid = false; 
<%-- 	"<%= isConferimentoDid%>"; --%>
 var isProfilingGG = false; 
<%-- 	 "<%=isProfilingGG%>"; --%>
var isCorso = "<%=isStrCorso%>";
var formPro = "<%=formPro%>";


function  AggiornaForm (codCorso, desCorso, anno){
	if(formPro=='false') {
		window.opener.document.Frm1.codCorso.value = codCorso;
		window.opener.document.Frm1.codCorsoHid.value=codCorso;
		window.opener.document.Frm1.strTitolo.value = desCorso;
	}
	else {
	      window.opener.document.Frm1.CODCORSO.value = codCorso;
	      window.opener.document.Frm1.DESCCORSO.value = desCorso.replace('^', '\'');
	      if(anno!='0'){
	    	  window.opener.document.Frm1.NUMANNO.value = anno;
	      } 
	}
	

 	
  	window.close();
}

function ricercaAvanzataCorsi() {
  var w=800; var l=((screen.availWidth)-w)/2;
  var h=500; var t=((screen.availHeight)-h)/2;
  //var feat = "status=YES,location=YES,toolbar=NO,scrollbars=YES,resizable=YES,height="+h+",width="+w+",top="+t+",left="+l;
  var feat = "status=NO,location=NO,toolbar=NO,scrollbars=YES,resizable=NO,height="+h+",width="+w+",top="+t+",left="+l;
  var url = "AdapterHTTP?PAGE=RicercaCorsiAvanzataPage";
  if(formPro=='true') {
	  url = url + "&FORMPRO=1";
  }
	  window.location=url;

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
	<input type="button" class="pulsante" name="torna" value="Torna alla ricerca" onClick="javascript:ricercaAvanzataCorsi();"/>
<%}%>
	<input type="button" class="pulsante" name="chiudi" value="Chiudi" onClick="javascript:window.close();"/>
</td>
<tr>
</table>
</center>
</body>
</html>










