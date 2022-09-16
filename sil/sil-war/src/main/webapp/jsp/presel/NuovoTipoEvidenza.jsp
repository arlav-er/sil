<%@ page contentType="text/html;charset=utf-8"%>
<%@ include file="../global/noCaching.inc"%>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="com.engiweb.framework.base.*,
                com.engiweb.framework.base.SourceBean,
                 
                com.engiweb.framework.security.*,java.math.*,
                java.lang.*,java.text.*,java.util.*, it.eng.sil.security.*,
                it.eng.sil.util.*, it.eng.sil.module.agenda.ShowApp,
                it.eng.afExt.utils.*"
%>


<%@ taglib uri="aftags" prefix="af" %> 

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
String _page = StringUtils.getAttributeStrNotNull(serviceRequest, "PAGE");
String _cdnFunzione = StringUtils.getAttributeStrNotNull(serviceRequest, "CDNFUNZIONE");

String codTipoEvidenza = "";
String strDescrizione = "";
String datInizioVal = "";
String datFineVal =  "";

String btnSalva = "Inserisci"; 
String btnChiudi = "Chiudi senza inserire";

PageAttribs attributi = new PageAttribs(user, _page);
boolean canModify = true;
%>

<html>
<head>
  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <link rel="stylesheet" href="../../css/listdetail.css" type="text/css">
  <af:linkScript path="../../js/"/>
  <title>Tipo Evidenza</title>
  <SCRIPT language="JavaScript" src="../../js/script_comuni.js"></SCRIPT>
  <script type="text/javascript">
  function checkDateVal()
  {
  	var coll = document.getElementsByName("DATINIZIOVAL");
  	var dataI = coll[0].value;
  	coll = document.getElementsByName("DATFINEVAL");
  	var dataF = coll[0].value;
  	var check = -1;
  	if(dataF!= "") { check = compareDate(dataI, dataF); }
  	if(check == -1) { return(true); }
  	if(check == 1) {
  		alert("La data di inizio val. deve essere precedente alla data di fine validita'");
  		return(false);
  	}
  	return(true);
  }
  </script>
</head>

<body class="gestione">
<p class="titolo">Tipo Evidenza</p>
<af:form name="frmEv" action="AdapterHTTP" method="POST" onSubmit="checkDateVal()">
<input type="hidden" name="PAGE" value="ListaTipiEvidenzePage"/>
<input type="hidden" name="MODULE" value="ITEV"/>
<input type="hidden" name="CDNFUNZIONE" value="<%=_cdnFunzione%>"/>
<input type="hidden" name="OLD_CODTIPOEVIDENZA" value=""/>

<%@ include file="dettTipoEvidenza.inc" %>
</af:form>

</body>
</html>