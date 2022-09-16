	<tr>
        <td class="etichetta">
       		Campo STRSPECIFICA
        </td>
		<td class="campo">
			<af:textBox name="STRSPECIFICA" type="text"
						title="inserire il valore di STRSPECIFICA"
						value="<%= strSpecifica %>"
						size="40" maxlength="101"
						required="true"
						classNameBase="input"
						readonly="<%= readonly %>"
						onKeyUp="fieldChanged()"
						/>
		</td>
	</tr>

	
	<tr>
        <td class="etichetta">
       		xxx descrizione
        </td>
		<td class="campo">
			<af:textBox name="xxx" type="text"
						title="xxx tooltip"
						value="<%= strXxx %>"
						size="40" maxlength="101"
						required="false"
						classNameBase="input"
						readonly="<%= readonly %>"
						onKeyUp="fieldChanged()"
						/>
		</td>
	</tr>
