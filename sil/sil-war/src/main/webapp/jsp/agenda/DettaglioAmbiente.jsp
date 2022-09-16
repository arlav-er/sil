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
  SourceBean contDataDefault = (SourceBean) serviceResponse.getAttribute("MSELECTDATADEFAULT");
  SourceBean rowDataDefault = (SourceBean) contDataDefault.getAttribute("ROWS.ROW");
  String strDataDefault = rowDataDefault.containsAttribute("DATA_DEFAULT") ? rowDataDefault.getAttribute("DATA_DEFAULT").toString() : "";
  int nStart=strDataDefault.indexOf('\'');
  int nEnd=strDataDefault.indexOf('\'',nStart+1);   
  strDataDefault=strDataDefault.substring (nStart+1,nEnd);
  
  Testata operatoreInfo=null;
  int _funzione=Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));
  String codCpi=(String) serviceRequest.getAttribute("CODCPI");
  String prgAmbiente = "";
  String strNumKloAmbiente="";
  String cdnUtins="";
  String dtmins="";
  String cdnUtmod="";
  String dtmmod="";
  String strDescrizione="";
  String dtmInizioVal="";
  String dtmFineVal="";
  String moduleName="MSalvaAmbiente";
  String btnSalva="Inserisci";
  String btnAnnulla="Chiudi senza inserire";
  String strCapacita="";
  String strCapienza="";
  boolean canModify = true;
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
  <title>Dettaglio Assegnazione</title>
</head>

<body class="gestione">
<p class="titolo">Nuovo Ambiente</p>
<%out.print(htmlStreamTop);%>
<%@ include file="dettaglio_ambiente.inc" %>
<%out.print(htmlStreamBottom);%>
</body>
</html>