<SCRIPT TYPE="text/javascript">

  function SelezionaDettaglio_onClick() {	

    codTipoInfo= Frm1.CODTIPOINFO;
    flgFrequente=Frm1.flgFrequente;

    if (codTipoInfo.value == "")
      return;
    paramFrequente=(flgFrequente.checked)?"&flgFrequente=true":"";
    window.open("AdapterHTTP?PAGE=ConoscenzeInfoPage&MODULE=M_ListDettagliConoscenzaInfo&CODTIPOINFO="+codTipoInfo.value+paramFrequente);

    fieldChanged();
  }

  function Tipi_onChange() {

    // Cambiando tipo il dettaglio non corrisponde più
    // quindi azzero la scelta fatta
    Frm1.CODMANSIONE.value= "";
    Frm1.DESCMANSIONE.value= "";
  }

  function GoToMainPage() {
	// Se la pagina è già in submit, ignoro questo nuovo invio!
	if (isInSubmit()) return;

    var url = "AdapterHTTP?PAGE=MansioniPage&CDNLAVORATORE=<%= cdnLavoratore %>&CDNFUNZIONE=<%= _funzione %>";
    setWindowLocation(url);
  }


function selectMansione_onClick(codMansione, codMansioneHid, descMansione, strTipoMansione) {	

    flgFrequente=Frm1.flgFrequente;
    paramFrequente=(flgFrequente.checked)?"&flgFrequente=true":"";
    if (codMansione.value==""){

      descMansione.value="";
      strTipoMansione.value="";      
    }
    else if (codMansione.value!=codMansioneHid.value){

    window.open("AdapterHTTP?PAGE=RicercaMansionePage&FLGIDO=S&codMansione="+codMansione.value+paramFrequente, "Mansioni", 'toolbar=0, scrollbars=1, height=600,width=800');     
    }
  }

function ricercaAvanzataMansioni() {
  window.open("AdapterHTTP?PAGE=RicercaMansioneAvanzataPage&FLGIDO=S", "Mansioni", 'toolbar=0, scrollbars=1 ,height=600,width=800');
}

function ricercaAvanzataMansioniConCodice(codMansioneItem, descMansioneItem, tipoMansioneItem) {
  var urlPage = "AdapterHTTP?PAGE=RicercaMansioneAvanzataConCodicePage&FLGIDO=S";
  if (arguments.length>0)  {    
    urlPage +="&_codMansioneItem="+codMansioneItem;
    urlPage +="&_descMansioneItem="+descMansioneItem;
    urlPage +="&_tipoMansioneItem="+tipoMansioneItem;    
  }
  window.open(urlPage, "Mansioni", 'toolbar=0, scrollbars=1,height=600,width=800');
}

function selectMansioneGenerica_onClick(indiceField, flgFrequente, codMansione, codMansioneHid, descMansione, strTipoMansione) {
    paramFrequente=(flgFrequente.checked)?"&flgFrequente=true":"";
    if (codMansione.value==""){

      descMansione.value="";
      strTipoMansione.value="";      
    }
    else if (codMansione.value!=codMansioneHid.value){

    window.open("AdapterHTTP?PAGE=RicercaMansionePage&FLGIDO=S&indiceMansione="+indiceField+"&codMansione="+codMansione.value+paramFrequente, "Mansioni", 'toolbar=0, scrollbars=1, height=600,width=800');     
    }
  }
  
 function ricercaAvanzataMansioniGenerica(indiceField) {
  window.open("AdapterHTTP?PAGE=RicercaMansioneAvanzataPage&FLGIDO=S&indiceMansione="+indiceField, "Mansioni", 'toolbar=0, scrollbars=1 ,height=600,width=800');
}


</SCRIPT>
