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
  com.engiweb.framework.security.*" %>

<%
Testata operatoreInfo = null;
String _page = (String) serviceRequest.getAttribute("PAGE");
String _pageProvenienza = (String) serviceRequest.getAttribute("PAGEPROVENIENZA");
int _funzione=Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));
String strPrgAzienda = serviceRequest.containsAttribute("PRGAZIENDA")? serviceRequest.getAttribute("PRGAZIENDA").toString():"";
String strPrgUnita = serviceRequest.containsAttribute("PRGUNITA")? serviceRequest.getAttribute("PRGUNITA").toString():"";
String strCdnLavoratore = serviceRequest.containsAttribute("CDNLAVORATORE")? serviceRequest.getAttribute("CDNLAVORATORE").toString():"";
String strRecuperaInfo = serviceRequest.containsAttribute("RECUPERAINFO")? serviceRequest.getAttribute("RECUPERAINFO").toString():"";
String codCpi = serviceRequest.containsAttribute("CODCPI")? serviceRequest.getAttribute("CODCPI").toString():"";
String data_al = serviceRequest.containsAttribute("DATAAL")? serviceRequest.getAttribute("DATAAL").toString():"";
String data_dal = serviceRequest.containsAttribute("DATADAL")? serviceRequest.getAttribute("DATADAL").toString():"";
String strDescMotivoContatto = serviceRequest.containsAttribute("MOTIVO_CONTATTO")? serviceRequest.getAttribute("MOTIVO_CONTATTO").toString():"";
String datascadenza = serviceRequest.containsAttribute("DATASCADENZA")? serviceRequest.getAttribute("DATASCADENZA").toString():"";
String strTipoScadenziario = serviceRequest.containsAttribute("SCADENZIARIO")? serviceRequest.getAttribute("SCADENZIARIO").toString():"";
String strFiltra = serviceRequest.containsAttribute("FILTRALISTA")? serviceRequest.getAttribute("FILTRALISTA").toString():"";
String strCodTipoVal = serviceRequest.containsAttribute("CODTIPOVALIDITA")? serviceRequest.getAttribute("CODTIPOVALIDITA").toString():"";
String strPatto297 = serviceRequest.containsAttribute("FLGPATTO")? serviceRequest.getAttribute("FLGPATTO").toString():"";
String strDirezione = serviceRequest.containsAttribute("DIREZIONE")? serviceRequest.getAttribute("DIREZIONE").toString():"";
String strTipo = serviceRequest.containsAttribute("TIPO")? serviceRequest.getAttribute("TIPO").toString():"";
String strPrgRosa = serviceRequest.containsAttribute("PRGROSA")? serviceRequest.getAttribute("PRGROSA").toString():"";
String strCpiRose = serviceRequest.containsAttribute("CPIROSE")? serviceRequest.getAttribute("CPIROSE").toString():"";
String strPrgRichiestaAz = serviceRequest.containsAttribute("PRGRICHIESTAAZ")? serviceRequest.getAttribute("PRGRICHIESTAAZ").toString():"";

String _strPrgAzienda_ 	  = serviceRequest.containsAttribute("_PRGAZIENDA_")? serviceRequest.getAttribute("_PRGAZIENDA_").toString():"";
String _strPrgUnita_   	  = serviceRequest.containsAttribute("_PRGUNITA_")? serviceRequest.getAttribute("_PRGUNITA_").toString():"";
String _strCdnLavoratore_ = serviceRequest.containsAttribute("_CDNLAVORATORE_")? serviceRequest.getAttribute("_CDNLAVORATORE_").toString():"";



User userCurr = (User) sessionContainer.getAttribute(User.USERID);
InfCorrentiLav infCorrentiLav= null;
InfCorrentiAzienda infCorrentiAzienda= null;
if (strCdnLavoratore.compareTo("") != 0) {
  infCorrentiLav = new InfCorrentiLav(sessionContainer, strCdnLavoratore, userCurr);
}
else {
  infCorrentiAzienda = new InfCorrentiAzienda(strPrgAzienda,strPrgUnita);
}

String cdnUtins="";
String dtmins="";
String cdnUtmod="";
String dtmmod="";

