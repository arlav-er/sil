<!-- @author: Stefania Orioli -->
<%@ page contentType="text/html;charset=utf-8"%>
<%@ include file="../global/noCaching.inc"%>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="com.engiweb.framework.base.*,
                com.engiweb.framework.base.SourceBean,
                 
                com.engiweb.framework.security.*,
                it.eng.afExt.utils.*, java.math.*,
                java.lang.*,java.text.*,java.util.*, it.eng.sil.security.User,
                it.eng.sil.util.*, it.eng.sil.module.agenda.ShowApp"
%>

<%@ taglib uri="cal" prefix="cal" %>
<%@ taglib uri="aftags" prefix="af" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>



<%
String streamDescrCpi = "";
SourceBean content = (SourceBean) serviceResponse.getAttribute("MDESCRCPI");
if(content!=null) {
  SourceBean row = (SourceBean) content.getAttribute("ROWS.ROW");
  if(row!=null) { streamDescrCpi = row.getAttribute("STRDESCRIZIONE").toString(); }
}
%>


<%
String giornoDB = "";
String meseDB = "";
String annoDB = "";
// Istanzio giornoDB, meseDB, annoDB
// Data Odierna
Calendar oggi = Calendar.getInstance();
giornoDB = Integer.toString(oggi.get(5));
meseDB = Integer.toString(oggi.get(2)+1);
annoDB = Integer.toString(oggi.get(1));

String nrosDB = "";
String giorno = "";
String mese = "";
String anno = "";
String cod_vista = "";

int mod = 0;

if(serviceRequest.containsAttribute("giorno")) { giorno = serviceRequest.getAttribute("giorno").toString(); }
if(serviceRequest.containsAttribute("mese")) { mese = serviceRequest.getAttribute("mese").toString(); }
if(serviceRequest.containsAttribute("anno")) { anno = serviceRequest.getAttribute("anno").toString(); }
if(serviceRequest.containsAttribute("nrosDB")) { nrosDB = serviceRequest.getAttribute("nrosDB").toString(); }
if(serviceRequest.containsAttribute("giornoDB")) { giornoDB = serviceRequest.getAttribute("giornoDB").toString(); }
if(serviceRequest.containsAttribute("meseDB")) { meseDB = serviceRequest.getAttribute("meseDB").toString(); }
if(serviceRequest.containsAttribute("annoDB")) { annoDB = serviceRequest.getAttribute("annoDB").toString(); }
if(serviceRequest.containsAttribute("MOD")) { mod = Integer.parseInt(serviceRequest.getAttribute("MOD").toString()); }

String MOD = Integer.toString(mod);
String htmlStream = "";
if(mod == 1) {
  //htmlStream = ShowApp.listaAppSettimana(requestContainer, serviceResponse);
}

int cdnUt = user.getCodut();
int cdnTipoGruppo = user.getCdnTipoGruppo();
String codCpi;
if(cdnTipoGruppo==1) { codCpi =  user.getCodRif(); }
else { codCpi = requestContainer.getAttribute("agenda_codCpi").toString(); }
%>

<html>
<head>
  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <link rel="stylesheet" href="../../css/listdetail.css" type="text/css">
  <title>Calendario</title>
  <af:linkScript path="../../js/" />
  <script language="Javascript" src="../../js/utili.js" type="text/javascript"></script>
</head>

<body class="gestione" onload="rinfresca()">
<h2>
  <%if(!streamDescrCpi.equals("")) {%>
  <b>Gestione Festivit&agrave; - Centro per l'Impiego di <%=streamDescrCpi%></b>
  <%}%>
</h2>

<table align="center" width="96%" maxwidth="96%" cellspacing="0" cellpadding="0">
<tr class="note">
  <td align="center" width="250px">
      <!-- Calendario -->
      <cal:calendario moduleName="MGiorni_NL"  mod="<%=MOD%>" pageName="FestiviPage" attivaFestivi="1" codCpi="<%=codCpi%>"/>
  </td>
  <td width="16px">&nbsp;</td>
  <td align="left" valign="top">
    <% if(mod==0) { %>
          <%
            String htmlTxt = ShowApp.infoGiorno(requestContainer, serviceResponse);
            out.print(htmlTxt);
          %>
    <% } else { %>
          <% if(mod==1) {%>
             <%
              String htmlTxtSett = ShowApp.infoSettimana(requestContainer, serviceResponse);
              out.print(htmlTxtSett);
             %>
          <% }%>
    <%} // if(mod==0)%>
  </td>
</tr>
</table>
</body>
</html>
