/*
* Chiamata quando viene l' operatore decide di forzare una operazione di inserimento di un movimento
*/
function confermaInserimento(elementName, newValue) {
	vElem = document.getElementsByName(elementName);
	vElem[0].value=newValue;
	selezionaComboAgevolazioni();
  	doFormSubmit(document.Frm1);
  }