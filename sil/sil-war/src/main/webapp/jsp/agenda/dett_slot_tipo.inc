<!-- @author: Stefania Orioli -->
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
<script type="text/javascript">
function chiudi()
{
	// Se la pagina è già in submit, ignoro questo nuovo invio!
	if (isInSubmit()) return;

    if (flagChanged==true){
          if (!confirm("I dati sono cambiati.\nProcedere lo stesso ?")) { return; }
          else {
                document.frm.MODULE.value = "";
                document.frm.MODULE.disabled = true;
                doFormSubmit(document.frm);
          }
    } else {
          document.frm.MODULE.value = "";
          document.frm.MODULE.disabled = true;
          doFormSubmit(document.frm);
    }
}


//Controlla che l'orario str sia nel formato hh:mm
function checkTime(str)
{
  var hourPat = /^(\d{1,2})(\:)(\d{1,2})$/;
  var matchArray = str.match(hourPat);

  if(matchArray == null) {
    //alert("Inserire l'orario nel formato hh:mm");
    return false;
  }
	
  var h,m;
  h = matchArray[1];
  m = matchArray[3];
	
  //alert(h);
  //alert(m);
	
  if (h<0 || h > 23) {
    //alert("L'orario " + str + " non è corretto.\nInserire un orario valido.");
    return false;
  }
  if (m<0 || m > 59) {
    //alert("L'orario " + str + " non è corretto.\nInserire un orario valido.");
    return false;
  }
	
  return true;
}
  
function calcola(n)
{
  var orarioPat = /^(\d{1,2})(\:)(\d{1,2})$/;
  var matchArray = null;

  var dalle = document.frm.STRORADALLE.value;
  var alle = document.frm.STRORAALLE.value;
  var min = document.frm.NUMMINUTI.value;
  var qta = document.frm.NUMQTA.value;

  var ok = true;
  var msg = "Per il calcolo è necessario inserire correttamente tutti gli altri dati";
  var c, h, m;
    
  switch(n) {
    case 1:
            // click su "alle"
            if(dalle!="" && checkTime(dalle) && !isNaN(min) && !isNaN(qta)) {
              alle = "";
              c = qta * min;
              matchArray = dalle.match(orarioPat);
              h = Math.floor(matchArray[1]);
              m = Math.floor(matchArray[3]);
              c = (h * 60) + m + c;
              h = Math.floor(c/60);
              m = c % 60;
              if(h < 10) { alle = "0"; }
              alle += h + ":";
              if(m < 10) { alle += "0"; }
              alle += m;
              document.frm.STRORAALLE.value = alle;
            } else {
              ok = false;
            }
            break;
    case 2:
            // click su "minuti"
            if(dalle!="" && checkTime(dalle) && alle!="" && checkTime(alle) && !isNaN(qta) && qta!=0) {
              // min = (alle - dalle) / qta;
              min = "";
              matchArray = alle.match(orarioPat);
              h = Math.floor(matchArray[1]);
              m = Math.floor(matchArray[3]);
              c = (h * 60) + m; // alle
              matchArray = dalle.match(orarioPat);
              h = Math.floor(matchArray[1]);
              m = Math.floor(matchArray[3]);
              c = c - ( (h*60) + m);
              c = Math.floor(c / qta);
              document.frm.NUMMINUTI.value = c;
            } else {
              ok=false;
            }
            break;
    case 3:
            // click su "qta"
            if(dalle!="" && checkTime(dalle) && alle!="" && checkTime(alle) && !isNaN(min) && min!=0) {
              // qta = (alle - dalle) / min
              matchArray = alle.match(orarioPat);
              h = Math.floor(matchArray[1]);
              m = Math.floor(matchArray[3]);
              c = (h * 60) + m; // alle
              matchArray = dalle.match(orarioPat);
              h = Math.floor(matchArray[1]);
              m = Math.floor(matchArray[3]);
              c = c - ( (h*60) + m);
              c = Math.floor(c / min);
              document.frm.NUMQTA.value = c;
            } else {
              ok = false;
            }
            break;
  } // switch(n)
  if(!ok) { alert(msg); }
  else { fieldChanged(); }
}
	function listaAssegnazione() {    	
    	codServizio = document.getElementsByName("CODSERVIZIO");
    	prgAmbiente = document.getElementsByName("PRGAMBIENTE");
    	prgSpi = document.getElementsByName("PRGSPI");
    	codCpi = document.getElementsByName("CODCPI");
    	var f = "AdapterHTTP?PAGE=ListaAssegnazioniPage" + 
    		"&PRGAMBIENTE=" + prgAmbiente[0].value + 
    		"&CODSERVIZIO="+codServizio[0].value + 
    		"&PRGSPI=" + prgSpi[0].value + 
        	"&AGENDA_CODCPI=" + codCpi[0].value;
        var t = "_blank";
        var feat = "toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,width=500,height=400,top=100,left=100"
        openedWindow = window.open(f, t, feat);
    }
    function assegnazioneScelta(prgSpi, codServizio, prgAmbiente){
    	document.frm.PRGSPI.value=prgSpi;
    	document.frm.CODSERVIZIO.value = codServizio;
    	document.frm.PRGAMBIENTE.value = prgAmbiente;
    	flagChanged = true;
    	openedWindow.close();
    }   
