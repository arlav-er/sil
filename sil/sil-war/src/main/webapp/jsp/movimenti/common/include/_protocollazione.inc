<table class="main" border="0">
	<tr>
		<td colspan="6" class="azzurro_bianco">
			<table cellpadding="0" cellspacing="0" border="0" width="100%">
    			<tr>
    				<td class="etichetta2">Stato atto</td>
        			<td>
        				<table cellpadding="0" cellspacing="0" border="0" width="100%">
        					<tr>        
        						<td><af:comboBox  classNameBase="input" disabled ="true"  name="CODSTATOATTO_P"  
                          				moduleName="ComboStatoAtto" selectedValue="<%=codStatoAttoV%>"  
                          				addBlank="true" blankValue="" required="false"/>
        						</td>
        				<%-- 28/03/2007 Protocollazione DOCAREA: se abilitata si nascondono i campi del protocollo --%>
        				<% //if ((prgMovimento!=null && !prgMovimento.equals("")) || it.eng.sil.bean.protocollo.ProtocolloDocumentoUtil.protocollazioneLocale()) { 
        				if ( consulta || it.eng.sil.bean.protocollo.ProtocolloDocumentoUtil.protocollazioneLocale()) { 
        				%>
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
	    	         			<% if (it.eng.sil.bean.protocollo.ProtocolloDocumentoUtil.protocollazioneLocale()) { %>
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
				                <% } else { %>
									<input name="oraProt" type="hidden" value="00:00" />
								<% } %>
    						<% } else { %>
    							<td colspan="8">
    								<input type="hidden" name="numAnnoProt" value="<%= Utils.notNull(numAnnoProtV) %>">
    								<input type="hidden" name="numProtocollo">
    								<input type="hidden" name="dataProt" value="<%= datProtV %>">
    								<input type="hidden" name="oraProt" value="00:00">
    							</td>
    						<% } %> 
    						</tr>
    					</table>
    				</td>
    			</tr>
	    		<tr>
	    			<td class="etichetta2" nowrap="nowrap">Motivo Atto</td>
	    			<td class="campo2" nowrap="nowrap">
	    				<table cellpadding="0" cellspacing="0" border="0">
	        				<tr>
	        					<td class="campo2">
	        						<af:comboBox classNameBase="input" 
	        							name="CODMOTANNULLAMENTO" 
	        							moduleName="ComboMotAnnullamento" 
	        							title="Motivo Atto" 
	        							addBlank="true" 
	        							selectedValue="<%=codMotAnnullamento%>" 
	        							disabled="true"/>
	        					</td>
	        					<td class="etichetta2"  style="width: 50px">Doc. di</td>
	        					<td class="campo2" style="width: 80px">
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
			            		<td class="etichetta2">Rif.</td>
		            			<td class="campo2"><strong><%=docRif%>&nbsp;</strong></td>
		            			<td>&nbsp;</td>              
		        			</tr>
        				</table>
    				<td>
				<tr>
			</table>              
		</td>
	</tr>
  	<tr><td><br/></td></tr>
</table>