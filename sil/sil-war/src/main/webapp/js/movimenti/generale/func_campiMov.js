var finestraApprendistato;
var finestraTirocinio;

//Funzione di segnalazione per la scelta delle mansioni
function checkMansione(nameMansione) {
	var cod = new String(eval('document.Frm1.' + nameMansione + '.value'));
	if (cod.length == 7) {
		if (cod.substring(5, 7) == '00') {
			if (confirm("Non è stata indicata una mansione specifica. Continuare?")) {
				return controllaQualificaOnSubmit(nameMansione);
			}
		}
		else {
			return controllaQualificaOnSubmit(nameMansione);
		}
	}	
	else {
		if (cod.substring(4, 6) == '00') {
			if (confirm("Non è stata indicata una mansione specifica. Continuare?")) {
				return controllaQualificaOnSubmit(nameMansione);
			}
		} else return controllaQualificaOnSubmit(nameMansione);
	}
}

//abilita e disabilita la scadenza
function visualizzaScadenza(tempo) {
  var tempovalue = tempo.options[tempo.selectedIndex].value;
  var scadenza = document.getElementById("scadenza");
  var datascadenza = document.getElementById("datascadenza");
  var calcDataFine = document.getElementById("calcDataFine");
  
  if (tempovalue == "I" || tempovalue == "" ) {
    //document.Frm1.datFineMov.value = "";
    //scadenza.style.display = "none";
    //datascadenza.style.display = "none";
    //calcDataFine.style.display = "none";
  }
  else {
    //scadenza.style.display="";
    //datascadenza.style.display = "";
    //calcDataFine.style.display = "";
    if(document.Frm1.CODTIPOMOV.value == 'TRA' && (document.Frm1.CODTIPOTRASF.value == 'DI' || 
       document.Frm1.CODTIPOTRASF.value == "AI" || document.Frm1.CODTIPOTRASF.value == "FI" 
       || document.Frm1.CODTIPOTRASF.value == "II") ) {
    	document.Frm1.CODTIPOTRASF.value = "";
    }
  }   
}

function getTipoOrario(codice) {
	var ret = "";
	var codiceCurr = "";
	var i = 0;
	if(vettCodiceOrario != "") {
		for (;i<vettCodiceOrario.length;i++) {
			codiceCurr = vettCodiceOrario[i];
			if (codice == codiceCurr) {
				ret = vettTipoOrario[i];
				break;
			}	
		}
	}
	return ret;
}

//abilita e disabilita le ore settimanali
function visualizzaOreSett(orario) {
  var orariovalue = orario.options[orario.selectedIndex].value;
  var labelore = document.getElementById("labelore");
  var numore = document.getElementById("numore");
  var tipoOrario = getTipoOrario(orariovalue);
  if (orariovalue== "" || tipoOrario == "T" || tipoOrario == "N") {
    document.Frm1.numOreSett.value = "";
    labelore.style.display = "none";
    numore.style.display = "none";
    if(document.Frm1.CODTIPOMOV.value == 'TRA' && document.Frm1.CODTIPOTRASF.value == 'TP') {
    	document.Frm1.CODTIPOTRASF.value = "";	
    }
  }
  else {
    labelore.style.display = "";
    numore.style.display = "";
    if(document.Frm1.CODTIPOMOV.value == 'TRA' && document.Frm1.CODTIPOTRASF.value == 'PP') {
    	document.Frm1.CODTIPOTRASF.value = "";	
    }
  }   
}


//Controlla che il campo data fine non sia nullo in caso di tempo determinato
function checkDataFineTD() 
{ 
  var codMonoTempo = document.Frm1.codMonoTempo.value;
  var datFineMov = document.Frm1.datFineMov.value;
  var codTipoMov = document.Frm1.CODTIPOMOV.value;
  if(codMonoTempo == "D" && datFineMov == "" && codTipoMov != 'CES') {
  	alert("In caso di tempo determinato occore specificare la data fine");
	return false;
  }
  return true;
}


//Controlla che il campo numOreSettimanali non sia nullo in caso di part-time
function checkNumOreSettimanali() {
	var orarioValue = document.Frm1.codOrario.value;
  	var tipoOrario = getTipoOrario(orarioValue);
  	if( (tipoOrario == "P") && (document.Frm1.numOreSett.value == null || document.Frm1.numOreSett.value == "") ) { 
  		alert("In caso di part time occore specificare il numero di ore settimanali");
		return false;
  	}
 	//CONTROLLO POST FASE 3 : Togliere controllo di coerenza tipo orario N / tipo contratto
 	//if (tipoOrario == "N") {
 	//	var tipoContratto = document.Frm1.codTipoAss.value;
 	//	if (tipoContratto != 'B.01.00' &&
 	//	    tipoContratto != 'B.02.00' &&
  	//    	tipoContratto != 'B.03.00' &&
 	//	    tipoContratto != 'C.01.00' &&
 	//	    tipoContratto != 'C.02.00' &&
 	//	    tipoContratto != 'L.01.00' &&
 	//	    tipoContratto != 'L.01.01' &&
 	//	    tipoContratto != 'M.01.00' &&
 	//	    tipoContratto != 'M.01.01') {
 	//		alert("Il codice relativo al tipo di contratto non compatibile con l'orario");
 	//		return false;  
 	//	}
 	//}
  	return true;
}


function checkAvvAgr(){
	var lavoroAgricoltura = "";
	var giorni = "";
	var lavorazione ="";
	if (document.Frm1.codTipoAss.value == "H.01.00" && dataLavAgric != "" 
		&& compareDate(dataOdierna,dataLavAgric) < 0) {
			if(document.Frm1.CODTIPOMOV.value == "AVV" && (document.Frm1.CODLAVORAZIONE.value == "" || document.Frm1.NUMGGPREVISTIAGR.value == "" ) ) {
				if (codRegioneProv != codRegioneCalabria) {
					alert("I campi Giorni presunti in agric. e Lavorazione sono obbligatori");
					return false;
				}
			}
			if(document.Frm1.CODTIPOMOV.value == "CES" && (document.Frm1.CODLAVORAZIONECES.value == "" || document.Frm1.NUMGGEFFETTUATIAGR.value == "" ) ) {
				if (codRegioneProv != codRegioneCalabria) {
					alert("I campi Giorni effettivi in agric. e Lavorazione sono obbligatori");
					return false;
				}
			}
	} else {
		if(dataLavAgric != "" && compareDate(dataOdierna,dataLavAgric) > 0) {
			document.Frm1.FLGLAVOROAGR.value="N";
			if(document.Frm1.CODTIPOMOV.value != "CES") {
				lavoroAgricoltura = document.Frm1.lavAgr;
				giorni = document.Frm1.NUMGGPREVISTIAGR.value;
				lavorazione = document.Frm1.CODLAVORAZIONE.value;
			}	
			if(lavoroAgricoltura.checked){
				document.Frm1.FLGLAVOROAGR.value="S";
				if(document.Frm1.codMonoTempo.value == 'D' && giorni == "") {
					if (codRegioneProv != codRegioneCalabria) {
						alert("Il campo Giorni presunti in agric. è obbligatorio");
					    return false;
					}
				}
			}
			if( (giorni != "" || lavorazione != "") && document.Frm1.codMonoTempo.value == 'D' && !lavoroAgricoltura.checked
				&& document.Frm1.codTipoAss.value != "H.01.00" ) {
				if (codRegioneProv != codRegioneCalabria) {
					alert("E' necessario indicare il flag Lavoro in agric.");
					return false;
				}
				else {
					document.Frm1.NUMGGPREVISTIAGR.value = "";
					document.Frm1.CODLAVORAZIONE.value = "";
				}
			}
		}
	}
	return true;
}

