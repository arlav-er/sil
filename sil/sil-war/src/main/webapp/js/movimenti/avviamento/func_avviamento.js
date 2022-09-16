var finestraApprendistato;
var finestraTirocinio;

//Funzione di segnalazione per la scelta delle mansioni
function checkMansione(nameMansione) {
	var cod = new String(eval('document.Frm1.' + nameMansione + '.value'));
	if (cod.substring(4, 6) == '00') {
		if (confirm("Non è stata indicata una mansione specifica. Continuare?")) {
			return controllaQualificaOnSubmit(nameMansione);
		}
	} else return controllaQualificaOnSubmit(nameMansione);
}

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

//Controlla che il campo numOreSettimanali non sia nullo in caso di part-time
function checkNumOreSettimanali() 
{ 
  var orariovalue = document.Frm1.codOrario.value;
  if(!sospesoJS){
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
	  
	  if (orariovalue == "N") {
 		var tipoContratto = document.Frm1.codTipoAss.value;
 		if (tipoContratto != 'NB4' &&
 		    tipoContratto != 'IN2' &&
 		    tipoContratto != 'T1' &&
 		    tipoContratto != 'NU5') {
 			alert("Il codice relativo al tipo di contratto non compatibile con l'orario");
 			return false;  
 		}
 	}
  }
  return true;
}

