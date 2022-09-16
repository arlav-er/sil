<% if (codTipoMov.equals("TRA")) {%>
<tr>
	<td colspan="4">          
		<div class="sezione2" id="Trasformazione">
			&nbsp;Trasformazione
		</div>
	</td>
</tr>
<tr>
	<td class="etichetta">Data inizio</td>
	<td class="campo" colspan="3">
		<%if (consulta) {%>
		  <af:textBox classNameBase="input" onKeyUp="fieldChanged();" type="date" validateOnPost="true" name="datInizioMov" title="Data inizio trasformazione" required="true" size="12" maxlength="10" value="<%=datInizioMov%>" readonly="<%=String.valueOf(!canModify)%>"/>
		<%} else {%>
		  <af:textBox classNameBase="input" onKeyUp="fieldChanged();settaAutorizzazioneDurataTD(false);" type="date" validateOnPost="true" name="datInizioMov" title="Data inizio trasformazione" required="true" size="12" maxlength="10" value="<%=datInizioMov%>" readonly="<%=String.valueOf(!canModify)%>" onBlur="javascript:calcolaDiffGiorni(this,document.Frm1.NUMGGTRAMOVCOMUNICAZIONE,varRange);"/>
		<%}%>  
	</td>
</tr>

<%if (codTipoAzienda.equalsIgnoreCase("INT")) {%>
	<tr>
	<td class="etichetta" nowrap>Data inizio missione</td>
	<td class="campo">
	<af:textBox classNameBase="input" onKeyUp="fieldChanged();" type="date" validateOnPost="true" name="DATINIZIORAPLAV" title="Data inizio missione"  size="12" maxlength="10" value="<%=datiniziomissione%>" readonly="<%=String.valueOf(!canModify)%>"/>
	</td>
	<td class="etichetta" nowrap>Data fine missione</td>
	<td class="campo">
	<af:textBox classNameBase="input" onKeyUp="fieldChanged();" type="date" validateOnPost="true" name="DATFINERAPLAV" title="Data fine missione"  size="12" maxlength="10" value="<%=datfinemissione%>" readonly="<%=String.valueOf(!canModify)%>"/>
	</td>
	</tr>
<%} else {%>
	<input type="hidden" name="DATINIZIORAPLAV" value="">
	<input type="hidden" name="DATFINERAPLAV" value="">
<%}%>

<tr>
  <td class="etichetta" style="display:'inline'" id="labelRit">Giorni dalla data di comunicazione</td>
  <td class="campo" colspan="3" style="display:'inline'" id="campoRit">
	  <af:textBox classNameBase="input" onKeyUp="fieldChanged();" type="number" validateOnPost="true" name="NUMGGTRAMOVCOMUNICAZIONE" required="false" title="Giorni di ritardo"  size="12" maxlength="10" value="<%=numGgTraMovComunicaz%>" readonly="<%=String.valueOf(!canModify)%>"/>
  </td>
</tr>
<tr>
  <td colspan="4">          
	  <div class="sezione2" id="SituazioneAttuale">
		  &nbsp;Nuove Condizioni
	  </div>
  </td>
</tr>    
<tr>
  <td class="etichetta" nowrap>Mansione a tempo</td>
  <td class="campo" >
	  <af:comboBox classNameBase="input" name="codMonoTempo" onChange="visualizzaScadenza(this);setDatFine(this);fieldChanged();gestisciFlagTempo(this);" disabled="<%=String.valueOf(!canModify)%>" required="true" title="Tempo della trasformazione">
	    <option value="D" <% if (codMonoTempo.equals("D")) {%>selected="selected" <%}%>>Determinato</option>
	    <option value="I" <% if (codMonoTempo.equals("I")) {%>selected="selected" <%}%>>Indeterminato</option>
	  </af:comboBox>
  </td>
  <td id="scadenza" style="display: <%=(codMonoTempo.equals("I")) ? "none" : "inline" %>" class="etichetta">
	  Scadenza
  </td>
  <td class="campo" id="datascadenza" style="display: <%=(codMonoTempo.equals("I")) ? "none" : "inline" %>">
	  <af:textBox classNameBase="input" onKeyUp="fieldChanged();settaAutorizzazioneDurataTD(false);" type="date" validateOnPost="true" name="datFineMov" title="Scadenza trasformazione"  size="12" maxlength="10" value="<%=datFineMov%>" readonly="<%=String.valueOf(readOnlyDatFine)%>" required='false' onBlur="javascript:checkFormatDate(this);"/>
	  <%if (consulta && (datFineMovEff != null && !datFineMovEff.equals("") && !datFineMovEff.equalsIgnoreCase(datFineMov))) {%>
	    (Effettiva: <strong><%=datFineMovEff%></strong>)
	  <%}%>
	  <% if(canModify) {%>
	    &nbsp;&nbsp;
	    <!--<input type="button" class="pulsanti" name="calcDataFine" value="Calcola" onClick="javascript:if(document.Frm1.datFineMov.readOnly==undefined){apriSetDataFine();}"/>-->
	    <input type="button" class="pulsanti" name="calcDataFine" value="Calcola" onClick="apriSetDataFine();"/>
	  <%}%>
  </td>        
</tr>
<tr>
  <td class="etichetta">Orario</td>
  <td class="campo">
  	<af:comboBox moduleName="ComboTipoOrario" title="Orario" selectedValue="<%=codOrario%>" classNameBase="input" name="codOrario" onChange="visualizzaOreSett(this);fieldChanged();" addBlank="true" disabled="<%=String.valueOf(!canModify)%>" required="true"/>
  </td>
  <td id="labelore" style="display: <%=(codOrario.equals("TP1") || codOrario.equals("N")) ? "none" : "inline" %>" class="etichetta">Ore settimanali</td>
  <td class="campo" id="numore" style="display: <%=(codOrario.equals("TP1") || codOrario.equals("N")) ? "none" : "inline" %>">
  	<af:textBox classNameBase="input" onKeyUp="fieldChanged();" type="fixdecimal" validateOnPost="true" name="numOreSett" title="\'Ore settimanali\'"  size="12" maxlength="38" value="<%=numOreSett%>" readonly="<%=String.valueOf(!canModify)%>" required="false"/>&nbsp;*
  </td>             
</tr> 
<%} 
else if (codTipoMov.equals("PRO")) {%>
<tr>
  <td colspan="4">          
	  <div class="sezione2" id="Proroga">
	  	&nbsp;Proroga
	  </div>
  </td>
</tr>
<tr>
  <td class="etichetta">Data inizio</td>
  <td class="campo">
<%if (consulta) {%>
  <af:textBox classNameBase="input" onKeyUp="fieldChanged();" type="date" validateOnPost="true" required="true" name="datInizioMov" title="Data inizio proroga"  size="12" maxlength="10" value="<%=datInizioMov%>" readonly="<%=String.valueOf(!canModify)%>"/>
<%} else {%>
  <af:textBox classNameBase="input" onKeyUp="fieldChanged();" type="date" validateOnPost="true" required="true" name="datInizioMov" title="Data inizio proroga"  size="12" maxlength="10" value="<%=datInizioMov%>" readonly="<%=String.valueOf(!canModify)%>" onBlur="javascript:calcolaDiffGiorni(this,document.Frm1.NUMGGTRAMOVCOMUNICAZIONE,varRange);"/>
<%}%> 
</td>
  <td class="etichetta" nowrap>Data fine</td>
  <td class="campo" nowrap>
  <af:textBox classNameBase="input" onKeyUp="fieldChanged();" type="date" validateOnPost="true" required="true" name="datFineMov" title="Data fine proroga"  size="12" maxlength="10" value="<%=datFineMov%>" readonly="<%=String.valueOf(!canModify)%>" onBlur="javascript:checkFormatDate(this);"/>
  <%if ( consulta && (datFineMovEff != null && !datFineMovEff.equals("") && !datFineMovEff.equalsIgnoreCase(datFineMov))) {%>
    (Effettiva: <strong><%=datFineMovEff%></strong>)
  <%}%>
  <% if(canModify) {%>
      &nbsp;&nbsp;
      <input type="button" class="pulsanti" name="calcDataFine" value="Calcola" onClick="javascript:apriSetDataFine();"/>
  <%}%>
  </td>
  </tr>
  
<%if (codTipoAzienda.equalsIgnoreCase("INT")) {%>
	<tr>
	<td class="etichetta" nowrap>Data inizio missione</td>
	<td class="campo">
	<af:textBox classNameBase="input" onKeyUp="fieldChanged();" type="date" validateOnPost="true" name="DATINIZIORAPLAV" title="Data inizio missione"  size="12" maxlength="10" value="<%=datiniziomissione%>" readonly="<%=String.valueOf(!canModify)%>"/>
	</td>
	<td class="etichetta" nowrap>Data fine missione</td>
	<td class="campo">
	<af:textBox classNameBase="input" onKeyUp="fieldChanged();" type="date" validateOnPost="true" name="DATFINERAPLAV" title="Data fine missione"  size="12" maxlength="10" value="<%=datfinemissione%>" readonly="<%=String.valueOf(!canModify)%>"/>
	</td>
	</tr>
<%} else {%>
	<input type="hidden" name="DATINIZIORAPLAV" value="">
	<input type="hidden" name="DATFINERAPLAV" value="">
<%}%>
  
<tr>
  <td class="etichetta" style="display:'inline'" id="labelRit">Giorni di<br/>possibile ritardo</td>
  <td class="campo" style="display:'inline'" id="campoRit" colspan="3">
  <af:textBox classNameBase="input" onKeyUp="fieldChanged();" type="number" validateOnPost="true" name="NUMGGTRAMOVCOMUNICAZIONE" required="false" title="Giorni di ritardo"  size="12" maxlength="10" value="<%=numGgTraMovComunicaz%>" readonly="<%=String.valueOf(!canModify)%>"/>
  </td>
  </tr>
  <input type="hidden" name="CODMONOTEMPO" value="D"/>
  <input type="hidden" name="codOrario" value="<%=codOrario%>"/>
  <input type="hidden" name="numOreSett" value="<%=numOreSett%>"/>
<%}%>
<tr>
<td colspan="4">          
<div class='sezione2' id="SituazioneAttuale">
&nbsp;Situazione attuale all'atto della Proroga/Trasformazione
</div>
</td>
</tr>
<!--Giovanni D'Auria 08/02/2005 inizio -->
<tr>
	<td class="etichetta" nowrap>Qualifica</td>
		<td class="campo" colspan="3">
		<af:textBox classNameBase="input" validateWithFunction='<%= (!consulta) ? "checkMansione" : ""%>' required="false" title="Qualifica" name="CODMANSIONE" size="7" maxlength="6" value="<%=codMansione%>" readonly="<%=String.valueOf(!canModify)%>"/>
		<%if (canModify) {%>              
		  <af:textBox type="hidden" name="codMansioneHid"/>    
		  <a href="javascript:selectMansioneOnClick(document.Frm1.CODMANSIONE, document.Frm1.codMansioneHid, document.Frm1.DESCMANSIONE,  document.Frm1.strTipoMansione);">
		  <img src="../../img/binocolo.gif" alt="Cerca"></A>
		<%}%>   
		  <af:textBox classNameBase="input" type="text" size="50" name="DESCMANSIONE" value="<%=descrMansione%>" readonly="<%=String.valueOf(!canModify)%>"/>
        <%if (canModify) {%>
		  <A href="javascript:selectMansionePerDescrizione(document.Frm1.DESCMANSIONE);">
			<img src="../../img/binocolo.gif" alt="Cerca per descrizione">
		  </A>
		<%}%>     
	</td>
