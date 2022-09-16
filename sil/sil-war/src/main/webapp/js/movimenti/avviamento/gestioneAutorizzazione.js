// codice per la gestione del pulsante del CFL

//visualizza il pulsante del CFL in base al tipo di avviamento
function visualizzaPulsanteCFL(codtipoass) {
	if (codtipoass == 'NO4') {
		document.getElementById("pulsanteCFL").style.display = 'inline';
	} else 	{
		document.getElementById("pulsanteCFL").style.display = 'none';
	}
}

//Apre la pop-up dell'autorizzazione CFL relativa al movimento, solo se
//la data di inizio è valorizzata
function apriCFL() {
	dataInizioMovimento = document.Frm1.datInizioMov;
	if (!checkFormatDate(dataInizioMovimento)) {
		alert('Occorre prima indicare la data di inizio del movimento');
	} else {
		var f = "AdapterHTTP?PAGE=MovDatiCFLPage";
		 f = f + "&PRGAZIENDA=" + prgAzienda + "&PRGUNITA=";
		 f = f + prgUnita + "&DATCFL=" + datCFL + "&STRNUMCFL=" + strNumCFL;
		 f = f + "&STRTIPOCFL=" + strTipoCFL + "&CONTESTOCFL=" + contestoCFL;
		 f = f + "&PRGMOVIMENTOAPP=" + prgMovimentoApp + "&DATINIZIOMOV=";
		 f = f + dataInizioMovimento.value;
		var t = "_blank";
		var feat = "toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=no,resizable=yes,width=600,height=200,top=100,left=100";
		window.open(f, t, feat);
	}
}

//Setta i nuovi valori del CFL
function setCFL(datCFL, strNumCFL, strTipoCFL) {
	document.Frm1.DATCFL.value = datCFL;
	document.Frm1.STRNUMCFL.value = strNumCFL;
	document.Frm1.STRTIPOCFL.value = strTipoCFL;
	cflimpostato = true;
}

//Resetta il CFL se era impostato
function resetCFL() {
	document.Frm1.DATCFL.value = '';
	document.Frm1.STRNUMCFL.value = '';
	document.Frm1.STRTIPOCFL.value = '';
	cflimpostato = false;
}