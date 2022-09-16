<%/*
	File di include per la gestione delle linguette nelle pagine del dettaglio 
	avviamento, funziona in base ai parametri: 
	- parametri di contesto (boolean inserisci, valida, consulta, rettifica)

*/
	//Scelgo i nomi delle page a seconda del contesto
	String pageGenerale = "";
	if (inserisci) {
		pageGenerale = "MovDettaglioGeneraleInserisciPage";
	} else if (valida) {
		pageGenerale = "MovValidaDettaglioGeneralePage";
	} else if (consulta) {
		pageGenerale = "MovDettaglioGeneraleConsultaPage";
	} else if (rettifica) {
		pageGenerale = "MovDettaglioGeneraleRettificaPage";
	}
%>
 
  <div class="menu">
  
    <a id="linguettaGenerale" href='#' onclick='goToNoCheck("<%=pageGenerale%>");' class='bordato1'><span class='tr_round11'>&nbsp;Generale&nbsp;</span></a>
    <a id="linguettaCessazione" href='#' class='sel1'><span class='tr_round1'>&nbsp;Cessazione&nbsp;</span></a>
  </div>