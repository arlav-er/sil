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
  
	if (! filter.canView()){
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
	}

	// NOTE: Attributi della pagina (pulsanti e link) 
	PageAttribs attributi = new PageAttribs(user, "ListaGeneraleEvidenzePage");
	//boolean canInsert = attributi.containsButton("nuovo");
	boolean canDelete=attributi.containsButton("cancella");
  
  
  
	String _funzione=(String) serviceRequest.getAttribute("CDNFUNZIONE");
	 
	String strCodiceFiscale = StringUtils.getAttributeStrNotNull(serviceRequest,"strCodiceFiscale");
	String strCognome       = StringUtils.getAttributeStrNotNull(serviceRequest,"strCognome");
	String strNome          = StringUtils.getAttributeStrNotNull(serviceRequest,"strNome");
 //String tipoRicerca      = StringUtils.getAttributeStrNotNull(serviceRequest,"tipoRicerca");

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
  
     url="AdapterHTTP?PAGE=RicercaGeneraleEvidenzePage";
     url += "&CDNFUNZIONE="+"<%=_funzione%>";
     setWindowLocation(url);
  }
 </script>
 
</head>

<body  class="gestione" onload="rinfresca()">
<center>
<font color="red">
    <af:showErrors/>
</font>
</center>
<af:form dontValidate="true">

<af:list moduleName="ListaGeneraleEvidenze" canDelete="<%=canDelete ? \"1\" : \"0\"%>" 
		 canInsert="0" />

<center><input class="pulsante" type = "button" name="torna" value="Torna alla pagina di ricerca" onclick="tornaAllaRicerca()"/></center>
</af:form>
<br/>
</body>
</html>
<%//}%>