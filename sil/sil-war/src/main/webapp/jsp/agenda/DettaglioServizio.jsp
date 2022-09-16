<!-- @author: Giovanni Landi -->
<%@ page contentType="text/html;charset=utf-8"%>
<%@ include file="../global/noCaching.inc"%>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="com.engiweb.framework.base.*,
                com.engiweb.framework.base.SourceBean,
                com.engiweb.framework.security.*,java.math.*,
                java.lang.*,java.text.*,java.util.*, it.eng.sil.module.movimenti.constant.Properties, it.eng.sil.security.User,
                it.eng.sil.util.*, it.eng.sil.module.agenda.ShowApp,
                it.eng.afExt.utils.*"
%>


<%@ taglib uri="aftags" prefix="af" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>
<%
  Testata operatoreInfo=null;

  int _funzione = Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));

  BigDecimal numKloServizio=null;
  String codServizio="";
  String cdnUtins="";
  String cdnUtmod="";
  String dtmins="";
  String dtmmod="";
  String dtmInizioVal="";
  String dtmFineVal="";
  String strDescrizione="";
  String prgServizioArea="";
  
  String descArea = null;
  String servizio = null;
  String codArea = null;
  String validi = null;
  
  String tipoAttivita = null;
  String prestazione = null;
  String descTipoAttivita= null;
  String descPrestazione= null;
  String polAttiva= null;
  String flgPatronato = "";
  
  String prgTipoAttivita="";
  String prgPrestazione="";
   
  
  servizio = (String) serviceRequest.getAttribute("servizio");
  descArea = (String) serviceRequest.getAttribute("descArea");
  validi = (String) serviceRequest.getAttribute("validi");
  codArea = (String) serviceRequest.getAttribute("codArea");  
  
  
  descTipoAttivita = (String) serviceRequest.getAttribute("descTipoAttivita");
  
  descPrestazione = (String) serviceRequest.getAttribute("descPrestazione");
  polAttiva  = (String) serviceRequest.getAttribute("polAttiva");
  %>
  
  <%
  //tipoAttivita = (String) serviceRequest.getAttribute("tipoAttivita");
  //prestazione = (String) serviceRequest.getAttribute("prestazione");
  NavigationCache sceltaUnitaAzienda = null;
  String[] fields = {"PRESTAZIONE", "TIPOATTIVITA","POLATTIVA"};
  sceltaUnitaAzienda = new NavigationCache(fields);
  if (sessionContainer.getAttribute("SERVIZIOCACHE") != null)
  {
		sceltaUnitaAzienda = (NavigationCache) sessionContainer.getAttribute("SERVIZIOCACHE");
  		tipoAttivita = sceltaUnitaAzienda.getField("TIPOATTIVITA").toString();   
  		prestazione = sceltaUnitaAzienda.getField("PRESTAZIONE").toString();
  		polAttiva = sceltaUnitaAzienda.getField("POLATTIVA").toString();
  }
  
  String prgTipoAttivitaRicerca = (String) serviceRequest.getAttribute("prgTipoAttivitaRicerca");
  String prgPrestazioneRicerca = (String) serviceRequest.getAttribute("prgPrestazioneRicerca");
  
  SourceBean contDataDefault = (SourceBean) serviceResponse.getAttribute("MSELECTDATADEFAULT");
  SourceBean rowDataDefault = (SourceBean) contDataDefault.getAttribute("ROWS.ROW");
  String strDataDefault = rowDataDefault.containsAttribute("DATA_DEFAULT") ? rowDataDefault.getAttribute("DATA_DEFAULT").toString() : "";
  int nStart=strDataDefault.indexOf('\'');
  int nEnd=strDataDefault.indexOf('\'',nStart+1);   
  strDataDefault=strDataDefault.substring (nStart+1,nEnd);
  
  boolean visFlgPatronato = false;
  SourceBean rowConfigServizioPT = (SourceBean) serviceResponse.getAttribute("M_ConfigServizioPatronato.rows.row");
  
  if (rowConfigServizioPT != null && rowConfigServizioPT.containsAttribute("num")) {
	String numConfig = rowConfigServizioPT.getAttribute("num").toString();
	if (numConfig.equalsIgnoreCase(Properties.CUSTOM_CONFIG)) {
		visFlgPatronato = true;
	}
  }
  
  SourceBean cont = (SourceBean) serviceResponse.getAttribute("MDETTAGLIOSERVIZIO");
  SourceBean row = (SourceBean) cont.getAttribute("ROWS.ROW");
  codServizio=row.containsAttribute("CODSERVIZIO") ? row.getAttribute("CODSERVIZIO").toString() : "";
  strDescrizione=row.containsAttribute("STRDESCRIZIONE") ? row.getAttribute("STRDESCRIZIONE").toString() : "";
  dtmInizioVal=row.containsAttribute("DATINIZIOVAL") ? row.getAttribute("DATINIZIOVAL").toString() : "";
  dtmFineVal=row.containsAttribute("DATFINEVAL") ? row.getAttribute("DATFINEVAL").toString() : "";
  prgServizioArea=row.containsAttribute("CODICE") ? row.getAttribute("CODICE").toString() : "";
  cdnUtins= row.containsAttribute("cdnUtins") ? row.getAttribute("cdnUtins").toString() : "";
  dtmins=row.containsAttribute("dtmins") ? row.getAttribute("dtmins").toString() : "";
  cdnUtmod=row.containsAttribute("cdnUtmod") ? row.getAttribute("cdnUtmod").toString() : "";
  dtmmod=row.containsAttribute("dtmmod") ? row.getAttribute("dtmmod").toString() : "";
  numKloServizio = (BigDecimal) row.getAttribute("NUMKLOSERVIZIO");
  numKloServizio=numKloServizio.add(new BigDecimal(1));
  
  prgTipoAttivita=row.containsAttribute("CODICETIPOATTIVITA") ? row.getAttribute("CODICETIPOATTIVITA").toString() : "";
  prgPrestazione=row.containsAttribute("CODICEPRESTAZIONE") ? row.getAttribute("CODICEPRESTAZIONE").toString() : "";
  polAttiva=row.containsAttribute("FLGPOLATTIVA") ? row.getAttribute("FLGPOLATTIVA").toString() : "";
  flgPatronato=row.containsAttribute("FLGPATRONATO") ? row.getAttribute("FLGPATRONATO").toString() : "";
  
  String moduleName="MAggiornaServizio";
  String btnSalva="Aggiorna";
  String btnAnnulla="Chiudi senza aggiornare";
  String strNumKloServizio="";
  
  if (numKloServizio != null) {
    strNumKloServizio=numKloServizio.toString();
  }

  operatoreInfo = new Testata(cdnUtins, dtmins, cdnUtmod, dtmmod);
  //PageAttribs attributi = new PageAttribs(user, "DettaglioServizioPage");
  //boolean canModify = attributi.containsButton("salva");
  boolean canModify = true;
  if(!canModify) { btnAnnulla = "Chiudi"; }
  String htmlStreamTop = StyleUtils.roundTopTable(canModify);
  String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);
  
  String labelServizio = "Servizio";
  String umbriaGestAz = "0";
  if(serviceResponse.containsAttribute("M_CONFIG_UMB_NGE_AZ")){
  	umbriaGestAz = Utils.notNull(serviceResponse.getAttribute("M_CONFIG_UMB_NGE_AZ.ROWS.ROW.NUM"));
  }
  if(umbriaGestAz.equalsIgnoreCase("1")){
  	labelServizio = "Area";
  }
   
%>
<html>
<head>
  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <link rel="stylesheet" href="../../css/listdetail.css" type="text/css">
  <af:linkScript path="../../js/"/>
  <title>Dettaglio <%=labelServizio %></title>
</head>

<body class="gestione">

<p class="titolo">Consulta <%=labelServizio %></p>
<%out.print(htmlStreamTop);%>
<%@ include file="dettaglio_servizio.inc" %>
<div align="center">
<%operatoreInfo.showHTML(out);%>
</div>
<%out.print(htmlStreamBottom);%>

</body>
</html>