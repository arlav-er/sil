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
  Object objFlgIndispensabile = null;
  String strFlgIndispensabile="";
  String strCompetenza="Consulta Competenza";
  String cdnUtins="";
  String dtmins="";
  String cdnUtmod="";
  String dtmmod="";
  String moduleName="MAggiornaCompetenzaRichiesta";
  String btnSalva="Aggiorna";
  String btnAnnulla="";
  String codTipoCompetenza="";
  String strDescTipoCompetenza="";
  String codCompetenza="";
  String strDescCompetenza="";
  
  int _funzione=Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));
  String _page = (String) serviceRequest.getAttribute("PAGE");

  Vector competenze_Rows = null;
  competenze_Rows=serviceResponse.getAttributeAsVector("M_GETCOMPETENZE.ROWS.ROW");
  SourceBean row_Dett = null;
  
  SourceBean cont = (SourceBean) serviceResponse.getAttribute("M_GETDETTAGLIOCOMPETENZARICHIESTA");
  SourceBean row = (SourceBean) cont.getAttribute("ROWS.ROW");
  BigDecimal prgCompetenza = (BigDecimal) row.getAttribute("PRGCOMPETENZA");
  String strPrgCompetenza = prgCompetenza.toString();

  Object prgAlternativa = (Object)sessionContainer.getAttribute("prgAlternativa");
  String strAlternativa="";
  if (prgAlternativa != null) {
    strAlternativa=prgAlternativa.toString();
  }

  objFlgIndispensabile=row.getAttribute("FLGINDISPENSABILE");
  if (objFlgIndispensabile != null) {
    strFlgIndispensabile = objFlgIndispensabile.toString();
  }
  
  strDescCompetenza=row.containsAttribute("DESCRIZIONECOMPETENZA") ? row.getAttribute("DESCRIZIONECOMPETENZA").toString() : "";
  strDescTipoCompetenza=row.containsAttribute("DESCRIZIONETIPOCOMPETENZA") ? row.getAttribute("DESCRIZIONETIPOCOMPETENZA").toString() : "";
  codTipoCompetenza= row.containsAttribute("CODTIPOCOMPETENZA") ? row.getAttribute("CODTIPOCOMPETENZA").toString() : "";
  codCompetenza= row.containsAttribute("CODCOMPETENZA") ? row.getAttribute("CODCOMPETENZA").toString() : "";
  cdnUtins= row.containsAttribute("cdnUtins") ? row.getAttribute("cdnUtins").toString() : "";
  dtmins=row.containsAttribute("dtmins") ? row.getAttribute("dtmins").toString() : "";
  cdnUtmod=row.containsAttribute("cdnUtmod") ? row.getAttribute("cdnUtmod").toString() : "";
  dtmmod=row.containsAttribute("dtmmod") ? row.getAttribute("dtmmod").toString() : "";
  
  Linguette linguette = new Linguette( user, _funzione, "IdoCompetenzePage", new BigDecimal(prgRichiestaAz));
  linguette.setCodiceItem("prgRichiestaAz");

  // NOTE: Attributi della pagina (pulsanti e link) 
  PageAttribs attributi = new PageAttribs(user, "IdoCompetenzePage");
  boolean canModify= attributi.containsButton("AGGIORNA");
  boolean canManage=canModify;
  btnAnnulla = canModify ? "Chiudi senza aggiornare" : "Chiudi";
  String htmlStreamTop = StyleUtils.roundTopTable(canModify);
  String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);
  
  LinguettaAlternativa linguettaAlternativa= new LinguettaAlternativa(prgAzienda, prgUnita, prgRichiestaAz, prgAlternativa,  _funzione, "IdoCompetenzePage", (!canModify));
  InfCorrentiAzienda infCorrentiAzienda= new InfCorrentiAzienda(prgAzienda,prgUnita,prgRichiestaAz);
  
  Testata operatoreInfo = null;
  operatoreInfo = new Testata(cdnUtins, dtmins, cdnUtmod, dtmmod);
%>

<html>

<head>
  <title><%=strCompetenza%></title>

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
<script language="javascript">
  function caricacombo(){   
    caricaCompetenze('<%=codTipoCompetenza%>','<%=codCompetenza%>','dettaglio');  
  }
</script>
<%@ include file="Competenze_CommonScripts.inc" %>
</head>
<body class="gestione" onLoad="caricacombo()">
<%
  infCorrentiAzienda.show(out); 
  linguettaAlternativa.show(out); 
%>
<BR/>
<%
linguette.show(out);
%>
<af:form method="POST" action="AdapterHTTP" name="MainForm">
<input type="hidden" name="PAGE" value="IdoCompetenzePage"/>
<input type="hidden" name="cdnFunzione" value="<%=_funzione%>"/>
<input type="hidden" name="PRGRICHIESTAAZ" value="<%=prgRichiestaAz%>">
<input type="hidden" name="PRGALTERNATIVA" value="<%=strAlternativa%>">
<input type="hidden" name="PRGCOMPETENZA" value="<%=strPrgCompetenza%>">
<center>
<table>
<tr>
<td align="center"><b><%=strCompetenza%></b></td>
</tr>
</table>
</center>
<br>
<%out.print(htmlStreamTop);%>
<%@ include file="Competenza_Elemento.inc" %>
<BR>
<center>
<table>
<tr>
<%
if (canModify) {
%>
  <td align="center">
    <input type="submit" class="pulsanti" name="SALVA" value="<%=btnSalva%>">
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

</html>