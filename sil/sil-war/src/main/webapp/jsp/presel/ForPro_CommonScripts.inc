<SCRIPT TYPE="text/javascript">
  isFirstTime = true;
  
  function DisabilitaCampi_onLoad() {	
    AbilitaStage_onClick();
    AbilitaCessazione_onClick();
  }

  function AbilitaStage_onClick() {	
    if (document.Frm1.FLGSTAGE.value=="S") {
      document.Frm1.STRAZIENDA.disabled=false;
      document.getElementById("str_azienda").style.display="";

      document.Frm1.strComAzienda.disabled=false;
      document.getElementById("str_com_azienda").style.display="";

      document.Frm1.NUMORESTAGE.disabled=false;

      document.Frm1.STRLOCALITAAZIENDA.disabled=false;
      document.getElementById("str_localita").style.display="";

      document.Frm1.STRINDIRIZZOAZIENDA.disabled=false;
      document.getElementById("str_indirizzo").style.display="";
    }else{
      //document.Frm1.STRAZIENDA.value="";
      document.Frm1.STRAZIENDA.disabled=true;
      document.getElementById("str_azienda").style.display="none";

      //document.Frm1.strComAzienda.value="";
      document.Frm1.strComAzienda.disabled=true;
      document.getElementById("str_com_azienda").style.display="none";

      //document.Frm1.NUMORESTAGE.value="";      
      document.Frm1.NUMORESTAGE.disabled=true;

      //document.Frm1.STRLOCALITAAZIENDA.value="";
      document.Frm1.STRLOCALITAAZIENDA.disabled=true;
      document.getElementById("str_localita").style.display="none";

      //document.Frm1.STRINDIRIZZOAZIENDA.value="";
      document.Frm1.STRINDIRIZZOAZIENDA.disabled=true;
      document.getElementById("str_indirizzo").style.display="none";
      
      //document.Frm1.CODCOMAZIENDA.value="";
      //document.Frm1.CODCOMAZIENDAHid.value="";
    }
    
  }
  
  function CorsoConStage_onClick() {
    
    AbilitaStage_onClick();
    fieldChanged();
  }

  function AbilitaCessazione_onClick() {	
    if (document.Frm1.FLGCOMPLETATO.value=="N"){
      document.Frm1.STRMOTCESSAZIONE.disabled=false;
      document.getElementById("mot_ces").style.display="";              
    }else{
      //document.Frm1.STRMOTCESSAZIONE.value="";
      document.Frm1.STRMOTCESSAZIONE.disabled=true;  
      document.getElementById("mot_ces").style.display="none";        
    }
  }

  function SelezionaDettaglio_onClick() {	

    codCorso= document.Frm1.CODCORSO;
    descCorso= document.Frm1.DESCCORSO;
    numAnno = document.Frm1.NUMANNO;

    window.open("AdapterHTTP?PAGE=ForProPage&MODULE=M_ListForProCorso&CODCORSO="+codCorso.value+"&DESCCORSO="+descCorso.value+"&NUMANNO="+numAnno.value,'','toolbar=0,scrollbars=1');
  }

  function Tipi_onChange() {

    // Cambiando tipo il dettaglio non corrisponde più
    // quindi azzero la scelta fatta
    document.Frm1.CODCORSO.value= "";
    document.Frm1.DESCCORSO.value= "";
  }

  function setisFirstTime(ft){
    isFirstTime = ft;
  }

  function SaveCorsoHidden(){    
    document.Frm1.descCorsoHid.value = Frm1.DESCCORSO.value;
    document.Frm1.numAnnoHid.value = Frm1.NUMANNO.value;
  }

  function PulisciRricercaCorso(campo){    
    if(document.Frm1.CODCORSO.value == "") {
      return;
    }
    else {
      //setisFirstTime(false);
      if(campo == 'corso'){
        if (document.Frm1.DESCCORSO.value != Frm1.descCorsoHid.value) {
          document.Frm1.CODCORSO.value= "";
          document.Frm1.NUMANNO.value= "";
        }
      }
      else if(campo == 'anno'){
        if (document.Frm1.NUMANNO.value != Frm1.numAnnoHid.value) {
          document.Frm1.CODCORSO.value= "";
          document.Frm1.DESCCORSO.value= "";
        }
      }
    }
  }

  function GoToMainPage() {
	// Se la pagina è già in submit, ignoro questo nuovo invio!
	if (isInSubmit()) return;

    var url = "AdapterHTTP?PAGE=ForProPage&CDNLAVORATORE=<%= cdnLavoratore %>&CDNFUNZIONE=<%= _funzione %>";
    setWindowLocation(url);
  }

  // Utilizzabile in caso di inserimento e aggiornamento record,
  // verifica la correttezza dei campi presenti.
  function Salva_onClick() {
	// Se la pagina è già in submit, ignoro questo nuovo invio!
	if (isInSubmit()) return;

    if ( (document.forms[0].CODCORSO.value.length == 0) && 
         (document.forms[0].STRDESCRIZIONE.value.length == 0)    
    ) {

      alert("Prima è necessario specificare il Corso oppure la Descrizione");
      return;
    }
    
    // -- NOTE: Gestione Patto
    if ( !controllaPatto() )
      return;
    //--

    doFormSubmit(document.forms[0]);
  }

  // Relativo al combobox "Completato"
  function FlgCompletato_fieldChanged() {
  
    fieldChanged();
    AbilitaCessazione_onClick();
  }

  function ricercaAvanzataCorso_onClick() {	

	    window.open("AdapterHTTP?PAGE=RicercaCorsiAvanzataPage&FORMPRO=1",'','toolbar=0,scrollbars=1');
   }
  
</SCRIPT>
