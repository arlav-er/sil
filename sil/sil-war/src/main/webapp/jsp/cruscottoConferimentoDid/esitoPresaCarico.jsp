<%@page import="it.gov.mlps.DataModels.InformationDelivery.ConferimentoDID._1_0.TipoEvento"%>
<%@ page
	contentType="text/html;charset=utf-8"
	
	import="javax.xml.datatype.XMLGregorianCalendar,
			it.eng.sil.module.conf.did.ConferimentoUtility,
			com.engiweb.framework.base.*,
			it.eng.sil.pojo.yg.sap.due.*,
			it.eng.sil.security.*,
			it.eng.afExt.utils.*,
			it.eng.sil.util.*,
			java.math.*,
			java.util.*"
	
	extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage"
%>

<%@ taglib uri="aftags" prefix="af" %>

<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%
	String  titolo = "Esito presa in carico per trasferimento";
	String  _page  = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"PAGE"); 

	// Lettura parametri dalla REQUEST
	String  _funzione   = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "cdnfunzione");
	String  cdnLavoratore    = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"cdnLavoratore");
	
	String dataTrasferimento = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"DATATRASFCOMP");
	String msgTrasferimento = "Data di presa in carico per trasferimento " + dataTrasferimento;
	
	boolean  canModify= false;
	// Stringhe con HTML per layout tabelle
	String htmlStreamTop = StyleUtils.roundTopTable(canModify);
	String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);
	 
%>

<html>
<head>
<title><%= titolo %></title>
  	<link rel="stylesheet" type="text/css" href="../../css/stili.css">
  	<link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
  	<link rel="stylesheet" type="text/css" href="../../css/stiliTemplate.css"/>

<af:linkScript path="../../js/"/>

<%-- INCLUDERE QUI ALTRI JAVASRIPT CON CLAUSOLE DEL TIPO:
<script language="Javascript" src="../../js/xxx.js"></script>
--%>

<script language="Javascript">

	/* Funzione chiamata al caricamento della pagina */
	function onLoad() {
		rinfresca();
 	}
	
	function goToCruscottoDid(){
		if (isInSubmit()) return;
	      url="AdapterHTTP?PAGE=SitAttualeConfDIDPage";
	      url += "&CDNFUNZIONE="+"<%=_funzione%>";      
	      url += "&cdnLavoratore="+"<%=cdnLavoratore%>";     
 	      setWindowLocation(url);
	}
 
</script>
</head>

<body class="gestione" onload="onLoad()">

<%
	// TESTATA LAVORATORE
	if (StringUtils.isFilled(cdnLavoratore)) {
		InfCorrentiLav testata = new InfCorrentiLav(cdnLavoratore, user);
		testata.show(out);
	}

%>

<p>
	<font color="green">
		<af:showMessages prefix="M_InviaSapPrendiTitolarieta"/>
  	</font>
  	<font color="red"><af:showErrors /></font>
</p>

<af:form name="Frm1" action="AdapterHTTP" method="POST">
<p class="titolo"><%= titolo %></p>
	 
 	
<%= htmlStreamTop %>
<table class="main">
<tr>
<td class="campo"><%=msgTrasferimento%>
</td>
</tr>
<tr>
<td>
	<center>
		<input type="button" class="pulsanti" name="chiudi" value="Chiudi" onclick="goToCruscottoDid();" />
	</center>
</td>
</tr>

</table>
<%= htmlStreamBottom %>

</af:form>

</body>
</html>
