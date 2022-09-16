
    
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

//Funzione per alert degli impatti
function checkDatInizioMovImpattiCessazione(datInizioMov, datInizioMovPrec) {
	d = new String(datInizioMov.value);
	dataInizio = new Date(d.substring(6,10), (d.substring(3,5) - 1), d.substring(0,2));
	limiteImpatti = new Date(2003, 0, 30);
	if (dataInizio < limiteImpatti) {
		alert('Il movimento é precedente alla data del 30/01/2003. Il sistema non prevede automatismi per la gestione degli impatti.');
	} else {
	//	datinizprec = new String(datInizioMovPrec.value);
	//	if (datinizprec != null && datinizprec != '') {
	//		dataPrec = new Date(datinizprec.substring(6,10), (datinizprec.substring(3,5) - 1), datinizprec.substring(0,2));		
	//		if (dataPrec < limiteImpatti) {
	//			alert('Il movimento che si sta cessando è precedente alla data del 30/01/2003. Il sistema non prevede automatismi per la gestione degli impatti.');
	//		}
	//	}
	}
	return true;	
}

//Funzione di segnalazione per la scelta delle mansioni
function checkMansione(nameMansione) {
	var cod = new String(eval('document.Frm1.' + nameMansione + '.value'));
	return controllaQualificaOnSubmit(nameMansione);
}
//funzione che effettua il cambio del campo hidden del grado quando cambia il valore della combo visualizzata
function riportaGradoHidden() {
	document.Frm1.codGrado.value = document.Frm1.codGradoVisualizz.options[document.Frm1.codGradoVisualizz.selectedIndex].value;
}

//Imposta la combo grado per l'apprendistato
function impostaGrado(){
  //code = document.forms[0].CODMONOTIPO.value;
  o = document.getElementsByName("CODMONOTIPO");
  grado = o[0].value;  
  if (grado == "A" || grado == "T" ) {
  	//Ritrova l'indice che corrisponde all'apprendistato
  	opt = document.Frm1.codGradoVisualizz.options;
  	var index = 1;
  	// 14 = generico
  	// 15 = apprendista
  	var codGrado = grado=="A"?"15":"14";
  	for (var i=0; i < opt.length; i++) {if (opt[i].value == codGrado) {index = i;}}
    document.Frm1.codGradoVisualizz.selectedIndex=index;
    document.Frm1.codGradoVisualizz.disabled=true;
    riportaGradoHidden();
  }
  else{
    document.Frm1.codGradoVisualizz.disabled=false;
    
  }
}

//Ricerca della mansione per descrizione
function selectMansionePerDescrizione(desMansione) {
	window.open("AdapterHTTP?PAGE=RicercaMansionePage&desMansione="+desMansione.value+"&flgFrequente=", "Mansioni", 'toolbar=0, scrollbars=1');          
}



//abilita e disabilita le ore settimanali
function visualizzaOreSett(orario) {
	if(orario != null){
  var orariovalue = orario.options[orario.selectedIndex].value;
  var labelore = document.getElementById("labelore");
  if (orariovalue == "PTO") {
    	labelore.style.display = "inline";	    
  }
  else if (orariovalue == "PTV") {
    	labelore.style.display = "inline";	    
  }
  else if (orariovalue == "M") {
    	labelore.style.display = "inline";	    
  }
  else {
  	document.Frm1.numOreSett.value = "";
    labelore.style.display = "none"; 	
  }   
  }
}

function checkNumOreSettimanali() 
{ 
  var orariovalue = document.Frm1.codOrario.value;
  
  if( (orariovalue == "PTO") && (document.Frm1.numOreSett.value == null || document.Frm1.numOreSett.value == "") )
  { alert("In caso di part time occore specificare il numero di ore settimanali");
    return false;
  }
  else if( (orariovalue == "PTV") && (document.Frm1.numOreSett.value == null || document.Frm1.numOreSett.value == "") )
  { alert("In caso di part time occore specificare il numero di ore settimanali");
    return false;
  }
  else if( (orariovalue == "M") && (document.Frm1.numOreSett.value == null || document.Frm1.numOreSett.value == "") )
  { alert("In caso di part time occore specificare il numero di ore settimanali");
    return false;
  }
  return true;  
}

function checkOrario() 
{ 
  var dataIniziovalue = document.Frm1.DATAINIZIOAVVCEV.value;
  var codOrariovalue = document.Frm1.codOrario.value;
  if(dataIniziovalue != "" && codOrariovalue == "")
	  { alert("Se è stata inserita la data di inizio avviamento è obbligatorio inserire il tipo di orario");
	    return false;
	  }
  return true;
}