<%@ page contentType="text/html;charset=utf-8"%>
 
<%@ page import="com.engiweb.framework.base.*,
                com.engiweb.framework.security.*,
                java.lang.*,
                java.text.*,
                java.util.*,
                java.math.*,
                it.eng.afExt.utils.*,
                it.eng.sil.util.*,
                it.eng.sil.security.*" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ taglib uri="aftags" prefix="af"%>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>


<%
	boolean showListaAdesioni = false;
	Vector listaDataAdesioni = serviceResponse
			.getAttributeAsVector("M_YG_CheckDataAdesione.ROWS.ROW");
	if (listaDataAdesioni.size() > 0) {
		showListaAdesioni = true;
	}

	String htmlStreamTop = StyleUtils.roundTopTable(false);
  	String htmlStreamBottom = StyleUtils.roundBottomTable(false);
%>


<html>
<head>
 <title>Verifica data adesione GG</title>
 <link rel="stylesheet" type="text/css" href="../../css/stiliCoop.css"/>
 <link rel="stylesheet" media="print, screen" type="text/css" href="../../css/listdetailCoop.css" />
 <af:linkScript path="../../js/"/>
 <script TYPE="text/javascript">
  
	  function ValorizzaDataAdesione(dataAdesione){
		  window.opener.document.Frm1.datAdesioneGG.value = dataAdesione;
		  window.close();
	  }
	  
  </script>
 
</head>
<!--<body  onload="checkError();" class="gestione" > -->
<body class="gestione" onload="rinfresca()">
<p class="titolo"><br><b>Verifica data adesione GG</b></p>
<%if (showListaAdesioni) {%>
	<af:list moduleName="M_YG_CheckDataAdesione" skipNavigationButton="1" jsSelect="ValorizzaDataAdesione" />
<%} else {%>
	<%out.print(htmlStreamTop);%>
	<center>
	<font color="red">
		<af:showErrors/>
	</font>
	<font color="green">
	 	<af:showMessages prefix="M_YG_CheckDataAdesione"/>
	</font>
	</center>
	<%out.print(htmlStreamBottom);%>
<%}%>
<center><input class="pulsante" type = "button" name="chiudi" value="Chiudi" onclick="window.close()"/></center>
</body>
</html>
