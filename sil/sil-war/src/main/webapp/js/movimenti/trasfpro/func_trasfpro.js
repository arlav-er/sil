
//abilita e disabilita la scadenza
function visualizzaScadenza(tempo) {
  var tempovalue = tempo.options[tempo.selectedIndex].value;
  var scadenza = document.getElementById("scadenza");
  var datascadenza = document.getElementById("datascadenza");
  if (tempovalue == "I") {
    document.Frm1.datFineMov.value = "";
    scadenza.style.display = "none";
    datascadenza.style.display = "none";
  }
  else {
    scadenza.style.display="inline";
    datascadenza.style.display = "inline";
  }   
}

//Ricerca della mansione per descrizione
function selectMansionePerDescrizione(desMansione) {
	window.open("AdapterHTTP?PAGE=RicercaMansionePage&desMansione="+desMansione.value+"&flgFrequente=", "Mansioni", 'toolbar=0, scrollbars=1');          
}

//Gestisce il flag che indica un cambio di tempo
function gestisciFlagTempo(tempo) {
  var tempovalue = tempo.options[tempo.selectedIndex].value;
  o = document.getElementsByName("FLGMODTEMPO");
  flgModTempo = o[0];
  if (tempovalue != tempoiniziale) {
    flgModTempo.value = "S";      
  } else {
    flgModTempo.value = "N";          
  }
}

//Gestisce il flag che indica un cambio di reddito
function gestisciFlagReddito(reddito) {
  var redditovalue = reddito.value;
  o = document.getElementsByName("FLGMODREDDITO");
  flgModReddito = o[0];
  if (redditovalue != redditoiniziale) {
    flgModReddito.value = "S";      
  } else {
    flgModReddito.value = "N";          
  }
}

//abilita e disabilita le ore settimanali
function visualizzaOreSett(orario) {
  var orariovalue = orario.options[orario.selectedIndex].value;
  var labelore = document.getElementById("labelore");
  var numore = document.getElementById("numore");
  if (orariovalue == "TP1" || orariovalue == "N") {
    document.Frm1.numOreSett.value = "";
    labelore.style.display = "none";
    numore.style.display = "none";
  }
  else {
    labelore.style.display = "inline";
    numore.style.display = "inline";
  }   
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
  window.open("AdapterHTTP?PAGE=RicercaCCNLAvanzataPage", "CCNL", 'toolbar=0, scrollbars=1, height=300, width=550');
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

//Funzione di segnalazione per la scelta delle mansioni
function checkMansione(nameMansione) {
	var cod = new String(eval('document.Frm1.' + nameMansione + '.value'));
	return controllaQualificaOnSubmit(nameMansione);
}
//Imposta la combo grado per l'apprendistato
function impostaGrado(){
  alert("impostaGrado");
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

//funzione che effettua prevalorizza il campo hidden del grado la prima volta che si carica la pagina delle trasformazioni
    function riportaGradoPrec() {
       if (document.Frm1.codGradoPrec.value == "") {
          document.Frm1.codGradoPrec.value = document.Frm1.codGrado.options[document.Frm1.codGrado.selectedIndex].value;
	   }
	}
	
function checkCambioGrado()
{ o = document.getElementsByName("CODTIPOMOV");
  tipoMov = o[0].value;
  if (tipoMov == "TRA" && document.Frm1.codMonoTempo.value == "I" ){
      if (document.Frm1.codGrado.value == document.Frm1.codGradoPrec.value)
	  { if (document.Frm1.CODTIPOASS.value == "NO7" || document.Frm1.CODTIPOASS.value == "NB7") {
	        if(confirm("IL grado della mansione NON è stato modificato:\nNON cambiando il grado NON sarà cambiato il tipo di rapporto.\nVuoi continuare?"))
		    {return true;
		    } else return false;
	    }
	    else if ((document.Frm1.CODTIPOASS.value == "NO4") || (document.Frm1.CODTIPOASS.value == "NB6") ){
		    if(confirm("IL grado della mansione NON è stato modificato.\nVuoi continuare?"))
		    {return true;
		    } else return false;
	    } 
	  }
  }
  return true;
}
