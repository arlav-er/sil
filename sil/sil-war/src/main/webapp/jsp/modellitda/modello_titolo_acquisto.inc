	<tr>
  		<td class="etichetta">Programma&nbsp;</td>
  		<td class="campo">
		  	<af:comboBox classNameBase="input" 
		  		name="codServizio" 
		  		moduleName="COMBO_SERVIZIO_SCAD_COLLOQUI" 
		  		onChange="reloadDettaglioModelloCodServizio()"
            	addBlank="true" 
            	blankValue=""	 
            	required="true" 
            	selectedValue="<%=codServizio%>" 
            	title="Programma" 
            	disabled="<%=isReading%>"/>
		</td>
	</tr>
	<tr>
  		<td class="etichetta">Obiettivo/Misura YEI&nbsp;</td>
  		<td class="campo">
		  	<af:comboBox classNameBase="input" 
		  		name="obiettivoMisura" 
		  		moduleName="<%= nomeModuloObiettivi%>" 
		  		onChange="reloadDettaglioModello()"
            	addBlank="true" 
            	blankValue=""	 
            	required="true" 
            	selectedValue="<%=strPrgAzioneRagg%>" 
            	title="Obiettivo/Misura YEI" 
            	disabled="<%=isReading%>"/>
		</td>
	</tr>
	
	<tr>
  		<td class="etichetta">Azione&nbsp;</td>
  		<td class="campo">
		  <af:comboBox classNameBase="input" 
		  		name="prgAzioni" 
		  		moduleName="<%= nomeModulo%>" 
		  		addBlank="true" 
		  		blankValue=""
		  		onChange="fieldChanged()"
            	required="true"  
            	selectedValue="<%=strPrgAzioni%>" 
            	title="Azione" 
            	disabled="<%=isReading%>"/>
		</td>
	</tr>
	<%if(!isNuovo){%>
	<% if (canModify) { %>
		<tr colspan="2">
		<%}else{%>
		<tr>
		<%}%>
  		<td class="etichetta">Tipologia di servizio:&nbsp;</td>
  		<td class="campo">
		  <af:textBox type="text" name="descrizioneTipoServizio" title="Tipologia di servizio"
			                        value="<%=strDescrizioneTipoServizio%>"
			                        classNameBase="input"
 			                        readonly ="true" 
			                  />
		</td>
		<% if (canModify) { %>
		<td class="etichetta">&nbsp;</td>
  		<td class="campo">
	 		<input class="pulsante" type = "button" name="cambiaTipoServizio" value="Cambia Tipologia" onClick="apriPopUpModificaTipoServizio();"/>
		</td>
		<%}%>
	</tr>
	<%}%>
	<tr>
    	<td class="etichetta">TDA soggetti in CM o svantaggiati&nbsp;</td>
      	<td class="campo">
      		<af:comboBox 
	        				name="tdaCM"
                         	moduleName="M_ComboSiNo" 
                        	selectedValue="<%= tdaCM %>"
                        	addBlank="true" 
                        	blankValue=""
                        	required="false" classNameBase="input"
                        	onChange="fieldChanged()" 
                        	disabled ="<%=readonly%>" 
                        	title="TDA soggetti in CM o svantaggiati"/>
       	</td>
   	</tr>
	<tr>
  		<td class="etichetta">Regola di compilazione Modalit&agrave;&nbsp;</td>
  		<td class="campo">
		  <af:comboBox classNameBase="input" 
		  	name="codModalita" 
		  	moduleName="ComboModalitaErogazione" 
            addBlank="true" 
            blankValue=""	
            onChange="fieldChanged()"	 
            required="false"  
            selectedValue="<%=codModalita%>" 
            title="Regola di compilazione Modalità" 
            disabled="<%=readonly%>"/>
		</td>
	</tr>
	<tr>
  		<td class="etichetta">Giorni entro cui attivare&nbsp;</td>
  		<td class="campo">
		  <af:textBox type="text" name="giorniAttivazione" title="Giorni entro cui attivare"
			                        value="<%=strGiorniAttivazione%>"
			                        classNameBase="input"
			                        readonly ="<%=readonly%>" 
			                        onKeyUp="fieldChanged()"
			                        validateOnPost = "true"
			                        size="10"/>
		</td>
	</tr>
	<tr>
  		<td class="etichetta">Giorni entro cui concludere&nbsp;</td>
  		<td class="campo">
		  <af:textBox type="text" name="giorniErogazione" title="Giorni entro cui concludere"
			                        value="<%=strGiorniErogazione%>"
			                        classNameBase="input"
			                        onKeyUp="fieldChanged()"
			                        readonly ="<%=readonly%>" 
			                        validateOnPost = "true"
			                        size="10"/>
		</td>
	</tr>
	<tr>
    	<td class="etichetta">Attivo&nbsp;</td>
      	<td class="campo">
      		<af:comboBox 
	        				name="modelloAttivo"
                         	moduleName="M_ComboSiNo" 
                        	selectedValue="<%= modelloAttivo %>"
                        	addBlank="true" 
                        	blankValue=""
                        	required="false" classNameBase="input"
                        	onChange="fieldChanged()" 
                        	disabled ="<%=canModifyFlag%>" 
                        	title="Attivo"/>
       	</td>
   	</tr>
  	<tr>
  		<td class="etichetta">Valore massimo EURO&nbsp;</td>
  		<td class="campo">
		  <af:textBox type="text" name="strValoreMax" title="Valore massimo EURO"
			                        value="<%=strValoreMax%>"
			                        classNameBase="input"
			                        validateOnPost = "true"
			                        onKeyUp="fieldChanged()"
			                        readonly ="<%=canModifyMax%>" 
			                        size="10"/>
		</td>
	</tr>