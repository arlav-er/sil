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
String _funzione=(String) serviceRequest.getAttribute("CDNFUNZIONE");

String annoGrad = StringUtils.getAttributeStrNotNull(serviceRequest,"annoGrad");
String codMonoTipoGrad = StringUtils.getAttributeStrNotNull(serviceRequest,"codMonoTipoGrad");
String statoGrad = StringUtils.getAttributeStrNotNull(serviceRequest,"statoGrad");
String provinciaIscr = StringUtils.getAttributeStrNotNull(serviceRequest,"PROVINCIA_ISCR");

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
  
     url="AdapterHTTP?PAGE=CMRicercaGradAnnualePage";
     url += "&CDNFUNZIONE="+"<%=_funzione%>";
     url += "&annoGrad="+"<%=annoGrad%>";
     url += "&codMonoTipoGrad="+"<%=codMonoTipoGrad%>";
     url += "&statoGrad="+"<%=statoGrad%>";
     url += "&PROVINCIA_ISCR="+"<%=provinciaIscr%>";
     setWindowLocation(url);
  }
  
  function nuovaGradAnnuale()
  {  // Se la pagina è già in submit, ignoro questo nuovo invio!
     if (isInSubmit()) return;
     url="AdapterHTTP?PAGE=CMNewGradAnnualePage";
     url+="&goBackListPage=CMListaGradAnnualePage";
     url+= "&CDNFUNZIONE="+"<%=_funzione%>";  
     setWindowLocation(url);
  }
  
  </script>
</head>
<body class="gestione" onload="rinfresca()">

<!-- messaggi di esito delle operazioni applicative -->
<font color="red"><af:showErrors/></font>


<af:list moduleName="M_ListaGradAnnuale" />

<table class=main>
<tr>
	<td>
		<input class="pulsanti" type="button" name="nuovaGrad" value="Nuova graduatoria" onclick="nuovaGradAnnuale()" />
	</td>
</tr>
<tr>
	<td>
		<input class="pulsante" type = "button" name="torna" value="Torna alla ricerca" onclick="tornaAllaRicerca()"/>		
	</td>
</tr>
</table>
</body>
</html>
  
  