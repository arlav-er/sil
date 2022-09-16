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
    //Oggetti per l'applicazione dello stile grafico
    String htmlStreamTop = StyleUtils.roundTopTable(false);
    String htmlStreamBottom = StyleUtils.roundBottomTable(false);
    
	//Definizione variabili
	String ragioneSocialeAzProv = "";
	String pIvaAzProv = "";
	String codFiscaleAzProv = "";
	String IndirizzoAzProv = "";
	String descrTipoAzProv = "";
	String ragioneSocialeAzDest = "";
	String pIvaAzDest = "";
	String codFiscaleAzDest = "";
	String IndirizzoAzDest = "";
	String descrTipoAzDest = "";
  
	//Recupero eventuali dati delle aziende già impostate  
	SourceBean rowAzProv = (SourceBean) serviceResponse.getAttribute("M_TRGetAzProvenienza.ROWS.ROW");	
	SourceBean rowUazProv = (SourceBean) serviceResponse.getAttribute("M_TRGetUazProvenienza.ROWS.ROW");
	SourceBean rowAzDest = (SourceBean) serviceResponse.getAttribute("M_TRGetAzDestinazione.ROWS.ROW");	
	SourceBean rowUazDest = (SourceBean) serviceResponse.getAttribute("M_TRGetUazDestinazione.ROWS.ROW");	
	if (rowAzProv != null) {
		ragioneSocialeAzProv = StringUtils.getAttributeStrNotNull(rowAzProv, "strRagioneSociale");
		pIvaAzProv = StringUtils.getAttributeStrNotNull(rowAzProv, "strPartitaIva");
		codFiscaleAzProv = StringUtils.getAttributeStrNotNull(rowAzProv, "strCodiceFiscale");
		descrTipoAzProv = StringUtils.getAttributeStrNotNull(rowUazProv, "descTipoAzienda");
	}
	if (rowUazProv != null) {
		IndirizzoAzProv = StringUtils.getAttributeStrNotNull(rowUazProv, "strIndirizzo") + 
			" (" + StringUtils.getAttributeStrNotNull(rowUazProv, "strDenominazione") + ")";
  	}
  	
	if (rowAzDest != null) {
		ragioneSocialeAzDest = StringUtils.getAttributeStrNotNull(rowAzDest, "strRagioneSociale");
		pIvaAzDest = StringUtils.getAttributeStrNotNull(rowAzDest, "strPartitaIva");
		codFiscaleAzDest = StringUtils.getAttributeStrNotNull(rowAzDest, "strCodiceFiscale");
		descrTipoAzDest = StringUtils.getAttributeStrNotNull(rowUazDest, "descTipoAzienda");
	}
	if (rowUazDest != null) {
		IndirizzoAzDest = StringUtils.getAttributeStrNotNull(rowUazDest, "strIndirizzo") + 
			" (" + StringUtils.getAttributeStrNotNull(rowUazDest, "strDenominazione") + ")";
  	}  

%>
<%@ include file="GestioneCacheTrasfRamoAzienda.inc" %>

