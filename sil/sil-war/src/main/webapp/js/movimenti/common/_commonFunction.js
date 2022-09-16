//Funzione per alert degli impatti
function checkDatInizioMovImpatti() {
	var datInizioMov = "";
	datInizioMov = document.Frm1.datInizioMov.value;
	if(document.Frm1.CODTIPOMOV.value == "CES") {
		datInizioMov = document.Frm1.datInizioMovCes.value;
	}
	if(document.Frm1.CODTIPOMOV.value == "TRA") {
		datInizioMov = document.Frm1.datInizioMovTra.value;
	}
	if(document.Frm1.CODTIPOMOV.value == "PRO") {
		datInizioMov = document.Frm1.datFineMov.value;
	}
	d = new String(datInizioMov);
	dataInizio = new Date(d.substring(6,10), (d.substring(3,5) - 1), d.substring(0,2));
	
	if(document.Frm1.CODTIPOMOV.value == "PRO") {
		limiteImpatti = new Date(2003, 0, 29); 
	} else {
		limiteImpatti = new Date(2003, 0, 30);
	}
	if (dataInizio < limiteImpatti) {
		alert('Il movimento é precedente alla data del 30/01/2003. Il sistema non prevede automatismi per la gestione degli impatti.');
		return true;	
	}
	return true;
}


function checkDatInizioMovImpatti(dataNormativaPrec297) {
	var datInizioMov = "";
	datInizioMov = document.Frm1.datInizioMov.value;
	if(document.Frm1.CODTIPOMOV.value == "CES") {
		datInizioMov = document.Frm1.datInizioMovCes.value;
	}
	if(document.Frm1.CODTIPOMOV.value == "TRA") {
		datInizioMov = document.Frm1.datInizioMovTra.value;
	}
	if(document.Frm1.CODTIPOMOV.value == "PRO") {
		datInizioMov = document.Frm1.datFineMov.value;
	}
	d = new String(datInizioMov);
	dataInizio = new Date(d.substring(6,10), (d.substring(3,5) - 1), d.substring(0,2));
	
	dNormativa = new String(dataNormativaPrec297);
	limiteImpatti = new Date(dNormativa.substring(6,10), (dNormativa.substring(3,5) - 1), dNormativa.substring(0,2)); 
	
	if (dataInizio < limiteImpatti) {
		alert('Il movimento é precedente alla data del ' + dNormativa + '. Il sistema non prevede automatismi per la gestione degli impatti.');
		return true;	
	}
	return true;
}

//Funzione per il refresh delle pagine del dettaglio movimento
function aggiornaDettaglioMovimento() {
	//Se sto consultando faccio il refresh attraversi l'invio della form 
	//(il nome della page è impostato a default sulla linguetta corrente)
	if (contesto == 'consulta') {
		document.Frm1.ACTION.value = "naviga";
      	doFormSubmit(document.Frm1);
	}
}

//Restituisce la stringa vuota quando viene passato null
function strNotNull(str) {
	if (str == null) return "";
	else return str;
}

// Funzione che controlla sul server se una qualifica esiste o no
function controllaQualificaOnSubmit(campoQualifica) {
	var qualifica = new String(eval('document.Frm1.' + campoQualifica + '.value'));
	if (qualifica == "") { 
		return true;
	}
	var exist = false;
	try {
		exist = controllaEsistenzaChiave(qualifica, "CODMANSIONE", "DE_MANSIONE");
	} catch (e) {
		return confirm("Impossibile controllare che la qualifica " + qualifica + " esista, proseguire comunque?");
	}
	if (!exist) {
		alert("Il codice della qualifica " + qualifica + " non esiste");
		return false;
	} else return true;
}

// Funzione che controlla sul server se un ccnl esiste o no
function controllaCCNLOnSubmit(campoCCNL) {
	//Recupero dell'elemento che contiene il codice
	var field = eval('document.Frm1.' + campoCCNL);
	var codCCNL = new String(field.value);
	if(codCCNL != ""){
		//Lo setto maiuscolo e lo reimposto nella form
		codCCNL = codCCNL.toUpperCase();
		field.value = codCCNL;
		var exist = false;
		try {
			exist = controllaEsistenzaChiave(codCCNL, "CODCCNL", "DE_CONTRATTO_COLLETTIVO");
		} catch (e) {
			return confirm("Impossibile controllare che il codice del contratto collettivo " + codCCNL + " esista, proseguire comunque?");
		}
		if (!exist) {
			alert("Il codice del contratto collettivo " + codCCNL + " non esiste");
			return false;
		}
	}
	return true;
}

function controlloLavAutonomo() {
	ret = true;
	var o = document.getElementsByName("CODMONOTIPO");
	var codMonoTipo = o.item(0).value;
	var tipoAss = codMonoTipo=='N'?"autonomo":"";
	if (codiceFiscaleLav != codiceFiscaleAz && tipoAss=='autonomo')
		ret = confirm("Attenzione: Il lavoratore e l'azienda non hanno lo stesso codice fiscale. Si desidera proseguire nella registrazione del movimento?");
	return ret;
}

//Setta a S o N il flag per l'autorizzazione dell'utente ai movimenti a TD con durata elevata
//a seconda del valore boolean passato.
function settaAutorizzazioneDurataTD(autorizza) {
	if (autorizza) {
		document.Frm1.FLGAUTORIZZADURATATD.value = "S";		
	} else {
		document.Frm1.FLGAUTORIZZADURATATD.value = "N";			
	}	
} 

//Riesegue l'elaborazione a seguito dell'autorizzazione utente sulla durata del movimento
function proseguiDopoAutorizzazioneDurataTD() {
	settaAutorizzazioneDurataTD(true);
	doFormSubmit(document.Frm1);
}

//Controlla tipo comunicazione
function checkTipoComunicazione(codTipoComunic) {
	var msg = "";
	if (codTipoComunic == "04" || codTipoComunic == "03") {
		if (codTipoComunic == "04") {
			msg = "La validazione del movimento si riferisce all'annullamento di una comunicazione precedente, proseguire?";
		}
		else {
			msg = "La validazione del movimento si riferisce alla rettifica di una comunicazione precedente, proseguire?";
		}
		if (confirm(msg)) {
			return true;
		}
		else {
			return false;
		}
	}
	return true;
}