    <SCRIPT TYPE="text/javascript">
    
    //Se ho modifiche nei campi della posizione Inps le riporta nel campo nascosto
	function unificaPosInps() {
	  document.Frm1.STRPOSINPS.value = document.Frm1.STRPOSINPS1.value + document.Frm1.STRPOSINPS2.value;
	}
	
	//Se ho modifiche nei campi della patInail le riporta nel campo nascosto
	function unificaPatInail() {
	  document.Frm1.STRPATINAIL.value = document.Frm1.STRPATINAIL1.value + document.Frm1.STRPATINAIL2.value;
	}
	
	function visualizzaCampi(elem,stato){
	  var d = document.getElementById(elem);
	  d.style.display = stato;
	}
    
    //Apre la pop-up di inserimento della testata aziendale
	function apriInserisciAzienda() {
	  var f = "AdapterHTTP?PAGE=MovimentiTestataAziendaPage&CONTESTO=AZIENDA" +
	  	"&FUNZ_AGGIORNAMENTO=aggiornaDettaglioGeneraleAzienda&prgMobilitaIscrApp=" + prgMobilitaApp;
	  var t = "_blank";
	  var feat = "toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,width=800,height=600,top=0,left=100";
	  var openedTestataAzienda = window.open(f, t, feat);
	}
    
    //apre la pop-up dell'unita aziendale trovata
    function apriUnitaAziendale(prgAz,prgUn,cdnFunz) {
        var f = "AdapterHTTP?PAGE=IdoUnitaAziendaPage&PRGAZIENDA=" + prgAz + "&PRGUNITA=" + prgUn + "&CDNFUNZIONE=" + cdnFunz;
        var t = "_blank";
        var feat = "toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,width=800,height=600,top=75,left=100";
        opened = window.open(f, t, feat);
        window.openPopUpAzienda = true;
    }
    
    //apre la pop-up di scelta dell'unita aziendale tra quelle trovate
	function apriScegliUnitaAzienda(prgAzienda, cdnFunzione) {
	  var f = "AdapterHTTP?PAGE=ListaUnitaAziendaPage&CONTESTO=AZIENDA&PRGAZ=" + prgAzienda + 
	  "&CDNFUNZIONE=" + cdnFunzione + "&prgMobilitaIscrApp=" + prgMobilitaApp +
	  "&FUNZ_AGGIORNAMENTO=aggiornaDettaglioGeneraleAzienda";
	  var t = "_blank";
	  var feat = "toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,width=800,height=600,top=0,left=100";
	  opened = window.open(f, t, feat);	
	}
	
	//apre la pop-up per l'aggiornamento dei dati dell'azienda
	function apriAggiornaAzienda(prgAzienda, prgUnita, cdnFunzione, prgMobilitaApp) {
	  var f = "AdapterHTTP?PAGE=AggiornaDatiAziendaPage&PRGAZIENDA=" + prgAzienda + "&PRGUNITA=" + prgUnita + 
	  "&CDNFUNZIONE=" + cdnFunzione + "&prgMobilitaIscrApp=" + prgMobilitaApp + "&CONTESTO=AZIENDA" + 
	  "&FUNZ_AGGIORNAMENTO=aggiornaDettaglioGeneraleAzienda";
	  var t = "_blank";
	  var feat = "toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,width=800,height=600,top=0,left=100";
	  opened = window.open(f, t, feat);
	}
	
	//Apre la pop-up di inserimento dell'unita aziendale
	function apriInserisciUnitaAzienda() {
	  var f = "AdapterHTTP?PAGE=MovimentiUnitaAziendaPage&CONTESTO=AZIENDA" + 
	  	"&PRGAZIENDA=" + document.Frm1.PRGAZIENDA.value + "&prgMobilitaIscrApp=" + prgMobilitaApp
	  	 + "&FUNZ_AGGIORNAMENTO=aggiornaDettaglioGeneraleAzienda";
	  var t = "_blank";
	  var feat = "toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,width=800,height=600,top=0,left=100";
	  opened = window.open(f, t, feat);
	}
	
	//Aggiorna la pagina salvando i dati contenuti
	function aggiornaDettaglioGeneraleAzienda() {
		document.Frm1.PAGE.value = "ValidazioneMobilitaGeneralePage";
		doFormSubmit(document.Frm1);
	}
       
    </SCRIPT>