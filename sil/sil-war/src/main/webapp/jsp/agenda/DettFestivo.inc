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

function checkRipetizione(t)
{
  var otr_tit=document.getElementById("titolo_ripetizioni");
  var otr_inizio=document.getElementById("tr_inizio");
  var otr_fine=document.getElementById("tr_fine");
  var oDataIn = document.getElementsByName("DATINIZIOVAL");
  var oDataFin = document.getElementsByName("DATFINEVAL");
  var oNumAnno = document.getElementsByName("NUMAAAA");
  if(t=="O") {
    otr_tit.style.display="none";
    otr_inizio.style.display="none";
    otr_fine.style.display="none";
    oDataIn[0].value="<%=data%>";
    oDataFin[0].value="<%=data%>";
    oNumAnno[0].value="<%=annoDB%>";
  } else {
    otr_tit.style.display="";
    otr_inizio.style.display="";
    otr_fine.style.display="";
    oDataIn[0].value="<%=data%>";
    oDataFin[0].value="";
    oNumAnno[0].value="";
  }
  fieldChanged();
}

function annullaClick()
{
  document.frm.reset();
  var i, l, v;
  v="";
  l = document.frm.tipo.length;
  for(i=0; i < l; i++) {
    if(document.frm.tipo[i].checked) { v = document.frm.tipo[i].value; }
  }
  checkRipetizione(v);
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
</script>
<p align="center">
  <font color="green"><af:showMessages prefix="MSALVAFESTIVO"/></font>
  <font color="red"><af:showErrors /></font>
</p>

<af:form name="frm" action="AdapterHTTP" method="POST" onSubmit="controllaDate()">
<input type="hidden" name="PAGE" value="ModGNLPage"/>

<input name="giornoDB" type="hidden" value="<%=giornoDB%>"/>
<input name="meseDB" type="hidden" value="<%=meseDB%>"/>
<input name="annoDB" type="hidden" value="<%=annoDB%>"/>
<input name="giorno" type="hidden" value="<%=giorno%>"/>
<input name="mese" type="hidden" value="<%=mese%>"/>
<input name="anno" type="hidden" value="<%=anno%>"/>
<input name="MOD" type="hidden" value="<%=mod%>"/>

<input type="hidden" name="NUMGG" value="<%=numGG%>"/>
<input type="hidden" name="NUMMM" value="<%=numMM%>"/>
<input type="hidden" name="NUMAAAA" value="<%=numAA%>"/>
<input type="hidden" name="CODCPI" value="<%=codCpi%>"/>
<input type="hidden" name="PRGGIORNONL" value="<%=prgGiornoNl%>"/>
<input type="hidden" name="NUMKLOGIORNONL" value="<%=numKloGiornoNl%>"/>
<input type="hidden" name="data" value="<%=data%>"/>

<%out.print(htmlStreamTop);%>
<table class="main">
<tr>
  <td class="etichetta">Data</td>
  <td class="campo">
  	<b><%=data%></b>
  	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
  	Chiusura non festiva&nbsp;
    <af:comboBox 
	      name="FLGCHIUSURANONFESTIVA"
	      classNameBase="input"
	      onChange="javaScript: fieldChanged();">
	      <option value=""  <% if ( "".equalsIgnoreCase(FLGCHIUSURANONFESTIVA) )  { %>SELECTED="true"<% } %> ></option>      
	      <option value="S" <% if ( "S".equalsIgnoreCase(FLGCHIUSURANONFESTIVA) ) { %>SELECTED="true"<% } %> >Sì</option>
	      <option value="N" <% if ( "N".equalsIgnoreCase(FLGCHIUSURANONFESTIVA) ) { %>SELECTED="true"<% } %> >No</option>      
    </af:comboBox>       	
  </td>
</tr>
<tr>
  <td colspan="2" align="left"><div class="sezione2">Ripetizioni</div></td>
</tr>
<tr>
  <td class="etichetta">Solo Oggi</td>
  <td class="campo">
  <input name="tipo" type="radio" onClick="checkRipetizione('O')" value="O" <%if(tipo.equals("O")) {%>CHECKED<%}%> <%if(!canModify) {%> disabled <%}%>/>
  </td>
</tr>
<tr>
  <td class="etichetta">Ogni anno</td>
  <td class="campo"><input type="radio" name="tipo" onClick="checkRipetizione('A')" value="A" <%if(tipo.equals("A")) {%>CHECKED<%}%> <%if(!canModify) {%> disabled <%}%> /></td>
</tr>

<tr id="titolo_ripetizioni" <%if(tipo.equals("O")) {%>style="display: none"<%}%>>
  <td colspan="2" align="left"><div class="sezione2">Periodo di Validit&agrave;</div></td>
</tr>
<tr id="tr_inizio" <%if(tipo.equals("O")) {%>style="display: none"<%}%>>
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
<tr id="tr_fine" <%if(tipo.equals("O")) {%>style="display: none"<%}%>>
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
    <tr><td colspan="2">&nbsp;</td></tr>
    <%if(canModify) {%>
    <tr>
      <td colspan="2" align="center">
      <input type="submit" name="SALVA" class="pulsanti" value="<%=btnSalva%>"/>&nbsp;&nbsp;
      <input type="button" class="pulsanti" value="Annulla" onClick="annullaClick()"/>
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