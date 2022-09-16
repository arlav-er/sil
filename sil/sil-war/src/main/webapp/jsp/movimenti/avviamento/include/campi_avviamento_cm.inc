<tr>
	<td colspan="6">          
		<div class="sezione2" id="Condizioni">
			&nbsp;Condizioni
		</div>
	</td>
</tr>
<tr>
	<td class="etichetta">Data inizio</td>
	<td class="campo">
<%if (consulta) {%>
		<af:textBox classNameBase="input" onKeyUp="fieldChanged();resetCFL();" type="date" validateOnPost="true" name="datInizioMov" required="true" title="Data Inizio Assunzione"  size="12" maxlength="10" value="<%=datInizioMov%>" readonly="<%=String.valueOf(!canModify)%>"/>
<%} else {%>
		<af:textBox classNameBase="input" onKeyUp="fieldChanged();resetCFL();settaAutorizzazioneDurataTD(false);" type="date" validateOnPost="true" name="datInizioMov" required="true" title="Data Inizio Assunzione"  size="12" maxlength="10" value="<%=datInizioMov%>" readonly="<%=String.valueOf(!canModify)%>" onBlur="javascript:calcolaDiffGiorni(this,document.Frm1.NUMGGTRAMOVCOMUNICAZIONE,varRange);" callBackDateFunction="callBackDateFunction()"/>
<%}%>
    </td>
	<td class="etichetta" style="display:'inline'" id="labelRit">Giorni&nbsp;dalla&nbsp;data<br>di&nbsp;comunicazione</td>
	<td class="campo" colspan="3" style="display:'inline'" id="campoRit" nowrap="true">
		<af:textBox classNameBase="input" onKeyUp="fieldChanged();" type="number" validateOnPost="true" name="NUMGGTRAMOVCOMUNICAZIONE" required="false" title="Giorni di ritardo"  size="12" maxlength="10" value="<%=numGgTraMovComunicaz%>" readonly="<%=String.valueOf(!canModify)%>"/>
	</td>
</tr>

<tr>
	<td class="etichetta">Matricola</td>
	<td class="campo" colspan="5">
	    <%int sizeMatr = 15;
	      if (strMatricola.length() > 12) { sizeMatr = (strMatricola.length() + 5); }
	    %>
		<af:textBox classNameBase="input" onKeyUp="fieldChanged();" name="strMatricola" title="Matricola" size="<%=Integer.toString(sizeMatr)%>" maxlength="100" value="<%=strMatricola%>" readonly="<%=String.valueOf(!canModify)%>"/>
	</td>
</tr>
<tr>
	<td class="etichetta" nowrap>Tempo</td>
	<td class="campo">
		<af:comboBox classNameBase="input" name="codMonoTempo" selectedValue="<%=codMonoTempo%>" title="Tempo dell'avviamento" onChange="visualizzaScadenza(this);fieldChanged();aggiornaTipoAss('', '', '', '');" disabled="<%=String.valueOf(!canModify)%>" required="true" addBlank="true">
			<option value="D" <% if (codMonoTempo.equals("D")) {%>selected="true" <%}%>>Determinato</option>
			<option value="I" <% if (codMonoTempo.equals("I")) {%>selected="true" <%}%>>Indeterminato</option>
		</af:comboBox>
	</td>
	<td id="scadenza" style="display: <%=(codMonoTempo.equals("I") || codMonoTempo.equals("")) ? "none" : "inline" %>" class="etichetta">
		Scadenza
	</td>
	<td class="campo" colspan="3" id="datascadenza" style="display: <%=(codMonoTempo.equals("I") || codMonoTempo.equals("")) ? "none" : "inline" %>" nowrap="true">
		<af:textBox classNameBase="input" onKeyUp="fieldChanged();settaAutorizzazioneDurataTD(false);" type="date" validateOnPost="true" name="datFineMov" title="scadenza"  size="12" value="<%=datFineMov%>" readonly="<%=String.valueOf(!canModify)%>" required='false' onBlur="javascript:checkFormatDate(this);"/>
<%if (consulta && (datFineMovEff != null && !datFineMovEff.equals("") && !datFineMovEff.equalsIgnoreCase(datFineMov))) {%>
		  (Effettiva: <strong><%=datFineMovEff%></strong>)
<%}%>
		&nbsp;&nbsp;
