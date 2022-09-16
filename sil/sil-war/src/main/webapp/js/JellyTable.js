// *** JellyTable ***
// GESTIONE DI UNA TABELLA "GENERICA" ----------------------------------------
// (by Luigi Antenucci & Franco Vuoto  - (c) Engiweb.com di Bologna)
//
// Essa dev'essere definita come:
//
//   var arrTabProva    = new Array();
//          (La tabella è memorizzata come struttura di array ad array.)
//
//   var arrHeaderProva = new Array();
//   arrHeaderProva[0]  = "Codice";
//   arrHeaderProva[1]  = "info1";
//   arrHeaderProva[2]  = "info2";
//   arrHeaderProva[3]  = "info3";
//          (Questo array contiene i nomi delle colonne della tabella.
//           Serve inoltre per indicare quali colonne *non* devono essere mostrate.
//           Se un elemento contiene una stringa vuota allora l'intera colonna
//           non viene mostrata.  Si noti però che, se definito, il campo di 
//           input (vedi sotto) viene creato indipendentemente dal valore di
//           questo attributo);
//
//   var arrCampiProva = new Array();
//   arrCampiProva[0]  = "codice";
//          (Quest altro contiene i nomi dei campi di "INPUT-text-HIDDEN" che
//           verranno generati nella pagina per poterli passare in SUBMIT alla
//           pagina futura. Nel caso il nome sia definito come stringa vuota
//           non verrà generato il campo di input; non serve riempire tutte
//           le colonne rimanenti a stringa vuota perché se non esistono non
//           vengono usate).
//           NOTA: il nome del campo NON DIPENDE dal nome della tabella! Quindi non
//           dare lo stesso nome di campo in tabelle diverse nella stessa pagina!
//           Si veda la libreria lato JAVA "JellyTable" per il recupero dei campi.
//   var hideCampiProva = true;
//           Questa variabile serve per indicare se si vuole che i campi di testo
//           definiti in "arrCampi" siano visibili o meno (nota che se sono
//           visibili sono anche editabili dall'utente).
//   var showAggiungiProva = true;
//           Se posta a "true" mostra il bottone di "Aggiungi" per permettere
//           di invocare la funzione "lookupProva()" (dove "Prova" va sostituito
//           col vero nome dato alla tabella). Si vedano gli esempi.
//   var showTogliProva = true;
//           Simile, per i bottoni di "elimina" posti a lato di ogni riga.
//           Si invocherà funzione "removeProva(riga)".
//   var showModificaProva = true;
//           Simile, per i bottoni di "modifica" posti a lato di ogni riga.
//           Si invocherà la funzione "modifyProva(riga)".


// Parte per rilevare la modifica dei dati contenuti nelle tabelle
var flagJellyTableChanged = false;    // Almeno un dato è stato cambiato?

function jellyTableChanged() {
  flagJellyTableChanged = true;
}


