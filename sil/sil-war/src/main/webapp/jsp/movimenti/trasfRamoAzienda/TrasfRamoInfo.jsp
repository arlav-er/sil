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
                  java.text.*,
                  com.engiweb.framework.security.*,
                  it.eng.sil.bean.protocollo.ProtocolloDocumentoUtil" %>


<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
    // NOTE: Attributi della pagina (pulsanti e link) 
    PageAttribs attributi = new PageAttribs(user, (String) serviceRequest.getAttribute("PAGE"));
    String _funzione=(String) serviceRequest.getAttribute("CDNFUNZIONE");
    //Oggetti per l'applicazione dello stile grafico
    String htmlStreamTop = StyleUtils.roundTopTable(false);
    String htmlStreamBottom = StyleUtils.roundBottomTable(false);
	boolean canModify = true;
	
	//Variabili per la gestione della protocollazione ================
	String prAutomatica     = null; 
	String estReportDefautl = null;
	BigDecimal numProtV     = null;
	BigDecimal numAnnoProtV = null;
	String     datProtV     = (new SimpleDateFormat("dd/MM/yyyy")).format(new Date());
	String     oraProtV     = (new SimpleDateFormat("HH:mm")).format(new Date());
	String     docInOut     = "";
	String     docRif       = "";
	boolean numProtEditable = false;
	Vector rowsPR           = null;
	SourceBean rowPR        = null;
	
	rowsPR = serviceResponse.getAttributeAsVector("M_GetProtocollazione.ROWS.ROW");
	
	
	if(rowsPR != null && !rowsPR.isEmpty())
	{ rowPR = (SourceBean) rowsPR.elementAt(0);
		prAutomatica     = (String) rowPR.getAttribute("FLGPROTOCOLLOAUT");
		if ( prAutomatica.equalsIgnoreCase("N") )
		{ numProtEditable = true; 
		}
		else
		{ numProtV          = (BigDecimal) rowPR.getAttribute("NUMPROTOCOLLO");
		}
		numAnnoProtV      = new BigDecimal(datProtV.substring(6,10));
		estReportDefautl = (String) rowPR.getAttribute("CODTIPOFILEESTREPORT");
	}
	
	//========================================================================



	//Campi comuni a tutte le linguette delle pagine per il trasferimento
	//del ramo aziendale, tranne per i lavoratori selezionati, che vengono
	//mantenuti in sessione perchè potrebbero essere centinaia...
	String PRGAZIENDAPROVENIENZA = StringUtils.getAttributeStrNotNull(serviceRequest, "PRGAZIENDAPROVENIENZA");
    String PRGUNITAPROVENIENZA = StringUtils.getAttributeStrNotNull(serviceRequest, "PRGUNITAPROVENIENZA");
    String PRGAZIENDADESTINAZIONE = StringUtils.getAttributeStrNotNull(serviceRequest, "PRGAZIENDADESTINAZIONE");
    String PRGUNITADESTINAZIONE = StringUtils.getAttributeStrNotNull(serviceRequest, "PRGUNITADESTINAZIONE");  
    String FILTROCFLAV = StringUtils.getAttributeStrNotNull(serviceRequest, "FILTROCFLAV");  
    String FILTRONOMELAV = StringUtils.getAttributeStrNotNull(serviceRequest, "FILTRONOMELAV");  
    String FILTROCOGNOMELAV = StringUtils.getAttributeStrNotNull(serviceRequest, "FILTROCOGNOMELAV");  
    String FILTRODATAINIZIOASS = StringUtils.getAttributeStrNotNull(serviceRequest, "FILTRODATAINIZIOASS");  
    String FILTRODATAFINEASS = StringUtils.getAttributeStrNotNull(serviceRequest, "FILTRODATAFINEASS");  
    String FILTROCODTIPOASS = StringUtils.getAttributeStrNotNull(serviceRequest, "FILTROCODTIPOASS");  
    String CODMONOMOVDICH = StringUtils.getAttributeStrNotNull(serviceRequest, "CODMONOMOVDICH");
    String DATCOMUNICAZ = StringUtils.getAttributeStrNotNull(serviceRequest, "DATCOMUNICAZ");
    String DATTRASFERIMENTO = StringUtils.getAttributeStrNotNull(serviceRequest, "DATTRASFERIMENTO");
    String NUMGGTRAMOVCOMUNICAZIONE = StringUtils.getAttributeStrNotNull(serviceRequest, "NUMGGTRAMOVCOMUNICAZIONE");
    String CODTIPOTRASF = StringUtils.getAttributeStrNotNull(serviceRequest, "CODTIPOTRASF");
    String dataFineAffittoRamo = StringUtils.getAttributeStrNotNull(serviceRequest, "DATFINEAFFITTORAMO");
    
 
