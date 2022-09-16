/*
 * customTL.js
 *
 * Questa classe \u00E8 parte integrante della Tag-Lib "customTL"
 *
 * \u00E8 stata eliminata la funzione goConfirmGenericCustomTL
 * che verr\u00E0 implementata direttamente nella jsp
 * viene utilizzata quando la tag di lista viene inserito in un form
 *
 * (c)2003 EngiWeb di Bologna
 * versione aggornata del Set.'04 by GG
 */


// Variabili globali 
  
var _arrFunz = new Array();	// Cresce dinamicamente
var _arrIndex = 0;			// Per memorizzare la posizione corrente

var _lockSubmit = false;	// Per impedire invii successivi della form
var _enableLock = true;		// Se a FALSE, non si esegue il "lock" e neppure
							// la disabilitazione degli elementi della form.
var _debugTL = false;		// Mettere a TRUE per stampare "alert" di debug

     
/**
 * Funzione globale di controllo su "OnSubmit".
 * Viene messa sull'"onSubmit" del tag FORM tramite il tag "af:FORM".
 *   Se gi\u00E0 in submit (flag di "blocco" a true)
 *     Ritorna 'false' senza fare nulla
 *   Altrimenti,
 *     Esegue la validazione dei campi della pagina (se presenti)
 *     In caso che ci sia un errore,
 *       Rende 'false'
 *     Altrimenti (tutto ok),
 *       Disabilita i bottoni della pagina
 *       Attiva il flag di "blocco" (per evitare successivi invii)
 *       Rende 'true'
 *
 * Nota: funziona bene anche se non c'\u00E8 alcun controllo di validazione nella pagina!
 */
function controllaFunzTL() {

	// Se la pagina \u00E8 gi\u00E0 in submit, IGNORO questo nuovo invio!
	if (isInSubmit()) {
		return false;
	}

	if (_debugTL) alert("customTL: invocata controllaFunzTL()");

	prepareSubmit();
	
	if ((_arrFunz != undefined) && (_arrFunz != null)) {
		for (var i=0; i<_arrFunz.length; i++) {
			if (_debugTL) alert("customTL: valuto ["+ _arrFunz[i] +"]");
			var ok = eval(_arrFunz[i]);
			if (!ok) {
				undoSubmit();		// Riabilita tutto
				return false;		// Torna senza fare la 'submit'
			}
		}
	}
	return true;				// Torna dicendo che si puo' fare la 'submit'
} //controllaFunzTL


/**
 * Come la controllaFunzTL ma NON fa la validazione dei campi.
 * Usata per l'onSubmit di una FORM in cui non vanno validati i campi.
 * (si veda attributo "dontValidate" del tag af:form)
 *   Se gi\u00E0 in submit (flag di "blocco" a true)
 *     Ritorna 'false' senza fare nulla
 *   Altrimenti,
 *     Disabilita i bottoni della pagina
 *     Attiva il flag di "blocco" (per evitare successivi invii)
 *     Rende 'true'
 */
function controllaNullaTL() {

	// Se la pagina \u00E8 gi\u00E0 in submit, IGNORO questo nuovo invio!
	if (isInSubmit()) {
		return false;
	}

	if (_debugTL) alert("customTL: invocata controllaNullaTL()");
	
	prepareSubmit();
	return true;				// Torna dicendo che si puo' fare la 'submit'
} //controllaNullaTL



/*
 * Funzione da usare in caso di "submit" fatta a mano (via Javascript).
 * Nel caso che controllaFunzTL() renda TRUE ma che eventuali controlli
 * fatti prima della "submit" vera e propria indichino di annullare la
 * "submit", occorre invocare questa funzione di "undo".
 * E' importante perche' abilita tutti i bottoni disabilitati dalla controllaFunzTL().
 */
function undoSubmit() {
	if (! _enableLock) return;
	
	if (_debugTL) alert("customTL: eseguo undoSubmit()");
	customTL_formFieldsEnabled(true);	// Riabilita tutto
	_lockSubmit = false;
}

/*
 * Usata internamente all'inizio della controllaFunzTL()
 * Prepara per la submit e abilita la flag di LOCK
 */
function prepareSubmit() {
	if (! _enableLock) return;

	customTL_formFieldsEnabled(false);	// Disabilita tutto
	_lockSubmit = true;
}

