<script type="text/javascript">
	// Per rilevare la modifica dei dati da parte dell'utente
	var flagChanged = false;

	function fieldChanged() {
<%if (canModify) {
				out.print("flagChanged = true;");
			}%>
	}

	function annulla() {
		// Se la pagina è già in submit, ignoro questo nuovo invio!
		console.log("entro in annulla");
		if (isInSubmit())
			return;

		if (flagChanged == true) {
			if (!confirm("I dati sono cambiati.\nProcedere lo stesso ?")) {
				return;
			} else {
				document.frmAzione.MODULE.value = "";
				document.frmAzione.MODULE.disabled = true;
				doFormSubmit(document.frmAzione);
			}
		} else {
			document.frmAzione.MODULE.value = "";
			document.frmAzione.MODULE.disabled = true;
			doFormSubmit(document.frmAzione);
		}
	}

	function UpperCaseCodice(objCodice) {
		if (objCodice.value != '') {
			objCodice.value = (objCodice.value).toUpperCase();
		}
	}

	function checkDate() {
		console.log("entro in checkDate");
		if (document.getElementsByName("DATFINEVAL") == null
				|| document.getElementsByName("DATFINEVAL") == "")
			return true;

		var objData1 = document.getElementsByName("DATINIZIOVAL");
		var objData2 = document.getElementsByName("DATFINEVAL");

		strData1 = objData1.item(0).value;
		strData2 = objData2.item(0).value;

		//costruisco la data di inizio
		d1giorno = parseInt(strData1.substr(0, 2), 10);
		d1mese = parseInt(strData1.substr(3, 2), 10) - 1; //il conteggio dei mesi parte da zero 
		d1anno = parseInt(strData1.substr(6, 4), 10);
		data1 = new Date(d1anno, d1mese, d1giorno);

		//costruisce la data di fine
		d2giorno = parseInt(strData2.substr(0, 2), 10);
		d2mese = parseInt(strData2.substr(3, 2), 10) - 1;
		d2anno = parseInt(strData2.substr(6, 4), 10);
		data2 = new Date(d2anno, d2mese, d2giorno);

		ok = true;
		if (data2 < data1) {
			alert("La data di fine validità deve essere successiva o uguale alla data di inizio validità");
			document.getElementsByName("DATINIZIOVAL").item(0).focus();
			ok = false;
		}

		if (ok == true)
			return checkPrestazione();
		else
			return false;
		//return ok;

		//return ok;
	}
	function checkPrestazione() {

		var objData1 = document.getElementsByName("FLAGPOLATTIVA");
		var objData2 = document.getElementsByName("CODICEPRESTAZIONE");

		var flagOperativita = objData1.item(0).checked;
		var prestazione = objData2.item(0).value;
		ok = true;
		if (flagOperativita && prestazione.length == 0) {
			alert("Valorizzare la prestazione");
			ok = false;
		}
		return ok;
	}
</script>

