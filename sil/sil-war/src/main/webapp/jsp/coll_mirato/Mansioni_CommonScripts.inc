<SCRIPT TYPE="text/javascript">

function selectMansione_onClick(codMansione, codMansioneHid, descMansione, strTipoMansione) {	

    flgFrequente=Frm1.flgFrequente;
    paramFrequente=(flgFrequente.checked)?"&flgFrequente=true":"";
    if (codMansione.value==""){

      descMansione.value="";
      strTipoMansione.value="";      
    }
    else if (codMansione.value!=codMansioneHid.value){
       
        window.open("AdapterHTTP?PAGE=RicercaMansionePage&codMansione="+codMansione.value+paramFrequente, "Mansioni", 'toolbar=0, scrollbars=1');     
    }
  }

function ricercaAvanzataMansioni() {

  var w=800; var l=((screen.availWidth)-w)/2;
  var h=500; var t=((screen.availHeight)-h)/2;
  //var feat = "status=YES,location=YES,toolbar=NO,scrollbars=YES,resizable=YES,height="+h+",width="+w+",top="+t+",left="+l;
  var feat = "status=NO,location=NO,toolbar=NO,scrollbars=YES,resizable=NO,height="+h+",width="+w+",top="+t+",left="+l;
  window.open("AdapterHTTP?PAGE=RicercaMansioneAvanzataPage", "Mansioni", feat);
  
  
  //window.open("AdapterHTTP?PAGE=RicercaMansioneAvanzataPage", "Mansioni", 'toolbar=0, scrollbars=1');
}

 function toggleVisQualifica() {
    var divQualifica = document.getElementById("qualifica");
    if (divQualifica != null)
    {
      divQualifica.style.display= (document.Frm1.flgPubblica.value=="S")?"":"none";
    }
  }

</SCRIPT>
