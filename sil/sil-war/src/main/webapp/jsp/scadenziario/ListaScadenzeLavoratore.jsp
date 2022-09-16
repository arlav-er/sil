<%@ page contentType="text/html;charset=utf-8"%>
<%@ include file="../global/noCaching.inc"%>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="com.engiweb.framework.base.*,
  com.engiweb.framework.dispatching.module.AbstractModule,
  
  com.engiweb.framework.util.QueryExecutor,
  it.eng.sil.security.*,
  it.eng.afExt.utils.*,
  it.eng.sil.util.*,
  java.util.*,
  java.math.*,
  java.io.*,
  it.eng.sil.security.User,
  it.eng.sil.security.ProfileDataFilter,
  com.engiweb.framework.security.*" %>
  
<%@ taglib uri="aftags" prefix="af" %>
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
String strCdnLavoratore = serviceRequest.getAttribute("CDNLAVORATORE").toString();
InfCorrentiLav infCorrentiLav= null;
infCorrentiLav = new InfCorrentiLav(sessionContainer, strCdnLavoratore, user);
infCorrentiLav.setSkipLista(true);

String _page = (String) serviceRequest.getAttribute("PAGE");
ProfileDataFilter filter = new ProfileDataFilter(user, _page);
filter.setCdnLavoratore(new BigDecimal(strCdnLavoratore));
boolean canView=filter.canViewLavoratore();
if (! canView){
	response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
}else{

%>

<html>
<head>
<title>Scadenze Lavoratore</title>
<link rel="stylesheet" href="../../css/stili.css" type="text/css">
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
<af:linkScript path="../../js/" />
</head>
<body>
<%
infCorrentiLav.show(out); 
PageAttribs attributi = new PageAttribs(user, "ScadOrganizzativePage");
boolean canLav = attributi.containsButton("CONTATTI_LAVORATORE");
boolean canValSchedaLav = attributi.containsButton("VALIDA_SCHEDALAV");
boolean canScadSoggiornoLav = attributi.containsButton("VALIDA_PERMESSOLAV");
PageAttribs attributi1 = new PageAttribs(user, "ScadAmministrativePage");
boolean canScadDatePercLav = attributi1.containsButton("SCAD_DATE_PERCORSI_LAV");
boolean canScadCollLavPor = attributi1.containsButton("SCAD_COLLOQUIO_LAV_POR");
boolean canScadStipulaPatto = attributi1.containsButton("SCAD_STIPULA_PATTO_LAV");
boolean canScadPatto = attributi1.containsButton("SCAD_VALIDITA_PATTO");

boolean bScadenzaOk = false;
boolean bCanScadenza = false;

String htmlStreamTop = StyleUtils.roundTopTable(false);
String htmlStreamBottom = StyleUtils.roundBottomTable(false);

SourceBean contScadenze = null;
Vector rows_VectorScadenze = null;

if (canLav) {
  bCanScadenza = true;
  contScadenze = (SourceBean) serviceResponse.getAttribute("M_ListaScadLavRicontattare");
  rows_VectorScadenze = contScadenze.getAttributeAsVector("ROWS.ROW");
  if (rows_VectorScadenze.size() > 0) {
    bScadenzaOk = true;
  %>
    <af:list moduleName="M_ListaScadLavRicontattare" skipNavigationButton="1"/>
  <%
  }
}

contScadenze = null;
rows_VectorScadenze = null;

if (canValSchedaLav) {
  bCanScadenza = true;
  contScadenze = (SourceBean) serviceResponse.getAttribute("M_ListaScadValiditaSchedaLav");
  rows_VectorScadenze = contScadenze.getAttributeAsVector("ROWS.ROW");
  if (rows_VectorScadenze.size() > 0) {
    bScadenzaOk = true;
  %>
    <af:list moduleName="M_ListaScadValiditaSchedaLav" skipNavigationButton="1"/>
  <%
  }
}

contScadenze = null;
rows_VectorScadenze = null;

if (canScadSoggiornoLav) {
  bCanScadenza = true;
  contScadenze = (SourceBean) serviceResponse.getAttribute("M_ListaScadPermessoSoggLav");
  rows_VectorScadenze = contScadenze.getAttributeAsVector("ROWS.ROW");
  if (rows_VectorScadenze.size() > 0) {
    bScadenzaOk = true;
  %>
    <af:list moduleName="M_ListaScadPermessoSoggLav" skipNavigationButton="1"/>
  <%
  }
}

contScadenze = null;
rows_VectorScadenze = null;

if (canScadDatePercLav) {
  bCanScadenza = true;
  contScadenze = (SourceBean) serviceResponse.getAttribute("M_ListaScadAzioniConcordLav");
  rows_VectorScadenze = contScadenze.getAttributeAsVector("ROWS.ROW");
  if (rows_VectorScadenze.size() > 0) {
    bScadenzaOk = true;
  %>
    <af:list moduleName="M_ListaScadAzioniConcordLav" skipNavigationButton="1"/>
  <%
  }
}

contScadenze = null;
rows_VectorScadenze = null;

if (canScadCollLavPor) {
  bCanScadenza = true;
  contScadenze = (SourceBean) serviceResponse.getAttribute("M_ListaScadPrimoCollLav");
  rows_VectorScadenze = contScadenze.getAttributeAsVector("ROWS.ROW");
  if (rows_VectorScadenze.size() > 0) {
    bScadenzaOk = true;
  %>
    <af:list moduleName="M_ListaScadPrimoCollLav" skipNavigationButton="1"/>
  <%
  }
}

contScadenze = null;
rows_VectorScadenze = null;

if (canScadStipulaPatto) {
  bCanScadenza = true;
  contScadenze = (SourceBean) serviceResponse.getAttribute("M_ListaScadStipulaPattoLav");
  rows_VectorScadenze = contScadenze.getAttributeAsVector("ROWS.ROW");
  if (rows_VectorScadenze.size() > 0) {
    bScadenzaOk = true;
  %>
    <af:list moduleName="M_ListaScadStipulaPattoLav" skipNavigationButton="1"/>
  <%
  }
}

contScadenze = null;
rows_VectorScadenze = null;

if (canScadPatto) {
  bCanScadenza = true;
  contScadenze = (SourceBean) serviceResponse.getAttribute("M_ListaScadenzaPattoLav");
  rows_VectorScadenze = contScadenze.getAttributeAsVector("ROWS.ROW");
  if (rows_VectorScadenze.size() > 0) {
    bScadenzaOk = true;
  %>
    <af:list moduleName="M_ListaScadenzaPattoLav" skipNavigationButton="1"/>
  <%
  }
}

contScadenze = null;
rows_VectorScadenze = null;

if (!bCanScadenza) {
%>
  <br>
  <%out.print(htmlStreamTop);%>
  <p align="center">
  <table class="lista" align="center">
  <tr><td align="center"><b>Operatore non abilitato a visualizzare le scadenze del lavoratore</b></td></tr>
  </table></p>
<%
  out.print(htmlStreamBottom);
}
else {
  if (!bScadenzaOk) {
  %>
    <br>
    <%out.print(htmlStreamTop);%>
    <p align="center">
    <table class="lista" align="center">
    <tr><td align="center"><b>Non ci sono scadenze per il lavoratore</b></td></tr>
    </table></p>
  <%
    out.print(htmlStreamBottom);
  }
}

%>
<center>
<table><tr><td align="center">
<input type="button" class="pulsanti" name="buttChiudi" value="Chiudi" onClick="javascript:window.close();">
</td></tr></table>
</center>
</body>
</html>

<% } %>