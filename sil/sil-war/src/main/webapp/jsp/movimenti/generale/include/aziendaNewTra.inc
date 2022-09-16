
<tr>
	<td class="etichetta">C.F.</td>
  	<td class="campo">
  		<af:textBox classNameBase="input" name="strCodiceFiscaleAzTra" title="Codice Fiscale" size="20" value="<%=strCodiceFiscaleAz%>" readonly="true"/>
  	</td>
</tr>
<tr> 
	<td class="etichetta">Rag. Soc.</td>
  	<td class="campo">
  		<af:textBox classNameBase="input" onKeyUp="fieldChanged();" name="strRagioneSocialeAzTruncTra" title="Ragione Sociale" size="80" value="<%=strRagioneSocialeAzTruncTra%>" readonly="true"/>
  			<af:textBox type="hidden" name="strRagioneSocialeAzTra" value="<%=strRagioneSocialeAzTra%>"/>
  	</td>
</tr>
<tr> 
	<td class="etichetta">Indirizzo</td>
  	<td class="campo">
  		<af:textBox classNameBase="input" onKeyUp="fieldChanged();" name="strIndirizzoUAzVisualizzatoTra" title="Indirizzo" size="80" value='<%=strIndirizzoUAzTra + " (" + strComuneUAzTra + ", " + strCapUAzTra + ")"%>' readonly="true"/> 
  		<input type="hidden" name="strIndirizzoUAzTra" value="<%=strIndirizzoUAzTra%>"/>
  		<input type="hidden" name="strComuneUAzTra" value="<%=strComuneUAzTra%>"/>
  		<input type="hidden" name="strCapUAzTra" value="<%=strCapUAzTra%>"/>
  	</td>
</tr>
 
