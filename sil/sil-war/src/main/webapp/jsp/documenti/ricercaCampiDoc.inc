	<tr valign="top">
		<td class="etichetta">Tipo documento</td>
		<td class="campo"><af:comboBox name="tipoDocumento" moduleName="ComboTipoDocumento"
										addBlank="true" multiple="true" /></td>
	</tr>
	<tr valign="top">
		<td class="etichetta">Descrizione</td>
		<td class="campo"><af:textBox name="strDescrizione" classNameBase="input"
										title="Descrizione"
										size="75" maxlength="100" /></td>
	</tr>
	<tr valign="top">
		<td class="etichetta">Autocertificazione</td>
		<td class="campo"><SELECT name="autoCertificazione">
			<OPTION value=""></OPTION>
			<OPTION value="S">Sì</OPTION>
			<OPTION value="N">No</OPTION>
		</SELECT></td>
	</tr>
	<tr valign="top">
		<td class="etichetta">Documento amministrativo</td>
		<td class="campo"><SELECT name="docAmministrativo">
			<OPTION value=""></OPTION>
			<OPTION value="S">Sì</OPTION>
			<OPTION value="N">No</OPTION>
		</SELECT></td>
	</tr>
	<tr valign="top">
		<td class="etichetta">Documento di identificazione principale</td>
		<td class="campo"><SELECT name="docIdentificazione">
			<OPTION value=""></OPTION>
			<OPTION value="S">Sì</OPTION>
			<OPTION value="N">No</OPTION>
		</SELECT></td>
	</tr>
	<tr valign="top">
		<td class="etichetta">Data inizio validit&agrave;</td>
		<td class="campo">
			<span class="etichetta">da</span>
			<af:textBox name="DatInizio_da" title="Data inizio validità" type="date" validateOnPost="true" size="11" maxlength="10"/>
			&nbsp;
			<span class="etichetta">a</span>
			<af:textBox name="DatInizio_a" title="Data inizio validità" type="date" validateOnPost="true" size="11" maxlength="10"/>
		</td>
	</tr>
	<tr valign="top">
		<td class="etichetta">Data fine validit&agrave;</td>
		<td class="campo">
			<span class="etichetta">da</span>
			<af:textBox name="DatFine_da" title="Data fine validità" type="date" validateOnPost="true" size="11" maxlength="10"/>
			&nbsp;
			<span class="etichetta">a</span>
			<af:textBox name="DatFine_a" title="Data fine validità" type="date" validateOnPost="true" size="11" maxlength="10"/>
		</td>
	</tr>

	<tr valign="top">
		<td class="etichetta">Numero di protocollo esterno</td>
		<td class="campo"><af:textBox name="strNumDoc" classNameBase="input"
										size="18" maxlength="15" /></td>
	</tr>

	<tr valign="top">
		<td class="etichetta">Ente che ha prodotto/rilasciato</td>
		<td class="campo"><af:textBox name="strEnteRilascio" classNameBase="input"
										title="Descrizione dell'ente che ha prodotto/rilasciato il documento"
										size="75" maxlength="100" /></td>
	</tr>
	<tr valign="top">
		<td class="etichetta">&nbsp;</td>
		<td class="campo">tipo ricerca:
				<input type="radio" name="tipoRicercaEnteRil"  value="esatta" checked="true" /> esatta
				&nbsp;&nbsp;&nbsp;&nbsp;
				<input type="radio" name="tipoRicercaEnteRil" value="contiene" />contiene
		</td>
	</tr>
	<tr valign="top">
		<td class="etichetta" colspan="2">&nbsp;</td>
	</tr>

	<tr valign="top">
		<td class="etichetta">CPI di riferimento</td>
		<td class="campo"><af:comboBox name="codCpi" moduleName="M_GetCpiPoloProvinciale"
										addBlank="true" /></td>
	</tr>