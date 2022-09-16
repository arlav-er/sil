<SCRIPT TYPE="text/javascript">

  function SelezionaDettaglio_onClick() {	

    codTipoInfo= MainForm.CODTIPOINFO;

    if (codTipoInfo.value == "")
      return;

    window.open("AdapterHTTP?PAGE=ConoscenzeInfoPage&MODULE=M_ListDettagliConoscenzaInfo&CODTIPOINFO="+codTipoInfo.value, "Tipi", 'toolbar=0, scrollbars=1');

    fieldChanged();
  }




  function GoToMainPage() {

    var url = "AdapterHTTP?PAGE=ConoscenzeLingPage&CDNLAVORATORE=<%= cdnLavoratore %>&CDNFUNZIONE=<%= _funzione %>";
    setWindowLocation(url);
  }

</SCRIPT>
