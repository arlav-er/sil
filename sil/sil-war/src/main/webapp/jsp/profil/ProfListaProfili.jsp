<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="
                  com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.sil.security.*,
                  it.eng.sil.util.*,
                  java.util.*,
                  java.math.*,
                  java.io.*,
                  com.engiweb.framework.security.*" %>

      
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
  String cdnFunzione = (String) serviceRequest.getAttribute("cdnfunzione");
  String _page = (String) serviceRequest.getAttribute("PAGE"); 
  ProfileDataFilter filter = new ProfileDataFilter(user, _page);
  
	if (! filter.canView()){
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
	}else{
  
%>

<html>
<head>

<link rel="stylesheet" type="text/css" href="../../css/stili.css" />
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css" />
    <af:linkScript path="../../js/"/>
  <script language="javascript">
      function nuovo(){
      	// Se la pagina è già in submit, ignoro questo nuovo invio!
		if (isInSubmit()) return;
      
        var url = "AdapterHTTP?PAGE=ProfDettaglioProfiloPage&NUOVO=nuovo&cdnfunzione=<%=cdnFunzione%>";
        setWindowLocation(url);
      }
  </script>
</head>

<body onload="checkError();rinfresca();">
<af:error/>
     <af:list moduleName="ProfListaProfili"/>	

  <%
      // GESTIONE ATTRIBUTI....
       //String _page = (String) serviceRequest.getAttribute("PAGE");
       PageAttribs attributi = new PageAttribs(user, _page);
        
  %>

        <p align="center">
            <%if (attributi.containsButton("nuovo")){%>
                <input class="pulsante" type="button" VALUE="Nuovo profilo" onClick="nuovo()"/>
            <%}%>
      </p> 

</body>
</html>
<%}%>


