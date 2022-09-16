<!-- @author: Paolo Cavaciocchi - September 2003 -->
<%@ page contentType="text/html;charset=utf-8"%>
<%@ page import="com.engiweb.framework.base.*,
                com.engiweb.framework.security.*,
                java.lang.*,java.text.*,java.util.*,
                it.eng.afExt.utils.*, it.eng.sil.util.*,
                it.eng.sil.security.*" %>

<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%@ taglib uri="aftags" prefix="af" %>

<%

class Deapostrofa{
 	public String esegui(String str){
    String ret=new String();

 		for(int i = 0; i < str.length(); i++){
 		  if(str.charAt(i)=='\''){
        ret = ret + "''";
 		  }else{
 		    ret=ret + str.charAt(i);
 		  }
 		}
    return ret;
 	}
}

Deapostrofa de=new Deapostrofa();
%>

<%
// Inizializzazione
String prgazienda = "";
String strragionesociale = "";
String prgunita="";
String strindirizzo="";
String codcom="";

String moduleName = "LISTA_AZIENDE_UNITA_MOD";
SourceBean content = null;

content = (SourceBean) serviceResponse.getAttribute(moduleName);
%>

<html>
<head>
  <title>Lista Aziende Unità</title>
  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <af:linkScript path="../../js/" />
  <script type="text/javascript">
    function seleziona(prgazienda,prgunita){
      opener.document.frmNuovoAppuntamento.PRGAZIENDA.value = prgazienda;
      opener.document.frmNuovoAppuntamento.PRGUNITA.value = prgunita;      
      window.close();
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
    Aziende e relative unità
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
        <b>Attenzione non ci sono aziende!</b>
      </td>
    </tr>
<%
    }
    for(i=0; i < rows.size(); i++) {
      row = (SourceBean) rows.elementAt(i);
      prgazienda = row.getAttribute("PRGAZIENDA").toString();
      if (row.containsAttribute("PRGUNITA")) {
        prgunita = row.getAttribute("PRGUNITA").toString();
      }else{
        prgunita="";
      }
      if (row.containsAttribute("STRRAGIONESOCIALE")) {
        strragionesociale = row.getAttribute("STRRAGIONESOCIALE").toString();
        strragionesociale=de.esegui(strragionesociale);
      }else{
        strragionesociale="";
      }
      if (row.containsAttribute("STRRINDIRIZZO")) {
        strindirizzo = row.getAttribute("STRRINDIRIZZO").toString();
        strindirizzo=de.esegui(strindirizzo);        
      }else{
        strindirizzo="";
      }
      if (row.containsAttribute("CODCOM")) {
        codcom = row.getAttribute("CODCOM").toString();
      }else{
        codcom="";
      }
%>
    <tr>
      <td class="sottolineato" align="left">
      <a href="#" onClick="javascript:seleziona('<%=prgazienda%>','<%=prgunita%>');"><%=strragionesociale%></a></td>
      <td class="sottolineato" align="left"><%=strindirizzo%></td>
      <td class="sottolineato" align="left"><%=codcom%></td>      
    </tr>
<%
    }
} // if(content != null)
else {
%>
    <tr valign="middle">
      <td class="def" align="center"><img src="../../img/info.gif" alt="!"></td>
      <td class="def" align="center">
        <b>Attenzione non ci sono aziende!</b>
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