/*
 * Rende lo stato del "blocco" della submit.
 * Viene impostato/disattivato da prepareSubmit e undoSubmit.
 * Se a TRUE, la prepareSubmit \u00E8 gi\u00E0 stata invocata ma non
 * si \u00E8 ancora passati per un "undo".
 */
function isInSubmit() {
	return _enableLock && _lockSubmit;
}


/* 
 * riportaControlloUtente(boolContrUtente)
 * Funzione usata internamente dal tag di"FORM" per
 * recuperare il controllo messo dall'utente sulla
 * "onSubmit" e eventualmente fare la 'undoSubmit()'.
 */
function riportaControlloUtente(boolContrUtente) {
	if (_debugTL) alert("customTL: eseguo riportaControlloUtente(" + boolContrUtente + ")");
	
	if ((boolContrUtente == undefined) || (boolContrUtente == null))
		boolContrUtente = false;
	
	if (! boolContrUtente) {
		undoSubmit();
	}
	return boolContrUtente;
}

/* --- FUNZIONI PER LA NAVIGAZIONE DELLE PAGINE --- */

function rinfresca(){
	try{
		window.top.footer.rinfresca(); 
	} catch(e) {} 
}
  
  
/*
 * goTo(queryString)
 * Imposta la "window.location" con il valore di "AdapterHTTP?" seguito dalla queryString passata.
 * In piu, esegue la 'prepareSubmit()' per disabilitare ulteriori azioni sulla pagina,
 * e chiede conferma dello spostamento nel caso i dati della pagina fossero cambiati.
 */
function goTo(queryString) {

	prepareSubmit(); 
	try{
		if (flagChanged==true) {
			if (! confirm("I dati sono cambiati.\r\nProcedere lo stesso ?")) {
				undoSubmit();
				return;
			}
		}
	}catch(e) {}
	window.location = "AdapterHTTP?" + queryString;
}

	

/*
 * setWindowLocation(newLocation [, doPrepare])
 * Imposta la "window.location" con il valore di newLocation passato.
 * In piu, esegue la 'prepareSubmit()' per disabilitare ulteriori azioni sulla pagina.
 * Il parametro booleano facoltativo "doPrepare" indica se fare o meno la "prepareSubmit"
 * prima di trasferire il controllo alla nuova pagina (il valore di default \u00E8 TRUE).
 */
function setWindowLocation(newLocation) {
	if (_debugTL) alert("customTL: setWindowLocation()\n con url="+newLocation);

	var doPrepare = true;
	if (setWindowLocation.arguments.length > 1) {
		doPrepare = setWindowLocation.arguments[1];
	}

	if (doPrepare) {
		prepareSubmit();
	}
	window.location = newLocation;
}

/*
 * setOpenerWindowLocation(newLocation [, doPrepare])
 * Imposta la "opener.window.location" (cio\u00E8 la "window.location" della finestra
 * "opener": quella che ha aperto la finestra corrente come una pop-up) con il
 * valore di newLocation passato.
 * In piu, esegue la 'prepareSubmit()' per disabilitare ulteriori azioni sulla pagina.
 * Il parametro booleano facoltativo "doPrepare" indica se fare o meno la "prepareSubmit"
 * prima di trasferire il controllo alla nuova pagina (il valore di default \u00E8 TRUE).
 */
function setOpenerWindowLocation(newLocation) {
	if (_debugTL) alert("customTL: setOpenerWindowLocation()\n con url="+newLocation);

	var doPrepare = true;
	if (setOpenerWindowLocation.arguments.length > 1) {
		doPrepare = setOpenerWindowLocation.arguments[1];
	}

	if (doPrepare) {
		window.opener.prepareSubmit();
	}
	window.opener.location = newLocation;
}


/*
 * doFormSubmit( [formObj [, doPrepare]] )
 * Per eseguire il SUBMIT della form (p.es. passare come parametro "document.miaForm").
 * In piu, esegue la 'prepareSubmit()' per disabilitare ulteriori azioni sulla pagina.
 * Il parametro \u00E8 FACOLTATIVO: se non si specifica un formObj (o se lo si imposta a null
 * o stringa vuota), viene usato quello definito da "document.forms[0]".
 * CONSIGLIO: conviene passare sempre la form da usare, in caso in futuro si aggiungano
 * ulteriori form alla pagina.
 * Il parametro booleano facoltativo "doPrepare" indica se fare o meno la "prepareSubmit"
 * prima di trasferire il controllo alla nuova pagina (il valore di default \u00E8 TRUE).
 */
