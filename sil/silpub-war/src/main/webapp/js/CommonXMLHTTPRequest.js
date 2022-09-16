// CommonXMLHTTPRequest.js
//Questo file contiene alcune funzioni che si occupano di inviare al
//server le richieste XMLHTTP necessarie per il
//recupero dei dati durante la navigazione dell'utente.
//Le funzioni presenti in questa libreria scelgono automaticamente gli 
//oggetti da utilizzare a seconda del tipo di browser utilizzato dall'utente


//Esegue una richiesta XMLHTTP di tipo GET sincrona utilizzando come 
//url del file XML da caricare il parametro passato e restituisce la risposta
//ottenuta come oggetto XMLdocument 
//(sulla quale si può navigare con gli appositi metodi). 
//In caso di problemi ritorna null.
function syncXMLHTTPGETRequest(request) {

	var xmlhttp = null;
	var xmlDoc = null;
	try {    	
		if(window.ActiveXObject) {
			//Caso IExplorer (non a norma W3C)
			xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
			xmlhttp.open("GET", request, false);
			xmlhttp.send(null);
			xmlDoc = new ActiveXObject("Microsoft.XMLDOM");
			xmlDoc.async = false;
			xmlDoc.loadXML(xmlhttp.responseText);
    	 	return xmlDoc;
        } else if (document.implementation && document.implementation.createDocument) {
          	//Caso Netscape e mozilla 
          	//(in realtà è quello dettato dal W3C che dovrebbe diventare universale... 
          	//infatti è molto + semplice!)
      		var xmlDoc=document.implementation.createDocument("", "", null);
      		xmlDoc.async = false;
			xmlDoc.load(request);
      		return xmlDoc;
        } else { 
        	//Altri browser non supportati
        	return null;
        }		
 	} catch(e){
 		//Se ho problemi ritorno null
 		return null;
  	}
}

//Controllo di esistenza (case insensitive) di una chiave all'interno di una colonna di una tabella del DB
//Utile nei controlli dei campi come qualifica, ccnl, comune, ecc... nelle form dell'applicativo.
//Restituisce true o false a seconda dell'esistenza o meno del codice.
//Se si verificano errori lancia un'eccezione stringa con un messaggio di default 
//(sovrascrivibile dall'utilizzatore).
function controllaEsistenzaChiave(chiave, nomeColonna, nomeTabella) {
	var request = "AdapterHTTP?PAGE=ControllaEsistenzaChiavePage&CHIAVE=" + chiave 
				+ "&NOMECOLONNA=" + nomeColonna + "&NOMETABELLA=" + nomeTabella;
	
	//Controllo sul server
	var result = syncXMLHTTPGETRequest(request);
	if (result == null) {
		throw "Impossibile controllare l'esistenza del codice " + chiave;
	} else {
		if (result.documentElement.childNodes.item(0).nodeValue != "true") {
			return false;
		} else return true;
	}
}

//Dato il nome del modulo per il reperimento delle informazioni del lavoratore o dell'azienda,
//restituisce la riga tramite la quale prelevare i singoli dati.
//@author rolfini
function getXMLHTTPRow(result, nomeModulo) {

		//reperisco la root della response xml
		//se la root è nulla, c'è stato qualche problema nella response
		xmlresult = result.documentElement; 
		if (xmlresult==null) { 
			throw "Impossibile reperire la response!!!";
		}
		
		//prelevo la lista di elementi corrispondenti al nome del modulo passato nei parametri
		//naturalmente la lista deve avere un solo valore.
		//se la lista è vuota, il modulo specificato non esiste nella response
		datiElements=xmlresult.getElementsByTagName(nomeModulo);
		if (datiElements==null) {
			throw "Impossibile reperire il modulo richiesto dalla response";
		} 		
		
		//prelevo il primo (ed unico) elemento della lista.
		//se l'elemento non esiste, il modulo non ha trovato alcun dato
		//corrispondente
		dati=datiElements[0];
		if (dati==null) {
			throw "Impossibile reperire i dati richiesti nel modulo";
		}
		
		//prelevo gli elementi "ROW". Ne Deve esistere uno solo
		rows=dati.getElementsByTagName("ROW");
		if (rows==null){
			throw "Impossibile reperire i dati richiesti nel modulo";
		}
		row=rows[0];		
		//restituisco l'elemento ROW trovato. 
		//al suo interno troveremo tanti attributi quanti sono
		//i dati restituiti.
		return row;
}
