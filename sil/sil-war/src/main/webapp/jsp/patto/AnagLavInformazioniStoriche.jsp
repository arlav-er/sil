<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">

<%@ page import="com.engiweb.framework.base.*,
                com.engiweb.framework.security.*,
                java.lang.*,java.text.*,java.util.*,
                it.eng.afExt.utils.*, it.eng.sil.util.*,
                it.eng.sil.security.*,
                it.eng.sil.module.movimenti.InfoLavoratore,
                java.math.BigDecimal,java.util.*"%>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%@ taglib uri="aftags" prefix="af"%>

<% 
  // NOTE: Attributi della pagina (pulsanti e link)
  PageAttribs attributi = new PageAttribs(user, "ElAnagDettaglioPage");
  boolean canDelete=attributi.containsButton("aggiorna");
%>


<html>
<head><title>Ricerca Informazioni storiche Anagrafica Lavoratore</title>

<!-- <link rel="stylesheet" type="text/css" href="../../css/stili.css"/>
 <link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>-->
<link rel="stylesheet" type="text/css" href="../../css/stili.css" />
 <link rel="stylesheet" type="text/css" href="../../css/listdetail.css" />

<af:linkScript path="../../js/" />
 
</head>

<body onload="checkError();">
<af:error/>
<%
    //Vector lista = (Vector)serviceResponse.getAttributeAsVector("M_ElAnagLavInformazioniStoriche.ROWS.ROW");
    //if (lista.size()>0) {
    String cdnLavoratore = (String)serviceRequest.getAttribute("CDNLAVORATORE");
    InfoLavoratore _lav = new InfoLavoratore( new BigDecimal(cdnLavoratore) );
    String nomeLav = _lav.getNome(); //(String)((SourceBean)lista.get(0)).getAttribute("STRNOME");
    String cognomeLav = _lav.getCognome(); //(String)((SourceBean)lista.get(0)).getAttribute("STRCOGNOME");
    String codiceFiscale = _lav.getCodFisc(); //(String)((SourceBean)lista.get(0)).getAttribute("STRCODICEFISCALE");       
%>
<center>
<table class="lista">
<tr>
  <td colspan="4"><%@ include file="../amministrazione/_testata_semplice_lavoratore.inc" %></td>
</tr>
</table>
</center>
<%
    //}
%>
<H2>Inf. storiche relative all' inserimento nell' elenco anagrafico del lavoratore</H2>

     <af:list moduleName="M_ElAnagLavInformazioniStoriche" canDelete="<%= canDelete ? \"1\" : \"0\" %>" />

<center><button onclick="window.close()" class="pulsanti">Chiudi</button></center>

</body>
</html>
