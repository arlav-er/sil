<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">

<%@ page import="com.engiweb.framework.base.*,
                com.engiweb.framework.security.*,
                java.lang.*,java.text.*,java.util.*,
                it.eng.afExt.utils.*, it.eng.sil.util.*,
                it.eng.sil.security.*" %>
<%@ include file="../../global/noCaching.inc" %>
<%@ include file="../../global/getCommonObjects.inc" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%@ taglib uri="aftags" prefix="af"%>

<% 
	String _page = (String) serviceRequest.getAttribute("PAGE");
	ProfileDataFilter filter = new ProfileDataFilter(user, _page);
	String queryString = "";
	
	if (! filter.canView()){
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
	}else{
		 String _funzione=(String) serviceRequest.getAttribute("CDNFUNZIONE");
		
		 String codiceFiscale 				= StringUtils.getAttributeStrNotNull(serviceRequest,"CF");
		 String cognome       				= StringUtils.getAttributeStrNotNull(serviceRequest,"COGNOME");
		 String nome          				= StringUtils.getAttributeStrNotNull(serviceRequest,"NOME");
		 String dataPrestazioneDa       	= StringUtils.getAttributeStrNotNull(serviceRequest,"dataPrestazioneDa");
		 String dataPrestazioneA        	= StringUtils.getAttributeStrNotNull(serviceRequest,"dataPrestazioneA");
		 String dataInizioPrestazioneDa     = StringUtils.getAttributeStrNotNull(serviceRequest,"dataInizioPrestazioneDa");
		 String dataInizioPrestazioneA      = StringUtils.getAttributeStrNotNull(serviceRequest,"dataInizioPrestazioneA");
		 String dataFinePrestazioneDa      	= StringUtils.getAttributeStrNotNull(serviceRequest,"dataFinePrestazioneDa");
		 String dataFinePrestazioneA      	= StringUtils.getAttributeStrNotNull(serviceRequest,"dataFinePrestazioneA");
		 String dataComunicazioneDa      	= StringUtils.getAttributeStrNotNull(serviceRequest,"dataComunicazioneDa");
		 String dataComunicazioneA      	= StringUtils.getAttributeStrNotNull(serviceRequest,"dataComunicazioneA");
		 String IDComunicazione      		= StringUtils.getAttributeStrNotNull(serviceRequest,"IDComunicazione");
		 String NProvvedimento      		= StringUtils.getAttributeStrNotNull(serviceRequest,"NProvvedimento");
		 String dataProvvedimento      		= StringUtils.getAttributeStrNotNull(serviceRequest,"dataProvvedimento");
		 String TipoEvento      			= StringUtils.getAttributeStrNotNull(serviceRequest,"TipoEvento");
		 String TipoComunicazione      		= StringUtils.getAttributeStrNotNull(serviceRequest,"TipoComunicazione");
		 String MotivoComunicazione      	= StringUtils.getAttributeStrNotNull(serviceRequest,"MotivoComunicazione");
		 String StatoDomanda      			= StringUtils.getAttributeStrNotNull(serviceRequest,"StatoDomanda");
		 String Autorizzabile      			= StringUtils.getAttributeStrNotNull(serviceRequest,"Autorizzabile");
	
%>

<html>
<head>
<title></title>
 <%@ include file="../../documenti/_apriGestioneDoc.inc"%>
 <link rel="stylesheet" type="text/css" href="../../css/stili.css" />
 <link rel="stylesheet" type="text/css" href="../../css/listdetail.css" />
 <af:linkScript path="../../js/" />
 
<script type="text/javascript">
function tornaAllaRicerca()
{  // Se la pagina è già in submit, ignoro questo nuovo invio!
   if (isInSubmit()) return;

   url="AdapterHTTP?PAGE=NuovoRAPage";
   url += "&CDNFUNZIONE="+"<%=_funzione%>";
   url += "&CF="+"<%=codiceFiscale%>";
   url += "&nome="+"<%=nome%>";
   url += "&cognome="+"<%=cognome%>";
   url += "&dataPrestazioneDa="+"<%=dataPrestazioneDa%>";
   url += "&dataPrestazioneA="+"<%=dataPrestazioneA%>";
   url += "&dataInizioPrestazioneDa="+"<%=dataInizioPrestazioneDa%>";
   url += "&dataInizioPrestazioneA="+"<%=dataInizioPrestazioneA%>";
   url += "&dataFinePrestazioneDa="+"<%=dataFinePrestazioneDa%>";
   url += "&dataFinePrestazioneA="+"<%=dataFinePrestazioneA%>";
   url += "&dataComunicazioneDa="+"<%=dataComunicazioneDa%>";
   url += "&dataComunicazioneA="+"<%=dataComunicazioneA%>";
   url += "&IDComunicazione="+"<%=IDComunicazione%>";
   url += "&NProvvedimento="+"<%=NProvvedimento%>";
   url += "&dataProvvedimento="+"<%=dataProvvedimento%>";
   url += "&TipoEvento="+"<%=TipoEvento%>";
   url += "&TipoComunicazione="+"<%=TipoComunicazione%>";
   url += "&MotivoComunicazione="+"<%=MotivoComunicazione%>";
   url += "&StatoDomanda="+"<%=StatoDomanda%>";
   url += "&Autorizzabile="+"<%=Autorizzabile%>";
   setWindowLocation(url);
}

function stampaLista() {
	apriGestioneDoc('RPT_LISTA_NRA',
		  	'&CF=<%=codiceFiscale%>&nome=<%=nome%>&cognome=<%=cognome%>&dataPrestazioneDa=<%=dataPrestazioneDa%>' +
			'&dataPrestazioneA=<%=dataPrestazioneA%>&dataInizioPrestazioneDa=<%=dataInizioPrestazioneDa%>&dataInizioPrestazioneA=<%=dataInizioPrestazioneA%>' + 
			'&dataFinePrestazioneDa=<%=dataFinePrestazioneDa%>&dataFinePrestazioneA=<%=dataFinePrestazioneA%>&dataComunicazioneDa=<%=dataComunicazioneDa%>' + 
			'&dataComunicazioneA=<%=dataComunicazioneA%>&IDComunicazione=<%=IDComunicazione%>&NProvvedimento=<%=NProvvedimento%>' +
			'&dataProvvedimento=<%=dataProvvedimento%>&TipoEvento=<%=TipoEvento%>&TipoComunicazione=<%=TipoComunicazione%>' + 
			'&MotivoComunicazione=<%=MotivoComunicazione%>&StatoDomanda=<%=StatoDomanda%>&Autorizzabile=<%=Autorizzabile%>', 'STRA');
}
</script>
 
</head>

<body onload="rinfresca();rinfresca_laterale()" class="gestione">

<af:error/>
<af:list moduleName="M_ListaNRA" getBack="true"/>
<center>
<input class="pulsanti" type = "button" name="stampa" value="Stampa" onclick="stampaLista();" />
<input class="pulsante" type = "button" name="torna" value="Torna alla pagina di ricerca" onclick="tornaAllaRicerca()"/>
</center>
</body>
</html>
<%}%>
