<%-- Campi di input usati da pagine di dettaglio e di nuovo --%>
	<%if(!canModify){
	 	if(!isFromWs){%>
	<tr>
        <td class="etichetta">
       		Tipo
        </td>
		<td class="campo">
			<af:textBox name="tipoProfiling" type="text"
						title="Tipo di inserimento a sistema del Profiling GG"
						value="<%= descrTipoProfiling %>"
						size="40" 
						classNameBase="input"
						readonly="<%= readonlyAlways %>"
						onKeyUp="fieldChanged()"
						/>
		</td>
	</tr>
	<%}
	}%>
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
						readonly="<%= readonlyAlways %>"
						onKeyUp="fieldChanged()"
						/>
 		</td>
	</tr>
	<tr>
        <td class="etichetta">
       		Provincia
        </td>
		<td class="campo">
 			<af:comboBox name="provincia"
						title="Provincia"
						moduleName="M_COMBO_PROVINCIA_CodiceMinDescr" 
						selectedValue="<%= codiceProvincia %>"
						addBlank="true"
						disabled="<%= readonlyAlways %>"
						classNameBase="input"
						onChange="fieldChanged()" />
		</td>
	</tr>
	<%if(!canModify){%>
	<tr>
        <td class="etichetta">
       		Data Calcolo
        </td>
		<td class="campo">
		 <af:textBox name="dataCalcolo" type="text"
						title="Data Calcolo"
						value="<%= strDataCalcolo %>"
						size="16" maxlength="50"
						classNameBase="input"
						readonly="<%= readonly %>"
						onKeyUp="fieldChanged()"
						/>
 		</td>
	</tr>
	<%}%>
	<tr>
        <td class="etichetta">
       		Durata Presenza in Italia
        </td>
		<td class="campo">
 			<af:comboBox name="presenzaItalia"
						title="Durata Presenza in Italia"
						moduleName="M_CCD_COMBO_MN_PF_PRESENZAIT" 
						selectedValue="<%= codiceDurataPrIt %>"
						addBlank="true"
						required = "<%= required %>"
						disabled="<%= readonly %>"
						classNameBase="input"
						onChange="fieldChanged()" />
		</td>
	</tr>
	<tr>
        <td class="etichetta">
       		Condizione Occupazionale Anno Precedente
        </td>
		<td class="campo">
 			<af:comboBox name="condOccupaz"
						title="Condizione Occupazionale Anno Precedente"
						moduleName="M_CCD_COMBO_PF_OCCUP" 
						selectedValue="<%= codiceCondOccupaz %>"
						addBlank="true"
						required = "<%= required %>"
						disabled="<%= readonly %>"
						classNameBase="input"
						onChange="fieldChanged()" />
		</td>
	</tr>
	
	<tr>
      <td class="etichetta">Titolo di studio</td>
      <%if(!isFromWs){%>
      <td class="campo">
      	 <%if(canModify){%>
            <af:textBox required = "<%= required %>" classNameBase="input" type="text" title="Titolo di studio" name="strTitolo" size="100" 
            	readonly="<%=readonly %>"  value="<%=Utils.notNull(descrTitolo) %>"    />
        <%}else{%>
           <af:textArea name="strTitolo" cols="60"
					rows="2" title="Titolo di studio" readonly="<%=readonly %>" classNameBase="input"
					value="<%=Utils.notNull(descrTitolo) %>" />
        <%}%>
     </td>
      <%}else{%>
      <td class="campo">
      <af:textArea name="strTitolo" cols="60"
					rows="2" title="Titolo di studio" readonly="<%=readonly %>" classNameBase="input"
					value="<%=descrTitolo%>" />
		</td>
       <%}%>  
     
    </tr>
    <tr>
        <td class="etichetta">
       		Codice Titolo
        </td>
		<td class="campo">
 			<af:textBox classNameBase="input" type="text" readonly="true" name="codTitolo"  title="Titolo di studio" 
        		value="<%=Utils.notNull(codTitoloStudio) %>" size="8"/>
		</td>
	</tr>
    
    
    <%if(!canModify){%>
	<tr>
        <td class="etichetta">
       		Sesso
        </td>
		<td class="campo">
		 <af:textBox name="sesso" type="text"
						title="Sesso"
						value="<%= strSesso %>"
						size="40" 
						classNameBase="input"
						readonly="<%= readonly %>"
						onKeyUp="fieldChanged()"
						/>
 		</td>
	</tr>
	<tr>
        <td class="etichetta">
       		Et&agrave;
        </td>
		<td class="campo">
		 <af:textBox name="eta" type="text"
						title="Eta"
						value="<%= strEta %>"
						size="40" 
						classNameBase="input"
						readonly="<%= readonly %>"
						onKeyUp="fieldChanged()"
						/>
 		</td>
	</tr>
	<tr>
        <td class="etichetta">
       		Indice2
        </td>
		<td class="campo">
		 <af:textBox name="indice2" type="text"
						title="Indice2"
						value="<%= strIndice2 %>"
						size="40" 
						classNameBase="input"
						readonly="<%= readonly %>"
						onKeyUp="fieldChanged()"
						/>
 		</td>
	</tr>
	<tr>
        <td class="etichetta">
       		Descrizione Indice Profiling2
        </td>
		<td class="campo">
		 <af:textBox name="descrIndice2" type="text"
						title="Descrizione Indice Profiling2"
						value="<%= descrIndiceProf2 %>"
						size="40" 
						classNameBase="input"
						readonly="<%= readonly %>"
						onKeyUp="fieldChanged()"
						/>
 		</td>
	</tr>
	<%}%>