function mostraTabella(arrTab, idTab, strPostfissoFunz, arrHeader, arrCampi,
                       hideCampi, showAggiungi, showTogli, showModifica) {
  // by Luigi Antenucci & Franco Vuoto   (c) Engiweb.com di Bologna
  // arrTab = la struttura ad array con i dati da mostrare
  // idTab  = l'oggetto "DIV" identificato dal suo attributo ID (in cui mostrare la tabella)
  // strPostfissoFunz = stringa con il postfisso del nome delle funzioni per lookup e di delete
  //               (es. strPostfissoFunz="SitoCollegato" per invocare la "lookupSitoCollegato")
  // arrHeader = array con le stringhe da stampare nell'header della tabella (dove ho "" non stampo la colonna)
  // arrCampi  =  array con le stringhe per i nomi dei campi "INPUT" da creare

  // Se i campi sono MOSTRATI, l'utente può averli modificati: devo recuperarli
  // (il dato mostrato a video non è in sincronia con quello nella tabella)

  var s;

  if (arrTab == undefined) 
    alert("arrTab non definita");

  if (arrTab.length == 0) {
    s = "<P class='LISTAVUOTA'>(lista vuota)</P>";
  }
  else {
    s = "<table class='lista'>";

    if (arrHeader.length > 0) {     // Stampa HEADER
      s += "<tr class='lista'>";
      for (var c=0; c<arrHeader.length; c++) {
        if (arrHeader[c] != "") {
          s += "<th class='lista'>" + arrHeader[c] + "&nbsp;</th>";
        }
      }
      if (showTogli || showModifica) {
        s += "<th class='lista'>&nbsp;</th>";
      }
      s += "</tr>";
    } //if

    for (var r=0; r<arrTab.length; r++) {
      stile = " class='lista" + (r%2) + "'";   // stile della riga
      s += "<tr" + stile + ">";
      for (var c=0; c<arrTab[r].length; c++) {
        var mostraColonna = (arrHeader[c] != "");     // Ho stampato la colonna?

        if (mostraColonna) {
          s += "<td" + stile + ">";
        }

        var stampato = false;
        if ((c < arrCampi.length) && (arrCampi[c] != "")) {   // Lo metto in un campo?
          if (hideCampi || !mostraColonna) {
            s += "<INPUT type='HIDDEN'";
          }
          else {
            s += "<INPUT type='TEXT' onKeyUp='jellyTableChanged()'";
            stampato = true;
          }
          s += " name='" + arrCampi[c] + "_" + r + "' value='" + arrTab[r][c] + "'>";
        }

        if (mostraColonna) {
          if (!stampato) {
            s += arrTab[r][c];
          }
          s += "&nbsp;</td>";
        }
      } //for c

      // Bottoni di fine riga
      if (showTogli || showModifica) {
        s += "<td" + stile + ">";
        if (showTogli) {
          s += "<input type='BUTTON' class='PulsanteVerde' value=' canc ' title='Toglie la riga' " +
               "       onClick='remove" + strPostfissoFunz + "(" + r + ");'>";
        }
        if (showModifica) {
          s += "<input type='BUTTON' class='PulsanteVerde' value=' mod ' title='Modifica la riga' " +
               "       onClick='modify" + strPostfissoFunz + "(" + r + ");'>";
        }
        s += "</td>";
      }
      s += "</tr>";
    } //for r

    s += "</table>";

  } //else

  if (showAggiungi) {

    s += "<br><input type='BUTTON' class='PulsanteVerde' value='Aggiungi' title='Aggiunge una nuova riga' " +
         "           onClick='lookup" + strPostfissoFunz + "();'>";
  }

  idTab.innerHTML = s;

} //mostraTabella

function sincronizzaTabella(arrTab, arrCampi) {
  // Se i campi sono visualizzati a video, l'utente può averli modificati senza
  // che venisse modificata la struttura dati (arrTab).
  // Questa funzione riporta tali valori dagli "INPUT-tag" alla "arrTab".
  // DEV'ESSERE USATA QUANDO SI IMPOSTA "hideCampi=false";
  // VA INVOCATA PRIMA DI FARE MODIFICHE ALLA STRUTTURA DATI DELLA TABELLA,
  // per es. si invoca in sequenza: sincronizza - removeRiga - mostra;
  // oppure: sincronizza - addRiga - mostra;

  for (var r=0; r<arrTab.length; r++) {
    for (var c=0; c<arrTab[r].length; c++) {
      if ((c < arrCampi.length) && (arrCampi[c] != "")) {   // Il dato è stato messo in un campo?
        var objCampo = eval("document.forms[0]." + arrCampi[c] + "_" + r);
        if (objCampo != undefined) {
          arrTab[r][c] = objCampo.value;
          jellyTableChanged();
        }
      }
    } //for c
  } //for r

} //sincronizzaTabella


