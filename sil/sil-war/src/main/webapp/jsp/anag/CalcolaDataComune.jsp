<%@ page contentType="text/html;charset=utf-8"%>
<%@ page import="com.engiweb.framework.base.*, 
                com.engiweb.framework.configuration.ConfigSingleton,
                 java.lang.*,
                java.text.*, java.util.*,it.eng.sil.util.*, 
                java.math.*, it.eng.sil.security.* "%>

<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%@ taglib uri="aftags" prefix="af"%>


<html>
<head>

<%
String strData="";
String strComune="";
String strCodCom="";
String strSessoCod="";
String strcodicefiscale="";
strcodicefiscale = (String)serviceRequest.getAttribute("strcodicefiscale");

SourceBean cont = (SourceBean) serviceResponse.getAttribute("M_InsertDataEComune");
SourceBean row = null;
row = (SourceBean) cont.getAttribute("ROWS.ROW");
boolean valid;
if (row != null) {
  strData=row.containsAttribute("data") ? row.getAttribute("data").toString() : "";
  strComune=row.containsAttribute("strdenominazione") ? row.getAttribute("strdenominazione").toString() : "";
  strSessoCod=row.containsAttribute("sesso") ? row.getAttribute("sesso").toString() : "";
  strCodCom=row.containsAttribute("codcom") ? row.getAttribute("codcom").toString() : "";
  valid = true;
}
else {
	String error = (String) cont.getAttribute("ERROR_ID");
	if (error != null) {
		valid = false;		
	}
	else  {
		valid = true;
	}
}
%>

<title>
<%if(valid){%>
	Calcola
<%} else{%>
	Alert
<%}%>
</title>

<link rel="stylesheet" href="../../css/stili.css" type="text/css">
<af:linkScript path="../../js/"/>
</head>



<body class="gestione">
<af:form name="Frm" dontValidate="true">
<input type="hidden" name="data" value="<%=strData%>">
<input type="hidden" name="comune" value="<%=strComune%>">
<input type="hidden" name="sesso" value="<%=strSessoCod%>">
<input type="hidden" name="codicecomune" value="<%=strCodCom%>">
<input type="hidden" name="valid" value="<%=valid%>">
</af:form>
<SCRIPT TYPE="text/javascript">
<%// if (valid) { %>
  opener.PrendiValori();
<%//} %>
</SCRIPT>
</body>
</html>