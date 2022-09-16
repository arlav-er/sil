/*
 * FUNZIONI USATE DELL' ".INC" DI "ProtocolloDoc_Elemento.inc".
 */

var vAmbiti = new Array();

function Ambito(codAmbito, codTipoDoc, descTipoDoc) {
	this.codAmbito   = codAmbito;
	this.codTipoDoc  = codTipoDoc;
	this.descTipoDoc = descTipoDoc;
}

/* richiamata dal cambio della combo del campo "Riferimento" nella sezione del PROTOCOLLO */
function ComboAmbito_onChange() {

	var tipoAmbitoObj    = document.forms[0].ambito;
	var tipoDocumentoObj = document.forms[0].tipoDocumento;

	indice = tipoAmbitoObj.selectedIndex;
	tipo   = tipoAmbitoObj.options[indice].value;
	j=0;

	resetCombo(tipoDocumentoObj);
	tipoDocumentoObj.options[j++]=new Option("", "", false, false);
	for (i=0; i < vAmbiti.length; i++) {
		if ((vAmbiti[i].codAmbito == tipo) || (tipo == "")) {
			tipoDocumentoObj.options[j]=new Option(vAmbiti[i].descTipoDoc, vAmbiti[i].codTipoDoc, false, false);
			j++;
		}
	}
}

function resetCombo(comboObj) {
	while (comboObj.options.length > 0) {
		comboObj.options[0] = null;
	}
}