function checkCampiAziSomm(){
	var flagPerInt = document.getElementById("flagPersonaleInterno").checked;
		if(document.Frm1.CODTIPOAZIENDA.value == "INT" && !flagPerInt){
			if(document.Frm1.strMatricola.value=="" && document.Frm1.CODTIPOMOV.value == "AVV") {
				alert("Il campo Matricola è obbligatorio");
				return false;
			}
			if( document.Frm1.CODTIPOMOV.value == "AVV" && (document.Frm1.strRagioneSocialeAzUtil.value == "" && document.Frm1.DATINIZIORAPLAV.value!= "" )
			 	|| (document.Frm1.strRagioneSocialeAzUtil.value != "" && document.Frm1.DATINIZIORAPLAV.value== "" )) {
					alert("L'azienda utilizzatrice e i dati di missione sono obbligatori.");
					return false;	 
			}
			if(document.Frm1.codMonoTempo.value == "D" && document.Frm1.DATFINERAPLAV == "") {
  				alert("In caso di tempo determinato occore specificare la data fine missione");
				return false;
			}
		}
		else {
			if (document.Frm1.CODMANSIONE.value == "") {
				alert("Il campo Mansione è obbligatorio");
				return false;
			}
			if (document.Frm1.codOrario.value == "") {
				alert("Il campo Orario è obbligatorio");
				return false;
			} 					
		}
	return true;
}

//CONTROLLO POST FASE 3 : eliminare controllo ccnl + livello o compenso obbligatorie
//veniva chiamata nella funzione invia di : InserisciMovimento.jsp, ValidaDettaglioMovimento.jsp 
//e RettificaMovimento.jsp 
//function checkCompensoContrattoLivello() {
//	var livello="";
//	var flagPerInt = document.getElementById("flagPersonaleInterno").checked;
//	if ( (document.Frm1.CODTIPOAZIENDA.value == "INT" && flagPerInt) || (document.Frm1.CODTIPOAZIENDA.value != "INT") ) {
//		if(document.Frm1.CODTIPOMOV.value == "CES") {
//			livello = document.Frm1.numLivelloCes.value;
//		} else { 
//			livello = document.Frm1.numLivello.value;
//		} 
//		if( (document.Frm1.codCCNL.value == '' && livello== '' && document.Frm1.decRetribuzioneAnn.value == '') 
//			 || ( document.Frm1.decRetribuzioneAnn.value == '' && ( (document.Frm1.codCCNL.value != '' && livello=='') || (document.Frm1.codCCNL.value == '' && livello!='')) ) ) {
//   		alert("I campi CCNL e Livello oppure il campo Compenso sono obbligatori");
//    		return false;
//    	}
//    }
//    return true;
//}


//Ricerca della mansione per descrizione
function selectMansionePerDescrizione(desMansione) {
	window.open("AdapterHTTP?PAGE=RicercaMansionePage&desMansione="+desMansione.value+"&flgFrequente=", "Mansioni", 'toolbar=0, scrollbars=1');          
}

//ricerca delle mansioni con pulsante di lookup
function selectMansioneOnClick(codMansione, codMansioneHid, descMansione, strTipoMansione) {	
  if (codMansione.value==""){
    descMansione.value="";
    strTipoMansione.value="";      
  }
  else if (codMansione.value!=codMansioneHid.value){
  window.open("AdapterHTTP?PAGE=RicercaMansionePage&codMansione="+codMansione.value, "Mansioni", 'toolbar=0, scrollbars=1');     
  }
}

//ricerca avanzata mansioni    
function ricercaAvanzataMansioni() {
  window.open("AdapterHTTP?PAGE=RicercaMansioneAvanzataPage", "Mansioni", 'toolbar=0, scrollbars=1');
}

//ricerca avanzata CCNL
function ricercaAvanzataCCNL() {
  //Giovanni D'Auria 25/02/2005
  var t="AdapterHTTP?PAGE=RicercaCCNLAvanzataPage";
  var tipoAvviamento = document.Frm1.CODMONOTIPO.value;
  if (tipoAvviamento == "A") {
  	t += "&apprendistato=";
  }
  //fine
  window.open(t, "CCNL", 'toolbar=0, scrollbars=1, height=300, width=550');
}

//trasforma in tutto maiuscolo il codice del CCNL
function codCCNLUpperCase(inputName){
  var ctrlObj = eval("document.forms[0]." + inputName);
  eval("document.forms[0]."+inputName+".value=document.forms[0]."+inputName+".value.toUpperCase();");
  return true;
}

function valorizzaCampiData(){
  document.Frm1.datFineMov.value = finestraAperta.document.Form1.dataFine.value;
  finestraAperta.close();
}
    
function apriSetDataFine(){
  if(document.Frm1.datInizioMov.value != ""){      
    var f = "AdapterHTTP?PAGE=MovSceltaDataFine&funzioneDiAggiornamento=valorizzaCampiData&dataInizio=" + document.Frm1.datInizioMov.value;
    var t = "_blank";
    var feat = "toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,width=500,height=200,top=250,left=350";
    finestraAperta = window.open(f, t, feat);
  }
}

function resettaCampiTirocinio () {
	document.Frm1.CODSOGGPROMOTOREMIN.value = "";
	document.Frm1.CODCATEGORIATIR.value = "";
	document.Frm1.CODTIPOLOGIATIR.value = "";
	document.Frm1.STRDENOMINAZIONETIR.value = "";
	document.Frm1.CODTIPOENTEPROMOTORE.value = "";
	document.Frm1.STRCODFISCPROMOTORETIR.value = "";
}

function visualizzaPulsanteApprendistato(codMonoTipo, flgContrattoTI){
	var obj = document.getElementById("appButton");
	var objPF = document.getElementById("visDataFinePF");
	var objTir = document.getElementById("appButtonTirocinio");
	var objEnteTirocinio;
	//if (configEntePromotore == "1") {
	//	objEnteTirocinio = document.getElementById("enteTirocinio");
	//}
	if (codMonoTipo == "A") {
		obj.style.display = "";
		if (flgContrattoTI == "S") {
			objPF.style.display = "";
		}
		else {
			objPF.value = "";
			objPF.style.display = "none";
		}	
		objTir.style.display = "none";
		resettaCampiTirocinio();
		//if (canModify == "true" && configEntePromotore == "1") {
		//	var optEnte = document.Frm1.codTipoEntePromotore.options;
		//	for (var i=0; i < optEnte.length; i++) {
		//		if (optEnte[i].value == "") {
		//			optEnte[i].selected = true;
		//		}	
		//	}
		//}
		//if (configEntePromotore == "1") {
		//	objEnteTirocinio.style.display = "none";
		//}
	} else {
  		obj.style.display = "none";
  		objPF.value = "";
  		objPF.style.display = "none";
  		objTir.style.display = "none";
  		if (compareDate(dataOdierna,dataTirocinio) >= 0) {
  			if (codMonoTipo == "T" && document.Frm1.codTipoAss.value != 'NB5') {
  				if (document.Frm1.codTipoAss.value != "C.04.00" && document.Frm1.codTipoAss.value != "c.04.00" &&
  					document.Frm1.codTipoAss.value != "C.03.00" && document.Frm1.codTipoAss.value != "c.03.00") {
  					objTir.style.display = "";
  					//if (configEntePromotore == "1") {
  					//	objEnteTirocinio.style.display = "";
  	      			//}
  				}
      		}
      		else {
      			objTir.style.display = "none";
      			resettaCampiTirocinio();
      			//if (canModify == "true" && configEntePromotore == "1") {
      			//	var optEnte = document.Frm1.codTipoEntePromotore.options;
      			//	for (var i=0; i < optEnte.length; i++) {
      			//  		if (optEnte[i].value == "") {
      			//  			optEnte[i].selected = true;
      			//  		}	
      			//  	}
      			//}
      			//if (configEntePromotore == "1") {
      			//	objEnteTirocinio.style.display = "none";
      			//}
      		}
       } 
	}
}

