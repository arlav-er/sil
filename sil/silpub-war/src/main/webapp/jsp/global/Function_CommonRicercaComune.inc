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
  function btFindComuneCAP_onclick(codcomune, nomecomune, retcap, tipoRicerca, cosaCerchi, codCPIcampo, funzione) {

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


//al momento della submit controlla se i campi del comune sono obbligatori e li completa
function btFindComuneCAP_onSubmit(codcomune, nomecomune, retcap, required) 
{   var cod = codcomune.value;
    var des = nomecomune.value;
    var tipoRicerca ="";
    
    //alert("Sono in btFindComuneCAP_onSubmit()");
    var titoloCampo = "comune";
    if (nomecomune.title != null && nomecomune.title != "") titoloCampo = nomecomune.title;
    
    var codComCorretto = false;
    if ((cod != null) && (cod != ""))
    { var regExp = /^[A-Z]\d{3}/;
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
    { comuneGiaInserito = true;
    }
    
    if(!comuneGiaInserito)
    { //I campi relativi al comune non sono completi
      //Preparo la request e l'inserisco nella var s
      //alert("Comune da ricercare");
      if (codComCorretto)               tipoRicerca ="codice";
      else if(des != null && des != "") tipoRicerca ="descrizione";      

      if(tipoRicerca == "")
      { if(required) { alert("E necessario inserire o il codice o il nome del "+titoloCampo); return false; }
        else return true;
      }
      else
      {
        var s= "AdapterHTTP?PAGE=RicercaSpecificoComunePage";
        if (tipoRicerca == 'descrizione') s += "&strdenominazione=" + nomecomune.value.toUpperCase();
        else  s += "&codcom=" + codcomune.value.toUpperCase();
  
        s += "&retcod="+codcomune.name;
        s +="&retnome="+nomecomune.name;
        if (retcap!=null){ s +="&retcap="+retcap.name; }
        s +="&tipoRicerca="+tipoRicerca;
  
        //Invio la richiesta al server in "backgroud"
        var xmlhttp = null;
        var xmlPars = null;
    
        if(window.ActiveXObject) {
          xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
          try{
             var stato = ""; var i=1;
	     xmlhttp.Open("GET", s, false);
             xmlhttp.Send(null);
             /*
             var div = document.getElementById("divDisplay");
             var nSt = xmlhttp.readyState;
             while ( nSt != 4)
             { stato += i+" - "+ nSt + "\n";
               i++;
	       div.innerHTML=stato;
	             nSt = xmlhttp.readyState;
             }
             */
             xmlPars = new ActiveXObject("Microsoft.XMLDOM")
             //xmlPars.async="false"
           }
           catch(e){
             if(e instanceof Error) 
             { alert("Ricerca del comune fallita\nErrore:: "+e.value);
               return false;
             }
           }
        } else {
          xmlhttp = new XMLHttpRequest();
          xmlhttp.open("GET", s, true);
          xmlhttp.send(null);
        }
  	
        //"Parserizzo" lo stream XML ottenuto e ricavo i risultati.
        var textXML = xmlhttp.responseText;
        xmlPars.loadXML(textXML);
      
        var esito  = xmlPars.documentElement.getAttribute("esito");
        if(esito=="success") 
        { var desComune = xmlPars.getElementsByTagName("nome")[0].text;
          var codComune = xmlPars.getElementsByTagName("codice")[0].text;
          var CAP       = xmlPars.getElementsByTagName("cap")[0].text;
          var provincia = xmlPars.getElementsByTagName("provincia")[0].text;
          var comune = "comune="+desComune+"::esito="+esito+" ("+provincia+")"+"; cod="+codComune+" CAP_"+CAP;
          codcomune.value  = codComune;
          nomecomune.value = desComune;
          if (retcap!=null){ retcap.value= CAP; }
        } 
        else if(esito=="error") 
        { alert("I dati inseriti relativi al comune \nsono errati o incompleti.");
          return false
        }
      }
      //Sezione div per testare la function
      //var div = document.getElementById("divDisplay");
      //div.innerHTML=comune;
    
    }//if()
    //else alert("Il comune è già inserito");
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