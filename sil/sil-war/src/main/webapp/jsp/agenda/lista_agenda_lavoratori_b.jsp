<!-- @author: Paolo Cavaciocchi - September 2003 -->
<%@ page contentType="text/html;charset=utf-8"%>

<%@ page import="com.engiweb.framework.base.*,
                 
                com.engiweb.framework.security.*,
                java.lang.*,java.text.*,
                java.util.*,
                it.eng.sil.util.*,
                it.eng.afExt.utils.*, it.eng.sil.util.*,
                it.eng.sil.security.*" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>
<%@ include file="../global/apice.inc" %>

<%@ taglib uri="aftags" prefix="af" %>

<%
Deapostrofa de=new Deapostrofa();
%>

<%
// Inizializzazione
String cdnlavoratore = "";
String strcodicefiscale = "";
String strcognome="";
String strnome="";

String moduleName = "LISTA_AGENDA_LAVORATORI_MOD";
SourceBean content = null;

content = (SourceBean) serviceResponse.getAttribute(moduleName);
%>

<html>
<head>
  <title>Lista Agenda Lavoratori</title>
  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <af:linkScript path="../../js/" />
  
  <script type="text/javascript">
    function ritorna(cdnlavoratore,strcognome,strnome){
      opener.frmNuovoAppuntamento.CDNLAVORATORE.focus();
      opener.frmNuovoAppuntamento.CDNLAVORATORE.value=cdnlavoratore;
      opener.frmNuovoAppuntamento.NOMELAVORATORE.value=strcognome + " " + strnome;
      opener.frmNuovoAppuntamento.select1.focus();
    }
  </script>
</head>
<body class="gestione">
<br>
<table class="def" align="center">
<%
if(content != null) {
%>

<tr>
    <td class="bordato" align="center" colspan="2">
    <textarea rows=5 cols=80><%=serviceResponse.toXML()%></textarea></td>
</tr>

<tr>
    <td class="bordato" align="center" colspan="2">
    Lavoratori associati all'appuntamento
    </td>
</tr>
<%
    Vector rows = content.getAttributeAsVector("ROWS.ROW");
    SourceBean row = null;
    int i;
    if(rows.size()==0) {
%>
    <tr valign="middle">
      <td class="def" align="center"><img src="../../img/info.gif" alt="!"></td>
      <td class="def" align="center">
        <b>Attenzione non ci sono lavoratori per questo appuntamento!</b>
      </td>
    </tr>
<%
    }
    for(i=0; i < rows.size(); i++) {
      row = (SourceBean) rows.elementAt(i);
      cdnlavoratore = row.getAttribute("CDNLAVORATORE").toString();
      if (row.containsAttribute("STRCODICEFISCALE")) {
        strcodicefiscale = row.getAttribute("STRCODICEFISCALE").toString();
        strcodicefiscale = de.esegui(strcodicefiscale);        
      }else{
        strcodicefiscale="";
      }
      if (row.containsAttribute("STRCOGNOME")) {
        strcognome = row.getAttribute("STRCOGNOME").toString();
        strcognome = de.esegui(strcognome);                
      }else{
        strcognome="";
      }
      if (row.containsAttribute("STRNOME")) {
        strnome = row.getAttribute("STRNOME").toString();
        strnome = de.esegui(strnome);       
      }else{
        strnome="";
      }
%>
    <tr>
      <td class="sottolineato" align="left">
      <a href="#" onClick="javascript:ritorna('<%=cdnlavoratore%>','<%=strcognome%>','<%=strnome%>');"><%=strcodicefiscale%></a></td>
      <td class="sottolineato" align="left"><%=strcognome%></td>
      <td class="sottolineato" align="left"><%=strnome%></td>      
    </tr>
<%
    }
} // if(content != null)
else {
%>
    <tr valign="middle">
      <td class="def" align="center"><img src="../../img/info.gif" alt="!"></td>
      <td class="def" align="center">
        <b>Attenzione non ci sono lavoratori per questo appuntamento!</b>
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
<%//out.print(serviceResponse.toXML());%>
</body>
</html>
