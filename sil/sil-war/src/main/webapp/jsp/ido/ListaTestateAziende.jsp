<%@ page contentType="text/html;charset=utf-8"%>
<%@ page import="
  com.engiweb.framework.base.*, 
  com.engiweb.framework.configuration.ConfigSingleton,
   
  it.eng.sil.util.*, 
  it.eng.afExt.utils.StringUtils,
  java.lang.*,
  java.text.*, 
  java.util.*,
  java.math.*,
  it.eng.sil.security.*"%>

<%@ taglib uri="aftags" prefix="af"%>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc"%>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%

  String _page = (String) serviceRequest.getAttribute("PAGE"); 
  ProfileDataFilter filter = new ProfileDataFilter(user, _page);
  
  if (! filter.canView()){
	response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
  }else{

  int _funzione = Integer.parseInt((String)serviceRequest.getAttribute("CDNFUNZIONE"));
  
  PageAttribs attributi = new PageAttribs(user, (String)serviceRequest.getAttribute("PAGE"));
  boolean canDoNuovo   = attributi.containsButton("nuovo");
%>

<html>

<head>
  <title>Lista testate aziende</title>

  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>

  <af:linkScript path="../../js/"/>
  <script language="JavaScript" src="../../js/layers.js"></script> 
  <%@ include file="../global/Function_CommonRicercaComune.inc" %>
  <SCRIPT TYPE="text/javascript">

function Select(prgAzienda, prgUnita) {
  	// Se la pagina è già in submit, ignoro questo nuovo invio!
    if (isInSubmit()) return;

    var s= "AdapterHTTP?PAGE=IdoTestataAziendaPage" +
    		"&PRGAZIENDA=" + prgAzienda +
    		"&CDNFUNZIONE=<%=_funzione%>" +
    		"&AGG_FUNZ_INS_UNITA=" +
    		"&prgUnita=" + prgUnita +
    		"&ret=IdoListaAziendePage";
    setWindowLocation(s);
}


function onLoad() {
	// nulla da fare
}

</SCRIPT>

<script language="Javascript">
  function tornaAllaRicerca()
  {   
      // Se la pagina è già in submit, ignoro questo nuovo invio!
      if (isInSubmit()) return;
<%
      String RagioneSociale = StringUtils.getAttributeStrNotNull(serviceRequest,"RagioneSociale");
	  RagioneSociale = com.engiweb.framework.tags.Util.urlEncode( RagioneSociale );
	  
	  String indirizzo = StringUtils.getAttributeStrNotNull(serviceRequest,"Indirizzo");
	  indirizzo = com.engiweb.framework.tags.Util.urlEncode( indirizzo );
	  
	  String denominazione = StringUtils.getAttributeStrNotNull(serviceRequest,"strDenominazioneAz");
	  denominazione = com.engiweb.framework.tags.Util.urlEncode( denominazione );
%>
      
      url="AdapterHTTP?PAGE=IdoAziendaRicercaPage";
      url += "&CDNFUNZIONE="+"<%=_funzione%>";
      url += "&RagioneSociale="+"<%=RagioneSociale%>";
      url += "&cf="+"<%=StringUtils.getAttributeStrNotNull(serviceRequest,"cf")%>";
      url += "&codAzStato="+"<%=StringUtils.getAttributeStrNotNull(serviceRequest,"codAzStato")%>";
      url += "&codCPI="+"<%=StringUtils.getAttributeStrNotNull(serviceRequest,"codCPI")%>";
      url += "&codCPIHid"+"<%=StringUtils.getAttributeStrNotNull(serviceRequest,"codCPIHid")%>";
      url += "&codCPIifDOMeqRESHid="+"<%=StringUtils.getAttributeStrNotNull(serviceRequest,"codCPIifDOMeqRESHid")%>";
      url += "&codComAz="+"<%=StringUtils.getAttributeStrNotNull(serviceRequest,"codComAz")%>";
      url += "&codComAzHid="+"<%=StringUtils.getAttributeStrNotNull(serviceRequest,"codComAzHid")%>";
      url += "&codProvincia="+"<%=StringUtils.getAttributeStrNotNull(serviceRequest,"codProvincia")%>";
      url += "&codTipoAzienda="+"<%=StringUtils.getAttributeStrNotNull(serviceRequest,"codTipoAzienda")%>";
      url += "&codNatGiuridica="+"<%=StringUtils.getAttributeStrNotNull(serviceRequest,"codNatGiuridica")%>";
      url += "&indirizzo="+"<%=indirizzo%>";
      url += "&piva="+"<%=StringUtils.getAttributeStrNotNull(serviceRequest,"piva")%>";
      url += "&strCPI="+"<%=StringUtils.getAttributeStrNotNull(serviceRequest,"strCPI")%>";
      url += "&strCPIHid=="+"<%=StringUtils.getAttributeStrNotNull(serviceRequest,"strCPIHid=")%>";
      url += "&strComAz="+"<%=StringUtils.getAttributeStrNotNull(serviceRequest,"strComAz")%>";
      url += "&strComAzHid="+"<%=StringUtils.getAttributeStrNotNull(serviceRequest,"strComAzHid")%>";
      url += "&utente="+"<%=StringUtils.getAttributeStrNotNull(serviceRequest,"utente")%>";
      url += "&strDenominazioneAz="+"<%=denominazione%>";
      url += "&flgSedeLegale=<%=StringUtils.getAttributeStrNotNull(serviceRequest,"flgSedeLegale")%>";
     
      setWindowLocation(url);
  }
 </script>

</head>
<BODY onLoad="onLoad()">
<af:form name="Frm1" method="POST" action="AdapterHTTP" >

  <input type="hidden" name="PAGE" value="IdoTestataAziendaPage"/>
  <input type="hidden" name="cdnFunzione" value="<%= _funzione %>"/>

  <af:list moduleName="M_DynRicercaTestateAziende" 
           jsSelect="Select"/>
  <center>
	<% if (canDoNuovo) { %>
		<input class="pulsante" type="submit" name="inserisci" value="Nuova azienda" /><br/>
	<% } %>
    <input class="pulsante" type="button" name="torna" value="Torna alla pagina di ricerca"
    		onclick="tornaAllaRicerca()"/><br/>
  </center>

</af:form>
</BODY>
</html>
<%}%>