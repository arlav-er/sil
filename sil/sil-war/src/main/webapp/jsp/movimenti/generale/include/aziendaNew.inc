<tr>
<td>
<!--Tabella con i dati dell'azienda-->
  <table border="0" width="96%" cellpadding="0" cellspacing="0">
  <tr>
  	<td class="etichetta" width="15%">C.F.</td>
  	<td class="campo" width="30%">
  		<af:textBox classNameBase="input" name="strCodiceFiscaleAz" title="Codice Fiscale Azienda" size="22" value="<%=strCodiceFiscaleAz%>" readonly="true" required="true"/>
  	</td>
  	<td class="etichetta" width="15%">Rag.&nbsp;Soc.</td>
  	<td class="campo" colspan="4" width="30%">
  		<af:textBox classNameBase="input" onKeyUp="fieldChanged();" name="strRagioneSocialeAzTrunc" title="Ragione Sociale" size="40" value="<%=strRagioneSocialeAzTrunc%>" readonly="true"/>
  			<af:textBox type="hidden" name="strRagioneSocialeAz" value="<%=strRagioneSocialeAz%>"/>
  	</td>
  </tr>
  
  <%
	String displayInterinaliEstera = "none";
	if ( (interinale) || (currentcontext.startsWith("valida") && !cfSommEstera.equals("")) ) { 
		displayInterinaliEstera = "";
	}
  %>
  <tr valign="top" id="azSommEstera" style="display:<%=displayInterinaliEstera%>">
  	<td class="etichetta" width="15%">C.F.&nbsp;Estera</td>
  	<td class="campo" width="30%">
  		<af:textBox classNameBase="input" name="CODFISCAZESTERA" title="Codice Fiscale Azienda Estera" size="30" value="<%=cfSommEstera%>" readonly="true" />
  	</td>
  	<td class="etichetta" width="15%">Rag.&nbsp;Soc.&nbsp;Estera</td>
  	<td class="campo" colspan="4" width="30%">
  		<af:textBox classNameBase="input" name="RAGSOCAZESTERA" title="Ragione Sociale Estera" size="40" value="<%=ragSocSommEstera%>" readonly="true"/>
  	</td>
  </tr>
   			
   <tr valign="top">
  	<td class="etichetta" width="15%">Indirizzo</td>
  	<td class="campo" colspan="4" width="30%">
  		<af:textBox classNameBase="input" onKeyUp="fieldChanged();" name="strIndirizzoUAzVisualizzato" title="Indirizzo" size="80" value='<%=strIndirizzoUAz + " (" + strComuneUAz + ", " + strCapUAz + ")"%>' readonly="true" required="true"/>
  		<input type="hidden" name="strIndirizzoUAz" value="<%=strIndirizzoUAz%>"/>
  		<input type="hidden" name="strComuneUAz" value="<%=strComuneUAz%>"/>
  		<input type="hidden" name="strCapUAz" value="<%=strCapUAz%>"/>
  	</td>
  </tr>
  <%
	String displayCampiAzienda = "none";
  %>

  <tr valign="top" id="datiAzienda_0" style="display:<%=displayCampiAzienda%>"> 
  	<td class="etichetta" width="15%">Natura az.</td>
  	<td class="campo" width="30%">
  		<af:textBox classNameBase="input" onKeyUp="fieldChanged();" name="natGiuridicaAz" title="Natura Giuridica" size="30" value="<%=natGiuridicaAz%>" readonly="true"/>
  		<input type="hidden" name="CODNATGIURIDICAAZ" value="<%=codNatGiuridicaAz%>"/>
  	</td>
  	<td class="etichetta" width="15%">Tipologia</td>
  	<td class="campo" width="30%">
  		<af:textBox classNameBase="input" onKeyUp="fieldChanged();" name="DESCRTIPOAZIENDA" title="Tipologia" size="25" value="<%=descrTipoAzienda%>" readonly="true"/>&nbsp;*        
  	</td>
  </tr>
  
  <tr valign="top" id="datiAzienda_1" style="display:<%=displayCampiAzienda%>"> 
  	<td class="etichetta" width="15%">Attività</td>
  	<td class="campo" width="30%">
  		<af:textBox classNameBase="input" onKeyUp="fieldChanged();" name="codAtecoStrDesAttivitaAz" title="Attività Azienda" size="50" value='<%=codAtecoUAz + " - " + strDesAtecoUAz%>' readonly="true"/>
  		<input type="hidden" name="codAtecoUAz" value="<%=codAtecoUAz%>"/>
  		<input type="hidden" name="strDesAtecoUAz" value="<%=strDesAtecoUAz%>"/>
  	</td>
  	<td class="etichetta" width="15%">CCNL</td>
  	<td class="campo" width="30%">
  		<af:textBox classNameBase="input" onKeyUp="fieldChanged();" name="codDescrCCNLAz" title="CCNL Azienda" size="40" value='<%=codCCNLAz + " - " + descrCCNLAz%>' readonly="true"/>
  		<input type="hidden" name="codCCNLAz" value="<%=codCCNLAz%>" />
  		<input type="hidden" name="descrCCNLAz" value="<%=descrCCNLAz%>"/>
  	</td>
  </tr>               
  <tr valign="top" id="datiAzienda_2" style="display:<%=displayCampiAzienda%>"> 
  	<td class="etichetta" width="15%">Telefono</td>
  	<td class="campo" width="30%">
  		<af:textBox classNameBase="input" onKeyUp="fieldChanged();" name="strTelUAz" title="Telefono" size="25" value="<%=strTelUAz%>" readonly="true"/>
  	</td>
  	<td class="etichetta" width="15%">Fax</td>
  	<td class="campo" width="30%">
   		<af:textBox classNameBase="input" onKeyUp="fieldChanged();" name="strFaxUAz" title="Fax" size="25" value='<%=strFaxUAz%>' readonly="true"/>
  	</td>
  </tr>               
  <tr valign="top" id="datiAzienda_3" style="display:<%=displayCampiAzienda%>"> 
  	<td class="etichetta" width="15%">Pos. INPS</td>
  	<td class="campo" width="30%">
  		<af:textBox classNameBase="input" onKeyUp="fieldChanged();unificaPosInps();" name="STRPOSINPS1" title="Posizione Inps1" size="2" maxlength="2" value="<%=posInps1%>" readonly="<%=String.valueOf(!canModify)%>"/> - 
  		<af:textBox classNameBase="input" onKeyUp="fieldChanged();unificaPosInps();" name="STRPOSINPS2" title="Posizione Inps2" size="15" maxlength="13" value="<%=posInps2%>" readonly="<%=String.valueOf(!canModify)%>"/>
  		<input type="hidden" name="STRPOSINPS" value="<%=posInps%>"/>
  	</td>
  	<td class="etichetta" width="15%">Pat INAIL</td>
  	<td class="campo" width="30%">
  		<af:textBox classNameBase="input" onKeyUp="fieldChanged();unificaPatInail();" name="STRPATINAIL1" title="Pat INAIL 1" size="10" maxlength="8" value="<%=patInail1%>" readonly="<%=String.valueOf(!canModify)%>"/> - 
  		<af:textBox classNameBase="input" onKeyUp="fieldChanged();unificaPatInail();" name="STRPATINAIL2" title="Pat INAIL 2" size="3" maxlength="2" value="<%=patInail2%>" readonly="<%=String.valueOf(!canModify)%>"/>
  		<input type="hidden" name="STRPATINAIL" value="<%=patInail%>"/>                    
  	</td>  
  </tr>
  <tr valign="top" id="datiAzienda_4" style="display:<%=displayCampiAzienda%>"> 
  	<td class="etichetta" width="15%">Rif. per pratica<br/>amm.</td>
  	<td class="campo" width="30%">
  		<af:textBox classNameBase="input" onKeyUp="fieldChanged();" name="STRREFERENTE" title="Riferimento per pratica amministrativa" value="<%=strReferente%>" readonly="<%=String.valueOf(!canModify)%>"/>
  	</td>
  	<td class="etichetta" width="15%">P.Iva</td>
  	<td class="campo" width="30%">
  		<af:textBox classNameBase="input" onKeyUp="fieldChanged();" name="strPartitaIvaAz" title="Partita Iva" size="13" value="<%=strPartitaIvaAz%>" readonly="true" />                    
  	</td>  
  </tr>

