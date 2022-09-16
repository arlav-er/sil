<div id="campiAvv" style="display:none">

<p class="titolo">
	<center><af:textBox classNameBase="input" name="titolo" value="<%=titolo%>" readonly="true"/><center>
</p>
	
<%= htmlStreamTop %>
<af:textBox type="hidden" name="STRVERSIONETRACCIATOhid" value="<%=strVersioneTracciato%>" />
<af:textBox type="hidden" name="numLivelloOldHid" value="<%=numLivello%>" />
<table class="main" border="0" width="96%" cellpadding="0" cellspacing="0">
<tr>
	<td class="etichetta2">Data inizio</td>
	<td colspan="5" class="campo2">
		<table style="border-collapse:collapse">
			<tr>
				<td class="campo2" nowrap id="datInizioMovEdit" style="display: <%= prgMovimentoPrec.equals("") ? "" : "none" %>" >
					<af:textBox classNameBase="input" onKeyUp="fieldChanged();resetCFL();settaAutorizzazioneDurataTD(false);" type="date" validateOnPost="true" name="datInizioMov" title="Data Inizio"  size="12" maxlength="10" value="<%=datInizioMov%>" readonly="<%=String.valueOf(!canModify)%>"/>&nbsp;
				</td>
				<td class="campo2" nowrap id="datInizioMovNoEdit" style="display: <%= prgMovimentoPrec.equals("") ? "none" : "" %>" >
					<af:textBox classNameBase="input" type="text" name="datInizioMovNoEdit" title="Data Inizio" size="12" maxlength="10" value="<%=datInizioMov%>" readonly="true"/>&nbsp;
				</td>
				<td nowrap id='visualizzaObbli' style="display: ( prgMovimentoPrec.equals("") && codTipoMov.equals("CES") ? "" : "none" )">*</td>
				<td>&nbsp;&nbsp;</td>
				<td class="etichetta2">Tempo</td>
  					<td class="campo2" nowrap id="codMonoTempoEdit" style="display: <%= prgMovimentoPrec.equals("") && !(codTipoMov.equals("CES") || codTipoMov.equals("PRO") ) ? "" : "none" %>" >
					<af:comboBox classNameBase="input" name="codMonoTempo" selectedValue="<%=codMonoTempo%>" title="Tempo" required="true" onChange="visualizzaScadenza(this);fieldChanged();aggiornaTipoContratto('', '', '', '');" disabled="<%=String.valueOf(!canModify)%>" addBlank="true">
						<option value="D" <% if (codMonoTempo.equals("D")) {%>selected="true" <%}%>>Determinato</option>
						<option value="I" <% if (codMonoTempo.equals("I")) {%>selected="true" <%}%>>Indeterminato</option>
					</af:comboBox>&nbsp;
				</td>
				<td class="campo2" nowrap id="codMonoTempoNoEdit" style="display: <%= !prgMovimentoPrec.equals("") && (codTipoMov.equals("CES") || codTipoMov.equals("PRO")) ? "" : "none" %>">
					<af:textBox classNameBase="input" type="text" name="codMonoTempoNoEdit" required="true" value='<%=codMonoTempo.equals("I") ? "Indeterminato" : "Determinato"%>' readonly="true"/>&nbsp;
				</td>
			 	<td nowrap id="scadenza" class="etichetta2">Data fine</td>
			 	<td class="campo2" id="datascadenza">
			 		<af:textBox classNameBase="input" onKeyUp="fieldChanged();settaAutorizzazioneDurataTD(false);" type="date" validateOnPost="true" name="datFineMov" title="scadenza"  size="12" value="<%=datFineMov%>" readonly="<%=String.valueOf(!canModify)%>" onBlur="javascript:checkFormatDate(this);"/>
			 	</td>
			 	<td class="campo2" nowrap id="datascadenzaNoEdit" style="display: none">
					<af:textBox classNameBase="input" type="date" name="datFineMovNoEdit" title="scadenza" size="12" value="<%=datFineMov%>" readonly="true"/>
			 	</td>
			 	<td nowrap>
					&nbsp;&nbsp;<input id="calcDataFine" type="button" class="pulsanti" name="calcDataFine" style="display: <%= canModify ? "" : "none" %>" value="Calcola" onClick="javascript:apriSetDataFine();" />
				</td>
			 </tr>
		</table>
	</td>
</tr>

