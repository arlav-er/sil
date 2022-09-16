<table>

<tr>
 <td class="etichetta">Data domanda</td>
  <td colspan="2">
  <af:textBox classNameBase="input" type="date" name="dataDomanda" value="<%=Utils.notNull(dataDomanda)%>" 
  				validateOnPost="true" title="Data domanda" readonly="<%=String.valueOf(!canModify)%>" 
  				onKeyUp="fieldChanged();" size="11" maxlength="10" required="false"/>
</td>
</tr>

<tr>
    <td class="etichetta">Tipo lista</td>
    <td><af:comboBox name="codTipoMob" moduleName="M_GETDEMBTIPO" selectedValue="<%=Utils.notNull(codTipoMob)%>"
                     classNameBase="input" addBlank="true" disabled="<%=String.valueOf(!canModify)%>"
                     required="true" title="Tipo lista" onChange="fieldChanged();"/>
    </td>
</tr>

<% 
if ("1".equals(str_conf_MBDUBBIO)){
%>
<tr>
    <td class="etichetta">Caso dubbio</td>
    <td class="campo" colspan="2">
 	    <af:comboBox name="flgCasoDubbio" classNameBase="input" addBlank="false" required="false" disabled="<%=String.valueOf(!canModify)%>" 
 	     title="Flag caso dubbio"> 
	        <OPTION value=""></OPTION>
	        <OPTION value="S" <%if (flgCasoDubbio != null && flgCasoDubbio.equalsIgnoreCase("S")) out.print("SELECTED=\"true\"");%>>Sì</OPTION>
	        <OPTION value="N" <%if (flgCasoDubbio != null && flgCasoDubbio.equalsIgnoreCase("N")) out.print("SELECTED=\"true\"");%>>No</OPTION>
	    </af:comboBox>
	 </td>
</tr>
<%}%>	
<tr>
	<td class="etichetta" nowrap>Datore di lavoro non imprenditore</td>
	<td class="campo" colspan="2">
 	    <af:comboBox name="flgNonImprenditore" classNameBase="input" addBlank="false" required="true" disabled="<%=String.valueOf(!canModify)%>"
 	     title="Datore di lavoro non imprenditore" > 
	        <OPTION value="" <%if (flgNonImprenditore == null) out.print("SELECTED=\"true\"");%>></OPTION>
	        <OPTION value="S" <%if (flgNonImprenditore != null && flgNonImprenditore.equalsIgnoreCase("S")) out.print("SELECTED=\"true\"");%>>Sì</OPTION>
	        <OPTION value="N" <%if (flgNonImprenditore != null && flgNonImprenditore.equalsIgnoreCase("N")) out.print("SELECTED=\"true\"");%>>No</OPTION>
	    </af:comboBox>  
    </td>
</tr>

<tr>
  <td class="etichetta" valign="top">Azienda</td>
  <td>
  <div id="sezione_azienda" style="display:none">
  </div>
  <af:textBox type="text" classNameBase="input" size="55" name="Ragione"   value="<%=Utils.notNull(strRagioneSocialeAz)%>" readonly="true"/><br>
  <af:textBox type="text" classNameBase="input" size="55" name="Indirizzo" value="<%=Utils.notNull(strIndirizzoUAz)%>" readonly="true"/><br>
  <af:textBox type="text" classNameBase="input" size="55" name="Comune"    value="<%=Utils.notNull(strComuneUAz)%>" readonly="true"/><br>
  <af:textBox type="text" classNameBase="input" size="55" name="PIva"      value="<%=Utils.notNull(strPartitaIvaAz)%>" readonly="true"/><br>
  <af:textBox type="text" classNameBase="input" size="55" name="strTel"    value="<%=Utils.notNull(strTelUAz)%>" readonly="true"/>
  <input type="hidden" name="PRGUNITA"   value="<%=prgUnita%>">
  <input type="hidden" name="PRGAZIENDA" value="<%=prgAzienda%>">
  <input type="hidden" name="CODCOM" value="<%=codComune%>">
  </td>
</tr>

