<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">

<%@ page import="com.engiweb.framework.base.*,
                com.engiweb.framework.security.*,
                java.lang.*,java.text.*,java.util.*,
                it.eng.afExt.utils.*, it.eng.sil.util.*,
                it.eng.sil.security.*" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%@ taglib uri="aftags" prefix="af"%>

<% 
  // NOTE: Attributi della pagina (pulsanti e link)
  PageAttribs attributi = new PageAttribs(user, "PattoLavDettaglioPage");
  boolean canDelete=attributi.containsButton("aggiorna");
%>

<html>
<head>

<!-- <link rel="stylesheet" type="text/css" href="../../css/stili.css"/>
 <link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>-->
<link rel="stylesheet" type="text/css" href="../../css/stili.css" />
 <link rel="stylesheet" type="text/css" href="../../css/listdetail.css" />
<title>Informazioni storiche patto/accordo lavoratore</title>
<af:linkScript path="../../js/" />
</head>

<body onload="checkError();">
<%
    Vector lista = (Vector)serviceResponse.getAttributeAsVector("M_PattoInformazioniStoriche.ROWS.ROW");
    if (lista.size()>0) {
        String nomeLav = (String)((SourceBean)lista.get(0)).getAttribute("STRNOME");
        String cognomeLav = (String)((SourceBean)lista.get(0)).getAttribute("STRCOGNOME");
        String codiceFiscale = (String)((SourceBean)lista.get(0)).getAttribute("STRCODICEFISCALE");            
%>
    <center><table class="lista">
    <tr>
  <td colspan="4"><%@ include file="../amministrazione/_testata_semplice_lavoratore.inc" %></td>
</tr>
<%--
        <tr><td width="50"></td><th class="lista">Nome&nbsp;</th><td width="20"></td><td class="lista" style="font-weight:bold;text-align:left"><%= nome %></td></tr>
        <tr><td></td><th class="lista">Cognome&nbsp;</th><td></td><td class="lista"  style="font-weight:bold;text-align:left"><%= cognome %></td></tr>
        <tr><td></td><th class="lista">Codice Fisc. &nbsp;</th><td></td><td class="lista"  style="font-weight:bold;text-align:left"><%= codiceFisc %></td></tr>
        --%>
    </table></center>
<%
    }
%>

<H2>Inf. storiche relative ai patti 150/accordi generici stipulati dal lavoratore</H2>


<af:error/>
     <af:list moduleName="M_PattoInformazioniStoriche" canDelete="<%= canDelete ? \"0\" : \"0\" %>"/>

     <center><button onclick="window.close()" class="pulsanti">Chiudi</button></center>
</body>
</html>
