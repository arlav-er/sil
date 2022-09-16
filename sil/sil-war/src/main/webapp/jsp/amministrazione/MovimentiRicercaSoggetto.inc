<%/*
    Funzione che apre la finestra di ricerca rapida, accetta cinque argomenti: 
    - soggetto: deve essere o 'Aziende' o 'Lavoratori', nel primo caso esegue la ricerca per Aziende,
      nel secondo quella per lavoratori.
    - funzionediaggiornamento: il nome della funzione Javascript (contenuta nella pagina che chiama la 
      ricerca) che si occupa di aggiornare la pagina stessa.
    - prgAzienda, prgUnita, cdnLavoratore: eventuali progressivi su cui basare la ricerca
*/%>

<script type="text/javascript">
    var opened;
    
    function apriSelezionaSoggetto(soggetto, funzionediaggiornamento, prgAzienda, prgUnita, cdnLavoratore) {
        var f = "AdapterHTTP?PAGE=MovimentiSelezionaSoggettoPage&MOV_SOGG=" + soggetto + "&AGG_FUNZ=" + funzionediaggiornamento + "&CDNFUNZIONE=<%=_funzione%>";
        if (prgAzienda != '' && (typeof prgAzienda) != "undefined") {f = f + "&PRGAZ=" + prgAzienda;}
        if (prgUnita != '' && (typeof prgUnita) != "undefined") {f = f + "&PRGUAZ=" + prgUnita;}
        if (cdnLavoratore != '' && (typeof cdnLavoratore) != "undefined") {f = f + "&CDNLAV=" + cdnLavoratore;}
        var t = "_blank";
        var feat = "toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,width=750,height=650,top=30,left=180";
        opened = window.open(f, t, feat);
    }

 </script>