function doFormSubmit() {
	if (_debugTL) alert("customTL: doFormSubmit()");
	
	var formObj = undefined;
	if (document.forms.length > 0) {
		formObj = document.forms[0];
	}
	if (doFormSubmit.arguments.length > 0) {
		formObj = doFormSubmit.arguments[0];
	}
	if (formObj == undefined) {
		alert("doFormSubmit: impossibile trovare la form data");
		return;
	}
	
	var doPrepare = true;
	if (doFormSubmit.arguments.length > 1) {
		doPrepare = doFormSubmit.arguments[1];
	}

	if (doPrepare) {
		prepareSubmit();
	}
	formObj.submit();	// SUBMIT!
}


/* --- FUNZIONI UTILI (DI USO GENERALE) --- */


// Funzione per il controllo dell'OBBLIGATORIETA' del valore nel campo.

function isRequired(inputName) {

	var ctrlObj = eval("document.forms[0]." + inputName);

	if (ctrlObj.value=="") {
		alert("Il campo " + ctrlObj.title + " \u00E8 obbligatorio");
		ctrlObj.focus();
		return false;
	}
	return true;
} //isRequired


// Funzione per fare il "TRIM" del valore nel campo.

function trimObject(inputName) {

	var ctrlObj = eval("document.forms[0]." + inputName);

	trimTextObject(ctrlObj);

	return true;
} //trimObject


// Funzione che unisce la "trimObject" e la "isRequired" su un campo dato.

function isRiempitoCampo(inputName) {

  trimObject(inputName);
  return isRequired(inputName);

} //isRiempitoCampo


/* Funzione dimostrativa (di base per tutte le funzioni di VALIDAZIONE)
 * NOTA: la funzione accetta come parametro di INPUT una STRINGA contenente
 * il nome dell'input-tag su cui eseguire la validazione. La funz. deve
 * rendere un valore booleano corrispondente all'esito della validazione
 * Essa accede all'oggetto vero tramite l'invocazione di "eval".
 */
function dummyValidate(inputName) {

	var ctrlObj = eval("document.forms[0]." + inputName);
	alert("DUMMY VALIDATE:\nValidazione del campo che ha\nNome=" + inputName + "\nValore=" + ctrlObj.value + "\nTitolo=" + ctrlObj.title);

	return true;
} //dummyValidate


// Funzioni di VALIDAZIONE

function validateDate(inputName) {
	var ctrlObj = eval("document.forms[0]." + inputName);

	var ok = checkFormatDate(ctrlObj);

	if (!ok) {
		alert("Data non corretta nel campo " + ctrlObj.title);
		ctrlObj.focus();
	}

	return ok;
}

// FV 7/10/2003
function validateTime(inputName) {
	var ctrlObj = eval("document.forms[0]." + inputName);

	var ok = checkFormatTime(ctrlObj);

	if (!ok) {
		alert("Orario non corretto nel campo " + ctrlObj.title);
		ctrlObj.focus();
	}

	return ok;
}

function validateInteger(inputName) {
	var ctrlObj = eval("document.forms[0]." + inputName);

  if (ctrlObj.value == "")
    return true;    // va bene se non ho inserito nulla
  else {
    var ok = isInteger(ctrlObj.value);

    if (!ok) {
      alert("Numero non corretto nel campo " + ctrlObj.title);
      ctrlObj.focus();
    }
    return ok;
  }
}

function validateFloat(inputName) {
	var ctrlObj = eval("document.forms[0]." + inputName);

  if (ctrlObj.value == "")
    return true;    // va bene se non ho inserito nulla
  else {
    var ok = isFloat(ctrlObj.value);   // usa virgola come punto (".")

    if (!ok) {
      alert("Numero non corretto nel campo " + ctrlObj.title);
      ctrlObj.focus();
    }
    return ok;
  }
}