<tr valign="top" id="aziSomm" style="display:none">
  <td class="etichetta2">Matricola</td>	
  	<td class="campo2">
		<%int sizeMatr = 15;
	      if (strMatricola.length() > 12) { sizeMatr = (strMatricola.length() + 5); }
	    %>
		<af:textBox classNameBase="input" onKeyUp="fieldChanged();" name="strMatricola" title="Matricola" size="<%=Integer.toString(sizeMatr)%>" maxlength="100" value="<%=strMatricola%>" readonly="<%=String.valueOf(!canModify)%>"/>
	</td>
</tr>

<tr id="visDateMissione" style="display:none">
	<td class="etichetta2">Data inizio missione</td>	
	<td colspan="5" class="campo2">
		<table style="border-collapse:collapse">
			<tr>
				<td class="campo2">
					<af:textBox classNameBase="input" onKeyUp="fieldChanged();" type="date" validateOnPost="true" name="DATINIZIORAPLAV" title="Data inizio missione"  size="12" maxlength="10" value="<%=datiniziomissione%>" readonly="<%=String.valueOf(!canModify)%>"/>
				</td>
				<td>&nbsp;&nbsp;&nbsp;</td>
				<td class="etichetta2">Data fine missione</td>
				<td class="campo2">
					<af:textBox classNameBase="input" onKeyUp="fieldChanged();" type="date" validateOnPost="true" name="DATFINERAPLAV" title="Data fine missione"  size="12" maxlength="10" value="<%=datfinemissione%>" readonly="<%=String.valueOf(!canModify)%>"/>
				</td>
			</tr>
		</table>
	</td>
</tr>
			
<tr>
	<td class="etichetta2">Orario</td>	
	<td colspan="5" class="campo2">
		<table style="border-collapse:collapse">
			<tr>
				<td class="campo2">
					<af:comboBox moduleName="ComboTipoOrario" selectedValue="<%=codOrario%>" title="Orario" classNameBase="input" name="codOrario" onChange="visualizzaOreSett(this);fieldChanged(); PulisciRetribuzAnnuale();" addBlank="true" disabled="<%=String.valueOf(!canModify)%>"/>&nbsp;
				</td>
				<td>&nbsp;</td>
				<td id="labelore" style="display:<%=(codOrario.equals("F") || codOrario.equals("N") || codOrario.equals("")) ? "none" : "" %>;" class="etichetta2">Ore settimanali</td>
				<td class="campo2" id="numore" style="display:<%=(codOrario.equals("F") || codOrario.equals("N") || codOrario.equals("")) ? "none" : "" %>;">
					<af:textBox classNameBase="input" onKeyUp="fieldChanged(); PulisciRetribuzAnnuale();" type="fixdecimal" validateOnPost="true" name="numOreSett" title="\'Ore settimanali\'" maxlength="9" size="12" value="<%=numOreSett%>" readonly="<%=String.valueOf(!canModify)%>" />&nbsp;*
				</td>
			</tr>
		</table>
	</td>
</tr>

<tr>
	<td class="etichetta2">Tipo di Contratto</td>
	<td class="campo" colspan="5" nowrap>
		<af:textBox title="Codice del tipo di contratto" validateWithFunction="checkTipoContratto" value="<%=codTipoAss%>" classNameBase="input" name="codTipoAss" size="8" maxlength="7" onKeyUp="fieldChanged();" required="true" readonly="<%=String.valueOf(!canModify)%>"/>&nbsp;
		<input type="hidden" name="CODMONOTIPO" value="<%=codMonoTipo%>"/>
		<input type="hidden" name="FLGCONTRATTOTI" value="<%=flgContrattoTI%>"/>
		<input type="hidden" name="CODCONTRATTO" value="<%=codContratto%>"/>
<% if (canModify) {%>
		&nbsp;<a href="javascript:cercaTipoContratto('codice');"><img src="../../img/binocolo.gif" alt="Cerca per codice"></a>&nbsp;
<%}%>              
		<af:textBox title="Descrizione del tipo di contratto" value="<%=descrTipoAss%>" classNameBase="input" name="descrTipoAss" size="50" onKeyUp="fieldChanged();" readonly="<%=String.valueOf(!canModify)%>"/>         
		
<% if (canModify) {%>
		&nbsp;<a href="javascript:cercaTipoContratto('descrizione');"><img src="../../img/binocolo.gif" alt="Cerca per descrizione"></a>&nbsp;
<%}%>
		<input id="appButton" style="display: none;" class="pulsanti" type="button" name="Apprendistato" value="Apprendistato" onClick="return apriApprendistato();"/>
		<input id="appButtonTirocinio" style="display: none;" class="pulsanti" type="button" name="Tirocinio" value="Tirocinio" onClick="return apriTirocinio();"/>
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

