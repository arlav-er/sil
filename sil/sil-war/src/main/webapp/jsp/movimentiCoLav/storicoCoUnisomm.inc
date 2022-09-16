		<tr>
		  <td colspan="4">
		  <div id="dettunisomm"  style="display:none">  
		<% 	
		for(int j=0; j < coResult.getUNISOMM().length; j++){
			UNISOMM_Type somm = coResult.getUNISOMM(j);
				
		%>	 
		<tr>
		  <td colspan="4">    
		    <div class="sezione2" id="sezioneSomm<%=j %>">
		    	<a name="sezioneSomm<%=j %>" href="#sezioneSomm<%=j %>" onClick="onOff('Somm_<%=j %>')"><img align="middle" id="imm1Somm_<%=j %>" alt="mostra/nascondi" src=" ../../img/chiuso.gif" border="0"></a>
		    		<b>&nbsp;<%=somm.getRowOrder() %>.&nbsp;codice comunicazione&nbsp;<%=somm.getInvio_CodiceComunicazione() %></b>
		    </div>
		  </td>
		</tr>
		
		<tr><td colspan="4">
		<div id="dettSomm_<%=j %>" style="display:none">
			<table cellpadding="0" cellspacing="0" border="0" width="100%">
			 <tr>
			   <td class="campo"  rowspan="5"><b>Agenzia Amministrazione</b></td>
			   <td>
			    <table align="left"  cellpadding="0" cellspacing="0" border="0" width="100%">
			      <tr>
			      	<td class="etichetta" >Codice fiscale</td>
			      	<td>
			          <af:textBox classNameBase="input" type="text" name="agAmmCf" value="<%=Utils.notNull(somm.getAgenziaSomministrazione_CodiceFiscale()) %>"  
			                      readonly="<%=String.valueOf(readOnlyStr)%>" />
			        </td>
			      </tr>
			      <tr>
			      	<td class="etichetta" >Denominazione</td>
			      	<td>
			          <af:textBox classNameBase="input" type="text" name="agAmmDen" value="<%=Utils.notNull(somm.getAgenziaSomministrazione_Denominazione())%>"  
			                      readonly="<%=String.valueOf(readOnlyStr)%>"  />
			        </td>
			      </tr>
			      <tr>
			      	<td class="etichetta" >Indirizzo sede lavoro</td>
			      	<td>
			          <af:textBox classNameBase="input" type="text" name="agAmmInd" value="<%=Utils.notNull(somm.getAgenziaSomministrazione_SedeLavoro_Indirizzo()) %>"  
			                      readonly="<%=String.valueOf(readOnlyStr)%>" />
			        </td>
			      </tr>
			   		<tr>
			      	<td class="etichetta" >CAP sede lavoro</td>
			      	<td>
			          <af:textBox classNameBase="input" type="text" name="agAmmCap" value="<%=Utils.notNull(somm.getAgenziaSomministrazione_SedeLavoro_CAP())%>"  
			                      readonly="<%=String.valueOf(readOnlyStr)%>"  /></td>
			      </tr>
			      <tr>
			      	<td class="etichetta" >Comune/Stato estero sede lavoro</td>
			      	<td>
		      		<%
						String agAmmcodComStr = Utils.notNull(somm.getAgenziaSomministrazione_SedeLavoro_ComuneNazione()) + " - ";
			      		agAmmcodComStr =agAmmcodComStr + it.eng.sil.module.movimenti.consultaCO.Properties.getDescrizioneFromCodice(serviceResponse, "COMBO_COMUNE",Utils.notNull(somm.getAgenziaSomministrazione_SedeLavoro_ComuneNazione()));
					%>
			           <af:textBox classNameBase="input" type="text" name="agAmmcodCom" value="<%=agAmmcodComStr %>" readonly="<%=String.valueOf(readOnlyStr)%>" />
				     </td>        
			      </tr>
			      <tr>
			      	<td class="etichetta" >Settore</td>
			      	  	<td><%
		      			String descrAtecoSomm = it.eng.sil.module.movimenti.consultaCO.Properties.getDescrizioneFromCodice(serviceResponse, "COMBO_MN_ATECO", Utils.notNull(somm.getDittaUtilizzatrice_Settore()));
		      			StringBuffer sbAtecoSomm = new StringBuffer(Utils.notNull(somm.getDittaUtilizzatrice_Settore()));
						sbAtecoSomm.append(" - ").append(descrAtecoSomm);
		      		%>
			             <af:textArea name="codiceAtecoSommStr" classNameBase="input" readonly="<%=String.valueOf(readOnlyStr)%>" 
			              value="<%=Utils.notNull(sbAtecoSomm.toString()) %>" cols="50" rows="5"/>
				      </td>
			      </tr>
			    </table>
			   </td>
			 </tr>
			</table>
			
			<table cellpadding="0" cellspacing="0" border="0" width="100%">
			 <tr>
			   <td class="campo"  rowspan="7"><b>Ditta Utilizzatrice</b></td>
			   <td>
			    <table align="left"  cellpadding="0" cellspacing="0" border="0" width="100%">
			      <tr>
			      	<td class="etichetta" >Codice fiscale</td>
			      	<td>
			          <af:textBox classNameBase="input" type="text" name="dittaCf" value="<%=Utils.notNull(somm.getDittaUtilizzatrice_CodiceFiscale()) %>"  
			                      readonly="<%=String.valueOf(readOnlyStr)%>" />
			        </td>
			      </tr>
			      <tr>
			      	<td class="etichetta" >Denominazione</td>
			      	<td>
			          <af:textBox classNameBase="input" type="text" name="dittaDen" value="<%=Utils.notNull(somm.getDittaUtilizzatrice_Denominazione())%>"  
			                      readonly="<%=String.valueOf(readOnlyStr)%>"  />
			        </td>
			      </tr>
			      <tr>
			      	<td class="etichetta" >Indirizzo sede lavoro</td>
			      	<td>
			          <af:textBox classNameBase="input" type="text" name="dittaInd" value="<%=Utils.notNull(somm.getDittaUtilizzatrice_SedeLavoro_Indirizzo()) %>"  
			                      readonly="<%=String.valueOf(readOnlyStr)%>" />
			        </td>
			      </tr>
			   	<tr>
			      	<td class="etichetta" >CAP sede lavoro</td>
			      	<td>
			          <af:textBox classNameBase="input" type="text" name="dittaCap" value="<%=Utils.notNull(somm.getDittaUtilizzatrice_SedeLavoro_CAP())%>"  
			                      readonly="<%=String.valueOf(readOnlyStr)%>"  /></td>
			      </tr>
			      <tr>
			      	<td class="etichetta" >Comune/Stato estero sede lavoro</td>
			      	<td>
			      	<%
						String dittacodComStr = Utils.notNull(somm.getDittaUtilizzatrice_SedeLavoro_ComuneNazione())+ " - ";
			      		dittacodComStr =dittacodComStr + it.eng.sil.module.movimenti.consultaCO.Properties.getDescrizioneFromCodice(serviceResponse, "COMBO_COMUNE",Utils.notNull(somm.getDittaUtilizzatrice_SedeLavoro_ComuneNazione()));
					%>
				     </td>        
			      </tr>
				 <tr>
			      	<td class="etichetta" >Pubblica amministrazione</td>
			      	<td>
			          <af:textBox classNameBase="input" type="text" name="pa" value="<%=Utils.notNull(somm.getDittaUtilizzatrice_PA())%>"  
			                      readonly="<%=String.valueOf(readOnlyStr)%>"  /></td>
			      </tr>
			      <tr>
			      	<td class="etichetta" >Settore</td>
			      	<td><%
		      			String dittaAtecoDescr = it.eng.sil.module.movimenti.consultaCO.Properties.getDescrizioneFromCodice(serviceResponse, "COMBO_MN_ATECO", Utils.notNull(somm.getDittaUtilizzatrice_Settore()));
		      			StringBuffer sbAtecoDitta= new StringBuffer(Utils.notNull(somm.getDittaUtilizzatrice_Settore()));
						sbAtecoDitta.append(" - ").append(dittaAtecoDescr);
		      		%>
			             <af:textArea name="dittaAtecoStr" classNameBase="input" readonly="<%=String.valueOf(readOnlyStr)%>" 
			              value="<%=Utils.notNull(sbAtecoDitta.toString()) %>" cols="50" rows="5"/>
				      </td>
			      </tr>
			    </table>
			   </td>
			 </tr>
			</table>
			
			<table cellpadding="0" cellspacing="0" border="0" width="100%">
			 <tr>
			 	<td class="campo"  rowspan="9"><b>Dati Rapporto Ag. Somm.</b></td>
			   <td>
			    <table align="left"  cellpadding="0" cellspacing="0" border="0" width="100%">
			      <tr>
			      	<td class="etichetta" >Data inizio</td>
			      	<td>
			      	<%String dataInizioS = it.eng.sil.module.movimenti.consultaCO.Properties.trasformaData(somm.getRapportoAgenziaSomm_DataInizio()); %>
			          <af:textBox classNameBase="input" type="text" name="datInRappS" value="<%=dataInizioS %>"  
			                      readonly="<%=String.valueOf(readOnlyStr)%>"  size="18" maxlength="10"/>
			        </td>
			      </tr>
			      <tr>
			      	<td class="etichetta" >Data fine</td>
			      	<td><%String dataFineS = it.eng.sil.module.movimenti.consultaCO.Properties.trasformaData(somm.getRapportoAgenziaSomm_DataFine()); %>
			          <af:textBox classNameBase="input" type="text" name="datFinRappS" value="<%=dataFineS %>"  
			                      readonly="<%=String.valueOf(readOnlyStr)%>"  size="18" maxlength="10"/>
			         </td>
			      </tr>
			     <tr>
			      	<td class="etichetta" >Tipologia Contrattuale</td>
			        	<td><%
		      			String descrContrSS = it.eng.sil.module.movimenti.consultaCO.Properties.getDescrizioneFromCodice(serviceResponse, "COMBO_MN_TIPO_CONTRATTO", Utils.notNull(somm.getRapportoAgenziaSomm_TipologiaContrattuale()));
		      			StringBuffer sbContrSS= new StringBuffer(Utils.notNull(somm.getRapportoAgenziaSomm_TipologiaContrattuale()));
						sbContrSS.append(" - ").append(descrContrSS);
		      		%>
			             <af:textArea name="codContrSStr" classNameBase="input" readonly="<%=String.valueOf(readOnlyStr)%>" 
			              value="<%=Utils.notNull(sbContrSS.toString()) %>" cols="50" rows="5"/>
				      </td>
			      
			      </tr>
			      <tr>
			      	<td class="etichetta" >Codice ente previdenziale</td>
		      		<td>
			               <af:textBox classNameBase="input" type="text" name="codEntePrev" value="<%=Utils.notNull(somm.getRapportoAgenziaSomm_CodiceEntePrevidenziale()) %>" 
			               readonly="<%=String.valueOf(readOnlyStr)%>" />  
				      </td>
			      </tr>
			      <tr>
			      	<td class="etichetta" >Matricola</td>
		      		<td>
			               <af:textBox classNameBase="input" type="text" name="matricola" value="<%=Utils.notNull(somm.getRapportoAgenziaSomm_NMatricola()) %>"  
						        readonly="<%=String.valueOf(readOnlyStr)%>" />
				      </td>
			      </tr>
			      <tr>
			      	<td class="etichetta" >Num. agenzia somministrazione</td>
		      		<td>
			               <af:textBox classNameBase="input" type="text" name="numAg" value="<%=Utils.notNull(somm.getRapportoAgenziaSomm_NAgenziaSomm()) %>"  
						        readonly="<%=String.valueOf(readOnlyStr)%>" />
				      </td>
			      </tr>			     
			      <tr>
			      	<td class="etichetta" >Indennit&agrave; disponibilit&agrave;</td>
		      		<td>
			               <af:textBox classNameBase="input" type="text" name="indDisp" value="<%=Utils.notNull(somm.getRapportoAgenziaSomm_IndennitaDisponibilita()) %>"  
						        readonly="<%=String.valueOf(readOnlyStr)%>" />
				      </td>
			      </tr>	
  				 <tr>
			      	<td class="etichetta" >Data cessazione</td>
		      		<td>
		      		<%if(StringUtils.isFilledNoBlank(somm.getRapportoAgenziaSomm_CessazioneData()) && !somm.getRapportoAgenziaSomm_CessazioneData().equalsIgnoreCase("0")) {
		      			String dataCessSomm = it.eng.sil.module.movimenti.consultaCO.Properties.trasformaData(somm.getRapportoAgenziaSomm_CessazioneData());	      		%>
			                 <af:textBox classNameBase="input" type="text" name="dataCessSomm" value="<%=dataCessSomm %>"  
			                      readonly="<%=String.valueOf(readOnlyStr)%>" />
			            <%}else{%>
			            	&nbsp;
			            <%}%>
				      </td>
			      </tr>
  				 <tr>
			      	<td class="etichetta" >Data fine proroga</td>
		      		<td>
		      		<%if(StringUtils.isFilledNoBlank(somm.getRapportoAgenziaSomm_Proroga_DataFine()) && !somm.getRapportoAgenziaSomm_Proroga_DataFine().equalsIgnoreCase("0")) {
		      			String dataFineProSomm = it.eng.sil.module.movimenti.consultaCO.Properties.trasformaData(somm.getRapportoAgenziaSomm_Proroga_DataFine());	      		%>
			                 <af:textBox classNameBase="input" type="text" name="dataFineProSomm" value="<%=dataFineProSomm %>"  
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
			 	<td class="campo"  rowspan="9"><b>Dati Rapporto Utilizzatrice</b></td>
			   <td>
			    <table align="left"  cellpadding="0" cellspacing="0" border="0" width="100%">
			      <tr>
			      	<td class="etichetta" >Data inizio missione</td>
			      	<td>
			     <%if(StringUtils.isFilledNoBlank(somm.getRapportoDittaUtilizzatrice_DataInizioMissione()) && !somm.getRapportoDittaUtilizzatrice_DataInizioMissione().equalsIgnoreCase("0")) {
		      			String dataInMiss = it.eng.sil.module.movimenti.consultaCO.Properties.trasformaData(somm.getRapportoDittaUtilizzatrice_DataInizioMissione());	      		%>
			                 <af:textBox classNameBase="input" type="text" name="dataInMiss" value="<%=dataInMiss %>"  
			                      readonly="<%=String.valueOf(readOnlyStr)%>" />
			            <%}else{%>
			            	&nbsp;
			            <%}%>
			        </td>
			      </tr>
			      <tr>
			      	<td class="etichetta" >Data fine missione</td>
			      	<td> 
			      	<%if(StringUtils.isFilledNoBlank(somm.getRapportoDittaUtilizzatrice_DataFineMissione()) && !somm.getRapportoDittaUtilizzatrice_DataFineMissione().equalsIgnoreCase("0")) {
		      			String dataFineMiss = it.eng.sil.module.movimenti.consultaCO.Properties.trasformaData(somm.getRapportoDittaUtilizzatrice_DataFineMissione());	      		%>
			                 <af:textBox classNameBase="input" type="text" name="dataFineMiss" value="<%=dataFineMiss %>"  
			                      readonly="<%=String.valueOf(readOnlyStr)%>" />
			            <%}else{%>
			            	&nbsp;
			            <%}%>
			         </td>
			      </tr>
			      <tr>
			      	<td class="etichetta" >CCNL</td>
			      	<td><%
		      			String descrCCNLSomm = it.eng.sil.module.movimenti.consultaCO.Properties.getDescrizioneFromCodice(serviceResponse, "M_COMBO_CCNL", Utils.notNull(somm.getRapportoDittaUtilizzatrice_CCNL()));
		      			StringBuffer sbCCNLSomm= new StringBuffer(Utils.notNull(somm.getRapportoDittaUtilizzatrice_CCNL()));
						sbCCNLSomm.append(" - ").append(descrCCNLSomm);
		      		%>
			                <af:textArea name="ccnlSommStr" classNameBase="input" readonly="<%=String.valueOf(readOnlyStr)%>"  
			                value="<%=Utils.notNull(sbCCNLSomm.toString()) %>" cols="50" rows="5"/>
				      </td>
			      </tr>
 				<tr>
			      	<td class="etichetta" >Tipo orario</td>
		      		<td>
		      		<%
						String orarioSommStr = Utils.notNull(somm.getRapportoDittaUtilizzatrice_TipoOrario())+ " - ";
			      		orarioSommStr =orarioSommStr + it.eng.sil.module.movimenti.consultaCO.Properties.getDescrizioneFromCodice(serviceResponse, "M_GetDatiOrarioAnteprimaVacancy",Utils.notNull(somm.getRapportoDittaUtilizzatrice_TipoOrario()));
					%>
			               <af:textBox classNameBase="input" type="text" name="orarioSomm" value="<%=orarioSommStr %>" readonly="<%=String.valueOf(readOnlyStr)%>" />
				      </td>
			      </tr>
			      <tr>
			      	<td class="etichetta" >Ore settimanali</td>
		      		<td><%if(StringUtils.isFilledNoBlank(somm.getRapportoDittaUtilizzatrice_TipoOrario_OreSett()) && !somm.getRapportoDittaUtilizzatrice_TipoOrario_OreSett().equalsIgnoreCase("0")) {%>
			               <af:textBox classNameBase="input" type="text" name="oreSettSomm" value="<%=Utils.notNull(somm.getRapportoDittaUtilizzatrice_TipoOrario_OreSett()) %>"  
			                      readonly="<%=String.valueOf(readOnlyStr)%>" />
			            <%}else{%>
			            	&nbsp;
			            <%}%>
				      </td>
			      </tr>			      		
			      <tr>
			      	<td class="etichetta" >Numero contratto somm.</td>
		      		<td>
			               <af:textBox classNameBase="input" type="text" name="numContrSomm" value="<%=Utils.notNull(somm.getRapportoAgenziaDittaUtilizzatrice_NContrattoSomm()) %>"
			                readonly="<%=String.valueOf(readOnlyStr)%>" />  
				      </td>
			      </tr>		
			      <tr>
			      	<td class="etichetta" >Lavoro in agricoltura</td>
		      		<td>		      		
		      		<%if(StringUtils.isFilledNoBlank(somm.getRapportoDittaUtilizzatrice_LavoroInAgricoltura()) && !somm.getRapportoDittaUtilizzatrice_LavoroInAgricoltura().equalsIgnoreCase("0")) {%>
			               <af:textBox classNameBase="input" type="text" name="lavAgrSomm" value="<%=Utils.notNull(somm.getRapportoDittaUtilizzatrice_LavoroInAgricoltura()) %>"  
			                      readonly="<%=String.valueOf(readOnlyStr)%>" />
			           <%}else{%>
			           	&nbsp;
			           <%}%>
				      </td>
			      </tr>		     
				 <tr>
			      	<td class="etichetta" >Agevolazioni</td>
		      		<td>
		      		<%
						String agevolSommStr = Utils.notNull(somm.getRapportoDittaUtilizzatrice_CodiceAgevolazioni())+ " - ";
			      		agevolSommStr =agevolSommStr + it.eng.sil.module.movimenti.consultaCO.Properties.getDescrizioneFromCodice(serviceResponse, "M_GetDatiAgevolazioneAnteprimaVacancy",Utils.notNull(somm.getRapportoDittaUtilizzatrice_CodiceAgevolazioni()));
					%>
			               <af:textBox classNameBase="input" type="text" name="agevolSomm" value="<%=agevolSommStr %>" readonly="<%=String.valueOf(readOnlyStr)%>" />;
				      </td>
			      </tr>		    	      
			      <tr>
			      	<td class="etichetta" >Data cessazione</td>
		      		<td>
		      		<%if(StringUtils.isFilledNoBlank(somm.getRapportoDittaUtilizzatrice_DataCessazione()) && !somm.getRapportoDittaUtilizzatrice_DataCessazione().equalsIgnoreCase("0")) {
		      			String dataCessazioneDittaSomm = it.eng.sil.module.movimenti.consultaCO.Properties.trasformaData(somm.getRapportoDittaUtilizzatrice_DataCessazione());
		      		%>
			                 <af:textBox classNameBase="input" type="text" name="dataCessazioneDittaSomm" value="<%=dataCessazioneDittaSomm %>"  
			                      readonly="<%=String.valueOf(readOnlyStr)%>" />
			            <%}else{%>
			            	&nbsp;
			            <%}%>
				      </td>
			      </tr>
			      <tr>
			      	<td class="etichetta" >Data fine proroga</td>
		      		<td>
		      		<%if(StringUtils.isFilledNoBlank(somm.getRapportoDittaUtilizzatrice_DataFineProroga()) && !somm.getRapportoDittaUtilizzatrice_DataFineProroga().equalsIgnoreCase("0")) {
		      			String dataFineProrogaDittaSomm = it.eng.sil.module.movimenti.consultaCO.Properties.trasformaData(somm.getRapportoDittaUtilizzatrice_DataFineProroga());		      		%>
			                   <af:textBox classNameBase="input" type="text" name="dataFineProrogaDittaSomm" value="<%=dataFineProrogaDittaSomm %>"  
			                      readonly="<%=String.valueOf(readOnlyStr)%>" />
			            <%}else{%>
			            	&nbsp;
			            <%}%>
				      </td>
			      </tr>
			      <tr>
			      	<td class="etichetta" >Data inizio rapp. trasformato</td>
		      		<td>
		      		<%if(StringUtils.isFilledNoBlank(somm.getRapportoDittaUtilizzatrice_DataTrasformazione()) && !somm.getRapportoDittaUtilizzatrice_DataTrasformazione().equalsIgnoreCase("0")) {
		      			String dataTrasformazioneDittaSomm = it.eng.sil.module.movimenti.consultaCO.Properties.trasformaData(somm.getRapportoDittaUtilizzatrice_DataTrasformazione());	      		%>
			                 <af:textBox classNameBase="input" type="text" name="dataTrasformazioneDittaSomm" value="<%=dataTrasformazioneDittaSomm %>"  
			                      readonly="<%=String.valueOf(readOnlyStr)%>" />
			            <%}else{%>
			            	&nbsp;
			            <%}%>
				      </td>
			      </tr>
				 <tr>
			      	<td class="etichetta" >Tipo trasformazione</td>
		      		<td>
		      	<%if(StringUtils.isFilledNoBlank(somm.getRapportoDittaUtilizzatrice_CodiceTrasformazione()) && !somm.getRapportoDittaUtilizzatrice_CodiceTrasformazione().equalsIgnoreCase("0")) {
						String tipoTrasfDittaSommStr = somm.getRapportoDittaUtilizzatrice_CodiceTrasformazione()+ " - ";
			      		tipoTrasfDittaSommStr =tipoTrasfDittaSommStr + it.eng.sil.module.movimenti.consultaCO.Properties.getDescrizioneFromCodice(serviceResponse, "M_COMBO_DE_TIPO_TRASF", somm.getRapportoDittaUtilizzatrice_CodiceTrasformazione());
					%>
			               <af:textBox classNameBase="input" type="text" name="tipoTrasfDittaSomm" value="<%=tipoTrasfDittaSommStr %>" readonly="<%=String.valueOf(readOnlyStr)%>" />
			            
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
			               <af:textBox classNameBase="input" type="text" name="comPrecSomm" value="<%=Utils.notNull(somm.getInvio_CodiceComunicazionePrecedente()) %>"  
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
