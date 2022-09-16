	<tr>
		<td colspan="6" class="azzurro_bianco">
			<table cellpadding="0" cellspacing="0" border="0" width="100%">
    			<tr>
        			<td>
        				<table cellpadding="0" cellspacing="0" border="0" width="100%">
        					<tr>        
        						<td class="etichetta2">Stato atto</td>
        						<%if (canSalvaStato) {%>
        						<td><af:comboBox  classNameBase="input" name="codStatoAtto"  
                          				moduleName="M_ComboStatoAttoProsp" selectedValue="<%=codStatoAtto%>" 
                          				required="false" onChange="gestione_Protocollazione()"/> 
                          		</td>       	
        						<%} else {%>
        						<td><af:comboBox  classNameBase="input" name="codStatoAtto"  
                          				moduleName="M_ComboStatoAttoProsp" selectedValue="<%=codStatoAtto%>" 
                          				required="false" disabled="true"/>
        						</td>        						
        						<%} %>      						
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
			                            title="data di protocollazione"  
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
	    				<table cellpadding="0" cellspacing="0" border="0" width="80%">
	        				<tr>
	        					<td class="etichetta2" nowrap>Doc. di</td>
	        					<td class="campo2" nowrap>
	        					<%
		        					String strDocInOut = null;
		        					if (docInOut.equalsIgnoreCase("I"))
				              			strDocInOut="INPUT";
				              		else if (docInOut.equalsIgnoreCase("O"))
				              			strDocInOut="OUTPUT";
				              		else strDocInOut="&nbsp;"; 
				              	%>
				              	
		               				<strong><%=strDocInOut%></strong>
			               		    <af:textBox type="hidden" name="striodoc" value="<%=docInOut%>"/>
       		                    </td>
			               		<td class="campo2" nowrap>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
			            		<td class="etichetta2"nowrap>Rif.</td>
		            			<td class="campo2" nowrap><strong><%=docRif%></strong></td> 
		            			<td class="campo2">&nbsp;&nbsp;&nbsp;</td>
		            			<af:textBox type="hidden" name="codAmbito" value="<%=docCodRif%>"/>
		            			<af:textBox type="hidden" name="strambitodoc" value="<%=docRif%>"/>            
		            			<td class="etichetta2" nowrap>Tipo Documento</td>
		            			<td class="campo2" nowrap><strong><%=docTipo%></strong></td>
		            			<af:textBox type="hidden" name="codTipoDocumento" value="<%=docCodTipo%>"/>
		            			<af:textBox type="hidden" name="strtipodoc" value="<%=docTipo%>"/>
					            <td class="campo2" nowrap>&nbsp;&nbsp;&nbsp;</td>
								<% if(canSalvaStato){
								  if (nuovoProspetto){%> 
					            	<td>
					            		<input type="submit" name="Salva" class="pulsanti" value="Salva" onClick="controllaDatARilascio()"/>
					            		<input type="hidden" name="inserisciDoc" value="1"/>
				 					</td>
					            <%}  if (noButton == false && !nuovoProspetto) {%> 
									<td>
										<input type="button" name="Aggiorna" class="pulsanti" value="Salva" onClick="aggiornaDocumento()">
									</td>
								<%}
								}%> 
					        </tr>
        				</table>
    				<td>
				<tr>
			</table>              
		</td>
	</tr>
  	<tr><td><br/></td></tr>


