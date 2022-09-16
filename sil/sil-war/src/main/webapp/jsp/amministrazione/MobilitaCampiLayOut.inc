<table>

<tr>
  <td class="etichetta">Data domanda</td>
  <td colspan="2">
  <af:textBox classNameBase="input" type="date" name="dataDomanda" value="<%=Utils.notNull(dataDomanda)%>" 
  				validateOnPost="true" title="Data domanda" readonly="<%=String.valueOf(readOnlyStr)%>" 
  				onKeyUp="fieldChanged();" size="11" maxlength="10" required="false"/>
  </td>
</tr>

<tr>
	<td class="etichetta"><%=labelStatoDomanda %></td>
    <td colspan="2">
   	<% if(canUpdate){ %>
    <input type="hidden" name="MODULE" value="M_AggiornaStatoMobilita" />
    <% } %>
    <af:comboBox name="codStatoMob" moduleName="M_GetStatoMob" selectedValue="<%=Utils.notNull(codMBStato)%>"
                     classNameBase="input" addBlank="true" disabled="<%=String.valueOf(!canComboStato)%>"
                     title="Stato" onChange="fieldChanged();"/>&nbsp;&nbsp
    <% if(canUpdate){ %>
    <input type="hidden" name="tipoMobilita" value="<%= codMBTipoLetto %>" />
    <input type="hidden" name="CONTINUA_AGGIORNAMENTO_STORICO" value="false" />
    <!-- input class="pulsante" type="submit" name="aggiorna" value="Aggiorna stato"/-->
    <% } %>
    </td>

</tr>
<tr>
    <td class="etichetta">Tipo lista</td>
    <%if (canTipoListaPatronato) {
	    if (flag_insert) { %>
		    <td colspan="2"><af:comboBox name="codTipoMob" moduleName="M_GETDEMBTIPOPATRONATO" selectedValue="<%=Utils.notNull(codMBTipoLetto)%>"
		                     classNameBase="input" addBlank="true" disabled="<%=String.valueOf(readOnlyStr)%>"
		                     required="true" title="Tipo lista" onChange="fieldChanged();"/>
		    </td>
	  	<%} else {
	  		if (canAggiornaMBPatronato) { %>
	  			<td colspan="2"><af:comboBox name="codTipoMob" moduleName="M_GETDEMBTIPOPATRONATO" selectedValue="<%=Utils.notNull(codMBTipoLetto)%>"
		                     classNameBase="input" addBlank="true" disabled="<%=String.valueOf(readOnlyStr)%>"
		                     required="true" title="Tipo lista" onChange="fieldChanged();"/>
		    	</td>
	  		<%} else { %>
		  		<td colspan="2"><af:comboBox name="codTipoMob" moduleName="M_GETDEMBTIPO" selectedValue="<%=Utils.notNull(codMBTipoLetto)%>"
		                     classNameBase="input" addBlank="true" disabled="true"
		                     required="true" title="Tipo lista" onChange="fieldChanged();"/>
		    	</td>
	  		<%}
	  	}    
	} else {%>
		<td colspan="2"><af:comboBox name="codTipoMob" moduleName="M_GETDEMBTIPO" selectedValue="<%=Utils.notNull(codMBTipoLetto)%>"
	                     classNameBase="input" addBlank="true" disabled="<%=String.valueOf(readOnlyStr)%>"
	                     required="true" title="Tipo lista" onChange="fieldChanged();"/>
	    </td>
	<%}%>
