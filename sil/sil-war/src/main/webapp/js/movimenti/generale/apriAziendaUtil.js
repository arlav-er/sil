function apriAzUtil() {
  var prgAziendaUtilLocal = document.Frm1.PRGAZIENDAUTILIZ.value;
  var prgUAziendaUtilLocal = document.Frm1.PRGUNITAUTILIZ.value;
  var strRagioneSocialeAzUtil = document.Frm1.strRagioneSocialeAzUtil.value;
  var numContratto = document.Frm1.STRAZINTNUMCONTRATTO.value;
  var dataInizio = document.Frm1.DATAZINTINIZIOCONTRATTO.value;
  var dataFine = document.Frm1.DATAZINTFINECONTRATTO.value;
  var legaleRapp = document.Frm1.STRAZINTRAP.value;
  var numSoggetti = document.Frm1.NUMAZINTSOGGETTI.value;
  var classeDip = document.Frm1.NUMAZINTDIPENDENTI.value;
  var strIndirizzoUAzUtil = document.Frm1.strIndirizzoUAzUtil.value;
  var strComuneUAzUtil = document.Frm1.strComuneUAzUtil.value;
  var flagAziEstera = document.Frm1.FLGDISTAZESTERA.value;
  
  strRagioneSocialeAzUtil = strRagioneSocialeAzUtil.replace('&','%26');
  
  if(document.Frm1.CODTIPOTRASF.value != '' && document.Frm1.CODTIPOTRASF.value == 'DL') {
  	var f = "AdapterHTTP?PAGE=MovCercaAziendaUtilPage&PRGAZIENDAUTIL=" + prgAziendaUtilLocal + 
    		"&PRGUNITAUTIL=" + prgUAziendaUtilLocal + "&FUNZ_AGG=aggiornaAziendaUtil" +
  			"&canModify=" + canModify +
  			"&FLGDISTAZESTERA=" + flagAziEstera +
  		  	"&strRagioneSocialeAzUtil=" + strRagioneSocialeAzUtil + 
  		    "&context=" +  document.Frm1.CURRENTCONTEXT.value + "&strIndirizzoAziendaUtil="+ strIndirizzoUAzUtil +"&strComuneAziendaUtil=" + strComuneUAzUtil +
  		    "&CODTIPOTRASF=" + document.Frm1.CODTIPOTRASF.value;
  		    var t = "_blank";
  		    var feat = "toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,width=850,height=600,top=0,left=100";
  		    openedAzUtil = window.open(f, t, feat);
  } else {
  	var f = "AdapterHTTP?PAGE=MovCercaAziendaUtilPage&PRGAZIENDAUTIL=" + prgAziendaUtilLocal + 
  			"&PRGUNITAUTIL=" + prgUAziendaUtilLocal + "&FUNZ_AGG=aggiornaAziendaUtil&numContratto=" + numContratto +
  			"&dataInizio=" + dataInizio + "&dataFine=" + dataFine + "&legaleRapp=" + legaleRapp + "&canModify=" + canModify +
  			"&numSoggetti=" + numSoggetti + "&classeDip=" + classeDip + "&strRagioneSocialeAzUtil=" + strRagioneSocialeAzUtil + 
  			"&context=" +  document.Frm1.CURRENTCONTEXT.value + "&strIndirizzoAziendaUtil="+ strIndirizzoUAzUtil +"&strComuneAziendaUtil=" + strComuneUAzUtil;
  			var t = "_blank";
  			var feat = "toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,width=850,height=600,top=0,left=100";
  			openedAzUtil = window.open(f, t, feat);
  }
}  

