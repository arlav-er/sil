<table class="main">
	<tr>
    	<td class="etichetta">Data inizio validit&agrave;</td>
    	<td colspan="6" class="campo">
      		<af:textBox classNameBase="input" type="date" name="inizio" value="<%=datinizioval%>" validateOnPost="true"   
                  required="true"  onKeyUp="fieldChanged();" size="11" maxlength="10" title="Data d'inizio"
            />
        </td>
	</tr>
	<tr>
    	<td class="etichetta">Data fine validit&agrave;</td>  
    	<td colspan="6" class="campo">
      		<af:textBox classNameBase="input" type="date" name="fine" value="<%=datfineval%>" validateOnPost="true"   
                  required="false" onKeyUp="fieldChanged();" size="11" maxlength="10" title="Data di fine"
            />
        </td>
	</tr>
	<% 
	String typeValIsee = "integer";
	if (configIBAN.equals("1")) { 
		typeValIsee = "float";
	} %>
	<tr>
    	<td class="etichetta">Valore ISEE</td>
    	<td colspan="6" class="campo"><af:textBox classNameBase="input" type="<%=typeValIsee%>" name="numvaloreisee" value="<%=strNumValoreIsee%>"  
                   required="true" onKeyUp="fieldChanged();" size="20" maxlength="38" title="Valore ISEE" validateOnPost="true" 
                   readonly="<%=String.valueOf(readOnlyStr)%>"/>
        </td>
	</tr>
	<tr>
    	<td class="etichetta">Punteggio ISEE</td>
    	<td colspan="6" class="campo"><af:textBox classNameBase="input" type="integer" name="numpuntiisee" value="<%=strPuntValoreIsee%>"   
                    required="false" onKeyUp="fieldChanged();" validateOnPost="true" size="20" maxlength="38" title="Punteggio ISEE"
                    readonly="true"/>
        </td>
	</tr>
	<tr>
    	<td class="etichetta">Anno di riferimento del reddito</td>
    	<td colspan="6" class="campo">
     		<af:textBox classNameBase="input" type="integer" name="numanno" value="<%=strNumAnno%>" validateOnPost="true"   
        			required="false" onKeyUp="fieldChanged();" size="5" maxlength="4" title="Anno di riferimento"
					readonly="<%=String.valueOf(readOnlyStr)%>"/>
        </td>
	</tr>
	<% if (configIBAN.equals("1")) { %>
	<tr>
    	<td class="etichetta">IBAN</td>
    	<td colspan="6" class="campo">
			<af:textBox classNameBase="input" title="Codice nazione" type="text" name="strIbanNazione" value="<%=strIbanNazione%>" 
					onKeyUp="fieldChanged();upperInnerTextCampo(this);" size="3" maxlength="2" readonly="<%= String.valueOf(readOnlyStr) %>"/>			
			<af:textBox classNameBase="input" title="Codice controllo" validateOnPost="true" type="integer" name="strIbanControllo" 
					value="<%=strIbanControllo%>" onKeyUp="fieldChanged();" size="3" maxlength="2" readonly="<%= String.valueOf(readOnlyStr) %>"/>		
			<af:textBox classNameBase="input" title="CIN" type="text" name="strCinLav" value="<%=strCinLav%>" 
					onKeyUp="fieldChanged();upperInnerTextCampo(this);" size="2" maxlength="1" readonly="<%= String.valueOf(readOnlyStr) %>"/>
			<af:textBox classNameBase="input" title="ABI" validateOnPost="true" type="integer" name="strAbiLav" value="<%=strAbiLav%>" 
					onKeyUp="fieldChanged();" size="6" maxlength="5" readonly="<%= String.valueOf(readOnlyStr) %>"/>
			<af:textBox classNameBase="input" title="CAB" validateOnPost="true" type="integer" name="strCabLav" value="<%=strCabLav%>" 
					onKeyUp="fieldChanged();" size="6" maxlength="5" readonly="<%= String.valueOf(readOnlyStr) %>"/>
			<af:textBox classNameBase="input" type="text" name="strCCLav" value="<%=strCCLav%>" 
					onKeyUp="fieldChanged();upperInnerTextCampo(this);" size="15" maxlength="12" readonly="<%= String.valueOf(readOnlyStr) %>"/>
        </td>
	</tr>	
	<% } %>
	<tr>
    	<td class="etichetta">Note</td>
    	<td colspan="6" class="campo">	
			<af:textArea classNameBase="textarea" name="strnota" value="<%=strNota%>"
                 cols="60" rows="4" maxlength="2000" onKeyUp="fieldChanged();"
                 readonly="<%=String.valueOf(readOnlyStr)%>"/>
      	</td>
	</tr>  	
  	
</table>