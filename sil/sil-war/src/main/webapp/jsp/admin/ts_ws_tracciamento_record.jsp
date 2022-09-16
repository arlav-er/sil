<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>

<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc"%>

<%@ page import="
  com.engiweb.framework.base.*,
  
  it.eng.sil.security.User,
  it.eng.afExt.utils.*,
  it.eng.sil.util.*,
  it.eng.sil.security.PageAttribs,
  java.math.*,
  it.eng.sil.security.*" %>
  
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %> 
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%
String _page = (String) serviceRequest.getAttribute("PAGE"); 
ProfileDataFilter filter = new ProfileDataFilter(user, _page);

	if (! filter.canView()){
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
	}

String htmlStreamTop = StyleUtils.roundTopTable(false);
String htmlStreamBottom = StyleUtils.roundBottomTable(false);

String prgWsTracciamento = StringUtils.getAttributeStrNotNull(serviceRequest,"prgwstracciamento");

boolean viewSoap = "true".equalsIgnoreCase(StringUtils.getAttributeStrNotNull(serviceRequest,"viewsoap"));

SourceBean result = (SourceBean) serviceResponse.getAttribute("TS_WS_TRACCIAMENTO_MOD");
 
 
String xml = (String) result.getAttribute("ROWS.ROW.STRMESSAGGIOSOAP");
 
%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="../../css/stili.css"/>
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>

<af:linkScript path="../../js/"/>

<script type="text/Javascript">
  function tornaAllaLista()
  {  // Se la pagina è già in submit, ignoro questo nuovo invio!
     window.close();
  }

  function vediTutto() {    
	  if (isInSubmit()) return;
  
	  url="AdapterHTTP?PAGE=WS_TRACCIAMENTO_RECORD_PAGE";
	  url += "&prgwstracciamento="+"<%=prgWsTracciamento%>";
	  url += "&viewSoap=true";  
	  setWindowLocation(url);
  }

  function vediDati() {    
	  if (isInSubmit()) return;
  
	  url="AdapterHTTP?PAGE=WS_TRACCIAMENTO_RECORD_PAGE";
	  url += "&prgwstracciamento="+"<%=prgWsTracciamento%>";  
	  setWindowLocation(url);
  }
</script>

<STYLE type="text/css">
   .miaClass {
   	text-align: left
   }
 </STYLE>




<title>Dettaglio del record <%=prgWsTracciamento %></title>
</head>
<body class="gestione">
<%if (viewSoap) { %>
<p class="titolo">Dettaglio del record (messaggio SOAP)</p>
<%}
else {%>
<p class="titolo">Dettaglio del record (contenuto dati)</p>
<%} %>
 <%out.print(htmlStreamTop);%>
<af:form className="miaClass">
	<%if (xml != null) {
		out.print(xml);
	}
	else {
		out.print("Non sono presenti dati all'interno del messaggio SOAP.");
	}
	%>

<br/>
<hr/>	

<input type="button" onclick="tornaAllaLista()" value="Chiudi">
<%if (viewSoap) { %>
	<input type="button" onclick="vediDati()" value="Visualizza solo dati">
<%} 
else {%>
	<input type="button" onclick="vediTutto()" value="Visualizza messaggio SOAP">
<%} %>
</af:form>
 <%out.print(htmlStreamBottom);%>
</body>
</html>