//Apre la pop-up di gestione dell'azienda utilizzatrice in validazione, visto che in questo contesto devo fare 
//una gestione particolare sull'azienda la pop-up è collegata ad una pagina differente
function apriAzUtilValidazione() {
	var codTipoTrasf = document.Frm1.CODTIPOTRASF.value;
	var flagAziEstera = document.Frm1.FLGDISTAZESTERA.value;
 	if(codTipoTrasf != '' && codTipoTrasf == 'DL') {
 		var f = "AdapterHTTP?PAGE=MovCercaAziendaUtilValidazionePage&PRGMOVIMENTOAPP=" + 
  				document.Frm1.PRGMOVIMENTOAPP.value + "&CDNFUNZIONE=" + document.Frm1.CDNFUNZIONE.value +
  				"&FLGDISTAZESTERA=" + flagAziEstera + 	  
  				"&FUNZ_AGGIORNAMENTO=aggiornaLuogoDiLavoro&CODTIPOTRASF=" + codTipoTrasf;
  				if (document.Frm1.CURRENTCONTEXT != null) {
      				f = f + "&context=" +  document.Frm1.CURRENTCONTEXT.value;
    			}
  				var t = "_blank";
  				var feat = "toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,width=850,height=600,top=0,left=100";
  				openedAzUtil = window.open(f, t, feat);
  	} 
  	else {
  		if (document.Frm1.MODIFICATAAZIENDA.value != null &&
      		document.Frm1.MODIFICATAAZIENDA.value != "" &&
      		document.Frm1.MODIFICATAAZIENDA.value == "true") {
      		apriAzUtil();
  		} else {
	  		var f = "AdapterHTTP?PAGE=MovCercaAziendaUtilValidazionePage&PRGMOVIMENTOAPP=" + 
	  				document.Frm1.PRGMOVIMENTOAPP.value + "&CDNFUNZIONE=" + document.Frm1.CDNFUNZIONE.value +
	  				"&FLGDISTAZESTERA=" + flagAziEstera + 	  
	  				"&FUNZ_AGGIORNAMENTO=aggiornaLuogoDiLavoro";
	  				if (document.Frm1.CURRENTCONTEXT != null) {
       					f = f + "&context=" +  document.Frm1.CURRENTCONTEXT.value;
      				}	
	  		var t = "_blank";
	  		var feat = "toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,width=850,height=600,top=0,left=100";
	  		openedAzUtil = window.open(f, t, feat);
  		}
	}
}

function apriAzUtilDistaccata() {
  var prgAziendaUtilLocal = document.Frm1.PRGAZIENDAUTILIZ.value;
  var prgUAziendaUtilLocal = document.Frm1.PRGUNITAUTILIZ.value;
  var strRagioneSocialeAzUtil = document.Frm1.strRagioneSocialeAzUtil.value;
  var strIndirizzoUAzUtil = document.Frm1.strIndirizzoUAzUtil.value;
  var strComuneUAzUtil = document.Frm1.strComuneUAzUtil.value;
  var flagAziEstera = document.Frm1.FLGDISTAZESTERA.value;
  
  var f = "AdapterHTTP?PAGE=MovCercaAziendaUtilPage&PRGAZIENDAUTIL=" + prgAziendaUtilLocal + 
  "&PRGUNITAUTIL=" + prgUAziendaUtilLocal + "&FUNZ_AGG=aggiornaAziendaUtil" +
  "&canModify=" + canModify +
  "&FLGDISTAZESTERA=" + flagAziEstera +
  "&strRagioneSocialeAzUtil=" + strRagioneSocialeAzUtil + 
  "&context=" +  document.Frm1.CURRENTCONTEXT.value + "&strIndirizzoAziendaUtil="+ strIndirizzoUAzUtil +"&strComuneAziendaUtil=" + strComuneUAzUtil +
  "&CODTIPOTRASF=" + document.Frm1.CODTIPOTRASF.value;
  var t = "_blank";
  var feat = "toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,width=850,height=600,top=0,left=100";
  openedAzUtil = window.open(f, t, feat);
}  

//Apre la pop-up di gestione dell'azienda utilizzatrice in validazione, visto che in questo contesto devo fare 
//una gestione particolare sull'azienda la pop-up è collegata ad una pagina differente
function apriAzUtilDistaccataValidazione() {
	var codTipoTrasf = document.Frm1.CODTIPOTRASF.value;
	var flagAziEstera = document.Frm1.FLGDISTAZESTERA.value;
 	var f = "AdapterHTTP?PAGE=MovCercaAziendaUtilValidazionePage&PRGMOVIMENTOAPP=" + 
  	document.Frm1.PRGMOVIMENTOAPP.value + "&CDNFUNZIONE=" + document.Frm1.CDNFUNZIONE.value +
  	"&FLGDISTAZESTERA=" + flagAziEstera + 	  
  	"&FUNZ_AGGIORNAMENTO=aggiornaLuogoDiLavoro&CODTIPOTRASF=" + codTipoTrasf;
  	if (document.Frm1.CURRENTCONTEXT != null) {
      	f = f + "&context=" +  document.Frm1.CURRENTCONTEXT.value;
    }
  	var t = "_blank";
  	var feat = "toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,width=850,height=600,top=0,left=100";
  	openedAzUtil = window.open(f, t, feat);
}

