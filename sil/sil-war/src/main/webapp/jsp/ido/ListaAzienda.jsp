<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">

<%@ page import="com.engiweb.framework.base.*,
                 java.lang.*,
                 java.text.*,
                 java.util.*, 
                 it.eng.afExt.utils.StringUtils,
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

  int _funzione=Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));
  PageAttribs attributi = new PageAttribs(user, "IdoListaAziendePage");
  boolean canInsert = attributi.containsButton("INSERISCI");

%>
<html>
<head>
<title>Lista Aziende</title>
 <af:linkScript path="../../js/" />
<link rel="stylesheet" href="../../css/stili.css" type="text/css">
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
<SCRIPT language="Javascript" type="text/javascript">
<!--
function go(url, alertFlag) {
// Se la pagina è già in submit, ignoro questo nuovo invio!
if (isInSubmit()) return;

var _url = "AdapterHTTP?" + url;
if (alertFlag == 'TRUE' ) {
if (confirm('Confermi operazione'))
setWindowLocation(_url);
}
else
setWindowLocation(_url);
}
// -->
</SCRIPT>
<script language="Javascript">
     <% 
     	//Genera il Javascript che si occuperà di inserire i links nel footer
        attributi.showHyperLinks(out, requestContainer,responseContainer,"");
      %>
</script>

<script lenguagge="Javascript">
  function tornaAllaRicerca()
  {  
     // Se la pagina è già in submit, ignoro questo nuovo invio!
     if (isInSubmit()) return;
     
     url="AdapterHTTP?PAGE=IdoAziendaRicercaPage";
     url += "&CDNFUNZIONE="+"<%=_funzione%>";
	 url += "&flgSedeLegale=<%=StringUtils.getAttributeStrNotNull(serviceRequest,"flgSedeLegale")%>";
     url += "&codTipoAzienda="+"<%=StringUtils.getAttributeStrNotNull(serviceRequest,"codTipoAzienda")%>";
     url += "&codNatGiuridica="+"<%=StringUtils.getAttributeStrNotNull(serviceRequest,"codNatGiuridica")%>";     
     url += "&cerca="+"<%=StringUtils.getAttributeStrNotNull(serviceRequest,"cerca")%>";
     url += "&cf="+"<%=StringUtils.getAttributeStrNotNull(serviceRequest,"cf")%>";
     url += "&piva="+"<%=StringUtils.getAttributeStrNotNull(serviceRequest,"piva")%>";

<%
      String RagioneSociale = StringUtils.getAttributeStrNotNull(serviceRequest,"RagioneSociale");
	  RagioneSociale = com.engiweb.framework.tags.Util.urlEncode( RagioneSociale );
	  
	  String indirizzo = StringUtils.getAttributeStrNotNull(serviceRequest,"Indirizzo");
	  indirizzo = com.engiweb.framework.tags.Util.urlEncode( indirizzo );
	  
	  String denominazione = StringUtils.getAttributeStrNotNull(serviceRequest,"strDenominazioneAz");
	  denominazione = com.engiweb.framework.tags.Util.urlEncode( denominazione );

%>
     url += "&RagioneSociale="+"<%=RagioneSociale%>";
     url += "&Indirizzo="+"<%=indirizzo%>";
     url += "&codComAz="+"<%=StringUtils.getAttributeStrNotNull(serviceRequest,"codComAz")%>";
     url += "&codComAzHid="+"<%=StringUtils.getAttributeStrNotNull(serviceRequest,"codComAzHid")%>";
     url += "&codCPI="+"<%=StringUtils.getAttributeStrNotNull(serviceRequest,"codCPI")%>";
     url += "&codCPIHid="+"<%=StringUtils.getAttributeStrNotNull(serviceRequest,"codCPIHid")%>";
     url += "&strCPI="+"<%=StringUtils.getAttributeStrNotNull(serviceRequest,"strCPI")%>";
     url += "&strCPIHid="+"<%=StringUtils.getAttributeStrNotNull(serviceRequest,"strCPIHid")%>";
     url += "&codCPIifDOMeqRESHid="+"<%=StringUtils.getAttributeStrNotNull(serviceRequest,"codCPIifDOMeqRESHid")%>";
     url += "&codAzStato="+"<%=StringUtils.getAttributeStrNotNull(serviceRequest,"codAzStato")%>";
     url += "&codProvincia="+"<%=StringUtils.getAttributeStrNotNull(serviceRequest,"codProvincia")%>";
     url += "&strComAz="+"<%=StringUtils.getAttributeStrNotNull(serviceRequest,"strComAz")%>";
     url += "&strComAzHid="+"<%=StringUtils.getAttributeStrNotNull(serviceRequest,"strComAzHid")%>";
     url += "&utente="+"<%=StringUtils.getAttributeStrNotNull(serviceRequest,"utente")%>";
     url += "&strDenominazioneAz="+"<%=denominazione%>";
     url += "&codAteco="+"<%=StringUtils.getAttributeStrNotNull(serviceRequest,"codAteco")%>";
     url += "&strTipoAteco="+"<%=StringUtils.getAttributeStrNotNull(serviceRequest,"strTipoAteco")%>";
     url += "&strAteco="+"<%=StringUtils.getAttributeStrNotNull(serviceRequest,"strAteco")%>";
     
     setWindowLocation(url);
  }
 </script>


</head>
<body class="gestione" onload="rinfresca()">
<af:form dontValidate="true">
<af:list moduleName="M_DYNRICERCASEDIAZIENDE" configProviderClass="it.eng.sil.module.ido.DynRicSediAziendaCongif" canInsert="<%= canInsert ? \"1\" : \"0\" %>" canDelete="0"/>
<center><input class="pulsante" type = "button" name="torna" value="Torna alla pagina di ricerca" onclick="tornaAllaRicerca()"/></center>
</af:form>
<br/>
<br/>
</body>
</html>
<%}%>