<%if(canModify) {%>
		<input type="button" class="pulsanti" name="calcDataFine" value="Calcola" onClick="javascript:apriSetDataFine();" />
<%}%>
	</td>
</tr>

<tr>
	<td class="etichetta">Orario</td>	
	<td colspan="5" class="campo2">
		<table style="border-collapse:collapse">
			<tr>
				<td class="campo2">
					<af:comboBox moduleName="ComboTipoOrario" selectedValue="<%=codOrario%>" title="Orario" classNameBase="input" name="codOrario" onChange="visualizzaOreSett(this);fieldChanged();" addBlank="true" disabled="<%=String.valueOf(!canModify)%>"/>
				</td>
				<td>&nbsp;</td>
				<td id="labelore" style="display: <%=(codOrario.equals("TP1") || codOrario.equals("N")) ? "none" : "inline" %>" class="etichetta2">Ore settimanali</td>
				<td class="campo2" id="numore" style="display: <%=(codOrario.equals("TP1") || codOrario.equals("N")) ? "none" : "inline" %>">
					<af:textBox classNameBase="input" onKeyUp="fieldChanged();" type="fixdecimal" validateOnPost="true" name="numOreSett" title="\'Ore settimanali\'" maxlength="9" size="12" value="<%=numOreSett%>" readonly="<%=String.valueOf(!canModify)%>" required='false'/>&nbsp;*
				</td>
				<%boolean mostraCampiAgricoltura = ( numGGPrevistiAgr!=null && !numGGPrevistiAgr.equals("") && Integer.parseInt(numGGPrevistiAgr)>0 );
				  if (canModify || (!canModify && mostraCampiAgricoltura)) {%>
				<td class="etichetta2">Giorni presunti in agric.</td>
				<td class="campo2">
					<af:textBox classNameBase="input" type="integer" validateOnPost="true"  onKeyUp="mostraCampiAgricoltura();fieldChanged();" name="NUMGGPREVISTIAGR" title="Num. giorni previsti in agr."  size="4" maxlength="3" value="<%=numGGPrevistiAgr%>" readonly="<%=String.valueOf(!canModify)%>"/>
				</td>
<%}%>
<%if (!canModify && numGGEffettuatiAgr!=null && !numGGEffettuatiAgr.equals("")) {%>
				<td>&nbsp;</td>
				<td class="etichetta2">Giorni effettivi in agric.</td>
				<td class="campo2">
					<af:textBox classNameBase="input" type="integer" validateOnPost="true"  onKeyUp="fieldChanged();" name="NUMGGEFFETTUATIAGR" title="Num. giorni effettivi in agr."  size="4" maxlength="3" value="<%=numGGEffettuatiAgr%>" readonly="<%=String.valueOf(!canModify)%>"/>
				</td>
<%}%>
			</tr>
		</table>
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
	<td class="etichetta">Tipo di Avviamento</td>
	<td class="campo" colspan="5" nowrap>
<%-- 05/02/2007 Savino: convenzioni --%>
<% 
	// INIT-PARTE-TEMP
	if (Sottosistema.CM.isOn()) { 
	// END-PARTE-TEMP
%>
		<af:textBox title="Codice del tipo di avviamento" validateWithFunction='<%= (!consulta) ? "checkTipoAss" : ""%>' value="<%=codTipoAss%>" classNameBase="input" name="codTipoAss" size="4" maxlength="3" onKeyUp="impostaGrado();fieldChanged();" required="true" readonly="<%=String.valueOf(!canModify)%>"/> 
<% 
	// INIT-PARTE-TEMP
	} else {
	// END-PARTE-TEMP
%>
		<af:textBox title="Codice del tipo di avviamento" validateWithFunction='<%= (!consulta) ? "checkTipoAss" : ""%>' value="<%=codTipoAss%>" classNameBase="input" name="codTipoAss" size="4" maxlength="3" onKeyUp="impostaGrado();fieldChanged();" required="true" readonly="<%=String.valueOf(!canModify)%>"/> 
<% 
	// INIT-PARTE-TEMP
	} 
	// END-PARTE-TEMP
%>
<% if (canModify) {%>
		&nbsp;<a href="javascript:cercaTipoAss('codice');"><img src="../../img/binocolo.gif" alt="Cerca per codice"></a>&nbsp;
<%}%>              
		<af:textBox title="Descrizione del tipo di avviamento" value="<%=descrTipoAss%>" classNameBase="input" name="descrTipoAss" size="50" onKeyUp="fieldChanged();" readonly="<%=String.valueOf(!canModify)%>"/>         
		<input type="hidden" name="CODMONOTIPO" value="<%=codMonoTipo%>"/>
		<input type="hidden" name="CODCONTRATTO" value="<%=codContratto%>"/>