//funzione per l'aggiornamento del luogo di lavoro e il refresh della pagina
function aggiornaLuogoDiLavoro(luogoDiLavoro,prgAzUtil, prgUnAzUtil) {
	document.Frm1.STRLUOGODILAVORO.value = luogoDiLavoro;
	if (prgAzUtil != null && prgUnAzUtil != null) {
	    if (document.Frm1.PRGAZIENDAUTILIZ != null) {
			document.Frm1.PRGAZIENDAUTILIZ.value = prgAzUtil;
		}
	    if (document.Frm1.PRGUNITAUTILIZ != null) {
		 	document.Frm1.PRGUNITAUTILIZ.value = prgUnAzUtil;
		}
		if (document.Frm1.STRAZINTNUMCONTRATTO != null) {
			document.Frm1.STRAZINTNUMCONTRATTO.value = openedAzUtil.document.Frm1.STRAZINTNUMCONTRATTO.value;
		}
		if (document.Frm1.DATAZINTINIZIOCONTRATTO != null) {
			document.Frm1.DATAZINTINIZIOCONTRATTO.value = openedAzUtil.document.Frm1.DATAZINTINIZIOCONTRATTO.value;
		}
		if (document.Frm1.DATAZINTFINECONTRATTO != null) {
			document.Frm1.DATAZINTFINECONTRATTO.value = openedAzUtil.document.Frm1.DATAZINTFINECONTRATTO.value;
		}
		if (document.Frm1.STRAZINTRAP != null) {
			document.Frm1.STRAZINTRAP.value = openedAzUtil.document.Frm1.STRAZINTRAP.value;
		}
		if (document.Frm1.NUMAZINTSOGGETTI != null) {
			document.Frm1.NUMAZINTSOGGETTI.value = openedAzUtil.document.Frm1.NUMAZINTSOGGETTI.value;
		}
		if (document.Frm1.NUMAZINTDIPENDENTI != null) {
			document.Frm1.NUMAZINTDIPENDENTI.value = openedAzUtil.document.Frm1.NUMAZINTDIPENDENTI.value;
		}
		if (document.Frm1.strRagioneSocialeAzUtil != null) {
			document.Frm1.strRagioneSocialeAzUtil.value = openedAzUtil.document.Frm1.strRagioneSocialeAzUtil.value;
		}
		if (document.Frm1.strIndirizzoUAzUtil != null) {
			document.Frm1.strIndirizzoUAzUtil.value = openedAzUtil.document.Frm1.strIndirizzoUAzUtil.value;
		}
		if (document.Frm1.strComuneUAzUtil != null) {
			document.Frm1.strComuneUAzUtil.value = openedAzUtil.document.Frm1.strComuneUAzUtil.value;
		}
	}
	
	document.Frm1.STRLUOGODILAVORO.readOnly = true;
	if ((document.Frm1.PRGAZIENDAUTILIZ.value != "") && (document.Frm1.PRGUNITAUTILIZ.value != "")) {
  		document.getElementById("DettaglioAziendaUtil").style.display = "none";
  		document.getElementById("DettaglioAziUtil").style.display = "";
  		document.getElementById("DeleteAziUtil").style.display = "";
  	}
	//aggiornaDettaglioGenerale();
}

