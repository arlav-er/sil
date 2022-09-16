//--------------------------------------------------------------------------------------
// SCRIPT PER L'APERTURA E LA GESTIONE DELLE FINESTRE DI LOOKUP
// Versione 1.0 - by Luigi Antenucci
// (alcune funzioni sono state prese da "script_comuni" e "modernizzate" per l'Application Framework)
// (il nome delle funzioni è stato cambiato per evitare conflitti con "script_comuni")
// Si usa in unione al file "jsp/global/lookupFormData.inc"
//
// ESEMPIO DI USO:
// ==============
//   
// ---CHIAMANTE ---
//     <SCRIPT language="JavaScript" src="../js/lookup.js"></SCRIPT>
//     <SCRIPT language="JavaScript">
//     function lookupComuneCoinvolto() {
//   
//       var queryString =  "proCod=" + document.form.proCod.value +
//                         "&comCod=" + document.form.comCod.value;
//   
//       apriLookup("lookupComunePage", "resultfunComuneCoinvolto", queryString);
//     }
//   
//     function resultfunComuneCoinvolto(comCod, comDesc, proCod, proDesc) {
//   
//       document.form.comCod.value  = comCod;
//       document.form.comDesc.value = comDesc
//       document.form.proCod.value  = proCod;
//       document.form.proDesc.value = proDesc;
//     }
//     </SCRIPT>
//   
//      <input type="BUTTON" class="PulsanteVerde" value="?" title="Cambia comune coinvolto"
//             onClick="lookupComuneCoinvolto()">
//
//   
// ---LOOKUP---
//   
//     <SCRIPT language="JavaScript" src="../js/lookup.js"></SCRIPT>
//     <SCRIPT language="JavaScript">
//   
//     function eseguiRiportaDati() {
//   
//       riportaDatiLookup (document.form.comCod.value,
//                          document.form.comCod.options[document.form.comCod.selectedIndex].text,   // = comDesc
//                          document.form.proCod.value,
//                          document.form.proCod.options[document.form.proCod.selectedIndex].text    // = proDesc
//     );
//   
//   
//     function aggiornaComboComuni() {
//       document.form.PAGE.value = "<%= (String)serviceRequest.getAttribute("PAGE") %>";
//       rinfrescaQuestaFinestraLookup(document.forms[0]);
//     }
//     </SCRIPT>
//   
//   <FORM ..>
//   <%@ include file ="../global/lookupFormData.inc" %>
//   
//   <INPUT type="BUTTON" value="Conferma" onClick="eseguiRiportaDati()">
//   </FORM>
//--------------------------------------------------------------------------------------

var ENABLE_MODAL = false;
var ENABLE_EVENT = false;

var CONTROL_SERVLET = "AdapterHTTP?";

/*
 * apriLookup(PAGE, resultfun [, queryString [, formObject]])
 * Apre una pagina di lookup in una nuova finestra modale
 * PARAMETRI PAGE = nome del PAGE da eseguire
 *           resultfun = nome della funzione javascript (contenuta nella pagina "opener")
 *                       che verrà invocata per "riportare" al chiamate i valori scelti
 *           queryString  = "query string" da aggiungere all'URL di chiamata
 *           formObject   = oggetto "form" contenente i campi di ricerca per la Lookup
 *                          (i dati vengono presi e passati nel QueryString)
 */
function apriLookup(PAGE, resultfun) {

  var _url = CONTROL_SERVLET + "PAGE=" + PAGE + "&resultfun=" + resultfun;

  if (arguments.length > 2) {
    var queryString = arguments[2];
    _url = _url + "&" + queryString;

    if (arguments.length > 3) {
      var formObject = arguments[3];
      var _qs = creaQueryStringDaForm(formObject);
      _url = _url + "&" + _qs;
    }
  }
  // alert("_url := "+_url);
  apriFinestraModale(_url, PAGE);   // imposto NomeFin:=PAGE
}

/*
 * apriLookupQueryString(queryString)
 * Apre una pagina di lookup in una nuova finestra modale.
 * Tutti i parametri sono contenuti in "queryString", compreso "PAGE" e "resultfun"
 */
function apriLookupQueryString(queryString) {

  var _url = CONTROL_SERVLET + queryString;

  apriFinestraModale(_url);   // Usa il "vecchio" NomeFin
}

/*
 * rinfrescaQuestaFinestraLookup(formObject)
 * Rinfresca la pagina di lookup CORRENTE: viene chiamata dalla finestra di lookup medesima!
 * Il parametro "formObject" contiene l'oggetto FORM corrente da cui prelevare tutti i dati
 * per eseguire il ricaricamento! NB: deve contenere anche il "PAGE" e il "resultfun".
 */
