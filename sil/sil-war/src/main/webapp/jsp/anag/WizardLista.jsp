<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">

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
  PageAttribs attributi = new PageAttribs(user, "AnagRicercaPage");
  boolean canInsert = attributi.containsButton("nuovo");
  boolean canDelete=attributi.containsButton("rimuovi");
%>

<html>
<head>
  <title>Risultati della ricerca</title>
 <link rel="stylesheet" type="text/css" href="../../css/stili.css"/>
 <link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
 <af:linkScript path="../../js/" />
</head>

<body onload="checkError();" class="gestione">
<af:error/>
<af:list moduleName="M_COOP_GETLAVORATOREIR" 
		 canDelete="<%= canDelete ? \"1\" : \"0\" %>" 
		 canInsert="<%= canInsert ? \"1\" : \"0\" %>" 
		 skipNavigationButton="1" 		 
/>

<!--<af:form method="POST" action="AdapterHTTP" dontValidate="true">
<input type="submit" name="inserisci" value="Inserisci un nuovo iscritto"/>
<input type="hidden" name="PAGE" value="AnagDettaglioPage"/>
<input type="hidden" name="flag_insert" value="1"/>
</af:form> -->
<%//out.print(serviceResponse.toXML());%>
</body>
</html>
