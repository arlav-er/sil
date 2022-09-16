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


<%
	String strDescCorso = (String)serviceRequest.getAttribute("DESCCORSO");

%>

<html>
<head>
<title>
 Lista Corsi-Ricerca avanzata
</title>
<link rel="stylesheet" href="../../css/stili.css" type="text/css">
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
  <af:linkScript path="../../js/" />
  
  <SCRIPT TYPE="text/javascript">
    function DettaglioSelect(id, desc, anno) {

      window.opener.document.Frm1.CODCORSO.value = id;
      window.opener.document.Frm1.DESCCORSO.value = desc.replace('^', '\'');
      if(anno!='0'){
    	  window.opener.document.Frm1.NUMANNO.value = anno;
      }
      window.close();
    }
  </SCRIPT>
</head>
<body class="gestione">
<af:form name="Frm1" method="POST" action="" dontValidate="true">
<!--onKeyPress="evitaEnter()"-->
<br/>
<p class="titolo"><b>Corsi - ricerca avanzata</b></p>

	<af:list moduleName="M_ListForProCorso" jsSelect="DettaglioSelect" />
	
</af:form>

</body>
</html>