<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import=" com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.sil.security.PageAttribs,
                  it.eng.afExt.utils.*,
                  it.eng.sil.security.User,
                  it.eng.sil.security.ProfileDataFilter,                  
                  it.eng.sil.util.*,
                  java.util.*,
                  java.math.*,
                  java.io.*,
                  com.engiweb.framework.security.*,
                  com.engiweb.framework.configuration.ConfigSingleton,
                  java.lang.*,
                  java.text.*
                  "%>
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>                  
<%
	String readOnlyString = "";
	String queryString = null;
	String cdnLavoratoreStr= (String )serviceRequest.getAttribute("CDNLAVORATORE");
	String _page = (String) serviceRequest.getAttribute("PAGE"); 
	ProfileDataFilter filter = new ProfileDataFilter(user, _page);
	filter.setCdnLavoratore(new BigDecimal(cdnLavoratoreStr));
  	PageAttribs attributi = new PageAttribs(user, _page);

  	boolean canPrint    = false;
	boolean infStorButt = false;
	boolean canInsert   = false;
	boolean canModify   = false;
	boolean canView=filter.canViewLavoratore();
	if (! canView){
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
	}else{
     	canPrint    = attributi.containsButton("stampa");  
      	infStorButt = attributi.containsButton("STORICO");
    	canInsert = attributi.containsButton("INSERISCI");
      	canModify   = attributi.containsButton("aggiorna");
    	if((!canInsert) && (!canModify)){
    		;
    	}else{
        	boolean canEdit=filter.canEditLavoratore();
        	if (canInsert){
          		canInsert=canEdit;
        	}
	        if (canModify){
	          canModify=canEdit;
	        }        
   	 	}
	}
	readOnlyString = String.valueOf(!canModify);
	//
	String htmlStreamTop = StyleUtils.roundTopTable(canModify);
	String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);
	//
	Vector mansioni = null;
	Vector percorsi = null;
	Vector appuntamenti = null;
	mansioni = serviceResponse.getAttributeAsVector("M_ModifichePattoMansioni.rows.row");
	percorsi = serviceResponse.getAttributeAsVector("M_ModifichePattoAzioni.rows.row");
	appuntamenti = serviceResponse.getAttributeAsVector("M_ModifichePattoAppuntamenti.rows.row");
	String dataUltimoProtocollo = (String)serviceResponse.getAttribute("M_PATTOPROTOCOLLATOMODIFICATO.rows.row.DATULTIMOPROTOCOLLO");
	
    String labelServizio = "Servizio";
    String umbriaGestAz = "0";
    if(serviceResponse.containsAttribute("M_CONFIG_UMB_NGE_AZ")){
    	umbriaGestAz = Utils.notNull(serviceResponse.getAttribute("M_CONFIG_UMB_NGE_AZ.ROWS.ROW.NUM"));
    }
    if(umbriaGestAz.equalsIgnoreCase("1")){
    	labelServizio = "Area";
    }
%>

<html>
<head>
<title>Dettaglio patto lavoratore</title>
<link rel="stylesheet" type="text/css" href="../../css/stili.css"/>
<af:linkScript path="../../js/"/>

<%@ include file="../documenti/_apriGestioneDoc.inc"%>
<script>
<%@ include file="_sezioneDinamica_script.inc"%>

var flagChanged = false;  

