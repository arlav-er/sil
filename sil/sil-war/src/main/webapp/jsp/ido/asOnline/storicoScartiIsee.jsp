<%@ page contentType="text/html;charset=utf-8"%>
<%@ include file="../../global/noCaching.inc"%>
<%@ include file="../../global/getCommonObjects.inc" %>

<%@ page
    import="com.engiweb.framework.base.*,
                com.engiweb.framework.security.*,
                java.lang.*,java.math.*,java.text.*,java.util.*,
                it.eng.afExt.utils.*, it.eng.sil.util.*,
                it.eng.sil.security.*"%>
                
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>  
<%@ taglib uri="aftags" prefix="af" %>


<%
String strCdnLavoratore = serviceRequest.getAttribute("CDNLAVORATORE").toString();

String _page = (String) serviceRequest.getAttribute("PAGE");

ProfileDataFilter filter = new ProfileDataFilter(user, _page);


if (!filter.canView()) {
    response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
    return;
}

%>

<html>
<head>
<title>Storico Scarti ISEE Istanze Online</title>
<link rel="stylesheet" href="../../css/stili.css" type="text/css">
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
<af:linkScript path="../../js/" />
</head>
<body class="gestione">



    <af:list moduleName="M_GetScartiIsee"/>

 
<center>
<table><tr><td align="center">
<input type="button" class="pulsanti" name="buttChiudi" value="Chiudi" onClick="window.close()">
</td></tr></table>
</center>
</body>
</html>

