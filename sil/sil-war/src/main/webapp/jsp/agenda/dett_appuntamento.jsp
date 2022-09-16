<!-- @author: Stefania Orioli - Agosto 2003 -->
<%@ page contentType="text/html;charset=utf-8"%>

<%@ page import="com.engiweb.framework.base.*, 
                com.engiweb.framework.configuration.ConfigSingleton,
                 java.lang.*,
                java.text.*, java.util.*,
                com.engiweb.framework.security.*,it.eng.sil.security.*"%>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%@ include file="../global/noCaching.inc"%>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ taglib uri="cal" prefix="cal" %>
<%@ taglib uri="aftags" prefix="af" %>

<%
   requestContainer = RequestContainerAccess.getRequestContainer(request);
   serviceRequest= requestContainer.getServiceRequest();

   // Inizializzazione
   String codCpi = "";
   String cpi = "";
   String prgAppuntamento ="";
   String data = "";
   String orario = "";
   String numMinuti = "";
   String codServizio = "";
   String servizio = "";
   String prgSpi = "";
   String operatore = "";
   String txtNote = "";
   String PrgTipoPrenotazione = "";
   String tipo_prenotazione = "";
   String strTelRif = "";
   String strEmailRif = "";
   String strTelMobileRif = "";
   String codEffettoAppunt = "";
   String effetto_app = "";
   String codEsitoAppunt = "";
   String esito_app = "";
   String prgEventoAzienda = "";
   String numKloAgenda = "";
   String prgUnita = "";
   String prgAzienda = "";

   String response_xml="MDETT_APPUNTAMENTO.ROW";
   SourceBean row=(SourceBean) serviceResponse.getAttribute(response_xml);
  
   
   if (row.containsAttribute("codCpi")) { codCpi = row.getAttribute("codCpi").toString(); }
   if (row.containsAttribute("cpi")) { cpi = row.getAttribute("cpi").toString(); }
   if (row.containsAttribute("prgAppuntamento")) { prgAppuntamento = row.getAttribute("prgAppuntamento").toString(); }
   if (row.containsAttribute("data")) { data = row.getAttribute("data").toString(); }
   if (row.containsAttribute("orario")) { orario = row.getAttribute("orario").toString(); }
   if (row.containsAttribute("numMinuti")) { numMinuti = row.getAttribute("numMinuti").toString(); }
   if (row.containsAttribute("codServizio")) { codServizio = row.getAttribute("codServizio").toString(); }
   if (row.containsAttribute("servizio")) { servizio = row.getAttribute("servizio").toString(); }
   if (row.containsAttribute("prgSpi")) { prgSpi = row.getAttribute("prgSpi").toString(); }
   if (row.containsAttribute("operatore")) { operatore = row.getAttribute("operatore").toString(); }
   if (row.containsAttribute("txtNote")) { txtNote = row.getAttribute("txtNote").toString(); }
   if (row.containsAttribute("PrgTipoPrenotazione")) { PrgTipoPrenotazione = row.getAttribute("PrgTipoPrenotazione").toString(); }
   if (row.containsAttribute("tipo_prenotazione")) { tipo_prenotazione = row.getAttribute("tipo_prenotazione").toString(); }
   if (row.containsAttribute("strTelRif")) { strTelRif = row.getAttribute("strTelRif").toString(); }
   if (row.containsAttribute("strEmailRif")) { strEmailRif = row.getAttribute("strEmailRif").toString(); }
   if (row.containsAttribute("strTelMobileRif")) { strTelMobileRif = row.getAttribute("strTelMobileRif").toString(); }
   if (row.containsAttribute("codEffettoAppunt")) { codEffettoAppunt = row.getAttribute("codEffettoAppunt").toString(); }
   if (row.containsAttribute("effetto_app")) { effetto_app = row.getAttribute("effetto_app").toString(); }
   if (row.containsAttribute("codEsitoAppunt")) { codEsitoAppunt = row.getAttribute("codEsitoAppunt").toString(); }
   if (row.containsAttribute("esito_app")) { esito_app = row.getAttribute("esito_app").toString(); }
   if (row.containsAttribute("prgEventoAzienda")) { prgEventoAzienda = row.getAttribute("prgEventoAzienda").toString(); }
   if (row.containsAttribute("numKloAgenda")) { numKloAgenda = row.getAttribute("numKloAgenda").toString(); }
   if (row.containsAttribute("prgUnita")) { prgUnita = row.getAttribute("prgUnita").toString(); }
   if (row.containsAttribute("prgAzienda")) { prgAzienda = row.getAttribute("prgAzienda").toString(); }