%>
<%@ include file="GestioneCacheTrasfRamoAzienda.inc" %>
<%@ include file="../../global/fieldChanged.inc" %>

<html>
	<head>
	  	<link rel="stylesheet" href="../../css/stili.css" type="text/css">
	  	    <af:linkScript path="../../js/"/>
	  	    <SCRIPT language="JavaScript" src="../../js/script_comuni.js"></SCRIPT>
	  	<title>Trasferimento Ramo Aziendale (Informazioni del trasferimento)</title>
	
	  	<script language="Javascript">

		<% 
			//Genera il Javascript che si occuperà di inserire i links nel footer
			attributi.showHyperLinks(out, requestContainer,responseContainer,"");
		%>

		// =============================================================================
		//     Sono le stesse funzioni js usate in GestisciStatoDoc.jsp
		// =============================================================================
		function checkAndFormatTime(oraObj)
		{
		  var strTime = oraObj.value;
		  var strHours = "";
		  var strMin   = "";
		  var separator = ":";
		  var strTimeArray;
		
		  var titleObj = "ora"; 
		  if(oraObj.title != null)
		  { titleObj = oraObj.title;
		  }
		  
		   if (strTime.indexOf(separator) != -1) {
		      strTimeArray = strTime.split(separator);
		      if (strTimeArray.length != 2) {
		         alert("Il campo "+titleObj+" non ha un orario scritto nel formato corretto:\nscrivere HH"+separator+"MM   es: 08"+separator+"12")
		         return false;
		      }
		      else {
		       strHours = strTimeArray[0];
		       strMin   = strTimeArray[1];
		      }
		   }
		   else if(strTime.length == 4)
		   { //Non c'è il separatore, probabilmente è stata inserita un'orario nel formato 1215 -> 12:15
		     //che comunque reputiamo valido...
		     strHours = strTime.substr(0,2);
		     strMin   = strTime.substr(2,4);
		   }
		   else
		   { alert("Il campo "+titleObj+" non ha un orario scritto nel formato corretto:\nscrivere HH"+separator+"MM   es: 08"+separator+"12");
		     return false;
		   }
		
		   var hours = parseInt(strHours, 10);
		   var min   = parseInt(strMin, 10);
		   
		   if(isNaN(hours))
		   { alert("L'ora inserita nel campo "+titleObj+" non è un numero:\nscrivere HH"+separator+"MM   es: 08"+separator+"12");
		     return false;
		   }
		   if(isNaN(min))
		   { alert("I minuti inseriti nel campo "+titleObj+" non sono un numero:\nscrivere HH"+separator+"MM   es: 08"+separator+"12");
		     return false;
		   }
		
		   hours = parseInt(strHours, 10);
		   min   = parseInt(strMin, 10);
		   
		   if( hours<0 || hours > 23 )
		   { alert("L'ora inserita nel campo "+titleObj+" non è orario valido.\nInserire un'ora compresa fra 0 e 23");
		     return false;
		   }
		   if( min<0 || min > 59 )
		   { alert("I minuti inseriti nel campo "+titleObj+" non sono corretti.\nInserire un numero compreso fra 0 e 59");
		     return false;
		   }
		
			oraObj.value = (hours<10?"0":"") + hours + separator + (min<10?"0":"") + min;
			return true;	  
		   
		}//end function
		
		
		function cambiAnnoProt(dataPRObj,annoProtObj)
		{
		  var dataProt = dataPRObj.value;
		  var lun = dataProt.length;
		
		  //Stiamo modificando la data di protocollazione. Quindi cambia anche l'anno di protocollazione
		  annoProtObj.value = ""; 
		  
		  if (lun > 5) {
		    var tmpDate = new Object();
		    tmpDate.value = dataProt;
		    if ( checkFormatDate(tmpDate) ) {
		       annoProtObj.value = tmpDate.value.substr(6,10);      
		    }
		    else if (lun==8 || lun==10) {
		      alert("La data di protocollazione non è corretta");
		    }
		  }
		  
		}//end function
		// =============================================================================

	
		//Funzioni per l'aggiornamento della form
		
		//Navigazione tra le linguette
    	function indietro() {
			// Se la pagina è già in submit, ignoro questo nuovo invio!
			if (isInSubmit()) return;

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
	    
	    //Controlla che la data di comunicazione non sia futura
	    function controllaDataComunicaz(){
	    	var datComjs = document.Frm1.DATCOMUNICAZ.value;
	    	var oggi = '<%=DateUtils.getNow()%>';
	    	var esito = true;
	    	
	    	var nbrDatCom = datComjs.substr(6,4) + datComjs.substr(3,2) + datComjs.substr(0,2);
	    	var nbrOggi = oggi.substr(6,4) + oggi.substr(3,2) + oggi.substr(0,2);
	    	if(nbrDatCom > nbrOggi){ 
	    		alert('La data di comunicazione non può essere futura.');
	    		esito = false;
	    	}
	    	return esito;
	    }
	    
	    
	    function calcGgMovComunicaz(){
	    	var dataComunicazione=document.Frm1.DATCOMUNICAZ;
	    	var dataMovimento=document.Frm1.DATTRASFERIMENTO;
	    	
	    	    	
	    		
			if(!checkFormatDate(dataComunicazione) & !checkFormatDate(dataMovimento)){
				alert('Il testo inserito non è una data');
				return;
			}
			
			var strDataComunicazione=dataComunicazione.value;
	    	var strDataMovimento=dataMovimento.value;	
	    	
	    	if((strDataComunicazione != "") && (strDataMovimento != "")){
		    	var diffInGiorni = confrontaDate(strDataMovimento,strDataComunicazione );
		    	if(diffInGiorni>=0){
		    		document.Frm1.NUMGGTRAMOVCOMUNICAZIONE.value=diffInGiorni;
		    	}else{
		    		document.Frm1.NUMGGTRAMOVCOMUNICAZIONE.value=0;	    		
		    	}
	    	}
	    	
	    }
     
	    function getTipoTrasferimento() {
	    	var datFineAffittoRamo = document.getElementById("datFineAffittoRamo");
	    	if(document.Frm1.CODTIPOTRASF.value == '02') {
	    		datFineAffittoRamo.style.display = "";
	    	} else {
	    		datFineAffittoRamo.style.display = "none";
	    		document.Frm1.DATFINEAFFITTORAMO.value = "";
	    	}
	    }
	    
	    
	   	
	 </script>		 
	</head>    
	<body class="gestione" onload="rinfresca();getTipoTrasferimento();">
		<div class='menu'>
			<a href='#' onclick="goToLinguetta('TrasfRamoSceltaAziendePage', false)" class="bordato1"><span class='tr_round11'>1&nbsp;Scelta&nbsp;Aziende</span></a>
			<a href='#' onclick="goToLinguetta('TrasfRamoSceltaLavPage', false)" class="bordato1"><span class='tr_round11'>2&nbsp;Scelta&nbsp;Movimenti</span></a>
			<a href='#' class='sel1'><span class='tr_round1'>3&nbsp;Ulteriori&nbsp;Informazioni</span></a>
		</div>
		<center>
			<af:form name="Frm1" method="POST" action="AdapterHTTP" onSubmit="controllaDataComunicaz()">
        	<input type="hidden" name="PAGE" value="TrasfRamoTrasferisciPage"/>
        	<input type="hidden" name="cdnFunzione" value="<%=_funzione%>"/>
        				
			<input type="hidden" name=PRGAZIENDAPROVENIENZA value="<%=PRGAZIENDAPROVENIENZA%>"/>			
			<input type="hidden" name=PRGUNITAPROVENIENZA value="<%=PRGUNITAPROVENIENZA%>"/>			
			<input type="hidden" name=PRGAZIENDADESTINAZIONE value="<%=PRGAZIENDADESTINAZIONE%>"/>			
			<input type="hidden" name=PRGUNITADESTINAZIONE value="<%=PRGUNITADESTINAZIONE%>"/>			
			<input type="hidden" name=FILTROCFLAV value="<%=FILTROCFLAV%>"/>			
			<input type="hidden" name=FILTRONOMELAV value="<%=FILTRONOMELAV%>"/>			
			<input type="hidden" name=FILTROCOGNOMELAV value="<%=FILTROCOGNOMELAV%>"/>			
			<input type="hidden" name=FILTRODATAINIZIOASS value="<%=FILTRODATAINIZIOASS%>"/>			
			<input type="hidden" name=FILTRODATAFINEASS value="<%=FILTRODATAFINEASS%>"/>			
			<input type="hidden" name=FILTROCODTIPOASS value="<%=FILTROCODTIPOASS%>"/>		
	     		
      		<%out.print(htmlStreamTop);%>
      		<table class="main" border="0">
			<tr>
				<td class="etichetta">Provenienza</td>
				<td class="campo">
              		<af:comboBox classNameBase="input" name="CODMONOMOVDICH" title="Movimento documentato o dichiarato" onChange="fieldChanged();" required="true">
                		<option value="O" <% if (CODMONOMOVDICH.equals("O") || CODMONOMOVDICH.equals("")) {%>selected="selected" <%}%>>Da comunicazione obbligatoria</option>
                		<option value="D" <% if (CODMONOMOVDICH.equals("D")) {%>selected="selected" <%}%>>Documentato dal lavoratore</option>
                		<option value="C" <% if (CODMONOMOVDICH.equals("C")) {%>selected="selected" <%}%>>Dichiarato</option>              
              		</af:comboBox>					
				</td>
			</tr>            
            
            <tr>
            	<td class="etichetta" nowrap>Data di comunicazione</td>
            	<td class="campo" nowrap>
                	<af:textBox classNameBase="input" 
                				size="11" 
								onKeyUp="fieldChanged();"
								onBlur="calcGgMovComunicaz();"
								maxlength="10" 
                				type="date" 
                				validateOnPost="true" 
                				title="Data Comunicazione" 
                				required="true" 
                				name="DATCOMUNICAZ" 
                				value="<%=DATCOMUNICAZ%>"
                				callBackDateFunction="calcGgMovComunicaz();"/>
            	</td>
            </tr>
            <tr>
            	<td class="etichetta" nowrap>Data del trasferimento</td>
            	<td class="campo" nowrap>
                	<af:textBox classNameBase="input" 
                				size="11" 
								onKeyUp="fieldChanged();"
								onBlur="calcGgMovComunicaz();"
								maxlength="10" 
                				type="date" 
                				validateOnPost="true" 
                				title="Data trasferimento" 
                				required="true" 
                				name="DATTRASFERIMENTO" 
                				value="<%=DATTRASFERIMENTO%>"
                				callBackDateFunction="calcGgMovComunicaz();"/>
            	</td>            	
            </tr>
            <tr>
				<td class="etichetta" id="labelRit">Giorni prima di comunicazione</td>
				<td class="campo" id="campoRit">
					<af:textBox classNameBase="input" 
								onKeyUp="fieldChanged();" 
								type="number" 
								validateOnPost="true" 
								name="NUMGGTRAMOVCOMUNICAZIONE" 
								required="false" 
								title="Giorni di ritardo"  
								size="12" 
								maxlength="10" 
								value="<%=NUMGGTRAMOVCOMUNICAZIONE%>"/>
				</td>
			</tr>
			<tr>
				<td class="etichetta">Motivo del trasferimento</td>
				<td class="campo"> 
				
				   	<af:comboBox moduleName="M_TRComboMotivoCess" 
								selectedValue="<%=CODTIPOTRASF%>" 
								classNameBase="input" 
								title="'Motivo del trasferimento'" 
								required="true" 
								name="CODTIPOTRASF" 
								onChange="fieldChanged();getTipoTrasferimento();" 
								addBlank="true"/>
				</td>
			</tr>
			
			
			<tr id="datFineAffittoRamo" style="display:none">
				<td class="etichetta2">Data fine affitto ramo</td>
				<td class="campo">
					<af:textBox classNameBase="input" onKeyUp="fieldChanged();" 
						type="date" validateOnPost="true" name="DATFINEAFFITTORAMO" 
						title="Data fine affitto ramo"  size="12" 
						value="<%=dataFineAffittoRamo%>"/>
				</td>
			</tr>
			
			
						
	        <tr>
	        	<td>&nbsp;</td>
	        	<td>&nbsp;</td>
	        </tr>
	        <tr>
	        	<td colspan="2" align="center">
	        		<strong>Protocollazione&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</strong>
	        	</td>
	        </tr>
	        <tr>
	        	<td colspan="2" align="center">
	        	<% if(!ProtocolloDocumentoUtil.protocollazioneLocale()){ %>
					<input type="hidden" name="numAnnoProt" value="<%= Utils.notNull(numAnnoProtV) %>" >
              		<input type="hidden" name="numProt"  value="<%= Utils.notNull(numProtV) %>" >
              		<input type="hidden" name="dataProt" value="<%= datProtV %>" >
              		<input type="hidden" name="oraProt"  value="<%= oraProtV %>" >
              		<table style="width: 70%;" class="main" cellpadding="0" cellspacing="0" border="0">
					 <tr>
					  <td class="etichetta2">Anno</td>
					   <td class="campo2">
					     <af:textBox name="numAnnoProt_"
					               validateOnPost="true" 
					               title="Anno di protocollazione"
					               value=""
					               classNameBase="input"
					               size="4"
					               maxlength="4"
					               readonly="true" />
					   </td>            
					   <td class="etichetta2">Num</td>
					   <td class="campo2" nowrap>
					     <af:textBox name="numProt_"
					               title="Numero di protocollo"
					               value=""
					               classNameBase="input"
					               size="6"
					               maxlength="38"
					               readonly="true" />
					   </td>
					   <td class="etichetta2">data</td>
					   <td>
					      <af:textBox name="dataProt_" 
					                  type="date" 
					                  value="" 
					                  size="11" 
					                  maxlength="10"
					                  title="data di protocollazione"  
					                  classNameBase="input" 
					                  readonly="true" 
					                  trim ="false" 
					       />&nbsp;</td>
					   <td class="etichetta2">ora</td>
					   <td>
					      <af:textBox name="oraProt_"
					                  type="date"
					                  value=""
					                  size="6" 
					                  maxlength="5"
					                  title="ora"  
					                  classNameBase="input" 
					                  readonly="true"
					                  trim ="false"
					       />&nbsp;
					   <input name="tipoProt" type="hidden" value="<%=prAutomatica%>" />
					   </td>
					 </tr>
					</table>
              	<%}else {%>
			        <table style="width: 70%;" class="main" cellpadding="0" cellspacing="0" border="0">
					 <tr>
					  <td class="etichetta2">Anno</td>
					   <td class="campo2">
					     <af:textBox name="numAnnoProt"
					               validateOnPost="true" 
					               title="Anno di protocollazione"
					               value="<%= Utils.notNull(numAnnoProtV) %>"
					               classNameBase="input"
					               size="4"
					               maxlength="4"
					               required="true"
					               readonly="true" />
					   </td>            
					   <td class="etichetta2">Num</td>
					   <td class="campo2" nowrap>
					     <af:textBox name="numProt"
					               title="Numero di protocollo"
					               value="<%= Utils.notNull(numProtV) %>"
					               classNameBase="input"
					               size="6"
					               maxlength="38"
					               required="true"
					               readonly="<%=String.valueOf(!numProtEditable)%>" />
					   </td>
					   <td class="etichetta2">data</td>
					   <td>
					      <af:textBox name="dataProt" 
					                  type="date" 
					                  value="<%=datProtV%>" 
					                  size="11" 
					                  maxlength="10"
					                  title="data di protocollazione"  
					                  classNameBase="input" 
					                  readonly="<%=String.valueOf(!numProtEditable)%>" 
					                  validateOnPost="true" 
					                  required="false" 
					                  trim ="false" 
					                  onKeyUp="cambiAnnoProt(this,numAnnoProt)" 
					                  onBlur="checkFormatDate(this)"
					       />&nbsp;*</td>
					   <td class="etichetta2">ora</td>
					   <td>
					      <af:textBox name="oraProt"
					                  type="date"
					                  value="<%=oraProtV%>"
					                  size="6" 
					                  maxlength="5"
					                  title="ora"  
					                  classNameBase="input" 
					                  readonly="<%=String.valueOf(!numProtEditable)%>"
					                  validateOnPost="false" 
					                  required="false" 
					                  trim ="false"
					                  onBlur="checkAndFormatTime(this)"
					       />&nbsp;*
					   <input name="tipoProt" type="hidden" value="<%=prAutomatica%>" />
					   </td>
					 </tr>
					</table>
				<%}%>
	      		</td>
	      	</tr>					
      		</table>
      		<%out.print(htmlStreamBottom);%>
			<input class="pulsanti" 
					type="submit" 
					name="trasferisci" 
					value="Trasferimenti - Trasformazioni Aziende"/>      				  
			</af:form>
		</center>
	</body>
</html>