<%--
	LOOKUP LAVORATORE
	NB: qui non e' incluso il campo chiave "cdnLavoratore" che va messo in un campo NASCOSTO!
	    non c'è neppure il bottone per la LOOKUP.
--%>
<tr valign="top">
	<td class="etichetta">Codice Fiscale</td>
	<td class="campo">
		<input type="text" name="codiceFiscaleLavoratore" class="inputView" readonly="true" value="<%=strCodiceFiscaleLav %>" size="30" maxlength="16"/>
	</td>
</tr>
<tr valign="top">
	<td class="etichetta">Cognome</td>
	<td class="campo">
		<input type="text" name="cognome" class="inputView" readonly="true" value="<%=strCognomeLav %>" size="30" maxlength="50"/>
	</td>
</tr>
<tr valign="top">
	<td class="etichetta">Nome</td>
	<td class="campo">
		<input type="text" name="nome" class="inputView" readonly="true" value="<%=strNomeLav %>" size="30" maxlength="50"/>
	</td>
</tr>
