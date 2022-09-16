<%-- Campi di input usati da pagine di dettaglio e di nuovo --%>

<%= htmlStreamTop %>
<table class="main">

	<tr>
        <td class="etichetta">
       		xxx descrizione
        </td>
		<td class="campo">
		
			<%-- Input di un campo di testo --%>
			<af:textBox name="xxx" type="text"
						title="xxx tooltip"
						value="<%= strXxx %>"
						size="40" maxlength="101"
						required="true"
						classNameBase="input"
						readonly="<%= readonly %>"
						onKeyUp="fieldChanged()"
						/>


			<%-- Input di una data --%>
			<af:textBox name="xxx" type="date"
						title="xxx tooltip"
						value="<%= strXxx %>"
						validateOnPost="true"
						size="11" maxlength="10"
						required="true"
						classNameBase="input"
						readonly="<%= readonly %>"
						onKeyUp="fieldChanged()" />


			<%-- Input a scelta con combo-box --%>
			<% String xxxValue = SourceBeanUtils.getAttrStrNotNull(dettModule, "codxxx"); %>
			<af:comboBox name="cdnxxx"
						title="xxx tooltip"
						moduleName="ComboxxxModule" 
						selectedValue="<%= strXxx %>"
						addBlank="true"
						required="true"
						disabled="<%= readonly %>"
						classNameBase="input"
						onChange="fieldChanged()" />


			<%-- Visualizzazione sola descrizione campo, con codice nascosto --%>
			<%
			String xxxCod = SourceBeanUtils.getAttrStrNotNull(dettModule, "XXX_COD");
			String xxxDes = SourceBeanUtils.getAttrStrNotNull(dettModule, "XXX_DES");
			%>
			<input type="hidden" name="xxxCod" value="<%= xxxCod %>" />
			<af:textBox name="xxxDes" type="text"
						value="<%= xxxDes %>" readonly="true"
						classNameBase="input"
						size="60" maxlength="100" />
		</td>
	</tr>

	<%-- Linea di separazione dei dati --%>
	<tr>
		<td colspan="4">
			<div class="sezione2"></div>
		</td>
	</tr>