function addRigaTabella(arrTab, primoElemento) {
  // nota: questa funzione ha un numero di argomenti variabile!
  // Si usa "arguments" per reperire tutti gli elementi: da primoElemento (obbligatorio) in poi
  // es di chiamata: addRigaTabella(arrTabMiaTabella, "11/4", "luigi", "chiamare");

  var newRow = arrTab.length;
  arrTab[newRow] = new Array();

  for (var i=1; i<arguments.length; i++) {
    arrTab[newRow][i-1] = arguments[i];
  }

  jellyTableChanged();
  //ora serve invocare la "mostra" opportuna!
}

function removeRigaTabella(arrTab, rigaDel) {

  var maxRow = arrTab.length;
  for (var r=(rigaDel+1); r<maxRow; r++) {
    arrTab[r-1] = arrTab[r];    // Sposta
  }
  arrTab.length--;

  jellyTableChanged();
  //ora serve invocare la "mostra" opportuna!
}


function existsRigaTabella(arrTab, primoElemento) {
  // nota: questa funzione ha un numero di argomenti variabile!
  // Si usa "arguments" per reperire tutti gli elementi: da primoElemento (obbligatorio) in poi
  // es di chiamata: existsRigaTabella(arrTabMiaTabella, "123", null, "9");
  // Rende TRUE se esiste già una riga contenente gli stessi valori (nell'ordine dato).
  // Se non interessa il valore di una colonna (ossia non si vuole che vengono usate 
  // nel confronto), basta passare un "null".

  var maxRow = arrTab.length;
  var maxCol = arguments.length - 1;  // il primo argom. e' il nome della tabella

  var r = 0;
  var trovata = false;
  while ((r<maxRow) & !trovata) {

    var c = 0;
    var uguale = true;
    while ((c<maxCol) & uguale) {

      if (arguments[c+1] != null) {
        uguale = arrTab[r][c] == arguments[c+1];
      }

      // DEBUG:  alert("r="+r+" c="+c+"\narrTab[r][c]="+arrTab[r][c]+"  arguments[c+1]="+arguments[c+1]+"  uguale="+uguale);

      c++;
    }
    trovata = uguale;   // l'ho trovata quando trovo una riga uguale a quella data

    r++;
  }
  return trovata;
}


function doppioneRigaTabella(arrTab, primoElementoConfrontabile) {
  // nota: questa funzione ha un numero di argomenti variabile!
  // es di chiamata: existsRigaTabella(arrTabMiaTabella, "X", null, "X");
  // Rende TRUE se esiste almeno una coppia di righe uguali, ossia che contengono gli
  // stessi valori (nell'ordine dato).
  // Gli argomenti passati corrispondono alle colonne interessate: quelle che
  // contengono "null" non interessano (non vengono usate nel confronto) mentre
  // le altre possono contenere qualsiasi cosa (non verrà usato il valore ma
  // il fatto che non sia "null" indica che si deve eseguire il confronto).

  var maxRow = arrTab.length;
  var maxCol = arguments.length - 1;  // il primo argom. e' il nome della tabella

  var r1 = 0;
  var trovata = false;
  while ((r1<maxRow) & !trovata) {

    var r2 = r1+1;     // Spazzolo dalla posizione successiva in poi
    while ((r2<maxRow) & !trovata) {

      var c=0;
      var uguale = true;
      while ((c<maxCol) & uguale) {

        if (arguments[c+1] != null) {
          uguale = arrTab[r1][c] == arrTab[r2][c];
        }

        //DEBUG:  alert("r1="+r1+" r2="+r2+" c="+c+"\narrTab[r1][c]="+arrTab[r1][c]+"  arrTab[r2][c]="+arrTab[r2][c]+"  uguale="+uguale);

        c++;
      }
      trovata = uguale;   // l'ho trovata quando trovo una riga uguale a quella data

      r2++;
    }
    r1++;
  }
  return trovata;
}

// *** (Fine File JellyTable) ***