<tr>
   <td class="etichetta">Periodo lavorativo dal</td>
   <td><af:textBox classNameBase="input"  type="date" name="datInizMov" value="<%=Utils.notNull(dataInizioMov)%>" title="Periodo lavorativo dal" 
                   required="true" validateOnPost="true" size="11" maxlength="10" readonly="<%=String.valueOf(!canModify)%>"/>
&nbsp;&nbsp;al&nbsp;<af:textBox classNameBase="input"  type="date" name="datFineMov" value="<%=Utils.notNull(dataFineMov)%>" title="Periodo lavorativo al" 
                                required="true" validateOnPost="true" size="11" maxlength="10" readonly="<%=String.valueOf(!canModify)%>"/>
   <%if ( (canModify) && (!prgAzienda.equals("")) ) {%>              
  	<a href="#"  onClick="javascript:apriListaAziendeUnita('<%=cdnLavoratore%>','<%=prgAzienda%>');"><img src=" ../../img/binocolo.gif" alt="Cerca movimento"/></a>&nbsp;&nbsp;
  	<a href="#" onClick="javascript:cancCampi();"><img src=" ../../img/del.gif" alt="Elimina movimento"/></a>                 
  <%}%>
   <input type="hidden" name="datInizMovHid" value="">
   <input type="hidden" name="datFineMovHid" value="">
   </td>
</tr>

<tr>
  <td class="etichetta">Data inizio</td>
  <td><af:textBox classNameBase="input" type="date" name="datInizio" value="<%=Utils.notNull(dataInizioMob)%>"
  			    title="Data inizio" validateOnPost="true"
                onKeyUp="fieldChanged();" size="11" maxlength="10" readonly="true"/>	
    <input type="hidden" name="datInizioHid" value="">
    </td>
</tr>

<tr>
  <td class="etichetta">Data fine originaria</td>
  <td><af:textBox classNameBase="input" type="date" name="datFineOrig" value="<%=Utils.notNull(dataFineMob)%>" validateOnPost="true" 
                title="Data fine originaria" readonly="<%=String.valueOf(!canModify)%>" onKeyUp="fieldChanged();" size="11" maxlength="10" required="true"/>
 <%if (canModify) {
   	if (!prgMovimento.equals("")) {%>
		<div id="sezione_valorizza_date" style="display:none">
	<%} else {%>
		<div id="sezione_valorizza_date" style="display:inline">
	<%}%>
 	<input class="pulsante" type="button" name="btnCalcolaDate" value="Valorizza date" onclick="gestioneDateMobilita('<%=cdnLavoratore%>');">
 </div>
 <%}%>
 </td>
</tr>

<tr>
  <td class="etichetta">Data fine</td>
  <td><af:textBox classNameBase="input" type="date" name="datFine" value="<%=Utils.notNull(dataFineMob)%>" validateOnPost="true" 
                title="Data fine" readonly="<%=String.valueOf(!canModify)%>" onKeyUp="fieldChanged();" size="11" maxlength="10" required="true"/></td>
</tr>

<tr>
  <td class="etichetta">Data max differ.</td>
  <td><af:textBox classNameBase="input" type="date" name="datMaxDiff" value="<%=Utils.notNull(dataMaxDiff)%>" validateOnPost="true" 
                  readonly="<%=String.valueOf(!canModify)%>" onKeyUp="fieldChanged();" size="11" maxlength="10" required="true"/></td>
</tr>

<tr>
    <td class="etichetta">Motivo scorrimento data max differ.<br/></td>
    <td class="campo" colspan="3">
    	<af:comboBox classNameBase="input" addBlank="true" name="MotScorrDataMaxDiff"
		    multiple="false" onChange="fieldChanged();" 
            focusOn="false" moduleName="M_MobMotivoScorrimento"
            selectedValue="<%=Utils.notNull(motScorrDataMaxDiff)%>" 
            disabled="<%= String.valueOf(!canModify) %>"/>
       </td>
</tr>

