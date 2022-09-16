<%--
	Esempio di estensione dei controlli sulla FORM.
	Es. validazioni ulteriori dei campi di input in RICERCA e DETTAGLIO.
	Vedansi documento "Sulle novità del tag di FORM.doc".
--%>

<af:linkScript path="../../js/"/>


<script language="Javascript">

	// Funzione per i controlli particolari (fuori standard tag-library)
	// Deve rendere un boolean: TRUE=controlli ok; FALSE=errore (abort submit)
	function checkCampiAggiuntiva() {
		var f = document.form;

		if (f.nome.value == "pippo") {
			alert("nome non accettabile");
			f.nome.focus();
			return false;
		}
		// Se sono qui va tutto bene
		return true;
	}


	// Fa qualcosa, valida i dati della form e ne fa il submit via Javascript
	function faiQualcosa1() {
		// Se la pagina è già in submit, ignoro questo nuovo invio!
		if (isInSubmit()) return;

		// ... fai qualcosa ...
		
		// Esegue SUBMIT manuale con validazione dei dati della form
		var datiOk = controllaFunzTL();
		datiOk = datiOk && checkCampiAggiuntiva();
		if (datiOk)
			doFormSubmit(document.form);
		else
			undoSubmit();
	}

	// Fa qualcosa, e fa il submit della form via Javascript SENZA validazione!
	function faiQualcosa2() {
		// Se la pagina è già in submit, ignoro questo nuovo invio!
		if (isInSubmit()) return;

		// ... fai qualcosa ...
		
		doFormSubmit(document.form);
		// oppure:  setWindowLocation("AdapterHTTP?PAGE=ZzzPage");
	}

</script>

. . .
. . .

<af:form name="form" action="AdapterHTTP" method="POST" onSubmit="checkCampiAggiuntiva()">
<input type="hidden" name="PAGE" value="xxxPage" />
. . .

	<%-- impostare gli attributi "validateOnPost" e "required" dei campi dove serve --%>
	<af:textBox name="xxx" type="date"
				validateOnPost="true"
				required="true"
				. . .
				/>

	. . .
	. . .

	<input type="submit" class="pulsanti" value="Inserisci" />
	<input type="reset"  class="pulsanti" value="Annulla" />

	<input type="button" class="pulsanti" name="fai1B" value="Fai 1" onClick="faiQualcosa1()" />
	<input type="button" class="pulsanti" name="fai2B" value="Fai 2" onClick="faiQualcosa2()" />

</af:form>
