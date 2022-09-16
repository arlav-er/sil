function valorizzaHid(campo){
  if (campo.checked){
  	if(document.Frm1.CODTIPOTRASF.value != 'DL') {
    	document.Frm1.FLGINTERASSPROPRIA.value="S";
    	azzeraAziendaUtil(false);
  	}
  } 
  else {
    document.Frm1.FLGINTERASSPROPRIA.value="N";
    //document.Frm1.CODTIPOASS.value="";   
  }
}

function controllaSelezioneTipoMov(page,obj){
  if(obj.value != ""){
    goToNoCheck(page);
  }
}

//resetta la combo del codTipoMov quando cambiano lavoratore o azienda
function resetCodTipoMov() {
	var obj = document.getElementById("campiAvv");
	var objMov = document.getElementById("campiMov");
	
	var sezCes = document.getElementById("sezCes");
	var sezPro = document.getElementById("sezPro");
	var sezTra1 = document.getElementById("sezTra1");
	var sezTra2 = document.getElementById("sezTra2");
	var sezTipoVariazione = document.getElementById("sezTipoVariazione");
	//var sezProProsecuzione = document.getElementById("sezProProsecuzione");
	
	if (document.Frm1.CODTIPOMOV.value != "AVV") { 
		document.Frm1.CODTIPOMOV.value = "";
		obj.style.display = "none";
		objMov.style.display = "none";
		sezCes.style.display = "none";
		sezPro.style.display = "none";
		sezTra1.style.display = "none";
		sezTra2.style.display = "none";
		sezTipoVariazione.style.display = "none";
		//sezProProsecuzione.style.display = "none";
		fieldChanged();
		gestisciPrecedente();
	}	
}