<tr id="visDataFinePF" style="display:none">
	<td class="etichetta2" nowrap>Data fine periodo formativo</td>	
	<td colspan="5" class="campo2">
		<af:textBox classNameBase="input" onKeyUp="fieldChanged();" type="date" validateOnPost="true" 
				name="datFinePF" title="Data fine periodo formativo"  size="12" maxlength="10" 
				value="<%=datFineMovPF%>" readonly="<%=String.valueOf(!canModify)%>"/>	
	</td>
</tr>

<tr>
	<td class="etichetta2" nowrap>Assunzione obbligatoria</td>	
	<td colspan="5" class="campo2">
		<table style="border-collapse:collapse">
			<tr>
				<td class="campo2">
				<input id="flagCheckAssObbl" type="checkbox" title="Flag L.68/99" name="flgCheckAssObbl" value="" <%=flgAssObbl.equalsIgnoreCase("S") ? "CHECKED" : ""%>
							<%=canModify ? "" : "disabled='true'"%> onclick="fieldChanged();abilitazioneConvenzione();"/>
				<input type="hidden" name="FLGLEGGE68" value=""/>
				<input type="hidden" name="flgAssObbl" value="<%=flgAssObbl%>"/>
				</td>
				<td>&nbsp;</td>
				
				<td class="etichetta2" nowrap>Categoria assunzione obbligatoria</td>
				<td class="campo2">
					<af:comboBox moduleName="ComboCategoriaAssObbligatoria" name="codCatAssObbl" classNameBase="input" addBlank="true"
						selectedValue="<%=codCatAssObbl%>" disabled="<%=String.valueOf(!canModify)%>" title="Categoria assunzione obbligatoria"
						onChange="fieldChanged();abilitazioneConvenzione();"/>
				</td>
			</tr>
		</table>
	</td>
</tr>

<tr>
	<td class="etichetta2" nowrap>L.68/99</td>
	<td colspan="5" class="campo2">
		<table style="border-collapse:collapse">
			<tr><td>
				<input id="flagL68" type="checkbox" title="Flag L.68/99" name="flagL68" value="" <%=flgLegge68.equals("S") ? "CHECKED" : ""%>
							disabled="true"/>
				</td>
				<td>&nbsp;</td>
				<td class="etichetta2" nowrap>Nulla Osta/Conv. Num.</td>
				<td class="campo2">
					<af:textBox name="numConvenzione" type="text" value="<%=numConvenzione%>" title="Numero Convenzione"  
						size="6" maxlength="30" classNameBase="input" readonly="true" disabled="true"/>
				</td>
				<td>&nbsp;</td>
				<td class="etichetta2" nowrap>Data</td>
				<td class="campo2">
					<af:textBox name="datConvenzione" type="date" value="<%=datConvenzione%>" title="Data Convenzione"  
						size="12" maxlength="10" classNameBase="input" readonly="true" disabled="true"/>
				</td>
				<td>
					<input type="hidden" name="bottoneConvenzione" value=""/>
				</td>
			</tr>
		</table>
	</td>
</tr>

<tr>
	<td class="etichetta2" nowrap>Mansione</td>
	<td class="campo" colspan="5">
		<af:textBox classNameBase="input" validateWithFunction="checkMansione" title="Mansione" name="CODMANSIONE" size="7" maxlength="7" value="<%=codMansione%>" readonly="<%=String.valueOf(!canModify)%>"/>&nbsp;
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
			<A href="javascript:ricercaAvanzataMansioni();">Ricerca avanzata</A>
		<%}%>
		<af:textBox type="hidden" name="strTipoMansione" value="<%=descrTipoMansione%>"/>
		<af:textBox type="hidden" name="CODTIPOMANSIONE" value=""/>     
	</td>	
</tr>
<tr><td></td></tr>

<tr>
	<td class="etichetta2">Descrizione mansione</td>
	<td class="campo" colspan="5">
		<af:textBox classNameBase="input" onKeyUp="fieldChanged();" type="text" name="strDesAttivita" title="Descrizione attività" maxlength="80" size="80" value="<%=strDesAttivita%>" readonly="<%=String.valueOf(!canModify)%>"/>
	</td>
</tr>

