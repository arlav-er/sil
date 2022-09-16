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
  PageAttribs attributi = new PageAttribs(user, "IdoPubbRicercaPage");
  //boolean canInsert = attributi.containsButton("INSERISCI");

%>
<html>
<head>
<title>Lista Pubblicazioni</title>
<link rel="stylesheet" href="../../css/stili.css" type="text/css">
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
<af:linkScript path="../../js/"/>
<SCRIPT language="Javascript" type="text/javascript">
</SCRIPT>

<script language="Javascript">
     <% 
     	//Genera il Javascript che si occuperà di inserire i links nel footer
        attributi.showHyperLinks(out, requestContainer,responseContainer,"");
      %>
</script>

<script language="Javascript">
  function tornaAllaRicerca()
  {   
      // Se la pagina è già in submit, ignoro questo nuovo invio!
      if (isInSubmit()) return;
  
      url="AdapterHTTP?PAGE=IdoPubbRicercaPage";
      url += "&ANNO="+"<%=StringUtils.getAttributeStrNotNull(serviceRequest,"ANNO")%>";
      url += "&CDNUT="+"<%=StringUtils.getAttributeStrNotNull(serviceRequest,"CDNUT")%>";
      url += "&CODMANSIONE="+"<%=StringUtils.getAttributeStrNotNull(serviceRequest,"CODMANSIONE")%>";
      url += "&CODTIPOMANSIONE="+"<%=StringUtils.getAttributeStrNotNull(serviceRequest,"CODTIPOMANSIONE")%>";
      url += "&DATPUBBLICAZIONE="+"<%=StringUtils.getAttributeStrNotNull(serviceRequest,"DATPUBBLICAZIONE")%>";
      url += "&DATSCADENZAPUBBLICAZIONE="+"<%=StringUtils.getAttributeStrNotNull(serviceRequest,"DATSCADENZAPUBBLICAZIONE")%>";
      url += "&DESCMANSIONE="+"<%=StringUtils.getAttributeStrNotNull(serviceRequest,"DESCMANSIONE")%>";
      url += "&FLGPUBBLICATA="+"<%=StringUtils.getAttributeStrNotNull(serviceRequest,"FLGPUBBLICATA")%>";
      url += "&Indirizzo="+"<%=StringUtils.getAttributeStrNotNull(serviceRequest,"Indirizzo")%>";
      url += "&NUMRICHIESTA="+"<%=StringUtils.getAttributeStrNotNull(serviceRequest,"NUMRICHIESTA")%>";
      url += "&RagioneSociale"+"<%=StringUtils.getAttributeStrNotNull(serviceRequest,"RagioneSociale")%>";
      url += "&UTRIC="+"<%=StringUtils.getAttributeStrNotNull(serviceRequest,"UTRIC")%>";
      url += "&CDNFUNZIONE="+"<%=_funzione%>";
      url += "&cerca="+"<%=StringUtils.getAttributeStrNotNull(serviceRequest,"cerca")%>";
      url += "&cf="+"<%=StringUtils.getAttributeStrNotNull(serviceRequest,"cf")%>";
      url += "&codCom="+"<%=StringUtils.getAttributeStrNotNull(serviceRequest,"codCom")%>";
      url += "&codComHid="+"<%=StringUtils.getAttributeStrNotNull(serviceRequest,"codComHid")%>";
      url += "&codMansioneHid="+"<%=StringUtils.getAttributeStrNotNull(serviceRequest,"codMansioneHid")%>";
      url += "&codTipoAzienda="+"<%=StringUtils.getAttributeStrNotNull(serviceRequest,"codTipoAzienda")%>";
      url += "&desComune="+"<%=StringUtils.getAttributeStrNotNull(serviceRequest,"desComune")%>";
      url += "&desComuneHid="+"<%=StringUtils.getAttributeStrNotNull(serviceRequest,"desComuneHid")%>";
      url += "&piva="+"<%=StringUtils.getAttributeStrNotNull(serviceRequest,"piva")%>";
      url += "&prgAzienda="+"<%=StringUtils.getAttributeStrNotNull(serviceRequest,"prgAzienda")%>";
      url += "&prgUnita="+"<%=StringUtils.getAttributeStrNotNull(serviceRequest,"prgUnita")%>";
      url += "&strTipoMansione="+"<%=StringUtils.getAttributeStrNotNull(serviceRequest,"strTipoMansione")%>";
      url += "&codMonoCMcatPubb="+"<%=StringUtils.getAttributeStrNotNull(serviceRequest,"codMonoCMcatPubb")%>";
      url += "&utente="+"<%=StringUtils.getAttributeStrNotNull(serviceRequest,"utente")%>";

      setWindowLocation(url);
  }
</script>
</head>
<body class="gestione" onload="rinfresca()">
<af:form dontValidate="true">
<af:list moduleName="M_DYNRICERCAPUBB" canInsert="0" canDelete="0" configProviderClass="it.eng.sil.module.ido.DynamicRicRichiestePubConfig"/>
<center><input class="pulsante" type = "button" name="torna" value="Torna alla pagina di ricerca" onclick="tornaAllaRicerca()"/></center>
</af:form>
<br/>
<br/>
</body>
</html>
<%}%>