function rinfrescaQuestaFinestraLookup(formObject) {

    var myQS = creaQueryStringDaForm(document.forms[0]);

    if (window.showModalDialog && ENABLE_MODAL) {
      chiudiFinestraModale();   // chiude fin. corrente
      window.top.dialogArguments.apriLookupQueryString(myQS);   // NB: invoca sulla finestra PADRE!
    }
    else {
      // non serve chiudere la finestra corrente: si autoritraccerà sopra! (ha lo stesso NOME)
      window.top.opener.apriLookupQueryString(myQS);   // NB: invoca sulla finestra PADRE!
    }
}

/*
 * getResultfun()
 * Rende il valore di "resultfun" corrente, cioè il nome della funzione di ritorno da invocare.
 */
function getResultfun() {
  if (document.forms[0].resultfun != undefined)
    return  document.forms[0].resultfun.value;
  else
    return  "";
}


/*
 * riportaDatiLookup([parametri|..])
 * Invoca la funzione JS "resultfun()" (impostata correntemente nella pagina -- vedi "getResultfun()")
 * della finestra padre dandogli tutti gli eventuali parametri passati a questa funzione.
 */
function riportaDatiLookup() {

  var resultfun = getResultfun();

  if ((resultfun != null) && (resultfun != "")) {

    // Creo la stringa per la CHIAMATA alla funzione di ritorno (con tutti i parametri)
    var resultfunCall = resultfun + "(";
    var separatore = "";
    for (var i=0; i<arguments.length; i++) {
      resultfunCall += separatore + "\"" + arguments[i] + "\"";
      separatore = ", ";
    }
    resultfunCall += ");";

    // alert("resultfunCall == " + resultfunCall);

    if (window.showModalDialog && ENABLE_MODAL) {
        // GG: in "dialogArguments" quando ho aperto la finestra ho passato "window" del chiamante!
        resultfunCall = "window.top.dialogArguments." + resultfunCall;
    }
    else {
        resultfunCall = "window.top.opener." + resultfunCall;
    }

    // VALUTAZIONE! Eseguo la chiamata alla funzione di ritorno
    eval(resultfunCall);

    chiudiFinestraModale();
  }
}


/*
 * riportaDatiLookupResultfun([resultfun [, parametri|..]])
 * Invoca la funzione JS "resultfun()" (passata eventualmente nel primo parametro come STRINGA)
 * della finestra padre dandogli tutti gli eventuali parametri passati in più a questa funzione.
 * Se non viene passata la "resultfun" verrà usata quella definita in "document.forms[0].resultfun.value"
 */
function riportaDatiLookupResultfun() {

  var resultfun = "";
  if (arguments.length > 1) {
    resultfun = arguments[1];
  }
  else {
    resultfun = getResultfun();
  }

  if ((resultfun != null) && (resultfun != "")) {

    // Creo la stringa per la CHIAMATA alla funzione di ritorno (con tutti i parametri)
    var resultfunCall = resultfun + "(";
    var separatore = "";
    for (var i=1; i<arguments.length; i++) {
      resultfunCall += separatore + "\"" + arguments[i] + "\"";
      separatore = ", ";
    }
    resultfunCall += ");";

    // alert("resultfunCall == " + resultfunCall);

    if (window.showModalDialog && ENABLE_MODAL) {
        // GG: in "dialogArguments" quando ho aperto la finestra ho passato "window" del chiamante!
        resultfunCall = "window.top.dialogArguments." + resultfunCall;
    }
    else {
        resultfunCall = "window.top.opener." + resultfunCall;
    }

    // VALUTAZIONE! Eseguo la chiamata alla funzione di ritorno
    eval(resultfunCall);

    chiudiFinestraModale();
  }
}


/*
 * creaQueryStringDaForm(form)
 * Crea la queryString (GET) per un dato oggetto "form"
 *  PARAMETRI:  form = oggetto form contenente i campi/bottoni
 */
function creaQueryStringDaForm(form) {
  var qs = "";
  //passaggio dei campi del form nella request(NB: gli spazi vanno convertiti in '+')
  var separatore = "";
  for (var i=0; i<form.elements.length; i=i+1) {
    if ((form.elements[i].type.toUpperCase() != "BUTTON") &&
        (form.elements[i].value != null) &&
        (form.elements[i].value != "")   ) {

          var _name  = escape(form.elements[i].name);
          var _value = escape(form.elements[i].value);
          qs = qs + separatore + _name + "=" + _value;
          separatore ="&";
    }
  }
  return qs;
}

