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
String fScad = StringUtils.getAttributeStrNotNull(serviceRequest, "SCAD");
int _funzione = Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));

String mess = StringUtils.getAttributeStrNotNull(serviceRequest, "MSG");
String lp = StringUtils.getAttributeStrNotNull(serviceRequest, "LP");

String prgEvidenza = "";
String cdnLavoratore = StringUtils.getAttributeStrNotNull(serviceRequest, "CDNLAVORATORE");
String prgTipoEvidenza = "";
String datDataScad = "";
String strEvidenza = "";
String numKloEvidenza =  "";

Testata testata = new Testata(null,null,null,null);

String btnSalva = "Inserisci";
String btnChiudi = "Chiudi senza inserire";
boolean canModify = true;

/*
int cdnGruppo=user.getCdnGruppo();
int cdnProfilo=user.getCdnProfilo();
*/
%>

<html>
<head>
  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <link rel="stylesheet" href="../../css/listdetail.css" type="text/css">
  <af:linkScript path="../../js/"/>
  <title>Nuova Evidenza</title>
</head> 

<body class="gestione">
<br>
<p class="titolo">Nuova Evidenza</p>
<af:form name="frmEv" action="AdapterHTTP" method="POST" onSubmit="checkDataScad()">
<input type="hidden" name="PAGE" value="ListaEvidenzePage"/>
<input type="hidden" name="MODULE" value="SEV"/>
<input type="hidden" name="CDNLAVORATORE" value="<%=cdnLavoratore%>"/>
<input type="hidden" name="PRGEVIDENZA" value="<%=prgEvidenza%>"/>
<input type="hidden" name="NUMKLOEVIDENZA" value="<%=numKloEvidenza%>"/>
<input type="hidden" name="CDNFUNZIONE" value="<%=_funzione%>"/>
<%if(!mess.equals("")) {%>
	<input type="hidden" name="MESSAGE" value="<%=mess%>"/>
<%}%>

<%if(!lp.equals("")) {%>
	<input type="hidden" name="LIST_PAGE" value="<%=lp%>"/>
<%}%>

<%if(!fScad.equals("")) {%>
	<input type="hidden" name="SCAD" value="N"/>
<%}%>

<%@ include file="dettEvidenza.inc" %>

</af:form>

</body>
</html>
