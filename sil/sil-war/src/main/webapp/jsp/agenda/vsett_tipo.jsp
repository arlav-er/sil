<!-- @author: Stefania Orioli -->
<%@ page contentType="text/html;charset=utf-8"%>
<%@ include file="../global/noCaching.inc"%>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="com.engiweb.framework.base.*,
                com.engiweb.framework.base.SourceBean,
                 
                com.engiweb.framework.security.*,java.math.*,
                java.lang.*,java.text.*,java.util.*, it.eng.sil.security.User,
                it.eng.sil.util.*, it.eng.sil.module.agenda.ShowApp,
                it.eng.afExt.utils.*"
%>


<%@ taglib uri="aftags" prefix="af" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
String prgSettTipo = (String) serviceRequest.getAttribute("PRGSETTIPO");
String codCpi = (String) serviceRequest.getAttribute("CODCPI");

String MODULE_NAME = "MDESCRSETTTIPO";
SourceBean cont = (SourceBean) serviceResponse.getAttribute(MODULE_NAME);
SourceBean row = (SourceBean) cont.getAttribute("ROW");
String strDescrizioneSettimana = StringUtils.getAttributeStrNotNull(row, "STRDESCRIZIONESETTIMANA");

MODULE_NAME = "MCOPIAGIORNOTIPO";
cont = (SourceBean) serviceResponse.getAttribute(MODULE_NAME);
if(cont!= null) { row = (SourceBean) cont.getAttribute("ROW"); }
else { row = null; }
String codiceRit = "";
int i = 0;
String msg = "";
if(row != null) {
	codiceRit = StringUtils.getAttributeStrNotNull(row, "CODICERIT");
	if(!codiceRit.equals("")) { i = Integer.parseInt(codiceRit); }
	switch(i) {
		case 0 : 
					msg = "";
					break;
		case 1 :
					msg = "Il giorno selezionato come destinazione non &egrave; vuoto";
					break;
		case -1 :
					msg = "Si &egrave; verificato un errore durante la copia";
					break;
		default :
					msg = "";
					break;
	}
}


String htmlText = ShowApp.repSettimanaTipo(requestContainer, serviceResponse);
%>
<html>
<head>
  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <link rel="stylesheet" href="../../css/listdetail.css" type="text/css">
  <af:linkScript path="../../js/"/>
  <title>Definizione Settimana Tipo</title>
</head>

<body class="gestione"  onload="rinfresca()">
<%if(!msg.equals("")) {%>
	<ul><li><%=msg%></li></ul>
<%}%>
<br>
<p class="titolo">Definizione Settimana Tipo: &quot;<%=strDescrizioneSettimana%>&quot;</p>

<p class="centrato">
<%out.print(htmlText);%>
</p>
<!-- COPIA GIORNO -->
<div align="center">
<af:form action="AdapterHTTP" name="frmCopia" method="POST" dontValidate="true">
<input type="hidden" name="PAGE" value="CopiaGiornoTipoPage">
<input type="hidden" name="PRGSETTIPO" value="<%=prgSettTipo%>">
<input type="hidden" name="CODCPI" value="<%=codCpi%>">
<input type="submit" class="pulsanti" value="Copia Definizione Giorno">
</af:form>
</div>
<!-- BACK -->
<div align="center">
<af:form action="AdapterHTTP" name="form" method="POST" dontValidate="true">
<input type="hidden" name="PAGE" value="ListSettimanaTipoPage">
<input type="submit" class="pulsanti" value="Torna alla lista">
</af:form>
</div>

</body>
</html>

