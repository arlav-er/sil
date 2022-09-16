/**
 * FUNZIONI USATE NELLE PAGINE JSP "InserisciModalita.jsp" e "DettaglioModalita.jsp" della GESTIONE MODELLI TDA.
 */
function checkPercentuale(){
	if(document.frm1.strPercentuale.value !== ""){
		var precentuale = parseInt(document.frm1.strPercentuale.value);
		if(precentuale > 100){
			alert("Il campo '" +document.frm1.strPercentuale.title +"' non può essere maggiore di 100");
			document.frm1.strPercentuale.focus();
			return false;
		}else
			return true;
	}else 
		return true;
}

function checkTipoDurata(){
	if(document.frm1.strDurataMin.value !== "" || document.frm1.strDurataMax.value !== ""){
		if(document.frm1.strTipoDurata.value == ""){
			alert("Il campo 'Tipologia durata' è obbligatorio quando uno o entrambi i campi 'Durata minima' e 'Durata massima' sono valorizzati");
			document.frm1.strTipoDurata.focus();
			return false;
		}else
			return true;
	}else 
		return true;
}

function checkDurataMin(){
	if(document.frm1.strDurataMax.value == null || document.frm1.strDurataMax.value == ""){
		return true;
	}else if(document.frm1.strDurataMin.value !== ""){
		var min = parseInt(document.frm1.strDurataMin.value);
		var max = parseInt(document.frm1.strDurataMax.value);
		if(min>max){
			alert("Il campo 'Durata minima' deve essere minore o uguale a 'Durata massima'");
			document.frm1.strDurataMin.focus();
			return false;
		}				
		else
			return true;
	}else
		return true;
}

function checkDurataMax(){
	if(document.frm1.strDurataMin.value == null || document.frm1.strDurataMin.value == ""){
		return true;
	}else if(document.frm1.strDurataMax.value !== ""){
		var min = parseInt(document.frm1.strDurataMin.value);
		var max = parseInt(document.frm1.strDurataMax.value);
		if(max<min){
			alert("Il campo 'Durata massima' deve essere maggiore o uguale a 'Durata minima'");
			document.frm1.strDurataMax.focus();
			return false;
		}else
			return true;
	}else
		return true;
}

function tipoRimborso(){
	fieldChanged();
	var currValue = document.frm1.strRimborso.value;
	if(prevValue == null || prevValue == ""){
		if(currValue == 'T'){
			document.frm1.strValUnit.readOnly = true;
			document.frm1.strValTot.readOnly = false;
			document.frm1.strValUnit.value="";
		}else if(currValue == 'C'){
			document.frm1.strValUnit.readOnly = false;
			document.frm1.strValTot.readOnly = true;
			document.frm1.strValTot.value="";
		}
	}else if(prevValue !== null && prevValue !== "" && (currValue == null || currValue == "")){
 		var r = confirm("Cambiado il Tipo di Rimborso non sarà possibile inserire nè il valore unitario nè quello totale. Proseguire?");
		if (r == true) {
			document.frm1.strValUnit.readOnly = true;
			document.frm1.strValTot.readOnly = true;
			document.frm1.strValTot.value="";
			document.frm1.strValUnit.value="";
		} else {
			document.frm1.strRimborso.value = prevValue;
			currValue = prevValue;
		}
	}else if(prevValue == "T" && currValue == "C"){
		var r = confirm("Cambiado il Tipo di Rimborso non sarà possibile inserire il valore totale. Proseguire?");
		if (r == true) {
			document.frm1.strValTot.readOnly = true;
			document.frm1.strValUnit.readOnly = false;
			document.frm1.strValTot.value="";
		} else {
			document.frm1.strRimborso.value = prevValue;
			currValue = prevValue;
		}
	}else if(prevValue == "C" && currValue == "T"){
 		var r = confirm("Cambiado il Tipo di Rimborso non sarà possibile inserire il valore unitario. Proseguire?");
 		if (r == true) {
 			document.frm1.strValUnit.readOnly = true;
 			document.frm1.strValTot.readOnly = false;
 			document.frm1.strValUnit.value="";
		} else {
			document.frm1.strRimborso.value = prevValue;
			currValue = prevValue;
		}
	}
	prevValue = currValue;
}

function controlliValoriEuro(){
	if (document.frm1.strValUnit.value!='' && !controllaFixedFloat('strValUnit', 8, 2)) {			
		return false;
	} 		
	if (document.frm1.strValTot.value!='' && !controllaFixedFloat('strValTot', 8, 2)) {			
		return false;
	} 
	return true;
}

function controllaModalitaObb(){
	return isRequired('codModalita');
}