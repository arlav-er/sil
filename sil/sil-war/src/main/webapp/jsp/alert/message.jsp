<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

        
<%@ page import="
                  com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.sil.security.*,
                  it.eng.sil.util.*,
                  it.eng.afExt.utils.StringUtils,
                  java.util.*,
                  java.math.*,
                  java.io.*,
                  com.engiweb.framework.security.*" %>

     
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">

<html lang="ita">
<head>
<% 
boolean utenteConvezionato = false;
SourceBean rowUt = (SourceBean)serviceResponse.getAttribute("checkUtenteConvenzione.rows.row");
if (rowUt != null) {
	String flgUtConvenzione=(String)rowUt.getAttribute("flgUtConvenzione");
	if (flgUtConvenzione != null && ("S").equalsIgnoreCase(flgUtConvenzione)) {
		utenteConvezionato = true;
	}
}

if(!utenteConvezionato){ 
%>
	<meta http-equiv="refresh" content="540">
<% 
}
%>	
	<link rel="stylesheet" href="../../css/stili.css" type="text/css">
	<script language="Javascript" src="../../js/utili.js" type="text/javascript"></script>
<% 
	SourceBean row = (SourceBean)serviceResponse.getAttribute("checkMessage.rows.row");
	if (row != null) {
		BigDecimal numeroMessaggi=(BigDecimal)row.getAttribute("numeromessaggi");
		
		if(numeroMessaggi != null && numeroMessaggi.intValue()!=0){ 
		//Apre la finestra di popup se ci sono dei messaggi da leggere
	%>
			<script>
			window.open("AdapterHTTP?PAGE=ViewAlertPage","Messaggi","toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,height=400,width=480" );
			</script>
	<%
		}
	}
%>
</head>
<body>
</body>
</html>
