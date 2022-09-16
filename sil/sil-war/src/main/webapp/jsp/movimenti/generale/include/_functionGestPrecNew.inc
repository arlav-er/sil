<SCRIPT language="javascript">
<%if(!precedente){%>
    //Gestisce il movimento precedente se non specificato in fase di inserimento
   	codTipoMovPrec = '';
	function gestisciPrecedente() {
		var codtipomov = document.Frm1.CODTIPOMOV.value;
		//Cerco di associare un movimento solo se sono in Trasf/Pro/Cess, 
      	//altrimenti resetto il precedente
      	if (codtipomov != 'AVV' && codtipomov != '') {
        	codTipoMovPrec = codtipomov;
        	//Guardo se ho gli elementi per la ricerca
        	var prgAzienda = document.Frm1.PRGAZIENDA.value;
        	var prgUnita = document.Frm1.PRGUNITA.value;
        	var cdnLav = document.Frm1.CDNLAVORATORE.value;
        	var flagTrasferimento = document.Frm1.FLGTRASFER.value;
        	var datFineMov = document.Frm1.datFineMov.value; 
        	if (cdnLav == '') {
          		alert('Occorre selezionare prima il lavoratore');
          		document.Frm1.CODTIPOMOV.value = '';
        	} else {
        		if (codtipomov == 'TRA' && flagTrasferimento == 'S') {
        			var prgMovAppTrasf = document.Frm1.PRGMOVIMENTOAPP.value;
        			cercaPrecedenteTrasferimento(prgMovAppTrasf, cdnLav, codtipomov);
        		}
        		else {
          			cercaPrecedente(prgAzienda, prgUnita, cdnLav, codtipomov, datFineMov);
          		}	
        	}         
      	} else {
        	document.Frm1.PRGMOVIMENTOPREC.value = '';
        	document.Frm1.CODTIPOMOVPREC.value = '';
       	 	document.Frm1.NUMKLOMOVPREC.value = '';
        	//Da sostituire con la funz che gestisce il prec e succ
        	//gestisciLinguettaPrecedente('');
        	document.Frm1.COLLEGATO.value = 'nessuno'; 
        	precedente = false;
      	}
	}

	
	
    //Cerca il movimento precedente, se ne trova più di uno mostra la lista da cui scegliere
    function cercaPrecedente(prgAzienda, prgUnita, cdnLavoratore, codTipoMov, datFineMov) {
      //Salvo il prgAzienda e prgUnita attuali
      prgAziendaPrec = prgAzienda;
      prgUnitaPrec = prgUnita;
      //Apre la finestra di ricerca
      var f = "AdapterHTTP?PAGE=SelezionaMovimentoCollegatoPage&PRGAZIENDA=" + prgAzienda + 
      "&PRGUNITA=" + prgUnita + "&CDNLAVORATORE=" + cdnLavoratore + "&CODTIPOMOV=" + codTipoMov + 
      "&CODTIPOASS=" + document.Frm1.codTipoAss.value + "&DATFINEMOV=" + datFineMov +
      "&updateFunctionName=aggiornaPrecedente&daMovimentiNew=S&context=" + document.Frm1.CURRENTCONTEXT.value;
      var t = "_blank";
      var feat = "toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,width=750,height=400,top=100,left=100";
      opened = window.open(f, t, feat);      
    }
    
    function cercaPrecedenteTrasferimento(prgMovimentoApp, cdnLavoratore, codTipoMov, datFineMov) {
    	//Apre la finestra di ricerca
      	var f = "AdapterHTTP?PAGE=SelezionaMovimentoCollegatoPage&CDNLAVORATORE=" + cdnLavoratore + 
      	"&PRGMOVIMENTOAPP=" + prgMovimentoApp + "&TRASFERIMENTOAZIENDA=true&daMovimentiNew=S" +
      	"&CODTIPOASS=" + document.Frm1.codTipoAss.value +
      	"&CODTIPOMOV=" + codTipoMov + "&DATFINEMOV=" + datFineMov + "&updateFunctionName=aggiornaPrecedenteTrasferimento&context=" + document.Frm1.CURRENTCONTEXT.value;
      	var t = "_blank";
      	var feat = "toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,width=750,height=400,top=100,left=100";
      	opened = window.open(f, t, feat);
    }
    
    function aggiornaPrecedenteTrasferimento(prgMovPrec, codTipoMovPrec, numKloMovPrec, dataInizioAvv, dataInizioMovPrec, codMonoTempoAvv, dataFineMovPrec, codTipoAss, insScollegato, prgAzienda, prgUnita) {
    	visualizzaObbli.style.display = "none";
    	if (document.Frm1.CODTIPOMOV.value == 'TRA' && contesto == 'valida') {
	      	//Guardo se il movimento precedente è stato scelto
			if (prgMovPrec == '') {
				//Non è stato scelto
				document.Frm1.DATAINIZIOAVV.value = dataInizioAvv;
	        	document.Frm1.DATINIZIOMOVPREC.value = dataInizioMovPrec;
		        document.Frm1.CODMONOTEMPOAVV.value = codMonoTempoAvv;
		        document.Frm1.DATFINEMOVPREC.value = dataFineMovPrec;
		     	document.Frm1.PRGMOVIMENTOPREC.value = prgMovPrec;
		        document.Frm1.CODTIPOMOVPREC.value = codTipoMovPrec;
		        document.Frm1.NUMKLOMOVPREC.value = eval(numKloMovPrec) + 1;
		        
		        var codMonoTempoEdit = document.getElementById("codMonoTempoEdit");
	    		var codMonoTempoNoEdit = document.getElementById("codMonoTempoNoEdit");
	    		var datascadenza = document.getElementById("datascadenza");
	    		var datascadenzaNoEdit = document.getElementById("datascadenzaNoEdit");
	    		var calcDataFine = document.getElementById("calcDataFine");
		        
		        if (!insScollegato) {
			        codMonoTempoEdit.style.display = "none";
					codMonoTempoNoEdit.style.display = "";
					document.Frm1.codMonoTempoNoEdit.value = 'Indeterminato';
					document.Frm1.codMonoTempo.value = 'I';
					document.Frm1.datFineMov.value = "";
	    			//scadenza.style.display = "none";
	    			//datascadenza.style.display = "none";
	    			//calcDataFine.style.display = "none";
	    		}	
    			
    			var dati = document.getElementById("campiAvv");
	        	var obj = document.getElementById("campiMov");
	        	var aziTra = document.getElementById("datiAziTra");
	        	var sezAziTra = document.getElementById("sezAziTra");          
		        if (!insScollegato) {
		        	document.Frm1.CODTIPOMOV.value = '';
				  	dati.style.display = "none";
				  	obj.style.display = "none";
				  	aziTra.style.display = "none";
				  	sezAziTra.style.display = "none";
		        }
		        document.Frm1.COLLEGATO.value = 'nessuno'; 
		        precedente = false;
		        inizializzaCollegati('', '');
		        var infoAvviamento = document.getElementById("infoAvv");        
		        infoAvviamento.innerHTML = "Inf. Valide in fase di avviamento:";
	      	} 
	      	else {
	        	//E' stato scelto, setto i dati con la XMLHTTPRequest
	        	visualizzaObbli.style.display = "";      
	      		settaDatiPrecedente(prgMovPrec);   	
		  	}
	  	}
	}
 
    //Aggiorna il prgMovimentoPrec quando si torna dalla lista 
    //e se gli argomenti sono tutti nulli disabilita il collegato.
    function aggiornaPrecedente(prgMovPrec, codTipoMovPrec, numKloMovPrec, dataInizioAvv, dataInizioMovPrec, codMonoTempoAvv, dataFineMovPrec, codTipoAss, insScollegato, prgAzienda, prgUnita,daMovimentiNew) 
    {
      //distinguo il funzionamento per i due contesti 
      //(quello di inserimento è il nuovo, la validazione andrà allineata a questo)
      if (contesto == 'inserisci' || contesto=='valida') {
	      //Guardo se il movimento precedente è stato scelto
	      if (prgMovPrec == '') {
	      	//Non è stato scelto
	      	var datInizioMovEdit = document.getElementById("datInizioMovEdit");
	      	var datInizioMovNoEdit = document.getElementById("datInizioMovNoEdit");
		  	if( (document.Frm1.CODTIPOMOV.value == "AVV") || (contesto == 'inserisci' && document.Frm1.CODTIPOMOV.value == "CES")) {
	      			datInizioMovNoEdit.style.display = "none";
					datInizioMovEdit.style.display = "";
		  	} else {
	      			datInizioMovNoEdit.style.display = "";
					datInizioMovEdit.style.display = "none";
	      	}
	      	
	      	var codMonoTempoEdit = document.getElementById("codMonoTempoEdit");
	    	var codMonoTempoNoEdit = document.getElementById("codMonoTempoNoEdit");
	    	var datascadenza = document.getElementById("datascadenza");
	    	var datascadenzaNoEdit = document.getElementById("datascadenzaNoEdit");
	    	var calcDataFine = document.getElementById("calcDataFine");
	    	
	    	if(document.Frm1.CODTIPOMOV.value == "CES") {
	    		codMonoTempoEdit.style.display = "none";
				codMonoTempoNoEdit.style.display = "";
				document.Frm1.codMonoTempoNoEdit.value = 'Indeterminato';
				document.Frm1.codMonoTempo.value = 'I';
				document.Frm1.datFineMov.value = "";
    			//scadenza.style.display = "none";
    			//datascadenza.style.display = "none";
    			//calcDataFine.style.display = "none";
		  	}
		  	
		  	if( (document.Frm1.CODTIPOMOV.value == "CES" || document.Frm1.CODTIPOMOV.value == "TRA") &&
		  		 document.Frm1.datInizioMov.value == "") {
				visualizzaObbli.style.display = "none";
			} else {
				visualizzaObbli.style.display = "";
			}
	        
	        document.Frm1.DATAINIZIOAVV.value = dataInizioAvv;
	        document.Frm1.DATINIZIOMOVPREC.value = dataInizioMovPrec;
	        if (contesto == 'valida' && document.Frm1.CODTIPOMOV.value == 'TRA' && document.Frm1.CODTIPOTRASF.value != 'DL') {
	        	document.Frm1.CODMONOTEMPOAVV.value = codMonoTempoAvv;
	        	document.Frm1.codMonoTempo.value = codMonoTempoAvv;
	        	if(document.Frm1.codMonoTempo.value == 'D') {
	        		//scadenza.style.display = "";
    				//datascadenza.style.display = "";
    				//calcDataFine.style.display = "";
	        	} else {
	        		document.Frm1.datFineMov.value = "";
    				//scadenza.style.display = "none";
    				//datascadenza.style.display = "none";
    				//calcDataFine.style.display = "none";
	        	}
	        }
	        document.Frm1.DATFINEMOVPREC.value = dataFineMovPrec;
	        document.Frm1.PRGMOVIMENTOPREC.value = prgMovPrec;
	        document.Frm1.CODTIPOMOVPREC.value = codTipoMovPrec;
	        document.Frm1.NUMKLOMOVPREC.value = eval(numKloMovPrec) + 1;          
	        var dati = document.getElementById("campiAvv");
	        var obj = document.getElementById("campiMov");
	        var aziTra = document.getElementById("datiAziTra");
	        var sezAziTra = document.getElementById("sezAziTra");
	        if (!insScollegato) {
	        	document.Frm1.CODTIPOMOV.value = '';
				dati.style.display = "none";
				obj.style.display = "none";
				aziTra.style.display = "none";
				sezAziTra.style.display = "none";
			}
	        document.Frm1.COLLEGATO.value = 'nessuno'; 
	        precedente = false;
	        inizializzaCollegati('', '');
	        var infoAvviamento = document.getElementById("infoAvv");
	        var visualizzaTestata = "<%=decMonoProv%>" + " Stato mov.: <strong>" 
        							+ document.Frm1.strTestataMovimento.value +"</strong> - Inf. in avv.: ";
        	infoAvviamento.innerHTML = visualizzaTestata;
	      } else {
	        //E' stato scelto, setto i dati con la XMLHTTPRequest      
	      	settaDatiPrecedente(prgMovPrec);
	      	//stefy 06/04/05
	      	// se sono in trasformazione/proroga/cessazione e ho scelto il mov. precedente
	      	// l'unità aziendale resta fissata una volta per tutte
	      	if(document.Frm1.CODTIPOMOV.value != "AVV" && contesto != "inserisci") { 
	      		//aggiornaTendinaAzXSceltaMovPrecedente(); 
	      		aggiornaAziendaXSceltaMovPrecedente(daMovimentiNew);
	      	}
	      	
		  }
	  }// else {
	  //vecchio funzionamento per la validazione
	  //(da togliere e allineare all'inserimento)
	    /*  document.Frm1.DATAINIZIOAVV.value = dataInizioAvv;
	      document.Frm1.DATINIZIOMOVPREC.value = dataInizioMovPrec;
	      document.Frm1.CODMONOTEMPOAVV.value = codMonoTempoAvv;
	      document.Frm1.DATFINEMOVPREC.value = dataFineMovPrec;
	    //  document.Frm1.CODTIPOASS.value = codTipoAss;      
	      document.Frm1.PRGMOVIMENTOPREC.value = prgMovPrec;
	      document.Frm1.CODTIPOMOVPREC.value = codTipoMovPrec;
	      document.Frm1.NUMKLOMOVPREC.value = eval(numKloMovPrec) + 1;   
	      if (prgMovPrec == '' || codTipoMovPrec == '' || numKloMovPrec == '') {  
	        if (!insScollegato) {
	          document.Frm1.CODTIPOMOV.value = '';
	        }
	        document.Frm1.COLLEGATO.value = 'nessuno'; 
	        precedente = false;
	        inizializzaCollegati('', '');
	        var infoAvviamento = document.getElementById("infoAvv");        
	        infoAvviamento.innerHTML = "Inf. Valide in fase di avviamento:";
	      } else {
	        document.Frm1.COLLEGATO.value = 'precedente';    
	        precedente = true;
	        //Se l'azienda non era settata la inserisco
	        if ((prgAziendaPrec != prgAzienda) || (prgUnitaPrec != prgUnita)) {
	          apriSelezionaSoggetto('Aziende', 'aggiornaAzienda', prgAzienda, prgUnita, '');          
	        }
	        //Aggiorno le informazioni in fase di avviamento
	        var infoAvviamento = document.getElementById("infoAvv");
	        infoAvviamento.innerHTML = "Inf. Valide in fase di avviamento: Rapporto a "+codMonoTempoAvv+" di tipo <strong>" + codTipoAss + "</strong> iniziato il <strong>" + dataInizioAvv + "</strong>";
	        inizializzaCollegati(prgMovPrec, '');
	      }
	  }*/     
    }
    <%} else{ %>
           //Per compatibilità con pagine vecchie
           function gestisciPrecedente() {}
      <%}%>
      
    
    
    // Stefy - SIMULO il refresh dovuto alla scelta dell'azienda da ricerca
    function aggiornaAziendaXSceltaMovPrecedente(daMovimentiNew)
    {
    	var _url = "AdapterHTTP?PAGE=MovimentiSelezionaUnitaRefreshPage";
    	_url += "&PRGMOVIMENTOAPP=" + document.Frm1.PRGMOVIMENTOAPP.value;
    	_url += "&CDNFUNZIONE=" + document.Frm1.CDNFUNZIONE.value;
    	_url += "&CONTESTO=AZIENDA";
    	_url += "&PRGUNITA=" + document.Frm1.PRGUNITA.value;
    	_url += "&PRGAZIENDA=" + document.Frm1.PRGAZIENDA.value;
    	_url += "&PRGAZIENDAUTILIZ=" + document.Frm1.PRGAZIENDAUTILIZ.value;
    	_url += "&PRGUNITAUTILIZ=" + document.Frm1.PRGUNITAUTILIZ.value;
    	_url += "&FUNZ_AGGIORNAMENTO=scegliUnitaAziendaNew";
    	_url += "&MODIFICATAAZIENDA=" + document.Frm1.MODIFICATAAZIENDA.value;
    	_url += "&daMovimentiNew=" + daMovimentiNew;
   	window.open(_url);
    }
    
    // Stefy - tolta la parte commentata  
    function aggiornaPrecedenteBIS(prgMovPrec, codTipoMovPrec, numKloMovPrec, dataInizioAvv, dataInizioMovPrec, codMonoTempoAvv, dataFineMovPrec, codTipoAss, insScollegato, prgAzienda, prgUnita,daMovimentiNew) 
    {
		settaDatiPrecedente(prgMovPrec);
	    if(document.Frm1.CODTIPOMOV.value != "AVV" && contesto != "inserisci") { 
	        aggiornaAziendaXSceltaMovPrecedente(daMovimentiNew);
	    }
    }



    
</SCRIPT>      