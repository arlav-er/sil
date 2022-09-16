<!-- @author: Paolo Roccetti - Gennaio 2004 -->
<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="
                  com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.sil.security.*,
                  java.util.*,
                  java.math.*,
                  java.io.*,
                  com.engiweb.framework.security.*" %>


<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
 // NOTE: Attributi della pagina (pulsanti e link) 
  PageAttribs attributi = new PageAttribs(user, "MovimentiRicercaPage");

 String _funzione=(String) serviceRequest.getAttribute("CDNFUNZIONE");
%>

<html>
<head>
  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <af:linkScript path="../../js/"/>
  <title>Risultato della Importazione</title>

  
  <script language="Javascript">
     <% 
     	//Genera il Javascript che si occuperà di inserire i links nel footer
        attributi.showHyperLinks(out, requestContainer,responseContainer,"");
      %>
  </script>

  
</head>


<body class="gestione">

  <br/>
  <p class="titolo">Importa Movimenti</p>
  <br/>

    <font color="red">
      <af:showErrors/>
    </font>
<%out.print(serviceResponse.toXML());%>

       <af:form method="POST" action="AdapterHTTP" dontValidate="true">
        <input class="pulsanti" type="submit" name="altraricerca" value="Altra ricerca"/>
        <input type="hidden" name="PAGE" value="MovimentiRicercaPage"/>
        <input type="hidden" name="cdnFunzione" value="<%=_funzione%>"/>
	   </af:form>

</body>
</html>
