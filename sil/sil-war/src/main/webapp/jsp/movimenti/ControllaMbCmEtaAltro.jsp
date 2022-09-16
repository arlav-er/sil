<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file ="../global/noCaching.inc" %>
<%@ include file ="../global/getCommonObjects.inc" %>
 
<%@ page import="
                  com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.*,
                   
                  com.engiweb.framework.util.*,
                  it.eng.sil.module.movimenti.*,
                  it.eng.afExt.utils.*,
                  it.eng.sil.security.*,
                  it.eng.sil.util.*,
                  it.eng.sil.*,
                  java.util.*,
                  java.text.*,
                  java.math.*,
                  java.io.*,					                  
                  com.engiweb.framework.security.*" %>
 
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage"%>


<html>
<head>
<script type="text/javascript" src="../../js/movimenti/generale/DatiLavoratore.js" language="JavaScript"></script>
<script>
<!--

function avviaControllo() {	
	<%
      SourceBean datiLav =  (SourceBean)serviceResponse.getAttribute("M_GetMbCmEtaLav.rows.row");
	  String setDatiLav = it.eng.sil.module.movimenti.DatiSensibiliLavoratore.toJS(datiLav, "window.opener.");
	  out.print(setDatiLav);
	%>
	window.opener.controllaInfoLavoratore();
	window.close();		
}
//-->
</script>
</head>
<body onload="avviaControllo()"></body>
</html>