</tr>
<%
if ("1".equals(str_conf_MBDUBBIO)){
%>
<tr>
    <td class="etichetta">Caso dubbio</td>
    <td class="campo" colspan="2">
 	    <af:comboBox name="casoDubbio" classNameBase="input" addBlank="false" required="true" 
 	     title="Flag caso dubbio" disabled="<%=String.valueOf(readOnlyStrFlag)%>" onChange="fieldChanged();controllaMotCasoDubbio(this,'tr_mot_sintetico_lb', 'tr_mot_sintetico_fd');"> 
	        <OPTION value=""></OPTION>
	        <OPTION value="S" <%if (flgCasoDubbio != null && flgCasoDubbio.equalsIgnoreCase("S")) out.print("SELECTED=\"true\"");%>>Sì</OPTION>
	        <OPTION value="N" <%if (flgCasoDubbio != null && flgCasoDubbio.equalsIgnoreCase("N")) out.print("SELECTED=\"true\"");%>>No</OPTION>
	    </af:comboBox>
	 </td>
</tr>
<tr>
    <td class="etichetta" nowrap>
    <div id="tr_mot_sintetico_lb" style="display:none">
    Motivo sintetico caso dubbio
    </div>
    </td>
    <td class="campo" colspan="2">
       <div id="tr_mot_sintetico_fd" style="display:none">
       <af:textArea classNameBase="textarea" name="strDescCasoDubbio" value="<%=Utils.notNull(strDescCasoDubbio)%>"
           cols="30" rows="4" maxlength="200" onKeyUp="fieldChanged();" readonly="<%=String.valueOf(readOnlyStrFlag)%>"/>
       </div>
     </td>
</tr>
<%}%>
<tr>
	<td class="etichetta" nowrap>Tipologia datore di lavoro</td>
	<td class="campo" colspan="2">
 	    <af:comboBox name="flgNonImprenditore" classNameBase="input" addBlank="false" required="true" onChange="fieldChanged();"
 	     title="Tipologia datore di lavoro" disabled="<%=String.valueOf(readOnlyStrFlag)%>"> 
	        <OPTION value="" <%if (nonImprenditore_flg == null) out.print("SELECTED=\"true\"");%>></OPTION>
	        <OPTION value="N" <%if (nonImprenditore_flg != null && nonImprenditore_flg.equalsIgnoreCase("N")) out.print("SELECTED=\"true\"");%>>Imprenditore</OPTION>
		<OPTION value="S" <%if (nonImprenditore_flg != null && nonImprenditore_flg.equalsIgnoreCase("S")) out.print("SELECTED=\"true\"");%>>Non imprenditore</OPTION>
	    </af:comboBox>
    </td>
</tr>
<tr>
  <%if(!readOnlyStr){%>
  <td class="etichetta"><br/>Azienda
	  <%if (!sceltaMov) {%>
	  	<div id="sezione_azienda" style="display:none">
	  <%} else {%>
	  	<div id="sezione_azienda" style="display:inline">
	  <%}%>
	  &nbsp;<a href="#" onClick="javascript:apriSelezionaSoggetto('Aziende', 'aggiornaAzienda','','','');"><img src="../../img/binocolo.gif" alt="Cerca"></a>
	  </div>
  </td>
  <td colspan="2">
  	&nbsp;<br/>
  </td>
</tr>
<tr>
  <td>&nbsp;</td>
  <%} else {%><td class="etichetta" valign="top">Azienda</td><%}%>
  
  <td colspan="2">
	  <af:textBox type="text" classNameBase="input" size="55" name="Ragione"   value="<%=Utils.notNull(rowRagioneSociale)%>" readonly="true"/><br>

	  <af:textBox type="text" classNameBase="input" size="55" name="Indirizzo" value="<%=Utils.notNull(rowIndirizzo)%>" readonly="true"/><br>
	  <af:textBox type="text" classNameBase="input" size="40" name="Comune"    value="<%=Utils.notNull(rowComune)%>" readonly="true"/> &nbsp;
	  <af:textBox type="text" classNameBase="input" size="10" name="Prov"   value="<%=Utils.notNull(rowProv)%>" readonly="true"/><br>  
	  <af:textBox type="text" classNameBase="input" size="20" name="CF"      value="<%=Utils.notNull(rowCf)%>" readonly="true"/>&nbsp;/&nbsp;
	  <af:textBox type="text" classNameBase="input" size="30" name="PIva"  value="<%=Utils.notNull(rowPIva)%>" readonly="true"/><br>
	  <af:textBox type="text" classNameBase="input" size="70" name="strTel"    value="<%=Utils.notNull(rowTel)%>" readonly="true"/>

	  <input type="hidden" name="PRGAZIENDA" value="<%=Utils.notNull(PRGAZIENDA)%>">
	  <input type="hidden" name="PRGUNITA"   value="<%=Utils.notNull(PRGUNITA)%>">
	  <input type="hidden" name="CODCOM" value="">
  </td>
