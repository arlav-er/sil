	<table class="main">
		<tr>
	        <td class="etichetta">
	       		CPI&nbsp;
	        </td>
			<td class="campo">
			
			<input type="hidden" name="codiceCpi" value="<%= Utils.notNull(codiceCpi)  %>" />
			 <af:textBox name="descrCpi" type="text"
							title="cpiTitolareMin" size="101"
							value="<%= Utils.notNull(descrCpi)  %>" 
							classNameBase="input"
							readonly="true"
				/>
	 		</td>
		</tr>
		<tr>
	        <td class="etichetta">
	       		Evento&nbsp;
	        </td>
			<td class="campo">
			   <af:comboBox name="tipoEvento" classNameBase="input"  addBlank="true"   title="Evento" 
                  		moduleName="M_ComboCondizionalita" selectedValue="<%=tipoEvento%>"
                      	onChange="fieldChanged();" required="true" disabled="<%= readonly %>"
               />
	 		</td>
		</tr>
		<tr>
	  	  <td class="etichetta">
	       		CF Beneficiario&nbsp;
	        </td>
			<td class="campo">
			 <af:textBox name="cfBeneficiario" type="text"
							title="cf Beneficiario"
							value="<%= Utils.notNull(cfBeneficiario) %>"
							size="20" maxlength="101"
							classNameBase="input"
							readonly="true"
				/>
	 		</td>
		</tr>
		<tr>
	    <td class="etichetta">
	       		CF Operatore CPI&nbsp;
	        </td>
			<td class="campo">
			 <af:textBox name="cfOperatoreCpi" type="text"
							title="CF Operatore CPI"
							value="<%= Utils.notNull(cfOperatoreCpi) %>"
							size="20" maxlength="16"
							classNameBase="input"
							readonly="<%= readonly %>" required="true"
							onKeyUp="fieldChanged();"
				/>
	 		</td>
		</tr>
		<tr>
	        <td class="etichetta">
	       		Tipo Domanda&nbsp;
	        </td>
			<td class="campo">
			  <input type="hidden" name="domandaTable"  />
			  <input type="hidden" name="chiavePrimaria"  />
			  <%if(showDettaglio){ %>
 					<input type="hidden" name="tipoDomanda" value="<%=codTipoDomanda%>" />
			  <%}else{%>
			  		<input type="hidden" name="tipoDomanda"  />
			  <%}%>
			  <input type="hidden" name="codiceStatoDomanda"  />
			  <input type="hidden" name="statoDomanda"  />
			   <af:comboBox name="tipoDomandaAll" classNameBase="input"  addBlank="true"   title="Tipo Domanda" 
                  		moduleName="M_ComboDomCondizionalita" selectedValue="<%=tipoDomanda%>"
                      	onChange="fieldChanged(); settaNomeTabella(); "   required="true" disabled="<%= readonly %>"
               />
               <%if(canModify){ %>
               	 <a
				HREF="javascript:cercaDomande();"><IMG
				name="image" border="0" src="../../img/binocolo.gif"
				alt="cerca domande"/></a>&nbsp;
			   <%}%>
	 		</td>
		</tr>
		<tr>
	        <td class="etichetta">
	       		Protocollo INPS/CodiceDomanda&nbsp;
	        </td>
			<td class="campo">
			 <af:textBox name="protInps" type="text"
							title="Protocollo INPS"
							value="<%= Utils.notNull(protocolloInps) %>"
							size="45" maxlength="101"
							classNameBase="input" required="true" readonly="<%= readonly %>"
							onKeyUp="fieldChanged();"
							 
				/>
	 		</td>
		</tr>
		<tr>
	        <td class="etichetta">
	       		Data Domanda&nbsp;
	        </td>
			<td class="campo">
			  <af:textBox onKeyUp="fieldChanged();" title="Data domanda" classNameBase="input" readonly="<%= readonly %>"  type="date"
                    name="dataDomanda"  validateOnPost="true"  value="<%= dataDomanda%>"  required="true"
                    size="12" maxlength="10"/>
	 		</td>
		</tr>
 		<tr>
	        <td class="etichetta">
	       		Data Evento&nbsp;
	        </td>
			<td class="campo">
			 <af:textBox name="dataEvento" type="date"
			 				validateOnPost="true" 
							title="Data Evento"
							value="<%= Utils.notNull(dataEvento) %>"
							size="12" maxlength="10"
							classNameBase="input"
							required="true"
							readonly="<%= readonly %>" 
							onKeyUp="fieldChanged();"
				/>
	 		</td>
		</tr>
		<%if(showDettaglio){ %>
 		<tr>
	        <td class="etichetta">
	       		Data Invio&nbsp;
	        </td>
			<td class="campo">
			 <af:textBox name="dataInvio" type="text"
							title="Data Invio"
							value="<%= Utils.notNull(dataInvio) %>"
							size="25"  
							classNameBase="input"
							readonly="true"  
				/>
	 		</td>
		</tr>
			<%if(StringUtils.isFilledNoBlank(strDataCancellazioneEvento)){
					String testoT = "Evento annullato " +  strDataCancellazioneEvento;
				%>
	 		<tr>
		        <td class="etichetta">
		       		&nbsp;
		        </td>
				<td class="campo">
				 <af:textBox name="eventoAnnullato" type="text"
								title="Data Invio"
								value="<%= testoT %>"
								size="30"  
								classNameBase="input"
								readonly="true"  
					/>
		 		</td>
			</tr>
			 <%}%>
		 <%}%>
  	  <tr>
            <td class="etichetta">Note&nbsp;</td>
            <td class="campo">
                <af:textArea classNameBase="textarea" onKeyUp="fieldChanged();" name="strnote" 
                maxlength="500" cols="60" rows="5" value="<%= note%>" readonly="<%= readonly %>"  />
            </td>
        </tr>
		 
	 
	</table>
	 
	 <table id="sezione_programma" width="100%" border=0>

    <tr>
        <td colspan="4">    
            <table class='sezione' cellspacing=0 cellpadding=0>
				<tr>					
					<td width=18></td>					
					<td class="sezione_titolo">Attività concordata</td>
					   <td class="sezione_pulsante"> 
		 <%if(canModify){ %>
                	<a
				HREF="javascript:cercaProgrammi();"><IMG
				name="image" border="0" src="../../img/binocolo.gif"
				alt="cerca programmi"/></a>
		 <%}%>&nbsp;	   
			 	</td>
 				</tr>
			</table>
		</td>
	</tr>
	
	<tr>
    	<td colspan=4 align="center">
        	<table id="T_S_PROGRAMMA" style="width:100%;">     
        		<tr>
        		
        		<%if(showDettaglio){ %>
 					 <input type="hidden" name="prgColloquio" value="<%= prgColloquio%>"  />
        	  		 <input type="hidden" name="prgPercorso"  value="<%= prgPercorso%>" />
			  <%}else{%>
			  		 <input type="hidden" name="prgColloquio"  />
        	  		 <input type="hidden" name="prgPercorso"  />
			  <%}%>
        		
	        		<td class="etichetta">Programma&nbsp;</td>        
	        		<td class="campo">
	        			 <af:textArea classNameBase="textarea"  name="programma" 
               				 cols="30" rows="3" value="<%= programma%>" readonly="true" />
	        				 
					</td>										
	        	</tr>
	        	<tr>
	        		<td class="etichetta">Data inizio prog.&nbsp;</td>        
	        		<td class=campo>
	        			 <af:textBox title="Data inizio programma" classNameBase="input" readonly="true" type="date"
			                    name="dataInizioProg" required="false" value="<%= dataInizioProg%>" 
			                    size="18"/>
					</td>
	        	</tr>
	        	<tr>
	        		<td class="etichetta">Data fine prog.&nbsp;</td>        
	        		<td class=campo>
	        			 <af:textBox title="Data fine programma" classNameBase="input" readonly="true" type="date"
			                    name="dataFineProg" required="false" value="<%= dataFineProg%>" 
			                    size="18"/>
					</td>
	        	</tr>
        		<tr>
	        		<td class="etichetta">Attività concordata&nbsp;</td>        
	        		<td class="campo">
	        		 	<af:textArea classNameBase="textarea"  name="attivita" 
               				 cols="30" rows="3" value="<%= attivita%>" readonly="true" /> 
					</td>										
	        	</tr>
	        	<tr>
	        		<td class="etichetta">Data avvio&nbsp;</td>        
	        		<td class=campo>
	        			 <af:textBox title="Data avvio" classNameBase="input" readonly="true" type="date"
			                    name="dataAvvio" required="false" value="<%= dataAvvio%>" 
			                    size="18"/>
					</td>
	        	</tr>
	        	<tr>
	        		<td class="etichetta">Data conclusione effettiva/prevista&nbsp;</td>        
	        		<td class=campo>
	        			 <af:textBox title="Data conclusione effettiva/prevista" classNameBase="input" readonly="true" type="date"
			                    name="dataConclusione" required="false" value="<%= dataConclusione%>" 
			                    size="18"/>
					</td>
	        	</tr>
        		<tr>
	        		<td class="etichetta">Esito&nbsp;</td>        
	        		<td class="campo">
	        				 <af:textBox title="Esito" classNameBase="input" readonly="true" type="text"
			                    name="esito" required="false" value="<%= esito %>"   
			                    />
					</td>										
	        	</tr>
	        </table>
	    </td>
	</tr>
	</table>