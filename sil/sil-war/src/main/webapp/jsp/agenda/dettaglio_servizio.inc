<script type="text/javascript">
  // Per rilevare la modifica dei dati da parte dell'utente
  var flagChanged = false;  
  
  
  function fieldChanged() {
      <%if (canModify) {out.print("flagChanged = true;");}%>


      //var myselect = document.getElementsByName("CODICETIPOATTIVITA");
      //var module = document.getElementsByName("MODULE").item(0).value;
      //console.log("tipo attivita PRIMA " + document.frmServizio.TIPOATTIVITA.value);
      //console.log("tipo attivita DOPO  " + document.frmServizio.CODICETIPOATTIVITA.options[document.frmServizio.CODICETIPOATTIVITA.selectedIndex].value);
      //if (document.frmServizio.TIPOATTIVITA.value==document.frmServizio.CODICETIPOATTIVITA.options[document.frmServizio.CODICETIPOATTIVITA.selectedIndex].value && module == "MAggiornaServizio");
	  //    alert("Attenzione: è necessario eseguire il re­invio delle SAP che utilizzano il servizio ed il tipo attività modificato");
  }

  
  function annulla() {
	// Se la pagina è già in submit, ignoro questo nuovo invio!
	if (isInSubmit()) return;

    if (flagChanged==true){
          if (!confirm("I dati sono cambiati.\nProcedere lo stesso ?")) { return; }
          else {
              document.frmServizio.MODULE.value = "";
              document.frmServizio.MODULE.disabled = true;
              doFormSubmit(document.frmServizio);
          }
    } else {
          document.frmServizio.MODULE.value = "";
          document.frmServizio.MODULE.disabled = true;
          doFormSubmit(document.frmServizio);
    }
  }

  function UpperCaseCodice(objCodice) {
    if (objCodice.value != '') {
      objCodice.value=(objCodice.value).toUpperCase();
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

		if (ok == true)
			return checkPrestazione();
		else
			return false;		
		//return ok;
	   
	  //return ok;
}
  function checkPrestazione() {
	  											   
	  	var objData1 = document.getElementsByName("FLGPOLATTIVA");
	  	var objData2 = document.getElementsByName("CODICEPRESTAZIONE");

		var flagOperativita=objData1.item(0).checked;
		var prestazione=objData2.item(0).value;
		console.log("flagOperativita " + flagOperativita);
		console.log("prestazione " + prestazione);
	    ok=true;
		if (flagOperativita && prestazione.length == 0) {
		      alert("Valorizzare la prestazione");
		      ok=false;
		   }
		  return ok;
}

</script>

<af:form name="frmServizio" action="AdapterHTTP" method="POST" onSubmit="checkDate()">
<input type="hidden" name="PAGE" value="GestServiziPage">
<input type="hidden" name="MODULE" value="<%=moduleName%>">
<input type="hidden" name="CDNFUNZIONE" value="<%=_funzione%>">
<input type="hidden" name="PRGSERVIZIOAREA" value="<%=prgServizioArea%>">
<input type="hidden" name="NUMKLOSERVIZIO" value="<%=strNumKloServizio%>">
<input type="hidden" name="DATA_DEFAULT" value="<%=strDataDefault%>">
<input type="hidden" name="CODSERVIZIO_OLD" value="<%=codServizio%>">

<input  type="hidden" name="DESCAREA" value="<%=descArea%>"/>    
<input  type="hidden" name="CODAREA" value="<%=codArea%>"/>            
<input  type="hidden" name="SERVIZIO" value="<%=servizio%>"/>            
<input  type="hidden" name="VALIDI" value="<%=validi%>"/>  
<input  type="hidden" name="DESCPRESTAZIONE" value="<%=descPrestazione%>"/>  
<input  type="hidden" name="PRESTAZIONE" value="<%=prestazione%>"/>
<input  type="hidden" name="TIPOATTIVITA" value="<%=tipoAttivita%>"/>    

<input  type="hidden" name="DESCTIPOATTIVITA" value="<%=descTipoAttivita%>"/>  
<input  type="hidden" name="POLATTIVA" value="<%=polAttiva%>"/>  
<input type="hidden" name="PRGTIPOATTIVITA" value="<%=prgTipoAttivita%>"/>
<input type="hidden" name="PRGPRESTAZIONE" value="<%=prgPrestazione%>"/>

<p align="center">
<table class="main">
<tr>
<td class="etichetta">Area Servizio</td>
<td class="campo">
  <af:comboBox name="CODICE" size="1" title="Area Servizio"
                     multiple="false" required="true"
                     focusOn="false" moduleName="ComboAreaServizio"
                     classNameBase="input"
                     disabled="<%= String.valueOf( !canModify ) %>"
                     onChange="fieldChanged()"
                     selectedValue="<%=prgServizioArea%>" addBlank="true" blankValue=""/>
</td>
</tr>

<tr>
<td class="etichetta">Codice <%=labelServizio %></td>
<td class="campo">
<af:textBox 
            type="text" 
            name="CODSERVIZIO" 
            title="Codice" 
            required="true" 
            size="10" 
            value="<%=codServizio%>" 
            onKeyUp="fieldChanged();"
            classNameBase="input"
            readonly="<%=String.valueOf(!canModify)%>"
            maxlength="8"/>
</td>
</tr>

<tr>
<td class="etichetta"><%=labelServizio %></td>
<td class="campo">
<af:textBox 
            type="text" 
            name="STRDESCRIZIONE" 
            title="<%=labelServizio %>" 
            required="true" 
            size="50" 
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
<af:textBox title="Data inizio validità" type="date" required="true" name="DATINIZIOVAL" value="<%=dtmInizioVal%>" size="11" maxlength="10" onKeyUp="fieldChanged();"
            classNameBase="input"
            readonly="<%=String.valueOf(!canModify)%>"
            validateOnPost="true"/>
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

<!-- fs20141008  start -->
<tr>
<td class="etichetta">Tipo Attivit&agrave;</td>
<td class="campo">
  <af:comboBox name="CODICETIPOATTIVITA" size="1" title="Tipo Attivit&agrave"
                     multiple="false" required="false"
                     focusOn="false" moduleName="ComboTipoAttivita"
                     classNameBase="input"
                     disabled="<%= String.valueOf( !canModify ) %>"
                     onChange="fieldChanged()"
                     selectedValue="<%=prgTipoAttivita%>" addBlank="true" blankValue=""/>
</td>
</tr>

<tr>
<td class="etichetta">Prestazione</td>
<td class="campo">
  <af:comboBox name="CODICEPRESTAZIONE" size="1" title="Prestazione"
                     multiple="false" required="false"
                     focusOn="false" moduleName="ComboPrestazione"
                     classNameBase="input"
                     disabled="<%= String.valueOf( !canModify ) %>"
                     onChange="fieldChanged()"
                     selectedValue="<%=prgPrestazione%>" addBlank="true" blankValue=""/>
</td>
</tr>


<tr>
    <td class="etichetta">Flag Pol. Attiva</td>
		<td class="campo">
			<input type="checkbox" name="FLGPOLATTIVA" <%=(polAttiva!=null&&polAttiva.equals("S"))?"checked='checked'":""%>/>
		</td>	
  </tr>
<!-- fs20141008  end -->

<%if (visFlgPatronato) {%>
<tr>
    <td class="etichetta">Destinato ai Patronati</td>
		<td class="campo">
			<input type="checkbox" name="FLGPATRONATO" <%=(flgPatronato!=null&&flgPatronato.equals("S"))?"checked='checked'":""%> value="S"/>
		</td>	
  </tr>
<%}%>
</table>
<br>
<table>
<tr>
  
<td align="center">
<%if(canModify) {%>
    <input type="submit" class="pulsanti" name="SALVA" value="<%=btnSalva%>" onclick="UpperCaseCodice(document.frmServizio.CODSERVIZIO);">
<%}%>
</td>

<td align="center">
<input type="button" class="pulsanti" name="ANNULLA" value="<%=btnAnnulla%>" onClick="annulla();">
</td>

</tr>
</table>

</af:form>