function fieldChanged() {
 <% if (!readOnlyString.equalsIgnoreCase("true")){ %> 
    flagChanged = true;
 <%}%> 
}
</script>
</head>
<body>
<center>
<br><br>
<%out.print(htmlStreamTop);%>
<table class="main" border="0">
	<tr align="left">
		<td>
			<table><tr>
			<td class="etichetta">Modifiche dal: </td>
			<td class="campo"><%= Utils.notNull(dataUltimoProtocollo)%></td>
			</tr></table>
		</td>		
	</tr>
	<tr>
		<td>
			<table width="100%">
				<tr>
			        <td colspan="4">    
			            <table class='sezione' cellspacing=0 cellpadding=0>
							<tr>					
								<td  width=18>
			                    	<img id='I_S_OR_PER' src='../../img/aperto.gif' onclick='cambia(this, document.getElementById("S_OR_PER"))'></td>
								<td class="sezione_titolo">Azioni</td>					
							</tr>
						</table>
					</td>
			    </tr>
			    <tr>
		    		<td colspan=4 align="left">
			        	<TABLE id='S_OR_PER' style='display:' width="100%" align="left">  
			        		<tr>
			        			<td>
					        	<script>initSezioni( new Sezione(document.getElementById('S_OR_PER'),
			        									 document.getElementById('I_S_OR_PER'),
			        									 true));
					        	</script>
			    		    	</td>
			    		    </tr>			    		    
			    		    	<%
			    		    		for (int i=0;i<percorsi.size();i++) {
			    		    			SourceBean row = (SourceBean)percorsi.get(i);
										String datStimata = Utils.notNull(row.getAttribute("DATSTIMATA"));      
										String prgAzioni = Utils.notNull(row.getAttribute("PRGAZIONI"));    
										String codEsito = Utils.notNull(row.getAttribute("CODESITO"));
										String datEffettiva = Utils.notNull(row.getAttribute("DATEFFETTIVA"));      
										String strNote = Utils.notNull(row.getAttribute("STRNOTE"));        
										String prgPercorso = Utils.notNull(row.getAttribute("PRGPERCORSO"));
										String prgColloquio = Utils.notNull(row.getAttribute("PRGCOLLOQUIO"));
										String prgAzioneRagg = Utils.notNull(row.getAttribute("PRGAZIONIRAGG"));
										String dataProtocollo = Utils.notNull(row.getAttribute("DATPROTOCOLLO"));
										String strTipoOp =  Utils.notNull(row.getAttribute("strTipoOp"));
										String modifica = decodificaTipoModifica(strTipoOp);
										Object  cdnutmod = Utils.notNull(row.getAttribute("cdnutmod"));
										Object dtmmod = row.getAttribute("dtmmod");
			    		    	%>
			    		    <tr>
			    		    	<td align="left" >
			    		    		<TABLE style="background:white" width="100%" align="left"><!-- OR_PER -->
			    		    			  <tr align="left">
			    		    			  	<td colspan=2>
			    		    			  		<table>
			    		    			  			<tr>
					    		    			  		<td class="etichetta2">Data protocollo</td>
										                <td class="campo2">
									                    <af:textBox onKeyUp="fieldChanged();" classNameBase="input" readonly="true"  type="date"
									                        name="datStimata" value="<%=dataProtocollo %>" validateOnPost="true" required = "false"
							        		                size="12" maxlength="10"/>
							                			</td>
							                			<td class="etichetta2">Tipo Modifica: </td>
										                <td class="campo2"  style="font-weight:bold"><%=modifica %>									                    
							                			</td>
							                			
							                		</tr>							                		
				    		    			  	</table>
				    		    			  </td>  
							              </tr>
				    		    		  <tr align="left">
							                <td class="etichetta2">Data stimata</td>
							                <td class="campo2">
							                    <af:textBox onKeyUp="fieldChanged();" classNameBase="input" readonly="true"  type="date"
							                        name="datStimata" value="<%=datStimata %>" validateOnPost="true" required = "false"
							                        size="12" maxlength="10"/>
							                </td>
							              </tr>
							              <tr>
							                <td class="etichetta2">Misura</td>						                
							                <td class="campo2"><af:comboBox disabled="true"  name="prgAzioneRagg"  
							                        moduleName="M_DEAZIONIRAGG" selectedValue="<%= prgAzioneRagg %>" classNameBase="input"
							                        addBlank="true" required="false" onChange="comboPair.populate();fieldChanged();" title="Raggruppamento"/></td>
							              </tr>
						              <%--if (azioni!=null){--%>
							              <tr>
							                <td class="etichetta2">Azione</td>
							                <td class="campo2">						                             
							                  <af:comboBox onChange="fieldChanged();" disabled="true"  name="prgAzioni"  
							                         selectedValue="<%= prgAzioni %>" moduleName="M_DEAZIONI" classNameBase="input"
							                        addBlank="true" required="false" title="Azione"/>
							              </tr>               
						              <%--}--%>
							              <tr>
			    		    			  	<td colspan=2>
			    		    			  		<table>
			    		    			  			<tr>
										                <td class="etichetta2">Esito</td>
										                <td class="campo2">
										                    <af:comboBox onChange="fieldChanged();" disabled="true"  name="codEsito"  
										                        moduleName="M_DEESITO" selectedValue="<%= codEsito %>" classNameBase="input"
										                        addBlank="true" required="false" title="Esito"  />
										                </td>
													<% if (!datEffettiva.equals("")) { %>
										                <td class="etichetta2">Data di svolgimento</td>
										                <td class="campo2">
										                    <af:textBox onKeyUp="fieldChanged();" classNameBase="input" readonly="true"  type="date"
										                        name="datEffettiva" value="<%=datEffettiva %>" validateOnPost="true" required = "false"
										                        size="12" maxlength="10"/>
										                </td>
													<% } %>
							                		</tr>							                		
				    		    			  	</table>
				    		    			  </td>  
							              </tr>
							              <tr>
							                <td class="etichetta2">Note</td>
							                <td class="campo2">
							                    <textarea rows="5" cols="60" name="strNote" readonly><%=strNote%></textarea>
							                </td>
							              </tr>   
	       	    		    			  <tr>
							                <td colspan=2 align="center">
							                <% Testata operatoreInfo = new Testata(null, null, cdnutmod, dtmmod);
							                	operatoreInfo.showHTML(out);
							                %>
							                </td>
							              </tr> 
			    		    		</TABLE><!-- OR_PER -->			    		    		
			    		    	</TD>
			    		    </TR>
			    		    <%}%>
			        	</table>
			        </td>
		        </tr>     
			</table>
		</td>
	</tr>
		
	<tr>
		<td>
			<table width="100%">
				<tr>
			        <td colspan="4">    
			            <table class='sezione' cellspacing=0 cellpadding=0>
							<tr>					
								<td  width=18>
			                    	<img id='I_S_PR_MAN' src='../../img/aperto.gif' onclick='cambia(this, document.getElementById("S_PR_MAN"))'></td>
								<td class="sezione_titolo">Mansioni</td>					
							</tr>
						</table>
					</td>
			    </tr>
			    <tr>
		    		<td colspan=4 align="center">
						<TABLE id='S_PR_MAN' style='width:100%;display:' cellpadding="3">  
						        		<tr>
						        			<td>
								        	<script>initSezioni( new Sezione(document.getElementById('S_PR_MAN'),
						        									 document.getElementById('I_S_PR_MAN'),
						        									 true));
								        	</script>
						    		    	</td>
						    		    </tr>
						    		    <%
					    		    	for (int i=0;i<mansioni.size();i++) {
						    		    	String codMansione= "",
										         descMansione= "",
										         desTipoMansione="",
										         flgEsperienza= "",
										         flgDisponibile= "",
										         flgDispFormazione= "",
										         flgEspForm= "",
										         flgPianiInsProf= "",
										         prgMansione="",
										         codMonoTempo= "";
										         String dataProtocollo = null;
									        //
						    		    	SourceBean rowDett = (SourceBean) mansioni.get(i);
									        codMansione = StringUtils.getAttributeStrNotNull(rowDett, "codMansione");
									        desTipoMansione = StringUtils.getAttributeStrNotNull(rowDett, "desTipoMansione");
									        descMansione = StringUtils.getAttributeStrNotNull(rowDett, "desc_Mansione");
									        flgEsperienza = StringUtils.getAttributeStrNotNull(rowDett, "flgEsperienza");
									        flgEspForm = StringUtils.getAttributeStrNotNull(rowDett, "flgEspForm");
									        flgDisponibile = StringUtils.getAttributeStrNotNull(rowDett, "flgDisponibile");
									        codMonoTempo = StringUtils.getAttributeStrNotNull(rowDett, "codMonoTempo");
									        flgDispFormazione = StringUtils.getAttributeStrNotNull(rowDett, "flgDispFormazione");
									        flgPianiInsProf = StringUtils.getAttributeStrNotNull(rowDett, "flgPianiInsProf");
									        dataProtocollo = StringUtils.getAttributeStrNotNull(rowDett, "datProtocollo");
									        String strTipoOp =  Utils.notNull(rowDett.getAttribute("strTipoOp"));
											String modifica = decodificaTipoModifica(strTipoOp);
											Object  cdnutmod = Utils.notNull(rowDett.getAttribute("cdnutmod"));
											Object dtmmod = rowDett.getAttribute("dtmmod");
						    		    %>
						    		    <tr>
						    		    	<td>
								    		    <table style="background: white">
								    		      <tr align="left">
									               <td colspan=2>
					    		    			  		<table width="100%">
					    		    			  			<tr>
							    		    			  		<td class="etichetta2">Data protocollo</td>
												                <td class="campo2">
											                    <af:textBox onKeyUp="fieldChanged();" classNameBase="input" readonly="true"  type="date"
											                        name="datStimata" value="<%=dataProtocollo %>" validateOnPost="true" required = "false"
									        		                size="12" maxlength="10"/>
									                			</td>
									                			<td class="etichetta2">Tipo Modifica: </td>
												                <td class="campo"  style="font-weight:bold"><%=modifica %>
											                    <%--<af:textBox onKeyUp="fieldChanged();" classNameBase="input" readonly="true" 
											                        name="datStimata" value="<%=modifica %>" validateOnPost="true" required = "false"
									        		                size="70" maxlength="70"/>--%>
									                			</td>
									                			
									                		</tr>							                		
						    		    			  	</table>
						    		    			  </td>  
									              </tr>
									    		    <tr>
													    <td class="etichetta">Codice mansione</td>
													    <td class="campo">
													      <af:textBox 
													        classNameBase="input" 
													        name="CODMANSIONE" 
													        size="7" 
													        maxlength="7" 
													        value="<%= codMansione.toString() %>" 
													        readonly="<%= String.valueOf(!canModify) %>" 
													      />
													    </td>
												  </tr>           
													<tr valign="top">
													    <td class="etichetta">Tipo</td>
													    <td class="campo">
													      <af:textBox type="hidden" name="CODTIPOMANSIONE" value="" />
													      <af:textBox classNameBase="input" name="strTipoMansione" value="<%=desTipoMansione%>" readonly="true" size="48" />
													    </td>
												    </tr>
												  <tr>
													  <td class="etichetta">Descrizione</td>
													  <td class="campo">
													      <af:textArea cols="30" 
													                   rows="4" 
													                   name="DESCMANSIONE" 
													                   classNameBase="textarea"
													                   readonly="true" 
													                   required="false"
													                   maxlength="100"
													                   value="<%= descMansione %>" />
													  </td>
												  </tr>
												  <tr>
												  	<td>&nbsp;</td>
												  </tr>
												  <tr>
												  	<td class="etichetta">Esperienza ?</td>
												  	<td>
												    <table>
												      <tr>
													      <td class="campo">
													        <af:comboBox
													          title="Esperienza"
													          name="FLGESPERIENZA"
													          classNameBase="input"
													          disabled="<%= String.valueOf( !canModify ) %>"
													          onChange="fieldChanged()"
													        >
													          <option value=""  <% if ( "".equals(flgEsperienza) )  { out.print("SELECTED=\"true\""); } %> ></option>
													          <option value="S" <% if ( "S".equals(flgEsperienza) ) { out.print("SELECTED=\"true\""); } %> >Si</option>
													          <option value="N" <% if ( "N".equals(flgEsperienza) ) { out.print("SELECTED=\"true\""); } %> >No</option>
													          <option value="E" <% if ( "E".equals(flgEsperienza) ) { out.print("SELECTED=\"true\""); } %> >Non documentata</option>
													        </af:comboBox>
													      </td>
													    </tr>
												      </table>
												      <td class="etichetta">Esperienza formativa ?</td>
												      <td class="campo">
												        <af:comboBox 
												          title="Esperienza formativa"
												          name="FLGESPFORM"
												          classNameBase="input"
												          disabled="<%= String.valueOf( !canModify ) %>"
												          onChange="fieldChanged()"
												        >
												          <option value=""  <% if ( "".equals(flgEspForm) )  { out.print("SELECTED=\"true\""); } %> ></option>
												          <option value="S" <% if ( "S".equals(flgEspForm) ) { out.print("SELECTED=\"true\""); } %> >Si</option>
												          <option value="N" <% if ( "N".equals(flgEspForm) ) { out.print("SELECTED=\"true\""); } %> >No</option>
												        </af:comboBox>
												      </td>												    												  
												</tr>
											
											<tr>
											  <td colspan="2" >
											    <br/>
											    <b>&nbsp;&nbsp;Disponibilit&agrave;</b>
											    <br/>
											    <hr width="90%"/>
											  </td>
											</tr>
											
											<tr>
											  <td class="etichetta">Disponibile a lavorare con la mansione ?</td>
											  <td>
											    <table><tr>
											      <td class="campo">
											        <af:comboBox 
											          title="Disponibile a lavorare con la mansione"
											          name="FLGDISPONIBILE"
											          classNameBase="input"
											          disabled="<%= String.valueOf( !canModify ) %>"
											          onChange="fieldChanged()"
											        >
											          <option value=""  <% if ( "".equals(flgDisponibile) )  { out.print("SELECTED=\"true\""); } %> ></option>
											          <option value="S" <% if ( "S".equals(flgDisponibile) ) { out.print("SELECTED=\"true\""); } %> >Si</option>
											          <option value="N" <% if ( "N".equals(flgDisponibile) ) { out.print("SELECTED=\"true\""); } %> >No</option>
											      </af:comboBox></td></tr>
											    </table>
											  </td>  
											</tr>
											<tr>
											  <td class="etichetta">Disponibile alla formazione professionale?</td>
											  <td>
											    <table><tr>
											      <td class="campo">
											        <af:comboBox 
											          title="Disponibile alla formazione"
											          name="FLGDISPFORMAZIONE"
											          classNameBase="input"
											          disabled="<%= String.valueOf( !canModify ) %>"
											          onChange="fieldChanged()"
											        >
											          <option value=""  <% if ( "".equals(flgDispFormazione) )  { out.print("SELECTED=\"true\""); } %> ></option>
											          <option value="S" <% if ( "S".equals(flgDispFormazione) ) { out.print("SELECTED=\"true\""); } %> >Si</option>
											          <option value="N" <% if ( "N".equals(flgDispFormazione) ) { out.print("SELECTED=\"true\""); } %> >No</option>
											        </af:comboBox>
											      </td></tr>
											    </table>						    
											  </td>
											</tr>
											<tr>
								                <td colspan=2 align="center">
								                <% Testata operatoreInfo = new Testata(null, null, cdnutmod, dtmmod);
								                	operatoreInfo.showHTML(out);
								                %>
								                </td>
								              </tr> 
								        </table>						           
						        </td>
					        </tr>     					        
					        <%}%>
						</table>
					</td>
				</tr>				
			</table>		
		</td>
	</tr>
	<tr>
		<td>
			<table width="100%">
				<tr>
			        <td colspan="4">    
			            <table class='sezione' cellspacing=0 cellpadding=0>
							<tr>					
								<td  width=18>
			                    	<img id='I_S_AG_LAV' src='../../img/aperto.gif' onclick='cambia(this, document.getElementById("S_AG_LAV"))'></td>
								<td class="sezione_titolo">Appuntamenti</td>					
							</tr>
						</table>
					</td>
			    </tr>
			    <tr>
		    		<td colspan=4 align="center">
						<TABLE id='S_AG_LAV' style='width:100%;display:' cellpadding="3">  
						        		<tr>
						        			<td>
								        	<script>initSezioni( new Sezione(document.getElementById('S_AG_LAV'),
						        									 document.getElementById('I_S_AG_LAV'),
						        									 true));
								        	</script>
						    		    	</td>
						    		    </tr>
						    		    <%
					    		    	for (int i=0;i<appuntamenti.size();i++) {
						    		    	String dataAppuntamento= "",
										         orario= "",
										         NUMMINUTI="",
										         PRGSPI= "",
										         CODSERVIZIO= "",
										         PRGAMBIENTE= "";
										         String dataProtocollo = null;
									        //
						    		    	SourceBean rowDett = (SourceBean) appuntamenti.get(i);
									        orario = StringUtils.getAttributeStrNotNull(rowDett, "orario");
									        dataAppuntamento = StringUtils.getAttributeStrNotNull(rowDett, "dataAppuntamento");
									        NUMMINUTI = Utils.notNull(rowDett.getAttribute("NUMMINUTI"));
									        PRGSPI = Utils.notNull(rowDett.getAttribute("PRGSPI"));
									        CODSERVIZIO = StringUtils.getAttributeStrNotNull(rowDett, "CODSERVIZIO");
									        PRGAMBIENTE = Utils.notNull(rowDett.getAttribute("PRGAMBIENTE"));									        
									        dataProtocollo = StringUtils.getAttributeStrNotNull(rowDett, "datProtocollo");
									        String strTipoOp =  Utils.notNull(rowDett.getAttribute("strTipoOp"));
											String modifica = decodificaTipoModifica(strTipoOp);
											Object  cdnutmod = Utils.notNull(rowDett.getAttribute("cdnutmod"));
											Object dtmmod = rowDett.getAttribute("dtmmod");
						    		    %>
						    		    <tr>
						    		    	<td>
								    		    <table style="background: white">
								    		      <tr align="left">
									                <td colspan=2>
					    		    			  		<table>
					    		    			  			<tr>
							    		    			  		<td class="etichetta2">Data protocollo</td>
												                <td class="campo2">
											                    <af:textBox onKeyUp="fieldChanged();" classNameBase="input" readonly="true"  type="date"
											                        name="datStimata" value="<%=dataProtocollo %>" validateOnPost="true" required = "false"
									        		                size="12" maxlength="10"/>
									                			</td>
									                			<td class="etichetta2">Tipo Modifica: </td>
												                <td class="campo2"  style="font-weight:bold"><%=modifica %>
												                <%--
											                    <af:textBox onKeyUp="fieldChanged();" classNameBase="input" readonly="true" 
											                        name="datStimata" value="<%=modifica %>" validateOnPost="true" required = "false"
									        		                size="70" maxlength="70"/>--%>
									                			</td>
									                			
									                		</tr>							                		
						    		    			  	</table>
						    		    			  </td>  
						    		    		  </tr>
									              <tr>
													  <td class="etichetta">Data</td>
													  <td class="campo">													  
													      <af:textBox name="data_app"
													              size="11"
													              maxlength="10"
													              required="true"
													              validateOnPost="true"  
													              classNameBase="input" readonly="true"       
													              value="<%=dataAppuntamento%>"/>													  
													  </td>
													</tr>
													<tr>
													  <td class="etichetta">Orario</td>
													  <td class="campo">
													  <af:textBox name="orario"
													              size="5"
													              maxlength="5"
													              required="true"
													              type="time"
													              title="Orario"
													              validateOnPost="true"
													              onKeyUp="fieldChanged();"
													              classNameBase="input"
													              readonly="true"
													              value="<%=orario%>"/>
													  </td>
													</tr>
													<tr>
													  <td class="etichetta">Durata Appuntamento</td>
													  <td class="campo">
													  <af:textBox name="NUMMINUTI" 
													              size="5" 
													              maxlength="3" 
													              type="integer"
													              validateOnPost="true"
													              title="Durata Appuntamento"
													              onKeyUp="fieldChanged();"
													              classNameBase="input"
													              readonly="<%=String.valueOf(!canModify)%>"
													              value="<%=NUMMINUTI%>"
													  />&nbsp;minuti
													  </td>
													</tr>
													<tr>
													  <td class="etichetta">Operatore</td>
													    <td class="campo">
													      <af:comboBox name="PRGSPI" size="1" title="Operatore"
													                     multiple="false" required="false"
													                     focusOn="false" moduleName="COMBO_SPI"
													                     selectedValue="<%=PRGSPI%>" addBlank="true" blankValue=""
													                     classNameBase="input"
													                     disabled="true"
													                     onChange="fieldChanged()"/>    
													    </td>
													</tr>
													<tr>
													<%String titoloServ = "Codice " + labelServizio; %>
													  <td class="etichetta"><%=labelServizio %></td>
													    <td class="campo">
													      <af:comboBox name="CODSERVIZIO" size="1" title="<%=titoloServ %>"
													                     multiple="false" required="false"
													                     focusOn="false" moduleName="COMBO_SERVIZIO"
													                     selectedValue="<%=CODSERVIZIO%>" addBlank="true" blankValue=""
													                     classNameBase="input"
													                     disabled="true"
													                     onChange="fieldChanged()"/>    
													    </td>
													</tr>
													<tr>
													  <td class="etichetta">Ambiente/Aula</td>
													  <td class="campo">
													      <af:comboBox name="PRGAMBIENTE" size="1" title="Ambiente/Aula"
													                     multiple="false" required="false"
													                     focusOn="false" moduleName="COMBO_AMBIENTE"
													                     selectedValue="<%=PRGAMBIENTE%>" addBlank="true" blankValue=""
													                     classNameBase="input"
													                     disabled="true"
													                     onChange="fieldChanged()"/>    
													    </td>
													</tr>
													<tr>
										                <td colspan=2 align="center">
										                <% Testata operatoreInfo = new Testata(null, null, cdnutmod, dtmmod);
										                	operatoreInfo.showHTML(out);
										                %>
										                </td>
										            </tr> 
								           </table>						           
						        </td>
					        </tr>     
					        <%}%>
						</table>
					</td>
				</tr>				
			</table>		
		</td>
	</tr>