/*
 * getParametroDaQueryString(queryString, nomePar)
 * Cerca nella "queryString" data il parametro "nomePar" e ne rende il valore (come stringa).
 * Se non lo trova rende stringa vuota.
 */
function getParametroDaQueryString(queryString, nomePar) {
  // Devo rintracciare la stringa "nomePar=value" e prelevare la parte di "valore".
  // es: cerco "pippo" in "pluto=bello&pippo=stupido&paperone=ricco"
  //                                   ^     ^      ^
  //                               begin  beginVal  endVal

  var nome = escape(nomePar) + "=";   // crea la stringa da cercare!
  var valore = "";
  if (queryString.length > 0) {
    // Cerco la stringa: prima con "&" davanti e se non la trovo provo a inizio queryString.
    var begin = queryString.indexOf ("&" + nome);
    var beginVal = -1;
    if (begin >= 0) {   // Se trovato: calcolo l'inizio della parte di valore
      beginVal = begin + nome.length + 1;     // "1" per il "&"
    }
    else {              // Se non trovato, provo a inizio stringa
      var inizioQS = queryString.substring(0,nome.length);
      if (inizioQS == nome) {
        beginVal = nome.length;
      }
    }
    
    if (beginVal >= 0) {   // Se trovato, recupero il valore
      // Cerco la fine: mi sposto a inizio valore e cerco il prossimo "&"
      var endVal = queryString.indexOf ("&", beginVal);
      if (endVal == -1) {  // potrebbe essere a fine queryString
        endVal = queryString.length;
      }
      valore = unescape ( queryString.substring (beginVal, endVal) );
    }
  }
  return valore;
}


/*
 * disegnaBottoneLookup(onClickStr, tooltip, descrizione, classStyle)
 * Stampa nella pagina HTML il codice standard per un bottone di lookup.
 *  1. onClickStr = codice JS di invocazione sul click del bottone (unico parametro obbligatorio)
 *  2. tooltip = titolo che appare come tooltip (DEFAULT: "")
 *  3. descrizione = stringa che appare sul bottone (DEFAULT: "?")
 *  4. classStyle = stile del bottone ("class") (DEFAULT: "PulsanteVerde")
 * Esempio:
 * invocata con
 *    disegnaBottoneLookup("lookupComuneCoinvolto()", "Cambia comune coinvolto", "?", "PulsanteVerde");
 * produce l'HTML:
 *    <input type="BUTTON" onClick="lookupComuneCoinvolto()" class="PulsanteVerde" value="?" title="Cambia comune coinvolto">
 */
function disegnaBottoneLookup(onClickStr) {

  var tooltip = "";
  var descrizione = "?";
  var classStyle = "PulsanteVerde";
  if (arguments.length > 1)  tooltip     = arguments[1];
  if (arguments.length > 2)  descrizione = arguments[2];
  if (arguments.length > 3)  classStyle  = arguments[3];

  document.writeln("\<input type=\"BUTTON\" onClick=\"" + onClickStr + "\"");
  document.writeln("             value=\"" + descrizione + "\" title=\"" + tooltip + "\" class=\"" + classStyle + "\"\>");
}

/*
 * disegnaImmagineLookup(onClickStr, tooltip, imageName)
 * Stampa nella pagina HTML il codice standard per un'immagine di lookup.
 *  1. onClickStr = codice JS di invocazione sul click del bottone (unico parametro obbligatorio)
 *  2. tooltip = titolo che appare come tooltip (DEFAULT: "")
 *  3. imageName = nome dell'immagine da usare (DEFAULT: "../../img/lookup.gif")
 * Esempio:
 * invocata con
 *    disegnaBottoneLookup("lookupComuneCoinvolto()", "Cambia comune coinvolto", "../../img/lookup.gif");
 * produce l'HTML:
 *    <A HREF="javascript://" onClick="lookupComuneCoinvolto()"><IMG SRC="../../img/lookup.gif" BORDER="0" TITLE="Cambia comune coinvolto"></A>
 */
function disegnaImmagineLookup(onClickStr) {

  var tooltip = "";
  var imageName = "../../img/lookup.gif";
  if (arguments.length > 1)  tooltip   = arguments[1];
  if (arguments.length > 2)  imageName = arguments[2];

  document.write  ("\<A HREF=\"javascript://\" onClick=\"" + onClickStr + "\"\>");
  document.write  ("\<IMG SRC=\"" + imageName + "\" BORDER=\"0\" ALT=\"" + tooltip + "\" ALIGN=\"ABSMIDDLE\"\>");
  document.writeln("\</A\>");
}

