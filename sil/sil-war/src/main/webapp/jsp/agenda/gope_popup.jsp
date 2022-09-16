<!-- @author: Stefania Orioli - Jan 2004 -->
<%@ page contentType="text/html;charset=utf-8"%>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>
<%@ page import="com.engiweb.framework.base.*,
                 
                com.engiweb.framework.security.*,
                java.lang.*,java.text.*,
                java.util.Vector,java.util.*,
                it.eng.sil.util.*,it.eng.sil.security.*"
%>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%@ taglib uri="aftags" prefix="af" %>

<%
// Debug
//String qs = QueryString.GetQueryString((SourceBean) serviceRequest);
%>

<%
// Inizializzazione
String strCognome = "";
String strNome = "";
String moduleName = "MGRUPPOOPE_POPUP";
SourceBean content = null;

content = (SourceBean) serviceResponse.getAttribute(moduleName);
%>
<%
String htmlStreamTop = StyleUtils.roundTopTable(false);
String htmlStreamBottom = StyleUtils.roundBottomTable(false);
%>
<html>
<head>
  <title>Gruppo Lavoratori</title>
  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <af:linkScript path="../../js/" />
</head>
<body class="gestione">
<br>
<h2>Operatori impegnati nell'appuntamento/slot</h2>
<table class="lista" align="center">
<%
if(content != null) {
%>

<!--tr>
    <td class="bordato" align="center" colspan="2">
    Operatori impegnati nell'appuntamento/slot
    </td>
</tr-->
<%
    Vector rows = content.getAttributeAsVector("ROWS.ROW");
    SourceBean row = null;
    int i;
    if(rows.size()==0) {
%>
    <tr valign="middle">
      <td class="def" align="center"><img src="../../img/info.gif" alt="!"></td>
      <td class="def" align="center">
        <b>Attenzione non ci sono operatori per questo appuntamento/slot!</b>
      </td>
    </tr>
<%
    }
%>
    <tr>
      <th class="lista">Cognome</th>
      <th class="lista">Nome</th>
    </tr>

<%
    String classRow = "";
    boolean pd = true;
    for(i=0; i < rows.size(); i++) {
      row = (SourceBean) rows.elementAt(i);
      if(pd==true) { pd = false; classRow = "lista_dispari"; }
      else { pd = true; classRow = "lista_pari"; }
      strCognome = row.getAttribute("strCognome").toString();
      strNome = row.getAttribute("strNome").toString();
%>
    <tr>
      <td class="<%=classRow%>" align="left"><%=strCognome%></td>
      <td class="<%=classRow%>" align="left"><%=strNome%></td>
    </tr>
<%
    }
} // if(content != null)
else {
%>
    <tr valign="middle">
      <td class="def" align="center"><img src="../../img/info.gif" alt="!"></td>
      <td class="def" align="center">
        <b>Attenzione non ci sono lavoratori prenotati per questo appuntamento!</b>
      </td>
    </tr>
<%
}
%>
<tr><td class="def" colspan="2">&nbsp;</td></tr>
<tr>
    <td class="def" colspan="2" align="center">
    <input type="button" class="pulsanti" value="&nbsp;CHIUDI&nbsp;"
     onClick="window.close();">
    </td>
</tr>
</table>
<%out.print(htmlStreamBottom);%>
<%//out.print(serviceResponse.toXML());%>
</body>
</html>
