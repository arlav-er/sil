<% if (tipoTrasferimento.equals("A")) {
		codCpiTit="";
		strCpiTit="";
	}
	boolean intraProvinciale = false;
	if (IntraProvinciale.equals("true")){
		intraProvinciale = true;
	}
%>

			<tr>
				<td class="etichetta">Data Trasferimento</td>
				<td class="campo">
					<af:textBox classNameBase="input" type="date" title="Data Trasferimento" name="DATTRASFERIMENTO" value="<%=DATTRASFERIMENTO%>" validateOnPost="true" validateWithFunction="segnalaData"
                    inline="onChange=\"aggiornaDataSosp()\"" required="true" readonly="<%=String.valueOf(!canModify)%>" onKeyUp="fieldChanged();" size="11" maxlength="10"/>
				</td>
			</tr>
			<tr>
			    <td align="center" colspan="4">
			      <div class="sezione2">Informazioni per il nuovo CPI</div>
			    </td>
			</tr>
			<tr>
			<td colspan="4" class="titolo"><br/><center><b>Nuovo Domicilio</b></center></td>
			</tr>			
			<tr>
			    <td class="etichetta">Indirizzo&nbsp;</td>
			    <td class="campo"><af:textBox classNameBase="input"  title="indirizzo domicilio" onKeyUp="fieldChanged();" type="text" name="STRINDIRIZZODOM" value="<%=STRINDIRIZZODOM%>" size="50" maxlength="50" required="true" readonly="<%= String.valueOf(!canModify) %>" /></td>
			    <td class="etichetta" >&nbsp;</td>
			    <td class="campo">&nbsp;</td>
			</tr>
			<tr>
			    <td class="etichetta">Località&nbsp;</td>
			    <td class="campo"><af:textBox classNameBase="input" title="località" onKeyUp="fieldChanged();" type="text" name="STRLOCALITADOM" value="<%=STRLOCALITADOM%>" size="50" maxlength="50" readonly="<%= String.valueOf(!canModify) %>" /></td>
			    <td class="etichetta" >&nbsp;</td>
			    <td class="campo">&nbsp;</td>
			</tr>
			<tr>
			    <td class="etichetta">Comune&nbsp;</td>
			    <td class="campo" nowrap="nowrap">
			        <af:textBox classNameBase="input" 
			        			type="text" 
			        			name="codComdom" 
			        			value="<%=codComdom%>" 
			        			title="Nuovo codice comune domicilio"
			            		onKeyUp="fieldChanged();PulisciRicerca(document.Frm1.codComdom, document.Frm1.codComdomHid, document.Frm1.strComdom, document.Frm1.strComdomHid, document.Frm1.strCapDom, document.Frm1.strCapDomHid, 'codice');"             
			            		size="4" 
			            		maxlength="4"
			            		validateWithFunction="<%=controllaCodComInCPI%>" 
			            		readonly="<%= String.valueOf(!canModify) %>" />&nbsp;	
			        <% if(canModify) { %>
				        <A HREF="javascript:btFindComuneCAP_onclick(document.Frm1.codComdom, 
				                                                    document.Frm1.strComdom, 
				                                                    document.Frm1.strCapDom, 
				                                                    'codice',''
				                                                    <% if (tipoTrasferimento.equals("A")) { %>
				                                                    , document.Frm1.codCPI, 'getCPIDesc()', 'inserisciComDomNonScaduto()'
				                                                    <% } else { %>
				                                                    , null, 'inserisciComDomNonScaduto()'
				                                                    <% } %>
				                                                    );">
				        <IMG name="image" border="0" src="../../img/binocolo.gif" alt="cerca per codice"/></a>&nbsp;
			        <% } %>
			        <af:textBox type="hidden" name="codComdomHid" value="<%=codComdom%>" />
			        
			        <af:textBox classNameBase="input" 
			        			type="text"  
			        			name="strComdom" 
			        			value="<%=strComdom%>"
			            		onKeyUp="fieldChanged();PulisciRicerca(document.Frm1.codComdom, document.Frm1.codComdomHid, document.Frm1.strComdom, document.Frm1.strComdomHid, document.Frm1.strCapDom, document.Frm1.strCapDomHid,'descrizione');"  
			            		size="50" 
			            		maxlength="50" 
			            		title="comune di domicilio" 
			            		inline='onkeypress="if (event.keyCode==13) { event.keyCode=9; this.blur(); }"' 
			            		readonly="<%= String.valueOf(!canModify) %>" />&nbsp;*&nbsp;&nbsp;
			        <% if(canModify) { %>
				        <A HREF="javascript:btFindComuneCAP_onclick(document.Frm1.codComdom, 
				                                                    document.Frm1.strComdom, 
				                                                    document.Frm1.strCapDom, 
				                                                    'descrizione',''
				                                                     <% if (tipoTrasferimento.equals("A")){ %>
				                                                     , document.Frm1.codCPI, 'getCPIDesc()', 'inserisciComDomNonScaduto()'
				                                                     <% } else { %>
				                                                     , null, 'inserisciComDomNonScaduto()'
				                                                     <% } %>
				                                                     );">
																		
						<IMG name="image" border="0" src="../../img/binocolo.gif" alt="cerca per descrizione"/></a>
			        <% } %>
			        <af:textBox type="hidden" name="strComdomHid" value="<%=strComdom%>" />
    				Cap&nbsp;
      				<af:textBox classNameBase="input" 
      							name="strCapDom" 
      							value="<%=strCapDom%>" 
            					onKeyUp="fieldChanged();PulisciRicerca(document.Frm1.codComdom, document.Frm1.codComdomHid, document.Frm1.strComdom, document.Frm1.strComdomHid, document.Frm1.strCapDom, document.Frm1.strCapDomHid,'cap');"
            					title="Cap del domicilio" 
            					type="text"  
            					size="5" 
            					maxlength="5" 
            					validateWithFunction="" 
            					readonly="<%= String.valueOf(!canModify) %>"/>
      				<af:textBox type="hidden" name="strCapDomHid" value="<%=strCapDom%>" />
    			</td>
			</tr>
			<tr>
			    <td class="etichetta">Nuovo CPI Competente</td>
			    <td class="campo">
			        <af:textBox classNameBase="input" type="text" name="codCPI" 
		        		value="<%=codCpiTit%>" 
			            size="10" maxlength="9" validateWithFunction="codCPIUpperCase" 
			            readonly="true"
			        />&nbsp;
			        <af:textBox type="text" classNameBase="input" name="strCPI" value="<%=strCpiTit%>"
			            size="30" maxlength="50" 
			            readonly="true"
			        />&nbsp;
				</td>
			</tr>
			<tr>
			    <td align="center" colspan="4">
			      <div class="sezione2">Informazioni dal CPI di Provenienza</div>
			    </td>
			</tr>
			<tr>
			    <td class="etichetta">CPI di provenienza</td>
			    <td class="campo">
			    <% if (IntraProvinciale.equals("false") && coopAttiva) {%>
			        <af:textBox classNameBase="input" type="text" name="CODCPIORIG" value="<%=codCpiOrig%>" size="10"
			        			maxlength="9" onKeyUp="fieldChanged();PulisciRicercaCPIdaTrasf(document.Frm1.CODCPIORIG, 
			        			document.Frm1.CODCPIORIGHid, document.Frm1.DESCRCPIORIG, document.Frm1.DESCRCPIORIGHid,'codice', 
			        			document.Frm1.msgIRLav, document.Frm1.msgXIR, document.Frm1.Coop, document.Frm1.imgCoop);" 
			        			validateWithFunction="controllaPresenzaCpiDiProvenienza" readonly="false"/>

			       	<A HREF="javascript:btFindCPI_onclick(  document.Frm1.CODCPIORIG,
			       											document.Frm1.CODCPIORIGHid,
			                                                document.Frm1.DESCRCPIORIG,
			                                                document.Frm1.DESCRCPIORIGHid,
			                                                'codiceDATRASF');">			                                                			                                             
			        <IMG name="binocolo" border="0" src="../../img/binocolo.gif" alt="cerca per codice"/></a>&nbsp;			
					<af:textBox type="hidden" name="CODCPIORIGHid" value="" />
								        			
			        <af:textBox type="text" classNameBase="input" title="CPI di provenienza" name="DESCRCPIORIG" value="<%=desCpiOrig%>" 
			        size="30" maxlength="50" required="true" onKeyUp="fieldChanged();PulisciRicercaCPIdaTrasf(document.Frm1.CODCPIORIG, 
			        document.Frm1.CODCPIORIGHid, document.Frm1.DESCRCPIORIG, document.Frm1.DESCRCPIORIGHid,'descrizione', 
			        document.Frm1.msgIRLav, document.Frm1.msgXIR, document.Frm1.Coop, document.Frm1.imgCoop);" readonly="false"/>

			       	<A HREF="javascript:btFindCPI_onclick(  document.Frm1.CODCPIORIG,
			       											document.Frm1.CODCPIORIGHid,
			                                                document.Frm1.DESCRCPIORIG,
			                                                document.Frm1.DESCRCPIORIGHid,			                               
			                                                'descrizioneDATRASF');">
			        <IMG name="binocolo2" border="0" src="../../img/binocolo.gif" alt="cerca per descrizione"/></a>&nbsp;&nbsp;
			        <af:textBox type="hidden" name="DESCRCPIORIGHid" value="" />
			        <af:textBox type="hidden" name="userProvCpi" value="<%= userProvCpi%>" />
			        <af:textBox type="hidden" name="origProvCpi" value="<%= provCpiOrig%>" />
			        <af:textBox type="hidden" name="userRegCpi" value="<%= userRegCpi%>" />
			        <af:textBox type="hidden" name="origRegCpi" value="<%= regCpiOrig%>" />
			        <% if (coopAttiva && poloInCoop) {%>
			        	<af:textBox type="text" classNameBase="input" name="Coop" value="In cooperazione" 
			        		size="15" maxlength="15" readonly="true"/><IMG name="imgCoop" border="0" src="../../img/cooperazione.gif"/>
			        <% } else {%>
			        	<af:textBox type="text" classNameBase="input" name="Coop" value="Invio cartaceo" 
			        		size="15" maxlength="15" readonly="true"/><IMG name="imgCoop" border="0" src="../../img/text.gif"/>
			        <% } %>
			    <% } else {%>
			    	<af:textBox classNameBase="input" type="text" title="Codice CPI di provenienza" name="CODCPIORIG" 
			    		value="<%=codCpiOrig%>" size="10" maxlength="9" validateWithFunction="codCPIUpperCase"
			    		readonly="true"
			        />&nbsp;
			        <af:textBox type="text" classNameBase="input" name="DESCRCPIORIG" value="<%=desCpiOrig%>" 
			        	size="30" maxlength="50" 
			            readonly="true"
			        />&nbsp;
			    <% } %> 
			</tr>
			<% if (IntraProvinciale.equals("false")) {%>
				<tr>
					<td class="etichetta"></td>
					<td class="campo">
						<af:textBox type="text" classNameBase="input" name="msgIRLav" value="<%=msgIRLav%>" 
			        		size="80" maxlength="80" readonly="true"/>
				</tr>
				<tr>
					<td class="etichetta"></td>
					<td class="campo">
						<af:textBox type="text" classNameBase="input" name="msgXIR" value="<%=msgXIR%>" 
			        		size="60" maxlength="60" readonly="true"/>
				</tr>
			<% } %>
			<tr>
			  <td class="etichetta">Stato occupazionale </td>
			  <td colspan="3">
			  <table width="100%" border="0"><tr><td>
			     <af:comboBox classNameBase="input" name="CODSTATOOCCUPAZ" moduleName="M_GETDESTATOOCC" selectedValue="<%=CODSTATOOCCUPAZ%>" addBlank="true"
			                  title="Stato occupazionale" onChange="aggiornaRaggruppamento()" required="true" disabled="<%= String.valueOf(!canModify || ((!isInterProvincia && !isInterRegione) && intraProvinciale))%>" />
  		         <af:textBox type="hidden" name="codStatoOccupazRagg" value="<%=codStatoOccRagg%>" />
			  </td></tr></table>
			  </td>
			</tr>
		<tr><td colspan = 2>
		<div id="divAnzianita" name="divAnzianita" style='display:<%=((codStatoOccRagg.equalsIgnoreCase("I") || codStatoOccRagg.equalsIgnoreCase("D"))?"":"none")%>'>
		<table>
			<tr>
			  <td class="etichetta">Anzianità di disoccupazione</td>
			  <td colspan="3">
			    <table border="0" width="100%">
			    <tr><td width="25%">dal
			        <af:textBox classNameBase="input" type="date" name="datAnzianitaDisoc" value="<%=Utils.notNull(dataAnzDisoc)%>" validateOnPost="true" 
			                    inline="onChange=\"aggiornaDataDichiarazione()\"" readonly="<%= String.valueOf(!canModify || ((!isInterProvincia && !isInterRegione) && intraProvinciale))%>" onKeyUp="fieldChanged();" size="11" maxlength="10"/></td>
			    </tr>
			    </table>              
			  </td>
			</tr>

			<tr>
			<td class="etichetta">mesi sosp.</td>
			        <td colspan=3 class="campo">
			          <table border="0" width="100%" class="main">
			           <tr>
			                <td class="etichetta" nowrap>
			                  prima del&nbsp;
			                  <af:textBox classNameBase="input" type="date" name="datcalcolomesisosp" value="<%=Utils.notNull(datcalcolomesisosp)%>" validateOnPost="true"
			                              readonly="<%=String.valueOf(!canModify || ((!isInterProvincia && !isInterRegione) && intraProvinciale))%>" onKeyUp="fieldChanged();"  size="11" maxlength="10"/>
			                </td>
			                <td nowrap>
			                  <af:textBox classNameBase="input" type="integer" name="numMesiSosp" value="<%=Utils.notNull(numMesiSospPrecInt)%>" validateOnPost="true"
			                              readonly="<%=String.valueOf(!canModify || ((!isInterProvincia && !isInterRegione) && intraProvinciale))%>" onKeyUp="fieldChanged();"  size="4" maxlength="3"/>
			                </td>
			                <td class="etichetta" nowrap>                            
			                  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			                  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;+&nbsp;succ.
			                </td>
			              <td class="campo" nowrap>
			                <af:textBox classNameBase="input" type="integer" name="numMesiSospSucc" value="<%=Utils.notNull(numMesiSospInt)%>"  validateOnPost="true"
			                          readonly="true"  size="4" maxlength="3"/>
			              </td>
			              <td class="etichetta">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
			              <td class="campo">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
			              <td nowrap>
			                =
			              </td>
			              <td class="etichetta" nowrap>
			              <b><%=Utils.notNull(String.valueOf(Integer.parseInt(numMesiSospPrecInt)+Integer.parseInt(numMesiSospInt)))%></b>			            
			                <input type="hidden" name="totNumMesiSosp" value="<%=Utils.notNull(String.valueOf(Integer.parseInt(numMesiSospPrecInt)+Integer.parseInt(numMesiSospInt)))%>">
			              </td>
		                </tr> 
			          </table>
			        </td>
			</tr>
                 
			<tr>
			   <td class="etichetta">Mesi anzianità</td>
			   <td colspan="3" nowrap>
			     <table border="0" width="100%" class="main">
			     <tr>
			        <td class="etichetta" nowrap>
			            prima del&nbsp;
			              <af:textBox classNameBase="input" type="date" name="datcalcoloanzianita" value="<%=Utils.notNull(datcalcoloanzianita)%>" validateOnPost="true"
			                          readonly="<%= String.valueOf(!canModify || ((!isInterProvincia && !isInterRegione) && intraProvinciale))%>" onKeyUp="fieldChanged();"  size="11" maxlength="10"/>
			        </td>
			        <td nowrap>
			          <af:textBox classNameBase="input" type="integer" name="numAnzianitaPrec297" value="<%=Utils.notNull(mesiAnzPrecInt)%>"  validateOnPost="true"
			                      readonly="<%= String.valueOf(!canModify || ((!isInterProvincia && !isInterRegione) && intraProvinciale))%>" onKeyUp="fieldChanged();" size="4" maxlength="38"/>
			          &nbsp;
			          <%if(!canModify || ((!isInterProvincia && !isInterRegione) && intraProvinciale)){%>
			          	  <af:textBox classNameBase="input" type="text" name="CODMONOCALCOLOANZIANITAPREC297" value="<%=codMonoCalcAnzPrec%>" readonly="true" size="4" maxlength="3"/>
			          <%}else{%>
				          <af:comboBox classNameBase="input" name="CODMONOCALCOLOANZIANITAPREC297" addBlank="false" onChange="fieldChanged();" disabled="<%=Utils.notNull(String.valueOf(!canModify))%>">
				            <OPTION value="" <%if (codMonoCalcAnzPrec == null) out.print("SELECTED=\"true\"");%>></OPTION>
				            <OPTION value="A" <%if ((codMonoCalcAnzPrec!=null) && codMonoCalcAnzPrec.equalsIgnoreCase("A")) out.print("SELECTED=\"true\"");%>>A</OPTION>
				            <OPTION value="M" <%if ((codMonoCalcAnzPrec!=null) && codMonoCalcAnzPrec.equalsIgnoreCase("M")) out.print("SELECTED=\"true\"");%>>M</OPTION>
				          </af:comboBox>                    
					  <%}%>
			        </td>
			        <td class="etichetta" nowrap>
			          &nbsp;&nbsp;+&nbsp;succ.
			        </td>
			      <td class="campo" nowrap>
			        <af:textBox classNameBase="input" type="integer" name="numMesiAnzianita" value="<%=Utils.notNull(mesiAnzInt)%>" validateOnPost="true"
			                    readonly="true" size="4" maxlength="3"/>
			        
			      </td>
			      <td class="etichetta" nowrap>
			        -&nbsp;sosp.
			      </td>
			      <td class="campo" nowrap>
			        <af:textBox classNameBase="input" type="integer" name="_totNumMesiSosp" value="<%=Utils.notNull(String.valueOf(Integer.parseInt(numMesiSospPrecInt)+Integer.parseInt(numMesiSospInt)))%>"  validateOnPost="true"
			                    readonly="true" size="4" maxlength="3"/>
			      </td>
			      <td nowrap>
			        =
			      </td>
			      <td class="etichetta" nowrap>
			      <b><%=Utils.notNull(String.valueOf(Integer.parseInt(mesiAnzPrecInt)+Integer.parseInt(mesiAnzInt)-(Integer.parseInt(numMesiSospPrecInt)+Integer.parseInt(numMesiSospInt))))%></b>
			        <input type="hidden" name="totNumMesiAnz" value="<%=Utils.notNull(String.valueOf(Integer.parseInt(mesiAnzPrecInt)+Integer.parseInt(mesiAnzInt)-(Integer.parseInt(numMesiSospPrecInt)+Integer.parseInt(numMesiSospInt))))%>">
			      </td>
			     </tr>
			     </table>
			   </td>
			</tr>
			<tr>
			  <td>&nbsp;</td>
			  <td class="campo">
			    <%if(mesiAnz != null && mesiAnz.compareTo(new BigDecimal(24)) >= 0)  { %>
			      <af:textBox classNameBase="input" type="text" name="tipoAnzianita" readonly="true"
			                   value="soggetto alla legge 407/90" size="40"/>
			    <% } %>
			 </td>
			</tr>
			<tr>
				<td class="etichetta" nowrap>Data DID</td>
				<td class="campo" nowrap>
					<af:textBox classNameBase="input" type="date" name="datDichiarazione" value="<%=Utils.notNull(DATDICHIARAZIONE)%>" validateOnPost="true" 
					readonly="<%=String.valueOf(isPresenteDid || !canModify || ((!isInterProvincia && !isInterRegione) && intraProvinciale))%>" onKeyUp="fieldChanged();" size="11" maxlength="10"/>
				</td>
			</tr>

		</table>	
		</div>
		</td></tr>
			<tr><td colspan=4>&nbsp;</td></tr>

			<tr>
				<td class="etichetta" nowrap>Soggetto iscritto al 181</td>
				<td class="campo" nowrap>
			     <af:comboBox classNameBase="input" name="FLG181" selectedValue="<%=Utils.notNull(FLG181)%>"
			                  title="Soggetto Iscritto al 181" disabled="<%= String.valueOf(!canModify)%>">
			            <OPTION value="" <%if (FLG181.equals("")) out.print("SELECTED=\"true\"");%>></OPTION>
			            <OPTION value="S" <%if (FLG181.equalsIgnoreCase("S")) out.print("SELECTED=\"true\"");%>>S</OPTION>
			            <OPTION value="N" <%if (FLG181.equalsIgnoreCase("N")) out.print("SELECTED=\"true\"");%>>N</OPTION>			     
			     </af:comboBox>
				</td>
			</tr>
			<tr>
				<td class="etichetta" nowrap>Note</td>
				<td class="campo" nowrap>
					<af:textArea title="Note" cols="40" rows="3" classNameBase="input" name="STRNOTE" value="<%=STRNOTE%>" onKeyUp="fieldChanged();" maxlength="1000" readonly="<%= String.valueOf(!canModify)%>"/>
				</td>
			</tr>



