<SCRIPT TYPE="text/javascript">
	function apriReferentePermessoSoggiorno(){
		var isOKPrg = false;
   		var f = "AdapterHTTP?PAGE=PermSoggReferentePerMovimentiPage";
   	  	var objPrg = document.Frm1.PRGMOVIMENTO;
   	  	if (objPrg != undefined) {
   	  		if(document.Frm1.PRGMOVIMENTO.value != ''){
   	  			f = f + "&PRGMOVIMENTO="+document.Frm1.PRGMOVIMENTO.value;
   	  			isOKPrg = true; 
   	  		}
   	  	}
   	  	if (!isOKPrg) {
   	  		objPrg = document.Frm1.PRGMOVIMENTOAPP;
   	  		if (objPrg != undefined) {
       	  		if(document.Frm1.PRGMOVIMENTOAPP.value != ''){
       	  			f = f + "&PRGMOVIMENTOAPP="+document.Frm1.PRGMOVIMENTOAPP.value; 
       	  			isOKPrg = true;
       	  		}
       		}
   	  	
   	  	}
   	  	if (!isOKPrg) {
   	  		objPrg = document.Frm1.PRGMOVIMENTORETT;
   			if (objPrg != undefined) {
       	  		if(document.Frm1.PRGMOVIMENTORETT.value != ''){
       	  			f = f + "&PRGMOVIMENTORETT="+document.Frm1.PRGMOVIMENTORETT.value;
       	  			isOKPrg = true;
       	  		}
       		}	
   	  	}
   	  	var t = "_blank";
      	var feat = "toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,width=800,height=400,top=75,left=100";
      	opened = window.open(f, t, feat);
  	}
</SCRIPT>