<%boolean mostraCampiAgricoltura = false;%>
<tr id="agrPrevisti" style="display:''">
	<%if(flgLavAgr) {%>
	<td class="etichetta2" nowrap="nowrap">Lavoro in agric.</td>	
	<%} else {%>
		<td class="etichetta2" nowrap="nowrap">Giorni&nbsp;presunti&nbsp;in&nbsp;agric.</td>
	<%}%>
	<td colspan="5" class="campo2">
		<table style="border-collapse:collapse">
		<tr>
			<%if(flgLavAgr) {%>
			<td>
			<input id="lavAgr" type="checkbox" title="Lavoro in agric" name="lavAgr" value="" <%=flgLavoroAgr.equals("S") ? "CHECKED" : ""%>
					<%=canModify ? "" : "disabled='true'"%> onclick="fieldChanged();campiAgricoltura();"/>
					<input type="hidden" name="FLGLAVOROAGR" value="<%=flgLavoroAgr%>"/>	
			</td>
			<td>&nbsp;&nbsp;</td>
			<td class="etichetta2" nowrap="nowrap">Giorni&nbsp;presunti&nbsp;in&nbsp;agric.</td>
			<%}%>
			<%mostraCampiAgricoltura = ( numGGPrevistiAgr!=null && !numGGPrevistiAgr.equals("") && Integer.parseInt(numGGPrevistiAgr)>0 );
			if (canModify || (!canModify && mostraCampiAgricoltura)) {%>
				<td class="campo2">
					<af:textBox classNameBase="input" type="integer" validateOnPost="true" onKeyUp="fieldChanged();" name="NUMGGPREVISTIAGR" title="Num. giorni previsti in agr." size="4" maxlength="4" value="<%=numGGPrevistiAgr%>" readonly="<%=String.valueOf(!canModify)%>"/>
				</td>
		   <%}%>
				<td>&nbsp;&nbsp;</td>
				<td colspan="2">
	 				<table class="main" width="100%" border="0" cellspacing="0" cellpadding="0">
	   					<tr>
		 					<td class="etichetta">Lavorazione</td>
		 					<td class="campo" nowrap="true">
		    					<af:textBox classNameBase="input" type="text" onKeyUp="fieldChanged();" name="CODLAVORAZIONE" title="Lavorazione" size="25" maxlength="100" value="<%=codLavorazione%>" readonly="<%=String.valueOf(!canModify)%>" />&nbsp;&nbsp;
		 					</td>
		 				</tr>
	 				</table>
	 			</td>
				<td>&nbsp;&nbsp;</td>
	 			<td>
	 			<table>
	 				<tr>
					<td class="etichetta">Livello</td>
					<td class="campo">
						<af:textBox classNameBase="input" type="text" onKeyUp="fieldChanged();PulisciRetribuzAnnuale();" name="numLivello" title="Livello" maxlength="8" size="8" value="<%=numLivello%>" readonly="<%=String.valueOf(!canModify)%>"/>
					</td>
					<%if (canModify) {%>
					 <td class="campo">	
						<a href="javascript:selectLivelloInquadramento();"><img name="image" border="0" src="../../img/binocolo.gif" alt="Cerca"></a>&nbsp;
					 </td>
					<%}%>  	
					 </tr>
					 </table>
				</td>
			</tr>
		</table>
	</td>
</tr>

