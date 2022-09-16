<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">

<%@ page import="com.engiweb.framework.base.*,
                 java.lang.*,
                 java.text.*,
                 java.util.*,
                 java.util.HashSet,
                 it.eng.afExt.utils.StringUtils,
                 it.eng.sil.security.*,
                 it.eng.sil.util.Sottosistema"%>
            
<%@ taglib uri="aftags" prefix="af"%>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc"%>
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%

  String _page = (String) serviceRequest.getAttribute("PAGE"); 
  ProfileDataFilter filter = new ProfileDataFilter(user, _page);
  PageAttribs attributi = new PageAttribs(user, _page);
  
  boolean canPrint=false;
  
  if (! filter.canView()){
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
	} else {
		
		canPrint = attributi.containsButton("Stampa");
	}
  
  String chiamata = StringUtils.getAttributeStrNotNull(serviceRequest,"chiamata");	
  String CodCPI = StringUtils.getAttributeStrNotNull(serviceRequest,"CodCPI");	
  String queryString = null;
 
%>

<html>
<head>
<title>Lista Adesioni</title>
<link rel="stylesheet" href="../../css/stili.css" type="text/css">
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
<af:linkScript path="../../js/"/>

<%@ include file="../documenti/_apriGestioneDoc.inc"%>
  
<SCRIPT language="Javascript" type="text/javascript">
<!--
function go(url, alertFlag) {
// Se la pagina è già in submit, ignoro questo nuovo invio!
if (isInSubmit()) return;

var _url = "AdapterHTTP?" + url;
if (alertFlag == 'TRUE' ) {
if (confirm('Confermi operazione'))
setWindowLocation(_url);
}
else
setWindowLocation(_url);
}

// -->
</SCRIPT>
<script language="Javascript">
<% 
     	//Genera il Javascript che si occuperà di inserire i links nel footer
        attributi.showHyperLinks(out, requestContainer,responseContainer,"");
%>
</script>

<script language="Javascript">

	function tornaAllaRicerca()
  	{  
     // Se la pagina è già in submit, ignoro questo nuovo invio!
     if (isInSubmit()) return;
  
     url="AdapterHTTP?PAGE=ASRicercaAdesioniChiamPage";
     url += "&CodCPI="+"<%=CodCPI%>";
     url += "&chiamata="+"<%=chiamata%>";
     setWindowLocation(url);
  }
  
  	function stampaAdesioni () {
		if (!controllaFunzTL()) return;
	  
	 	apriGestioneDoc('RPT_ADESIONI_CHIAMATA','&CodCPI=<%=CodCPI%>&chiamata=<%=chiamata%>' , 'ALSEAO');
	 	undoSubmit();
	 }
	 
</script>
</head>
<body class="gestione" onload="rinfresca();">
<p></p>
<p class="titolo">RISULTATO DELLA RICERCA ADESIONI</p>

<% 		 
	
	String descCPI_H = StringUtils.getAttributeStrNotNull(serviceRequest,"descCPI_H");
	String attr   = null;
	String valore = null;
	String txtOut = "";
		
	attr= "descCPI_H";	
	valore = (String) serviceRequest.getAttribute(attr);		       
	if(valore != null && !valore.equals("")) {
		txtOut += "Cpi comp.: <strong>"+ valore +"</strong>; ";
	}
	attr= "chiamata";
	valore = (String) serviceRequest.getAttribute(attr);
	if(valore != null && !valore.equals("")) {
		txtOut += "Data della Chiamata <strong>"+ valore +"</strong>; ";	
	}   
%>		

<p align="center">
	<%if(txtOut.length() > 0) { 
		txtOut = "Filtri di ricerca:<br/> " + txtOut; %>
			<table cellpadding="2" cellspacing="10" border="0" width="100%">
				<tr><td style="border-width: 1px; border-style: solid; text-color: #000080; border-color: #000080; background-color:#dcdcdc">
		      		<%out.print(txtOut);%>
		     	</td></tr>
		   </table>
	<%}%>

<af:form dontValidate="true">
<af:list moduleName="M_AS_LISTA_RICH_CHIAM"/>
<center><input class="pulsante" type = "button" name="torna" value="Torna alla pagina di ricerca" onclick="tornaAllaRicerca()"/></center>
<br/>
<% if(canPrint) { %>
	<center><input type="button" class="pulsanti" value="Stampa" onclick="stampaAdesioni()" /></center>
<%}%>
<br/>
<br/>
</af:form>
</body>
</html>
