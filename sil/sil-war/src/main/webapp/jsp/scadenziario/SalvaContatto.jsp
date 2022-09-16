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

<%
SourceBean rigaGiorniNL = null;
rigaGiorniNL = (SourceBean)serviceResponse.getAttribute("MVerificaDataNL");
Vector rows_vettGiorniNL = rigaGiorniNL.getAttributeAsVector("ROWS.ROW");

String _page = (String) serviceRequest.getAttribute("PAGE");
String _pageProvenienza = (String) serviceRequest.getAttribute("PAGEPROVENIENZA");
int _funzione=Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));
String strPrgAzienda = serviceRequest.containsAttribute("PRGAZIENDA")? serviceRequest.getAttribute("PRGAZIENDA").toString():"";
String strPrgUnita = serviceRequest.containsAttribute("PRGUNITA")? serviceRequest.getAttribute("PRGUNITA").toString():"";
String strCdnLavoratore = serviceRequest.containsAttribute("CDNLAVORATORE")? serviceRequest.getAttribute("CDNLAVORATORE").toString():"";
String strRecuperaInfo = serviceRequest.containsAttribute("RECUPERAINFO")? serviceRequest.getAttribute("RECUPERAINFO").toString():"";
String codCpi = serviceRequest.containsAttribute("CODCPI")? serviceRequest.getAttribute("CODCPI").toString():"";
String CodCpiApp = serviceRequest.containsAttribute("CodCpiApp")? serviceRequest.getAttribute("CodCpiApp").toString():"";
String data_al = serviceRequest.containsAttribute("DATAAL")? serviceRequest.getAttribute("DATAAL").toString():"";
String data_dal = serviceRequest.containsAttribute("DATADAL")? serviceRequest.getAttribute("DATADAL").toString():"";
String strDescMotivoContatto = serviceRequest.containsAttribute("MOTIVO_CONTATTO")? serviceRequest.getAttribute("MOTIVO_CONTATTO").toString():"";
String datascadenza = serviceRequest.containsAttribute("DATASCADENZA")? serviceRequest.getAttribute("DATASCADENZA").toString():"";
String strTipoScadenziario = serviceRequest.containsAttribute("SCADENZIARIO")? serviceRequest.getAttribute("SCADENZIARIO").toString():"";
String strFiltra = serviceRequest.containsAttribute("FILTRALISTA")? serviceRequest.getAttribute("FILTRALISTA").toString():"";
String strCodTipoVal = serviceRequest.containsAttribute("CODTIPOVALIDITA")? serviceRequest.getAttribute("CODTIPOVALIDITA").toString():"";
String strPatto297 = serviceRequest.containsAttribute("FLGPATTO")? serviceRequest.getAttribute("FLGPATTO").toString():"";
String strStatoValCV = serviceRequest.containsAttribute("statoValCV")? serviceRequest.getAttribute("statoValCV").toString():"";
String strDirezione = serviceRequest.containsAttribute("DIREZIONE")? serviceRequest.getAttribute("DIREZIONE").toString():"";
String strTipo = serviceRequest.containsAttribute("TIPO")? serviceRequest.getAttribute("TIPO").toString():"";
String strPrgRosa = serviceRequest.containsAttribute("PRGROSA")? serviceRequest.getAttribute("PRGROSA").toString():"";
String strCpiRose = serviceRequest.containsAttribute("CPIROSE")? serviceRequest.getAttribute("CPIROSE").toString():"";
String strPrgRichiestaAz = serviceRequest.containsAttribute("PRGRICHIESTAAZ")? serviceRequest.getAttribute("PRGRICHIESTAAZ").toString():"";
String htmlStreamTop = StyleUtils.roundTopTable(false);
String htmlStreamBottom = StyleUtils.roundBottomTable(false);

