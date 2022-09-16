<%/*
	File di include per la gestione delle linguette nell epagine del dettaglio 
	generale, funziona in base ai parametri: 
	- tipo di movimento corrente (String codTipoMovCorr)
	- parametri di contesto (boolean inserisci, valida, consulta, rettifica)
	
	Per aggiornare le linguette in base al tipo di movimento corrente utilizzare
	la funzione javascript gestisciLinguette(codTipoMovCorr)
*/
	//Scelgo i nomi delle page a seconda del contesto
	String pageAvviamento = "";
	String pageTrasfPro = "";
	String pageCessazione = "";
	if (inserisci) {
		pageAvviamento = "MovDettaglioAvviamentoInserisciPage";
		pageTrasfPro = "MovDettaglioTrasfProInserisciPage";
		pageCessazione = "MovDettaglioCessazioneInserisciPage";
	} else if (valida) {
		pageAvviamento = "MovValidaAvviamentoPage";
		pageTrasfPro = "MovValidaTrasfProPage";
		pageCessazione = "MovValidaCessazionePage";	
	} else if (consulta) {
		pageAvviamento = "MovDettaglioAvviamentoConsultaPage";
		pageTrasfPro = "MovDettaglioTrasfProConsultaPage";
		pageCessazione = "MovDettaglioCessazioneConsultaPage";	
	} else if (rettifica) {
		pageAvviamento = "MovDettaglioAvviamentoRettificaPage";
		pageTrasfPro = "MovDettaglioTrasfProRettificaPage";
		pageCessazione = "MovDettaglioCessazioneRettificaPage";	
	}
%>
 
  <div class="menu">
    <a id="linguettaGenerale" href='#' class='sel1'><span class='tr_round1'>&nbsp;Generale&nbsp;</span></a>
    <a id="linguettaAvviamento" href='#' onclick='gestioneCompetenze();' class='bordato1'><span class='tr_round11'>&nbsp;Avviamento&nbsp;</span></a>
    <a id="linguettaTrasfPro" href='#' onclick='gestioneCompetenze();' class='bordato1'><span class='tr_round11'>&nbsp;Trasformazione/Proroga&nbsp;</span></a>
    <a id="linguettaCessazione" href='#' onclick='gestioneCompetenze();' class='bordato1'><span class='tr_round11'>&nbsp;Cessazione&nbsp;</span></a>
  </div>
<script language="javascript">
	gestisciLinguette(codTipoMovCorr);
</script>  