function validateFixDecimal(inputName) {
	var ctrlObj = eval("document.forms[0]." + inputName);
	var numDDigit = 2;
	var re = new RegExp(",","g");

  if (ctrlObj.value == "")
    return true;    // va bene se non ho inserito nulla
  else {

    ctrlObj.value = ctrlObj.value.replace(re,".");

    var s = ctrlObj.value.split(".");
    
    if (s.length == 1) {
	    return validateInteger(inputName);
    }
    if (s.length == 2) {
      if (eval(s[1].length > numDDigit)) {
	      alert("Troppe cifre decimali nel campo " + ctrlObj.title +".\nSono ammesse al massimo "+numDDigit+" cifre decimali");
	      ctrlObj.focus();
          return false;
      }
    } else {
	      alert("Numero non corretto nel campo " + ctrlObj.title);
	      ctrlObj.focus();
	      return false;
    }

    ok = isFloat(ctrlObj.value);   // usa virgola come punto (".")

    if (!ok) {
      alert("Numero non corretto nel campo " + ctrlObj.title);
      ctrlObj.focus();
    }
    return ok;
  }
}

function validateCodiceFiscale(inputName) {
	var ctrlObj = eval("document.forms[0]." + inputName);

  if (ctrlObj.value != "") {    // va bene se non ho inserito nulla
    if (! IsCodFiscale(ctrlObj.value)) {
      alert("Codice fiscale non corretto nel campo " + ctrlObj.title);
      ctrlObj.focus();
      return false;
    }
  }
  return true;
}

function validateCodiceFiscaleConRichiesta(inputName) {
	var ctrlObj = eval("document.forms[0]." + inputName);

  if (ctrlObj.value != "") {    // va bene se non ho inserito nulla
    if (! IsCodFiscale(ctrlObj.value)) {
      var continuo = confirm("Codice fiscale non corretto nel campo " + ctrlObj.title +
                             "\nLo uso comunque?");
      if (!continuo) {
        ctrlObj.focus();
        return false;
      }
    }
  }
  return true;
}

function validatePartitaIva(inputName) {
	var ctrlObj = eval("document.forms[0]." + inputName);

  if (ctrlObj.value != "") {    // va bene se non ho inserito nulla
    if (! IsPartitaIva(ctrlObj.value)) {
      alert("Partita IVA non corretta nel campo " + ctrlObj.title);
      ctrlObj.focus();
      return false;
    }
  }
  return true;
}

function validatePartitaIvaConRichiesta(inputName) {
	var ctrlObj = eval("document.forms[0]." + inputName);

  if (ctrlObj.value != "") {    // va bene se non ho inserito nulla
    if (! IsPartitaIva(ctrlObj.value)) {
      var continuo = confirm("Partita IVA non corretta nel campo " + ctrlObj.title +
                             "\nLa uso comunque?");
      if (!continuo) {
        ctrlObj.focus();
        return false;
      }
    }
  }
  return true;
}


/* --- PARTE PRIVATA --- */

/*
 * Abilita/disabilita tutti i bottoni di tutte le form presenti nella pagina
 * E' DA CONSIDERARSI "PRIVATA": NON VA CHIAMATA DALL'ESTERNO!
 * by Luigi Antenucci
 */
function customTL_formFieldsEnabled(enabled) {

	// PROBLEMA: se si fa un DOWNLOAD (es. di un documento) dopo si perde il "document"!
	if (typeof(document) == "unknown") return;

	for (var f=0; f<document.forms.length; f++) {   // Per tutte le form
		var form = document.forms[f];

		for (var e=0; e<form.length; e++) {   // Per tutti gli elementi della form
			var elem = form.elements[e];

			// controllo il TYPE dell'elemento:
			if  (elem.type) {
				if ((elem.type == "button") ||
				    (elem.type == "submit") ||
				    (elem.type == "reset"))
					customTL_fieldEnabled(elem, enabled);
			}
		}
	}
}

function customTL_fieldEnabled(objCampo, enabled) {
	// NON SE NE FA IL "DISABLED" PERCHE' MOLTE "PAGE" FANNO
	// DEI CONTROLLI SULLA PRESENZA DEI BOTTONI PER DECIDERE
	// QUALE AZIONE FARE (VEDANSI TAG DI "CONDITIONS").
	// SI E' OPTATO PER USARE UNA FLAG DI "lock" CON
	// L'AGGIUNTA DEL CAMBIO DELLO STILE DEI BOTTONI

	// Cambio lo stile dell'oggetto
	var className = objCampo.className;
	var pos = className.indexOf("Disabled");
	if (enabled) {
		if (pos >= 0) {
			// Rimuovo il "Disabled" (se c'\u00E8)
			className = className.substring(0,pos);
		}
	}
	else {
		if (pos == -1) {
			// Aggiungo il "DIsabled" (se non c'\u00E8 gi\u00E0)
			className += "Disabled";
		}
	}
	objCampo.className = className;
}        
        