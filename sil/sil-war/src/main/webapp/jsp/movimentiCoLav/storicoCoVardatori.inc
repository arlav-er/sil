		<tr>
		  <td colspan="4">
		  <div id="dettvardatori"  style="display:none">  
		<% 	
		for(int k=0; k < coResult.getVARDATORI().length; k++){
			VARDATORI_Type vardatori = coResult.getVARDATORI(k);
				
		%>	 
		<tr>
		  <td colspan="4">    
		    <div class="sezione2" id="sezioneVard<%=k %>">
		    	<a name="sezioneVard<%=k %>" href="#sezioneVard<%=k %>" onClick="onOff('Vard_<%=k %>')"><img align="middle" id="imm1Vard_<%=k %>" alt="mostra/nascondi" src=" ../../img/chiuso.gif" border="0"></a>
		    		<b>&nbsp;<%=vardatori.getRowOrder() %>.&nbsp;codice comunicazione&nbsp;<%=vardatori.getInvio_CodiceComunicazione() %></b>
		    </div>
		  </td>
		</tr>
		
		<tr><td colspan="4">
		<div id="dettVard_<%=k %>" style="display:none">
			<table cellpadding="0" cellspacing="0" border="0" width="100%">
			 <tr>
			   <td class="campo"  rowspan="4"><b>Datore Lavoro</b></td>
			   <td>
			    <table align="left"  cellpadding="0" cellspacing="0" border="0" width="100%">
			      <tr>
			      	<td class="etichetta" >Codice fiscale</td>
			      	<td>
			          <af:textBox classNameBase="input" type="text" name="vardCf" value="<%=Utils.notNull(vardatori.getDatoreLavoro_CodiceFiscale()) %>"  
			                      readonly="<%=String.valueOf(readOnlyStr)%>" />
			        </td>
			      </tr>
			      <tr>
			      	<td class="etichetta" >Denominazione</td>
			      	<td>
			          <af:textBox classNameBase="input" type="text" name="vardDen" value="<%=Utils.notNull(vardatori.getDatoreLavoro_Denominazione())%>"  
			                      readonly="<%=String.valueOf(readOnlyStr)%>"  />
			        </td>
			      </tr>
			      <tr>
			      	<td class="etichetta" >Pubblica amministrazione</td>
			      	<td>
			          <af:textBox classNameBase="input" type="text" name="vardPa" value="<%=Utils.notNull(vardatori.getDatoreLavoro_PA())%>"  
			                      readonly="<%=String.valueOf(readOnlyStr)%>"  /></td>
			      </tr>
			      <tr>
			      	<td class="etichetta" >Settore</td>
			      	<td><%
		      			String descrAtecoVard = it.eng.sil.module.movimenti.consultaCO.Properties.getDescrizioneFromCodice(serviceResponse, "COMBO_MN_ATECO", Utils.notNull(vardatori.getDatoreLavoro_Settore()));
		      			StringBuffer sbAtecoVard = new StringBuffer(Utils.notNull(vardatori.getDatoreLavoro_Settore()));
						sbAtecoVard.append(" - ").append(descrAtecoVard);
		      		%>
			             <af:textArea name="codiceAtecoVardStr" classNameBase="input" readonly="<%=String.valueOf(readOnlyStr)%>" 
			              value="<%=Utils.notNull(sbAtecoVard.toString()) %>" cols="50" rows="5"/>
				      </td>
			      </tr>
			    </table>
			   </td>
			 </tr>
			</table>
			
			<table cellpadding="0" cellspacing="0" border="0" width="100%">
			 <tr>
			   <td class="campo"  rowspan="3"><b>Sede Lavoro</b></td>
			   <td>
			    <table align="left"  cellpadding="0" cellspacing="0" border="0" width="100%">
			    			      <tr>
			      	<td class="etichetta" >Indirizzo</td>
			      	<td>
			          <af:textBox classNameBase="input" type="text" name="vardInd" value="<%=Utils.notNull(vardatori.getDatoreLavoro_SedeLavoro_Indirizzo()) %>"  
			                      readonly="<%=String.valueOf(readOnlyStr)%>" />
			        </td>
			      </tr>
			   		<tr>
			      	<td class="etichetta" >CAP</td>
			      	<td>
			          <af:textBox classNameBase="input" type="text" name="vardCap" value="<%=Utils.notNull(vardatori.getDatoreLavoro_SedeLavoro_CAP())%>"  
			                      readonly="<%=String.valueOf(readOnlyStr)%>"  /></td>
			      </tr>
			      <tr>
			      	<td class="etichetta" >Comune/Stato estero</td>
			      	<td>
		      		<%
						String vardComStr = Utils.notNull(vardatori.getDatoreLavoro_SedeLavoro_ComuneNazione())+ " - ";
			      		vardComStr =vardComStr + it.eng.sil.module.movimenti.consultaCO.Properties.getDescrizioneFromCodice(serviceResponse, "COMBO_COMUNE",Utils.notNull(vardatori.getDatoreLavoro_SedeLavoro_ComuneNazione()));
					%>			      	
			           <af:textBox classNameBase="input" type="text" name="vardCom" value="<%=vardComStr%>" readonly="<%=String.valueOf(readOnlyStr)%>" />
				     </td>        
			      </tr>
			    </table>
			   </td>
			 </tr>
			</table>	

			<table cellpadding="0" cellspacing="0" border="0" width="100%">
			 <tr>
			   <td class="campo"  rowspan="3"><b>Datore Lavoro Precedente</b></td>
			   <td>
			    <table align="left"  cellpadding="0" cellspacing="0" border="0" width="100%">
			      <tr>
			      	<td class="etichetta" >Codice fiscale</td>
			      	<td>
			          <af:textBox classNameBase="input" type="text" name="vardCfPrec" value="<%=Utils.notNull(vardatori.getDatoreLavoroPrecedente_CodiceFiscale()) %>"  
			                      readonly="<%=String.valueOf(readOnlyStr)%>" />
			        </td>
			      </tr>
			      <tr>
			      	<td class="etichetta" >Denominazione</td>
			      	<td>
			          <af:textBox classNameBase="input" type="text" name="vardDenPrec" value="<%=Utils.notNull(vardatori.getDatoreLavoroPrecedente_Denominazione())%>"  
			                      readonly="<%=String.valueOf(readOnlyStr)%>"  />
			        </td>
			      </tr>
			      <tr>
			      	<td class="etichetta" >Settore</td>
			      		<td><%
		      			String descrAtecoVardPrec = it.eng.sil.module.movimenti.consultaCO.Properties.getDescrizioneFromCodice(serviceResponse, "COMBO_MN_ATECO", Utils.notNull(vardatori.getDatoreLavoroPrecedente_Settore()));
		      			StringBuffer sbAtecoVardPrec = new StringBuffer(Utils.notNull(vardatori.getDatoreLavoroPrecedente_Settore()));
						sbAtecoVardPrec.append(" - ").append(descrAtecoVardPrec);
		      		%>
			             <af:textArea name="codiceAtecoVardPrecStr" classNameBase="input" readonly="<%=String.valueOf(readOnlyStr)%>" 
			              value="<%=Utils.notNull(sbAtecoVardPrec.toString()) %>" cols="50" rows="5"/>
				      </td>
			      </tr>
			    </table>
			   </td>
			 </tr>
			</table>

			<table cellpadding="0" cellspacing="0" border="0" width="100%">
			 <tr>
			   <td class="campo"  rowspan="2"><b>Dati Trasferimento</b></td>
			   <td>
			    <table align="left"  cellpadding="0" cellspacing="0" border="0" width="100%">
      				<tr>
			      	<td class="etichetta" >Data inizio</td>
		      		<td>
		      		<%if(StringUtils.isFilledNoBlank(vardatori.getTrasferimentoAzienda_DataInizio() ) && !vardatori.getTrasferimentoAzienda_DataInizio().equalsIgnoreCase("0")) {
		      			String dataTrasformazioneVard = it.eng.sil.module.movimenti.consultaCO.Properties.trasformaData(vardatori.getTrasferimentoAzienda_DataInizio());	      		%>
			                 <af:textBox classNameBase="input" type="text" name="dataTrasformazioneVard" value="<%=dataTrasformazioneVard %>"  
			                      readonly="<%=String.valueOf(readOnlyStr)%>" />
			            <%}else{%>
			            	&nbsp;
			            <%}%>
				      </td>
			      </tr>
				 <tr>
			      	<td class="etichetta" >Tipo</td>
		      		<td>
		      	<%if(StringUtils.isFilledNoBlank(vardatori.getTrasferimentoAzienda_CodiceTrasferimento()) && !vardatori.getTrasferimentoAzienda_CodiceTrasferimento().equalsIgnoreCase("0")) {
						String tipoTrasfVardAzStr = vardatori.getTrasferimentoAzienda_CodiceTrasferimento() + " - ";
			      		tipoTrasfVardAzStr =tipoTrasfVardAzStr + it.eng.sil.module.movimenti.consultaCO.Properties.getDescrizioneFromCodice(serviceResponse, "M_COMBO_DE_TIPO_TRASF",vardatori.getTrasferimentoAzienda_CodiceTrasferimento());
					%>
			               <af:textBox classNameBase="input" type="text" name="tipoTrasfVardAz" value="<%=tipoTrasfVardAzStr%>" readonly="<%=String.valueOf(readOnlyStr)%>" />
				     <%}else{%>
			            &nbsp;
			          <%}%>
			          </td>
			      </tr>			    
			    </table>
			   </td>
			 </tr>
			</table>	
					
			<table cellpadding="0" cellspacing="0" border="0" width="100%">
			 <tr>
			 	<td class="campo"  rowspan="8"><b>Dati Rapporto</b></td>
			   <td>
			    <table align="left"  cellpadding="0" cellspacing="0" border="0" width="100%">
			      <tr>
			      	<td class="etichetta" >Data inizio</td>
			      	<td>
			      	<%String dataInizioRappVard = it.eng.sil.module.movimenti.consultaCO.Properties.trasformaData(vardatori.getRapporto_DataInizio()); %>
			          <af:textBox classNameBase="input" type="text" name="dataInizioRappVard" value="<%=dataInizioRappVard %>"  
			                      readonly="<%=String.valueOf(readOnlyStr)%>"  size="18" maxlength="10"/>
			        </td>
			      </tr>
			      <tr>
			      	<td class="etichetta" >Data fine</td>
			      	<td>
			      	<%if(StringUtils.isFilledNoBlank(vardatori.getRapporto_DataFine() ) && !vardatori.getRapporto_DataFine().equalsIgnoreCase("0")) {
		      			String dataFineRappVard = it.eng.sil.module.movimenti.consultaCO.Properties.trasformaData(vardatori.getRapporto_DataFine());	      		%>
			                <af:textBox classNameBase="input" type="text" name="dataFineRappVard" value="<%=dataFineRappVard %>"  
			                      readonly="<%=String.valueOf(readOnlyStr)%>"  size="18" maxlength="10"/>
			            <%}else{%>
			            	&nbsp;
			            <%}%>
			         </td>
			      </tr>
			     <tr>
			      	<td class="etichetta" >Tipologia Contrattuale</td>
			      	
			      	<td><%
		      			String descrContrVardatori = it.eng.sil.module.movimenti.consultaCO.Properties.getDescrizioneFromCodice(serviceResponse, "COMBO_MN_TIPO_CONTRATTO", Utils.notNull(vardatori.getRapporto_TipologiaContrattuale()));
		      			StringBuffer sbContrVardatori = new StringBuffer(Utils.notNull(vardatori.getRapporto_TipologiaContrattuale()));
						sbContrVardatori.append(" - ").append(descrContrVardatori);
		      		%>
			             <af:textArea name="codContrVardatoriStr" classNameBase="input" readonly="<%=String.valueOf(readOnlyStr)%>" 
			              value="<%=Utils.notNull(sbContrVardatori.toString()) %>" cols="50" rows="5"/>
				      </td>
			      </tr>
			      <tr>
			      	<td class="etichetta" >CCNL</td>
			      	<td><%
		      			String descrCCNLVard = it.eng.sil.module.movimenti.consultaCO.Properties.getDescrizioneFromCodice(serviceResponse, "M_COMBO_CCNL", Utils.notNull(vardatori.getRapporto_CCNL()));
		      			StringBuffer sbCCNLVard = new StringBuffer(Utils.notNull(vardatori.getRapporto_CCNL()));
						sbCCNLVard.append(" - ").append(descrCCNLVard);
		      		%>
			                <af:textArea name="ccnlVardStr" classNameBase="input" readonly="<%=String.valueOf(readOnlyStr)%>"  
			                value="<%=Utils.notNull(sbCCNLVard.toString()) %>" cols="50" rows="5"/>
				      </td>
			      	
			      </tr>
			      <tr>
			      	<td class="etichetta" >Ente previdenziale</td>
		      		<td>
		      		<%
						String enteVardStr = Utils.notNull(vardatori.getRapporto_EntePrevidenziale()) + " - ";
			      		enteVardStr =enteVardStr + it.eng.sil.module.movimenti.consultaCO.Properties.getDescrizioneFromCodice(serviceResponse, "M_COMBO_ENTE_PREVIDENZIALE", Utils.notNull(vardatori.getRapporto_EntePrevidenziale()));
					%>
			               <af:textBox classNameBase="input" type="text" name="enteVard" value="<%=enteVardStr %>"  readonly="<%=String.valueOf(readOnlyStr)%>" /> 
				      </td>
			      </tr>
				 <tr>
			      	<td class="etichetta" >Agevolazioni</td>
		      		<td>
		      		<%
						String agevolVardStr = Utils.notNull(vardatori.getRapporto_Agevolazioni()) + " - ";
			      		agevolVardStr =agevolVardStr + it.eng.sil.module.movimenti.consultaCO.Properties.getDescrizioneFromCodice(serviceResponse, "M_GetDatiAgevolazioneAnteprimaVacancy", Utils.notNull(vardatori.getRapporto_Agevolazioni()));
					%>
			               <af:textBox classNameBase="input" type="text" name="agevolVard" value="<%=agevolVardStr %>" readonly="<%=String.valueOf(readOnlyStr)%>" />
				      </td>
			      </tr>		
			      <tr>
			      	<td class="etichetta" >Socio lavoratore</td>
		      		<td>
			               <af:textBox classNameBase="input" type="text" name="socioLav" value="<%=Utils.notNull(vardatori.getRapporto_SocioLavoratore()) %>"  
			                      readonly="<%=String.valueOf(readOnlyStr)%>" />
				      </td>
			      </tr>	  
			      <tr>
			      	<td class="etichetta" >Lavoro in agricoltura</td>
		      		<td>		      		
		      		<%if(StringUtils.isFilledNoBlank(vardatori.getRapporto_LavoroAgricoltura()) && !vardatori.getRapporto_LavoroAgricoltura().equalsIgnoreCase("0")) {%>
			               <af:textBox classNameBase="input" type="text" name="lavAgrVard" value="<%=Utils.notNull(vardatori.getRapporto_LavoroAgricoltura()) %>"  
			                      readonly="<%=String.valueOf(readOnlyStr)%>" />
			           <%}else{%>
			           	&nbsp;
			           <%}%>
				      </td>
			      </tr>		     
				</table>
			   </td>
			 </tr>
			</table>
			
			<table cellpadding="0" cellspacing="0" border="0" width="100%">
			 <tr>
			 	<td class="campo"  rowspan="2"><b>Comunicazione Precedente</b></td>
			   <td>
			    <table align="left"  cellpadding="0" cellspacing="0" border="0" width="100%">			      
			      <tr>
			      	<td class="etichetta" >Codice</td>
		      		<td>
			               <af:textBox classNameBase="input" type="text" name="comPrecVard" value="<%=Utils.notNull(vardatori.getInvio_CodiceComunicazionePrecedente()) %>"  
			                      readonly="<%=String.valueOf(readOnlyStr)%>" />
				      </td>
			      </tr>
			    </table>
			   </td>
			 </tr>
			</table>
		</div>
		</td>
		</tr>
	 	
	<%	}	%>
	</div>
		</td>
		</tr>
