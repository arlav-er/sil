/*
 * FUNZIONI USATE NELLA PAGINA JSP "dettagliDocumento.jsp" dei DOCUMENTI.
 */

	//==================================================================================
	//  Funzioni usate per la protocollazione che gestiscono la data 
	
	function checkAndFormatTime(oraObj)
	{ var strTime = oraObj.value;
	  var strHours = "";
	  var strMin   = "";
	  var separator = ":";
	  var strTimeArray;
	
	  if (document.form.CODSTATOATTO.value == 'PR') {
	
	    var titleObj = "ora";
	    if(strTime.title != null)
	    { titleObj = strTime.title;
	    }
	  
	     if (strTime.indexOf(separator) != -1) {
	        strTimeArray = strTime.split(separator);
	        if (strTimeArray.length != 2) {
	           alert("Il campo "+titleObj+" non ha un orario scritto nel formato corretto:\nscrivere HH"+separator+"MM   es: 08"+separator+"12")
	           return false;
	        }
	        else {
	         strHours = strTimeArray[0];
	         strMin   = strTimeArray[1];
	        }
	     }
	     else if(strTime.length == 4)
	     { //Non c'è il separatore, probabilmente è stata inserita un'orario nel formato 1215 -> 12:15
	       //che comunque reputiamo valido...
	       strHours = strTime.substr(0,2);
	       strMin   = strTime.substr(2,4);
	     }
	     else
	     { alert("Il campo "+titleObj+" non ha un orario scritto nel formato corretto:\nscrivere HH"+separator+"MM   es: 08"+separator+"12");
	       return false;
	     }
	
	     var hours = parseInt(strHours, 10);
	     var min   = parseInt(strMin, 10);
	   
	     if(isNaN(hours))
	     { alert("L'ora inserita non è un numero:\nscrivere HH"+separator+"MM   es: 08"+separator+"12");
	       return false;
	     }
	     if(isNaN(min))
	     { alert("I minuti inseriti non sono un numero:\nscrivere HH"+separator+"MM   es: 08"+separator+"12");
	       return false;
	     }
	
	     hours = parseInt(strHours, 10);
	     min   = parseInt(strMin, 10);
	   
	     if( hours<0 || hours > 23 )
	     { alert("L'ora inserita non è orario valido.\nInserire un'ora compresa fra 0 e 23");
	       return false;
	     }
	     if( min<0 || min > 59 )
	     { alert("I minuti inseriti non sono corretti.\nInserire un numero compresa fra 0 e 59");
	       return false;
	     }
	
	    oraObj.value = (hours<10?"0":"") + hours + separator + (min<10?"0":"") + min;
	    
	  }
		return true;	  
	   
	}//end function
 
 
 
 	function cambiAnnoProt(dataPRObj,annoProtObj) {
 	  var dataProt = dataPRObj.value;
 	  var lun = dataProt.length;
	
	  //Stiamo modificando la data di protocollazione. Quindi cambia anche l'anno di protocollazione
	  annoProtObj.value = ""; 
	  
	  if (lun > 5) {
	    var tmpDate = new Object();
	    tmpDate.value = dataProt;
	    if ( checkFormatDate(tmpDate) ) {
	       annoProtObj.value = tmpDate.value.substr(6,10);      
	    }
	    else if (lun==8 || lun==10) {
	      alert("La data di protocollazione non è corretta");
	    }
	  }
	}


	function azzeraCampiProtocollazione() {
	  var objFrm = document.form;
	  objFrm.numAnnoProt.value = "";
	  objFrm.numProtocollo.value = "";
	  objFrm.dataProt.value = "";
	  objFrm.oraProt.value = "";
	  objFrm.dataOraProt.value = "";
	}


	/* Cambio della combo di "Protocollazione" S/N */
	function protocollazione_onChange() {
		// Se la pagina è già in submit, ignoro questo nuovo invio!
		if (isInSubmit()) return;

		var objFrm = document.form;
		// AndS 15/09/05: se l'operazione e' in fase di modifica e il codStatoAtto originale e' PR
		//        allora bisogna bloccare l'operazione per evitare che vengano cancellate le info di protocollazione
		//        originali.
		if (_codStatoAtto=='PR' && objFrm.CODSTATOATTO.value=='NP') {
			alert("Stato non permesso per un documento protocollato");
			objFrm.CODSTATOATTO.value="PR";
			return;
		}
		if (_codStatoAtto=='PR') {
			// non bisogna fare niente
			return;
		}
		// se invece il documento e' in fase di inserimento o in modifica ma non era protocollato allora
		// il comportamento rimane come prima.
		if (objFrm.CODSTATOATTO.value == "PR" ) {
		
			/*
			 * Si fa il submit della form, in modo che con il refresh della pagina
			 * si possono recuperare le corrette informazioni sulla protocollazione.
			 */
			// AndS 20/09/05: distinzione tra nuovo documento e modifica
			// per distinguere il caso del documento con numero di protocollo e il documento 
			// esistente che si vuole protocollare si simula un nuovo documento
			// AndS 07/10/2005: la chiamata alla page del documento e' stata sostituita dalla chiamata
			//    xmlhttpRequest()
			caricaInfoProtocollo();
		}
		else {
			
			azzeraCampiProtocollazione();
			// se la prototollazione e' manuale allora bisogna ripristinare lo stile "sola lettura"
			if (document.form.tipoProt.value=="N") {
				document.form.oraProt.className="inputView";				
				document.form.dataProt.className="inputView";
				document.form.numProtocollo.className="inputView";
				document.form.oraProt.readOnly=true;
				document.form.dataProt.readOnly=true;
				document.form.numProtocollo.readOnly=true;
			}
			// AndS 15/09/05: ora la sezione protocollazione rimane sempre visibile
			//showLayer("sezioneProt", false);
		}
	}

	/* Cambio della combo di "Riferimento" (a cui la combo "Tipo documento" e' vincolata) */
	function codAmbito_onChange() {
		comboPair.populate();
		controllaRiferimento();
	}
	
	
	/* Prenotazione protocollo - protocollazione veloce (bottone) */
	function protoVeloce() {
		
		document.form.FlgCodMonoIO.value = "I";		// Doc. di := input
		
		if (document.form.codAmbito.value != "MV") {
			document.form.codAmbito.value  = "MV";		// Riferimento := Movimenti amministrativi
			codAmbito_onChange();						// (emula il cambio della combo)
			document.form.codTipoDocumento.value = "";	// Tipo documento := (da definire)
		}
		
		if (document.form.CODSTATOATTO.value != "PR") {
			document.form.CODSTATOATTO.value  = "PR";	// Protocollazione := Sì
			protocollazione_onChange();					// (emula il cambio della combo)
		}
	}


	function togliInfo_gen(idAziLav, readOnlyStr) {
		// idAziLav = "Azi" o "Lav"
		// readOnlyStr = conversione della var. lato java omonima (in Java è un boolean)

		var f = document.form;
		
		if (idAziLav == "Lav")
			f.cdnLavoratore.value = "";
		else if (idAziLav == "Azi")
			{ f.prgAzienda.value = ""; f.prgUnita.value = ""; }
		
		displayLookInfo_gen(idAziLav, readOnlyStr);
		
		fieldChanged();
	}

	/*
	 * Mostra alternativamente il "div" della lookup o delle info per Lav/Azi.
	 */
	function displayLookInfo_gen(idAziLav, readOnlyStr) {
		// idAziLav = "Azi" o "Lav"
		// readOnlyStr = conversione della var. lato java omonima (in Java è un boolean)

		// Dato "idAziLav" recupero l'oggetto contenente la chiave
		var f = document.form;
		var fieldObj = undefined;
		var lookObj  = undefined;
		if (idAziLav == "Lav")
			{ fieldObj = f.cdnLavoratore;  lookObj = f.lookLavoratore; }
		else if (idAziLav == "Azi")
			{ fieldObj = f.prgAzienda;     lookObj = f.lookAzienda; }
		
		var enableInfo = (fieldObj.value != "");	// Ho un valore? Se sì, mostro Info in ogni caso
		var enableLook = ! enableInfo;				// Il "look" è esclusivo alle Info
		
		enableLook = enableLook && (lookObj.value.toLowerCase()=="true") && ! readOnlyStr;

		// DEMO: alert(idAziLav+": enableInfo="+enableInfo+" enableLook="+enableLook);

		showLayer("divLook"+idAziLav+"_look", enableLook);
		showLayer("divLook"+idAziLav+"_info", enableInfo);
	}


	/*
	 * Funzione che converte una data da formato STRINGA "GG/MM/AAAA" in oggetto DATE.
	 * by GIGI 9/5/2003
	 */
	function convertStr2Date(dataStr) {
		var aa = parseInt(dataStr.substr(6,4),10);
		var mm = parseInt(dataStr.substr(3,2),10)-1;
		var gg = parseInt(dataStr.substr(0,2),10);
		var dataObj = new Date(Date.UTC(aa, mm, gg));
		return dataObj;
	}


	/*
	 * Funzione che confronta due DATE (passati come oggetti Date).
	 * Rende il NUMERO DI GIORNI tra le due date:
	 *   == 0  -->  date coincidenti
	 *   > 0   -->  data1 < data2
	 *   < 0   -->  data1 > data2
	 * (ossia esegue la sottrazione tra data2 e data1:
	 *  rende "data2 - data1" in numero di giorni)
	 * by GIGI 9/5/2003
	 */
	function confrontaDateObj(data1, data2) {
		var diffTime = data2.getTime() - data1.getTime();
		var diffGiorni = parseInt(diffTime/1000/60/60/24);	// ? o round ?
		return diffGiorni;
	}

	/* Mostra o nasconde un elemento (layer).
	 *  - layerID = stringa conenente l'ID dell'elemento da usare
	 *  - isVisible = mostra (true) o nasconde (false) l'elemento.
	 * by GIGI 7/12/2004
	 */
	function showLayer(layerID, isVisible) {
	
		document.getElementById(layerID).style.display = (isVisible ? "" : "none");
	}


	/** Chiamata sull'ONSUBMIT della Form del dettaglio */
	function checkCampi() {
		var f = document.form;
		if (!validateDate('DatInizio')) return false;
		if (!validateDate('datFine')) return false;
		if (!validateDate('DatAcqril')) return false;
		
		if (! checkAndFormatTime(f.oraProt))
			return false;

		// CONTROLLI SULLA DATA DI INIZIO VALIDITA'
		// AndS 30/09/05: questi controlli vanno eseguiti solo se non si annulla il documento.
		// Se si annulla il documento e' anche possibile che i dati siano incongruenti.
		if (f.CODSTATOATTO.value=="AU") return true;
		if (f.DatInizio.value != "") {
			var inizio = convertStr2Date(f.DatInizio.value);

			/* 1. Bloccante: data inizio validità successiva alla data fine validità. */
			if (f.datFine.value != "") {
				var fine = convertStr2Date(f.datFine.value);

				var deltaT = confrontaDateObj(inizio, fine);
				if (deltaT < 0) {
					alert("La data di inizio validità non può essere superiore a quella di fine validità!");
					f.DatInizio.focus();
					return false;	// bloccante
				}
			}

			/* 2. Non bloccante: data inizio validità del documento successiva a quella corrente */
			var adesso = new Date();
			adesso.setHours(0);
			adesso.setMinutes(0);
			adesso.setSeconds(0);
			var deltaTo = confrontaDateObj(inizio, adesso);

			if (deltaTo < 0) {
				if (! confirm("La data di inizio validità è superiore a quella odierna!\n\n"+
							 "Si desidera continuare?")) {
					f.DatInizio.focus();
					return false;
				}
			}
		}







		// CONTROLLI SULLA DATA DI INIZIO ACQUISIZIONE/RILASCIO
		if (f.DatAcqril.value != "") {
			var acqril = convertStr2Date(f.DatAcqril.value);

			/* 1. Bloccante: data inizio validità successiva alla data di acquisizione/rilascio. */
			if (f.DatInizio.value != "") {
				var inizio = convertStr2Date(f.DatInizio.value);

				var deltaT = confrontaDateObj(inizio, acqril);
				if (deltaT < 0) {
					alert("La data di inizio validità non può essere superiore a quella di acquisizione/rilascio!");
					f.DatInizio.focus();
					return false;	// bloccante
				}
			}

			/* 2. Bloccante: data inizio validità del documento successiva a quella corrente */
			var adesso = new Date();
			adesso.setHours(0);
			adesso.setMinutes(0);
			adesso.setSeconds(0);
			var deltaTo = confrontaDateObj(acqril, adesso);

			if (deltaTo < 0) {
				alert("La data di inizio acquisizione/rilascio è superiore a quella odierna!");
				f.DatAcqril.focus();
				return false;	// bloccante
			}
		}

		// AndS 07/10/05: se la protocollazione e' manuale e si sta protocollando 
		// allora bisogna aggiornare il campo dataOraProt
		if (document.form.tipoProt.value=="N" && document.form.CODSTATOATTO.value=="PR" && _codStatoAtto!="PR")
			document.form.dataOraProt.value=document.form.dataProt.value+" "+ document.form.oraProt.value;

		// Se sono qua, va tutto bene
		return true;
	}

	function blobVuotoWarning(file)	{
		alert("Nel database non è stato trovato il file:\n\"" + file + "\"");
	}
