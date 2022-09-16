<script language="javascript">
<!--
 
  var canMod = false;  
 	<%if (canModify) {%>
	    canMod=true;
	<%}%>
  var consultaProf = false;  
 	<%if (consultaProfilo) {%>
	    consultaProf=true;
	<%}%>
	
 	var personalitaOk = true;
	var msgPers = '';
	<%if (numRisPers ==0) {%>
	    personalitaOk=false;
	    msgPers  =  'Il questionatio sulla personalità non è stato compilato. ';
 		msgPers +=  'Confermando il calcolo e il salvataggio del profilo non sarà più possibile rispondere ' ;
 		msgPers +=  'alle domande utili allo score sulla personalità. Vuoi procedere?';	
 	<%}else if(numRisPers <10){%>
	 personalitaOk=false;
	    msgPers  =  'Il questionatio sulla personalità  è stato solo parzialmente compilato. ';
 		msgPers +=  'Confermando il calcolo del profilo non sarà più possibile rispondere ' ;
 		msgPers +=  'alle domande restanti utili al completamento dello score sulla personalità. Vuoi procedere?';
	<%}%>
 
function toggleRadio(isOn, name){
	var formElements = document.Frm1;
	for (i = 0; i < formElements.length; i++) { 
		if(typeof(formElements[i]) != "undefined"){
			var codiceDomanda = formElements[i].name;
			if(codiceDomanda == name){
				if(isOn){
					formElements[i].disabled = false;
				}else{
					formElements[i].disabled = true;
				}	
			}
		}
	}
}
function gestisciAltro(){
	var d07 = document.getElementById("D07_4_altro");
	if(d07!==null){
		d07.style.display = "";
	}
 }
function gestisciD08(isOn){
	var d08Link1= document.getElementById("D08_link_1");
	var d08Link2= document.getElementById("D08_link_2");
	if(d08Link1!==null && d08Link2!==null && isOn){
		d08Link1.style.pointerEvents = "none";
		d08Link2.style.pointerEvents = "none";
	}else if(d08Link1!==null && d08Link2!==null && !isOn){
		d08Link1.style.pointerEvents = "";
		d08Link2.style.pointerEvents = "";
	}
 }
function gestisciD17(elem, skipValue){
	if(typeof(elem) != "undefined" && elem.name=='D17'){
		if(elem.value=='D17_1'){			
			document.Frm1.D18[0].checked = true;
			document.Frm1.D18[1].checked = false;
			document.Frm1.D18[0].style ="pointer-events: none; opacity: 0.5;";
			document.Frm1.D18[1].style ="pointer-events: none; opacity: 0.5;";
		}else if( elem.value=='D17_2'){	  
			if(skipValue =='noSkip'){
				document.Frm1.D18[0].checked = false;
				document.Frm1.D18[1].checked = false;
			}
			document.Frm1.D18[0].style ="pointer-events: auto; opacity: 1;";
			document.Frm1.D18[1].style ="pointer-events: auto; opacity: 1;";
		}
	}
}
function gestisciD20(elem){
	if(typeof(elem) != "undefined" && elem.name=='D20'){
		if(elem.value=='D20_2'){
			document.Frm1.D21[0].style ="pointer-events: auto; opacity: 1;";
			document.Frm1.D21[1].style ="pointer-events: auto; opacity: 1;";
		}else if( elem.value=='D20_1'){
			document.Frm1.D21[0].checked = false;
			document.Frm1.D21[1].checked = false;
			document.Frm1.D21[0].style ="pointer-events: none; opacity:  0.5;";
			document.Frm1.D21[1].style ="pointer-events: none; opacity:  0.5;";
		}
	}
}
function gestisciD45(elem){
	if(typeof(elem) != "undefined" && elem.name=='D45'){
	 
		if(  elem.value=='D45_1'){

			document.Frm1.D22[0].disabled = false;
			document.Frm1.D22[1].disabled = false;
			document.Frm1.D23[0].disabled = false;
			document.Frm1.D23[1].disabled = false;
			document.Frm1.D24[0].disabled = false;
			document.Frm1.D24[1].disabled = false;
			document.Frm1.D25[0].disabled = false;
			document.Frm1.D25[1].disabled = false;
			document.Frm1.D26[0].disabled = false;
			document.Frm1.D26[1].disabled = false;
			document.Frm1.D27[0].disabled = false;
			document.Frm1.D27[1].disabled = false;
			document.Frm1.D28[0].disabled = false;
			document.Frm1.D28[1].disabled = false;
			document.Frm1.D29[0].disabled = false;
			document.Frm1.D29[1].disabled = false;
		}else if( elem.value=='D45_2'){

			document.Frm1.D22[0].checked = false;
			document.Frm1.D22[1].checked = false;
			document.Frm1.D23[0].checked = false;
			document.Frm1.D23[1].checked = false;
			document.Frm1.D24[0].checked = false;
			document.Frm1.D24[1].checked = false;
			document.Frm1.D25[0].checked = false;
			document.Frm1.D25[1].checked = false;
			document.Frm1.D26[0].checked = false;
			document.Frm1.D26[1].checked = false;
			document.Frm1.D27[0].checked = false;
			document.Frm1.D27[1].checked = false;
			document.Frm1.D28[0].checked = false;
			document.Frm1.D28[1].checked = false;
			document.Frm1.D29[0].checked = false;
			document.Frm1.D29[1].checked = false;
			document.Frm1.D22[0].disabled = true;
			document.Frm1.D22[1].disabled = true;
			document.Frm1.D23[0].disabled = true;
			document.Frm1.D23[1].disabled = true;
			document.Frm1.D24[0].disabled = true;
			document.Frm1.D24[1].disabled = true;
			document.Frm1.D25[0].disabled = true;
			document.Frm1.D25[1].disabled = true;
			document.Frm1.D26[0].disabled = true;
			document.Frm1.D26[1].disabled = true;
			document.Frm1.D27[0].disabled = true;
			document.Frm1.D27[1].disabled = true;
			document.Frm1.D28[0].disabled = true;
			document.Frm1.D28[1].disabled = true;
			document.Frm1.D29[0].disabled = true;
			document.Frm1.D29[1].disabled = true;
		} 
	}
} 

