	<tr>
        <td class="etichetta">
       		Codice Fiscale
        </td>
		<td class="campo">
		 <af:textBox name="codiceFiscale" type="text"
						title="Codice Fiscale Lavoratore"
						value="<%= strCodiceFiscale %>"
						size="40" maxlength="101"
						classNameBase="input"
						readonly="true"
			/>
 		</td>
	</tr>
	<tr>
        <td class="etichetta">
       		Data riferimento
        </td>
		<td class="campo">
		 <af:textBox name="dataRif" type="text"
						title="Data riferimento"
						value="<%= strDataRiferimento %>"
						size="40" maxlength="101"
						classNameBase="input"
						readonly="true"
			/>
 		</td>
	</tr>
	<tr>
        <td class="etichetta">
       		Tipo verifica
        </td>
		<td class="campo">
		  	<af:comboBox name="verificaNeet" size="1" title="Tipo verifica"
			  multiple="false" disabled="true" classNameBase="input"
			  moduleName="M_ComboVerificaNeet" selectedValue="<%=tipoVerificaNeet%>"
			/>
 		</td>
	</tr>
	<tr>
        <td class="etichetta">
       		Esito verifica NEET
        </td>
		<td class="campo">
		 <af:textBox name="esitoVerifica" type="text"
						title="Esito verifica NEET"
						value="<%= esitoVerificaNeet %>"
						size="40" maxlength="101"
						classNameBase="input"
						readonly="true"
			/>
 		</td>
	</tr>
	
	<tr>
		<td colspan="2"><div class="sezione2"/>Dettaglio verifica condizioni NEET</td>
	</tr>
	
	<tr>
        <td class="etichetta">
       		Verifica Et&agrave;
        </td>
		<td class="campo">
		 <af:textBox name="eta" type="text"
						title="Eta"
						value="<%= flgEta %>"
						size="40" maxlength="101"
						classNameBase="input"
						readonly="true"
			/>
 		</td>
	</tr>
	<tr>
        <td class="etichetta">
       		Verifica Residenza
        </td>
		<td class="campo">
		 <af:textBox name="residenza" type="text"
						title="residenza"
						value="<%= flgResidenza %>"
						size="40" maxlength="101"
						classNameBase="input"
						readonly="true"
			/>
 		</td>
	</tr>
	<tr>
        <td class="etichetta">
       		Verifica Disoccupazione
        </td>
		<td class="campo">
		 <af:textBox name="disoccupaz" type="text"
						title="disoccupazione"
						value="<%= flgDisocc %>"
						size="40" maxlength="101"
						classNameBase="input"
						readonly="true"
			/>
 		</td>
	</tr>
	<tr>
        <td class="etichetta">
       		Verifica Percorsi Scolastici
        </td>
		<td class="campo">
		 <af:textBox name="scuola" type="text"
						title="percorsi scolastici"
						value="<%= flgScuola %>"
						size="40" maxlength="101"
						classNameBase="input"
						readonly="true"
			/>
 		</td>
	</tr>
	<tr>
        <td class="etichetta">
       		Verifica Percorsi Universitari
        </td>
		<td class="campo">
		 <af:textBox name="uni" type="text"
						title="percorsi universitari"
						value="<%= flgUni %>"
						size="40" maxlength="101"
						classNameBase="input"
						readonly="true"
			/>
 		</td>
	</tr>
	<tr>
        <td class="etichetta">
       		Esito min.
        </td>
		<td class="campo">
		 <af:textBox name="codEsitoMin" type="text"
						title="esito ministeriale"
						value="<%= esitoMinistero %>"
						size="6"
						classNameBase="input"
						readonly="true"
		/> - 
		  <af:comboBox name="descrEsitoMin" size="1" title="esito min descr"
			  multiple="false" 
			  disabled="true" classNameBase="input"
			  moduleName="M_ComboMinEsitoNeet" 
			  selectedValue="<%=esitoMinistero%>"
		 />
 		</td>
	</tr>