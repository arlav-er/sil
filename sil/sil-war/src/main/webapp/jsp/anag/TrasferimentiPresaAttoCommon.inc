<% if (tipoTrasferimento.equals("A")) {
		codCpiTit="";
		strCpiTit="";
	}
%>

			<tr>
				<td class="etichetta">Data Trasferimento</td>
				<td class="campo">
					<af:textBox classNameBase="input" type="date" title="Data Trasferimento" name="DATTRASFERIMENTO" value="<%=DATTRASFERIMENTO%>" validateOnPost="true" validateWithFunction="segnalaData"
                    required="true" readonly="<%=String.valueOf(!canModify)%>" onKeyUp="fieldChanged();" size="11" maxlength="10"/>
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
				                                                    , document.Frm1.codCPI, 'getCPIDesc()'
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
				                                                     , document.Frm1.codCPI, 'getCPIDesc()'
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
			            onKeyUp="fieldChanged();PulisciRicercaCPI(document.Frm1.codCPI, document.Frm1.codCPIHid, document.Frm1.strCPI, document.Frm1.strCPIHid, 'codice');" 
			            size="10" maxlength="9" validateWithFunction="codCPIUpperCase" 
			            readonly="true"
			        />&nbsp;
			        <!-- <% if(canModify) { %>
			        <A HREF="javascript:btFindCPI_onclick(  document.Frm1.codCPI, 
			                                                document.Frm1.codCPIHid, 
			                                                document.Frm1.strCPI, 
			                                                document.Frm1.strCPIHid, 
			                                                'codice');">
			            <IMG name="image" border="0" src="../../img/binocolo.gif" alt="cerca per codice"/></a>&nbsp;
			        <% } %>-->
			        <af:textBox type="hidden" name="codCPIHid" value="" />
			        <af:textBox type="text" classNameBase="input" name="strCPI" value="<%=strCpiTit%>"
			            onKeyUp="fieldChanged();PulisciRicercaCPI(document.Frm1.codCPI, document.Frm1.codCPIHid, document.Frm1.strCPI, document.Frm1.strCPIHid, 'descrizione');" 
			            size="30" maxlength="50" 
			            inline="onkeypress=\"if (event.keyCode==13) { event.keyCode=9; this.blur(); }\"" 
			            readonly="true"
			        />&nbsp;
			        <!-- <% if(canModify) { %>
			        <A HREF="javascript:btFindCPI_onclick(  document.Frm1.codCPI, 
			                                                document.Frm1.codCPIHid, 
			                                                document.Frm1.strCPI, 
			                                                document.Frm1.strCPIHid, 
			                                                'descrizione');">
			            <IMG name="image" border="0" src="../../img/binocolo.gif" alt="cerca per descrizione"/></a>
			        <% } %>-->
			        <af:textBox type="hidden" name="strCPIHid" value="" />
			        <%-- campo valorizzato dalla funzione javaScript di FindComune.jsp --%>
			        <af:textBox type="hidden" name="codCPIifDOMeqRESHid" value="" /> 
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
			        <af:textBox classNameBase="input" type="text" name="CODCPIORIG" value="<%=CODCPIORIG%>" size="10"
			        			maxlength="9" readonly="true"/>
			        <af:textBox type="text" classNameBase="input" name="DESCRCPIORIG" value="<%=DESCRCPIORIG%>" 
			        size="30" maxlength="50" readonly="true"/>
			</tr>
			<tr>
			  <td class="etichetta">Stato occupazionale </td>
			  <td colspan="3">
			  <table width="100%" border="0"><tr><td>
			     <af:comboBox classNameBase="input" name="CODSTATOOCCUPAZ" moduleName="M_GETDESTATOOCC" selectedValue="<%=CODSTATOOCCUPAZ%>" addBlank="true"
			                  title="Stato occupazionale" required="true" disabled="<%= String.valueOf(!canModify || !esterno)%>" />
			  </td></tr></table>
			  </td>
			</tr>




			<% // VISUALIZZO QUESTE INFORMAZIONI SOLO SE IL LAVORATORE E' DISOCCUPATO O INOCCUPATO (OVVERO APPARTIENE ALLA CATEGORIA DLGS 150) 
			    if (codStatoOccRagg!=null && !codStatoOccRagg.equals("") && (codStatoOccRagg.charAt(0)=='D' || codStatoOccRagg.charAt(0)=='I')) { %>
			<tr>
			  <td class="etichetta">Anzianità di disoccupazione dal </td>
			  <td colspan="3">
			    <table border="0" width="100%">
			    <tr><td width="25%">
			        <af:textBox classNameBase="input" type="date" name="datAnzianitaDisoc" value="<%=Utils.notNull(dataAnzDisoc)%>" validateOnPost="true" 
			                    readonly="true" onKeyUp="fieldChanged();" size="11" maxlength="10"/></td>
			    </tr>
			    </table>              
			  </td>
			</tr>
			<tr>
			<td class="etichetta">mesi sosp.</td>
			        <td colspan=3 class="campo">
			          <table border="0" width="100%" class="main">
			           <tr>
   						  <%if (((datcalcolomesisosp == null) && (datcalcoloanzianita == null)) || (dataAnzDisoc !=null && (!dataAnzDisoc.equals(datcalcolomesisosp) || !dataAnzDisoc.equals(datcalcoloanzianita))) ) {
			                    visualizzazione = true;
			              %>
			                <td class="etichetta" nowrap>
			                  prima del&nbsp;
			                  <af:textBox classNameBase="input" type="date" name="datcalcolomesisosp" value="<%=Utils.notNull(datcalcolomesisosp)%>" validateOnPost="true"
			                              readonly="<%=String.valueOf(!canModify)%>" onKeyUp="fieldChanged();"  size="10" maxlength="10"/>
			                </td>
			                <td nowrap>
			                  <af:textBox classNameBase="input" type="integer" name="numMesiSosp" value="<%=Utils.notNull(numMesiSospPrecInt)%>" validateOnPost="true"
			                              readonly="<%=String.valueOf(!canModify)%>" onKeyUp="fieldChanged();"  size="4" maxlength="3"/>
			                </td>
			                <td class="etichetta" nowrap>                            
			                  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			                  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;+&nbsp;succ.
			                </td>
			              <%}%>
			              <td class="campo" nowrap>
			                <af:textBox classNameBase="input" type="integer" name="numMesiSospSucc" value="<%=Utils.notNull(numMesiSospInt)%>"  validateOnPost="true"
			                          readonly="true" onKeyUp="fieldChanged();"  size="4" maxlength="3"/>
			              </td>
			              <td class="etichetta">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
			              <td class="campo">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
			              <td nowrap>
			                =
			              </td>
			              <td class="campo" nowrap>
			                <af:textBox classNameBase="input" type="integer" name="totNumMesiSosp" value="<%=Utils.notNull(String.valueOf(Integer.parseInt(numMesiSospPrecInt)+Integer.parseInt(numMesiSospInt)))%>" validateOnPost="true"
			                          readonly="true" onKeyUp="fieldChanged();"  size="4" maxlength="3"/>
			              </td>
			              <% if ( !visualizzazione ) {%>
			                  <td class="etichetta">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
			                  <td class="campo">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
			                  <td class="etichetta">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
			                  <td class="campo">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
			                  <td class="etichetta">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
			                  <td class="campo">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
			              <% } %>
			            </tr>
			          </table>
			        </td>
			</tr>
                 
			<tr>
			   <td class="etichetta">Mesi anzianità</td>
			   <td colspan="3" nowrap>
			     <table border="0" width="100%" class="main">
			     <tr>
			      <% if (((datcalcolomesisosp == null) && (datcalcoloanzianita == null)) || (dataAnzDisoc !=null && (!dataAnzDisoc.equals(datcalcolomesisosp) || !dataAnzDisoc.equals(datcalcoloanzianita))) ) {
			            visualizzazione = true;
			      %>
			        <td class="etichetta" nowrap>
			            prima del&nbsp;
			              <af:textBox classNameBase="input" type="date" name="datcalcoloanzianita" value="<%=Utils.notNull(datcalcoloanzianita)%>" validateOnPost="true"
			                          readonly="<%=String.valueOf(!canModify)%>" onKeyUp="fieldChanged();"  size="10" maxlength="10"/>
			        </td>
			        <td nowrap>
			          <af:textBox classNameBase="input" type="integer" name="numAnzianitaPrec297" value="<%=Utils.notNull(mesiAnzPrecInt)%>"  validateOnPost="true"
			                      readonly="<%=String.valueOf(!canModify)%>" onKeyUp="fieldChanged();" size="4" maxlength="38"/>
			          &nbsp;
			          <af:comboBox classNameBase="input" name="CODMONOCALCOLOANZIANITAPREC297" addBlank="false" onChange="fieldChanged();" disabled="<%=Utils.notNull(String.valueOf(!canModify))%>">
			            <OPTION value="" <%if (codMonoCalcAnzPrec == null) out.print("SELECTED=\"true\"");%>></OPTION>
			            <OPTION value="A" <%if ((codMonoCalcAnzPrec!=null) && codMonoCalcAnzPrec.equalsIgnoreCase("A")) out.print("SELECTED=\"true\"");%>>A</OPTION>
			            <OPTION value="M" <%if ((codMonoCalcAnzPrec!=null) && codMonoCalcAnzPrec.equalsIgnoreCase("M")) out.print("SELECTED=\"true\"");%>>M</OPTION>
			          </af:comboBox>                    
			        </td>
			        <td class="etichetta" nowrap>
			          &nbsp;&nbsp;+&nbsp;succ.
			        </td>
			      <%}%>
			      <td class="campo" nowrap>
			        <af:textBox classNameBase="input" type="integer" name="numMesiAnzianita" value="<%=Utils.notNull(mesiAnzInt)%>" validateOnPost="true"
			                    readonly="true" onKeyUp="fieldChanged();" size="4" maxlength="3"/>
			        
			      </td>
			      <td class="etichetta" nowrap>
			        -&nbsp;sosp.
			      </td>
			      <td class="campo" nowrap>
			        <af:textBox classNameBase="input" type="integer" name="_totNumMesiSosp" value="<%=Utils.notNull(String.valueOf(Integer.parseInt(numMesiSospPrecInt)+Integer.parseInt(numMesiSospInt)))%>"  validateOnPost="true"
			                    readonly="true" onKeyUp="fieldChanged();"  size="4" maxlength="3"/>
			      </td>
			      <td nowrap>
			        =
			      </td>
			      <td class="campo" nowrap>
			        <af:textBox classNameBase="input" type="integer" name="totNumMesiAnz" value="<%=Utils.notNull(String.valueOf(Integer.parseInt(mesiAnzPrecInt)+Integer.parseInt(mesiAnzInt)-(Integer.parseInt(numMesiSospPrecInt)+Integer.parseInt(numMesiSospInt))))%>"  validateOnPost="true"
			                    readonly="true" onKeyUp="fieldChanged();"  size="4" maxlength="3"/>
			      </td>
			      <% if ( !visualizzazione ) {%>
			          <td class="etichetta">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
			          <td class="campo">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
			          <td class="etichetta">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
			          <td class="campo">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
			          <td class="etichetta">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
			          <td class="campo">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
			      <% } %>
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
<%} else { // 'D' || 'I', altrimenti inserisco le informazioni come campi hidden %>  
        <input type="hidden" name="datAnzianitaDisoc" value="<%=Utils.notNull(dataAnzDisoc)%>"/>
        <input type="hidden" name="datcalcolomesisosp" value="<%=Utils.notNull(datcalcolomesisosp)%>"/>
        <input type="hidden" name="numMesiSosp" value="<%=Utils.notNull(numMesiSospPrecInt)%>"/>
        <input type="hidden" name="datcalcoloanzianita" value="<%=Utils.notNull(datcalcoloanzianita)%>"/>
        <input type="hidden" name="numAnzianitaPrec297" value="<%=Utils.notNull(mesiAnzPrecInt)%>"/>
        <input type="hidden" name="CODMONOCALCOLOANZIANITAPREC297" value="<%=Utils.notNull(codMonoCalcAnzPrec)%>"/>   
<%}%>
			<tr><td colspan=4>&nbsp;</td></tr>
			<tr>
				<td class="etichetta" nowrap>Data DID</td>
				<td class="campo" nowrap>
					<af:textBox classNameBase="input" type="date" name="DATDICHIARAZIONE" value="<%=Utils.notNull(DATDICHIARAZIONE)%>" validateOnPost="true" 
					readonly="true" onKeyUp="fieldChanged();" size="11" maxlength="10"/>
				</td>
			</tr>
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