</tr>


<tr>
   <td class="etichetta">Periodo lavorativo dal</td>
   <td colspan="2"><af:textBox classNameBase="input"  type="date" name="datInizMov" value="<%=Utils.notNull(dataInizMov)%>" title="Periodo lavorativo dal" 
                    required="true" readonly="<%=String.valueOf(!sceltaMov)%>" validateOnPost="true" size="11" maxlength="10"/>
&nbsp;&nbsp;al&nbsp;<af:textBox classNameBase="input"  type="date" name="datFineMov" value="<%=Utils.notNull(dataFineMov)%>" title="Periodo lavorativo al" 
                   required="true" readonly="<%=String.valueOf(!sceltaMov)%>" validateOnPost="true" size="11" maxlength="10"/>
  
  <%if (sceltaMov) {%>              
  	<a href="#"  onClick="javascript:apriListaAziendeUnita('<%=cdnLavoratore%>', document.Frm1.PRGAZIENDA.value);"><img src=" ../../img/binocolo.gif" alt="Cerca movimento"/></a>&nbsp;&nbsp;
  	<a href="#" onClick="javascript:cancCampi();"><img src=" ../../img/del.gif" alt="Elimina movimento"/></a>                 
  <%}%>
   <input type="hidden" name="datInizMovHid" value="">
   <input type="hidden" name="datFineMovHid" value="">
   </td>
</tr>

<tr>
  <td class="etichetta">Data inizio</td>
  <td colspan="2"><af:textBox classNameBase="input" type="date" name="datInizio" value="<%=Utils.notNull(dataInizio)%>"
  			    title="Data inizio" validateOnPost="true"
                readonly="true" onKeyUp="fieldChanged();" size="11" maxlength="10"/>	
    <input type="hidden" name="datInizioHid" value="">
  </td>
</tr>

<%if (Sottosistema.MO.isOn()) {%>
<tr>
  <td class="etichetta">Data fine originaria</td>
  <td colspan="2">
  <af:textBox classNameBase="input" type="date" name="datFineOrig" value="<%=Utils.notNull(dataFineOrig)%>" validateOnPost="true" 
                title="Data fine originaria" readonly="<%=String.valueOf(readOnlyStr)%>" onKeyUp="fieldChanged();" size="11" maxlength="10" required="true"/>
	<%if (!readOnlyStr) {
	    if (!sceltaMov) {%>
		  	<div id="sezione_valorizza_date" style="display:none">
		<%} else {%>
			<div id="sezione_valorizza_date" style="display:inline">
		<%}%>
		<input class="pulsante" type="button" name="btnCalcolaDate" value="Valorizza date" onclick="gestioneDateMobilita('<%=cdnLavoratore%>');">
		</div>
 	<%}%>
  </td>
</tr>
<%}%>

<tr>
  <td class="etichetta">Data fine</td>
  <td colspan="2"><af:textBox classNameBase="input" type="date" name="datFine" value="<%=Utils.notNull(dataFine)%>" validateOnPost="true" 
                title="Data fine" readonly="<%=String.valueOf(readOnlyStr)%>" onKeyUp="fieldChanged();" size="11" maxlength="10" required="true"/></td>
</tr>

<tr>
  <td class="etichetta">Data max differ.</td>
  <td colspan="2"><af:textBox classNameBase="input" type="date" name="datMaxDiff" value="<%=Utils.notNull(dataMaxDiff)%>" validateOnPost="true" 
                  readonly="<%=String.valueOf(readOnlyStr)%>" onKeyUp="fieldChanged();" size="11" maxlength="10" required="true"/></td>
