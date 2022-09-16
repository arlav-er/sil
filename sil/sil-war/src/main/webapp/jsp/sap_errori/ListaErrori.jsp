<%@ page contentType="text/html;charset=utf-8"%>
 
<%@ page import="com.engiweb.framework.base.*,
                com.engiweb.framework.security.*,
                java.lang.*,java.text.*,java.util.*,
                it.eng.afExt.utils.*, it.eng.sil.util.*,
                it.eng.sil.security.*" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%@ taglib uri="aftags" prefix="af"%>

<% 

  String _page = (String) serviceRequest.getAttribute("PAGE"); 
  ProfileDataFilter filter = new ProfileDataFilter(user, _page);
  //NOTE: Attributi della pagina (pulsanti e link) 
  PageAttribs attributi = new PageAttribs(user, _page);
  if (!filter.canView()) {
  	response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
  	return;
  }
  
  
 	String _funzione=(String) serviceRequest.getAttribute("CDNFUNZIONE");
 
	String strCodiceFiscale 	= StringUtils.getAttributeStrNotNull(serviceRequest,"strCodiceFiscale");
	String strCognome       	= StringUtils.getAttributeStrNotNull(serviceRequest,"strCognome");
	String strNome          	= StringUtils.getAttributeStrNotNull(serviceRequest,"strNome");
	String tipoRicerca      	= StringUtils.getAttributeStrNotNull(serviceRequest,"tipoRicerca");
	String dataInvioDa		= StringUtils.getAttributeStrNotNull(serviceRequest, "dataInvioDa");  
	String dataInvioA			= StringUtils.getAttributeStrNotNull(serviceRequest, "dataInvioA");  
	String cpiCompetente = StringUtils.getAttributeStrNotNull(serviceRequest,"codcpi");

%>

<html>
<head>
  <title>Risultati della ricerca</title>
  <link rel="stylesheet" type="text/css" href="../../css/stili.css"/>
  <link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
  
  <af:linkScript path="../../js/"/>

  <script type="text/Javascript">
  function tornaAllaRicerca()
  {  // Se la pagina è già in submit, ignoro questo nuovo invio!
      
	 if (isInSubmit()) return;
  
     url="AdapterHTTP?PAGE=RicercaErroriSapPage";
     url += "&CDNFUNZIONE="+"<%=_funzione%>";
     url += "&strCodiceFiscale="+"<%=strCodiceFiscale%>";
     url += "&strCognome="+"<%=strCognome%>";
     url += "&strNome="+"<%=strNome%>";
     url += "&tipoRicerca="+"<%=tipoRicerca%>";
     url += "&dataInvioDa="+"<%=dataInvioDa%>";
     url += "&dataInvioA="+"<%=dataInvioA%>";
     url += "&codcpi="+"<%=cpiCompetente%>";
     setWindowLocation(url);
  }
  </script>
 
</head>
<body  class="gestione" onload="rinfresca();">
<p>
	<font color="green">
		<af:showMessages/>
  	</font>
  	<font color="red"><af:showErrors /></font>
</p>

<af:list moduleName="M_ListaErroriInvioSap" getBack="true"/>

<center>
<table class="main">
<tr>
<td>
<input class="pulsante" type = "button" name="torna" value="Torna alla pagina di ricerca" onclick="tornaAllaRicerca()"/>
</td>
</tr>
</table>

</center>
 
</body>
</html>