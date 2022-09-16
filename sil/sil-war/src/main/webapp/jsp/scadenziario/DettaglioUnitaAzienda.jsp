<%@ page contentType="text/html;charset=utf-8"%>

<%@ page import="
  com.engiweb.framework.base.*, 
  com.engiweb.framework.configuration.ConfigSingleton,
   
  it.eng.sil.util.*, 
  it.eng.afExt.utils.StringUtils,
  java.lang.*,
  java.text.*, 
  java.util.*,
  java.math.*,
  it.eng.sil.security.*"%>

<%@ taglib uri="aftags" prefix="af"%>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc"%>
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
SourceBean aziendaRow=null;
SourceBean unitaRow=null;

String strCodiceFiscale="";
String strPartitaIva="";
String strRagioneSociale="";
String strIndirizzo="";
String strLocalita="";
String desComune="";
String strCap="";
String flgMezziPub="";
String codAzStato="";
String tipo_ateco="";
String strDesAteco="";
String strResponsabile="";
String strReferente="";
String strTel="";
String strFax="";
String strEmail="";
String datInizio="";
String datFine="";
String strNote="";
String flgSede="";
String numREA="";
String cdnUtins="";
String dtmins="";
String cdnUtmod="";
String dtmmod="";

String _page = (String) serviceRequest.getAttribute("PAGE");
String _pageProvenienza = (String) serviceRequest.getAttribute("PAGEPROVENIENZA");
int _funzione=Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));
String prgAzienda = serviceRequest.containsAttribute("PRGAZIENDA")? serviceRequest.getAttribute("PRGAZIENDA").toString():"";
String strPrgUnita = serviceRequest.containsAttribute("PRGUNITA")? serviceRequest.getAttribute("PRGUNITA").toString():"";
String strCdnLavoratore = serviceRequest.containsAttribute("CDNLAVORATORE")? serviceRequest.getAttribute("CDNLAVORATORE").toString():"";
String codCpi = serviceRequest.containsAttribute("CODCPI")? serviceRequest.getAttribute("CODCPI").toString():"";
String data_al = serviceRequest.containsAttribute("DATAAL")? serviceRequest.getAttribute("DATAAL").toString():"";
String data_dal = serviceRequest.containsAttribute("DATADAL")? serviceRequest.getAttribute("DATADAL").toString():"";
String strDirezione = serviceRequest.containsAttribute("DIREZIONE")? serviceRequest.getAttribute("DIREZIONE").toString():"";
String strDescMotivoContatto = serviceRequest.containsAttribute("MOTIVO_CONTATTO")? serviceRequest.getAttribute("MOTIVO_CONTATTO").toString():"";
String datascadenza = serviceRequest.containsAttribute("DATASCADENZA")? serviceRequest.getAttribute("DATASCADENZA").toString():"";
String strTipoScadenziario = serviceRequest.containsAttribute("SCADENZIARIO")? serviceRequest.getAttribute("SCADENZIARIO").toString():"";
String strFiltra = serviceRequest.containsAttribute("FILTRALISTA")? serviceRequest.getAttribute("FILTRALISTA").toString():"";
String strCodTipoVal = serviceRequest.containsAttribute("CODTIPOVALIDITA")? serviceRequest.getAttribute("CODTIPOVALIDITA").toString():"";
String strPatto297 = serviceRequest.containsAttribute("FLGPATTO")? serviceRequest.getAttribute("FLGPATTO").toString():"";

