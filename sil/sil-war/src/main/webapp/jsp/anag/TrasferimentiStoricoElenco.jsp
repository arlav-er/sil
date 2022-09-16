<%@ page contentType="text/html;charset=utf-8"%>
 
<%@ page import="com.engiweb.framework.base.*,
                com.engiweb.framework.security.*,
                java.lang.*,java.text.*,java.util.*,
                it.eng.afExt.utils.*, it.eng.sil.util.*,
                it.eng.sil.security.*" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%@ taglib uri="aftags" prefix="af"%>

<% 
  // NOTE: Attributi della pagina (pulsanti e link) 
  PageAttribs attributi = new PageAttribs(user, "TrasferimentiStoricoElencoPage");
  boolean canInsert = false;//attributi.containsButton("nuovo");
  boolean canDelete = true;//attributi.containsButton("rimuovi");
  
  //cooperazione applicativa
  
  
 String _funzione=(String) serviceRequest.getAttribute("CDNFUNZIONE");
 

%>

<html>
<head>
 <title>Elenco Storico Trasferimenti</title>
 <!-- ../jsp/anag/TrasferimentiStoricoElenco.jsp -->
 <link rel="stylesheet" type="text/css" href="../../css/stili.css"/>
 <link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
 <af:linkScript path="../../js/"/>

 <script type="text/Javascript">
 </script>
 
</head>
<!--<body  onload="checkError();" class="gestione" > -->
<body  class="gestione" onload="rinfresca()">
<center>
<font color="red">
    <af:showErrors/>
</font>
</center>
<af:form dontValidate="true">
<br/>
<af:list moduleName="M_TrasferimentiStoricoElenco" canDelete="<%= canDelete ? \"1\" : \"0\" %>" />

<center><input class="pulsante" type = "button" name="chiudi" value="Chiudi" onclick="window.close()"/></center>
</af:form>
<br/>
</body>
</html>
