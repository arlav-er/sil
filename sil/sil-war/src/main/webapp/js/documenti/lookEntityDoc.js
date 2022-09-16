/*
 * FUNZIONI USATE DEI ".INC" DI "lookLavoratore" E DI "lookAzienda".
 */

var opened;		// riferimento alla finestra aperta

function apriSelezionaLavoratore() {
	var url = "AdapterHTTP?PAGE=DocumentiSelezionaLavoratorePage" +
				"&AGG_FUNZ=aggiornaLavoratore";
	myOpenWin(url);
}

function aggiornaLavoratore(cdnLavoratore, codiceFiscaleLavoratore, cognome, nome) {

	document.forms[0].cdnLavoratore.value = cdnLavoratore;

	document.forms[0].codiceFiscaleLavoratore.value = codiceFiscaleLavoratore;
	document.forms[0].cognome.value = cognome;
	document.forms[0].nome.value = nome;
	
	flagChanged = true;		// i dati sono stati modificati
	opened.close();
}

function apriSelezionaAzienda() {
	var url = "AdapterHTTP?PAGE=DocumentiSelezionaAziendaPage" +
				"&AGG_FUNZ=aggiornaAzienda";
	myOpenWin(url);
}

function aggiornaAzienda(prgAzienda, prgUnita, aziRagione,
						 aziIndirizzo, aziComune, aziPIva, aziStrTel) {

	document.forms[0].prgAzienda.value = prgAzienda;
	document.forms[0].prgUnita.value = prgUnita;

	document.forms[0].aziRagione.value = aziRagione;
	document.forms[0].aziIndirizzo.value = aziIndirizzo;
	document.forms[0].aziComune.value = aziComune;
	document.forms[0].aziPIva.value = aziPIva;
	document.forms[0].aziStrTel.value = aziStrTel;

	flagChanged = true;		// i dati sono stati modificati
	opened.close();
}

function myOpenWin(url) {
	// Se la pagina è già in submit, ignoro questo nuovo invio!
	if (isInSubmit()) return;

	var feat = "toolbar=no,location=no,directories=no,status=yes,menubar=no,scrollbars=yes,resizable=yes," +
				"width=600,height=500,top=50,left=100"
    opened = window.open(url, "_blank", feat);
    opened.focus();
}
