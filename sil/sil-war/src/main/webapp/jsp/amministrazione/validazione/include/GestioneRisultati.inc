  <!-- Visualizzazione sezione errori -->
  <%
    if (serviceResponse.containsAttribute("M_MOBVALIDAMOBILITA.RECORDS")) {
        try {
			SourceBean result = (SourceBean) serviceResponse.getAttribute("M_MOBVALIDAMOBILITA.RECORDS");
        	elemRisultati = GraficaUtils.showRisultati(result, false, new Boolean(true));
        	elemScriptAlert = GraficaUtils.showAlert(result);
        	elemScriptConfirm = GraficaUtils.showConfirm(result);            
          } catch (Exception e) {
            out.print("<p class='titolo'>Impossibile visualizzare il risultato della validazione: " + e.getMessage() + "</p>");
          }		
	}
	//Non viene visualizzata la sezione a tendina se non vi sono problemi
    if(elemRisultati != null && !elemRisultati.equals("")) out.print(elemRisultati);
  %>