//Recupera e setta i dati del movimento corrente quando si è operata la scelta del movimento
//precedente
function settaDatiPrecedente(prgMovPrec) {
	//Query per recupero dati
	var request = "AdapterHTTP?PAGE=MovRecuperoXMLHTTPDatiMovimentoPage&PRGMOVIMENTO=" + prgMovPrec;
	//Controllo sul server
	var result = syncXMLHTTPGETRequest(request);
	if (result == null || result.responseXML.documentElement == null || (result.responseXML.documentElement.tagName != "ROW")) {
		alert("Impossibile reperire i dati del movimento precedente, \n" + 
			"i dati dell'azienda utilizzatrice non saranno valorizzati automaticamente.");
	} else {
		//Settaggio dati generali
		row = result.responseXML.documentElement;
		document.Frm1.CODFISCAZESTERA.value = strNotNull(row.getAttribute("CODFISCUAZESTERA"));
		document.Frm1.RAGSOCAZESTERA.value = strNotNull(row.getAttribute("RAGSOCUAZESTERA"));
		
		document.Frm1.DATAINIZIOAVV.value = strNotNull(row.getAttribute("DATAINIZIOAVV"));
		document.Frm1.DATINIZIOMOVPREC.value = strNotNull(row.getAttribute("DATINIZIOMOV"));
		document.Frm1.datInizioMov.value = strNotNull(row.getAttribute("DATINIZIOMOV"));
		var datInizioMovEdit = document.getElementById("datInizioMovEdit");
	    var datInizioMovNoEdit = document.getElementById("datInizioMovNoEdit");
	    var visualizzaObbli = document.getElementById("visualizzaObbli");
		if(document.Frm1.datInizioMov.value != "") {
			document.Frm1.datInizioMovNoEdit.value = document.Frm1.datInizioMov.value;
			datInizioMovNoEdit.style.display = "";
			datInizioMovEdit.style.display = "none";
		}
		
		visualizzaObbli.style.display = "";
		
		document.Frm1.codMonoTempo.value = strNotNull(row.getAttribute("CODMONOTEMPO"));
		document.Frm1.datFineMov.value = strNotNull(row.getAttribute("DATFINEMOV"));
		if(document.Frm1.codMonoTempo.value == 'I' || document.Frm1.codMonoTempo.value == "") {
			//visualizzaCampi("scadenza","none");
			//visualizzaCampi("datascadenza","none");
			//visualizzaCampi("calcDataFine","none");
			document.Frm1.codMonoTempoNoEdit.value = 'Indeterminato';
		}
		else {
			//visualizzaCampi("scadenza","");
			//visualizzaCampi("datascadenza","");
			//visualizzaCampi("calcDataFine","");
			document.Frm1.codMonoTempoNoEdit.value = 'Determinato';
		}
  		
  		var codMonoTempoEdit = document.getElementById("codMonoTempoEdit");
	    var codMonoTempoNoEdit = document.getElementById("codMonoTempoNoEdit");
	    var datascadenza = document.getElementById("datascadenza");
	    var datascadenzaNoEdit = document.getElementById("datascadenzaNoEdit");
		
		if(document.Frm1.CODTIPOMOV.value == 'CES' || document.Frm1.CODTIPOMOV.value == 'PRO') {
			codMonoTempoEdit.style.display = "none";
			codMonoTempoNoEdit.style.display = "";	
			if(document.Frm1.codMonoTempo.value == 'D') {
				document.Frm1.datFineMovNoEdit.value = document.Frm1.datFineMov.value;
				//datascadenza.style.display = "none";
				//datascadenzaNoEdit.style.display = "";
				document.Frm1.calcDataFine.readonly=true;
				document.Frm1.calcDataFine.disabled=true;
				document.Frm1.calcDataFine.className="pulsanti";
			}
		} else {
			codMonoTempoEdit.style.display = "";
			codMonoTempoNoEdit.style.display = "none";
			if(document.Frm1.codMonoTempo.value == 'D') {
				//datascadenza.style.display = "";
				//datascadenzaNoEdit.style.display = "none";
				document.Frm1.calcDataFine.readonly=false;
				document.Frm1.calcDataFine.disabled=false;
				document.Frm1.calcDataFine.className="pulsanti";
			}
		}
		if (document.Frm1.codOrario != null && document.Frm1.codOrario.value == '') {
			document.Frm1.codOrario.value = strNotNull(row.getAttribute("CODORARIO"));
		}
		var tipoOrario = getTipoOrario(document.Frm1.codOrario.value);
		if(document.Frm1.codOrario.value == "" || tipoOrario == "T" || tipoOrario == "N") {
			visualizzaCampi("labelore","none");
			visualizzaCampi("numore","none");
		}
		else {
			visualizzaCampi("labelore","");
			visualizzaCampi("numore","");
			if (document.Frm1.numOreSett != null && document.Frm1.numOreSett.value == '') {
				document.Frm1.numOreSett.value = strNotNull(row.getAttribute("NUMORESETT"));
			}
		}
		document.Frm1.flagL68.value = strNotNull(row.getAttribute("FLGLEGGE68"));
		if(document.Frm1.flagL68.value == "S") {
			document.Frm1.FLGLEGGE68.value = "S";
			document.Frm1.flagL68.checked = true;
			document.Frm1.numConvenzione.value = strNotNull(row.getAttribute("NUMCONVENZIONE"));
			document.Frm1.datConvenzione.value = strNotNull(row.getAttribute("DATCONVENZIONE"));
			document.Frm1.flgCheckAssObbl.checked = true;
			document.Frm1.flgAssObbl.value = "S";
			document.Frm1.codCatAssObbl.value = strNotNull(row.getAttribute("CODCATASSOBBL"));
			document.Frm1.DATFINESGRAVIO.value = strNotNull(row.getAttribute("DATFINESGRAVIO"));
			document.Frm1.DECIMPORTOCONCESSO.value = strNotNull(row.getAttribute("DECIMPORTOCONCESSO"));
			document.Frm1.DATFINESGRAVIO.readonly=false;
			document.Frm1.DATFINESGRAVIO.disabled=false;
			document.Frm1.DECIMPORTOCONCESSO.readonly=false;
			document.Frm1.DECIMPORTOCONCESSO.disabled=false;
			document.Frm1.DATFINESGRAVIO.className="inputEdit";
			document.Frm1.DECIMPORTOCONCESSO.className="inputEdit";
		} else {
			document.Frm1.FLGLEGGE68.value = "N";
		}
		
		document.Frm1.CODMANSIONE.value = strNotNull(row.getAttribute("CODMANSIONE"));
		document.Frm1.DESCMANSIONE.value = strNotNull(row.getAttribute("DESCRMANSIONE"));
		document.Frm1.CODTIPOMANSIONE.value = strNotNull(row.getAttribute("CODTIPOMANSIONE"));
		document.Frm1.strTipoMansione.value = strNotNull(row.getAttribute("DESCRTIPOMANSIONE"));
		document.Frm1.strDesAttivita.value = strNotNull(row.getAttribute("STRDESATTIVITA"));
		// (Anna Paola 23/01/2014)
		//document.Frm1.decRetribuzioneMen.value = strNotNull(row.getAttribute("DECRETRIBUZIONEMEN"));
		//if(document.Frm1.decRetribuzioneMen.value != ""){
		//	document.Frm1.decRetribuzioneMen.value=document.Frm1.decRetribuzioneMen.value.replace(',','.');
		//	document.Frm1.decRetribuzioneAnn.value = document.Frm1.decRetribuzioneMen.value * 12;
		//}
		document.Frm1.codAgevolazione.value = strNotNull(row.getAttribute("CODAGEVOLAZIONE"));
		
		document.Frm1.CODMONOTEMPOAVV.value = strNotNull(row.getAttribute("CODMONOTEMPOAVV"));
		document.Frm1.DATFINEMOVPREC.value = strNotNull(row.getAttribute("DATFINEMOV"));
		document.Frm1.codTipoAss.value = strNotNull(row.getAttribute("CODTIPOASS")); 
		document.Frm1.descrTipoAss.value = strNotNull(row.getAttribute("DESCRTIPOASS")); 
		document.Frm1.PRGMOVIMENTOPREC.value = strNotNull(row.getAttribute("PRGMOVIMENTO"));
		document.Frm1.CODTIPOMOVPREC.value = strNotNull(row.getAttribute("CODTIPOMOV"));
		document.Frm1.NUMKLOMOVPREC.value = eval(strNotNull(row.getAttribute("NUMKLOMOV"))) + 1;   
		document.Frm1.COLLEGATO.value = 'precedente';    
		precedente = true;
		document.Frm1.CODMVCESSAZIONEPREC.value = strNotNull(row.getAttribute("CODMVCESSAZIONE"));
		
		//Aggiorno le informazioni in fase di avviamento e il frame dei movimenti collegati
		var infoAvviamento = document.getElementById("infoAvv");
		var tempoAvv = "";
		
		if (document.Frm1.CODTIPOMOV.value != "" && document.Frm1.CODTIPOMOV.value == "AVV") {
		   tempoAvv = document.Frm1.CODMONOTEMPO.value;
		} else {
		  tempoAvv = document.Frm1.CODMONOTEMPOAVV.value;
		}
		if (tempoAvv == "D") {
		    tempoAvv = "TD";
		} else if (tempoAvv == "I") {
		    tempoAvv = "TI";
		}
		
		infoAvviamento.innerHTML = "Stato mov.: <strong>In corso</strong> - Inf. in avv.: Rapporto a <strong>"
		                        + tempoAvv +"</strong> di tipo <strong>" 
								+ document.Frm1.codTipoAss.value + "</strong> iniziato il <strong>" 
								+ document.Frm1.DATAINIZIOAVV.value + "</strong>";
		inizializzaCollegati(prgMovPrec, '');
		
		//Settaggio Azienda se non stiamo effettuando un trasferimento 
		//(validazione di una trasformazione con FLGTRASFER = 'S')
		if (document.Frm1.FLGTRASFER == null || document.Frm1.FLGTRASFER.value != 'S') {
			document.Frm1.PRGAZIENDA.value = strNotNull(row.getAttribute("PRGAZIENDA"));
			document.Frm1.PRGUNITA.value = strNotNull(row.getAttribute("PRGUNITA"));
			document.Frm1.CODCPI.value = strNotNull(row.getAttribute("CODCPI"));
			document.Frm1.strPartitaIvaAz.value = strNotNull(row.getAttribute("STRPARTITAIVAAZ"));
			document.Frm1.strCodiceFiscaleAz.value = strNotNull(row.getAttribute("STRCODICEFISCALEAZ"));
			document.Frm1.strRagioneSocialeAz.value = strNotNull(row.getAttribute("STRRAGIONESOCIALEAZ"));
			if (document.Frm1.strRagioneSocialeAz.value.length > 36) {
				document.Frm1.strRagioneSocialeAzTrunc.value = document.Frm1.strRagioneSocialeAz.value.substring(0,34) + "...";
			} else {
				document.Frm1.strRagioneSocialeAzTrunc.value = document.Frm1.strRagioneSocialeAz.value;
			}
			document.Frm1.strIndirizzoUAzVisualizzato.value = strNotNull(row.getAttribute("STRINDIRIZZOUAZ")) + " (" 
															+ strNotNull(row.getAttribute("STRCOMUNEUAZ")) + ", " 
															+ strNotNull(row.getAttribute("STRCAPUAZ")) + ")"; 
			document.Frm1.strIndirizzoUAz.value = strNotNull(row.getAttribute("STRINDIRIZZOUAZ")); 
			document.Frm1.STRENTERILASCIO.value = document.Frm1.strRagioneSocialeAz.value + " - " 
												+ document.Frm1.strIndirizzoUAz.value;
			document.Frm1.strTelUAz.value = strNotNull(row.getAttribute("STRTELUAZ"));
			document.Frm1.strFaxUAz.value = strNotNull(row.getAttribute("STRFAZUAZ"));
			document.Frm1.codCCNLAz.value = strNotNull(row.getAttribute("CODCCNLAZ"));
			document.Frm1.descrCCNLAz.value = strNotNull(row.getAttribute("DESCRCCNLAZ"));
			document.Frm1.codDescrCCNLAz.value = document.Frm1.codCCNLAz.value + " - " 
												+ document.Frm1.descrCCNLAz.value;
			
			if (document.Frm1.codCCNL != null && document.Frm1.codCCNL.value == '') {
				document.Frm1.codCCNL.value = strNotNull(row.getAttribute("CODCCNLAZ"));
				document.Frm1.strCCNL.value = strNotNull(row.getAttribute("DESCRCCNLAZ"));
			}
			
			if(document.Frm1.strCodiceFiscaleAzTra.value != document.Frm1.strCodiceFiscaleAz.value) {
				document.Frm1.strCodiceFiscaleAzTra.value = document.Frm1.strCodiceFiscaleAz.value;
  				document.Frm1.strRagioneSocialeAzTruncTra.value = "";
  				document.Frm1.strIndirizzoUAzVisualizzatoTra.value = "";
  			}
  			document.Frm1.codAtecoUAz.value = strNotNull(row.getAttribute("CODATECOUAZ"));
			document.Frm1.strDesAtecoUAz.value = strNotNull(row.getAttribute("STRDESATECOUAZ"));
			document.Frm1.codAtecoStrDesAttivitaAz.value = document.Frm1.codAtecoUAz.value + " - " 
															+ document.Frm1.strDesAtecoUAz.value;
			document.Frm1.DESCRTIPOAZIENDA.value = strNotNull(row.getAttribute("DESCRTIPOAZIENDA"));
			document.Frm1.CODTIPOAZIENDA.value = strNotNull(row.getAttribute("CODTIPOAZIENDA"));
			if (document.Frm1.CODTIPOAZIENDA.value == codInterinale){
    			document.Frm1.STRNUMALBOINTERINALI.value = strNotNull(row.getAttribute("STRNUMALBOINTERINALI")); 
    			visualizzaCampi("alboInt","");
    			visualizzaCampi("azSommEstera", "");
    			if(document.Frm1.FLGINTERASSPROPRIACHECKBOX.checked == "false");{
    				visualizzaCampi("aziSomm","");
    				visualizzaCampi("visDateMissione","");
    				document.Frm1.strMatricola.value = strNotNull(row.getAttribute("STRMATRICOLA"));
    				if (!(document.Frm1.CODTIPOMOV.value == 'PRO' && contesto == 'valida')) {
						document.Frm1.DATINIZIORAPLAV.value = strNotNull(row.getAttribute("DATINIZIORAPLAV"));
						document.Frm1.DATFINERAPLAV.value = strNotNull(row.getAttribute("DATFINERAPLAV"));
					}
    			}
  			} else { 
  				visualizzaCampi("alboInt","none");
  				visualizzaCampi("visDateMissione","none");
  				visualizzaCampi("aziSomm","none");
  				visualizzaCampi("azSommEstera", "none");
  			}
  			
  			
  			if(document.Frm1.CODTIPOMOV.value == "CES"){
				visualizzaCampi("agrEffettivi","");
				visualizzaCampi("agrPrevisti","none");
				document.Frm1.NUMGGEFFETTUATIAGR.value = strNotNull(row.getAttribute("NUMGGPREVISTIAGR"));
				if (document.Frm1.numLivelloCes != null && document.Frm1.numLivelloCes.value == '') {
					document.Frm1.numLivelloCes.value = strNotNull(row.getAttribute("NUMLIVELLO"));
				}
				document.Frm1.CODLAVORAZIONECES.value = strNotNull(row.getAttribute("CODLAVORAZIONE"));
				if( dataLavAgric != "" && compareDate(dataOdierna,dataLavAgric) > 0) {
					document.Frm1.FLGLAVOROAGRCES.value = strNotNull(row.getAttribute("FLGLAVOROAGR"));
					if(document.Frm1.FLGLAVOROAGRCES.value == "S") {
						document.Frm1.lavAgrCes.checked = true;
					}
				}
			}
			else {
				visualizzaCampi("agrEffettivi","none");
				visualizzaCampi("agrPrevisti","");
				document.Frm1.NUMGGPREVISTIAGR.value = strNotNull(row.getAttribute("NUMGGPREVISTIAGR"));
				if (document.Frm1.numLivello != null && document.Frm1.numLivello.value == '') {
					document.Frm1.numLivello.value = strNotNull(row.getAttribute("NUMLIVELLO"));
				}
				document.Frm1.CODLAVORAZIONE.value = strNotNull(row.getAttribute("CODLAVORAZIONE"));
				if( dataLavAgric != "" && compareDate(dataOdierna,dataLavAgric) > 0) {
					document.Frm1.FLGLAVOROAGR.value = strNotNull(row.getAttribute("FLGLAVOROAGR"));
					if(document.Frm1.FLGLAVOROAGR.value == "S") {
						document.Frm1.lavAgr.checked = true;
					}
				}
			}
			
			document.Frm1.natGiuridicaAz.value = strNotNull(row.getAttribute("NATGIURIDICAAZ"));
			document.Frm1.CODNATGIURIDICAAZ.value = strNotNull(row.getAttribute("CODNATGIURIDICAAZ"));
			document.Frm1.STRREFERENTE.value = strNotNull(row.getAttribute("STRREFERENTE"));   		   
			//Inps ed inail
			var str = null;
			if (strNotNull(row.getAttribute("STRPATINAIL")) != "") {
				str = strNotNull(row.getAttribute("STRPATINAIL")).toString();
				document.Frm1.STRPATINAIL1.value = str.substr(0,8);
				document.Frm1.STRPATINAIL2.value = str.substr(8,2);
				document.Frm1.STRPATINAIL.value = strNotNull(row.getAttribute("STRPATINAIL"));
			}
			if (strNotNull(row.getAttribute("STRPOSINPS")) != "") {      
				str = strNotNull(row.getAttribute("STRPOSINPS")).toString();
				document.Frm1.STRPOSINPS1.value = str.substr(0,2);
				document.Frm1.STRPOSINPS2.value = str.substr(2,str.length-2);
				document.Frm1.STRPOSINPS.value = strNotNull(row.getAttribute("STRPOSINPS"));
			}
			if (strNotNull(row.getAttribute("FLGCAMBIAMENTIDATI")) != "" && document.Frm1.flgCambiamentiDati != null) {
				document.Frm1.flgCambiamentiDati.value = document.Frm1.flgCambiamentiDati.value 
														+ strNotNull(row.getAttribute("FLGCAMBIAMENTIDATI"));
			}
			if ((document.Frm1.PRGAZIENDA.value != "") && (document.Frm1.PRGUNITA.value != "")) {
				if (document.getElementById("DettaglioSedeAzienda") != null) {
					document.getElementById("DettaglioSedeAzienda").style.display = "";
				}
			}
			
			if (document.getElementById("AggiungiAziendaMovimento") != null) {
				if (document.getElementById("AggiungiAziendaMovimento").style.display == '') {
					document.getElementById("AggiungiAziendaMovimento").style.display = "none";
				}
			}
			
			visualizzaInterinali(document.Frm1.CODTIPOAZIENDA.value, 
				(strNotNull(row.getAttribute("FLGINTERASSPROPRIA")) == "S"));
			document.Frm1.RESETCFL.value = 'true';
	
			//Se ho le info dell'azienda utilizzatrice le aggiorno
			if (document.Frm1.CODTIPOTRASF == null || document.Frm1.CODTIPOTRASF.value != "DL") {
				if ((strNotNull(row.getAttribute("PRGAZIENDAUTILIZ")) != "") && 
					(strNotNull(row.getAttribute("PRGUNITAUTILIZ")) != "")) {
					document.Frm1.PRGAZIENDAUTILIZ.value = strNotNull(row.getAttribute("PRGAZIENDAUTILIZ"));
					document.Frm1.PRGUNITAUTILIZ.value = strNotNull(row.getAttribute("PRGUNITAUTILIZ"));
					document.Frm1.STRAZINTNUMCONTRATTO.value = strNotNull(row.getAttribute("STRAZINTNUMCONTRATTO"));
					if (!(document.Frm1.CODTIPOMOV.value == 'PRO' && contesto == 'valida')) {
						document.Frm1.DATAZINTINIZIOCONTRATTO.value = strNotNull(row.getAttribute("DATAZINTINIZIOCONTRATTO"));
						document.Frm1.DATAZINTFINECONTRATTO.value = strNotNull(row.getAttribute("DATAZINTFINECONTRATTO"));
					}
					document.Frm1.STRAZINTRAP.value = strNotNull(row.getAttribute("STRAZINTRAP"));
					document.Frm1.NUMAZINTSOGGETTI.value = strNotNull(row.getAttribute("NUMAZINTSOGGETTI"));
					document.Frm1.NUMAZINTDIPENDENTI.value = strNotNull(row.getAttribute("NUMAZINTDIPENDENTI"));
					document.Frm1.STRLUOGODILAVORO.value = strNotNull(row.getAttribute("STRRAGIONESOCIALEAZUTIL")) + " - "
															+ strNotNull(row.getAttribute("STRINDIRIZZOUAZUTIL")) + " ("
															+ strNotNull(row.getAttribute("STRCOMUNEUAZUTIL")) + ")";
					document.Frm1.STRLUOGODILAVORO.readOnly = true;
					document.Frm1.strRagioneSocialeAzUtil.value = strNotNull(row.getAttribute("STRRAGIONESOCIALEAZUTIL"));
					document.Frm1.strIndirizzoUAzUtil.value = strNotNull(row.getAttribute("STRINDIRIZZOUAZUTIL"));
					document.Frm1.strComuneUAzUtil.value = strNotNull(row.getAttribute("STRCOMUNEUAZUTIL"));	
					aziendaUtilVar = "S";
					document.getElementById("DettaglioAziendaUtil").style.display = "none";
  					document.getElementById("DettaglioAziUtil").style.display = "";
  					document.getElementById("DeleteAziUtil").style.display = "";				
				//Altrimenti inserisco solo il luogo di lavoro se non avevo già impostato l'azienda utilizzatrice
				} else if ((strNotNull(document.Frm1.PRGAZIENDAUTILIZ.value) == "") &&
			 				(strNotNull(document.Frm1.PRGUNITAUTILIZ.value) == "")) {
					document.Frm1.STRLUOGODILAVORO.value = strNotNull(row.getAttribute("STRLUOGODILAVORO"));
					document.Frm1.STRLUOGODILAVORO.readOnly = false;
					document.getElementById("DettaglioAziendaUtil").style.display = "";
  					document.getElementById("DettaglioAziUtil").style.display = "none";
  					document.getElementById("DeleteAziUtil").style.display = "none";
					document.Frm1.PRGAZIENDAUTILIZ.value = "";
					document.Frm1.PRGUNITAUTILIZ.value = "";
					document.Frm1.STRAZINTNUMCONTRATTO.value = "";
					document.Frm1.DATAZINTINIZIOCONTRATTO.value = "";
					document.Frm1.DATAZINTFINECONTRATTO.value = "";
					document.Frm1.STRAZINTRAP.value = "";
					document.Frm1.NUMAZINTSOGGETTI.value = "";
					document.Frm1.NUMAZINTDIPENDENTI.value = "";
					document.Frm1.strRagioneSocialeAzUtil.value = "";
					document.Frm1.strIndirizzoUAzUtil.value = "";
					document.Frm1.strComuneUAzUtil.value = "";	
					//document.Frm1.FLGINTERASSPROPRIA.value = "";
					aziendaUtilVar = "";
				}				
			}
		}
	}
}

