<SCRIPT TYPE="text/javascript">

  function PulisciRicerca(codcomune, codcomunehid, nomecomune, nomecomunehid, cap, caphid, strTipo) {
    if (strTipo == 'descrizione') {
      if (nomecomune.value != nomecomunehid.value) {
        nomecomunehid.value=(nomecomune.value).toUpperCase();
        codcomunehid.value="";
        codcomune.value="";
        if(cap != null) {
          cap.value="";
          caphid.value="";
        }
      }
    }
    else if(strTipo == 'codice'){
      if (codcomune.value != codcomunehid.value) {
        codcomunehid.value=(codcomune.value).toUpperCase();
        nomecomune.value="";
        nomecomunehid.value="";
        if(cap != null) {
          cap.value="";
          caphid.value="";
        }
      }
    }
    else if(strTipo == 'cap'){
      if (cap.value != caphid.value) {
        caphid.value=(cap.value).toUpperCase();
        nomecomune.value="";
        nomecomunehid.value="";
        codcomunehid.value="";
        codcomune.value="";
      }
    }
  }

  //btFindComuneCAP_onclick  implementata al posto di btFindComune_onclick per visualizzare anche il CAP solo dove è richiesto
  function btFindComuneCAP_onclick(codcomune, codcomunehid, retcod, nomecomune, nomecomunehid, retnome, retcap, retcaphid, tipoRicerca) {
    var s= "AdapterHTTP?PAGE=RicercaComunePage";
    if (tipoRicerca == 'descrizione') {
      if (nomecomune.value == ""){
        nomecomunehid.value = "";
        codcomunehid.value = "";
        codcomune.value = "";
        if(retcap != null) {
          retcap.value="";
          retcaphid.value="";
        }
      }
      else {
        s += "&strdenominazione=" + nomecomune.value.toUpperCase();
        s += "&retcod="+codcomune.name;
        s +="&retnome="+nomecomune.name;
        if (retcap!=null){
        s +="&retcap="+retcap.name;
        }
        s +="&tipoRicerca="+tipoRicerca;
        window.open(s,"Comuni", 'menubar=1, toolbar=0, scrollbars=1');
       }
    }
    else {
      if (tipoRicerca == 'codice') {
        if (codcomune.value == ""){
          nomecomunehid.value = "";
          codcomunehid.value = "";
          nomecomune.value = "";
          if(retcap != null) {
            retcap.value="";
            retcaphid.value="";
          }
        }
        else {
          s += "&codcom=" + codcomune.value.toUpperCase();
          s += "&retcod="+codcomune.name;
          s +="&retnome="+nomecomune.name;
          if (retcap!=null){
            s +="&retcap="+retcap.name;
          }
          s +="&tipoRicerca="+tipoRicerca;
          window.open(s,"Comuni", 'menubar=1, toolbar=0, scrollbars=1');
        }
      }  
    }  
  }

var campiComuneOK = false;
function campiComuneCompletati(trueORfalse)
{ campiComuneOK = trueORfalse;
  //alert("campi_ComuneOK="+campiComuneOK);
}


</SCRIPT>