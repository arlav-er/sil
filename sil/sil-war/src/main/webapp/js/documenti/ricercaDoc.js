/*
 * GG 21-2-05 - controlli per la ricerca documenti
 */

	/* vedi dettagliDocumento.js */
	function convertStr2Date(dataStr) {
		var aa = parseInt(dataStr.substr(6,4),10);
		var mm = parseInt(dataStr.substr(3,2),10)-1;
		var gg = parseInt(dataStr.substr(0,2),10);
		var dataObj = new Date(Date.UTC(aa, mm, gg));
		return dataObj;
	}

	/* vedi dettagliDocumento.js */
	function confrontaDateObj(data1, data2) {
		var diffTime = data2.getTime() - data1.getTime();
		var diffGiorni = parseInt(diffTime/1000/60/60/24);	// ? o round ?
		return diffGiorni;
	}

	
	function okIntervalloDate(campo_da, campo_a) {
		// CONTROLLO "DA" <= "A" (solo se ambo valorizzati)
		if ((campo_da.value != "") && (campo_a.value != "")) {
		
			if (! checkFormatDate(campo_da)) return true;	// darò errore dopo
			if (! checkFormatDate(campo_a))  return true;
		
			var data_da = convertStr2Date(campo_da.value);
			var data_a  = convertStr2Date(campo_a.value);
	
			var deltaT = confrontaDateObj(data_da, data_a);
			if (deltaT < 0) {
				alert("Nel campo '" + campo_da.title + "' la data 'da' non può essere superiore a quella 'a'!");
				campo_da.focus();
				return false;	// bloccante
			}
		}
		// Confronto ok (o date non entrambe valorizzate)
		return true;
	}


	function okIntervalloNumero(campo_da, campo_a) {
		if ((campo_da.value != "") && (campo_a.value != "")) {

			if (! isInteger(campo_da.value)) return true;	// darò errore dopo
			if (! isInteger(campo_a.value))  return true;

			var deltaT = parseInt(campo_a.value,10) - parseInt(campo_da.value,10);
			if (deltaT < 0) {
				alert("Nel campo '" + campo_da.title + "' il numero 'da' non può essere superiore a quello 'a'!");
				campo_da.focus();
				return false;	// bloccante
			}
		}
		// Confronto ok (o date non entrambe valorizzate)
		return true;
	}


	function checkNumProto(campo_da, campo_a) {

		// Se ambo i campi interi oppure uno dei due vuoti, OK
		if (isInteger(campo_da.value,true) && isInteger(campo_a.value,true)) return true;

		// Se sono qua, uno dei due è non numerico! Per essere ok devono essere uguali		
		if ((campo_da.value != campo_a.value)) {
			alert("Nel campo '" + campo_da.title + "' si possono inserire\n" +
				  "gli estremi di un intervallo numerico oppure\n" +
				  "il medesimo valore (anche non numerico) per una ricerca puntuale!");
			campo_da.focus();
			return false;	// bloccante
		}
		return true;
	}
	/**
	* restituisce il numero di giorni tra campo_da e campo_a
	*/
	function intervalloDate(campo_da, campo_a) {
		if (campo_da.value=='' || campo_a.value=='') return Number.POSITIVE_INFINITY;		
		var data_da = convertStr2Date(campo_da.value);
		var data_a  = convertStr2Date(campo_a.value);
		return confrontaDateObj(data_da, data_a);
	}
	/**
	* restituisce true solo se sono stati valorizzati il lavoratore o l'azienda
	*/
	function checkSoggetto() {
		return (document.formina.cdnLavoratore.value!='') || (document.formina.prgAzienda.value!='');
	}
	/**
	 * Chiamata prima della SUBMIT della Form della ricerca documenti.
	 * Rendere FALSE per abortire il submit.
	 */
	function checkCampiRicerca() {
		var msg;
		var f = document.formina;
		var ok = false;
		var isMov = false;
		var numProtDa = f.numProtocollo_da.value;
		var numProtA = f.numProtocollo_a.value;
		if (!controllaFunzTL() ) {
			undoSubmit();
			return false;
		}
		undoSubmit();
		// Controllo correttezza dati
		
		if (! okIntervalloDate(f.dataProtocollo_da, f.dataProtocollo_a)) return false;
		if (! okIntervalloDate(f.DatInizio_da,      f.DatInizio_a))      return false;
		if (! okIntervalloDate(f.DatFine_da,        f.DatFine_a))        return false;
		if (! okIntervalloNumero(f.annoProtocollo_da,   f.annoProtocollo_a)) return false;
		if (! okIntervalloNumero(f.numProtocollo_da, f.numProtocollo_a))     return false;
		// Controllo aggiuntivo sul campo "numeroProtocollo" che può essere o meno numerico
		if (! checkNumProto(f.numProtocollo_da, f.numProtocollo_a)) return false;
		//ottengo il riferimento alla combo del tipo documento
		s = document.formina.tipoDocumento;
		//controllo se il tipo di documento è un movimento (AVV/CES/PRO/TRA)
		if(s.selectedIndex>=0 ){
			if((s.options[s.selectedIndex].value == 'MVAVV') ||
					(s.options[s.selectedIndex].value == 'MVCES')||
					(s.options[s.selectedIndex].value == 'MVPRO')||
					(s.options[s.selectedIndex].value == 'MVTRA')){
					isMov = true;
			}
		}
		// parametri necessari per la ricerca
		//controllo il numero di protocollo
		if ( (numProtA != "") && (numProtDa != "") ){
			if( ( parseInt(numProtA,10) - parseInt(numProtDa,10) )+1 <= 10 ){
				ok = true;
			}
		}
		// 1) controllo delle date
		if(!ok){
			ok = (intervalloDate(f.dataProtocollo_da, f.dataProtocollo_a) +1 <=1) ||(intervalloDate(f.DatInizio_da, f.DatInizio_a) +1 <=1) ;
		}
		// 2) controllo del soggetto della ricerca: lavoratore o azienda
		if (!ok) {
			ok=checkSoggetto();
		}
		// 3) controllo del tipo di documento e date
		
		if (!ok) {
			
			i=0;
			
			if (s.selectedIndex>=0) {
				while (i<s.length && !ok) {
					ok = s.options[i].selected && s.options[i].value!='';
					i++;
				}
				if(isMov){
					ok = ok &&((intervalloDate(f.dataProtocollo_da, f.dataProtocollo_a) +1 <=1) ||(intervalloDate(f.DatInizio_da, f.DatInizio_a) +1 <=1));					
				}else{
					ok = ok && ((intervalloDate(f.dataProtocollo_da, f.dataProtocollo_a)+1 <=30)||(intervalloDate(f.DatInizio_da, f.DatInizio_a) +1 <=30));
				} 
			}
		}
		if (!ok) {
			msg = "Parametri generici. \n"+
			"OPZIONE 1 - Uno dei seguenti valori: Lavoratore / Azienda.\n"+
			"OPZIONE 2 - Uno dei seguenti intervalli : data di protocollo  da... a.../  data di inizio validità da... a... con periodo max un giorno.\n"+
			"OPZIONE 3 - Tutti i seguenti valori: tipo di documento (se movimento si applica OPZIONE 2) / data protocollo da... a... con periodo max 30 gg.\n"+
			"OPZIONE 4 - Tutti i seguenti valori: tipo di documento (se movimento si applica OPZIONE 2) / data inizio validità da a con periodo max 30 gg.\n"+
			"OPZIONE 5 - Numero di protocollo da... a... con intervallo max 10 valori.";
			
			alert(msg);
		}
		return ok;
	}

	/**
	 * Chiamata prima della SUBMIT della Form della ricerca documenti allegati della stampa parametrica
	 * Rendere FALSE per abortire il submit.
	 */
	function checkCampiRicercaAllegatiStampeParam() {
		var msg;
		var f = document.frmRicerca;
		var numProtDa = f.numProtocollo_da.value;
		var numProtA = f.numProtocollo_a.value;
		if (!controllaFunzTL() ) {
			undoSubmit();
			return false;
		}
		undoSubmit();
		
		// Controllo correttezza dati
		if (! okIntervalloDate(f.dataProtocollo_da, f.dataProtocollo_a)) return false;
		if (! okIntervalloDate(f.DatInizio_da,      f.DatInizio_a))      return false;
		if (! okIntervalloDate(f.DatFine_da,        f.DatFine_a))        return false;
		if (! okIntervalloNumero(f.annoProtocollo_da,   f.annoProtocollo_a)) return false;
		if (! okIntervalloNumero(f.numProtocollo_da, f.numProtocollo_a))     return false;
		
		document.frmRicerca.PAGE.value = "ListaDocumentiAllegatiStampParamPage";
		return true;
	}
