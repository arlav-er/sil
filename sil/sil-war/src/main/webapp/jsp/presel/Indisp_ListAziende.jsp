<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">

<%@ page import="com.engiweb.framework.base.*,
                 java.lang.*,
                 java.text.*,
                 java.util.*, 
                 it.eng.afExt.utils.StringUtils,
                 it.eng.sil.security.*"%>
            
<%@ taglib uri="aftags" prefix="af"%>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc"%>
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<html>
<head>
<title>
Aziende-Ricerca avanzata
</title>
<link rel="stylesheet" href="../../css/stili.css" type="text/css">
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
<af:linkScript path="../../js/" />

<SCRIPT TYPE="text/javascript">
<!--
 function DettaglioSelect(desc) {
      window.opener.Frm1.STRRAGIONESOCIALE.value= desc.replace(/\^/g, '\'').replace(/\|/g, '\"');
      window.close();
    }

-->
</SCRIPT>
</head>
<body class="gestione">
<af:list moduleName="M_ListAziende" jsSelect="DettaglioSelect" />

</body>
</html>

