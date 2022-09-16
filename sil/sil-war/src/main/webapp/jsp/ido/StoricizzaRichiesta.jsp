<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="com.engiweb.framework.base.*,
                com.engiweb.framework.base.SourceBean,
                 
                com.engiweb.framework.security.*,java.math.*,
                java.lang.*,java.text.*,java.util.*, it.eng.sil.security.User,
                it.eng.sil.util.*, it.eng.sil.module.agenda.ShowApp,
                it.eng.afExt.utils.*"
%>

      
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>


<%

String MODULE_NAME = "M_StoricizzaRichiesta";
String msgStoricizzaRichiesta = "";
if(serviceResponse.containsAttribute(MODULE_NAME)) {
  SourceBean cont = (SourceBean) serviceResponse.getAttribute(MODULE_NAME);
  SourceBean rowAllineamento = (SourceBean) cont.getAttribute("ROW");
  String CodiceRit = "";
  if(rowAllineamento!=null) { CodiceRit = StringUtils.getAttributeStrNotNull(rowAllineamento, "CodiceRit"); }
  if(!CodiceRit.equals("-1")) { msgStoricizzaRichiesta = "Storicizza Richiesta non riuscito"; }

    else {  msgStoricizzaRichiesta = "Storicizza Richiesta avvenuto con successo"; }
  }
 
%>

<html>
<HEAD>
  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <link rel="stylesheet" href="../../css/listdetail.css" type="text/css">
  <af:linkScript path="../../js/"/>
  <title>Storicizza Richiesta</title>
</head>

<body class="gestione" onLoad="rinfresca()">
<p class="titolo">Storicizza Richiesta</p>
<p align="left">
<%if(!msgStoricizzaRichiesta.equals("")) {%>
  <ul><li><%=msgStoricizzaRichiesta%></li></ul>
<%}%>
<!--/p-->

</body>
</html>