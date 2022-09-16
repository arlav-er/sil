<script language="Javascript">

var checkAllegati = <%=checkAllegati%>;
//var checkFirma = <%--=checkFirma--%>;
var msgConfermaGeneraStampa = "<%=msgConfermaGeneraStampa%>";

function generaStampa(arrMandatoryTypeAttachs) {
	// Se la pagina è già in submit, ignoro questo nuovo invio!
//	if (isInSubmit())
//		return;

	if (checkAllegati == false) {
		//alert("Attenzione! La stampa prevede almeno un allegato obbligatorio. Utilizzare il pulsante 'Inserisci allegato' per acquisirlo");
		
		var mex = "Attenzione! La stampa prevede i seguenti allegati obbligatori: \n \n";
		var mexAttachs = "";
		var fields = arrMandatoryTypeAttachs.split(';');
		var j = 0;
		for (var i = 0; i < fields.length-1; i++) {
		    j = j + 1;
			mexAttachs = mexAttachs + "["+ j +"] " + fields[i] + " \n";
		}
		alert(mex + mexAttachs);
		
		//alert("Attenzione! Sugli allegati che risultano obbligatori e' necessario acquisire almeno i dati minimi");
		return;
	}

//	if (checkFirma == false) {
//		alert("Attenzione! Consenso non presente. Per la firma grafometrica e' necessario raccogliere il consenso mediante la funzione Gestione consenso");
//		return;
//	}
		
	if (flagChanged == true) {
		if (!confirm("I dati sono cambiati.\nProcedere lo stesso ?")) {
			return;
		} else {
			if (msgConfermaGeneraStampa != "") {
				if (confirm(msgConfermaGeneraStampa)) {
					document.form.PAGE.value = "EditorPageLav";
					document.form.submit();	
				}
				else {
					return;
				}
			}
			else {
				document.form.PAGE.value = "EditorPageLav";
				document.form.submit();
			}
		}
	} else {
		if (msgConfermaGeneraStampa != "") {
			if (confirm(msgConfermaGeneraStampa)) {
				document.form.PAGE.value = "EditorPageLav";
				document.form.submit();	
			}
			else {
				return;
			}
		}
		else {
			document.form.PAGE.value = "EditorPageLav";
			document.form.submit();
		}
	}
}

function gestioneAllegatiDocumento() {

	if (flagChanged == true) {
		if (!confirm("I dati sono cambiati.\nProcedere lo stesso ?")) {
			return;
		} else {
			document.form.PAGE.value = "GestAllegatiDocumentoPage";
			document.form.submit();
		}
	} else {
		document.form.PAGE.value = "GestAllegatiDocumentoPage";
		document.form.submit();
	}
}

function annulla() {
	
	if (flagChanged == true) {
		if (!confirm("I dati sono cambiati.\nProcedere lo stesso ?")) {
			return;
		} else {
			document.form.PAGE.value = "ListaStampeParLavPage";
			document.form.submit();
		}
	} else {
		document.form.PAGE.value = "ListaStampeParLavPage";
		document.form.submit();
	}
}

</script>