aziendaRow= (SourceBean) serviceResponse.getAttribute("M_GetTestataAzienda.ROWS.ROW");
strCodiceFiscale= StringUtils.getAttributeStrNotNull(aziendaRow, "strCodiceFiscale");
strPartitaIva=StringUtils.getAttributeStrNotNull(aziendaRow, "strPartitaIva");
strRagioneSociale=StringUtils.getAttributeStrNotNull(aziendaRow, "strRagioneSociale");
unitaRow = (SourceBean) serviceResponse.getAttribute("M_GetUnitaAzienda.ROWS.ROW");
strIndirizzo=StringUtils.getAttributeStrNotNull(unitaRow, "strIndirizzo");
strLocalita=StringUtils.getAttributeStrNotNull(unitaRow, "strLocalita");
desComune=StringUtils.getAttributeStrNotNull(unitaRow, "strDenominazione");
desComune=(!desComune.equals(""))?desComune+" ("+StringUtils.getAttributeStrNotNull(unitaRow, "provincia")+")":"";
strCap=StringUtils.getAttributeStrNotNull(unitaRow, "strCap");
flgMezziPub=StringUtils.getAttributeStrNotNull(unitaRow, "flgMezziPub");
codAzStato=StringUtils.getAttributeStrNotNull(unitaRow, "codAzStato");
tipo_ateco =StringUtils.getAttributeStrNotNull(unitaRow, "tipo_ateco");  
strDesAteco=StringUtils.getAttributeStrNotNull(unitaRow, "strDesAteco");
strResponsabile =StringUtils.getAttributeStrNotNull(unitaRow, "strResponsabile");
strReferente =StringUtils.getAttributeStrNotNull(unitaRow, "strReferente");
strTel=StringUtils.getAttributeStrNotNull(unitaRow, "strTel");
strFax=StringUtils.getAttributeStrNotNull(unitaRow, "strFax");
strEmail=StringUtils.getAttributeStrNotNull(unitaRow, "strEmail");
datInizio=StringUtils.getAttributeStrNotNull(unitaRow, "datInizio");
datFine=StringUtils.getAttributeStrNotNull(unitaRow, "datFine");
strNote=StringUtils.getAttributeStrNotNull(unitaRow, "strNote");
flgSede=StringUtils.getAttributeStrNotNull(unitaRow, "flgSede");
numREA=unitaRow.containsAttribute("strREA") ? unitaRow.getAttribute("strREA").toString() : "";
cdnUtins= unitaRow.containsAttribute("cdnUtins") ? unitaRow.getAttribute("cdnUtins").toString() : "";
dtmins=unitaRow.containsAttribute("dtmins") ? unitaRow.getAttribute("dtmins").toString() : "";
cdnUtmod=unitaRow.containsAttribute("cdnUtmod") ? unitaRow.getAttribute("cdnUtmod").toString() : "";
dtmmod=unitaRow.containsAttribute("dtmmod") ? unitaRow.getAttribute("dtmmod").toString() : "";


String htmlStreamTop = StyleUtils.roundTopTable(false);
String htmlStreamBottom = StyleUtils.roundBottomTable(false);
%>
<html>

<head>
  <title>Azienda</title>
  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <af:linkScript path="../../js/"/> 
