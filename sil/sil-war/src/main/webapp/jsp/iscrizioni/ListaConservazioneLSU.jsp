<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">

<%@ page import="com.engiweb.framework.base.*,java.text.*,java.util.*"%>
<%@ taglib uri="aftags" prefix="af"%>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="
	it.eng.afExt.utils.StringUtils,
	it.eng.sil.security.* " %>
                  
                  
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>
<%
String _funzione=(String) serviceRequest.getAttribute("CDNFUNZIONE");
String datInizioDa = StringUtils.getAttributeStrNotNull(serviceRequest, "datinizioda");
String datInizioA = StringUtils.getAttributeStrNotNull(serviceRequest, "datinizioa");
String codCpi = StringUtils.getAttributeStrNotNull(serviceRequest, "codCpi");
String descCpi = serviceResponse.containsAttribute("M_DescCPI.ROWS.ROW")?serviceResponse.getAttribute("M_DescCPI.ROWS.ROW.STRDESCRIZIONE").toString():"";
String parameters = "";
String queryString = null;
String txtFilters = "";

if (!datInizioDa.equals("")) {
	parameters = parameters + "&datinizioda="+datInizioDa;
	txtFilters += " Data inizio iscrizione da: <strong>" + datInizioDa + "</strong>;";
}
if (!datInizioA.equals("")) {
	parameters = parameters + "&datinizioa="+datInizioA;
	txtFilters += " Data inizio iscrizione a: <strong>" + datInizioA + "</strong>;";
}
if (!codCpi.equals("")) {
	parameters = parameters + "&CodCPI="+codCpi;
	txtFilters += " Cpi comp.: <strong>" + descCpi + "</strong>";	
}
%>
<html>
<head>
  	<title>Esito Ricerca Lavoratori Conservazione LSU</title>

  	<link rel="stylesheet" href="../../css/stili.css" type="text/css">
  	<link rel="stylesheet" href="../../css/listdetail.css" type="text/css">
  	<af:linkScript path="../../js/"/>
  	
  	<%@ include file="../documenti/_apriGestioneDoc.inc"%>

	<script type="text/Javascript">
  	function tornaAllaRicerca() {  
  		// Se la pagina è già in submit, ignoro questo nuovo invio!
     	if (isInSubmit()) return;
	   	url="AdapterHTTP?PAGE=RicercaConservazioneLSUPage";
		url += "&CDNFUNZIONE="+"<%=_funzione%>";
	    setWindowLocation(url);
  	}
  	
  	function stampaListaConservazioneLSU() {
  		apriGestioneDoc('RPT_LISTA_CONSERVAZIONE_LSU','<%=parameters%>','CONSLSU');
	}
  	</script>

</head>

<body class="gestione" onload="rinfresca()">
<%
if (txtFilters.length() > 0) {
	txtFilters = "Filtri di ricerca:<br/> " + txtFilters;
%>
<table cellpadding="2" cellspacing="10" border="0" width="100%">
	<tr>
		<td style="border-width: 1px; border-style: solid; text-color: #000080; border-color: #000080; background-color: #dcdcdc">
		<%
      	out.print(txtFilters);
        %>
		</td>
	</tr>
</table>
<%
}
%>
<p align="center">
  <center>
	<af:list moduleName="M_ListConservazioneLSU" />    
  </center>
  <br/>
  <center>
  <table>
  <tr><td>
  <input class="pulsante" type="button" name="torna" value="Nuova Ricerca" onclick="tornaAllaRicerca()"/>
  <input class="pulsante" type="button" name="stampa" value="Stampa" onclick="stampaListaConservazioneLSU()" />
  </td></tr>
  </table>
  </center>
</body>
</html>