function caricaInfoProtocollo () {	
	request = "AdapterHTTP?PAGE=NuovoProtocolloPage&dummy="+new Date().getTime(); // evito cosi' che la cache faccia brutti scherzi
	var result = syncXMLHTTPGETRequest(request);
	if (result == null  ) {
		alert("Errore in lettura dati protocollo \n");
	} else {
		xmlresult = result.responseXML.documentElement; 
		if (xmlresult==null) {
			alert ("Impossibile leggere i dati dal server");
			return;
		}
		datiElements=xmlresult.getElementsByTagName("M_GETPROTOCOLLAZIONE");
		rows=datiElements[0].getElementsByTagName("ROW");		
		if (rows.length>0) {
			row = rows[0];
			dataOraProt = row.getAttribute("DATAORAPROT");
			dataProt = dataOraProt.substring(0,10);
			oraProt = dataOraProt.substring(11,16);
			prAutomatica = row.getAttribute("FLGPROTOCOLLOAUT");
			if ( prAutomatica=="S" ) {
				// le informazioni sono in sola lettura
				// lo erano gia' prima
				/* 
				document.form.oraProt.readonly=true;
				document.form.dataProt.readonly=true;
				document.form.numProtocollo.readonly=true;
				*/
				// recupero delle info dallo xml ricevuto
				if (document.form.flgProtLocale.value == "S") {
					numProt     = row.getAttribute("NUMPROTOCOLLO");
					numAnnoProt = row.getAttribute("NUMANNOPROT");
					kLockProt   = row.getAttribute("NUMKLOPROTOCOLLO");
					// abilitazione del campo lock della tabella protocollo
					document.form.KLOCKPROT.disabled=false;
				} else {
					numProt="";
					numAnnoProt="";
					kLockProt = "";
					oraProt = "00:00";
					document.form.KLOCKPROT.disabled=true;
				}
				// assegnazione dei valori ai campi
				document.form.oraProt.value=oraProt;
				document.form.dataProt.value=dataProt;
				document.form.numProtocollo.value=numProt;
				document.form.numAnnoProt.value=numAnnoProt;
				document.form.dataOraProt.value=dataOraProt;
				// la protocollazione e' automatica
				document.form.tipoProt.value="S";
				document.form.KLOCKPROT.value=kLockProt;
			}
			else {
				// deve essere possibile editare le info di protocollazione		
				document.form.oraProt.className="inputEdit";				
				document.form.dataProt.className="inputEdit";
				document.form.numProtocollo.className="inputEdit";
				document.form.oraProt.readOnly=false;
				document.form.dataProt.readOnly=false;
				document.form.numProtocollo.readOnly=false;
				document.form.oraProt.disabled=false;
				document.form.dataProt.disabled=false;
				document.form.numProtocollo.disabled=false;
				// aggiornamento delle info
				numAnnoProt = row.getAttribute("NUMANNOPROT");
				document.form.numAnnoProt.value=numAnnoProt;
				document.form.oraProt.value=oraProt;
				document.form.dataProt.value=dataProt;
				// il lock del protocollo va disabilitato
				document.form.KLOCKPROT.value="";
				document.form.KLOCKPROT.disabled=true;
				// la protocollazione non e' automatica
				document.form.tipoProt.value="N";
			}
		}
	}
}
// EOF