</head>
<body class="gestione">
<af:form name="Frm1" method="POST" action="AdapterHTTP">
<input type="hidden" name="CODCPICONTATTO" value="<%=codCpi%>">
<input type="hidden" name="CODCPI" value="<%=codCpi%>">
<input type="hidden" name="PAGE" value="ScadListaPage">
<input type="hidden" name="PAGEPROVENIENZA" value="<%=_pageProvenienza%>">
<input type="hidden" name="PRGUNITA" value="<%=strPrgUnita%>">
<input type="hidden" name="PRGAZIENDA" value="<%=prgAzienda%>">
<input type="hidden" name="CDNLAVORATORE" value="<%=strCdnLavoratore%>">
<input type="hidden" name="CDNFUNZIONE" value="<%=_funzione%>">
<input type="hidden" name="DATASCADENZA" value="<%=datascadenza%>">
<input type="hidden" name="PRGCONTATTO" value="">
<input type="hidden" name="DATAAL" value="<%=data_al%>">
<input type="hidden" name="DATADAL" value="<%=data_dal%>">
<input type="hidden" name="DIREZIONE" value="<%=strDirezione%>">
<input type="hidden" name="MOTIVO_CONTATTO" value="<%=strDescMotivoContatto%>"/>
<input type="hidden" name="FILTRALISTA" value="<%=strFiltra%>">
<input type="hidden" name="SCADENZIARIO" value="<%=strTipoScadenziario%>"/>
<input type="hidden" name="CODTIPOVALIDITA" value="<%=strCodTipoVal%>">
<input type="hidden" name="FLGPATTO" value="<%=strPatto297%>">
<%out.print(htmlStreamTop);%>
  <table class="main">
  <tr>
  <td/>
  </tr>     
  <tr>
    <td colspan="2" ><br/><br/></td>
  </tr>

  <tr>
  <td colspan="2" class="campo">
      Azienda: <b><%=strRagioneSociale%></b><br/>
      Partita Iva: <b><%=strPartitaIva%></b><br/>
      Codice Fiscale: <b><%=strCodiceFiscale%></b><br/>
      <br/>
  </td>
  </tr>

  <tr valign="top">
  <td class="etichetta">Sede legale </td>
  <%
  if (flgSede.equals("S")) {
    flgSede = "Sì";
  }
  else {
    if (flgSede.equals("N")) {
      flgSede = "No";
    }
  }
  %>
  <td class="campo"><b><%=flgSede%></b></td>
  </tr>
    
   <tr valign="top">
    <td class="etichetta">Numero REA</td>
    <td class="campo"><b><%=numREA%></b></td>
  </tr>

  <tr valign="top">
    <td class="etichetta">Indirizzo </td>
    <td class="campo"><b><%=strIndirizzo%></b></td>
  </tr>

  <tr valign="top">
    <td class="etichetta">Localita</td>
    <td class="campo"><b><%=strLocalita%></b></td>
  </tr>
    
  <tr>
    <td class="etichetta">Comune</td>
    <td class="campo"><b><%=desComune%></b></td>                
  </tr>

  <tr valign="top">
    <td class="etichetta">Cap</td>
    <td class="campo"><b><%= strCap %></b></td>
  </tr>

  <tr valign="top">
    <td class="etichetta">Stato azienda</td>
    <td class="campo">
      <af:comboBox classNameBase="input" onChange="fieldChanged();" title="Stato azienda" name="codAzStato" selectedValue="<%=codAzStato%>" disabled="true" moduleName="M_GetStatiAzienda" addBlank="true"/>
    </td>
  </tr>

  <tr valign="top">
    <td class="etichetta">Tipo</td>
    <td class="campo"><b><%=tipo_ateco%></b></td>
  </tr>
  
  <tr>
    <td class="etichetta">Attività</td>
    <td class="campo"><b><%=strDesAteco%></b></td>
  </tr>
    
  <tr valign="top">
    <td class="etichetta">Raggiungibile con mezzi pubblici</td>
    <%
    if (flgMezziPub.equals("S")) {
      flgMezziPub = "Sì";
    }
    else {
      if (flgMezziPub.equals("N")) {
        flgMezziPub = "No";
      }
    }
    %>
    <td class="campo"><b><%=flgMezziPub%></b></td>
  </tr>

  <tr valign="top">
    <td class="etichetta">Responsabile</td>
    <td class="campo"><b><%=strResponsabile%></b></td>
  </tr>

  <tr valign="top">
    <td class="etichetta">Referente</td>
    <td class="campo"><b><%=strReferente%></b></td>
  </tr>

  <tr valign="top">
    <td class="etichetta">Telefono</td>
    <td class="campo"><b><%=strTel%></b></td>
  </tr>

  <tr valign="top">
    <td class="etichetta">Fax</td>
    <td class="campo"><b><%=strFax%></b></td>
  </tr>

  <tr valign="top">
    <td class="etichetta">Email</td>
    <td class="campo"><b><%=strEmail%></b></td>
  </tr>
</table>

<table class="main">
  <tr valign="top">
    <td class="etichetta">Data di inizio dell'attività</td>
    <td class="campo"><b><%=datInizio%></b></td>
  </tr>
  <tr valign="top">
    <td class="etichetta">Data di fine dell'attività</td>
    <td class="campo"><b><%=datFine%></b></td>
  </tr>
</table>

<table class="main">
  <tr valign="top">
    <td class="etichetta">Note</td>
    <td class="campo">
      <af:textArea classNameBase="textarea" onKeyUp="fieldChanged();" name="strNote" cols="50" value="<%=strNote%>" readonly="true"/>
    </td>
  </tr>
</table>
<%out.print(htmlStreamBottom);%>
<br>
<table align="center">
<tr>
<td align="center">
<input type="submit" class="pulsanti" name="annulla" value="Chiudi">
</td>
</tr>
</table>  
</af:form>     
</body>
</html>