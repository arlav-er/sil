<script type="text/javascript">
    // Per rilevare la modifica dei dati da parte dell'utente
    var flagChanged = false;  
    
    
    function fieldChanged() {
        <%if (canModify) {out.print("flagChanged = true;");}%>
    }
</script>
    
<%
String htmlStreamTop = StyleUtils.roundTopTable(canModify);
String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);
%>
<script language="Javascript">
function controllaDate()
{
  var datePat = /^(\d{1,2})(\/|-)(\d{1,2})(\/|-)(\d{4})$/;
  var di = document.frm.DATINIZIOVAL.value;
  var df = document.frm.DATFINEVAL.value;
  var ok1, ok2;
  var s, g, m, a;
  var dataI, dataF;
  
  var matchArray = di.match(datePat);
  if(matchArray == null) { 
    ok1 = false;
    dataI = "";
  } else { 
    ok1 = true;
    s = matchArray[2];
    var tmp1 = di.split(s);
    g = tmp1[0];
    m = tmp1[1];
    a = tmp1[2];
    dataI = parseInt(a + m + g,10);
  }

  matchArray = df.match(datePat);
  if(matchArray == null) { 
    ok2 = false;
    dataF = "";
  } else { 
    ok2 = true;
    s = matchArray[2];
    var tmp2 = df.split(s);
    g = tmp2[0];
    m = tmp2[1];
    a = tmp2[2];
    dataF = parseInt(a + m + g,10);
  }
  
  if(ok1 && ok2) {
    if(dataI <= dataF) { return(true); }
    else { 
      alert("La data di Fine Validità deve essere maggiore o uguale alla data di Inizio Validità"); 
      return(false);
    }
  } else {
    return(true);
  }
}

function chiudi()
{
  // Se la pagina è già in submit, ignoro questo nuovo invio!
  if (isInSubmit()) return;

  if (flagChanged==true){
          if (!confirm("I dati sono cambiati.\nProcedere lo stesso ?")) { return; }
          else {
                document.frm.MODULE.value = "";
                document.frm.MODULE.disabled = true;
                document.frm.reset();
                doFormSubmit(document.frm);
          }
  } else {
      document.frm.MODULE.value = "";
      document.frm.MODULE.disabled = true;
      document.frm.reset();
      doFormSubmit(document.frm);
  }
}
</script>

<%out.print(htmlStreamTop);%>
<table class="main">
<af:form name="frm" action="AdapterHTTP" method="POST" onSubmit="controllaDate()">
<input type="hidden" name="PAGE" value="ListSettimanaTipoPage"/>
<input type="hidden" name="MODULE" value="<%=moduleName%>"/>

<input type="hidden" name="CODCPI" value="<%=codCpi%>"/>
<input type="hidden" name="PRGSETTIPO" value="<%=prgSettTipo%>"/>
<input type="hidden" name="NUMKLO" value="<%=numKlo%>"/>

<tr>
  <td class="etichetta">Descrizione</td>
  <td class="campo">
    <af:textBox name="STRDESCRIZIONESETTIMANA"
                value="<%=strDescrizioneSettimana%>"
                size="30"
                maxlength="30"
                title="Descrizione"
                required="true"
                type="text"
                onKeyUp="fieldChanged();"
                classNameBase="input"
                readonly="<%=String.valueOf(!canModify)%>"
    />
  </td>
</tr>
<tr>
  <td colspan="2" align="left"><div class="sezione2">Periodo di Validit&agrave;</div></td>
</tr>
<tr>
  <td class="etichetta">Data Inizio</td>
  <td class="campo">
  <af:textBox name="DATINIZIOVAL"
                value="<%=datInizioVal%>"
                size="11"
                maxlength="10"
                title="Data Inizio Validit&agrave;"
                alt="Data Inizio"
                type="date"
                validateOnPost="true"
                onKeyUp="fieldChanged();"
                classNameBase="input"
                readonly="<%=String.valueOf(!canModify)%>"
   />
  </td>
</tr>
<tr>
  <td class="etichetta">Data Fine</td>
  <td class="campo">
    <af:textBox name="DATFINEVAL"
                value="<%=datFineVal%>"
                size="11"
                maxlength="10"
                title="Data Fine Validit&agrave;"
                alt="Data Fine"
                type="date"
                validateOnPost="true"
                onKeyUp="fieldChanged();"
                classNameBase="input"
                readonly="<%=String.valueOf(!canModify)%>"
   />
  </td>
</tr>

<tr><td colspan="2">&nbsp;</td></tr>
<tr>
  <td colspan="2" align="center">
  <input type="submit" name="SALVA" class="pulsanti" value="<%=btnSalva%>"/>&nbsp;&nbsp;
  <input type="button" class="pulsanti" value="<%=btnChiudi%>" onClick="chiudi()"/>
  </td>
</tr>
</af:form>
</table>
<%out.print(htmlStreamBottom);%>