</tr>
<!-- tipo e descrizione mansione -->
<tr>
	<td colspan="4" class="campo">
		<af:textBox type="hidden" name="CODTIPOMANSIONE" value=""/>
		Tipo mans.:&nbsp;
		<af:textBox classNameBase="input" type="text" size="27" name="strTipoMansione" value="<%=descrTipoMansione%>" readonly="true"/>
		<%if (canModify) {%>
			<A href="javascript:ricercaAvanzataMansioni();">Ricerca avanzata</A>
		<%}%>    
		
	</td>
</tr>
<!--Giovanni D'Auria 08/02/2005 fine -->
<tr>
<td class="etichetta">Descrizione mansione</td>
<td class="campo" colspan="3">
<af:textBox classNameBase="input" onKeyUp="fieldChanged();" type="text" name="strDesAttivita" title="Descrizione attività" maxlength="100" size="100" value="<%=strDesAttivita%>" readonly="<%=String.valueOf(!canModify)%>"/>
</td>       
</tr>
<tr>
<td class="etichetta">Grado</td>
<td class="campo" nowrap>
    <af:comboBox moduleName="ComboTipoGrado" title="Grado" selectedValue="<%=codGrado%>" classNameBase="input" name="codGrado" onChange="fieldChanged();" addBlank="true" disabled="<%=String.valueOf(!canModify)%>"/>
	<%--af:comboBox moduleName="ComboTipoGrado" title="Grado" selectedValue="<%=codGrado%>" classNameBase="input" name="codGradoVisualizz" onChange="fieldChanged();riportaGradoHidden();" addBlank="true" disabled="<%=String.valueOf(!canModify)%>"/>
    <input type="hidden" name="codGrado" value="<%=codGrado%>"/--%>
