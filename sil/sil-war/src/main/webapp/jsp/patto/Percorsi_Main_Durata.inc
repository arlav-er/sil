<table id="sezione_durata" width="100%" border=0>

    <tr>
        <td colspan="4">    
            <table class='sezione' cellspacing=0 cellpadding=0>
				<tr>					
					<td width=18></td>					
					<td class="sezione_titolo">Durata</td>
				</tr>
			</table>
		</td>
	</tr>
	
	<tr>
    	<td colspan=4 align="center">
        	<table id="T_S_DURATA_YG" style="width:100%;">     
        		<tr>
	        		<td class="etichetta">Tipologia durata&nbsp;</td>        
	        		<td class=campo>
	        			<af:comboBox 
	        				name="CODTIPOLOGIADURATA"
	        				disabled="<%=fieldReadOnly%>"  
                        	moduleName="M_COMBO_MN_YG_TIPOLOGIA_DURATA" 
                        	selectedValue="<%= codTipologiaDurata %>"
                        	addBlank="true" 
                        	required="false" classNameBase="input"
                        	onChange="fieldChanged();" 
                        	title="Tipologia durata"/>
					</td>
	        	</tr>
	        	<tr>
	        		<td class="etichetta">Durata minima&nbsp;</td>
	        		<td class=campo>
	        			<af:textBox 
	        				name="NUMYGDURATAMIN"
							classNameBase="input" 
							size="4" 
							maxlength="4"
							type="integer"
							validateOnPost="true"
							onKeyUp="fieldChanged();" 
							value="<%= numYgDurataMin %>" 
							readonly="<%=fieldReadOnly%>"
							title="Durata minima"/>
					</td>
	        	</tr>
	        	<tr>
	        		<td class="etichetta">Durata massima&nbsp;</td>
	        		<td class=campo>
	        			<af:textBox
	        				name="NUMYGDURATAMAX" 
							classNameBase="input" 
							size="4" 
							maxlength="4" 
							type="integer"
							validateOnPost="true"
							onKeyUp="fieldChanged();"
							value="<%= numYgDurataMax %>" 
							readonly="<%=fieldReadOnly%>"
							title="Durata massima"/>
					</td>
	        	</tr>
	        	<tr>
	        		<td class="etichetta">Durata effettiva&nbsp;</td>
	        		<td class=campo>
	        			<af:textBox
	        				name="NUMYGDURATAEFF" 
							classNameBase="input" 
							size="4" 
							maxlength="4" 
							type="integer"
							validateOnPost="true"
							onKeyUp="fieldChanged();"
							value="<%= numygDurataEff %>" 
							readonly="<%=fieldReadOnly%>"
							title="Durata effettiva"/>
	        		</td>
	        	</tr>
	        </table>
	    </td>
	</tr>

</table>
