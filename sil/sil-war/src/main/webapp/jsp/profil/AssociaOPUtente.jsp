<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">

<%@ page import="com.engiweb.framework.base.*,
                 java.lang.*,
                 java.text.*,
                 java.util.*,
                 it.eng.sil.security.User,
                 it.eng.afExt.utils.StringUtils"%>
<%@ taglib uri="aftags" prefix="af"%>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc"%>
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>
<%

	String nome=(String) serviceRequest.getAttribute("nome");
	String cognome=(String) serviceRequest.getAttribute("cognome");
	String codFis=(String) serviceRequest.getAttribute("codFis");
	String dataNas=(String) serviceRequest.getAttribute("dataNas");
	String sesso=(String) serviceRequest.getAttribute("sesso");

%>

<html>
<head>
<title>Operatore</title>
<link rel="stylesheet" href="../../css/stili.css" type="text/css">
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
<af:linkScript path="../../js/" />
<SCRIPT type="text/javascript">
<!--
function aggiornaForm (prgSpi, nome, cognome, codFis, dataNas)
	{
		window.opener.aggiornaOperatore(prgSpi, nome, cognome, codFis, dataNas);
		window.close();
	}
	
function goInserisci() 
	{
		// Se la pagina è già in submit, ignoro questo nuovo invio!
		if (isInSubmit()) return;
	
		var url = "AdapterHTTP?PAGE=InsertUtenteOperatorePage&nome=<%=nome%>&cognome=<%=cognome%>&codFis=<%=codFis%>&dataNas=<%=dataNas%>&sesso=<%=sesso%>";
		setWindowLocation(url);
	}	
	
-->
</SCRIPT>


</head>
<body class="gestione">

<af:form dontValidate="true">

<af:JSButtonList moduleName="M_CERCAOPERATORI" jsSelect="aggiornaForm"/>
<br/>
<center><input class="pulsante" type="button" name="insertOp" value="Inserisci un nuovo operatore" onClick="goInserisci();" /></center>
<br/><br/>
<center><input class="pulsante" type="button" name="chiudi" value="chiudi" class="pulsante" onClick="window.close();"/></center>

</af:form>

</body>
</html>