    var opened;
    
    function apriSelezionaSoggetto(soggetto, funzionediaggiornamento, prgAzienda, prgUnita, cdnLavoratore) {
        var f = "AdapterHTTP?PAGE=MovimentiSelezionaSoggettoPage&MOV_SOGG=" + soggetto + "&AGG_FUNZ=" + funzionediaggiornamento + "&CDNFUNZIONE=" + _funzione;
        
        for (i=0;i<document.forms[0].elements.length;i++) {
        	var elem = document.forms[0].elements[i];
        	if (elem.name == "CURRENTCONTEXT") {
        		f = f + "&context=" +  elem.name.value;	
        	}
        	else { 
	        	if (elem.name == "CODTIPOTRASF") {
	        		f = f + "&CODTIPOTRASF=" +  elem.name.value;	
	        	}
	       } 
        }
        
        if (prgAzienda != '' && (typeof prgAzienda) != "undefined") {f = f + "&PRGAZ=" + prgAzienda;}
        if (prgUnita != '' && (typeof prgUnita) != "undefined") {f = f + "&PRGUAZ=" + prgUnita;}
        if (cdnLavoratore != '' && (typeof cdnLavoratore) != "undefined") {f = f + "&CDNLAV=" + cdnLavoratore;}
        var t = "_blank"; 
        var feat = "toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,width=750,height=650,top=30,left=180";
        opened = window.open(f, t, feat);
    }
    
    
    var openedSP;
    
    function apriSelezionaSoggettoSP(soggetto, funzionediaggiornamento, prgAzienda, prgUnita, cdnLavoratore) {
        var f = "AdapterHTTP?PAGE=MovimentiSelezionaSoggettoPage&MOV_SOGG=" + soggetto + "&AGG_FUNZ=" + funzionediaggiornamento + "&CDNFUNZIONE=" + _funzione;
        
        for (i=0;i<document.forms[0].elements.length;i++) {
        	var elem = document.forms[0].elements[i];
        	if (elem.name == "CURRENTCONTEXT") {
        		f = f + "&context=" +  elem.name.value;	
        	}
        	else { 
	        	if (elem.name == "CODTIPOTRASF") {
	        		f = f + "&CODTIPOTRASF=" +  elem.name.value;	
	        	}
	       } 
        }
        
        if (prgAzienda != '' && (typeof prgAzienda) != "undefined") {f = f + "&PRGAZ=" + prgAzienda;}
        if (prgUnita != '' && (typeof prgUnita) != "undefined") {f = f + "&PRGUAZ=" + prgUnita;}
        if (cdnLavoratore != '' && (typeof cdnLavoratore) != "undefined") {f = f + "&CDNLAV=" + cdnLavoratore;}
        var t = "_blank"; 
        var feat = "toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,width=750,height=650,top=30,left=180";
        openedSP = window.open(f, t, feat);
    }
    
    
    
    

    function apriUnitaAziendale(prgAz,prgUn,cdnFunz,prgMov){
        var f = "AdapterHTTP?PAGE=IdoUnitaAziendaPage&PRGAZIENDA=" + prgAz + "&PRGUNITA=" + prgUn + "&PRGMOVIMENTO=" + prgMov + "&CDNFUNZIONE=" + cdnFunz;
        
        for (i=0;i<document.forms[0].elements.length;i++) {
        	var elem = document.forms[0].elements[i];
        	if (elem.name == "CURRENTCONTEXT") {
        		f = f + "&context=" +  elem.name.value;	
        	}
        	else { 
	        	if (elem.name == "CODTIPOTRASF") {
	        		f = f + "&CODTIPOTRASF=" +  elem.name.value;	
	        	}
	       }
        }
        
        var t = "_blank";
        var feat = "toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,width=800,height=600,top=75,left=100";
        opened = window.open(f, t, feat);
        window.openPopUpAzienda = true;
    }


