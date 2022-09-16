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
        						   <af:textBox classNameBase="input" type="text" required="true" name="CODSTATOATTO" 
    								 		value="<%=Utils.notNull(CODSTATOATTO)%>" readonly="<%=String.valueOf(readOnlyStr)%>" 
    										onKeyUp="fieldChanged();" validateOnPost="true"/>
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
			                            readonly="true"/>
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
			                            readonly="true"/>
			                   </td>
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
				              			strDocInOut="Input";
				              		else if (docInOut.equalsIgnoreCase("O"))
				              			strDocInOut="Output";
				              		else strDocInOut="&nbsp;"; 
				              	%>
		               				<strong><%=strDocInOut%></strong>
			               		</td>
			               		
			               		<td class="campo2">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
			            		<td class="etichetta2">Rif.</td>
		            			<td class="campo2"><strong><%=docRif%></strong></td> 
		            			<td class="campo2">&nbsp;&nbsp;&nbsp;</td>
		            		    <td class="etichetta2">Tipo Documento</td>
		            			<td class="campo2"><strong><%=docTipo%></strong></td>
		            	</table>
    				<td>
				<tr>
			</table>              
		</td>
	</tr>
<tr><td><br/></td></tr>
</table>

<table class="main">
<tr>
    <td class="etichetta">Data ultima iscrizione/reiscrizione</td>
    <td colspan=3 class="campo">
    	<af:textBox classNameBase="input" type="date" required="true" name="datUltimaIscr" value="<%=Utils.notNull(datUltimaIscr)%>"  
                    readonly="<%=String.valueOf(readOnlyStr)%>" onKeyUp="fieldChanged();" validateOnPost="true" size="11" maxlength="10" /></td>
</tr>
<tr>
    <td class="etichetta">Data anzianit&agrave; iscrizione</td>
    <td colspan=3 class="campo"><af:textBox classNameBase="input" type="date" name="datAnzianita68" value="<%=Utils.notNull(datAnzianita68)%>"  
                    readonly="<%=String.valueOf(readOnlyStr)%>" onKeyUp="fieldChanged();" validateOnPost="true" size="11" maxlength="10" /></td>
</tr>
<tr>
	<td class="etichetta">Numero d'iscrizione</td>
    <td colspan=3 class="campo">
    	<af:textBox classNameBase="input" type="integer" name="NUMISCRIZIONE" value="<%=numIscrizione%>"  
        	readonly="true" size="4" maxlength="3"/>
    </td>
</tr>
<tr>
	<td class="etichetta2">Tipo</td>
    <td class="campo2">
    	<af:textBox classNameBase="input" type="text" name="tipoRaggiunto" value="<%=Utils.notNull(tipoRaggiunto)%>"  
    				readonly="true" onKeyUp="fieldChanged();" required="true" validateOnPost="false" size="10" maxlength="10" />
    </td>
</tr>
<tr>
    <td class="etichetta">Categoria </td>
    <td class=campo2 width="30%">
    	<af:comboBox name="codCMTipoIscr" moduleName="M_GETDECMTIPOISCR" selectedValue="<%=codTipoIscr%>"
                     classNameBase="input" title="Tipo iscrizione" required="true" addBlank="true"
                     onChange="fieldChanged();" disabled="<%=String.valueOf(readOnlyStr)%>"/>
    </td>
</tr>
<tr>
    <td class="etichetta">Tipo invalidità</td>
    <td colspan=3 class="campo">
    	<af:comboBox name="codCMTipoInvalidita" moduleName="M_GETDECMTIPOINVALIDITA" selectedValue="<%=codTipoInvalidita%>"
                     classNameBase="input" addBlank="true" onChange="fieldChanged();" 
                     disabled="<%=String.valueOf(readOnlyStr)%>"/>
    </td>
