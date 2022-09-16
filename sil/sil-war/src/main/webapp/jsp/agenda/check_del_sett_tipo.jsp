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

MODULE_NAME="MCHECKSLOTTIPO";
cont = (SourceBean) serviceResponse.getAttribute(MODULE_NAME);
row = (SourceBean) cont.getAttribute("ROW");
BigDecimal nro = (BigDecimal) row.getAttribute("NRO_SLOT");
%>
<html>
<head>
  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <link rel="stylesheet" href="../../css/listdetail.css" type="text/css">
  <af:linkScript path="../../js/"/>
  <title>Cancellazione Settimana Tipo</title>
  <script language="Javascript">
    function ok_del()
    {
	  // Se la pagina è già in submit, ignoro questo nuovo invio!
	  if (isInSubmit()) return;

      doFormSubmit(document.frm);
    }

    function no_del()
    {
	  // Se la pagina è già in submit, ignoro questo nuovo invio!
	  if (isInSubmit()) return;
      
      document.frm.MODULE.value = "";
      document.frm.MODULE.disabled = true;
      doFormSubmit(document.frm);
    }
  </script>
</head>
<%
String htmlStreamTop = StyleUtils.roundTopTable(false);
String htmlStreamBottom = StyleUtils.roundBottomTable(false);
%>
<body class="gestione"
<% if(nro.equals(new BigDecimal(0))) { %>
  onLoad="ok_del()"
<%}%>
>

<af:form action="AdapterHTTP" name="frm" method="POST" dontValidate="true">
<input type="hidden" name="PAGE" value="ListSettimanaTipoPage">
<input type="hidden" name="MODULE" value="MDelSettTipo">
<input type="hidden" name="PRGSETTIPO" value="<%=prgSettTipo%>">
<input type="hidden" name="CODCPI" value="<%=codCpi%>">

<%if(Integer.parseInt(nro.toString()) > 0) {%>
<br>
<p class="titolo">Attenzione!</p>

<%out.print(htmlStreamTop);%>
<table class="main">
<tr>
  <td align="justify">
  Sono gi&agrave; stati definiti degli slot tipo per la settimana-tipo &quot;<%=strDescrizioneSettimana%>&quot;.<br>
  L'operazione cancellerà anche queste definizioni, oltre a quella della settimana tipo
  </td>
</tr>
<tr><td>&nbsp;</td></tr>
<tr>
  <td align="center">
  <input type="button" class="pulsanti" value="Cancella" onClick="ok_del()">
  <input type="button" class="pulsanti" value="Annulla" onClick="no_del()">  
  </td>
</tr>
</table>
<%out.print(htmlStreamBottom);%>
<%}%>

</af:form>

</body>
</html>

