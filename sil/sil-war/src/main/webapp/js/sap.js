/**
 * Funzioni di validazione dei form di importazione MySAP in SIL.
 */

/**
 * Valori dell'attributo style.display.
 */
var SHOW = '';
var HIDE = 'none';

/**
 * ID degli errori nelle sezioni.
 */
var TITOLO = '_titolo_';
var ERR_IMPORTA = 'Per importare i dati nel SIL, e\' necessario compilare alcuni campi evidenziati nelle sezioni sottostanti.';
var ERR_IMPORTA_SEZIONE = 'Per importare i dati nel SIL, e\' necessario compilare i campi evidenziati.<br>&nbsp;';
var WAR_CHECKS = 'Nessun elemento selezionato in una o piu\' sezioni.';
var WAR_CHECK = 'Nessun elemento selezionato nella sezione ' + TITOLO + '.';
var CONFIRM_SECTION = 'L\'operazione effettuera\' l\'importazione delle voci selezionate appartenenti alla sezione ' + TITOLO + '.\nConfermare?';

/**
 * Campi obbligatori delle sezioni.
 */
var NOT_NULL_TIT_STU = ['codTitolo','codMonoStato'];
var NOT_NULL_FOR_PRO = [];
var NOT_NULL_LINGUE  = ['codLingua','cdnGradoLetto','cdnGradoScritto','cdnGradoParlato'];
var NOT_NULL_CON_INF = [];
var NOT_01_PATENTI   = ['Patenti_codAbilitazioneGenHid'];
var NOT_NULL_ESP_LAV = ['codMansione','codContratto'];
var NOT_NULL_PROPEN  = ['codMansione','codOrarioHid'];

var chiudiSezioniVuote = true;

function checkForSubmit(idForm, idErrori, lstField) {
  var submit = true;
  for (var r = 0; r < getNumRecord(idForm, lstField[0]); r++) {
	if (getIfIsChecked(idForm, r)) {
		if (idForm == 'frmPropen') {
			for (var z = 0; z < getNumRecord(idForm, lstField[1] + '_' + r); z++) {
				if (getMatrixValue(idForm, lstField[1], r, z) == '') {
					editMatrixField(idForm, lstField[1], r, z);
					submit = false;
				}					
			}				
		} else {
			for (var f = 0; f < lstField.length; f++) {
				if (getArrayValue(idForm, lstField[f], r) == '') {
					editArrayField(idForm, lstField[f], r);
					submit = false;
				}	
			}
		}
	}
  }
  return submit;
}


function validateSection(idForm, idErrori, lstField) {
  var validate = true;
  if (checkIfSelectedChecks(idForm)) {
	  if (confirm(CONFIRM_SECTION.replace(TITOLO, document.getElementById(idForm).value))) {
		  validate = checkForSubmit(idForm, idErrori, lstField);
		  if (!validate) {
			  showError(idErrori, ERR_IMPORTA_SEZIONE);
		  } else {
			  document.getElementsByName("manage")[0].value = idForm;
		  }
	  } else {
		  validate = false;
	  }
  } else {
  	  alert(WAR_CHECK.replace(TITOLO, document.getElementById(idForm).value));
  	  validate = false;
  }
  return validate;
}

function validateAllSections() {
  if (checkIfSelectedChecks('')) {
	  var chkTitStu = checkForSubmit('frmTitStu', 'errTitStu', NOT_NULL_TIT_STU);
	  var chkForPro = checkForSubmit('frmForPro', 'errForPro', NOT_NULL_FOR_PRO);
	  var chkLingue = checkForSubmit('frmLingue', 'errLingue', NOT_NULL_LINGUE);
	  var chkConInf = checkForSubmit('frmConInf', 'errConInf', NOT_NULL_CON_INF);
	  var chkAbilita = checkForSubmit('frmAbilita', 'errAbilita', NOT_01_PATENTI);
	  var chkEspLav = checkForSubmit('frmEspLav', 'errEspLav', NOT_NULL_ESP_LAV);  
	  var chkPropen = checkForSubmit('frmPropen', 'errPropen', NOT_NULL_PROPEN);
	  
	  if (chkTitStu && chkForPro && chkLingue && chkConInf && chkAbilita && chkEspLav && chkPropen) {
		  return true;
	  } else {
	  	  showError('errImporta', ERR_IMPORTA);
		  return false;
	  }
  } else {
  	  alert(WAR_CHECKS);
	  return false;
  }  
}

function setCampoHidden(combo, campohidden) {
	document.getElementsByName(campohidden)[0].value = combo.value;
}

/**
 * Restituisce il valore di un campo.
 * @param strFieldName: nome del campo.
 * @return Il valore del campo 
 * oppure una stringa vuota in caso di errore.
 */
function getArrayValue(strForm, strField, i) {
  try {
    return document.getElementsByName(strForm + '_' + strField + '_' + i)[0].value;
  } catch (objError) {
    return '';
  }
}

