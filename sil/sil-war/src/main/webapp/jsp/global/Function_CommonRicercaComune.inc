<SCRIPT TYPE="text/javascript">

  function PulisciRicerca(codcomune, codcomunehid, nomecomune, nomecomunehid, cap, caphid, strTipo) {
    if (strTipo == 'descrizione') {
      if (nomecomune.value != nomecomunehid.value) {
        nomecomunehid.value=(nomecomune.value).toUpperCase();
        nomecomune.value=(nomecomune.value).toUpperCase();
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
        codcomune.value=(codcomune.value).toUpperCase();
        nomecomune.value="";
        nomecomunehid.value="";	
        if(cap != null) {
          cap.value="";
          caphid.value="";
        }
      }
    }
    else if(strTipo == 'cap'){
    /*
      if (cap.value != caphid.value) {
      	//alert("CAP:"+cap.name+"="+cap.value+"  e CAP_Hid:"+caphid.name+"="+caphid.value+"   sono diversi.");
        caphid.value=(cap.value).toUpperCase();
        nomecomune.value="";
        nomecomunehid.value="";
        codcomunehid.value="";
        codcomune.value="";
      }
      Sarebbe opportuno togliere la chiamata a questa funzione nel campo CAP anziche commentare questa sezione.
      Ma visto che il tempo è poco e che è usata in 18 JSP... rimandiamo a data da destinarsi.
    */
    }
  }

  //btFindComuneCAP_onclick  implementata al posto di btFindComune_onclick per visualizzare anche il CAP solo dove è richiesto
  function btFindComuneCAP_onclick(codcomune, nomecomune, retcap, tipoRicerca, cosaCerchi, codCPIcampo, funzione, funzione_2) {

    var s= "AdapterHTTP?PAGE=RicercaComunePage";

    cosaCerchi = cosaCerchi.toUpperCase();
    
    if (tipoRicerca == 'descrizione') {
      if (nomecomune.value == ""){//NON faccio niente e azzero i valori dei prametri
        codcomune.value = "";
        if(retcap != null) {
          retcap.value="";
        }
      }
      else {//aggiungo i parametri alla chiamata alla RicercaComunePage e apro una popUp
        s += "&strdenominazione=" + nomecomune.value.toUpperCase();
        s += "&retcod="+codcomune.name;
        s += "&retnome="+nomecomune.name;
        if (retcap!=null){
        s += "&retcap="+retcap.name;
        }
        s += "&tipoRicerca="+tipoRicerca;
        
        if ( cosaCerchi == 'COMUNI') {
        s += "&tipo=COMUNI";           
        } else if ( cosaCerchi == 'STATI'){
        s += "&tipo=STATI";
        } else {
        s += "&tipo=";
        }
	if (codCPIcampo != null) {
	    s += "&codCPIcampo="+codCPIcampo.name;
	}
	if (funzione != null) {
	    s += "&funzione="+funzione;
	}
	if (funzione_2 != null) {
	    s += "&funzione_2="+funzione_2;
	}
        window.open(s,"Comuni", 'toolbar=0, scrollbars=1');
       }
    }
    else {
      if (tipoRicerca == 'codice') {

        var titoloCampo = "comune/stato";
        if (nomecomune.title != null && nomecomune.title != "") titoloCampo = nomecomune.title;
     
        if (codcomune.value == ""){//NON faccio niente e azzero i valori dei prametri
          nomecomune.value = "";
          if(retcap != null) {
            retcap.value="";
          }
        }
        else {//aggiungo i parametri alla chiamata alla RicercaComunePage e apro una popUp
          s += "&codcom=" + codcomune.value.toUpperCase();
          s += "&retcod="+codcomune.name;
          s +="&retnome="+nomecomune.name;
          if (retcap!=null){
            s +="&retcap="+retcap.name;
          }
          s +="&tipoRicerca="+tipoRicerca;
          
          if ( cosaCerchi == 'COMUNI') {
             s += "&tipo=COMUNI";
             var regExp = /^[A-Y]/;
             if((codcomune.length > 4) || (codcomune.value.search(regExp) == -1))
             {	alert("Codice del "+titoloCampo+" errato");
               return;
             }
           
          } else if ( cosaCerchi == 'STATI'){
             s += "&tipo=STATI";
             var regExp = /^[Z]/;
             if((codcomune.length > 4) || (codcomune.value.search(regExp) == -1))
             {	alert("Codice del "+titoloCampo+" errato");
               return;
             }
          } else {
             s += "&tipo=";
             var regExp = /^[A-Z]/;
             if((codcomune.length > 4) || (codcomune.value.search(regExp) == -1))
             {	alert("Codice del "+titoloCampo+" errato");
               return;
             }
          }
          if (codCPIcampo != null) {
              s += "&codCPIcampo="+codCPIcampo.name;
          }
          if (funzione != null) {
             s += "&funzione="+funzione;
          }
		if (funzione_2 != null) {
		    s += "&funzione_2="+funzione_2;
		}
          window.open(s,"Comuni", 'toolbar=0, scrollbars=1');
        }
      }//codice
    }  
  }//end function