//Aggiorna le combo collegate quando avviene qualche cambiamento nelle combo da cui dipendono
function refreshComboCollegate() {   
  args = '&CODTIPOAZIENDA=' + codTipoAzienda 
  args = args + '&CODMONOTEMPO='+ document.Frm1.codMonoTempo.value;
  args = args + '&CODTIPOASS='+ document.Frm1.codTipoAss.value;      
  refreshCombo('ComboAgevolazioneSelettiva', 'Frm1.codAgevolazione', args);      
}


//Cerca il tipo di assunzione
function cercaTipoContratto(criterio){
  var f;
  var descr;
  var codTipoAzienda = document.Frm1.CODTIPOAZIENDA.value;
  if (codTipoAzienda=="INT" && document.Frm1.FLGINTERASSPROPRIA.value=="S"){
    f = "AdapterHTTP?PAGE=SelezionaContrattiSelettivaPage&CODTIPOAZIENDA=AZI" + "&codMonoTempo=" + document.Frm1.codMonoTempo.value + "&FLGINTERASSPROPRIA=" + document.Frm1.FLGINTERASSPROPRIA.value; 
  }
  else {
  	f = "AdapterHTTP?PAGE=SelezionaContrattiSelettivaPage&CODTIPOAZIENDA=" + codTipoAzienda + "&codMonoTempo=" + document.Frm1.codMonoTempo.value + "&FLGINTERASSPROPRIA=" + document.Frm1.FLGINTERASSPROPRIA.value;
  }
  f = f + "&CRITERIO=" + criterio;
  f = f + "&codTipoAss=" + document.Frm1.codTipoAss.value;
  //se la descrizione ha caratteri speciali la ricerca potrebbe non funzionare
  descr = document.Frm1.descrTipoAss.value;
  descr = descr.replace("%","&amp;");
  f = f + "&descrTipoAss=" + descr;
  f = f + "&updateFunctionName=aggiornaTipoContratto";
  f = f + "&codCCNLAz=" + document.Frm1.codCCNL.value;
  
  var t = "_blank";
  var feat = "toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,width=500,height=400,top=100,left=100";
  window.open(f, t, feat);
} 

 //Aggiorna il tipo di assunzione e le combo collegate
function aggiornaTipoContratto(codice, descrizione,codmonotipo,codContratto,flgcontrattoti) {
  document.Frm1.codTipoAss.value = codice;
  document.Frm1.descrTipoAss.value = descrizione;
  document.Frm1.CODMONOTIPO.value = codmonotipo;
  document.Frm1.CODCONTRATTO.value = codContratto;
  document.Frm1.FLGCONTRATTOTI.value = flgcontrattoti;
  //refreshComboCollegate();
  visualizzaPulsanteApprendistato(codmonotipo, flgcontrattoti);
  // CM: convenzione
  //abilitazioneConvenzione();
}

//Apre la selezione dell'apprendistato
function apriApprendistato(){
	var f = "AdapterHTTP?PAGE=MovApprendistatoPage";
	
	if(document.Frm1.CURRENTCONTEXT.value != ""){
      f = f + "&CURRENTCONTEXT=" + document.Frm1.CURRENTCONTEXT.value;
    }
    if (document.Frm1.PRGMOVIMENTO != null && document.Frm1.CURRENTCONTEXT.value == "consulta" ){
        f = f + "&PRGMOVIMENTO=" + document.Frm1.PRGMOVIMENTO.value;
    } else if (document.Frm1.PRGMOVIMENTOAPP != null && document.Frm1.CURRENTCONTEXT.value == "valida") {
        f = f + "&PRGMOVIMENTOAPP=" + document.Frm1.PRGMOVIMENTOAPP.value;
    }
    if (document.Frm1.flgCambiamentiDati != null){
      f = f + "&flgCambiamentiDati=" + document.Frm1.flgCambiamentiDati.value;
    }
    if (codTipoAzienda=="INT" && document.Frm1.FLGINTERASSPROPRIA.value=="S"){
      f = f + "&CODTIPOAZIENDA=AZI";
    }
    else {
      f = f + "&CODTIPOAZIENDA=" + document.Frm1.CODTIPOAZIENDA.value;
    }
    f = f  + "&codTipoAss=" + document.Frm1.codTipoAss.value;
    
    f = f + "&updateFunctionName=AggiornaApprendistato";
    f = f + "&strCognomeTutore=" + document.Frm1.STRCOGNOMETUTORE.value;
    f = f + "&strNomeTutore=" + document.Frm1.STRNOMETUTORE.value;
    f = f + "&strCodiceFiscaleTutore=" + document.Frm1.STRCODICEFISCALETUTORE.value;
    f = f + "&codQualificaSrq=" + document.Frm1.CODQUALIFICASRQ.value;
    f = f + "&flgTitolareTutore=" + document.Frm1.FLGTITOLARETUTORE.value;
    f = f + "&numAnniEspTutore=" + document.Frm1.NUMANNIESPTUTORE.value;
    f = f + "&strLivelloTutore=" + document.Frm1.STRLIVELLOTUTORE.value;
    f = f + "&CODMANSIONETUTORE=" + document.Frm1.CODMANSIONETUTORE.value;
    f = f + "&codTipoMov=" + document.Frm1.CODTIPOMOV.value;
    f = f + "&strNote=" + document.Frm1.STRNOTE.value;
    f = f + "&flgArtigiana=" + document.Frm1.FLGARTIGIANA.value;
   	f = f + "&codCCNLAz=" + document.Frm1.codCCNL.value;
   	f = f + "&codRegioneProv=" + codRegioneProv;
	var t = "_blank";
    var feat = "toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,width=800,height=550,top=70,left=100";
    finestraApprendistato = window.open(f, t, feat);
}

//Funzione di aggiornamento valori dell'apprendistato
function AggiornaApprendistato(){
  document.Frm1.STRCOGNOMETUTORE.value = finestraApprendistato.document.Frm1.STRCOGNOMETUTORE.value;
  document.Frm1.STRNOMETUTORE.value = finestraApprendistato.document.Frm1.STRNOMETUTORE.value;
  document.Frm1.STRCODICEFISCALETUTORE.value = finestraApprendistato.document.Frm1.STRCODICEFISCALETUTORE.value;
  document.Frm1.CODQUALIFICASRQ.value = finestraApprendistato.document.Frm1.CODQUALIFICASRQ.value;
  document.Frm1.FLGTITOLARETUTORE.value = finestraApprendistato.document.Frm1.FLGTITOLARETUTORE.value;
  document.Frm1.NUMANNIESPTUTORE.value = finestraApprendistato.document.Frm1.NUMANNIESPTUTORE.value;
  document.Frm1.STRLIVELLOTUTORE.value = finestraApprendistato.document.Frm1.STRLIVELLOTUTORE.value;
  document.Frm1.CODMANSIONETUTORE.value = finestraApprendistato.document.Frm1.CODMANSIONE.value;
  document.Frm1.FLGARTIGIANA.value = finestraApprendistato.document.Frm1.FLGARTIGIANA.value;
  document.Frm1.STRNOTE.value = finestraApprendistato.document.Frm1.STRNOTE.value;
  if(document.Frm1.flgCambiamentiDati != null){
    document.Frm1.flgCambiamentiDati.value = finestraApprendistato.document.Frm1.flgCambiamentiDati.value;
  }
  finestraApprendistato.close();
}

