<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="com.engiweb.framework.base.*,
  com.engiweb.framework.dispatching.module.AbstractModule,
  
  com.engiweb.framework.util.QueryExecutor,
  it.eng.sil.security.User,
  it.eng.afExt.utils.*,
  it.eng.sil.util.*,
  java.util.*,
  java.math.*,
  java.io.*,
  com.engiweb.framework.dbaccess.sql.TimestampDecorator,
  it.eng.sil.security.PageAttribs,
  com.engiweb.framework.security.*"%>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
  String prgAzienda="";
  String prgUnita="";
  prgAzienda =  (String)serviceRequest.getAttribute("PRGAZIENDA");
  prgUnita = (String)serviceRequest.getAttribute("PRGUNITA");
  String prgRichiestaAz = (String)serviceRequest.getAttribute("PRGRICHIESTAAZ");
  String strConoscenza="Consulta Conoscenza Informatica";
  String cdnUtins="";
  String dtmins="";
  String cdnUtmod="";
  String dtmmod="";
  String moduleName="MAggiornaInfoRichiesta";
  String btnSalva="Aggiorna";
  String btnAnnulla;
  String codTipo="";
  String strDescTipo="";
  String strDescDettTipo="";
  String codDettTipo="";
  Object objFlgIndispensabile = null;
  String strFlgIndispensabile="";
  
  int _funzione=Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));
  String _page = (String) serviceRequest.getAttribute("PAGE");
  
  Vector tipiDettInfoRows = null;
  tipiDettInfoRows=serviceResponse.getAttributeAsVector("M_LISTDETTAGLIALLCONOSCENZAINFO.ROWS.ROW");
  SourceBean row_dettInfo = null;
  
  SourceBean cont = (SourceBean) serviceResponse.getAttribute("M_GETDETTAGLIOINFORICHIESTA");
  SourceBean row = (SourceBean) cont.getAttribute("ROWS.ROW");
  BigDecimal prgInfo = (BigDecimal) row.getAttribute("PRGINFO");
  String strPrgInfo = prgInfo.toString();

  Object prgAlternativa = (Object)sessionContainer.getAttribute("prgAlternativa");
  String strAlternativa="";
  if (prgAlternativa != null) {
    strAlternativa=prgAlternativa.toString();
  }
  
  strDescTipo=row.containsAttribute("STRDESCRIZIONE") ? row.getAttribute("STRDESCRIZIONE").toString() : "";
  codTipo= row.containsAttribute("CODTIPOINFO") ? row.getAttribute("CODTIPOINFO").toString() : "";
  codDettTipo= row.containsAttribute("CODICE") ? row.getAttribute("CODICE").toString() : "";
  cdnUtins= row.containsAttribute("cdnUtins") ? row.getAttribute("cdnUtins").toString() : "";
  dtmins=row.containsAttribute("dtmins") ? row.getAttribute("dtmins").toString() : "";
  cdnUtmod=row.containsAttribute("cdnUtmod") ? row.getAttribute("cdnUtmod").toString() : "";
  dtmmod=row.containsAttribute("dtmmod") ? row.getAttribute("dtmmod").toString() : "";
  
  Linguette linguette = new Linguette( user, _funzione, "IdoInformaticaPage", new BigDecimal(prgRichiestaAz));
  linguette.setCodiceItem("prgRichiestaAz");

  // NOTE: Attributi della pagina (pulsanti e link) 
  PageAttribs attributi = new PageAttribs(user, "IdoInformaticaPage");
  boolean canModify= attributi.containsButton("AGGIORNA");
  boolean canManage=canModify;
  btnAnnulla= canModify ? "Chiudi senza aggiornare" : "Chiudi";
  String htmlStreamTop = StyleUtils.roundTopTable(canModify);
  String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);
  LinguettaAlternativa linguettaAlternativa= new LinguettaAlternativa(prgAzienda, prgUnita, prgRichiestaAz, prgAlternativa,  _funzione, "IdoInformaticaPage", (!canModify));
  InfCorrentiAzienda infCorrentiAzienda= new InfCorrentiAzienda(prgAzienda,prgUnita,prgRichiestaAz);
  
  BigDecimal cdnGradoConoscenza=row.containsAttribute("CDNGRADO") ? (BigDecimal)row.getAttribute("CDNGRADO") : null;
  String strGradoConoscenza = "";

  if (cdnGradoConoscenza != null) {
    strGradoConoscenza = cdnGradoConoscenza.toString();
  }
  
  objFlgIndispensabile=row.getAttribute("FLGINDISPENSABILE");
  if (objFlgIndispensabile != null) {
    strFlgIndispensabile = objFlgIndispensabile.toString();
  }

  Testata operatoreInfo = null;
  operatoreInfo = new Testata(cdnUtins, dtmins, cdnUtmod, dtmmod);
%>

<html>

<head>
  <title>Informatica</title>

  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>

  <af:linkScript path="../../js/"/>

  <SCRIPT TYPE="text/javascript">
  var flagChanged = false;
  window.top.menu.caricaMenuAzienda(<%=_funzione%>, <%=prgAzienda%>, <%=prgUnita%>);
  
  </SCRIPT>
  <script language="Javascript">
     <% 
     	//Genera il Javascript che si occuperà di inserire i links nel footer
        attributi.showHyperLinks(out, requestContainer,responseContainer,"");
      %>
</script>
<%@ include file="ConoInfo_CommonScripts.inc" %>
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
<af:form method="POST" action="AdapterHTTP" name="MainForm">
<input type="hidden" name="PAGE" value="IdoInformaticaPage"/>
<input type="hidden" name="cdnFunzione" value="<%=_funzione%>"/>
<input type="hidden" name="PRGRICHIESTAAZ" value="<%=prgRichiestaAz%>">
<input type="hidden" name="PRGALTERNATIVA" value="<%=strAlternativa%>">
<input type="hidden" name="PRGINFO" value="<%=strPrgInfo%>">
<%out.print(htmlStreamTop);%>
<%@ include file="ConoInfo_Elemento.inc" %>
<center>
<BR>
<table>
<tr>
<%
if (canModify) {
%>
<td align="center">
  <input type="submit" class="pulsanti" name="salva" value="<%=btnSalva%>">
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
</center>
</af:form>
<% operatoreInfo.showHTML(out); %>
</body>
<script language="javascript">
  caricaDettInfo('<%=codTipo%>','<%=codDettTipo%>','dettaglio');
  if (!<%=canModify%>) { document.MainForm.CODICE.disabled = true; }
</script>
</html>