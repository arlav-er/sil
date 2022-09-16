<!-- @author: Paolo Roccetti - Gennaio 2004 -->
<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="
                  com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.afExt.utils.*,
                  it.eng.sil.security.*,
                  it.eng.sil.util.*,
                  it.eng.sil.module.movimenti.*,
                  java.util.*,
                  java.math.*,
                  java.io.*,
                  com.engiweb.framework.security.*" %>


<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
	// NOTE: Attributi della pagina (pulsanti e link) 
	PageAttribs attributi = new PageAttribs(user, "ListaUnitaAziendaPage");
	String _funzione=(String) serviceRequest.getAttribute("CDNFUNZIONE");
	
	String prgMovimentoApp = StringUtils.getAttributeStrNotNull(serviceRequest, "PRGMOVIMENTOAPP");
	String prgMobilitaApp = StringUtils.getAttributeStrNotNull(serviceRequest, "prgMobilitaIscrApp");
	
	//Mi dice se sto operando per l'azienda principale o per quella utilizzatrice
	String contesto = StringUtils.getAttributeStrNotNull(serviceRequest, "CONTESTO");
	boolean contestoAzienda = contesto.equalsIgnoreCase("AZIENDA");
	boolean contestoAzUtil = contesto.equalsIgnoreCase("AZUTIL");	
	
	//Recupero i dati disponibili sull'azienda dal movimento
	String indirizzoUazMov = "";
	String comuneUazMov = "";
	
	if (!prgMobilitaApp.equals("")) {
		if (contestoAzienda) {
			indirizzoUazMov = StringUtils.getAttributeStrNotNull(serviceResponse, "M_MobilitaGetDettaglioMobApp.ROWS.ROW.STRUAINDIRIZZO");
			comuneUazMov = StringUtils.getAttributeStrNotNull(serviceResponse, "M_MobilitaGetDettaglioMobApp.ROWS.ROW.STRDESCRCOMUNE");
		}
	}
	else {
		if (contestoAzienda) {
			indirizzoUazMov = StringUtils.getAttributeStrNotNull(serviceResponse, "M_MovGetDettMovApp.ROWS.ROW.strIndirizzoUAz");
			comuneUazMov = StringUtils.getAttributeStrNotNull(serviceResponse, "M_MovGetDettMovApp.ROWS.ROW.strComuneUAz");
		} else if (contestoAzUtil) {
			indirizzoUazMov = StringUtils.getAttributeStrNotNull(serviceResponse, "M_MovGetDettMovApp.ROWS.ROW.strIndirizzoUAzUtil");
			comuneUazMov = StringUtils.getAttributeStrNotNull(serviceResponse, "M_MovGetDettMovApp.ROWS.ROW.strComuneUAzUtil");
		}
	}
	//Recupero risultati della query di lista per vedere se devo visualizzare il pulsante di inserimento o meno
	boolean canInsertNewUnita = true;
	//Se il comune non è indicato lo metto di default
	if (!comuneUazMov.equals("")) {
		Vector rows = serviceResponse.getAttributeAsVector("M_MovimentiGetListaAziende.ROWS.ROW");
		for (int i = 0; i < rows.size(); i++ ) {
			SourceBean row = (SourceBean) rows.get(i);
			String indirizzo = StringUtils.getAttributeStrNotNull(row, "STRINDIRIZZO");
			if (indirizzo.equalsIgnoreCase(indirizzoUazMov)) {
				canInsertNewUnita = false;
			}
		}
	}
	
	String strInsertNuovaUnita = (canInsertNewUnita ? "" : "0");
%>

<html>
<head>
  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <link rel="stylesheet" href="../../css/listdetail.css" type="text/css">
  <af:linkScript path="../../js/"/>
  <title>Lista Unita Aziendali compatibili</title>
  <script language="Javascript">
     <% 
     	//Genera il Javascript che si occuperà di inserire i links nel footer
        attributi.showHyperLinks(out, requestContainer,responseContainer,"");
     %>
	
  </script>
</head>

<body class="gestione" onload="">
	<CENTER>
	  	<p class="titolo">Lista Unita Aziendali compatibili</p>
	  <br/>
	  <p>Dati contenuti nel movimento:<br/>
		Indirizzo:&nbsp;<strong><%=(!indirizzoUazMov.equals("") ? indirizzoUazMov : "Non indicato")%></strong><br/>
		Comune:&nbsp;<strong><%=(!comuneUazMov.equals("") ? comuneUazMov : "Non indicato")%></strong>
	  </p>
	  <af:list moduleName="M_MovimentiGetListaAziende"
	  	configProviderClass="it.eng.sil.module.movimenti.DynamicListaUnitaAziendeConfig" 
	  	canInsert="<%=strInsertNuovaUnita%>"/>	
	
	<input type="button" class="pulsanti" value="Chiudi" onClick="window.close();"/>
	</CENTER> 
	   
</body>  
  
  
  
  
