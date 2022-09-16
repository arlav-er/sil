//Se ho modifiche nei campi della posizione Inps le riporta nel campo nascosto
function unificaPosInps() {
  document.Frm1.STRPOSINPS.value = document.Frm1.STRPOSINPS1.value + document.Frm1.STRPOSINPS2.value;
}

//Se ho modifiche nei campi della patInail le riporta nel campo nascosto
function unificaPatInail() {
  document.Frm1.STRPATINAIL.value = document.Frm1.STRPATINAIL1.value + document.Frm1.STRPATINAIL2.value;
}

function gestisciSezioneAzUtil(checked) {
  if (!checked) {
    document.getElementById("flagPersonaleInternoFIELD").value="N";
  }
  else {
    document.getElementById("flagPersonaleInternoFIELD").value="S";
  }
}

function visualizzaCampi(elem,stato){
  var d = document.getElementById(elem);
  d.style.display = stato;
}


function visualizzaInterinali(tipoAzienda, flgAssInterna) {
  if (tipoAzienda == codInterinale) {
    document.getElementById("CheckboxPersonaleInterno").style.display="inline";
    if (flgAssInterna) {
      document.getElementById("flagPersonaleInterno").checked = true;
    }
    else {
      document.getElementById("flagPersonaleInterno").checked = false;
    }
    if (precedente){ //se è interna e riguarda il movimento precedente
       document.getElementById("flagPersonaleInterno").disabled = true;
    }
    gestisciSezioneAzUtil(flgAssInterna);
  } 
  else {
    document.getElementById("CheckboxPersonaleInterno").style.display="none"; 
    document.getElementById("flagPersonaleInterno").checked = false;
    document.getElementById("flagPersonaleInternoFIELD").value="";
  }
  
  
  
}

//Apre la pop-up di inserimento della testata aziendale
function apriInserisciAzienda() {
  var f = "AdapterHTTP?PAGE=MovimentiTestataAziendaPage&CONTESTO=AZIENDA" + 
  	"&PRGMOVIMENTOAPP=" + document.Frm1.PRGMOVIMENTOAPP.value +   
  	"&FUNZ_AGGIORNAMENTO=aggiornaDettaglioGenerale";
  
  if (document.Frm1.CURRENTCONTEXT != null) {
  	f = f + "&context=" +  document.Frm1.CURRENTCONTEXT.value;
  }	
  
  var t = "_blank";
  var feat = "toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,width=800,height=600,top=0,left=100";
  openedTestataAzienda = window.open(f, t, feat);
}

//Apre la pop-up di inserimento dell'unita aziendale
function apriInserisciUnitaAzienda() {
  var f = "AdapterHTTP?PAGE=MovimentiUnitaAziendaPage&CONTESTO=AZIENDA" + 
  	"&PRGMOVIMENTOAPP=" + document.Frm1.PRGMOVIMENTOAPP.value + "&PRGAZIENDA=" + document.Frm1.PRGAZIENDA.value
  	 + "&FUNZ_AGGIORNAMENTO=aggiornaDettaglioGenerale";

  if (document.Frm1.CURRENTCONTEXT != null) {
  	f = f + "&context=" +  document.Frm1.CURRENTCONTEXT.value;
  }		 
  	 
  var t = "_blank";
  var feat = "toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,width=800,height=600,top=0,left=100";
  opened = window.open(f, t, feat);
}