</tr>

<tr>
    <td class="etichetta">Motivo scorrimento data max differ.<br/></td>
    <td class="campo" colspan="2">
    	<af:comboBox classNameBase="input" addBlank="true" name="MotScorrDataMaxDiff"
		    multiple="false" onChange="fieldChanged();" 
            focusOn="false" moduleName="M_MobMotivoScorrimento"
            selectedValue="<%=Utils.notNull(motScorrDataMaxDiff)%>" 
            disabled="<%= String.valueOf(readOnlyStr) %>"/>
       </td>
</tr>

<tr>
  <td class="etichetta">Indennità</td>
  <td class="campo" colspan="2">
    <af:comboBox name="flgIndennita" classNameBase="input" addBlank="false" onChange="fieldChanged();controllaIndennita(this,'opzioni_indennita');" disabled="<%=String.valueOf(readOnlyStr)%>"> 
        <OPTION value=""></OPTION>
        <OPTION value="S" <%if (indennita_flg != null && indennita_flg.equalsIgnoreCase("S")) out.print("SELECTED=\"true\"");%>>Sì</OPTION>
        <OPTION value="N" <%if (indennita_flg != null && indennita_flg.equalsIgnoreCase("N")) out.print("SELECTED=\"true\"");%>>No</OPTION>
    </af:comboBox>  
   	<div id="opzioni_indennita" style="display:none">
    &nbsp;&nbsp;dal&nbsp;<af:textBox classNameBase="input" type="date" name="dataInizioIndenn" value="<%=Utils.notNull(dataInizioIndenn)%>" title="Inizio Indennità"
    						required="false" readonly="<%=String.valueOf(readOnlyStr)%>" size="11" maxlength="10" validateOnPost="true"/>
    &nbsp;&nbsp;al&nbsp;<af:textBox classNameBase="input" type="date" name="dataFineIndenn" value="<%=Utils.notNull(dataFineIndenn)%>" title="Fine Indennità"
    						required="false" readonly="<%=String.valueOf(readOnlyStr)%>" size="11" maxlength="10" validateOnPost="true"/>
 	</div>
 	<input type="hidden" name="dataFineIndennHid" value="<%=Utils.notNull(dataFineIndenn)%>">
 	<%if (indennita_flg != null && indennita_flg.equalsIgnoreCase("S")) {
 	%>
 	 <script>
 	 	var objIndennita = document.getElementById('opzioni_indennita');
      	objIndennita.style.display = "inline";
 	 </script>
 	<%}%> 
  </td>
</tr>

