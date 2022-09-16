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
String msg = StringUtils.getAttributeStrNotNull(serviceRequest, "MESSAGE");
String lp = StringUtils.getAttributeStrNotNull(serviceRequest, "LIST_PAGE");

String MODULE_NAME="MDETTTIPOEVIDENZA";
SourceBean cont = (SourceBean) serviceResponse.getAttribute(MODULE_NAME);
SourceBean row = (SourceBean) cont.getAttribute("ROW");
BigDecimal prgTipoEvidenza=null;
String codTipoEvidenza= "";
String strDescrizione = "";
String datInizioVal = "";
String datFineVal =  "";


if(row!=null) {
	codTipoEvidenza = StringUtils.getAttributeStrNotNull(row, "CODTIPOEVIDENZA");
	strDescrizione = StringUtils.getAttributeStrNotNull(row, "STRDESCRIZIONE");
	datInizioVal = StringUtils.getAttributeStrNotNull(row, "DATINIZIOVAL");
	datFineVal = StringUtils.getAttributeStrNotNull(row, "DATFINEVAL");
	prgTipoEvidenza= (BigDecimal) row.getAttribute("PRGTIPOEVIDENZA");
}


String btnSalva = "Aggiorna"; 
String btnChiudi = "Chiudi senza aggiornare";

ProfileDataFilter filter = new ProfileDataFilter(user, _page);
boolean canView=filter.canView();
boolean canModify=false;

PageAttribs attributi = new PageAttribs(user, _page);

if (! canView){
	response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
}else{
	 canModify = attributi.containsButton("AGGIORNA");
}
if(!canModify) { btnChiudi = "Chiudi"; }

Linguette  l = new Linguette(user, Integer.parseInt(_cdnFunzione) , _page, prgTipoEvidenza);
l.setCodiceItem("prgTipoEvidenza");


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
  	var check = compareDate(dataI, dataF);
  	if(check == -1) { return(true); }
  	if(check == 1) {
  		alert("La data di inizio val. deve essere precedente alla data di fine validita'");
  		return(false);
  	}
  	return(true);
  }
  
  function annulla(){	
		
		document.frmEv.STRDESCRIZIONE.value="";
		document.frmEv.CODTIPOEVIDENZA.value="";
		document.frmEv.DATINIZIOVAL.value="";
		document.frmEv.DATFINEVAL.value="";
	}
  
  </script>
</head>

<body class="gestione">
<p/>
 
 <% l.show(out); %>

<p class="titolo">Tipo Evidenza</p>
<af:form name="frmEv" action="AdapterHTTP" method="POST" onSubmit="checkDateVal()">
<input type="hidden" name="PAGE" value="ListaTipiEvidenzePage"/>
<input type="hidden" name="MODULE" value="STEV"/>
<input type="hidden" name="CDNFUNZIONE" value="<%=_cdnFunzione%>"/>
<input type="hidden" name="PRGTIPOEVIDENZA" value="<%=prgTipoEvidenza%>"/>
<!-- Per il ritorno alla lista tipi evidenze -->
<%if(!msg.equals("")) {%>
	<input type="hidden" name="MESSAGE" value="<%=msg%>"/>
<%}%>

<%if(!lp.equals("")) {%>
	<input type="hidden" name="LIST_PAGE" value="<%=lp%>"/>
<%}%>

<%@ include file="dettTipoEvidenza.inc" %>
</af:form>

</body>
</html>