<tr>
  <td class="etichetta">Indennità</td>
  <td>
    <af:comboBox name="flgIndennita" classNameBase="input" addBlank="false" onChange="fieldChanged();controllaIndennita(this,'opzioni_indennita');" disabled="<%=String.valueOf(!canModify)%>"> 
        <OPTION value=""></OPTION>
        <OPTION value="S" <%if (indennita_flg != null && indennita_flg.equalsIgnoreCase("S")) out.print("SELECTED=\"true\"");%>>Sì</OPTION>
        <OPTION value="N" <%if (indennita_flg != null && indennita_flg.equalsIgnoreCase("N")) out.print("SELECTED=\"true\"");%>>No</OPTION>
    </af:comboBox>  
   	<div id="opzioni_indennita" style="display:none">
    &nbsp;&nbsp;dal&nbsp;<af:textBox classNameBase="input" type="date" name="dataInizioIndenn" value="<%=Utils.notNull(dataInizioIndennita)%>" title="Inizio Indennità"
    						required="false" readonly="<%=String.valueOf(!canModify)%>" size="11" maxlength="10" validateOnPost="true"/>
    &nbsp;&nbsp;al&nbsp;<af:textBox classNameBase="input" type="date" name="dataFineIndenn" value="<%=Utils.notNull(dataFineIndennita)%>" title="Fine Indennità"
    						required="false" readonly="<%=String.valueOf(!canModify)%>" size="11" maxlength="10" validateOnPost="true"/>
 	</div>
 	<%if (indennita_flg != null && indennita_flg.equalsIgnoreCase("S")) {
 	%>
 	 <script>
 	 	var objIndennita = document.getElementById('opzioni_indennita');
      	objIndennita.style.display = "inline";
 	 </script>
 	<%} 
 	else {%> 
 	<script>
 		document.Frm1.dataInizioIndenn.value = "";
      	document.Frm1.dataFineIndenn.value = "";
 	</script>
 	<%}%> 
  </td>
</tr>

<tr>
	<td class="etichetta" nowrap>Qualifica</td>
	<td class="campo" nowrap>
		<af:textBox classNameBase="input" title="Qualifica" name="CODMANSIONE" size="7" maxlength="7" 
		    value="<%=Utils.notNull(codMansione)%>" readonly="<%=String.valueOf(!canModify)%>"/>
		<af:textBox type="hidden" name="codMansioneHid" value="<%=Utils.notNull(codMansione)%>"/>
		<%if (canModify) {%>
	  		<div id="sezione_mansione_codice" style="display:inline">
		  		<a href="javascript:selectMansioneOnClick(document.Frm1.CODMANSIONE, document.Frm1.codMansioneHid, document.Frm1.DESCMANSIONE,  document.Frm1.strTipoMansione);"><img src="../../img/binocolo.gif" alt="Cerca"></A>
	  		</div>
	  	<%}%>
        <af:textBox classNameBase="input" type="text" size="40" name="DESCMANSIONE" 
        	value="<%=Utils.notNull(descMansione)%>" readonly="<%=String.valueOf(!canModify)%>"/>
        <af:textBox type="hidden" name="strTipoMansione" value=""/>
        <%if (canModify) {%>
	  		<div id="sezione_mansione_desc" style="display:inline">
		  		<A href="javascript:selectMansionePerDescrizione(document.Frm1.DESCMANSIONE);">
	   	 			<img src="../../img/binocolo.gif" alt="Cerca per descrizione">
	   			</A>
	   		</div>
	   		<div id="sezione_mansione_avanzata" style="display:inline">
		  		<A href="javascript:ricercaAvanzataMansioni();">Ricerca avanzata</A>
			</div>
			<script language="javascript">
			  if(document.Frm1.CODMANSIONE.value != null && document.Frm1.CODMANSIONE.value != "" &&
			    (document.Frm1.strTipoMansione.value == "")) {
			     selectMansioneOnClick(document.Frm1.CODMANSIONE, document.Frm1.codMansioneHid, document.Frm1.DESCMANSIONE,  document.Frm1.strTipoMansione);
			  }
			</script>
	 	<%}%>
	<af:textBox type="hidden" name="CODTIPOMANSIONE" value=""/>
	<af:textBox type="hidden" name="strDesAttivita" value=""/>
	</td>
