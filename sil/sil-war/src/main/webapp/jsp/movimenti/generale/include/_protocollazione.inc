<%@ include file ="_functionProtocollazione.inc" %>

<table class="main" cellpadding="0" cellspacing="0" border="0">
	<TR>
    	<td class="campo" colspan="4"></td>
    	<td class="etichetta" colspan="2"><%=decMonoProv%></td>
	</TR>
	<tr>
    	<td class="azzurro_bianco" colspan="6">
			<table class="main" cellpadding="0" cellspacing="0" border="0">
				<tr>
					<td colspan=4>
        				<table class="main" cellpadding="0" cellspacing="0" border="0">
          					<tr>
            					<td class="etichetta" nowrap="nowrap">Provenienza</td>
            						<td class="campo" nowrap="nowrap">
              							<af:comboBox classNameBase="input" name="codMonoMovDich" title="Movimento documentato o dichiarato" onChange="fieldChanged();valDevalDatComunicaz();cambiacodMonoMovDich()" disabled="<%=String.valueOf(!canModifyStato)%>" required="true">
                							<option value="O" <% if (codMonoMovDich.equals("O") || codMonoMovDich.equals("")) {%>selected="selected" <%}%>>Da comunicazione obbligatoria</option>
                							<option value="D" <% if (codMonoMovDich.equals("D")) {%>selected="selected" <%}%>>Documentato dal lavoratore</option>
                							<option value="C" <% if (codMonoMovDich.equals("C")) {%>selected="selected" <%}%>>Dichiarato</option>              
              							</af:comboBox>
              							<input type="hidden" name="codMonoMovDichHid" value="<%=codMonoMovDich%>" />
            						</td>
		       						<td class="etichetta" nowrap="nowrap">Data di comunicazione</td>
            						<td class="campo" nowrap="nowrap">
              							<div id='dataCom1' style="display: inline;">
                							<af:textBox classNameBase="input" size="11" maxlength="10" type="date" validateOnPost="true" title="Data Comunicazione" required="true" name="DATCOMUNICAZ" value="<%=datComunicaz%>" onKeyUp="fieldChanged();" readonly="<%=String.valueOf(!canModify)%>"/>
              							</div>              
              							<div id='dataCom2' style="display: none;">
                							<af:textBox classNameBase="input" size="11" maxlength="10" type="date" title="Data Comunicazione" name="DATCOMUNICAZ2" value="" onKeyUp="fieldChanged();" onBlur="if(document.Frm1.codMonoMovDich.value == 'O') calcolaDiffGiorni2(this, document.Frm1.DATAINIZIO, document.Frm1.NUMGGTRAMOVCOMUNICAZIONE2, varRange);"/>&nbsp;*
              							</div>              
            						</td>
            <%if(consulta) {%>
									<td class="etichetta" style="display: inline;" id="labelRit" nowrap="nowrap">Giorni di</br>possibile ritardo</td>
              						<td class="campo" style="display: inline;" id="campoRit">
                						<div id='ritardo1' style="display: inline;">
                  							<af:textBox classNameBase="input" onKeyUp="fieldChanged();" type="number" validateOnPost="true" name="NUMGGTRAMOVCOMUNICAZIONE" required="false" title="Giorni di ritardo"  size="4" maxlength="10" value="<%=numGgTraMovComunicaz%>" readonly="<%=String.valueOf(!canModify)%>"/>
                						</div>

                						<div id='ritardo2' style="display: none;">
                  							<af:textBox classNameBase="input" onKeyUp="fieldChanged();" type="number" name="NUMGGTRAMOVCOMUNICAZIONE2" required="false" title="Giorni di ritardo"  size="4" maxlength="10" value=""/>
                						</div>
              						</td>
           <%} else {%>
									<td class="etichetta" >&nbsp;</td>
									<td class="campo" >&nbsp;</td>
             <% } %>
          						</tr>
        				</table>
       				</td>
      			</tr>
      			<tr>
      				<td colspan="4">
        				<div class='sezione2' id='sezProtocollazione'></div>
        				<br/>
        			</td>
      			</tr>
                
       			<tr>
          			<td class="etichetta2" nowrap="nowrap">Stato&nbsp;Atto</td>
          			<td class="campo2" nowrap="nowrap">
            <%if (!consulta) {
                 boolean disabilita = !canModifyStato;
                 // 27/04/2007 in caso di validazione lo stato atto da mostrare e' PA
                 String _page = (String)serviceRequest.getAttribute("PAGE");
                 boolean sareDocareaProtocollati = "MovValidaDettaglioGeneralePage".equalsIgnoreCase(_page) 
					&& it.eng.sil.bean.protocollo.ProtocolloDocumentoUtil.importDocProtocollati() 
					&& !it.eng.sil.bean.protocollo.ProtocolloDocumentoUtil.protocollazioneLocale();
				 
				 if (sareDocareaProtocollati) 
				 	codStatoAtto = "PR";
				 else 
				 	codStatoAtto = "PA";
            %>
            
            <af:comboBox  classNameBase="input" disabled ="true"  name="CODSTATOATTO"  
                          				moduleName="ComboStatoAtto" selectedValue="<%=codStatoAtto%>"  
                          				addBlank="true" blankValue="" required="false"/>
                          				
            
            <%-- (15/11/2004) andrea: combiato valore in PA
						<af:textBox type="hidden" classNameBase="input" name="CODSTATOATTO" title="Stato Atto" value="PA" />
              			<af:textBox classNameBase="input" name="STRSTATOATTO" title="Stato Atto" value="IN ATTESA DI ESSERE PROT." readonly="true" size="30" />
			commentata il 26/04/2007 --%>
			
            <%-- (07/11/2004) Davide: creata combo per permettere la selezione del movimento sospeso
                        <%if( codStatoAtto.equals("") ) { codStatoAtto = "PA"; }%>
                        <af:comboBox name="CODSTATOATTO" selectedValue="<%=codStatoAtto%>" title="Stato Atto" classNameBase="input"
                                     required="true" onChange="fieldChanged();" disabled="<%=String.valueOf(disabilita)%>">
                         <option value="PA" >IN ATTESA DI ESSERE PROT.</option>
                         <option value="SS" >SOSPESO</option>
                        </af:comboBox>
             --%>
            <%} else {
            		boolean disabilita = !canModifyStato;
            		if(!codStatoAtto.equals("PR"))
            			disabilita = true; 
            %>
	              	<af:comboBox classNameBase="input" name="CODSTATOATTO" moduleName="ComboStatoAtto" title="Stato Atto" required="true" addBlank="true" selectedValue="<%=codStatoAtto%>" onChange="fieldChanged();chekObbligatorieta(this);" disabled="<%=String.valueOf(disabilita)%>"/>
            <%}%>
          			</td>
			<%-- 26/04/2007 Protocollazione DOCAREA: se abilitata si nascondono i campi del protocollo --%>
			<%
