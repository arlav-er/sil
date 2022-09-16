<%/*
	File da includere per il frame dei movimenti collegati, necessita
	delle variabili Java:
	- _funzione
	
	e di una chamata alla funzione Javascript inizializzaCollegati
	quando la pagina che ospita l'include è stata caricata.
*/

//Path del movimento collegato
String sourcePathCol = "AdapterHTTP?PAGE=MovimentiCollegatiPage";
sourcePathCol += "&cdnfunzione=" + _funzione;
sourcePathCol += "&codtipomovimento=" + codTipoMov;
%><iframe name="movimentiCollegati" scrolling="no" frameborder="0" width="100%" height="7%" id="Collegati" src="<%=sourcePathCol%>"></iframe>
<script language="javascript">
//inizializza il frame dei movimenti collegati
function inizializzaCollegati(prgPrec, prgSucc) {
	window.frames['movimentiCollegati'].document.impostaCollegati(prgPrec, prgSucc);
}
</script>