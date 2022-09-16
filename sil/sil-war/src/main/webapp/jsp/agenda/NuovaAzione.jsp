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
  //SourceBean contDataDefault = (SourceBean) serviceResponse.getAttribute("MSELECTDATADEFAULT");
  //SourceBean rowDataDefault = (SourceBean) contDataDefault.getAttribute("ROWS.ROW");
  //String strDataDefault = rowDataDefault.containsAttribute("DATA_DEFAULT") ? rowDataDefault.getAttribute("DATA_DEFAULT").toString() : "";
  //int nStart=strDataDefault.indexOf('\'');
  //int nEnd=strDataDefault.indexOf('\'',nStart+1);   
  //strDataDefault=strDataDefault.substring (nStart+1,nEnd);
  
  Testata operatoreInfo=null;
  int _funzione = 0;
  if (! "null".equalsIgnoreCase((String) serviceRequest.getAttribute("CDNFUNZIONE")))
	  _funzione=Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));
  
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
  String moduleName="MSalvaAzione";
  String btnSalva="Inserisci";
  String btnAnnulla="Chiudi senza inserire";
  boolean canModify = true;
  String htmlStreamTop = StyleUtils.roundTopTable(canModify);
  String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);
  

  String descObbiettivoMisuraYeiDett= "";
  String selComboObbiettivoMisuraYeiDett= "";
  String azioneDett= "";
  String prgAzioni= "";
  String datInizioValDett= "";
  String datFineValDett= "";
  String selComboPrestazioneDett= "";
  String descPrestazioneDett= "";
  String selComboTipoAttivitaDett= "";
  String descTipoAttivitaDett= "";
  String flagMisuraYeiDett= "";
  String flagPolAttivaDett= "";
  String descPrestazione= "";
  String comboObbiettivoMisuraYei= "";
  String comboTipoAttivita= "";
  String comboPrestazione= "";
  String azione= "";  
  String flagMisuraYei = "";
  String flagPolAttiva= "";
  String descObbiettivoMisuraYei = ""; 
  String descTipoAttivita = "";   


  comboObbiettivoMisuraYei = (String) serviceRequest.getAttribute("COMBOOBBIETTIVOMISURAYEI");
  comboTipoAttivita = (String) serviceRequest.getAttribute("COMBOTIPOATTIVITA");
  comboPrestazione = (String) serviceRequest.getAttribute("COMBOPRESTAZIONE");
  azione = (String) serviceRequest.getAttribute("AZIONE");  
  flagMisuraYei = (String) serviceRequest.getAttribute("FLAGMISURAYEI");
  flagPolAttiva = (String) serviceRequest.getAttribute("FLAGPOLATTIVA");
  descObbiettivoMisuraYei  = (String) serviceRequest.getAttribute("DESCOBBIETTIVOMISURAYEI");
  descTipoAttivita = (String) serviceRequest.getAttribute("DESCTIPOATTIVITA");
  descPrestazione = (String) serviceRequest.getAttribute("DESCPRESTAZIONE");

%>
<html>
<head>
  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <link rel="stylesheet" href="../../css/listdetail.css" type="text/css">
  <af:linkScript path="../../js/"/>
  <title>Nuova  Azione</title>
</head>

<body class="gestione">
<p class="titolo">Nuova Azione</p>
<%out.print(htmlStreamTop);%>
<%@ include file="dettaglio_azione.inc" %>
<%out.print(htmlStreamBottom);%> 
</body>
</html>