<html>
	<head>
	  	<link rel="stylesheet" href="../../css/stili.css" type="text/css">
	  	    <af:linkScript path="../../js/"/>
	  	<title>Trasferimento Ramo Aziendale (Scelta Aziende)</title>
	
	  	<script language="Javascript">
		<% 
			//Genera il Javascript che si occuperà di inserire i links nel footer
			attributi.showHyperLinks(out, requestContainer,responseContainer,"");
		%>
	  	</SCRIPT>
	
		<%@ include file="../MovimentiRicercaSoggetto.inc" %>
		<%@ include file="../MovimentiSezioniATendina.inc" %>
		<%@ include file="../Common_Function_Mov.inc" %>
	
		<!--Funzioni per l'aggiornamento della form -->
		<script type="text/javascript">
		<!--
		    function aggiornaAziendaProvenienza(){
		    	//Controllo di non aver scelto la stessa azienda della destinazione
		        if (!(document.Frm1.PRGAZIENDADESTINAZIONE.value == opened.dati.prgAzienda &&
		        	 document.Frm1.PRGUNITADESTINAZIONE.value == opened.dati.prgUnita)) {
			        
			        document.Frm1.ragioneSocialeAzProv.value = opened.dati.ragioneSociale;
			        document.Frm1.pIvaAzProv.value = opened.dati.partitaIva;
			        document.Frm1.codFiscaleAzProv.value = opened.dati.codiceFiscaleAzienda;
			        document.Frm1.IndirizzoAzProv.value = opened.dati.strIndirizzoAzienda + 
															" (" + opened.dati.comuneAzienda + ")";
			        document.Frm1.PRGAZIENDAPROVENIENZA.value = opened.dati.prgAzienda;
			        document.Frm1.PRGUNITAPROVENIENZA.value = opened.dati.prgUnita;
			        document.Frm1.descrTipoAzProv.value = opened.dati.descrTipoAz;
			        opened.close();
			        //Se la sezione è chiusa la apro
			        if (document.getElementById("sezioneAziendaProv").style.display == "none") {
			        	cambia(document.getElementById('tendinaAziendaProvenienza'),
			        			document.getElementById('sezioneAziendaProv'));	
			        }
			    } else {
			    	alert("Impossibile scegliere come provenienza la stessa sede della destinazione!");
			    	opened.close();
			    }
		    }
		    
		    function aggiornaAziendaDestinazione(){
		    	//Controllo di non aver scelto la stessa azienda della provenienza
		        if (!(document.Frm1.PRGAZIENDAPROVENIENZA.value == opened.dati.prgAzienda &&
		        	 document.Frm1.PRGUNITAPROVENIENZA.value == opened.dati.prgUnita)) {	
		        	 	    
			        document.Frm1.ragioneSocialeAzDest.value = opened.dati.ragioneSociale;
			        document.Frm1.pIvaAzDest.value = opened.dati.partitaIva;
			        document.Frm1.codFiscaleAzDest.value = opened.dati.codiceFiscaleAzienda;
			        document.Frm1.IndirizzoAzDest.value = opened.dati.strIndirizzoAzienda + 
			        										" (" + opened.dati.comuneAzienda + ")";
			        document.Frm1.PRGAZIENDADESTINAZIONE.value = opened.dati.prgAzienda;
			        document.Frm1.PRGUNITADESTINAZIONE.value = opened.dati.prgUnita;
			        document.Frm1.descrTipoAzDest.value = opened.dati.descrTipoAz;
			        opened.close();
			        //Se la sezione è chiusa la apro
			        if (document.getElementById("sezioneAziendaDest").style.display == "none") {
			        	cambia(document.getElementById('tendinaAziendaDestinazione'), 
			        			document.getElementById('sezioneAziendaDest'));	
			        }
			    } else {
			    	alert("Impossibile scegliere come destinazione la stessa sede della provenienza!");
			    	opened.close();
			    }
		    }
		    
		    //Navigazione tra le linguette
		    function avanti() {
		    	if (controllaFunzTL() && riportaControlloUtente( controllaScelte() ) ) {
		    		doFormSubmit(document.Frm1);
		    	}
		    }
		    
		    //Controlla che siano state scelte entrambe le aziende
		    function controllaScelte() {
		    	if (document.Frm1.PRGUNITAPROVENIENZA.value == "" ||
		    		document.Frm1.PRGUNITADESTINAZIONE.value == "") {
		    			alert("Occorre prima selezionare entrambe le aziende coinvolte");
		    			return false;
		    	} else return true;
		    }
		    
		    //Funzione che consente la navigazione tra le linguette
		    function goToLinguetta(page, checkForm) {
				// Se la pagina è già in submit, ignoro questo nuovo invio!
				if (isInSubmit()) return;

		    	document.Frm1.PAGE.value = page;
		    	if (checkForm) {
		    		if (controllaFunzTL() && riportaControlloUtente( controllaScelte() ) ) {
		    			doFormSubmit(document.Frm1);
		    		}
		    	} else doFormSubmit(document.Frm1);
		    }
		-->
	 </script>
	</head>    
	<body class="gestione" onload="rinfresca();">
		<div class='menu'>
			<a href='#' class='sel1'><span class='tr_round1'>1&nbsp;Scelta&nbsp;Aziende</span></a>
			<a href='#' onclick="goToLinguetta('TrasfRamoSceltaLavPage', true)" class="bordato1"><span class='tr_round11'>2&nbsp;Scelta&nbsp;Movimenti</span></a>
			<a href='#' onclick="goToLinguetta('TrasfRamoInfoPage', true)" class="bordato1"><span class='tr_round11'>3&nbsp;Ulteriori&nbsp;Informazioni</span></a>
		</div>

		<center>
			<af:form name="Frm1" method="POST" action="AdapterHTTP">
        	<input type="hidden" name="PAGE" value="TrasfRamoSceltaLavPage"/>
        	<input type="hidden" name="cdnFunzione" value="<%=_funzione%>"/>
        				
			<%@ include file="CampiComuniTrasfRamoAzienda.inc" %>						
      		
      		<%out.print(htmlStreamTop);%>
      		<table class="main" border="0">
	      		<tr>
	      			<td>
            			<div class="sezione2">
              				<img id='tendinaAziendaProvenienza' 
              					alt="Chiudi" 
              					src="../../img/chiuso.gif" 
              					onclick="cambia(this,document.getElementById('sezioneAziendaProv'));"/>&nbsp;&nbsp;&nbsp;Azienda Provenienza
            				&nbsp;&nbsp;
              				<a href="#" 
              					onClick="javascript:apriSelezionaSoggetto('Aziende', 'aggiornaAziendaProvenienza','','','');">
              					<img src="../../img/binocolo.gif" alt="Cerca">
              				</a>
            			</div>	      				
	      			</td>
	      		</tr>
	      		<tr>
	      			<td>
              			<div id="sezioneAziendaProv" style="display: none;">
                			<table class="main" width="100%" border="0">
                    			<tr>
                      				<td class="etichetta">Codice Fiscale</td>
                      				<td class="campo">
                        				<af:textBox classNameBase="input" 
                        							type="text" 
                        							name="codFiscaleAzProv" 
                        							readonly="true" 
                        							value="<%=codFiscaleAzProv%>" 
                        							size="30" 
                        							maxlength="16"/>                       
                      				</td>
                    			</tr>
                    			<tr valign="top">
                      				<td class="etichetta">Partita IVA</td>
                      				<td class="campo">
			                        <af:textBox classNameBase="input" 
			                        			type="text" 
			                        			name="pIvaAzProv" 
			                        			readonly="true" 
			                        			value="<%=pIvaAzProv%>" 
			                        			size="30" 
			                        			maxlength="11"/>
			                      	</td>
			                    </tr>
			                    <tr>
			                      	<td class="etichetta">Ragione Sociale</td>
			                      	<td class="campo">
			                        	<af:textBox classNameBase="input" 
			                        				type="text" 
			                        				name="ragioneSocialeAzProv" 
			                        				readonly="true" 
			                        				value="<%=ragioneSocialeAzProv%>" 
			                        				size="60" 
			                        				maxlength="100"/>
			                      	</td>
			                    </tr>
			                    <tr>
			                      	<td class="etichetta">Indirizzo (Comune)</td>
			                      	<td class="campo">
			                        	<af:textBox classNameBase="input" 
			                        				type="text" 
			                        				name="IndirizzoAzProv" 
			                        				readonly="true" 
			                        				value="<%=IndirizzoAzProv%>" 
			                        				size="60" 
			                        				maxlength="100"/>
			                      	</td>
			                    </tr>
			                    <tr>
			                      	<td class="etichetta">Tipo Azienda</td>
			                      	<td class="campo">
			                        	<af:textBox classNameBase="input" 
			                        				type="text" 
			                        				name="descrTipoAzProv" 
			                        				readonly="true" 
			                        				value="<%=descrTipoAzProv%>" 
			                        				size="30" 
			                        				maxlength="30"/>
			                      	</td>
			                    </tr>
							</table>
			             </div>	      				
	      			</td>
	      		</tr>
	      		<tr>
	      			<td>
            			<div class="sezione2">
              				<img id='tendinaAziendaDestinazione' 
              					alt="Chiudi" 
              					src="../../img/chiuso.gif" 
              					onclick="cambia(this,document.getElementById('sezioneAziendaDest'));"/>&nbsp;&nbsp;&nbsp;Azienda Destinazione
            				&nbsp;&nbsp;
              				<a href="#" 
              					onClick="javascript:apriSelezionaSoggetto('Aziende', 'aggiornaAziendaDestinazione','','','');">
              					<img src="../../img/binocolo.gif" alt="Cerca">
              				</a>
            			</div>	      				
	      			</td>
	      		</tr>
	      		<tr>
	      			<td>
              			<div id="sezioneAziendaDest" style="display: none;">
                			<table class="main" width="100%" border="0">
                    			<tr>
                      				<td class="etichetta">Codice Fiscale</td>
                      				<td class="campo">
                        				<af:textBox classNameBase="input" 
                        							type="text" 
                        							name="codFiscaleAzDest" 
                        							readonly="true" 
                        							value="<%=codFiscaleAzDest%>" 
                        							size="30" 
                        							maxlength="16"/>                       
                      				</td>
                    			</tr>
                    			<tr valign="top">
                      				<td class="etichetta">Partita IVA</td>
                      				<td class="campo">
			                        <af:textBox classNameBase="input" 
			                        			type="text" 
			                        			name="pIvaAzDest" 
			                        			readonly="true" 
			                        			value="<%=pIvaAzDest%>" 
			                        			size="30" 
			                        			maxlength="11"/>
			                      	</td>
			                    </tr>
			                    <tr>
			                      	<td class="etichetta">Ragione Sociale</td>
			                      	<td class="campo">
			                        	<af:textBox classNameBase="input" 
			                        				type="text" 
			                        				name="ragioneSocialeAzDest" 
			                        				readonly="true" 
			                        				value="<%=ragioneSocialeAzDest%>" 
			                        				size="60" 
			                        				maxlength="100"/>
			                      	</td>
			                    </tr>
			                    <tr>
			                      	<td class="etichetta">Indirizzo (Comune)</td>
			                      	<td class="campo">
			                        	<af:textBox classNameBase="input" 
			                        				type="text" 
			                        				name="IndirizzoAzDest" 
			                        				readonly="true" 
			                        				value="<%=IndirizzoAzDest%>" 
			                        				size="60" 
			                        				maxlength="100"/>
			                      	</td>
			                    </tr>
			                    <tr>
			                      	<td class="etichetta">Tipo Azienda</td>
			                      	<td class="campo">
			                        	<af:textBox classNameBase="input" 
			                        				type="text" 
			                        				name="descrTipoAzDest" 
			                        				readonly="true" 
			                        				value="<%=descrTipoAzDest%>" 
			                        				size="30" 
			                        				maxlength="30"/>
			                      	</td>
			                    </tr>
							</table>
			             </div>	      				
	      			</td>
	      		</tr>
      		</table>
      		<%out.print(htmlStreamBottom);%>		  
			</af:form>
		</center>
		<script language="javascript">
			if (document.Frm1.PRGUNITAPROVENIENZA.value != "" && 
				document.Frm1.PRGAZIENDAPROVENIENZA.value != "") {
				cambia(document.getElementById('tendinaAziendaProvenienza'), 
						document.getElementById('sezioneAziendaProv'));
			}
			if (document.Frm1.PRGUNITADESTINAZIONE.value != "" &&
				document.Frm1.PRGAZIENDADESTINAZIONE.value != "") {
				cambia(document.getElementById('tendinaAziendaDestinazione'), 
						document.getElementById('sezioneAziendaDest'));
			}			
		</script>	
	</body>
</html>