<table class="main" border="0">
	<tr>
		<td colspan="6" class="azzurro_bianco">
			<table cellpadding="0" cellspacing="0" border="0" width="100%">
    			<tr>
        			<td>
        				<table cellpadding="0" cellspacing="0" border="0" width="100%">
        					<tr>        
        						<td class="etichetta2">Stato atto</td>
        						<td>
        						   <af:comboBox  classNameBase="input" title="Stato atto" name="CODSTATOATTO"  
                          				moduleName="COMBO_STATO_ATTO_ISCR_L68" selectedValue="<%=CODSTATOATTO%>" 
                          				required="true" disabled="<%=String.valueOf(noButton)%>" onChange="gestisci_Protocollazione()"/> 
                          		</td>       						
        						<td class="etichetta2">anno</td>
        						<td class="campo2">
        							<af:textBox name="numAnnoProt"
					                     validateOnPost="true" 
					                     title="Anno di protocollazione"
					                     value="<%= Utils.notNull(numAnnoProtV) %>"
					                     classNameBase="input"
					                     size="4"
					                     maxlength="4" 
					                     readonly="true" /></td>
        						<td class="etichetta2">num.</td>
        						<td class="campo2">
        							<af:textBox name="numProtocollo"
				                         title="Numero di protocollo"
				                         value="<%= Utils.notNull(numProtV) %>"
				                         classNameBase="input"
				                         size="6"
				                         maxlength="38"
				                         readonly="true" />
				                </td>
        						<td class="etichetta2">data</td>
        						<td class="campo2">
           							<af:textBox name="dataProt" 
			                            type="date" 
			                            value="<%=datProtV%>" 
			                            size="11" 
			                            maxlength="10"
			                            title="Data di protocollazione"  
			                            classNameBase="input" 
			                            required="false" 
			                            readonly="true"                             
			                            trim ="false" />
			                   </td>
			                   
			                  <input type="hidden" name="tipoProt" value="S" />
			                   
			                   <td class="etichetta2">ora</td>
								<td class="campo2">
           							<af:textBox name="oraProt"
			                            type="date"
			                            value="<%=oraProtV%>"
			                            size="6" 
			                            maxlength="5"
			                            title="ora"  
			                            classNameBase="input" 
			                            readonly="true"
			                            trim ="false" />
			                   </td>
			                 <input type="hidden" name="dataOraProt" value="<%=dataOraProt%>" />
			                  </tr>
    					</table>
    				</td>
    			</tr>
	    		<tr>
	    			<td class="campo2" nowrap="nowrap">
	    				<table cellpadding="0" cellspacing="0" border="0" width="90%">
	        				<tr>
	        					<td class="etichetta2">Doc. di</td>
	        					<td class="campo2">
	        					<%
		        					String strDocInOut = null;
		        					if (docInOut.equalsIgnoreCase("I"))
				              			strDocInOut="INPUT";
				              		else if (docInOut.equalsIgnoreCase("O"))
				              			strDocInOut="OUTPUT";
				              		else strDocInOut="&nbsp;"; 
				              	%>
		               				<strong><%=strDocInOut%></strong>
			               		</td>
			               		<input type="hidden" name="FlgcodMonoIO" value="<%=docInOut%>" />
			               		<td class="campo2">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
			            		<td class="etichetta2">Rif.</td>
		            			<td class="campo2"><strong><%=docRif%></strong></td> 
		            			<td class="campo2">&nbsp;&nbsp;&nbsp;</td>
		            				<af:textBox type="hidden" name="codAmbito" value="<%=docCodRif%>"/>            
		            			<td class="etichetta2">Tipo Documento</td>
		            			<td class="campo2"><strong><%=docTipo%></strong></td>
		            				<af:textBox type="hidden" name="codTipoDocumento" value="<%=docCodTipo%>"/>
					            <td class="campo2">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
								<% if(canSalvaStato){
								   if (nuovaIscrizione){%> 
					            	<td>
					            		<input type="submit" name="Salva" class="pulsanti" value="Salva"/>
				 					</td>
					            <%}  if (!noButton && !nuovaIscrizione) {%> 
									<td>
										<input type="button" name="Aggiorna" class="pulsanti" value="Salva" onClick="aggiornaDocumento()">
									</td>
								<%}
								}%>
						</table>
    				<td>
				<tr>
			</table>              
		</td>
	</tr>
  	<tr><td><br/></td></tr>
</table>
