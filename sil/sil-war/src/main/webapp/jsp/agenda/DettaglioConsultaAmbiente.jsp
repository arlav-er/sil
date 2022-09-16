<!-- @author: Giovanni Landi -->
<%@ page contentType="text/html;charset=utf-8"%>
<%@ include file="../global/noCaching.inc"%>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="com.engiweb.framework.base.*,
                com.engiweb.framework.base.SourceBean,
                 
                com.engiweb.framework.security.*,java.math.*,
                java.lang.*,java.text.*,java.util.*, it.eng.sil.security.User,
                it.eng.sil.util.*, it.eng.sil.module.agenda.ShowApp,
                it.eng.afExt.utils.*"
%>


<%@ taglib uri="aftags" prefix="af" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>
<%
  Testata operatoreInfo=null;
  String codCpi=(String) serviceRequest.getAttribute("CODCPI");
  int _funzione=Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));
  BigDecimal numKloAmbiente=null;
  BigDecimal prg=null;
  String cdnUtins="";
  String cdnUtmod="";
  String dtmins="";
  String dtmmod="";
  String dtmInizioVal="";
  String dtmFineVal="";
  String strDescrizione="";

  SourceBean contDataDefault = (SourceBean) serviceResponse.getAttribute("MSELECTDATADEFAULT");
  SourceBean rowDataDefault = (SourceBean) contDataDefault.getAttribute("ROWS.ROW");
  String strDataDefault = rowDataDefault.containsAttribute("DATA_DEFAULT") ? rowDataDefault.getAttribute("DATA_DEFAULT").toString() : "";
  int nStart=strDataDefault.indexOf('\'');
  int nEnd=strDataDefault.indexOf('\'',nStart+1);   
  strDataDefault=strDataDefault.substring (nStart+1,nEnd);
  
  SourceBean cont = (SourceBean) serviceResponse.getAttribute("MDETTAGLIOAMBIENTE");
  SourceBean row = (SourceBean) cont.getAttribute("ROWS.ROW");
  prg = (BigDecimal) row.getAttribute("PRGAMBIENTE");
  strDescrizione=row.containsAttribute("STRDESCRIZIONE") ? row.getAttribute("STRDESCRIZIONE").toString() : "";
  dtmInizioVal=row.containsAttribute("DATINIZIOVAL") ? row.getAttribute("DATINIZIOVAL").toString() : "";
  dtmFineVal=row.containsAttribute("DATFINEVAL") ? row.getAttribute("DATFINEVAL").toString() : "";
  BigDecimal numCapacita = (BigDecimal) row.getAttribute("NUMCAPACITA");
  BigDecimal numCapienza = (BigDecimal) row.getAttribute("NUMCAPIENZA");
  cdnUtins= row.containsAttribute("cdnUtins") ? row.getAttribute("cdnUtins").toString() : "";
  dtmins=row.containsAttribute("dtmins") ? row.getAttribute("dtmins").toString() : "";
  cdnUtmod=row.containsAttribute("cdnUtmod") ? row.getAttribute("cdnUtmod").toString() : "";
  dtmmod=row.containsAttribute("dtmmod") ? row.getAttribute("dtmmod").toString() : "";
  numKloAmbiente = (BigDecimal) row.getAttribute("NUMKLOAMBIENTE");
  numKloAmbiente=numKloAmbiente.add(new BigDecimal(1));
  String moduleName="MAggiornaAmbiente";
  //String btnSalva="Aggiorna";
  //String btnAnnulla="Chiudi senza salvare";
  String prgAmbiente="";
  String strNumKloAmbiente="";
  
  if (prg != null) {
    prgAmbiente=prg.toString();
  }
  if (numKloAmbiente != null) {
    strNumKloAmbiente=numKloAmbiente.toString();
  }

  String strCapacita="";
  String strCapienza="";
  if (numCapacita != null) {
    strCapacita=numCapacita.toString();
  }
  if (numCapienza != null) {
    strCapienza=numCapienza.toString();
  }

  String btnSalva = "Aggiorna";
  String btnAnnulla = "Chiudi senza aggiornare";
  //PageAttribs attributi = new PageAttribs(user, "SELECT_AGENDA_PAGE");
  //boolean canModify = attributi.containsButton("salva");
  boolean canModify = true;
  if(!canModify) { btnAnnulla = "Chiudi"; }

  operatoreInfo = new Testata(cdnUtins, dtmins, cdnUtmod, dtmmod);
  String htmlStreamTop = StyleUtils.roundTopTable(canModify);
  String htmlStreamBottom = StyleUtils.roundBottomTable(canModify); 

 String tok = "_TOKEN_" + "GESTAMBIENTIPAGE";
 String url = (String) sessionContainer.getAttribute(tok.toUpperCase());
 int i = 0;
 int j = 0;
 
 i = url.indexOf("LIST_PAGE=") + "LIST_PAGE=".length();
 j = url.indexOf("&",i);
 if (j==-1) { j=url.length(); }
 String strListPage = url.substring(i,j);

 i = url.indexOf("MESSAGE=") + "MESSAGE=".length();;
 j = url.indexOf("&",i);
 if (j==-1) { j=url.length(); }
 String strMessage = url.substring(i,j);
  
%>
<html>
<head>
  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <link rel="stylesheet" href="../../css/listdetail.css" type="text/css">
  <af:linkScript path="../../js/"/>
  <title>Dettaglio Ambiente</title>
</head>

<body class="gestione">
<p class="titolo">Consulta Ambiente</p>
<%out.print(htmlStreamTop);%>
<%@ include file="dettaglio_ambiente.inc" %>
<div align="center">
<%operatoreInfo.showHTML(out);%>
</div>
<%out.print(htmlStreamBottom);%>

</body>
</html>