<% if (canModify) {%>
		&nbsp;<a href="javascript:cercaTipoAss('descrizione');"><img src="../../img/binocolo.gif" alt="Cerca per descrizione"></a>&nbsp;
<%}%>
		<input id="appButtonTirocinio" style="display: none;" class="pulsanti" type="button" name="Tirocinio" value="Tirocinio" onClick="return apriTirocinio();"/>
		<input id="appButton" style="display: none;" class="pulsanti" type="button" name="Apprendistato" value="Apprendistato" onClick="return apriApprendistato();"/>
		<table border="0" cellpadding="0" cellspacing="0">
    		<tr>
				<td class="campo" id="pulsanteCFL" style="display: none;">
		        	<input class="pulsanti" type="button" name="CFL" value="Autorizzazione CFL" onClick="apriCFL();"/>
	      		</td>
				<input type="hidden" name="DATCFL" value="<%=DATCFL%>"/>
				<input type="hidden" name="STRNUMCFL" value="<%=STRNUMCFL%>"/>
				<input type="hidden" name="STRTIPOCFL" value="<%=STRTIPOCFL%>"/>
			</tr>
		</table>
	</td>
</tr>
<script language="Javascript">
	//visualizzaPulsanteCFL('<%=codTipoAss%>');
</script>
<tr>
	<td class="etichetta">Normativa/Motivo di utilizzo</td>
	<td class="campo" colspan="5">
		<af:comboBox classNameBase="input" name="codNormativa" moduleName="ComboNormativaSelettiva" validateWithFunction='<%= (!consulta) ? "checkCampoObbligatorio " : ""%>' title="Normativa" selectedValue="<%=codNormativa%>" onChange="fieldChanged();" addBlank="true" required="false" disabled="<%=String.valueOf(!canModify)%>"/>
	</td>            
</tr>           
<%-- 05/02/2007 Savino: convenzioni --%>
<% 
	// INIT-PARTE-TEMP
	if (Sottosistema.CM.isOn()) { 
	// END-PARTE-TEMP
%>
<% //boolean abilitaConvenzione = "NOH".equals(codTipoAss) || "NU2".equals(codTipoAss); %>


<tr>
	<td class="etichetta">L.68/99</td>	
	<td colspan="5" class="campo2">
		<table style="border-collapse:collapse">
			<tr>
				<td class="campo2">
				<input id="flagL68" type="checkbox" title="Flag L.68/99" name="flagL68" value="" <%=flgLegge68.equals("S") ? "CHECKED" : ""%>
							<%=canModify ? "" : "disabled='true'"%> onclick="fieldChanged();abilitazioneConvenzione();"/>
				<input type="hidden" name="FLGLEGGE68"/>
				</td>
				<td>&nbsp;</td>
				<td class="etichetta2">Nulla Osta/Conv. Num.</td>
				<td class="campo2">
					<af:textBox name="numConvenzione" type="text" value="<%=numConvenzione%>" title="Numero Convenzione"  
								size="6" maxlength="30" classNameBase="input" 
								readonly="<%=String.valueOf(!canModify)%>" disabled="false" onKeyUp="fieldChanged();" />
				</td>
				<td>&nbsp;</td>
				<td class="etichetta2">Data
				<td class="campo2">					
					<af:textBox name="datConvenzione" type="date" value="<%=datConvenzione%>" title="Data Convenzione"  
								size="12" maxlength="10" validateOnPost="true"   classNameBase="input" 
								readonly="<%=String.valueOf(!canModify)%>" disabled="false" onKeyUp="fieldChanged();" />
				</td>
				<td>&nbsp;&nbsp;</td>
				<% if (canModify) {%>
					<td>
						<input type="button" name="bottoneConvenzione" value="Nulla osta/Convenzioni" onclick="apriConvenzione()" class="pulsanti">
					</td>
				<% } %>
			</tr>
		</table>
	</td>
</tr>
<td>&nbsp;</td>

<% 
	// INIT-PARTE-TEMP
	} 
	// END-PARTE-TEMP
%>
<tr>
	<td colspan="6">          
		<div class="sezione2" id="Tipo di attivita del lavoratore">
			&nbsp;Tipo di attività del lavoratore
		</div>
	</td>
