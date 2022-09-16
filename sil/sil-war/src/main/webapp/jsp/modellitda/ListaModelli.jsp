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
  boolean canInsert = false;
  
  
  if (!filter.canView()) {
  	response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
  }else{
	  canInsert = attributi.containsButton("NuovoModello");
  }

  
  String _funzione=(String) serviceRequest.getAttribute("CDNFUNZIONE");
 
  String obiettivoMisura = StringUtils.getAttributeStrNotNull(serviceRequest,"obiettivoMisura");
  String strValoreMax          = StringUtils.getAttributeStrNotNull(serviceRequest,"strValoreMax");
  String strModalita           = StringUtils.getAttributeStrNotNull(serviceRequest,"strModalita");
  String strGiorniAttivazione  = StringUtils.getAttributeStrNotNull(serviceRequest,"strGiorniAttivazione");
  String strGiorniErogazione   = StringUtils.getAttributeStrNotNull(serviceRequest,"strGiorniErogazione");
  String modelloAttivo    	  = StringUtils.getAttributeStrNotNull(serviceRequest,"modelloAttivo");
  String modelloCM   = StringUtils.getAttributeStrNotNull(serviceRequest,"modelloCM");

  
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
  
     url="AdapterHTTP?PAGE=RicercaModelliTdaPage";
     url += "&CDNFUNZIONE="+"<%=_funzione%>";
     url += "&obiettivoMisura="+"<%=obiettivoMisura%>";
     url += "&modelloAttivo="+"<%=modelloAttivo%>";
     url += "&modelloCM="+"<%=modelloCM%>";
     setWindowLocation(url);
  }
  
  function nuovoModello(){	
		
	  if (isInSubmit()) return;
	  
		var url = 'AdapterHTTP?PAGE=ModelloTdaMainPage';
		url = url + '&cdnfunzione=<%=_funzione%>';
		
		setWindowLocation(url);
	 }
  </script>
 
</head>
<body  class="gestione" onload="rinfresca();">

<af:list moduleName="M_ListaModelliTda" getBack="true"/>

<center>
		<table class="main">
		<%if(canInsert){ %>
		 <tr>
		<td>
		<input class="pulsante" type="button" name="inserisci" value="Nuovo Modello" onclick="nuovoModello()"/>
		</td>
		</tr>
		<%}%>
		<tr>
		<td>
		<input class="pulsante" type = "button" name="torna" value="Torna alla pagina di ricerca" onclick="tornaAllaRicerca()"/>
		</td>
		</tr>
</table>

</center>

</body>
</html>