//Chiede conferma della rettifica in consultazione e passa alle pagine di rettifica
function confermaRettifica(campoStatoAtto) {
	//Estrazione del codice dello stato atto scelto
	var value = campoStatoAtto.value;
	if (value == 'AR') {
		if (successivo) {
			alert("Impossibile rettificare un movimento che possiede un successivo.");
			return;
		} 
		
		if(isSanato){
			alert("Impossibile rettificare un movimento coinvolto in una dichiarazione di reddito.");
			return;
		}
		
		if (confirm("La rettifica del presente movimento diverrà effettiva solo a seguito\n" + 
						   "dell'inserimento del nuovo movimento protocollato.\nSi desidera proseguire?")) {
			var get = "AdapterHTTP?PAGE=MovDettaglioGeneraleRettificaPage&PRGMOVIMENTO=" 
					+ document.Frm1.PRGMOVIMENTO.value + "&CDNFUNZIONE=" + _funzione
					+ "&NUMKLOMOV=" + document.Frm1.NUMKLOMOV.value
					+ "&CODMONOMOVDICH=" + document.Frm1.codMonoMovDich.value
					+ "&CODSTATOATTO=" + document.Frm1.CODSTATOATTO.value
					+ "&DATCOMUNICAZ=" + document.Frm1.DATCOMUNICAZ.value
					+ "&NUMGGTRAMOVCOMUNICAZIONE=" + document.Frm1.NUMGGTRAMOVCOMUNICAZIONE.value
					+ "&CODMOTANNULLAMENTO=" + document.Frm1.CODMOTANNULLAMENTO.value
					+ "&STRCODICEFISCALETUTORE=" + document.Frm1.STRCODICEFISCALETUTORE.value
					+ "&PRGMOVIMENTOLD=" + document.Frm1.PRGMOVIMENTOLD.value
					+ "&MISSIONE=" + document.Frm1.MISSIONE.value
					+ "&MostraTra=MostraTra";
			if (document.Frm1.PAGERITORNOLISTA != null) {
				get += "&PAGERITORNOLISTA=" + document.Frm1.PAGERITORNOLISTA.value;
			}
			setWindowLocation(get);
		}
	}
}

