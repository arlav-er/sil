<!-- @author: Giovanni Landi -->
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
  com.engiweb.framework.security.*" 
%>
  
<%@ taglib uri="aftags" prefix="af" %>
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
  int _funzione=Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));
  BigDecimal prgAzienda=null;
  BigDecimal prgUnita=null;
  String strPrgAziendaMenu="";
  String strPrgUnitaMenu="";
  strPrgAziendaMenu = (String) serviceRequest.getAttribute("PRGAZIENDA");
  strPrgUnitaMenu = (String) serviceRequest.getAttribute("PRGUNITA");

  String _page = "GestLingueRichiestaPage";
  PageAttribs attributi = new PageAttribs(user, _page);
  boolean canModify= attributi.containsButton("AGGIORNA");
  boolean canManage=canModify;
  Testata operatoreInfo=null;
  BigDecimal prgRichiestaAz = null;
  BigDecimal cdnGradoLetto = null;
  BigDecimal cdnGradoScritto = null;
  BigDecimal cdnGradoParlato = null;
  String codLingua="";
  String cdnUtins="";
  String dtmins="";
  String cdnUtmod="";
  String dtmmod="";
  String moduleName="MAggiornaLinguaRichiesta";
  String btnSalva="Aggiorna";
  String btnAnnulla= canModify ? "Chiudi senza aggiornare" : "Chiudi";
  Object objFlgIndispensabile = null;
  String strFlgIndispensabile="";

  Object prgAlternativa = (Object)sessionContainer.getAttribute("prgAlternativa");
  String strAlternativa="";
  if (prgAlternativa != null) {
    strAlternativa=prgAlternativa.toString();
  }
  
  SourceBean cont = (SourceBean) serviceResponse.getAttribute("SelectDettaglioLinguaRichiesta");
  SourceBean row = (SourceBean) cont.getAttribute("ROWS.ROW");
  BigDecimal prgLingua = (BigDecimal) row.getAttribute("PRGLINGUA");

  cdnUtins= row.containsAttribute("cdnUtins") ? row.getAttribute("cdnUtins").toString() : "";
  dtmins=row.containsAttribute("dtmins") ? row.getAttribute("dtmins").toString() : "";
  cdnUtmod=row.containsAttribute("cdnUtmod") ? row.getAttribute("cdnUtmod").toString() : "";
  dtmmod=row.containsAttribute("dtmmod") ? row.getAttribute("dtmmod").toString() : "";
  
  if (prgLingua != null) {
    operatoreInfo = new Testata(cdnUtins, dtmins, cdnUtmod, dtmmod);
  }
  String strRichiestaAz=(String)serviceRequest.getAttribute("PRGRICHIESTAAZ");
  prgRichiestaAz = new BigDecimal(strRichiestaAz);
  codLingua=row.getAttribute("CODLINGUA").toString();
  cdnGradoLetto=row.containsAttribute("CDNGRADOLETTO") ? (BigDecimal)row.getAttribute("CDNGRADOLETTO") : null;
  cdnGradoScritto=row.containsAttribute("CDNGRADOSCRITTO") ? (BigDecimal)row.getAttribute("CDNGRADOSCRITTO") : null;
  cdnGradoParlato=row.containsAttribute("CDNGRADOPARLATO") ? (BigDecimal)row.getAttribute("CDNGRADOPARLATO") : null;
  objFlgIndispensabile=row.getAttribute("FLGINDISPENSABILE");
  if (objFlgIndispensabile != null) {
    strFlgIndispensabile = objFlgIndispensabile.toString();
  }
  LinguettaAlternativa linguettaAlternativa= new LinguettaAlternativa(prgAzienda, prgUnita, prgRichiestaAz, prgAlternativa,  _funzione, _page, (!canModify));
  Linguette linguette = new Linguette( user, _funzione, _page, new BigDecimal(strRichiestaAz));
  linguette.setCodiceItem("prgRichiestaAz");
  InfCorrentiAzienda infCorrentiAzienda= new InfCorrentiAzienda(strPrgAziendaMenu,strPrgUnitaMenu,strRichiestaAz);
  String htmlStreamTop = StyleUtils.roundTopTable(canModify);
  String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);
%>
<html>
<head>
  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <link rel="stylesheet" href="../../css/listdetail.css" type="text/css">
  <af:linkScript path="../../js/"/>
  <title>Dettaglio Conoscenza Linguistica</title>
  <script language="Javascript">
    window.top.menu.caricaMenuAzienda(<%=_funzione%>, <%=strPrgAziendaMenu%>, <%=strPrgUnitaMenu%>);
  </script>
  <script language="Javascript">
     <% 
     	//Genera il Javascript che si occuperà di inserire i links nel footer
        attributi.showHyperLinks(out, requestContainer,responseContainer,"");
      %>
</script>
</head>
<body class="gestione"  >
<%
  infCorrentiAzienda.show(out); 
  linguettaAlternativa.show(out); 
%>
<BR/>
<%
linguette.show(out);
%>
<br>
<af:form name="frmMascheraLinguaRichiesta" action="AdapterHTTP" method="POST">
<input type="hidden" name="PAGE" value="<%=_page%>">
<input type="hidden" name="MODULE" value="<%=moduleName%>">
<input type="hidden" name="PRGRICHIESTAAZ" value="<%=prgRichiestaAz%>">
<input type="hidden" name="PRGLINGUA" value="<%=prgLingua%>">
<input type="hidden" name="CDNFUNZIONE" value="<%=_funzione%>">
<p align="center">
<%out.print(htmlStreamTop);%>
<table class="main">
<%@ include file="DettaglioLinguaRichiesta.inc" %>
</table>
<br>
<table>
<tr>
<%
if (canModify) {
%>
  <td align="center">
  <input type="submit" class="pulsanti" name="salva" value="<%=btnSalva%>" onclick="javascript:ControllaGrado();">
  </td>
<%
}
%>
<td align="center">
  <input type="submit" class="pulsanti" name="ANNULLA" value="<%=btnAnnulla%>">
</td>
</tr>
</table>
<%out.print(htmlStreamBottom);%>
</af:form>
<%if (prgLingua != null) operatoreInfo.showHTML(out);%>
</body>
</html>