<%if (Sottosistema.MO.isOn()) {%>
<tr>
	<td class="etichetta" nowrap>Qualifica</td>
	<td colspan="2" class="campo" nowrap>
		<af:textBox classNameBase="input" title="Qualifica" name="CODMANSIONE" size="7" maxlength="7" 
		    value="<%=Utils.notNull(codMans)%>" readonly="<%=String.valueOf(readOnlyStr)%>"/>
		<af:textBox type="hidden" name="codMansioneHid" value="<%=Utils.notNull(codMans)%>"/>
		<%if (!readOnlyStr) {%>
	  		<div id="sezione_mansione_codice" style="display:inline">
	  			<a href="javascript:selectMansioneOnClick(document.Frm1.CODMANSIONE, document.Frm1.codMansioneHid, document.Frm1.DESCMANSIONE,  document.Frm1.strTipoMansione);"><img src="../../img/binocolo.gif" alt="Cerca"></A>
	  		</div>
	  	<%}%>
        <af:textBox classNameBase="input" type="text" size="40" name="DESCMANSIONE" 
        	value="<%=Utils.notNull(mans)%>" readonly="<%=String.valueOf(readOnlyStr)%>"/>
        <af:textBox type="hidden" name="strTipoMansione" value=""/>	
        <%if (!readOnlyStr) {%>
	  			<div id="sezione_mansione_desc" style="display:inline">
       			<A href="javascript:selectMansionePerDescrizione(document.Frm1.DESCMANSIONE);">
	   	 			<img src="../../img/binocolo.gif" alt="Cerca per descrizione">
	   				</A>
	   			</div>
		   		<div id="sezione_mansione_avanzata" style="display:inline">
			  		<A href="javascript:ricercaAvanzataMansioni();">Ricerca avanzata</A>
				</div>
				<%--
				<script language="javascript">				
				  if(document.Frm1.CODMANSIONE.value != null && document.Frm1.CODMANSIONE.value != "" &&
				    (document.Frm1.strTipoMansione.value == "")) {
				     selectMansioneOnClick(document.Frm1.CODMANSIONE, document.Frm1.codMansioneHid, document.Frm1.DESCMANSIONE,  document.Frm1.strTipoMansione);
				  }
				</script>
				 --%>
	 	<%}%>
	<af:textBox type="hidden" name="CODTIPOMANSIONE" value=""/>
	<af:textBox type="hidden" name="strDesAttivita" value=""/>
	</td>
</tr>

<%if (configuarazioneMob.equals("0")) {%>
<tr>
<td class="etichetta">Grado</td>
	<td class="campo" colspan="2">
		<af:comboBox moduleName="ComboTipoGrado" title="Grado" selectedValue="<%=Utils.notNull(codGrado)%>" classNameBase="input" 
			 name="codGrado" addBlank="true" disabled="<%=String.valueOf(readOnlyStr)%>"/>
		<input type="hidden" name="codGradoHid" value="">
	</td>
</tr>
<%} else {%>
	<input type="hidden" name="codGrado" value="<%=Utils.notNull(codGrado)%>">
	<input type="hidden" name="codGradoHid" value="">
<%}%>
	
<tr>
<td class="etichetta">Livello</td>
	<td class="campo" colspan="2">
		<af:textBox classNameBase="input" type="text" name="strLivello" readonly="<%=String.valueOf(readOnlyStr)%>"
		   title="Livello" maxlength="8" size="8" value="<%=Utils.notNull(numLivello)%>"/>
	</td>
</tr>

<tr>
	<td class="etichetta">CCNL</td>
	<td class="campo" colspan="2">
		<af:textBox classNameBase="input" title="CCNL" onKeyUp="fieldChanged();PulisciRicercaCCNL(document.Frm1.codCCNL, document.Frm1.codCCNLHid, document.Frm1.strCCNL, document.Frm1.strCCNLHid, 'codice');" 
		   type="text" name="codCCNL" value="<%=Utils.notNull(codCCNL)%>" readonly="<%=String.valueOf(readOnlyStr)%>"
		   size="10" maxlength="8" />&nbsp;
		 
		 <%if (!readOnlyStr) {%>
			  	<div id="sezione_ccnl_codice" style="display:inline">
		     		<A HREF="javascript:btFindCCNL_onclick(document.Frm1.codCCNL, document.Frm1.codCCNLHid, document.Frm1.strCCNL, document.Frm1.strCCNLHid, 'codice');">
						<IMG name="image" border="0" src="../../img/binocolo.gif" alt="cerca per codice"/></a>&nbsp;
				</div>
		<%}%>
		<af:textBox type="hidden" name="codCCNLHid" value="<%=Utils.notNull(codCCNL)%>" />
		<af:textBox type="text" classNameBase="input" onKeyUp="fieldChanged();PulisciRicercaCCNL(document.Frm1.codCCNL, document.Frm1.codCCNLHid, document.Frm1.strCCNL, Frm1.strCCNLHid, 'descrizione');" 
		    name="strCCNL" value="<%=Utils.notNull(strDescrizioneCCNL)%>" size="35" maxlength="50" readonly="<%=String.valueOf(readOnlyStr)%>"/>&nbsp;
		<%if (!readOnlyStr) { %>
			<div id="sezione_ccnl_desc" style="display:inline">
		    	<A HREF="javascript:btFindCCNL_onclick(document.Frm1.codCCNL, document.Frm1.codCCNLHid, document.Frm1.strCCNL, document.Frm1.strCCNLHid, 'descrizione');">
					<IMG name="image" border="0" src="../../img/binocolo.gif" alt="cerca per descrizione"/></a>
			</div>
			<af:textBox type="hidden" name="strCCNLHid" value="<%=Utils.notNull(strDescrizioneCCNL)%>" />
		<%}	
			
		if (!readOnlyStr) {%>
			<div id="sezione_ccnl_ricerca_avanzata" style="display:inline">
		    	<A href="javascript:ricercaAvanzataCCNL();">Ricerca avanzata</A> 
			</div>
		<%}%>		             
	</td>
</tr>
<%}%>

