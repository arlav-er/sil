/*
* Chiamata quando viene l' operatore decide di forzare una operazione di inserimento di un movimento
*/
  function ripetiInserimento(valore, codStatoAtto, valFlagMsgStatoOccMan) {
  	document.Frm1.FORZA_INSERIMENTO.value = "true";
  	if (document.Frm1.CONTINUA_CALCOLO_SOCC != null) {
  		if (valFlagMsgStatoOccMan != "") {
  			document.Frm1.CONTINUA_CALCOLO_SOCC.value = valFlagMsgStatoOccMan;
  		}
  		else {
  			document.Frm1.CONTINUA_CALCOLO_SOCC.value = "true";
  		}
  	}
  	if((valore!=null) && (valore!="") && (valore!=undefined) && (codStatoAtto!=null) && 
       (codStatoAtto!="") && (codStatoAtto!=undefined)){
      document.Frm1.ACTION.value='aggiorna';//Utilizzato per il salva nella pagina del dettaglio generale
      document.Frm1.CODSTATOATTO.value = codStatoAtto;
      document.Frm1.permettiImpatti.value = 'true';
    }
  	selezionaComboAgevolazioni();
  	doFormSubmit(document.Frm1);
  }
  
  function ripetiInserimentoDaAnnullamento(valore, codStatoAtto, codMotAnnullamento, valFlagMsgStatoOccMan) {
  	document.Frm1.FORZA_INSERIMENTO.value = "true";
  	if (document.Frm1.CONTINUA_CALCOLO_SOCC != null) {
  		if (valFlagMsgStatoOccMan != "") {
  			document.Frm1.CONTINUA_CALCOLO_SOCC.value = valFlagMsgStatoOccMan;
  		}
  		else {
  			document.Frm1.CONTINUA_CALCOLO_SOCC.value = "true";
  		}
  	}
  	if((valore!=null) && (valore!="") && (valore!=undefined) && 
  	   (codStatoAtto!=null) && (codStatoAtto!="") && (codStatoAtto!=undefined) && 
       (codMotAnnullamento!=null) && (codMotAnnullamento!="") && (codMotAnnullamento!=undefined)){
      document.Frm1.ACTION.value='aggiorna';//Utilizzato per il salva nella pagina del dettaglio generale
      document.Frm1.CODSTATOATTO.value = codStatoAtto;
      document.Frm1.CODMOTANNULLAMENTO.value = codMotAnnullamento;
      document.Frm1.permettiImpatti.value = 'true';
    }
  	selezionaComboAgevolazioni();
  	doFormSubmit(document.Frm1);
  }
  
  function continuaRicalcolo(valore) {
  	document.Frm1.FORZA_INSERIMENTO.value = valore;
  	if (document.Frm1.CONTINUA_CALCOLO_SOCC != null)
  		document.Frm1.CONTINUA_CALCOLO_SOCC.value = "true";
  	selezionaComboAgevolazioni();
  	doFormSubmit(document.Frm1);
  }
  
  function forzaEtaApprendistato(valore) {
  	document.Frm1.FORZA_INSERIMENTO_ETA_APPRENDISTATO.value = valore;
  	selezionaComboAgevolazioni();
  	doFormSubmit(document.Frm1);
  }
  
  function resetFlagForzatura() {
  	document.Frm1.FORZA_INSERIMENTO_ETA_APPRENDISTATO.value = "false";
  }