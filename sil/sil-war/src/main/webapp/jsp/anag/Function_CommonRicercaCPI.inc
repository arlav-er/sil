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

  function PulisciRicercaCPIdaTrasf(codcpi, codcpihid, nomecpi, nomecpihid, strTipo, messaggioIR, msgXIR, Coop, imgCoop) {
  	if (msgXIR.value != "") {
  		messaggioIR.value = msgXIR.value;
  		msgXIR.value = "";
  	}
  	Coop.value = "Invio cartaceo";
    imgCoop.src = "../../img/text.gif";
  	PulisciRicercaCPI(codcpi, codcpihid, nomecpi, nomecpihid, strTipo);
  }
  
  function btFindCPI_onclick(codcpi, codcpihid, nomecpi, nomecpihid, tipoRicerca) {
    var s= "AdapterHTTP?PAGE=RicercaCPIPage";
    if (tipoRicerca == 'descrizioneDATRASF') {
      s +="&provenienza=trasferimento";
      tipoRicerca= 'descrizione';
    }
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
      if (tipoRicerca == 'codiceDATRASF') {
      	s +="&provenienza=trasferimento";
      	tipoRicerca= 'codice';
      }
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