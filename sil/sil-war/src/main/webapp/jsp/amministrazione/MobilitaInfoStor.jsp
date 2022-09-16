<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">

<%@ page import="com.engiweb.framework.base.*,
                com.engiweb.framework.security.*,
                java.lang.*,java.text.*,java.util.*,
                it.eng.afExt.utils.*, it.eng.sil.util.*,
                java.math.*,
                it.eng.sil.security.*" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%@ taglib uri="aftags" prefix="af"%>

<% 
  Vector rows = serviceResponse.getAttributeAsVector("M_GetDatiLav.ROWS.ROW"); 
  String nomeLav    = "";
  String cognomeLav = "";
  String codFis     = "";
  //Recupero dati Lavoratore
  if(rows != null && !rows.isEmpty())
  { SourceBean row    = (SourceBean) rows.elementAt(0);
    nomeLav    = (String) row.getAttribute("STRNOME");
    cognomeLav = (String) row.getAttribute("STRCOGNOME");
    codFis     = (String) row.getAttribute("STRCODICEFISCALE");
  }
  String cdnLavoratore= (String )serviceRequest.getAttribute("CDNLAVORATORE");	
  // NOTE: Attributi della pagina (pulsanti e link)
  PageAttribs attributi = new PageAttribs(user, "AmministrListeSpecPage");
  boolean canDelete = attributi.containsButton("CANCELLA");
%>

<html>
<head>
  <title>Inf Storiche Mobilita</title>
 <link rel="stylesheet" type="text/css" href=" ../../css/stili.css"/>
 <link rel="stylesheet" type="text/css" href=" ../../css/listdetail.css"/>
 <af:linkScript path="../../js/" />
</head>

<body onload="checkError();" class="gestione">
<af:showMessages prefix="M_DeleteMobilita"/> 
<af:error/>
<p align="center">
<table class="main">
<tr><td><br/></td></tr>
<tr><td>
  <div style="border-width: 1px; border-style: solid; text-color: #000080; border-color: #000080; background-color:#dcdcdc">
  &nbsp;&nbsp;Lavoratore: <b><%=cognomeLav%>&nbsp;<%=nomeLav%></b> &nbsp;&nbsp;codice&nbsp;fiscale: <b><%=codFis%></b>
  </div>
</td></tr>
</table>

<af:list moduleName="M_InfStorMobilita" canDelete="<%= canDelete ? \"1\" : \"0\" %>" configProviderClass="it.eng.sil.module.amministrazione.MobilitaInfoStorListConfig" />

<table class="main">
  <tr><td>&nbsp;</td></tr>
  <tr><td align="center"><input type="button" class="pulsanti" value="Chiudi" onClick="window.close()"></td></tr>
</table>


</body>
</html>