//Consultazione del movimento rettificato da quello attualmente consultato
function consultaRettificato(prgMovRett) {
	var get = "AdapterHTTP?PAGE=MovDettaglioGeneraleConsultaPage&PRGMOVIMENTO=" + prgMovRett 
			+ "&CDNFUNZIONE=" + _funzione + "&CURRENTCONTEXT=salva&ACTION=naviga&PROVENIENZA=ListaMov"
			+ "&MostraTra=MostraTra"
			//Parte necessaria al ritorno alla consultazione del movimento protocollato
			+ "&PRGMOVIMENTOPROTDARETTIFICA=";
			if (document.Frm1.PRGMOVIMENTOPROTDARETTIFICA != null) {
				get += document.Frm1.PRGMOVIMENTOPROTDARETTIFICA.value;
			} else {
				get += document.Frm1.PRGMOVIMENTO.value;
			}
	if (document.Frm1.PAGERITORNOLISTA != null) {
		get += "&PAGERITORNOLISTA=" + document.Frm1.PAGERITORNOLISTA.value;
	}
	setWindowLocation(get);
}

//Consultazione del movimento protocollato da uno rettificato
function consultaProtocollatoDaRettifica(prgMovProt) {
	var get = "AdapterHTTP?PAGE=MovDettaglioGeneraleConsultaPage&PRGMOVIMENTO=" + prgMovProt 
			+ "&CDNFUNZIONE=" + _funzione + "&CURRENTCONTEXT=salva&ACTION=naviga&PROVENIENZA=ListaMov"
			+ "&MostraTra=MostraTra";
	if (document.Frm1.PAGERITORNOLISTA != null) {
		get += "&PAGERITORNOLISTA=" + document.Frm1.PAGERITORNOLISTA.value;
	}
	setWindowLocation(get);
}

