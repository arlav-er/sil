function checkCampiObbligatori(strCodiceFiscale, strCognome) {
	if (
	    ((strCodiceFiscale.value != null) && (strCodiceFiscale.value.length >= 6)) ||
	    (strCognome.value.length >= 2)
	   )
		return true;
	alert("Inserire almeno sei caratteri del codice fiscale\no almeno due caratteri del cognome");
	return false;
}

function checkCampiObbligatoriAzi(cf_ric, piva_ric, ragsoc_ric) {
	if (
	    ((cf_ric.value != null) && (cf_ric.value.length >= 6)) ||
	    ((piva_ric.value != null) && (piva_ric.value.length >= 6)) ||
	    (ragsoc_ric.value.length >= 2)
	   )
		return true;
	alert("Inserire almeno sei caratteri nel codice fiscale o partita iva\no almeno due nella ragione sociale");
	return false;
}
