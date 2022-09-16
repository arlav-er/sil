<tr class="note">
      <td colspan="2">
      <div class="sezione2">
        <img id='tendinaMov' alt="Chiudi" src="../../img/aperto.gif" onclick="cambiaTendina(this,'movimentoSez',document.Frm1.datcomunicazioneda);"/>&nbsp;&nbsp;&nbsp;
        Dati Movimento
      </div>
      </td>
    </tr>
    <tr>
      <td colspan="2">
        <div id="movimentoSez" style="display: none;">
          <table class="main" width="100%" border="0" cellpadding="2" celspacing="0">
           	<tr>
              <td class="etichetta" nowrap>Solo mov. aperti</td>
              <td class="campo">
              		<table class="main" width="100%" border="0" cellpadding="2" celspacing="0">
						<tr>
								<td >
									<af:comboBox classNameBase="input" name="onlyOpenMov"  >
                  						<option value="S" >Si</option>
                  						<option value="N" selected >No</option>              
                					</af:comboBox>   
								</td>
								<td class="etichetta" nowrap>
									Movimenti in ritardo
								</td>
								<td class="campo">
									<af:comboBox classNameBase="input" name="numGgTraMovComunicaz" >
                  						<option value="" selected ></option>
						                <option value="S" >Si</option>
						                <option value="N" >No</option>              
					                </af:comboBox>                
								</td>
								
						</tr> 
              		</table>
              </td> 
            </tr>
                         
              
			<tr>
				<td class="etichetta" nowrap>Rif. pratica amm.</td>
				<td class="campo">
					 <af:textBox type="text" name="referente" value="" size="42" maxlength="100" onKeyUp="to_upper(this);"/>
				</td>
			</tr>
          	 
          	 <tr>
				<td class="etichetta">Codice comunicazione</td>	
				<td colspan="5" class="campo2">
					<table style="border-collapse:collapse">
						<tr>
							<td class="campo2">
								<af:textBox type="text" name="codComunicazione" value="" size="22" maxlength="20" onKeyUp="to_upper(this);"/>
							</td>
							<td>&nbsp;&nbsp;&nbsp;</td>
							<td class="etichetta">cerca nelle missioni</td>
          					<td class="campo">
            					<Input type="checkBox" title="cerca nelle missioni" name="missioni"/>
          					</td>
						</tr>
					</table>
				</td>
			</tr>
			<tr>
          		<td class="etichetta2">Codice comunicazione precedente</td>
				<td class="campo2">
					<af:textBox type="text" name="codComunicazionePrec" value="" size="22" maxlength="20" onKeyUp="to_upper(this);"/>
				</td>
        	</tr>  
      
            <tr>
              <td class="etichetta">Provenienza</td>
              <td class="campo" nowrap>
                <af:comboBox classNameBase="input" name="codMonoMovDich" title="Movimento documentato o dichiarato" addBlank="true">
                  <option value="O" >Da comunicazione obbligatoria</option>
                  <option value="D" >Documentato dal lavoratore</option>
                  <option value="C" >Dichiarato</option>              
                </af:comboBox>
             </td>
            </tr>

            <tr>
              <td class="etichetta">Stato atto</td>
			  <td> 
			  	<table border="0" cellpadding="0" cellspacing="0">
			  	<tr>             
	              <td class="campo" nowrap>
					<af:comboBox classNameBase="input" name="CODSTATOATTO" moduleName="ComboStatoAtto" title="Stato Atto" addBlank="true" selectedValue="PR" onChange="inibisciScelta(this, 'PA')"/>	              
	              </td>
				  <td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
	              <td class="etichetta" align="right" nowrap>Motivazione&nbsp;
					<af:comboBox classNameBase="input" name="codMotAnnullamento" moduleName="ComboMotAnnullamento" title="Motivazione annullamento" addBlank="true"/>
	              </td>
              	</tr>
              	</table>
              </td>
            </tr>
          
            <tr>
              <td class="etichetta" nowrap>Data comunic. da</td>
              <td class="campo" >
                      <af:textBox type="date" name="datcomunicazioneda" validateOnPost="true" value="" size="10" maxlength="10"/>
                      a&nbsp;&nbsp;<af:textBox type="date" name="datcomunicazionea" validateOnPost="true" value="" size="10" maxlength="10"/>
              </td>            
            </tr>
            <tr>
              <td class="etichetta" nowrap>Data mov. da</td>
              <td class="campo" >
                      <af:textBox type="date" name="datmovimentoda" validateOnPost="true" value="" size="10" maxlength="10"/>
                      a&nbsp;&nbsp;<af:textBox type="date" name="datmovimentoa" validateOnPost="true" value="" size="10" maxlength="10"/>
              </td>            
            </tr>
			<tr>
			  <td class="etichetta">Tipo movimento</td>
			    <td>
			      <table border="0" cellpadding="0" cellspacing="0">
			        <tr>
			          <td class="campo" nowrap>
			            <af:comboBox classNameBase="input" name="tipoMovimento" moduleName="ComboTipoMovimento" addBlank="true" onChange="javascript:cambiaCombo(this);"/>
			          </td>
			          <td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
			          <td class="etichetta"  nowrap>Motivo cessazione&nbsp;
			            <af:comboBox classNameBase="input" name="motivoCessazione" moduleName="ComboTipoCessazione" addBlank="true" disabled="false" />
			          </td>
			      </tr>
			    </table>
			  </td>
			</tr>
            <tr>
              <td class="etichetta" nowrap>Tempo</td>
              <td class="campo" >
                      <af:comboBox classNameBase="input" name="codMonoTempo" moduleName=""  addBlank="true" onChange="javascript:aggiornaTipoContratto('', '', '', '');" >
                        <option value="D">Determinato</option>
                        <option value="I">Indeterminato</option>                
                      </af:comboBox>
              </td>            
            </tr>
            <tr>
              <td class="etichetta">Tipo contratto</td>
			  <td> 
			  	<table border="0" cellpadding="0" cellspacing="0">
			  	<tr>             
	              <td class="campo"  nowrap>
	                  <af:textBox title="Codice del tipo di contratto" value="" classNameBase="input" name="codTipoAss" size="8" onKeyUp="fieldChanged();to_upper(this);"/> 
	                  &nbsp;<a href="javascript:cercaTipoContratto('codice');"><img src="../../img/binocolo.gif" alt="Cerca per codice"></a>&nbsp;
	                  <af:textBox title="Descrizione del tipo di contratto" value="" classNameBase="input" name="descrTipoAss" size="45" onKeyUp="fieldChanged();"/>
	                  <a href="javascript:cercaTipoContratto('descrizione');"><img src="../../img/binocolo.gif" alt="Cerca per descrizione"></a>              
	              </td>
	              <td class="etichetta"  align="right" nowrap>Filtro&nbsp;
	                  <af:comboBox classNameBase="input" name="avvFiltrato" moduleName=""  onChange="" >
	                    <option value="DL">In base a dip. logiche</option>
	                    <option value="NF">Non filtrato</option>                
	                  </af:comboBox>
	              </td>
              	</tr>
              	</table>
              </td>
            </tr>
           <!--Giovanni D'Auria 08/03/2005 -->    
            <tr>
              	<td class="etichetta" nowrap>Mansione</td>
            	<td class="campo">
	              <af:textBox classNameBase="input" name="CODMANSIONE" size="7" maxlength="7"/>      
	              <af:textBox type="hidden" name="codMansioneHid" />
	              <a href="javascript:selectMansione_onClick(document.Frm1.CODMANSIONE, document.Frm1.codMansioneHid, document.Frm1.DESCMANSIONE,  document.Frm1.strTipoMansione);"><img src="../../img/binocolo.gif" alt="Cerca"></A>&nbsp;&nbsp;
	              <af:textBox type="hidden" name="CODTIPOMANSIONE" value="" />
	              <af:textBox classNameBase="input" type="text" size="60" name="DESCMANSIONE" value="" readonly="false" />
	              <!--<af:textBox type="text" name="strTipoMansione" value="" />
	              <af:textBox type="text" name="DESCMANSIONE" value="" />-->
	           	  <A href="javascript:selectMansionePerDescrizione(document.Frm1.DESCMANSIONE);">
					<img src="../../img/binocolo.gif" alt="Cerca per descrizione">
				  </A>
	            </td>
            </tr>

            <tr id="tipoMans" style="">
              <td class="etichetta">Tipo mans.</td>
              <td class="campo">
                <af:textBox classNameBase="input" size="50" type="text" name="strTipoMansione" value="" readonly="true" />
                <A href="javascript:ricercaAvanzataMansioni();">Ricerca avanzata</A>
              </td>
            </tr>

 			<!--
 			<tr id="descrMans" style=""> 
              <td class="etichetta">Mans.</td>
              <td class="campo">
                <af:textBox classNameBase="input" type="text" size="70" name="DESCMANSIONE" value="" readonly="true" />
              </td>
             </tr>
             -->

             <tr>
		        <td class="etichetta">Codice CCNL</td>
		        <td class="campo" >
		          <af:textBox classNameBase="input" onKeyUp="PulisciRicercaCCNL(document.Frm1.codCCNL, document.Frm1.codCCNLHid, document.Frm1.strCCNL, document.Frm1.strCCNLHid, 'codice');" type="text" name="codCCNL" size="5" maxlength="4" validateWithFunction="codCCNLUpperCase"  value="" />&nbsp;
		          <A HREF="javascript:btFindCCNL_onclick(document.Frm1.codCCNL, document.Frm1.codCCNLHid, document.Frm1.strCCNL, document.Frm1.strCCNLHid, 'codice');"><IMG name="image" border="0" src="../../img/binocolo.gif" alt="cerca per codice"/></a>&nbsp;
		          <af:textBox type="text" name="strCCNL" value="" size="48" maxlength="50" title="descrizione CCNL"/>
		          <A HREF="javascript:btFindCCNL_onclick(document.Frm1.codCCNL, document.Frm1.codCCNLHid, document.Frm1.strCCNL, document.Frm1.strCCNLHid, 'descrizione');"><IMG name="image" border="0" src="../../img/binocolo.gif" alt="cerca per descrizione"/></a>
		          <af:textBox type="hidden" name="codCCNLHid" value="" />
		        </td>
		    </tr>
    		<tr>
		      	<td class="etichetta">Tipo CCNL</td>
		      	<td class="campo">
	  	          <af:textBox type="text" classNameBase="input" name="strCCNLPadre" value="" size="48" maxlength="50" title="CCNL" readonly="true" />
	  	          <af:textBox type="hidden" name="strCCNLHid" value="" />
	  	          <A href="javascript:ricercaAvanzataCCNL();">Ricerca avanzata</A>
	      		</td>
		     </tr>
		     
           
           
       <!--Giovanni D'Auria 08/03/2005 fine-->           
           
              <tr>
                <td class="etichetta">Agevolazioni</td>
				<td> 
				  <table border="0" cellpadding="0" cellspacing="0">
				  <tr>                             
	                <td class="campo" nowrap>
	                  <af:comboBox classNameBase="input" name="codAgevolazione" moduleName="ComboTipoAgevolazioni" addBlank="true" />
	                </td>					
	              </tr>
              	 </table>
                </td>            
              </tr>
 			
 			<!--Giovanni D'Auria 01/06/2005 inizio-->   

              <tr>
                <td class="etichetta" nowrap>CPI comp. azienda</td>
                <td class="campo" colspan="">
                  <af:comboBox name="CodCPIAz" moduleName="M_ELENCOCPI" addBlank="true" selectedValue=""/>
                </td>
              </tr>
              
              <tr>
                <td class="etichetta" >Movimento inserito </td>
                <td class="campo" colspan="">
                  <af:comboBox name="insertBy" moduleName="" addBlank="true" selectedValue="">
                  <option value="M">Manualmente</option>
                  <option value="F">da File</option>
                  <option value="S">da Sare</option>
                  <option value="P">da Prolabor</option>
                  <option value="C">da Cooperazione</option>
                  </af:comboBox>
                </td>
              </tr>
<%	if(attributi.containsButton("RICERCAL68")){	%>
	 	<tr>
                <td class="etichetta" >Movimenti in L. 68 </td>
                <td class="campo">
                  <input type="checkbox" name="flgl68" title="Cerca movimenti in legge 68/99"></input>
                </td>
              </tr>          
<% 	} %>               
			<!--Giovanni D'Auria 01/06/2005 fine-->   
 
               
<!--             
              <tr>
                <td class="etichetta">Centro per l'impiego del lavoratore</td>
                <td class="campo" colspan="2">
                  <af:comboBox name="CodCPILav" moduleName="M_ELENCOCPI" addBlank="true" selectedValue=""/>
                </td>
              </tr>
-->              
            </table border="0" cellpadding="0" cellspacing="0">
          </div>
        </td>
      </tr>