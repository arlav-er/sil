<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

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


<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>
<%
	String agg_funz = StringUtils.getAttributeStrNotNull(serviceRequest, "AGGFUNZ");
	String aggiorna = agg_funz.equals("") ? "" : "window.opener." + agg_funz + "();";
%>
<html>
	<head>
		<link rel="stylesheet" href="../../css/stili.css" type="text/css">
		<af:linkScript path="../../js/"/>
	<SCRIPT language="JavaScript">

	</SCRIPT>

	</head>
	<body onload="<%=aggiorna%>" class="gestione">
		<CENTER>
			<font color="green">
				<af:showMessages prefix="CollegaMovimentoSuccessivo"/>
			</font>
			<font color="red">
				<af:showErrors />
			</font>
    		<input type="button" class="pulsanti" value="Chiudi" onClick="window.close()">	
    	</CENTER>
	</body>
</html>
