	<tr>
  		<td class="etichetta">Modalit&agrave;&nbsp;</td>
  		<td class="campo">
  		<%if(isNuovo){%>
		  <af:comboBox classNameBase="input" 
		  		name="codModalita" 
		  		moduleName="M_COMBO_MODALITA_EROGAZIONE_TDA_NONUSATI" 
            	addBlank="true" 
            	blankValue="" 
            	required="true"  
            	onChange="fieldChanged()"
            	selectedValue="" 
            	title="Modalità" 
            	disabled="<%=readonly%>"/>
         	<%}else{%>
         	   <af:comboBox classNameBase="input" 
		  		name="codModalita" 
		  		moduleName="M_COMBO_MODALITA_EROGAZIONE_TDA" 
            	addBlank="true" 
            	blankValue="" 
            	required="true"  
            	onChange="fieldChanged()"
            	selectedValue="<%=codModalita%>" 
            	title="Modalità" 
            	disabled="<%=readonly%>"/>
         	<%}%>
		</td>
	</tr>
			<tr>
     
	        		<td class="etichetta">Tipologia durata&nbsp;</td>        
	        		<td class=campo>
	        			<af:comboBox 
	        				name="strTipoDurata"
	        				disabled="<%=readonly%>"  
                        	moduleName="M_COMBO_MN_YG_TIPOLOGIA_DURATA" 
                        	selectedValue="<%= strTipoDurata %>"
                        	addBlank="true" 
                        	required="false" classNameBase="input"
                        	onChange="fieldChanged()" 
                        	title="Tipologia durata"/>
					</td>
	        	</tr>
	        	<tr>
	        		<td class="etichetta">Durata minima&nbsp;</td>
	        		<td class=campo>
	        			<af:textBox 
	        				name="strDurataMin"
							classNameBase="input" 
							size="4" 
							maxlength="4"
							type="integer"
							validateOnPost="true"
							onKeyUp="fieldChanged()" 
							value="<%= strDurataMin %>" 
							readonly="<%=readonly%>"
							title="Durata minima"/>
					</td>
	        	</tr>
	        	<tr>
	        		<td class="etichetta">Durata massima&nbsp;</td>
	        		<td class=campo>
	        			<af:textBox
	        				name="strDurataMax" 
							classNameBase="input" 
							size="4" 
							maxlength="4" 
							type="integer"
							validateOnPost="true"
							onKeyUp="fieldChanged()"
							value="<%= strDurataMax %>" 
							readonly="<%=readonly%>"
							title="Durata massima"/>
					</td>
	        	</tr>
	       
	    <tr>
  			<td class="etichetta">Percentuale completamento&nbsp;</td>
  			<td class="campo">
		 			 <af:textBox
	        				name="strPercentuale" 
							classNameBase="input" 
							size="4" 
							maxlength="4" 
							type="integer"
							validateOnPost="true"
							onKeyUp="fieldChanged()"
							value="<%= strPercentuale %>" 
							readonly="<%=readonly%>"
							title="Percentuale completamento"/>
			</td>
		</tr>
	   <tr>
    	<td class="etichetta">Tipo rimborso&nbsp;</td>
      	<td class="campo">
      		<af:comboBox 
	        				name="strRimborso"
	        				disabled="<%=readonly%>"  
                        	moduleName="M_GetModalitaRimborsoTDA" 
                        	selectedValue="<%= strRimborso %>"
                        	addBlank="true" 
                        	blankValue=""
                        	required="false" classNameBase="input"
                        	onChange="tipoRimborso();" 
                        	title="Tipo rimborso"/>
    	 
       		</td>
   		</tr>
		<tr>
  			<td class="etichetta">Valore unitario EURO&nbsp;</td>
  			<td class="campo">
			  <af:textBox type="text" name="strValUnit" title="Valore massimo EURO"
			                        value="<%=strValUnit%>"
			                        classNameBase="input"
			                        onKeyUp="fieldChanged()"
			                        validateOnPost = "true"
			                        readonly ="<%=readonly%>" 
			                        size="10"/>
			</td>
		</tr>
		<tr>
  			<td class="etichetta">Valore totale EURO&nbsp;</td>
  			<td class="campo">
			  <af:textBox type="text" name="strValTot" title="Valore totale EURO"
			                        value="<%=strValTot%>"
			                        classNameBase="input"
			                        onKeyUp="fieldChanged()"
			                        validateOnPost = "true"
			                        readonly ="<%=readonly%>" 
			                        size="10"/>
			</td>
		</tr>