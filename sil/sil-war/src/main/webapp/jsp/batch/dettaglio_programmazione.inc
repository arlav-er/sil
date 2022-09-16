<script type="text/javascript">
	// Per rilevare la modifica dei dati da parte dell'utente
  	var flagChanged = false;
  
  	function fieldChanged() {
    	<%if (canOperation) {out.print("flagChanged = true;");}%>
  	}

  	function checkCampiObbligatori() {
  		var objDataInizioVal;
  		var objDataFineVal;
  		var tipoBatchValue = document.frmProgrammazione.CODTIPOBATCH.value;
  		if (tipoBatchValue == "APPUNSMS") {
  			if (document.frmProgrammazione.codMotContattoApp.value == "") {
  				alert("Il campo Motivo contatto è obbligatorio");
  				return false;
  			}
  			if (document.frmProgrammazione.ggContattoApp.value == "") {
  				alert("Il campo Giorni ultimo contatto è obbligatorio");
  				return false;
  			}
  			if (parseInt(document.frmProgrammazione.ggContattoApp.value,10) <= 0) {
  				alert("Il campo Giorni ultimo contatto deve essere maggiore di zero");
  				return false;
  			}
  			if (document.frmProgrammazione.codServizioApp.value == "") {
  				alert("Il campo Servizio appuntamento è obbligatorio");
  				return false;
  			}
  			if (document.frmProgrammazione.codStatoApp.value == "") {
  				alert("Il campo Stato appuntamento è obbligatorio");
  				return false;
  			}
  			if (document.frmProgrammazione.ggRangeApp.value == "") {
  				alert("Il campo Giorni range appuntamento è obbligatorio");
  				return false;
  			}
  			if (parseInt(document.frmProgrammazione.ggRangeApp.value,10) <= 0) {
  				alert("Il campo Giorni range appuntamento deve essere maggiore di zero");
  				return false;
  			}
  			if (document.frmProgrammazione.ggProgrammazioneBApp.value == "") {
  				alert("Il campo Giorni schedulazione programmazione è obbligatorio");
  				return false;
  			}
  			if (parseInt(document.frmProgrammazione.ggProgrammazioneBApp.value,10) <= 0) {
  				alert("Il campo Giorni schedulazione programmazione deve essere maggiore di zero");
  				return false;
  			}
  			<%if (!isNuovaProgrammazione) {%>
  				if (document.frmProgrammazione.flgAttivoApp.value == "") {
  					alert("Il campo Attivo è obbligatorio");
  					return false;
  				}
			<%}%>
			if (document.frmProgrammazione.smsApp.value == "") {
  				alert("Il campo Tipo SMS è obbligatorio");
  				return false;
  			}
  			if (document.frmProgrammazione.evidenzaApp.value == "") {
  				alert("Il campo Tipo evidenza è obbligatorio");
  				return false;
  			}
  			objDataInizioVal = document.frmProgrammazione.DatInizioValApp;
  			objDataFineVal = document.frmProgrammazione.DatFineValApp;
  	    }
  	    else {
  	    	if (tipoBatchValue == "AZIONSMS") {
  	    		if (document.frmProgrammazione.codMotContattoAzione.value == "") {
  					alert("Il campo Motivo contatto è obbligatorio");
  					return false;
  				}
  				if (document.frmProgrammazione.ggContattoAzione.value == "") {
  					alert("Il campo Giorni ultimo contatto è obbligatorio");
  					return false;
  				}
  				if (parseInt(document.frmProgrammazione.ggContattoAzione.value,10) <= 0) {
  					alert("Il campo Giorni ultimo contatto deve essere maggiore di zero");
  					return false;
  				}
  				if (document.frmProgrammazione.codAzione.value == "") {
  					alert("Il campo Azione è obbligatorio");
  					return false;
  				}
  				if (document.frmProgrammazione.codEsitoAzione.value == "") {
  					alert("Il campo Esito azione è obbligatorio");
  					return false;
  				}
  				if (document.frmProgrammazione.ggRangeAzione.value == "") {
  					alert("Il campo Giorni range data stimata è obbligatorio");
  					return false;
  				}
  				if (parseInt(document.frmProgrammazione.ggRangeAzione.value,10) <= 0) {
  					alert("Il campo Giorni range data stimata deve essere maggiore di zero");
  					return false;
  				}
  				if (document.frmProgrammazione.ggProgrammazioneBAzione.value == "") {
  					alert("Il campo Giorni schedulazione programmazione è obbligatorio");
  					return false;
  				}
  				if (parseInt(document.frmProgrammazione.ggProgrammazioneBAzione.value,10) <= 0) {
  					alert("Il campo Giorni schedulazione programmazione deve essere maggiore di zero");
  					return false;
  				}
  	    		<%if (!isNuovaProgrammazione) {%>
  					if (document.frmProgrammazione.flgAttivoAzione.value == "") {
  						alert("Il campo Attivo è obbligatorio");
  						return false;
  					}
				<%}%>
				if (document.frmProgrammazione.smsAzione.value == "") {
  					alert("Il campo Tipo SMS è obbligatorio");
  					return false;
  				}
  				if (document.frmProgrammazione.evidenzaAzione.value == "") {
  					alert("Il campo Tipo evidenza è obbligatorio");
  					return false;
  				}
  	    		objDataInizioVal = document.frmProgrammazione.DatInizioValAzione;
  	  			objDataFineVal = document.frmProgrammazione.DatFineValAzione;	
  	    	}
  	    	else {
  	  	    	if (tipoBatchValue == "DIDSMS") {
  	  	    		if (document.frmProgrammazione.codMotContattoDid.value == "") {
  						alert("Il campo Motivo contatto è obbligatorio");
  						return false;
  					}
  					if (document.frmProgrammazione.ggContattoDid.value == "") {
  						alert("Il campo Giorni ultimo contatto è obbligatorio");
  						return false;
  					}
  					if (parseInt(document.frmProgrammazione.ggContattoDid.value,10) <= 0) {
  						alert("Il campo Giorni ultimo contatto deve essere maggiore di zero");
  						return false;
  					}
  	  	    		if (document.frmProgrammazione.ggProgrammazioneBDid.value == "") {
  						alert("Il campo Giorni schedulazione programmazione è obbligatorio");
  						return false;
  					}
  					if (parseInt(document.frmProgrammazione.ggProgrammazioneBDid.value,10) <= 0) {
  						alert("Il campo Giorni schedulazione programmazione deve essere maggiore di zero");
  						return false;
  					}
  	    			<%if (!isNuovaProgrammazione) {%>
  						if (document.frmProgrammazione.flgAttivoDid.value == "") {
  							alert("Il campo Attivo è obbligatorio");
  							return false;
  						}
					<%}%>
					if (document.frmProgrammazione.smsDid.value == "") {
  						alert("Il campo Tipo SMS è obbligatorio");
  						return false;
  					}
  					if (document.frmProgrammazione.evidenzaDid.value == "") {
  						alert("Il campo Tipo evidenza è obbligatorio");
  						return false;
  					}
	  	  	    	objDataInizioVal = document.frmProgrammazione.DatInizioValDid;
	  	  			objDataFineVal = document.frmProgrammazione.DatFineValDid;	
  	  	    	}
  	  	    	else {
  	  	    		if (tipoBatchValue == "PERDISMS") {
	  	  	    		if (document.frmProgrammazione.codMotContattoPerdDisocc.value == "") {
	  						alert("Il campo Motivo contatto è obbligatorio");
	  						return false;
	  					}
	  	  	    		if (document.frmProgrammazione.flgIscrCM.value == "") {
							alert("Il campo Iscritti CM è obbligatorio");
							return false;
						}
	  	  	    		if (document.frmProgrammazione.ggRangePerdDisocc.value == "") {
							alert("Il campo Giorni rilevazione è obbligatorio");
							return false;
						}
	  	  	    		if (parseInt(document.frmProgrammazione.ggRangePerdDisocc.value,10) <= 0) {
							alert("Il campo Giorni rilevazione deve essere maggiore di zero");
							return false;
						}
	  	  	    		if (document.frmProgrammazione.ggRangeFineDid.value != "") {
		  	  	    		if (parseInt(document.frmProgrammazione.ggRangeFineDid.value,10) <= 0) {
								alert("Il campo Giorni data fine DID deve essere maggiore di zero");
								return false;
							}
	  	  	    		}
	  	  	    		if (document.frmProgrammazione.ggProgrammazioneBPerdDisocc.value == "") {
  							alert("Il campo Giorni schedulazione programmazione è obbligatorio");
  							return false;
  						}
	  	  	    		if (parseInt(document.frmProgrammazione.ggProgrammazioneBPerdDisocc.value,10) <= 0) {
  							alert("Il campo Giorni schedulazione programmazione deve essere maggiore di zero");
  							return false;
  						}

		  	  	    	<%if (!isNuovaProgrammazione) {%>
							if (document.frmProgrammazione.flgAttivoPerdDisocc.value == "") {
								alert("Il campo Attivo è obbligatorio");
								return false;
							}
						<%}%>
						if (document.frmProgrammazione.smsPerdDisocc.value == "") {
							alert("Il campo Tipo SMS è obbligatorio");
							return false;
						}
						if (document.frmProgrammazione.evidenzaPerdDisocc.value == "") {
							alert("Il campo Tipo evidenza è obbligatorio");
							return false;
						}
		  	  	    	objDataInizioVal = document.frmProgrammazione.DatInizioValPerdDisocc;
		  	  			objDataFineVal = document.frmProgrammazione.DatFineValPerdDisocc;
  	  	    		}
  	  	    	}   		
  	    	}
  	    }

  	    if (objDataInizioVal.value == "") {
  	    	alert("La data di inizio validità è obbligatoria");
  	    	return false;
  	    }  
  	  	if (objDataFineVal.value == "") {
	    	alert("La data di fine validità è obbligatoria");
	    	return false;
	    }

  		//costruisco la data di inizio
  	  	d1giorno = parseInt(objDataInizioVal.value.substr(0,2),10);
  	  	d1mese = parseInt(objDataInizioVal.value.substr(3, 2),10)-1;
  	  	d1anno = parseInt(objDataInizioVal.value.substr(6,4),10);
  	  	data1 = new Date(d1anno, d1mese, d1giorno);
  	
  	  	//costruisce la data di fine
  	  	d2giorno = parseInt(objDataFineVal.value.substr(0,2),10);
  	  	d2mese = parseInt(objDataFineVal.value.substr(3,2),10)-1;
  	  	d2anno = parseInt(objDataFineVal.value.substr(6,4),10);
  	  	data2 = new Date(d2anno, d2mese, d2giorno);
  	  	
  	  	if (data2 < data1) {
  	    	alert("La data di fine validità deve essere successiva o uguale alla data di inizio validità");
  	    	return false;
  	   	}
  	   	
    	return true;
    }

  	function annulla() {
  		// Se la pagina è già in submit, ignoro questo nuovo invio!
  		if (isInSubmit()) return;

  	    if (flagChanged) {
  	    	if (!confirm("I dati sono cambiati.\nProcedere lo stesso ?")) { 
  	  	    	return; 
  	  	    }
  	       	else {
            	document.frmProgrammazione.OPERAZIONEPROGR.value = "LISTA";
                doFormSubmit(document.frmProgrammazione);
            }
  	    }
  	  	else {
      		document.frmProgrammazione.OPERAZIONEPROGR.value = "LISTA";
          	doFormSubmit(document.frmProgrammazione);
      	}
  	}

  	function gestisciCampiBatch() {
  		var sezBatchApp = document.getElementById("sezBatchApp");
  		var sezBatchAzioni = document.getElementById("sezBatchAzioni");
  		var sezBatchDid = document.getElementById("sezBatchDid");
  		var sezBatchPerdDisocc = document.getElementById("sezBatchPerdDisocc");
  		var sezInfoPerdDisocc = document.getElementById("sezInfoPerdDisocc");
  		
  		var tipoBatchValue = document.frmProgrammazione.CODTIPOBATCH.value;
  		
  		if (tipoBatchValue == "APPUNSMS") {
  			sezBatchAzioni.style.display = "none";
  			sezBatchDid.style.display = "none";
  			sezBatchPerdDisocc.style.display = "none";
  			sezInfoPerdDisocc.style.display = "none";
  			sezBatchApp.style.display = "";
  	    }
  	    else {
  	    	if (tipoBatchValue == "AZIONSMS") {
  	    		sezBatchAzioni.style.display = "";
  	  			sezBatchDid.style.display = "none";
  	  			sezBatchApp.style.display = "none";
  	  			sezBatchPerdDisocc.style.display = "none";
  	  			sezInfoPerdDisocc.style.display = "none";
  	    	}
  	    	else {
  	  	    	if (tipoBatchValue == "DIDSMS") {
  	  	    		sezBatchAzioni.style.display = "none";
  	  	  			sezBatchDid.style.display = "";
  	  	  			sezBatchApp.style.display = "none";
  	  	  			sezBatchPerdDisocc.style.display = "none";
  	  	  			sezInfoPerdDisocc.style.display = "none";
  	  	    	}
  	  	    	else {
  	  	    		if (tipoBatchValue == "PERDISMS") {
	  	  	    		sezBatchAzioni.style.display = "none";
		  	  			sezBatchDid.style.display = "none";
		  	  			sezBatchApp.style.display = "none";
		  	  			sezBatchPerdDisocc.style.display = "";
		  	  			sezInfoPerdDisocc.style.display = "";
  	  	    		}
  	  	    	}
  	    	}
  	    }
  	} 
  	
