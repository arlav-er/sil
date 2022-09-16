<SCRIPT TYPE="text/javascript">
	function selectMansione_onClick(codMansione, codMansioneHid, descMansione, strTipoMansione) {	
		if (codMansione.value == "") {
			descMansione.value = "";
	      	strTipoMansione.value = "";      
	    }
	    else {
	    	if (codMansione.value != codMansioneHid.value) {
	        	window.open("AdapterHTTP?PAGE=RicercaMansionePage&FLGIDO=S&codMansione="+codMansione.value, "Mansioni", 'toolbar=0, scrollbars=1');
	    	}
	    }
	}

	function ricercaAvanzataMansioni() {
	    window.open("AdapterHTTP?PAGE=RicercaMansioneAvanzataPage&FLGIDO=S", "Mansioni", 'toolbar=0, scrollbars=1,height=600, width=800');
	}
</SCRIPT>