</table>
<%out.print(htmlStreamBottom);%>
</center>
</BODY>
</HTML>
<%! 
	private String decodificaTipoModifica(String tipo) {
		if (tipo.equals("D")) 
			return "Cancellazione";
		else 
		if (tipo.equals("A")) 
			return "Associazione nuova azione";
		if (tipo.equals("AP")) 
			return "Associazione nuovo appuntamento";
		else 
		if (tipo.equals("UD")) 
			return "Modifica data entro la quale deve essere svolta l'azione";
		else 
		if (tipo.equals("UA")) 
			return "Modificata l'azione che deve essere svolta";
		else 
		if (tipo.equals("ULN")) 
			return "Tolta la disponibilità a lavorare sulla mansione";
		else 
		if (tipo.equals("ULS")) 
			return "Aggiunta la disponibilità a lavorare sulla mansione";
		else 
		if (tipo.equals("UFN")) 
			return "Tolta la disponibilità alla formazione sulla mansione";
		else 
		if (tipo.equals("UFS")) 
			return "Aggiunta disponibilità alla formazione sulla mansione";		
		if (tipo.equals("I")) 
			return "Inserimento nuova mansione con disponibilità al lavoro e/o alla formazione in presenza di un patto già protocollato";		
		if (tipo.equals("US")) 
			return "Modifica servizio dell'appuntamento associato al patto";		
		if (tipo.equals("UD")) 
			return "Modifica data dell'appuntamento associato al patto";		
		else return "ppppppppppp";
	}
%>