<tr id="agrEffettivi" style="display:'none'">
	<%if(flgLavAgr) {%>
		<td class="etichetta2" nowrap="nowrap">Lavoro in agric.</td>	
	<%} else {%>
		<td class="etichetta2" nowrap="nowrap">Giorni&nbsp;effettivi&nbsp;in&nbsp;agric.</td>
	<%}%>
	<td colspan="5" class="campo2">
	<table style="border-collapse:collapse">
		<tr>
			<%if(flgLavAgr) {%>
				<td>
					<input id="lavAgrCes" type="checkbox" title="Lavoro in agric" name="lavAgrCes" value="" <%=flgLavoroAgr.equals("S") ? "CHECKED" : ""%>
							<%=canModify ? "" : "disabled='true'"%> onclick="fieldChanged();campiAgricoltura();"/>
							<input type="hidden" name="FLGLAVOROAGRCES" value="<%=flgLavoroAgr%>"/>
				</td>
				<td>&nbsp;&nbsp;</td>
				<td class="etichetta2" nowrap="nowrap">Giorni&nbsp;effettivi&nbsp;in&nbsp;agric.</td>
			<%}%>
			
			<%mostraCampiAgricoltura = ( numGGEffettuatiAgr!=null && !numGGEffettuatiAgr.equals("") && Integer.parseInt(numGGEffettuatiAgr)>0 );
			if (canModify || (!canModify && mostraCampiAgricoltura)) {%>
			<td class="campo2">
				<af:textBox classNameBase="input" type="integer" validateOnPost="true"  onKeyUp="fieldChanged();" name="NUMGGEFFETTUATIAGR" title="Num. giorni effettivi in agr."  size="4" maxlength="4" value="<%=numGGEffettuatiAgr%>" readonly="<%=String.valueOf(!canModify)%>"/>
			</td>
		   <%} %>
				<td>&nbsp;&nbsp;</td>
				<td colspan="2">
	 				<table class="main" width="100%" border="0" cellspacing="0" cellpadding="0">
	   					<tr>
		 					<td class="etichetta">Lavorazione</td>
		 					<td class="campo" nowrap="true">
		    					<af:textBox classNameBase="input" onKeyUp="fieldChanged();" name="CODLAVORAZIONECES" title="Lavorazione" size="25" maxlength="100" value="<%=codLavorazione%>" readonly="<%=String.valueOf(!canModify)%>"/>&nbsp;&nbsp;
		 					</td>
		 				</tr>
	 				</table>
	 			</td>
				<td>&nbsp;&nbsp;</td>
				<td>
	 			<table>
	 				<tr>
					<td class="etichetta">Livello</td>
					<td class="campo">
						<af:textBox classNameBase="input" type="text" onKeyUp="fieldChanged(); PulisciRetribuzAnnuale();" name="numLivelloCes" title="Livello" maxlength="8" size="8" value="<%=numLivello%>" readonly="<%=String.valueOf(!canModify)%>"/>
					</td>
						<%if (canModify) {%>
					 <td>		
						<a href="javascript:selectLivelloInquadramento();"><img src="../../img/binocolo.gif" alt="Cerca"></a>&nbsp;
						<%}%>  
					</td>
					</tr>
				</table>
				</td>
			</tr>
		</table>
	</td>
</tr>
<tr>
	<td class="etichetta2" nowrap="nowrap">Lav. in mobilità</td>
		<td colspan="5" class="campo2">
		<table style="border-collapse:collapse">
			<tr>
				<td class="campo2">
					<input id="lavInMob" type="checkbox" title="Lavoro in mobilita" name="lavInMob" value="" <%=flgLavInMobilita.equals("S") ? "CHECKED" : ""%>
							<%=canModify ? "" : "disabled='true'"%> onclick="fieldChanged();gestisciFlagLavMobilita();"/>
					<input type="hidden" name="FLGLAVOROINMOBILITA" value="<%=flgLavInMobilita%>"/>
				</td>
				<td>&nbsp;&nbsp;</td>
				<td id="lavStagionaleLabel" class="etichetta2" style="display:''" >Lavoro Stagionale</td>
				<td id="lavStagioneFlg" class="campo2" style="display:''">
					<input id="lavStagionale" type="checkbox" title="Lavoro stagionale" name="lavStagionale" value="" <%=flgLavStagionale.equals("S") ? "CHECKED" : ""%>
						<%=canModify ? "" : "disabled='true'"%> onclick="fieldChanged();gestisciFlagLavStagionale();"/>
					<input type="hidden" name="FLGLAVOROSTAGIONALE" value="<%=flgLavStagionale%>"/>
				</td>
			</tr>
		</table>
	</td>
</tr>

<tr>
	<td class="etichetta2" nowrap="nowrap">Compenso Annuale</td>	
	<td colspan="5" class="campo2">
		<table style="border-collapse:collapse">
			<tr>
				<td class="campo2">
				<% if (canModify) {%>
					<af:textBox classNameBase="input" onKeyUp="fieldChanged();valorizzaCompensoMensile();" onBlur="fieldChanged();valorizzaCompensoMensile();" type="integer" name="decRetribuzioneAnn" title="Compenso annuale" maxlength="10" size="11" value="<%=decRetribuzioneAnn%>" validateOnPost="true" readonly="<%=String.valueOf(!canModify)%>"/>
				 	<A HREF="javascript:calcolaRetribuzione();"><IMG name="image" border="0" src="../../img/calc.gif" alt="cerca per codice"/></a>&nbsp;&#8364; /12 =
				<%}%>  
				</td>
				<td class="etichetta2">Compenso Mensile</td>
				<td class="campo2">
					<af:textBox classNameBase="input" type="number" name="decRetribuzioneMen" title="Compenso Mensile" maxlength="13" size="12" value="<%=decRetribuzioneMen%>" validateOnPost="true" readonly="true"/>&#8364;
					<input type="hidden" name="compensoMensile" value="<%=decRetribuzioneMen%>"/>
   				</td>  
			</tr>
		</table>
	</td>