</td>
<td class="etichetta">Livello</td>
<td class="campo">
<af:textBox classNameBase="input" onKeyUp="fieldChanged();" type="text" name="numLivello" title="Livello" maxlength="6" size="4" value="<%=numLivello%>" validateOnPost="true" readonly="<%=String.valueOf(!canModify)%>"/>
</td>
</tr>
<tr>
<td class="etichetta">Compenso Mensile</td>
<td class="campo">
<af:textBox classNameBase="input" onKeyUp="fieldChanged();gestisciFlagReddito(this);" type="integer" name="decRetribuzioneMen" title="Compenso" maxlength="10" size="15" value="<%=decRetribuzioneMen%>" validateOnPost="true" readonly="<%=String.valueOf(!canModify)%>"/>&#8364;
</td> 
<% if (consulta && !(decretribmensanata.equals("") && datasanata.equals("") && tipodichsanata.equals(""))) {%>
		<td class="campo">
			(Sanato 
				<%= (decretribmensanata.equals("")? "" : " a " + decretribmensanata )%>&#8364;
				<%= (datasanata.equals("")? "" : " in data " + datasanata )%>
				<%= (tipodichsanata.equals("")? "" : " con " + tipodichsanata )%>
				<%= (codiceDich.equals("DGRS") ? " precedente alla data " + datInizioMovSupReddito : "")%>
			)
		</td>
<%} else if (rettifica && !(decretribmensanata.equals("") && datasanata.equals("") && tipodichsanata.equals(""))) {%>
	<td class="campo">
			<div id="sezioneRetribuzioneSanata">
			(Sanato 
				<%= (decretribmensanata.equals("")? "" : " a " + decretribmensanata )%>&#8364;
				<%= (datasanata.equals("")? "" : " in data " + datasanata )%>
				<%= (tipodichsanata.equals("")? "" : " con " + tipodichsanata )%>
				<%= (codiceDich.equals("DGRS") ? " precedente alla data " + datInizioMovSupReddito : "")%>
			)
			<!-- <a href="javascript:azzeraSituazioneSanata();"><img src="../../img/del.gif" alt="AnnullaSituazioneSanata" title="Annullamento della situazione sanata"></a> -->
			</div>
			<input type="hidden" name=DECRETRIBUZIONEMENSANATA value="<%=decretribmensanata%>"/>
			<input type="hidden" name="DATSITSANATA" value="<%=datasanata%>"/>
			<input type="hidden" name="TIPODICHSANATA" value="<%=tipodichsanata%>"/>
			<input type="hidden" name="CODICEDICH" value="<%=codiceDich%>"/>
			<input type="hidden" name="PRGDICHLAV" value="<%=prgDichLav%>"/>
			<input type="hidden" name="DATINIZIOMOVSUPREDDITO" value="<%=datInizioMovSupReddito%>"/>				
	</td>
<%} else {%>
	<td>&nbsp;</td><td>&nbsp;</td>
<%}%>            
</tr>   
<tr>
<td class="etichetta">CCNL</td>
<td class="campo" colspan="3">
<af:textBox classNameBase="input" title="CCNL" onKeyUp="fieldChanged();PulisciRicercaCCNL(Frm1.codCCNL, Frm1.codCCNLHid, Frm1.strCCNL, Frm1.strCCNLHid, 'codice');" type="text" name="codCCNL" value="<%=codCCNL%>" size="10" maxlength="8" validateWithFunction="controllaCCNLOnSubmit" readonly="<%=String.valueOf(!canModify)%>"/>&nbsp;
<%if (canModify) {%>
  <A HREF="javascript:btFindCCNL_onclick(document.Frm1.codCCNL, document.Frm1.codCCNLHid, document.Frm1.strCCNL, document.Frm1.strCCNLHid, 'codice');"><IMG name="image" border="0" src="../../img/binocolo.gif" alt="cerca per codice"/></a>&nbsp;
  <af:textBox type="hidden" name="codCCNLHid" value="<%=codCCNL%>"/>
<%}%> 
<af:textBox type="text" classNameBase="input" onKeyUp="fieldChanged();PulisciRicercaCCNL(Frm1.codCCNL, Frm1.codCCNLHid, Frm1.strCCNL, Frm1.strCCNLHid, 'descrizione');" name="strCCNL" value="<%=strDescrizioneCCNL%>" size="25" maxlength="50" inline="
onkeypress=\"if (event.keyCode==13) { event.keyCode=9; this.blur(); }\"" readonly="<%=String.valueOf(!canModify)%>"/>&nbsp;
<%if (canModify) {%>                    
  <A HREF="javascript:btFindCCNL_onclick(document.Frm1.codCCNL, document.Frm1.codCCNLHid, document.Frm1.strCCNL, document.Frm1.strCCNLHid, 'descrizione');"><IMG name="image" border="0" src="../../img/binocolo.gif" alt="cerca per descrizione"/></a>
  <af:textBox type="hidden" name="strCCNLHid" value="<%=strDescrizioneCCNL%>"/>
  <A href="javascript:ricercaAvanzataCCNL();">Ricerca avanzata</A>
<%}%>                 
</td>
</tr>
<tr>
<td class="etichetta">Benefici</td>
<td class="campo" colspan="3">
<af:comboBox moduleName="ComboTipoAgevolazioni" title="Benefici" selectedValue="<%=codAgevolazione%>" classNameBase="input" name="codAgevolazione" onChange="fieldChanged();" addBlank="true" disabled="<%=String.valueOf(!canModify)%>"/>
</td>
</tr>