function getMatrixValue(strForm, strField, i, j) {
  try {
    return document.getElementsByName(strForm + '_' + strField + '_' + i + '_' + j)[0].value;
  } catch (objError) {
    return '';
  }
}

//pezza a colori fosforescenti
function getIfIsChecked(strForm, i) {
  try {
	var isChecked = false;
	if (strForm == 'frmAbilita') {
		isChecked = document.getElementsByName('frmAbilita_Patenti_chkImporta')[0].checked;
	} else {
		isChecked = document.getElementsByName(strForm + '_chkImporta_' + i)[0].checked;
	} 
    return isChecked;
  } catch (objError) {
    return false;
  }
}

function showError(idErrori, strMessage) {
  document.getElementById(idErrori).innerHTML = strMessage;
}

function getNumRecord(strForm, strField) {
	var inputs = document.getElementsByTagName("input");
	var j = 0;
	for(var i = 0; i < inputs.length; i++) {
	    if(inputs[i].name.indexOf(strForm + '_' + strField) != -1) {
	        j++;
	    }
	}	
	return j;
}
/**
 * Cambia lo stato di tutti i check box della form.
 * Se idForm == '' cambia lo stato per tutta la pagina. 
 * @param idMaster: bottone che comana lo stato dei check. 
 * @param idForm: form che contiene i check da impostare
 */
function selectAllCheck(idMaster, idForm) {
	var master = document.getElementById(idMaster);
	  
	var allInputs = document.getElementsByTagName("input");
	for (var i = 0, max = allInputs.length; i < max; i++) {
		if (allInputs[i].type == 'checkbox') {
			if(idForm != '') {
				if (allInputs[i].name.indexOf(idForm) != -1) 
					allInputs[i].checked = master.checked;
			} else {
				allInputs[i].checked = master.checked;
			}
		}
	}  
}

function checkIfSelectedChecks(idForm) {
	var allChecks = false;
	var allInputs = document.getElementsByTagName("input");
	for (var i = 0, max = allInputs.length; i < max; i++) {
		if (allInputs[i].type == 'checkbox') {
			if(idForm != '') {
				if (allInputs[i].name.indexOf(idForm) != -1) 
					if (allInputs[i].checked) 
						allChecks = true;
			} else {
				if (allInputs[i].checked) 
					allChecks = true;
			}
		}
	}  
	return allChecks;
}

/**
 * Mostra / nasconde la sezione.
 * @param idBottone: lo stato del bottone 
 * che richiama la funzione viene usato
 * per impostare lo stato dei flag.
 * @param idTabella: <table> che contiene i flag da impostare:
 * se e' vuota imposta tutti i flag della pagina.
 */
function toggle(idBottone, idTabella) {
  bottone = document.getElementById(idBottone);
  tabella = document.getElementById(idTabella);

  if (typeof tabella != 'undefined' && tabella != null) {
	  if (tabella.style.display == HIDE) {
	    tabella.style.display = SHOW;
	    bottone.src = '../../img/aperto.gif';
	  } else {
	    tabella.style.display = HIDE;
	    bottone.src = '../../img/chiuso.gif';
	  }
  }
}

/**
 * Mostra un campo in editing.
 *  
 * @param strForm: id del form.
 * @param strField: nome del campo.
 * @param iRecord: indice del record.
 */
function editArrayField(strForm, strField, iRecord) {
  // prefissi degli id
  var LBL = 'lbl';
  var EDT = 'edt';
  var ETI = 'eti';
  if (strForm == 'frmAbilita')	strField = strField.replace('_','.');

  // recupera le td della table
  var etiField = document.getElementById(ETI + '.' + strForm + '.' + strField + '.' + iRecord);
  if (!etiField) etiField = document.getElementById(ETI + '.' + strForm + '.' + strField);
  var edtField = document.getElementById(EDT + '.' + strForm + '.' + strField + '.' + iRecord);
  var lblField = document.getElementById(LBL + '.' + strForm + '.' + strField + '.' + iRecord);

  // cambia lo stato
  etiField.style.color = 'red';
  edtField.style.display = SHOW;
  lblField.style.display = HIDE;
}  

function editMatrixField(strForm, strField, iRecord, zRecord) {
  // prefissi degli id
  var LBL = 'lbl';
  var EDT = 'edt';
  var ETI = 'eti';

  // recupera le td della table
  var etiField = document.getElementById(ETI + '.' + strForm + '.' + strField + '.' + iRecord);
  var edtField = document.getElementById(EDT + '.' + strForm + '.' + strField + '.' + iRecord + '.' + zRecord);
  var lblField = document.getElementById(LBL + '.' + strForm + '.' + strField + '.' + iRecord + '.' + zRecord);

  // cambia lo stato
  etiField.style.color = 'red';
  edtField.style.display = SHOW;
  lblField.style.display = HIDE;
}

