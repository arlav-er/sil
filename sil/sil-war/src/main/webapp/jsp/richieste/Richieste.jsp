<!-- @author: Paolo Roccetti - Maggio 2004 -->
<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="
                  com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.*,
                  com.engiweb.framework.tracing.*,
                  com.engiweb.framework.util.*,
                  it.eng.sil.module.movimenti.*,
                  it.eng.sil.module.*,
                  it.eng.afExt.utils.*,
                  it.eng.sil.security.*,
                  it.eng.sil.util.*,
                  it.eng.sil.*,
                  java.util.*,
                  java.math.*,
                  java.io.*,
                  com.engiweb.framework.security.*" %>


<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%  
  // NOTE: Attributi della pagina (pulsanti e link) 
  PageAttribs attributi = new PageAttribs(user, (String) serviceRequest.getAttribute("PAGE"));
  boolean canModify = true;
  String _funzione = (String) serviceRequest.getAttribute("CDNFUNZIONE");

  //Estrazione degli identificatori di richiesta utente
  String serviceid = StringUtils.getAttributeStrNotNull(serviceResponse, "M_MOVVALIDAINTERATTIVA.SERVICECONTEXTID");
  String requestid = StringUtils.getAttributeStrNotNull(serviceResponse, "M_MOVVALIDAINTERATTIVA.REQUESTCONTEXTID");
  
  //parametro di prova
  String value = StringUtils.getAttributeStrNotNull(serviceResponse, "M_MOVVALIDAINTERATTIVA.PARAMETRI.PARAM1");

%>

<html>
  <head>
    <link rel="stylesheet" href="../../css/stili.css" type="text/css">
    <af:linkScript path="../../js/"/>
    <title>Richiesta di Informazioni dal sistema</title>

    <script language="Javascript">
    <% 
      //Genera il Javascript che si occuperà di inserire i links nel footer
      attributi.showHyperLinks(out, requestContainer,responseContainer,"");
    %>
    <!--

    -->
    </SCRIPT>
  </head>

  <body class="gestione" onload="rinfresca();">
    <center>
      <af:form name="Frm1" method="POST" action="AdapterHTTP">
      	Modificare il parametro e premere il pulsante
        <af:textBox classNameBase="input" type="text" name="RESP1" required="true" title="Dato immesso dall'utente" size="12" value="<%=value%>"/>
        <input type="hidden" name="PAGE" value="MovValidaInterattivaPage"/>
        <input type="hidden" name="CDNFUNZIONE" value="<%=_funzione%>"/>
        <input type="hidden" name=SERVICECONTEXTID value="<%=serviceid%>"/>
        <input type="hidden" name=REQUESTCONTEXTID value="<%=requestid%>"/>        
        <input type="submit" class="pulsanti" name="submitbutton" value="Fatto!"/>
      </af:form>
    </center>

      
	</body>
</html>  