</tr>
<tr>
    <td class="etichetta">Percentuale invalidità</td>
    <td colspan=3 class="campo">
    	<af:textBox classNameBase="input" type="integer" name="numPercInvalildita" value="<%=Utils.notNull(percInval)%>"  
                    readonly="<%=String.valueOf(readOnlyStr)%>" onKeyUp="fieldChanged();" validateOnPost="true" size="4" maxlength="3" />%</td>
</tr>
<tr>
    <td class="etichetta">Data accertamento sanitario</td>
    <td colspan=3 class="campo">
    	<af:textBox classNameBase="input" type="date" name="datAccertSanitario" 
     			    value="<%=Utils.notNull(datAccSanitario)%>" readonly="<%=String.valueOf(readOnlyStr)%>" 
     			    onKeyUp="fieldChanged();" validateOnPost="true" size="11" maxlength="10" /></td>
</tr>
<tr>
    <td class="etichetta">Tipo accertamento sanitario</td>
    <td colspan=3 class="campo">
    	<af:textBox classNameBase="input" type="text" name="codAccSanitario" value="<%=Utils.notNull(codAccSanitario)%>"  
    				readonly="true" onKeyUp="fieldChanged();" validateOnPost="false" size="80" maxlength="80" />
   </td>
</tr>
<tr>
    <td class="etichetta">Data Sospensione</td>
    <td colspan=3 class="campo">
    	<af:textBox classNameBase="input" type="date" name="datSospensione" value="<%=Utils.notNull(datSospensione)%>"  
                    readonly="<%=String.valueOf(readOnlyStr)%>" onKeyUp="fieldChanged();" validateOnPost="true" 
                    size="11" maxlength="10"/>
    </td>
</tr>
<tr>
    <td class="etichetta">Numero Mesi Sospensione</td>
    <td colspan=3 class="campo">
    	<af:textBox classNameBase="input" type="integer" name="NUMMESISOSPESTERNI" value="<%=numMesiSospEsterni%>"  
        	readonly="true" onKeyUp="fieldChanged();" validateOnPost="true" size="4" maxlength="3" />
    </td>
</tr>

<tr>
    <td class="etichetta">Verbale accertamento</td>
    <td colspan=3 class="campo">
    	<af:textBox classNameBase="input" type="text" name="PRGVERBALEACC" value="<%=Utils.notNull(prgVerbaleAcc)%>"  
    				readonly="true" onKeyUp="fieldChanged();" validateOnPost="false" size="80" maxlength="80" />
    	</td>
</tr>
<tr>
    <td class="etichetta">Operatore di riferimento</td>
    <td colspan=3 class="campo">
    	<af:textBox classNameBase="input" type="text" name="PRGSPIMOD" value="<%=Utils.notNull(prgSpiMod)%>"  
    				readonly="true" onKeyUp="fieldChanged();" validateOnPost="false" size="80" maxlength="80"/>
    </td>
</tr>
<tr>
    <td class="etichetta">Centro per l'impiego competente</td>
    <td colspan=3 class="campo">
    	<af:textBox classNameBase="input" name="CPICOMP" value="<%=cpiCompLav%>" readonly="true" size="30" maxlength="28" />
    </td>
</tr>
<tr><td colspan="4"><hr width="90%"/></td></tr>
<tr><td colspan="4">
<table class="main" border="0">
  <tr>
  	  <td class="etichetta">
  	  	Note<br/>
  	  </td>
      <td class="campo" colspan="3">
       <af:textArea classNameBase="textarea" name="strNote" value="<%=Utils.notNull(strNote)%>"
                    cols="60" rows="4" maxlength="2000"
                    onKeyUp="fieldChanged();" readonly="<%=String.valueOf(!canModifyNote)%>"  />
      </td>
  </tr>
  <%if(canModifyNote && (!flag_insert)){%>
	  <tr>
	  	<td colspan="4" align="center">
	  		<input class="pulsante" type="button" name="buttonAggiornaNote" value="Aggiorna note" 
	       		onClick="aggiornaNote(document.form1)">
	  	</td>
	  </tr>
  <%}%>
</table>
</td></tr>
</table>