<tr>
<td class="etichetta">Ore</td>
<td class="campo2" colspan="2">
<af:textBox classNameBase="input" onKeyUp="fieldChanged();" type="fixdecimal" validateOnPost="true" name="numOreSett" 
	title="\'Ore settimanali\'" maxlength="9" size="12" value="<%=numOreSett%>" readonly="<%=String.valueOf(readOnlyStr)%>" />
</td>
</tr>

<tr>
    <td class="etichetta"><%=labelDataCrt %><br/></td>
    <td class="campo" colspan="2">
      <af:textBox classNameBase="input" name="datCRT" value="<%=Utils.notNull(datCRT)%>"
                   size="11" maxlength="10" type="date" validateOnPost="true" title="Data CRT"
                   onKeyUp="fieldChanged();" readonly="<%=String.valueOf(readOnlyStr)%>"  />
    </td>
</tr>

<%if (Sottosistema.MO.isOn()) {%>
<tr>
   <td class="etichetta"><%=labelNumeroCrt %><br/></td>
	<td class="campo" colspan="2">
  	  <af:textBox classNameBase="input" name="numCRT" value="<%=Utils.notNull(numCRT)%>"
               size="15" maxlength="15" title="Numero CRT"
               onKeyUp="fieldChanged();" readonly="<%=String.valueOf(readOnlyStr)%>" />
   </td>
</tr>

<tr>
    <td class="etichetta"><%=labelRegioneCrt %><br/></td>
    <td class="campo" colspan="2">
      <af:comboBox classNameBase="input" addBlank="true" name="regioneCRT"
		    multiple="false" onChange="fieldChanged();" 
            focusOn="false" moduleName="M_MobGetRegioni"
            selectedValue="<%=Utils.notNull(regCRT)%>"  
            disabled="<%= String.valueOf(readOnlyStr) %>"/>
       </td>
</tr>

<tr>
    <td class="etichetta"><%=labelProvinciaCrt %><br/></td>
    <td class="campo" colspan="2">
    	<af:comboBox classNameBase="input" addBlank="true" name="provCRT"
		    multiple="false" onChange="fieldChanged();" 
            focusOn="false" moduleName="M_MobGetProvince" 
            selectedValue="<%=Utils.notNull(provCRT)%>" 
            disabled="<%= String.valueOf(readOnlyStr) %>"/>
       </td>
</tr>
<%}%>

<tr>
    <td class="etichetta">Motivo decadenza<br/></td>
    <td class="campo" colspan="2">
    	<af:comboBox classNameBase="input" addBlank="true" name="MotDecad"
		    multiple="false" onChange="fieldChanged();" 
            focusOn="false" moduleName="M_MobilitaGetTipoFine"
            selectedValue="<%=Utils.notNull(motivoFine)%>" 
            disabled="<%= String.valueOf(readOnlyStr) %>"/>
       </td>
</tr>





