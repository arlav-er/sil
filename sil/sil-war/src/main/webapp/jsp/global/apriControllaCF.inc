<script language="JavaScript">

function apriControllaCF(checkCF,nomeCF,cognomeCF,sessoCF,datNascCF,comNascCF,codComNascCF,CF,flagCF,funz)
{ 
  var comune = comNascCF.value;
  
   if(checkCF && (nomeCF.value=="" || cognomeCF.value=="" || sessoCF.value=="" || datNascCF.value=="" || comune=="" || prov==""))
   { alert("Tutti i campi relativi a nome, cognome, sesso,\ncomune provincia e data di nascita\nsono obbligatori al fine del controllo del codice fiscale");
     return;
   } 

   if(!checkCF && CF.value=="")
   { alert("Il codice fiscale non è inserito:\nimpossibile verificare o ricavere i dati anagrafici");
     return;
   }

  var prov = "";
  if(comune != null && comune != "")
  { //Ricavo la provincia dal campo comune (Se inserita)
    var regExp = /\((\D{2})\)/g;
    var matchAr = comune.match(regExp);
    if (matchAr != null && matchAr.length ==1) 
    { prov = matchAr[0];
      prov = prov.substring(1,3);
      var idx = comune.search(regExp);
      comune = comune.substr(0,idx);
    }
    else if(checkCF)  
    { alert("Il campo comune non presenta la provincia\no non è inserita correttamente\ninserire la provincia tra parentesi es:\n\n      BOLOGNA (BO)");
      return;
    }
   }//if(comune)
   //alert("Comune:"+comune+":  prov:"+prov);

   if (datNascCF.value != null &&  datNascCF.value != "") 
   { var dataOK = checkFormatDate(datNascCF);
     if(!dataOK) 
     { alert("La data non è corretta. Inserire gg/mm/aaaa");
       return;
     }
   }
   
    var urlo = "AdapterHTTP?PAGE=ControllaCodiceFiscalePage";
    urlo += "&checkCF="+checkCF;
    urlo += "&nome="+nomeCF.value;
    urlo += "&cognome="+cognomeCF.value;
    urlo += "&sesso="+sessoCF.value;
    urlo += "&datNasc="+datNascCF.value;
    urlo += "&comNasc="+comune;
    urlo += "&provNasc="+prov;
    urlo += "&CF="+CF.value;

    urlo += "&campoNome="+nomeCF.name;
    urlo += "&campoCognome="+cognomeCF.name;
    urlo += "&campoSesso="+sessoCF.name;
    urlo += "&campoDatNasc="+datNascCF.name;
    urlo += "&campoComNasc="+comNascCF.name;
    urlo += "&campoCodComNasc="+codComNascCF.name;
    urlo += "&campoCF="+CF.name;
    urlo += "&campoFlagOkCF="+flagCF.name;

    var titolo = "ControllaCF";
    var w=750; var l=((screen.availWidth)-w)/2;
    var h=300; var t=((screen.availHeight)-h)/2;
    //var feat = "status=YES,location=YES,toolbar=NO,scrollbars=YES,resizable=YES,height="+h+",width="+w+",top="+t+",left="+l;
    var feat = "status=NO,location=NO,toolbar=NO,scrollbars=YES,resizable=NO,height="+h+",width="+w+",top="+t+",left="+l;
    eval(funz);
    window.open(urlo, titolo, feat);
    
}
</script>