//azzera l'azienda utilizzatrice, se confirm è true viene chiesta conferma all'utente, se è false no.
function azzeraAziendaUtil(askConfirm) { 
  if ((strNotNull(document.Frm1.PRGAZIENDAUTILIZ.value) != "") && 
	(strNotNull(document.Frm1.PRGUNITAUTILIZ.value) != "")) {
	  var ris = true;
	  if (askConfirm) {
	  	ris = confirm("Si desidera procedere con lo scollegamento dell'azienda utilizzatrice?");
	  }
	  if (ris){
	  	document.Frm1.PRGAZIENDAUTILIZ.value = "";
	    document.Frm1.PRGUNITAUTILIZ.value = "";
	    document.Frm1.STRLUOGODILAVORO.value = "";
	    document.Frm1.strRagioneSocialeAzUtil.value = "";
	    document.Frm1.STRAZINTNUMCONTRATTO.value = "";
	    document.Frm1.DATAZINTINIZIOCONTRATTO.value = "";
	    document.Frm1.DATAZINTFINECONTRATTO.value = "";
	    document.Frm1.STRAZINTRAP.value = "";
	    document.Frm1.NUMAZINTSOGGETTI.value = "";
	    document.Frm1.NUMAZINTDIPENDENTI.value = "";
	    document.Frm1.strIndirizzoUAzUtil.value = "";
	    document.Frm1.strComuneUAzUtil.value = "";
	    //Imposto il luogo di lavoro con i dati dell'azienda principale, se ci sono
	    if ((strNotNull(document.Frm1.PRGAZIENDA.value) != "") && (strNotNull(document.Frm1.PRGUNITA.value) != "")) {
	  		document.Frm1.STRLUOGODILAVORO.value = document.Frm1.strRagioneSocialeAz.value + " - " 
	  										 	+ document.Frm1.strIndirizzoUAzVisualizzato.value;  
	  	}  
	    document.Frm1.STRLUOGODILAVORO.readOnly = false;
	    document.getElementById("DettaglioAziendaUtil").style.display = "";
		document.getElementById("DettaglioAziUtil").style.display = "none";
  		document.getElementById("DeleteAziUtil").style.display = "none";
  	}
  }
}

//funzione per la selezione dell'azienda utilizzatrice con pulsante di lookup
function aggiornaAziendaUtil() {
  document.Frm1.PRGAZIENDAUTILIZ.value = openedAzUtil.dati.prgAziendaUtil;
  document.Frm1.PRGUNITAUTILIZ.value = openedAzUtil.dati.prgUnitaUtil;
  document.Frm1.STRAZINTNUMCONTRATTO.value = openedAzUtil.dati.numContratto;
  document.Frm1.DATAZINTINIZIOCONTRATTO.value = openedAzUtil.dati.dataInizio;
  document.Frm1.DATAZINTFINECONTRATTO.value = openedAzUtil.dati.dataFine;
  document.Frm1.STRAZINTRAP.value = openedAzUtil.dati.legaleRapp;
  document.Frm1.NUMAZINTSOGGETTI.value = openedAzUtil.dati.numSoggetti;
  document.Frm1.NUMAZINTDIPENDENTI.value = openedAzUtil.dati.classeDip; 
  document.Frm1.strRagioneSocialeAzUtil.value = openedAzUtil.dati.strRagioneSociale;
  document.Frm1.strIndirizzoUAzUtil.value = openedAzUtil.dati.strIndirizzoAzienda;
  document.Frm1.strComuneUAzUtil.value = openedAzUtil.dati.comuneAzienda;    
  document.Frm1.STRLUOGODILAVORO.value = document.Frm1.strRagioneSocialeAzUtil.value + " - " 
  										+ document.Frm1.strIndirizzoUAzUtil.value + " (" 
  										+ document.Frm1.strComuneUAzUtil.value + ")";
  document.Frm1.STRLUOGODILAVORO.readOnly = true;
  if ((document.Frm1.PRGAZIENDAUTILIZ.value != "") && (document.Frm1.PRGUNITAUTILIZ.value != "")) {
  	document.getElementById("DettaglioAziendaUtil").style.display = "none";
  	document.getElementById("DettaglioAziUtil").style.display = "";
  	document.getElementById("DeleteAziUtil").style.display = "";
  }
  
  aziendaUtilVar = "S";
  openedAzUtil.close();
  //Deseleziono checkbox personale interno
  if (document.Frm1.FLGINTERASSPROPRIACHECKBOX.checked && document.Frm1.CODTIPOTRASF.value != 'DL') {
  	document.Frm1.FLGINTERASSPROPRIACHECKBOX.checked = false;
  	document.Frm1.FLGINTERASSPROPRIACHECKBOX.click;
  	if (document.Frm1.CODTIPOAZIENDA.value == codInterinale){
 		visualizzaCampi("aziSomm","");
    	visualizzaCampi("visDateMissione","");
    	visualizzaCampi("sezTipoVariazione","");
    } else {
    	visualizzaCampi("aziSomm","none");
    	visualizzaCampi("visDateMissione","none");
    	visualizzaCampi("sezTipoVariazione","none");
    }	
  }
  valorizzaHid(document.Frm1.FLGINTERASSPROPRIACHECKBOX);
}