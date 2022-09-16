<script language="Javascript">
	function setStatoAtto(){
		var codStatoAtto = document.Frm1.CODSTATOATTO.value;
		document.Frm1.codStatoAtto.value = codStatoAtto;
	}
</script>

<input type="hidden" name="dataOraProt" value="<%=dataOraProt%>" >
<input type="hidden" name="codTipoDocumento" value="CMDCOMP" >
<input type="hidden" name="tipoProt" value="S" >
<input type="hidden" name="codAmbito" value="L68" >
<input type="hidden" name="FlgcodMonoIO" value="<%=docInOut%>" >

<table class="main" border="0">
	<tr>
		<td colspan="6" class="azzurro_bianco">
			<table cellpadding="0" cellspacing="0" border="0" width="100%">
	    		<tr>
	        		<td>
        				<table cellpadding="0" cellspacing="0" border="0" width="100%">
        					<tr>        
        						<td class="etichetta2">Stato atto</td>
        						<td><af:comboBox  classNameBase="input" name="CODSTATOATTO"  
                          				moduleName="M_ComboStatoAtto" selectedValue="<%=CODSTATOATTO%>" 
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
			                            title="data di protocollazione"  
			                            classNameBase="input" 
			                            required="false" 
			                            readonly="true"                             
			                            trim ="false" />
			                   </td>
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
    						</tr>
    					</table>
    				</td>
    			</tr>
	    		<tr>
	    			<td class="campo2" nowrap="nowrap">
	    				<table cellpadding="0" cellspacing="0" border="0" width="80%">
	        				<tr>
	        					<td class="etichetta2">Doc. di</td>
	        					<td class="campo2">
	        					<%
		        					String strDocInOut = null;
		        					if (docInOut.equalsIgnoreCase("I"))
				              			strDocInOut="Input";
				              		else if (docInOut.equalsIgnoreCase("O"))
				              			strDocInOut="Output";
				              		else strDocInOut="&nbsp;"; 
				              	%>
		               				<strong><%=strDocInOut%></strong>
			               		</td>
			               		<td class="campo2">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
			            		<td class="etichetta2">Rif.</td>
		            			<td class="campo2"><strong><%=docRif%></strong></td> 
		            			<td class="campo2">&nbsp;&nbsp;&nbsp;</td>
		            			<td class="etichetta2">Tipo Documento</td>
		            			<td class="campo2"><strong><%=docTipo%></strong></td>
					            <td class="campo2">&nbsp;&nbsp;&nbsp;</td>
					           	<% if(canSalvaStato){
								  	if (nuovoComputo){%> 
					           			<td>
					            			<input type="submit" name="Inserisci" class="pulsanti" value="Salva" onClick="controllaDatARilascio()">
					            		</td>
					            	<%} if (noButton == false && !nuovoComputo) {%>
										<td>
											<input type="button" name="Aggiorna" class="pulsanti" value="Salva" onClick="aggiornaDocumento()">
										</td>
									<%} 
					             }%>
		        			</tr>
        				</table>
    				</td>
				</tr>
			</table>
		</td>
	</tr>
  	<tr><td><br/></td></tr>				
</table>