SourceBean cont = (SourceBean) serviceResponse.getAttribute("MDETTCONTATTO");
SourceBean row = (SourceBean) cont.getAttribute("ROWS.ROW");
cdnUtins= row.containsAttribute("cdnUtins") ? row.getAttribute("cdnUtins").toString() : "";
dtmins=row.containsAttribute("dtmins") ? row.getAttribute("dtmins").toString() : "";
cdnUtmod=row.containsAttribute("cdnUtmod") ? row.getAttribute("cdnUtmod").toString() : "";
dtmmod=row.containsAttribute("dtmmod") ? row.getAttribute("dtmmod").toString() : "";
operatoreInfo = new Testata(cdnUtins, dtmins, cdnUtmod, dtmmod);
String datContatto = row.containsAttribute("DATCONTATTO") ? row.getAttribute("DATCONTATTO").toString() : "";
String strOraContatto = row.containsAttribute("STRORACONTATTO") ? row.getAttribute("STRORACONTATTO").toString() : "";
BigDecimal prgSpiContatto = null;
prgSpiContatto = (BigDecimal) row.getAttribute("PRGSPICONTATTO");
String strSpiContatto = "";
if (prgSpiContatto != null) {
  strSpiContatto = prgSpiContatto.toString();
}
BigDecimal prgTipoContatto = null;
prgTipoContatto = (BigDecimal) row.getAttribute("PRGTIPOCONTATTO");
String strTipoContatto = "";
if (prgTipoContatto != null) {
  strTipoContatto = prgTipoContatto.toString();
}

String strDisponibilitaRosa = row.containsAttribute("STRDISPONIBILITAROSA")? row.getAttribute("STRDISPONIBILITAROSA").toString():"";

String strDirezioneContatto = row.getAttribute("STRIO").toString();
strDirezioneContatto = strDirezioneContatto.toUpperCase();
String outDirezioneContatto = "";
if (strDirezioneContatto.equalsIgnoreCase("O")) {
	outDirezioneContatto = "Dal CpI";
}
if (strDirezioneContatto.equalsIgnoreCase("I")) {
	outDirezioneContatto = "Al CpI";
}
BigDecimal prgMotivoContatto = null;
prgMotivoContatto = (BigDecimal) row.getAttribute("PRGMOTCONTATTO");
String strMotivoContatto = "";
if (prgMotivoContatto != null) {
  strMotivoContatto = prgMotivoContatto.toString();
}
String txtContatto = row.containsAttribute("TXTCONTATTO") ? row.getAttribute("TXTCONTATTO").toString() : "";
BigDecimal prgEffettoContatto = null;
prgEffettoContatto = (BigDecimal) row.getAttribute("PRGEFFETTOCONTATTO");
String strEffettoContatto = "";
if (prgEffettoContatto != null) {
  strEffettoContatto = prgEffettoContatto.toString();
}
String flgRicontattare = row.containsAttribute("FLGRICONTATTARE") ? row.getAttribute("FLGRICONTATTARE").toString() : "";
flgRicontattare = flgRicontattare.toUpperCase();
String outRicontattare = "";
if(flgRicontattare.equals("S")) {
	outRicontattare ="S&igrave";
}
if(flgRicontattare.equals("N")) {
    outRicontattare ="No";
}
 


String datEntroIl = row.containsAttribute("DATENTROIL") ? row.getAttribute("DATENTROIL").toString() : "";

String htmlStreamTop = StyleUtils.roundTopTable(false);
String htmlStreamBottom = StyleUtils.roundBottomTable(false);


String messRosa = StringUtils.getAttributeStrNotNull(serviceRequest, "MESSAGE_ROSA");
String listPageRosa = StringUtils.getAttributeStrNotNull(serviceRequest, "LIST_PAGE_ROSA");

String messScad = StringUtils.getAttributeStrNotNull(serviceRequest, "MESSAGE_SCAD");
String listPageScad = StringUtils.getAttributeStrNotNull(serviceRequest, "LIST_PAGE_SCAD");

String mess = StringUtils.getAttributeStrNotNull(serviceRequest, "MESSAGE");
String listPage = StringUtils.getAttributeStrNotNull(serviceRequest, "LIST_PAGE");
%>
  
