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
<%@ include file="GestioneCacheTrasfRamoAzienda.inc" %>
<%
    // NOTE: Attributi della pagina (pulsanti e link) 
    PageAttribs attributi = new PageAttribs(user, (String) serviceRequest.getAttribute("PAGE"));
    String _funzione=(String) serviceRequest.getAttribute("CDNFUNZIONE");
    //Oggetti per l'applicazione dello stile grafico
    String htmlStreamTop = StyleUtils.roundTopTable(false);
    String htmlStreamBottom = StyleUtils.roundBottomTable(false);

	//Gestione cache e recupero eventuali lavoratori già checkati
	NavigationCache checkedLavCache = (NavigationCache) sessionContainer.getAttribute("TRASFRAMOAZIENDACACHE");
	Vector checkedLavVector = new Vector();
	if (checkedLavCache != null) {
		//Controllo coerenza cache con aziende in request
		String PRGAZIENDAPROVENIENZA = StringUtils.getAttributeStrNotNull(serviceRequest, "PRGAZIENDAPROVENIENZA");
    	String PRGUNITAPROVENIENZA = StringUtils.getAttributeStrNotNull(serviceRequest, "PRGUNITAPROVENIENZA");
    	String PRGAZIENDAPROVCACHE = (String) checkedLavCache.getField("PRGAZIENDAPROVENIENZA"); 
    	String PRGUNITAPROVCACHE = (String) checkedLavCache.getField("PRGUNITAPROVENIENZA"); 
		if (!PRGAZIENDAPROVENIENZA.equals(PRGAZIENDAPROVCACHE) || 
			!PRGUNITAPROVENIENZA.equals(PRGUNITAPROVCACHE)) {
			//Cache incoerente, la cancello e la ricreo
			sessionContainer.delAttribute("TRASFRAMOAZIENDACACHE");
			String[] fields = {"CHECKBOXMOV", "PRGAZIENDAPROVENIENZA", "PRGUNITAPROVENIENZA"};
			NavigationCache newCache = new NavigationCache(fields);
			newCache.enable();
			sessionContainer.setAttribute("TRASFRAMOAZIENDACACHE", newCache);						
		} else  {
			//Cache coerente, recupero dati
			Object checkedLavObj = checkedLavCache.getField("CHECKBOXMOV");
			if (checkedLavObj != null) {
				if (checkedLavObj instanceof Vector) {
					checkedLavVector = (Vector) checkedLavObj;
				} else if (!"EMPTY".equalsIgnoreCase(checkedLavObj.toString())) {
					checkedLavVector.addElement(checkedLavObj.toString());
				}
			}
		}
	} else {
		//Cache inesistente, la creo
		String[] fields = {"CHECKBOXMOV", "PRGAZIENDAPROVENIENZA", "PRGUNITAPROVENIENZA"};
		NavigationCache newCache = new NavigationCache(fields);
		newCache.enable();
		sessionContainer.setAttribute("TRASFRAMOAZIENDACACHE", newCache);
	}
	
	Iterator iterCheckedLav = checkedLavVector.iterator();
  	
%>