function gestisciCampi() {
	var opened;
	var obj = document.getElementById("campiAvv");
	var objMov = document.getElementById("campiMov");
	
	var sezCes = document.getElementById("sezCes");
	var sezPro = document.getElementById("sezPro");
	var sezTra1 = document.getElementById("sezTra1");
	var sezTra2 = document.getElementById("sezTra2");
	var sezTipoVariazione = document.getElementById("sezTipoVariazione");
	//var sezProProsecuzione = document.getElementById("sezProProsecuzione");
	
	var tipoMovValue = document.Frm1.CODTIPOMOV.value;
	
	if(tipoMovValue != "") {
    	var f = "AdapterHTTP?PAGE=FantasmaGestisciCampiPage&AGG_FUNZ=funzioneTitolo&CODTIPOMOV="+tipoMovValue;
    	var t = "_blank";
    	var feat = "toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=no,resizable=no,width=1,height=1,top=0,left=0";
    	opened = window.open(f, t, feat);
    }
    else {  
    		obj.style.display = "none";
    		objMov.style.display = "none";
    		sezCes.style.display = "none";
    		sezTra1.style.display = "none";
    		sezTra2.style.display = "none";
    		sezPro.style.display = "none";
    		sezTipoVariazione.style.display = "none";
    		//sezProProsecuzione.style.display = "none";
    	}
} 