//funzione per la selezione dell'azienda con pulsante di lookup, gestisce la sezione per aziende interinali quando se ne 
//seleziona una di quel tipo
function aggiornaAzienda() {
  document.Frm1.PRGAZIENDA.value = opened.dati.prgAzienda;
  document.Frm1.PRGUNITA.value = opened.dati.prgUnita;
  document.Frm1.CODCPI.value = opened.dati.codCpi;  
  document.Frm1.strPartitaIvaAz.value = opened.dati.partitaIva;
  document.Frm1.strCodiceFiscaleAz.value = opened.dati.codiceFiscaleAzienda;
  document.Frm1.strRagioneSocialeAz.value = opened.dati.ragioneSociale;
  if (document.Frm1.strRagioneSocialeAz.value.length > 30) {
    document.Frm1.strRagioneSocialeAzTrunc.value = document.Frm1.strRagioneSocialeAz.value.substring(0,26) + "...";
  } 
  else {
    document.Frm1.strRagioneSocialeAzTrunc.value = opened.dati.ragioneSociale;
  }
  document.Frm1.strIndirizzoUAzVisualizzato.value = opened.dati.strIndirizzoAzienda + " (" + opened.dati.comuneAzienda + ", " + opened.dati.strCapAzienda + ")"; 
  document.Frm1.strIndirizzoUAz.value = opened.dati.strIndirizzoAzienda; 
  document.Frm1.STRENTERILASCIO.value = document.Frm1.strRagioneSocialeAz.value + " - " + document.Frm1.strIndirizzoUAz.value;
  document.Frm1.strTelUAz.value = opened.dati.strTelAzienda;
  document.Frm1.strFaxUAz.value = opened.dati.strFaxAzienda;
  document.Frm1.codCCNLAz.value = opened.dati.CCNLAz;
  document.Frm1.descrCCNLAz.value = opened.dati.descrCCNLAz;
  document.Frm1.codDescrCCNLAz.value =opened.dati.CCNLAz + " - " + opened.dati.descrCCNLAz;
  document.Frm1.codAtecoUAz.value = opened.dati.codAteco;
  document.Frm1.strDesAtecoUAz.value = opened.dati.strDesAtecoUAz;
  document.Frm1.codAtecoStrDesAttivitaAz.value = opened.dati.codAteco + " - " + opened.dati.strDesAtecoUAz;
  document.Frm1.DESCRTIPOAZIENDA.value = opened.dati.descrTipoAz;
  document.Frm1.CODTIPOAZIENDA.value = opened.dati.codTipoAz;
  if (document.Frm1.CODTIPOAZIENDA.value == codInterinale){
    document.Frm1.STRNUMALBOINTERINALI.value = opened.dati.numAlboInterinali;
    visualizzaCampi("alboInt","inline");
  } else visualizzaCampi("alboInt","none");
  var strCampi = opened.dati.numRegCommitt.toString();
  if (strCampi != "" ){
    document.Frm1.STRNUMREGISTROCOMMITT.value = opened.dati.numRegCommitt;   
    visualizzaCampi("regCommit","inline");
  } else visualizzaCampi("regCommit","none");
  document.Frm1.CODTIPOAZIENDA.value = opened.dati.codTipoAz;
  document.Frm1.natGiuridicaAz.value = opened.dati.natGiurAz;
  document.Frm1.CODNATGIURIDICAAZ.value = opened.dati.codNatGiurAz; 
  document.Frm1.STRREFERENTE.value = opened.dati.strReferente;
  //Se non ho l'azienda utilizzatrice imposto il luogo di lavoro
  if ((strNotNull(document.Frm1.PRGAZIENDAUTILIZ.value) == "") &&
	(strNotNull(document.Frm1.PRGUNITAUTILIZ.value) == "")) {
  	document.Frm1.STRLUOGODILAVORO.value = document.Frm1.strRagioneSocialeAz.value + " - " 
  										 + document.Frm1.strIndirizzoUAzVisualizzato.value;
  	document.Frm1.STRLUOGODILAVORO.readOnly = false;
  }      
  //Inps ed inail
  var str = null;
  if (opened.dati.strPatInail != null) {
    str = opened.dati.strPatInail.toString();
    document.Frm1.STRPATINAIL1.value = str.substr(0,8);
    document.Frm1.STRPATINAIL2.value = str.substr(8,2);
  }
  if (opened.dati.strNumeroInps != null) {      
    str = opened.dati.strNumeroInps.toString();
    document.Frm1.STRPOSINPS1.value = str.substr(0,2);
    document.Frm1.STRPOSINPS2.value = str.substr(2,str.length-2);
  }
  document.Frm1.FLGDATIOK.value = opened.dati.FLGDATIOK;
  if ( document.Frm1.FLGDATIOK.value == "S" ){ 
    document.Frm1.FLGDATIOK.value = "Si";
  }
  else {
    if ( document.Frm1.FLGDATIOK.value != "" ){
      document.Frm1.FLGDATIOK.value = "No";
    }
  }

  if (opened.dati.flgCambiamentiDati != null && document.Frm1.flgCambiamentiDati != null) {
    document.Frm1.flgCambiamentiDati.value = document.Frm1.flgCambiamentiDati.value + opened.dati.flgCambiamentiDati;
  }
  opened.close();
  if ((document.Frm1.PRGAZIENDA.value != "") && (document.Frm1.PRGUNITA.value != "")) {
    if (document.getElementById("DettaglioSedeAzienda") != null) {
      document.getElementById("DettaglioSedeAzienda").style.display = "inline";
    }
  }
  if (document.getElementById("AggiungiAziendaMovimento") != null) {
    if (document.getElementById("AggiungiAziendaMovimento").style.display == 'inline') {
      document.getElementById("AggiungiAziendaMovimento").style.display = "none";
    }
  }    
  visualizzaInterinali(document.Frm1.CODTIPOAZIENDA.value, false);
  document.Frm1.RESETCFL.value = 'true';
  //Gestione della combo del codTipoMov
  resetCodTipoMov();
}

