  function apriInserisciLavoratore() {
  	var f = "AdapterHTTP?PAGE=ANAGDETTAGLIOPAGEANAGINS&PRGMOVIMENTOAPP=" + prgMovApp + "&AGG_FUNZ=aggLav&CDNFUNZIONE=1&PROVENIENZA=MOVIMENTI&VALIDAMOV=S";
    var t = "_blank";
    var feat = "toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,width=750,height=600,top=0,left=100";
    opened = window.open(f, t, feat);
  }

  //funzione per la selezione del lavoratore con pulsante di lookup
  function aggLav() {
  	document.Frm1.CODCPILAV.value = opened.dati.codCpiLav;
    document.Frm1.CDNLAVORATORE.value = opened.dati.cdnLavoratore;
    document.Frm1.strCodiceFiscaleLav.value = opened.dati.codiceFiscaleLavoratore;
    document.Frm1.strNomeLav.value = opened.dati.nome;
    document.Frm1.strCognomeLav.value = opened.dati.cognome;
    document.Frm1.strNomeCognomeLav.value = opened.dati.cognome + " " + opened.dati.nome;
    document.Frm1.datNascLav.value = opened.dati.datNasc;
    document.Frm1.descrStatoOcc.value = opened.dati.descrStatoOcc;
    document.Frm1.datInizioOcc.value = opened.dati.datInizioOcc;
    document.Frm1.datAnzOcc.value = opened.dati.datAnzOcc;
   	
   	if (opened.dati.flgCambiamentiDati != null && document.Frm1.flgCambiamentiDati != null) {
      document.Frm1.flgCambiamentiDati.value = document.Frm1.flgCambiamentiDati.value + opened.dati.flgCambiamentiDati;
    }
      
    opened.close();

    if (document.getElementById("AggiungiLavoratoreMovimento") != null) {
      if (document.getElementById("AggiungiLavoratoreMovimento").style.display == '') {
        document.getElementById("AggiungiLavoratoreMovimento").style.display = "none";
      }
    }
  
   //if (document.getElementById("ricercaLavoratore") != null) {
   //   if (document.getElementById("ricercaLavoratore").style.display == '') {
   //     document.getElementById("ricercaLavoratore").style.display = "none";
   //   }
   // }
   	
   	if (document.getElementById("datiLav") != null) {
    	if (document.getElementById("datiLav").style.display == '') {
    		document.getElementById("datiLavoratore").style.display = "";
        	document.getElementById("datiLav").style.display = "none";
        	tendinaLavoratore.src=imgChiusa;
      	}
    }
    
    
    if (inserisci == 'true' && !precedente) {
    	document.Frm1.CODTIPOMOV.value = '';
    }
    
    //Gestione della combo del codTipoMov
    if (contesto != 'valida') {
    	resetCodTipoMov();
    }
    
    if (contesto == 'valida') {
    	gestisciPrecedente();
    }
    
    // si controllano i dati del lavoratore
  	apriControlloLavoratore(document.Frm1.CDNLAVORATORE.value);
  }
  
	