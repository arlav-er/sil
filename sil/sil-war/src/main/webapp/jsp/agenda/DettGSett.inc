<%
String htmlStreamTop = StyleUtils.roundTopTable(canModify);
String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);
%>
<script language="Javascript">
// Per rilevare la modifica dei dati da parte dell'utente
var flagChanged = false;  


function fieldChanged() {
    <%if (canModify) {out.print("flagChanged = true;");}%>
}

function checkSubmit()
{
  if (flagChanged==true){
    if (!confirm("I dati sono cambiati.\nProcedere lo stesso ?")) { return(false); }
    else { return(true); }
  } else { return(true); }

}

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
    dataI = parseInt(a + m + g, 10);
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
    dataF = parseInt(a + m + g, 10);
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
</script>


<p align="center">
  <font color="green"><af:showMessages prefix="MSALVAGSETT"/></font>
  <font color="red"><af:showErrors /></font>
</p>
<%out.print(htmlStreamTop);%>
<table class="main">
<tr>
  <td class="etichetta">Data</td>
  <td class="campo"><b><%=data%></b></td>
</tr>
<af:form name="frm" action="AdapterHTTP" method="POST" onSubmit="controllaDate()">
<input type="hidden" name="PAGE" value="ModGSettPage"/>

<input name="giornoDB" type="hidden" value="<%=giornoDB%>"/>
<input name="meseDB" type="hidden" value="<%=meseDB%>"/>
<input name="annoDB" type="hidden" value="<%=annoDB%>"/>
<input name="giorno" type="hidden" value="<%=giorno%>"/>
<input name="mese" type="hidden" value="<%=mese%>"/>
<input name="anno" type="hidden" value="<%=anno%>"/>
<input name="MOD" type="hidden" value="<%=mod%>"/>

<input type="hidden" name="NUMGSETT" value="<%=numGSett%>"/>
<input type="hidden" name="CODCPI" value="<%=codCpi%>"/>
<input type="hidden" name="PRGGIORNONL" value="<%=prgGiornoNl%>"/>
<input type="hidden" name="NUMKLOGIORNONL" value="<%=numKloGiornoNl%>"/>
<input type="hidden" name="data" value="<%=data%>"/>
<tr>
  <td colspan="2" align="left"><div class="sezione2">Ripetizioni</div></td>
</tr>
<%
String giorniEst[] = {"&nbsp;", "Luned&igrave;", "Marted&igrave;", "Mercoled&igrave;", "Gioved&igrave;", "Venerd&igrave;", "Sabati", "Domeniche"};
%>
<tr>
  <td class=\"etichetta\">
  <% if(numGSett == 7) {%>
          Tutte le
  <% } else { %>
          Tutti i
  <% } %>
  <%=giorniEst[numGSett]%>
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
<%if(!errIns) {%>
  <%if(canModify) {%>
      <tr><td colspan="2">&nbsp;</td></tr>
      <tr>
        <td colspan="2" align="center">
        <input type="submit" name="SALVA" class="pulsanti" value="<%=btnSalva%>"/>&nbsp;&nbsp;
        <input type="reset" class="pulsanti" value="Annulla"/>
        </td>
      </tr>
  <%}%>
<%}%>
</af:form>
<tr><td colspan="2">&nbsp;</td></tr>

<af:form name="backAg" action="AdapterHTTP" method="POST" dontValidate="true">
<input name="PAGE" type="hidden" value="FestiviPage"/>
<input name="giornoDB" type="hidden" value="<%=giornoDB%>"/>
<input name="meseDB" type="hidden" value="<%=meseDB%>"/>
<input name="annoDB" type="hidden" value="<%=annoDB%>"/>
<input name="giorno" type="hidden" value="<%=giorno%>"/>
<input name="mese" type="hidden" value="<%=mese%>"/>
<input name="anno" type="hidden" value="<%=anno%>"/>
<input name="MOD" type="hidden" value="<%=mod%>"/>
<tr>
  <td colspan="2" align="center">
  <input type="submit" class="pulsanti" name="back" value="<%=btnChiudi%>" onClick="return checkSubmit()"/>
  </td>
</tr>
</af:form>
</table>
<%out.print(htmlStreamBottom);%>