//Azzera i dati dell'azienda
function azzeraAzienda() {
  if (confirm("Si desidera procedere con lo scollegamento dell'azienda?")){
	document.Frm1.PRGAZIENDA.value = '';
	document.Frm1.PRGUNITA.value = '';
	document.Frm1.strPartitaIvaAz.value = '';
	document.Frm1.strCodiceFiscaleAz.value = '';
	document.Frm1.strRagioneSocialeAz.value = '';
	document.Frm1.strRagioneSocialeAzTrunc.value = '';
	document.Frm1.strIndirizzoUAzVisualizzato.value = '';
	document.Frm1.strIndirizzoUAz.value = '';
	document.Frm1.STRENTERILASCIO.value = '';
	document.Frm1.strTelUAz.value = '';
	document.Frm1.strFaxUAz.value = '';
	document.Frm1.codCCNLAz.value = '';
	document.Frm1.descrCCNLAz.value = '';
	document.Frm1.codDescrCCNLAz.value = '';
	document.Frm1.codAtecoUAz.value = '';
	document.Frm1.strDesAtecoUAz.value = '';
	document.Frm1.codAtecoStrDesAttivitaAz.value = '';
	document.Frm1.DESCRTIPOAZIENDA.value = '';
	document.Frm1.STRNUMALBOINTERINALI.value = '';
	document.Frm1.STRNUMREGISTROCOMMITT.value = '';
	document.Frm1.CODTIPOAZIENDA.value = '';
	document.Frm1.natGiuridicaAz.value = '';
	document.Frm1.STRREFERENTE.value = '';	
	document.Frm1.CODNATGIURIDICAAZ.value = ''; 
	document.Frm1.STRLUOGODILAVORO.value = '';
  	document.Frm1.STRLUOGODILAVORO.readOnly = false;	
	document.Frm1.STRPATINAIL1.value = '';
	document.Frm1.STRPATINAIL2.value = '';
	document.Frm1.STRPOSINPS1.value = '';
	document.Frm1.STRPOSINPS2.value = '';
	document.Frm1.FLGDATIOK.value = '';
	if (document.Frm1.flgCambiamentiDati != null) {
		document.Frm1.flgCambiamentiDati.value = '';
	}
    if (document.getElementById("DettaglioSedeAzienda") != null) {
      document.getElementById("DettaglioSedeAzienda").style.display = "none";
    }
	if (document.getElementById("AggiungiAziendaMovimento") != null) {
		document.getElementById("AggiungiAziendaMovimento").style.display = "none";
	}  
	visualizzaInterinali('', false);
    document.Frm1.PRGAZIENDAUTILIZ.value = '';
    document.Frm1.PRGUNITAUTILIZ.value = '';
    document.Frm1.strRagioneSocialeAzUtil.value = '';
	document.Frm1.strIndirizzoUAzUtil.value = '';
	document.Frm1.strComuneUAzUtil.value = '';    
    document.Frm1.STRAZINTNUMCONTRATTO.value = '';
    document.Frm1.DATAZINTINIZIOCONTRATTO.value = '';
    document.Frm1.DATAZINTFINECONTRATTO.value = '';
    document.Frm1.STRAZINTRAP.value = '';
    document.Frm1.NUMAZINTSOGGETTI.value = '';
    document.Frm1.NUMAZINTDIPENDENTI.value = '';
    resetCodTipoMov();
  }
}

//apre la pop-up per l'aggiornamento dei dati dell'azienda
function apriAggiornaAzienda(prgAzienda, prgUnita, cdnFunzione, prgMovimentoApp) {
  var f = "AdapterHTTP?PAGE=AggiornaDatiAziendaPage&PRGAZIENDA=" + prgAzienda + "&PRGUNITA=" + prgUnita + 
  "&CDNFUNZIONE=" + cdnFunzione + "&PRGMOVIMENTOAPP=" + prgMovimentoApp + "&CONTESTO=AZIENDA" + 
  "&FUNZ_AGGIORNAMENTO=aggiornaDettaglioGenerale";
  
  if (document.Frm1.CURRENTCONTEXT.value != null) {
   	f = f + "&context=" +  document.Frm1.CURRENTCONTEXT.value;
  }	  
  
  var t = "_blank"; 
  var feat = "toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,width=800,height=600,top=0,left=100";
  opened = window.open(f, t, feat);
}

//apre la pop-up di scelta dell'unita aziendale tra quelle trovate
function apriScegliUnitaAzienda(prgAzienda, cdnFunzione, prgMovimentoApp) {
  var f = "AdapterHTTP?PAGE=ListaUnitaAziendaPage&CONTESTO=AZIENDA&PRGAZ=" + prgAzienda + 
  "&CDNFUNZIONE=" + cdnFunzione + "&PRGMOVIMENTOAPP=" + prgMovimentoApp + 
  "&FUNZ_AGGIORNAMENTO=aggiornaDettaglioGenerale";
  
  if (document.Frm1.CURRENTCONTEXT.value != null) {
  	f = f + "&context=" +  document.Frm1.CURRENTCONTEXT.value;
  }	
  
  var t = "_blank";
  var feat = "toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,width=800,height=600,top=0,left=100";
  opened = window.open(f, t, feat);	
}

//Aggiorna la pagina salvando i dati contenuti
function aggiornaDettaglioGenerale() {
	doFormSubmit(document.Frm1);
}