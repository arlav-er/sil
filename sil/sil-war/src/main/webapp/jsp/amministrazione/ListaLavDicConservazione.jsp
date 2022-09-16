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
boolean btnStampaDisable = false;
SourceBean rowCnt = (SourceBean) serviceResponse.getAttribute("M_ListaLavDichConservazioneCnt");
int quantiLav = rowCnt.getAttributeAsVector("ROWS.ROW").size();
if (quantiLav >= MAX_LAV_LIMIT) {
	btnStampaDisable = true;	
}
String _funzione=(String) serviceRequest.getAttribute("CDNFUNZIONE");
String datInizioRichDa = StringUtils.getAttributeStrNotNull(serviceRequest, "dataInizioRicerca");
String datInizioRichA = StringUtils.getAttributeStrNotNull(serviceRequest, "dataInizioRicercaA");
String codCpi = StringUtils.getAttributeStrNotNull(serviceRequest,"CodCPI");
String descTipoRicerca = "";
String CodTipoLista = "";
Vector CodTipoListaVet = serviceRequest.getAttributeAsVector("tipoRicerca");
if(CodTipoListaVet.size() > 0) {		
	for(int i=0; i<CodTipoListaVet.size(); i++) {
		String valore = CodTipoListaVet.get(i).toString();
	  	if(!valore.equals("")) {
		  	if(CodTipoLista.length() > 0) {
			 	CodTipoLista = CodTipoLista + "," + valore;
			}
		  	else {
			 	CodTipoLista += valore; 
		  	}
		  	if (valore.equals("A")) {
		  		if (descTipoRicerca.equals("")) {
		  			descTipoRicerca = "Mancata dichiarazione";	
		  		}
		  		else {
		  			descTipoRicerca = descTipoRicerca + "/Mancata dichiarazione";
		  		}
		  	}
		  	else {
		  		if (valore.equals("B")) {
		  			if (descTipoRicerca.equals("")) {
		  				descTipoRicerca = "Data fine non valida";		
		  			}
		  			else {
		  				descTipoRicerca = descTipoRicerca + "/Data fine non valida";	
		  			}
		  		}
		  	}
		}
	}
}
else {
	descTipoRicerca = "Mancata dichiarazione/Data fine non valida";	
}

String cpiComp = "";
SourceBean cpiCompSb = (SourceBean) serviceResponse.getAttribute("CM_GET_CODCPI.ROWS.ROW");				
if (cpiCompSb != null) {	
	cpiComp = (String) cpiCompSb.getAttribute("DESCRIZIONE");
}
String parameters = "";
String queryString = null;
String txtFilters = "";

if (!datInizioRichDa.equals("")) {
	parameters = parameters + "&dataInizioRicerca="+datInizioRichDa;
	txtFilters += " Data inizio richiesta da: <strong>" + datInizioRichDa + "</strong>;";
}
if (!datInizioRichA.equals("")) {
	parameters = parameters + "&dataInizioRicercaA="+datInizioRichA;
	txtFilters += " Data inizio richiesta a: <strong>" + datInizioRichA + "</strong>;";
}
if (!codCpi.equals("")) {
	parameters = parameters + "&CodCPI="+codCpi;
	txtFilters += " Cpi comp.: <strong>" + cpiComp + "</strong>";	
}
if (!CodTipoLista.equals("")) {
	parameters = parameters + "&tipoRicerca="+CodTipoLista;
	txtFilters += " Tipo ricerca: <strong>" + descTipoRicerca + "</strong>;";
}
%>
<html>
<head>
  	<title>Esito Ricerca Elenco Sanatoria Redditi</title>

  	<link rel="stylesheet" href="../../css/stili.css" type="text/css">
  	<link rel="stylesheet" href="../../css/listdetail.css" type="text/css">
  	<af:linkScript path="../../js/"/>
  	
  	<%@ include file="../documenti/_apriGestioneDoc.inc"%>

	<script type="text/Javascript">
  	function tornaAllaRicerca() {  
  		// Se la pagina è già in submit, ignoro questo nuovo invio!
     	if (isInSubmit()) return;
	   	url="AdapterHTTP?PAGE=RicercaDichConservazionePage";
		url += "&CDNFUNZIONE="+"<%=_funzione%>";
	    setWindowLocation(url);
  	}
  	
  	function stampaSanatoriaRedditi() {
		apriGestioneDoc('RPT_SANATORIA_REDDITI','<%=parameters%>+&stampa=stampa','DICH');
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
	<af:list moduleName="M_ListaLavDichConservazione" />    
  </center>
  <br/>
  <center>
  <table>
  <tr><td>
  <input class="pulsante" type="button" name="torna" value="Nuova Ricerca" onclick="tornaAllaRicerca()"/>
  <input class="pulsante<%=((!btnStampaDisable)?"":"Disabled")%>" type="button" name="stampa" value="Stampa"
  			<%=(btnStampaDisable)?"disabled=\"True\"":""%> onclick="stampaSanatoriaRedditi()" />
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