</tr>
<!--D'Auria Giovanni 07/03/05 inizio -->
<tr>
	<td class="etichetta" nowrap>Qualifica</td>
	<td class="campo" colspan="5">
		<af:textBox classNameBase="input" validateWithFunction='<%= (!consulta) ? "checkMansione" : ""%>' title="Qualifica" name="CODMANSIONE" size="7" maxlength="6" required="false" value="<%=codMansione%>" readonly="<%=String.valueOf(!canModify)%>"/>
		<%if (canModify) {%>              
			<af:textBox type="hidden" name="codMansioneHid"/>    
			<a href="javascript:selectMansioneOnClick(document.Frm1.CODMANSIONE, document.Frm1.codMansioneHid, document.Frm1.DESCMANSIONE,  document.Frm1.strTipoMansione);">
			<img src="../../img/binocolo.gif" alt="Cerca"></A>&nbsp;
		<%}%>   
       <af:textBox classNameBase="input" type="text" size="60" name="DESCMANSIONE" value="<%=descrMansione%>" readonly="<%=String.valueOf(!canModify)%>"/>
       <%if (canModify) {%> 
			<A href="javascript:selectMansionePerDescrizione(document.Frm1.DESCMANSIONE);">
				<img src="../../img/binocolo.gif" alt="Cerca per descrizione">
			</A>
		<%}%>     
	</td>	
</tr>
<!-- tipo e descrizione mansione -->
<tr>
	<td class="etichetta">Tipo mans.:</td>
	<td colspan="5" class="campo">
		<af:textBox type="hidden" name="CODTIPOMANSIONE" value=""/>
		<%
		if (descrTipoMansione.length() > 80){ 
		    descrTipoMansione = descrTipoMansione.substring(0,77) + "...";
		}
		int size = descrTipoMansione.length();
		if (size == 0) {size = 35;}
		size += 5;
		%>
		<af:textBox classNameBase="input" type="text" size="<%=size%>" name="strTipoMansione" value="<%=descrTipoMansione%>" readonly="true"/>&nbsp;&nbsp;
		<%if (canModify) {%> 
			<A href="javascript:ricercaAvanzataMansioni();">Ricerca avanzata</A>
		<%}%>
		<script language="javascript">
		  if(document.Frm1.CODMANSIONE.value != null && document.Frm1.CODMANSIONE.value != "" &&
		    (document.Frm1.strTipoMansione.value == "")) {
		     selectMansioneOnClick(document.Frm1.CODMANSIONE, document.Frm1.codMansioneHid, document.Frm1.DESCMANSIONE,  document.Frm1.strTipoMansione);
		  }
		</script>
	</td>
</tr>
<tr><td></td></tr>
<!--fine -->
<tr>
	<td class="etichetta">Descrizione mansione</td>
	<td class="campo" colspan="5">
		<af:textBox classNameBase="input" onKeyUp="fieldChanged();" type="text" name="strDesAttivita" title="Descrizione attività" maxlength="100" size="100" value="<%=strDesAttivita%>" readonly="<%=String.valueOf(!canModify)%>"/>
	</td>
</tr>

<tr>
	<td class="etichetta">
		<div id="sezioneSRQ1" style="display: none;">
		Qualifica SRQ
		</div>
	</td>
	<td class="campo" colspan="5">
	<div id="sezioneSRQ2" style="display: none;">
	<af:textBox classNameBase="input" title="Qualifica SRQ" name="CODQUALIFICASRQ" size="7" maxlength="4" value="<%=codQualificaSrq%>" validateWithFunction='<%= (!consulta) ? "checkTipoQualificaSRQ" : ""%>' readonly="<%=String.valueOf(!canModify)%>"/>
	<%if (canModify) {%>   
			<a href="javascript:selectQualificaSRQOnClick(document.Frm1.CODQUALIFICASRQ, document.Frm1.DESCQUALIFICASRQ, 'codice');">
			<img src="../../img/binocolo.gif" alt="Cerca"></A>&nbsp;
	<%}%>
	<af:textBox classNameBase="input" type="text" size="60" name="DESCQUALIFICASRQ" value="<%=descQualificaSrq%>" readonly="<%=String.valueOf(!canModify)%>"/>
    <%if (canModify) {%> 
		<A href="javascript:selectQualificaSRQOnClick(document.Frm1.CODQUALIFICASRQ, document.Frm1.DESCQUALIFICASRQ, 'descrizione');">
			<img src="../../img/binocolo.gif" alt="Cerca per descrizione">
		</A>
		<A href="javascript:ricercaAvanzataQualificaSRQ();">Ricerca avanzata</A>
	<%}%>
	</div>
	</td>