</script>

<%out.print(htmlStreamTop);%>
<table class="main">
<af:form action="AdapterHTTP" name="frm" method="POST">
<input type="hidden" name="PAGE" value="VGiornoTipoPage">
<input type="hidden" name="MODULE" value="<%=frmModule%>">
<input type="hidden" name="PRGSETTIPO" value="<%=prgSettTipo%>">
<input type="hidden" name="CODCPI" value="<%=codCpi%>">
<input type="hidden" name="NUMGIORNOSETT" value="<%=numGiornoSett%>">
<input type="hidden" name="PRGGIORNO" value="<%=prgGiorno%>">
<tr>
  <td class="etichetta">Settimana Tipo</td>
  <td class="campo"><b><%=strDescrizioneSettimana%></b></td>
</tr>
<tr>
  <td class="etichetta">Giorno</td>
  <td class="campo"><b><%=aGiorni[numGiornoSett]%></b></td>
</tr>

<tr>
  <td class="etichetta">Operatore</td>
  <td class="campo">
  <af:comboBox name="PRGSPI"
               size="1"
               title="Operatore"
               multiple="false"
               required="true"
               moduleName="COMBO_SPI_SCAD"
               selectedValue="<%=prgSpi%>"
               addBlank="true"
               blankValue=""
               classNameBase="input"
               disabled="<%= String.valueOf( !canModify ) %>"
               onChange="fieldChanged()"
  />
  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type="button" class="pulsanti"  value="Assegnazione" onclick="listaAssegnazione()"/>  
  </td>
</tr>
<tr>
  <td class="etichetta"><%=labelServizio%></td>
  <td class="campo">
  <af:comboBox name="CODSERVIZIO"
               size="1"
               title="<%=labelServizio%>"
               multiple="false"
               required="false"
               moduleName="COMBO_SERVIZIO_SCAD"
               selectedValue="<%=codServizio%>"
               addBlank="true"
               blankValue=""
               classNameBase="input"
               disabled="<%= String.valueOf( !canModify ) %>"
               onChange="fieldChanged()"
  />
  </td>
</tr>
<tr>
  <td class="etichetta">Ambiente/Aula</td>
    <td class="campo">
      <af:comboBox name="PRGAMBIENTE" size="1" title="Ambiente/Aula"
                     multiple="false" required="false"
                     focusOn="false" moduleName="COMBO_AMBIENTE"
                     selectedValue="<%=prgAmbiente%>" addBlank="true" blankValue=""
                     classNameBase="input"
                     disabled="<%= String.valueOf( !canModify ) %>"
                     onChange="fieldChanged()"/>
    </td>
