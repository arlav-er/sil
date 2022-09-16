<script language="JavaScript">

function controllaIndennita(campo, elem1) {
      var obj1 = document.getElementById(elem1);
      if (campo.value != "" && campo.value == "S") {
        if (document.Frm1.datInizio.value != "" && document.Frm1.datFine.value != "") {
        	document.Frm1.dataInizioIndenn.value = document.Frm1.datInizio.value;
        	document.Frm1.dataFineIndenn.value = document.Frm1.datFine.value;
      	}
      	obj1.style.display = "inline";
      }
      else {
      	document.Frm1.dataInizioIndenn.value = "";
      	document.Frm1.dataFineIndenn.value = "";
      	obj1.style.display = "none";     	
      }
}

function controllaCasoDubbio() {
	var objCasoDubbio = document.Frm1.casoDubbio;
	if (objCasoDubbio != undefined) {
		var flagVarCasoDubbio = document.Frm1.casoDubbio.value;
		if (flagVarCasoDubbio != "" && flagVarCasoDubbio == "S") {
			var objMotLabel = document.getElementById('tr_mot_sintetico_lb');
			objMotLabel.style.display = "inline";
			var objMotField = document.getElementById('tr_mot_sintetico_fd');
			objMotField.style.display = "inline";
		}
	}
}

function controllaMotCasoDubbio(campo, elemMotLabel, elemMotField) {
	var obj1 = document.getElementById(elemMotLabel);
	var obj2 = document.getElementById(elemMotField);
    if (campo.value != "" && campo.value == "S") {
		obj1.style.display = "inline";
		obj2.style.display = "inline";
    }
   	else {
  		document.Frm1.strDescCasoDubbio.value = "";
  		obj1.style.display = "none";
  		obj2.style.display = "none";
   	}
}

function controlloMobilita() {
	if (document.Frm1.PRGAZIENDA.value == "") {
		alert("Attenzione! Bisogna scegliere l'azienda.");
      	return false;
	}
	
	if (document.Frm1.flgIndennita.value == "S") {
		if (document.Frm1.dataInizioIndenn.value == "" || document.Frm1.dataFineIndenn.value == "") {
      		alert("Attenzione! Bisogna valorizzare data inizio e data fine indennità.");
      		return false;
      	}
      	else {
      		if (compDate(document.Frm1.dataFineIndenn.value, document.Frm1.dataInizioIndenn.value) < 0) {
				alert("Attenzione! Data fine indennità deve essere maggiore o uguale della data inizio indennità.");
		      	return false;	
			}
			if (compDate(document.Frm1.dataFineIndenn.value, document.Frm1.datMaxDiff.value) > 0) {
				alert("Attenzione! Data fine indennità deve essere minore o uguale della data max differimento.");
		      	return false;	
			}
      	}
	}
	
	if (document.Frm1.provCRT.value != "" && document.Frm1.regioneCRT.value != "") {
		alert("Attenzione! Bisogna indicare come ente la regione o la provincia.");
      	return false;
	}
	
	if (compDate(document.Frm1.datFineMov.value, document.Frm1.datInizMov.value) < 0) {
		alert("Attenzione! Data fine movimento deve essere maggiore o uguale della data inizio movimento.");
      	return false;	
	}
	
	if (compDate(document.Frm1.datMaxDiff.value, document.Frm1.datFine.value) < 0) {
		alert("Attenzione! Data max differimento deve essere maggiore o uguale della data fine mobilità.");
      	return false;	
	}
	
	document.Frm1.datInizMovHid.value = document.Frm1.datInizMov.value;
	document.Frm1.datFineMovHid.value = document.Frm1.datFineMov.value;
	document.Frm1.datInizioHid.value = document.Frm1.datInizio.value;
	document.Frm1.codGradoHid.value = document.Frm1.codGrado.value;
	
	if (document.Frm1.flagDisponibilita != null) {
		if (document.Frm1.flagDisponibilita.checked) {
			document.Frm1.flagDisponibilita.value = "S";	
		}
		else {
			document.Frm1.flagDisponibilita.value = "";
			if (document.Frm1.FLGSCHEDA.value == "S") {
				if (confirm("Verranno cancellate le disponibilità inserite, continuare ?")) {
					return true;
				}
				else {
					return false;
				}
			}
		}
	}
	
	if (document.Frm1.MotDecad.value != "" && document.Frm1.MotDecad.value == "Z2") {
		if (confirm("Vuoi gestire manualmente questo motivo decadenza?")) {
			return true;
		}
		else {
			return false;
		}
	}
		
	return true;
}

function ricercaAvanzataMansioni() {
  	window.open("AdapterHTTP?PAGE=RicercaMansioneAvanzataPage", "Mansioni", 'toolbar=0, scrollbars=1,height=600, width=800');
}

