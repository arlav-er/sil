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
  String _page = (String) serviceRequest.getAttribute("PAGE"); 
  ProfileDataFilter filter = new ProfileDataFilter(user, _page);
  
  if (! filter.canView()){
	response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
  }else{

  String prgappuntamento=serviceRequest.getAttribute("PRGAPPUNTAMENTO").toString();
  String codcpi=serviceRequest.getAttribute("CODCPI").toString();  
  
%>

<html>
<head>
  <title>Lista Lavoratori</title>
 <link rel="stylesheet" type="text/css" href="../../css/stili.css"/>
 <link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
 <af:linkScript path="../../js/" />
</head>

<body class="gestione">
  <af:list moduleName="LISTA_AGENDA_LAVORATORI_MOD"/>
<table class="main" align="center">
<tr><td>&nbsp;</td></tr>
<tr>
  <td align="center">
    <input type="button" class="pulsanti" value="Chiudi" onClick="window.close()">
  </td>
</tr>
</table>

</body>
</html>
<%}%>