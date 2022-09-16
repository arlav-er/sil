<!-- @author: Giovanni Landi -->
<%@ page contentType="text/html;charset=utf-8"%>
<%@ include file="../global/noCaching.inc"%>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="com.engiweb.framework.base.*,
                com.engiweb.framework.base.SourceBean,
                 
                com.engiweb.framework.security.*,java.math.*,
                java.lang.*,java.text.*,java.util.*, 
                it.eng.sil.security.*,
                it.eng.sil.util.*, it.eng.sil.module.agenda.ShowApp,
                it.eng.afExt.utils.*"
%>


<%@ taglib uri="aftags" prefix="af" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>
<%
  Testata operatoreInfo=null;
  String _page = (String) serviceRequest.getAttribute("PAGE"); 
  ProfileDataFilter filter = new ProfileDataFilter(user, _page);
  int _funzione = Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));
  if (! filter.canView()){
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");

	}
  
  String cdnUtins="";
  String cdnUtmod="";
  String dtmins="";
  String dtmmod="";
  
  String comboObbiettivoMisuraYei, comboTipoAttivita, comboPrestazione, azione,  flagMisuraYei = null;
  String flagPolAttiva, descObbiettivoMisuraYei, descTipoAttivita, descPrestazione = null;   
  
  comboObbiettivoMisuraYei = (String) serviceRequest.getAttribute("COMBOOBBIETTIVOMISURAYEI");
  comboTipoAttivita = (String) serviceRequest.getAttribute("COMBOTIPOATTIVITA");
  comboPrestazione = (String) serviceRequest.getAttribute("COMBOPRESTAZIONE");
  azione = (String) serviceRequest.getAttribute("AZIONE");  
  flagMisuraYei = (String) serviceRequest.getAttribute("FLAGMISURAYEI");
  flagPolAttiva = (String) serviceRequest.getAttribute("FLAGPOLATTIVA");
  descObbiettivoMisuraYei  = (String) serviceRequest.getAttribute("DESCOBBIETTIVOMISURAYEI");
  descTipoAttivita = (String) serviceRequest.getAttribute("DESCTIPOATTIVITA");
  descPrestazione = (String) serviceRequest.getAttribute("DESCPRESTAZIONE");
   
  
  SourceBean cont = (SourceBean) serviceResponse.getAttribute("MDETTAGLIOAZIONE");
  SourceBean row = (SourceBean) cont.getAttribute("ROWS.ROW");
  
  //dati letti dalla query di dettaglio
  String descObbiettivoMisuraYeiDett=row.containsAttribute("DESCOBBIETTIVOMISURAYEI") ? row.getAttribute("DESCOBBIETTIVOMISURAYEI").toString() : "";
  String selComboObbiettivoMisuraYeiDett=row.containsAttribute("PRGAZIONIRAGG") ? row.getAttribute("PRGAZIONIRAGG").toString() : "";
  String azioneDett=row.containsAttribute("AZIONE") ? row.getAttribute("AZIONE").toString() : "";
  String prgAzioni=row.containsAttribute("PRGAZIONI") ? row.getAttribute("PRGAZIONI").toString() : "";
  String datInizioValDett=row.containsAttribute("DATINIZIOVAL") ? row.getAttribute("DATINIZIOVAL").toString() : "";
  String datFineValDett=row.containsAttribute("DATFINEVAL") ? row.getAttribute("DATFINEVAL").toString() : "";
  String selComboPrestazioneDett=row.containsAttribute("COMBOPRESTAZIONE") ? row.getAttribute("COMBOPRESTAZIONE").toString() : "";
  String descPrestazioneDett=row.containsAttribute("DESCPRESTAZIONE") ? row.getAttribute("DESCPRESTAZIONE").toString() : "";
  String selComboTipoAttivitaDett=row.containsAttribute("COMBOTIPOATTIVITA") ? row.getAttribute("COMBOTIPOATTIVITA").toString() : "";
  String descTipoAttivitaDett=row.containsAttribute("DESCTIPOATTIVITA") ? row.getAttribute("DESCTIPOATTIVITA").toString() : "";
  String flagMisuraYeiDett=row.containsAttribute("FLAGMISURAYEI") ? row.getAttribute("FLAGMISURAYEI").toString() : "";
  String flagPolAttivaDett=row.containsAttribute("FLAGPOLATTIVA") ? row.getAttribute("FLAGPOLATTIVA").toString() : "";
  // fine

  
  cdnUtins= row.containsAttribute("cdnUtins") ? row.getAttribute("cdnUtins").toString() : "";
  dtmins=row.containsAttribute("dtmins") ? row.getAttribute("dtmins").toString() : "";
  cdnUtmod=row.containsAttribute("cdnUtmod") ? row.getAttribute("cdnUtmod").toString() : "";
  dtmmod=row.containsAttribute("dtmmod") ? row.getAttribute("dtmmod").toString() : "";
  
  
  //String moduleName="MAggiornaAzione";
  String moduleName="MAggiornaAzione"; //
  String btnSalva="Aggiorna";
  String btnAnnulla="Torna alla lista";

  operatoreInfo = new Testata(cdnUtins, dtmins, cdnUtmod, dtmmod);
  //PageAttribs attributi = new PageAttribs(user, "DettaglioServizioPage");
  //boolean canModify = attributi.containsButton("salva");
  boolean canModify = false;
  if(canModify) { btnAnnulla = "Chiudi"; }
  String htmlStreamTop = StyleUtils.roundTopTable(canModify);
  String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);
   
%>
<html>
<head>
  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <link rel="stylesheet" href="../../css/listdetail.css" type="text/css">
  <af:linkScript path="../../js/"/>
  <title>Dettaglio Azione</title>
</head>

<body class="gestione">

<p class="titolo">Consulta Azione</p>
<%out.print(htmlStreamTop);%>
<%@ include file="dettaglio_azione.inc" %>
<div align="center">
<%operatoreInfo.showHTML(out);%>
</div>
<%out.print(htmlStreamBottom);%>

</body>
</html>