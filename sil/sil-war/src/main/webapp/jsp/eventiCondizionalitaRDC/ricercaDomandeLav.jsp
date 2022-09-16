<%@ page contentType="text/html;charset=utf-8"%>

<%@ page
	import="com.engiweb.framework.base.*,
                com.engiweb.framework.security.*,
                java.lang.*,java.text.*,java.util.*,
                it.eng.afExt.utils.*, it.eng.sil.util.*,
                it.eng.sil.security.*"%>
<%@ include file="../global/noCaching.inc"%>
<%@ include file="../global/getCommonObjects.inc"%>

<%@ page
	extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage"%>

<%@ taglib uri="aftags" prefix="af"%>

<%
 	String _page = "RicercaDomandePage";
	ProfileDataFilter filter = new ProfileDataFilter(user, _page);
    PageAttribs attributi = new PageAttribs(user, _page);
 
	
	if (!filter.canView()) {
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
		return;
	} 
%>

<html>
<head>
<title>Notifiche RDC</title>
<link rel="stylesheet" type="text/css" href="../../css/stili.css" />
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css" />
<af:linkScript path="../../js/" />

<script type="text/Javascript">
	if (window.top.menu != undefined){
			window.top.menu.location="AdapterHTTP?PAGE=MenuCompletoPage";
	}

 
	function AggiornaForm (chiave, protocolloInps, dataDomanda, codiceStatoDomanda, statoDomanda) {
       try{
    	   window.opener.document.Frm1.chiavePrimaria.value = chiave;
    	   window.opener.document.Frm1.protInps.value = protocolloInps;
    	   window.opener.document.Frm1.dataDomanda.value = dataDomanda;
    	   window.opener.document.Frm1.codiceStatoDomanda.value = codiceStatoDomanda;
    	   window.opener.document.Frm1.statoDomanda.value = statoDomanda;
     	   window.close();
       }
		catch(err) {
		}
	  
	}
	
 
</script>

</head>
<body class="gestione" onload="rinfresca();">
	<center>
		<font color="red"> 
			<af:showErrors />
		</font>
		
	</center>
	 <center><table class="main">
	 	<af:JSButtonList moduleName="M_ListaDomandeCondizionalita" jsSelect="AggiornaForm" 
	 	configProviderClass="it.eng.sil.module.condizionalita.rdc.DynamicConfigDomandeCondizionalita" 
						 getBack="false"/>
	 
	<br />
	</center>
	<center><button onclick="window.close()" class="pulsanti">Chiudi</button></center>
	
</body>
</html>