</tr>

<%if (configuarazioneMob.equals("0")) {%>
	<tr>
	<td class="etichetta">Grado</td>
		<td class="campo" colspan="3">
			<af:comboBox moduleName="ComboTipoGrado" title="Grado" selectedValue="<%=Utils.notNull(codGradoMov)%>" classNameBase="input" 
				 name="codGrado" addBlank="true" disabled="<%=String.valueOf(!canModify)%>"/>
			<input type="hidden" name="codGradoHid" value="">
		</td>
	</tr>
<%} else {%>
	<input type="hidden" name="codGrado" value="<%=Utils.notNull(codGradoMov)%>">
	<input type="hidden" name="codGradoHid" value="">
<%}%>

<tr>
<td class="etichetta">Livello</td>
	<td class="campo" colspan="3">
		<af:textBox classNameBase="input" type="text" name="strLivello"
		   title="Livello" maxlength="8" size="8" value="<%=Utils.notNull(strLivello)%>" readonly="<%=String.valueOf(!canModify)%>"/>
	</td>
</tr>

<tr>
	<td class="etichetta">CCNL</td>
	<td class="campo" colspan="3">
		<af:textBox classNameBase="input" title="CCNL" onKeyUp="fieldChanged();PulisciRicercaCCNL(document.Frm1.codCCNL, document.Frm1.codCCNLHid, document.Frm1.strCCNL, document.Frm1.strCCNLHid, 'codice');" 
		   type="text" name="codCCNL" value="<%=Utils.notNull(codCCNLMov)%>" size="10" maxlength="8" readonly="<%=String.valueOf(!canModify)%>"/>&nbsp;
		   	<%if (canModify) {%>
				<div id="sezione_ccnl_codice" style="display:inline">
			    	<A HREF="javascript:btFindCCNL_onclick(document.Frm1.codCCNL, document.Frm1.codCCNLHid, document.Frm1.strCCNL, document.Frm1.strCCNLHid, 'codice');">
				  		<IMG name="image" border="0" src="../../img/binocolo.gif" alt="cerca per codice"/></a>&nbsp;
			  	</div>
		    <%}%>
		<af:textBox type="hidden" name="codCCNLHid" value="<%=Utils.notNull(codCCNLMov)%>" />
		<af:textBox type="text" classNameBase="input" onKeyUp="fieldChanged();PulisciRicercaCCNL(document.Frm1.codCCNL, document.Frm1.codCCNLHid, document.Frm1.strCCNL, Frm1.strCCNLHid, 'descrizione');" 
		    name="strCCNL" value="<%=Utils.notNull(descrCCNLMov)%>" size="35" maxlength="50" readonly="<%=String.valueOf(!canModify)%>"/>&nbsp;                   
		<%if (canModify) { %>
			<div id="sezione_ccnl_desc" style="display:inline">
				<A HREF="javascript:btFindCCNL_onclick(document.Frm1.codCCNL, document.Frm1.codCCNLHid, document.Frm1.strCCNL, document.Frm1.strCCNLHid, 'descrizione');">
					<IMG name="image" border="0" src="../../img/binocolo.gif" alt="cerca per descrizione"/></a>
			</div>
		<%}%>
		<af:textBox type="hidden" name="strCCNLHid" value="<%=Utils.notNull(descrCCNLMov)%>" />
		<%if (canModify) {%>
			<div id="sezione_ccnl_ricerca_avanzata" style="display:inline">
				<A href="javascript:ricercaAvanzataCCNL();">Ricerca avanzata</A> 
			</div>
		<%}%>
	</td>
</tr>

<tr>
	<td class="etichetta">Ore</td>
	<td class="campo2" colspan="2">
	<af:textBox classNameBase="input" onKeyUp="fieldChanged();" type="fixdecimal" validateOnPost="true" name="numOreSett" 
		title="\'Ore settimanali\'" maxlength="9" size="12" value="<%=numOreSett%>" readonly="<%=String.valueOf(!canModify)%>" />
	</td>
