<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">

<%@ page import="com.engiweb.framework.base.*,java.lang.*,java.text.*,java.util.*"%>
<%@ taglib uri="aftags" prefix="af"%>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="
	it.eng.afExt.utils.StringUtils,
	it.eng.sil.security.*,
	it.eng.afExt.utils.* " %>
                  
                  
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>
<%
int MAX_LAV_LIMIT = MessageCodes.General.MAX_NUMERO_RISULTATI;
SourceBean rowCnt = (SourceBean) serviceResponse.getAttribute("M_ListaUtentiEsterniCnt");
int quantiLav = rowCnt.getAttributeAsVector("ROWS.ROW").size();
boolean btnStampaDisable = false;
if (quantiLav >= MAX_LAV_LIMIT) {
	btnStampaDisable = true;	
}
String _funzione=(String) serviceRequest.getAttribute("CDNFUNZIONE");
String dataIn = StringUtils.getAttributeStrNotNull(serviceRequest, "dataIn");
//String dataInA = StringUtils.getAttributeStrNotNull(serviceRequest, "dataInA");
String tipoLista = StringUtils.getAttributeStrNotNull(serviceRequest, "CodTipoLista");
String descTipoLista = "";
String esitoDomanda = StringUtils.getAttributeStrNotNull(serviceRequest, "codEsito");
String descEsito = "";
String codCpi = StringUtils.getAttributeStrNotNull(serviceRequest,"CodCPI");
String parameters = "";
String queryString = null;
String cpiComp = "";
SourceBean cpiCompSb = (SourceBean) serviceResponse.getAttribute("CM_GET_CODCPI.ROWS.ROW");	
if (cpiCompSb != null) {	
	cpiComp = (String) cpiCompSb.getAttribute("DESCRIZIONE");
}
Vector rowsTipoListaSb = serviceResponse.getAttributeAsVector("M_MO_TIPO_LISTA.ROWS.ROW");
int sizeTipoLista = rowsTipoListaSb.size();
if (!tipoLista.equals("") && sizeTipoLista > 0) {
	for (int i=0;i<sizeTipoLista;i++) {
		SourceBean rowCurr = (SourceBean)rowsTipoListaSb.get(i);
		String codiceLista = rowCurr.getAttribute("CODICE").toString();
		if (codiceLista.equalsIgnoreCase(tipoLista)) {
			descTipoLista = StringUtils.getAttributeStrNotNull(rowCurr,"DESCRIZIONE");
			break;
		}
	}
}
Vector rowsEsitoSb = serviceResponse.getAttributeAsVector("M_GetStatoMob.ROWS.ROW");
int sizeEsito = rowsEsitoSb.size();
if (!esitoDomanda.equals("") && sizeEsito > 0) {
	for (int i=0;i<sizeEsito;i++) {
		SourceBean rowCurr = (SourceBean)rowsEsitoSb.get(i);
		String codiceEsito = rowCurr.getAttribute("CODICE").toString();
		if (codiceEsito.equalsIgnoreCase(esitoDomanda)) {
			descEsito = StringUtils.getAttributeStrNotNull(rowCurr,"DESCRIZIONE");
			break;
		}
	}
}
String txtFilters = "";
if (!dataIn.equals("")) {
	parameters = parameters + "&dataIn="+dataIn;
	txtFilters += " Data CPM/Delibera Provinciale: <strong>" + dataIn + "</strong>;";
}

//if (!dataInA.equals("")) {
//	parameters = parameters + "&dataInA="+dataInA;
//	txtFilters += " Data domanda a: <strong>" + dataInA + "</strong>;";
//}

if (!tipoLista.equals("")) {
	parameters = parameters + "&CodTipoLista="+tipoLista;
	txtFilters += " Tipo lista: <strong>" + descTipoLista + "</strong>;";
}
if (!esitoDomanda.equals("")) {
	parameters = parameters + "&codEsito="+esitoDomanda;
	txtFilters += " Stato della domanda: <strong>" + descEsito + "</strong>;";
}
if (!codCpi.equals("")) {
	parameters = parameters + "&CodCPI="+codCpi;
	txtFilters += " Cpi comp.: <strong>" + cpiComp + "</strong>";	
}
%>
<html>
<head>
  	<title>Esito Ricerca</title>

  	<link rel="stylesheet" href="../../css/stili.css" type="text/css">
  	<link rel="stylesheet" href="../../css/listdetail.css" type="text/css">
  	<af:linkScript path="../../js/"/>
  	
  	<%@ include file="../documenti/_apriGestioneDoc.inc"%>

	<script type="text/Javascript">
  	function tornaAllaRicerca() {  
  		// Se la pagina è già in submit, ignoro questo nuovo invio!
     	if (isInSubmit()) return;
	   	url="AdapterHTTP?PAGE=RicercaMobilitaUtentiEsterniPage";
		url += "&CDNFUNZIONE="+"<%=_funzione%>";
	    setWindowLocation(url);
  	}
  	
  	function stampaLista() {
		apriGestioneDoc('RPT_MOB_UTENTI','<%=parameters%>','MOBLISTR');
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
	<af:list moduleName="M_ListaUtentiEsterni" />    
  </center>
  <br/>
  <center>
  <table>
  <tr><td>
  <input class="pulsante" type="button" name="torna" value="Nuova Ricerca" onclick="tornaAllaRicerca()"/>
  <input class="pulsante<%=((!btnStampaDisable)?"":"Disabled")%>" type="button" name="stampa" value="Stampa" 
  	<%=(btnStampaDisable)?"disabled=\"True\"":""%> onclick="stampaLista()" />
  </td></tr>
  </table>
  </center>
  <%if (btnStampaDisable) {%>
  <br>
  <table width="100%">
    <tr><td colspan=3 style="campo2">Il numero dei lavoratori estratti &egrave; elevato. Raffinare i criteri di ricerca.</td></tr>
  </table>
  <%}%>
  </body>
</html>