function funzioneTitolo(titolo,sezTitolo,codMonoMovDich){
	var obj = document.getElementById("campiAvv");
	var objMov = document.getElementById("campiMov");
	var sezCes = document.getElementById("sezCes");
	var sezPro = document.getElementById("sezPro");
	var sezTra1 = document.getElementById("sezTra1");
	var sezTra2= document.getElementById("sezTra2");
	var sezTipoVariazione = document.getElementById("sezTipoVariazione");
	//var sezProProsecuzione = document.getElementById("sezProProsecuzione");
	var datiAziTra= document.getElementById("datiAziTra");
	var sezAziTra= document.getElementById("sezAziTra");
	var datInizioMovEdit = document.getElementById("datInizioMovEdit");
	var datInizioMovNoEdit = document.getElementById("datInizioMovNoEdit");
	var codMonoTempoEdit = document.getElementById("codMonoTempoEdit");
	var codMonoTempoNoEdit = document.getElementById("codMonoTempoNoEdit");
	var scadenza = document.getElementById("scadenza");
	var datascadenza = document.getElementById("datascadenza");
	var datascadenzaNoEdit = document.getElementById("datascadenzaNoEdit");
	var calcDataFine = document.getElementById("calcDataFine");
	if(contesto != 'consulta') { 
		if( precedente || document.Frm1.CODTIPOMOV.value != "AVV") { 
			datInizioMovNoEdit.style.display = "";
			datInizioMovEdit.style.display = "none";
			if( (document.Frm1.CODTIPOMOV.value == 'CES') || (document.Frm1.CODTIPOMOV.value == 'PRO') ) {
				codMonoTempoEdit.style.display = "none";
				codMonoTempoNoEdit.style.display = "";
				if(document.Frm1.codMonoTempo.value == 'D') {
					if (document.Frm1.CODTIPOMOV.value == 'PRO' && contesto == 'valida') {
						//datascadenza.style.display = "";
						//datascadenzaNoEdit.style.display = "none";
						document.Frm1.calcDataFine.readonly=false;
						document.Frm1.calcDataFine.disabled=false;
						document.Frm1.calcDataFine.className="pulsanti";
					}
					else {
						document.Frm1.datFineMovNoEdit.value = document.Frm1.datFineMov.value;
						//datascadenza.style.display = "none";
						//datascadenzaNoEdit.style.display = "";
						document.Frm1.calcDataFine.readonly=true;
						document.Frm1.calcDataFine.disabled=true;
						document.Frm1.calcDataFine.className="pulsanti";
						if(document.Frm1.CODTIPOMOV.value == 'CES' && document.Frm1.datFineMov.value == ''){
							//scadenza.style.display = "none";
							//datascadenza.style.display = "none";
							//datascadenzaNoEdit.style.display = "none";
							//calcDataFine.style.display = "none";
						}
					}
				}
			
			} else {
				codMonoTempoEdit.style.display = "";
				codMonoTempoNoEdit.style.display = "none";
				if(document.Frm1.codMonoTempo.value == 'D') {
					//datascadenza.style.display = "";
					//datascadenzaNoEdit.style.display = "none";
					document.Frm1.calcDataFine.readonly=false;
					document.Frm1.calcDataFine.disabled=false;
					document.Frm1.calcDataFine.className="pulsanti";
				}
			}
		} else {
			datInizioMovNoEdit.style.display = "none";
			datInizioMovEdit.style.display = "";
			codMonoTempoEdit.style.display = "";
			codMonoTempoNoEdit.style.display = "none";
			if(document.Frm1.codMonoTempo.value == 'D') {
				//datascadenza.style.display = "";
				//datascadenzaNoEdit.style.display = "none";
				document.Frm1.calcDataFine.readonly=false;
				document.Frm1.calcDataFine.disabled=false;
				document.Frm1.calcDataFine.className="pulsanti";
			}
		}
	} else {
		codMonoTempoEdit.style.display = "";
		codMonoTempoNoEdit.style.display = "none";	
		if(document.Frm1.datFineMov.value != '') {
			//datascadenza.style.display = "";
			//datascadenzaNoEdit.style.display = "none";
		}
	}
	
	if(!precedente && (document.Frm1.CODTIPOMOV.value == "CES" || document.Frm1.CODTIPOMOV.value == "TRA") ) {
		visualizzaObbli.style.display = "none";
	}
	else {
		visualizzaObbli.style.display = "";
	}
	document.Frm1.titolo.value = titolo;
	var tipoMov = document.Frm1.CODTIPOMOV.value;
	if(sezTitolo != "") { document.Frm1.sezTitolo.value = sezTitolo;}
	obj.style.display = "none";
  	objMov.style.display = "none";
	sezCes.style.display = "none";
	sezTra1.style.display = "none";
	sezTra2.style.display = "none";
	sezPro.style.display = "none";
	datiAziTra.style.display = "none";
	sezAziTra.style.display = "none";
	sezTipoVariazione.style.display = "none";
	//sezProProsecuzione.style.display = "none";
	
	visualizzaCampi("agrEffettivi","none");
	visualizzaCampi("agrPrevisti","none");
 	if(titolo != ""){
 		obj.style.display = "";
 		if (document.Frm1.CODTIPOAZIENDA.value == codInterinale) {
			lavStagionaleLabel.style.display = "none";
			lavStagioneFlg.style.display = "none";
		}
 		else
 			{
				lavStagionaleLabel.style.display = "";
				lavStagioneFlg.style.display = "";
 			}
 		if(tipoMov == "AVV") { 
			visualizzaCampi("agrPrevisti","");
		}
		else {
			if (document.Frm1.CODTIPOAZIENDA.value == codInterinale) {
				sezTipoVariazione.style.display = "";
				lavStagionaleLabel.style.display = "none";
				lavStagioneFlg.style.display = "none";
			}
			//else {
				//if(tipoMov == "PRO") {
					//sezProProsecuzione.style.display = "";
				//}

			//}	
		}
 		if(tipoMov == "CES") { 
			objMov.style.display = "";
			sezCes.style.display = "";
			
			visualizzaCampi("agrEffettivi","");
			
		} if(tipoMov == "PRO") { 
			objMov.style.display = "";
			sezPro.style.display = "";
			
			visualizzaCampi("agrPrevisti","");
			
		} if(tipoMov == "TRA") { 
			objMov.style.display = "";
			sezTra1.style.display = "";
			sezTra2.style.display = "";
			
			visualizzaCampi("agrPrevisti","");
		}
		getTipoTrasf();
	} 
	
	
}