<html>
	<head>
	  	<link rel="stylesheet" href="../../css/stili.css" type="text/css">
  		<link rel="stylesheet" href="../../css/listdetail.css" type="text/css">
	  	    <af:linkScript path="../../js/"/>
	  	<title>Trasferimento Ramo Aziendale (Scelta Lavoratori)</title>
	
	  	<script language="Javascript">
		<% 
			//Genera il Javascript che si occuperà di inserire i links nel footer
			attributi.showHyperLinks(out, requestContainer,responseContainer,"");
		%>
	  	</SCRIPT>
	  		
		<!--Funzioni per l'aggiornamento della form -->
		<script type="text/javascript">
		<!--
		//Array con i valori delle checkbox che erano già checkate precedentemente
    	var checkedLavList = new Array(<%
    		boolean firstElement = true;
    		while (iterCheckedLav.hasNext()) {
    			if (!firstElement) {out.print(", ");}
    			else {firstElement = false;}
    			out.print("\"" + (String) iterCheckedLav.next() + "\"");
    		}
    		%>);
    	
    	
    	//Variabile che indica il numero di checkbox correntemente selezionate	
		var checkNumber = 0;
		
    	//funzione che imposta i valori iniziali delle checkbox scorrendo la form contenuta nella pagina
    	function initCheckboxLav() {
    		for (var j = 0; j < document.Frm1.length; j++){
    			var checkbox = document.Frm1.elements[j];
    			if (checkbox.name == "CHECKBOXMOV") {
    				for (var i = 0; (i < checkedLavList.length) && (!checkbox.checked); i++) {
    					if (checkedLavList[i] == checkbox.value) {
    						checkbox.checked = true;
    						checkNumber += 1;
    					}
    				} 
    			}
    		}
    		gestisciCampoEmpty();
    	}
    	
    	//Funzione che viene eseguita ad ogni click delle checkbox
    	function checkboxLavClick(checkbox, cdnLavoratore) {
    		checkbox.value = cdnLavoratore;
    		if (checkbox.checked) {checkNumber += 1;}
    		else checkNumber -= 1;
    		gestisciCampoEmpty();    		 
    	}
    	
    	//Funzione che gestisce tutte le checkbox della pagina
    	function checkAllCheckboxLav(checked) {
    		for (var j = 0; j < document.Frm1.length; j++){
    			var checkbox = document.Frm1.elements[j];
    			if (checkbox.name == "CHECKBOXMOV") {
    				if (checkbox.type == "checkbox") {
    					//Controllo se la checkbox è diversa dalla selezione
    					if (checkbox.checked != checked) {
    						//Modifico la checkbox e aggiorno il contatore
    						checkbox.checked = checked;
    						if (checked) {
    							checkNumber += 1;
    						} else 	checkNumber -= 1;	
    					}
    				}
    			}
    		}
    		gestisciCampoEmpty();    		
    	}    
    	
    	//Gestione del campo empty della pagina per l'aggiornamento della cache di navigazione
    	function gestisciCampoEmpty() {
    		document.getElementById("empty").disabled = (checkNumber > 0);
    	}
    	
    	//Navigazione tra le linguette
    	function avanti() {
    		document.Frm1.PAGE.value = "TrasfRamoInfoPage";
    		if (controllaFunzTL()) { doFormSubmit(document.Frm1); }
    	}	
    	
    	function indietro() {
			// Se la pagina è già in submit, ignoro questo nuovo invio!
			if (isInSubmit()) return;

    		document.Frm1.PAGE.value = "TrasfRamoSceltaAziendePage";
    		doFormSubmit(document.Frm1);
    	}
    	
    	//Funzione per l'apertura della pop-up di filtro lavoratori
    	function apriFiltraLavoratori() {
        	var f = "AdapterHTTP?PAGE=TrasfRamoFiltroLavPage" + 
        			"&FILTROCFLAV=" + document.Frm1.FILTROCFLAV.value + 
        			"&FILTROCOGNOMELAV=" + document.Frm1.FILTROCOGNOMELAV.value +
        			"&FILTRONOMELAV=" + document.Frm1.FILTRONOMELAV.value +        			        			
        			"&FILTRODATAINIZIOASS=" + document.Frm1.FILTRODATAINIZIOASS.value +
        			"&FILTRODATAFINEASS=" + document.Frm1.FILTRODATAFINEASS.value +
        			"&FILTROCODTIPOASS=" + document.Frm1.FILTROCODTIPOASS.value
        	var t = "_blank";
        	var feat = "toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,width=750,height=650,top=30,left=180";
        	window.open(f, t, feat);    		
    	}
    	
    	//Funzione che aggiorna i campi di filtro nella ricerca dei lavoratori
    	function updateFiltriLavoratori(cf, cognome, nome, datainizioass, datafineass, codtipoass) {
			// Se la pagina è già in submit, ignoro questo nuovo invio!
			if (isInSubmit()) return;

    		document.Frm1.FILTROCFLAV.value = cf;
    		document.Frm1.FILTROCOGNOMELAV.value = cognome;
    		document.Frm1.FILTRONOMELAV.value = nome;
    		document.Frm1.FILTRODATAINIZIOASS.value = datainizioass;
    		document.Frm1.FILTRODATAFINEASS.value = datafineass;
    		document.Frm1.FILTROCODTIPOASS.value = codtipoass;
    		//Ricarico la pagina
    		document.Frm1.PAGE.value = "TrasfRamoSceltaLavPage";
    		doFormSubmit(document.Frm1);    		
    		
    	}
    	
	    //Funzione che consente la navigazione tra le linguette
	    function goToLinguetta(page, checkForm) {
			// Se la pagina è già in submit, ignoro questo nuovo invio!
			if (isInSubmit()) return;

	    	document.Frm1.PAGE.value = page;
	    	if (checkForm) {
	    		if (controllaFunzTL()) {
	    			doFormSubmit(document.Frm1);
	    		}
	    	} else doFormSubmit(document.Frm1);
	    }    	
		-->
	 </script>
	</head>    
	<body class="gestione" onload="rinfresca();">
		<div class='menu'>
			<a href='#' onclick="goToLinguetta('TrasfRamoSceltaAziendePage', false)" class="bordato1"><span class='tr_round11'>1&nbsp;Scelta&nbsp;Aziende</span></a>
			<a href='#' class='sel1'><span class='tr_round1'>2&nbsp;Scelta&nbsp;Movimenti</span></a>
			<a href='#' onclick="goToLinguetta('TrasfRamoInfoPage', true)" class="bordato1"><span class='tr_round11'>3&nbsp;Ulteriori&nbsp;Informazioni</span></a>
		</div>
		<af:form name="Frm1" method="POST" action="AdapterHTTP">
    		<input type="hidden" name="PAGE" value="TrasfRamoInfoPage"/>
    		<input type="hidden" name="cdnFunzione" value="<%=_funzione%>"/>
    		<input type="hidden" name="UPDATETRASFRAMOAZIENDACACHE" value="true"/>        		

			<table width="96%" align="center">
				<tr valign="middle">
					<td align="left" class="azzurro_bianco">
					<input type="checkbox" value="" name="SelectAll" onclick="javascript:checkAllCheckboxLav(this.checked);"/>&nbsp;Seleziona/Deseleziona tutti
					<input id="empty" type="hidden" value="EMPTY" name="CHECKBOXMOV"/>
					&nbsp;&nbsp;
					</td>
					<td>&nbsp;</td>
				</tr>
			</table>    
    				
			<%@ include file="CampiComuniTrasfRamoAzienda.inc" %>
			<center>						
	  			<af:list moduleName="M_TRGetListaLav"/>
				<input class="pulsanti" type="button" name="filtra" value="Filtra movimenti" onclick="javascript:apriFiltraLavoratori();"/>
	  		</center>

		</af:form>			
		<SCRIPT language="javascript">
			initCheckboxLav();
		</SCRIPT>	
	</body>
</html>
