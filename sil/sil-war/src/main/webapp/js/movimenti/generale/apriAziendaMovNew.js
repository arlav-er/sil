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
	var tipoMov = document.Frm1.CODTIPOMOV.value;
	var motivoTra = document.Frm1.CODTIPOTRASF.value;
	if (tipoAzienda == codInterinale) {
    	document.getElementById("CheckboxPersonaleInterno").style.display="";
    	document.getElementById("sezTipoVariazione").style.display="";
    	if (flgAssInterna) {
      		document.getElementById("flagPersonaleInterno").checked = true;
      		document.getElementById("visDateMissione").style.display="none";
    		document.getElementById("aziSomm").style.display="none";
    		if(!consulta && tipoMov != '' && tipoMov == 'TRA' && motivoTra != '' && motivoTra == 'DL') {
    			document.getElementById("flagAziEstera").disabled = false;
    			document.getElementById("flagDistacco").disabled = false;
    		} 
    	}
    	else {
      		document.getElementById("flagPersonaleInterno").checked = false;
      		document.getElementById("visDateMissione").style.display="";
    		document.getElementById("aziSomm").style.display="";
    		if(!consulta && tipoMov != '' && tipoMov == 'TRA' && motivoTra != '' && motivoTra == 'DL') {
    			document.getElementById("flagAziEstera").disabled = true;
    			document.getElementById("flagDistacco").disabled = true;
    		}  
      	}
    	//if (precedente){ //se è interna e riguarda il movimento precedente
       	//	document.getElementById("flagPersonaleInterno").disabled = true;
    	//}
    	gestisciSezioneAzUtil(flgAssInterna);
  	} 
  	else {
    	document.getElementById("CheckboxPersonaleInterno").style.display="none";
    	document.getElementById("sezTipoVariazione").style.display="none";
    	document.getElementById("flagPersonaleInterno").checked = false;
    }
}

function visualizzaAziUt(){
	if(document.Frm1.PRGAZIENDAUTILIZ.value != '' && document.Frm1.PRGUNITAUTILIZ.value != '') {
		document.getElementById("DettaglioAziendaUtil").style.display = "none";
  		document.getElementById("DettaglioAziUtil").style.display = "";
  		document.getElementById("DeleteAziUtil").style.display = "";
  	}
	else {
		document.getElementById("DettaglioAziendaUtil").style.display = "";
  		document.getElementById("DettaglioAziUtil").style.display = "none";
  		document.getElementById("DeleteAziUtil").style.display = "none";
	}
}

//Apre la pop-up di inserimento della testata aziendale
function apriInserisciAzienda() {
  var f = "AdapterHTTP?PAGE=MovimentiTestataAziendaPage&CONTESTO=AZIENDA" + 
  	"&PRGMOVIMENTOAPP=" + document.Frm1.PRGMOVIMENTOAPP.value + 
  	"&FUNZ_AGGIORNAMENTO=aggiornaDettaglioGenerale";
  var t = "_blank";
  var feat = "toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,width=800,height=600,top=0,left=100";
  openedTestataAzienda = window.open(f, t, feat);
}

//Apre la pop-up di inserimento della testata aziendale
function apriInserisciAziendaNew() {
  var f = "AdapterHTTP?PAGE=MovimentiTestataAziendaPage&CONTESTO=AZIENDA&daMovimentiNew=S" + 
  	"&PRGMOVIMENTOAPP=" + document.Frm1.PRGMOVIMENTOAPP.value + 
  	"&FUNZ_AGGIORNAMENTO=aggiornaDettaglioAzienda";
  var t = "_blank";
  var feat = "toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,width=800,height=600,top=0,left=100";
  opened = window.open(f, t, feat);
}

