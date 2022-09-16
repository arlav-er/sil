<SCRIPT TYPE="text/javascript">
  function aggiornaDataEntroIl() {
    var v = document.frmContatto.FLGRICONTATTARE.value;
    if(v!="S") { 
      document.frmContatto.DATENTROIL.value = "";
      document.frmContatto.DATENTROIL.disabled = true;
    }
    else {
      document.frmContatto.DATENTROIL.disabled = false;
    }   
  }


  function controllaCampi() {
    var v = document.frmContatto.FLGRICONTATTARE.value;
    if(v=="S" && document.frmContatto.DATENTROIL.value =="") {
      alert ("Inserire data entro il");
      return false;
    }

    annoContatto = document.frmContatto.DATCONTATTO.value.substr(6,4);
    meseContatto = document.frmContatto.DATCONTATTO.value.substr(3,2);
    giornoContatto = document.frmContatto.DATCONTATTO.value.substr(0,2);
    var dataContatto = parseInt(annoContatto + meseContatto + giornoContatto, 10);
    var dataRicontattare = 0;
  
    if(document.frmContatto.DATENTROIL.value !="") {
      annoRicontattare = document.frmContatto.DATENTROIL.value.substr(6,4);
      meseRicontattare = document.frmContatto.DATENTROIL.value.substr(3,2);
      giornoRicontattare = document.frmContatto.DATENTROIL.value.substr(0,2);
      dataRicontattare = parseInt(annoRicontattare + meseRicontattare + giornoRicontattare, 10);
      if (dataContatto > dataRicontattare) {
        alert ("Data entro il deve essere maggiore o uguale della data contatto");
        return false;
      }
    }
    // Savino 13/03/2006: in verifiche-soggetti pronti all'incrocio il parametro data scadenza
    //     viene utilizzato per la descrizione cpi, col rischio di avere caratteri non accettati da javascript (Es.: reggio nell'emilia) 
    var dataScadenza = new String('<%=StringUtils.formatValue4Javascript(datascadenza)%>');
    var dataUltimoContatto = new String('<%=dataUltimoContatto%>');
    
    if (dataScadenza != "") {
      annoScadenza = dataScadenza.substr(6,4);
      meseScadenza = dataScadenza.substr(3,2);
      giornoScadenza = dataScadenza.substr(0,2);
    } else {
        annoScadenza = "";
        meseScadenza = "";
        giornoScadenza = "";
      }
  
    if (dataUltimoContatto != "") {
      annoUltimoContatto = dataUltimoContatto.substr(6,4);
      meseUltimoContatto = dataUltimoContatto.substr(3,2);
      giornoUltimoContatto = dataUltimoContatto.substr(0,2);
    } else {
          annoUltimoContatto = "";
          meseUltimoContatto = "";
          giornoUltimoContatto = "";
      }

    dataScadenza = parseInt(annoScadenza + meseScadenza + giornoScadenza, 10);
    dataUltimoContatto = parseInt(annoUltimoContatto + meseUltimoContatto + giornoUltimoContatto, 10);

    if (dataContatto > dataScadenza && '<%=strTipoScadenziario%>' != 'VER1' && '<%=strTipoScadenziario%>' != 'VER2') {
      alert ("Data nuovo contatto maggiore della data scadenza");
      if (confirm('Salvare comunque il nuovo contatto?')) {
        return true;
      }
      else {
        return false;
      }
    }
    else {
      if (dataContatto <= dataUltimoContatto) {
        alert ("Data nuovo contatto deve essere maggiore della data ultimo contatto");
        if (confirm('Salvare comunque il nuovo contatto?')) {
          return true;
        }
        else {
          return false;  
        }
      }
      else {
        return(true);
      }  
    }
  }
  
  function Annulla() {
	// Se la pagina è già in submit, ignoro questo nuovo invio!
	if (isInSubmit()) return;

    document.frmContatto.PAGE.value='ScadContattoPage';
    doFormSubmit(document.frmContatto);
  }

    function conferma(azione,strPage){
		// Se la pagina è già in submit, ignoro questo nuovo invio!
		if (isInSubmit()) return;
		
		if (azione=="BACK"){
          document.frmContatto.PAGE.value=strPage;
          if (strPage == "MatchEsitoPage") {
			document.frmContatto.CDNLAVORATORE.value = document.frmContatto._CDNLAVORATORE_.value;
		  	document.frmContatto.PRGAZIENDA.value = document.frmContatto._PRGAZIENDA_.value;
		  	document.frmContatto.PRGUNITA.value = document.frmContatto._PRGUNITA_.value; 
		  	document.frmContatto.MESSAGE.disabled = false;
			document.frmContatto.LIST_PAGE.disabled = false;
			document.frmContatto.MESSAGE.value = document.frmContatto.MESSAGE_ROSA.value;
			document.frmContatto.LIST_PAGE.value = document.frmContatto.LIST_PAGE_ROSA.value;
			document.frmContatto.MESSAGE_ROSA.disabled = true;
			document.frmContatto.LIST_PAGE_ROSA.disabled = true;
			document.frmContatto.MESSAGE_SCAD.disabled = true;
			document.frmContatto.LIST_PAGE_SCAD.disabled = true;     		      		
		  } // MatchEsitoPage 
		  else {
			  if (strPage == "DispInsRosaPage") {
				  document.frmContatto.MESSAGE.disabled = false;
				  document.frmContatto.LIST_PAGE.disabled = false;
				  document.frmContatto.MESSAGE.value = document.frmContatto.MESSAGE_ROSA.value;
				  document.frmContatto.LIST_PAGE.value = document.frmContatto.LIST_PAGE_ROSA.value;
				  document.frmContatto.MESSAGE_ROSA.disabled = true;
				  document.frmContatto.LIST_PAGE_ROSA.disabled = true;
				  document.frmContatto.MESSAGE_SCAD.disabled = true;
				  document.frmContatto.LIST_PAGE_SCAD.disabled = true;
			  } // DispInRosaPage
			  else {
				  document.frmContatto.MESSAGE.disabled = false;
				  document.frmContatto.LIST_PAGE.disabled = false;
				  document.frmContatto.MESSAGE.value = document.frmContatto.MESSAGE_SCAD.value;
				  document.frmContatto.LIST_PAGE.value = document.frmContatto.LIST_PAGE_SCAD.value;
				  document.frmContatto.MESSAGE_ROSA.disabled = true;
				  document.frmContatto.LIST_PAGE_ROSA.disabled = true;
				  document.frmContatto.MESSAGE_SCAD.disabled = true;
				  document.frmContatto.LIST_PAGE_SCAD.disabled = true;
			  } // else
    	 } // else
       }
       doFormSubmit(document.frmContatto);
     }
  function DettaglioContatto (prgContatto) {
	// Se la pagina è già in submit, ignoro questo nuovo invio!
	if (isInSubmit()) return;

    document.frmContatto.PRGCONTATTO.value = prgContatto;
    document.frmContatto.PAGE.value = "DettaglioContattoPage";
    doFormSubmit(document.frmContatto);
  }
  
</SCRIPT>