function selectMansioneOnClick(codMansione, codMansioneHid, descMansione, strTipoMansione) {	
  	if (codMansione.value==""){
    	descMansione.value="";
    	strTipoMansione.value="";      
  	}
  	else {
  		window.open("AdapterHTTP?PAGE=RicercaMansionePage&codMansione="+codMansione.value, "Mansioni", 'toolbar=0, scrollbars=1');     
  	}
}
//Ricerca della mansione per descrizione
function selectMansionePerDescrizione(desMansione) {
	window.open("AdapterHTTP?PAGE=RicercaMansionePage&desMansione="+desMansione.value, "Mansioni", 'toolbar=0, scrollbars=1');          
}


function ricercaAvanzataCCNL() {
	window.open("AdapterHTTP?PAGE=RicercaCCNLAvanzataPage", "CCNL", 'toolbar=0, scrollbars=1, height=300, width=550');
}      

function caricaGestioneSchedaInformativa(prg) {
	var url = "AdapterHTTP?PAGE=MobGestioneDisponibilitaPage";
	url += "&PRGMOBILITAISCR="+prg;
	window.open(url, "Disponibilità", 'toolbar=0, scrollbars=1, height=500, width=550');
}

function gestioneBtnSchedaInformativa(campo) {
	var objBtnScheda = document.getElementById('sezione_scheda_informativa');
	if (campo.checked) {
		objBtnScheda.style.display = "inline";
	}
	else {
		objBtnScheda.style.display = "none";
	}
}

function toDate(newDate) {
    var tokens = newDate.split('/');
    var usDate= tokens[2]+"/"+tokens[1]+"/"+tokens[0];
    return new Date(usDate);
}

function compDate(date1,date2) {
    if (date1==null || date1=="") return -1;    
    if (date2==null || date2=="") return 1;
    if ( toDate(date1).getTime()<toDate(date2).getTime()) return -1;
    if ( toDate(date1).getTime()>toDate(date2).getTime()) return 1;
    return 0;
}

function isDateParam(dateStr) {
	var datePat = /^(\d{1,2})(\/|-)(\d{1,2})(\/|-)(\d{4})$/;
    var matchArray = dateStr.match(datePat); // is the format ok?

    if (matchArray == null) {
      return false;
    }

    month = matchArray[3]; // p@rse date into variables
    day = matchArray[1];
    year = matchArray[5];
   	
    if (month < 1 || month > 12) { // check month range
      return false;
    }

    if (day < 1 || day > 31) {
      return false;
    }

    if ((month==4 || month==6 || month==9 || month==11) && day==31) {
      return false;
    }

    if (month == 2) { // check for february 29th
      var isleap = (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0));
      if (day > 29 || (day==29 && !isleap)) {
        return false;
      }
    }
    return true; // date is valid
}

function gestioneDateMobilita(cdnLavoratore) {
	if (document.Frm1.PRGAZIENDA.value == "") {
		alert("Attenzione! Bisogna scegliere l'azienda.");
	}
	else {
		if (document.Frm1.datFineMov.value == "" || document.Frm1.datInizMov.value == "") {
			alert("Attenzione! Bisogna indicare il periodo lavorativo dal al.");
		}
		else {
			if (!checkFormatDate(document.Frm1.datInizMov)) {alert('Data non corretta nel campo ' + document.Frm1.datInizMov.title); return;}
			if (!checkFormatDate(document.Frm1.datFineMov)) {alert('Data non corretta nel campo ' + document.Frm1.datFineMov.title); return;}
			if (!isDateParam(document.Frm1.datInizMov.value) || !isDateParam(document.Frm1.datFineMov.value)) {
				alert("Attenzione! Data inizio e/o fine movimento non valida. Formato deve essere gg/mm/yyyy.");
			}
			else {
				var url = "AdapterHTTP?PAGE=MobGestioneDatePage";
				var dataFineMov = document.Frm1.datFineMov.value;
				url += "&DATFINEMOV="+dataFineMov;
				url += "&PRGAZIENDA="+document.Frm1.PRGAZIENDA.value;
				url += "&PRGUNITA="+document.Frm1.PRGUNITA.value;
				url += "&cdnLavoratore="+cdnLavoratore;
				url += "&CODCOM="+document.Frm1.CODCOM.value;
				window.open(url, "Date", 'toolbar=0, scrollbars=1, height=300, width=400');	
			}
		}
	}
}

function visPaginaEventiScorrimento(cdnLavoratore) {
	var url = "AdapterHTTP?PAGE=MobEventiScorrimentoPage";
	url += "&CDNLAVORATORE="+cdnLavoratore;
	url += "&DATINIZIOMOB="+document.Frm1.datInizio.value;
	if (document.Frm1.datMaxDiff.value != "") {
		if (isDateParam(document.Frm1.datMaxDiff.value)) {
			url += "&DATMAXDIFF="+document.Frm1.datMaxDiff.value;
			window.open(url, "Eventi", 'toolbar=0, scrollbars=1, height=500, width=600');	
		}
		else {
			alert("Attenzione! Data max differimento non valida. Formato deve essere gg/mm/yyyy.");
		}
	}
	else {
		alert("Attenzione! Valorizzare data max differimento.");
	}
}
     
</script>
<SCRIPT language="JavaScript" src="../../js/Function_CommonRicercaCCNL.js"></SCRIPT>