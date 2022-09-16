 <SCRIPT TYPE="text/javascript">
<!--

  //dichiaro gli array per riempire la mia supercombo
  // var CCNL_tipo=new Array();
  // var CCNL_cod=new Array();
  // var CCNL_des=new Array();
  <%/*     for(int i=0; i<CCNLRows.size(); i++)  { 
              row_CCNL = (SourceBean) CCNLRows.elementAt(i);
              out.print("CCNL_tipo["+i+"]=\""+ row_CCNL.getAttribute("TIPO").toString()+"\";\n");
              out.print("CCNL_cod["+i+"]=\""+ row_CCNL.getAttribute("CODICE").toString()+"\";\n");
              out.print("CCNL_des["+i+"]=\""+ row_CCNL.getAttribute("DESCRIZIONE").toString()+"\";\n");              
        } 
    */
  %>
-->
  </SCRIPT>



    
  <SCRIPT TYPE="text/javascript">

<!--

flgAutonomo_array=new Array();

// Rilevazione Modifiche da parte dell'utente
  var flagChanged = false;
        
  function fieldChanged() {
    
  }


function caricaCCNL(codTipoCCNL) {
   /*i=0;
   j=1; //salta una riga (aggiunge un blank)

   while (document.Frm1.codCCNL.options.length>0) {
        document.Frm1.codCCNL.options[0]=null;
    }
    for (i=0; i<CCNL_tipo.length ;i++) {
       if (CCNL_tipo[i]==codTipoCCNL) {
          document.Frm1.codCCNL.options[j]=new Option(CCNL_des[i], CCNL_cod[i]);
          //setto il selected
          document.Frm1.codCCNL.options[j].selected=(CCNL_cod[i]=="<%=codCCNL%>");
           j++;
       }
    } 
    */
}

function onOff(value){
var div1 = document.getElementById("scelta_mansione");
    if (document.Frm1.MansioniInserite.value!="-1") {
        document.Frm1.CODMANSIONE.value=value;
        document.Frm1.DESCMANSIONE.value="---";
        div1.style.display="none";
        document.Frm1.InserimentoMansione.value=0;
    }else {
        document.Frm1.FLGESPERIENZA.value="S";
        document.Frm1.CODTIPOMANSIONE.value="";
        document.Frm1.DESCMANSIONE.value="";
        div1.style.display="";
        document.Frm1.InserimentoMansione.value=1;
    }

}


/* obsoleta
function TipiMansione_onClick(codTipoMansione) {	
    if (codTipoMansione.value==""){
      codMansione.value="";
      strMansione.value="";
    }
    else {
      window.open("AdapterHTTP?PAGE=CurrAlberoMansioniPage&padre="+codTipoMansione.value, "Mansioni", 'toolbar=0, scrollbars=1');
    }
  }
*/

function selectMansione_onClick(codMansione, codMansioneHid, descMansione, strTipoMansione) {	

    if (codMansione.value==""){
    
      descMansione.value="";
      strTipoMansione.value="";      
    }
    else if (codMansione.value!=codMansioneHid.value){
    window.open("AdapterHTTP?PAGE=RicercaMansionePage&codMansione="+codMansione.value, "Mansioni", 'toolbar=0, scrollbars=1');     
    }
  }

function ricercaAvanzataMansioni() {
  window.open("AdapterHTTP?PAGE=RicercaMansioneAvanzataPage", "Mansioni", 'toolbar=0, scrollbars=1,height=600, width=800');
}


/* obsoleta
function Tipi_onChange() {

    // Cambiando tipo il dettaglio non corrisponde più
    // quindi azzero la scelta fatta
    Frm1.CODMANSIONE.value= "";
    Frm1.DESCMANSIONE.value= "";
  }
*/

 function selectATECO_onClick(codAteco, codAtecoHid, strAteco, strTipoAteco) {	

    if (codAteco.value==""){
      strAteco.value="";
      strTipoAteco.value=""      
    }
    else if (codAteco.value!=codAtecoHid.value){
    window.open("AdapterHTTP?PAGE=RicercaAtecoPage&codAteco="+codAteco.value, "Attività", 'toolbar=0, scrollbars=1');     
    }
  }

function codComuneUpperCase(inputName){

  var ctrlObj = eval("document.forms[0]." + inputName);
  eval("document.forms[0]."+inputName+".value=document.forms[0]."+inputName+".value.toUpperCase();")
	return true;
}


function toggleVisMotivazioni(codMvCessazione){
  
  var divMotivazioni = document.getElementById("motivazioni");
  if (codMvCessazione.value!="") {
      divMotivazioni.style.display="";
  } else {
    divMotivazioni.style.display="none";
  }
    
}

function is_autonomo(codContratto){
 var autonomo=false;
 for (i=0; (i<flgAutonomo_array.length) && (!autonomo); i++) {
      if (flgAutonomo_array[i]==codContratto.value) {autonomo=true};
  }
  
 return autonomo;
}


