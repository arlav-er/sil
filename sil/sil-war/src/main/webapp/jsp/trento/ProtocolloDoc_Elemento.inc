<tr valign="top">
	<td class="etichetta">Stato Atto</td>
	<td class="campo">
		<af:comboBox classNameBase="input" name="CODSTATOATTO" moduleName="COMBO_STATO_ATTO_DOC"
                    title="Stato atto del documento" selectedValue="PR" disabled="true"/>
	</td>
</tr>
<tr valign="top">
	<td class="etichetta">Anno</td>
	<td class="campo">
		<span class="etichetta">da</span>
		<af:textBox name="annoProtocollo_da" title="Anno" type="integer" validateOnPost="true" size="4" maxlength="4"/>
		&nbsp;
		<span class="etichetta">a</span>
		<af:textBox name="annoProtocollo_a" title="Anno" type="integer" validateOnPost="true" size="4" maxlength="4"/>
	</td>
</tr>
<tr valign="top">
	<td class="etichetta">Numero</td>
	<td class="campo">
		<span class="etichetta">da</span>
		<af:textBox name="numProtocollo_da" type="integer" title="Numero protocollo" validateOnPost="true" size="10" maxlength="10"/>
		&nbsp;
		<span class="etichetta">a</span>
		<af:textBox name="numProtocollo_a" type="integer" title="Numero protocollo" validateOnPost="true" size="10" maxlength="10"/>
	</td>
</tr>
<tr valign="top">
	<td class="etichetta">Data</td>
	<td class="campo">
		<span class="etichetta">da</span>
		<af:textBox name="dataProtocollo_da" title="Data protocollazione" type="date" validateOnPost="true" size="11" maxlength="10"/>
		&nbsp;
		<span class="etichetta">a</span>
		<af:textBox name="dataProtocollo_a" title="Data protocollazione" type="date" validateOnPost="true" size="11" maxlength="10"/>
	</td>
</tr>
<tr valign="top">
	<td class="etichetta">Documento di Input/Output</td>
	<td class="campo">
		<SELECT name="docIO">
			<OPTION value=""></OPTION>
			<OPTION value="I">Input</OPTION>
			<OPTION value="O">Output</OPTION>
		</SELECT>	
	</td>
</tr>
<tr valign="top">
	<td class="etichetta">Riferimento</td>
	<td class="campo">
		<af:comboBox name="ambito" moduleName="ComboAmbito" onChange="ComboAmbito_onChange()" addBlank="true"/>
	</td>
</tr>