//By Davide
var campiComuneOK = false;
function campiComuneCompletati(trueORfalse)
{ campiComuneOK = trueORfalse;
}






// Permetto l'inserimento solo di comuni esistenti:

// Comune (generico)
function inserisciComuneNonScaduto() {
   	if (isScaduto(document.Frm1.strCom.value)) {
   	   document.Frm1.codCom.value = "";
	   document.Frm1.strCom.value = "";
	}
}
function inserisciComuneTerrMansNonScaduto() {
   	if (isScaduto(document.Frm1.strComune.value)) {
   	   document.Frm1.codComune.value = "";
	   document.Frm1.strComune.value = "";
	}
}

// Comune di riferimento (nuovo/modifica gruppo)
function inserisciComNasNonScaduto() {
   	if (isScaduto(document.Frm1.strComNas.value)) {
   	   document.Frm1.codComNas.value = "";
	   document.Frm1.strComNas.value = "";
	}
}

// Comune unità azienda
function inserisciComUANonScaduto() {
   	if (isScaduto(document.Frm1.desComune.value)) {
   	   document.Frm1.codCom.value = "";
	   document.Frm1.desComune.value = "";
	}
}

// Comune profilo socio-lavorativo
function inserisciComSocioLavNonScaduto() {
   	if (isScaduto(document.Frm1.descrCom.value)) {
   	   document.Frm1.CODCOMTUTORE.value = "";
	   document.Frm1.descrCom.value = "";
	}
}

// Comune territorio
function inserisciComTerrNonScaduto() {
   	if (isScaduto(document.Frm1.STRCOMUNE.value)) {
   	   document.Frm1.CODCOM.value = "";
	   document.Frm1.STRCOMUNE.value = "";
	}
}

// Comune azienda
function inserisciComAziendaNonScaduto() {
   	if (isScaduto(document.Frm1.strComAzienda.value)) {
   	   document.Frm1.codComAzienda.value = "";
	   document.Frm1.strComAzienda.value = "";
	}
}
function inserisciCOMAZIENDANonScaduto() {
   	if (isScaduto(document.Frm1.strComAzienda.value)) {
   	   document.Frm1.CODCOMAZIENDA.value = "";
	   document.Frm1.strComAzienda.value = "";
	}
}

// Comune Ente
function inserisciComEnteNonScaduto() {
   	if (isScaduto(document.Frm1.strComEnte.value)) {
   	   document.Frm1.CODCOMENTE.value = "";
	   document.Frm1.strComEnte.value = "";
	}
}

// Comune domicilio
function inserisciComDomNonScaduto() {
   	if (isScaduto(document.Frm1.strComdom.value)) {
   	   document.Frm1.codComdom.value = "";
	   document.Frm1.strComdom.value = "";
	   document.Frm1.strCapDom.value = "";
	}
}