function toggleVisContratto(codContratto){

    var vis_ccnl=false;
    var vis_livello=false;
    var vis_formprof=false;
    var vis_completato=false;
    var divCCNL=document.getElementById("vis_ccnl");
    var divLivello=document.getElementById("vis_livello");
    var divFormProf=document.getElementById("vis_formprof");
    var divCompletato=document.getElementById("vis_completato");

    if (!is_autonomo(codContratto)) {
        divCCNL.style.display="";
      //  divLivello.style.display="";
        if (codContratto=="TI") {
          divFormProf.style.display="";
          divCompletato.style.display="";
        }
    } else {
        divCCNL.style.display="none";
     //   divLivello.style.display="none";
        divFormProf.style.display="none";
        divCompletato.style.display="none";
    }

}

function checkAnnoInizioFine(inputName) {

 var ok=true;
 if (document.forms[0].numAnnoFine.value!="" ) {
  if (document.forms[0].numAnnoInizio.value>document.forms[0].numAnnoFine.value) {
      alert("L'anno di inizio deve essere minore o uguale all'anno di fine");
      document.forms[0].numAnnoInizio.focus();
      ok=false;
  }
  }

  return ok;
}


function checkMeseInizioFine(inputName) {

  var ok=true;
  var annoInizio=document.forms[0].numAnnoInizio.value;
  var annoFine=document.forms[0].numAnnoFine.value;
  var meseInizio=document.forms[0].numMeseInizio.value;
  var meseFine=document.forms[0].numMeseFine.value;
  
  if ((annoInizio!="" && annoFine!="") && (annoInizio==annoFine)) {
    if ((meseInizio!="" && meseFine!="")&& (meseInizio>meseFine)){
      alert("Il mese di inizio deve essere minore o uguale al mese di fine");
      document.forms[0].numMeseInizio.focus();
      ok=false;
    }
  }
  return ok;
}
function controllaDate() {
  var annoInizio=document.forms[0].numAnnoInizio.value;
  var annoFine=document.forms[0].numAnnoFine.value;
  var meseInizio=document.forms[0].numMeseInizio.value;
  var meseFine=document.forms[0].numMeseFine.value;
  var mesi=document.forms[0].numMesi.value;

  //rolfini 29/07: a seguito dei cambiamenti del tag form
  //è necessario che le funzioni chiamate all'interno della submit
  //restituiscano un boolean
  return true;

}

function calcolaMesi(inputName) {

  var annoInizio=document.forms[0].numAnnoInizio.value;
  var annoFine=document.forms[0].numAnnoFine.value;
  var meseInizio=document.forms[0].numMeseInizio.value;
  var meseFine=document.forms[0].numMeseFine.value;
  //var mesi=document.forms[0].numMesi.value;
  var mesi="";
  
  if (!(annoInizio>=1900 && annoInizio<=2100)){
	alert ("L'anno di inizio deve essere compreso tra 1900 e 2100");
	return false;
  }
  if (!(annoFine>=1900 && annoFine<=2100) && annoFine!=""){
	alert ("L'anno di fine deve essere compreso tra 1900 e 2100");
	return false;
  }
  if (!(meseInizio>=1 && meseInizio<=12) && meseInizio!=""){
	alert ("Il mese di inizio deve essere compreso tra 1 e 12");
	return false;
  }
  if (!(meseFine>=1 && meseFine<=12) && meseFine!=""){
	alert ("Il mese di fine deve essere compreso tra 1 e 12");
	return false;
  }
  if (!(mesi>=0)){
	alert ("La durata in mesi non deve essere negativa");
	return false;
  }

  if (annoInizio!="" && annoFine!="" && meseInizio!="" &&  meseFine!="") {
        var dataInizio=new Date();
        dataInizio.setFullYear(annoInizio, meseInizio-1, 1);
        var dataFine=new Date();
        dataFine.setFullYear(annoFine, meseFine-1, 1);
        var diff=dataFine - dataInizio //differenza in millisecondi
        mesi=Math.round(diff/2592000000);  //diff fratto i millisecondi di un mese (!)      
  }
        

  document.forms[0].numMesi.value=mesi;


  
  return true;
}

function ricercaAziende() {
  apriSelezionaSoggetto('Aziende', 'aggiornaAzienda','','','');
  //window.open("AdapterHTTP?PAGE=CurrEspCercaAziendaPage", "Aziende", 'toolbar=0, scrollbars=0, width=400,height=300,top=100,left=100');
}


function ricercaAvanzataAteco() {
  window.open("AdapterHTTP?PAGE=RicercaAtecoAvanzataPage", "Attività", 'toolbar=0, scrollbars=1');
}

function ricercaAvanzataCCNL() {
  window.open("AdapterHTTP?PAGE=RicercaCCNLAvanzataPage", "CCNL", 'toolbar=0, scrollbars=1, height=300, width=550');
}

function codCCNLUpperCase(inputName){

  var ctrlObj = eval("document.forms[0]." + inputName);
  eval("document.forms[0]."+inputName+".value=document.forms[0]."+inputName+".value.toUpperCase();");
	return true;
}
-->


  </SCRIPT>