//			In validazione non ho il prgmovimento, e se devo visualizzare il protocollo lo so dal tipo in uso e dall'ambito
			// Per il momento mi accontento di vedere solo lo stato del documento
			%>
			<% //if ((prgMovimento!=null && !prgMovimento.equals("")) 
				if ((consulta)
					|| it.eng.sil.bean.protocollo.ProtocolloDocumentoUtil.protocollazioneLocale() ) { %>
          			<td class="etichetta2">Anno</td>
          			<td>
            			<table class="main" cellpadding="0" cellspacing="0" border="0">
             				<tr>
             					<td class="campo2" nowrap="nowrap">
               						<div id='anno' style="display:" >
                   						<af:textBox name="numAnnoProt"
                             				validateOnPost="true" 
											title="Anno di protocollazione"
											value="<%= Utils.notNull(numAnnoProtV) %>"
											classNameBase="input"
											size="4"
											maxlength="4"											
											required="true"
											readonly="true" />
               						</div>

					               <div id='anno2' style="display: none;">
					                   <af:textBox name="numAnnoProt2" 
											title="Anno di protocollazione"
											value=""
											classNameBase="input"
											size="4"
											maxlength="4"
											readonly = "true" />*
					               </div>
             					</td>            
             					<td class="etichetta2" >Num</td>
             					<td class="campo2" nowrap="nowrap">
               						<div id='numeroProt' style="display: ">
					                   <af:textBox name="numProtocollo"
					                             title="Numero di protocollo"
					                             value="<%= Utils.notNull(numProtV) %>"
					                             classNameBase="input"
					                             size="6"
					                             maxlength="38"					                             
					                             required="true"
					                             readonly="<%=String.valueOf(!numProtEditable)%>" />
					               </div>
					               <div id='numeroProt2' style="display: none;">
					                   <af:textBox name="numProtocollo2"
					                             title="Numero di protocollo"
					                             value=""
					                             classNameBase="input"
					                             size="6"
					                             maxlength="38"/>*
					               </div>
                 						<input type="hidden" name="KLOCKPROT"  value="<%=kLockProt%>">
                 				</td>
								<td class="etichetta2">data</td>
								<td nowrap="nowrap">
									<div id='dataProt' style="display: ">
									    <af:textBox name="dataProt" 
									                type="date" 
									                value="<%=datProtV%>" 
									                size="11" 
									                maxlength="10"
									                title="data di protocollazione"  
									                classNameBase="input" 
									                readonly="<%=String.valueOf(!numProtEditable)%>" 
									                validateOnPost="true" 
									                required="false" 
									                trim ="false" 
									                onKeyUp="cambiAnnoProt(this,numAnnoProt)" 
									                onBlur="checkFormatDate(this)"
									     />*
									</div>
	            					<div id='dataProt2' style="display: none;">
										<af:textBox name="dataProt2" 
										            type="date" 
										            value="" 
										            size="11" 
										            maxlength="10"
										            title="data di protocollazione"  
										            classNameBase="input" 
										            validateOnPost="true" 
										            required="false" 
										            trim ="false" 
										            onKeyUp="cambiAnnoProt(this,numAnnoProt)" 
										            onBlur="checkFormatDate(this)"
										 />*
									</div>
             					</td>
             			<% if (it.eng.sil.bean.protocollo.ProtocolloDocumentoUtil.protocollazioneLocale()) { %>
             					<td class="etichetta2">ora</td>
								<td nowrap="nowrap">
									<div id='oraProt' style="display: ">
									    <af:textBox name="oraProt"
									                type="date"
									                value="<%=oraProtV%>"
									                size="5" 
									                maxlength="5"
									                title="ora"  
									                classNameBase="input" 
									                readonly="<%=String.valueOf(!numProtEditable)%>"
									                validateOnPost="false" 
									                required="false" 
									                trim ="false"
									                onBlur="checkAndFormatTime(this)"
									     />*
									</div>

									<div id='oraProt2' style="display: none;">
									    <af:textBox name="oraProt2"
									                type="date"
									                value=""
									                size="5" 
									                maxlength="5"
									                title="ora"  
									                classNameBase="input" 
									                validateOnPost="false" 
									                required="false" 
									                trim ="false"
									                onBlur="checkAndFormatTime(this)"
									     />*
									</div>
						<% } else { %>
									<input name="oraProt" type="hidden" value="00:00" />
									<input name="oraProt2" type="hidden" value="" />
						<% } %>
             						<input name="tipoProt" type="hidden" value="<%=prAutomatica%>" />
             					</td>
           					</tr>
        				</table>
        			</td>
		<% } else { %>
    							<td colspan="8">
    								<input type="hidden" name="numAnnoProt" value="<%= Utils.notNull(numAnnoProtV) %>">
    								<input type="hidden" name="numProtocollo">
    								<input type="hidden" name="dataProt" value="<%= datProtV %>">
    								<input type="hidden" name="oraProt" value="00:00">
    								<input name="tipoProt" type="hidden" value="<%=prAutomatica%>" />
    							</td>
    	<% } %> 
      			</tr>
      			<tr>
          			<td class="etichetta2" nowrap="nowrap">Motivo Atto</td>
          			<td class="campo2" nowrap="nowrap" colspan=3>
          				<table  cellpadding="0" cellspacing="0" border="0">
          					<tr>
          						<td>
            <%  
	            	boolean disabilita = !canModifyStato;
	            	if(!codStatoAtto.equals("PR")) disabilita = true; 
	            	if (!consulta) 
	            		codMotAnnullamento = "";
            %>
	        	      			<af:comboBox classNameBase="input" name="CODMOTANNULLAMENTO" moduleName="ComboMotAnnullamento" title="Motivo Atto" addBlank="true" selectedValue="<%=codMotAnnullamento%>" onChange="fieldChanged();" disabled="<%=String.valueOf(disabilita)%>"/><span id="obbligo" style="display:none">&nbsp;*</span>
            <%--} else { %>&nbsp;<% } --%>
	          					</td>      
	          					<td class="etichetta2" style="width: 50px">Doc. di </td>
	          					<td class="campo2" style="width: 80px"><strong>
          				
              				<!--tr-->
                  				
              <% if (docInOut.equalsIgnoreCase("I")){ %>Input
              <% } else if (docInOut.equalsIgnoreCase("O")){ %>Output
              <%     } else { %>&nbsp;<% } %></strong>
	                  			</td>
	                  			<td class="etichetta2">Rif.</td><td class="campo2"><strong><%=docRif%></strong></td>
	                  			<td>
              <%if (consulta && canModifyProtocol && canView && codStatoAtto.equals("PR")) {%>
                    		 		&nbsp;&nbsp;<input type="button" class="pulsante" name="submitbutton" value="Salva" onClick="javascript:salvaDati();"/>
              <% } %>
                  				</td>
              				</tr>
            			</table>
          			</td>
        		</tr>
      		</table>
    	</td>
    </tr>
	<tr><td><br/></td></tr>
</table>