// Comune residenza
function inserisciComResNonScaduto() {
   	if (isScaduto(document.Frm1.strComRes.value)) {
   	   document.Frm1.codComRes.value = "";
	   document.Frm1.strComRes.value = "";
	   document.Frm1.strCapRes.value = "";
	}
}

// Comune recapito
function inserisciComRecNonScaduto() {
   	if (isScaduto(document.Frm1.strComRec.value)) {
   	   document.Frm1.codComRec.value = "";
	   document.Frm1.strComRec.value = "";
	   document.Frm1.strCapRec.value = "";
	}
}

function isScaduto(st) {
	var str = st.toLowerCase();
	// Nei casi di nomi lunghi c'è un troncamento, per questo motivo ho messo una sottostringa nel controllo:
	if (str.indexOf("(comune non più") != -1) {
		alert("Non è possibile inserire un Comune non più esistente");
		return true;
	} else if (str.indexOf("(stato non più") != -1) {
		alert("Non è possibile inserire uno Stato non più esistente");
		return true;
	} else if (str.indexOf("(comune o stato non più") != -1) {
		alert("Non è possibile inserire un Comune o Stato non più esistente");
		return true;
	}
	return false;
}










//al momento della submit controlla se i campi del comune sono obbligatori e li completa
function btFindComuneCAP_onSubmit(codcomune, nomecomune, retcap, required, codCPIcampo, funzione) 
{   var cod = codcomune.value;
    var des = nomecomune.value;
    var tipoRicerca ="";
    
    //alert("Sono in btFindComuneCAP_onSubmit("+des+")");
    var titoloCampo = "comune";
    if (nomecomune.title != null && nomecomune.title != "") titoloCampo = nomecomune.title;
       
    var codComCorretto = false;
    if ((cod != null) && (cod != ""))
    { 
       
      var regExp = /^[A-Z]\d{3}/;
      if((cod.length == 4) && (cod.search(regExp) != -1))
      { 
      	codComCorretto = true;
       }
      else
      {	alert("Codice del "+titoloCampo+" errato");
        return false;
      }
    }
    //Controllo se il comune è già stato inserito. In tal caso non procedo alla ricerca.
    //Il comune è già inserito se sono presenti entrambi i campi codice e descrizione comune.
    //Questi campi possono essere valorizzati contemporaneamente solo a seguito di una
    //ricerca e non di un inserimento "manuale" da parte dell'operatore.
    var comuneGiaInserito = false;    
    if(codComCorretto && (des.length>1))
    { 
        comuneGiaInserito = true;
         
    }
    
    if(!comuneGiaInserito){ 
    
      //I campi relativi al comune non sono completi
	  //Preparo la request e l'inserisco nella var s
      //alert("Comune da ricercare");
      if (codComCorretto){
          tipoRicerca ="codice";
      }else if(des != null && des != ""){
      	tipoRicerca ="descrizione";      
      }
      if(tipoRicerca == ""){ 
          if(required) { 
      		alert("E' necessario inserire o il codice o il nome del "+titoloCampo); return false; 
      	  }else{
       	    return true;
      	}
      }
      else{
        
        var s= "AdapterHTTP?PAGE=RicercaSpecificoComunePage";
        if (tipoRicerca == 'descrizione'){
        	 s += "&strdenominazione=" + nomecomune.value.toUpperCase();
        }else{
        	 s += "&codcom=" + codcomune.value.toUpperCase();
        }
        s += "&retcod="+codcomune.name;
        s +="&retnome="+nomecomune.name;
        if (retcap!=null){ s +="&retcap="+retcap.name; }
        s +="&tipoRicerca="+tipoRicerca;
        

        /*
        if (funzione != null) {
           s += "&funzione="+funzione;
        }
        alert("URL:: "+s);
        */
        
        //Invio la richiesta al server in "backgroud"
        var xmlhttp = null;
        var xmlPars = null;
    
        if(window.ActiveXObject) {   // IE
          xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
          try{
	         xmlhttp.Open("GET", s, false);
             xmlhttp.Send(null);
			 xmlPars = new ActiveXObject("Microsoft.XMLDOM");
             /*
             var stato = ""; var i=1;
             var div = document.getElementById("divDisplay");
             var nSt = xmlhttp.readyState;
             while ( nSt != 4)
             {   stato += i+" - "+ nSt + "\n";
                 i++;
	             div.innerHTML=stato;
	             nSt = xmlhttp.readyState;
             }
			 xmlPars.async="false"
             */             
           }
           catch(e){
             if(e instanceof Error) 
             { alert("Ricerca del comune fallita\nErrore:: "+e.value);
               return false;
             }
           }
        } else if (document.implementation && document.implementation.createDocument) {   // Mozilla
			//xmlPars= document.implementation.createDocument("","",null);
            
            xmlhttp = new XMLHttpRequest();
            xmlhttp.open("GET", s, false);
            xmlhttp.send(null);           
         		            
        } 
        var textXML = xmlhttp.responseText;		            
        var re=/\s+$|^\s+/g;
        textXML= textXML.replace(re,"");
        
        if(window.ActiveXObject) {
			 xmlPars.loadXML(textXML);		            
        } else {
			var domParser = new DOMParser();
			xmlPars = domParser.parseFromString(textXML, "text/xml");
        }

        var esito  = xmlPars.documentElement.getAttribute("esito");     
        
        
        if(esito=="success") 
        { 
        	
          var desComune = xmlPars.getElementsByTagName("nome")[0].childNodes[0].nodeValue;
          var codComune = xmlPars.getElementsByTagName("codice")[0].childNodes[0].nodeValue;
          var CAP       = xmlPars.getElementsByTagName("cap")[0].childNodes[0].nodeValue;
          var provincia = xmlPars.getElementsByTagName("provincia")[0].childNodes[0].nodeValue;
          var codcpi    = xmlPars.getElementsByTagName("codcpi")[0].childNodes[0].nodeValue;
          //var comune = "comune="+desComune+"::esito="+esito+" ("+provincia+")"+"; cod="+codComune+" CAP_"+CAP;
          codcomune.value  = codComune;
          nomecomune.value = desComune;
          if (retcap!=null){ retcap.value= CAP; }
          if (codCPIcampo!=null) { codCPIcampo.value = codcpi}
        } 
        else if(esito=="error") 
        { 		  
          alert("I dati inseriti relativi al comune \nsono errati o incompleti.");
          return false
        }
        
      }
      //Sezione div per testare la function
      //var div = document.getElementById("divDisplay");
      //div.innerHTML=comune;
      
      if(funzione!=null && funzione!="")
      { //alert("FUNCTION:: "+funzione);
        /* Si esegue la funzione  JS (o meglio la stringa) passata come argomento*/
        eval(funzione);
      }

    }//if() else alert("Il comune è già inserito");
    
    return true;
    
}




/*function codComuneCheck(inputName){
	var ctrlObj = eval("document.forms[0]." + inputName);
  if(ctrlObj.value=="")
  { 
		ctrlObj.focus();
    return false;
  }
  if (ctrlObj.value.length>4) 
  { alert("Il codice comune è errato");
		ctrlObj.focus();
    return false;
  }
  eval("document.forms[0]."+inputName+".value=document.forms[0]."+inputName+".value.toUpperCase();")
	return true;
}

function desComuneCheck(inputName){
	var ctrlObj = eval("document.forms[0]." + inputName);
  if(ctrlObj.value=="")
  { alert("Il campo\""+ctrlObj.title+"\" è obbligatorio");
		ctrlObj.focus();
    return false;
  }
	return true;
}
*/
</SCRIPT>
