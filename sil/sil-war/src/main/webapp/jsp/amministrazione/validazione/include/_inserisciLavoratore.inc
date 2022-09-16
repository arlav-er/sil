    <SCRIPT TYPE="text/javascript">
    
    function apriInserisciLavoratore() {
	    var f = "AdapterHTTP?PAGE=ANAGDETTAGLIOPAGEANAGINS&prgMobilitaIscrApp=" + prgMobilitaApp + "&AGG_FUNZ=aggiornaLavoratore&CDNFUNZIONE=1&PROVENIENZA=MOVIMENTI&VALIDAMOBILITA=S";
	    var t = "_blank";
	    var feat = "toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,width=750,height=600,top=0,left=100";
		opened = window.open(f, t, feat);
	}
	
	//Aggiorna la pagina salvando i dati contenuti
	function aggiornaDettaglioGeneraleLavoratore() {
		document.Frm1.PAGE.value = "ValidazioneMobilitaGeneralePage";
		doFormSubmit(document.Frm1);
	}
	
	function aggiornaLavoratore() {
		document.Frm1.codCpiLav.value = opened.dati.codCpiLav;
	    document.Frm1.CDNLAVORATORE.value = opened.dati.cdnLavoratore;
	    document.Frm1.strCodiceFiscaleLav.value = opened.dati.codiceFiscaleLavoratore;
	    document.Frm1.strNomeLav.value = opened.dati.nome;
	    document.Frm1.strCognomeLav.value = opened.dati.cognome;
	    document.Frm1.strNomeCognomeLav.value = opened.dati.cognome + " " + opened.dati.nome;
	    document.Frm1.datNascLav.value = opened.dati.datNasc;
	      
	    document.Frm1.FLGCFOK.value = opened.dati.FLGCFOK;
	    if ( document.Frm1.FLGCFOK.value == "S" ) { 
	    	document.Frm1.FLGCFOK.value = "Si";
	    }
	    else {
	    	if ( document.Frm1.FLGCFOK.value != "" ){
	        	document.Frm1.FLGCFOK.value = "No";
	       	}
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
	    aggiornaDettaglioGeneraleLavoratore();
	}
    
       
    </SCRIPT>
    <div id="AggiungiLavoratoreMovimento" style="display:inline;">
  		<a href="#" onClick="javascript:apriInserisciLavoratore();"><img src="../../img/add2.gif" alt="nuovo lavoratore"></a>
  	</div>
