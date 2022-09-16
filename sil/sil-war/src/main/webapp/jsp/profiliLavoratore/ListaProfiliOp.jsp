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
  boolean nuovoProfilo =nuovoProfilo = attributi.containsButton("INSERISCI"); 
  if (!filter.canView()) {
  	response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
  	return;
  }
  
  
 	String _funzione=(String) serviceRequest.getAttribute("CDNFUNZIONE");
 
	String strCodiceFiscale 	= StringUtils.getAttributeStrNotNull(serviceRequest,"strCodiceFiscale");
	String strCognome       	= StringUtils.getAttributeStrNotNull(serviceRequest,"strCognome");
	String strNome          	= StringUtils.getAttributeStrNotNull(serviceRequest,"strNome");
	String tipoRicerca      	= StringUtils.getAttributeStrNotNull(serviceRequest,"tipoRicerca");
	String dataProfiloDa		= StringUtils.getAttributeStrNotNull(serviceRequest, "dataProfiloDa");  
	String dataProfiloA			= StringUtils.getAttributeStrNotNull(serviceRequest, "dataProfiloA"); 
	String indiceProfilo        = StringUtils.getAttributeStrNotNull(serviceRequest,"indiceProfilo");
	String statoProfilo			= StringUtils.getAttributeStrNotNull(serviceRequest,"statoProfilo");
	String codCPIComp 				= StringUtils.getAttributeStrNotNull(serviceRequest,"codCPIComp");
	String codCPIProf 				= StringUtils.getAttributeStrNotNull(serviceRequest,"codCPIProf");

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
  
     url="AdapterHTTP?PAGE=RicercaProfiliMenuPage";
     url += "&CDNFUNZIONE="+"<%=_funzione%>";
     url += "&strCodiceFiscale="+"<%=strCodiceFiscale%>";
     url += "&strCognome="+"<%=strCognome%>";
     url += "&strNome="+"<%=strNome%>";
     url += "&tipoRicerca="+"<%=tipoRicerca%>";
     url += "&indiceProfilo="+"<%=indiceProfilo%>";
     url += "&statoProfilo="+"<%=statoProfilo%>";
     url += "&dataProfiloDa="+"<%=dataProfiloDa%>";
     url += "&dataProfiloA="+"<%=dataProfiloA%>";
     url += "&codCPIComp="+"<%=codCPIComp%>";
     url += "&codCPIProf="+"<%=codCPIProf%>";
     setWindowLocation(url);
  }
 
	function nuovoProfilo() {
		// Se la pagina è già in submit, ignoro questo nuovo invio!
		if (isInSubmit()) return;

		var msg = "Per inserire un nuovo profilo è necessario prima accedere alla scheda del lavoratore e, successivamente, ";
		msg += "alla voce di menu contestuale 'Profilatura Lavoratore'. Continua per accedere alla ricerca del lavoratore.";
		
		if (confirm(msg)) {
			var url = "AdapterHTTP?PAGE=AnagMainPage&CDNFUNZIONE=1";
			setWindowLocation(url);
		}
	}
  </script>
 
</head>
<body  class="gestione" onload="rinfresca();rinfresca_laterale();">
<p>
	<font color="green">
		<af:showMessages/>
  	</font>
  	<font color="red"><af:showErrors /></font>
</p>

<af:list moduleName="M_ListaProfiliOp" getBack="true"/>

<center>
<table class="main">
 <%if(nuovoProfilo){ %>
<tr>
<td>
<input class="pulsante" type = "button" name="nuovo" value="Nuovo Profilo" onclick="nuovoProfilo()"/>
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