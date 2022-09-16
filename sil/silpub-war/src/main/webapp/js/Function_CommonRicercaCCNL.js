  function PulisciRicercaCCNL(codCCNL, codCCNLhid, nomeCCNL, nomeCCNLhid, strTipo) {
    if (strTipo == 'descrizione') {
      if (nomeCCNL.value != nomeCCNLhid.value) {
        nomeCCNL.value=(nomeCCNL.value).toUpperCase();
        codCCNLhid.value="";
        codCCNL.value="";
      }
    }
    else {
     if (codCCNL.value != codCCNLhid.value) {
        codCCNL.value=(codCCNL.value).toUpperCase();
        nomeCCNL.value="";
        nomeCCNLhid.value="";
      }
    }
  }

  
  function btFindCCNL_onclick(codCCNL, codCCNLhid, nomeCCNL, nomeCCNLhid, tipoRicerca) {
    var s= "AdapterHTTP?PAGE=RicercaCCNLPage";
    if (tipoRicerca == 'descrizione') {
      if (nomeCCNL.value == ""){
        nomeCCNLhid.value = "";
        codCCNLhid.value = "";
        codCCNL.value = "";
      }
      else {
        s += "&strdenominazione=" + nomeCCNL.value.toUpperCase();
        s += "&retcod="+codCCNL.name;
        s +="&retnome="+nomeCCNL.name;
        s +="&tipoRicerca="+tipoRicerca;
        window.open(s,"CCNL", 'toolbar=0, scrollbars=1');
       }
    }
    else {
      if (tipoRicerca == 'codice') {
        if (codCCNL.value == ""){
          nomeCCNLhid.value = "";
          codCCNLhid.value = "";
          nomeCCNL.value = "";
        }
        else if (codCCNLhid.value!=codCCNL.value) {
          s += "&codccnl=" + codCCNL.value.toUpperCase();
          s += "&retcod="+codCCNL.name;
          s +="&retnome="+nomeCCNL.name;
          s +="&tipoRicerca="+tipoRicerca;
          window.open(s,"CCNL", 'toolbar=0, scrollbars=1');
        }
      }  
    }  
  }