<%if (Sottosistema.MO.isOn()) {
  if ( (!flag_insert) && (canViewSchedaDispo) ) {%>
	<tr> 
		<td class="etichetta" nowrap>Scheda informativa rilasciata</td>
		<td class="campo" colspan="2">
			<%if (flagDispo.equals("S")) {%>
				<input type="checkbox" onclick="gestioneBtnSchedaInformativa(this);" name="flagDisponibilita" value="S" checked/>
				&nbsp;&nbsp;&nbsp;
				<div id="sezione_scheda_informativa" style="display:inline">
					<input class="pulsante" type="button" name="btnDisponibilita" value="Scheda disponibilit&agrave;" 
						onClick="caricaGestioneSchedaInformativaUpdateFlag(<%=prgMobilita%>,<%=keyLock%>);">
<%--
						onClick="caricaGestioneSchedaInformativa(<%=prgMobilita%>);">
--%>				
					&nbsp;&nbsp;&nbsp;		
					<%if(_page.equals("MobilitaInfoStorDettPage") && canDelete){%>
						<a href="javascript:delDispo();"><img src="../../img/del.gif" alt="Elimina disponibilità"></A>
						<input type="hidden" name="save"   value="Aggiorna"/>
					<%}%>
				</div>

		 	<%} 
		 	else {%>
		 		<input type="checkbox" onclick="gestioneBtnSchedaInformativa(this);" name="flagDisponibilita" value="S"/>
		 		<div id="sezione_scheda_informativa" style="display:none">
		 			<input class="pulsante" type="button" name="btnDisponibilita" value="Scheda disponibilit&agrave;" 
						onClick="caricaGestioneSchedaInformativaUpdateFlag(<%=prgMobilita%>,<%=keyLock%>);">  
<%--
						onClick="caricaGestioneSchedaInformativa(<%=prgMobilita%>);">  
--%>
					&nbsp;&nbsp;&nbsp;
					<%if(_page.equals("MobilitaInfoStorDettPage") && canDelete){%>
						<a href="javascript:delDispo();"><img src="../../img/del.gif" alt="Elimina disponibilità"></A>
						<input type="hidden" name="save"   value="Aggiorna"/>
					<%}%>
				</div>
		 	<%}%>
		    <input type="hidden" name="FLGSCHEDA" value="<%=Utils.notNull(flagDispo)%>">
		</td>
	</tr>
  <%}
}%>

<tr>
    <td class="etichetta">Motivo riapertura<br/></td>
    <td class="campo" colspan="2">
    	<af:comboBox classNameBase="input" addBlank="true" name="MotRiapertura"
		    multiple="false" onChange="fieldChanged();" disabled="true"
            focusOn="false" moduleName="M_MotivoRiaperturaMob" title="Motivo riapertura"
            selectedValue="<%=Utils.notNull(motivoRiapertura)%>"/>
       </td>
</tr>


<%if (! "".equals(codDomanda)) { %>

<tr>
    <td class="etichetta">Cod. domanda<br/></td>
    <td class="campo" colspan="2">
    	<af:textBox classNameBase="input" name="codDomanda" value="<%=codDomanda%>"
               maxlength="16" title="Cod. domanda"
               readonly="true" />
		   
       </td>
</tr>

<%} %>

<tr>
	<td colspan="3">
		<hr width="90%"/>
	</td>
</tr>

<tr><td colspan="3">
  <table border="0">
    <tr>
        <td class="etichetta">Note<br/></td>
        <td class="campo" colspan="2">
          <af:textArea classNameBase="textarea" name="strNote" value="<%=Utils.notNull(strNote)%>"
                       cols="60" rows="4" maxlength="2000"
                       onKeyUp="fieldChanged();" readonly="<%=String.valueOf(!canModifyNote)%>"/>
           </td>
           
           <%if(canModifyNote){%>
           	  <td>&nbsp;&nbsp;</td> 
			  <td>
			   <input class="pulsante" type="button" name="buttonAggiornaNote" value="Aggiorna note" 
			          onClick="aggiornaNote(document.Frm1);">
			  </td>
		   <%}%>   
    </tr>
  </table>
  
</td></tr>
</table>