%>

<html>
<head>
  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <link rel="stylesheet" href="../../css/listdetail.css" type="text/css">
  <title>Dettaglio Appuntamento</title>
  <af:linkScript path="../../js/" />
</head>

<body class="gestione">

<%out.print(serviceResponse.toXML());%>

<table class="def" align="center" cellspacing="1px" width="90%">
<tr valign="middle"><td colspan="2" class="bordato"><h2>Dettaglio Appuntamento</h2></td></tr>
<tr>
  <td class="sottolineato" colspan="2">(Progressivo Appuntamento: <%=prgAppuntamento%>)</td>
</tr>
<tr>
  <td class="sottolineato">Data</td>
  <td class="sottolineato"><%=data%></td>
</tr>
<tr>
  <td class="sottolineato">Ora</td>
  <td class="sottolineato"><%=orario%></td>
</tr>
<tr>
  <td class="sottolineato">Durata</td>
  <td class="sottolineato"><%=numMinuti%></td>
</tr>
<tr>
  <td class="sottolineato">CPI</td>
  <td class="sottolineato">(<%=codCpi%>) <%=cpi%></td>
</tr>
<tr>
  <td class="sottolineato">Servizio</td>
  <td class="sottolineato">(<%=codServizio%>) <%=servizio%></td>
</tr>
<tr>
  <td class="sottolineato">Operatore</td>
  <td class="sottolineato">(<%=prgSpi%>) <%=operatore%></td>
</tr>
<tr>
  <td class="sottolineato">Note</td>
  <td class="sottolineato"><%=txtNote%></td>
</tr>
<tr>
  <td class="sottolineato">Tipo Prenotazione</td>
  <td class="sottolineato">(<%=PrgTipoPrenotazione%>) <%=tipo_prenotazione%></td>
</tr>
<tr>
  <td class="sottolineato">Telefono Rif.</td>
  <td class="sottolineato"><%=strTelRif%></td>
</tr>
<tr>
  <td class="sottolineato">E-mail Rif</td>
  <td class="sottolineato"><%=strEmailRif%></td>
</tr>
<tr>
  <td class="sottolineato">Cellulare Rif.</td>
  <td class="sottolineato"><%=strTelMobileRif%></td>
</tr>
<tr>
  <td class="sottolineato">Effetto Appuntamento</td>
  <td class="sottolineato">(<%=codEffettoAppunt%>) <%=effetto_app%></td>
</tr>
<tr>
  <td class="sottolineato">Esito Appuntamento</td>
  <td class="sottolineato">(<%=codEsitoAppunt%>) <%=esito_app%></td>
</tr>
<tr>
  <td class="sottolineato">prgEventoAzienda</td>
  <td class="sottolineato">(<%=prgEventoAzienda%>)</td>
</tr>
<tr>
  <td class="sottolineato">prgUnita</td>
  <td class="sottolineato">(<%=prgUnita%>)</td>
</tr>
<tr>
  <td class="sottolineato">prgAzienda</td>
  <td class="sottolineato">(<%=prgAzienda%>)</td>
</tr>
<tr>
  <td class="sottolineato">numKloAgenda</td>
  <td class="sottolineato">(<%=numKloAgenda%>)</td>
</tr>
</table>

</body>
</html>