<%@ taglib uri="aftags" prefix="af" %>
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<html>
<head>
  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <link rel="stylesheet" href="../../css/listdetail.css" type="text/css">
  <af:linkScript path="../../js/"/>
  <title>Percorso lavoratore: Contatto</title>
  <script type="text/javascript">
    function chiudi () { 
	  // Se la pagina è già in submit, ignoro questo nuovo invio!
	  if (isInSubmit()) return;

      document.frmContatto.PAGE.value = "ScadContattoPage";
      doFormSubmit(document.frmContatto);
    }
  </script> 
  
</head>
<body class="gestione">
<%--
if (strCdnLavoratore.compareTo("") != 0) {
  infCorrentiLav.show(out); 
}
else {
  infCorrentiAzienda.show(out); 
}
--%>
<af:form name="frmContatto" action="AdapterHTTP" method="POST">
<input type="hidden" name="CODCPICONTATTO" value="<%=codCpi%>">
<input type="hidden" name="CODCPI" value="<%=codCpi%>">
<input type="hidden" name="PAGE" value="ScadSalvaContattoPage">
<input type="hidden" name="PAGEPROVENIENZA" value="<%=_pageProvenienza%>">
<input type="hidden" name="PRGUNITA" value="<%=strPrgUnita%>">
<input type="hidden" name="PRGAZIENDA" value="<%=strPrgAzienda%>">
<input type="hidden" name="CDNLAVORATORE" value="<%=strCdnLavoratore%>">
<input type="hidden" name="RECUPERAINFO" value="<%=strRecuperaInfo%>">
<input type="hidden" name="CDNFUNZIONE" value="<%=_funzione%>">
<input type="hidden" name="DATASCADENZA" value="<%=datascadenza%>">
<input type="hidden" name="PRGCONTATTO" value="">
<input type="hidden" name="DATAAL" value="<%=data_al%>">
<input type="hidden" name="DATADAL" value="<%=data_dal%>">
<input type="hidden" name="MOTIVO_CONTATTO" value="<%=strDescMotivoContatto%>"/>
<input type="hidden" name="FILTRALISTA" value="<%=strFiltra%>">
<input type="hidden" name="SCADENZIARIO" value="<%=strTipoScadenziario%>"/>
<input type="hidden" name="CODTIPOVALIDITA" value="<%=strCodTipoVal%>">
<input type="hidden" name="FLGPATTO" value="<%=strPatto297%>">
<input type="hidden" name="DIREZIONE" value="<%=strDirezione%>">
<input type="hidden" name="TIPO" value="<%=strTipo%>">
<input type="hidden" name="PRGROSA" value="<%=strPrgRosa%>">
<input type="hidden" name="CPIROSE" value="<%=strCpiRose%>">
<input type="hidden" name="PRGRICHIESTAAZ" value="<%=strPrgRichiestaAz%>">

<input type="hidden" name="_PRGUNITA_" value="<%=_strPrgUnita_%>">
<input type="hidden" name="_PRGAZIENDA_" value="<%=_strPrgAzienda_%>">
<input type="hidden" name="_CDNLAVORATORE_" value="<%=_strCdnLavoratore_%>">

<input type="hidden" name="MESSAGE" value="<%=messRosa%>" disabled/>
<input type="hidden" name="LIST_PAGE" value="<%=listPageScad%>" disabled/>
<input type="hidden" name="MESSAGE_ROSA" value="<%=messRosa%>"/>
<input type="hidden" name="LIST_PAGE_ROSA" value="<%=listPageRosa%>"/>
<input type="hidden" name="MESSAGE_SCAD" value="<%=messScad%>"/>
<input type="hidden" name="LIST_PAGE_SCAD" value="<%=listPageScad%>"/>

<p align="center">
<%out.print(htmlStreamTop);%>
<table class="main">
<tr><td colspan="2"><p class="titolo">Dettaglio Contatto</p></td></tr>
<tr>
  <td class="etichetta">Data</td>
  <td class="campo">
  <af:textBox name="DATCONTATTO"
              size="11"
              maxlength="10"
              type="date"
              readonly="true"
              classNameBase="input"
              value="<%=datContatto%>"/>
  </td>
