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

String MODULE_NAME="MDESCRSETTTIPO";
SourceBean cont = (SourceBean) serviceResponse.getAttribute(MODULE_NAME);
SourceBean row = (SourceBean) cont.getAttribute("ROW");
String strDescrizioneSettimana = StringUtils.getAttributeStrNotNull(row, "STRDESCRIZIONESETTIMANA");

int numGiornoSett = Integer.parseInt(serviceRequest.getAttribute("NUMGIORNOSETT").toString());
String aGiorni[] = {"", "Luned&igrave;", "Marted&igrave;", "Mercoled&igrave;", "Gioved&igrave;", "Venerd&igrave;", "Sabato", "Domenica" };

String htmlText = ShowApp.repGiornoTipo(requestContainer, serviceResponse);
%>
<html>
<head>
  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <link rel="stylesheet" href="../../css/listdetail.css" type="text/css">
  <af:linkScript path="../../js/"/>
  <title>Definizione Settimana Tipo - Dettaglio Giorno</title>
</head>

<body class="gestione"  onload="rinfresca()">
<br>
<p class="titolo">
Definizione Settimana Tipo: &quot;<%=strDescrizioneSettimana%>&quot;
<br>
<%out.print(aGiorni[numGiornoSett]);%>
</p>

<p align="center">
<!--table class="main">
<tr>
  <td class="etichetta">prgSettTipo</td>
  <td class="campo"><%=prgSettTipo%></td>
</tr>
<tr>
  <td class="etichetta">codCpi</td>
  <td class="campo"><%=codCpi%></td>
</tr>
<tr>
  <td class="etichetta">descrizione</td>
  <td class="campo"><%=strDescrizioneSettimana%></td>
</tr>
</table-->
<%out.print(htmlText);%>
</p>
<div align="center">
<af:form action="AdapterHTTP" name="form" method="POST" dontValidate="true">
<input type="hidden" name="PAGE" value="VSettTipoPage">
<input type="hidden" name="PRGSETTIPO" value="<%=prgSettTipo%>">
<input type="hidden" name="CODCPI" value="<%=codCpi%>">
<input type="submit" class="pulsanti" value="Torna alla definizione della Settimana Tipo">
</af:form>
</div>

</body>
</html>

