<script type="text/javascript">
  // Per rilevare la modifica dei dati da parte dell'utente
  var flagChanged = false;  
  
  
  function fieldChanged() {
      <%if (canModify) {out.print("flagChanged = true;");}%>
  }

  function annulla() {
	  // Se la pagina è già in submit, ignoro questo nuovo invio!
	  if (isInSubmit()) return;

      //document.frmNuovaAssegnazione.MODULE.value = "";
      //document.frmNuovaAssegnazione.MODULE.disabled = true;
      //doFormSubmit(document.frmNuovaAssegnazione);
      
      <%
		String token = "_TOKEN_" + "GestAssegnazionePage";
		String urlDiLista = (String) sessionContainer.getAttribute(token.toUpperCase());
		if (urlDiLista != null) {
			%>
			setWindowLocation("AdapterHTTP?<%= urlDiLista %>");
			<%
		}
      %>
  }

</script>
<%
  String newPrgSpiAssegnazione="";
  String newprgAmbiente="";
  if (prgSpiAssegnazione != null) {
    newPrgSpiAssegnazione=prgSpiAssegnazione.toString();
  }
  if (prgAmbiente != null) {
    newprgAmbiente=prgAmbiente.toString();
  }
%>

<af:form name="frmNuovaAssegnazione" action="AdapterHTTP" method="POST">
<input type="hidden" name="PAGE" value="GestAssegnazionePage">
<input type="hidden" name="MODULE" value="<%=moduleName%>">
<input type="hidden" name="CODCPI" value="<%=CODCPI%>">
<input type="hidden" name="PRGASSEGNAZIONE" value="<%=prgAssegnazione%>">
<input type="hidden" name="NUMKLOASSEGNAZIONE" value="<%=numKloAssegnazione%>">
<input type="hidden" name="LIST_PAGE" value="<%=strListPage%>"/>
<input type="hidden" name="MESSAGE" value="<%=strMessage%>"/>

<p align="center">
<table class="main">

<tr>
  <td class="etichetta"><%=labelServizio%></td>
    <td class="campo">
      <af:comboBox name="CODSERVIZIO" size="1" title="<%=labelServizio%>"
                     multiple="false" required="true"
                     focusOn="false" moduleName="COMBO_SERVIZIO_SCAD"
                     selectedValue="<%=codServizio%>" addBlank="true" blankValue=""
                     classNameBase="input"
                     disabled="<%= String.valueOf( !canModify ) %>"
                     onChange="fieldChanged()"/>
    </td>
</tr>

<tr>
  <td class="etichetta">Operatore</td>
    <td class="campo">
      <af:comboBox name="PRGSPIASSEGNAZIONE" size="1" title="Operatore"
                     multiple="false" required="true"
                     focusOn="false" moduleName="COMBO_SPI_SCAD"
                     selectedValue="<%=newPrgSpiAssegnazione%>" addBlank="true" blankValue=""
                     classNameBase="input"
                     disabled="<%= String.valueOf( !canModify ) %>"
                     onChange="fieldChanged()"/>
    </td>
</tr>

<tr>
  <td class="etichetta">Ambiente</td>
    <td class="campo">
      <af:comboBox name="PRGAMBIENTE" size="1" title="Ambiente"
                     multiple="false" required="true"
                     focusOn="false" moduleName="COMBO_AMBIENTE"
                     selectedValue="<%=newprgAmbiente%>" addBlank="true" blankValue=""
                     classNameBase="input"
                     disabled="<%= String.valueOf( !canModify ) %>"
                     onChange="fieldChanged()"/>
    </td>
</tr>
</table>
<br>
<table>
<tr>
  
  <td align="center">
    <%if(canModify) {%>
      <input type="submit" class="pulsanti" name="SALVA" value="<%=btnSalva%>">
    <%}%>
  </td>

  <td align="center">
    <input type="button" class="pulsanti" name="ANNULLA" value="<%=btnAnnulla%>" onClick="annulla()">
  </td>

</tr>
</table>

</af:form>