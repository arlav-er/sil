<!--Parte aggiunta da Roccetti il 16/11/2004: se sono in una pop-up (variabile openPopUp impostata a true nella
	finestra opener) visualizzo il pulsante di chiusura
    -->
	<div id="PulsanteChiusuraPopUp" style="display: none;">
	<center>
  		<br>
  		<input type="button" class="pulsanti" value="Chiudi" onClick="window.close();"/> 
	</center>
	</div>
	<script language="javascript">
	if (window.opener != null && window.opener.openPopUpAzienda != null && window.opener.openPopUpAzienda == true) {
		document.getElementById("PulsanteChiusuraPopUp").style.display = "inline";
	} 
	</script>