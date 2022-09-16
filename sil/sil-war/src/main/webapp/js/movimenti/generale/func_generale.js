function valorizzaHid(campo){
  if (campo.checked){
    document.Frm1.FLGINTERASSPROPRIA.value="S";
    azzeraAziendaUtil(false);
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
	if (document.Frm1.CODTIPOMOV.value != "AVV") { 
		document.Frm1.CODTIPOMOV.value = "";
		fieldChanged();
		gestisciPrecedente();
		gestisciLinguette("");
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
		document.Frm1.DATAINIZIOAVV.value = strNotNull(row.getAttribute("DATAINIZIOAVV"));
		document.Frm1.DATINIZIOMOVPREC.value = strNotNull(row.getAttribute("DATINIZIOMOV"));
		document.Frm1.CODMONOTEMPOAVV.value = strNotNull(row.getAttribute("CODMONOTEMPOAVV"));
		document.Frm1.DATFINEMOVPREC.value = strNotNull(row.getAttribute("DATFINEMOV"));
		document.Frm1.CODTIPOASS.value = strNotNull(row.getAttribute("CODTIPOASS"));  
		document.Frm1.PRGMOVIMENTOPREC.value = strNotNull(row.getAttribute("PRGMOVIMENTO"));
		document.Frm1.CODTIPOMOVPREC.value = strNotNull(row.getAttribute("CODTIPOMOV"));
		document.Frm1.CODTIPOCONTRATTO.value = strNotNull(row.getAttribute("CODTIPOCONTRATTO"));
		document.Frm1.NUMKLOMOVPREC.value = eval(strNotNull(row.getAttribute("NUMKLOMOV"))) + 1;   
		document.Frm1.COLLEGATO.value = 'precedente';    
		precedente = true;
		
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
		
		infoAvviamento.innerHTML = "Inf. Valide in fase di avviamento: Rapporto a <strong>"
		                        + tempoAvv +"</strong> di tipo <strong>" 
								+ document.Frm1.CODTIPOASS.value + "</strong> iniziato il <strong>" 
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
			if (document.Frm1.strRagioneSocialeAz.value.length > 30) {
				document.Frm1.strRagioneSocialeAzTrunc.value = document.Frm1.strRagioneSocialeAz.value.substring(0,26) + "...";
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
			document.Frm1.codAtecoUAz.value = strNotNull(row.getAttribute("CODATECOUAZ"));
			document.Frm1.strDesAtecoUAz.value = strNotNull(row.getAttribute("STRDESATECOUAZ"));
			document.Frm1.codAtecoStrDesAttivitaAz.value = document.Frm1.codAtecoUAz.value + " - " 
															+ document.Frm1.strDesAtecoUAz.value;
			document.Frm1.DESCRTIPOAZIENDA.value = strNotNull(row.getAttribute("DESCRTIPOAZIENDA"));
			document.Frm1.CODTIPOAZIENDA.value = strNotNull(row.getAttribute("CODTIPOAZIENDA"));
			if (document.Frm1.CODTIPOAZIENDA.value == codInterinale){
				document.Frm1.STRNUMALBOINTERINALI.value = strNotNull(row.getAttribute("STRNUMALBOINTERINALI"));
				visualizzaCampi("alboInt","inline");
			} else visualizzaCampi("alboInt","none");
			var strCampi = strNotNull(row.getAttribute("STRNUMREGISTROCOMMITT"));
			if (strCampi != "") {
				document.Frm1.STRNUMREGISTROCOMMITT.value = strCampi;
				visualizzaCampi("regCommit","inline");
			} else visualizzaCampi("regCommit","none");
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
			document.Frm1.FLGDATIOK.value = strNotNull(row.getAttribute("FLGDATIOK"));
			if ( document.Frm1.FLGDATIOK.value == "S" ){ 
				document.Frm1.FLGDATIOK.value = "Si";
			} else {
				if ( document.Frm1.FLGDATIOK.value != "" ){
					document.Frm1.FLGDATIOK.value = "No";
				}
			}
			if (strNotNull(row.getAttribute("FLGCAMBIAMENTIDATI")) != "" && document.Frm1.flgCambiamentiDati != null) {
				document.Frm1.flgCambiamentiDati.value = document.Frm1.flgCambiamentiDati.value 
														+ strNotNull(row.getAttribute("FLGCAMBIAMENTIDATI"));
			}
			if ((document.Frm1.PRGAZIENDA.value != "") && (document.Frm1.PRGUNITA.value != "")) {
				if (document.getElementById("DettaglioSedeAzienda") != null) {
					document.getElementById("DettaglioSedeAzienda").style.display = "inline";
				}
			}
			if (document.getElementById("AggiungiAziendaMovimento") != null) {
				if (document.getElementById("AggiungiAziendaMovimento").style.display == 'inline') {
					document.getElementById("AggiungiAziendaMovimento").style.display = "none";
				}
			}
		    
			visualizzaInterinali(document.Frm1.CODTIPOAZIENDA.value, 
				(strNotNull(row.getAttribute("FLGINTERASSPROPRIA")) == "S"));
			document.Frm1.RESETCFL.value = 'true';
	
			//Se ho le info dell'azienda utilizzatrice le aggiorno (tranne per il distacco)
			if (document.Frm1.CODTIPOTRASF == null || document.Frm1.CODTIPOTRASF.value != "DL") {
				if ((strNotNull(row.getAttribute("PRGAZIENDAUTILIZ")) != "") && 
					(strNotNull(row.getAttribute("PRGUNITAUTILIZ")) != "")) {
					document.Frm1.PRGAZIENDAUTILIZ.value = strNotNull(row.getAttribute("PRGAZIENDAUTILIZ"));
					document.Frm1.PRGUNITAUTILIZ.value = strNotNull(row.getAttribute("PRGUNITAUTILIZ"));
					document.Frm1.STRAZINTNUMCONTRATTO.value = strNotNull(row.getAttribute("STRAZINTNUMCONTRATTO"));
					document.Frm1.DATAZINTINIZIOCONTRATTO.value = strNotNull(row.getAttribute("DATAZINTINIZIOCONTRATTO"));
					document.Frm1.DATAZINTFINECONTRATTO.value = strNotNull(row.getAttribute("DATAZINTFINECONTRATTO"));
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
				//Altrimenti inserisco solo il luogo di lavoro se non avevo già impostato l'azienda utilizzatrice
				} else if ((strNotNull(document.Frm1.PRGAZIENDAUTILIZ.value) == "") &&
				 			(strNotNull(document.Frm1.PRGUNITAUTILIZ.value) == "")) {
					document.Frm1.STRLUOGODILAVORO.value = strNotNull(row.getAttribute("STRLUOGODILAVORO"));
					document.Frm1.STRLUOGODILAVORO.readOnly = false;
					
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
					+ "&CODMOTANNULLAMENTO=" + document.Frm1.CODMOTANNULLAMENTO.value;
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
			+ "&CDNFUNZIONE=" + _funzione + "&CURRENTCONTEXT=salva&ACTION=naviga&PROVENIENZA=ListaMov";
	if (document.Frm1.PAGERITORNOLISTA != null) {
		get += "&PAGERITORNOLISTA=" + document.Frm1.PAGERITORNOLISTA.value;
	}
	setWindowLocation(get);
}