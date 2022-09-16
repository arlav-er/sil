<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">

<%@ page import="com.engiweb.framework.base.*,java.text.*,java.util.*"%>
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
SourceBean rowCnt = (SourceBean) serviceResponse.getAttribute("M_ListaComitatoSilCnt");
int quantiLav = rowCnt.getAttributeAsVector("ROWS.ROW").size();
if (quantiLav >= MAX_LAV_LIMIT) {
	btnStampaDisable = true;	
}
String _funzione=(String) serviceRequest.getAttribute("CDNFUNZIONE");
String datRiunone = StringUtils.getAttributeStrNotNull(serviceRequest, "dataRiunone");
//String datRiunoneA = StringUtils.getAttributeStrNotNull(serviceRequest, "dataRiunoneA");
String CodTipoLista = "";
String descTipoLista = "";
Vector rowsTipoListaSb = serviceResponse.getAttributeAsVector("M_MO_TIPO_LISTA.ROWS.ROW");
int sizeTipoLista = rowsTipoListaSb.size();
Vector CodTipoListaVet = serviceRequest.getAttributeAsVector("CodTipoLista");
if(CodTipoListaVet.size() > 0) {		
	for(int i=0; i<CodTipoListaVet.size(); i++) {
	  	if(!CodTipoListaVet.elementAt(i).equals("")) {
	  		String codiceCurr = CodTipoListaVet.elementAt(i).toString();
	  		if (sizeTipoLista > 0) {
	  			for (int k=0;k<sizeTipoLista;k++) {
	  				SourceBean rowCurr = (SourceBean)rowsTipoListaSb.get(k);
	  				String codiceTipoLista = rowCurr.getAttribute("CODICE").toString();
	  				if (codiceTipoLista.equalsIgnoreCase(codiceCurr)) {
	  					if (descTipoLista.equals("")) {
	  						descTipoLista = StringUtils.getAttributeStrNotNull(rowCurr,"DESCRIZIONE");	
	  					}
	  					else {
	  						descTipoLista = descTipoLista + "/" + StringUtils.getAttributeStrNotNull(rowCurr,"DESCRIZIONE");	
	  					}
	  					break;
	  				}
	  			}
	  		}
		  	if(CodTipoLista.length() > 0) {
			 	CodTipoLista = CodTipoLista + "," + CodTipoListaVet.elementAt(i);
			}
		  	else {
			 	CodTipoLista += CodTipoListaVet.elementAt(i); 
		  	}
		}
	}
}
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
if (!datRiunone.equals("")) {
	parameters = parameters + "&dataRiunone="+datRiunone;
	txtFilters += " Data CPM/Delibera Provinciale: <strong>" + datRiunone + "</strong>;";
}

//if (!datRiunoneA.equals("")) {
//	parameters = parameters + "&dataRiunoneA="+datRiunoneA;
//	txtFilters += " Data riunone comitato a: <strong>" + datRiunoneA + "</strong>;";
//}

if (!CodTipoLista.equals("")) {
	parameters = parameters + "&CodTipoLista="+CodTipoLista;
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
	   	url="AdapterHTTP?PAGE=RicercaComitatoSilPage";
		url += "&CDNFUNZIONE="+"<%=_funzione%>";
	    setWindowLocation(url);
  	}
  	
  	function stampaLista() {
		apriGestioneDoc('RPT_MOB_COMITATO','<%=parameters%>+&stampa=stampa','MOBLISTR');
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
	<af:list moduleName="M_ListaComitatoSil" />    
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