<%
	/*Decide se visualizzare il numero di iscrizione all'albo per aziende interinali*/
	String displayInterinali = "none";
	if (interinale){ displayInterinali = "";}
%>
  <tr valign="top" id="alboInt" style="display:<%=displayInterinali%>">
  	<td class="etichetta">Iscr. Albo Interinale</td>
  	<td colspan="4" class="campo">
  		<af:textBox classNameBase="input" onKeyUp="fieldChanged();" name="STRNUMALBOINTERINALI" title="Numero di iscrizione all'albo interinale" size="100" maxlength="100" value="<%=strNumAlboInterinali%>" readonly="true"/>
  	</td>
  </tr>
  <input type="hidden" name="STRNUMREGISTROCOMMITT" value="<%=strNumRegistroCommitt%>"/>
  <tr>
  	<td class="etichetta">Sede/Ente Promotore</td>	
	<td colspan="4" class="campo2" nowrap>
		<table style="border-collapse:collapse">
			<tr>
				<td class="campo2" nowrap>
				  <af:textBox classNameBase="input" onKeyUp="fieldChanged();" name="STRLUOGODILAVORO" title="Luogo di lavoro" size="70" maxlength="500" value="<%=strLuogoDiLavoro%>" readonly="<%=String.valueOf(!canModify)%>"/>
  				  <% if(!consulta) { %>
  					<a href="#" id="DettaglioAziendaUtil" style="display:''" onClick="javascript:apriAzUtil<%=(valida ? "Validazione" : "")%>();">          
                    <img src="../../img/binocolo.gif" alt="Cerca azienda nel DB"></a>
                 <%}%> 
                    <a href="#" id="DettaglioAziUtil" style="display:none" onClick="javascript:apriAzUtil<%=(valida ? "Validazione" : "")%>();">          
                    <img src="../../img/Azi-Utilizzatrice.gif" alt="Azienda Utilizzatrice"></a>
                <% if(!consulta) { %>
                    <a href="#" id="DeleteAziUtil" style="display:none" onClick="javascript:azzeraAziendaUtil(true);">           
                    <img src="../../img/del.gif" alt="Scollega azienda utilizzatrice"></a>
                <%}%>
  	 			<td>&nbsp;&nbsp;</td>
				<td nowrap="nowrap">
					<div id="CheckboxPersonaleInterno" style="display:<%=(interinale ? "" : "none")%>">Pers. interno
    				<input id="flagPersonaleInterno" type="checkbox" title="Flag Personale Interno" name="FLGINTERASSPROPRIACHECKBOX" 
    						<%=(interinale && assInterna) ? "CHECKED" : ""%> value="" <%=canModify ? "" : "disabled='true'"%> 
    						onclick="fieldChanged();valorizzaHid(this);controllaAziSomm();"/>
    				<input id="flagPersonaleInternoFIELD" type="hidden" name="FLGINTERASSPROPRIA" value="<%=flgAssPropria%>"/>&nbsp;
    				</div>
    			</td>
			  </tr>
	   </table>
	</td>
</tr>
</table>
</td>
</tr>