/* ----------------------------------------------
 *  FUNZIONI PER DOCUMENTI e DOCUMENTI ASSOCIATI
 *  Nuova versione by Luigi Antenucci
 * ----------------------------------------------
 */

/*
 * Questo script viene utilizzato per aprire in pop-up una lista di documenti associati
 * al lavoratore ad una certa pagina (jsp).
 * La var "pagina" contiene la PAGE associata (attraverso il file publishers.xml) alla JSP.
 */
function docAssociati(cdnLavoratore,
						pagina, cdnFunzione, parentFrameName, strChiaveTabella) {

	var myqs  = "&cdnLavoratore=" + cdnLavoratore +
				"&lookLavoratore=false" +
				"&lookAzienda=true" +
				"&contesto=L";

	docOpenPopupAbstract("DocumentiAssociatiPage", myqs,
						 pagina, cdnFunzione, parentFrameName, strChiaveTabella);
}

/*
 * Analogo al precedente, ma funziona per un'azienda (data da "prgAzienda" e "prgUnita").
 */
function docAssociatiAzienda(prgAzienda, prgUnita,
							 pagina, cdnFunzione, parentFrameName, strChiaveTabella) {

	var myqs  = "&prgAzienda=" + prgAzienda + "&prgUnita=" + prgUnita +
				"&lookLavoratore=true" +
				"&lookAzienda=false" +
				"&contesto=A";

	docOpenPopupAbstract("DocumentiAssociatiPage", myqs,
						 pagina, cdnFunzione, parentFrameName, strChiaveTabella);
}

/*
 * Questo script apre in pop-up il dettaglio del documento di identificazione
 * valido per il lavoratore dato.
 * Se non ve ne sono apre la videata di inserimento.
 */
function docDettaglioIdentif(cdnLavoratore, prgDocumento,
							 pagina, cdnFunzione, parentFrameName, strChiaveTabella, fromPatto) {

	// NB: lookLavoratore=false perche' non si deve poter modificare il lavoratore impostato
	//     lookAzienda=false perche' non si deve selezionare un'azienda.
	var myqs  = "&cdnLavoratore=" + cdnLavoratore +
				"&lookLavoratore=false" +
				"&lookAzienda=false" +
				"&contesto=L" +
				"&SALVADATIDID=true" +
				"&CARTAIDENTITA=CARTAIDENTITA" +
 	            "&pageDocAssociata=" + pagina;
	if (fromPatto)
		myqs += "&fromPattoDettaglio=1";
	if (docAssIsDef(prgDocumento) && (prgDocumento != "0") && (prgDocumento != "null")) {
		myqs += "&prgDocumento=" + prgDocumento;
	} else {
		myqs += "&NUOVO=true";
	}
	
	docOpenPopupAbstract("DettagliDocumentoPage", myqs,
						 pagina, cdnFunzione, parentFrameName, strChiaveTabella);
}

// ----------------------------------------------------------------------------------------

function docAssIsDef(p) {
	return (p != undefined) && (p != null) && (p != "");
}


function docOpenPopupAbstract(page, pezzoQueryString, 
							  pagina, cdnFunzione, parentFrameName, strChiaveTabella) {
	// Questo script viene utilizzato per aprire in pop-up una lista di documenti associati ad una data pagina(jsp).
	// La var pagina contiene la PAGE associata (attraverso il file publishers.xml) alla JSP.

	var url2add = "";
	if (docAssIsDef(pagina)) {
		url2add += "&pagina=" + pagina;
	}
	if (docAssIsDef(strChiaveTabella)) {
		url2add += "&strChiaveTabella=" + strChiaveTabella;
	}
	if (docAssIsDef(cdnFunzione)) {
		url2add += "&cdnFunzione=" + cdnFunzione;
	}
	if (docAssIsDef(parentFrameName)) {
		url2add += "&FRAME_NAME=" + parentFrameName;
	}

	// DEBUG: alert("url2add=\n" + url2add);
	
	var url = "AdapterHTTP?PAGE=" + page +
							pezzoQueryString +		// "cdnLavoratore=.." o "prgAzienda=.."
							url2add +
							"&popUp=true";
	
	// Apre la finestra
	var w=(screen.availWidth)*0.85;  var l=(screen.availHeight)*0.1;
	var h=(screen.availHeight)*0.8;  var t=(screen.availHeight)*0.1;
	var feat = "status=NO,location=NO,toolbar=NO,scrollbars=YES,resizable=YES," +
	  			 "height="+h+",width="+w+",top="+t+",left="+l;
	var opened = window.open(url, "_blank", feat);
	opened.focus();
}
