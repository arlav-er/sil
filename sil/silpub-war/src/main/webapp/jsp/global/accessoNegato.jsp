<%@ page contentType="text/html;charset=utf-8"%>
<%@ page import="com.engiweb.framework.configuration.ConfigSingleton,
                 com.engiweb.framework.base.*,
                 com.engiweb.framework.dispatching.module.*,
                 com.engiweb.framework.error.*,
                 com.engiweb.framework.security.*,
                 it.eng.sil.util.*,
                 it.eng.sil.security.*,                 
                 java.util.*" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>
<%@ taglib uri="aftags" prefix="af" %>

<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%


	
	String referer= (String)requestContainer.getAttribute("referer");
	boolean canReturn=false;
	
//	if(referer!=null){
//		if (referer.toUpperCase().indexOf("PAGE=")<0){
//     		canReturn=false;
//		}

//	}


%>


<html>

<HEAD>
 <TITLE>Accesso negato</TITLE>
 <META HTTP-EQUIV="expires" CONTENT="0">
 <META HTTP-EQUIV="Cache-Control" CONTENT="no-cache">
 <META HTTP-EQUIV="pragma" CONTENT="private">

 <link REL="STYLESHEET" TYPE="text/css" HREF="../../css/stili.css">
<af:linkScript path="../../js/"/>


<% if (canReturn){ %>

<script language="Javascript">
	function indietro(){
	    // Se la pagina è già in submit, ignoro questo nuovo invio!
        if (isInSubmit()) return;
	
		var url = "<%=referer%>";
		setWindowLocation(url);
	}
</script>

<% } %>


</HEAD>

<body class="gestione" onLoad="rinfresca();">

<af:form dontValidate="true">
<p align="center">&nbsp;</p>
<p class="titolo">Accesso non consentito</p>
<p align="center">&nbsp;</p>
<p align="center"><img border="0" src="../../img/accessoNegato.gif"></p>
</af:form>

</body>
</html>
