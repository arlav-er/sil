
<%
	String codAtto=null;
	String strCPI = null;
	int size = 11, max = 65;
	String cod = null;
	String codCon = null;
%>


<%
	it.eng.sil.module.amministrazione.accorpamento.Did dati1 = null, dati2=null;
	dati1 = new it.eng.sil.module.amministrazione.accorpamento.Did((SourceBean) infoLav1.getAttribute("GET_AN_DISPO.ROWS.ROW"), 
		(SourceBean) infoLav1.getAttribute("PROTOCOLLO_DID.ROWS.ROW"),
		(SourceBean) infoLav1.getAttribute("GET_ULTIMO_MOV.ROWS.ROW"));
	dati2 = new it.eng.sil.module.amministrazione.accorpamento.Did((SourceBean) infoLav2.getAttribute("GET_AN_DISPO.ROWS.ROW"), 
		(SourceBean) infoLav2.getAttribute("PROTOCOLLO_DID.ROWS.ROW"),
		(SourceBean) infoLav2.getAttribute("GET_ULTIMO_MOV.ROWS.ROW"));
%>

<table class="main">	
	<td>    
        <table class='sezione2' cellspacing=0 cellpadding=0>
            <tr>
                <td  width=18><img id='IMG_S_DID' src='../../img/chiuso.gif' onclick='cambia(this, document.getElementById("S_DID"))'></td>
                <td  class='titolo_sezione'>Dichiarazione immediata disponibilità</td>    				
                <td align='right' width='30'></td>
            </tr>
        </table>
    </td>		
	<tr>
		<td>
			<TABLE class="main" id='S_DID' style='display:none'>  
        		<script>initSezioni(new Sezione(document.getElementById('S_DID'),document.getElementById('IMG_S_DID'),false));</script>
				<tr>
				  <td class="azzurro_bianco">
				    <table cellpadding="0" cellspacing="0" width="100%">
					    <tr>
					    	<td class="colonna1">Stato atto</td>
					        <td>	        
						        <%
						            codAtto = Utils.notNull(dati1.CODSTATOATTO);
						            if (codAtto.equals("")) codAtto = "";
						        %>
					        	<af:comboBox  classNameBase="input" disabled ="<%= \"true\" %>"  name="CODSTATOATTO"  
					                          moduleName="M_STATOATTODISPO" selectedValue="<%=codAtto%>" 
					                          addBlank="true" blankValue="" required="true"/>
					        </td>
					        <td>	        
						        <%
						            codAtto = Utils.notNull(dati2.CODSTATOATTO);
						            if (codAtto.equals("")) codAtto = "";
						        %>
					        	<af:comboBox  classNameBase="input" disabled ="<%= \"true\" %>"  name="CODSTATOATTO"  
					                          moduleName="M_STATOATTODISPO" selectedValue="<%=codAtto%>" 
					                          addBlank="true" blankValue="" required="true"/>
					        </td>
					    </tr>
					    <tr>
					        <td class="colonna1">anno</td>
					        <td><af:textBox classNameBase="input" type="text" name="annoProt" value="<%=dati1.annoProt!=null ? dati1.annoProt : \"\"%>"
					                                     readonly="true" title="Anno protocollo" size="5" maxlength="4" /></td>
					        <td><af:textBox classNameBase="input" type="text" name="annoProt" value="<%=dati2.annoProt!=null ? dati2.annoProt : \"\"%>"
					                                     readonly="true" title="Anno protocollo" size="5" maxlength="4" /></td>                             
					    </tr>
					    <tr>
					        <td class="colonna1">num</td>
					        <td><af:textBox classNameBase="input" type="text" name="numProt" value="<%=Utils.notNull(dati1.numProt)%>"
					                                     readonly="true" title="Numero protocollo" size="8" maxlength="100" />
					        </td>
					        <td><af:textBox classNameBase="input" type="text" name="numProt" value="<%=Utils.notNull(dati2.numProt)%>"
					                                     readonly="true" title="Numero protocollo" size="8" maxlength="100" />
					        </td>
					    </tr>
					    <tr>
					        <td class="colonna1">data
					        <td class="colonna2">
					           <af:textBox name="dataProt" 
					                       type="date" 
					                       value="<%=dati1.dataProt%>" 
					                       size="11" 
					                       maxlength="10"
					                       title="data di protocollazione"  
					                       classNameBase="input" 
					                       readonly="true" 
					                       validateOnPost="true" 
					                       required="false" 
					                       trim ="false"                        
					            /></td>
					        <td class="colonna2">
					           <af:textBox name="dataProt" 
					                       type="date" 
					                       value="<%=dati2.dataProt%>" 
					                       size="11" 
					                       maxlength="10"
					                       title="data di protocollazione"  
					                       classNameBase="input" 
					                       readonly="true" 
					                       validateOnPost="true" 
					                       required="false" 
					                       trim ="false"                        
					            /></td>
					    </tr>
					    <tr>
					        <td class="colonna1">ora
					        <td class="colonna2">
					           <af:textBox name="oraProt"
					                       type="text"
					                       value="<%=dati1.oraProt%>"
					                       size="6" 
					                       maxlength="5"
					                       title="data di protocollazione"  
					                       classNameBase="input" 
					                       readonly="true"
					                       validateOnPost="false" 
					                       required="false" 
					                       trim ="false"
					                  /></td>
					        <td class="colonna2">
					           <af:textBox name="oraProt"
					                       type="text"
					                       value="<%=dati2.oraProt%>"
					                       size="6" 
					                       maxlength="5"
					                       title="data di protocollazione"  
					                       classNameBase="input" 
					                       readonly="true"
					                       validateOnPost="false" 
					                       required="false" 
					                       trim ="false"
					                  /></td>
					    </tr>        
					    <tr><td class="colonna1">Doc. di</td>
					    	<td class="colonna2">
					    		<af:textBox name="docDiIO" type="text"
					                           value="<%= dati1.docDiIO%>"
					                           size="6" 
					                           maxlength="6"
					                           title="data di protocollazione"  
					                           classNameBase="input" 
					                           readonly="true"
					                           validateOnPost="false" 
					                           required="false" 
					                           trim ="false"
					                           />        
					        </td>
					        <td class="colonna2">
					    		<af:textBox name="docDiIO" type="text"
					                           value="<%= dati2.docDiIO%>"
					                           size="6" 
					                           maxlength="6"
					                           title="data di protocollazione"  
					                           classNameBase="input" 
					                           readonly="true"
					                           validateOnPost="false" 
					                           required="false" 
					                           trim ="false"
					                           />        
					        </td>
						</tr>
						<tr>
					        <td class="colonna1">Rif.</td>
					        <td class="colonna2">
					               <af:textBox name="docInOrOut" type="text"
					                           value="<%=dati1.docRif%>"
					                           size="<%=dati1.docRif.length()+5%>"
					                           title="data di protocollazione"  
					                           classNameBase="input" 
					                           readonly="true"
					                           validateOnPost="false" 
					                           required="false" 
					                           trim ="false"
					                           />
					        </td>
					        <td class="colonna2">
					               <af:textBox name="docInOrOut" type="text"
					                           value="<%=dati2.docRif%>"
					                           size="<%=dati2.docRif.length()+5%>"
					                           title="data di protocollazione"  
					                           classNameBase="input" 
					                           readonly="true"
					                           validateOnPost="false" 
					                           required="false" 
					                           trim ="false"
					                           />
					        </td>
					   	</tr>    
					</table>
				   </td>
				</tr>					
				<tr>
					<td>
						<table class="main">
							<tr><td colspan="3"><div class="sezione">Informazioni valide all'atto della dichiarazione</div></td></tr>
							
							<tr>
							   <td class="colonna1">Data inserimento nell'elenco anagrafico</td>
							   <td class="colonna2"><af:textBox classNameBase="input" type="date" name="datInizio" value="<%=Utils.notNull(dati1.datInizio)%>"
							                                   validateOnPost="true" readonly="true" 
							                                   required="false" title="Data inserimento nell'elenco anagrafico"
							                                   size="11" maxlength="10"/></td>
							   <td class="colonna2"><af:textBox classNameBase="input" type="date" name="datInizio" value="<%=Utils.notNull(dati2.datInizio)%>"
							                                   validateOnPost="true" readonly="true" 
							                                   required="false" title="Data inserimento nell'elenco anagrafico"
							                                   size="11" maxlength="10"/></td>
							</tr>							
							<tr>
							    <td class="colonna1">Cpi titolare dei dati</td>
							    <td class="colonna2">
							    	<%							    		
							          	if(dati1.descCPI != null) {
							            	strCPI = dati1.descCPI;
							            	strCPI += " - " + dati1.codCPI;
							          	}
							          	else strCPI = "";
							        %>
							          <af:textBox classNameBase="input" type="text" name="codCPI" value="<%=Utils.notNull(strCPI)%>" validateOnPost="true" 
							                      readonly="true"  required="false" title="Cpi titolare dei dati"
							                      size="30" maxlength="50"/>
							    </td>
							    <td class="colonna2">
							    	<%							    		
							          	if(dati2.descCPI != null) {
							            	strCPI = dati2.descCPI;
							            	strCPI += " - " + dati2.codCPI;
							          	}
							          	else strCPI = "";
							        %>
							          <af:textBox classNameBase="input" type="text" name="codCPI" value="<%=Utils.notNull(strCPI)%>" validateOnPost="true" 
							                      readonly="true"  required="false" title="Cpi titolare dei dati"
							                      size="30" maxlength="50"/>
							    </td>
							</tr>
							<tr>
							  <td class="colonna1">Stato occupazionale</td>
							  <td class="colonna2">				    
							    <%
							      size = 11; max = 65;
							      if(dati1.descStato != null) { 
							      	size = dati1.descStato.length()+3;
							        if(size > max) { 
							        	dati1.descStato = dati1.descStato.substring(0,max)+"...";
							          	size = max + 4;
							        }
							      }
							    %><af:textBox classNameBase="input" name="prgStatoOccupaz2" readonly="true" value="<%=Utils.notNull(dati1.descStato)%>"
							                  required="false" title="Stato occupazionale" size="<%=size%>" maxlength="100"/>
							  </td>
							  <td class="colonna2">				    
							    <%
							      size = 11; max = 65;
							      if(dati2.descStato != null){ 
							      	size = dati2.descStato.length()+3;
							        if(size > max) { 
							        	dati2.descStato = dati2.descStato.substring(0,max)+"...";
							            size = max + 4;
							        }
							      }
							    %><af:textBox classNameBase="input" name="prgStatoOccupaz2" readonly="true" value="<%=Utils.notNull(dati2.descStato)%>"
							                  required="false" title="Stato occupazionale" size="<%=size%>" maxlength="100"/>
							  </td>
							</tr>				
							<tr ><td colspan="3"><div class="sezione"></div></td></tr>				
							<tr>
							  <td  class="colonna1">Data dichiarazione</td>
							  <td  class="colonna2">
							    <af:textBox classNameBase="input" type="date" name="datDichiarazione" value="<%=Utils.notNull(dati1.datDichiarazione)%>"
							                readonly="<%=String.valueOf( ! canModify )%>" required="true" title="Data dichiarazione/conferma"
							                validateOnPost="true" size="12" maxlength="10" />
							  </td>
							  <td  class="colonna2">
							    <af:textBox classNameBase="input" type="date" name="datDichiarazione" value="<%=Utils.notNull(dati2.datDichiarazione)%>"
							                readonly="<%=String.valueOf( ! canModify )%>" required="true" title="Data dichiarazione/conferma"
							                validateOnPost="true" size="12" maxlength="10" />
							  </td>
							</tr>
							<tr>
							  <td class="colonna1">Tipo dichiarazione</td>
							  <td class="colonna2">
							      <%
							      	cod = Utils.notNull(dati1.CODTIPODICHDISP);
							    	if (cod.equals("")) cod = "";
							      %>
							    <af:comboBox disabled="<%=String.valueOf( ! canModify )%>"  name="CODTIPODICHDISP"  moduleName="M_TIPODICHDISP" 
							                selectedValue="<%=cod%>" classNameBase="input" 
							                 addBlank="true" blankValue="" required="true" title="Tipo dichiarazione"/>
							  </td>
							  <td class="colonna2">
							      <%
							      	cod = Utils.notNull(dati2.CODTIPODICHDISP);
							    	if (cod.equals("")) cod = "";
							      %>
							    <af:comboBox disabled="<%=String.valueOf( ! canModify )%>"  name="CODTIPODICHDISP"  moduleName="M_TIPODICHDISP" 
							                selectedValue="<%=cod%>" classNameBase="input" 
							                addBlank="true" blankValue="" required="true" title="Tipo dichiarazione"/>
							  </td>
							</tr>
							<tr><td colspan="3">&nbsp;</td><tr>
							  <td class="colonna1" >Attività lavorativa precedente<br> o in corso</td>
							  <td class="colonna2">
							  <% 
							  	codCon = Utils.notNull(dati1.CODULTIMOCONTRATTO);
							  	if (codCon.equals("")) codCon = dati1.codContrattoMovimento; 
							  %>
							   <af:comboBox disabled ="<%=\"true\"%>"  name="CODULTIMOCONTRATTO"  moduleName="M_CONTRATTO"
							   			 classNameBase="input" selectedValue="<%=codCon%>"  addBlank="true" />
							  </td>
							  <td class="colonna2">
							  <% 
							  	codCon = Utils.notNull(dati2.CODULTIMOCONTRATTO);
							  	if (codCon.equals("")) codCon = dati2.codContrattoMovimento; 
							  %>
							   <af:comboBox disabled ="<%=\"true\"%>"  name="CODULTIMOCONTRATTO"  moduleName="M_CONTRATTO"
							   			 classNameBase="input" selectedValue="<%=codCon%>"  addBlank="true" />
							  </td>
							</tr>				
							<tr>
							    <td class="colonna1">Data scadenza primo colloquio orientamento</td>
							    <td class="colonna2">
							        <af:textBox classNameBase="input" readonly="<%=String.valueOf( ! canModify )%>" type="date"
							                    name="DATSCADCONFERMA" value="<%=Utils.notNull(dati1.DATSCADCONFERMA)%>" validateOnPost="true" required="true"
							                    size="12" maxlength="10"/></td>
							    <td class="colonna2">
							        <af:textBox classNameBase="input" readonly="<%=String.valueOf( ! canModify )%>" type="date"
							                    name="DATSCADCONFERMA" value="<%=Utils.notNull(dati2.DATSCADCONFERMA)%>" validateOnPost="true" required="true"
							                    size="12" maxlength="10"/></td>
							</tr>
							<tr>
							    <td class="colonna1">Data scadenza stipula patto</td>
							    <td class="colonna2">
							       <af:textBox classNameBase="input" readonly="<%=String.valueOf( ! canModify )%>"  type="date"
							                   name="DATSCADEROGAZSERVIZI" value="<%=Utils.notNull(dati1.DATSCADEROGAZSERVIZI)%>" validateOnPost="true" 
							                   size="12" maxlength="10"/></td>
							     <td class="colonna2">
							       <af:textBox classNameBase="input" readonly="<%=String.valueOf( ! canModify )%>"  type="date"
							                   name="DATSCADEROGAZSERVIZI" value="<%=Utils.notNull(dati2.DATSCADEROGAZSERVIZI)%>" validateOnPost="true" 
							                   size="12" maxlength="10"/></td>
							</tr>				
							<tr><td><br/></td></tr>
							<tr>
							    <td colspan="3"> 
							    	<div class="sezione">Chiusura dell'atto</div>          
							    </td>
							</tr>
							<tr>
							    <td class="colonna1">Data fine atto</td>
							    <td class="colonna2">
							      <af:textBox classNameBase="input" readonly="<%=String.valueOf( ! canModify )%>" type="date"
							                  name="DATFINE" value="<%=Utils.notNull(dati1.DATFINE)%>" validateOnPost="true" 
							                  size="12" maxlength="10"/></td>
							    <td class="colonna2">
							      <af:textBox classNameBase="input" readonly="<%=String.valueOf( ! canModify )%>" type="date"
							                  name="DATFINE" value="<%=Utils.notNull(dati2.DATFINE)%>" validateOnPost="true" 
							                  size="12" maxlength="10"/></td>
							</tr>
							<tr>
							    <td class="colonna1">Motivo fine atto</td>
							    <td class="colonna2">
							      <af:comboBox disabled ="<%=String.valueOf( ! canModify )%>"  name="CODMOTIVOFINEATTO" 
							                moduleName="M_MOTFINEATTO" selectedValue="<%=Utils.notNull(dati1.CODMOTIVOFINEATTO)%>"  
							                classNameBase ="input" addBlank="true"/></td>
							    <td class="colonna2">
							      <af:comboBox disabled ="<%=String.valueOf( ! canModify )%>"  name="CODMOTIVOFINEATTO" 
							                moduleName="M_MOTFINEATTO" selectedValue="<%=Utils.notNull(dati2.CODMOTIVOFINEATTO)%>"  
							                classNameBase ="input" addBlank="true"/></td>
							</tr>
							<tr><td colspan="3"><br></td></tr>
							<tr>
							    <td class="colonna1">Note</td>
							    <td class="colonna2">
							        <af:textArea classNameBase="textarea" name="strNote" value="<%=dati1.STRNOTE%>"
							                 cols="30" rows="4" maxlength="100"
							                 readonly="<%=String.valueOf( ! canModify )%>"/>
							    </td>
							    <td class="colonna2">
							        <af:textArea classNameBase="textarea" name="strNote" value="<%=dati2.STRNOTE%>"
							                 cols="30" rows="4" maxlength="100"
							                 readonly="<%=String.valueOf( ! canModify )%>"/>
							    </td>
							</tr>
						</table>
					</td>
			  	</tr>	
			</table>
		</td>
	</tr>
</table>


