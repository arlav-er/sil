<%
String htmlStreamTop = StyleUtils.roundTopTable(canModify);
String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);
%>
<script type="text/javascript">
    // Per rilevare la modifica dei dati da parte dell'utente
    var flagChanged = false;  

    function fieldChanged() {
        <%if (canModify) {out.print("flagChanged = true;");}%>
    } 
    
    function conferma(azione){
	  // Se la pagina è già in submit, ignoro questo nuovo invio!
	  if (isInSubmit()) return;

      if (azione=="BACK"){
        if (flagChanged==true){
          if (!confirm("I dati sono cambiati.\nProcedere lo stesso ?")) { return; }
          else {
              document.frmEv.MODULE.value = "";
              document.frmEv.MODULE.disabled = true;
              doFormSubmit(document.frmEv);
			   }
        } else {
          document.frmEv.MODULE.value = "";
          document.frmEv.MODULE.disabled = true;
          doFormSubmit(document.frmEv);
        }
      }
      
    }
    
</script>



<%out.print(htmlStreamTop);%>
<table class="main">
<tr>
  <td class="etichetta">Codice</td>
  <td class="campo">
      <af:textBox name="CODTIPOEVIDENZA"
      		  title="Codice evidenza"
      		  type="text"
              size="4"
              maxlength="2"
              required="true"
              validateOnPost="true"
              onKeyUp="fieldChanged();"
              classNameBase="input"
              readonly="<%=String.valueOf(!canModify)%>"
              value="<%=codTipoEvidenza%>"/>
  </td>
</tr>
<tr class="note">
  <td class="etichetta">Descrizione</td>
  <td class="campo">
    <af:textArea name="STRDESCRIZIONE" 
                 cols="60" 
                 rows="4" 
                 required="true" 
                 title="Descr. tipo evidenza"
                 maxlength="30"
                 onKeyUp="fieldChanged();"
                 classNameBase="input"
                 readonly="<%=String.valueOf(!canModify)%>"
                 value="<%=strDescrizione%>"
    />
  </td>
</tr>
<tr>
  <td class="etichetta">Data inizio val.</td>
  <td class="campo">
      <af:textBox name="DATINIZIOVAL"
      		  title="Data inizio val."
      		  type="date"
              size="11"
              maxlength="10"
              required="true"
              validateOnPost="true"
              onKeyUp="fieldChanged();"
              classNameBase="input"
              readonly="<%=String.valueOf(!canModify)%>"
              value="<%=datInizioVal%>"/>
  </td>
</tr>
<tr>
  <td class="etichetta">Data fine val.</td>
  <td class="campo">
      <af:textBox name="DATFINEVAL"
              title="Data fine val."
      		  type="date"
              size="11"
              maxlength="10"
              required="true"
              validateOnPost="true"
              onKeyUp="fieldChanged();"
              classNameBase="input"
              readonly="<%=String.valueOf(!canModify)%>"
              value="<%=datFineVal%>"/>
  </td>
</tr>
<tr><td colspan="2">&nbsp;</td></tr>
<%if(canModify) { %>
  <tr>
    <td colspan="2" align="center">
    <input type="submit" class="pulsanti" name="SALVA" value="<%=btnSalva%>">  
    &nbsp;&nbsp;
    <input type="reset" class="pulsanti" name="reset" value="Annulla" onClick="return annulla();">
    </td>
  </tr>
  <tr><td colspan="2">&nbsp;</td></tr>
<%}%>
<tr>
  <td colspan="2" align="center">
  <input type="button" class="pulsanti" name="INDIETRO" value="<%=btnChiudi%>" onCLick="javascript:conferma('BACK');">  
  </td>
</tr>
</table>

<%out.print(htmlStreamBottom);%>