function getSezioni(){
	var imgV = document.getElementById("tendinaLavoratore");
  	var lav1 = document.getElementById("datiLavoratore");
	var lav2 = document.getElementById("datiLav");
	if(contesto != 'inserisci') {
		if(lav2.style.display==""){
			lav1.style.display = "none";
			lav2.style.display = "";
			imgV.src=imgAperta;
    	}else { 
    		lav1.style.display = "";
			lav2.style.display = "none";
			imgV.src = imgChiusa;
		}
	}
}


function cambiaLav(img,lav1,lav2,cdnLavoratore) {
	var lav1 = document.getElementById(lav1);
	var lav2 = document.getElementById(lav2);
	if ( document.Frm1.CDNLAVORATORE.value != "" && document.Frm1.CDNLAVORATORE.value == cdnLavoratore ) {
		if (lav1.style.display == "") {
			lav1.style.display = "none";
			lav2.style.display = "";
			img.src = imgAperta;
    		img.alt = "Chiudi";
		}
		else if (lav1.style.display == "none") {
			lav1.style.display = "";
			lav2.style.display = "none";
			img.src = imgChiusa;
			img.alt = 'Apri';
		}
	}
}


function cambiaDatiAzienda(immagine, sezione, num) {
		for(i=0;i<num;i++){
			var sez = sezione + i;
			var sezAzi = document.getElementById(sez);
			if (sezAzi.style.display == "") {
				sezAzi.style.display="none";
				sezAzi.aperta=false;
				immagine.src=imgChiusa;
	    		immagine.alt="Apri";
			}
			else {
				sezAzi.style.display="";
				sezAzi.aperta=true;
				immagine.src=imgAperta;
	    		immagine.alt="Chiudi";
			}
		}
}

var opened;
	
function apriLavoratore(soggetto, funzionediaggiornamento, cdnLavoratore) {
	var f = "AdapterHTTP?PAGE=MovimentiSelezionaSoggettoPage&MOV_SOGG=" + soggetto + "&AGG_FUNZ=" + funzionediaggiornamento + "&CDNFUNZIONE=" + _funzione;
    if (cdnLavoratore != '' && (typeof cdnLavoratore) != "undefined") {f = f + "&CDNLAV=" + cdnLavoratore;}
    var t = "_blank";
    var feat = "toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,width=750,height=650,top=30,left=180";
    opened = window.open(f, t, feat);
}

function controllaAziSomm() {
	var mat = document.getElementById("aziSomm");
	var datMis = document.getElementById("visDateMissione");
	var tipoMov = document.Frm1.CODTIPOMOV.value;
	var motivoTra = document.Frm1.CODTIPOTRASF.value;
	var datFineDistacco = document.getElementById("datFineDistacco");
	var flgAziDistacco = document.getElementById("flgAziDistacco");
	if(document.Frm1.FLGINTERASSPROPRIACHECKBOX.checked) {
		mat.style.display = "none";
		datMis.style.display = "none";
		document.Frm1.strMatricola.value = "";
		document.Frm1.DATINIZIORAPLAV.value = "";
		document.Frm1.DATFINERAPLAV.value = "";
		if(tipoMov != '' && tipoMov == 'TRA' && motivoTra != '' && motivoTra == 'DL') {
    		datFineDistacco.style.display = "";
  			flgAziDistacco.style.display = "";
    	}  
	} else { 
		mat.style.display = "";
		datMis.style.display = "";
		if(tipoMov != '' && tipoMov == 'TRA' && motivoTra != '' && motivoTra == 'DL') {
    		document.Frm1.CODTIPOTRASF.value = "";
    		document.Frm1.DATFINEDISTACCO.value = "";
    		document.Frm1.flagDistacco.checked = false;
    		document.Frm1.flagAziEstera.checked = false;
    		datFineDistacco.style.display = "none";
  			flgAziDistacco.style.display = "none";
    	} 
	}
}

function getTipoTrasf() {
	var aziTra = document.getElementById("datiAziTra");
	var sezAziTra = document.getElementById("sezAziTra");
	var datFineDistacco = document.getElementById("datFineDistacco");
	var flgAziDistacco = document.getElementById("flgAziDistacco");
	var datFineAffittoRamo = document.getElementById("datFineAffittoRamo");
	
	if(!consulta) {
		if(document.Frm1.CODTIPOMOV.value == 'TRA' && document.Frm1.CODTIPOTRASF.value == 'TL') {
			aziTra.style.display = "";
			sezAziTra.style.display = "";
			document.Frm1.strCodiceFiscaleAzTra.value = document.Frm1.strCodiceFiscaleAz.value;
			document.Frm1.strCodiceFiscaleAzTra.readOnly = true;
		} else {
			aziTra.style.display = "none";
			sezAziTra.style.display = "none";
		}
	}
	if(document.Frm1.CODTIPOMOV.value == 'TRA' && document.Frm1.CODTIPOTRASF.value == 'DL') {
		if(document.Frm1.flagAziEstera.checked) {
			document.Frm1.bottoneDistacco.readonly=true;
			document.Frm1.bottoneDistacco.disabled=true;
			document.Frm1.bottoneDistacco.className="pulsantiDisabled";
		} else {
  			document.Frm1.bottoneDistacco.className="pulsanti";
		}
		datFineDistacco.style.display = "";
		flgAziDistacco.style.display = "";
	} else {
  		datFineDistacco.style.display = "none";
  		flgAziDistacco.style.display = "none";
	}
	if(document.Frm1.CODTIPOMOV.value == 'TRA' && document.Frm1.CODTIPOTRASF.value == '02') {
		datFineAffittoRamo.style.display = "";
	} else {
		datFineAffittoRamo.style.display = "none";
		document.Frm1.DATFINEAFFITTORAMO.value = "";
	}
}

function apriAziTrasf() {
	if(document.Frm1.CODTIPOTRASF.value == 'TL'){
		var strCodiceFiscale = document.Frm1.strCodiceFiscaleAz.value;
		var f = "AdapterHTTP?PAGE=ScegliAziTrasferimentoPage&cf=" + strCodiceFiscale + 
				"&AGG_FUNZ=aggAziTra&codMvTrasformazione=TL"; 
		var t = "_blank";
   		var feat = "toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,width=750,height=650,top=30,left=180";
    	opened = window.open(f, t, feat);
    }
}