//Controllo che il codTipoAss esista prima dell'invio della form
function checkTipoContratto(nomeTipoContratto) {

	if(document.Frm1.codTipoAss.value!=""){
  	document.Frm1.codTipoAss.value = document.Frm1.codTipoAss.value.toUpperCase();
  	var codTipoAzienda = document.Frm1.CODTIPOAZIENDA.value;
  	var f;
  	var descr;
  	if (codTipoAzienda=="INT" && document.Frm1.FLGINTERASSPROPRIA.value=="S"){
    	f = "AdapterHTTP?PAGE=ControllaTipoContrattoXMLPage&CODTIPOAZIENDA=AZI" + "&codMonoTempo=" + document.Frm1.codMonoTempo.value + "&FLGINTERASSPROPRIA=" + document.Frm1.FLGINTERASSPROPRIA.value;
  	}
  	else {
  		f = "AdapterHTTP?PAGE=ControllaTipoContrattoXMLPage&CODTIPOAZIENDA=" + codTipoAzienda + "&codMonoTempo=" + document.Frm1.codMonoTempo.value + "&FLGINTERASSPROPRIA=" + document.Frm1.FLGINTERASSPROPRIA.value;
  	}
  	f = f + "&CRITERIO=codice";
  	f = f + "&codTipoAss=" + document.Frm1.codTipoAss.value.toUpperCase();
  	descr = document.Frm1.descrTipoAss.value;
  	descr = descr.replace("%","&amp;");
  	f = f + "&descrTipoContratto=" + descr;
  	f = f + "&codCCNLAz=" + document.Frm1.codCCNL.value;
 	var result = syncXMLHTTPGETRequest(f);
   	if (result == null || result.responseXML.documentElement == null || (result.responseXML.documentElement.tagName != "ROW")) {
   		alert("Il codice '"+document.Frm1.codTipoAss.value+"' relativo al 'Tipo di contratto' \n° incompatibile con i dati inseriti nel movimento o errato.");
	    return false;
	} else {
	
	  	return true;
	}
  }
  
}

//function mostraCampiAgricoltura() {
//	if( (document.Frm1.NUMGGPREVISTIAGR.value != null && 
//			(document.Frm1.NUMGGPREVISTIAGR.value > 0 || document.Frm1.NUMGGPREVISTIAGR.value != "") )  ||
//			(document.Frm1.NUMGGEFFETTUATIAGR.value != null && 
//			(document.Frm1.NUMGGEFFETTUATIAGR.value > 0 || document.Frm1.NUMGGEFFETTUATIAGR.value != "") ) ) {
//    	
//    	if(document.Frm1.CODTIPOMOV.value == "CES") {
//    		document.getElementById("lavorazioneCes").style.display = "";
//    		document.getElementById("lavorazione").style.display = "none";
//    	} else {
//    		document.getElementById("lavorazioneCes").style.display = "none";
//    		document.getElementById("lavorazione").style.display = "";
//  		}
//  	 } else {
//     	document.getElementById("lavorazione").style.display = "none";
//     	document.getElementById("lavorazioneCes").style.display = "none";
//  	}
//}

function copiaValue(objDaCopiare, objInCuiCopiare) {
  if(objDaCopiare.value!=null && objInCuiCopiare!=null) {
     //alert("Copia da "+objDaCopiare.name+"  il valore:"+objDaCopiare.value+"\n a "+objInCuiCopiare.name);
     objInCuiCopiare.value = objDaCopiare.value;
  }
}

function controlloCampiAgricoltura() {
	return true;
}

function selezionaPulsanteApprendistato(){
	return true;
}


function setSize(obj,maxLen) {
  if(obj.value!=null && obj.value!="") {
     if(obj.value.length>maxLen) {
       obj.value = obj.value.substr(0,maxLen) + "...";
       obj.size = (maxLen + 5);
     } else {
       alert("OBJ: "+obj.value+"\nlength: "+obj.value.length+"\nobj.size: "+(obj.value.length + 5));
       obj.size = (obj.value.length + 5);
       alert("OBJ: "+obj.value+"\nlength: "+obj.value.length+"\nobj.size: "+(obj.value.length + 5));
       
     }
  }
  //alert("OBJ: "+obj.value+"\nlength: "+obj.value.length+"\nobj.size: "+obj.size);
}

function ControllaInserimento(codMonoTipo){
//	if (codMonoTipo == "A") {
//		if(!confirm("Attenzione: le comunicazioni di Apprendistato dovrebbero arrivare tramite SARE.Vuoi registrare manualmente il movimento?")){
//        	 return false;
//        }
//    }    
    return true;
}

function apriConvenzione() {
	var f = "AdapterHTTP?PAGE=MovListaConvenzionePage&prgAzienda="+document.Frm1.PRGAZIENDA.value+"&cdnLavoratore="+document.Frm1.CDNLAVORATORE.value;
	var t = "_blank";
    var feat = "toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,width=800,height=550,top=70,left=100";
    finestraConvenzione = window.open(f, t, feat);
}
//Funzione di aggiornamento valori dell'apprendistato (Convenzioni/Nulla Osta)
function aggiornaConvNullaOsta(){
  document.Frm1.datConvenzione.value = finestraConvenzione.datConvNullaOsta;
  document.Frm1.numConvenzione.value = finestraConvenzione.numConvNullaOsta;
  finestraConvenzione.close();
}

function controllaConvenzione() {
	var categoriaLavAss = document.Frm1.codCatAssObbl.value;
	var flgL68CategoriaLavAss = "";
	for (i = 0; i < vettCatLavAssObbl.length; i++) {
		if (vettCatLavAssObbl[i] == categoriaLavAss) {
			flgL68CategoriaLavAss = vettFlg68CatLavAssObbl[i];
		}
	}
	if (document.Frm1.flgCheckAssObbl.checked) {
		document.Frm1.flgAssObbl.value = "S";
		if (document.Frm1.codCatAssObbl.value == '') {
			alert("Il campo categoria lavoratore assunto in regime di obbligo deve essere valorizzato se si indica assunzione obbligatoria.");
			return false;
		}
	}
	else {
		document.Frm1.flgAssObbl.value = "";
		if (document.Frm1.codCatAssObbl.value != '') {
			alert("Il campo categoria lavoratore assunto in regime di obbligo non deve essere valorizzato se non si indica assunzione obbligatoria.");
			return false;
		}
	}
	if (document.Frm1.LAVORATORECOLLMIRATO.value != "true") {
		if (document.Frm1.flgCheckAssObbl.checked) {
			if (flgL68CategoriaLavAss == "S") {
				alert("Il lavoratore non risulta iscritto al collocamento mirato!");
				disabilitaCampiL68();
				return false;
			}
		}
	}
	
	var numConvenzione = document.Frm1.numConvenzione.value;
	var datConvenzione = document.Frm1.datConvenzione.value;
	
	if (numConvenzione != "" && datConvenzione != "") {
		if (!document.Frm1.flgCheckAssObbl.checked || flgL68CategoriaLavAss != "S") {
			if (!confirm("Il movimento contiene informazioni relative ad un rapporto in regime di obbligo secondo il vecchio tracciato,\n" + 
						"per mantenere tali informazioni è necessario tradurre le informazioni nei nuovi campi (assunzione in obbligo e categoria lavoratore in obbligo).\n" + 
						"Se confermi l'operazione senza aggiornare i nuovi campi i vecchi dati (flg legge 68, data e numero convenzione/nulla osta) verranno cancellati da questo movimento.")) {
				return false;
			}
		}
	}
	return true;
}

function valorizzaCompensoMensile() {
	var decRetrAnn = document.Frm1.decRetribuzioneAnn.value / 12;
	if (document.Frm1.decRetribuzioneAnn.value != "") {
		if(isNaN(document.Frm1.decRetribuzioneAnn.value)){
			alert("Attenzione: il valore inserito non è un numero!");
			document.Frm1.decRetribuzioneAnn.value = "";
			document.Frm1.decRetribuzioneMen.value = "";
		}else {
			document.Frm1.decRetribuzioneMen.value = Math.round(decRetrAnn*100)/100;
			document.Frm1.compensoMensile.value = document.Frm1.decRetribuzioneMen.value;
		}
	}
	else {document.Frm1.decRetribuzioneMen.value = "";}
}

