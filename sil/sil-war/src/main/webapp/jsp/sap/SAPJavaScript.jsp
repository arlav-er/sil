<script>
/**
 * Funzioni di validazione dei form di importazione MySAP in SIL.
 * E' stato necessario includere le funzioni in un file JSP invece che JS
 * per evitare conflitti con il tag af:linkScript.
 * @author Guido Zuccaro
 * @since 01/04/2016
 */

/**
 * Valori dell'attributo style.display.
 */
const SHOW = '';
const HIDE = 'none';

/**
 * ID degli errori nelle sezioni.
 */
const ERR_IMPORTA = 'errImporta';
const ERR_IS_NULL = 'Campo obbligatorio non valorizzato: ';

/**
 * Campi obbligatori delle sezioni.
 */
const NOT_NULL_TIT_STU = ['codTitolo', 'codMonoStato'];
const NOT_NULL_FOR_PRO = ['codCorso'];
const NOT_NULL_LINGUE  = ['codLingua', 'codLetto'];
const NOT_NULL_CON_INF = ['codTipoInfo', 'codDettInfo', 'cdnGrado'];
const NOT_NULL_ABILITA = ['codAbilitazioneGen'];
const NOT_NULL_ESP_LAV = [];

var chiudiSezioniVuote = true;

/**
 * Effettua la validazione del form.
 * @param idSezione: id della sezione da validare.
 */
function validateForm(idForm, idErrori, lstField) {
  var submit = true;
  for (var r = 0; r < getNumRecord(lstField[0]); r++) 
    for (var f = 0; f < lstField.length; f++)
      if (getValue(lstField[f], r) == '') {
          showError(idErrori, ERR_IS_NULL + lstField[f] + '[' + r + ']');
          editField(idForm, lstField[f], r);
          submit = false;
      }
  if (submit)
    document.getElementById(idForm).submit();
}

/**
 * Restituisce il valore di un campo.
 * @param strFieldName: nome del campo.
 * @return Il valore del campo 
 * oppure una stringa vuota in caso di errore.
 */
function getValue(strFieldName, i) {
  try {
    return document.getElementsByName(strFieldName)[i].value;
  } catch (objError) {
    return '';
  }
}

/**
 * Mostra un messaggio di errore.
 * Gli errori vengono mostrati in testa alla pagina 
 * ed alla sezione di competenza.
 * @param strMessage: messaggio di errore.
 */
function showError(idErrori, strMessage) {
  const BR = '<br/>';
  // document.getElementById(ERR_IMPORTA).innerHTML += strMessage + BR;
  document.getElementById(idErrori).innerHTML += strMessage + BR;
}

/**
 * Restituisce il numero di record in una sezione 
 * in base ai valori del campo chiave.
 * @param idSezione: id della sezione che contiene i record.
 * @param strChiave: nome del campo chiave.
 */
function getNumRecord(strChiave) {
  return document.getElementsByName(strChiave).length;
}
/**
 * Cambia lo stato di tutti i check box della form.
 * Se idForm == '' cambia lo stato per tutta la pagina. 
 * @param idMaster: bottone che comana lo stato dei check. 
 * @param idForm: form che contiene i check da impostare
 */
function selectAllCheck(idMaster, idForm) {
  var master = document.getElementById(idMaster);
  var check; 
  if(idForm != '')
    check = document.querySelectorAll('#' + idForm + ' input[type="checkbox"]');
  else 
    check = document.querySelectorAll('input[type="checkbox"]');
    for (var c = 0; c < check.length; c++) 
      check[c].checked = master.checked;
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
  if (tabella.style.display == HIDE) {
    tabella.style.display = SHOW;
    bottone.src = '../../img/aperto.gif';
  } else {
    tabella.style.display = HIDE;
    bottone.src = '../../img/chiuso.gif';
  }
}

/**
 * Mostra un campo in editing.
 *  
 * @param strForm: id del form.
 * @param strField: nome del campo.
 * @param iRecord: indice del record.
 */
function editField(strForm, strField, iRecord) {
  // prefissi degli id
  const LBL = 'lbl';
  const EDT = 'edt';

  // recupera le td della table
  var edtField = document.getElementById(EDT + '.' + strForm + '.' + strField + '.' + iRecord);
  var lblField = document.getElementById(LBL + '.' + strForm + '.' + strField + '.' + iRecord);

  // cambia lo stato
  edtField.style.display = SHOW;
  lblField.style.display = HIDE;
} 
</script>