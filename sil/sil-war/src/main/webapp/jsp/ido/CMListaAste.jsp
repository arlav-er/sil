<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">

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
      
	if (! filter.canView()){
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
	}
	else{


	    PageAttribs attributi = new PageAttribs(user, "CMListaAstePage");
	
		String cdnLavoratore= (String )serviceRequest.getAttribute("CDNLAVORATORE");
	    String cdnFunzione=(String)serviceRequest.getAttribute("CDNFUNZIONE");
				
	    String strErrorCode = "";
	    String msgConferma = "";
	    boolean confirm = false;   
	    	   
	    //Gestione cache e recupero eventuali lavoratori già checkati
		NavigationCache checkedAdesCache = (NavigationCache) sessionContainer.getAttribute("CMLISTAASTECACHE");
		Vector checkedAdesVector = new Vector();
		if (checkedAdesCache != null) {
			//Controllo coerenza cache con aziende in request
			String PRGROSA = StringUtils.getAttributeStrNotNull(serviceRequest, "PRGROSA");
	    	
	    	String PRGROSACACHE = (String) checkedAdesCache.getField("PRGROSA"); 
	    
			if (!PRGROSA.equals(PRGROSACACHE)) {
				//Cache incoerente, la cancello e la ricreo
				sessionContainer.delAttribute("CMLISTAASTECACHE");
				String[] fields = {"CHECKBOXADES", "PRGROSA"};
				NavigationCache newCache = new NavigationCache(fields);
				newCache.enable();
				sessionContainer.setAttribute("CMLISTAASTECACHE", newCache);						
			} else  {
				//Cache coerente, recupero dati
				Object checkedAdesObj = checkedAdesCache.getField("CHECKBOXADES");  
				if (checkedAdesObj != null) {
					if (checkedAdesObj instanceof Vector) {
						checkedAdesVector = (Vector) checkedAdesObj;
					} else if (!"EMPTY".equalsIgnoreCase(checkedAdesObj.toString())) {
						checkedAdesVector.addElement(checkedAdesObj.toString());
					}
				}
			}
		} else {
			//Cache inesistente, la creo
			String[] fields = {"CHECKBOXADES", "PRGROSA"};
			NavigationCache newCache = new NavigationCache(fields);
			newCache.enable();
			sessionContainer.setAttribute("CMLISTAASTECACHE", newCache);
		}
		
		Iterator iterCheckedAdes = checkedAdesVector.iterator();   
		
		String tipoGraduatoriaCm = "";
		if (serviceResponse.containsAttribute("M_GetConfigGraduatoria_cm.ROWS.ROW.codmonotipogradcm")) {
			tipoGraduatoriaCm = (String)serviceResponse.getAttribute("M_GetConfigGraduatoria_cm.ROWS.ROW.codmonotipogradcm").toString();
		}		
		
		String prgSpi = "";
		if (serviceResponse.containsAttribute("M_GETPRGSPIUT.ROWS.ROW.PRGSPI")){
		    prgSpi     = (String)serviceResponse.getAttribute("M_GETPRGSPIUT.ROWS.ROW.PRGSPI").toString();
		}
		else {
			prgSpi = "";
		}
%>


<html>
<head>
<title></title>
 <link rel="stylesheet" type="text/css" href="../../css/stili.css" />
 <link rel="stylesheet" type="text/css" href="../../css/listdetail.css" />
 <af:linkScript path="../../js/" />
 
 <script type="text/javascript">
		<!--
		//Array con i valori delle checkbox che erano già checkate precedentemente  
    	var checkedAdesList = new Array(<%
    		boolean firstElement = true;
    		while (iterCheckedAdes.hasNext()) {
    			if (!firstElement) {out.print(", ");}
    			else {firstElement = false;}
    			out.print("\"" + (String) iterCheckedAdes.next() + "\"");
    		}
    		%>);
    	
    	
    	//Variabile che indica il numero di checkbox correntemente selezionate	
		var checkNumber = 0;
		
    	//funzione che imposta i valori iniziali delle checkbox scorrendo la form contenuta nella pagina
    	function initCheckboxAdes() {
    		for (var j = 0; j < document.Frm1.length; j++){
    			var checkbox = document.Frm1.elements[j];
    			if (checkbox.name == "CHECKBOXADES") {	    			      			     
    				for (var i = 0; (i < checkedAdesList.length) && (!checkbox.checked); i++) {
    					if (checkedAdesList[i] == checkbox.value) {    					   
    						checkbox.checked = true;
    						checkNumber += 1;
    					}
    				} 
    			}
    		}
    		gestisciCampoEmpty();
    	}
    	
    	//Funzione che viene eseguita ad ogni click delle checkbox
    	function checkboxAdesClick(checkbox, prgrosa, checkControllo, checkMovimento) {
    	    /*
    	    if (checkControllo != 0 && checkMovimento != 0) { 
	    	    if (checkbox.checked) {    	            	    
	     		  	alert("Attenzione: il lavoratore ha un rapporto di lavoro aperto alla data della chiamata. \r\n La richiesta prevede un contratto a tempo determinato");
	     		}	    		    		
    		}	 
    		else {
    		*/
    		    checkbox.value = prgrosa;
	    		if (checkbox.checked) {checkNumber += 1;}
	    		else checkNumber -= 1;
	    		gestisciCampoEmpty();   
	    	/*	   		   
    		}
    		*/
    	}
    	
    	//Funzione che gestisce tutte le checkbox della pagina
    	function checkAllCheckboxAdes(checked) {
    		for (var j = 0; j < document.Frm1.length; j++){
    			var checkbox = document.Frm1.elements[j];
    			if (checkbox.name == "CHECKBOXADES") {
    				if (checkbox.type == "checkbox") {    				    
    					//Controllo se la checkbox è diversa dalla selezione
    					if (checkbox.checked != checked) {
    						//Modifico la checkbox e aggiorno il contatore
    						checkbox.checked = checked;
    						checkbox.onclick();
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
    	
    	function checkRichieste() {
	    	var prgRosaChk = "";
    		for (var j = 0; j < document.Frm1.length; j++){
    			var checkbox = document.Frm1.elements[j];    			
    			if (checkbox.name == "CHECKBOXADES") {
    				if (checkbox.checked) {
    				    if (prgRosaChk != "") {
    				    	prgRosaChk += "@" + checkbox.value;
    				    }
    				    else {
    				    	prgRosaChk = checkbox.value;
    				    }
    				}
    			}
    		} 
    		document.Frm1.CKBOX_ADESIONE.value = prgRosaChk;
    	}
       -->    	  	
	 </script>
	
 <script language="Javascript">
  function tornaAllaRicerca()
  {   
      // Se la pagina è già in submit, ignoro questo nuovo invio!
      if (isInSubmit()) return;
<%
	  String datChiamata = StringUtils.getAttributeStrNotNull(serviceRequest, "datChiamata");         
      String utente          = StringUtils.getAttributeStrNotNull(serviceRequest, "utente");    
      
	  datChiamata = com.engiweb.framework.tags.Util.urlEncode( datChiamata );	  	  	  
	  utente = com.engiweb.framework.tags.Util.urlEncode( utente );
%>      
      url="AdapterHTTP?PAGE=CMCercaRichiesteAdesionePage";
      url += "&CDNFUNZIONE="+"<%=cdnFunzione%>";      
      url += "&cdnLavoratore="+"<%=cdnLavoratore%>";   
      url += "&datChiamata="+"<%=datChiamata%>";
      url += "&utente="+"<%=utente%>";    
     
      setWindowLocation(url);
  }
  
  function tornaAllaLista()
  {   
      // Se la pagina è già in submit, ignoro questo nuovo invio!
      if (isInSubmit()) return;
      
      url="AdapterHTTP?PAGE=CMListaAdesioniPage";
      url += "&CDNFUNZIONE="+"<%=cdnFunzione%>";      
      url += "&cdnLavoratore="+"<%=cdnLavoratore%>";     
     
      setWindowLocation(url);
  }
  
  function associa() {
	  // Se la pagina è già in submit, ignoro questo nuovo invio!
	  if (isInSubmit()) return;
		
	  checkRichieste();	
	  	 
	  if( document.Frm1.CKBOX_ADESIONE.value == "" ) {
      	alert("Selezionare almeno una richiesta!");
      	return false;
      }	  	
      
      if(document.Frm1.prgSpi.value == "") {
      	alert("Selezionare un operatore!");
      	return false;
      }       
      <% 
	  if (("4").equals(tipoGraduatoriaCm)) { 
	  %>	
      	if(document.Frm1.QUALIFICA.value == "") {
        	alert("Selezionare la qualifica!");
        	return false;
     	 }
      <%
	  }
      %>
      return true;	  
  }
  

 </script>
</head>

<body onload="rinfresca()">
<af:form name="Frm1" method="POST" action="AdapterHTTP">
<input type="hidden" name="MODULE" value="M_CMAssociaRichiesteAdesione"/>
<input type="hidden" name="CDNLAVORATORE" value="<%=cdnLavoratore%>"/>
<input name="P_CDNUTENTE" type="hidden" value="<%=user.getCodut()%>"/>
<input name="P_CDNGRUPPO" type="hidden" value="<%=user.getCdnGruppo()%>"/>
<input type="hidden" name="CDNFUNZIONE" value="<%=cdnFunzione%>"/>
<input type="hidden" name="PAGE" value="CMListaAdesioniPage"/>
<input type="hidden" name="CKBOX_ADESIONE" value=""/>
<%
    if (cdnLavoratore != null) {         
        InfCorrentiLav _testata= new InfCorrentiLav(  RequestContainer.getRequestContainer().getSessionContainer(), cdnLavoratore, user);
        _testata.setSkipLista(true);
        _testata.show(out);
	}
%>
	<center>    	
        <font color="red">
          <af:showErrors />
        </font>
	</center>
	
	<p class="titolo">Lista aste</p>
	
	<table width="96%" align="center">
		<tr valign="middle">
			<td align="left" class="azzurro_bianco">	
				<table width="100%">
					<tr>	
						<td>
							<input type="checkbox" value="" name="SelectAll" onclick="javascript:checkAllCheckboxAdes(this.checked);"/>&nbsp;Seleziona/Deseleziona tutti
							<input id="empty" type="hidden" value="EMPTY" name="CHECKBOXADES"/>
							&nbsp;&nbsp;
						<td>
						<td>
							&nbsp; 
							<input type="image" src="../../img/add.gif" border="0" value="Associa" onclick="return associa();">
							Associa selezionati &nbsp;&nbsp;
						</td>		
						<td>
							&nbsp; Operatore 
							<af:comboBox name="prgSpi"
					                     size="1"
					                     title="Operatore di riferimento"
					                     multiple="false"
					                     required="false"
					                     focusOn="false"
					                     moduleName="COMBO_SPI_SCAD"
					                     addBlank="true"
					                     blankValue=""
					                     selectedValue = "<%=prgSpi%>"					                     
					                     classNameBase="input"					                    
					        />
							*
						</td>
						<% 
						if (("4").equals(tipoGraduatoriaCm)) { 
						%>						
							<td>
								&nbsp; Qualifica 
								<af:comboBox name="QUALIFICA"
					                     size="1"
					                     title="Qualifica"
					                     multiple="false"
					                     required="false"
					                     focusOn="false"
					                     moduleName="CM_GET_DE_QUALIFICATO"
					                     addBlank="true"
					                     blankValue=""					                     				                    
					                     classNameBase="input"					                    
					        />								
						    *
							</td>
						<% 
						}
						else { 
						%>
							<input type="hidden" name="QUALIFICA" value=""/>
						<%
						}
						%>
					</tr>
				</table>						
			</td>			
			<td>&nbsp;</td>
		</tr>
	</table> 
	
	<af:list moduleName="M_GetCMListaAste" />
    
    <center>
    	<input class="pulsante" type = "button" name="torna" value="Torna alla pagina di ricerca" onclick="tornaAllaRicerca()"/>
    	&nbsp;&nbsp;
    	<input class="pulsante" type = "button" name="torna" value="Lista Adesioni" onclick="tornaAllaLista()"/>
    </center>
    
<br>       		  
</af:form>
<SCRIPT language="javascript">
	initCheckboxAdes();
</SCRIPT>

</body>
</html>
<% } %>