</tr>
<tr>
  <td class="etichetta">Orario</td>
  <td class="campo">
  <af:textBox name="STRORACONTATTO"
              size="5"
              maxlength="5"
              type="time"
              title="Orario"
              classNameBase="input"
              readonly="true"
              value="<%=strOraContatto%>"/>
  </td>
</tr>
<tr>
  <td class="etichetta">Operatore</td>
    <td class="campo">
      <af:comboBox name="PRGSPICONTATTO" size="1" title="Operatore"
                     multiple="false" disabled="true"
                     classNameBase="input"
                     focusOn="false" moduleName="COMBO_SPI_SCAD"
                     selectedValue="<%=strSpiContatto%>" addBlank="true" blankValue=""/>
    </td>
</tr>
<tr>
  <td class="etichetta">Tipo</td>
  
  <td class="campo">
    <af:comboBox name="PRGTIPOCONTATTO"
                 size="1"
                 title="Tipo di Contatto"
                 multiple="false" 
                 disabled="true" 
                 focusOn="false" 
                 classNameBase="input"
                 moduleName="COMBO_TIPO_CONTATTO_AG"
                 selectedValue="<%=strTipoContatto%>" 
                 addBlank="true" 
                 blankValue=""/>
    &nbsp;&nbsp;
    <af:textBox  name="STRIO"
                 size="10"
				 type="text"
                 title="Tipo di Contatto"
                 classNameBase="input"
                 readonly="true"
                 value="<%=outDirezioneContatto%>"/>
  </td>
</tr>

<tr class="note">
  <td class="etichetta">Note</td>
  <td class="campo">
    <af:textArea name="TXTCONTATTO" 
                 cols="60" 
                 rows="4" 
                 title="Note"
                 classNameBase="textarea"
                 readonly="true"
                 value="<%=txtContatto%>"/>
  </td>
</tr>
<tr>
  <td class="etichetta">Motivo</td>
  
  <td class="campo">
    <af:comboBox name="PRGMOTCONTATTO" 
                 size="1"
                 title="Motivo del Contatto"
                 multiple="false" 
                 disabled="true" 
                 focusOn="false" 
                 classNameBase="input"
                 moduleName="COMBO_MOTIVO_CONTATTO_AG"
                 selectedValue="<%=strMotivoContatto%>" 
                 addBlank="true" 
                 blankValue=""/>    
  </td>
</tr>
<tr>
  <td class="etichetta">Effetto</td>
  
  <td class="campo">
    <af:comboBox name="PRGEFFETTOCONTATTO" 
                 size="1"
                 title="Effetto del Contatto"
                 multiple="false" 
                 disabled="true"
                 classNameBase="input"
                 focusOn="false" 
                 moduleName="COMBO_EFFETTO_CONTATTO_AG"
                 selectedValue="<%=strEffettoContatto%>" 
                 addBlank="true" 
                 blankValue=""/>
  </td>
</tr>

<tr>
  <td class="etichetta">Esito disp. rosa</td>
  <td class="campo">
  <af:textBox name="STRDISPONIBILITAROSA"
              size="30"       
              title="Esito disponibilità rosa"       
              classNameBase="input"
              readonly="true"
              value="<%=strDisponibilitaRosa%>"/>
  </td>
</tr>

<tr>
  <td class="etichetta">Da Ricontattare</td>
  <td class="campo">
    <af:textBox name="FLGRICONTATTARE"
                 size="2"
                 title="Da ricontattare"
                 focusOn="false"
                 classNameBase="input"
				 readonly="true"
                 value="<%=outRicontattare%>"/>
    &nbsp;&nbsp;entro il&nbsp;&nbsp;
    <af:textBox name="DATENTROIL"
                size="11"
                maxlength="10"
                readonly="true"
                classNameBase="input"
                type="date"
                title="Ricontattare Entro il"
                value="<%=datEntroIl%>"/>
  </td>
</tr>
</table>
<br>

<%out.print(htmlStreamBottom);%>
</af:form>
<center>
	<% operatoreInfo.showHTML(out);%>	
	<br>
	<input type="button" class="pulsanti" value="Chiudi" onclick="window.close()">
</center>
</body>
</html>