String strDataContattoS = serviceRequest.containsAttribute("DATCONTATTO")?serviceRequest.getAttribute("DATCONTATTO").toString():"";
String strOraContattoS = serviceRequest.containsAttribute("STRORACONTATTO")?serviceRequest.getAttribute("STRORACONTATTO").toString():"";
String strSpiContattoS = serviceRequest.containsAttribute("PRGSPICONTATTO")?serviceRequest.getAttribute("PRGSPICONTATTO").toString():"";
String strTxtContattoS = serviceRequest.containsAttribute("TXTCONTATTO")?serviceRequest.getAttribute("TXTCONTATTO").toString():"";
String strFlgRicontattareS = serviceRequest.containsAttribute("FLGRICONTATTARE")?serviceRequest.getAttribute("FLGRICONTATTARE").toString():"";
String strDirezioneS = serviceRequest.containsAttribute("STRIO")?serviceRequest.getAttribute("STRIO").toString():"";
String strDataEntroIlS = serviceRequest.containsAttribute("DATENTROIL")?serviceRequest.getAttribute("DATENTROIL").toString():"";
String strMotContattoS = serviceRequest.containsAttribute("PRGMOTCONTATTO")?serviceRequest.getAttribute("PRGMOTCONTATTO").toString():"";
String strTipoContattoS = serviceRequest.containsAttribute("PRGTIPOCONTATTO")?serviceRequest.getAttribute("PRGTIPOCONTATTO").toString():"";
String strEffContattoS = serviceRequest.containsAttribute("PRGEFFETTOCONTATTO")?serviceRequest.getAttribute("PRGEFFETTOCONTATTO").toString():"";

String datInizioIscr = serviceRequest.containsAttribute("DATINIZIOISCR") ? ((String) serviceRequest.getAttribute("DATINIZIOISCR")) : "";
String datStimata = serviceRequest.containsAttribute("DATSTIMATA") ? ((String) serviceRequest.getAttribute("DATSTIMATA")) : "";
String codEsito = serviceRequest.containsAttribute("CODESITO") ? ((String) serviceRequest.getAttribute("CODESITO")) : "";
String codAzione = serviceRequest.containsAttribute("CODAZIONE") ? ((String) serviceRequest.getAttribute("CODAZIONE")) : "";

String messRosa = StringUtils.getAttributeStrNotNull(serviceRequest, "MESSAGE_ROSA");
String listPageRosa = StringUtils.getAttributeStrNotNull(serviceRequest, "LIST_PAGE_ROSA");

String messScad = StringUtils.getAttributeStrNotNull(serviceRequest, "MESSAGE_SCAD");
String listPageScad = StringUtils.getAttributeStrNotNull(serviceRequest, "LIST_PAGE_SCAD");


String _strPrgAzienda_ 	  = serviceRequest.containsAttribute("_PRGAZIENDA_")? serviceRequest.getAttribute("_PRGAZIENDA_").toString():"";
String _strPrgUnita_   	  = serviceRequest.containsAttribute("_PRGUNITA_")? serviceRequest.getAttribute("_PRGUNITA_").toString():"";
String _strCdnLavoratore_ = serviceRequest.containsAttribute("_CDNLAVORATORE_")? serviceRequest.getAttribute("_CDNLAVORATORE_").toString():"";

%>
  
<%@ taglib uri="aftags" prefix="af" %>
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<html>
<head>
  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <link rel="stylesheet" href="../../css/listdetail.css" type="text/css">
  <af:linkScript path="../../js/"/>
  <title></title>
  <script language="Javascript">
  </script>
</head>
<body class="gestione">
<af:form name="frmSalvaContatto" action="AdapterHTTP" method="POST">
<input type="hidden" name="CODCPI" value="<%=codCpi%>">
<input type="hidden" name="PAGE" value="ScadContattoPage">
<input type="hidden" name="PAGEPROVENIENZA" value="<%=_pageProvenienza%>">
<input type="hidden" name="PRGUNITA" value="<%=strPrgUnita%>">
<input type="hidden" name="PRGAZIENDA" value="<%=strPrgAzienda%>">
<input type="hidden" name="CDNLAVORATORE" value="<%=strCdnLavoratore%>">
<input type="hidden" name="RECUPERAINFO" value="<%=strRecuperaInfo%>">
<input type="hidden" name="CDNFUNZIONE" value="<%=_funzione%>">
<input type="hidden" name="DATASCADENZA" value="<%=datascadenza%>">
<input type="hidden" name="DATAAL" value="<%=data_al%>">
<input type="hidden" name="DATADAL" value="<%=data_dal%>">
<input type="hidden" name="MOTIVO_CONTATTO" value="<%=strDescMotivoContatto%>"/>
<input type="hidden" name="FILTRALISTA" value="<%=strFiltra%>">
<input type="hidden" name="SCADENZIARIO" value="<%=strTipoScadenziario%>"/>
<input type="hidden" name="CODTIPOVALIDITA" value="<%=strCodTipoVal%>">
<input type="hidden" name="FLGPATTO" value="<%=strPatto297%>">
<input type="hidden" name="statoValCV" value="<%=strStatoValCV%>">
<input type="hidden" name="CodCpiApp" value="<%=CodCpiApp%>">


