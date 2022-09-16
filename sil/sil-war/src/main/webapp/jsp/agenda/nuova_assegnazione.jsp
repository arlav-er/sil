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
  String CODCPI;
  String prgAssegnazione = "";
  String numKloAssegnazione = "";
  String codServizio="";
  BigDecimal prgSpiAssegnazione = null;
  BigDecimal prgAmbiente = null;

  int cdnUt = user.getCodut();
  int cdnTipoGruppo = user.getCdnTipoGruppo();
    
  if(cdnTipoGruppo==1) { 
    CODCPI =  user.getCodRif(); 
  }
  else { 
    CODCPI = requestContainer.getAttribute("agenda_codCpi").toString(); 
  }
  String moduleName="INS";
  String btnSalva="Inserisci";
  String btnAnnulla="Chiudi senza inserire";
  boolean canModify = true;
  String htmlStreamTop = StyleUtils.roundTopTable(canModify);
  String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);
  
 String tok = "_TOKEN_" + "GESTASSEGNAZIONEPAGE";
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
  <title>Nuova Assegnazione</title>
</head>

<body class="gestione">
<p class="titolo">Nuova Assegnazione</p>
<%out.print(htmlStreamTop);%>
<%@ include file="dett_assegnazione.inc" %>
<%out.print(htmlStreamBottom);%>
</body>
</html>
