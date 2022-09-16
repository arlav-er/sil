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

String MODULE_NAME="MDETTEVIDENZA";
SourceBean cont = (SourceBean) serviceResponse.getAttribute(MODULE_NAME);
SourceBean row = (SourceBean) cont.getAttribute("ROW");
String prgEvidenza = StringUtils.getAttributeStrNotNull(serviceRequest, "PRGEVIDENZA");;
String cdnLavoratore = StringUtils.getAttributeStrNotNull(serviceRequest, "CDNLAVORATORE");
String prgTipoEvidenza = "";
String datDataScad = "";
String strEvidenza = "";
String numKloEvidenza =  "";
String cdnUtIns = "";
String dtmIns = "";
String cdnUtMod = "";
String dtmMod = "";
Testata testata = new Testata(null,null,null,null);

if(row!=null) {
        prgTipoEvidenza = row.containsAttribute("PRGTIPOEVIDENZA")?row.getAttribute("PRGTIPOEVIDENZA").toString():"";
        datDataScad = StringUtils.getAttributeStrNotNull(row, "DATDATASCAD");
        strEvidenza = StringUtils.getAttributeStrNotNull(row, "STREVIDENZA");
		numKloEvidenza = StringUtils.getAttributeStrNotNull(row, "NUMKLOEVIDENZA");
        cdnUtIns = StringUtils.getAttributeStrNotNull(row, "CDNUTINS");
        dtmIns = StringUtils.getAttributeStrNotNull(row, "DTMINS");
        cdnUtMod = StringUtils.getAttributeStrNotNull(row, "CDNUTMOD");
        dtmMod = StringUtils.getAttributeStrNotNull(row, "DTMMOD");
        testata = new Testata(cdnUtIns, dtmIns, cdnUtMod, dtmMod);
}


String btnSalva = "Aggiorna";
String btnChiudi = "Chiudi senza aggiornare";

String _page = "ListaEvidenzePage"; 
ProfileDataFilter filter = new ProfileDataFilter(user, _page);
filter.setCdnLavoratore(new BigDecimal(cdnLavoratore));

boolean canView=filter.canViewLavoratore();
boolean canModify = false;


PageAttribs attributi = new PageAttribs(user, _page);

if (! canView){
	response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
}else{
	canModify = attributi.containsButton("AGGIORNA");
}

if ( !canModify) {
	// do nothing
} else {
	boolean canEdit = filter.canEditLavoratore();
	if ( !canEdit ) { canModify = false;	}
}


if(!canModify) { btnChiudi = "Chiudi"; }

String mess = StringUtils.getAttributeStrNotNull(serviceRequest, "MESSAGE");
String lp = StringUtils.getAttributeStrNotNull(serviceRequest, "LIST_PAGE");

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
  <title>Dettaglio Evidenza</title>
</head>

<body class="gestione">
<br>
<p class="titolo">Dettaglio Evidenza</p>

<af:form name="frmEv" action="AdapterHTTP" method="POST" onSubmit="checkDataScad()">
<input type="hidden" name="PAGE" value="ListaEvidenzePage"/>
<input type="hidden" name="MODULE" value="SEV"/>
<input type="hidden" name="CDNLAVORATORE" value="<%=cdnLavoratore%>"/>
<input type="hidden" name="PRGEVIDENZA" value="<%=prgEvidenza%>"/>
<input type="hidden" name="NUMKLOEVIDENZA" value="<%=(Integer.parseInt(numKloEvidenza)+1)%>"/>
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