function abilitazioneConvenzione() {
	var categoriaLavAss = document.Frm1.codCatAssObbl.value;
	var flgL68CategoriaLavAss = "";
	for (i = 0; i < vettCatLavAssObbl.length; i++) {
		if (vettCatLavAssObbl[i] == categoriaLavAss) {
			flgL68CategoriaLavAss = vettFlg68CatLavAssObbl[i];
		}
	}
	if (document.Frm1.flgCheckAssObbl.checked &&
	    (document.Frm1.strCodiceFiscaleLav.value == "" || document.Frm1.strCodiceFiscaleAz.value == "") ) {
		if (flgL68CategoriaLavAss == "S") {
			alert("Inserire prima il lavoratore e l'azienda");
			document.Frm1.flgCheckAssObbl.checked = "";
		}
	}
	else {
		if (document.Frm1.LAVORATORECOLLMIRATO.value == "true") {
			abilita = true;
		} else {
			abilita = false;
		}
		document.Frm1.DATFINESGRAVIO.readonly=!abilita;
		document.Frm1.DATFINESGRAVIO.disabled=!abilita;
		document.Frm1.DECIMPORTOCONCESSO.readonly=!abilita;
		document.Frm1.DECIMPORTOCONCESSO.disabled=!abilita;
		if (abilita) {
			if (document.Frm1.flgCheckAssObbl.checked && flgL68CategoriaLavAss == "S") {
				document.Frm1.DATFINESGRAVIO.className="inputEdit";
				document.Frm1.DECIMPORTOCONCESSO.className="inputEdit";
			}
		}
		else {
			if (document.Frm1.flgCheckAssObbl.checked && flgL68CategoriaLavAss == "S") {
				alert("Il lavoratore non risulta iscritto al collocamento mirato!");
				document.Frm1.flgCheckAssObbl.checked = "";
				document.Frm1.FLGLEGGE68.value="N";
				document.Frm1.flgAssObbl.value = "N";
				document.Frm1.codCatAssObbl.value="";
				document.Frm1.DATFINESGRAVIO.value="";
   			    document.Frm1.DECIMPORTOCONCESSO.value="";
   			    document.Frm1.DATFINESGRAVIO.className="viewRiepilogo";
			    document.Frm1.DECIMPORTOCONCESSO.className="viewRiepilogo";	
			}
		}
		
		if (!document.Frm1.flgCheckAssObbl.checked || flgL68CategoriaLavAss != "S") {
			disabilitaCampiL68();
		}
	}
}

function abilitazioneConvenzioneOnLoad() {
	var categoriaLavAss = document.Frm1.codCatAssObbl.value;
	var flgL68CategoriaLavAss = "";
	for (i = 0; i < vettCatLavAssObbl.length; i++) {
		if (vettCatLavAssObbl[i] == categoriaLavAss) {
			flgL68CategoriaLavAss = vettFlg68CatLavAssObbl[i];
		}
	}
	if (document.Frm1.LAVORATORECOLLMIRATO.value == "true") {
		abilita = true;
	} else {
		abilita = false;
	}
    
    if (!document.Frm1.flgCheckAssObbl.checked || flgL68CategoriaLavAss != "S") {
    	document.Frm1.DATFINESGRAVIO.readonly=true;
		document.Frm1.DATFINESGRAVIO.disabled=true;
		document.Frm1.DECIMPORTOCONCESSO.readonly=true;
		document.Frm1.DECIMPORTOCONCESSO.disabled=true;
    }
    
    if (abilita) {
		if (document.Frm1.flgCheckAssObbl.checked && flgL68CategoriaLavAss == "S") {
			document.Frm1.DATFINESGRAVIO.className="inputEdit";
			document.Frm1.DECIMPORTOCONCESSO.className="inputEdit";
		}
	}
    else {
		if (document.Frm1.flgCheckAssObbl.checked && flgL68CategoriaLavAss == "S") {
			alert("Il lavoratore non risulta iscritto al collocamento mirato!");
		}
	}
    
    if (!abilita && (!document.Frm1.flgCheckAssObbl.checked || flgL68CategoriaLavAss != "S") ) {
		document.Frm1.DATFINESGRAVIO.readonly=!abilita;
		document.Frm1.DATFINESGRAVIO.disabled=!abilita;
		document.Frm1.DECIMPORTOCONCESSO.readonly=!abilita;
		document.Frm1.DECIMPORTOCONCESSO.disabled=!abilita;
		document.Frm1.DATFINESGRAVIO.className="viewRiepilogo";
		document.Frm1.DECIMPORTOCONCESSO.className="viewRiepilogo";
	}
    //if (!document.Frm1.flgCheckAssObbl.checked || flgL68CategoriaLavAss != "S") {
	//	disabilitaCampiL68();
	//}
}

function disabilitaCampiL68() {
	document.Frm1.DATFINESGRAVIO.readonly=true;
	document.Frm1.DATFINESGRAVIO.disabled=true;
	document.Frm1.DECIMPORTOCONCESSO.readonly=true;
	document.Frm1.DECIMPORTOCONCESSO.disabled=true;
	document.Frm1.DATFINESGRAVIO.value="";
	document.Frm1.DECIMPORTOCONCESSO.value="";
	document.Frm1.DATFINESGRAVIO.className="viewRiepilogo";
	document.Frm1.DECIMPORTOCONCESSO.className="viewRiepilogo";
}

