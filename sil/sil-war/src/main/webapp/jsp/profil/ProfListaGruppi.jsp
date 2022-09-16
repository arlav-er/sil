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
  String denominazione = (String) serviceRequest.getAttribute("STRDENOMINAZIONERIC");
  String tipoGruppo = (String) serviceRequest.getAttribute("TIPOGRUPPORIC");
  String flagStandard   = (String) serviceRequest.getAttribute("FLGSTANDARDRIC");
  String _page = (String) serviceRequest.getAttribute("PAGE"); 
  ProfileDataFilter filter = new ProfileDataFilter(user, _page);
  
  PageAttribs attributi = new PageAttribs(user, _page);
 
  
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
      
        var url = "AdapterHTTP?PAGE=ProfNuovoGruppoPage&cdnfunzione=<%=cdnFunzione%>&MODE=NEW";
        setWindowLocation(url);
      }
  </script>
</head>

<body onload="checkError();rinfresca();">
<input type="hidden" name="STRDENOMINAZIONERIC" value="<%=denominazione%>"/>
<input type="hidden" name="TIPOGRUPPORIC" value="<%=tipoGruppo%>"/>
<input type="hidden" name="FLGSTANDARDRIC" value="<%=flagStandard%>"/>

<af:error/>
<af:list moduleName="M_ProfListaGruppi"/>	

<% if (attributi.containsButton("nuovo")){%>
	 <table class="main">
		   <tr>
		    <td colspan="2" align="center">
		      <input class="pulsante" type="button" name="btnInserisci" value="Nuovo gruppo" onClick="nuovo()"/>
		    </td>
		   </tr>
   </table>
   <% }%>


</body>
</html>
<%}%>


