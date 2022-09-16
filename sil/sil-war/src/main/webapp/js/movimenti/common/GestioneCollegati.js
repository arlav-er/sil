// GestioneCollegati.js
var inizializzato = false;

//funzione che reimposta il frame per la navigazione sui movimenti collegati
function impostaCollegati(prgPrimoPrecedente, prgPrimoSuccessivo) {
	inizializzato = true;
	precedente = false;
	successivo = false;
	if (prgPrimoPrecedente != null && prgPrimoPrecedente != '') {precedente = true;}
	if (prgPrimoSuccessivo != null && prgPrimoSuccessivo != '') {successivo = true;}
	//Visualizzo un messaggio a seconda dei valori passati
	sezionedati = document.getElementById("datiCollegato");
	sezionetitolo = document.getElementById("titoloCollegato").innerHTML = "";
	if (precedente || successivo) {
		sezionedati.innerHTML = "<p align='center'><strong>La posizione corrisponde al movimento corrente</strong></p>";
	} else {
		sezionedati.innerHTML = "<p align='center'><strong>Non esistono movimenti collegati da consultare</strong></p>";
	}	
	//Reimposto i valori dei progressivi
	document.Form1.PrgPrimoPrec.value = prgPrimoPrecedente;
	document.Form1.PrgPrimoSucc.value = prgPrimoSuccessivo;
	prgMovimentoPrec = prgPrimoPrecedente;
	prgMovimentoSucc = prgPrimoSuccessivo;
	document.Form1.PrgMovimentoColl.value = "CORRENTE";
	reimpostaPulsantiCollegati(precedente, successivo);
}

//Reimposta i pulsanti per i movimenti collegati
function reimpostaPulsantiCollegati(prec, succ) {
	if (prec) {document.getElementById("PulsantePrecedente").style.display = 'inline';}
	else {document.getElementById("PulsantePrecedente").style.display = 'none';}
	if (succ) {document.getElementById("PulsanteSuccessivo").style.display = 'inline';}
	else {
		document.getElementById("PulsanteSuccessivo").style.display = 'none';
		if ((document.Form1.PrgMovimentoColl.value == "CORRENTE") &&
			typeof window.parent.collegaSuccessivo == "function" &&
			document.getElementById("PulsanteCollegaSuccessivo") != null) {
			document.getElementById("PulsanteCollegaSuccessivo").style.display = 'inline';
			//Apro la sezione se è chiusa
			if (document.getElementById("sezioneCollegato").aperta == false) {
				gestisciFrameCollegato(document.getElementById("pulsanteCollegati"), 
									   document.getElementById("sezioneCollegato"));	
			}
			
		}
	}	
}

//Passa al movimento collegato precedente o successivo
function consultaCollegato(direzione) {
	if (direzione == 'avanti') { 
		document.Form1.PrgMovimentoColl.value = prgMovimentoSucc;
	} else if (direzione == 'indietro') { 
		document.Form1.PrgMovimentoColl.value = prgMovimentoPrec;
	}
	var datiOk = controllaFunzTL();
	if (datiOk) { doFormSubmit(document.Form1); }	
}

//Effettua il resize dei frame e la gestione della tendina quando si 
//preme il pulsante per aprire o chiudere la sezione.
function gestisciFrameCollegato(immagine, sezione) {
	if (sezione.aperta) {
		window.parent.document.getElementById("Collegati").style.height = '7%';
	} else {
		window.parent.document.getElementById("Collegati").style.height = '30%';
	}
	cambia(immagine,sezione);
}