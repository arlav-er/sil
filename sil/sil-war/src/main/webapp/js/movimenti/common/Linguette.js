//funzione che effettua la navigazione tra le linguette
function goToCheck(page) {
	if (page != null && page != '') {
		document.Frm1.PAGE.value = page;
		document.Frm1.ACTION.value = "naviga";
		var datiOk = controllaFunzTL();
		if (datiOk) { doFormSubmit(document.Frm1); }
	}
}

function goToNoCheck(page) {
	if (page != null && page != '') {
		document.Frm1.PAGE.value = page;
		document.Frm1.ACTION.value = "naviga";
      doFormSubmit(document.Frm1); 
	}
}

//Abilita e disabilita le linguette a seconda del tipo di movimento
function gestisciLinguette(codtipomovcorr) {
	document.getElementById("linguettaAvviamento").style.display = "none";          
	document.getElementById("linguettaTrasfPro").style.display = "none";
	document.getElementById("linguettaCessazione").style.display = "none";
	if (codtipomovcorr == "AVV") {
		document.getElementById("linguettaAvviamento").style.display = "inline";
	}
	if (codtipomovcorr == "TRA" || codtipomovcorr == "PRO") {  
    	document.getElementById("linguettaTrasfPro").style.display = "inline";
	}
	if (codtipomovcorr == "CES") {
    	document.getElementById("linguettaCessazione").style.display = "inline";
	} 
}