</tr>

<tr>
    <td class="etichetta"><%=labelDataCrt %><br/></td>
    <td class="campo" colspan="3">
      <af:textBox classNameBase="input" name="datCRT" value="<%=dataCRT%>"
                   size="11" maxlength="10" type="date" validateOnPost="true" title="Data CRT"
                   onKeyUp="fieldChanged();" readonly="true"  />
       </td>
</tr>

<tr>
   <td class="etichetta"><%=labelNumeroCrt %><br/></td>
	<td class="campo" colspan="3">
  	  <af:textBox classNameBase="input" name="numCRT" value="<%=Utils.notNull(numCRT)%>"
               size="15" maxlength="15" title="Numero CRT"
               onKeyUp="fieldChanged();" readonly="true" />
   </td>
</tr>

<tr>
    <td class="etichetta"><%=labelRegioneCrt %><br/></td>
    <td class="campo" colspan="3">
      <af:comboBox classNameBase="input" addBlank="true" name="regioneCRT"
		    multiple="false" onChange="fieldChanged();" 
            focusOn="false" moduleName="M_MobGetRegioni"
            selectedValue="<%=Utils.notNull(regCRT)%>"  
            disabled="<%= String.valueOf(!canModify) %>"/>
       </td>
</tr>

<tr>
    <td class="etichetta"><%=labelProvinciaCrt %><br/></td>
    <td class="campo" colspan="3">
    	<af:comboBox classNameBase="input" addBlank="true" name="provCRT"
		    multiple="false" onChange="fieldChanged();" 
            focusOn="false" moduleName="M_MobGetProvince" 
            selectedValue="<%=Utils.notNull(provCRT)%>" 
            disabled="<%= String.valueOf(!canModify) %>"/>
       </td>
</tr>

<tr>
    <td class="etichetta">Motivo decadenza<br/></td>
    <td class="campo" colspan="3">
    	<af:comboBox classNameBase="input" addBlank="true" name="MotDecad"
		    multiple="false" onChange="fieldChanged();" 
            focusOn="false" moduleName="M_MobilitaGetTipoFine"
            selectedValue="<%=codMotivoFine%>" 
            disabled="true"/>
       </td>
</tr>

<%if (! "".equals(codDomanda)) { %>

<tr>
    <td class="etichetta">Cod. domanda<br/></td>
    <td class="campo" colspan="3">
    	<af:textBox classNameBase="input" name="CODDOMANDA" value="<%=codDomanda%>"
               maxlength="16" title="Cod. domanda"
               readonly="true" />
		   
       </td>
</tr>

<%} %>


</table>

<SCRIPT TYPE="text/javascript">
	<%
	if (!canModify || !sceltaMov) {%>
		document.Frm1.datInizMov.readOnly = true;
		document.Frm1.datFineMov.readOnly = true;
		document.Frm1.datInizio.readOnly = true;
		document.Frm1.CODMANSIONE.readOnly = true;
		document.Frm1.DESCMANSIONE.readOnly = true;
		document.Frm1.codGrado.disabled = true;
		document.Frm1.strLivello.readOnly = true;
		document.Frm1.codCCNL.readOnly = true;
		document.Frm1.strCCNL.readOnly = true;
	    window.document.Frm1.datInizMov.className="inputView";
		window.document.Frm1.datFineMov.className="inputView";
		window.document.Frm1.datInizio.className="inputView";
      	window.document.Frm1.codCCNL.className="inputView";
      	window.document.Frm1.strCCNL.className="inputView";
      	window.document.Frm1.CODMANSIONE.className="inputView";
      	window.document.Frm1.DESCMANSIONE.className="inputView";
      	window.document.Frm1.strLivello.className="inputView";
      	window.document.Frm1.datInizMov.className="inputView";
      	window.document.Frm1.datFineMov.className="inputView";
		window.document.Frm1.datInizio.className="inputView";
	<%}%>
</SCRIPT>

