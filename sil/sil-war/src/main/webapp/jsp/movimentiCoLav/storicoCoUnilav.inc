		<tr>
		  <td colspan="4">
		  <div id="dettunilav">  
		<% 	
		for(int i=0; i < coResult.getUNILAV().length; i++){
			UNILAV_Type lav = coResult.getUNILAV(i);
				
		%>	 
		<tr>
		  <td colspan="4">    
		    <div class="sezione2" id="sezioneUL<%=i %>">
		    	<a name="sezioneUL<%=i %>" href="#sezioneUL<%=i %>" onClick="onOff('ul_<%=i %>')"><img align="middle" id="imm1ul_<%=i %>" alt="mostra/nascondi" src=" ../../img/chiuso.gif" border="0"></a>
		    		<b>&nbsp;<%=lav.getRowOrder() %>.&nbsp;codice comunicazione&nbsp;<%=lav.getInvio_CodiceComunicazione() %></b>
		    </div>
		  </td>
		</tr>
		
		<tr><td colspan="4">
		<div id="dettul_<%=i %>" style="display:none">
			<table cellpadding="0" cellspacing="0" border="0" width="100%">
			 <tr>
			   <td class="campo"  rowspan="5"><b>Datore Lavoro</b></td>
			   <td>
			    <table align="left"  cellpadding="0" cellspacing="0" border="0" width="100%">
			      <tr>
			      	<td class="etichetta" >Codice fiscale</td>
			      	<td>
			          <af:textBox classNameBase="input" type="text" name="cfDatLav" value="<%=Utils.notNull(lav.getDatoreLavoro_CodiceFiscale()) %>"  
			                      readonly="<%=String.valueOf(readOnlyStr)%>" />
			        </td>
			      </tr>
			      <tr>
			      	<td class="etichetta" >Denominazione</td>
			      	<td>
			          <af:textBox classNameBase="input" type="text" name="denDatLav" value="<%=Utils.notNull(lav.getDatoreLavoro_Denominazione())%>"  
			                      readonly="<%=String.valueOf(readOnlyStr)%>"  />
			        </td>
			      </tr>
			      <tr>
			      	<td class="etichetta" >Pubblica Amministrazione</td>
			      	<td>
			          <af:textBox classNameBase="input" type="text" name="paLav" value="<%=Utils.notNull(lav.getDatoreLavoro_PA()) %>"  
			                      readonly="<%=String.valueOf(readOnlyStr)%>" />
			        </td>
			      </tr>
			      <tr>
			      	<td class="etichetta" >Settore</td>
			      	<td><%
		      			String descrAteco = it.eng.sil.module.movimenti.consultaCO.Properties.getDescrizioneFromCodice(serviceResponse, "COMBO_MN_ATECO", Utils.notNull(lav.getDatoreLavoro_Settore()));
		      			StringBuffer sbAteco = new StringBuffer(Utils.notNull(lav.getDatoreLavoro_Settore()));
		      			sbAteco.append(" - ").append(descrAteco);
		      		%>
			             <af:textArea name="codiceAtecoStr" classNameBase="input" readonly="<%=String.valueOf(readOnlyStr)%>" 
			              value="<%=Utils.notNull(sbAteco.toString()) %>" cols="50" rows="5"/>
				      </td>
			      	
			      </tr>
			      <tr>
			      	<td class="etichetta" >Codice fiscale datore distaccatario</td>
			      	<td>
			          <af:textBox classNameBase="input" type="text" name="cfDatLavDist" value="<%=Utils.notNull(lav.getDatoreLavoroDistaccatario_CodiceFiscale()) %>"  
			                      readonly="<%=String.valueOf(readOnlyStr)%>" />
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
			          <af:textBox classNameBase="input" type="text" name="indSedLeg" value="<%=Utils.notNull(lav.getDatoreLavoro_SedeLavoro_Indirizzo())%>"  
			                      readonly="<%=String.valueOf(readOnlyStr)%>"   />
			        </td>
			      </tr>
			      <tr>
			      	<td class="etichetta" >CAP</td>
			      	<td>
			          <af:textBox classNameBase="input" type="text" name="capSedLeg" value="<%=Utils.notNull(lav.getDatoreLavoro_SedeLavoro_CAP())%>"  
			                      readonly="<%=String.valueOf(readOnlyStr)%>"  /></td>
			      </tr>
			     <tr>
			      	<td class="etichetta" >Comune/Stato estero</td>
			      	<td>
			      	<% 
			      		String codComStr = Utils.notNull(lav.getDatoreLavoro_SedeLavoro_ComuneNazione()) + " - ";
			      		codComStr =codComStr + it.eng.sil.module.movimenti.consultaCO.Properties.getDescrizioneFromCodice(serviceResponse, "COMBO_COMUNE", Utils.notNull(lav.getDatoreLavoro_SedeLavoro_ComuneNazione()));
			      	%>
			           <af:textBox classNameBase="input" type="text" name="codCom" value="<%=codComStr%>"  readonly="<%=String.valueOf(readOnlyStr)%>" />
				     </td>        
			      </tr>
			    </table>
			   </td>
			 </tr>
			</table>
			
			<table cellpadding="0" cellspacing="0" border="0" width="100%">
			 <tr>
			 	<td class="campo"  rowspan="11"><b>Dati Rapporto</b></td>
			   <td>
			    <table align="left"  cellpadding="0" cellspacing="0" border="0" width="100%">
			      <tr>
			      	<td class="etichetta" >Data inizio</td>
			      	<td>
			      	<%String dataInizio = it.eng.sil.module.movimenti.consultaCO.Properties.trasformaData(lav.getRapporto_DataInizio()); %>
			          <af:textBox classNameBase="input" type="text" name="datInRapp" value="<%=dataInizio %>"  
			                      readonly="<%=String.valueOf(readOnlyStr)%>"  size="18" maxlength="10"/>
			        </td>
			      </tr>
			      <tr>
			      	<td class="etichetta" >Data fine</td>
			      	<td><%String dataFine = it.eng.sil.module.movimenti.consultaCO.Properties.trasformaData(lav.getRapporto_DataFine()); %>
			          <af:textBox classNameBase="input" type="text" name="datFinRapp" value="<%=dataFine %>"  
			                      readonly="<%=String.valueOf(readOnlyStr)%>"  size="18" maxlength="10"/>
			         </td>
			      </tr>
			     <tr>
			      	<td class="etichetta" >Tipologia Contrattuale</td>
			      	<td><%
		      			String descrContr = it.eng.sil.module.movimenti.consultaCO.Properties.getDescrizioneFromCodice(serviceResponse, "COMBO_MN_TIPO_CONTRATTO", Utils.notNull(lav.getRapporto_TipologiaContrattuale()));
		      			StringBuffer sbContr = new StringBuffer(Utils.notNull(lav.getRapporto_TipologiaContrattuale()));
		      			sbContr.append(" - ").append(descrContr);
		      		%>
			             <af:textArea name="codContrStr" classNameBase="input" readonly="<%=String.valueOf(readOnlyStr)%>" 
			              value="<%=Utils.notNull(sbContr.toString()) %>" cols="50" rows="5"/>
				      </td>
			      </tr>
		<tr>
			      	<td class="etichetta" >CCNL</td>
		      		<td><%
		      			String descrCCNL = it.eng.sil.module.movimenti.consultaCO.Properties.getDescrizioneFromCodice(serviceResponse, "M_COMBO_CCNL", Utils.notNull(lav.getRapporto_CCNL()));
		      			StringBuffer sbCCNL = new StringBuffer(Utils.notNull(lav.getRapporto_CCNL()));
		      			sbCCNL.append(" - ").append(descrCCNL);
		      		%>
			                <af:textArea name="ccnlStr" classNameBase="input" readonly="<%=String.valueOf(readOnlyStr)%>"  
			                value="<%=Utils.notNull(sbCCNL.toString()) %>" cols="50" rows="5"/>
				      </td>
			      </tr>
			      <tr>
			      	<td class="etichetta" >Ente previdenziale</td>
		      		<td>
		      		<%
						String enteStr = Utils.notNull(lav.getRapporto_EntePrevidenziale()) + " - ";
			      		enteStr =enteStr + it.eng.sil.module.movimenti.consultaCO.Properties.getDescrizioneFromCodice(serviceResponse, "M_COMBO_ENTE_PREVIDENZIALE", Utils.notNull(lav.getRapporto_EntePrevidenziale()));
					%>
			               <af:textBox classNameBase="input" type="text" name="ente" value="<%=enteStr %>" readonly="<%=String.valueOf(readOnlyStr)%>" /> 
				      </td>
			      </tr>
			      <tr>
			      	<td class="etichetta" >Tipo orario</td>
		      		<td>
		      		<%
						String orarioStr = Utils.notNull(lav.getRapporto_TipoOrario()) + " - ";
			      		orarioStr =orarioStr + it.eng.sil.module.movimenti.consultaCO.Properties.getDescrizioneFromCodice(serviceResponse, "M_GetDatiOrarioAnteprimaVacancy", Utils.notNull(lav.getRapporto_TipoOrario()));
					%>		      		
			               <af:textBox classNameBase="input" type="text" name="orario" value="<%=orarioStr %>"  readonly="<%=String.valueOf(readOnlyStr)%>" />
				      </td>
			      </tr>
			      <tr>
			      	<td class="etichetta" >Ore settimanali</td>
		      		<td><%if(StringUtils.isFilledNoBlank(lav.getRapporto_OreSettimanaliMedie()) && !lav.getRapporto_OreSettimanaliMedie().equalsIgnoreCase("0")) {%>
			               <af:textBox classNameBase="input" type="text" name="oreSett" value="<%=Utils.notNull(lav.getRapporto_OreSettimanaliMedie()) %>"  
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
						String agevolStr = Utils.notNull(lav.getRapporto_Agevolazioni()) + " - ";
			      		agevolStr =agevolStr + it.eng.sil.module.movimenti.consultaCO.Properties.getDescrizioneFromCodice(serviceResponse, "M_GetDatiAgevolazioneAnteprimaVacancy", Utils.notNull(lav.getRapporto_Agevolazioni()));
					%>
			               <af:textBox classNameBase="input" type="text" name="agevol" value="<%=agevolStr%>"  readonly="<%=String.valueOf(readOnlyStr)%>" />
				      </td>
			      </tr>
			      <tr>
			      	<td class="etichetta" >Assunzione per forza maggiore</td>
		      		<td>
			               <af:textBox classNameBase="input" type="text" name="forzaMagg" value="<%=Utils.notNull(lav.getInvio_ForzaMaggiore()) %>"  
			                      readonly="<%=String.valueOf(readOnlyStr)%>" />
				      </td>
			      </tr>
			      <tr>
			      	<td class="etichetta" >Socio lavoratore</td>
		      		<td>
			               <af:textBox classNameBase="input" type="text" name="socioLav" value="<%=Utils.notNull(lav.getRapporto_SocioLavoratore()) %>"  
			                      readonly="<%=String.valueOf(readOnlyStr)%>" />
				      </td>
			      </tr>
			      <tr>
			      	<td class="etichetta" >Lavoro in agricoltura</td>
		      		<td>		      		
		      		<%if(StringUtils.isFilledNoBlank(lav.getRapporto_LavoroAgricoltura()) && !lav.getRapporto_LavoroAgricoltura().equalsIgnoreCase("0")) {%>
			               <af:textBox classNameBase="input" type="text" name="lavAgr" value="<%=Utils.notNull(lav.getRapporto_LavoroAgricoltura()) %>"  
			                      readonly="<%=String.valueOf(readOnlyStr)%>" />
			           <%}else{%>
			           	&nbsp;
			           <%}%>
				      </td>
			      </tr>
			      <tr>
			      	<td class="etichetta" >CF lavoratore coobbligato</td>
		      		<td>
			               <af:textBox classNameBase="input" type="text" name="cfLavCobb" value="<%=Utils.notNull(lav.getLavoratoreCoobbligato_CodiceFiscale()) %>"  
			                      readonly="<%=String.valueOf(readOnlyStr)%>" />
				      </td>
			      </tr>
			    </table>
			   </td>
			 </tr>
			</table>
			
			<table cellpadding="0" cellspacing="0" border="0" width="100%">
			 <tr>
			 	<td class="campo"  rowspan="2"><b>Dati Cessazione</b></td>
			   <td>
			    <table align="left"  cellpadding="0" cellspacing="0" border="0" width="100%">			      
			      <tr>
			      	<td class="etichetta" >Data cessazione</td>
		      		<td>
		      		<%if(StringUtils.isFilledNoBlank(lav.getRapporto_Cessazione_Data()) && !lav.getRapporto_Cessazione_Data().equalsIgnoreCase("0")) {
		      			String dataCessazione = it.eng.sil.module.movimenti.consultaCO.Properties.trasformaData(lav.getRapporto_Cessazione_Data());
		      		%>
			                 <af:textBox classNameBase="input" type="text" name="datCess" value="<%=dataCessazione %>"  
			                      readonly="<%=String.valueOf(readOnlyStr)%>" />
			            <%}else{%>
			            	&nbsp;
			            <%}%>
				      </td>
			      </tr>
				 <tr>
			      	<td class="etichetta" >Causa</td>
		      		<td>
		      		<%if(StringUtils.isFilledNoBlank(lav.getRapporto_Cessazione_CodiceCausa()) && !lav.getRapporto_Cessazione_CodiceCausa().equalsIgnoreCase("0")) { 
		      				      		 
						String causaCessStr = lav.getRapporto_Cessazione_CodiceCausa() + " - ";
			      		causaCessStr =causaCessStr + it.eng.sil.module.movimenti.consultaCO.Properties.getDescrizioneFromCodice(serviceResponse, "M_COMBO_DE_CESSAZIONE",lav.getRapporto_Cessazione_CodiceCausa());
					%>
			               <af:textBox classNameBase="input" type="text" name="causaCess" value="<%=causaCessStr %>"  readonly="<%=String.valueOf(readOnlyStr)%>" /> 
			             
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
			 	<td class="campo"  rowspan="2"><b>Dati Proroga</b></td>
			   <td>
			    <table align="left"  cellpadding="0" cellspacing="0" border="0" width="100%">			      
			      <tr>
			      	<td class="etichetta" >Data fine proroga</td>
		      		<td>
		      		<%if(StringUtils.isFilledNoBlank(lav.getRapporto_Proroga_DataFine()) && !lav.getRapporto_Proroga_DataFine().equalsIgnoreCase("0")) {
		      			String dataFineProroga = it.eng.sil.module.movimenti.consultaCO.Properties.trasformaData(lav.getRapporto_Proroga_DataFine());		      		%>
			                   <af:textBox classNameBase="input" type="text" name="datFinePro" value="<%=dataFineProroga %>"  
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
			 	<td class="campo"  rowspan="2"><b>Dati Trasformazione</b></td>
			   <td>
			    <table align="left"  cellpadding="0" cellspacing="0" border="0" width="100%">			      
			      <tr>
			      	<td class="etichetta" >Data inizio rapp. trasformato</td>
		      		<td>
		      		<%if(StringUtils.isFilledNoBlank(lav.getRapporto_Trasformazione_Data()) && !lav.getRapporto_Trasformazione_Data().equalsIgnoreCase("0")) {
		      			String dataTrasformazione = it.eng.sil.module.movimenti.consultaCO.Properties.trasformaData(lav.getRapporto_Trasformazione_Data());	      		%>
			                 <af:textBox classNameBase="input" type="text" name="datRappTr" value="<%=dataTrasformazione %>"  
			                      readonly="<%=String.valueOf(readOnlyStr)%>" />
			            <%}else{%>
			            	&nbsp;
			            <%}%>
				      </td>
			      </tr>
				 <tr>
			      	<td class="etichetta" >Tipo</td>
		      		<td>
		      	<%if(StringUtils.isFilledNoBlank(lav.getRapporto_Trasformazione_CodiceTrasformazione()) && !lav.getRapporto_Trasformazione_CodiceTrasformazione().equalsIgnoreCase("0")) {
						String tipoTrasfStr = lav.getRapporto_Trasformazione_CodiceTrasformazione() + " - ";
			      		tipoTrasfStr =tipoTrasfStr + it.eng.sil.module.movimenti.consultaCO.Properties.getDescrizioneFromCodice(serviceResponse, "M_COMBO_DE_TIPO_TRASF",lav.getRapporto_Trasformazione_CodiceTrasformazione());
					%>
			               <af:textBox classNameBase="input" type="text" name="tipoTrasf" value="<%=tipoTrasfStr%>" readonly="<%=String.valueOf(readOnlyStr)%>" /> 
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
			               <af:textBox classNameBase="input" type="text" name="comPrec" value="<%=Utils.notNull(lav.getInvio_CodiceComunicazionePrecedente()) %>"  
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