function gestisciViewDomande(){
	rinfresca();
	if(canMod){
		if(consultaProf ){ 
			var formElements = document.Frm1;
			for (i = 0; i < formElements.length; i++) { 
				if(typeof(formElements[i]) != "undefined"){
					var codiceDomanda = formElements[i].name;
					console.log(codiceDomanda);
					if(codiceDomanda == 'D20'){
						if(formElements[i].checked){
							gestisciD20(formElements[i]);
						}
						
					}
					if(codiceDomanda == 'D45'){
					 	if(formElements[i].checked){
							gestisciD45(formElements[i]);
						}
						 
					}
					if(codiceDomanda == 'D17'){
						if(formElements[i].checked){
							gestisciD17(formElements[i],'skip');
						}
					 
					}
				}
			}
		} 
	}
}
function onChangeRadio(elem){
 	gestisciD20(elem);
	gestisciD45(elem);
	gestisciD17(elem, 'noSkip');
	fieldChanged();
}

//qualifica
function nuovaMansione()
{
  if (isInSubmit()) return;

  var s= "AdapterHTTP?PAGE=NuovaAssMansioniPage";
  s += "&MODULE=M_NuovaAssMansioni";
  s += "&CDNFUNZIONE=<%=_funzione%>";

  setWindowLocation(s);
}
function getSelectedChbox(frm) {
  var selchbox = [];// array that will store the value of selected checkboxes
  // gets all the input tags in frm, and their number
  var inpfields = frm.getElementsByTagName('input');
  var nr_inpfields = inpfields.length;
  // traverse the inpfields elements, and adds the value of selected (checked) checkbox in selchbox
  for(var i=0; i<nr_inpfields; i++) {
    if(inpfields[i].type == 'checkbox' && inpfields[i].checked == true) selchbox.push(inpfields[i].value);
  }
  return selchbox;
}   
function checkCampiAggiuntiva(){
	var f = document.Frm1;
	if(typeof(f.D44_chb) != "undefined"){
		var arrChbox = getSelectedChbox(f);
		document.getElementById("D44").value = arrChbox.toString();
		 
		return true;
	}else
	
		return true;
}
function okCalcolaSalva(){
	var msg = "Proseguendo con il calcolo e il salvataggio non sarà più possibile effettuare nuovi calcoli e/o modifiche al profilo. Proseguire?";
				
	if (confirm(msg)) {
 		return true;
	}else{
		return false;
	}
}
function checkChangedCampi(){
	var tipoOp = document.Frm1.TIPO_OPERAZIONE.value;
	if (tipoOp != "SALVA_FLGLINGUA") {
		if (flagChanged==true) {
			if (!confirm("I dati sono cambiati.\r\nProcedere lo stesso ?")) {
				return false;
			}
		}
	}
	return true;
}
function checkSumbitCalcolo(){
			var tipoOp = document.Frm1.TIPO_OPERAZIONE.value;
			
			if(tipoOp == 'CALCOLA_SALVA'){
   			 
   			 		if(personalitaOk){
							return okCalcolaSalva();
   			 		}else{
   			 			if (confirm(msgPers)) {
 							return okCalcolaSalva();
						}else{
							return false;
						}
   			 		}
			}else{
 				return true;
			}
		}
function onClickCalcola(tipoOp){
   		document.Frm1.TIPO_OPERAZIONE.value= tipoOp;
}
function calcolaScore(){
   		 
   		var urlpage="AdapterHTTP?";
   		urlpage+="CDNFUNZIONE=<%=_funzione%>&PAGE=ScorePersonalitaPage&TIPO_OPERAZIONE=CALCOLA_PERSONALITA&PRGLAVORATOREPROFILO=<%=prgLavoratoreProfilo%>";
		urlpage +="&CDNLAVORATORE=<%=cdnLavoratore%>";
  		window.open(urlpage,'Dettaglio Score Personalità','toolbar=NO,statusbar=YES,width=700,height=250,top=50,left=100,scrollbars=NO,resizable=YES');
}

-->
</script>