<af:form name="frmAzione" action="AdapterHTTP" method="POST" onSubmit="checkDate()">
	<input type="hidden" name="PAGE" value="GestAzioniPage">
	<input type="hidden" name="MODULE" value="<%=moduleName%>">
	<input type="hidden" name="CDNFUNZIONE" value="<%=_funzione%>">
	<input type="hidden" name="COMBOOBBIETTIVOMISURAYEI" value="<%=comboObbiettivoMisuraYei%>">
    <input type="hidden" name="COMBOTIPOATTIVITA"  value="<%=comboTipoAttivita%>">
    <input type="hidden" name="COMBOPRESTAZIONE"  value="<%=comboPrestazione%>">
    <input type="hidden" name="AZIONE" value="<%=azione%>">
    <input type="hidden" name="PRGAZIONI" value="<%=prgAzioni%>">
    <input type="hidden" name="FLAGMISURAYEI" value="<%=flagMisuraYei%>">
    <input type="hidden" name="FLAGPOLATTIVA" value="<%=flagPolAttiva%>">
  	<input type="hidden" name="DESCOBBIETTIVOMISURAYEI" value="<%=descObbiettivoMisuraYei%>">
  	<input type="hidden" name="DESCTIPOATTIVITA" value="<%=descTipoAttivita%>">
  	<input type="hidden" name="DESCPRESTAZIONE" value="<%=descPrestazione%>">

    <input type="hidden" name="AZIONE_OLD" value="<%=azioneDett%>">  
	<input type="hidden" name="COMBOOBBIETTIVOMISURAYEI_OLD" value="<%=selComboObbiettivoMisuraYeiDett%>">
    <input type="hidden" name="COMBOTIPOATTIVITA_OLD"  value="<%=selComboTipoAttivitaDett%>">
    <input type="hidden" name="COMBOPRESTAZIONE_OLD"  value="<%=selComboPrestazioneDett%>">
    <input type="hidden" name="FLAGMISURAYEI_OLD" value="<%=flagMisuraYeiDett%>">
    <input type="hidden" name="FLAGPOLATTIVA_OLD" value="<%=flagPolAttivaDett%>">

	<p align="center">
	<table class="main">
		<tr>
			<td class="etichetta">Obbiettivo/Misura YEI</td>
			<td class="campo">
				<af:comboBox name="comboObbiettivoMisuraYeiDett" size="1"
					title="Obbiettivo/Misura YEI" multiple="false" required="true"
					focusOn="false" moduleName="ComboObbiettivoMisuraYei"
					classNameBase="input"
					disabled="<%=String.valueOf(!canModify)%>"
					onChange="fieldChanged()" selectedValue="<%=selComboObbiettivoMisuraYeiDett%>"
					addBlank="true" blankValue="" /></td>
		</tr>

		<tr>
			<td class="etichetta">Azione</td>
			<td class="campo"><af:textBox type="text" name="AZIONEDETT"
					title="Azione" required="true" size="50" value="<%=azioneDett%>"
					onKeyUp="fieldChanged();" classNameBase="input"
					readonly="<%=String.valueOf(!canModify)%>" maxlength="50" /></td>
		</tr>



		<!-- fs20141008  start -->
		<tr>
			<td class="etichetta">Tipo Attivit&agrave;</td>
			<td class="campo"><af:comboBox name="COMBOTIPOATTIVITADETT"
					size="1" title="Tipo Attivit&agrave" multiple="false"
					required="true" focusOn="false" moduleName="ComboTipoAttivita"
					classNameBase="input"
					disabled="<%=String.valueOf(!canModify)%>"
					onChange="fieldChanged()" selectedValue="<%=selComboTipoAttivitaDett%>"
					addBlank="true" blankValue="" /></td>
		</tr>

		<tr>
			<td class="etichetta">Prestazione</td>
			<td class="campo"><af:comboBox name="COMBOPRESTAZIONEDETT" size="1"
					title="Prestazione" multiple="false" required="true"
					focusOn="false" moduleName="ComboPrestazione" classNameBase="input"
					disabled="<%=String.valueOf(!canModify)%>"
					onChange="fieldChanged()" selectedValue="<%=selComboPrestazioneDett%>"
					addBlank="true" blankValue="" /></td>
		</tr>


		<tr>
			<td class="etichetta">Flag misura YEI</td>
			<td class="campo"><input type="checkbox" name="FLAGMISURAYEIDETT"
				<%=(flagMisuraYeiDett != null && flagMisuraYeiDett.equals("S")) ? "checked='checked'"
						: ""%> 
				<%=canModify == false ? "disabled='disabled'"	: ""%> 
						/>
			</td>
		</tr>
		
		<tr>
			<td class="etichetta">Flag politica attiva</td>
			<td class="campo"><input type="checkbox" name="FLAGPOLATTIVADETT"
				<%=(flagPolAttivaDett != null && flagPolAttivaDett.equals("S")) ? "checked='checked'"
						: ""%> 
				<%=canModify == false ? "disabled='disabled'"	: ""%> 
						/>
			</td>
		</tr>
	</table>
	<br>
	<table>
		<tr>

			<td align="center">
				<%
					if (canModify) {
				%> <input type="submit" class="pulsanti" name="SALVA"
				value="<%=btnSalva%>"
				onclick="UpperCaseCodice(document.frmAzione.AZIONE);">
				<%
					}
				%>
			</td>

			<td align="center"><input type="button" class="pulsanti"
				name="ANNULLA" value="<%=btnAnnulla%>" onClick="annulla();">
			</td>

		</tr>
	</table>

</af:form>