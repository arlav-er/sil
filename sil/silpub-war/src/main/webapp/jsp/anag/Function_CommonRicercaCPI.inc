<SCRIPT TYPE="text/javascript">

  function PulisciRicercaCPI(codcpi, codcpihid, nomecpi, nomecpihid, strTipo) {
    if (strTipo == 'descrizione') {
      if (nomecpi.value != nomecpihid.value) {
        nomecpihid.value=(nomecpi.value).toUpperCase();
        codcpihid.value="";
        codcpi.value="";
      }
    }
    else {
      if (codcpi.value != codcpihid.value) {
        codcpihid.value=(codcpi.value).toUpperCase();
        nomecpi.value="";
        nomecpihid.value="";
      }
    }
  }

  
  function btFindCPI_onclick(codcpi, codcpihid, nomecpi, nomecpihid, tipoRicerca) {
    var s= "AdapterHTTP?PAGE=RicercaCPIPage";
    if (tipoRicerca == 'descrizione') {
      if (nomecpi.value == ""){
        nomecpihid.value = "";
        codcpihid.value = "";
        codcpi.value = "";
      }
      else {
        s += "&strdenominazione=" + nomecpi.value.toUpperCase();
        s += "&retcod="+codcpi.name;
        s +="&retnome="+nomecpi.name;
        s +="&tipoRicerca="+tipoRicerca;
        window.open(s,"CPI", 'toolbar=0, scrollbars=1');
       }
    }
    else {
      if (tipoRicerca == 'codice') {
        if (codcpi.value == ""){
          nomecpihid.value = "";
          codcpihid.value = "";
          nomecpi.value = "";
        }
        else {
          s += "&codcpi=" + codcpi.value.toUpperCase();
          s += "&retcod="+codcpi.name;
          s +="&retnome="+nomecpi.name;
          s +="&tipoRicerca="+tipoRicerca;
          window.open(s,"CPI", 'toolbar=0, scrollbars=1');
        }
      }  
    }  
  }

</SCRIPT>