</tr>
<tr>
	<td class="etichetta">Grado</td>
	<td class="campo" nowrap>
		<af:comboBox moduleName="ComboTipoGrado" title="Grado" selectedValue="<%=codGrado%>" classNameBase="input" name="codGradoVisualizz" onChange="fieldChanged();riportaGradoHidden();" addBlank="true" disabled="<%=String.valueOf(!canModify)%>"/>
		<input type="hidden" name="codGrado" value="<%=codGrado%>"/>
	</td>
    <td class="etichetta">Livello</td>
	<td class="campo">
	  <af:textBox classNameBase="input" type="text" onKeyUp="fieldChanged();" name="numLivello" title="Livello" required="false" maxlength="6" size="4" value="<%=numLivello%>" readonly="<%=String.valueOf(!canModify)%>"/>
	</td>
</tr>
<tr>
 	<td colspan="2">
	 <div id="lavorazione" style="display: <%=mostraCampiAgricoltura ? "inline" : "none" %>">
	 <table class="main" width="100%" border="0" cellspacing="0" cellpadding="0">
	   <tr>
		 <td class="etichetta">Lavorazione</td>
		 <td class="campo" nowrap="true">
		    <af:textBox classNameBase="input" onKeyUp="fieldChanged();" name="CODLAVORAZIONE" title="Lavorazione" size="25" maxlength="100" value="<%=codLavorazione%>" readonly="<%=String.valueOf(!canModify)%>"/>&nbsp;&nbsp;
		 </td>
		 </td>
	   </tr>
	 </table>
	 </div>
	</td>
	<td colspan="2">
	 <div id="categoria" style="display: <%=mostraCampiAgricoltura ? "inline" : "none" %>"">
	 <table class="main" width="100%" border="0" cellspacing="0" cellpadding="0">
	   <tr>
		<td class="etichetta" width="20%">Categoria</td>
	    <td class="campo" nowrap="true" align="left">
			<af:comboBox classNameBase="input" name="CODCATEGORIA" moduleName="ComboCategoria" selectedValue="<%=codCategoria%>" title="Categoria" disabled="<%=String.valueOf(!canModify)%>" addBlank="true"/>&nbsp;&nbsp;
		</td>
	  </tr>
	 </table>
	 </div>
	 </td>
</tr>
<tr>
	<td class="etichetta">Compenso Mensile</td>
	<td class="campo">
		<af:textBox classNameBase="input" onKeyUp="fieldChanged();" type="integer" name="decRetribuzioneMen" title="Compenso" maxlength="10" size="15" value="<%=decRetribuzioneMen%>" validateOnPost="true" readonly="<%=String.valueOf(!canModify)%>"/>&#8364;
	</td>
