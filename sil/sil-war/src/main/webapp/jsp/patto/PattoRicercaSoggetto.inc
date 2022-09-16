<%/*
    Funzione che apre la finestra di ricerca rapida, accetta cinque argomenti: 
    - funzionediaggiornamento: il nome della funzione Javascript (contenuta nella pagina che chiama la 
      ricerca) che si occupa di aggiornare la pagina stessa.
    - cdnLavoratore: eventuali progressivi su cui basare la ricerca
*/%>

<script type="text/javascript">
    var opened;
    
    function apriSelezionaSoggetto(funzionediaggiornamento, cdnLavoratore) {
    	var formObj = document.Frm1;
    	if (formObj.CODPROGRAMMA.value == "") {
    		alert("Selezione prima il campo Programma");
    	}
    	else {
	        var f = "AdapterHTTP?PAGE=SelezionaEnteAccreditatoPage&AGG_FUNZ=" + funzionediaggiornamento + "&CDNFUNZIONE=<%=_funzione%>";
	    	f = f + "&CODPROGRAMMA=" + formObj.CODPROGRAMMA.value;
	        if (cdnLavoratore != '' && (typeof cdnLavoratore) != "undefined") {f = f + "&CDNLAV=" + cdnLavoratore;}
	        var t = "_blank";
	        var feat = "toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,width=900,height=700,top=30,left=180";
	        opened = window.open(f, t, feat);
    	}
    }

 </script>
