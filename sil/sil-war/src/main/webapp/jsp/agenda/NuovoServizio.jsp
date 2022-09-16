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
  
  Testata operatoreInfo=null;
  int _funzione=Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));
  String codServizio="";
  String prgServizioArea="";
  String strNumKloServizio="";
  String cdnUtins="";
  String dtmins="";
  String cdnUtmod="";
  String dtmmod="";
  String strDescrizione="";
  String dtmInizioVal="";
  String dtmFineVal="";
  String moduleName="MSalvaServizio";
  String btnSalva="Inserisci";
  String btnAnnulla="Chiudi senza inserire";
  boolean canModify = true;
  String htmlStreamTop = StyleUtils.roundTopTable(canModify);
  String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);
  
  String descArea = null;
  String servizio = null;
  String codArea = null;
  String validi = null;
  servizio = (String) serviceRequest.getAttribute("servizio");
  descArea = (String) serviceRequest.getAttribute("descArea");
  validi = (String) serviceRequest.getAttribute("validi");
  codArea = (String) serviceRequest.getAttribute("codArea");  
  
  String tipoAttivita = null;
  String prestazione = null;
  String descTipoAttivita = null;
  String descPrestazione = null;
  String polAttiva= "";
  String flgPatronato = "";
  String prgTipoAttivita="";
  String prgPrestazione="";

  tipoAttivita = (String) serviceRequest.getAttribute("tipoAttivita");
  prestazione = (String) serviceRequest.getAttribute("prestazione");
  descTipoAttivita = (String) serviceRequest.getAttribute("descTipoAttivita");
  descPrestazione = (String) serviceRequest.getAttribute("descPrestazione");
  polAttiva = (String) serviceRequest.getAttribute("polAttiva");
  
  String labelServizio ="Servizio";
  String umbriaGestAz = "0";
  if(serviceResponse.containsAttribute("M_CONFIG_UMB_NGE_AZ")){
  	umbriaGestAz = Utils.notNull(serviceResponse.getAttribute("M_CONFIG_UMB_NGE_AZ.ROWS.ROW.NUM"));
  }
  if(umbriaGestAz.equals("1")){
  	labelServizio = "Area";
  }
%>
<html>
<head>
  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <link rel="stylesheet" href="../../css/listdetail.css" type="text/css">
  <af:linkScript path="../../js/"/>
  <title>Nuovo Servizio</title>
</head>

<body class="gestione">
<p class="titolo">Nuovo Servizio</p>
<%out.print(htmlStreamTop);%>
<%@ include file="dettaglio_servizio.inc" %>
<%out.print(htmlStreamBottom);%> 
</body>
</html>