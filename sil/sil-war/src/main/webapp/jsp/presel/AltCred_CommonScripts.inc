<SCRIPT TYPE="text/javascript">

  function SelezionaDettaglio_onClick() {	

    codTipoInfo= MainForm.CODTIPOINFO;

    if (codTipoInfo.value == "")
      return;

    window.open("AdapterHTTP?PAGE=ConoscenzeInfoPage&MODULE=M_ListDettagliConoscenzaInfo&CODTIPOINFO="+codTipoInfo.value, "Tipi", 'toolbar=0, scrollbars=1');

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

    var url = "AdapterHTTP?PAGE=AltCredPage&CDNLAVORATORE=<%= cdnLavoratore %>&CDNFUNZIONE=<%= _funzione %>";
    setWindowLocation(url);
  }

</SCRIPT>
