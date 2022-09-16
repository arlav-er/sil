<SCRIPT TYPE="text/javascript">

  function aggiornaAzienda(){
        document.Frm1.STRRAGIONESOCIALE.value = opened.dati.ragioneSociale;
        document.Frm1.PRGAZIENDA.value = opened.dati.prgAzienda;
        opened.close();
    }
  
  function ListaAziende_onClick() {	
    var ragSoc=document.Frm1.STRRAGIONESOCIALE.value;
    if (ragSoc=="")
      window.open("AdapterHTTP?PAGE=IndispPage&MODULE=M_ListAziende",
        "Aziende", 'toolbar=0, scrollbars=1');
    else
      window.open("AdapterHTTP?PAGE=IndispPage&MODULE=M_ListAziende&STRRAGIONESOCIALE="+ragSoc, 
        "Aziende", 'toolbar=0, scrollbars=1');    
  }

  // Utilizzabile in caso di inserimento e aggiornamento record,
  function Salva_onClick() {
  <%-- nel caso in cui la pagina sia richiesta dal template di associazione al patto --%>
  <% if (serviceRequest.getAttribute("ONLY_INSERT")!=null) {%>
        document.forms[0].CDNFUNZIONE.value="<%=serviceRequest.getAttribute("cdnFunzione")%>";
        document.forms[0].PAGE.value="AssociazioneAlPattoTemplatePage";
        
//        doFormSubmit(document.forms[0]);  
        return true;
    <%} %>
    // -- NOTE: Gestione Patto
    if ( !controllaPatto() )
      return false;
    else return true;
    //--

//    doFormSubmit(document.forms[0]);
  }

  function GoToMainPage() {
	// Se la pagina è già in submit, ignoro questo nuovo invio!
	if (isInSubmit()) return;

    var url = "AdapterHTTP?PAGE=IndispPage&CDNLAVORATORE=<%= cdnLavoratore %>&CDNFUNZIONE=<%= _funzione %>";
    setWindowLocation(url);
  }

  function cancellaCodiceAzienda() {
    document.Frm1.PRGAZIENDA.value = "";
  }
  

</SCRIPT>
