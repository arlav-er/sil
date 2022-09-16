<script type="text/javascript">
  // Per rilevare la modifica dei dati da parte dell'utente
  var flagChanged = false;  
  
  
  function fieldChanged() {
      <%if (canModify) {out.print("flagChanged = true;");}%>
  }
  
  function annulla() {
	// Se la pagina è già in submit, ignoro questo nuovo invio!
	if (isInSubmit()) return;

    if (flagChanged==true){
          if (!confirm("I dati sono cambiati.\nProcedere lo stesso ?")) { return; }
          else {
              document.frmAmbiente.MODULE.value = "";
              document.frmAmbiente.MODULE.disabled = true;
              doFormSubmit(document.frmAmbiente);
          }
    } else {
      <%
		String token = "_TOKEN_" + "GESTAMBIENTIPAGE";
		String urlDiLista = (String) sessionContainer.getAttribute(token.toUpperCase());
		if (urlDiLista != null) {
			%>
			setWindowLocation("AdapterHTTP?<%= urlDiLista %>");
			<%
		}
      %>
    }
  }
  
   function checkDate() {
  	
  	if( document.getElementsByName("DATFINEVAL")==null ||  	document.getElementsByName("DATFINEVAL")=="" )
  		return true;
  	
  	var objData1 = document.getElementsByName("DATINIZIOVAL");
  	var objData2 = document.getElementsByName("DATFINEVAL");
 	

	  strData1=objData1.item(0).value;
	  strData2=objData2.item(0).value;
	
	  //costruisco la data di inizio
	  d1giorno=parseInt(strData1.substr(0,2),10);
	  d1mese=parseInt(strData1.substr(3, 2),10)-1; //il conteggio dei mesi parte da zero 
	  d1anno=parseInt(strData1.substr(6,4),10);
	  data1=new Date(d1anno, d1mese, d1giorno);
	
	  //costruisce la data di fine
	  d2giorno=parseInt(strData2.substr(0,2),10);
	  d2mese=parseInt(strData2.substr(3,2),10)-1;
	  d2anno=parseInt(strData2.substr(6,4),10);
	  data2=new Date(d2anno, d2mese, d2giorno);
	  
	  ok=true;
	  if (data2 < data1) {
	      alert("La data di fine validità deve essere successiva o uguale alla data di inizio validità");
	      document.getElementsByName("DATINIZIOVAL").item(0).focus();
	      ok=false;
	   }
	  return ok;
}

</script>

<af:form name="frmAmbiente" action="AdapterHTTP" method="POST" onSubmit="checkDate()">
<input type="hidden" name="PAGE" value="GestAmbientiPage">
<input type="hidden" name="MODULE" value="<%=moduleName%>">
<input type="hidden" name="CODCPI" value="<%=codCpi%>">
<input type="hidden" name="CDNFUNZIONE" value="<%=_funzione%>">
<input type="hidden" name="PRGAMBIENTE" value="<%=prgAmbiente%>">
<input type="hidden" name="NUMKLOAMBIENTE" value="<%=strNumKloAmbiente%>">
<input type="hidden" name="DATA_DEFAULT" value="<%=strDataDefault%>">
<input type="hidden" name="LIST_PAGE" value="<%=strListPage%>"/>
<input type="hidden" name="MESSAGE" value="<%=strMessage%>"/>

<p align="center">
<table class="main">
<tr>
<td class="etichetta">Descrizione</td>
<td class="campo">
<af:textBox 
            type="text" 
            name="STRDESCRIZIONE" 
            title="Descrizione" 
            required="true" 
            size="30" 
            maxlength="100"
            onKeyUp="fieldChanged();"
            classNameBase="input"
            readonly="<%=String.valueOf(!canModify)%>"
            value="<%=strDescrizione%>"/>
</td>
</tr>

<tr>
<td class="etichetta">Data Inizio Validità</td>
<td class="campo">
<af:textBox 
            title="Data inizio validità" 
            type="date" 
            required="true" 
            name="DATINIZIOVAL" 
            value="<%=dtmInizioVal%>" 
            size="11" 
            maxlength="10" 
            validateOnPost="true"
            onKeyUp="fieldChanged();"
            classNameBase="input"
            readonly="<%=String.valueOf(!canModify)%>"
            />
</td>
</tr>

<tr>
<td class="etichetta">Data Fine Validità</td>
<td class="campo">
<af:textBox 
            title="Data fine validità" 
            type="date" 
            required="false" 
            name="DATFINEVAL" 
            value="<%=dtmFineVal%>" 
            size="11" 
            maxlength="10" 
            onKeyUp="fieldChanged();"
            classNameBase="input"
            readonly="<%=String.valueOf(!canModify)%>"
            validateOnPost="true"/>
</td>
</tr>

<tr>
<td class="etichetta">Postazioni di lavoro</td>
<td class="campo">
<af:textBox 
            type="text" 
            name="NUMCAPACITA" 
            value="<%=strCapacita%>" 
            size="5" 
            maxlength="3"
            required="true" 
            onKeyUp="fieldChanged();"
            classNameBase="input"
            readonly="<%=String.valueOf(!canModify)%>"
            title="Numero Capacità"/>
</td>
</tr>

<tr>
<td class="etichetta">Capienza</td>
<td class="campo">
<af:textBox 
            type="text" 
            name="NUMCAPIENZA" 
            value="<%=strCapienza%>" 
            size="5" 
            maxlength="3"
            required="true" 
            onKeyUp="fieldChanged();"
            classNameBase="input"
            readonly="<%=String.valueOf(!canModify)%>"
            title="Numero Capienza"/>
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
<input type="button" class="pulsanti" name="ANNULLA" value="<%=btnAnnulla%>" onClick="annulla();">
</td>

</tr>
</table>

</af:form>