function checkObb() {
	var contestoMov = document.Frm1.CURRENTCONTEXT.value;
	var flagAziEstera = document.Frm1.flagAziEstera.checked;
	var codTipoContratto = document.Frm1.codTipoAss.value.toUpperCase();
	var codCCNL = document.Frm1.codCCNL.value.toUpperCase();
	
	if(document.Frm1.CODTIPOMOV.value == "AVV") {
		if(document.Frm1.datInizioMov.value == "") {
			alert("Il campo Data Inizio è obbligatorio");
			return false;
		}
	}
	if(document.Frm1.CODTIPOMOV.value == "CES") {
		if(document.Frm1.datInizioMovCes.value == "") {
			alert("Il campo Data Cessazione è obbligatorio");
			return false;
		}
		if( document.Frm1.CODTIPOAZIENDA.value != "INT" && document.Frm1.codMvCessazione.value == "") {
			alert("Il campo Motivo cessazione è obbligatorio");
			return false;
		}
		if(document.Frm1.COLLEGATO.value == 'nessuno') {
			if(document.Frm1.codMonoTempo.value == "D") {
				alert("In caso di cessazione orfana o veloce il Tempo deve essere indeterminato");
				return false;
			}
		}
	}
	if(document.Frm1.CODTIPOMOV.value == "PRO" && document.Frm1.datFineMovPro.value == "") {
		alert("Il campo Data fine proroga è obbligatorio");
		return false;
	}
	if(document.Frm1.CODTIPOMOV.value == "TRA") { 
		if(document.Frm1.datInizioMovTra.value == "") {
			alert("Il campo Data trasformazione è obbligatorio");
			return false;
		}
		if(document.Frm1.CODTIPOTRASF.value == "") {
			alert("Il campo Motivo trasformazione è obbligatorio");
			return false;
		}
		if(document.Frm1.CODTIPOTRASF.value != "" && document.Frm1.CODTIPOTRASF.value == 'DL') {
			if(document.Frm1.DATFINEDISTACCO.value == "") {
				alert("Il campo Data fine distacco è obbligatorio");
				return false;
			}
			if(!flagAziEstera && (document.Frm1.strRagioneSocialeAzUtil.value == "" || document.Frm1.strIndirizzoUAzUtil.value == "" 
			   || document.Frm1.strComuneUAzUtil.value == "" ) ) {
					alert("Inserire l'Azienda Distaccataria");
					return false;
			}
		}
		if(document.Frm1.CODTIPOTRASF.value != "" && document.Frm1.CODTIPOTRASF.value == 'TL') {
			if(document.Frm1.strCodiceFiscaleAzTra.value == "" || document.Frm1.strIndirizzoUAzVisualizzatoTra.value == "" 
			   || document.Frm1.strRagioneSocialeAzTruncTra.value == "" ) {
					alert("Inserire la Sede Azienda Trasferimento");
					return false;
			}
		}
		if(document.Frm1.COLLEGATO.value == 'nessuno' && document.Frm1.codMonoTempo.value == "D" && contestoMov != 'valida') {
			alert("Impossibile inserire una trasformazione a tempo determinato senza aver scelto il movimento precedente");
			return false;
		}
	}
	if(document.Frm1.CODMONOTIPO.value == "A")
	{
		if(document.Frm1.datFinePF.value == "") {
			alert("Il campo Data fine Periodo Formativo è obbligatorio per i contratti di apprendistato");
			return false;	
		}
	}
	
	 var strVersioneTracciato = '';
	 if (document.Frm1.STRVERSIONETRACCIATOhid != null) {
		 strVersioneTracciato = document.Frm1.STRVERSIONETRACCIATOhid.value;
	 }

	 if (strVersioneTracciato != '' && Number(strVersioneTracciato.substring(0, 1)) >=4) {
		// TO DO controllo presenza decRetribuzioneAnn
		 if (document.Frm1.decRetribuzioneAnn.value=='') {
			alert("Il compenso annuale è obbligatorio");
			return false;
		 }
		 if (Number(document.Frm1.decRetribuzioneAnn.value)<1 && codTipoContratto!="C.01.00"){
			alert("Per la tipologia contrattuale indicata non è possibile indicare un compenso annuale uguale a 0");
			return false;
		 }
		
		 if (codTipoContratto=="C.01.00" && codCCNL!="ND") {
			 alert("CCNL non compatibile con la tipologia contrattuale 'C.01.00 -€“ Tirocinio' ");
			return false;
		 }
		 
		 
	 }	
	
		
	if(document.Frm1.CODMONOTIPO.value == "T")
	{
		// il seguente controllo e' in realtà duplicato vedi funzione AggiornaTirocinio
		if(document.Frm1.STRCODFISCPROMOTORETIR.value == "")
		{
			if (document.Frm1.codTipoAss.value == "C.01.00" || document.Frm1.codTipoAss.value == "c.01.00") {
				alert("Codice Fiscale Promotore Tirocinio obbligatorio per i contratti di Tirocinio");
				return false;
			}
		}
		
		var codTipoMov = document.Frm1.CODTIPOMOV.value;
		
		if ((document.Frm1.codTipoAss.value == "C.01.00" || 
				document.Frm1.codTipoAss.value == "c.01.00") &&
				codTipoMov != 'CES') {
		
			var mesi;
			var datInizioMov;
			var datFineMov;
			var codCategoriaTir = document.Frm1.CODCATEGORIATIR.value
			
			if (codTipoMov == 'AVV' || codTipoMov == 'TRA') {
				if (document.Frm1.datInizioMov) {
					datInizioMov = document.Frm1.datInizioMov.value;
				}
				if (document.Frm1.datFineMov) {
					datFineMov = document.Frm1.datFineMov.value;
				}
			}
			if (codTipoMov == 'PRO') {
				if (document.Frm1.datInizioMov) {
					// DATINIZIOMOV e' in verità la datfinemov + 1
					datInizioMov = giornoSuccessivo(document.Frm1.datFineMov.value)
				}
				if (document.Frm1.datFineMovPro) {
					datFineMov = document.Frm1.datFineMovPro.value;
				}
			}
			
			if (datInizioMov && datFineMov) {
				mesi = numeroMesiDiLavoro(datInizioMov, datFineMov);
				if (mesi) {
					
					function findValueInArray(value, array) {
						var isFound = false;
						var i;
						for (i = 0; i < array.length; i += 1) {
				  			if (array[i] == value) {
				  				isFound = true;
				  				break;
				  			}
				  		}
						return isFound;
					}
					
					var valoriCodCategoriaTir;
					
					valoriCodCategoriaTir = ["04", "05", "06", "07"];
					if (mesi > 6 && findValueInArray(codCategoriaTir, valoriCodCategoriaTir)) {
			  			if (!confirm("Per la categoria tirocinante selezionata la durata massima del rapporto è di sei mesi.\n\nSi desidera procedere?")) {
			  				return false;
			  			}
			  		}
					
					valoriCodCategoriaTir = ["01", "03", "08"];
					if (mesi > 12 && findValueInArray(codCategoriaTir, valoriCodCategoriaTir)) {
						if (!confirm("Per la categoria tirocinante selezionata la durata massima del rapporto è di 12 mesi.\n\nSi desidera procedere?")) {
							return false;
						}
			  		}
					
					valoriCodCategoriaTir = ["02", "09"];
					if (mesi > 24 && findValueInArray(codCategoriaTir, valoriCodCategoriaTir)) {
						if (!confirm("Per la categoria tirocinante selezionata la durata massima del rapporto è di 24 mesi.\n\nSi desidera procedere?")) {
							return false;
						}
			  		}
			  		
				}
			}
			
		}
				
	}
	
	// controlli apprendistato
	
	if (document.Frm1.codTipoAss.value == "A.03.08" || 
			document.Frm1.codTipoAss.value == "A.03.09" ||
			document.Frm1.codTipoAss.value == "A.03.10") {
		
		//if (document.Frm1.codMonoTempo.value == "D" && document.Frm1.FLGLAVOROSTAGIONALE.value != "S") {
		//	alert("Per la tipologia di contratto selezionata è necessario indicare tempo indeterminato");
		//	return false;
		//}
		
		if (document.Frm1.codMonoTempo.value == "I" && document.Frm1.FLGLAVOROSTAGIONALE.value == "S" && document.Frm1.datFineMov.value != "") {
			alert("Per la tipologia di contratto selezionata è necessario indicare tempo determinato");
			return false;
		}
		
	}
	
	// controlli contratti X
	
	if (document.Frm1.codTipoAss.value == "A.04.02" || 
			document.Frm1.codTipoAss.value == "A.08.02" ||
			document.Frm1.codTipoAss.value == "A.07.02" ||
			document.Frm1.codTipoAss.value == "A.05.02") {
		
		if (document.Frm1.codMonoTempo.value == "I" && document.Frm1.FLGLAVOROSTAGIONALE.value == "S") {
			alert("In caso di lavoro stagionale è necessario indicare tempo determinato.");
			return false;
		}
		
	}
	
	if(document.Frm1.FLGLAVOROSTAGIONALE.value == "S") {
		
		var valoreOk = false;
  		var valoriAmmessiCategoria = 
  			["A.02.00", "A.02.01", "B.01.00", "B.02.00", "B.03.00", "C.03.00", "A.03.08", "A.03.09", "A.03.10", "A.04.02", "A.08.02", "A.07.02", "A.05.02", "H.02.00"];
  		
  		for (i = 0; i < valoriAmmessiCategoria.length; i += 1) {
  			if (valoriAmmessiCategoria[i] == document.Frm1.codTipoAss.value) {
  				valoreOk = true;
  				break;
  			}
  		}
  		
  		if (!valoreOk) {
  			alert("Non è possibile specificare lavoro stagionale per la tipologia di contratto selezionata");
  			return false;
  		}
  		
  		//if (!document.Frm1.datFineMov.value) {
		//	alert("Il campo data fine movimento e' obbligatorio in caso di rapporto stagionale");
		//	return false;
		//}
	  	
	}
	
	return true;
}

