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
String codCpi           = serviceRequest.containsAttribute("CODCPI")? serviceRequest.getAttribute("CODCPI").toString():"";
String data_al          = serviceRequest.containsAttribute("DATAAL")? serviceRequest.getAttribute("DATAAL").toString():"";
String data_dal         = serviceRequest.containsAttribute("DATADAL")? serviceRequest.getAttribute("DATADAL").toString():"";
String strDirezione     = serviceRequest.containsAttribute("DIREZIONE")? serviceRequest.getAttribute("DIREZIONE").toString():"";

String strMotivoContatto = serviceRequest.containsAttribute("MOTIVO_CONTATTO")? serviceRequest.getAttribute("MOTIVO_CONTATTO").toString():"";
String datascadenza      = serviceRequest.containsAttribute("DATASCADENZA")? serviceRequest.getAttribute("DATASCADENZA").toString():"";
String strCodScadenza    = serviceRequest.containsAttribute("SCADENZIARIO")? serviceRequest.getAttribute("SCADENZIARIO").toString():"";
String strFiltra         = serviceRequest.containsAttribute("FILTRALISTA")? serviceRequest.getAttribute("FILTRALISTA").toString():"";
String strCodTipoVal     = serviceRequest.containsAttribute("CODTIPOVALIDITA")? serviceRequest.getAttribute("CODTIPOVALIDITA").toString():"";
String strPatto297       = serviceRequest.containsAttribute("FLGPATTO")? serviceRequest.getAttribute("FLGPATTO").toString():"";
String strStatoValCV     = serviceRequest.containsAttribute("statoValCV")? serviceRequest.getAttribute("statoValCV").toString():"";
String OpzioneCV 		 = serviceRequest.containsAttribute("OpzioneCV")? (String)serviceRequest.getAttribute("OpzioneCV") : "";

//parametri per la gestione delle rose
String strPrgRosa        = serviceRequest.containsAttribute("PRGROSA")? serviceRequest.getAttribute("PRGROSA").toString():"";
String strCpiRose        = serviceRequest.containsAttribute("CPIROSE")? serviceRequest.getAttribute("CPIROSE").toString():"";
String strPrgRichiestaAz = serviceRequest.containsAttribute("PRGRICHIESTAAZ")? serviceRequest.getAttribute("PRGRICHIESTAAZ").toString():"";
String strCognome        = serviceRequest.containsAttribute("cognome")? serviceRequest.getAttribute("cognome").toString():"";
String strNome           = serviceRequest.containsAttribute("nome")? serviceRequest.getAttribute("nome").toString():"";
String strCF             = serviceRequest.containsAttribute("CF")? serviceRequest.getAttribute("CF").toString():"";
String strDidRilasciata  = serviceRequest.containsAttribute("didRilasciata")? serviceRequest.getAttribute("didRilasciata").toString():"";
String strCodStataOcc    = serviceRequest.containsAttribute("codStatoOcc")? serviceRequest.getAttribute("codStatoOcc").toString():"";
String strViewNonPresentati = serviceRequest.containsAttribute("viewNonPresentati")? serviceRequest.getAttribute("viewNonPresentati").toString():"";
String strNumVol            = serviceRequest.containsAttribute("NumVol")? serviceRequest.getAttribute("NumVol").toString():"";
String strDataNP            = serviceRequest.containsAttribute("dataNP")? serviceRequest.getAttribute("dataNP").toString():"";
String strCategoria181      = serviceRequest.containsAttribute("categoria181")? serviceRequest.getAttribute("categoria181").toString():"";
String strLegge407_90       = serviceRequest.containsAttribute("legge407_90")? serviceRequest.getAttribute("legge407_90").toString():"";
String strLungaDur          = serviceRequest.containsAttribute("lungaDur")? serviceRequest.getAttribute("lungaDur").toString():"";
String strRevRic            = serviceRequest.containsAttribute("revRic")? serviceRequest.getAttribute("revRic").toString():"";
String operatore            = serviceRequest.containsAttribute("PRGSPI") ? ((String) serviceRequest.getAttribute("PRGSPI")) : "";
String CodCpiApp            = serviceRequest.containsAttribute("CodCpiApp")? serviceRequest.getAttribute("CodCpiApp").toString():"";
String dataDalSlot = serviceRequest.containsAttribute("dataDalSlot") ? ((String) serviceRequest.getAttribute("dataDalSlot")) : "";
String codServizio = serviceRequest.containsAttribute("CODSERVIZIO") ? ((String) serviceRequest.getAttribute("CODSERVIZIO")) : "";

String datInizioIscr = serviceRequest.containsAttribute("DATINIZIOISCR") ? ((String) serviceRequest.getAttribute("DATINIZIOISCR")) : "";
String datStimata = serviceRequest.containsAttribute("DATSTIMATA") ? ((String) serviceRequest.getAttribute("DATSTIMATA")) : "";
String codEsito = serviceRequest.containsAttribute("CODESITO") ? ((String) serviceRequest.getAttribute("CODESITO")) : "";
String codAzione = serviceRequest.containsAttribute("CODAZIONE") ? ((String) serviceRequest.getAttribute("CODAZIONE")) : "";

