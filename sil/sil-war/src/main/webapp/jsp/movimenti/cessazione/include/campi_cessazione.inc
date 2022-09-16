<%if (mostraCampoInizioAvviamento) {%>
<tr>
    <td colspan="4">
     <div class="sezione2" id="Avviamento">&nbsp;Condizioni di Avviamento a TI 
     </div>
    </td>
</tr>

<tr>
	<td class="etichetta">Data inizio</td>
	<td class="campo" colspan="3" nowrap>
	
		<table width="100%" border="0">
			<tr>
				<td  width="30%">
					<af:textBox name="DATAINIZIOAVVCEV" value="<%=datInizioAVVperCVE%>" type="date" onKeyUp="fieldChanged();visualizzaCombo();" onBlur="visualizzaCombo();" title="Data inizio avviamento da collegare"  classNameBase="input" onKeyUp="fieldChanged();visualizzaCombo();" validateOnPost="true" size="12" maxlength="10" readonly="<%=String.valueOf(!canModify || valida)%>"  callBackDateFunction="visualizzaCombo();"/>
				</td>
				<td width="70%" align="left">
					<span id="orario" style="<%=((!canModify ))?"visibility:hidden":"visibility:visible"%>">
						<table border="0">
							<tr>
								<td name="labOrario" id="labelorario" class="etichetta2" >
									Orario
								</td>
								
								<td  id="campoOrario" align="left" >
									<af:comboBox moduleName="ComboTipoOrario" selectedValue="<%=codOrario%>" title="Orario" classNameBase="input" name="codOrario"  onChange="visualizzaOreSett(this);fieldChanged();"  addBlank="true" disabled="<%=String.valueOf(!canModify )%>"  />*
								</td>
				
								<td >
								<span id="labelore">
								<table>
									<tr>
										<td  class="campo" width="70%">
											Ore settimanali&nbsp;
											<af:textBox classNameBase="input" onKeyUp="fieldChanged();" type="fixdecimal" validateOnPost="true" name="numOreSett" title="Ore settimanali" maxlength="5" size="5" value="<%=numOreSett%>" readonly="<%=String.valueOf(!canModify )%>" />*
											<script language="javascript">
											visualizzaOreSett(document.Frm1.codOrario);
											</script>
										</td>
									</tr>
								</table>
								</span>
								</td>
							</tr>
						</table>
					</span>
				</td>
			</tr>
		</table>	
	</td>	
</tr>



<tr><td>&nbsp;</td></tr>
<%}%>

<tr>
	<td colspan="4">          
		<div class="sezione2" id="Cessazione">
			&nbsp;Condizioni di Cessazione
		</div>
	</td>
</tr>
<tr>
	<td class="etichetta">Motivo</td>
	<td class="campo">
		<af:comboBox moduleName="ComboTipoCessazione" selectedValue="<%=codMvCessazione%>" 
			classNameBase="input" title="Motivo cessazione" 
			 name="codMvCessazione" onChange="fieldChanged();" 
			addBlank="true" disabled="<%=String.valueOf(!canModify)%>"/> 
	</td> 
	<td class="etichetta">Data</td>
	<td class="campo" nowrap>
		<af:textBox classNameBase="input" onKeyUp="fieldChanged();" type="date" validateOnPost="true" name="datInizioMov" title="Data Cessazione" required="true" size="12" maxlength="10" value="<%=datInizioMov%>" readonly="<%=String.valueOf(!canModify)%>" onBlur="javascript:calcolaDiffGiorni(this,document.Frm1.NUMGGTRAMOVCOMUNICAZIONE,varRange);"/>
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
	<TD class="etichetta" >Giorni dalla data di comunicazione</TD>
	<td class="campo" id="labelRit" colspan="2" >
		<af:textBox classNameBase="input" onKeyUp="fieldChanged();" 
				type="number" validateOnPost="true" name="NUMGGTRAMOVCOMUNICAZIONE" 
				required="false" title="Giorni di ritardo"  size="12" maxlength="10" 
				value="<%=numGgTraMovComunicaz%>" readonly="<%=String.valueOf(!canModify)%>"/>
	</td>
</tr>

<tr>
	<td colspan="4">          
	<div class="sezione2" id="SituazioneAttuale">
	&nbsp;Situazione attuale all'atto della Cessazione
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
	<td colspan="4" class="campo">
		<af:textBox type="hidden" name="CODTIPOMANSIONE" value=""/>
		Tipo mans.:&nbsp;
		<af:textBox classNameBase="input" type="text" size="27" name="strTipoMansione" value="<%=descrTipoMansione%>" readonly="true"/>&nbsp;&nbsp;
		<%if (canModify) {%> 
			<A href="javascript:ricercaAvanzataMansioni();">Ricerca avanzata</A>
		<%}%> 
		
	</td>
</tr>

<!--Giovanni D'Auria 08/02/2005 fine -->
<tr>
	<td class="etichetta">Grado</td>
	<td class="campo" nowrap>
	<%--af:comboBox moduleName="ComboTipoGrado" title="Grado" selectedValue="<%=codGrado%>" classNameBase="input" name="codGrado" onChange="fieldChanged();" addBlank="true" disabled="<%=String.valueOf(!canModify)%>"/>--%>
		<af:comboBox moduleName="ComboTipoGrado" title="Grado" selectedValue="<%=codGrado%>" classNameBase="input" name="codGradoVisualizz" onChange="fieldChanged();riportaGradoHidden();" addBlank="true" disabled="<%=String.valueOf(!canModify)%>"/>
	    <input type="hidden" name="codGrado" value="<%=codGrado%>"/>
	</td>
	<td class="etichetta">Livello</td>
	<td class="campo">
	<%--String lunghezzaLivello = "2";
	  if ( numGGPrevistiAgr!=null && !numGGPrevistiAgr.equals("")) {
	    lunghezzaLivello = "3";
	  }
	--%>
	<af:textBox classNameBase="input" onKeyUp="fieldChanged();" type="text" name="numLivello" title="Livello" maxlength="6" size="4" value="<%=numLivello%>" validateOnPost="true" readonly="<%=String.valueOf(!canModify)%>"/>
	</td>
</tr>

<%if (canModify || (!canModify && numGGEffettuatiAgr!=null  && !numGGEffettuatiAgr.equals(""))) {%>
<tr>
	<td class="etichetta">Giorni effettivi in agric.</td>
	<td class="campo" colspan="3">
	<af:textBox classNameBase="input" type="integer" validateOnPost="true"  onKeyUp="fieldChanged();" name="NUMGGEFFETTUATIAGR" title="Num. giorni effettivi in agr."  size="4" maxlength="3" value="<%=numGGEffettuatiAgr%>" readonly="<%=String.valueOf(!canModify)%>"/>
	</td>
</tr>
<%}%>
