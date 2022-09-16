<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>


<%@ page import="com.engiweb.framework.base.*,
                com.engiweb.framework.security.*,
                java.lang.*,java.text.*,java.util.*,
                it.eng.afExt.utils.*, it.eng.sil.util.*,
                it.eng.sil.security.*" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<%
  String htmlStreamTop = StyleUtils.roundTopTable(false);
  String htmlStreamBottom = StyleUtils.roundBottomTable(false);
%>
<title>Cerca azienda</title>
<link rel="stylesheet" href="../../css/stili.css" type="text/css">
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
<af:linkScript path="../../js/"/>
</head>

<body class="gestione">
<af:form name="Frm1" method="POST" action="AdapterHTTP" >
<BR/>
<p align="center">
<%out.print(htmlStreamTop);%>
<table class="main"> 
      <tr><td>&nbsp;</td></tr>
      <tr><td colspan="2"><p class="titolo">Ricerca Aziende</p></td></tr>
      <tr><td>&nbsp;</td></tr>
      <tr>
        <td class="etichetta">Ragione Sociale</td>
        <td class="campo">
          <input type="text" name="RagioneSociale" value="" size="20" maxlength="100"/>
        </td>
      </tr>
      <tr>
        <td class="etichetta">Partita IVA</td>
        <td class="campo">
          <input type="text" name="piva" value="" size="20" maxlength="11"/>
        </td>
      </tr>
      <tr>
        <td class="etichetta">Codice Fiscale</td>
        <td class="campo">
          <input type="text" name="cf" value="" size="20" maxlength="16"/>
        </td>
      </tr>

      <tr><td colspan="2">&nbsp;</td></tr>
      <tr>
        <td colspan="2" align="center">
          <input class="pulsanti" type="submit" name="cerca" value="Cerca"/>&nbsp;&nbsp;
          <input type="button" class="pulsanti" value="Chiudi" onClick="window.close();"/>
        </td>
      </tr>
      <tr><td>&nbsp;</td></tr>
</table>
<%out.print(htmlStreamBottom);%>
         <input type="hidden" name="PAGE" value="ListaAziendePage"/>
</af:form>
</body>
</html>