</tr>

<tr>
	<td class="etichetta2">CCNL</td>
	<td class="campo" colspan="5">
		<af:textBox classNameBase="input" title="CCNL" onKeyUp="fieldChanged(); PulisciRetribuzAnnuale();" type="text" name="codCCNL" required="false" value="<%=codCCNL%>" size="5" maxlength="4" validateWithFunction="controllaCCNLOnSubmit" readonly="<%=String.valueOf(!canModify)%>"/>&nbsp;
		<af:textBox type="hidden" name="codCCNLHid" value="<%=codCCNL%>" />
	<%if (canModify) {%>
		<A HREF="javascript:btFindCCNL_onclick(document.Frm1.codCCNL, document.Frm1.codCCNLHid, document.Frm1.strCCNL, document.Frm1.strCCNLHid, 'codice');"><IMG name="image" border="0" src="../../img/binocolo.gif" alt="cerca per codice"/></a>&nbsp;
		
	<%}%> 
		<af:textBox type="text" classNameBase="input" onKeyUp="fieldChanged();PulisciRetribuzAnnuale();" name="strCCNL" value="<%=strDescrizioneCCNL%>" size="60" inline="
				    onkeypress=\"if (event.keyCode==13) { event.keyCode=9; this.blur(); }\"" readonly="<%=String.valueOf(!canModify)%>"/>&nbsp;
		<af:textBox type="hidden" name="strCCNLHid" value="<%=strDescrizioneCCNL%>" />
	<%if (canModify) {%>                    
		<A HREF="javascript:btFindCCNL_onclick(document.Frm1.codCCNL, document.Frm1.codCCNLHid, document.Frm1.strCCNL, document.Frm1.strCCNLHid, 'descrizione');"><IMG name="image" border="0" src="../../img/binocolo.gif" alt="cerca per descrizione"/></a>
		<A href="javascript:ricercaAvanzataCCNL();">Ricerca avanzata</A>
<%}%>                 
	</td>
</tr>

<tr><td colspan="6">&nbsp;</td></tr>
<% if (canModify) {%>
	<tr>
	<td class="etichetta2">Agevolazioni</td>
	<td class="campo" colspan="5">
	<af:comboBox moduleName="ComboTipoAgevolazioni" title="Benefici" 
		classNameBase="input" name="codAgevolazione" onChange="fieldChanged();" multiple="true" size="8" />            
	</td>
	</tr>
<%} 
else {
	if (rowsAgevolazioni.size() == 0) {
	%>
		<tr>
		<td class="etichetta2">Agevolazioni</td>
		<td class="campo" colspan="5">&nbsp;</td>
		</tr>
	<%}
	for (int nAgevolazioni=0;nAgevolazioni<rowsAgevolazioni.size();nAgevolazioni++) {
		SourceBean appAgevolazioni = (SourceBean)rowsAgevolazioni.get(nAgevolazioni);
		String descAgevolazioneCurr = appAgevolazioni.getAttribute("strdescrizione").toString();
		if (nAgevolazioni > 0) {
		%>
			<tr><td class="etichetta2">&nbsp;</td>
			<td class="campo" colspan="5"><b><%=descAgevolazioneCurr%></b></td>
			</tr>
		<%} else {%>
			<tr>
			<td class="etichetta2">Agevolazioni</td>
			<td class="campo" colspan="5"><b><%=descAgevolazioneCurr%></b></td>
			</tr>
		<%}	
	}
}%>

<tr>
	<td nowrap class="etichetta2">Ente prev.</td>	
	<td colspan="5" class="campo2">
		<table style="border-collapse:collapse">
			<tr>
				<td class="campo2">
				<td class="campo2">
					<af:textBox name="strEnte" type="text" value="<%=strEnte%>" title="Ente prev."  
								size="40" maxlength="40" classNameBase="input" readonly="true"/>
				</td>
				<td>&nbsp;</td>
				<td nowrap class="etichetta2">Cod. ente prev. 
				<td class="campo2">					
					<af:textBox name="strcodiceenteprev" type="text" value="<%=strcodiceenteprev%>" title="Cod. ente prev."  
								size="30" maxlength="30" classNameBase="input" readonly="true"/>
				</td>
				<td>&nbsp;&nbsp;</td>
				
			</tr>
		</table>
	</td>