/*
 * disegnaLookup(onClickStr, tooltip)
 * Usa una delle due funzioni di sopra per mostrare il tasto di "lookup" (bottone o immagine)
 */
function disegnaLookup(onClickStr, tooltip) {
  disegnaImmagineLookup(onClickStr, tooltip);
}


//--------------------------------------------------------------------------------------
// Script per gestione finestre modali
//--------------------------------------------------------------------------------------
var winFinestraModale = null;
var nomeFinCorrente = "lookup";

/*
 * apriFinestraModale(url [, nomeFin])
 * Visualizza il dato "url" in una nuova finestra modale.
 * PARAMETRI: 1 - url da visualizzare
 *            2 - stringa con nome della finestra di lookup
 */
function apriFinestraModale(url) {
  var nomeFin;
  if (apriFinestraModale.arguments.length > 1) {
    nomeFin = apriFinestraModale.arguments[1];
    nomeFinCorrente = nomeFin;    // Salvo per ricordarselo in caso di "refresh"
  }
  else {
    nomeFin = nomeFinCorrente;    // Uso il vecchio nome della finestra aperta
  }

  // calcolo posizione iniziale (centrando la finestra nel video)
  dimX = 400;
  dimY = 200;
  posX = Math.round((screen.availWidth  - dimX) / 2);
  if (posX < 0) posX=0;
  posY = Math.round((screen.availHeight - dimY) / 2);
  if (posY < 0) posY=0;

  if (window.showModalDialog && ENABLE_MODAL) {
    // GG: in "dialogArguments" (2° parametro) passo la "window" del chiamante!
    window.showModalDialog (url, window,
                            "dialogLeft="+posX+";dialogTop="+posY+";dialogWidth="+dimX+"px;dialogHeight="+dimY+"px;" +
                            "help=no;status=no;resizable=yes");
  }
  else {
    if (ENABLE_EVENT) {
      window.top.captureEvents (Event.CLICK|Event.FOCUS);
      window.top.onclick=ignoreEvents;
      window.top.onfocus=handleFocus;
    }
    winFinestraModale = window.open (url, nomeFin,
                                     "left="+posX+",top="+posY+",width="+dimX+"px,height="+dimY+"px," +
                                     "dependent=yes,align=middle,help=no,status=no,toolbar=no,scrollbars=yes,resizable=yes");
    winFinestraModale.focus();
  }
}

/*
 * chiudiFinestraModale()
 * Chiude la finestra modale CORRENTE (esegue la "window.close()" appropriata)
 */
function chiudiFinestraModale() {
  if (window.showModalDialog && ENABLE_MODAL) {
    window.top.close();
  }
  else {
    if (ENABLE_EVENT) {
      window.top.opener.releaseEvents (Event.CLICK|Event.FOCUS);
    }
    window.top.close();
  }
}

/*
 * ignoreEvents(e)
 * Ignora gli eventi di tipo "e" (non fa nulla)
 */
function ignoreEvents(e) {
  return false;
}

/*
 * handleFocus()
 * Se è presente la finestra modale, gli ritorna il controllo (viene impostato il "focus" su essa)
 */
function handleFocus() {
  if (winFinestraModale != null) {
    if (!winFinestraModale.closed) {
      winFinestraModale.focus();
    }
    else {
      window.top.releaseEvents(Event.CLICK|Event.FOCUS);
    }
  }
  return false;
}

function setPosizioneFinestra (posX, posY) {

  if (window.showModalDialog && ENABLE_MODAL) {
    window.dialogLeft = posX + "px";
    window.dialogTop  = posY + "px";
  }
  else {
    self.moveTo(posX,posY);
  }
}

function setDimensioneFinestra (dimX, dimY) {

  if (window.showModalDialog && ENABLE_MODAL) {
    window.dialogWidth   = dimX + "px";
    window.dialogHeight  = dimY + "px";
  }
  else {
    self.resizeTo(dimX,dimY);
  }
}

function centraFinestra (dimX, dimY) {
  posX = Math.round((screen.availWidth  - dimX) / 2);
  if (posX < 0) posX=0;
  posY = Math.round((screen.availHeight - dimY) / 2);
  if (posY < 0) posY=0;

  setPosizioneFinestra (posX, posY);
  setDimensioneFinestra(dimX, dimY);
}


/* --- FINE FILE --- */