function campiAgricoltura() {
	var lavorazione = '';
	var flagAgricoltura = '';
	if(document.Frm1.CODTIPOMOV.value == "CES"){
		visualizzaCampi("agrEffettivi","");
		visualizzaCampi("agrPrevisti","none");
	} else {
		visualizzaCampi("agrEffettivi","none");
		visualizzaCampi("agrPrevisti","");
		if (dataLavAgric != "" && compareDate(dataOdierna,dataLavAgric) > 0) {
			flagAgricoltura = document.Frm1.lavAgr;
				if(flagAgricoltura.checked){
					document.Frm1.FLGLAVOROAGR.value="S";
				} else {
					document.Frm1.FLGLAVOROAGR.value="N";
				}
			}
	}
}


//Apre la selezione del tirocinio
function apriTirocinio() {
	var f = "AdapterHTTP?PAGE=MovTirocinioPage";
	if(document.Frm1.CURRENTCONTEXT.value != ""){
    	f = f + "&CURRENTCONTEXT=" + document.Frm1.CURRENTCONTEXT.value;
    }
    if (document.Frm1.PRGMOVIMENTO != null && document.Frm1.CURRENTCONTEXT.value == "consulta") {
        f = f + "&PRGMOVIMENTO=" + document.Frm1.PRGMOVIMENTO.value;
    } else if (document.Frm1.PRGMOVIMENTOAPP != null && document.Frm1.CURRENTCONTEXT.value == "valida") {
    	f = f + "&PRGMOVIMENTOAPP=" + document.Frm1.PRGMOVIMENTOAPP.value;
    }
    if (document.Frm1.PRGMOVIMENTORETT != null && document.Frm1.CURRENTCONTEXT.value == "inserisci") {
    	f = f + "&PRGMOVIMENTO=" + document.Frm1.PRGMOVIMENTORETT.value;
    }
    
    f = f + "&updateFunctionName=AggiornaTirocinio";
    f = f + "&strCognomeTutore=" + document.Frm1.STRCOGNOMETUTORE.value;
    f = f + "&strNomeTutore=" + document.Frm1.STRNOMETUTORE.value;
    f = f + "&strCodiceFiscaleTutore=" + document.Frm1.STRCODICEFISCALETUTORE.value;
    f = f + "&codSoggPromotoreMin=" + document.Frm1.CODSOGGPROMOTOREMIN.value;
    f = f + "&codCategoriaTir=" + document.Frm1.CODCATEGORIATIR.value;
    f = f + "&codTipologiaTir=" + document.Frm1.CODTIPOLOGIATIR.value;
    f = f + "&STRDENOMINAZIONETIR=" + document.Frm1.STRDENOMINAZIONETIR.value;
    f = f + "&CODTIPOENTEPROMOTORE=" + document.Frm1.CODTIPOENTEPROMOTORE.value;
    
    if (document.Frm1.CODQUALIFICASRQ.value != "") {
    	f = f + "&codQualificaSrq=" + document.Frm1.CODQUALIFICASRQ.value;
    }	
	f = f + "&strCodFiscPromotoreTir=" + document.Frm1.STRCODFISCPROMOTORETIR.value;
	var t = "_blank";
    var feat = "toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,width=1096,height=550,top=40,left=100";
    finestraTirocinio = window.open(f, t, feat);
}

//Funzione di aggiornamento valori del tirocinio
function AggiornaTirocinio() {
	
	var codSoggPromotoreMinCombo = finestraTirocinio.document.Frm1.codSoggPromotoreMin;
	var codCategoriaTirocinanteCombo = finestraTirocinio.document.Frm1.codCategoriaTir;
	var codTipologiaTirocinioCombo = finestraTirocinio.document.Frm1.codTipologiaTir;
	
	var codCategoriaTir = codCategoriaTirocinanteCombo.options[codCategoriaTirocinanteCombo.selectedIndex].value;
	var codTipologiaTir = codTipologiaTirocinioCombo.options[codTipologiaTirocinioCombo.selectedIndex].value;
	
	var strDenominazioneTir = finestraTirocinio.document.Frm1.STRDENOMINAZIONETIR.value
	var strCodFiscPromotoreTir = finestraTirocinio.document.Frm1.STRCODFISCPROMOTORETIR.value;
	var codSoggPromotoreMin = codSoggPromotoreMinCombo.options[codSoggPromotoreMinCombo.selectedIndex].value;
	
	if (document.Frm1.codTipoAss.value == 'C.01.00') {
		
		if (!strCodFiscPromotoreTir) {
	  		finestraTirocinio.alert("Il codice fiscale del soggetto promotore tirocinio è obbligatorio");
			return false;
		}
		if (!strDenominazioneTir) {
	  		finestraTirocinio.alert("La denominazione del soggetto promotore tirocinio è obbligatoria");
			return false;
		}
		if (!codCategoriaTir) {
	  		finestraTirocinio.alert("La categoria tirocinante è obbligatoria");
			return false;
		}
		if (!codTipologiaTir) {
	  		finestraTirocinio.alert("La tipologia tirocinio è obbligatoria");
			return false;
		}
		if (!codSoggPromotoreMin) {
	  		finestraTirocinio.alert("Il tipo soggetto promotore è obbligatorio");
			return false;
		}
	}
	
  	if (finestraTirocinio.document.Frm1.STRCODFISCPROMOTORETIR.value && document.Frm1.codTipoAss.value != 'C.01.00') {
  		finestraTirocinio.alert("Il \"CF soggetto promotore tirocinio\" deve essere compilato in caso di \"tipologia contrattuale\" uguale a C.01.00 - TIROCINIO. Non deve essere compilato negli altri casi");
		return false;
	}
  	
  	if (finestraTirocinio.document.Frm1.STRCODFISCPROMOTORETIR.value == document.Frm1.strCodiceFiscaleAz.value) {
  		finestraTirocinio.alert("Il CF soggetto promotore tirocinio non può essere uguale al campo \"Codice Fiscale\" della sezione \"Datore di lavoro\"");
		return false;
	}
  	
  	var valoriAmmessiCategoria = [], 
		i = 0,
		valoreOk = false;
  	
  	// se l'utente seleziona uno dei due, deve selezionare anche l'altro
  	
  	//if (codTipologiaTir && !codCategoriaTir) {
  	//	finestraTirocinio.alert("Se la categoria di tirocinio e' valorizzata inserire la tipologia di tirocinio");
  	//	return false;
  	//}
  	
  	//if (!codTipologiaTir && codCategoriaTir) {
  	//	finestraTirocinio.alert("Se la tipologia di tirocinio e' valorizzata inserire la categoria di tirocinio");
  	//	return false;
  	//}
  	
  	// controlli di coerenza
  	
  	if (codTipologiaTir == "A") {
  		
  		valoreOk = false;
  		valoriAmmessiCategoria = ["01", "02", "04", "05", "06", "07"];
  		
  		for (i = 0; i < valoriAmmessiCategoria.length; i += 1) {
  			if (valoriAmmessiCategoria[i] == codCategoriaTir) {
  				valoreOk = true;
  				break;
  			}
  		}
  		
  		if (!valoreOk) {
  			finestraTirocinio.alert("La tipologia di tirocinio deve essere valorizzata coerentemente con la categoria del tirocinante");
  			return false;
  		}
  		
  	}
  	
  	if (codTipologiaTir == "B") {
  		
  		valoreOk = false;
  		valoriAmmessiCategoria = ["01", "02", "03", "08"];
  		
  		for (i = 0; i < valoriAmmessiCategoria.length; i += 1) {
  			if (valoriAmmessiCategoria[i] == codCategoriaTir) {
  				valoreOk = true;
  				break;
  			}
  		}
  		
  		if (!valoreOk) {
  			finestraTirocinio.alert("La tipologia di tirocinio deve essere valorizzata coerentemente con la categoria del tirocinante");
  			return false;
  		}
  		
  	}
  	
  	if (codTipologiaTir == "C") {
  		
  		valoreOk = false;
  		valoriAmmessiCategoria = ["09"];
  		
  		for (i = 0; i < valoriAmmessiCategoria.length; i += 1) {
  			if (valoriAmmessiCategoria[i] == codCategoriaTir) {
  				valoreOk = true;
  				break;
  			}
  		}
  		
  		if (!valoreOk) {
  			finestraTirocinio.alert("La tipologia di tirocinio deve essere valorizzata coerentemente con la categoria del tirocinante");
  			return false;
  		}
  		
  	}
  	
  	document.Frm1.CODSOGGPROMOTOREMIN.value = codSoggPromotoreMinCombo.options[codSoggPromotoreMinCombo.selectedIndex].value;
	document.Frm1.CODCATEGORIATIR.value = codCategoriaTirocinanteCombo.options[codCategoriaTirocinanteCombo.selectedIndex].value;
	document.Frm1.CODTIPOLOGIATIR.value = codTipologiaTirocinioCombo.options[codTipologiaTirocinioCombo.selectedIndex].value;
	document.Frm1.STRDENOMINAZIONETIR.value = finestraTirocinio.document.Frm1.STRDENOMINAZIONETIR.value;
  	document.Frm1.STRCOGNOMETUTORE.value = finestraTirocinio.document.Frm1.STRCOGNOMETUTORE.value;
  	document.Frm1.STRNOMETUTORE.value = finestraTirocinio.document.Frm1.STRNOMETUTORE.value;
  	document.Frm1.STRCODICEFISCALETUTORE.value = finestraTirocinio.document.Frm1.STRCODICEFISCALETUTORE.value;
  	document.Frm1.CODQUALIFICASRQ.value = finestraTirocinio.document.Frm1.CODQUALIFICASRQ.value;
  	document.Frm1.STRCODFISCPROMOTORETIR.value = finestraTirocinio.document.Frm1.STRCODFISCPROMOTORETIR.value;
  	
  	if (finestraTirocinio.document.Frm1.codTipoEntePromotore) {
  		// campo disponibile solo in RER
  		document.Frm1.CODTIPOENTEPROMOTORE.value = finestraTirocinio.document.Frm1.codTipoEntePromotore.value;
  	}
  	
  	finestraTirocinio.close();
  	
}