</tr>
<tr>
	<td colspan="6">          
		<div class="sezione2"">
			&nbsp;Incentivi art. 13
		</div>
	</td>
</tr>
<tr>
	<td class="etichetta2">&nbsp;</td>
	<td class="campo" colspan="5">
		<table  border="0" cellpadding="0" cellspacing="0" style="table-collapse: collapse">
			<tr>
				<td class="etichetta2">Data
				<td class="campo2">
					<af:textBox name="DATFINESGRAVIO" type="date" value="<%=datFineSgravio%>" title="Data fine sgravio"
								size="12" maxlength="10" validateOnPost="true"   classNameBase="input" 
								readonly="<%=String.valueOf(!canModify)%>" onKeyUp="fieldChanged();" />
				<td class="etichetta2">&nbsp;&nbsp;&nbsp;&nbsp;Importo
				<td class="campo2">
					<af:textBox name="DECIMPORTOCONCESSO" type="integer" value="<%=decImportoConcesso%>" title="Importo concesso"  
								size="10" maxlength="10" validateOnPost="true"   classNameBase="input" 
								readonly="<%=String.valueOf(!canModify)%>" onKeyUp="fieldChanged();" />
				<td>&nbsp;&nbsp;&nbsp;&nbsp;
			</tr>
		</table>
	</td>
</tr>
</table>
<%= htmlStreamBottom %>
</div>
<div id="campiMov" style="display:none">
<p class="titolo">
		<center><af:textBox classNameBase="input" name="sezTitolo" value="<%=sezTitolo%>" size="30" maxlength="50" readonly="true"/></center>
</p>

<%= htmlStreamTop %>

<table class="main" border="0" width="96%" cellpadding="0" cellspacing="0">

<tr id="sezTipoVariazione" style="display:none">
	<td class="etichetta">Tipo Variazione</td>
	<td colspan="5" class="campo2">
  		<af:comboBox moduleName="ComboCodiceVariazione" selectedValue="<%=codVariazione%>" 
		 	classNameBase="input" title="Tipo Variazione" 
		 	name="CODVARIAZIONE" onChange="fieldChanged();" 
		 	addBlank="true" disabled="<%=String.valueOf(!canModify)%>"/>
  	</td>
</tr>

<tr id="sezCes" style="display:none">
	<td class="etichetta2" width="15%">Data&nbsp;cessazione</td>	
	<td colspan="5" class="campo2">
		<table style="border-collapse:collapse">
		<tr>
			<td class="campo2" nowrap>
				<af:textBox classNameBase="input" onKeyUp="fieldChanged();" type="date" validateOnPost="true" name="datInizioMovCes" title="Data Cessazione" size="12" maxlength="10" value="<%=datInizioMovCes%>" readonly="<%=String.valueOf(!canModify)%>"/>&nbsp;*
			</td>
			<td>&nbsp;&nbsp;&nbsp;</td>
			<td class="etichetta2" width="15%">Motivo Cessazione</td>
			<td class="campo2" width="30%">
				<af:comboBox moduleName="ComboTipoCessazione" selectedValue="<%=codMvCessazione%>" 
						 	classNameBase="input" title="Motivo cessazione" 
			 			 	name="codMvCessazione" onChange="fieldChanged();" 
						 	addBlank="true" disabled="<%=String.valueOf(!canModify)%>"/>
			</td>
			<input type="hidden" name="CODMVCESSAZIONEPREC" value="<%=codMvCessazionePrec%>"/>
		</tr>
	</table>
	</td>
</tr>

<tr id="sezPro" style="display:none">
	<td class="etichetta">Data fine proroga</td>	
	<td colspan="5" class="campo">
	<table style="border-collapse:collapse">
		<tr>
			<td class="campo" nowrap>
		  		<af:textBox classNameBase="input" onKeyUp="fieldChanged();" type="date" validateOnPost="true" name="datFineMovPro" title="Data fine proroga"  size="12" maxlength="10" value="<%=datFineMovPro%>" readonly="<%=String.valueOf(!canModify)%>" onBlur="javascript:checkFormatDate(this);"/>&nbsp;*
		  	</td>
	  	</tr>
	</table>
	</td>
</tr>

<input type="hidden" name="FLGPROSECUZIONE" value="<%=flgProsecuzione%>"/>

