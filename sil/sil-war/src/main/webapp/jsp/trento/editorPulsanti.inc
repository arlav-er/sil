<%
if(canModify){
	if ("A".equalsIgnoreCase(TIPOEDITOR)) {%>
				<td>
					<!-- <input type="submit" value="Stampa" class="stampa pulsanti"" onclick="impostaAction('stampaFake');" /> -->
					<%
						if ("Modifica".equalsIgnoreCase(TIPOOPERAZIONEEDITOR)) {
					%>
					<input type="submit" value="Modifica" id="modificaEditor" class="pulsanti"
						onclick="impostaAction('modificaEditor');" />
					<%
						} else {
					%>
					<input type="submit" value="Salva" class="pulsanti" 
						onclick="impostaAction('salvaEditor');" />
					<%
						}
					%>
	<% } else {%>
			<td>
				<!-- onclick="impostaAction('salvaAMDOC');" -->
				<input id="salvaDocLav" type="submit" value="Salva" class="pulsanti" />
	<%}

}%>
	<input type="submit" value="Chiudi Editor" class="pulsanti" onclick="impostaAction('apriEditor');" />
</td>