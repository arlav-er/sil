<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">

<%@ page import="com.engiweb.framework.base.*,
                 it.eng.sil.util.*,
                 it.eng.sil.security.*,
                 java.lang.*,
                 java.text.*,
                 java.util.*,
                 it.eng.afExt.utils.*" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%@ taglib uri="aftags" prefix="af"%>

<%-- 

  Vector rows = serviceResponse.getAttributeAsVector("M_InfStorPermSogg.ROWS.ROW");
  String nomeLav    = "";
  String cognomeLav = "";
  String codFis     = "";
  if(rows != null && !rows.isEmpty())
  { SourceBean row    = (SourceBean) rows.elementAt(0);
    nomeLav    = (String) row.getAttribute("NOME");
    cognomeLav = (String) row.getAttribute("COGNOME");
    codFis     = (String) row.getAttribute("CF");
  }

  // NOTE: Attributi della pagina (pulsanti e link)
  PageAttribs attributi = new PageAttribs(user, "AmministrPermSoggPage");
  boolean canDelete=attributi.containsButton("aggiorna");
--%>
<%
    boolean canDelete = true;
%>
<html>
<head>
  <title>Inf Storiche Privacy</title>
 <link rel="stylesheet" type="text/css" href="../../css/stili.css"/>
 <link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
 <af:linkScript path="../../js/" />
</head>

<body onload="checkError();" class="gestione">
<af:error/>
<p align="center">
<table class="main">
<tr><td><br/></td></tr>

</table>

<af:list moduleName="M_LISTAPRIVACYSTORICO" canDelete="<%= canDelete ? \"1\" : \"0\" %>" />

<table class="main">
  <tr><td>&nbsp;</td></tr>
  <tr><td align="center"><input type="button" class="pulsanti" value="Chiudi" onClick="window.close()"></td></tr>
</table>


</body>
</html>