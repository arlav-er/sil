  <!-- Visualizzazione sezione errori -->
  <%
    String elemRisultati = "";
    String elemScriptAlert = "";
    String elemScriptConfirm = "";
  	if (serviceResponse.containsAttribute("M_MovInserisciMovimentoCompleto.RECORDS")) {
		try {
			SourceBean result = (SourceBean) serviceResponse.getAttribute("M_MovInserisciMovimentoCompleto.RECORDS");
        	elemRisultati = GraficaUtils.showRisultati(result, false, new Boolean(true));
        	elemScriptAlert = GraficaUtils.showAlert(result);
        	elemScriptConfirm = GraficaUtils.showConfirm(result);   
  		} catch (Exception e) {
	    	out.print("<p class='titolo'>Impossibile visualizzare il risultato dell'inserimento: " + e.getMessage() + "</p>");
	  	}
	} else if (serviceResponse.containsAttribute("M_MovValidaFinal.RECORDS")) {
        try {
			SourceBean result = (SourceBean) serviceResponse.getAttribute("M_MovValidaFinal.RECORDS");
        	elemRisultati = GraficaUtils.showRisultati(result, false, new Boolean(true));
        	elemScriptAlert = GraficaUtils.showAlert(result);
        	elemScriptConfirm = GraficaUtils.showConfirm(result);            
          } catch (Exception e) {
            out.print("<p class='titolo'>Impossibile visualizzare il risultato della validazione: " + e.getMessage() + "</p>");
          }		
	} else if (serviceResponse.containsAttribute("M_MovSalvaGenConsulta.RECORDS")) {
		try {
			SourceBean result = (SourceBean) serviceResponse.getAttribute("M_MovSalvaGenConsulta.RECORDS");
		    elemRisultati = GraficaUtils.showRisultati(result, false, new Boolean(true));
		    elemScriptAlert = GraficaUtils.showAlert(result);
		    elemScriptConfirm = GraficaUtils.showConfirm(result); 
		} catch (Exception e) {
			out.print("<p class='titolo'>Impossibile visualizzare il risultato della modifica: " + e.getMessage() + "</p>");
		}		
	} else if (serviceResponse.containsAttribute("M_MovRettificaMovimento.RECORDS")) {
		try {
			SourceBean result = (SourceBean) serviceResponse.getAttribute("M_MovRettificaMovimento.RECORDS");
		    elemRisultati = GraficaUtils.showRisultati(result, false, new Boolean(true));
		    elemScriptAlert = GraficaUtils.showAlert(result);
		    elemScriptConfirm = GraficaUtils.showConfirm(result); 
		} catch (Exception e) {
			out.print("<p class='titolo'>Impossibile visualizzare il risultato della rettifica: " + e.getMessage() + "</p>");
		}		
	}
	//Non viene visualizzata la sezione a tendina se non vi sono problemi
    if(!elemRisultati.equals("")) out.print(elemRisultati);   
  %>