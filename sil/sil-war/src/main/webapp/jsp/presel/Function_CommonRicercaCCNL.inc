<SCRIPT TYPE="text/javascript">

  function PulisciRicercaCCNL(codCCNL, codCCNLhid, nomeCCNL, nomeCCNLhid, strTipo) {
    if (strTipo == 'descrizione') {
      if (nomeCCNL.value != nomeCCNLhid.value) {
        nomeCCNLhid.value=(nomeCCNL.value).toUpperCase();
        codCCNLhid.value="";
        codCCNL.value="";
      }
    }
    else {
     if (codCCNL.value != codCCNLhid.value) {
        codCCNLhid.value=(codCCNL.value).toUpperCase();
        nomeCCNL.value="";
        nomeCCNLhid.value="";
      }
    }
  }

  
  function btFindCCNL_onclick(codCCNL, codCCNLhid, nomeCCNL, nomeCCNLhid, tipoRicerca) {
    var s= "AdapterHTTP?PAGE=RicercaCCNLPage";
    
    //inizio
    //Ricavo il codice di avviamento
    var tipoAvviamento = document.Frm1.codTipoAss;
    if(tipoAvviamento != undefined){
    	//Verifico che sia un avviamenti di tipo apprendistato
    	if(tipoAvviamento.value == "NO7" || tipoAvviamento.value == "NB7"){
    		//setto una variabile che permette l'esecuzione del modulo per il 
    		//calcolo del numeri dei mesi per l'apprendistato solo nel caso di tipo di avviamento NO7
			s += "&apprendistato=";    	
    	}
    }
    //fine
    
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
        else {
          s += "&codccnl=" + codCCNL.value.toUpperCase();
          s += "&retcod="+codCCNL.name;
          s +="&retnome="+nomeCCNL.name;
          s +="&tipoRicerca="+tipoRicerca;
          window.open(s,"CCNL", 'toolbar=0, scrollbars=1');
        }
      }  
    }  
  }

</SCRIPT>