function checkCampoObbligatorio(){
	var campoObb = document.Frm1.codTipoAss.value;
  		if (campoObb == "NB7" && document.Frm1.codNormativa.value==''){ 
		alert("Il campo Normativa/Motivo di utilizzo è obbligatorio");
		return false;
	 }
	 return true;
}



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
  var tipoAvviamento = document.Frm1.codTipoAss.value;
  if(tipoAvviamento == "NO7" || tipoAvviamento == "NB7"){
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


function visualizzaPulsanteApprendistato(codMonoTipo){
  	var obj = document.getElementById("appButton");
  	var objTir = document.getElementById("appButtonTirocinio");
  	var objSezioneSRQ1 = document.getElementById("sezioneSRQ1");
	var objSezioneSRQ2 = document.getElementById("sezioneSRQ2");
  	if (codMonoTipo == "A") {
    	obj.style.display = "inline";
    	objTir.style.display = "none";
    	if (objSezioneSRQ1 != null) objSezioneSRQ1.style.display = "inline";
    	if (objSezioneSRQ2 != null) objSezioneSRQ2.style.display = "inline";
  	} else {
    	obj.style.display = "none";
      	if (objSezioneSRQ1 != null) objSezioneSRQ1.style.display = "none";
      	if (objSezioneSRQ2 != null) objSezioneSRQ2.style.display = "none";
      	if (document.Frm1.CODQUALIFICASRQ != null) document.Frm1.CODQUALIFICASRQ.value = "";
      	if (document.Frm1.DESCQUALIFICASRQ != null) document.Frm1.DESCQUALIFICASRQ.value = "";
      	if (codMonoTipo == "T") {
      		objTir.style.display = "inline";
      	}
      	else {
      		objTir.style.display = "none";
      	}
	}
}

function checkTipoQualificaSRQ(){
	var campoObb = document.Frm1.codTipoAss.value;
	if (campoObb == "NB7" && document.Frm1.CODQUALIFICASRQ.value=='' && codRegioneProv == "8") { 
		alert("Il campo Qualifica SRQ è obbligatorio");
		return false;
	}
	return true;
}

//Aggiorna le combo collegate quando avviene qualche cambiamento nelle combo da cui dipendono
function refreshComboCollegate() {   
  args = '&CODTIPOAZIENDA=' + codTipoAzienda + '&CODNATGIURIDICA=' + codNatGiuridicaAz;
  args = args + '&CODMONOTEMPO='+ document.Frm1.codMonoTempo.value;
  args = args + '&CODTIPOASS='+ document.Frm1.codTipoAss.value; 
  refreshCombo('ComboNormativaSelettiva', 'Frm1.codNormativa', args);
  refreshCombo('ComboAgevolazioneSelettiva', 'Frm1.codAgevolazione', args);      
}

//Cerca il tipo di assunzione
function cercaTipoAss(criterio){
  var f;
  var descr;
  if (codTipoAzienda=="INT" && document.Frm1.FLGINTERASSPROPRIA.value=="S"){
 	f = "AdapterHTTP?PAGE=SelezionaTipoAssSelettivaPage&CODTIPOAZIENDA=AZI" + "&CODNATGIURIDICA=" + codNatGiuridicaAz + "&codMonoTempo=" + document.Frm1.codMonoTempo.value;
  }
  else {
   	f = "AdapterHTTP?PAGE=SelezionaTipoAssSelettivaPage&CODTIPOAZIENDA=" + codTipoAzienda + "&CODNATGIURIDICA=" + codNatGiuridicaAz + "&codMonoTempo=" + document.Frm1.codMonoTempo.value;
  }
  
  f = f + "&CRITERIO=" + criterio;
  f = f + "&codTipoAss=" + document.Frm1.codTipoAss.value;
  //aggiunto questo controllo perchè la descrizione del codice NU4 ovvero "Numerica riservatari 12%  (ord.)(scaduto)"
 // contiene il carattere % che crea problemi nella query string 
  descr = document.Frm1.descrTipoAss.value;
  descr = descr.replace("%","&amp;");
  f = f + "&descrTipoAss=" + descr;
  f = f + "&updateFunctionName=aggiornaTipoAss";
  //D'Auria Giovanni 10/03/2005 inizio
  f = f + "&codCCNLAz=" + document.Frm1.codCCNL.value;
  //D'Auria Giovanni 10/03/2005 fine
  var t = "_blank";
  var feat = "toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,width=500,height=400,top=100,left=100";
  window.open(f, t, feat);
} 

//Aggiorna il tipo di assunzione e le combo collegate
 function aggiornaTipoAss(codice, descrizione,codmonotipo,codContratto, mesiApprendistato) {
  document.Frm1.codTipoAss.value = codice;
  document.Frm1.descrTipoAss.value = descrizione;
  document.Frm1.CODMONOTIPO.value = codmonotipo;
  document.Frm1.CODCONTRATTO.value = codContratto;
  document.Frm1.NUMMESIAPPRENDISTATO.value = mesiApprendistato;
   
  refreshComboCollegate();
  //Gestione cfl al cambio del tipo di assunzione
  //visualizzaPulsanteCFL(codice);
  //resetCFL();
  visualizzaPulsanteApprendistato(codmonotipo);
  impostaGrado();
 }

//Apre la selezione dell'apprendistato
function apriApprendistato(){
	var codCCNLAzi=document.Frm1.codCCNL.value;
	if(codCCNLAzi == ""){
		alert('Il campo CCNL è obbligatorio');
		return false;
	}
	var f = "AdapterHTTP?PAGE=MovApprendistatoPage";
	if(document.Frm1.CURRENTCONTEXT.value != ""){
      f = f + "&CURRENTCONTEXT=" + document.Frm1.CURRENTCONTEXT.value;
    }
    if (document.Frm1.PRGMOVIMENTO != null && document.Frm1.CURRENTCONTEXT.value == "consulta"){
        f = f + "&PRGMOVIMENTO=" + document.Frm1.PRGMOVIMENTO.value;
    } else if (document.Frm1.PRGMOVIMENTOAPP != null && (document.Frm1.CURRENTCONTEXT.value == "valida" || document.Frm1.CURRENTCONTEXT.value == "validaArchivio")) {
        f = f + "&PRGMOVIMENTOAPP=" + document.Frm1.PRGMOVIMENTOAPP.value;
    }
    if (document.Frm1.flgCambiamentiDati != null){
      f = f + "&flgCambiamentiDati=" + document.Frm1.flgCambiamentiDati.value;
    }
    if (document.Frm1.STRMANSIONETUTORE != null){
      f = f + "&STRMANSIONETUTORE=" + document.Frm1.STRMANSIONETUTORE.value;
    }
    if (document.Frm1.STRTIPOMANSIONETUTORE != null){
      f = f + "&STRTIPOMANSIONETUTORE=" + document.Frm1.STRTIPOMANSIONETUTORE.value;
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
    f = f + "&flgTitolareTutore=" + document.Frm1.FLGTITOLARETUTORE.value;
    f = f + "&numAnniEspTutore=" + document.Frm1.NUMANNIESPTUTORE.value;
    f = f + "&strLivelloTutore=" + document.Frm1.STRLIVELLOTUTORE.value;
    f = f + "&CODMANSIONETUTORE=" + document.Frm1.CODMANSIONETUTORE.value;
    f = f + "&datVisitaMedica=" + document.Frm1.DATVISITAMEDICA.value;
    f = f + "&strNote=" + document.Frm1.STRNOTE.value;
    f = f + "&numMesiApprendistato=" + document.Frm1.NUMMESIAPPRENDISTATO.value;
    f = f + "&flgArtigiana=" + document.Frm1.FLGARTIGIANA.value;
   	f = f + "&codCCNLAz=" + document.Frm1.codCCNL.value;
	var t = "_blank";
    var feat = "toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,width=800,height=550,top=70,left=100";
    finestraApprendistato = window.open(f, t, feat);
}

//Funzione di aggiornamento valori dell'apprendistato
function AggiornaApprendistato(){
  document.Frm1.STRCOGNOMETUTORE.value = finestraApprendistato.document.Frm1.STRCOGNOMETUTORE.value;
  document.Frm1.STRNOMETUTORE.value = finestraApprendistato.document.Frm1.STRNOMETUTORE.value;
  document.Frm1.STRCODICEFISCALETUTORE.value = finestraApprendistato.document.Frm1.STRCODICEFISCALETUTORE.value;
  document.Frm1.FLGTITOLARETUTORE.value = finestraApprendistato.document.Frm1.FLGTITOLARETUTORE.value;
  document.Frm1.NUMANNIESPTUTORE.value = finestraApprendistato.document.Frm1.NUMANNIESPTUTORE.value;
  document.Frm1.STRLIVELLOTUTORE.value = finestraApprendistato.document.Frm1.STRLIVELLOTUTORE.value;
  document.Frm1.CODMANSIONETUTORE.value = finestraApprendistato.document.Frm1.CODMANSIONE.value;
  document.Frm1.STRTIPOMANSIONETUTORE.value = finestraApprendistato.document.Frm1.strTipoMansione.value;
  document.Frm1.STRMANSIONETUTORE.value = finestraApprendistato.document.Frm1.DESCMANSIONE.value;
  document.Frm1.DATVISITAMEDICA.value = finestraApprendistato.document.Frm1.DATVISITAMEDICA.value;
  document.Frm1.NUMMESIAPPRENDISTATO.value = finestraApprendistato.document.Frm1.NUMMESIAPPRENDISTATO.value;
  document.Frm1.FLGARTIGIANA.value = finestraApprendistato.document.Frm1.FLGARTIGIANA.value;
  document.Frm1.STRNOTE.value = finestraApprendistato.document.Frm1.STRNOTE.value;
  if(document.Frm1.flgCambiamentiDati != null){
    document.Frm1.flgCambiamentiDati.value = finestraApprendistato.document.Frm1.flgCambiamentiDati.value;
  }
  finestraApprendistato.close();
}

//funzione che effettua il cambio del campo hidden del grado quando cambia il valore della combo visualizzata
function riportaGradoHidden() {
	document.Frm1.codGrado.value = document.Frm1.codGradoVisualizz.options[document.Frm1.codGradoVisualizz.selectedIndex].value;
}

//Imposta la combo grado per l'apprendistato
function impostaGrado(){
  o = document.getElementsByName("CODMONOTIPO");
  grado = o[0].value;  
  if (grado == "A") {
  	//Ritrova l'indice che corrisponde all'apprendistato
  	opt = document.Frm1.codGradoVisualizz.options;
  	var index = 1;
  	// 15 = apprendista
  	var codGrado = "15";
  	for (var i=0; i < opt.length; i++) {if (opt[i].value == codGrado) {index = i;}}
    document.Frm1.codGradoVisualizz.selectedIndex=index;
    document.Frm1.codGradoVisualizz.disabled=true;
    riportaGradoHidden();
  }
  else{
    document.Frm1.codGradoVisualizz.disabled=false;
  }
}

//Controllo che il codTipoAss esista prima dell'invio della form
function checkTipoAss(nomeTipoAss) {
	// AndSav. venerdi' 17/02/2006: il codice deve essere maiuscolo
  document.Frm1.codTipoAss.value = document.Frm1.codTipoAss.value.toUpperCase();
  var f;
  var descr;
  if (codTipoAzienda=="INT" && document.Frm1.FLGINTERASSPROPRIA.value=="S"){
    f = "AdapterHTTP?PAGE=ControllaTipoAssXMLPage&CODTIPOAZIENDA=AZI" + "&CODNATGIURIDICA=" + codNatGiuridicaAz + "&codMonoTempo=" + document.Frm1.codMonoTempo.value;
  }
  else {
    f = "AdapterHTTP?PAGE=ControllaTipoAssXMLPage&CODTIPOAZIENDA=" + codTipoAzienda + "&CODNATGIURIDICA=" + codNatGiuridicaAz + "&codMonoTempo=" + document.Frm1.codMonoTempo.value;
  }
  f = f + "&CRITERIO=codice";
  f = f + "&codTipoAss=" + document.Frm1.codTipoAss.value.toUpperCase();
  //aggiunto questo controllo perchè la descrizione del codice NU4 ovvero "Numerica riservatari 12%  (ord.)(scaduto)"
  // contiene il carattere % che crea problemi nella query string 
  descr = document.Frm1.descrTipoAss.value;
  descr = descr.replace("%","&amp;");
  f = f + "&descrTipoAss=" + descr;
  //f = f + "&descrTipoAss=" + document.Frm1.descrTipoAss.value;
  //f = f + "&updateFunctionName=aggiornaTipoAss";
  //D'Auria Giovanni 10/03/2005 inizio
  f = f + "&codCCNLAz=" + document.Frm1.codCCNL.value;
  //D'Auria Giovanni 10/03/2005 fine

  //Controllo sul server
	var result = syncXMLHTTPGETRequest(f);
	if (result == null || result.responseXML.documentElement == null || (result.responseXML.documentElement.tagName != "ROW")) {
		alert("Il codice '"+document.Frm1.codTipoAss.value+"' relativo al 'Tipo di assunzione' \nè incompatibile con i dati inseriti nel movimento o errato.");
	   return false;
	} else {
	  /*alert("OK i dati sono stati recuperati!\n"+strNotNull(row.getAttribute("codmonotipo"))+
      "\n"+strNotNull(row.getAttribute("codContratto"))+"\n"+row.getAttribute("descrizione"));*/
	  //document.Frm1.codTipoAss.value   = strNotNull(row.getAttribute("codice"));
	  //document.Frm1.descrTipoAss.value = strNotNull(row.getAttribute("descrizione"));
	  //document.Frm1.CODMONOTIPO.value  = strNotNull(row.getAttribute("codmonotipo"));
	  //document.Frm1.CODCONTRATTO.value = strNotNull(row.getAttribute("codContratto"));
	  //document.Frm1.NUMMESIAPPRENDISTATO.value = ???; In questo caso NON viene valorizzato.

      return true;
	}
	
}

/*function checkTipoAss(nomeTipoAss)	{
    //Recupero dell'elemento che contiene il codice
	var field = eval('document.Frm1.' + nomeTipoAss);
	var codTipoAss = new String(field.value);
	//Lo setto maiuscolo e lo reimposto nella form
	codTipoAss = codTipoAss.toUpperCase();
	field.value = codTipoAss;
	var exist = false;
	try {
		exist = controllaEsistenzaChiave(codTipoAss, "CODTIPOASS", "DE_MV_TIPO_ASS");
	} catch (e) {
		return confirm("Impossibile controllare che il tipo di assunzione " + codTipoAss + " esista, proseguire comunque?");
	}
	if (!exist) {
		alert("Il codice del tipo di assunzione " + codTipoAss + " non esiste");
		return false;
	} else return true;
}*/

function mostraCampiAgricoltura() {

  if(document.Frm1.NUMGGPREVISTIAGR.value != null && document.Frm1.NUMGGPREVISTIAGR.value > 0) {
      document.getElementById("lavorazione").style.display = "inline";
      document.getElementById("categoria").style.display = "inline";
  } else {
     document.getElementById("lavorazione").style.display = "none";
     document.getElementById("categoria").style.display = "none";
  }
}

function copiaValue(objDaCopiare, objInCuiCopiare) {
  if(objDaCopiare.value!=null && objInCuiCopiare!=null) {
     //alert("Copia da "+objDaCopiare.name+"  il valore:"+objDaCopiare.value+"\n a "+objInCuiCopiare.name);
     objInCuiCopiare.value = objDaCopiare.value;
  }
}

function controlloCampiAgricoltura() {
	return true;
}

function selezionaPulsanteApprendistato() {
	if((document.Frm1.codTipoAss.value == "NB7") && (document.Frm1.STRCOGNOMETUTORE.value == "" || document.Frm1.NUMMESIAPPRENDISTATO.value == "") ) {
		alert("Accedere alla sezione dell'apprendistato per completare l'inserimento dei dati");
		return false;
	}
	var campoCodMonoTipo = document.Frm1.CODMONOTIPO.value;
	if ( (campoCodMonoTipo.toUpperCase() == 'T') && (codRegioneProv == '8') ) {
		if ( (document.Frm1.PRGAZIENDAUTILIZ != null && document.Frm1.PRGAZIENDAUTILIZ.value == '') || (document.Frm1.STRAZINTNUMCONTRATTO != null && document.Frm1.STRAZINTNUMCONTRATTO.value == '') || (document.Frm1.DATAZINTINIZIOCONTRATTO != null && document.Frm1.DATAZINTINIZIOCONTRATTO.value == '') ) {
			alert("Accedere alla sezione azienda utilizzatrice/ente promotore per completare l'inserimento dei dati. Specificare ente promotore, numero e data convenzione");
			return false;
		}
		if (document.Frm1.STRCOGNOMETUTORE.value == '' || document.Frm1.STRNOMETUTORE.value == '') {
			alert("Accedere alla sezione del tirocinio per completare l'inserimento dei dati. Specificare cognome e nome tutore");
			return false;
		}	
	}
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

//ricerca qualifica SRQ con pulsante di lookup
function selectQualificaSRQOnClick(codQualificaSrq, descQualificaSrq, tipoRicerca) {
	if (tipoRicerca == 'codice') {
		if (codQualificaSrq.value == "") {
			descQualificaSrq.value = "";	
		}
		else {
			window.open("AdapterHTTP?PAGE=RicercaQualificaSRQPage&tipoRicerca=codice&CODQUALIFICASRQ="+codQualificaSrq.value, "Ricerca", 'toolbar=0, scrollbars=1');
		}
	}
	else {
		if (tipoRicerca == 'descrizione') {
			if (descQualificaSrq.value != "") {
				window.open("AdapterHTTP?PAGE=RicercaQualificaSRQPage&tipoRicerca=descrizione&descQualificaSrq="+descQualificaSrq.value, "Ricerca", 'toolbar=0, scrollbars=1');
			}
		}
	} 
  	
}

function ricercaAvanzataQualificaSRQ() {
	var t="AdapterHTTP?PAGE=RicercaQualificaSRQAvanzataPage";
	window.open(t, "Ricerca", 'toolbar=0, scrollbars=1');
}

function ControllaInserimento(){
	if (document.Frm1.codTipoAss.value == "NB7" || document.Frm1.codTipoAss.value == "NO7"){
     	if(!confirm("Attenzione: le comunicazioni di Apprendistato dovrebbero arrivare tramite SARE.Vuoi registrare manualmente il movimento?")){
        	 return false;
        }
    }    
    return true;
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
    f = f + "&updateFunctionName=AggiornaTirocinio";
    f = f + "&strCognomeTutore=" + document.Frm1.STRCOGNOMETUTORE.value;
    f = f + "&strNomeTutore=" + document.Frm1.STRNOMETUTORE.value;
    f = f + "&strCodiceFiscaleTutore=" + document.Frm1.STRCODICEFISCALETUTORE.value;
	var t = "_blank";
    var feat = "toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,width=800,height=550,top=70,left=100";
    finestraTirocinio = window.open(f, t, feat);
}

//Funzione di aggiornamento valori del tirocinio
function AggiornaTirocinio() {
  	document.Frm1.STRCOGNOMETUTORE.value = finestraTirocinio.document.Frm1.STRCOGNOMETUTORE.value;
  	document.Frm1.STRNOMETUTORE.value = finestraTirocinio.document.Frm1.STRNOMETUTORE.value;
  	document.Frm1.STRCODICEFISCALETUTORE.value = finestraTirocinio.document.Frm1.STRCODICEFISCALETUTORE.value;
  	finestraTirocinio.close();
}