boolean fromPattoAzioni = serviceRequest.containsAttribute("PATTO_AZIONI");
Vector codLstTab = null;
if(fromPattoAzioni) codLstTab = serviceRequest.getAttributeAsVector("COD_LST_TAB");
else codLstTab = new Vector(0);
String pageChiamante = StringUtils.getAttributeStrNotNull(serviceRequest,"PAGECHIAMANTE");
String statoSezioni = StringUtils.getAttributeStrNotNull(serviceRequest,"statoSezioni");
String nonFiltrare = StringUtils.getAttributeStrNotNull(serviceRequest,"NONFILTRARE");

String messScad = StringUtils.getAttributeStrNotNull(serviceRequest, "MESSAGE_SCAD");
String listPageScad = StringUtils.getAttributeStrNotNull(serviceRequest, "LIST_PAGE_SCAD");

String messRosa = StringUtils.getAttributeStrNotNull(serviceRequest, "MESSAGE_ROSA");
String listPageRosa = StringUtils.getAttributeStrNotNull(serviceRequest, "LIST_PAGE_ROSA");

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
  var codCpi = '<%=codCpi%>';
  var CodCpiApp = '<%=CodCpiApp%>';
  
  var data_dal = '<%=data_dal%>';
  var data_al = '<%=data_al%>';
  var strDirezioneLocal = '<%=strDirezione%>';
  var strMotivoContatto = '<%=strMotivoContatto%>';
<%-- Savino 13/03/2006: in verifiche-soggetti pronti all'incrocio il parametro data scadenza
          viene utilizzato per la descrizione cpi, col rischio di avere caratteri non accettati da javascript (Es.: reggio nell'emilia) 
--%>
  var datascadenza = '<%=StringUtils.formatValue4Javascript(datascadenza)%>';
  var strCodScadenza = '<%=strCodScadenza%>';
  var strFiltra = '<%=strFiltra%>';
  var strCodTipoVal = '<%=strCodTipoVal%>';
  var strPatto297 = '<%=strPatto297%>';
  <%-- aggiunta variabile strStatoValCV per filtrare le Scadenze validità della scheda lavoratori 
  	   anche per stato validità curriculum  --%>
  var strStatoValCV = '<%=strStatoValCV%>';
  var OpzioneCV = '<%=OpzioneCV%>';
  var localPrgRosa = '<%=strPrgRosa%>';
  var localCodCpiRose = '<%=strCpiRose%>';
  var localPrgRichiestaAz = '<%=strPrgRichiestaAz%>';
  var strCognomeLocal = '<%=strCognome%>';
  var strNomeLocal = '<%=strNome%>';
  var strCFLocal = '<%=strCF%>';
  var strDidRilasciataLocal = '<%=strDidRilasciata%>';
  var strCodStataOccLocal = '<%=strCodStataOcc%>';
  var strViewNonPresLocal = '<%=strViewNonPresentati%>';
  var strNumVolLocal = '<%=strNumVol%>';
  var strDataNPLocal = '<%=strDataNP%>';
  var strCategoria181Local = '<%=strCategoria181%>';
  var strLegge407_90Local = '<%=strLegge407_90%>';
  var strLungaDurLocal = '<%=strLungaDur%>';
  var strRevRicLocal = '<%=strRevRic%>';
  var dataDalSlotLocal = '<%=dataDalSlot%>';
  var codServizioLocal = '<%=codServizio%>';
  var fromPattoAzioni = '';
  var statoSezioni = '';
  var codLstTab = new Array();
  <% for (int i=0;i<codLstTab.size();i++) {%>
  codLstTab[<%=i%>]='<%=codLstTab.get(i)%>';
  <%}%>
  <%if(fromPattoAzioni){%> 
    fromPattoAzioni = 'true';
    statoSezioni = '<%=statoSezioni%>';
    nonFiltrare = '<%=nonFiltrare%>';
  <%}%>
  var pageChiamante = '<%=pageChiamante%>';
  
  var strmessScad = '<%=messScad%>';
  var strlistPageScad = '<%=listPageScad%>';
  var strmessRosa = '<%=messRosa%>';
  var strlistPageRosa = '<%=listPageRosa%>';
  var operatore = '<%=operatore%>';

  var datInizioIscr = '<%=datInizioIscr%>';
  var datStimata = '<%=datStimata%>';
  var codEsito = '<%=codEsito%>';
  var codAzione = '<%=codAzione%>';

</script>
<script language="Javascript">
<% // gestione hyperlinks: provenienza da amministrazione o azienda
	if (!strCdnLavoratore.equals("")) attributi.showHyperLinks(out, requestContainer, responseContainer,"cdnLavoratore=" + strCdnLavoratore);
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
  <frame name="ScadSuperiore" title="" src="AdapterHTTP?PAGE=ScadSlotPrenotaRecuperoDatiPage" scrolling="auto" frameborder="0">
  <frame name="ScadInferiore" title="" src="AdapterHTTP?PAGE=ScadAppuntamentoRecuperoDatiPage" scrolling="auto" frameborder="0">
  </frameset>
<%
}
else {
%>
  <frameset frameborder="YES" rows="100%"  border="0" framespacing="0">
  <frame name="ScadInferiore" title="" src="AdapterHTTP?PAGE=ScadAppuntamentoRecuperoDatiPage" scrolling="auto" frameborder="0">
  </frameset>
<%
}
%>



</html>