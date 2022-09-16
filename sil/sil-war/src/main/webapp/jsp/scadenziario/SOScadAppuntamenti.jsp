<%@ page contentType="text/html;charset=utf-8"%>
<%@ include file="../global/noCaching.inc"%>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="com.engiweb.framework.base.*,
  com.engiweb.framework.dispatching.module.AbstractModule,
  
  com.engiweb.framework.util.QueryExecutor,
  it.eng.sil.security.*,
  it.eng.afExt.utils.*,
  it.eng.afExt.utils.StringUtils,
  it.eng.sil.util.*,
  java.util.*,
  java.math.*,
  java.io.*,
  com.engiweb.framework.security.*" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%@ taglib uri="aftags" prefix="af" %>

<%
String _page            = (String) serviceRequest.getAttribute("PAGE");
String _pageProvenienza = (String) serviceRequest.getAttribute("PAGEPROVENIENZA");
int _funzione           = Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));
String strPrgAzienda    = serviceRequest.containsAttribute("PRGAZIENDA")? serviceRequest.getAttribute("PRGAZIENDA").toString():"";
String strPrgUnita      = serviceRequest.containsAttribute("PRGUNITA")? serviceRequest.getAttribute("PRGUNITA").toString():"";
String strCdnLavoratore = serviceRequest.containsAttribute("CDNLAVORATORE")? serviceRequest.getAttribute("CDNLAVORATORE").toString():"";
String codCpi          = serviceRequest.containsAttribute("CODCPI")? serviceRequest.getAttribute("CODCPI").toString():"";
String data_al          = serviceRequest.containsAttribute("DATAAL")? serviceRequest.getAttribute("DATAAL").toString():"";
String data_dal         = serviceRequest.containsAttribute("DATADAL")? serviceRequest.getAttribute("DATADAL").toString():"";
String operatore            = serviceRequest.containsAttribute("PRGSPI") ? ((String) serviceRequest.getAttribute("PRGSPI")) : "";
String codTipoContatto = serviceRequest.containsAttribute("CODCPICONTATTO") ? ((String) serviceRequest.getAttribute("CODCPICONTATTO")) : "";
String dataDalSlot = serviceRequest.containsAttribute("dataDalSlot") ? ((String) serviceRequest.getAttribute("dataDalSlot")) : "";
String codServizio = serviceRequest.containsAttribute("CODSERVIZIO") ? ((String) serviceRequest.getAttribute("CODSERVIZIO")) : "";

%>
<%
User userCurr = (User) sessionContainer.getAttribute(User.USERID);
boolean canNuovoAppuntamento;
PageAttribs attributi = new PageAttribs(userCurr, _page);
List listaSezioni = attributi.getSectionList();
if (listaSezioni.contains("INSERISCI")) {
  canNuovoAppuntamento = true;
}
else {
  canNuovoAppuntamento = false;
}

%>

<html>
<head>

<af:linkScript path="../../js/" />

<script language="javascript">
  var localPageProvenienza = '<%=_pageProvenienza%>';
  var localFunzione = '<%=_funzione%>';
  var strPrgAzienda = '<%=strPrgAzienda%>';
  var strPrgUnita = '<%=strPrgUnita%>';
  var strCdnLavoratore = '<%=strCdnLavoratore%>';
  var codTipoContatto = '<%=codTipoContatto%>';
  var codCpiLocal = '<%=codCpi%>';
  var data_dal = '<%=data_dal%>';
  var data_al = '<%=data_al%>';
  var codice = '<%=codCpi%>';
  var dataDalSlotLocal = '<%=dataDalSlot%>';
  var codServizioLocal = '<%=codServizio%>';
  var operatore = '<%=operatore%>';

</script>
<script language="Javascript">
<% // gestione hyperlinks: provenienza da amministrazione o azienda
	if (!strCdnLavoratore.equals("")) attributi.showHyperLinks(out, requestContainer, responseContainer,"cdnLavoratore="+strCdnLavoratore);
	else 
		attributi.showHyperLinks(out, requestContainer,responseContainer,"prgAzienda="+strPrgAzienda+"&prgUnita="+strPrgUnita);
	// chiamata esplicita di rinfresca()
%>
rinfresca();
</SCRIPT>
</head>
<%
if (canNuovoAppuntamento) {
%>
  <frameset frameborder="YES" rows="50%,50%"  border="0" framespacing="0">
  <frame name="ScadSuperiore" title="" src="AdapterHTTP?PAGE=SOScadSlotPrenotaRecuperoDatiPage" scrolling="auto" frameborder="0">
  <frame name="ScadInferiore" title="" src="AdapterHTTP?PAGE=SOScadAppuntamentoRecuperoDatiPage" scrolling="auto" frameborder="0">
  </frameset>
<%
}
else {
%>
  <frameset frameborder="YES" rows="100%"  border="0" framespacing="0">
  <frame name="ScadInferiore" title="" src="AdapterHTTP?PAGE=SOScadAppuntamentoRecuperoDatiPage" scrolling="auto" frameborder="0">
  </frameset>
<%
}
%>

</html>