function aggAziTra() {
	document.Frm1.PRGAZIENDATRA.value = opened.dati.prgAzienda;
	document.Frm1.PRGUNITATRA.value = opened.dati.prgUnita;
  	document.Frm1.strRagioneSocialeAzTruncTra.value = opened.dati.ragioneSociale;
  	document.Frm1.strIndirizzoUAzVisualizzatoTra.value = opened.dati.strIndirizzoAzienda + " (" + opened.dati.comuneAzienda + ", " + opened.dati.strCapAzienda + ")";
	document.Frm1.strIndirizzoUAzTra.value = opened.dati.strIndirizzoAzienda
	document.Frm1.strComuneUAzTra.value = opened.dati.comuneAzienda
	document.Frm1.strCapUAzTra.value = opened.dati.strCapAzienda
	opened.close();
} 

function getTipoTrasf2() {
	var scadenza = document.getElementById("scadenza");
  	var datascadenza = document.getElementById("datascadenza");
  	var labelore = document.getElementById("labelore");
  	var numore = document.getElementById("numore");
  	var calcDataFine = document.getElementById("calcDataFine");
  	var flagPerInt = document.getElementById("flagPersonaleInterno").checked;
  	var datFineDistacco = document.getElementById("datFineDistacco");
	var flgAziDistacco = document.getElementById("flgAziDistacco");
	
	if(document.Frm1.CODTIPOTRASF.value == 'DI' || document.Frm1.CODTIPOTRASF.value == "AI"
	   || document.Frm1.CODTIPOTRASF.value == "FI" || document.Frm1.CODTIPOTRASF.value == "II") {
	
		document.Frm1.codMonoTempo.value = 'I';
		document.Frm1.datFineMov.value = "";
    	//scadenza.style.display = "none";
    	//datascadenza.style.display = "none";
    	//calcDataFine.style.display = "none";   
	}
	if(document.Frm1.CODTIPOTRASF.value == 'PP') {
		document.Frm1.codOrario.value = 'F';
		labelore.style.display = "none";
		numore.style.display = "none";
		document.Frm1.numOreSett.value = '';
	}
	var tipoOrario = getTipoOrario(document.Frm1.codOrario.value);
	if( document.Frm1.CODTIPOTRASF.value == 'TP' && (tipoOrario == 'T' || tipoOrario == 'N') ) {
		alert("Indicare il tempo parziale e il numero di ore settimanali");
		document.Frm1.CODTIPOTRASF.value = '';
	}
	if(document.Frm1.CODTIPOTRASF.value == 'NT') {
		alert("Operazione non consentita!");
		document.Frm1.CODTIPOTRASF.value = '';
	}
	
	if(document.Frm1.CODTIPOTRASF.value == 'DL') {
		if(document.Frm1.CODTIPOAZIENDA.value == "INT" && !flagPerInt) {
			alert("E' possibile effettuare un distacco solo per personale interno");
			document.Frm1.CODTIPOTRASF.value = '';
			datFineDistacco.style.display = "none";
  			flgAziDistacco.style.display = "none";
			return false;
		} else {
			document.Frm1.bottoneDistacco.className="pulsanti";
  			document.Frm1.bottoneDistacco.readonly=false;
			document.Frm1.bottoneDistacco.disabled=false;
		}
	}
}

function gestisciFlagDistacco() {
	if(document.Frm1.flagDistacco.checked) {
		document.Frm1.FLGDISTPARZIALE.value="S";
	} else {
		document.Frm1.FLGDISTPARZIALE.value="N";
	}
}

function gestisciFlagAziEsterna() {
	if(document.Frm1.flagAziEstera.checked) {
		document.Frm1.FLGDISTAZESTERA.value="S";
		document.Frm1.bottoneDistacco.readonly=true;
		document.Frm1.bottoneDistacco.disabled=true;
		document.Frm1.bottoneDistacco.className="pulsantiDisabled";
		document.Frm1.FLGDISTAZESTERA.value="S";
		document.Frm1.PRGAZIENDAUTILIZ.value = "";
  		document.Frm1.PRGUNITAUTILIZ.value = "";
   		document.Frm1.strRagioneSocialeAzUtil.value = "";
  		document.Frm1.strIndirizzoUAzUtil.value = "";
  		document.Frm1.strComuneUAzUtil.value = "";    
  	} else {
  		document.Frm1.bottoneDistacco.className="pulsanti";
  		document.Frm1.bottoneDistacco.readonly=false;
		document.Frm1.bottoneDistacco.disabled=false;
		document.Frm1.FLGDISTAZESTERA.value="N";
  	}
}

function gestisciFlagLavMobilita() {
	if(document.Frm1.lavInMob.checked) {
		document.Frm1.FLGLAVOROINMOBILITA.value="S";
	} else {
		document.Frm1.FLGLAVOROINMOBILITA.value="N";
	}
}


function gestisciFlagLavStagionale() {
	if(document.Frm1.lavStagionale.checked) {
		document.Frm1.FLGLAVOROSTAGIONALE.value="S";
	} else {
		document.Frm1.FLGLAVOROSTAGIONALE.value="N";
	}
}



function PulisciRicercaCCNLLivello(numLivello, numLivelloCes, codCCNL, codCCNLhid, nomeCCNL, nomeCCNLhid, strTipo) {
    if (strTipo == 'descrizione') {
      if (nomeCCNL.value != nomeCCNLhid.value) {
        nomeCCNL.value=(nomeCCNL.value).toUpperCase();
        codCCNLhid.value="";
        codCCNL.value="";
        numLivello.value = "";
        numLivelloCes.value = "";
      }
    }
    else {
     if (codCCNL.value != codCCNLhid.value) {
        codCCNL.value=(codCCNL.value).toUpperCase();
        nomeCCNL.value="";
        nomeCCNLhid.value="";
        numLivello.value = "";
        numLivelloCes.value = "";
      }
    }
  }

// utilizzata in fase di inserimento e rettifica movimenti 
function PulisciRetribuzAnnuale() {	
	document.Frm1.decRetribuzioneAnn.value = "";	
}

// utilizzata in fase di validazione movimenti 
function PulisciRetribuzAnnualeValidaz() {
  var strVersioneTracciato = document.Frm1.STRVERSIONETRACCIATOhid.value;

  if (strVersioneTracciato != null && strVersioneTracciato != '' && Number(strVersioneTracciato.substring(0, 1)) >=4) {
	  document.Frm1.decRetribuzioneAnn.value = "";
  }	
}
