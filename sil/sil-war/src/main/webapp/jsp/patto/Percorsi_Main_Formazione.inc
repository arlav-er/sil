<table id="sezione_formazione" width="100%" border=0>

    <tr>
        <td colspan="4">    
            <table class='sezione' cellspacing=0 cellpadding=0>
				<tr>					
					<td width=18></td>					
					<td class="sezione_titolo">Formazione</td>
				</tr>
			</table>
		</td>
	</tr>
	
	<tr>
    	<td colspan=4 align="center">
        	<table id="T_S_FORMAZIONE" style="width:100%;">     
        		<tr>
	        		<td class="etichetta">Esito formazione&nbsp;</td>        
	        		<td class="campo">
	        			<af:comboBox disabled="true"  name="codEsitoSiferSez" classNameBase="input"
		                    moduleName="M_DeEsito_Sifer" selectedValue="<%= codEsitoSifer %>"
		                    addBlank="true" required="false" title="Esito formazione"/>
					</td>										
	        	</tr>
	        	<tr>
	        		<td class="etichetta">Data esito formazione&nbsp;</td>        
	        		<td class=campo>
	        			 <af:textBox title="Data esito formazione" classNameBase="input" readonly="true" type="date"
			                    name="dtmEsitoFormazioneSez" required="false" value="<%= dtmEsitoFormazione%>" 
			                    size="18"/>
					</td>
	        	</tr>
	        	<tr>
	        		<td class="etichetta">Data avvio formazione&nbsp;</td>        
	        		<td class="campo" >
	        			 <af:textBox title="Data avvio formazione" classNameBase="input" readonly="true" type="date"
			                    name="dtmAvvioFormazioneSez" required="false" value="<%= dtmAvvioFormazione%>" 
			                    size="12"/>
					</td>
	        	</tr>
	        	<tr>
	        		<td class="etichetta">Data fine formazione&nbsp;</td>        
	        		<td class="campo" >
	        			 <af:textBox title="Data fine formazione" classNameBase="input" readonly="true" type="date"
			                    name="dtmFineFormazioneSez" required="false" value="<%= dtmFineFormazione%>" 
			                    size="12"/>
					</td>
	        	</tr>	      
				<tr>
					<td class="etichetta">Note formazione</td>
		            <td class="campo">
		                <af:textArea classNameBase="textarea" onKeyUp="fieldChanged();" 
		                name="strnoteFornmazione" maxlength="500" cols="70" rows="4" value="<%=strNoteFormazione%>" readonly="true" />
		            </td>
				</tr> 	   
	        </table>
	    </td>
	</tr>
</table>
