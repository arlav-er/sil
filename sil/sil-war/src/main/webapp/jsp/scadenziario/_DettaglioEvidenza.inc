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
    
    function checkDataScad()
    {
    	var dataIn = "<%=datDataScad%>";
    	var oggi = new Date();
    	var data = new Date();
    	var dataFrm = document.frmEv.DATDATASCAD.value;
    	
    	g = oggi.getDate();
    	m = oggi.getMonth();
    	a = oggi.getFullYear();
    	oggi = new Date(a, m, g);
    	
    	if(dataFrm != "") {
    		g = parseInt(dataFrm.substr(0,2),10);
    		m = parseInt(dataFrm.substr(3, 2),10)-1;
    		a = parseInt(dataFrm.substr(6,4),10);
  			data = new Date(a, m, g);
  		}
  		
  		if(dataIn!=dataFrm) {
  			// se ho modificato una data questa non puo' essere inferiore a oggi
  			if(data < oggi) { 
  				// Come prolabor non devo poter inserire una data anteriore a oggi
  				alert("La data di scadenza non puo' essere anteriore al giorno attuale");
  				return(false);  			
  			} else { return(true); }
  		} else { return true; }
  		
    }

</script>


<%out.print(htmlStreamTop);%>
<table class="main">
<tr>
  <td class="etichetta">Data scadenza</td>
  <td class="campo">
      <af:textBox name="DATDATASCAD"
      		  title="Data scadenza"
      		  type="date"
              size="11"
              maxlength="10"
              required="true"
              validateOnPost="true"
              onKeyUp="fieldChanged();"
              classNameBase="input"
              readonly="<%=String.valueOf(!canModify)%>"
              value="<%=datDataScad%>"/>
  </td>
</tr>
<tr>
  <td class="etichetta">Tipo ev.</td>
    <td class="campo">
      <af:comboBox name="PRGTIPOEVIDENZA" size="1" title="Tipo evidenza"
                     multiple="false" required="true"
                     focusOn="false" moduleName="MTipiEvidenze"
                     selectedValue="<%=prgTipoEvidenza%>" addBlank="true" blankValue=""
                     classNameBase="input"
                     disabled="<%= String.valueOf( !canModify ) %>"
                     onChange="fieldChanged()"/>    
    </td>
</tr>
<tr class="note">
  <td class="etichetta">Messaggio</td>
  <td class="campo">
    <af:textArea name="STREVIDENZA" 
                 cols="60" 
                 rows="4" 
                 required="true" 
                 title="Messaggio evidenza"
                 maxlength="3000"
                 onKeyUp="fieldChanged();"
                 classNameBase="textarea"
                 readonly="<%=String.valueOf(!canModify)%>"
                 value="<%=strEvidenza%>"
    />
  </td>
</tr>
<tr><td colspan="2">&nbsp;</td></tr>
<%if(canModify) { %>
  <tr>
    <td colspan="2" align="center">
    <input type="submit" class="pulsanti" name="SALVA" value="<%=btnSalva%>">  
    &nbsp;&nbsp;
    <input type="reset" class="pulsanti" name="reset" value="Annulla" onClick="">
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
<% if(testata!=null) { %>
  <div align="center">
  <%testata.showHTML(out);%>
  </div>
<%}%>