<% if (consulta && !(decretribmensanata.equals("") && datasanata.equals("") && tipodichsanata.equals(""))) {%>
	<td class="campo">
			(Sanato 
				<%= (decretribmensanata.equals("")? "" : " a " + decretribmensanata )%>&#8364;
				<%= (datasanata.equals("")? "" : " in data " + datasanata )%>
				<%= (tipodichsanata.equals("")? "" : " con " + tipodichsanata )%>
				<%= (codiceDich.equals("DGRS") ? " a partire dal mov. con data inizio " + datInizioMovSupReddito : "")%>
			)
	</td>
	<td colspan="2">&nbsp;</td>
<%} else if (rettifica && !(decretribmensanata.equals("") && datasanata.equals("") && tipodichsanata.equals(""))) {%>
	<td class="campo">
			<div id="sezioneRetribuzioneSanata">
			(Sanato 
				<%= (decretribmensanata.equals("")? "" : " a " + decretribmensanata )%>&#8364;
				<%= (datasanata.equals("")? "" : " in data " + datasanata )%>
				<%= (tipodichsanata.equals("")? "" : " con " + tipodichsanata )%>
				<%= (codiceDich.equals("DGRS") ? " a partire dal mov. con data inizio " + datInizioMovSupReddito : "")%>
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
    <td>&nbsp;</td>
<%} else {%>
	<td>&nbsp;</td><td>&nbsp;</td>
<%}%>  
</tr>   
	<tr>
	<td class="etichetta">CCNL</td>
	<td class="campo" colspan="5">
		<af:textBox classNameBase="input" title="CCNL" onKeyUp="fieldChanged();PulisciRicercaCCNL(document.Frm1.codCCNL, document.Frm1.codCCNLHid, document.Frm1.strCCNL, document.Frm1.strCCNLHid, 'codice');" type="text" name="codCCNL" required="false" value="<%=codCCNL%>" size="10" maxlength="8" validateWithFunction="controllaCCNLOnSubmit" readonly="<%=String.valueOf(!canModify)%>"/>&nbsp;
		
<%if (canModify) {%>
		<A HREF="javascript:btFindCCNL_onclick(document.Frm1.codCCNL, document.Frm1.codCCNLHid, document.Frm1.strCCNL, document.Frm1.strCCNLHid, 'codice');"><IMG name="image" border="0" src="../../img/binocolo.gif" alt="cerca per codice"/></a>&nbsp;
		<af:textBox type="hidden" name="codCCNLHid" value="<%=codCCNL%>" />
<%}%> 
		<af:textBox type="text" classNameBase="input" onKeyUp="fieldChanged();PulisciRicercaCCNL(document.Frm1.codCCNL, document.Frm1.codCCNLHid, document.Frm1.strCCNL, Frm1.strCCNLHid, 'descrizione');" name="strCCNL" value="<%=strDescrizioneCCNL%>" size="25" maxlength="50" inline="
			onkeypress=\"if (event.keyCode==13) { event.keyCode=9; this.blur(); }\"" readonly="<%=String.valueOf(!canModify)%>"/>&nbsp;
<%if (canModify) {%>                    
		<A HREF="javascript:btFindCCNL_onclick(document.Frm1.codCCNL, document.Frm1.codCCNLHid, document.Frm1.strCCNL, document.Frm1.strCCNLHid, 'descrizione');"><IMG name="image" border="0" src="../../img/binocolo.gif" alt="cerca per descrizione"/></a>
		<af:textBox type="hidden" name="strCCNLHid" value="<%=strDescrizioneCCNL%>" />
		<A href="javascript:ricercaAvanzataCCNL();">Ricerca avanzata</A>
<%}%>                 
	</td>
</tr>
<tr>
	<td class="etichetta">Benefici</td>
	<td class="campo" colspan="5">
		<af:comboBox moduleName="ComboAgevolazioneSelettiva" title="Benefici" selectedValue="<%=codAgevolazione%>" classNameBase="input" name="codAgevolazione" onChange="fieldChanged();" addBlank="true" disabled="<%=String.valueOf(!canModify)%>"/>            
	</td>
</tr>
<%-- 08/02/2007 Savino: incentivi art. 13 --%>
<% 
	if(!valida) { 
		// INIT-PARTE-TEMP
		if (Sottosistema.CM.isOn()) { 
		// END-PARTE-TEMP
%>
<tr>
	<td colspan="6">          
		<div class="sezione2"">
			&nbsp;Incentivi art. 13
		</div>
	</td>
</tr>
<tr>
	<td class="etichetta">&nbsp;</td>
	<td class="campo" colspan="5">
		<table  border="0" cellpadding="0" cellspacing="0" style="table-collapse: collapse">
			<tr>
				<td class="etichetta2">Data
				<td class="campo2">
					<af:textBox name="DATFINESGRAVIO" type="date" value="<%=datFineSgravio%>" title="Data fine sgravio"
								required="false" size="12" maxlength="10" validateOnPost="true"   classNameBase="input" 
								readonly="<%=String.valueOf(!canModify)%>" onKeyUp="fieldChanged();" />
				<td class="etichetta2">&nbsp;&nbsp;&nbsp;&nbsp;Importo
				<td class="campo2">
					<af:textBox name="DECIMPORTOCONCESSO" type="integer" value="<%=decImportoConcesso%>" title="Importo concesso"  
								required="false" size="10" maxlength="10" validateOnPost="true"   classNameBase="input" 
								readonly="<%=String.valueOf(!canModify)%>" onKeyUp="fieldChanged();" />
				<td>&nbsp;&nbsp;&nbsp;&nbsp;
			</tr>
		</table>
	</td>
</tr>
<% 
		// INIT-PARTE-TEMP
		} 
		// END-PARTE-TEMP
	}
%>