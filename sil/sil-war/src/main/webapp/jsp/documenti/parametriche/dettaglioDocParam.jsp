<%@page import="org.apache.poi.util.SystemOutLogger"%>
<%@ page  contentType="text/html;charset=utf-8"
	
	import="com.engiweb.framework.base.*,
			com.engiweb.framework.util.JavaScript,
			java.text.SimpleDateFormat,
			java.util.*,
			java.io.*,
			java.math.*,
			it.eng.sil.security.*,
			it.eng.sil.util.*,
			it.eng.sil.bean.*,
			it.eng.afExt.utils.*,
			it.eng.sil.Values,
			it.eng.sil.bean.protocollo.ProtocolloDocumentoUtil,
			it.eng.sil.module.movimenti.constant.Properties"
	extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage"%>
	
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../../global/noCaching.inc" %>
<%@ include file="../../global/getCommonObjects.inc" %>

<%
BigDecimal prgDocumentoIns = null;
boolean protocollazione = (serviceRequest.containsAttribute("GENERASTAMPA") && 
		  serviceRequest.getAttribute("GENERASTAMPA").toString().equalsIgnoreCase("STAMPA"));
boolean visualizzaStampa = (serviceRequest.containsAttribute("GENERASTAMPA") && 
		  serviceRequest.getAttribute("GENERASTAMPA").toString().equalsIgnoreCase("VISUALIZZA"));

boolean checkEsitoOK = 
	serviceResponse.containsAttribute("MElaboraStampaParametrica.ESITOELABORAZIONE")&&
	serviceResponse.getAttribute("MElaboraStampaParametrica.ESITOELABORAZIONE").toString().equalsIgnoreCase("OK");

String cdnLavoratore = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"cdnLavoratore");
String cdnFunzione = (String) serviceRequest.getAttribute("CDNFUNZIONE");
String queryString = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"PAGEBACK");

String htmlStreamTop = StyleUtils.roundTopTable(false);
String htmlStreamBottom = StyleUtils.roundBottomTable(false);
boolean visualizzaHtml = true;

%>

<%@ include file="CommonFunctionStampaParam.inc" %>

<%
if(visualizzaStampa && checkEsitoOK) {
	visualizzaHtml = false;
	%>
	<script language="Javascript">
	  var urlDoc = "AdapterHTTP?";
	  urlDoc += "PAGE=REPORTFRAMESTAMPAPAGE";
	  urlDoc += "&QUERY_STRING="+HTTPrequest;
	  //alert(urlDoc);
	  document.location=urlDoc;
	</script>
	<%
} else {
	if (protocollazione && checkEsitoOK) {
		prgDocumentoIns = (BigDecimal)serviceResponse.getAttribute("MElaboraStampaParametrica.PRGDOCUMENTO");
		if (prgDocumentoIns != null){
			visualizzaHtml = false;
			%>
			<script language="Javascript">
				visualizzaDocumento('DOWNLOAD','','<%=prgDocumentoIns%>');
			</script>
			<%
		}
	}
	
	if (visualizzaHtml) {%>
		<html>
		<head>
		
		
		<title>Dettaglio Errore Stampa Parametrica</title>
		<link rel="stylesheet" type="text/css" href="../../css/stili.css" />
		<link rel="stylesheet" type="text/css" href="../../css/listdetail.css" />
		<af:linkScript path="../../js/"/>
		
		<script language="Javascript">
			
		</script>
		
		</head>
		
		<body class="gestione">
		<%
			// TESTATA LAVORATORE
			if (StringUtils.isFilled(cdnLavoratore)) {
				InfCorrentiLav testata = new InfCorrentiLav(RequestContainer.getRequestContainer().getSessionContainer(), cdnLavoratore, user);
				testata.setSkipLista(true);
				testata.show(out);
			}
		%>
		
		<font color="red">
			<af:showErrors/>
		</font>
		<font color="green">
		  	<af:showMessages prefix="MElaboraStampaParametrica"/>
		</font>
		<br>
		<%
		out.print(htmlStreamTop);
		%>
		<div align="center">
			<table class="main" border="0">
				<tr>
				<td>
				<input class="pulsanti" type="button" name="btnIndietro" value="Torna indietro" onclick="tornaIndietro('<%=queryString%>');">
				</td>
				</tr>
			</table>
		</div>
		<%
		out.print(htmlStreamBottom);
		%>
		</body>
		</html>
	<%}
}%>