<tr id="sezTra1" style="display:none">
<td class="etichetta" width="15%">Data trasformazione</td>	
  <td class="campo">
   <af:textBox classNameBase="input" onKeyUp="fieldChanged();settaAutorizzazioneDurataTD(false);" type="date" validateOnPost="true" name="datInizioMovTra" title="Data inizio trasformazione" size="12" maxlength="10" value="<%=datInizioMovTra%>" readonly="<%=String.valueOf(!canModify)%>"/>&nbsp;*
  </td>
</tr>

<tr id="sezTra2" style="display:none">
	<td class="etichetta" nowrap>Motivo&nbsp;Trasformazione</td>
	<td class="campo" nowrap>
		<af:comboBox moduleName="ComboTipoTrasformazione" selectedValue="<%=codMvTrasformazione%>" 
					 classNameBase="input" title="Motivo trasformazione" 
				     name="CODTIPOTRASF" onChange="fieldChanged();getTipoTrasf();getTipoTrasf2();" 
			    	 addBlank="true" disabled="<%=String.valueOf(!canModify)%>"/>*
	</td>	
</tr>

<tr><td></td></tr>

<tr id="datFineAffittoRamo" style="display:none">
	<td class="etichetta2">Data fine affitto ramo</td>	
	<td colspan="5" class="campo2">
		<table style="border-collapse:collapse">
			<tr>
				<td class="campo2">
					<af:textBox classNameBase="input" onKeyUp="fieldChanged();" type="date" validateOnPost="true" name="DATFINEAFFITTORAMO" title="Data fine affitto ramo"  size="12" value="<%=dataFineAffittoRamo%>" readonly="<%=String.valueOf(!canModify)%>" onBlur="javascript:checkFormatDate(this);"/>&nbsp;
				</td>
				<td>&nbsp;</td>
			</tr>
		 </table>
		</td>
</tr>

<tr><td></td></tr>
<tr id="datFineDistacco" style="display:none">
	<td class="etichetta2">Data fine distacco</td>	
	<td colspan="5" class="campo2">
		<table style="border-collapse:collapse">
			<tr>
				<td class="campo2">
					<af:textBox classNameBase="input" onKeyUp="fieldChanged();settaAutorizzazioneDurataTD(false);" type="date" validateOnPost="true" name="DATFINEDISTACCO" title="Data fine distacco"  size="12" value="<%=datFineDistacco%>" readonly="<%=String.valueOf(!canModify)%>" onBlur="javascript:checkFormatDate(this);"/>&nbsp;
				</td>
				<td>&nbsp;</td>
				<td class="etichetta2">Distacco Azi. Estera</td>
				<td><input id="flagAziEstera" type="checkbox" title="Flag Azi. Estera" name="flagAziEstera" value="" <%=flagAziEstera.equals("S") ? "CHECKED" : ""%>
						   <%=canModify ? "" : "disabled='true'"%> onclick="fieldChanged();gestisciFlagAziEsterna();"/>
				<input type="hidden" name="FLGDISTAZESTERA" value="<%=flagAziEstera%>"/>
				</td><td>&nbsp;&nbsp;
					<input type="button" name="bottoneDistacco" class="pulsanti" name="Distacco" value="Scelta azienda distaccataria" onClick="javascript:apriAzUtilDistaccata<%=(valida ? "Validazione" : "")%>();"/>
				</td>
			</tr>
		</table>
	</td>
</tr>
<tr id="flgAziDistacco" style="display:none">
	<td class="etichetta" nowrap>Distacco parziale</td>
	<td class="campo" nowrap>
		<input id="flagDistacco" type="checkbox" title="distacco parziale" name="flagDistacco" value="" <%=flgDistParziale.equals("S") ? "CHECKED" : ""%>
				<%=canModify ? "" : "disabled='true'"%> onclick="fieldChanged();gestisciFlagDistacco();"/>
				<input type="hidden" name="FLGDISTPARZIALE" value="<%=flgDistParziale%>"/>
	</td>
	
</tr>
<tr><td>&nbsp;&nbsp;</td></tr>

<tr>
	<td colspan="4">
    <div class='sezione2' id="sezAziTra" style="display:none">
    	<img alt='Chiudi' src="../../img/aperto.gif"/>
   		Sede Azienda Trasferimento&nbsp;&nbsp; 
    		<a id='ricercaAzienda' href="#" onClick="javascript:apriAziTrasf();">
    		<img src="../../img/binocolo.gif" alt="Cerca azienda nel DB"></a>
    </div>
	</td>
</tr>
</table>
<div id="datiAziTra" style="display:none">
	<table class="main">
		<%@ include file="aziendaNewTra.inc" %>
	</table>
</div>
<%= htmlStreamBottom %>
</div>
