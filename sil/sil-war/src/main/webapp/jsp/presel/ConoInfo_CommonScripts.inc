<SCRIPT TYPE="text/javascript">

  function SelezionaDettaglio_onClick() {	

    codTipoInfo= MainForm.CODTIPOINFO;

    if (codTipoInfo.value == "") {

      alert("Prima è necessario selezionare il tipo di conoscenza");
      return;
    }

    window.open("AdapterHTTP?PAGE=ConoscenzeInfoPage&MODULE=M_ListDettagliConoscenzaInfo&CODTIPOINFO="+codTipoInfo.value, "Tipi", 'toolbar=0, scrollbars=1');

    // NOTE: Selezionare una descrizione modifica il campo
    fieldChanged();
  }

  function Tipi_onChange() {

    // Cambiando tipo il dettaglio non corrisponde più
    // quindi azzero la scelta fatta
    MainForm.CODDETTINFO.value= "";
    MainForm.DESCDETTINFO.value= "";
  }

  function GoToMainPage() {
  	// Se la pagina è già in submit, ignoro questo nuovo invio!
	if (isInSubmit()) return;
  
    var url = "AdapterHTTP?PAGE=ConoscenzeInfoPage&CDNLAVORATORE=<%= cdnLavoratore %>&CDNFUNZIONE=<%= _funzione %>";
    setWindowLocation(url);
  }

  function Salva_onClick() {
	// Se la pagina è già in submit, ignoro questo nuovo invio!
	if (isInSubmit()) return;

    dettSelezionato = false;
    for (i=0; i<document.MainForm.CODICE.options.length; i++) {
        if( document.MainForm.CODICE.options[i].selected == true ) {
          dettSelezionato = true;
        }
    }
    if ( dettSelezionato == false ) {

      alert("Prima è necessario selezionare\nil dettaglio di un tipo di conoscenza");
      return;
    }

    doFormSubmit(document.forms[0]);
  }

</SCRIPT>