function controllaRappLavAccessorio() {
	if(document.Frm1.codTipoAss.value == 'NB5' ) {
  		if(!confirm("Attenzione il tipo contratto NB5 è da utilizzare solo per rapporti di lavoro accessorio ai sensi dell'art. 70 Dlgs. 276/03. Continuare?") ) {
        	 return false;
        }
    }    
    return true;
}

/*
 * Calcola il numero di mesi tra dataInizio e dataFine.
 * Si conta un mese se si superano 15 gg di lavoro.
 */
function numeroMesiDiLavoro(dataInizio, dataFine) {
	
	var ddInizio, ddFine, mmInizio, mmFine, yyyyInizio, yyyyFine, inizioDate, fineDate, mesi;
	
	if (!dataInizio || !dataFine) {
		return null;
	}
	
	ddInizio = dataInizio.substring(0, 2);
	mmInizio = dataInizio.substring(3, 5);
	yyyyInizio = dataInizio.substring(6, 10);
	
	ddFine = dataFine.substring(0, 2);
	mmFine = dataFine.substring(3, 5);
	yyyyFine = dataFine.substring(6, 10);
	
	inizioDate = new Date();
	inizioDate.setFullYear(yyyyInizio, mmInizio - 1, ddInizio);
	
	fineDate = new Date();
	fineDate.setFullYear(yyyyFine, mmFine - 1, ddFine);
	
	if (inizioDate > fineDate) {
		return null;
	}
	
	// calcola i mesi tra la data iniziale e finale (compreso il mese iniziale e finale)
	
	if (yyyyInizio == yyyyFine) {
		mesi = (mmFine - mmInizio) + 1;
	} else {
		mesi = (12 - mmInizio) + 1;
		mesi = mesi + ((yyyyFine - yyyyInizio) * 12) - (12 - mmFine);
	}
	
	// i mesi iniziale e finale vanno considerati solo se i giorni commerciali lavorati sono almeno 16
	
	if (mesi == 1) {
		if ((ddFine - ddInizio) + 1 < 16) {
			mesi = mesi - 1;
		}
	} else {
		if (((30 - ddInizio) + 1) < 16) {
			mesi = mesi - 1;
		}
		if (ddFine < 16) {
			mesi = mesi - 1;
		}
	}
	
	return mesi;
	
}

function giornoSuccessivo(dataString) {
	
	var dd, mm, yyyy, giornoSuccessivoDate;
	
	dd = dataString.substring(0, 2);
	mm = dataString.substring(3, 5);
	yyyy = dataString.substring(6, 10);
	
	giornoSuccessivoDate = new Date();
	giornoSuccessivoDate.setFullYear(yyyy, mm - 1, dd);
	
	giornoSuccessivoDate.setDate(giornoSuccessivoDate.getDate() + 1);
	dd = giornoSuccessivoDate.getDate();
	mm = giornoSuccessivoDate.getMonth() + 1;
	yyyy = giornoSuccessivoDate.getFullYear();
	
	if ((""+dd).length < 2) {
		dd = "0" + dd;
	}
	if ((""+mm).length < 2) {
		mm = "0" + mm;
	}
	
	return dd + "/" + mm + "/" + yyyy;
	
}

//ricerca del livello dato il CCNL
function selectLivelloInquadramento() {	
	var codCCNL=document.Frm1.codCCNL.value;
	if(codCCNL == ""){
		alert('Il campo CCNL è obbligatorio');
	} else {
		var t="AdapterHTTP?PAGE=RicercaLivelloPage&codccnl="+codCCNL;
		window.open(t, "Livello", 'toolbar=0, scrollbars=1, height=300, width=550');
	}
}

//calcola il compenso annuale
function calcolaRetribuzione() {
	var messageErr ="";
	var codCCNL=document.Frm1.codCCNL.value;
	var numLivello=document.Frm1.numLivello.value;
	var codOrario=document.Frm1.codOrario.value;
	var tipoOrario = getTipoOrario(codOrario);
	var numOreSett=document.Frm1.numOreSett.value;
	var numOreSettNonValorizzato = (tipoOrario=="P" && numOreSett=="");
	
	if(codCCNL=="" || numLivello=="" || codOrario=="" || numOreSettNonValorizzato){
		if (codCCNL=="") messageErr += "Il campo CCNL è obbligatorio.\n";
		if (numLivello=="")	messageErr += "Il campo Livello è obbligatorio.\n";
		if (codOrario=="") 	
			messageErr += "Il campo Orario è obbligatorio.\n";
		else if (numOreSettNonValorizzato)	
			messageErr += "Il campo Ore settimanali è obbligatorio.";
		alert(messageErr);
	} else {
		var esito = validateFixDecimal("numOreSett");
		numOreSett=document.Frm1.numOreSett.value;
		if (esito) {
			var t="AdapterHTTP?PAGE=CalcolaRetribuzionePage&codccnl="+codCCNL
				+"&numLivello="+numLivello
				+"&codOrario="+codOrario
				+"&oresett="+numOreSett;
			window.open(t, "Compenso annuale", 'toolbar=0, scrollbars=1, height=330, width=550')
		} 
	}
	
}