</script>

<af:form name="frmProgrammazione" action="AdapterHTTP" method="POST" onSubmit="checkCampiObbligatori()">
<input type="hidden" name="PAGE" value="ListaProgrammazioniPage">
<%if (!isNuovaProgrammazione) {%>
	<input type="hidden" name="OPERAZIONEPROGR" value="AGGIORNA"/>
	<input type="hidden" name="PRGPROGRAMMABATCH" value="<%=prgProgrammazione.toString()%>">
	<input type="hidden" name="NUMKLOPROGRAMMABATCH" value="<%=numKloProgrammazione.toString()%>">
<%} else {%>
	<input type="hidden" name="OPERAZIONEPROGR" value="INSERISCI"/>
<%}%>
<input type="hidden" name="CDNFUNZIONE" value="<%=_funzione%>">

<%out.print(htmlStreamTop);%>
<p align="center">

<table class="main">
	<tr>
	<td align="center">
	<table style="border-collapse:collapse">
		<tr>
		<td class="etichetta">Tipo Batch</td>
		<td class="campo">
		<af:comboBox name="CODTIPOBATCH" size="1" title="Tipo batch"
		   multiple="false" required="true" focusOn="false" moduleName="M_GetTipoBatchProgrammazione"
		   selectedValue="<%=codTipoBatch%>" addBlank="true" blankValue=""
		   classNameBase="input" disabled="<%= String.valueOf(noEditTipoBatch) %>" onChange="fieldChanged();gestisciCampiBatch();"/>
		</td>
		</tr>
	</table>
	</td>
	</tr>
	
	<tr id="sezBatchApp" style="<%=displayApp%>">
	<td align="center">
		<table style="border-collapse:collapse">
		<tr>
		<td class="etichetta" nowrap>Motivo contatto</td>
		<td class="campo">
			<af:comboBox moduleName="M_GetMotivoContattoAG" selectedValue="<%=codMotivoContatto%>" classNameBase="input" title="Motivo contatto" 
		 			 	name="codMotContattoApp" onChange="fieldChanged();" addBlank="true" blankValue="" disabled="<%=String.valueOf(!canOperation)%>"/>&nbsp;*
		</td>
		</tr>
		<tr>
		<td class="etichetta" nowrap>Giorni ultimo contatto</td>
		<td class="campo">
			<af:textBox classNameBase="input" type="integer" name="ggContattoApp" value="<%=strGGUltimoContatto%>" validateOnPost="true" title="Giorni ultimo contatto"
            	readonly="<%=String.valueOf(!canOperation)%>" onKeyUp="fieldChanged();" maxlength="3" size="4"/>&nbsp;*
		</td>
		</tr>
		<tr>
		<td class="etichetta" nowrap>Servizio appuntamento</td>
		<td class="campo">
			<af:comboBox moduleName="M_GetServizioAppuntamentoAG" selectedValue="<%=codServizio%>" classNameBase="input" title="Servizio appuntamento" 
		 			 	name="codServizioApp" onChange="fieldChanged();" addBlank="true" blankValue="" disabled="<%=String.valueOf(!canOperation)%>"/>&nbsp;*
		</td>
		</tr>
		<tr>
		<td class="etichetta" nowrap>Stato appuntamento</td>
		<td class="campo">
			<af:comboBox moduleName="M_GetStatoAppuntamentoAG" selectedValue="<%=codStatoAppuntamento%>" classNameBase="input" title="Stato appuntamento" 
		 			 	name="codStatoApp" onChange="fieldChanged();" addBlank="true" blankValue="" disabled="<%=String.valueOf(!canOperation)%>"/>&nbsp;*
		</td>
		</tr>
		<tr>
		<td class="etichetta" nowrap>Giorni range appuntamento</td>
		<td class="campo">
			<af:textBox classNameBase="input" type="integer" name="ggRangeApp" value="<%=strGGRangeBatch%>" validateOnPost="true" title="Giorni range appuntamento"
            	readonly="<%=String.valueOf(!canOperation)%>" onKeyUp="fieldChanged();" maxlength="3" size="4"/>&nbsp;*
		</td>
		</tr>
		<tr>
		<td class="etichetta" nowrap>CPI di riferimento</td>
		<td class="campo">
			<af:comboBox name="codCpiApp"
	        	         title="Centro per l'Impiego"
	            	     multiple="false"
	              	     required="false"
	                 	 focusOn="false"
	                 	 moduleName="M_GetCpiPoloProvincialeAll"
	                 	 addBlank="true"
	                 	 blankValue=""
	                 	 selectedValue="<%=codCpiRiferimento%>"
	                 	 onChange="fieldChanged();"
						 disabled="<%=String.valueOf(!canOperation)%>"/>
	  	</td>
		</tr>
		<tr>
		<td class="etichetta" nowrap>Giorni schedulazione programmazione</td>
		<td class="campo">
			<af:textBox classNameBase="input" type="integer" name="ggProgrammazioneBApp" value="<%=strGGSchedulazione%>" validateOnPost="true" title="Giorni schedulazione programmazione"
            	readonly="<%=String.valueOf(!canOperation)%>" onKeyUp="fieldChanged();" maxlength="3" size="4"/>&nbsp;*
		</td>
		</tr>
		<%if (!isNuovaProgrammazione) {%>
			<tr>
	        <td class="etichetta" nowrap>Attivo</td>
	        <td class="campo">
			<af:comboBox classNameBase="input" name="flgAttivoApp" onChange="fieldChanged();">
				<option value="S" <%if(flgAttivo.equalsIgnoreCase("S")) {%>SELECTED<%}%>>S&igrave;</option>
				<option value="N" <%if(flgAttivo.equalsIgnoreCase("N")) {%>SELECTED<%}%>>No</option>              
			</af:comboBox>&nbsp;*
			</td>
			</tr>
		<%} else {%>
			<tr>
	        <td class="etichetta" nowrap>Attivo</td>
	        <td class="campo">
	        <af:comboBox classNameBase="input" name="flgAttivoApp_read" disabled="true">
				<option value="N" selected="selected">No</option>              
			</af:comboBox>&nbsp;*
			</td>
			</tr>
		<%}%>
		<tr>
       	<td class="etichetta" nowrap>Tipo SMS</td>
        <td class="campo">
        	<af:comboBox selectedValue="<%=strTipoSms%>" classNameBase="input" title="Tipo SMS" 
		 		name="smsApp" onChange="fieldChanged();" disabled="<%=String.valueOf(!canOperation)%>"/>&nbsp;*     
        </td>
        </tr>
		<tr>
       	<td class="etichetta" nowrap>Tipo evidenza</td>
        <td class="campo">
        	<af:comboBox selectedValue="<%=strPrgTipoEvidenza%>" classNameBase="input" title="Tipo evidenza" 
		 		name="evidenzaApp" onChange="fieldChanged();" disabled="<%=String.valueOf(!canOperation)%>"/>&nbsp;*     
        </td>
        </tr>
		<tr>
		<td class="etichetta" nowrap>Data Inizio Validità</td>
		<td class="campo">
		<af:textBox title="Data inizio validità" type="date" 
            name="DatInizioValApp" value="<%=dataInizioVal%>" size="11" 
            maxlength="10" 
            validateOnPost="true"
            onKeyUp="fieldChanged();"
            classNameBase="input"
            readonly="<%=String.valueOf(!canOperation)%>"/>&nbsp;*
		</td>
		</tr>
		<tr>
		<td class="etichetta">Data Fine Validità</td>
		<td class="campo">
		<af:textBox title="Data fine validità" type="date" 
            name="DatFineValApp" value="<%=dataFineVal%>" size="11" 
            maxlength="10" 
            validateOnPost="true"
            onKeyUp="fieldChanged();"
            classNameBase="input"
            readonly="<%=String.valueOf(!canOperation)%>"/>&nbsp;*
		</td>
		</tr>
		<%if (!isNuovaProgrammazione) {%>
			<tr>
			<td class="etichetta">Data ultima elaborazione</td>
			<td class="campo">
			<af:textBox title="Data ultima elaborazione" type="date" 
	           	name="datUltimaSchedulazioneApp" value="<%=datUltimaElab%>" size="11" maxlength="10" 
	           	classNameBase="input" readonly="true"/>
			</td>
			</tr>
			<%if (!txtNoteElaborazione.equals("")) {%>
				<tr>
				<td class="etichetta">Note ultima elaborazione</td>
				<td class="campo">
				<af:textArea name="noteUltimaSchedulazioneApp" cols="60" rows="3"
					value="<%=txtNoteElaborazione%>" title="Note ultima elaborazione" readonly="true" classNameBase="input"/>
				</td>
				</tr>
			<%}
		}
		if (!isNuovaProgrammazione && flgAttivo.equalsIgnoreCase("S") && prograttivavalidita.equalsIgnoreCase("true")) {%>
			<tr>
			<td class="etichetta">Data prossima elaborazione</td>
			<td class="campo">
			<af:textBox title="Data prossima elaborazione" type="date" 
            	name="datProssimaSchedulazioneApp" value="<%=dataProssimaSchedulazione%>" size="11" maxlength="10" 
            	classNameBase="input" readonly="true"/>
			</td>
			</tr>
		<%}%>
		</table>
	</td>
	</tr>
	
	<tr id="sezBatchAzioni" style="<%=displayAzione%>">
	<td align="center">
		<table style="border-collapse:collapse">
		<tr>
		<td class="etichetta" nowrap>Motivo contatto</td>
		<td class="campo">
			<af:comboBox moduleName="M_GetMotivoContattoAG" selectedValue="<%=codMotivoContatto%>" classNameBase="input" title="Motivo contatto" 
		 			 	name="codMotContattoAzione" onChange="fieldChanged();" addBlank="true" blankValue="" disabled="<%=String.valueOf(!canOperation)%>"/>&nbsp;*
		</td>
		</tr>
		<tr>
		<td class="etichetta" nowrap>Giorni ultimo contatto</td>
		<td class="campo">
			<af:textBox classNameBase="input" type="integer" name="ggContattoAzione" value="<%=strGGUltimoContatto%>" validateOnPost="true" title="Giorni ultimo contatto"
            	readonly="<%=String.valueOf(!canOperation)%>" onKeyUp="fieldChanged();" maxlength="3" size="4"/>&nbsp;*
		</td>
		</tr>
		<tr>
		<td class="etichetta" nowrap>Servizio appuntamento</td>
		<td class="campo">
			<af:comboBox moduleName="M_GetServizioAppuntamentoAG" selectedValue="<%=codServizio%>" classNameBase="input" title="Servizio appuntamento" 
		 			 	name="codServizioAzione" onChange="fieldChanged();" addBlank="true" blankValue="" disabled="<%=String.valueOf(!canOperation)%>"/>
		</td>
		</tr>
		<tr>
		<td class="etichetta" nowrap>Azione</td>
		<td class="campo">
			<af:comboBox moduleName="M_DEAZIONI" selectedValue="<%=strPrgAzione%>" classNameBase="input" title="Azione" 
		 			 	name="codAzione" onChange="fieldChanged();" addBlank="true" blankValue="" disabled="<%=String.valueOf(!canOperation)%>"/>&nbsp;*
		</td>
		</tr>
		<tr>
		<td class="etichetta" nowrap>Esito azione</td>
		<td class="campo">
			<af:comboBox moduleName="M_DEESITO" selectedValue="<%=codEsitoAzione%>" classNameBase="input" title="Esito azione" 
		 			 	name="codEsitoAzione" onChange="fieldChanged();" addBlank="true" blankValue="" disabled="<%=String.valueOf(!canOperation)%>"/>&nbsp;*
		</td>
		</tr>
		<tr>
		<td class="etichetta" nowrap>Giorni range data stimata</td>
		<td class="campo">
			<af:textBox classNameBase="input" type="integer" name="ggRangeAzione" value="<%=strGGRangeBatch%>" validateOnPost="true" title="Giorni range data stimata"
            	readonly="<%=String.valueOf(!canOperation)%>" onKeyUp="fieldChanged();" maxlength="3" size="4"/>&nbsp;*
		</td>
		</tr>
		<tr>
		<td class="etichetta" nowrap>CPI di riferimento</td>
		<td class="campo">
			<af:comboBox name="codCpiAzione"
	        	         title="Centro per l'Impiego"
	            	     multiple="false"
	              	     required="false"
	                 	 focusOn="false"
	                 	 moduleName="M_GetCpiPoloProvincialeAll"
	                 	 addBlank="true"
	                 	 blankValue=""
	                 	 selectedValue="<%=codCpiRiferimento%>"
	                 	 onChange="fieldChanged();"
						 disabled="<%=String.valueOf(!canOperation)%>"/>
	  	</td>
		</tr>
		<tr>
		<td class="etichetta" nowrap>Giorni schedulazione programmazione</td>
		<td class="campo">
			<af:textBox classNameBase="input" type="integer" name="ggProgrammazioneBAzione" value="<%=strGGSchedulazione%>" validateOnPost="true" title="Giorni schedulazione programmazione"
            	readonly="<%=String.valueOf(!canOperation)%>" onKeyUp="fieldChanged();" maxlength="3" size="4"/>&nbsp;*
		</td>
		</tr>
		<%if (!isNuovaProgrammazione) {%>
			<tr>
	        <td class="etichetta" nowrap>Attivo</td>
	        <td class="campo">
			<af:comboBox classNameBase="input" name="flgAttivoAzione" onChange="fieldChanged();">
				<option value="S" <%if(flgAttivo.equalsIgnoreCase("S")) {%>SELECTED<%}%>>S&igrave;</option>
				<option value="N" <%if(flgAttivo.equalsIgnoreCase("N")) {%>SELECTED<%}%>>No</option>              
			</af:comboBox>&nbsp;*
			</td>
			</tr>
		<%} else {%>
			<tr>
	        <td class="etichetta" nowrap>Attivo</td>
	        <td class="campo">
	        <af:comboBox classNameBase="input" name="flgAttivoAzione_read" disabled="true">
				<option value="N" selected="selected">No</option>              
			</af:comboBox>&nbsp;*
			</td>
			</tr>
		<%}%>
		<tr>
       	<td class="etichetta" nowrap>Tipo SMS</td>
        <td class="campo">
        	<af:comboBox selectedValue="<%=strTipoSms%>" classNameBase="input" title="Tipo SMS" 
		 		name="smsAzione" onChange="fieldChanged();" disabled="<%=String.valueOf(!canOperation)%>"/>&nbsp;*     
        </td>
        </tr>
		<tr>
       	<td class="etichetta" nowrap>Tipo evidenza</td>
        <td class="campo">
        	<af:comboBox selectedValue="<%=strPrgTipoEvidenza%>" classNameBase="input" title="Tipo evidenza" 
		 		name="evidenzaAzione" onChange="fieldChanged();" disabled="<%=String.valueOf(!canOperation)%>"/>&nbsp;*     
        </td>
        </tr>
		<tr>
		<td class="etichetta" nowrap>Data Inizio Validità</td>
		<td class="campo">
		<af:textBox title="Data inizio validità" type="date" 
            name="DatInizioValAzione" value="<%=dataInizioVal%>" size="11" 
            maxlength="10" 
            validateOnPost="true"
            onKeyUp="fieldChanged();"
            classNameBase="input"
            readonly="<%=String.valueOf(!canOperation)%>"/>&nbsp;*
		</td>
		</tr>
		<tr>
		<td class="etichetta">Data Fine Validità</td>
		<td class="campo">
		<af:textBox title="Data fine validità" type="date" 
            name="DatFineValAzione" value="<%=dataFineVal%>" size="11" 
            maxlength="10" 
            validateOnPost="true"
            onKeyUp="fieldChanged();"
            classNameBase="input"
            readonly="<%=String.valueOf(!canOperation)%>"/>&nbsp;*
		</td>
		</tr>
		<%if (!isNuovaProgrammazione) {%>
			<tr>
			<td class="etichetta">Data ultima elaborazione</td>
			<td class="campo">
			<af:textBox title="Data ultima elaborazione" type="date" 
	           	name="datUltimaSchedulazioneAzione" value="<%=datUltimaElab%>" size="11" maxlength="10" 
	           	classNameBase="input" readonly="true"/>
			</td>
			</tr>
			<%if (!txtNoteElaborazione.equals("")) {%>
				<tr>
				<td class="etichetta">Note ultima elaborazione</td>
				<td class="campo">
				<af:textArea name="noteUltimaSchedulazioneAzione" cols="60" rows="3"
					value="<%=txtNoteElaborazione%>" title="Note ultima elaborazione" readonly="true" classNameBase="input"/>
				</td>
				</tr>
			<%}
		}
		if (!isNuovaProgrammazione && flgAttivo.equalsIgnoreCase("S") && prograttivavalidita.equalsIgnoreCase("true")) {%>
			<tr>
			<td class="etichetta">Data prossima elaborazione</td>
			<td class="campo">
			<af:textBox title="Data prossima elaborazione" type="date" 
            	name="datProssimaSchedulazioneAzione" value="<%=dataProssimaSchedulazione%>" size="11" maxlength="10" 
            	classNameBase="input" readonly="true"/>
			</td>
			</tr>
		<%}%>
		</table>
	</td>
	</tr>
	
	<tr id="sezBatchDid" style="<%=displayDid%>">
	<td align="center">
		<table style="border-collapse:collapse">
		<tr>
		<td class="etichetta" nowrap>Motivo contatto</td>
		<td class="campo">
			<af:comboBox moduleName="M_GetMotivoContattoAG" selectedValue="<%=codMotivoContatto%>" classNameBase="input" title="Motivo contatto" 
		 			 	name="codMotContattoDid" onChange="fieldChanged();" addBlank="true" blankValue="" disabled="<%=String.valueOf(!canOperation)%>"/>&nbsp;*
		</td>
		</tr>
		<tr>
		<td class="etichetta" nowrap>Giorni ultimo contatto</td>
		<td class="campo">
			<af:textBox classNameBase="input" type="integer" name="ggContattoDid" value="<%=strGGUltimoContatto%>" validateOnPost="true" title="Giorni ultimo contatto"
            	readonly="<%=String.valueOf(!canOperation)%>" onKeyUp="fieldChanged();" maxlength="3" size="4"/>&nbsp;*
		</td>
		</tr>
		<tr>
		<tr>
		<td class="etichetta" nowrap>CPI di riferimento</td>
		<td class="campo">
			<af:comboBox name="codCpiDid"
	        	         title="Centro per l'Impiego"
	            	     multiple="false"
	              	     required="false"
	                 	 focusOn="false"
	                 	 moduleName="M_GetCpiPoloProvincialeAll"
	                 	 addBlank="true"
	                 	 blankValue=""
	                 	 selectedValue="<%=codCpiRiferimento%>"
	                 	 onChange="fieldChanged();"
						 disabled="<%=String.valueOf(!canOperation)%>"/>
	  	</td>
		</tr>
		<td class="etichetta" nowrap>Giorni schedulazione programmazione</td>
		<td class="campo">
			<af:textBox classNameBase="input" type="integer" name="ggProgrammazioneBDid" value="<%=strGGSchedulazione%>" validateOnPost="true" title="Giorni schedulazione programmazione"
            	readonly="<%=String.valueOf(!canOperation)%>" onKeyUp="fieldChanged();" maxlength="3" size="4"/>&nbsp;*
		</td>
		</tr>
		<%if (!isNuovaProgrammazione) {%>
			<tr>
	        <td class="etichetta" nowrap>Attivo</td>
	        <td class="campo">
			<af:comboBox classNameBase="input" name="flgAttivoDid" onChange="fieldChanged();">
				<option value="S" <%if(flgAttivo.equalsIgnoreCase("S")) {%>SELECTED<%}%>>S&igrave;</option>
				<option value="N" <%if(flgAttivo.equalsIgnoreCase("N")) {%>SELECTED<%}%>>No</option>              
			</af:comboBox>&nbsp;*
			</td>
			</tr>
		<%} else {%>
			<tr>
	        <td class="etichetta" nowrap>Attivo</td>
	        <td class="campo">
	        <af:comboBox classNameBase="input" name="flgAttivoDid_read" disabled="true">
				<option value="N" selected="selected">No</option>              
			</af:comboBox>&nbsp;*
			</td>
			</tr>
		<%}%>
		<tr>
       	<td class="etichetta" nowrap>Tipo SMS</td>
        <td class="campo">
        	<af:comboBox selectedValue="<%=strTipoSms%>" classNameBase="input" title="Tipo SMS" 
		 		name="smsDid" onChange="fieldChanged();" disabled="<%=String.valueOf(!canOperation)%>"/>&nbsp;*     
        </td>
        </tr>
		<tr>
       	<td class="etichetta" nowrap>Tipo evidenza</td>
        <td class="campo">
        	<af:comboBox selectedValue="<%=strPrgTipoEvidenza%>" classNameBase="input" title="Tipo evidenza" 
		 		name="evidenzaDid" onChange="fieldChanged();" disabled="<%=String.valueOf(!canOperation)%>"/>&nbsp;*     
        </td>
        </tr>
		<tr>
		<td class="etichetta" nowrap>Data Inizio Validità</td>
		<td class="campo">
		<af:textBox title="Data inizio validità" type="date" 
            name="DatInizioValDid" value="<%=dataInizioVal%>" size="11" 
            maxlength="10" 
            validateOnPost="true"
            onKeyUp="fieldChanged();"
            classNameBase="input"
            readonly="<%=String.valueOf(!canOperation)%>"/>&nbsp;*
		</td>
		</tr>
		<tr>
		<td class="etichetta">Data Fine Validità</td>
		<td class="campo">
		<af:textBox title="Data fine validità" type="date" 
            name="DatFineValDid" value="<%=dataFineVal%>" size="11" 
            maxlength="10" 
            validateOnPost="true"
            onKeyUp="fieldChanged();"
            classNameBase="input"
            readonly="<%=String.valueOf(!canOperation)%>"/>&nbsp;*
		</td>
		</tr>
		<%if (!isNuovaProgrammazione) {%>
			<tr>
			<td class="etichetta">Data ultima elaborazione</td>
			<td class="campo">
			<af:textBox title="Data ultima elaborazione" type="date" 
	           	name="datUltimaSchedulazioneDid" value="<%=datUltimaElab%>" size="11" maxlength="10" 
	           	classNameBase="input" readonly="true"/>
			</td>
			</tr>
			<%if (!txtNoteElaborazione.equals("")) {%>
				<tr>
				<td class="etichetta">Note ultima elaborazione</td>
				<td class="campo">
				<af:textArea name="noteUltimaSchedulazioneDid" cols="60" rows="3"
					value="<%=txtNoteElaborazione%>" title="Note ultima elaborazione" readonly="true" classNameBase="input"/>
				</td>
				</tr>
			<%}
		}
		if (!isNuovaProgrammazione && flgAttivo.equalsIgnoreCase("S") && prograttivavalidita.equalsIgnoreCase("true")) {%>
			<tr>
			<td class="etichetta">Data prossima elaborazione</td>
			<td class="campo">
			<af:textBox title="Data prossima elaborazione" type="date" 
            	name="datProssimaSchedulazioneDid" value="<%=dataProssimaSchedulazione%>" size="11" maxlength="10" 
            	classNameBase="input" readonly="true"/>
			</td>
			</tr>
		<%}%>
		</table>
	</td>
	</tr>
	
	<tr id="sezBatchPerdDisocc" style="<%=displayPerditaDisocc%>">
	<td align="center">
		<table style="border-collapse:collapse">
		<tr>
		<td class="etichetta" nowrap>Motivo contatto</td>
		<td class="campo">
			<af:comboBox moduleName="M_GetMotivoContattoAG" selectedValue="<%=codMotivoContatto%>" classNameBase="input" title="Motivo contatto" 
		 			 	name="codMotContattoPerdDisocc" onChange="fieldChanged();" addBlank="true" blankValue="" disabled="<%=String.valueOf(!canOperation)%>"/>&nbsp;*
		</td>
		</tr>
		<tr>
		<td class="etichetta" nowrap>Motivo chiusura DID</td>
		<td class="campo">
			<af:comboBox moduleName="M_MotFineAttoSms" selectedValue="<%=codMotivoFineAttoDid%>" classNameBase="input" title="Motivo chiusura DID" 
		 			 	name="codMotivoFineAttoDid" onChange="fieldChanged();" addBlank="true" blankValue="" disabled="<%=String.valueOf(!canOperation)%>"/>
		</td>
		</tr>
		<tr>
		<td class="etichetta" nowrap>Iscritti CM</td>
		<td class="campo">
			<af:comboBox classNameBase="input" name="flgIscrCM" onChange="fieldChanged();" disabled="<%=String.valueOf(!canOperation)%>">
				<OPTION value=""  <%if (flgIscrCM == null || flgIscrCM.equals("")) out.print("SELECTED=\"true\"");%>></OPTION>
          		<OPTION value="S" <%if (flgIscrCM != null && flgIscrCM.equalsIgnoreCase("S")) out.print("SELECTED=\"true\"");%>>Sì</OPTION>
          		<OPTION value="N" <%if (flgIscrCM != null && flgIscrCM.equalsIgnoreCase("N")) out.print("SELECTED=\"true\"");%>>No</OPTION>
        	</af:comboBox>&nbsp;*
		</td>
		</tr>
		<tr>
		<td class="etichetta" nowrap>Giorni rilevazione</td>
		<td class="campo">
			<af:textBox classNameBase="input" type="integer" name="ggRangePerdDisocc" value="<%=strGGRangeBatch%>" validateOnPost="true" title="Giorni rilevazione"
            	readonly="<%=String.valueOf(!canOperation)%>" onKeyUp="fieldChanged();" maxlength="3" size="4"/>&nbsp;*
		</td>
		</tr>
		<tr>
		<td class="etichetta" nowrap>Giorni data fine DID</td>
		<td class="campo">
			<af:textBox classNameBase="input" type="integer" name="ggRangeFineDid" value="<%=strGGRangeDataFineDid%>" validateOnPost="true" title="Giorni data fine DID"
            	readonly="<%=String.valueOf(!canOperation)%>" onKeyUp="fieldChanged();" maxlength="3" size="4"/>
		</td>
		</tr>
		<tr>
		<td class="etichetta" nowrap>CPI di riferimento</td>
		<td class="campo">
			<af:comboBox name="codCpiPerdDisocc"
	        	         title="Centro per l'Impiego"
	            	     multiple="false"
	              	     required="false"
	                 	 focusOn="false"
	                 	 moduleName="M_GetCpiPoloProvincialeAll"
	                 	 addBlank="true"
	                 	 blankValue=""
	                 	 selectedValue="<%=codCpiRiferimento%>"
	                 	 onChange="fieldChanged();"
						 disabled="<%=String.valueOf(!canOperation)%>"/>
	  	</td>
		</tr>
		<tr>
		<tr>
		<td class="etichetta" nowrap>Giorni schedulazione programmazione</td>
		<td class="campo">
			<af:textBox classNameBase="input" type="integer" name="ggProgrammazioneBPerdDisocc" value="<%=strGGSchedulazione%>" validateOnPost="true" title="Giorni schedulazione programmazione"
            	readonly="<%=String.valueOf(!canOperation)%>" onKeyUp="fieldChanged();" maxlength="3" size="4"/>&nbsp;*
		</td>
		</tr>
		<%if (!isNuovaProgrammazione) {%>
			<tr>
	        <td class="etichetta" nowrap>Attivo</td>
	        <td class="campo">
			<af:comboBox classNameBase="input" name="flgAttivoPerdDisocc" onChange="fieldChanged();">
				<option value="S" <%if(flgAttivo.equalsIgnoreCase("S")) {%>SELECTED<%}%>>S&igrave;</option>
				<option value="N" <%if(flgAttivo.equalsIgnoreCase("N")) {%>SELECTED<%}%>>No</option>              
			</af:comboBox>&nbsp;*
			</td>
			</tr>
		<%} else {%>
			<tr>
	        <td class="etichetta" nowrap>Attivo</td>
	        <td class="campo">
	        <af:comboBox classNameBase="input" name="flgAttivoPerdDisocc_read" disabled="true">
				<option value="N" selected="selected">No</option>              
			</af:comboBox>&nbsp;*
			</td>
			</tr>
		<%}%>
		<tr>
       	<td class="etichetta" nowrap>Tipo SMS</td>
        <td class="campo">
        	<af:comboBox selectedValue="<%=strTipoSms%>" classNameBase="input" title="Tipo SMS" 
		 		name="smsPerdDisocc" onChange="fieldChanged();" disabled="<%=String.valueOf(!canOperation)%>"/>&nbsp;*     
        </td>
        </tr>
		<tr>
       	<td class="etichetta" nowrap>Tipo evidenza</td>
        <td class="campo">
        	<af:comboBox selectedValue="<%=strPrgTipoEvidenza%>" classNameBase="input" title="Tipo evidenza" 
		 		name="evidenzaPerdDisocc" onChange="fieldChanged();" disabled="<%=String.valueOf(!canOperation)%>"/>&nbsp;*     
        </td>
        </tr>
		<tr>
		<td class="etichetta" nowrap>Data Inizio Validità</td>
		<td class="campo">
		<af:textBox title="Data inizio validità" type="date" 
            name="DatInizioValPerdDisocc" value="<%=dataInizioVal%>" size="11" 
            maxlength="10" 
            validateOnPost="true"
            onKeyUp="fieldChanged();"
            classNameBase="input"
            readonly="<%=String.valueOf(!canOperation)%>"/>&nbsp;*
		</td>
		</tr>
		<tr>
		<td class="etichetta">Data Fine Validità</td>
		<td class="campo">
		<af:textBox title="Data fine validità" type="date" 
            name="DatFineValPerdDisocc" value="<%=dataFineVal%>" size="11" 
            maxlength="10" 
            validateOnPost="true"
            onKeyUp="fieldChanged();"
            classNameBase="input"
            readonly="<%=String.valueOf(!canOperation)%>"/>&nbsp;*
		</td>
		</tr>
		<%if (!isNuovaProgrammazione) {%>
			<tr>
			<td class="etichetta">Data ultima elaborazione</td>
			<td class="campo">
			<af:textBox title="Data ultima elaborazione" type="date" 
	           	name="datUltimaSchedulazionePerdDisocc" value="<%=datUltimaElab%>" size="11" maxlength="10" 
	           	classNameBase="input" readonly="true"/>
			</td>
			</tr>
			<%if (!txtNoteElaborazione.equals("")) {%>
				<tr>
				<td class="etichetta">Note ultima elaborazione</td>
				<td class="campo">
				<af:textArea name="noteUltimaSchedulazionePerdDisocc" cols="60" rows="3"
					value="<%=txtNoteElaborazione%>" title="Note ultima elaborazione" readonly="true" classNameBase="input"/>
				</td>
				</tr>
			<%}
		}
		if (!isNuovaProgrammazione && flgAttivo.equalsIgnoreCase("S") && prograttivavalidita.equalsIgnoreCase("true")) {%>
			<tr>
			<td class="etichetta">Data prossima elaborazione</td>
			<td class="campo">
			<af:textBox title="Data prossima elaborazione" type="date" 
            	name="datProssimaSchedulazionePerdDisocc" value="<%=dataProssimaSchedulazione%>" size="11" maxlength="10" 
            	classNameBase="input" readonly="true"/>
			</td>
			</tr>
		<%}%>
		</table>
	</td>
	</tr>
	