<input type="hidden" name="DIREZIONE" value="<%=strDirezione%>">
<input type="hidden" name="TIPO" value="<%=strTipo%>">
<input type="hidden" name="PRGROSA" value="<%=strPrgRosa%>">
<input type="hidden" name="CPIROSE" value="<%=strCpiRose%>">
<input type="hidden" name="PRGRICHIESTAAZ" value="<%=strPrgRichiestaAz%>">

<input type="hidden" name="_PRGUNITA_" value="<%=_strPrgUnita_%>">
<input type="hidden" name="_PRGAZIENDA_" value="<%=_strPrgAzienda_%>">
<input type="hidden" name="_CDNLAVORATORE_" value="<%=_strCdnLavoratore_%>">

<input type="hidden" name="MESSAGE_ROSA" value="<%=messRosa%>"/>
<input type="hidden" name="LIST_PAGE_ROSA" value="<%=listPageRosa%>"/>
<input type="hidden" name="MESSAGE_SCAD" value="<%=messScad%>"/>
<input type="hidden" name="LIST_PAGE_SCAD" value="<%=listPageScad%>"/>

<!-- per il salvataggio del contatto -->
<input type="hidden" name="SALVACONTATTO" value="">
<input type="hidden" name="CODCPICONTATTO" value="<%=codCpi%>">
<input type="hidden" name="DATCONTATTO" value="<%=strDataContattoS%>">
<input type="hidden" name="STRORACONTATTO" value="<%=strOraContattoS%>">
<input type="hidden" name="PRGSPICONTATTO" value="<%=strSpiContattoS%>">
<af:textBox	type="hidden" name="TXTCONTATTO" value="<%=strTxtContattoS%>"/>
<input type="hidden" name="FLGRICONTATTARE" value="<%=strFlgRicontattareS%>">
<input type="hidden" name="PARSTRIO" value="<%=strDirezioneS%>">
<input type="hidden" name="DATENTROIL" value="<%=strDataEntroIlS%>">
<input type="hidden" name="PRGMOTCONTATTO" value="<%=strMotContattoS%>">
<input type="hidden" name="PRGTIPOCONTATTO" value="<%=strTipoContattoS%>">
<input type="hidden" name="PRGEFFETTOCONTATTO" value="<%=strEffContattoS%>">

<input type="hidden" name="DATINIZIOISCR" value="<%=datInizioIscr%>">
<input type="hidden" name="DATSTIMATA" value="<%=datStimata%>">
<input type="hidden" name="CODESITO" value="<%=codEsito%>">
<input type="hidden" name="CODAZIONE" value="<%=codAzione%>">

<%
if (rows_vettGiorniNL.size() > 0) {
  out.print(htmlStreamTop);
%>
  <table class="main">
  <tr>
  <td align="justify">Il contatto non può essere inserito in quanto la data del contatto risulta essere un giorno non lavorativo.
  </td>
  </tr>
  <tr><td>&nbsp;</td></tr>
  <tr>
  <td align="center">
  <input type="submit" class="pulsanti" value="Indietro">
  </td>
  </tr>
  </table>
<%
  out.print(htmlStreamBottom);
}
%>
</af:form>

<%
if (rows_vettGiorniNL.size() == 0) {
%>
  <script language="javascript">
    document.frmSalvaContatto.SALVACONTATTO.value = "ok";
    doFormSubmit(document.frmSalvaContatto);
  </script>
<%
}
%>
</body>
</html>