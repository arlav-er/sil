<!-- @author: Paolo Roccetti - Agosto 2004 -->
<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../../global/noCaching.inc" %>
<%@ include file="../../global/getCommonObjects.inc" %>

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
    PageAttribs attributi = new PageAttribs(user, (String) serviceRequest.getAttribute("PAGE"));
    String _funzione=(String) serviceRequest.getAttribute("CDNFUNZIONE");
    String erroreBloccante= (String) serviceResponse.getAttribute("M_TRTrasferisci.ERRORE_BLOCCANTE");
    String warningTra= (String) serviceResponse.getAttribute("M_TRTrasferisci.WARNING_TRASFERIMENTO");
    //Oggetti per l'applicazione dello stile grafico
    String htmlStreamTop = StyleUtils.roundTopTable(false);
    String htmlStreamBottom = StyleUtils.roundBottomTable(false);
	
	//Controllo se ho avuto problemi nel trasferimento
	boolean problemi = (serviceResponse.getAttributeAsVector("M_TRTrasferisci.RESULT.ROWS.ROW")).size() > 0;

%>

<html>
	<head>
	  	<link rel="stylesheet" href="../../css/stili.css" type="text/css">
  		<link rel="stylesheet" href="../../css/listdetail.css" type="text/css">
	  	    <af:linkScript path="../../js/"/>
	  	<title>Trasferimento Ramo Aziendale (Risultati del trasferimento)</title>
	
	  	<script language="Javascript">
		<% 
			//Genera il Javascript che si occuperà di inserire i links nel footer
			attributi.showHyperLinks(out, requestContainer,responseContainer,"");
		%>
	  	</SCRIPT>
	
		<!--Funzioni per l'aggiornamento della form -->
		<script type="text/javascript">
		<!--
		
		//Navigazione tra le linguette
    	function indietro() {
			// Se la pagina è già in submit, ignoro questo nuovo invio!
			if (isInSubmit()) return;

    		doFormSubmit(document.Frm1);
    	}
    	
		-->
	 </script>
	</head>    
	<body class="gestione" onload="rinfresca();">
  		<br/>
  		<p class="titolo">Trasferimento Ramo Aziendale (Risultati del trasferimento)</p>
  		<br/>
		<center>
		<% if (warningTra != null) { 
			out.println("<p class=\"titolo\">WARNING: " + warningTra + "</p>");
		}
		if (problemi) {
			if(erroreBloccante!=null){
					out.println("<p class=\"titolo\">ERRORE BLOCCANTE: tutti i trasferimenti sono stati bloccati.</p>");
			} 
		%>
			<af:list configProviderClass="it.eng.sil.module.movimenti.trasfRamoAzienda.DynConfigResultList" moduleName="M_TRTrasferisci.RESULT"/>
		<%  if(erroreBloccante==null){ %>
	    	<af:list moduleName="M_TRGetListaMovLavTrasferiti" skipNavigationButton="1"/>
		<%  } 
	     } else {%>
	    	<p class="titolo">Tutti i lavoratori selezionati sono stati correttamente trasferiti</p>
	    	<br>
	    	<af:list moduleName="M_TRGetListaMovLavTrasferiti" skipNavigationButton="1"/>
	    <%}%>
			<af:form name="Frm1" method="POST" action="AdapterHTTP">
        	<input type="hidden" name="PAGE" value="TrasfRamoSceltaLavPage"/>
        	<input type="hidden" name="cdnFunzione" value="<%=_funzione%>"/>
        	<input type="button" name="Ritorna" value="Ritorna" onclick="javascript:doFormSubmit(document.Frm1);" class="pulsanti"/>
			<%@ include file="CampiComuniTrasfRamoAzienda.inc" %>		
			</af:form>
		</center>
	</body>
</html>