</table>

<br>
<table>
<tr>
<td align="center">
<%if (canInsert) {%>
    <input type="submit" class="pulsanti" name="BTNINSERT" value="<%=btnSalva%>">
<%}%>
</td>

<td align="center">
<input type="button" class="pulsanti" name="BTNANNULLA" value="<%=btnAnnulla%>" onClick="annulla();">
</td>

</tr>
</table>

<div align="center">
<%
if (operatoreInfo != null) {
	operatoreInfo.showHTML(out);	
}
%>
</div>

</p>
<%out.print(htmlStreamBottom);%>

<%if (isNuovaProgrammazione) {%>
	<table width="100%">
  		<tr><td style="campo2">N.B.: La nuova programmazione viene inserita non ATTIVA.</td></tr>
	</table>
<%}%>

<table id="sezInfoPerdDisocc" style="<%=displayPerditaDisocc%>" width="100%">
	<tr><td style="campo2">Nessun valore per il motivo chiusura DID equivale a considerare tutti i motivi di chiusura previsti</td></tr>
	<tr><td style="campo2">Iscritti CM=SI equivale a considerare anche le dichiarazioni sottoscritte da soggetti iscritti al collocamento mirato. Iscritti CM=NO equivale ad escludere gli iscritti al collocamento mirato</td></tr>
</table>

</af:form>