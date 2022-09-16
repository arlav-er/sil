<%  // *** aButtonsLine.inc ***
   /** PARTE DI STAMPA DEI BOTTONI IN BASSO ALLA PAGINA
    *  I bottoni sono:  SALVA - ELIMINA - ANNULLA/CHIUDI
    *
    * Occorre aver definito:
    * 1) una variabile booleana "isModificabile" che indica se abilitare il bottone di SALVA
    *    (inoltre questa variabile influenza l'etichetta del bottone di chiusura: ANNULLA o CHIUDI);
    * 2) una variabile booleana "isCancellabile" che indica se abilitare il bottone di ELIMINA.
    *
    * In ogni caso, la pressione del bottone (se mostrato) invoca i seguenti metodi JavaScript
    * che devono essere presenti nella pagina: Salva(), Elimina() e Annulla().
    */
%>
<table align="center">
	<tr>
		<td align="center">
      <% if (isModificabile) { %>
          <INPUT type="BUTTON" name="salva" class="PulsanteVerde" value=" Salva "
                 onClick="Salva()" accessKey="S" title="Salva [ALT-S]">&nbsp;&nbsp;
      <% }
         if (isCancellabile) { %>
          <INPUT type="BUTTON" name="elimina" class="PulsanteVerde" value="Elimina"
                 onClick="Elimina()" accessKey="E" title="Elimina [ALT-E]">&nbsp;&nbsp;
      <% }
         // Bottone di ANNULLA/CHIUDI (il comportamento è lo stesso)
         if (isModificabile) { %>
          <INPUT type="BUTTON" name="annulla" class="PulsanteVerde" value="Annulla"
                 onClick="Annulla()" accessKey="A" title="Annulla [ALT-A]">
      <% } else { %>
          <INPUT type="BUTTON" name="annulla" class="PulsanteVerde" value="Chiudi"
                 onClick="Annulla()" accessKey="C" title="Chiudi [ALT-C]">
      <% } %>
		</td>
	</tr>
</table>