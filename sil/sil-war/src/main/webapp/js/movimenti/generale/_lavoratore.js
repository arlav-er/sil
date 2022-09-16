  function apriInserisciLavoratore() {
    var f = "AdapterHTTP?PAGE=ANAGDETTAGLIOPAGEANAGINS&PRGMOVIMENTOAPP=" + prgMovApp + "&AGG_FUNZ=aggiornaLavoratore&CDNFUNZIONE=1&PROVENIENZA=MOVIMENTI&VALIDAMOV=S";
    if (document.Frm1.CURRENTCONTEXT != null) {
    	f = f + "&context=" +  document.Frm1.CURRENTCONTEXT.value;
    }	
    var t = "_blank";
    var feat = "toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,width=750,height=600,top=0,left=100";
    opened = window.open(f, t, feat);
  }

  //funzione per la selezione del lavoratore con pulsante di lookup
  function aggiornaLavoratore() {
   	document.Frm1.CODCPILAV.value = opened.dati.codCpiLav;
    document.Frm1.CDNLAVORATORE.value = opened.dati.cdnLavoratore;
    document.Frm1.strCodiceFiscaleLav.value = opened.dati.codiceFiscaleLavoratore;
    document.Frm1.strNomeLav.value = opened.dati.nome;
    document.Frm1.strCognomeLav.value = opened.dati.cognome;
    document.Frm1.strNomeCognomeLav.value = opened.dati.cognome + " " + opened.dati.nome;
    document.Frm1.datNascLav.value = opened.dati.datNasc;
      
    document.Frm1.FLGCFOK.value = opened.dati.FLGCFOK;
    if ( document.Frm1.FLGCFOK.value == "S" ){ 
                document.Frm1.FLGCFOK.value = "Si";
    }else 
          if ( document.Frm1.FLGCFOK.value != "" ){
            document.Frm1.FLGCFOK.value = "No";
          }
    
    if (opened.dati.flgCambiamentiDati != null && document.Frm1.flgCambiamentiDati != null) {
      document.Frm1.flgCambiamentiDati.value = document.Frm1.flgCambiamentiDati.value + opened.dati.flgCambiamentiDati;
    }
      
    opened.close();

    if (document.getElementById("AggiungiLavoratoreMovimento") != null) {
      if (document.getElementById("AggiungiLavoratoreMovimento").style.display == 'inline') {
        document.getElementById("AggiungiLavoratoreMovimento").style.display = "none";
      }
    }
    
    if (inserisci == 'true' && !precedente) {
      document.Frm1.CODTIPOMOV.value = '';
    }
    if (contesto == 'valida') {
    	aggiornaDettaglioGenerale();
    }
    //Gestione della combo del codTipoMov
  	resetCodTipoMov();
  	// si controllano i dati del lavoratore
  	apriControlloLavoratore(document.Frm1.CDNLAVORATORE.value);
  }
  
	