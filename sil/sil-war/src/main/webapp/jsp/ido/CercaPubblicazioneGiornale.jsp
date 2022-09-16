<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">

<%@ page import="com.engiweb.framework.base.*,
                 java.lang.*,
                 java.text.*,
                 java.util.*, 
                 it.eng.afExt.utils.StringUtils,
                 it.eng.sil.util.StyleUtils,
                 it.eng.sil.security.*"%>
            
<%@ taglib uri="aftags" prefix="af"%>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc"%>
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>


<% 
  int _funzione=Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));
  PageAttribs attributi = new PageAttribs(user, "IdoPubbGiorReportPage");
  boolean canModify= attributi.containsButton("inserisci");
  boolean canDelete= attributi.containsButton("rimuovi");  

  int listIns;
  int listDel;

  if (canModify){listIns=1;} else {listIns=0;}
  if (canDelete){listDel=1;} else {listDel=0;}  
  
listIns=1;
listDel=1;  
%>
<SCRIPT>
function goBack() {
	setWindowLocation("AdapterHTTP?PAGE=IdoPubbGiorReportPage&CDNFUNZIONE=<%=_funzione%>");
}		
</SCRIPT>
<html>
<head>
<title>CercaPubblicazioneGiornale.jsp</title>
<link rel="stylesheet" href="../../css/stili.css" type="text/css">
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
<af:linkScript path="../../js/" />

<script language="Javascript">
     <% 
     	//Genera il Javascript che si occuperà di inserire i links nel footer
        attributi.showHyperLinks(out, requestContainer,responseContainer,"");
      %>
</script>
</head>
<body class="gestione">
<table>
<tr>
  <td align=right>
    <center>
      <font color="red">
        <af:showErrors/>
      </font>
    </center>
  </td>
</tr>
</table>
<af:list moduleName="M_DYNRICERCAGIORPUBB" canInsert="<%=String.valueOf(listIns)%>" canDelete="<%=String.valueOf(listDel)%>"/>
	<center>
	  <input class="pulsante" type="button" onClick="goBack();" value="Torna alla pagina di ricerca"/>
	</center>
</body>
</html>