//Apre la pop-up di inserimento dell'unita aziendale
function apriInserisciUnitaAzienda() {
  var f = "AdapterHTTP?PAGE=MovimentiUnitaAziendaPage&CONTESTO=AZIENDA" + 
  	"&PRGMOVIMENTOAPP=" + document.Frm1.PRGMOVIMENTOAPP.value + "&PRGAZIENDA=" + document.Frm1.PRGAZIENDA.value
  	 + "&FUNZ_AGGIORNAMENTO=aggiornaDettaglioGenerale";
  var t = "_blank";
  var feat = "toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,width=800,height=600,top=0,left=100";
  opened = window.open(f, t, feat);
}

//Apre la pop-up di inserimento dell'unita aziendale
function apriInserisciUnitaAziendaNew() {
  var f = "AdapterHTTP?PAGE=MovimentiUnitaAziendaPage&CONTESTO=AZIENDA&daMovimentiNew=S" + 
  	"&PRGMOVIMENTOAPP=" + document.Frm1.PRGMOVIMENTOAPP.value + "&PRGAZIENDA=" + document.Frm1.PRGAZIENDA.value
  	 + "&FUNZ_AGGIORNAMENTO=aggiornaDettaglioAzienda&strCodiceFiscale=" + document.Frm1.strCodiceFiscaleAz.value
  	 + "&codComune=" + document.Frm1.codComune.value;
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
  if (document.Frm1.strRagioneSocialeAz.value.length > 36) {
    document.Frm1.strRagioneSocialeAzTrunc.value = document.Frm1.strRagioneSocialeAz.value.substring(0,34) + "...";
  } 
  else {
    document.Frm1.strRagioneSocialeAzTrunc.value = opened.dati.ragioneSociale;
  }
  var objCfEstero = document.Frm1.CODFISCAZESTERA;
  var objRagSocEstera = document.Frm1.RAGSOCAZESTERA;
  if (objCfEstero != undefined && opened.dati.CODFISCAZESTERA != undefined) {
	  document.Frm1.CODFISCAZESTERA.value = opened.dati.CODFISCAZESTERA;  
  }
		  
  if (objRagSocEstera != undefined && opened.dati.RAGSOCAZESTERA != undefined) {
	  document.Frm1.RAGSOCAZESTERA.value = opened.dati.RAGSOCAZESTERA;
  }
  document.Frm1.strIndirizzoUAzVisualizzato.value = opened.dati.strIndirizzoAzienda + " (" + opened.dati.comuneAzienda + ", " + opened.dati.strCapAzienda + ")"; 
  document.Frm1.strIndirizzoUAz.value = opened.dati.strIndirizzoAzienda; 
  document.Frm1.STRENTERILASCIO.value = document.Frm1.strRagioneSocialeAz.value + " - " + document.Frm1.strIndirizzoUAz.value;
  document.Frm1.strTelUAz.value = opened.dati.strTelAzienda;
  document.Frm1.strFaxUAz.value = opened.dati.strFaxAzienda;
  document.Frm1.codCCNLAz.value = opened.dati.CCNLAz;
  document.Frm1.descrCCNLAz.value = opened.dati.descrCCNLAz;
  document.Frm1.codDescrCCNLAz.value =opened.dati.CCNLAz + " - " + opened.dati.descrCCNLAz;
  
  //document.Frm1.codCCNL.value = opened.dati.CCNLAz;
  //document.Frm1.strCCNL.value = opened.dati.descrCCNLAz;
  
  document.Frm1.codAtecoUAz.value = opened.dati.codAteco;
  document.Frm1.strDesAtecoUAz.value = opened.dati.strDesAtecoUAz;
  document.Frm1.codAtecoStrDesAttivitaAz.value = opened.dati.codAteco + " - " + opened.dati.strDesAtecoUAz;
  document.Frm1.DESCRTIPOAZIENDA.value = opened.dati.descrTipoAz;
  document.Frm1.CODTIPOAZIENDA.value = opened.dati.codTipoAz;
  
  document.Frm1.CODREGIONE.value = opened.dati.codRegione;
  
  if (document.Frm1.CODTIPOAZIENDA.value == codInterinale){
 
    document.Frm1.STRNUMALBOINTERINALI.value = opened.dati.numAlboInterinali;
    visualizzaCampi("alboInt","");
    visualizzaCampi("azSommEstera", "");
    visualizzaCampi("lavStagionaleLabel", "none");
    visualizzaCampi("lavStagioneFlg", "none");
    visualizzaCampi("sezTipoVariazione", "");
    
   	if(document.Frm1.FLGINTERASSPROPRIACHECKBOX.checked == "false");{
    	visualizzaCampi("aziSomm","");
    	visualizzaCampi("visDateMissione","");	
    }
  } 
  else { 
 	visualizzaCampi("alboInt","none");
  	visualizzaCampi("visDateMissione","none");
  	visualizzaCampi("aziSomm","none");
  	visualizzaCampi("azSommEstera", "none");
  	visualizzaCampi("lavStagionaleLabel", "");
    visualizzaCampi("lavStagioneFlg", "");
    visualizzaCampi("sezTipoVariazione", "none");
  }
  
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
  	document.getElementById("DettaglioAziendaUtil").style.display = "";
  	document.getElementById("DettaglioAziUtil").style.display = "none";
  	document.getElementById("DeleteAziUtil").style.display = "none";
  }
  else {
 	document.getElementById("DettaglioAziendaUtil").style.display = "none";
  	document.getElementById("DettaglioAziUtil").style.display = "";
  	document.getElementById("DeleteAziUtil").style.display = "";
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
  
  if (opened.dati.flgCambiamentiDati != null && document.Frm1.flgCambiamentiDati != null) {
    document.Frm1.flgCambiamentiDati.value = document.Frm1.flgCambiamentiDati.value + opened.dati.flgCambiamentiDati;
  }
  opened.close();
  
 // var imgV = document.getElementById("tendinaAzienda");
 // for(i=0;i<5;i++){
 // 	var sez = "datiAzienda_" + i;
 // 	var sezAzi = document.getElementById(sez);
 //	sezAzi.style.display = "";
 // }      
 // imgV.src=imgAperta;
 
  if ((document.Frm1.PRGAZIENDA.value != "") && (document.Frm1.PRGUNITA.value != "")) {
  //  if (document.getElementById("ricercaAzienda") != null) {
    //  document.getElementById("ricercaAzienda").style.display = "none";
   // }
   // if (document.getElementById("scollegaAzienda") != null) {
    //  document.getElementById("scollegaAzienda").style.display = "none";
   // }
   	  if (document.getElementById("DettaglioSedeAzienda") != null) {
        document.getElementById("DettaglioSedeAzienda").style.display = "";
      }
  }
  if (document.getElementById("AggiungiAziendaMovimento") != null) {
    if (document.getElementById("AggiungiAziendaMovimento").style.display == '') {
      document.getElementById("AggiungiAziendaMovimento").style.display = "none";
    }
  }    
  visualizzaInterinali(document.Frm1.CODTIPOAZIENDA.value, false);
  document.Frm1.RESETCFL.value = 'true';
  
  if(document.Frm1.CODTIPOMOV.value != "" && document.Frm1.CODTIPOMOV.value == "TRA") {  
  	document.Frm1.strCodiceFiscaleAzTra.value = document.Frm1.strCodiceFiscaleAz.value;
  	document.Frm1.strRagioneSocialeAzTruncTra.value = "";
  	document.Frm1.strIndirizzoUAzVisualizzatoTra.value = "";
  }
  
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
	document.Frm1.codCCNL.value = '';
  	document.Frm1.strCCNL.value = '';
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
	document.Frm1.CODREGIONE.value = '';
	document.Frm1.codTipoAss.value = '';
	document.Frm1.descrTipoAss.value = '';
	document.Frm1.numConvenzione.value="";
	document.Frm1.datConvenzione.value="";
	document.Frm1.DATFINESGRAVIO.value="";
	document.Frm1.DECIMPORTOCONCESSO.value="";
	document.Frm1.numConvenzione.className="viewRiepilogo";
	document.Frm1.datConvenzione.className="viewRiepilogo";
	document.Frm1.DATFINESGRAVIO.className="viewRiepilogo";
	document.Frm1.DECIMPORTOCONCESSO.className="viewRiepilogo";
	// in caso di trasferimento del lavoratore devono essere aggiornati anche questi campi 
	document.Frm1.strCodiceFiscaleAzTra.value = '';
    document.Frm1.strRagioneSocialeAzTruncTra.value = '';
    document.Frm1.strRagioneSocialeAzTra.value = '';
    document.Frm1.strIndirizzoUAzVisualizzatoTra.value = '';
    document.Frm1.strIndirizzoUAzTra.value = '';
    document.Frm1.strComuneUAzTra.value = '';
    document.Frm1.strCapUAzTra.value = '';
    document.Frm1.CODFISCAZESTERA.value = '';
    document.Frm1.RAGSOCAZESTERA.value = '';
  }
	
	if (document.Frm1.flgCambiamentiDati != null) {
		document.Frm1.flgCambiamentiDati.value = '';
	}
	
	var imgV = document.getElementById("tendinaAzienda");
  	for(i=0;i<5;i++){
  		var sez = "datiAzienda_" + i;
  		var sezAzi = document.getElementById(sez);
		sezAzi.style.display = "none";
  	}      
  	imgV.src=imgChiusa;
  
    if (document.getElementById("DettaglioSedeAzienda") != null) {
      document.getElementById("DettaglioSedeAzienda").style.display = "none";
    }
	if (document.getElementById("AggiungiAziendaMovimento") != null) {
		document.getElementById("AggiungiAziendaMovimento").style.display = "none";
	}
	if (document.getElementById("visDateMissione") != null) {
		document.getElementById("visDateMissione").style.display = "none";
	}
	if (document.getElementById("aziSomm") != null) {
		document.getElementById("aziSomm").style.display = "none";
	}
	if (document.getElementById("sezTipoVariazione") != null) {
		document.getElementById("sezTipoVariazione").style.display = "none";
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

//apre la pop-up per l'aggiornamento dei dati dell'azienda
function apriAggiornaAziendaNew(prgAzienda, prgUnita, cdnFunzione, prgMovimentoApp) {
  var f = "AdapterHTTP?PAGE=AggiornaDatiAziendaPage&daMovimentiNew=S&PRGAZIENDA=" + prgAzienda + "&PRGUNITA=" + prgUnita + 
  "&CDNFUNZIONE=" + cdnFunzione + "&PRGMOVIMENTOAPP=" + prgMovimentoApp + "&CONTESTO=AZIENDA" + 
  "&FUNZ_AGGIORNAMENTO=aggiornaDettaglioGeneraleNew";
  
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
  var t = "_blank";
  var feat = "toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,width=800,height=600,top=0,left=100";
  opened = window.open(f, t, feat);	
}

//apre la pop-up di scelta dell'unita aziendale tra quelle trovate
function apriScegliUnitaAziendaNew(prgAzienda, cdnFunzione, prgMovimentoApp) {
  var f = "AdapterHTTP?PAGE=ListaUnitaAziendaPage&CONTESTO=AZIENDA&daMovimentiNew=S&PRGAZ=" + prgAzienda + 
  "&CDNFUNZIONE=" + cdnFunzione + "&PRGMOVIMENTOAPP=" + prgMovimentoApp + 
  "&FUNZ_AGGIORNAMENTO=scegliUnitaAziendaNew";
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

function aggiornaDettaglioGeneraleNew() {
	var esistonoDifferenze = opened.dati.differenze;
	document.Frm1.strRagioneSocialeAz.value = opened.dati.ragSocAz;
  	if (document.Frm1.strRagioneSocialeAz.value.length > 36) {
    	document.Frm1.strRagioneSocialeAzTrunc.value = document.Frm1.strRagioneSocialeAz.value.substring(0,34) + "...";
  	} 
 	else {
    	document.Frm1.strRagioneSocialeAzTrunc.value = opened.dati.ragSocAz;
  	}
  	document.Frm1.strPartitaIvaAz.value=opened.dati.pIvaAz;
  	document.Frm1.STRNUMALBOINTERINALI.value = opened.dati.numIscrAlboIntAz;
  	document.Frm1.strIndirizzoUAzVisualizzato.value = opened.dati.indirAz + " (" + opened.dati.comune + ", " + opened.dati.capAz + ")"; 
  	document.Frm1.strIndirizzoUAz.value = opened.dati.indirAz; 
  	document.Frm1.codAtecoUAz.value = opened.dati.codAtecoAz;
	document.Frm1.strDesAtecoUAz.value = opened.dati.strDesAteco;
	document.Frm1.strTelUAz.value = opened.dati.telAz;
	document.Frm1.strFaxUAz.value = opened.dati.FaxAz;
	document.Frm1.codCCNLAz.value = opened.dati.ccnlAz;
	document.Frm1.descrCCNLAz.value = opened.dati.descrCcnlAz;
	document.Frm1.codCCNL.value = opened.dati.ccnlAz;
  	document.Frm1.strCCNL.value = opened.dati.descrCcnlAz;
    document.Frm1.codDescrCCNLAz.value = document.Frm1.codCCNLAz.value + " - " + document.Frm1.descrCCNLAz.value;
	document.Frm1.codAtecoStrDesAttivitaAz.value = document.Frm1.codAtecoUAz.value + " - " + document.Frm1.strDesAtecoUAz.value;
	document.Frm1.CODFISCAZESTERA.value = opened.dati.cfAzEstera;
	document.Frm1.RAGSOCAZESTERA.value = opened.dati.ragSocAzEstera;
	
	if (esistonoDifferenze == "true") {
		document.getElementById("aggiornaAzienda").style.display = "none";
	} else {
		document.getElementById("aggiornaAzienda").style.display = "";
	}
}

function aggiornaDettaglioAzienda() {
	var diffUnitaIndirizzo = opened.dati.diffUnitaIndirizzo;
	var UgualeUnitaIndirizzo = opened.dati.UgualeUnitaIndirizzo;
	var esistonoDifferenze = opened.dati.esistonoDifferenze;
	var indirizzo = document.Frm1.strIndirizzoUAz.value;
	document.Frm1.PRGAZIENDA.value=opened.dati.prgAzienda;
	document.Frm1.PRGUNITA.value=opened.dati.prgUnita;
	if(document.Frm1.PRGAZIENDA.value != "") {
		document.getElementById("DettaglioInserisciAzienda").style.display = "none";
	} 
	document.Frm1.strPartitaIvaAz.value=opened.dati.strPartitaIva;
	document.Frm1.strRagioneSocialeAz.value = opened.dati.strRagioneSociale;
	
	if (document.Frm1.strRagioneSocialeAz.value.length > 36) {
    	document.Frm1.strRagioneSocialeAzTrunc.value = document.Frm1.strRagioneSocialeAz.value.substring(0,34) + "...";
  	} 
 	else {
    	document.Frm1.strRagioneSocialeAzTrunc.value = opened.dati.strRagioneSociale;
  	}
 	
 	document.Frm1.strCodiceFiscaleAz.value = opened.dati.strCodiceFiscale;
 	
  	document.Frm1.natGiuridicaAz.value = opened.dati.natGiuridicaAz;
  	document.Frm1.CODTIPOAZIENDA.value = opened.dati.CODTIPOAZIENDA;
  	document.Frm1.DESCRTIPOAZIENDA.value = opened.dati.DESCRTIPOAZIENDA;
  	document.Frm1.STRNUMALBOINTERINALI.value = opened.dati.STRNUMALBOINTERINALI;
  	
  	if (document.Frm1.CODTIPOAZIENDA.value == codInterinale){
 		document.Frm1.STRNUMALBOINTERINALI.value = opened.dati.STRNUMALBOINTERINALI;
    	visualizzaCampi("alboInt","");
    	document.getElementById("CheckboxPersonaleInterno").style.display="";
    	visualizzaCampi("aziSomm","");
    	visualizzaCampi("visDateMissione","");
    	visualizzaCampi("azSommEstera","");
    	visualizzaCampi("sezTipoVariazione","");
    } 
   	else { 
 		visualizzaCampi("alboInt","none");
 		document.getElementById("CheckboxPersonaleInterno").style.display="none";
 		visualizzaCampi("aziSomm","none");
    	visualizzaCampi("visDateMissione","none");
    	visualizzaCampi("azSommEstera","none");
    	visualizzaCampi("sezTipoVariazione","none");
  	}
  	if(document.Frm1.PRGUNITA.value != "") {
  		document.getElementById("DettaglioInserisciUnita").style.display = "none";
  		if(UgualeUnitaIndirizzo == "true" && (indirizzo != opened.dati.strIndirizzo) ) {
			document.getElementById("ScegliUnitaTrovate").style.display = "";
		} else {
			document.getElementById("ScegliUnitaTrovate").style.display = "none";
			document.getElementById("DettaglioApriUnita").style.display = "";
			if(esistonoDifferenze == "true") {
				document.getElementById("aggiornaAzienda").style.display = "";
			} else {
				document.getElementById("aggiornaAzienda").style.display = "none";
			}
		}
		document.Frm1.strIndirizzoUAzVisualizzato.value = opened.dati.strIndirizzo + " (" + opened.dati.desComune + ", " + opened.dati.strCap + ")"; 
  		document.Frm1.strIndirizzoUAz.value = opened.dati.strIndirizzo;
  		document.Frm1.codDescrCCNLAz.value = opened.dati.codCCNL + " - " + opened.dati.strCCNL
		document.Frm1.codAtecoStrDesAttivitaAz.value = opened.dati.codAteco+ " - " + opened.dati.strTipoAteco;
		document.Frm1.strTelUAz.value = opened.dati.strTel;
		document.Frm1.strFaxUAz.value = opened.dati.strFax;
		document.Frm1.STRNUMREGISTROCOMMITT.value = opened.dati.STRNUMREGISTROCOMMITT;
		document.getElementById("ApriScegliUnita").style.display = "none";
    }
	else {
		if(diffUnitaIndirizzo == "true" || diffUnitaIndirizzo == "") {
			document.getElementById("DettaglioInserisciUnita").style.display = "";
		} else {
			document.getElementById("DettaglioInserisciUnita").style.display = "none";
			document.getElementById("ApriScegliUnita").style.display = "";
		}
	}
}


function scegliUnitaAziendaNew(){
	var addMov = document.Frm1.ADD_MOVIMENTO.value;
	var diff = document.Frm1.Differenze.value;
	if(addMov == "aggiungiMov") {
		document.getElementById("ApriScegliUnita").style.display = "none";
		document.getElementById("DettaglioApriUnita").style.display = "";
		if(diff == "true") {
			document.getElementById("aggiornaAzienda").style.display = "";
		}
	}
}

function visualizzaAziUtConsulta(){
	if(document.Frm1.PRGAZIENDAUTILIZ.value != '' && document.Frm1.PRGUNITAUTILIZ.value != '') {
		document.getElementById("DettaglioAziUtil").style.display = "";
	} else {
		document.getElementById("DettaglioAziUtil").style.display = "none";
	}
}