</tr>
<tr>
  <td class="etichetta">Destinatario</td>
    <td class="campo">
      <af:comboBox name="CODDESTINATARIO" size="1" title="Ambiente/Aula"
                     multiple="false" required="false"
                     focusOn="false" moduleName="COMBO_DESTINATARIO"
                     selectedValue="<%=codDestinatario%>" addBlank="true" blankValue=""
                     classNameBase="input"
                     disabled="<%= String.valueOf( !canModify ) %>"
                     onChange="fieldChanged()"/>
    </td>
</tr>
<tr>
  <td class="etichetta">Prenotabile da utenti azienda e lavoratore</td>
  <td class="campo">
  <af:comboBox name="flgPubblico"
               size="1"
               title="Prenotabile da utenti azienda e lavoratore"
               required="false"
               moduleName=""
               selectedValue="<%=flgPubblico%>"
               addBlank="true"
               blankValue=""
               classNameBase="input"
               disabled="<%= String.valueOf( !canModify ) %>"
               onChange="fieldChanged()"
               >
    <option value="S" <%if(flgPubblico.equals("S")) {%>SELECTED<%}%> >S&igrave;</option>
    <option value="N" <%if(flgPubblico.equals("N")) {%>SELECTED<%}%>>No</option>
  </af:comboBox>
  </td>
</tr>
<tr>
  <td class="etichetta">Dalle</td>
  <td class="campo">
  <af:textBox name="STRORADALLE"
              value="<%=strDalle%>"
              size="6"
              maxlength="5"
              title="Dalle"
              type="time"
              validateOnPost="true"
              onKeyUp="fieldChanged();"
              classNameBase="input"
              readonly="<%=String.valueOf(!canModify)%>"
  />
  </td>
</tr>
<tr>
  <td class="etichetta">Fino Alle</td>
  <td class="campo">
  <af:textBox name="STRORAALLE"
              value="<%=strAlle%>"
              size="6"
              maxlength="5"
              title="Alle"
              type="time"
              validateOnPost="true"
              onKeyUp="fieldChanged();"
              classNameBase="input"
              readonly="<%=String.valueOf(!canModify)%>"
  />
  &nbsp;
  <img src="../../img/calc.gif" alt="Calcola campo Alle" onClick="calcola(1)">
  </td>
</tr>
<tr>
  <td class="etichetta">Minuti</td>
  <td class="campo">
  <af:textBox name="NUMMINUTI"
              value="<%=numMinuti%>"
              size="6"
              maxlength="3"
              title="Minuti"
              type="integer"
              onKeyUp="fieldChanged();"
              classNameBase="input"
              readonly="<%=String.valueOf(!canModify)%>"
  />
  &nbsp;
  <img src="../../img/calc.gif" alt="Calcola campo Minuti" onClick="calcola(2)">
  </td>
</tr>
<tr>
  <td class="etichetta">Quantit&agrave;</td>
  <td class="campo">
  <af:textBox name="NUMQTA"
              value="<%=numQta%>"
              size="6"
              maxlength="3"
              title="Quantità"
              type="integer"
              onKeyUp="fieldChanged();"
              classNameBase="input"
              readonly="<%=String.valueOf(!canModify)%>"
  />
  &nbsp;
  <img src="../../img/calc.gif" alt="Calcola campo Quantit&agrave;" onClick="calcola(3)">
  </td>
</tr>
<tr><td colspan="2">&nbsp;</td></tr>
<tr>
  <td colspan="2" align="center">
  <%if(canModify) {%>
      <input type="submit" class="pulsanti" value="<%=btnSalva%>">
      &nbsp;&nbsp;
  <%}%>
  <input type="button" class="pulsanti" value="<%=btnChiudi%>" onClick="chiudi()">
  </td>
</tr>
</af:form>
</table>
<%out.print(htmlStreamBottom);%>

